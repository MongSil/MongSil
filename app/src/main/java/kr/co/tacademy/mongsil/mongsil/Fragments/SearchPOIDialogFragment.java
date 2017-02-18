package kr.co.tacademy.mongsil.mongsil.Fragments;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import kr.co.tacademy.mongsil.mongsil.JSONParsers.NetworkDefineConstant;
import kr.co.tacademy.mongsil.mongsil.Adapters.POIAdapterCallback;
import kr.co.tacademy.mongsil.mongsil.JSONParsers.Models.POIData;
import kr.co.tacademy.mongsil.mongsil.JSONParsers.ParseDataParseHandler;
import kr.co.tacademy.mongsil.mongsil.Managers.PropertyManager;
import kr.co.tacademy.mongsil.mongsil.R;
import kr.co.tacademy.mongsil.mongsil.JSONParsers.Models.SearchPOIInfo;
import kr.co.tacademy.mongsil.mongsil.Adapters.SearchPOIRecyclerViewAdapter;
import kr.co.tacademy.mongsil.mongsil.Databases.UserDB;
import kr.co.tacademy.mongsil.mongsil.Databases.UserDBHelper;
import kr.co.tacademy.mongsil.mongsil.Databases.UserOpenHelperManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.util.Log.d;
import static android.util.Log.e;

/**
 * Created by ccei on 2016-08-02.
 * POI 검색 -> 지역 선택 -> 위도경도 불러옴 -> 날씨 API
 */
