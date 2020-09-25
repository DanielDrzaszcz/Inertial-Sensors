package com.dandrzas.inertialsensors.data;

import android.hardware.SensorManager;
import android.util.Log;

import java.util.Observable;

public abstract class OrientationAlgorithm extends Observable implements IFOrientationAlgorithm {
    protected SensorData sensorAccelerometer;
    protected SensorData sensorGyroscope;
    protected SensorData sensorMagnetometer;
    protected float pitchAccelerometer;
    protected float rollAccelerometer;
    protected float yawMagnetometer;
    protected float pitchGyroscope;
    protected float rollGyroscope;
    protected float yawGyroscope;
    private float[] gyroscopeXYXPrev = new float[3];
    protected float[] rollPitchYaw = new float[3];
    protected long previousSampleTime;
    protected long actualSampleTime;
    private boolean isRunning;
    private boolean isUpdatedAccelerometer;
    private boolean isUpdatedGyroscope;
    private boolean isUpdatedMagnetometer;
    private boolean isGyroscopeAvailable;
    private float[] rotationMatrixNWURPY = new float[9];
    private float[] rotationMatrixRPYNWU = new float[9];
    private boolean gyroInitDone;

    private final String TAG = OrientationAlgorithm.class.getSimpleName();

    protected void calc()
    {
        double rollSin = Math.sin(Math.toRadians(rollPitchYaw[0]));
        double rollCos = Math.cos(Math.toRadians(rollPitchYaw[0]));
        double pitchSin = Math.sin(Math.toRadians(rollPitchYaw[1]));
        double pitchCos = Math.cos(Math.toRadians(rollPitchYaw[1]));
        double yawSin = Math.sin(Math.toRadians(rollPitchYaw[2]));
        double yawCos = Math.cos(Math.toRadians(rollPitchYaw[2]));

        rotationMatrixRPYNWU[0] = (float) (yawSin * pitchCos);
        rotationMatrixRPYNWU[1] = (float) (rollCos * yawCos + rollSin * yawSin * pitchSin);
        rotationMatrixRPYNWU[2] = (float) ((-1) * rollSin * yawCos + rollCos * yawSin * pitchSin);
        rotationMatrixRPYNWU[3] = (float) (yawCos * pitchCos);
        rotationMatrixRPYNWU[4] = (float) ((-1) * rollCos * yawSin + rollSin * yawCos * pitchSin);
        rotationMatrixRPYNWU[5] = (float) (rollSin * yawSin + rollCos * yawCos * pitchSin);
        rotationMatrixRPYNWU[6] = (float) (pitchSin);
        rotationMatrixRPYNWU[7] = (float) ((-1) * rollSin * pitchCos);
        rotationMatrixRPYNWU[8] = (float) ((-1) * rollCos * pitchCos);

        rotationMatrixNWURPY[0]= rotationMatrixRPYNWU[0];
        rotationMatrixNWURPY[1]= rotationMatrixRPYNWU[3];
        rotationMatrixNWURPY[2]= rotationMatrixRPYNWU[6];
        rotationMatrixNWURPY[3]= rotationMatrixRPYNWU[1];
        rotationMatrixNWURPY[4]= rotationMatrixRPYNWU[4];
        rotationMatrixNWURPY[5]= rotationMatrixRPYNWU[7];
        rotationMatrixNWURPY[6]= rotationMatrixRPYNWU[2];
        rotationMatrixNWURPY[7]= rotationMatrixRPYNWU[5];
        rotationMatrixNWURPY[8]= rotationMatrixRPYNWU[8];


        previousSampleTime = actualSampleTime;
        isUpdatedAccelerometer = false;
        isUpdatedGyroscope = false;
        isUpdatedMagnetometer = false;
    }

    public void startComputing(boolean gyroscopeAvailable) {
        clearData();
        isRunning = true;
        isGyroscopeAvailable = gyroscopeAvailable;
        isUpdatedAccelerometer = false;
        isUpdatedGyroscope = false;
        isUpdatedMagnetometer = false;
    }

    protected void clearData()
    {
        rollPitchYaw[0] = 0;
        rollPitchYaw[1] = 0;
        rollPitchYaw[2] = 0;
        pitchGyroscope = 0;
        rollGyroscope = 0;
        yawGyroscope = 0;
        gyroscopeXYXPrev[0] = 0;
        gyroscopeXYXPrev[1]  = 0;
        gyroscopeXYXPrev[2]  = 0;
        isUpdatedAccelerometer = false;
        isUpdatedGyroscope = false;
        isUpdatedMagnetometer = false;
        gyroInitDone = false;
    }

    public void stopComputing() {
        isRunning = false;
    }

    @Override
    public long getSampleTime() {
        return actualSampleTime;
    }

    public float getPreviousSampleTime() {
        return (float) previousSampleTime;
    }

    public boolean isUpdatedAccelerometer() {
        return isUpdatedAccelerometer;
    }

    public void setUpdatedAccelerometer() {
        isUpdatedAccelerometer = true;
        if(isRunning&&isUpdatedAccelerometer&&(isUpdatedGyroscope||(!isGyroscopeAvailable))&&isUpdatedMagnetometer){
            actualSampleTime = sensorAccelerometer.getSampleTime();
            calc();
        }
    }

    public boolean isUpdatedGyroscope() {
        return isUpdatedGyroscope;
    }

    public void setUpdatedGyroscope() {
        isUpdatedGyroscope = true;
        if(isRunning&&isUpdatedAccelerometer&&(isUpdatedGyroscope||(!isGyroscopeAvailable))&&isUpdatedMagnetometer){
            actualSampleTime = sensorGyroscope.getSampleTime();
            calc();
        }
    }

