package com.awareness.backgroundtesting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.EventListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private EditText editTextInput;

    private SensorManager sensorManager;
    private Sensor sensor;
    private TriggerEventListener triggerEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextInput = findViewById(R.id.edit_text_input);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    public void startService(View v){
        String input = editTextInput.getText().toString();

        Intent serviceIntent = new Intent(this, ExampleService.class);
        serviceIntent.putExtra("inputExtra", input);

        startService(serviceIntent);
    }

    public void stopService(View v){
        Intent serviceIntent = new Intent(this, ExampleService.class);
        stopService(serviceIntent);
    }

    public void count(View v){
        for (int i = 0; i <10; i++){
            Log.d(TAG, "count: " + i);
            try {
                Thread.sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }

        }
        Log.d(TAG, "count: " + "Terminó correctamente");
    }

    public void countBackgroundThread(View v){

        ExampleThread thread = new ExampleThread(500);
        thread.start();
    }

    class ExampleThread extends Thread{
        SensorManager sensorManager;
        Sensor sensor;
        SensorEventListener sensorEventListener;

        int whip=0;

        int seconds;

        ExampleThread(int seconds){
            this.seconds = seconds;

            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            if (sensor == null){
                Toast.makeText(MainActivity.this, "El dispositivo no tiene el sensor", Toast.LENGTH_SHORT).show();
            }

            sensorEventListener = new SensorEventListener(){
                @Override
                public void onSensorChanged(SensorEvent sensorEvent) {
                    float x= sensorEvent.values[0];
                    Log.d(TAG, "onSensorChanged: " + x);
                    if (x<-5 && whip==0){
                        whip++;
                        getWindow().getDecorView().setBackgroundColor(Color.BLUE);
                        sound();
                    }else if (x>5 && whip == 1){
                        whip++;
                        getWindow().getDecorView().setBackgroundColor(Color.RED);
                    }

                    if (whip==2){
                        Log.e(TAG, "WHIP2: ");
                        sound();
                        whip = 0;
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int i) {

                }
            };

        }

        public void iniciar(){
            sensorManager.registerListener(sensorEventListener,sensor,SensorManager.SENSOR_DELAY_NORMAL);
        }

        public void detener(){
            sensorManager.unregisterListener(sensorEventListener);
        }

        public void sound(){
            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.latigazo);
            mediaPlayer.start();
        }



        @Override
        public void run() {

            iniciar();

            for (int i = 0; i <seconds; i++){
                Log.d(TAG, "count: " + i);
                try {
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }


    }

}