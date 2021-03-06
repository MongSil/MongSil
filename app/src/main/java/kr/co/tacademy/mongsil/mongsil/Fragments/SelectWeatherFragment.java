package kr.co.tacademy.mongsil.mongsil.Fragments;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import kr.co.tacademy.mongsil.mongsil.R;
import kr.co.tacademy.mongsil.mongsil.JSONParsers.Models.WeatherData;

/**
 * Created by Han on 2016-08-15.
 */
@Deprecated
public class SelectWeatherFragment extends Fragment {
    private static final String POSITION = "position";

    public interface OnSelectWeatherListener {
        public void onSelectWeather(int position);
    }
    OnSelectWeatherListener selectWeatherListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSelectWeatherListener) {
            selectWeatherListener = (OnSelectWeatherListener) context;
        }
    }

    public static SelectWeatherFragment newInstance(int position) {
        SelectWeatherFragment fragment = new SelectWeatherFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(POSITION, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    ImageView imgWeatherIcon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view =
                inflater.inflate(R.layout.layout_posting_select_weather, container, false);
        int position = getArguments().getInt("position");
        selectWeatherListener.onSelectWeather(position);

        imgWeatherIcon = (ImageView) view.findViewById(R.id.img_weather_icon);
        imgWeatherIcon.setImageResource(
                WeatherData.imgFromWeatherCode(String.valueOf(position), 0));
        imgWeatherIcon.setAnimation(AnimationApplyInterpolater(
                R.anim.bounce_interpolator, new LinearInterpolator()));
        AnimationDrawable animation =
                (AnimationDrawable) imgWeatherIcon.getDrawable();
        if(animation != null) {
            animation.start();
        }

        return view;
    }

    // 애니메이션 인터폴레이터 적용
    private Animation AnimationApplyInterpolater(
            int resourceId, final Interpolator interpolator) {
        Animation animation = AnimationUtils.loadAnimation(getContext(), resourceId);
        animation.setInterpolator(interpolator);
        return animation;
    }

}
