package com.dandrzas.inertialsensors.data;

import android.util.Log;

import java.util.Observable;

public class InertialTrackingAlgorithm  extends Observable {
    private SensorData sensorAccelerometer;
    private final String TAG = InertialTrackingAlgorithm.class.getSimpleName();
    private float[] calculatedMovement = new float[3];
    private float[] linearAcceleration = new float[3];
    private float[] gravity = new float[3];
    private float[] linearAccelerationPrev = new float[3];
    private float[] calculatedVelocity = new float[3];
    private float[] calculatedVelocityPrev = new float[3];
    private double previousSampleTime;
    private double actualSampleTime;
    private boolean isRunning;
    private float parAccelerometerHPFGain = 0.99f;
    boolean firstCalcDone;
    private float[] initialAcceleration = new float[3];
    private int calcCounter;
    private IFOrientationAlgorithm orientationAlgorithm;
    private float[] accelerationGlobal = new float[3];

    public InertialTrackingAlgorithm(SensorData sensorAccelerometer, IFOrientationAlgorithm orientationAlgorithm) {
        this.sensorAccelerometer = sensorAccelerometer;
        this.orientationAlgorithm = orientationAlgorithm;
    }

    public void calc()
    {
        Log.d(TAG, " DataChanged: " + calculatedMovement[0] + " " + calculatedMovement[1] + " " + calculatedMovement[2]);
        actualSampleTime = sensorAccelerometer.getSampleTime();
        if(!firstCalcDone){
            previousSampleTime = actualSampleTime;
            linearAccelerationPrev[0]=linearAcceleration[0];
            linearAccelerationPrev[1]=linearAcceleration[1];
            linearAccelerationPrev[2]=linearAcceleration[2];
            calculatedVelocityPrev[0]= calculatedVelocity[0];
            calculatedVelocityPrev[1]= calculatedVelocity[1];
            calculatedVelocityPrev[2]= calculatedVelocity[2];
        }
        if(calcCounter==500){
            initialAcceleration[0] = accelerationGlobal[0];
            initialAcceleration[1] = accelerationGlobal[1];
            initialAcceleration[2] = accelerationGlobal[2];
            Log.d("InrtialTrackingTestIni ", "initialAcceleration[0]: " + initialAcceleration[0]);
            Log.d("InrtialTrackingTestIni ", "initialAcceleration[1]: " + initialAcceleration[1]);
            Log.d("InrtialTrackingTestIni ", "initialAcceleration[2]: " + initialAcceleration[2]);
        }
        calcLinearGravity();
        calculatedVelocity = calcIntegral(linearAcceleration, linearAccelerationPrev, calculatedVelocity);
        Log.d("InrtialTrackingTest: ", "velocity: " + calculatedVelocity[0]);
        Log.d("InrtialTrackingTest: ", "velocity: " + calculatedVelocity[1]);
        Log.d("InrtialTrackingTest: ", "velocity: " + calculatedVelocity[2]);

        calculatedMovement = calcIntegral(calculatedVelocity, calculatedVelocityPrev, calculatedMovement);
        setChanged();
        notifyObservers(Constants.INERTIAL_TRACKER_ID);

        linearAccelerationPrev[0]=linearAcceleration[0];
        linearAccelerationPrev[1]=linearAcceleration[1];
        linearAccelerationPrev[2]=linearAcceleration[2];
        calculatedVelocityPrev[0]= calculatedVelocity[0];
        calculatedVelocityPrev[1]= calculatedVelocity[1];
        calculatedVelocityPrev[2]= calculatedVelocity[2];
        previousSampleTime = actualSampleTime;
        firstCalcDone = true;
        calcCounter++;
        Log.d("InrtialTrackingTestIni", "calcCounter: " + calcCounter);
    }

    private void clearData()
    {
        calculatedMovement[0] = 0;
        calculatedMovement[1] = 0;
        calculatedMovement[2] = 0;
        calculatedVelocity[0] = 0;
        calculatedVelocity[1] = 0;
        calculatedVelocity[2] = 0;
        firstCalcDone = false;
        calcCounter = 0;
        initialAcceleration[0]=0;
        initialAcceleration[1]=0;
        initialAcceleration[2]=0;
    }

    public void startComputing() {
        clearData();
        isRunning = true;
    }

    public void stopComputing() {
        isRunning = false;
    }

    public double getPreviousSampleTime() {
        return previousSampleTime;
    }

