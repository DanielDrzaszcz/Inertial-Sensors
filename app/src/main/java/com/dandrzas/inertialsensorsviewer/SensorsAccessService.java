package com.dandrzas.inertialsensorsviewer;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.dandrzas.inertialsensorsviewer.MVVM.Model.SensorsData;

public class SensorsAccessService extends IntentService implements SensorEventListener {

    private static final String ACTION_START = "com.dandrzas.inertialsensorsviewer.action.FOO";

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private SensorsData sersorsRepository;

    public SensorsAccessService() {
        super("SensorsAccessService");
    }
    public static void start(Context context) {
        Intent intent = new Intent(context, SensorsAccessService.class);
        intent.setAction(ACTION_START);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                handleActionStart();
            }
        }
    }

    private void handleActionStart() {

        mSensorManager = (SensorManager) getApplication().getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        Log.d("magnetometer type", mAccelerometer.getName());

        sersorsRepository = SensorsData.getInstance();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        sersorsRepository.setSensorValue(event.values);
        Log.d("dataRead: ", Float.toString(event.values[0])+"\n");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
