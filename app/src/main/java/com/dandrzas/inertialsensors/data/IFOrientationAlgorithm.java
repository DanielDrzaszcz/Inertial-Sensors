package com.dandrzas.inertialsensors.data;

public interface IFOrientationAlgorithm {

    public float[] getRollPitchYaw(boolean remapToVertical);

    public float[] getRotationMatrixNWURPY();

    public float[] getRotationMatrixRPYNWU();

    public long getSampleTime();

}
