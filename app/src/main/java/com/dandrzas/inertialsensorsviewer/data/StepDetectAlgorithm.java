package com.dandrzas.inertialsensorsviewer.data;

import android.util.Log;

import java.util.LinkedList;
import java.util.Observable;

public class StepDetectAlgorithm extends Observable {
    private SensorData sensorAccelerometer;
    private float actualSampleTime;
    private boolean isRunning;
    private float accelerationMagnitude;
    private float accelerationAverage;
    private float accelerationVariance;
    private float threshold1Value;
    private float threshold2Value;
    private boolean stepDetection;
    private int parWindowSize = 50;
    private int bufferSize = 2*parWindowSize+1;
    private LinkedList<Float> accelerationMagnitudeBuffer = new LinkedList<>();
    private LinkedList<Float> accelerationAverageBuffer = new LinkedList<>();
    private LinkedList<Float> threshold1ValuesBuffer = new LinkedList<>();
    private LinkedList<Float> threshold2ValuesBuffer = new LinkedList<>();
    private LinkedList<Float> accelerationVarianceBuffer = new LinkedList<>();
    private LinkedList<Boolean> stepDetectionBuffer = new LinkedList<>();
    private float parThreshold1 = 2;
    private float parThreshold2 = 1;
    private int stepsCounter;

    public StepDetectAlgorithm(SensorData sensorAccelerometer) {
        this.sensorAccelerometer = sensorAccelerometer;
    }

    private void clearData() {
        accelerationMagnitudeBuffer.clear();
        accelerationAverageBuffer.clear();
        accelerationVarianceBuffer.clear();
        threshold1ValuesBuffer.clear();
        threshold2ValuesBuffer.clear();
        stepDetectionBuffer.clear();
        stepsCounter = 0;
    }

    public void startComputing(boolean gyroscopeAvailable) {
        clearData();
        isRunning = true;
    }

    public void stopComputing() {
        isRunning = false;
    }

    public void setUpdatedAccelerometer() {
        if (isRunning) {
            actualSampleTime = sensorAccelerometer.getSampleTime();
            calcSample();
        }
    }

    private void calcSample() {
        // Acceleration magnitude calc
        accelerationMagnitude = (float) Math.sqrt(Math.pow(sensorAccelerometer.getSampleValue()[0], 2) + Math.pow(sensorAccelerometer.getSampleValue()[1], 2) + Math.pow(sensorAccelerometer.getSampleValue()[2], 2));
        if (accelerationMagnitudeBuffer.size() < bufferSize) {
            accelerationMagnitudeBuffer.add(accelerationMagnitude);
        } else {
            accelerationMagnitudeBuffer.remove(0);
            accelerationMagnitudeBuffer.add(accelerationMagnitude);
        }

        // Acceleration average calc
        float sum = 0;
        for (float sample : accelerationMagnitudeBuffer) {
            sum += sample;
        }

        accelerationAverage = sum / accelerationMagnitudeBuffer.size(); // Acceleration average add to the buffer
        if (accelerationAverageBuffer.size() < bufferSize) {
            accelerationAverageBuffer.add(accelerationAverage);
        } else {
            accelerationAverageBuffer.remove(0);
            accelerationAverageBuffer.add(accelerationAverage);
        }

        // Acceleration variance calc
        sum=0;
        for(float sample: accelerationMagnitudeBuffer){
            sum += Math.pow(sample-accelerationAverage,2);
        }
        accelerationVariance = sum / accelerationMagnitudeBuffer.size();
        if (accelerationVarianceBuffer.size() < bufferSize) {   // Acceleration variance add to the buffer
            accelerationVarianceBuffer.add(accelerationVariance);
        } else {
            accelerationVarianceBuffer.remove(0);
            accelerationVarianceBuffer.add(accelerationVariance);
        }
        Log.d("StepDetectTest: ", "accelerationVariance: " + accelerationVariance);

        // Treshold 1 calc
        if(accelerationVariance > parThreshold1){
            threshold1Value = parThreshold1;
        }
        else{
            threshold1Value = 0;
        }
        if (threshold1ValuesBuffer.size() < bufferSize) {   // Threshold 1 value add to the buffer
            threshold1ValuesBuffer.add(threshold1Value);
        } else {
            threshold1ValuesBuffer.remove(0);
            threshold1ValuesBuffer.add(threshold1Value);
        }

        // Treshold 2 calc
        if(accelerationVariance < parThreshold2){
            threshold2Value = parThreshold2;
        }
        else{
            threshold2Value = 0;
        }
        if (threshold2ValuesBuffer.size() < bufferSize) {    // Threshold 2 value add to the buffer
            threshold2ValuesBuffer.add(threshold2Value);
        } else {
            threshold2ValuesBuffer.remove(0);
            threshold2ValuesBuffer.add(threshold2Value);
        }

        // Step detect
        if(accelerationMagnitudeBuffer.size()==bufferSize){
            int sampleIndex = parWindowSize;
            boolean threshold1OK = threshold1ValuesBuffer.get(sampleIndex) < threshold1ValuesBuffer.get(sampleIndex-1);
            boolean threshold2OK = false;
            for(float thresholdVal: threshold2ValuesBuffer){
                if(thresholdVal== parThreshold2){
                    threshold2OK = true;
                }
            }
            if(threshold1OK && threshold2OK){
                stepDetection = true;
                stepsCounter++;
            }

            else{
                stepDetection = false;
            }
            if (stepDetectionBuffer.size() < bufferSize) {  // Step detects add to the buffer
                stepDetectionBuffer.add(stepDetection);
            } else {
                stepDetectionBuffer.remove(0);
                stepDetectionBuffer.add(stepDetection);
            }
        }
        else {
           stepDetectionBuffer.add(false);
        }

        setChanged();
        notifyObservers();
    }

    public float getAccelerationMagnitude() {
        return accelerationMagnitudeBuffer.getFirst();
    }

    public float getAccelerationAverage() {
        return accelerationAverageBuffer.getFirst();
    }

    public float getAccelerationVariance() {
        return accelerationVarianceBuffer.getFirst();
    }

    public float getThreshold1Value() {
        return threshold1ValuesBuffer.getFirst();
    }

    public float getThreshold2Value() {
        return threshold2ValuesBuffer.getFirst();
    }

    public boolean isStepDetection() {
        int size = stepDetectionBuffer.size();
        return stepDetectionBuffer.get(size/2);
    }

    public int getStepsCounter() {
        return stepsCounter;
    }

    public void setParWindowSize(int parWindowSize) {
        accelerationMagnitudeBuffer.clear();
        accelerationAverageBuffer.clear();
        accelerationVarianceBuffer.clear();
        threshold1ValuesBuffer.clear();
        threshold2ValuesBuffer.clear();
        stepDetectionBuffer.clear();
        this.parWindowSize = parWindowSize;
    }

    public void setParThreshold1(float parThreshold1) {
        this.parThreshold1 = parThreshold1;
    }

    public void setParThreshold2(float parThreshold2) {
        this.parThreshold2 = parThreshold2;
    }
}
