package org.vontech.bilitytester;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.vontect.core.AndroidAppTestConfig;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A class which downloads and sets up the required information for running tests
 * on this application.
 * @author Aaron Vontell
 */
public class BilitySetup {

    private Context context;

    private static final String ANDROID_HOST = "http://10.0.2.2:8080";

    public BilitySetup(Context context) {
        this.context = context;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                    .url(ANDROID_HOST + "/internal/getProjectInfo")
                    .build();
        try {
            Response response = client.newCall(request).execute();
            String result = response.body().string();
            try {
                JSONObject jObj = new JSONObject(result);
                AndroidAppTestConfig testConfig = new AndroidAppTestConfig(
                        jObj.getString("packageName"),
                        jObj.getInt("timeout"),
                        jObj.getInt("numRuns"),
                        jObj.getInt("seed"),
                        jObj.getInt("maxActions"),
                        jObj.getLong("id")
                );
                Log.e("TEST CONFIG", testConfig.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }


            Log.e("RESULTS", result);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}