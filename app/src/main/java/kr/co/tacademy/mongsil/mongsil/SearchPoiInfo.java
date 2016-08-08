package kr.co.tacademy.mongsil.mongsil;

import java.util.ArrayList;

/**
 * Created by ccei on 2016-08-08.
 */
public class SearchPoiInfo {
    int totalCount;
    int page;
    ArrayList<Poi> poi;

    SearchPoiInfo(ArrayList<Poi> poi) {
        this.poi = poi;
    }
}
