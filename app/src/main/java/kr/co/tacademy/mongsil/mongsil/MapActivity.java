package kr.co.tacademy.mongsil.mongsil;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.util.Log.e;

public class MapActivity extends BaseActivity {
    public static final String LOCATION = "location";

    int order = 0;
    MapWeatherButton weatherButton;

    int[] weatherId = {
            R.id.map_incheon,
            R.id.map_seoul,
            R.id.map_gangwon,
            R.id.map_chungju,
            R.id.map_junju,
            R.id.map_daejeon,
            R.id.map_gwangju,
            R.id.map_daegu,
            R.id.map_ulsan,
            R.id.map_busan,
            R.id.map_jeju
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.icon_close);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        weatherButton = (MapWeatherButton) findViewById(weatherId[order]);

        new AsyncLatLonWeatherJSONList().execute(
                LocationData.ChangeToLatLon(weatherButton.getLocationName()));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                toMainActivityFromthis();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 위도, 경도 날씨 AsyncTask
    public class AsyncLatLonWeatherJSONList extends AsyncTask<String, Integer, WeatherData> {

        @Override
        protected WeatherData doInBackground(String... args) {
            Response response = null;
            try {
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .build();

                Request request = new Request.Builder()
                        .addHeader("Accept", "application/json")
                        .addHeader("appKey", NetworkDefineConstant.SK_APP_KEY)
                        .url(String.format(
                                NetworkDefineConstant.SK_WEATHER_LAT_LON,
                                args[0], args[1]))
                        .build();

                response = toServer.newCall(request).execute();
                ResponseBody responseBody = response.body();

                boolean flag = response.isSuccessful();
                int responseCode = response.code();
                if (responseCode >= 400) return null;
                if (flag) {
                    return ParseDataParseHandler.getJSONWeatherList(
                            new StringBuilder(responseBody.string()));
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

        @Override
        protected void onPostExecute(WeatherData result) {
            if (result != null) {
                if(order < weatherId.length) {
                    order++;
                    weatherButton.setImgLocationWeatherIcon(WeatherData.imgFromWeatherCode(result.code, 0));
                    weatherButton.setImgLocationBackground(WeatherData.imgFromWeatherCode(result.code, 3));
                    weatherButton = (MapWeatherButton) findViewById(weatherId[order]);
                    new AsyncLatLonWeatherJSONList().execute(
                            LocationData.ChangeToLatLon(weatherButton.getLocationName()));
                }
            }
        }
    }

    private void toMainActivityFromthis() {
        Intent intent = new Intent(MapActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        toMainActivityFromthis();
    }
}
