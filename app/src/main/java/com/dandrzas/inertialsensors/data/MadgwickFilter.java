package com.dandrzas.inertialsensors.data;

import android.hardware.SensorManager;
import android.util.Log;

public class MadgwickFilter extends OrientationAlgorithm implements IFOrientationAlgorithm {

    private double samplePeriod;
    private float parBeta = 0.1f;
    private float[] quaternion;
    boolean firstCalcDone;

    @Override
    protected void calc() {

        float[] gyroVal = sensorGyroscope.getSampleValue();
        float[] accelVal = sensorAccelerometer.getSampleValue();
        float[] magnVal = sensorMagnetometer.getSampleValue();

        if(firstCalcDone){
            update(gyroVal[0], gyroVal[1], gyroVal[2], accelVal[0], accelVal[1], accelVal[2], magnVal[0], magnVal[1], magnVal[2]);
            //update(gyroVal[0], gyroVal[1], gyroVal[2], accelVal[0], accelVal[1], accelVal[2]);
            samplePeriod = (actualSampleTime-previousSampleTime)/1000000000;
            Log.d("Madgwick time: ", Double.toString(samplePeriod));
            float roll = (float)Math.atan2(quaternion[0] * quaternion[1] + quaternion[2] * quaternion[3], 0.5f - quaternion[1] * quaternion[1] - quaternion[2] * quaternion[2]);
            float pitch = (float)Math.asin(-2.0f * (quaternion[1] * quaternion[3] - quaternion[0] * quaternion[2]));
            float yaw  = (float)Math.atan2((-1)*(quaternion[1] * quaternion[2] + quaternion[0] * quaternion[3]), 0.5f - quaternion[2] * quaternion[2] - quaternion[3] * quaternion[3]);

            rollPitchYaw[0] = (float) Math.toDegrees(roll);
            rollPitchYaw[1] = (float) Math.toDegrees(pitch);
            rollPitchYaw[2] = (float) Math.toDegrees(yaw);

            setChanged();
            notifyObservers(Constants.MADGWICK_FILTER_ID);
        }

        super.calc();
        firstCalcDone = true;

    }

    @Override
    public float[] getRollPitchYaw(boolean remapToVertical) {
        if (remapToVertical) {
            float[] rotationQuaternion = {0.7071068f,0, 0.7071068f,0};
            float[] remappedQuaternion = new float[4];
            float[] remappedOrientation = new float[3];


            remappedQuaternion[0] = quaternion[0] * rotationQuaternion[0] - quaternion[1] * rotationQuaternion[1] - quaternion[2] * rotationQuaternion[2] - quaternion[3] * rotationQuaternion[3];
            remappedQuaternion[1] = quaternion[0] * rotationQuaternion[1] + quaternion[1] * rotationQuaternion[0] + quaternion[2] * rotationQuaternion[3] - quaternion[3] * rotationQuaternion[2];
            remappedQuaternion[2]= quaternion[0] * rotationQuaternion[2] - quaternion[1] * rotationQuaternion[3] + quaternion[2] * rotationQuaternion[0] + quaternion[3] * rotationQuaternion[1];
            remappedQuaternion[3] = quaternion[0] * rotationQuaternion[3] + quaternion[1] * rotationQuaternion[2] - quaternion[2] * rotationQuaternion[1] + quaternion[3] * rotationQuaternion[0];

            float remappedRoll = (float)Math.atan2(remappedQuaternion[0] * remappedQuaternion[1] + remappedQuaternion[2] * remappedQuaternion[3], 0.5f - remappedQuaternion[1] * remappedQuaternion[1] - remappedQuaternion[2] * remappedQuaternion[2]);
            float remappedPitch = (float)Math.asin(-2.0f * (remappedQuaternion[1] * remappedQuaternion[3] - remappedQuaternion[0] * remappedQuaternion[2]));
            float remappedYaw  = (float)Math.atan2((-1)*(remappedQuaternion[1] * remappedQuaternion[2] + remappedQuaternion[0] * remappedQuaternion[3]), 0.5f - remappedQuaternion[2] * remappedQuaternion[2] - remappedQuaternion[3] * remappedQuaternion[3]);

            remappedOrientation[0] = (float) Math.toDegrees(remappedRoll);
            remappedOrientation[1] = (float) Math.toDegrees(remappedPitch);
            remappedOrientation[2] = (float) Math.toDegrees(remappedYaw);

            return remappedOrientation;
        } else {
            return rollPitchYaw;
        }
    }

