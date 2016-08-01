package kr.co.tacademy.mongsil.mongsil;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skp.Tmap.TMapData;

/**
 * Created by ccei on 2016-08-02.
 */
public class SearchLocationDialogFragment extends DialogFragment {
    //private TMapTapi tmaptapi = new TMapTapi(MongSilApplication.getMongSilContext());

    //ArrayList<TMapPOIItem> POIItem = null;
    //String[] recyclerItem;

    TextView emptySearch;
    EditText editSearch;
    ImageView imgSearchCancel;

    public SearchLocationDialogFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.dialog_select_locaiton, container, false);
       /* tmapdata = new TMapData();
        tmaptapi.setSKPMapAuthentication (NetworkDefineConstant.APP_KEY);
        tmaptapi.setOnAuthenticationListener(new TMapTapi.OnAuthenticationListenerCallback() {
            @Override
            public void SKPMapApikeySucceed() {
                // pass
            }

            @Override
            public void SKPMapApikeyFailed(String s) {
                //onDismiss(getDialog());
            }
        });*/

        final LinearLayout searchContainer =
                (LinearLayout) view.findViewById(R.id.search_container);
        emptySearch = (TextView) view.findViewById(R.id.text_empty_search);
        editSearch = (EditText) view.findViewById(R.id.edit_search);
        imgSearchCancel = (ImageView) view.findViewById(R.id.img_search_cancel);
        final RecyclerView searchRecycler =
                (RecyclerView) view.findViewById(R.id.search_recycler);
        searchRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        final LocationSearchRecyclerViewAdapter adapter
                                = new LocationSearchRecyclerViewAdapter();
        searchRecycler.setAdapter(adapter);

        searchContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emptySearch.setVisibility(View.GONE);
                editSearch.setVisibility(View.VISIBLE);
                imgSearchCancel.setVisibility(View.VISIBLE);
                imgSearchCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        emptySearch.setVisibility(View.VISIBLE);
                        editSearch.setVisibility(View.GONE);
                        editSearch.setText("");
                        imgSearchCancel.setVisibility(View.GONE);
                    }
                });
                editSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        /*try {
                            //tmaptapi.invokeSearchPortal(editable.toString());
                            //POIItem = tmapdata.findAddressPOI(editable.toString());
                        } catch (IOException ie) {
                            ie.printStackTrace();
                        } catch (ParserConfigurationException pce) {
                            pce.printStackTrace();
                        } catch (SAXException se) {
                            se.printStackTrace();
                        }*/
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        //wlp.windowAnimations = R.style.BottomDialogAnimation;
        //wlp.gravity = Gravity.BOTTOM;
        getDialog().getWindow().setDimAmount(0);
        window.setAttributes(wlp);
    }

    // 주소 검색 리사이클러
    public class LocationSearchRecyclerViewAdapter
            extends RecyclerView.Adapter<LocationSearchRecyclerViewAdapter.ViewHolder> {

        LocationSearchRecyclerViewAdapter() { }

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
        public void onBindViewHolder(ViewHolder holder, int position) {
            /*if(POIItem.get(position) != null)
                holder.locationItem.setText(POIItem.get(position).getPOIName());*/
        }

        @Override
        public int getItemCount() {
            return 0;//POIItem.size();
        }
    }

}
