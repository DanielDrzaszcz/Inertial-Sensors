package com.dandrzas.inertialsensorsviewer.model.data;

import com.dandrzas.inertialsensorsviewer.model.domain.SensorData;

import java.util.Observable;

public class SensorsDataRepository extends Observable {

    private static SensorsDataRepository ourInstance;

    private SensorData sensorAccelerometer = new SensorData();
    private SensorData sensorGyroscope = new SensorData();
    private SensorData sensorMagnetometer = new SensorData();

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
        return sensorAccelerometer.getValue();
    }

    public void setAccelerometerValue(float[] accelerometerValue) {
        sensorAccelerometer.setValue(accelerometerValue);
        setChanged();
        notifyObservers(1);
    }

    public float[] getGyroscopeValue() {
        return sensorGyroscope.getValue();
    }

    public void setGyroscopeValue(float[] gyroscopeValue) {
        sensorGyroscope.setValue(gyroscopeValue);
        setChanged();
        notifyObservers(2);
    }

    public float[] getMagnetometerValue() {
        return sensorMagnetometer.getValue();
    }

    public void setMagnetometerValue(float[] magnetometerValue) {
        sensorMagnetometer.setValue(magnetometerValue);
        setChanged();
        notifyObservers(3);
    }

    public float getMinDelayAccelerometer() {
        return sensorAccelerometer.getMinDelay();
    }

    public void setMinDelayAccelerometer(float minDelayAccelerometer) {
        sensorAccelerometer.setMinDelay(minDelayAccelerometer);
        setChanged();
        notifyObservers(4);
    }

    public float getMinDelayGyroscope() {
        return sensorGyroscope.getMinDelay();
    }

    public void setMinDelayGyroscope(float minDelayGyroscope) {
        sensorGyroscope.setMinDelay(minDelayGyroscope);
        setChanged();
        notifyObservers(5);
    }

    public float getMinDelayMagnetometer() {
        return sensorMagnetometer.getMinDelay();
    }

    public void setMinDelayMagnetometer(float minDelayMagnetometer) {
        sensorMagnetometer.setMinDelay(minDelayMagnetometer);
        setChanged();
        notifyObservers(6);
    }
}