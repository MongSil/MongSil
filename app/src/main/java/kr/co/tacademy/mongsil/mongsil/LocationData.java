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
