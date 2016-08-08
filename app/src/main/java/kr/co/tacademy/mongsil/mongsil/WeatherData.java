package kr.co.tacademy.mongsil.mongsil;

/**
 * Created by ccei on 2016-08-08.
 */
public class WeatherData {
    String name;
    String code;

    /*  4X
        1 -> sunny		1
        2 -> littlecloud	2_
        3 -> manycloud		2_
        4 -> small_rain		6
        5 -> tiny snow		9
        6 -> manyrainsnow	3
        7 -> graycloud		5
        8 -> strong_rain	7
        9 -> big_snow		10
        10-> manycloudsnowrain  8_
        11-> bolt		11
        12-> bolt_rain		8_
        13-> bolt_snow		8_
        14-> manyrainsnow	3
        */
    public static int imgFromWeatherCode(String code) {
        if (code.equals("SKY_S00")) {
            // 상태없음
            return 0;
        }
        if (code.equals("SKY_S01")) {
            // 맑음
            return R.drawable.anim_list_icon_mongsil_sunny;
        }
        if (code.equals("SKY_S02")) {
            // 구름조금
            return R.drawable.anim_list_icon_mongsil_littlecloud;
        }
        if (code.equals("SKY_S03")) {
            // 구름많음
            return R.drawable.anim_list_icon_mongsil_manycloud;
        }
        if (code.equals("SKY_S04")) {
            // 구름많고 비
            return R.drawable.anim_list_icon_mongsil_smallrain;
        }
        if (code.equals("SKY_S05")) {
            // 구름많고 눈
            return R.drawable.anim_list_icon_mongsil_tiny_snow;
        }
        if (code.equals("SKY_S06")) {
            // 구름많고 비 또는 눈
            return R.drawable.anim_list_icon_mongsil_manycloud_rain_snow;
        }
        if (code.equals("SKY_S07")) {
            // 흐림
            return R.drawable.anim_list_icon_mongsil_gray_cloud;
        }
        if (code.equals("SKY_S08")) {
            // 흐리고 비
            return R.drawable.anim_list_icon_mongsil_strong_rain;
        }
        if (code.equals("SKY_S09")) {
            // 흐리고 눈
            return R.drawable.anim_list_icon_mongsil_big_snow;
        }
        if (code.equals("SKY_S10")) {
            // 흐리고 비 또는 눈
            return R.drawable.anim_list_icon_mongsil_manycloud_snow_rain;
        }
        if (code.equals("SKY_S11")) {
            // 흐리고 낙뢰
            return R.drawable.anim_list_icon_mongsil_bolt;
        }
        if (code.equals("SKY_S12")) {
            // 뇌우, 비
            return R.drawable.anim_list_icon_mongsil_rain_bolt;
        }
        if (code.equals("SKY_S13")) {
            // 뇌우, 눈
            return R.drawable.anim_list_icon_mongsil_snow_bolt;
        }
        if (code.equals("SKY_S14")) {
            // 뇌우, 비 또는 눈
            return R.drawable.anim_list_icon_mongsil_manycloud_rain_snow;
        }
        return 0;
    }
}
