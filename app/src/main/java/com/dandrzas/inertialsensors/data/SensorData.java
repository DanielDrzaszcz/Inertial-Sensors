package com.dandrzas.inertialsensors.data;

import android.util.Log;

import java.util.Observable;

public class SensorData extends Observable {
    private float[] sampleValue = new float[3];
    private long sampleTime;
    private boolean filterLowPassEnable;
    private float filterLowPassGain = 0.95f;
    private int sensorID;
    private float minDelay;

    public SensorData(int sensorID) {
        this.sensorID = sensorID;
    }

    public void setSampleValue(float[] sampleValue) {
        if(filterLowPassEnable){
            this.sampleValue = filtrationLowPass(sampleValue);
        }
        else{
            this.sampleValue = sampleValue;
        }
        setChanged();
        notifyObservers(sensorID);
    }

    public float[] getSampleValue() {
        return sampleValue;
    }

    public void setSampleTime(long value) {
        this.sampleTime = value;
    }

    public long getSampleTime() {
        return sampleTime;
    }

    public boolean isFilterLowPassEnable() {
        return filterLowPassEnable;
    }

    public void setFilterLowPassEnable(boolean filterLowPassEnable) {
        this.filterLowPassEnable = filterLowPassEnable;
    }

    public float getFilterLowPassGain() {
        return filterLowPassGain;
    }

    public void setFilterLowPassGain(float filterLowPassGain) {
        this.filterLowPassGain = filterLowPassGain;
    }

    private float[] filtrationLowPass(float[] newSampleValue){
        float[] filteredValue = new float[3];
        filteredValue[0] = filterLowPassGain * this.sampleValue[0] + (1 - filterLowPassGain) * newSampleValue[0];
        filteredValue[1] = filterLowPassGain * this.sampleValue[1] + (1 - filterLowPassGain) * newSampleValue[1];
        filteredValue[2] = filterLowPassGain * this.sampleValue[2] + (1 - filterLowPassGain) * newSampleValue[2];
        Log.d("FilterTest: ", Float.toString(filterLowPassGain));
        return filteredValue;
    }

    public void setMinDelay(float value) {
        this.minDelay = value;
    }

    public float getMinDelay() {
        return minDelay;
    }

}