package kr.co.tacademy.mongsil.mongsil;

/**
 * Created by Han on 2016-08-04.
 */
// 시는 17개, 시와 구는 280개, 시와 구와 동까지는 5330개
public class LocationData {
    public static String[] locationName = {
            "인천",
            "서울",
            "강원",
            "충주",
            "대전",
            "대구",
            "광주",
            "전주",
            "울산",
            "부산",
            "제주"
    };

    public static String[] locationCutName = {
        "경기",
        "충청",
        "경상",
        "전라"
    };

    public static String[] latList = {
            "37.4548790",
            "37.5670652",
            "37.8846254",
            "36.6355987",
            "36.3512682",
            "35.8713449",
            "35.1626730",
            "35.8190741",
            "35.5391178",
            "35.1788483",
            "33.4999915"
    };

    public static String[] lonList = {
            "126.7050769",
            "126.9772433",
            "127.7295267",
            "127.4904730",
            "127.3845458",
            "128.6006733",
            "126.9152019",
            "127.1059460",
            "129.3125368",
            "129.0758175",
            "126.5309697"
    };

    public static String ChangeToShortName(String area) {
        String cutName = area.substring(0, 3);
        if(cutName.equals(locationCutName[0])) { // 경기
            cutName = locationName[1];      // 서울
        } else if (cutName.equals(locationCutName[1])) { // 충청
            cutName = locationName[3];       // 충주
        } else if (cutName.equals(locationCutName[2])) { // 경상
            cutName = locationName[8];       // 울산
        } else if (cutName.equals(locationCutName[3])) { // 전라
            cutName = locationName[7];       // 전주
        }
        return cutName;
    }

    public static String[] ChangeToLatLon(String area) {
        int position = 0;
        String[] latLon;

        for(;position < locationName.length ; position++) {
            if(locationName[position].equals(area)) {
                break;
            }
        }

        latLon = new String[]{latList[position], lonList[position]};

        return latLon;
    }
}
