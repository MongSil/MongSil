package kr.co.tacademy.mongsil.mongsil;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.util.Log.e;

/**
 * Created by ccei on 2016-08-02.
 */
// TODO : POI 검색 -> 지역 선택 -> 위도경도 불러옴 -> 날씨 API
public class SearchPoiDialogFragment extends DialogFragment {
    TextView emptySearch;
    EditText editSearch;
    ImageView imgSearchCancel;
    InputMethodManager imm;

    RecyclerView searchRecycler;
    SearchPoiRecyclerViewAdapter poiAdapter;
    ArrayList<POIData> POIData;

    public SearchPoiDialogFragment() { }

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.LocationDialogTheme);
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
        POIData = new ArrayList<POIData>();
        poiAdapter = new SearchPoiRecyclerViewAdapter();
        searchRecycler.setAdapter(poiAdapter);

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
                        if(i == EditorInfo.IME_ACTION_SEARCH || i == EditorInfo.IME_ACTION_NONE) {
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

    private void cancelSearch() {
        imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);
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
        wlp.height = (int)(display.getHeight() * 0.9f);
        getDialog().getWindow().setDimAmount(0);
        window.setAttributes(wlp);
    }

    // 주소 검색 리사이클러
    public class SearchPoiRecyclerViewAdapter
            extends RecyclerView.Adapter<SearchPoiRecyclerViewAdapter.ViewHolder> {

        SearchPoiRecyclerViewAdapter() { }

        public class ViewHolder extends RecyclerView.ViewHolder {
            final View view;
            final TextView locationItem;

            public ViewHolder(View view) {
                super(view);
                this.view = view;
                locationItem =
                        (TextView) view.findViewById(R.id.text_location_item);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.layout_search_location_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.locationItem.setText(POIData.get(position).name);
            holder.locationItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    searchListener.onPOISearch(POIData.get(position));
                    cancelSearch();
                    dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return POIData.size();
        }
    }

    // 지역검색 AsyncTask
    public class AsyncPoiJSONList extends AsyncTask<String, Integer, SearchPoiInfo> {

        Response response;

        @Override
        protected SearchPoiInfo doInBackground(String... args) {
            try{
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
                Response response = toServer.newCall(request).execute();
                ResponseBody responseBody = response.body();

                boolean flag = response.isSuccessful();
                int responseCode = response.code();
                if (responseCode >= 400) return null;
                if (flag) {
                    return ParseDataParseHandler.getJSONPoiList(
                            new StringBuilder(responseBody.string()));
                }
                responseBody.close();
            }catch (UnknownHostException une) {
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
        protected void onPostExecute(SearchPoiInfo result) {
            if(result != null && result.POIData.size() > 0){
                POIData.clear();
                for(int i = 0; i < result.POIData.size() ; i++) {
                    POIData.add(result.POIData.get(i));
                }
                poiAdapter.notifyDataSetChanged();
                searchRecycler.setAdapter(poiAdapter);

            }
        }
    }
}
