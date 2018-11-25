package org.vontech.bilitytester;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.test.uiautomator.UiDevice;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import com.google.gson.Gson;

import org.vontech.core.interaction.UserAction;
import org.vontech.core.interfaces.LiteralInterace;
import org.vontech.core.interfaces.Percept;
import org.vontech.core.interfaces.Perceptifer;
import org.vontech.core.server.StartupEvent;
import org.vontech.core.types.AndroidAppTestConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * An object which manages all outgoing and incoming messages from the test server
 */
public class ServerConnection {

    private String url;
    private OkHttpClient client;
    private Gson parser;
    private Random gen = new Random();

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private static final String GET_INFO_ENDPOINT = "/internal/getProjectInfo";
    private static final String SEND_STARTUP_ENDPOINT = "/internal/receiveStartupEvent";
    private static final String SEND_INTERFACE_ENDPOINT = "/internal/receiveInterface";
    private static final String GET_ACTION_ENDPOINT = "/internal/getNextAction";
    private static final String SEND_SCREENSHOT_ENDPOINT = "/internal/receiveScreenshot";

    /**
     * Creates a connection to the AndroidServer
     * @param url The url to the Android server
     */
    public ServerConnection(String url) {
        this.url = url;
        this.client = new OkHttpClient();
        this.parser = new Gson();
    }

    /**
     * Retrieves the configuration for the test run from the AndroidServer
     * @return the AndroidAppTestConfig object for this test
     */
    public AndroidAppTestConfig getAppConfig() {

        Request request = new Request.Builder()
                .url(url + GET_INFO_ENDPOINT)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String result = response.body().string();

            AndroidAppTestConfig testConfig = parser.fromJson(result, AndroidAppTestConfig.class);
            Log.e("TEST CONFIG", testConfig.toString());

            return testConfig;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    /**
     * Sends an event to the server indicating that the Android test has started
     * the test setup.
     * @param config The configuration that started this server
     */
    void sendStartupEvent(AndroidAppTestConfig config) {
        Long id = Math.abs(gen.nextLong());
        StartupEvent ev = new StartupEvent(id, config, new Date().toString());
        Log.i("SENDING STARTUP", ev.toString());
        Pair<Integer, String> result = sendGenericEvent(SEND_STARTUP_ENDPOINT, ev);
        Log.i("STARTUP RESPONSE CODE", result.first.toString());
        Log.i("STARTUP RESPONSE", result.second);
    }

    /**
     * Sends a literal interface to the server, representing pieces of information
     * that may be viewed by a user. Contained percepts must be filtered through output
     * channels before being sent
     * @param face The interface to send to the server
     */
    void sendInterface(LiteralInterace face) {
        Log.i("SENDING INTERFACE", face.getMetadata().toString());
        Pair<Integer, String> result = sendGenericEvent(SEND_INTERFACE_ENDPOINT, face);
        Log.i("INTERFACE RESPONSE CODE", result.first.toString());
        Log.i("INTERFACE RESPONSE", result.second);
    }

    UserAction awaitNextAction() {
        Request request = new Request.Builder()
                .url(url + GET_ACTION_ENDPOINT)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String result = response.body().string();
            return parser.fromJson(result, UserAction.class);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Sends a generic event to the server, given the object to deserialize and
     * endpoint on the server
     * @param endpoint The endpoint to send this object to
     * @param ev The event to send
     * @return
     */
    Pair<Integer, String> sendGenericEvent(String endpoint, Object ev) {
        String json = parser.toJson(ev);
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url + endpoint)
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            return new Pair<>(response.code(), response.body().string());
        } catch (IOException e) {

        }
        return new Pair<>(500, null);
    }

    /**
     * Sends a screenshot to the server, with the given tag
     * @param tag
     * @param activity
     */
    Pair<Integer, String> sendScreenshot(String tag, Activity activity, int size, String sizeTag, UiDevice optionalDevice) {

        try {

            // First take the screenshot and save it to the device
            String mPath = Environment.getExternalStorageDirectory().toString() + "/bility/" + Calendar.getInstance().getTimeInMillis() + ".png";

            File imageFile = new File(mPath);
            imageFile.getParentFile().mkdirs();
            imageFile.createNewFile();

            int quality = 100;
            View v1 = activity.getWindow().getDecorView().getRootView();

            if (optionalDevice != null) {

                // If device available, get size/scale and take screenshot
                v1.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
                float scale = getScale(bitmap, size);
                optionalDevice.takeScreenshot(imageFile, scale, quality);

            } else {

                // Otherwise, capture regular bitmapView v1 = activity.getWindow().getDecorView().getRootView();
                v1.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
                bitmap = scaleDown(bitmap, size, true);
                v1.setDrawingCacheEnabled(false);

                FileOutputStream outputStream = new FileOutputStream(imageFile);

                bitmap.compress(Bitmap.CompressFormat.PNG, quality, outputStream);
                outputStream.flush();
                outputStream.close();

            }

            // Then send the screenshot to the server
            RequestBody requestBody = new MultipartBody.Builder()
                    .addFormDataPart("literalId", tag)
                    .addFormDataPart("sizeTag", sizeTag)
                    .addFormDataPart("file", imageFile.getName(),
                            RequestBody.create(MediaType.parse("image/png"), imageFile))
                    .build();

            Request request = new Request.Builder()
                    .url(url + SEND_SCREENSHOT_ENDPOINT)
                    .post(requestBody)
                    .build();

            Response response = client.newCall(request).execute();
            return new Pair<>(response.code(), response.body().string());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    public static float getScale(Bitmap realImage, float maxImageSize) {
        return Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
    }

}
