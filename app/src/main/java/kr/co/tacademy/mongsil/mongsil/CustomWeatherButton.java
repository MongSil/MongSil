package kr.co.tacademy.mongsil.mongsil;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by ccei on 2016-08-10.
 */
public class CustomWeatherButton extends RelativeLayout {
    TextView locationName;
    ImageView imgLocationBackground, imgLocationWeatherIcon;

    public CustomWeatherButton(Context context) {
        super(context);
        initView();
    }

    public CustomWeatherButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        getAttrs(attrs);
    }

    public CustomWeatherButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        initView();
        getAttrs(attrs, defStyle);
    }

    private void initView() {
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        View v = li.inflate(R.layout.layout_weather_button, this, false);
        addView(v);

        locationName = (TextView) findViewById(R.id.text_location_name);
        imgLocationBackground = (ImageView) findViewById(R.id.img_location_weather_background);
        imgLocationWeatherIcon = (ImageView) findViewById(R.id.img_location_weather_icon);
    }

    private void getAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.WeatherButton);
        setTypeArray(typedArray);
    }

    private void getAttrs(AttributeSet attrs, int defStyle) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.WeatherButton, defStyle, 0);
        setTypeArray(typedArray);

    }

    private void setTypeArray(TypedArray typedArray) {

        int imgLocationBackground_resID = typedArray
                .getResourceId(
                        R.styleable.WeatherButton_imgLocationBackground,
                        R.drawable.shape_weather_button);
        imgLocationBackground.setBackgroundResource(imgLocationBackground_resID);

        int imgLocationWeatherIcon_resID = typedArray
                .getResourceId(
                        R.styleable.WeatherButton_imgLocationWeatherIcon,
                        R.drawable.anim_list_icon_mongsil_smallrain);
        imgLocationWeatherIcon.setImageResource(imgLocationWeatherIcon_resID);

        String text_string = typedArray.getString(R.styleable.WeatherButton_textLocationName);
        locationName.setText(text_string);

        typedArray.recycle();

    }


    void setLocationName(String text_string) {
        locationName.setText(text_string);
    }

    void setImgLocationBackground(int resID) {
        imgLocationBackground.setBackgroundResource(resID);
    }

    void setImgLocationWeatherIcon(int resID) {
        imgLocationWeatherIcon.setImageResource(resID);
    }

}
