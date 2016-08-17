package kr.co.tacademy.mongsil.mongsil;

import java.util.ArrayList;

/**
 * Created by ccei on 2016-08-08.
 */
public class SearchPOIInfo {
    int totalCount;
    int page;
    ArrayList<POIData> POIData;

    SearchPOIInfo(ArrayList<POIData> POIData) {
        this.POIData = POIData;
    }
}
