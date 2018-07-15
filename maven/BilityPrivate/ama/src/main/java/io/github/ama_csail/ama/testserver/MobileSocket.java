package io.github.ama_csail.ama.testserver;

import android.app.Activity;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Date;

/**
 * A class which facilitates the sending of information from
 */
public class MobileSocket {

    private String ip;
    private int port;
    private Socket socket;
    private PrintStream output;

    private final int RETRY_COUNT = 5;

    public MobileSocket(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void start() {
        Log.i("AMA TEST", "Starting connection to test server at " + ip + ":" + port );
        connectSocket(5);
    }

    private void connectSocket(int retryCount) {
        try {
            if (socket == null) {
                socket = new Socket(ip, port);
                output = new PrintStream(socket.getOutputStream(), true);
            } else if (!socket.isConnected()) {
                output = new PrintStream(socket.getOutputStream(), true);
            }
        } catch (IOException e) {
            if (retryCount > 0) {
                connectSocket(retryCount - 1);
            } else {
                Log.e("AMA TEST", "Could not connect to " + ip + ":" + port + " - is the test server running?");
            }
        }
        if (socket != null && socket.isConnected()) {
            Log.i("AMA TEST", "Connected to test server!");
        }
    }

    private boolean isReady() {
        return output != null && socket.isConnected();
    }

    public void sendStartInfo(String packageName) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("type", "start");
            obj.put("timestamp", new Date());
            obj.put("package", packageName);
            send(RETRY_COUNT, "start info", obj);
        } catch (JSONException e) {
            Log.e("AMA TEST", "Error sending start info: " + e.getLocalizedMessage());
        }
    }

    public void sendFinishInfo(String packageName) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("type", "finish");
            obj.put("timestamp", new Date());
            obj.put("package", packageName);
            send(RETRY_COUNT, "finish info", obj);
        } catch (JSONException e) {
            Log.e("AMA TEST", "Error sending finish info: " + e.getLocalizedMessage());
        }
    }

    public void sendNewScreen(Activity activity) {
        String shortName = "Unknown Activity";
        String longName = "Unknown Package";
        if (activity != null) {
            shortName = activity.getLocalClassName();
            longName = activity.getPackageName();
        }
        try {
            JSONObject obj = new JSONObject();
            obj.put("type", "newScreen");
            obj.put("timestamp", new Date());
            obj.put("name", shortName);
            obj.put("package", longName);
            send(RETRY_COUNT, "screen info", obj);
        } catch (JSONException e) {
            Log.e("AMA TEST", "Error sending screen info: " + e.getLocalizedMessage());
        }
    }

    public void sendScreenshot(File file, Activity activity, Date date) {

        String shortActName = "Unknown Activity";
        String packageName = "Unknown Package";
        if (activity != null) {
            shortActName = activity.getLocalClassName();
            packageName = activity.getPackageName();
        }
        try {

            // First, try to get a stream of data for the file
            String image = base64FromFile(file);

            JSONObject obj = new JSONObject();
            obj.put("type", "screenshot");
            obj.put("timestamp", date);
            obj.put("activityName", shortActName);
            obj.put("package", packageName);
            obj.put("screenshot", image);
            send(RETRY_COUNT, "screenshot", obj);
        } catch (JSONException e) {
            Log.e("AMA TEST", "Error sending screenshot: " + e.getLocalizedMessage());
        } catch (FileNotFoundException e) {
            Log.e("AMA TEST", "Error sending screenshot: " + e.getLocalizedMessage());
        }

    }

    public void sendContentDescriptionMissing(Activity activity, String shortName, String longName, String id, int[] upperLeftBound, int[] lowerRightBound) {
        String shortActName = "Unknown Activity";
        String packageName = "Unknown Package";
        if (activity != null) {
            shortActName = activity.getLocalClassName();
            packageName = activity.getPackageName();
        }
        try {
            JSONObject obj = new JSONObject();
            obj.put("type", "missingContentDescription");
            obj.put("timestamp", new Date());
            obj.put("activityName", shortActName);
            obj.put("package", packageName);
            obj.put("viewName", shortName);
            obj.put("viewSpec", longName);
            obj.put("identifier", id);
            obj.put("upperLeft", "[" + upperLeftBound[0] + "," + upperLeftBound[1] + "]");
            obj.put("lowerRight", "[" + lowerRightBound[0] + "," + lowerRightBound[1] + "]");
            send(RETRY_COUNT, "content description info", obj);
        } catch (JSONException e) {
            Log.e("AMA TEST", "Error sending content description info: " + e.getLocalizedMessage());
        }
    }

    private void send(int retryCount, String descriptor, JSONObject data) {
        if (isReady()) {
            Log.i("AMA TEST", "SENDING " + descriptor);
            output.println(data.toString());
        } else {
            if (retryCount > 0) {
                send(retryCount - 1, descriptor, data);
            } else {
                Log.e("AMA TEST", "Could not send " + descriptor + " - is the server still connected?");
            }
        }
    }

    private String base64FromFile(File file) throws FileNotFoundException {
        InputStream inputStream = new FileInputStream(file);//You can get an inputStream using any IO API
        byte[] bytes;
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        bytes = output.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

}
