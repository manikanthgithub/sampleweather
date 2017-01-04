package weather.com.sampleweather;

import android.os.AsyncTask;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import weather.com.sampleweather.interfaces.OnTaskCompleted;


/**
 * Created by Mani on 1/4/2017.
 */
public class WebServiceCall extends AsyncTask<String, Void, String>{
    public OnTaskCompleted taskCompleted;

    public WebServiceCall(OnTaskCompleted activityContext){
        this.taskCompleted = activityContext;

    }
    @Override
    protected String doInBackground(String... strings) {
        String result = "";

//            calling url as url
        URL url;
//            calling HttpUrlConnection as urlConnection
        HttpURLConnection urlConnection;

//            Using try and catch block to find any errors
        try {
            // assigning url value of first object in array called urls which is declared in this start of this method
            url = new URL(strings[0]);

            // using urlConnection to open url which we assigning URL before
            urlConnection = (HttpURLConnection) url.openConnection();

            // Using InputStream to download the content
            InputStream inputStream = urlConnection.getInputStream();
            // Using InputStreamReader to read the inputstream or the data we r downloading
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

//                using it to check if we reached the end of String / Data
            int Data = inputStreamReader.read();
//              using While loop to assign that data to string called result because InputStreamReader reads only one character at a time
            while (Data != -1) {

                char current = (char) Data;

                result += current;

                Data = inputStreamReader.read();
            }

            // returning value of result

            return result;

//                Try and catch block to catch any errors
        } catch (Exception e) {
//                e.printStackTrace will just print a error report like a normal program do when it crashes u can change this with anything u like
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        taskCompleted.onTaskCompleted(result);
    }
}
