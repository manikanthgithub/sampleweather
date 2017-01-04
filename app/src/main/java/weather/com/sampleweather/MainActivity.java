package weather.com.sampleweather;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import weather.com.sampleweather.interfaces.OnTaskCompleted;

public class MainActivity extends AppCompatActivity implements OnTaskCompleted,View.OnClickListener,View.OnTouchListener,LocationListener{
    static final Integer LOCATION = 0x1;

    private Context mContext;
    Button submitBtn;
    EditText cityName;
    // flag for GPS status
    boolean isGPSEnabled = false;
    // Declaring a Location Manager
    protected LocationManager locationManager;
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    String cityNameCaptured;
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    public static final String TAG = "MainActivity";

    FrameLayout frameLayout;
    ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        cityName = (EditText) findViewById(R.id.cityName);
        submitBtn = (Button) findViewById(R.id.submitBtn);
        frameLayout = (FrameLayout) findViewById(R.id.currentWeatherFraglayout);
        submitBtn.setOnClickListener(this);
        frameLayout.setOnTouchListener(this);
        progress=new ProgressDialog(this);
        progress.setMessage("Weather Forecast");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        checkLocationPermissions(Manifest.permission.ACCESS_FINE_LOCATION,LOCATION);

    }

    private void checkLocationPermissions(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            locationFinder(permission);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED){
            switch (requestCode) {
                //Location
                case 1:
                    locationFinder(permissions[0]);
                    break;
            }
        }else{
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void locationFinder(String permission) {
        if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
            Log.d(TAG, "showSettingsAlert--->"+isGPSEnabled);

            // if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled) {
                if (location == null) {

                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("GPS Enabled", "GPS Enabled" + isGPSEnabled);
                    if (locationManager != null) {
                        List<String> providers = locationManager.getProviders(true);
                        Location bestLocation = null;
                        for (String provider : providers) {
                            Location l = locationManager.getLastKnownLocation(provider);
                            if (l == null) {
                                continue;
                            }
                            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                                // Found best last known location: %s", l);
                                location = l;
                            }
                        }
                        Log.d(TAG, "isGPSEnabled_location--->" + location);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                            Geocoder gcd = new Geocoder(mContext, Locale.getDefault());
                            List<Address> addresses;
                            try {
                                addresses = gcd.getFromLocation(location.getLatitude(),
                                        location.getLongitude(), 1);
                                if (addresses.size() > 0) {
                                    System.out.println(addresses.get(0).getLocality());
                                    cityNameCaptured = addresses.get(0).getLocality();
                                    cityName.setText(cityNameCaptured);
                                    Log.d(TAG, "cityNameCaptured--->" + cityNameCaptured);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            getGPSCordinates(String.valueOf(latitude), String.valueOf(longitude));
                        }
                    }
                }
            } else {
                Log.d(TAG, "showSettingsAlert--->");
                showSettingsAlert();
            }
        }
    }

    private void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
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

    private void getGPSCordinates(String lat,String longt) {
        String BASE_URL="http://api.openweathermap.org/data/2.5/weather?lat=" + lat +"&lon=" + longt + "&APPID=11432dfa16f5cedb9b1d70f4187bae80";
            // \n is for new line
            WebServiceCall task = new WebServiceCall(MainActivity.this);
            task.execute(BASE_URL);
            progress.show();
    }

    @Override
    public void onTaskCompleted(String response) {
        // Just showing the response in a Toast message
        progress.dismiss();
        Bundle bundle = new Bundle();
        CurrentWeatherFragment currentWeatherFrag = new CurrentWeatherFragment();
        bundle.putString("response",response);
        bundle.putString("cityNameCaptured",cityNameCaptured);
        currentWeatherFrag.setArguments(bundle);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.currentWeatherFraglayout,currentWeatherFrag);
        transaction.commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submitBtn :
                                hideSoftKeyboard(MainActivity.this);
                                progress.show();
                                cityNameCaptured = cityName.getText().toString();
                                String BASE_URL="http://api.openweathermap.org/data/2.5/weather?q="+cityNameCaptured+"&APPID=11432dfa16f5cedb9b1d70f4187bae80";
                                WebServiceCall task = new WebServiceCall(MainActivity.this);
                                task.execute(BASE_URL);
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        hideSoftKeyboard(MainActivity.this);
        return false;
    }
}
