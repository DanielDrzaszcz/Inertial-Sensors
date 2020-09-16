package com.dandrzas.inertialsensors.ui.pedometer;

import android.graphics.Color;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dandrzas.inertialsensors.data.DataManager;
import com.dandrzas.inertialsensors.data.StepDetectAlgorithm;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.util.Observable;
import java.util.Observer;

public class PedometerViewModel extends ViewModel implements Observer {

    private DataManager dataManager;
    private final String TAG = PedometerViewModel.class.getSimpleName();
    private final float graphInitialMaxY = 30;
    private final float graphInitialMinY = -5;
    private float graphMaxY = graphInitialMaxY;
    private float graphMinY = graphInitialMinY;
    private int graphMaxX = 1000;
    private LineGraphSeries<DataPoint> accelerationMagnitudeSeries = new LineGraphSeries<>();
    private LineGraphSeries<DataPoint> accelerationAverageSeries = new LineGraphSeries<>();
    private LineGraphSeries<DataPoint> accelerationVarianceSeries = new LineGraphSeries<>();
    private LineGraphSeries<DataPoint> accelerationTreshold1Series = new LineGraphSeries<>();
    private LineGraphSeries<DataPoint> accelerationTreshold2Series = new LineGraphSeries<>();
    private PointsGraphSeries<DataPoint> accelerationStepDetectSeries = new PointsGraphSeries<>();
    private MutableLiveData<Integer> stepsCounter = new MutableLiveData<>();

    public PedometerViewModel() {
        dataManager = DataManager.getInstance();
        dataManager.getStepDetectAlgorithm().addObserver(this);
        initDataSeries();
    }


    @Override
    public void update(Observable o, Object arg) {

        if (o instanceof StepDetectAlgorithm) {
            float accelerationMagnitude = ((StepDetectAlgorithm) o).getAccelerationMagnitude();
            float accelerationAverage = ((StepDetectAlgorithm) o).getAccelerationAverage();
            float accelerationVariance = ((StepDetectAlgorithm) o).getAccelerationVariance();
            float accelerationtreshold1Value = ((StepDetectAlgorithm) o).getThreshold1Value();
            float accelerationtreshold2Value = ((StepDetectAlgorithm) o).getThreshold2Value();
            stepsCounter.setValue(((StepDetectAlgorithm) o).getStepsCounter());

            boolean scrollToEnd1 = false;
            if (accelerationMagnitudeSeries.getHighestValueX() >= graphMaxX) {
                scrollToEnd1 = true;
            }
            accelerationMagnitudeSeries.appendData(new DataPoint(accelerationMagnitudeSeries.getHighestValueX() + 1, accelerationMagnitude), scrollToEnd1, graphMaxX);
            accelerationAverageSeries.appendData(new DataPoint(accelerationAverageSeries.getHighestValueX() + 1, accelerationAverage), scrollToEnd1, graphMaxX);
            accelerationVarianceSeries.appendData(new DataPoint(accelerationVarianceSeries.getHighestValueX() + 1, accelerationVariance), scrollToEnd1, graphMaxX);
            accelerationTreshold1Series.appendData(new DataPoint(accelerationTreshold1Series.getHighestValueX() + 1, accelerationtreshold1Value), scrollToEnd1, graphMaxX);
            accelerationTreshold2Series.appendData(new DataPoint(accelerationTreshold2Series.getHighestValueX() + 1, accelerationtreshold2Value), scrollToEnd1, graphMaxX);
            if(((StepDetectAlgorithm) o).isStepDetection()){
                accelerationStepDetectSeries.appendData(new DataPoint(accelerationTreshold1Series.getHighestValueX() + 1, 0), scrollToEnd1, graphMaxX);
            }
        }
    }

    public void startComputing()
    {
        dataManager.startComputing();
    }

    public void stopComputing()
    {
        dataManager.stopComputing();
    }

    public boolean isComputingRunning()
    {
        return dataManager.isComputingRunning();
    }

    private void initDataSeries() {
        accelerationMagnitudeSeries.setThickness(2);
        accelerationMagnitudeSeries.setColor(Color.BLACK);
        accelerationMagnitudeSeries.setTitle("Przyspieszenie");

        accelerationAverageSeries.setThickness(2);
        accelerationAverageSeries.setColor(Color.YELLOW);
        accelerationAverageSeries.setTitle("Uśrednione przysp");

        accelerationVarianceSeries.setThickness(2);
        accelerationVarianceSeries.setColor(Color.GREEN);
        accelerationVarianceSeries.setTitle("Wariancja przysp.");

        accelerationTreshold1Series.setThickness(2);
        accelerationTreshold1Series.setColor(Color.BLUE);
        accelerationTreshold1Series.setTitle("Próg 1");

        accelerationTreshold2Series.setThickness(2);
        accelerationTreshold2Series.setColor(Color.MAGENTA);
        accelerationTreshold2Series.setTitle("Próg 2");

        accelerationStepDetectSeries.setSize(10);
        accelerationStepDetectSeries.setColor(Color.RED);
        accelerationStepDetectSeries.setTitle("Krok");
    }

    public int getGraphMaxX() {

        int graphSeriesMaxX = graphMaxX;

        if (accelerationMagnitudeSeries.getHighestValueX() >= graphMaxX) {
            graphSeriesMaxX = (int) (accelerationMagnitudeSeries.getHighestValueX());
        } else {
            graphSeriesMaxX = graphMaxX;
        }

        return graphSeriesMaxX;
    }

    public int getGraphMinX() {

        if (accelerationMagnitudeSeries.getHighestValueX() >= graphMaxX) {
            return (int) (accelerationMagnitudeSeries.getLowestValueX());
        } else return 0;
    }

    public void setGraphMinY(float newMinYValue) {

        this.graphMinY = newMinYValue;
    }

    public float getGraphMinY() {

        return graphMinY;
    }

    public void setGraphMaxY(float newMaxYValue) {

        this.graphMaxY = newMaxYValue;
    }

    public float getGraphMaxY() {

        return graphMaxY;
    }

    public LineGraphSeries<DataPoint> getAccelerationMagnitudeSeries() {
        return accelerationMagnitudeSeries;
    }

    public LineGraphSeries<DataPoint> getAccelerationAverageSeries() {
        return accelerationAverageSeries;
    }

    public LineGraphSeries<DataPoint> getAccelerationVarianceSeries() {
        return accelerationVarianceSeries;
    }

    public LineGraphSeries<DataPoint> getAccelerationTreshold1Series() {
        return accelerationTreshold1Series;
    }

    public LineGraphSeries<DataPoint> getAccelerationTreshold2Series() {
        return accelerationTreshold2Series;
    }

    public PointsGraphSeries<DataPoint> getAccelerationStepDetectSeries() {
        return accelerationStepDetectSeries;
    }

    public MutableLiveData<Integer> getStepsCounter() {
        return stepsCounter;
    }
}