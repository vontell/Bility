package io.github.ama_csail.ama.testserver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Runner {

    public static void main(String[] args){

        try {

            int port = 8080;
            final ServerSocket serverMobileSocket = new ServerSocket(port);
            ServerSocket serverWebSocket = new ServerSocket(port+1);

            String address = InetAddress.getLocalHost().toString().split("/")[1];
            System.out.println("Server has started on " + address + ":" + port + ".\r\nWaiting for a connection...");
            System.out.println("ws://" + address + ":" + (port+1));

            Socket client = serverWebSocket.accept();
            System.out.println("A web client connected.");
            final PrintWriter webStream = new PrintWriter(client.getOutputStream(), true);
            //BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            webStream.print("connected");
            System.out.println("Sent connection message to web client.");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    waitForDevice(serverMobileSocket, webStream);
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void waitForDevice(ServerSocket server, PrintWriter webStream) {

        try {

            System.out.println("Waiting for device to connect");

            Socket client = server.accept();
            System.out.println("A device connected.");
            PrintWriter saver = new PrintWriter("accTest.ama", "UTF-8");
            System.out.println("Created test file for this test");

            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            String line;
            while ((line = in.readLine()) != null) {
                saver.println(line);
                if (line.length() > 300) {
                    line = line.substring(0,299) + "... [truncated]";
                }
                System.out.println(line);

                // TODO: Attempt to save image files

                // Send the data to the web client
                webStream.println(line);

            }
            saver.close();

            client.close();
            Thread.sleep(1000);
            waitForDevice(server, webStream);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
