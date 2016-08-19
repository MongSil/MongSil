package kr.co.tacademy.mongsil.mongsil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

    ImageView selectLocationBackground;
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

        selectLocationBackground =
                (ImageView) view.findViewById(R.id.img_select_location_background);
        //selectLocationBackground.setAlpha(0.5f);
        WindowManager wm;
        wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Bitmap emptyBitmap = Bitmap.createBitmap(display.getWidth(), display.getHeight(), null);
        selectLocationBackground.setImageBitmap(BlurBuilder.blur(emptyBitmap, 15));
        selectLocationRecycle =
                (RecyclerView) view.findViewById(R.id.signup_location_recycler);
        selectLocationRecycle.setLayoutManager(
                new GridLayoutManager(getActivity().getApplicationContext(), 3));

        // 지역 자료(arrays)
        String[] locations = LocationData.locationName;
        int[] imgLocations = {
                R.drawable.incheon, R.drawable.seoul, R.drawable.gangwon,
                R.drawable.cheonju, R.drawable.deajeon, R.drawable.deagu,
                R.drawable.gwangju, R.drawable.jeonju, R.drawable.ulsan,
                R.drawable.busan, R.drawable.jeju
        };
        for (int i = 0; i < locations.length; i++) {
            locationNames.add(locations[i]);
            locationImgs.add(imgLocations[i]);
        }

        /*private Bitmap BlurImage(Bitmap input) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            RenderScript rsScript = RenderScript.create(getActivity());
            Allocation alloc = Allocation.createFromBitmap(rsScript, input);
            ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rsScript, alloc.getElement());
            blur.setRadius(12);
            blur.setInput(alloc);
            Bitmap result = Bitmap.createBitmap(input.getWidth(), input.getHeight(), input.getConfig());
            Allocation outAlloc = Allocation.createFromBitmap(rsScript, result);
            blur.forEach(outAlloc);
            outAlloc.copyTo(result);
            rsScript.destroy();
            return result;
        }
    }*/
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
            private final ImageView imgSelector;
            private final TextView location;

            public ViewHolder(View itemView) {
                super(itemView);
                imgLocation = (CircleImageView) itemView.findViewById(R.id.img_signup_location_item);
                imgSelector = (ImageView) itemView.findViewById(R.id.img_selector);
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
            viewHolder.location.setText(locationNames.get(position));
            viewHolder.imgSelector.setSelected(selectedPos == position);
            if(viewHolder.imgSelector.isSelected()) {
                viewHolder.imgSelector.setVisibility(View.VISIBLE);
            } else {
                viewHolder.imgSelector.setVisibility(View.GONE);
            }
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
        WindowManager.LayoutParams wlp = new WindowManager.LayoutParams();
        // TODO : 블러 배경
        wlp.copyFrom(window.getAttributes());
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(wlp);
    }
}