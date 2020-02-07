package com.dandrzas.inertialsensorsviewer.Model;


import java.util.Observable;

public class SensorsDataRepository extends Observable {

    private float[] AccelerometerValue = new float[3];
    private float[] GyroscopeValue = new float[3];
    private float[] MagnetometerValue = new float[3];
    private static SensorsDataRepository ourInstance;

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
        notifyObservers();
    }

    public float[] getGyroscopeValue() {
        return GyroscopeValue;
    }

    public void setGyroscopeValue(float[] gyroscopeValue) {
        GyroscopeValue = gyroscopeValue;
        setChanged();
        notifyObservers();
    }

    public float[] getMagnetometerValue() {
        return MagnetometerValue;
    }

    public void setMagnetometerValue(float[] magnetometerValue) {
        MagnetometerValue = magnetometerValue;
        setChanged();
        notifyObservers();
    }
}
