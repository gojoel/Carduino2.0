package com.rc.carduino.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.rc.carduino.R;
import com.rc.carduino.activity.PreferencesActivity;
import com.rc.carduino.util.UDPSender;


public class ControllerFragment extends Fragment implements View.OnTouchListener, CompoundButton.OnCheckedChangeListener {

    public static final String PREF_KEY_IP_ADDRESS = "pref_key_host_ip";
    public static final String PREF_KEY_HOST_PORT = "pref_key_host_port";

    private OnControllerFragmentListener mListener;
    private Context mContext;

    // views
    Switch powerSwitch;
    ImageButton forwardButton;
    ImageButton reverseButton;
    ImageButton leftButton;
    ImageButton rightButton;

    // sound effects
    MediaPlayer mp_carStart;

    public ControllerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // sounds
        mp_carStart = MediaPlayer.create(mContext, R.raw.car_start);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_controller, container, false);

        //get IP and port from preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        UDPSender.setPort(Integer.parseInt(prefs.getString(PREF_KEY_HOST_PORT, "2390")));
        UDPSender.setAddress(prefs.getString(PREF_KEY_IP_ADDRESS, ""));

        // setup buttons
        powerSwitch = (Switch) view.findViewById(R.id.rc_powerSwitch);
        forwardButton = (ImageButton) view.findViewById(R.id.rc_button_up);
        reverseButton = (ImageButton) view.findViewById(R.id.rc_button_down);
        leftButton = (ImageButton) view.findViewById(R.id.rc_button_left);
        rightButton = (ImageButton) view.findViewById(R.id.rc_button_right);

        // button listeners
        forwardButton.setOnTouchListener(this);
        reverseButton.setOnTouchListener(this);
        reverseButton.setOnTouchListener(this);
        leftButton.setOnTouchListener(this);
        rightButton.setOnTouchListener(this);
        powerSwitch.setOnCheckedChangeListener(this);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = getActivity();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String ipAddress = prefs.getString(PREF_KEY_IP_ADDRESS, "");
        String port = prefs.getString(PREF_KEY_HOST_PORT, "");

        // user has not set ip / port in preferences
        if (ipAddress.equals("") || port.equals("")) {
            Toast.makeText(mContext, "Add IP Address and port number", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(mContext, PreferencesActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()) {
            case R.id.rc_button_up:
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    UDPSender.accelerate = true;
                    UDPSender.stop = true;
                } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    UDPSender.accelerate = true;
                }
                return true;

            case R.id.rc_button_down:
                if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    UDPSender.reverse = false;
                    UDPSender.stop = true;
                } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    UDPSender.reverse = true;
                }
                return true;

            case R.id.rc_button_left:
                if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    UDPSender.turnleft = false;
                    UDPSender.realign = true;
                }
                else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    UDPSender.turnleft = true;
                }
                return true;
            case R.id.rc_button_right:
                if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    UDPSender.turnleft = true;
                    UDPSender.realign = true;
                }
                else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    UDPSender.turnright = true;
                }
                return true;
        }
        return false;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton.isChecked()){
            UDPSender.sendingPackets = true;
            UDPSender.connected = true;
            mp_carStart.start();
            UDPSender.startControllerListener();
        } else {
            UDPSender.sendingPackets = false; // controller is off
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnControllerFragmentListener {
        // TODO: Update argument type and name
        void onControllerFragmentInteraction(Uri uri);
    }
}
