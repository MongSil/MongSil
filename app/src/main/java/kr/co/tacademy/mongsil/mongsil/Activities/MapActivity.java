package kr.co.tacademy.mongsil.mongsil.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import kr.co.tacademy.mongsil.mongsil.JSONParsers.Models.LocationData;
import kr.co.tacademy.mongsil.mongsil.Utils.MapWeatherButton;
import kr.co.tacademy.mongsil.mongsil.JSONParsers.NetworkDefineConstant;
import kr.co.tacademy.mongsil.mongsil.JSONParsers.ParseDataParseHandler;
import kr.co.tacademy.mongsil.mongsil.R;
import kr.co.tacademy.mongsil.mongsil.JSONParsers.Models.WeatherData;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.util.Log.e;

public class MapActivity extends BaseActivity implements View.OnClickListener{
    int order = 0;
    MapWeatherButton
            mapIncheon,
            mapSeoul,
            mapGangwon,
            mapChungju,
            mapJunju,
            mapDaejeon,
            mapGwangju,
            mapDaegu,
            mapUlsan,
            mapBusan,
            mapJeju;

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

        mapIncheon = (MapWeatherButton) findViewById(R.id.map_incheon);
        mapIncheon.setOnClickListener(this);
        mapSeoul   = (MapWeatherButton) findViewById(R.id.map_seoul);
        mapSeoul.setOnClickListener(this);
        mapGangwon = (MapWeatherButton) findViewById(R.id.map_gangwon);
        mapGangwon.setOnClickListener(this);
        mapChungju = (MapWeatherButton) findViewById(R.id.map_chungju);
        mapChungju.setOnClickListener(this);
        mapJunju   = (MapWeatherButton) findViewById(R.id.map_junju);
        mapJunju.setOnClickListener(this);
        mapDaejeon = (MapWeatherButton) findViewById(R.id.map_daejeon);
        mapDaejeon.setOnClickListener(this);
        mapGwangju = (MapWeatherButton) findViewById(R.id.map_gwangju);
        mapGwangju.setOnClickListener(this);
        mapDaegu   = (MapWeatherButton) findViewById(R.id.map_daegu);
        mapDaegu.setOnClickListener(this);
        mapUlsan   = (MapWeatherButton) findViewById(R.id.map_ulsan);
        mapUlsan.setOnClickListener(this);
        mapBusan   = (MapWeatherButton) findViewById(R.id.map_busan);
        mapBusan.setOnClickListener(this);
        mapJeju    = (MapWeatherButton) findViewById(R.id.map_jeju);
        mapJeju.setOnClickListener(this);

        new AsyncLatLonWeatherJSONList().execute(LocationData.ChangeToLatLon(mapIncheon.getLocationName()));

    }

    @Override
    public void onClick(View view) {
        MapWeatherButton v = (MapWeatherButton) view;
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("name", v.getLocationName());
        setResult(RESULT_OK,intent);
        finish();
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
                    if( order == 0) {
                        mapIncheon.setImgLocationBackground(WeatherData.imgFromWeatherCode(result.getCode(), 3));
                        mapIncheon.setImgLocationWeatherIcon(WeatherData.imgFromWeatherCode(result.getCode(), 0));
                        order++;
                        new AsyncLatLonWeatherJSONList().execute(LocationData.ChangeToLatLon(mapSeoul.getLocationName()));
                        return;
                    }
                    initButton(result.getCode());
                }
            }
        }
    }

    private void initButton(String weatherCode) {
        if (order == 1) {
            mapSeoul.setImgLocationBackground(WeatherData.imgFromWeatherCode(weatherCode, 3));
            mapSeoul.setImgLocationWeatherIcon(WeatherData.imgFromWeatherCode(weatherCode, 5));
            new AsyncLatLonWeatherJSONList().execute(LocationData.ChangeToLatLon(mapGangwon.getLocationName()));
        } else if (order == 2) {
            mapGangwon.setImgLocationBackground(WeatherData.imgFromWeatherCode(weatherCode, 3));
            mapGangwon.setImgLocationWeatherIcon(WeatherData.imgFromWeatherCode(weatherCode, 5));
            new AsyncLatLonWeatherJSONList().execute(LocationData.ChangeToLatLon(mapChungju.getLocationName()));
        } else if (order == 3) {
            mapChungju.setImgLocationBackground(WeatherData.imgFromWeatherCode(weatherCode, 3));
            mapChungju.setImgLocationWeatherIcon(WeatherData.imgFromWeatherCode(weatherCode, 5));
            new AsyncLatLonWeatherJSONList().execute(LocationData.ChangeToLatLon(mapJunju.getLocationName()));
        } else if (order == 4) {
            mapJunju.setImgLocationBackground(WeatherData.imgFromWeatherCode(weatherCode, 3));
            mapJunju.setImgLocationWeatherIcon(WeatherData.imgFromWeatherCode(weatherCode, 5));
            new AsyncLatLonWeatherJSONList().execute(LocationData.ChangeToLatLon(mapDaejeon.getLocationName()));
        } else if (order == 5) {
            mapDaejeon.setImgLocationBackground(WeatherData.imgFromWeatherCode(weatherCode, 3));
            mapDaejeon.setImgLocationWeatherIcon(WeatherData.imgFromWeatherCode(weatherCode, 5));
            new AsyncLatLonWeatherJSONList().execute(LocationData.ChangeToLatLon(mapGwangju.getLocationName()));
        } else if (order == 6) {
            mapGwangju.setImgLocationBackground(WeatherData.imgFromWeatherCode(weatherCode, 3));
            mapGwangju.setImgLocationWeatherIcon(WeatherData.imgFromWeatherCode(weatherCode, 5));
            new AsyncLatLonWeatherJSONList().execute(LocationData.ChangeToLatLon(mapDaegu.getLocationName()));
        } else if (order == 7) {
            mapDaegu.setImgLocationBackground(WeatherData.imgFromWeatherCode(weatherCode, 3));
            mapDaegu.setImgLocationWeatherIcon(WeatherData.imgFromWeatherCode(weatherCode, 5));
            new AsyncLatLonWeatherJSONList().execute(LocationData.ChangeToLatLon(mapUlsan.getLocationName()));
        } else if (order == 8) {
            mapUlsan.setImgLocationBackground(WeatherData.imgFromWeatherCode(weatherCode, 3));
            mapUlsan.setImgLocationWeatherIcon(WeatherData.imgFromWeatherCode(weatherCode, 5));
            new AsyncLatLonWeatherJSONList().execute(LocationData.ChangeToLatLon(mapBusan.getLocationName()));
        } else if (order == 9) {
            mapBusan.setImgLocationBackground(WeatherData.imgFromWeatherCode(weatherCode, 3));
            mapBusan.setImgLocationWeatherIcon(WeatherData.imgFromWeatherCode(weatherCode, 5));
            new AsyncLatLonWeatherJSONList().execute(LocationData.ChangeToLatLon(mapJeju.getLocationName()));
        } else if (order == 10) {
            mapJeju.setImgLocationBackground(WeatherData.imgFromWeatherCode(weatherCode, 3));
            mapJeju.setImgLocationWeatherIcon(WeatherData.imgFromWeatherCode(weatherCode, 5));
        }
        order++;
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