    private void calcLinearGravity(){
        float[] acceleration = new float[3];
        acceleration[0] = sensorAccelerometer.getSampleValue()[0];
        acceleration[1] = sensorAccelerometer.getSampleValue()[1];
        acceleration[2] = sensorAccelerometer.getSampleValue()[2];

        // Convert acceleration to global coordinates
        accelerationGlobal[0] = acceleration[0] * orientationAlgorithm.getRotationMatrixRPYNWU(false)[0] + acceleration[1] * orientationAlgorithm.getRotationMatrixRPYNWU(false)[1] + acceleration[2] * orientationAlgorithm.getRotationMatrixRPYNWU(false)[2];
        accelerationGlobal[1] = acceleration[0] * orientationAlgorithm.getRotationMatrixRPYNWU(false)[3] + acceleration[1] * orientationAlgorithm.getRotationMatrixRPYNWU(false)[4] + acceleration[2] * orientationAlgorithm.getRotationMatrixRPYNWU(false)[5];
        accelerationGlobal[2] = acceleration[0] * orientationAlgorithm.getRotationMatrixRPYNWU(false)[6] + acceleration[1] * orientationAlgorithm.getRotationMatrixRPYNWU(false)[7] + acceleration[2] * orientationAlgorithm.getRotationMatrixRPYNWU(false)[8];

  /*      accelerationGlobal[0] = acceleration[0];
        accelerationGlobal[1] = acceleration[1];
        accelerationGlobal[2] = acceleration[2];
*/
        if(calcCounter<500) {
            gravity[0] = 0;
            gravity[1] = 0;
            gravity[2] = 0;
            Log.d("InrtialTrackingTest: ", "firstCalcDone");
            linearAcceleration[0] = 0;
            linearAcceleration[1] = 0;
            linearAcceleration[2] = 0;

        }
        else{
          /*  gravity[0] = parAccelerometerHPFGain * gravity[0] + (1 - parAccelerometerHPFGain) * acceleration[0];
            gravity[1] = parAccelerometerHPFGain * gravity[1] + (1 - parAccelerometerHPFGain) * acceleration[1];
            gravity[2] = parAccelerometerHPFGain * gravity[2] + (1 - parAccelerometerHPFGain) * acceleration[2];
*/
            gravity[0] = initialAcceleration[0];
            gravity[1] = initialAcceleration[1];
            gravity[2] = initialAcceleration[2];
            linearAcceleration[0] = accelerationGlobal[0]-gravity[0];
            linearAcceleration[1] = accelerationGlobal[1]-gravity[1];
            linearAcceleration[2] = accelerationGlobal[2]-gravity[2];
        }

        float totalGravity = (float) Math.sqrt(gravity[0]*gravity[0]+gravity[1]*gravity[1]+gravity[2]*gravity[2]);

        Log.d("InrtialTrackingTest: ", "gravity: " + gravity[0]);
        Log.d("InrtialTrackingTest: ", "gravity: " + gravity[1]);
        Log.d("InrtialTrackingTest: ", "gravity: " + gravity[2]);
        Log.d("InrtialTrackingTest: ", "total gravity: " + totalGravity);

        float totalAcceleration = (float) Math.sqrt(linearAcceleration[0]*linearAcceleration[0]+linearAcceleration[1]*linearAcceleration[1]+linearAcceleration[2]*linearAcceleration[2]);
        Log.d("InrtialTrackingTest: ", "linearAcceleration: " + linearAcceleration[0]);
        Log.d("InrtialTrackingTest: ", "linearAcceleration: " + linearAcceleration[1]);
        Log.d("InrtialTrackingTest: ", "linearAcceleration: " + linearAcceleration[2]);
        Log.d("InrtialTrackingTest: ", "total linearAcceleration: " + totalAcceleration);
    }

    private float[] calcIntegral(float[] orgSignal, float[] orgSignalPrev, float[] actIntegratedValue){
        float NS2S = 1.0f / 1000000000.0f;
        float[] integratedSignal = new float[3];

        integratedSignal[0] = (float)(actIntegratedValue[0] + ((actualSampleTime-previousSampleTime)*NS2S)*((orgSignalPrev[0]+orgSignal[0])/2));
        integratedSignal[1] = (float)(actIntegratedValue[1] + ((actualSampleTime-previousSampleTime)*NS2S)*((orgSignalPrev[1]+orgSignal[1])/2));
        integratedSignal[2] = (float)(actIntegratedValue[2] + ((actualSampleTime-previousSampleTime)*NS2S)*((orgSignalPrev[2]+orgSignal[2])/2));

        return integratedSignal;
    }

    public float[] getCalculatedMovement() {
        return calculatedMovement;
    }

    public float[] getCalculatedVelocity() {
        return calculatedVelocity;
    }

    public float[] getLinearAcceleration() {
        return linearAcceleration;
    }

    public float[] getGravity() {
        return gravity;
    }

    public float[] getAccelerationGlobal() {
        return accelerationGlobal;
    }

    public IFOrientationAlgorithm getOrientationAlgorithm() {
        return orientationAlgorithm;
    }

    public double getActualSampleTime() {
        return actualSampleTime;
    }

}
