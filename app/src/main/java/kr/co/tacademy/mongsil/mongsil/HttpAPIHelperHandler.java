/*
 *   Http 통신을 컨트롤하는 역할을 담당
 */
package kr.co.tacademy.mongsil.mongsil;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class HttpAPIHelperHandler {
    private static final String DEBUG_TAG = "HttpAPIHelperHandler";

    public static ArrayList<Post> PostJSONAllSelect() {
        ArrayList<Post> posts = null;
        BufferedReader jsonStreamData = null;
        HttpURLConnection connection = null;
        try {
            connection = HttpConnectionManager.getHttpURLConnection(
                    NetworkDefineConstant.SERVER_POST);

            int responseCode = connection.getResponseCode();

            if (HttpURLConnection.HTTP_OK == responseCode) {
                jsonStreamData = new BufferedReader(new
                        InputStreamReader(connection.getInputStream()));
                StringBuilder jsonBuf = new StringBuilder();
                String jsonLine = "";
                while ((jsonLine = jsonStreamData.readLine()) != null) {
                    jsonBuf.append(jsonLine);
                }
                posts = ParseDataParseHandler.
                                getJSONPostRequestAllList(jsonBuf);
            }

        } catch (IOException e) {
            Log.e("postJSONAllSelect()", e.toString());
        } finally {
            if (jsonStreamData != null) {
                try {
                    jsonStreamData.close();
                } catch (IOException ioe) {
                }
            }
            if (connection != null) connection.disconnect();

        }

        return posts;
    }
}