    public boolean isUpdatedMagnetometer() {
        return isUpdatedMagnetometer;
    }

    public void setUpdatedMagnetometer() {
        isUpdatedMagnetometer = true;
        if(isRunning&&isUpdatedAccelerometer&&(isUpdatedGyroscope||(!isGyroscopeAvailable))&&isUpdatedMagnetometer){
            actualSampleTime = sensorMagnetometer.getSampleTime();
            calc();
        }
    }

    protected float[] calcRollPitchYawAccelMagn(){
        float pitchAccelerometerTemp = (float)(Math.atan2(sensorAccelerometer.getSampleValue()[0], sensorAccelerometer.getSampleValue()[2]));
        float sinPitch = (float)Math.sin(pitchAccelerometerTemp);
        float cosPitch = (float)Math.cos(pitchAccelerometerTemp);

        float rollAccelerometerTemp = (float)((-1)*Math.atan2(sensorAccelerometer.getSampleValue()[1], sinPitch*sensorAccelerometer.getSampleValue()[0]+cosPitch*sensorAccelerometer.getSampleValue()[2]));
        float sinRoll = (float)Math.sin(rollAccelerometerTemp);
        float cosRoll = (float)Math.cos(rollAccelerometerTemp);

        float yawArcTangArgY = sinPitch*sensorMagnetometer.getSampleValue()[2] - cosPitch*sensorMagnetometer.getSampleValue()[0];
        float yawAngArgX = cosRoll*sensorMagnetometer.getSampleValue()[1] + sinRoll*sinPitch*sensorMagnetometer.getSampleValue()[0] + cosPitch*sinRoll*sensorMagnetometer.getSampleValue()[2];
        // float yawArcTangArgY = sinRoll*sensorMagnetometer.getSampleValue()[2] - cosRoll*sensorMagnetometer.getSampleValue()[1];
         //float yawAngArgX = cosPitch*sensorMagnetometer.getSampleValue()[0] + sinRoll*sinPitch*sensorMagnetometer.getSampleValue()[1] + cosRoll*sinPitch*sensorMagnetometer.getSampleValue()[2];
        float yawMagnetometerTemp = (float)((-1)*Math.atan2(yawArcTangArgY, yawAngArgX));

        rollAccelerometer = (-1)*rollAccelerometerTemp;
        pitchAccelerometer = (-1)*pitchAccelerometerTemp;
        yawMagnetometer = yawMagnetometerTemp;

        float[] result ={rollAccelerometer, pitchAccelerometer, yawMagnetometer};
        return result;
    }
    protected float[] calcRollPitchYawGyroscope(){
        float NS2S = 1.0f / 1000000000.0f;

        if(gyroInitDone){
            rollGyroscope = (float)(rollGyroscope + ((actualSampleTime-previousSampleTime)*NS2S)*((gyroscopeXYXPrev[0]+sensorGyroscope.getSampleValue()[0])/2));
            if(rollGyroscope>Math.PI){
                rollGyroscope -= 2*Math.PI;
            } else if(rollGyroscope<(-1)*Math.PI){
                rollGyroscope += 2*Math.PI;
            }
            pitchGyroscope = (float)(pitchGyroscope + ((actualSampleTime-previousSampleTime)*NS2S)*((gyroscopeXYXPrev[1]+sensorGyroscope.getSampleValue()[1])/2));
            if(pitchGyroscope>Math.PI){
                pitchGyroscope -= 2*Math.PI;
            } else if(pitchGyroscope<(-1)*Math.PI){
                pitchGyroscope += 2*Math.PI;
            }
            yawGyroscope= (float)((yawGyroscope + ((actualSampleTime-previousSampleTime)*NS2S)*((gyroscopeXYXPrev[2]+sensorGyroscope.getSampleValue()[2])/2)));
            if(yawGyroscope>Math.PI){
                yawGyroscope -= 2*Math.PI;
            } else if(yawGyroscope<(-1)*Math.PI){
                yawGyroscope += 2*Math.PI;
            }
        }
        else{
            rollGyroscope = rollAccelerometer;
            pitchGyroscope = pitchAccelerometer;
            yawGyroscope = yawMagnetometer;
            gyroInitDone = true;
        }

        gyroscopeXYXPrev[0] = sensorGyroscope.getSampleValue()[0];
        gyroscopeXYXPrev[1] = sensorGyroscope.getSampleValue()[1];
        gyroscopeXYXPrev[2] = sensorGyroscope.getSampleValue()[2];

        float[] result ={rollGyroscope, pitchGyroscope, yawGyroscope};
        return result;
    }

    @Override
    public float[] getRollPitchYaw(boolean remapToVertical) {
        if (remapToVertical) {

            float[] remappedRotationMatrix = new float[9];
            float[] remappedOrientation = new float[3];
            SensorManager.remapCoordinateSystem(rotationMatrixNWURPY, SensorManager.AXIS_X, SensorManager.AXIS_Z, remappedRotationMatrix);
            remappedOrientation[0] = (float) Math.toDegrees(Math.atan2(remappedRotationMatrix[7], remappedRotationMatrix[8]));
            remappedOrientation[1] = (float) Math.toDegrees(Math.asin(-1*remappedRotationMatrix[6]));
            remappedOrientation[2] = (float) Math.toDegrees(Math.atan2(remappedRotationMatrix[3], remappedRotationMatrix[0]));

            return remappedOrientation;
        } else {
            return rollPitchYaw;
        }
    }

    @Override
    public float[] getRotationMatrixNWURPY() {
        return rotationMatrixNWURPY;
    }

    @Override
    public float[] getRotationMatrixRPYNWU() {
        return rotationMatrixRPYNWU;
    }
}
