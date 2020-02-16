package com.dandrzas.inertialsensorsviewer;

import java.util.Observable;

public class SensorsDataRepository extends Observable {

    private float[] AccelerometerValue = new float[6];
    private float[] GyroscopeValue = new float[6];
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
        setChanged();
        notifyObservers(4);
    }

    public float getMinDelayGyroscope() {
        return minDelayGyroscope;
    }

    public void setMinDelayGyroscope(float minDelayGyroscope) {
        this.minDelayGyroscope = minDelayGyroscope;
        setChanged();
        notifyObservers(5);
    }

    public float getMinDelayMagnetometer() {
        return minDelayMagnetometer;
    }

    public void setMinDelayMagnetometer(float minDelayMagnetometer) {
        this.minDelayMagnetometer = minDelayMagnetometer;
        setChanged();
        notifyObservers(6);
    }

}
