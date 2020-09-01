package com.dandrzas.inertialsensors.data;

import android.hardware.SensorManager;

import java.util.Observable;

public class SystemAlgorithm extends Observable implements IFOrientationAlgorithm {
    private float[] rollPitchYaw = new float[3];
    private float[] rotationMatrixNWURPY = new float[9];
    private float[] rotationMatrixRPYNWU = new float[9];

    public SystemAlgorithm() {
    }

    public void calcOrientation(float[] systemRotationSensorVal) {

        SensorManager.getRotationMatrixFromVector(rotationMatrixNWURPY, systemRotationSensorVal);
        float[] systemOrientation = new float[4];
        SensorManager.getOrientation(rotationMatrixNWURPY, systemOrientation);
        rotationMatrixRPYNWU[0]= rotationMatrixNWURPY[0];
        rotationMatrixRPYNWU[1]= rotationMatrixNWURPY[3];
        rotationMatrixRPYNWU[2]= rotationMatrixNWURPY[6];
        rotationMatrixRPYNWU[3]= rotationMatrixNWURPY[1];
        rotationMatrixRPYNWU[4]= rotationMatrixNWURPY[4];
        rotationMatrixRPYNWU[5]= rotationMatrixNWURPY[7];
        rotationMatrixRPYNWU[6]= rotationMatrixNWURPY[2];
        rotationMatrixRPYNWU[7]= rotationMatrixNWURPY[5];
        rotationMatrixRPYNWU[8]= rotationMatrixNWURPY[8];

        rollPitchYaw[0] = (float)((-1)*(systemOrientation[1] * 180) / Math.PI);
        rollPitchYaw[1] = (float)((systemOrientation[2] * 180) / Math.PI);
        rollPitchYaw[2] = (float)((systemOrientation[0] * 180) / Math.PI);

        setChanged();
        notifyObservers(Constants.SYSTEM_ALGORITHM_ID);
    }

    @Override
    public float[] getRollPitchYaw(boolean remapToVertical) {
        if (remapToVertical){
            float[] remappedRotationMatrix = new float[9];
            SensorManager.remapCoordinateSystem(rotationMatrixNWURPY, SensorManager.AXIS_X, SensorManager.AXIS_Z, remappedRotationMatrix);
            float[] remappedOrientationRad = new float[3];
            SensorManager.getOrientation(remappedRotationMatrix, remappedOrientationRad);
            float[] remappedOrientationDeg = new float[3];
            remappedOrientationDeg[0] = (float)((-1)*(Math.toDegrees(remappedOrientationRad[1])));
            remappedOrientationDeg[1] = (float)(Math.toDegrees(remappedOrientationRad[0]));
            remappedOrientationDeg[2] = (float)(Math.toDegrees(remappedOrientationRad[2]));
            return remappedOrientationDeg;
        }
        else{
            return rollPitchYaw;
        }    }

    @Override
    public float[] getRotationMatrixNWURPY(boolean remapToVertical) {
        return rotationMatrixNWURPY;
    }

    @Override
    public float[] getRotationMatrixRPYNWU(boolean remapToVertical) {
        return rotationMatrixRPYNWU;
    }
}