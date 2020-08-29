package com.dandrzas.inertialsensorsviewer.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import androidx.preference.PreferenceManager;

import java.util.Observable;


public class DataManager extends Observable implements SensorEventListener {
    private static DataManager ourInstance;
    private SensorData sensorAccelerometer = new SensorData(Constants.ACCELEROMETER_ID);
    private SensorData sensorGyroscope = new SensorData(Constants.GYROSCOPE_ID);
    private SensorData sensorMagnetometer = new SensorData(Constants.MAGNETOMETER_ID);
    private ComplementaryFilter algorithmComplementary = new ComplementaryFilter(sensorAccelerometer, sensorGyroscope, sensorMagnetometer);
    private SystemAlgorithm systemAlgorithm = new SystemAlgorithm();
    private OrientationWithoutFusion algorithmWithoutFusion = new OrientationWithoutFusion(sensorAccelerometer, sensorMagnetometer);
    private StepDetectAlgorithm stepDetectAlgorithm = new StepDetectAlgorithm(sensorAccelerometer);
    private InertialTrackingAlgorithm inertialTrackingAlgorithm = new InertialTrackingAlgorithm(sensorAccelerometer, algorithmWithoutFusion);
    private SensorManager mSensorManager;
    private final String TAG = DataManager.class.getSimpleName();
    private boolean computingRunning;
    private Context context;
    private boolean firstUpdAfterStart;
    private int selectedAlgorithm;

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
        algorithmComplementary.setParamAlfa(Float.parseFloat(preferences.getString("accelerometer_low_pass_gain", Float.toString(algorithmComplementary.getParamAlfa()))));
        sensorAccelerometer.setFilterLowPassEnable(preferences.getBoolean("accelerometer_filter_enable", sensorAccelerometer.isFilterLowPassEnable()));
        sensorAccelerometer.setFilterLowPassGain(Float.parseFloat(preferences.getString("accelerometer_low_pass_gain", Float.toString(sensorAccelerometer.getFilterLowPassGain()))));
    }

    public SystemAlgorithm getSystemAlgrithmInstance() {
        return systemAlgorithm;
    }

    public ComplementaryFilter getAlgorithmComplementaryInstance() {
        return algorithmComplementary;
    }

    public OrientationWithoutFusion getAlgorithmWithoutFusionInstance() {
        return algorithmWithoutFusion;
    }

    public StepDetectAlgorithm getStepDetectAlgorithm() {
        return stepDetectAlgorithm;
    }

    public InertialTrackingAlgorithm getInertialTrackingAlgorithmInstance() {
        return inertialTrackingAlgorithm;
    }

    public SensorData getAccelerometer() {
        return sensorAccelerometer;
    }

    public SensorData getMagnetometer() {
        return sensorMagnetometer;
    }

    public SensorData getGyroscope() {
        return sensorGyroscope;
    }

    public void startComputing()
    {
        firstUpdAfterStart = true;
        startSensorsListening();
        computingRunning = true;
        algorithmComplementary.startComputing(sensorGyroscope!=null);
        algorithmWithoutFusion.startComputing(false);
        stepDetectAlgorithm.startComputing(false);
        inertialTrackingAlgorithm.startComputing();
        setChanged();
        notifyObservers(Constants.COMPUTING_START_ID);
    }

    public void stopComputing()
    {
        computingRunning = false;
        firstUpdAfterStart = false;
        mSensorManager.unregisterListener(this);
        algorithmComplementary.stopComputing();
        algorithmWithoutFusion.stopComputing();
        stepDetectAlgorithm.stopComputing();
        inertialTrackingAlgorithm.stopComputing();
        setChanged();
        notifyObservers(Constants.COMPUTING_STOP_ID);
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
                        algorithmComplementary.setUpdatedAccelerometer();
                        algorithmWithoutFusion.setUpdatedAccelerometer();
                        stepDetectAlgorithm.setUpdatedAccelerometer();
                        inertialTrackingAlgorithm.calc();
                    }
                    break;

                case Sensor.TYPE_GYROSCOPE:
                    Log.d(TAG+"G",  " Gyroscope: " + Float.toString(event.timestamp));
                    sensorGyroscope.setSampleTime(event.timestamp);
                    sensorGyroscope.setSampleValue(event.values);
                    if(computingRunning){
                        algorithmComplementary.setUpdatedGyroscope();
                    }
                    break;

                case Sensor.TYPE_MAGNETIC_FIELD:
                    Log.d(TAG+"M",  "Magnetometer: " + (event.values[0]) + " "+ (event.values[1]) + " "+ (event.values[2]) + " ");
                    sensorMagnetometer.setSampleTime(event.timestamp);
                    sensorMagnetometer.setSampleValue(event.values);
                    if(computingRunning){
                        algorithmComplementary.setUpdatedMagnetometer();
                        algorithmWithoutFusion.setUpdatedMagnetometer();
                    }
                    break;

                case Sensor.TYPE_ROTATION_VECTOR:
                    systemAlgorithm.calcOrientation(event.values);
                    break;
            }
        firstUpdAfterStart = false;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void startSensorsListening() {
         Sensor mAccelerometer;
         Sensor mMagnetometer;
         Sensor mGyroscope;
         Sensor mVirtualOrientation;

        mSensorManager = (SensorManager) context.getApplicationContext().getSystemService(Context.SENSOR_SERVICE);

        // Accelerometer
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (mAccelerometer != null) {
            sensorAccelerometer.setMinDelay(mAccelerometer.getMinDelay() / 1000);
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        }

        // Gyroscope
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (mGyroscope != null) {
            sensorGyroscope.setMinDelay(mGyroscope.getMinDelay() / 1000);
            mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_FASTEST);
        }

        // Magnetometer
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (mMagnetometer != null) {
            sensorMagnetometer.setMinDelay(mMagnetometer.getMinDelay() / 1000);
            mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_FASTEST);
        }

        // Virtual orientation sensor
        mVirtualOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        if (mVirtualOrientation != null) {
            mSensorManager.registerListener(this, mVirtualOrientation, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    public boolean isFirstUpdAfterStart() {
        return firstUpdAfterStart;
    }

    public int getSelectedAlgorithm() {
        return selectedAlgorithm;
    }

    public void setSelectedAlgorithm(int selectedAlgorithm) {
        this.selectedAlgorithm = selectedAlgorithm;
    }
}
