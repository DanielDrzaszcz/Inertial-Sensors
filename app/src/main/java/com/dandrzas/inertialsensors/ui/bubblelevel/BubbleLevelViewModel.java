package com.dandrzas.inertialsensors.ui.bubblelevel;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dandrzas.inertialsensors.data.IFOrientationAlgorithm;
import com.dandrzas.inertialsensors.data.DataManager;
import com.dandrzas.inertialsensors.data.MadgwickFilter;
import com.dandrzas.inertialsensors.data.MahonyFilter;

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
        dataManager.getAlgorithmMadgwickFilter().addObserver(this);
        dataManager.getAlgorithmMahonyFilter().addObserver(this);
        dataManager.getAlgorithmKalmanFilter().addObserver(this);
    }


    @Override
    public void update(Observable o, Object arg) {
        float rotationX = 0;
        float circlePercentRotationX;
        float rotationY = 0;
        float circlePercentRotationY;
        float rotationZ = 0;
        float percentLineHorizontal;
        float percentLineVertical;
        float rotationXRemapped = 0;
        float rotationYRemapped = 0;
        float rotationZRemapped = 0;
        float verticalPixelsMove;
        float horizontalPixelsMove;
        float circlePixelsMoveX;
        float circlePixelsMoveY;

        Log.d(TAG, " Selected Algorithm: " + dataManager.getSelectedAlgorithm());
        if ((int) arg == dataManager.getSelectedAlgorithm()) {

            rotationX = ((IFOrientationAlgorithm) o).getRollPitchYaw(false)[0];
            rotationY = ((IFOrientationAlgorithm) o).getRollPitchYaw(false)[1];
            rotationZ = ((IFOrientationAlgorithm) o).getRollPitchYaw(false)[2];
            rotationXRemapped = ((IFOrientationAlgorithm) o).getRollPitchYaw(true)[0];

            // Circle position
            circlePercentRotationX = ((IFOrientationAlgorithm) o).getRollPitchYaw(false)[0] / 45;
            circlePercentRotationY = ((IFOrientationAlgorithm) o).getRollPitchYaw(false)[1] / 45;
            if (circlePercentRotationX > 1) {
                circlePercentRotationX = 1;
            }
            if (circlePercentRotationX < -1) {
                circlePercentRotationX = -1;
            }
            if (circlePercentRotationY > 1) {
                circlePercentRotationY = 1;
            }
            if (circlePercentRotationY < -1) {
                circlePercentRotationY = -1;
            }

            double RMax = 45;
            double RAct = Math.sqrt(rotationX * rotationX + rotationY * rotationY);
            double k = RMax / RAct;
            Log.d("CircleTest RMax: ", Double.toString(RMax));
            Log.d("CircleTest RAct: ", Double.toString(RAct));
            Log.d("CircleTest k: ", Double.toString(k));
            if (RAct <= RMax) {
                circlePixelsMoveX = circleBubblePosZero[0] - (circlePercentRotationY * circleRange / 2);
                circlePixelsMoveY = circleBubblePosZero[1] - (circlePercentRotationX * circleRange / 2);
            } else {
                circlePixelsMoveX = (float) (circleBubblePosZero[0] - (k * circlePercentRotationY * circleRange / 2));
                circlePixelsMoveY = (float) (circleBubblePosZero[1] - (k * circlePercentRotationX * circleRange / 2));
            }
            Float[] circlePixelsMove = {circlePixelsMoveX, circlePixelsMoveY};
            circleBubblePos.setValue(circlePixelsMove);
            Log.d(TAG, " circlePixelsMoveX: " + circlePixelsMoveX);
            Log.d(TAG, " circlePixelsMoveY: " + circlePixelsMoveY);

            // Line vertical position
            if((o instanceof MadgwickFilter || o instanceof MahonyFilter)) {
                percentLineVertical = rotationXRemapped / 90;
            }
            else{
                percentLineVertical = rotationX / 90;
            }
            if (percentLineVertical > 1) {
                percentLineVertical = 1;
            }
            if (percentLineVertical < -1) {
                percentLineVertical = -1;
            }
            verticalPixelsMove = percentLineVertical * lineVerticalRange / 2;
            lineVerticalBubblePos.setValue(lineVerticalBubblePosZero - verticalPixelsMove);

            // Line horizontal position
            percentLineHorizontal = rotationY / 90;
            if (percentLineHorizontal > 1) {
                percentLineHorizontal = 1;
            }
            if (percentLineHorizontal < -1) {
                percentLineHorizontal = -1;
            }
            horizontalPixelsMove = percentLineHorizontal * lineHorizontalRange / 2;
            lineHorizontalBubblePos.setValue(lineHorizontalBubblePosZero - horizontalPixelsMove);

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