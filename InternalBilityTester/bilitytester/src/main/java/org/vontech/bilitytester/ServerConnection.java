package org.vontech.bilitytester;

import android.util.Log;
import android.util.Pair;

import com.google.gson.Gson;

import org.vontech.core.interaction.UserAction;
import org.vontech.core.interfaces.LiteralInterace;
import org.vontech.core.interfaces.Percept;
import org.vontech.core.interfaces.Perceptifer;
import org.vontech.core.server.StartupEvent;
import org.vontech.core.types.AndroidAppTestConfig;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import okhttp3.MediaType;
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

}
