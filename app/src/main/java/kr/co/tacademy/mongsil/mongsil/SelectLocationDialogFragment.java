package kr.co.tacademy.mongsil.mongsil;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ccei on 2016-08-09.
 */
public class SelectLocationDialogFragment extends DialogFragment {
    public interface OnSelectLocationListener {
        void onSelectLocation(String selectLocation);
    }

    OnSelectLocationListener selectListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSelectLocationListener) {
            selectListener = (OnSelectLocationListener) context;
        }
    }



    public SelectLocationDialogFragment() {
    }

    // 툴바
    ImageView tbImgClose;
    TextView tbDone;

    ArrayList<String> locationNames;
    ArrayList<Integer> locationImgs;


    RecyclerView selectLocationRecycle;
    private int selectedPos = 0;

    public static SelectLocationDialogFragment newInstance() {
        SelectLocationDialogFragment fragment =
                new SelectLocationDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme);
        locationNames = new ArrayList<String>();
        locationImgs = new ArrayList<Integer>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_signup_select_location, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((BaseActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((BaseActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
        tbImgClose = (ImageView) view.findViewById(R.id.img_close);
        tbImgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        tbDone = (TextView) view.findViewById(R.id.text_done);

        selectLocationRecycle =
                (RecyclerView) view.findViewById(R.id.signup_location_recycler);
        selectLocationRecycle.setLayoutManager(
                new GridLayoutManager(getActivity().getApplicationContext(), 3));

        // 지역 자료(arrays)
        String[] locations = LocationData.locationName;
        int[] imgLocations = {
                R.drawable.incheon, R.drawable.seoul, R.drawable.gangwon,
                R.drawable.cheonju, 0, R.drawable.deagu,
                R.drawable.gwangju, R.drawable.jeonju, R.drawable.ulsan,
                R.drawable.busan, R.drawable.jeju
        };
        for (int i = 0; i < locations.length; i++) {
            locationNames.add(locations[i]);
            locationImgs.add(imgLocations[i]);
        }

        selectLocationRecycle.setAdapter(new SelectLocationViewAdapter());

        tbDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectListener.onSelectLocation(locationNames.get(selectedPos));
                dismiss();
            }
        });
        return view;
    }

    public class SelectLocationViewAdapter
            extends RecyclerView.Adapter<SelectLocationViewAdapter.ViewHolder> {

        SelectLocationViewAdapter() {
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final CircleImageView imgLocation;
            private final CircleImageView imgSelector;
            private final TextView location;

            public ViewHolder(View itemView) {
                super(itemView);
                imgLocation = (CircleImageView) itemView.findViewById(R.id.img_signup_location_item);
                imgSelector = (CircleImageView) itemView.findViewById(R.id.img_selector);
                location = (TextView) itemView.findViewById(R.id.text_signup_location_item);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            View view = inflater.inflate(
                    R.layout.layout_select_location_item, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
            // TODO : viewHolder.imgLocation.setImageResource();
            viewHolder.location.setText(locationNames.get(position));
            viewHolder.imgSelector.setSelected(selectedPos == position);
            viewHolder.imgLocation.setImageResource(locationImgs.get(position));
            viewHolder.imgLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    notifyItemChanged(selectedPos);
                    selectedPos = position;
                    notifyItemChanged(selectedPos);
                }
            });
        }

        @Override
        public int getItemCount() {
            return locationNames.size();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        window.setBackgroundDrawable(new ColorDrawable(0x7000000));
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }
}