public class SearchPOIDialogFragment extends DialogFragment
        implements POIAdapterCallback {
    public static interface OnPOISearchListener {
        public abstract void onPOISearch(POIData POIData);
    }

    private OnPOISearchListener searchListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPOISearchListener) {
            searchListener = (OnPOISearchListener) context;
        }
    }


    TextView emptySearch;
    EditText editSearch;
    ImageView imgSearchCancel;
    InputMethodManager imm;

    // 즐겨찾기 DB
    protected UserDBHelper userDB = null;

    RecyclerView searchRecycler;
    SearchPOIRecyclerViewAdapter poiAdapter;
    ArrayList<POIData> markedList;
    ArrayList<POIData> datas;

    public static SearchPOIDialogFragment newInstance() {
        SearchPOIDialogFragment fragment = new SearchPOIDialogFragment();
        return fragment;
    }

    public SearchPOIDialogFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.LocationDialogTheme);
        datas = new ArrayList<POIData>();
        userDB = UserOpenHelperManager.
                generateUserOpenHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.dialog_select_locaiton, container, false);

        RelativeLayout searchContainer =
                (RelativeLayout) view.findViewById(R.id.search_container);
        emptySearch = (TextView) view.findViewById(R.id.text_empty_search);
        editSearch = (EditText) view.findViewById(R.id.edit_search);
        imgSearchCancel = (ImageView) view.findViewById(R.id.img_search_cancel);

        searchRecycler = (RecyclerView) view.findViewById(R.id.search_recycler);
        searchRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        poiAdapter = new SearchPOIRecyclerViewAdapter(this);
        searchRecycler.setAdapter(poiAdapter);

        markInit();

        searchContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emptySearch.setVisibility(View.GONE);
                editSearch.setVisibility(View.VISIBLE);
                imgSearchCancel.setVisibility(View.VISIBLE);
                editSearch.requestFocus();
                imm = (InputMethodManager) getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editSearch, InputMethodManager.SHOW_FORCED);
                editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                        if (i == EditorInfo.IME_ACTION_SEARCH || i == EditorInfo.IME_ACTION_NONE) {
                            // 키보드에서 검색버튼을 누를 때
                            imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);
                            new AsyncPoiJSONList().execute(editSearch.getText().toString());
                            editSearch.clearFocus();
                            return true;
                        }
                        return false;
                    }
                });
                imgSearchCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cancelSearch();
                    }
                });
            }
        });
        return view;
    }

    private void markInit() {
        markedList = userMarkList();
        if (!(markedList == null || markedList.size() == 0)) {
            for (POIData poiData : markedList) {
                if(poiData.getName() != null) {
                    datas.add(poiData);
                }
                poiData.setMarked(true);

            }
            datas.add(0, new POIData(0));
        }
        poiAdapter.add(datas);
        poiAdapter.notifyDataSetChanged();
    }

    private void markChange(boolean select, POIData poiData) {
        int markCount = PropertyManager.getInstance().getMarkCount();
        if(select) { // 추가할 때
            // 노란색 별로 만든다
            poiData.setMarked(true);

            // 즐겨찾기 헤더가 없으면 추가
            if(datas.get(0).getTypeCode() != 0) {
                datas.add(0, new POIData(0));
            }

            // 데이터에 추가한다
            datas.add(markCount+1, poiData);
            if(markCount < 11) {
                PropertyManager.getInstance().setMarkCount(markCount + 1);
            }
        } else { // 삭제할 때
            // 별을 누른 항목과 같은 항목이 있는지 검사
            for (int i = 0; i < datas.size(); i++) {
                // 확인한 후 같은 항목이 존재하고
                if (poiData.getName().equals(datas.get(i).getName())) {
                    // 찾는 부분이 즐겨찾기 부분 사이즈보다 작으면 삭제
                    if (i < markCount+2) {
                        datas.remove(i);
                        break;
                    }
                }
            }
            if(markCount > 0) {
                PropertyManager.getInstance().setMarkCount(markCount - 1);
            }
            // 즐겨찾기 수가 0이면
            if(markCount < 1) {
                // 즐겨찾기 헤더 삭제
                datas.remove(0);
            }

            poiData.setMarked(false);
        }

        poiAdapter.add(datas);
    }

    private void cancelSearch() {
        if(imm != null) {
            imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);
        }
        emptySearch.setVisibility(View.VISIBLE);
        editSearch.setVisibility(View.GONE);
        editSearch.setText("");
        imgSearchCancel.setVisibility(View.GONE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        wlp.windowAnimations = R.style.LocationDialogAnimation;
        wlp.gravity = Gravity.BOTTOM;
        wlp.height = (int) (display.getHeight() * 0.89f);
        getDialog().getWindow().setDimAmount(0);
        window.setAttributes(wlp);
    }
    // 별을 눌렀을 때
    @Override
    public void onMarkCallback(boolean select, POIData poiData) {
        SQLiteDatabase dbHandler = userDB.getWritableDatabase();
        Cursor resultExist = null;
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(UserDB.UserMark.TABLE_MARK_NAME);

        if (select) {
            try {
                String markName = poiData.getName();
                dbHandler.beginTransaction();
                // 이름이 등록되었는지 확인해 보는 쿼리 빌더
                queryBuilder.appendWhere(UserDB.UserMark.USER_MARK_LOCATION_NAME + "='" + markName + "'");
                // 빌드된 쿼리로 결과집합을 알아 본다
                // 쿼리빌더를 사용 할 때는 첫번째 인자에 SQLiteDatabase객체를 등록함
                resultExist = queryBuilder.query(dbHandler, null, null, null, null, null, null);
                ContentValues markNameValues = new ContentValues();
                if (resultExist.getCount() == 0) {
                    // 이름이 존재 하지 않는다면 추가 한다.
                    markNameValues.put(UserDB.UserMark.USER_MARK_LOCATION_NAME, markName);
                } else { // 이름이 존재 한다면 처음으로 돌아가버렷
                    resultExist.moveToFirst();
                }
                //markNameValues.put(UserDB.UserMark._ID, poiData.id);
                markNameValues.put(UserDB.UserMark.USER_MARK_UPPER, poiData.getUpperAddrName());
                markNameValues.put(UserDB.UserMark.USER_MARK_MIDDLE, poiData.getMiddleAddrName());
                markNameValues.put(UserDB.UserMark.USER_MARK_LAT, poiData.getNoorLat());
                markNameValues.put(UserDB.UserMark.USER_MARK_LON, poiData.getNoorLon());
                dbHandler.insert(UserDB.UserMark.TABLE_MARK_NAME, "NODATA", markNameValues);
                dbHandler.setTransactionSuccessful();
                markChange(select, poiData);
            } catch (SQLiteException sqle) {
                Log.e("SearchPOIDBError", sqle.toString());
                if(markedList.size() > 0) {
                    PropertyManager.getInstance().setMarkCount(0);
                }
            } finally {
                dbHandler.endTransaction();
                if (resultExist != null) {
                    resultExist.close();
                }
                dbHandler.close();
            }
        } else {
            //빌드된 쿼리로 해당 결과 집합을 가져 온다
            markChange(select, poiData);
            dbHandler.execSQL("DELETE FROM " +
                    UserDB.UserMark.TABLE_MARK_NAME
                    + " WHERE " + UserDB.UserMark.USER_MARK_LOCATION_NAME +
                    "= '" + poiData.getName() + "';");
            dbHandler.close();
        }
    }

    @Override
    public void onSelectCallback(POIData poiData) {
        searchListener.onPOISearch(poiData);
        cancelSearch();
        dismiss();
    }

    private ArrayList<POIData> userMarkList() {
        SQLiteDatabase dbHandler = userDB.getReadableDatabase();

        //걸그룹 테이블의 ID와 뮤직테이블 ID값의 해당하는 데이터를 가져오는 쿼리를 빌드
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(UserDB.UserMark.TABLE_MARK_NAME);

        //결과 집합으로 가져올 컬럼의 이름들(2개 이상 테이블로 빌드시 풀네임을 주어야 함)
        String columnsToReturn[] = {
                UserDB.UserMark.TABLE_MARK_NAME + "." + UserDB.UserMark._ID,
                UserDB.UserMark.TABLE_MARK_NAME + "." + UserDB.UserMark.USER_MARK_LOCATION_NAME,
                UserDB.UserMark.TABLE_MARK_NAME + "." + UserDB.UserMark.USER_MARK_UPPER,
                UserDB.UserMark.TABLE_MARK_NAME + "." + UserDB.UserMark.USER_MARK_MIDDLE,
                UserDB.UserMark.TABLE_MARK_NAME + "." + UserDB.UserMark.USER_MARK_LAT,
                UserDB.UserMark.TABLE_MARK_NAME + "." + UserDB.UserMark.USER_MARK_LON
        };
        //빌드된 쿼리로 해당 결과 집합을 가져 온다
        Cursor joinResultSet = queryBuilder.query(dbHandler,
                columnsToReturn, null, null, null, null,
                "tbl_user_mark." + UserDB.UserMark.SORT_ORDER);

        ArrayList<POIData> poiDatas = new ArrayList<POIData>();
        if (joinResultSet.moveToFirst()) {
            int resultSetSize = joinResultSet.getCount();
            for (int i = 0; i < resultSetSize; i++) {
                poiDatas.add(new POIData(
                        joinResultSet.getLong(
                                joinResultSet.getColumnIndex(UserDB.UserMark._ID)),
                        joinResultSet.getString(
                                joinResultSet.getColumnIndex(UserDB.UserMark.USER_MARK_LOCATION_NAME)),
                        joinResultSet.getString(
                                joinResultSet.getColumnIndex(UserDB.UserMark.USER_MARK_UPPER)),
                        joinResultSet.getString(
                                joinResultSet.getColumnIndex(UserDB.UserMark.USER_MARK_MIDDLE)),
                        joinResultSet.getString(
                                joinResultSet.getColumnIndex(UserDB.UserMark.USER_MARK_LAT)),
                        joinResultSet.getString(
                                joinResultSet.getColumnIndex(UserDB.UserMark.USER_MARK_LON))));
                joinResultSet.moveToNext();
            } //for문 끝
        }
        joinResultSet.close();
        dbHandler.close();
        return poiDatas;
    }

    // 지역검색 AsyncTask
    public class AsyncPoiJSONList extends AsyncTask<String, Integer, SearchPOIInfo> {
        @Override
        protected SearchPOIInfo doInBackground(String... args) {
            Response response = null;
            try {
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                Request request = new Request.Builder()
                        .addHeader("Accept", "application/json")
                        .addHeader("appKey", NetworkDefineConstant.SK_APP_KEY)
                        .url(String.format(
                                NetworkDefineConstant.SK_POI_SEARCH,
                                args[0]))
                        .build();
                response = toServer.newCall(request).execute();
                ResponseBody responseBody = response.body();

                boolean flag = response.isSuccessful();
                int responseCode = response.code();
                if (responseCode >= 400) return null;
                if (flag) {
                    return ParseDataParseHandler.getJSONPoiList(
                            new StringBuilder(responseBody.string()));
                }
            } catch (UnknownHostException une) {
                e("connectionFail", une.toString());
            } catch (UnsupportedEncodingException uee) {
                e("connectionFail", uee.toString());
            } catch (Exception e) {
                e("connectionFail", e.toString());
            } finally {
                if (response != null) {
                    response.close();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(SearchPOIInfo result) {
            if (result != null && result.getPOIData().size() > 0) {
                datas.clear();

                // 즐겨찾기한 데이터를 넣음
                markInit();

                // 검색한 데이터 삽입
                for (POIData poiData : result.getPOIData()) {
                    datas.add(poiData);
                }

                // 즐겨찾기가 있을 경우
                if( markedList != null && markedList.size() > 0) {
                    // 검색 목록에서 즐겨찾기와 같은 항목이 있는지 확인
                    for (int i = 1; i < markedList.size(); i++) {
                        for (int j = 1; j < result.getPOIData().size(); j++) {
                            // 확인한 후 같은 항목이 존재하면 즐겨찾기 마크로 바꿈
                            if (markedList.get(i).getName().equals(result.getPOIData().get(j).getName())
                                    && markedList.get(i).getNoorLat().equals(result.getPOIData().get(j).getNoorLat())
                                    && markedList.get(i).getNoorLon().equals(result.getPOIData().get(j).getNoorLon())) {
                                result.getPOIData().get(j).setMarked(true);
                                result.getPOIData().get(j).setId(markedList.get(i).getId());
                            }
                        }
                    }
                    // 즐겨찾기 항목 크기의 다음에 검색내역 추가
                    datas.add(markedList.size() + 1, new POIData(1));
                } else { // 즐겨찾기가 없을 경우 검색내역 추가
                    datas.add(0, new POIData(1));
                }

                poiAdapter.add(datas);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (userDB != null) {
            userDB.close();
        }
    }
}
