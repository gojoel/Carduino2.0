package com.rc.carduino.util;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by horus on 6/4/17.
 */

public class UDPSender {

    private static final String TAG = UDPSender.class.getName();

    // controller button state
    public static boolean accelerate = false;
    public static boolean reverse = false;
    public static boolean turnleft = false;
    public static boolean turnright = false;
    public static boolean stop = false;
    public static boolean realign = false;
    public static boolean connected = false;

    public static final String ACCELERATE = "a";
    public static final String REVERSE = "b";
    public static final String TURNLEFT = "l";
    public static final String TURNRIGHT = "r";
    public static final String STOP = "s";
    public static final String REALIGN = "w";
    public static final String CONNECTED = "sup";

    public static int port = 0;
    public static String ipAddress;
    private static InetAddress local;

    public static boolean sendingPackets = false;
    private static DatagramSocket socket;
    static DatagramPacket packet;
    public static boolean asyncTaskRunning = false;

    public static void setAddress(String ipAddress) {
        UDPSender.ipAddress = ipAddress;
    }

    public static void setPort(int port) {
        UDPSender.port = port;
    }

    /*
     * Async thread to listen for each button states and send packets accordingly
     * Runs indefinitely
     */
    public static void startControllerListener() {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                asyncTaskRunning = true;
                sendingPackets = true;

                while(sendingPackets) {

                    if(accelerate)
                        logandSendPacket(ACCELERATE, ipAddress, port);

                    else if(reverse)
                        logandSendPacket(REVERSE, ipAddress, port);

                    if(turnleft)
                        logandSendPacket(TURNLEFT, ipAddress, port);

                    else if(turnright)
                        logandSendPacket(TURNRIGHT, ipAddress, port);

                    else if(stop) {
                        logandSendPacket(STOP, ipAddress, port);
                        stop = false;
                    }

                    else if(realign)
                        logandSendPacket(REALIGN, ipAddress, port);

                    else if(connected) {
                        logandSendPacket(CONNECTED, ipAddress, port);
                        connected = false;
                    }

                }
                return null;
            }
        }
                .execute();
    }

    private static void logandSendPacket(String packetContents, String ipAddress, int port) {

        try {
            //Create a socket for communications
            DatagramSocket socket = new DatagramSocket(port);

            //Resolve IP Address
            InetAddress address = InetAddress.getByName(ipAddress);

            //Package message
            int messageLength = packetContents.length();
            byte [] byteMessage = packetContents.getBytes();

            //Create packet
            DatagramPacket packet = new DatagramPacket(byteMessage, messageLength, port);

            //Send packet
            socket.send(packet);

            Log.d("UDP", String.format("Send packet %s on port %d to address %s", packet.toString(), port, ipAddress));
        }
        catch (IOException e) {
            //Log error
            Log.e("UDP", "IOException occurred during packet send", e);
        }
    }
}
