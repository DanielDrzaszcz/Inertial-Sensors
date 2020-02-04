package com.dandrzas.inertialsensorsviewer.MVVM.Model;


import java.util.Observable;

public class SensorsData extends Observable {

    private float[] sensorValue = {0,0,0};

    private static final SensorsData ourInstance = new SensorsData();

    private SensorsData()
    {
    }

    public static SensorsData getInstance()
    {
        return ourInstance;
    }

    public float[] getSensorValue() {
        return sensorValue;
    }

    public void setSensorValue(float[] sensorValue) {
        this.sensorValue = sensorValue;
        setChanged();
        notifyObservers();
    }

}
