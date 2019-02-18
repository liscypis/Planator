package com.wojteklisowski.planator.asynctasks;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetRawData {
    private static final String TAG = "GetRawData";

    public String readUrl(String myUrl) {
        Log.d(TAG, "readUrl: " + myUrl);
        String json = "";
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(myUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();

            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            json = stringBuffer.toString();

            bufferedReader.close();
            inputStream.close();
            urlConnection.disconnect();

        } catch(MalformedURLException e) {
            Log.e(TAG, "Invalid URL " + e.getMessage() );
        } catch(IOException e) {
            Log.e(TAG, "IO Exception: " + e.getMessage());
        }
        Log.d(TAG, "Returning JSON: " + json);

        return json;
    }
}
