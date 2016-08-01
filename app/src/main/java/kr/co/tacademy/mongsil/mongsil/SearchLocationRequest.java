package kr.co.tacademy.mongsil.mongsil;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;

/**
 * Created by ccei on 2016-08-02.
 */
public class SearchLocationRequest extends NetworkRequest<Post>{
    String area1, area2;
    String userId = null;
    int start, end;

    public SearchLocationRequest(String area1, String area2)
            throws UnsupportedEncodingException {
        this.area1 = URLEncoder.encode(area1, "utf8");
        this.area2 = URLEncoder.encode(area2, "utf8");
    }
    public SearchLocationRequest(String area1, String area2, String userId)
            throws UnsupportedEncodingException {
        this(area1, area2);
        this.userId = userId;
    }
    private static final String URL_FORMAT =
            NetworkDefineConstant.SERVER_POST
                    + "?area1=%s&area2=%s&userId=%s&start=100&end=95";

    @Override
    public URL getURL() throws MalformedURLException {
        String urlText = String.format(URL_FORMAT, area1, area2, userId);
        return new URL(urlText);
    }

    @Override
    public Post parse(InputStream is) throws ParseException {
        Gson gson = new Gson();
        InputStreamReader isr = new InputStreamReader(is);
        try {
            PostData data = gson.fromJson(isr, PostData.class);
            return data.post;
        } catch (JsonSyntaxException | JsonIOException je) {
            throw new ParseException(je.getMessage(), 0);
        }
    }

    @Override
    public void setRequestHeader(HttpURLConnection connection) {
        super.setRequestHeader(connection);
        connection.setRequestProperty("Accept", "application/json");
    }
}
