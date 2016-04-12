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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mBeaconManager = ((BluvisionSampleSDKApplication) getActivity().getApplication())
                .getBeaconManager();

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

        }else{
            Toast.makeText(getActivity(),"Connection failed", Toast.LENGTH_LONG).show();
        }

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
        Toast.makeText(getActivity(),"Bateria: "+v+" Temperatura: "+v1+" N/A: "+i, Toast.LENGTH_LONG).show();

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