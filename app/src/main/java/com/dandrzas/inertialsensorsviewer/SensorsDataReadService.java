package com.dandrzas.inertialsensorsviewer;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.dandrzas.inertialsensorsviewer.Model.SensorsDataRepository;

public class SensorsDataReadService extends IntentService implements SensorEventListener {

    private static final String ACTION_START = "com.dandrzas.inertialsensorsviewer.action.FOO";

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private SensorsDataRepository sensorsDataRepository;
    private Sensor mMagnetometer;
    private Sensor mGyroscope;

    public SensorsDataReadService() {
        super("SensorsDataReadService");
    }
    public static void start(Context context) {
        Intent intent = new Intent(context, SensorsDataReadService.class);
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
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);

        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_UI);

        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_UI);

        sensorsDataRepository = SensorsDataRepository.getInstance();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(sensorsDataRepository!=null) {
            if (event.sensor == mAccelerometer) {
                sensorsDataRepository.setAccelerometerValue(event.values);
                // Log.d("dataReadAccelerom: ", Float.toString(event.values[0]) + "\n");
            } else if (event.sensor == mGyroscope) {
                sensorsDataRepository.setGyroscopeValue(event.values);
                // Log.d("dataReadGyroscope: ", Float.toString(event.values[0]) + "\n");

            } else if (event.sensor == mMagnetometer) {
                sensorsDataRepository.setMagnetometerValue(event.values);
                // Log.d("dataReadMagnetometer: ", Float.toString(event.values[0]) + "\n");

            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
