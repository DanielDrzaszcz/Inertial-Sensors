package com.dandrzas.inertialsensorsviewer;


import android.util.Log;

import java.util.Observable;

public class SensorsDataRepository extends Observable {

    private float[] AccelerometerValue = new float[3];
    private float[] GyroscopeValue = new float[3];
    private float[] MagnetometerValue = new float[3];
    private static SensorsDataRepository ourInstance;
    private float minDelayAccelerometer;
    private float minDelayGyroscope;
    private float minDelayMagnetometer;

    private SensorsDataRepository()
    {
    }

    public static SensorsDataRepository getInstance()
    {
        if(ourInstance==null)
        {
            ourInstance = new SensorsDataRepository();
        }
        return ourInstance;
    }

    public float[] getAccelerometerValue() {
        return AccelerometerValue;
    }

    public void setAccelerometerValue(float[] accelerometerValue) {
        this.AccelerometerValue = accelerometerValue;
        setChanged();
        notifyObservers(1);
    }

    public float[] getGyroscopeValue() {
        return GyroscopeValue;
    }

    public void setGyroscopeValue(float[] gyroscopeValue) {
        GyroscopeValue = gyroscopeValue;
        setChanged();
        notifyObservers(2);
    }

    public float[] getMagnetometerValue() {
        return MagnetometerValue;
    }

    public void setMagnetometerValue(float[] magnetometerValue) {
        MagnetometerValue = magnetometerValue;
        setChanged();
        notifyObservers(3);
    }

    public float getMinDelayAccelerometer() {
        return minDelayAccelerometer;
    }

    public void setMinDelayAccelerometer(float minDelayAccelerometer) {
        this.minDelayAccelerometer = minDelayAccelerometer;
        Log.d("maxDelayTestAccelerometer: ", Float.toString(minDelayAccelerometer));
        setChanged();
        notifyObservers(4);
    }

    public float getMinDelayGyroscope() {
        return minDelayGyroscope;
    }

    public void setMinDelayGyroscope(float minDelayGyroscope) {
        this.minDelayGyroscope = minDelayGyroscope;
        Log.d("maxDelayTestGyro: ", Float.toString(minDelayGyroscope));
        setChanged();
        notifyObservers(5);
    }

    public float getMinDelayMagnetometer() {
        return minDelayMagnetometer;
    }

    public void setMinDelayMagnetometer(float minDelayMagnetometer) {
        this.minDelayMagnetometer = minDelayMagnetometer;
        Log.d("maxDelayTestMagnetometer: ", Float.toString(minDelayMagnetometer));
        setChanged();
        notifyObservers(6);
    }

}
