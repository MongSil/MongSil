package kr.co.tacademy.mongsil.mongsil.JSONParsers.Models;

import java.util.ArrayList;

/**
 * Created by ccei on 2016-08-08.
 */
public class SearchPOIInfo {
    int totalCount;
    int page;
    ArrayList<POIData> POIData;

    public SearchPOIInfo(ArrayList<POIData> POIData) {
        this.POIData = POIData;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public ArrayList<kr.co.tacademy.mongsil.mongsil.JSONParsers.Models.POIData> getPOIData() {
        return POIData;
    }

    public void setPOIData(ArrayList<kr.co.tacademy.mongsil.mongsil.JSONParsers.Models.POIData> POIData) {
        this.POIData = POIData;
    }
}
