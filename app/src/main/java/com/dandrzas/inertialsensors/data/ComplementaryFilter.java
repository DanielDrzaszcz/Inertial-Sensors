package com.dandrzas.inertialsensors.data;


public class ComplementaryFilter extends OrientationAlgorithm {

    private float paramAlfa = 0.5f;
    private final String TAG = ComplementaryFilter.class.getSimpleName();

    public ComplementaryFilter(SensorData sensorAccelerometer, SensorData sensorGyroscope, SensorData sensorMagnetometer)
    {
        this.sensorAccelerometer = sensorAccelerometer;
        this.sensorGyroscope = sensorGyroscope;
        this.sensorMagnetometer = sensorMagnetometer;
    }

    @Override
    public void calc() {
        calcRollPitchYawAccelMagn();
        calcRollPitchYawGyroscope();

        float pitch = paramAlfa*pitchGyroscope + (1-paramAlfa)*pitchAccelerometer;
        float roll = paramAlfa*rollGyroscope + (1-paramAlfa)*rollAccelerometer;
        float yaw = (-1)*paramAlfa*yawGyroscope + (1-paramAlfa)*yawMagnetometer;

        rollPitchYaw[0] = (float) (Math.toDegrees(roll));
        rollPitchYaw[1] = (float) (Math.toDegrees(pitch));
        rollPitchYaw[2] = (float) Math.toDegrees(yaw);

        setChanged();
        notifyObservers(Constants.COMPLEMENTARY_FILTER_ID);
        super.calc();
    }

    public float getParamAlfa() {
        return paramAlfa;
    }

    public void setParamAlfa(float paramAlfa) {
        this.paramAlfa = paramAlfa;
    }

}
