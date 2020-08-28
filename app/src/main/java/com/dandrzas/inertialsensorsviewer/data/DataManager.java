package com.dandrzas.inertialsensorsviewer.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dandrzas.inertialsensorslibrary.Constants;
import com.dandrzas.inertialsensorslibrary.data.SensorData;


public class DataManager implements SensorEventListener {
    private static DataManager ourInstance;
    private SensorData sensorAccelerometer = new SensorData(Constants.ACCELEROMETER_ID);
    private StepDetectAlgorithm stepDetectAlgorithm = new StepDetectAlgorithm(sensorAccelerometer);
    private SensorManager mSensorManager;
    private final String TAG = DataManager.class.getSimpleName();
    private boolean computingRunning;
    private Context context;

    private DataManager() {
  }

    public static DataManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new DataManager();
        }
        return ourInstance;
    }

    public void setContext(Context context){
      this.context = context;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        sensorAccelerometer.setFilterLowPassEnable(preferences.getBoolean("accelerometer_filter_enable", sensorAccelerometer.isFilterLowPassEnable()));
        sensorAccelerometer.setFilterLowPassGain(Float.parseFloat(preferences.getString("accelerometer_low_pass_gain", Float.toString(sensorAccelerometer.getFilterLowPassGain()))));
    }

    public StepDetectAlgorithm getStepDetectAlgorithm() {
        return stepDetectAlgorithm;
    }

    public SensorData getAccelerometer() {
        return sensorAccelerometer;
    }


    public void startComputing()
    {
        stepDetectAlgorithm.clearData();
        computingRunning = true;
        stepDetectAlgorithm.startComputing(false);
    }

    public void stopComputing()
    {
        computingRunning = false;
        mSensorManager.unregisterListener(this);
        stepDetectAlgorithm.stopComputing();
    }

    public boolean isComputingRunning()
    {
        return computingRunning;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

            switch (event.sensor.getType()){
                case Sensor.TYPE_ACCELEROMETER:
                    Log.d(TAG,  " Accelerometer: " + Float.toString(event.timestamp));
                    sensorAccelerometer.setSampleTime(event.timestamp);
                    sensorAccelerometer.setSampleValue(event.values);

                    if(computingRunning){
                        stepDetectAlgorithm.setUpdatedAccelerometer();
                    }
                    break;
            }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public  void startSensorsListening() {
         Sensor mAccelerometer;

        mSensorManager = (SensorManager) context.getApplicationContext().getSystemService(Context.SENSOR_SERVICE);

        // Accelerometer
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (mAccelerometer != null) {
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

}
