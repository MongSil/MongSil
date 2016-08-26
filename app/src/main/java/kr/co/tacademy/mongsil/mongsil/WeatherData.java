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
                   1 : 메인 배경 리소스
                   2 : 메인 배경 애니메이션 리소스
                   3 : 지도 원 배경 리소스
                   4 : 글 작성, 글 상세보기 배경 리소스
                   5 : 지도 아이콘 리소스
        */
    public static int imgFromWeatherCode(String code, int request) {
        if (code.equals("SKY_A01") || code.equals("0")) {
            // 맑음
            if(request == 0) {
                return R.drawable.anim_list_icon_mongsil_sunny;
            } else if(request == 1) {
                return R.drawable.background_snow;
            } else if(request == 2) {
                return R.drawable.anim_list_gray_cloud;
            } else if(request == 3) {
                return R.drawable.circle_sunny;
            } else if(request == 4) {
                return R.drawable.background_sunny;
            } else if(request == 5) {
                return R.drawable.map_icon_mongsil_sunny;
            }
        }
        if (code.equals("SKY_A02") || code.equals("1")) {
            // 구름조금
            if(request == 0) {
                return R.drawable.anim_list_icon_mongsil_littlecloud;
            } else if(request == 1) {
                return R.drawable.background_graycloud;
            } else if(request == 2) {
                return R.drawable.anim_list_gray_cloud;
            } else if(request == 3) {
                return R.drawable.circle_cloud;
            } else if(request == 4) {
                return R.drawable.background_smallcloud;
            } else if(request == 5) {
                return R.drawable.map_icon_mongsil_littlecloud;
            }
        }
        if (code.equals("SKY_A03") || code.equals("2")) {
            // 구름많음
            if(request == 0) {
                return R.drawable.anim_list_icon_mongsil_manycloud;
            } else if(request == 1) {
                return R.drawable.background_graycloud;
            } else if(request == 2) {
                return R.drawable.anim_list_gray_cloud;
            } else if(request == 3) {
                return R.drawable.circle_cloud;
            } else if(request == 4) {
                return R.drawable.background_manycloud;
            } else if(request == 5) {
                return R.drawable.map_icon_mongsil_manycloud;
            }
        }
        if (code.equals("SKY_A04") || code.equals("3")) {
            // 구름많고 비
            if(request == 0) {
                return R.drawable.anim_list_icon_mongsil_manycloud_rain;
            } else if(request == 1) {
                return R.drawable.background_graycloud;
            } else if(request == 2) {
                return R.drawable.anim_list_small_rain;
            } else if(request == 3) {
                return R.drawable.circle_small_rain;
            } else if(request == 4) {
                return R.drawable.background_manycloud_rain;
            } else if(request == 5) {
                return R.drawable.map_icon_mongsil_manycloud_rain;
            }
        }
        if (code.equals("SKY_A05") || code.equals("4")) {
            // 구름많고 눈
            if(request == 0) {
                return R.drawable.anim_list_icon_mongsil_manycloud_snow;
            } else if(request == 1) {
                return R.drawable.background_graycloud;
            } else if(request == 2) {
                return R.drawable.anim_list_snow;
            } else if(request == 3) {
                return R.drawable.circle_snow;
            } else if(request == 4) {
                return R.drawable.background_manycloud_snow;
            } else if(request == 5) {
                return R.drawable.map_icon_mongsil_manycloud_snow;
            }
        }
        if (code.equals("SKY_A06") || code.equals("5")) {
            // 구름많고 비 또는 눈
            if(request == 0) {
                return R.drawable.anim_list_icon_mongsil_manycloud_rain_snow;
            } else if(request == 1) {
                return R.drawable.background_graycloud;
            } else if(request == 2) {
                return R.drawable.anim_list_small_rain;
            } else if(request == 3) {
                return R.drawable.circle_cloud;
            } else if(request == 4) {
                return R.drawable.background_manycloud_rain_snow;
            } else if(request == 5) {
                return R.drawable.map_icon_mongsil_gray_rain_snow;
            }
        }
        if (code.equals("SKY_A07") || code.equals("6")) {
            // 흐림
            if(request == 0) {
                return R.drawable.anim_list_icon_mongsil_gray_cloud;
            } else if(request == 1) {
                return R.drawable.background_graycloud;
            } else if(request == 2) {
                return R.drawable.anim_list_gray_cloud;
            } else if(request == 3) {
                return R.drawable.circle_cloud;
            } else if(request == 4) {
                return R.drawable.background_gray;
            } else if(request == 5) {
                return R.drawable.map_icon_mongsil_graycloud;
            }
        }
        if (code.equals("SKY_A08") || code.equals("7")) {
            // 흐리고 비
            if(request == 0) {
                return R.drawable.anim_list_icon_mongsil_smallrain;
            } else if(request == 1) {
                return R.drawable.background_small_rain;
            } else if(request == 2) {
                return R.drawable.anim_list_small_rain;
            } else if(request == 3) {
                return R.drawable.circle_small_rain;
            } else if(request == 4) {
                return R.drawable.background_grayrain;
            } else if(request == 5) {
                return R.drawable.map_icon_mongsil_gray_rain;
            }
        }
        if (code.equals("SKY_A09") || code.equals("8")) {
            // 흐리고 눈
            if(request == 0) {
                return R.drawable.anim_list_icon_mongsil_gray_snow;
            } else if(request == 1) {
                return R.drawable.background_snow;
            } else if(request == 2) {
                return R.drawable.anim_list_snow;
            } else if(request == 3) {
                return R.drawable.circle_snow;
            } else if(request == 4) {
                return R.drawable.background_graysnow;
            } else if(request == 5) {
                return R.drawable.map_icon_mongsil_gray_snow;
            }
        }
        if (code.equals("SKY_A10") || code.equals("9")) {
            // 흐리고 비 또는 눈
            if(request == 0) {
                return R.drawable.anim_list_icon_mongsil_gray_rain_snow;
            } else if(request == 1) {
                return R.drawable.background_graycloud;
            } else if(request == 2) {
                return R.drawable.anim_list_small_rain;
            } else if(request == 3) {
                return R.drawable.circle_cloud;
            } else if(request == 4) {
                return R.drawable.background_gray_rain_snow;
            } else if(request == 5) {
                return R.drawable.map_icon_mongsil_gray_rain_snow;
            }
        }
        if (code.equals("SKY_A11") || code.equals("10")) {
            // 흐리고 낙뢰
            if(request == 0) {
                return R.drawable.anim_list_icon_mongsil_gray_bolt;
            } else if(request == 1) {
                return R.drawable.background_graycloud;
            } else if(request == 2) {
                return R.drawable.anim_list_bolt;
            } else if(request == 3) {
                return R.drawable.circle_bolt;
            } else if(request == 4) {
                return R.drawable.background_gray_bolt;
            } else if(request == 5) {
                return R.drawable.map_icon_mongsil_graycloud_bolt;
            }
        }
        if (code.equals("SKY_A12") || code.equals("11")) {
            // 뇌우, 비
            if(request == 0) {
                return R.drawable.anim_list_icon_mongsil_bolt_rain;
            } else if(request == 1) {
                return R.drawable.background_strong_rain;
            } else if(request == 2) {
                return R.drawable.anim_list_bolt;
            }  else if(request == 3) {
                return R.drawable.circle_bolt;
            } else if(request == 4) {
                return R.drawable.background_bolt_rain;
            } else if(request == 5) {
                return R.drawable.map_icon_mongsil_boltrain;
            }
        }
        if (code.equals("SKY_A13") || code.equals("12")) {
            // 뇌우, 눈
            if(request == 0) {
                return R.drawable.anim_list_icon_mongsil_bolt_snow;
            } else if(request == 1) {
                return R.drawable.background_strong_rain;
            } else if(request == 2) {
                return R.drawable.anim_list_bolt;
            } else if(request == 3) {
                return R.drawable.circle_bolt;
            } else if(request == 4) {
                return R.drawable.background_bolt_snow;
            } else if(request == 5) {
                return R.drawable.map_icon_mongsil_boltsnow;
            }
        }
        if (code.equals("SKY_A14") || code.equals("13")) {
            // 뇌우, 비 또는 눈
            if(request == 0) {
                return R.drawable.anim_list_icon_mongsil_bolt_rain_snow;
            } else if(request == 1) {
                return R.drawable.background_strong_rain;
            } else if(request == 2) {
                return R.drawable.anim_list_bolt;
            } else if(request == 3) {
                return R.drawable.circle_bolt;
            } else if(request == 4) {
                return R.drawable.background_bolt_rain_snow;
            } else if(request == 5) {
                return R.drawable.map_icon_mongsil_bolt_rain_snow;
            }
        }
        return 0;
    }
}
