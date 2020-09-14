package com.dandrzas.inertialsensors.data;

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
    private MadgwickFilter algorithmMadgwickFilter = new MadgwickFilter(sensorAccelerometer, sensorGyroscope, sensorMagnetometer);
    private MahonyFilter algorithmMahonyFilter = new MahonyFilter(sensorAccelerometer, sensorGyroscope, sensorMagnetometer);
    private KalmanFilter algorithmKalmanFilter = new KalmanFilter(sensorAccelerometer, sensorGyroscope, sensorMagnetometer);
    private StepDetectAlgorithm stepDetectAlgorithm = new StepDetectAlgorithm(sensorAccelerometer);
    private InertialTrackingAlgorithm inertialTrackingAlgorithm = new InertialTrackingAlgorithm(sensorAccelerometer, algorithmWithoutFusion);
    private SensorManager mSensorManager;
    private final String TAG = DataManager.class.getSimpleName();
    private boolean computingRunning;
    private Context context;
    private boolean firstUpdAfterStart;
    private int selectedAlgorithm;
    private CSVDataSaver csvDataSaver = CSVDataSaver.getInstance();

    private DataManager() {
  }

    public static DataManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new DataManager();
        }
        return ourInstance;
    }

    public void init(Context context){
      this.context = context;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        algorithmComplementary.setParamAlfa(Float.parseFloat(preferences.getString("parameter_alfa", Float.toString(algorithmComplementary.getParamAlfa()))));
        sensorAccelerometer.setFilterLowPassEnable(preferences.getBoolean("accelerometer_filter_enable", sensorAccelerometer.isFilterLowPassEnable()));
        sensorAccelerometer.setFilterLowPassGain(Float.parseFloat(preferences.getString("accelerometer_low_pass_gain", Float.toString(sensorAccelerometer.getFilterLowPassGain()))));
        algorithmMadgwickFilter.setParBeta(Float.parseFloat(preferences.getString("parameter_beta", Float.toString(algorithmMadgwickFilter.getParBeta()))));
        algorithmMahonyFilter.setParKi(Float.parseFloat(preferences.getString("parameter_ki", Float.toString(algorithmMahonyFilter.getParKi()))));
        algorithmMahonyFilter.setParKp(Float.parseFloat(preferences.getString("parameter_kp", Float.toString(algorithmMahonyFilter.getParKp()))));
        algorithmKalmanFilter.setParQAngle(Float.parseFloat(preferences.getString("parameter_q_angle", Float.toString(algorithmKalmanFilter.getParQAngle()))));
        algorithmKalmanFilter.setParQBias(Float.parseFloat(preferences.getString("parameter_q_bias", Float.toString(algorithmKalmanFilter.getParQBias()))));
        algorithmKalmanFilter.setParRMeasure(Float.parseFloat(preferences.getString("parameter_r", Float.toString(algorithmKalmanFilter.getParRMeasure()))));

        String selectedAlgorithm = preferences.getString("selected_algorithm", "system_default_algorithm");
        switch (selectedAlgorithm){
            case "system_default_algorithm":
                setSelectedAlgorithm(Constants.SYSTEM_ALGORITHM_ID);
                break;
            case "orientation_without_fusion":
                setSelectedAlgorithm(Constants.ORIENTATION_WITHOUT_FUSION);
                break;
            case "complementary_filter":
                setSelectedAlgorithm(Constants.COMPLEMENTARY_FILTER_ID);
                break;
            case "kalman_filter":
                setSelectedAlgorithm(Constants.KALMAN_FILTER_ID);
                break;
            case "mahony_filter":
                setSelectedAlgorithm(Constants.MAHONY_FILTER_ID);
                break;
            case "madgwick_filter":
                setSelectedAlgorithm(Constants.MADGWICK_FILTER_ID);
                break;
        }
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

    public MadgwickFilter getAlgorithmMadgwickFilter() {
        return algorithmMadgwickFilter;
    }

    public KalmanFilter getAlgorithmKalmanFilter() {
        return algorithmKalmanFilter;
    }

    public MahonyFilter getAlgorithmMahonyFilter() {
        return algorithmMahonyFilter;
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
        algorithmMadgwickFilter.startComputing(sensorGyroscope!=null);
        algorithmMahonyFilter.startComputing(sensorGyroscope!=null);
        algorithmKalmanFilter.startComputing(sensorGyroscope!=null);
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
        algorithmMadgwickFilter.stopComputing();
        algorithmMahonyFilter.stopComputing();
        algorithmKalmanFilter.stopComputing();
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
                case Sensor.TYPE_ACCELEROMETER_UNCALIBRATED:
                    Log.d(TAG,  " Accelerometer: " + Float.toString(event.timestamp));
                    sensorAccelerometer.setSampleTime(event.timestamp);
                    sensorAccelerometer.setSampleValue(event.values);
                    csvDataSaver.saveDataAccelerometer(event);

                    if(computingRunning){
                        algorithmComplementary.setUpdatedAccelerometer();
                        algorithmWithoutFusion.setUpdatedAccelerometer();
                        algorithmMadgwickFilter.setUpdatedAccelerometer();
                        algorithmMahonyFilter.setUpdatedAccelerometer();
                        algorithmKalmanFilter.setUpdatedAccelerometer();
                        stepDetectAlgorithm.setUpdatedAccelerometer();
                        inertialTrackingAlgorithm.calc();
                    }
                    break;

                case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                    Log.d(TAG+"G",  " Gyroscope: " + Float.toString(event.timestamp));
                    sensorGyroscope.setSampleTime(event.timestamp);
                    sensorGyroscope.setSampleValue(event.values);
                    if(computingRunning){
                        algorithmComplementary.setUpdatedGyroscope();
                        algorithmMadgwickFilter.setUpdatedGyroscope();
                        algorithmMahonyFilter.setUpdatedGyroscope();
                        algorithmKalmanFilter.setUpdatedGyroscope();
                    }
                    csvDataSaver.saveDataGyroscope(event);
                    break;

                case Sensor.TYPE_MAGNETIC_FIELD:
                    Log.d(TAG+"M",  "Magnetometer: " + (event.values[0]) + " "+ (event.values[1]) + " "+ (event.values[2]) + " ");
                    sensorMagnetometer.setSampleTime(event.timestamp);
                    sensorMagnetometer.setSampleValue(event.values);
                    if(computingRunning){
                        algorithmComplementary.setUpdatedMagnetometer();
                        algorithmWithoutFusion.setUpdatedMagnetometer();
                        algorithmMadgwickFilter.setUpdatedMagnetometer();
                        algorithmMahonyFilter.setUpdatedMagnetometer();
                        algorithmKalmanFilter.setUpdatedMagnetometer();
                    }
                    csvDataSaver.saveDataMagnetometer(event);
                    break;

                case Sensor.TYPE_ROTATION_VECTOR:
                    systemAlgorithm.calcOrientation(event.values, event.timestamp);
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
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER_UNCALIBRATED);
        if (mAccelerometer != null) {
            sensorAccelerometer.setMinDelay(mAccelerometer.getMinDelay() / 1000);
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        }

        // Gyroscope
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);
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

        switch (selectedAlgorithm) {
            case Constants.SYSTEM_ALGORITHM_ID:
                inertialTrackingAlgorithm.setOrientationAlgorithm(systemAlgorithm);
                break;
            case Constants.ORIENTATION_WITHOUT_FUSION:
                inertialTrackingAlgorithm.setOrientationAlgorithm(algorithmWithoutFusion);
                break;
            case Constants.COMPLEMENTARY_FILTER_ID:
                inertialTrackingAlgorithm.setOrientationAlgorithm(algorithmComplementary);
                break;
            case Constants.KALMAN_FILTER_ID:
                inertialTrackingAlgorithm.setOrientationAlgorithm(algorithmKalmanFilter);
                break;
            case Constants.MAHONY_FILTER_ID:
                inertialTrackingAlgorithm.setOrientationAlgorithm(algorithmMahonyFilter);
                break;
            case Constants.MADGWICK_FILTER_ID:
                inertialTrackingAlgorithm.setOrientationAlgorithm(algorithmMadgwickFilter);
                break;
        }
    }
}
