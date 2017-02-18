package kr.co.tacademy.mongsil.mongsil.Adapters;

import kr.co.tacademy.mongsil.mongsil.JSONParsers.Models.POIData;

/**
 * Created by ccei on 2016-08-16.
 */
public interface POIAdapterCallback {
    void onMarkCallback(boolean select, POIData poiData);
    void onSelectCallback(POIData poiData);
}