    @Override
    protected void clearData() {
        super.clearData();
        firstCalcDone = false;
    }

    public double getSamplePeriod() {
        return samplePeriod;
    }

    public float getParBeta() {
        return parBeta;
    }

    public void setParBeta(float parBeta) {
        this.parBeta = parBeta;
    }

    public float[] getQuaternion() {
        return quaternion;
    }

    public MadgwickFilter(SensorData sensorAccelerometer, SensorData sensorGyroscope, SensorData sensorMagnetometer) {
        this.sensorAccelerometer = sensorAccelerometer;
        this.sensorGyroscope = sensorGyroscope;
        this.sensorMagnetometer = sensorMagnetometer;
        this.quaternion = new float[] { 1f, 0f, 0f, 0f };
    }

    /**
     * Algorithm AHRS update method. Requires only gyroscope and accelerometer
     * data.
     * <p>
     * Optimised for minimal arithmetic. <br>
     * Total ±: 160 <br>
     * Total *: 172 <br>
     * Total /: 5 <br>
     * Total sqrt: 5 <br>
     *
     * @param gx
     *            Gyroscope x axis measurement in radians/s.
     * @param gy
     *            Gyroscope y axis measurement in radians/s.
     * @param gz
     *            Gyroscope z axis measurement in radians/s.
     * @param ax
     *            Accelerometer x axis measurement in any calibrated units.
     * @param ay
     *            Accelerometer y axis measurement in any calibrated units.
     * @param az
     *            Accelerometer z axis measurement in any calibrated units.
     * @param mx
     *            Magnetometer x axis measurement in any calibrated units.
     * @param my
     *            Magnetometer y axis measurement in any calibrated units.
     * @param mz
     *            Magnetometer z axis measurement in any calibrated units.
     */
    public void update(float gx, float gy, float gz, float ax, float ay,
                       float az, float mx, float my, float mz) {
        float q1 = quaternion[0], q2 = quaternion[1], q3 = quaternion[2], q4 = quaternion[3]; // short
        // name
        // local
        // variable
        // for
        // readability
        float norm;
        float hx, hy, _2bx, _2bz;
        float s1, s2, s3, s4;
        float qDot1, qDot2, qDot3, qDot4;

        // Auxiliary variables to avoid repeated arithmetic
        float _2q1mx;
        float _2q1my;
        float _2q1mz;
        float _2q2mx;
        float _4bx;
        float _4bz;
        float _2q1 = 2f * q1;
        float _2q2 = 2f * q2;
        float _2q3 = 2f * q3;
        float _2q4 = 2f * q4;
        float _2q1q3 = 2f * q1 * q3;
        float _2q3q4 = 2f * q3 * q4;
        float q1q1 = q1 * q1;
        float q1q2 = q1 * q2;
        float q1q3 = q1 * q3;
        float q1q4 = q1 * q4;
        float q2q2 = q2 * q2;
        float q2q3 = q2 * q3;
        float q2q4 = q2 * q4;
        float q3q3 = q3 * q3;
        float q3q4 = q3 * q4;
        float q4q4 = q4 * q4;

        // Normalise accelerometer measurement
        norm = (float) Math.sqrt(ax * ax + ay * ay + az * az);
        if (norm == 0f)
            return; // handle NaN
        norm = 1 / norm; // use reciprocal for division
        ax *= norm;
        ay *= norm;
        az *= norm;

        // Normalise magnetometer measurement
        norm = (float) Math.sqrt(mx * mx + my * my + mz * mz);
        if (norm == 0f)
            return; // handle NaN
        norm = 1 / norm; // use reciprocal for division
        mx *= norm;
        my *= norm;
        mz *= norm;

        // Reference direction of Earth's magnetic field
        _2q1mx = 2f * q1 * mx;
        _2q1my = 2f * q1 * my;
        _2q1mz = 2f * q1 * mz;
        _2q2mx = 2f * q2 * mx;
        hx = mx * q1q1 - _2q1my * q4 + _2q1mz * q3 + mx * q2q2 + _2q2 * my * q3
                + _2q2 * mz * q4 - mx * q3q3 - mx * q4q4;
        hy = _2q1mx * q4 + my * q1q1 - _2q1mz * q2 + _2q2mx * q3 - my * q2q2
                + my * q3q3 + _2q3 * mz * q4 - my * q4q4;
        _2bx = (float) Math.sqrt(hx * hx + hy * hy);
        _2bz = -_2q1mx * q3 + _2q1my * q2 + mz * q1q1 + _2q2mx * q4 - mz * q2q2
                + _2q3 * my * q4 - mz * q3q3 + mz * q4q4;
        _4bx = 2f * _2bx;
        _4bz = 2f * _2bz;

        // Gradient decent algorithm corrective step
        s1 = -_2q3 * (2f * q2q4 - _2q1q3 - ax) + _2q2
                * (2f * q1q2 + _2q3q4 - ay) - _2bz * q3
                * (_2bx * (0.5f - q3q3 - q4q4) + _2bz * (q2q4 - q1q3) - mx)
                + (-_2bx * q4 + _2bz * q2)
                * (_2bx * (q2q3 - q1q4) + _2bz * (q1q2 + q3q4) - my) + _2bx
                * q3
                * (_2bx * (q1q3 + q2q4) + _2bz * (0.5f - q2q2 - q3q3) - mz);
        s2 = _2q4 * (2f * q2q4 - _2q1q3 - ax) + _2q1
                * (2f * q1q2 + _2q3q4 - ay) - 4f * q2
                * (1 - 2f * q2q2 - 2f * q3q3 - az) + _2bz * q4
                * (_2bx * (0.5f - q3q3 - q4q4) + _2bz * (q2q4 - q1q3) - mx)
                + (_2bx * q3 + _2bz * q1)
                * (_2bx * (q2q3 - q1q4) + _2bz * (q1q2 + q3q4) - my)
                + (_2bx * q4 - _4bz * q2)
                * (_2bx * (q1q3 + q2q4) + _2bz * (0.5f - q2q2 - q3q3) - mz);
        s3 = -_2q1 * (2f * q2q4 - _2q1q3 - ax) + _2q4
                * (2f * q1q2 + _2q3q4 - ay) - 4f * q3
                * (1 - 2f * q2q2 - 2f * q3q3 - az) + (-_4bx * q3 - _2bz * q1)
                * (_2bx * (0.5f - q3q3 - q4q4) + _2bz * (q2q4 - q1q3) - mx)
                + (_2bx * q2 + _2bz * q4)
                * (_2bx * (q2q3 - q1q4) + _2bz * (q1q2 + q3q4) - my)
                + (_2bx * q1 - _4bz * q3)
                * (_2bx * (q1q3 + q2q4) + _2bz * (0.5f - q2q2 - q3q3) - mz);
        s4 = _2q2 * (2f * q2q4 - _2q1q3 - ax) + _2q3
                * (2f * q1q2 + _2q3q4 - ay) + (-_4bx * q4 + _2bz * q2)
                * (_2bx * (0.5f - q3q3 - q4q4) + _2bz * (q2q4 - q1q3) - mx)
                + (-_2bx * q1 + _2bz * q3)
                * (_2bx * (q2q3 - q1q4) + _2bz * (q1q2 + q3q4) - my) + _2bx
                * q2
                * (_2bx * (q1q3 + q2q4) + _2bz * (0.5f - q2q2 - q3q3) - mz);
        norm = 1f / (float) Math.sqrt(s1 * s1 + s2 * s2 + s3 * s3 + s4 * s4); // normalise
        // step
        // magnitude
        s1 *= norm;
        s2 *= norm;
        s3 *= norm;
        s4 *= norm;

        // Compute rate of change of quaternion
        qDot1 = 0.5f * (-q2 * gx - q3 * gy - q4 * gz) - parBeta * s1;
        qDot2 = 0.5f * (q1 * gx + q3 * gz - q4 * gy) - parBeta * s2;
        qDot3 = 0.5f * (q1 * gy - q2 * gz + q4 * gx) - parBeta * s3;
        qDot4 = 0.5f * (q1 * gz + q2 * gy - q3 * gx) - parBeta * s4;

        // Integrate to yield quaternion
         q1 += qDot1 * samplePeriod;
        q2 += qDot2 * samplePeriod;
        q3 += qDot3 * samplePeriod;
        q4 += qDot4 * samplePeriod;
        norm = 1f / (float) Math.sqrt(q1 * q1 + q2 * q2 + q3 * q3 + q4 * q4); // normalise
        // quaternion
        quaternion[0] = q1 * norm;
        quaternion[1] = q2 * norm;
        quaternion[2] = q3 * norm;
        quaternion[3] = q4 * norm;
    }

