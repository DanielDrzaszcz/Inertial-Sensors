package com.dandrzas.inertialsensorsviewer.data;


public class OrientationWithoutFusion extends OrientationAlgorithm {
    public OrientationWithoutFusion(SensorData sensorAccelerometer, SensorData sensorMagnetometer) {
        this.sensorAccelerometer = sensorAccelerometer;
        this.sensorMagnetometer = sensorMagnetometer;
    }

    @Override
    protected void calc() {
        calcRollPitchYawAccelMagn();
        rollPitchYaw[0] = (float) ((Math.toDegrees(rollAccelerometer)));
        rollPitchYaw[1] = (float) (Math.toDegrees(pitchAccelerometer));
        rollPitchYaw[2] = (float) Math.toDegrees(yawMagnetometer);

        setChanged();
        notifyObservers(Constants.ORIENTATION_WITHOUT_FUSION);

        super.calc();
    }
}
