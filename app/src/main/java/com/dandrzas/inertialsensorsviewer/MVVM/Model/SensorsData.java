package com.dandrzas.inertialsensorsviewer.MVVM.Model;


import java.util.Observable;

public class SensorsData extends Observable {

    private float[] AccelerometerValue = {0,0,0};
    private float[] GyroscopeValue = {0,0,0};
    private float[] MagnetometerValue = {0,0,0};

    private static final SensorsData ourInstance = new SensorsData();

    private SensorsData()
    {
    }

    public static SensorsData getInstance()
    {
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
