package com.dandrzas.inertialsensors.data;

import android.util.Log;

public class MahonyFilter extends OrientationAlgorithm implements IFOrientationAlgorithm {

    private double samplePeriod;
    private float parKi = 0f;
    private float parKp = 1f;
    private float[] eInt;
    private float[] quaternion;
    boolean firstCalcDone;

    public MahonyFilter(SensorData sensorAccelerometer, SensorData sensorGyroscope, SensorData sensorMagnetometer) {
        this.sensorAccelerometer = sensorAccelerometer;
        this.sensorGyroscope = sensorGyroscope;
        this.sensorMagnetometer = sensorMagnetometer;
        this.quaternion = new float[] { 1f, 0f, 0f, 0f };
        this.eInt = new float[] { 0f, 0f, 0f };
    }

    @Override
    protected void calc() {

        float[] gyroVal = sensorGyroscope.getSampleValue();
        float[] accelVal = sensorAccelerometer.getSampleValue();
        float[] magnVal = sensorMagnetometer.getSampleValue();

        if(firstCalcDone){
            update(gyroVal[0], gyroVal[1], gyroVal[2], accelVal[0], accelVal[1], accelVal[2], magnVal[0], magnVal[1], magnVal[2]);
            //update(gyroVal[0], gyroVal[1], gyroVal[2], accelVal[0], accelVal[1], accelVal[2]);
            double sampleTimeDiff= (actualSampleTime-previousSampleTime);
            samplePeriod =  sampleTimeDiff/1000000000;
            Log.d("Mahony time: ", Double.toString(samplePeriod));
            Log.d("Mahony update: ", "kp: " + Float.toString(parKp) + "ki: " + Float.toString(parKi));
            float roll = (float)Math.atan2(quaternion[0] * quaternion[1] + quaternion[2] * quaternion[3], 0.5f - quaternion[1] * quaternion[1] - quaternion[2] * quaternion[2]);
            float pitch = (float)Math.asin(-2.0f * (quaternion[1] * quaternion[3] - quaternion[0] * quaternion[2]));
            float yaw  = (float)Math.atan2(quaternion[1] * quaternion[2] + quaternion[0] * quaternion[3], 0.5f - quaternion[2] * quaternion[2] - quaternion[3] * quaternion[3]);
            yaw += 0.5*Math.PI;
            if(yaw>Math.PI){
                yaw -= 2*Math.PI;
            }
            rollPitchYaw[0] = (float) Math.toDegrees(roll);
            rollPitchYaw[1] = (float) Math.toDegrees(pitch);
            rollPitchYaw[2] = (float) Math.toDegrees(yaw);

            setChanged();
            notifyObservers(Constants.MAHONY_FILTER_ID);
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
            float remappedYaw  = (float)Math.atan2(remappedQuaternion[1] * remappedQuaternion[2] + remappedQuaternion[0] * remappedQuaternion[3], 0.5f - remappedQuaternion[2] * remappedQuaternion[2] - remappedQuaternion[3] * remappedQuaternion[3]);
            remappedYaw += 0.5*Math.PI;
            if(remappedYaw>Math.PI){
                remappedYaw -= 2*Math.PI;
            }
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
        quaternion[0]= 1f;
        quaternion[1]= 0f;
        quaternion[2]= 0f;
        quaternion[3]= 0f;
        eInt[0] = 0f;
        eInt[1] = 0f;
        eInt[2] = 0f;
    }

    public void update(float gx, float gy, float gz, float ax, float ay,
                       float az, float mx, float my, float mz) {

        float q1 = quaternion[0], q2 = quaternion[1], q3 = quaternion[2], q4 = quaternion[3];   // short name local variable for readability
        float norm;
        float hx, hy, bx, bz;
        float vx, vy, vz, wx, wy, wz;
        float ex, ey, ez;
        float pa, pb, pc;

        // Auxiliary variables to avoid repeated arithmetic
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
        norm = (float)Math.sqrt(ax * ax + ay * ay + az * az);
        if (norm == 0f) return; // handle NaN
        norm = 1 / norm;        // use reciprocal for division
        ax *= norm;
        ay *= norm;
        az *= norm;

        // Normalise magnetometer measurement
        norm = (float)Math.sqrt(mx * mx + my * my + mz * mz);
        if (norm == 0f) return; // handle NaN
        norm = 1 / norm;        // use reciprocal for division
        mx *= norm;
        my *= norm;
        mz *= norm;

        // Reference direction of Earth's magnetic field
        hx = 2f * mx * (0.5f - q3q3 - q4q4) + 2f * my * (q2q3 - q1q4) + 2f * mz * (q2q4 + q1q3);
        hy = 2f * mx * (q2q3 + q1q4) + 2f * my * (0.5f - q2q2 - q4q4) + 2f * mz * (q3q4 - q1q2);
        bx = (float)Math.sqrt((hx * hx) + (hy * hy));
        bz = 2f * mx * (q2q4 - q1q3) + 2f * my * (q3q4 + q1q2) + 2f * mz * (0.5f - q2q2 - q3q3);

        // Estimated direction of gravity and magnetic field
        vx = 2f * (q2q4 - q1q3);
        vy = 2f * (q1q2 + q3q4);
        vz = q1q1 - q2q2 - q3q3 + q4q4;
        wx = 2f * bx * (0.5f - q3q3 - q4q4) + 2f * bz * (q2q4 - q1q3);
        wy = 2f * bx * (q2q3 - q1q4) + 2f * bz * (q1q2 + q3q4);
        wz = 2f * bx * (q1q3 + q2q4) + 2f * bz * (0.5f - q2q2 - q3q3);

        // Error is cross product between estimated direction and measured direction of gravity
        ex = (ay * vz - az * vy) + (my * wz - mz * wy);
        ey = (az * vx - ax * vz) + (mz * wx - mx * wz);
        ez = (ax * vy - ay * vx) + (mx * wy - my * wx);
        if (parKi > 0f)
        {
            eInt[0] += ex;      // accumulate integral error
            eInt[1] += ey;
            eInt[2] += ez;
        }
        else
        {
            eInt[0] = 0.0f;     // prevent integral wind up
            eInt[1] = 0.0f;
            eInt[2] = 0.0f;
        }

        // Apply feedback terms
        gx = gx + parKp * ex + parKi * eInt[0];
        gy = gy + parKp * ey + parKi * eInt[1];
        gz = gz + parKp * ez + parKi * eInt[2];

        // Integrate rate of change of quaternion
        pa = q2;
        pb = q3;
        pc = q4;
        q1 = (float)(q1 + (-q2 * gx - q3 * gy - q4 * gz) * (0.5f * samplePeriod));
        q2 = (float)(pa + (q1 * gx + pb * gz - pc * gy) * (0.5f * samplePeriod));
        q3 = (float)(pb + (q1 * gy - pa * gz + pc * gx) * (0.5f * samplePeriod));
        q4 = (float)(pc + (q1 * gz + pa * gy - pb * gx) * (0.5f * samplePeriod));

        // Normalise quaternion
        norm = (float)Math.sqrt(q1 * q1 + q2 * q2 + q3 * q3 + q4 * q4);
        norm = 1.0f / norm;
        quaternion[0] = q1 * norm;
        quaternion[1] = q2 * norm;
        quaternion[2] = q3 * norm;
        quaternion[3] = q4 * norm;

    }

    public void update(float gx, float gy, float gz, float ax, float ay,
                       float az) {
        float q1 = quaternion[0], q2 = quaternion[1], q3 = quaternion[2], q4 = quaternion[3];   // short name local variable for readability
        float norm;
        float vx, vy, vz;
        float ex, ey, ez;
        float pa, pb, pc;

        // Normalise accelerometer measurement
        norm = (float)Math.sqrt(ax * ax + ay * ay + az * az);
        if (norm == 0f) return; // handle NaN
        norm = 1 / norm;        // use reciprocal for division
        ax *= norm;
        ay *= norm;
        az *= norm;

        // Estimated direction of gravity
        vx = 2.0f * (q2 * q4 - q1 * q3);
        vy = 2.0f * (q1 * q2 + q3 * q4);
        vz = q1 * q1 - q2 * q2 - q3 * q3 + q4 * q4;

        // Error is cross product between estimated direction and measured direction of gravity
        ex = (ay * vz - az * vy);
        ey = (az * vx - ax * vz);
        ez = (ax * vy - ay * vx);
        if (parKi > 0f)
        {
            eInt[0] += ex;      // accumulate integral error
            eInt[1] += ey;
            eInt[2] += ez;
        }
        else
        {
            eInt[0] = 0.0f;     // prevent integral wind up
            eInt[1] = 0.0f;
            eInt[2] = 0.0f;
        }

        // Apply feedback terms
        gx = gx + parKp * ex + parKi * eInt[0];
        gy = gy + parKp * ey + parKi * eInt[1];
        gz = gz + parKp * ez + parKi * eInt[2];

        // Integrate rate of change of quaternion
        pa = q2;
        pb = q3;
        pc = q4;
        q1 = (float)(q1 + (-q2 * gx - q3 * gy - q4 * gz) * (0.5f * samplePeriod));
        q2 = (float)(pa + (q1 * gx + pb * gz - pc * gy) * (0.5f * samplePeriod));
        q3 = (float)(pb + (q1 * gy - pa * gz + pc * gx) * (0.5f * samplePeriod));
        q4 = (float)(pc + (q1 * gz + pa * gy - pb * gx) * (0.5f * samplePeriod));

        // Normalise quaternion
        norm = (float)Math.sqrt(q1 * q1 + q2 * q2 + q3 * q3 + q4 * q4);
        norm = 1.0f / norm;
        quaternion[0] = q1 * norm;
        quaternion[1] = q2 * norm;
        quaternion[2] = q3 * norm;
        quaternion[3] = q4 * norm;
    }

    public double getSamplePeriod() {
        return samplePeriod;
    }


    public float getParKi() {
        return parKi;
    }

    public void setParKi(float parKi) {
        this.parKi = parKi;
    }

    public float getParKp() {
        return parKp;
    }

    public void setParKp(float parKp) {
        this.parKp = parKp;
    }

    public float[] getQuaternion() {
        return quaternion;
    }

}
