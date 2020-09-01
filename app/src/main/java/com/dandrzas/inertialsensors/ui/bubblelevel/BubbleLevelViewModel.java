package com.dandrzas.inertialsensors.ui.bubblelevel;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dandrzas.inertialsensors.data.IFOrientationAlgorithm;
import com.dandrzas.inertialsensors.data.DataManager;

import java.util.Observable;
import java.util.Observer;

public class BubbleLevelViewModel extends ViewModel implements Observer {

    private DataManager dataManager;
    private final String TAG = BubbleLevelViewModel.class.getSimpleName();
    private float lineHorizontalRange;
    private MutableLiveData<Float> lineHorizontalBubblePos = new MutableLiveData<>();
    private float lineHorizontalBubblePosZero;
    private float lineVerticalRange;
    private MutableLiveData<Float> lineVerticalBubblePos = new MutableLiveData<>();
    private float lineVerticalBubblePosZero;
    private MutableLiveData<Float[]> circleBubblePos = new MutableLiveData<>();
    private float[] circleBubblePosZero = new float[2];
    private float circleRange;
    private boolean viewsInitDone;

    public BubbleLevelViewModel() {
        dataManager = DataManager.getInstance();
        dataManager.getAlgorithmComplementaryInstance().addObserver(this);
        dataManager.getSystemAlgrithmInstance().addObserver(this);
        dataManager.getAlgorithmWithoutFusionInstance().addObserver(this);
    }


    @Override
    public void update(Observable o, Object arg) {
        float rotationX = 0;
        float percentRotationX;
        float rotationY = 0;
        float percentRotationY;
        float rotationZ = 0;
        float percentRotationZHorizontal;
        float percentRotationZVertical;
        float rotationXRemapped = 0;
        float rotationYRemapped = 0;
        float rotationZRemapped = 0;
        float verticalPixelsMoveX;
        float horizontalPixelsMoveY;
        float circlePixelsMoveX;
        float circlePixelsMoveY;

        Log.d(TAG, " Selected Algorithm: " + dataManager.getSelectedAlgorithm());
        if ((int) arg == dataManager.getSelectedAlgorithm()) {

            rotationX = ((IFOrientationAlgorithm) o).getRollPitchYaw(false)[0];
            rotationY = ((IFOrientationAlgorithm) o).getRollPitchYaw(false)[1];
            rotationZ = ((IFOrientationAlgorithm) o).getRollPitchYaw(false)[2];
            rotationXRemapped = ((IFOrientationAlgorithm) o).getRollPitchYaw(true)[0];
            rotationYRemapped = ((IFOrientationAlgorithm) o).getRollPitchYaw(true)[1];
            rotationZRemapped = ((IFOrientationAlgorithm) o).getRollPitchYaw(true)[2];


            // Circle position
            if (rotationX > 45) {
                rotationX = 45;
            }
            if (rotationX < -45) {
                rotationX = -45;
            }
            if (rotationY > 45) {
                rotationY = 45;
            }
            if (rotationY < -45) {
                rotationY = -45;
            }
            percentRotationX = rotationX / 45;
            percentRotationY = rotationY / 45;
            double RMax = 45;
            double RAct = Math.sqrt(rotationX * rotationX + rotationY * rotationY);
            double k = RMax / RAct;
            Log.d("CircleTest RMax: ", Double.toString(RMax));
            Log.d("CircleTest RAct: ", Double.toString(RAct));
            Log.d("CircleTest k: ", Double.toString(k));
            if (RAct <= RMax) {
                circlePixelsMoveX = circleBubblePosZero[0] - (percentRotationY * circleRange / 2);
                circlePixelsMoveY = circleBubblePosZero[1] - (percentRotationX * circleRange / 2);
            } else {
                circlePixelsMoveX = (float) (circleBubblePosZero[0] - (k * percentRotationY * circleRange / 2));
                circlePixelsMoveY = (float) (circleBubblePosZero[1] - (k * percentRotationX * circleRange / 2));
            }
            Float[] circlePixelsMove = {circlePixelsMoveX, circlePixelsMoveY};
            circleBubblePos.setValue(circlePixelsMove);
            Log.d(TAG, " circlePixelsMoveX: " + circlePixelsMoveX);
            Log.d(TAG, " circlePixelsMoveY: " + circlePixelsMoveY);

            // Line vertical position
            percentRotationZVertical = (rotationZRemapped + 90) / 90;
            if (percentRotationZVertical > 1) {
                percentRotationZVertical = 1;
            }
            if (percentRotationZVertical < -1) {
                percentRotationZVertical = -1;
            }
            verticalPixelsMoveX = percentRotationZVertical * lineVerticalRange / 2;
            Log.d(TAG, " rotationX: " + rotationXRemapped);
            Log.d(TAG, " pixelsMoveX: " + verticalPixelsMoveX);
            Log.d(TAG, " lineVerticalRange: " + lineVerticalRange);
            Log.d(TAG, " rotationZ: " + rotationZRemapped);
            lineVerticalBubblePos.setValue(lineVerticalBubblePosZero - verticalPixelsMoveX);

            // Line horizontal position
            percentRotationZHorizontal = rotationZRemapped / 90;
            if (percentRotationZHorizontal > 1) {
                percentRotationZHorizontal = 1;
            }
            if (percentRotationZHorizontal < -1) {
                percentRotationZHorizontal = -1;
            }
            horizontalPixelsMoveY = percentRotationZHorizontal * lineHorizontalRange / 2;
            lineHorizontalBubblePos.setValue(lineHorizontalBubblePosZero - horizontalPixelsMoveY);

        }
    }

    public void setLineHorizontalRange(float lineHorizontalRange) {
        this.lineHorizontalRange = (float) 0.79 * lineHorizontalRange;
    }

    public void setLineHorizontalBubblePos(float lineHorizontalBubblePos) {
        this.lineHorizontalBubblePosZero = lineHorizontalBubblePos;
    }

    public void setLineVerticalRange(float lineVerticalRange) {
        this.lineVerticalRange = (float) 0.79 * lineVerticalRange;
    }

    public void setLineVerticalBubblePos(float lineVerticalBubblePos) {
        this.lineVerticalBubblePosZero = lineVerticalBubblePos;
    }

    public MutableLiveData<Float> getLineHorizontalBubblePos() {
        return lineHorizontalBubblePos;
    }

    public MutableLiveData<Float> getLineVerticalBubblePos() {
        return lineVerticalBubblePos;
    }

    public void setCircleRange(float circleRange) {
        this.circleRange = (float) 0.85 * circleRange;
    }

    public void setCircleBubblePosZero(float circleBubblePosZeroX, float circleBubblePosZeroY) {
        this.circleBubblePosZero[0] = circleBubblePosZeroX;
        this.circleBubblePosZero[1] = circleBubblePosZeroY;
    }

    public MutableLiveData<Float[]> getCircleBubblePos() {
        return circleBubblePos;
    }

    public boolean isViewsInitDone() {
        return viewsInitDone;
    }

    public void setViewsInitDone(boolean viewsInitDone) {
        this.viewsInitDone = viewsInitDone;
    }

}