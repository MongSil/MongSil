package kr.co.tacademy.mongsil.mongsil.JSONParsers;

import android.os.AsyncTask;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import kr.co.tacademy.mongsil.mongsil.Enums.DataEnum;
import kr.co.tacademy.mongsil.mongsil.Enums.SendEnum;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.util.Log.e;

/**
 * Created by Han on 2017-02-20.
 */

public class AsyncTaskJSONParser<T> extends AsyncTask<String, Void, T> {
    private DataEnum dataEnum;
    private ProcessResponse<T> func;

    public interface ProcessResponse<T> {
        void process(T result);
    }

    public AsyncTaskJSONParser(DataEnum dataEnum, ProcessResponse<T> func) {
        this.dataEnum = dataEnum;
        this.func = func;
    }

    // TODO JSON도 수정하기
    @Override
    protected T doInBackground(String... args) {
        StringBuilder data = null;
        switch (dataEnum) {
            case WEATHER_DATA:
                data = receiveFromServer(SendEnum.SK, String.format(
                        NetworkDefineConstant.SK_WEATHER_LAT_LON,
                        args[0], args[1]));
                return (T) ParseDataParseHandler.getJSONWeatherList(data);
            case STRING_DATA:
                if (dataEnum.isTypeEnum()) {
                    switch (dataEnum.getTypeEnum()) {
                        case RE_GEO:
                            data = receiveFromServer(SendEnum.SK, String.format(
                                    NetworkDefineConstant.SK_REVERSE_GEOCOING,
                                    args[0], args[1]));
                            return (T) ParseDataParseHandler.getJSONResGeo(data);
                        case REMOVE_POST:
                            data = receiveFromServer(SendEnum.SERVER, String.format(
                                    NetworkDefineConstant.DELETE_SERVER_POST_REMOVE,
                                    args[0]));
                            return (T) ParseDataParseHandler.getJSONGoodOrNo(data);
                        case REMOVE_REPLY:
                            data = receiveFromServer(SendEnum.SERVER, String.format(
                                    NetworkDefineConstant.DELETE_SERVER_REPLY_REMOVE,
                                    args[0], args[1]));
                            return (T) ParseDataParseHandler.getJSONGoodOrNo(data);
                        default:
                            Log.e("Error : ", "TypeEnum is not existed!");
                            return (T) args;
                    }
                }
            case USER_DATA:
                data = receiveFromServer(SendEnum.SERVER, String.format(
                        NetworkDefineConstant.GET_SERVER_USER_INFO,
                        args[0]));
                return (T) ParseDataParseHandler.getJSONUserInfo(data);
            case POST_DATA:
                data = receiveFromServer(SendEnum.SERVER, String.format(
                        NetworkDefineConstant.GET_SERVER_POST_DETAIL,
                        args[0]));
                return (T) ParseDataParseHandler.getJSONPostDetailRequestList(data);
            case REPLY_DATA:
                data = receiveFromServer(SendEnum.SERVER, String.format(
                        NetworkDefineConstant.GET_SERVER_POST_DETAIL_REPLY,
                        args[0], args[1]));
                return (T) ParseDataParseHandler.getJSONReplyRequestList(data);
            default:
                Log.e("Error : ", "DataEnum is not existed!");
                return (T) args;
        }
    }

    @Override
    protected void onPostExecute(T t) {
        func.process(t);
    }

    private static StringBuilder receiveFromServer(SendEnum sendEnum, String url) {
        Response response = null;
        try {
            OkHttpClient toServer = new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .build();
            response = toServer.newCall(makeRequest(sendEnum, url)).execute();
            ResponseBody responseBody = response.body();

            boolean flag = response.isSuccessful();
            int responseCode = response.code();
            if (responseCode >= 400) return null;
            if (flag) {
                return new StringBuilder(responseBody.string());
            }
        } catch (UnknownHostException une) {
            e("connectionFail", une.toString());
        } catch (UnsupportedEncodingException uee) {
            e("connectionFail", uee.toString());
        } catch (Exception e) {
            e("connectionFail", e.toString());
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return null;
    }

    private static Request makeRequest(SendEnum sendEnum, String url) {
        Request.Builder request = new Request.Builder();
        if (sendEnum.equals(SendEnum.SK)) {
            request.addHeader("Accept", "application/json")
                    .addHeader("appKey", NetworkDefineConstant.SK_APP_KEY);
        }
        return request.url(url).build();
    }

}