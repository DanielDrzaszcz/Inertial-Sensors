package com.dandrzas.inertialsensors.ui.movement;

import android.graphics.Color;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dandrzas.inertialsensors.data.Constants;
import com.dandrzas.inertialsensors.data.DataManager;
import com.dandrzas.inertialsensors.data.InertialTrackingAlgorithm;
import com.github.mikephil.charting.data.BubbleDataSet;
import com.github.mikephil.charting.data.BubbleEntry;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class MovementTraceViewModel extends ViewModel implements Observer {
    private DataManager dataManager;
    private final String TAG = MovementTraceViewModel.class.getSimpleName();
    private MutableLiveData<Float[]> movementPos = new MutableLiveData<>();
    private BubbleDataSet bubbleDataSet;
    private ArrayList bubbleEntries;
    private float chartInitAxisRange = 10;
    private MutableLiveData<Boolean> chartDataVisible = new MutableLiveData<>();
   private MutableLiveData<Float> chartXMax = new MutableLiveData<>(chartInitAxisRange);
    private MutableLiveData<Float> chartYMax = new MutableLiveData<>(chartInitAxisRange);
    private MutableLiveData<Float> chartXMin = new MutableLiveData<>((-1)*chartInitAxisRange);
    private MutableLiveData<Float> chartYMin = new MutableLiveData<>((-1)*chartInitAxisRange);
    private MutableLiveData<Float[]> velocity = new MutableLiveData<>();
    private int updateCounter;
    private Float[] valuesMovement = new Float[3];
    private Float[] valuesVelocity = new Float[3];
    private MutableLiveData<Boolean> progressBarVisible = new MutableLiveData<>();

    public MovementTraceViewModel() {
        dataManager = DataManager.getInstance();
        dataManager.addObserver(this);
        dataManager.getAlgorithmComplementaryInstance().addObserver(this);
        dataManager.getSystemAlgrithmInstance().addObserver(this);
        dataManager.getAlgorithmWithoutFusionInstance().addObserver(this);
        dataManager.getInertialTrackingAlgorithmInstance().addObserver(this);
        progressBarVisible.setValue(false);
        chartDataVisible.setValue(false);
        initDataSeries();
    }

    @Override
    public void update(Observable o, Object arg) {

        if(dataManager.isFirstUpdAfterStart()){
            bubbleEntries.clear();
            bubbleDataSet.clear();
            updateCounter = 0;
        }
        if(updateCounter == 1){
            bubbleDataSet.calcMinMax();
            chartXMax.setValue(chartInitAxisRange);
            chartYMax.setValue(chartInitAxisRange);
            chartXMin.setValue((-1)*chartInitAxisRange);
            chartYMin.setValue((-1)*chartInitAxisRange);
        }
        Log.d(TAG, " Selected Algorithm: " + dataManager.getSelectedAlgorithm());

        if (o instanceof InertialTrackingAlgorithm) {
            valuesMovement[0] = ((InertialTrackingAlgorithm) o).getCalculatedMovement()[0];
            valuesMovement[1] = ((InertialTrackingAlgorithm) o).getCalculatedMovement()[1];
            valuesMovement[2] = ((InertialTrackingAlgorithm) o).getCalculatedMovement()[2];
            movementPos.setValue(valuesMovement);

            valuesVelocity[0] = ((InertialTrackingAlgorithm) o).getCalculatedVelocity()[0];
            valuesVelocity[1] = ((InertialTrackingAlgorithm) o).getCalculatedVelocity()[1];
            valuesVelocity[2] = ((InertialTrackingAlgorithm) o).getCalculatedVelocity()[2];
            velocity.setValue(valuesVelocity);

            bubbleDataSet.addEntry(new BubbleEntry(valuesMovement[0], valuesMovement[1],0.1f));

            if(bubbleDataSet.getXMax()>chartXMax.getValue()){
                chartXMax.setValue(bubbleDataSet.getXMax());
            }
            if(bubbleDataSet.getYMax()>chartYMax.getValue()){
                chartYMax.setValue(bubbleDataSet.getYMax());
            }
            if(bubbleDataSet.getXMin()<chartXMin.getValue()){
                chartXMin.setValue(bubbleDataSet.getXMin());
            }
            if(bubbleDataSet.getYMin()<chartYMin.getValue()){
                chartYMin.setValue(bubbleDataSet.getYMin());
            }
            updateCounter++;
        }

        if(o instanceof DataManager){
            if((int)(arg)== Constants.COMPUTING_START_ID){
                progressBarVisible.setValue(true);
                chartDataVisible.setValue(false);
            }
            else if((int)(arg)== Constants.COMPUTING_STOP_ID){
                progressBarVisible.setValue(false);
                chartDataVisible.setValue(true);
            }
        }
    }

    public MutableLiveData<Float[]> getMovementPos() {
        return movementPos;
    }

    public MutableLiveData<Float[]> getVelocity() {
        return velocity;
    }

    private void initDataSeries() {
        bubbleEntries = new ArrayList<>();
        bubbleDataSet = new BubbleDataSet(bubbleEntries, "przemieszczenie");

        bubbleDataSet.setColors(Color.BLUE);
        bubbleDataSet.setValueTextSize(0);
        bubbleDataSet.setNormalizeSizeEnabled(false);
    }

    public BubbleDataSet getBubbleDataSet() {
        return bubbleDataSet;
    }

    public MutableLiveData<Boolean> getChartDataVisible() {
        return chartDataVisible;
    }

    public MutableLiveData<Float> getChartXMax() {
        return chartXMax;
    }

    public MutableLiveData<Float> getChartYMax() {
        return chartYMax;
    }

    public MutableLiveData<Float> getChartXMin() {
        return chartXMin;
    }

    public MutableLiveData<Float> getChartYMin() {
        return chartYMin;
    }

    public MutableLiveData<Boolean> getProgressBarVisible() {
        return progressBarVisible;
    }

    void startView(){
        progressBarVisible.setValue(dataManager.isComputingRunning());
        chartDataVisible.setValue(!dataManager.isComputingRunning());
    }
}