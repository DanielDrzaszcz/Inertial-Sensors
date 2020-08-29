package com.dandrzas.inertialsensorsviewer.data;

public interface IFOrientationAlgorithm {

    public float[] getRollPitchYaw(boolean remapToVertical);

    public float[] getRotationMatrixNWURPY(boolean remapToVertical);

    public float[] getRotationMatrixRPYNWU(boolean remapToVertical);

}
