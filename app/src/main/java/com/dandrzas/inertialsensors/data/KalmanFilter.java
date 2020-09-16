package com.dandrzas.inertialsensors.data;

import android.util.Log;

public class KalmanFilter extends OrientationAlgorithm implements IFOrientationAlgorithm {
    private final String TAG = KalmanFilter.class.getSimpleName();
    private KalmanSingleAxis kalmanRoll = new KalmanSingleAxis();
    private KalmanSingleAxis kalmanPitch = new KalmanSingleAxis();
    private KalmanSingleAxis kalmanYaw = new KalmanSingleAxis();
    private float parQAngle = 0.00001f; // Process noise variance for the accelerometer
    private float parQBias = 0.00003f; // Process noise variance for the gyro bias
    private float parRMeasure = 0.0002f; // Measurement noise variance

    public KalmanFilter(SensorData sensorAccelerometer, SensorData sensorGyroscope, SensorData sensorMagnetometer)
    {
        this.sensorAccelerometer = sensorAccelerometer;
        this.sensorGyroscope = sensorGyroscope;
        this.sensorMagnetometer = sensorMagnetometer;
    }

    @Override
    public void calc() {
        calcRollPitchYawAccelMagn();
        calcRollPitchYawGyroscope();

        float sampleTime = (actualSampleTime-previousSampleTime)/1000000000.0f;
        float roll = kalmanRoll.update(rollAccelerometer, sensorGyroscope.getSampleValue()[0], sampleTime);
        float pitch = kalmanPitch.update(pitchAccelerometer, sensorGyroscope.getSampleValue()[1], sampleTime);
        float yaw = kalmanYaw.update(yawMagnetometer, sensorGyroscope.getSampleValue()[2], sampleTime);

        rollPitchYaw[0] = (float) (Math.toDegrees(roll));
        rollPitchYaw[1] = (float) (Math.toDegrees(pitch));
        rollPitchYaw[2] = (float) Math.toDegrees(yaw);

        setChanged();
        notifyObservers(Constants.KALMAN_FILTER_ID);
        super.calc();
    }

    @Override
    protected void clearData() {
        super.clearData();
        kalmanRoll.clearData();
        kalmanPitch.clearData();
        kalmanYaw.clearData();
    }


    public float getParQAngle() {
        return parQAngle;
    }

    public void setParQAngle(float parQAngle) {
        this.parQAngle = parQAngle;
    }

    public float getParQBias() {
        return parQBias;
    }

    public void setParQBias(float parQBias) {
        this.parQBias = parQBias;
    }

    public float getParRMeasure() {
        return parRMeasure;
    }

    public void setParRMeasure(float parRMeasure) {
        this.parRMeasure = parRMeasure;
    }


    class KalmanSingleAxis{
        private float angle; // The angle calculated by the Kalman filter
        private float bias; // The gyro bias calculated by the Kalman filter
        private float[][] P = new float[2][2]; // Error covariance matrix
        private float[] K = new float[2];   // Kalman gain

        float update(float newAngle, float newRate, float dt){

            ////// Time Update /////
            // Project the state ahead
            angle = angle + dt * (newRate - bias);
            // Project the error covariance ahead
            P[0][0] += dt * (dt*P[1][1] - P[0][1] - P[1][0] + parQAngle);
            P[0][1] -= dt * P[1][1];
            P[1][0] -= dt * P[1][1];
            P[1][1] += parQBias * dt;

            ////// Measurement Update /////
            // Compute the Kalman gain
            K[0] = P[0][0] / (P[0][0] + parRMeasure);
            K[1] = P[1][0] / (P[0][0] + parRMeasure);
            // Update estimate with measurement
            angle += K[0] * (newAngle - angle);
            bias += K[1] * (newAngle - angle);
            // Update the error covariance
            P[0][0] -= K[0] * P[0][0];
            P[0][1] -= K[0] * P[0][1];
            P[1][0] -= K[1] * P[0][0];
            P[1][1] -= K[1] * P[0][1];

            return angle;
        }

        void clearData(){
            angle = 0.0f;
            bias = 0.0f;
            P[0][0] = 0.0f;
            P[0][1] = 0.0f;
            P[1][0] = 0.0f;
            P[1][1] = 0.0f;
        }
    }
}
