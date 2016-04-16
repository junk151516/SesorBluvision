package com.bluvision.buvisionsdksample.fragments;

import com.bluvision.beeks.sdk.constants.BeaconType;
import com.bluvision.beeks.sdk.domainobjects.Beacon;
import com.bluvision.beeks.sdk.domainobjects.ConfigurableBeacon;
import com.bluvision.beeks.sdk.domainobjects.EddystoneURLBeacon;
import com.bluvision.beeks.sdk.domainobjects.IBeacon;
import com.bluvision.beeks.sdk.domainobjects.SBeacon;
import com.bluvision.beeks.sdk.interfaces.BeaconConfigurationListener;
import com.bluvision.beeks.sdk.util.BeaconManager;
import com.bluvision.buvisionsdksample.BluvisionSampleSDKApplication;
import com.bluvision.buvisionsdksample.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Leandro Salas on 10/15/15.
 */
public class BeaconDetail extends BaseFragment implements BeaconConfigurationListener {

    private View rootView;


    private BeaconManager mBeaconManager;
    private Beacon mBeacon;
    private SBeacon sBeacon;
    EditText temperature;
    Timer myTimer;
    float temperatureReal;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mBeaconManager = ((BluvisionSampleSDKApplication) getActivity().getApplication())
                .getBeaconManager();

        temperature = (EditText) rootView.findViewById(R.id.editTextTemp);


        if(mBeacon!=null) {

            ((TextView) rootView.findViewById(R.id.txtName)).setText(mBeacon.getDevice().getName());
            ((TextView) rootView.findViewById(R.id.txtMacAddress))
                    .setText(mBeacon.getDevice().getAddress());


            if(mBeacon.getBeaconType()==BeaconType.S_BEACON){
                ((Button)rootView.findViewById(R.id.btnConnect)).setEnabled(true);
                sBeacon = (SBeacon)mBeacon;
            }

            ConcurrentHashMap<BeaconType,Beacon> beacons = mBeacon.getAssociations();
            for (Beacon beaconAssociated : beacons.values()){

                if(beaconAssociated.getBeaconType()==BeaconType.S_BEACON){
                    ((Button)rootView.findViewById(R.id.btnConnect)).setEnabled(true);
                    sBeacon = (SBeacon)beaconAssociated;
                }

            }


            if(mBeacon.getBeaconType()==BeaconType.I_BEACON){
                Toast.makeText(getActivity(),((IBeacon)mBeacon).getUuid(),Toast.LENGTH_LONG).show();
                Toast.makeText(getActivity(),String.valueOf(((IBeacon)mBeacon).getMajor()),Toast.LENGTH_LONG).show();
                Toast.makeText(getActivity(), String.valueOf(((IBeacon) mBeacon).getMinor()), Toast.LENGTH_LONG).show();
            }


            if(mBeacon.getBeaconType()==BeaconType.EDDYSTONE_URL_BEACON){
                Toast.makeText(getActivity(),((EddystoneURLBeacon)mBeacon).getURL(),Toast.LENGTH_LONG).show();
            }

        }


