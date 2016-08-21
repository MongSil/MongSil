package kr.co.tacademy.mongsil.mongsil;

import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

public class PostPreviewDialogFragment extends DialogFragment {
    private static final String LOCATION = "location";
    private static final String CONTENT = "content";
    private static final String WEATHER_POS = "weather_pos";
    private static final String PHOTO = "photo";

    private String location;
    private String content;
    private int weatherPos;
    private Bitmap photo;


    public PostPreviewDialogFragment() {
    }

    public static PostPreviewDialogFragment newInstance(
            String location, String content, int weatherPos, Bitmap photo) {
        PostPreviewDialogFragment fragment = new PostPreviewDialogFragment();
        Bundle args = new Bundle();
        args.putString(LOCATION, location);
        args.putString(CONTENT, content);
        args.putInt(WEATHER_POS, weatherPos);
        args.putParcelable(PHOTO, photo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            location = getArguments().getString(LOCATION);
            content = getArguments().getString(CONTENT);
            weatherPos = getArguments().getInt(WEATHER_POS);
            photo = getArguments().getParcelable(PHOTO);
        }
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme);
    }

    ImageView imgClose, imgBackground, imgWeatherIcon;
    TextView postContent, postLocation, postName, postTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_post_preview, container, false);

        imgClose = (ImageView) view.findViewById(R.id.img_close_x);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        imgBackground = (ImageView) view.findViewById(R.id.img_preview_background);
        imgBackground.setBackgroundResource(R.drawable.sign_up_background);
        if(photo != null) {
            imgBackground.setBackgroundResource(
                    WeatherData.imgFromWeatherCode(String.valueOf(weatherPos), 3));
        } else {

        }

        postContent = (TextView) view.findViewById(R.id.text_post_content);
        postContent.setText(content);

        imgWeatherIcon = (ImageView) view.findViewById(R.id.img_weather_icon);
        imgWeatherIcon.setImageResource(
                WeatherData.imgFromWeatherCode(String.valueOf(weatherPos), 0));
        imgWeatherIcon.setAnimation(AnimationApplyInterpolater(
                R.anim.bounce_interpolator, new LinearInterpolator()));
        if(imgWeatherIcon.isShown()) {
            ((AnimationDrawable) imgWeatherIcon.getDrawable()).start();
        }

        postLocation = (TextView) view.findViewById(R.id.text_post_location);
        postLocation.setText(location);

        postName = (TextView) view.findViewById(R.id.text_post_name);
        postName.setText(PropertyManager.getInstance().getNickname());

        postTime = (TextView) view.findViewById(R.id.text_post_time);
        postTime.setText(TimeData.PreviewPostTime());


        return view;
    }

    // 애니메이션 인터폴레이터 적용
    private Animation AnimationApplyInterpolater(
            int resourceId, final Interpolator interpolator) {
        Animation animation = AnimationUtils.loadAnimation(getContext(), resourceId);
        animation.setInterpolator(interpolator);
        return animation;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        window.setAttributes(wlp);
    }

    @Override
    public void onStop() {
        super.onStop();
        dismissAllowingStateLoss();
    }
}
