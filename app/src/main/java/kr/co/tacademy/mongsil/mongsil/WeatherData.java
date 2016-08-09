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
        request -> 0 : 아이콘 리소스
                   1 : 배경 리소스
        */
    public static int imgFromWeatherCode(String code, int request) {
        if (code.equals("SKY_A00") || code.equals("0")) {
            // 상태없음
            if(request == 0) {
                return 0;
            }
        }
        if (code.equals("SKY_A01") || code.equals("1")) {
            // 맑음
            if(request == 0) {
                return R.drawable.anim_list_icon_mongsil_sunny;
            }
        }
        if (code.equals("SKY_A02") || code.equals("2")) {
            // 구름조금
            if(request == 0) {
                return R.drawable.anim_list_icon_mongsil_littlecloud;
            }
        }
        if (code.equals("SKY_A03") || code.equals("3")) {
            // 구름많음
            if(request == 0) {
                return R.drawable.anim_list_icon_mongsil_manycloud;
            }
        }
        if (code.equals("SKY_A04") || code.equals("4")) {
            // 구름많고 비
            if(request == 0) {
                return R.drawable.anim_list_icon_mongsil_smallrain;
            }
        }
        if (code.equals("SKY_A05") || code.equals("5")) {
            // 구름많고 눈
            if(request == 0) {
                return R.drawable.anim_list_icon_mongsil_tiny_snow;
            }
        }
        if (code.equals("SKY_A06") || code.equals("6")) {
            // 구름많고 비 또는 눈
            if(request == 0) {
                return R.drawable.anim_list_icon_mongsil_manycloud_rain_snow;
            }
        }
        if (code.equals("SKY_A07") || code.equals("7")) {
            // 흐림
            if(request == 0) {
                return R.drawable.anim_list_icon_mongsil_gray_cloud;
            }
        }
        if (code.equals("SKY_A08") || code.equals("8")) {
            // 흐리고 비
            if(request == 0) {
                return R.drawable.anim_list_icon_mongsil_strong_rain;
            }
        }
        if (code.equals("SKY_A09") || code.equals("9")) {
            // 흐리고 눈
            if(request == 0) {
                return R.drawable.anim_list_icon_mongsil_big_snow;
            }
        }
        if (code.equals("SKY_A10") || code.equals("10")) {
            // 흐리고 비 또는 눈
            if(request == 0) {
                return R.drawable.anim_list_icon_mongsil_manycloud_snow_rain;
            }
        }
        if (code.equals("SKY_A11") || code.equals("11")) {
            // 흐리고 낙뢰
            if(request == 0) {
                return R.drawable.anim_list_icon_mongsil_bolt;
            }
        }
        if (code.equals("SKY_A12") || code.equals("12")) {
            // 뇌우, 비
            if(request == 0) {
                return R.drawable.anim_list_icon_mongsil_rain_bolt;
            }
        }
        if (code.equals("SKY_A13") || code.equals("13")) {
            // 뇌우, 눈
            if(request == 0) {
                return R.drawable.anim_list_icon_mongsil_snow_bolt;
            }
        }
        if (code.equals("SKY_A14") || code.equals("14")) {
            // 뇌우, 비 또는 눈
            if(request == 0) {
                return R.drawable.anim_list_icon_mongsil_manycloud_rain_snow;
            }
        }
        return 0;
    }
}