        ((Button)rootView.findViewById(R.id.btnConnect)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(sBeacon!=null){

                            sBeacon.setBeaconConfigurationListener(BeaconDetail.this);

                            String password = ((EditText)rootView.findViewById(R.id.txtPassword)).getText().toString();

                            //if password is empty tries to connect without a passoword
                            sBeacon.connect(getActivity(),password);

                            Log.e("SID", sBeacon.getsId());


                        }

                    }
                });


        ((Button)rootView.findViewById(R.id.btnDisconnect)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(sBeacon!=null){

                            sBeacon.disconnect();

                        }

                    }
                });
        // añado una temp nueva
        ((Button)rootView.findViewById(R.id.getTemp)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(sBeacon!=null){

                            //Log.e("SID", String.valueOf(sBeacon.getTemperature()));
                            //Log.e("SID", String.valueOf(sBeacon.getBattery()));
                            //sBeacon.alert(true, true);
                            //Log.e("SID", String.valueOf(sBeacon.getTemperatureFromScanRecord()));
                            //sBeacon.readAdvertisementSettings();
                            sBeacon.readDeviceStatus();
                            //Toast.makeText(getActivity(),String.valueOf(sBeacon.getTemperature()), Toast.LENGTH_LONG).show();
                        }
                    }
                });
        ((Button)rootView.findViewById(R.id.getLed)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(sBeacon!=null){
                            sBeacon.alert(true, true);
                        }
                    }
                });



        return rootView;
    }

    public void setBeacon(Beacon beacon) {
        mBeacon = beacon;
    }


    @Override
    public void onFailedToUpdateFirmware(int i) {

    }

    @Override
    public void onConnect(boolean connected, boolean authenticated) {


        Log.e("Connect", "connected:" + connected + " authenticated:" + authenticated);

        if(connected && authenticated){

            sBeacon.alert(true, true);
            ConfigurableBeacon configurableBeacon = (ConfigurableBeacon)sBeacon;

            configurableBeacon.readIBeaconUUID();
                    ((Button) rootView.findViewById(R.id.btnConnect)).setEnabled(false);
            ((Button)rootView.findViewById(R.id.btnDisconnect)).setEnabled(true);
            Toast.makeText(getActivity(),"Connected", Toast.LENGTH_LONG).show();
           //codigo añadido
            myTimer = new Timer();
            myTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    // If you want to modify a view in your Activity
                    timerMethod();
                }
            }, 1000, 15000); // initial delay 1 second, interval 1 second


        }else{
            Toast.makeText(getActivity(),"Connection failed", Toast.LENGTH_LONG).show();
        }

    }

    public void timerMethod() {
        //sBeacon.alert(true, true);
        sBeacon.readDeviceStatus();
        Log.e("SID", "Temperatura : "+temperatureReal);
       updateReceivedData(temperatureReal+"#"+temperatureReal);
    }



    private void updateReceivedData(String karelRead1) {

        // si la trama contiene el mensaje de thingspeak envia la trama
        //StringTokenizer st = new StringTokenizer(message, "thingspeak");
        // se saca un substring que elimina el indice thingspeak y el /n del final de la linea
        String ms = karelRead1;


        // // TODO: 22/03/2016  api key automatic 3831SHX4AS2XD41T
        String url = "https://api.thingspeak.com/update?api_key=K24OF4CX99WPXXV7";
        //String url = "https://api.thingspeak.com/update?api_key=3831SHX4AS2XD41T";

        // String url = "https://api.thingspeak.com/update?api_key="+sharedpreferences.getString(KeyThingspeak, "");
        int i=0;


        for (String token : ms.split("#")) {
            i++;
            url = url+"&field"+i+"="+token;
        }

        final String finalUrl = url;
        Thread thread = new Thread() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                StringBuilder total = new StringBuilder();

                try {
                    URL url1 = new URL(finalUrl);
                    urlConnection = (HttpURLConnection) url1.openConnection();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader r = new BufferedReader(new InputStreamReader(in));
                    String line;
                    while ((line = r.readLine()) != null) {
                        total.append(line);
                    }
                    Log.d("web", total.toString()+" "+finalUrl);
                    if(total.toString().equals("0")){
                        //ThingspeakState= false;
                    }else{
                        //ThingspeakState= true;
                    }

                    //readStream(in);
                    urlConnection.disconnect();

                    //finally {
                    //  urlConnection.disconnect();
                    //}
                } catch (IOException e) {
                    //ThingspeakState= false;
                    e.printStackTrace();
                } catch (NullPointerException e){
                    //ThingspeakState= false;
                    e.printStackTrace();
                }
            }
        };

        thread.start();
        karelRead1 = new String();
    }

    @Override
    public void onDisconnect() {

        ((Button)rootView.findViewById(R.id.btnConnect)).setEnabled(true);
        ((Button)rootView.findViewById(R.id.btnDisconnect)).setEnabled(false);
        Log.e("Connect", "Disconnected");
        Toast.makeText(getActivity(),"Disconnected", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onCommandToNotConnectedBeacon() {

    }

    @Override
    public void onReadConnectionSettings(int i, int i1, int i2, int i3) {

    }

    @Override
    public void onSetConnectionSettings(int i, int i1, int i2, int i3) {

    }
    public void readtemp(View view) {
        Log.e("Connect", "Disconnected");
        Toast.makeText(getActivity(),"Disconnected", Toast.LENGTH_LONG).show();

    }
    @Override
    public void onFailedToReadConnectionSettings() {

    }

    @Override
    public void onFailedToSetConnectionSettings() {

    }

    @Override
    public void onReadTemperature(double v) {
        Log.e("Connect", "temp: " + v);
    }

    @Override
    public void onFailedToReadTemperature() {

    }

    @Override
    public void onConnectionExist() {


    }

    @Override
    public void onReadIBeaconUUID(UUID uuid) {

        Toast.makeText(getActivity(),"UUID: " + uuid.toString(), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onSetIBeaconUUID(UUID uuid) {

    }

    @Override
    public void onFailedToReadIBeaconUUID() {

    }

    @Override
    public void onFailedToSetIBeaconUUID() {

    }

    @Override
    public void onReadIBeaconMajorAndMinor(int i, int i1) {

    }

    @Override
    public void onSetIBeaconMajorAndMinor(int i, int i1) {

    }

    @Override
    public void onFailedToReadIBeaconMajorAndMinor() {

    }

    @Override
    public void onFailedToSetIBeaconMajorAndMinor() {

    }

    @Override
    public void onReadEddystoneUID(byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void onSetEddystoneUID(byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void onFailedToReadEddystoneUID() {

    }

    @Override
    public void onFailedToSetEddystoneUID() {

    }

    @Override
    public void onReadEddystoneURL(String s) {

    }

    @Override
    public void onSetEddystoneURL(String s) {

    }

    @Override
    public void onFailedToReadEddystoneURL() {

    }

    @Override
    public void onFailedToSetEddystoneURL() {

    }

    @Override
    public void onReadDeviceStatus(float v, float v1, short i) {
        //Toast.makeText(getActivity(),"Bateria: "+v+" Temperatura: "+v1+" N/A: "+i, Toast.LENGTH_LONG).show();
        temperature.setText(v1 + "°C");
        temperatureReal = v1;
    }

    @Override
    public void onFailedToReadDeviceStatus() {

    }

    @Override
    public void onReadFrameTypeIntervalTxPower(byte b, byte b1, byte b2, float v, float v1) {

    }

    @Override
    public void onSetFrameTypeIntervalTxPower(byte b, byte b1, byte b2, float v, float v1) {

    }

    @Override
    public void onFailedToReadFrameTypeIntervalTxPower() {

    }

    @Override
    public void onFailedToSetFrameTypeIntervalTxPower() {

    }

    @Override
    public void onSetFrameTypeConnectionRates(byte b, byte b1, byte b2) {

    }

    @Override
    public void onReadFrameTypeConnectionRates(byte b, byte b1, byte b2) {

    }

    @Override
    public void onFailedToReadFrameTypeConnectionRates() {

    }

    @Override
    public void onFailedToSetFrameTypeConnectionRates() {

    }

    @Override
    public void onReadAdvertisementSettings(float v, float v1, float v2) {
        Toast.makeText(getActivity(),"UUID: " +"dato 1: "+v+"dato 2: "+v1+"dato 3: "+v2, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onSetAdvertisementSettings(float v, float v1, float v2) {

    }

    @Override
    public void onFailedToReadAdvertisementSettings() {

    }

    @Override
    public void onFailedToSetAdvertisementSettings() {

    }

    @Override
    public void onSetAccelerometerConfiguration() {

    }

    @Override
    public void onFailedToSetAccelerometerConfiguration() {

    }

    @Override
    public void onSetPassword(boolean b) {

    }

    @Override
    public void onUpdateFirmware(double v) {

    }
}
class Task implements Runnable {

    float temperatura;

    public void setTemperatura(float temp){
        temperatura = temp;
    }
    @Override
    public void run() {

        for (int i = 0; i <= 10; i++) {

            final int value = i;
               try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

