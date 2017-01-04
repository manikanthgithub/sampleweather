package weather.com.sampleweather;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Mani on 1/4/2017.
 */
public class CurrentWeatherFragment extends Fragment {

    View row;
    TextView tvCityName,tvSpeed,tvDeg,tvCurrentTemp,tvMinTemp,tvMaxTemp,tvHumidity,tvSunrise,tvSunset,tvWeatherDesc;
    public static final String TAG = "CurrentWeatherFragment";
    String weatherDesc,temp,minTemp,maxTemp,humidity,windSpeed,deg;
    String jsonResponse,cityNameCaptured;
    long sunriseMillis,sunsetMillis;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        row = inflater.inflate(R.layout.currentweather,container,false);
        Bundle bundle = getArguments();
        jsonResponse = bundle.getString("response");
        cityNameCaptured = bundle.getString("cityNameCaptured");
        tvCityName           =(TextView)   row.findViewById(R.id.tv_city_name);
        tvWeatherDesc        = (TextView) row.findViewById(R.id.desc);
        tvSpeed=(TextView)      row.findViewById(R.id.speed);
        tvCurrentTemp=(TextView)       row.findViewById(R.id.current_temp);
        tvMinTemp=(TextView)       row.findViewById(R.id.minTemp);
        tvMaxTemp=(TextView)       row.findViewById(R.id.maxTemp);

        tvHumidity=(TextView)   row.findViewById(R.id.humidity);
        tvSunrise=(TextView)    row.findViewById(R.id.sunrise);
        tvSunset=(TextView)     row.findViewById(R.id.sunset);

        tvCityName.setText(cityNameCaptured);
        try {
            JSONObject obj = new JSONObject(jsonResponse);
            JSONArray jsonWeatherArray = obj.getJSONArray("weather");

            String sunrise = obj.getJSONObject("sys").optString("sunrise");
            sunriseMillis = Long.parseLong(sunrise) * 1000;
            sunrise =  convertSecstoDate(sunriseMillis);

            String sunset = obj.getJSONObject("sys").optString("sunset");
            sunsetMillis = Long.parseLong(sunset) * 1000;
            Log.d(TAG,"jsonArray--->"+jsonWeatherArray);
            sunset = convertSecstoDate(sunsetMillis);

            for (int i = 0 ; i<jsonWeatherArray.length();i++) {
               JSONObject jsonObj = (JSONObject) jsonWeatherArray.get(i);
                weatherDesc = jsonObj.getString("description");
                Log.d(TAG,"jsonArray--->"+jsonObj.optString("description"));
            }

                temp = obj.getJSONObject("main").optString("temp");
                maxTemp = obj.getJSONObject("main").optString("temp_max");
                minTemp = obj.getJSONObject("main").optString("temp_min");
                humidity = obj.getJSONObject("main").optString("humidity");
                windSpeed = obj.getJSONObject("wind").optString("speed");

            tvWeatherDesc.setText(weatherDesc);
            tvCurrentTemp.setText(temp);
            tvMinTemp.setText(minTemp);
            tvMaxTemp.setText(maxTemp);
            tvSpeed.setText(windSpeed);
            tvHumidity.setText(humidity);
            tvSunrise.setText(sunrise);
            tvSunset.setText(sunset);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return row;
    }

    private String convertSecstoDate(long millis) {

        Date date = new Date(millis);
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm,a", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        String formattedDate = sdf.format(date);
        System.out.println(formattedDate); // Tuesday,November 1,2011 12:00,AM
        return formattedDate;

    }

    public static void hideSoftKeyboard(AppCompatActivity activity) {

        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Context.INPUT_METHOD_SERVICE);
        // Check if softkeyboard is visible on screen or not
        if (inputMethodManager.isAcceptingText()) {
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
        }

    }
}
