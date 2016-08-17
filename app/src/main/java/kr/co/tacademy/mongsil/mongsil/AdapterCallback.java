package kr.co.tacademy.mongsil.mongsil;

/**
 * Created by ccei on 2016-08-16.
 */
public interface AdapterCallback {
    void onMarkCallback(boolean select, POIData poiData, int position);
    void onSelectCallback(POIData poiData);
}