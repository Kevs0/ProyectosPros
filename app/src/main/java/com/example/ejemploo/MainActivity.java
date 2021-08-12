package com.example.ejemploo;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.pm.PermissionInfoCompat;

import com.example.ejemploo.databinding.ActivityMainBinding;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends Activity implements /*SensorEventListener,*/ GoogleApiClient
        .ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private GoogleApiClient googleApiClient;
    private Node myNode;
    private TextView mTextView;
    private Button button;
    private ActivityMainBinding binding;
    private SensorManager sensorManager;
    private Sensor sensor;
    int contador;
    private static final String DATO="lO que sea";
    private  static  final  String TAG = "Enviar msg:";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.button = this.findViewById(R.id.boton);
        this.button.setOnClickListener(this);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        }

       /* this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        sensor= sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mTextView = binding.text;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor==this.sensor){
            contador=(int) sensorEvent.values [0];
            this.mTextView.setText(String.valueOf(contador));
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this,this.sensor);
    }
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this,this.sensor,SensorManager.SENSOR_DELAY_NORMAL);
    }*/


    @Override
    public void onConnected(@Nullable Bundle bundle) {
    Wearable.NodeApi
            .getConnectedNodes(googleApiClient)
            .setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                @Override
                public void onResult(@NonNull NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                    for (Node node: getConnectedNodesResult.getNodes()) {
                        if(myNode != null && myNode.isNearby() ){
                            myNode = node;
                            Log.d(TAG,"Se conecto a:"+myNode.getDisplayName());

                        }
                    }
                   if(myNode == null){
                       Log.d(TAG,"NO SE PUDO CONECTAR, BOBO");
                   }
                }
            });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    public void enviarDato(){

        String data = "HOLA NENA";
        send(data);
    }

    private void send(String data) {
    if(myNode != null && googleApiClient != null){
        Wearable.MessageApi.sendMessage(googleApiClient,myNode.getId(),DATO,data.getBytes())
                .setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(@NonNull MessageApi.SendMessageResult sendMessageResult) {
                        if(!sendMessageResult.getStatus().isSuccess()){
                            Log.d(TAG,"No se pudo enviar");
                        }else{
                            Log.d(TAG,"Mensaje enviado!!!!!");
                        }
                    }
                });
    }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.boton) {
            enviarDato();
        }
    }
}