    /**
     * Algorithm IMU update method. Requires only gyroscope and accelerometer
     * data.
     * <p>
     * Optimised for minimal arithmetic. <br>
     * Total ±: 45 <br>
     * Total *: 85 <br>
     * Total /: 3 <br>
     * Total sqrt: 3
     *
     * @param gx
     *            Gyroscope x axis measurement in radians/s.
     * @param gy
     *            Gyroscope y axis measurement in radians/s.
     * @param gz
     *            Gyroscope z axis measurement in radians/s.
     * @param ax
     *            Accelerometer x axis measurement in any calibrated units.
     * @param ay
     *            Accelerometer y axis measurement in any calibrated units.
     * @param az
     *            Accelerometer z axis measurement in any calibrated units.
     */
    public void update(float gx, float gy, float gz, float ax, float ay,
                       float az) {
        float q1 = quaternion[0], q2 = quaternion[1], q3 = quaternion[2], q4 = quaternion[3]; // short
        // name
        // local
        // variable
        // for
        // readability
        float norm;
        float s1, s2, s3, s4;
        float qDot1, qDot2, qDot3, qDot4;

        // Auxiliary variables to avoid repeated arithmetic
        float _2q1 = 2f * q1;
        float _2q2 = 2f * q2;
        float _2q3 = 2f * q3;
        float _2q4 = 2f * q4;
        float _4q1 = 4f * q1;
        float _4q2 = 4f * q2;
        float _4q3 = 4f * q3;
        float _8q2 = 8f * q2;
        float _8q3 = 8f * q3;
        float q1q1 = q1 * q1;
        float q2q2 = q2 * q2;
        float q3q3 = q3 * q3;
        float q4q4 = q4 * q4;

        // Normalise accelerometer measurement
        norm = (float) Math.sqrt(ax * ax + ay * ay + az * az);
        if (norm == 0f)
            return; // handle NaN
        norm = 1 / norm; // use reciprocal for division
        ax *= norm;
        ay *= norm;
        az *= norm;

        // Gradient decent algorithm corrective step
        s1 = _4q1 * q3q3 + _2q3 * ax + _4q1 * q2q2 - _2q2 * ay;
        s2 = _4q2 * q4q4 - _2q4 * ax + 4f * q1q1 * q2 - _2q1 * ay - _4q2 + _8q2
                * q2q2 + _8q2 * q3q3 + _4q2 * az;
        s3 = 4f * q1q1 * q3 + _2q1 * ax + _4q3 * q4q4 - _2q4 * ay - _4q3 + _8q3
                * q2q2 + _8q3 * q3q3 + _4q3 * az;
        s4 = 4f * q2q2 * q4 - _2q2 * ax + 4f * q3q3 * q4 - _2q3 * ay;
        norm = 1f / (float) Math.sqrt(s1 * s1 + s2 * s2 + s3 * s3 + s4 * s4); // normalise
        // step
        // magnitude
        s1 *= norm;
        s2 *= norm;
        s3 *= norm;
        s4 *= norm;

        // Compute rate of change of quaternion
        qDot1 = 0.5f * (-q2 * gx - q3 * gy - q4 * gz) - parBeta * s1;
        qDot2 = 0.5f * (q1 * gx + q3 * gz - q4 * gy) - parBeta * s2;
        qDot3 = 0.5f * (q1 * gy - q2 * gz + q4 * gx) - parBeta * s3;
        qDot4 = 0.5f * (q1 * gz + q2 * gy - q3 * gx) - parBeta * s4;

        // Integrate to yield quaternion
        q1 += qDot1 * samplePeriod;
        q2 += qDot2 * samplePeriod;
        q3 += qDot3 * samplePeriod;
        q4 += qDot4 * samplePeriod;
        norm = 1f / (float) Math.sqrt(q1 * q1 + q2 * q2 + q3 * q3 + q4 * q4); // normalise
        // quaternion
        quaternion[0] = q1 * norm;
        quaternion[1] = q2 * norm;
        quaternion[2] = q3 * norm;
        quaternion[3] = q4 * norm;
    }

}
