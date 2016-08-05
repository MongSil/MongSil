package kr.co.tacademy.mongsil.mongsil;

import org.json.JSONObject;

/**
 * Created by ccei on 2016-08-05.
 */
public class Page implements JSONParseHandler {
    int totalCount;
    int count;
    @Override
    public void setData(JSONObject jsonObject) {
        totalCount = jsonObject.optInt("totalCount");
        count = jsonObject.optInt("count");
    }
}
