package com.dandrzas.inertialsensorsviewer;

import android.graphics.Color;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import java.util.Observable;
import java.util.Observer;

public class MainActivityViewModel extends ViewModel implements Observer {

    private LineGraphSeries<DataPoint> graphAccelerometerSeriesX;
    private LineGraphSeries<DataPoint> graphAccelerometerSeriesY;
    private LineGraphSeries<DataPoint> graphAccelerometerSeriesZ;

    private LineGraphSeries<DataPoint> graphGyroscopeSeriesX;
    private LineGraphSeries<DataPoint> graphGyroscopeSeriesY;
    private LineGraphSeries<DataPoint> graphGyroscopeSeriesZ;

    private LineGraphSeries<DataPoint> graphMagnetometerSeriesX;
    private LineGraphSeries<DataPoint> graphMagnetometerSeriesY;
    private LineGraphSeries<DataPoint> graphMagnetometerSeriesZ;

    private MutableLiveData<LineGraphSeries<DataPoint>> graphSeriesXLiveData;
    private MutableLiveData<LineGraphSeries<DataPoint>> graphSeriesYLiveData;
    private MutableLiveData<LineGraphSeries<DataPoint>> graphSeriesZLiveData;
    private LineGraphSeries<DataPoint> graphSeriesX;
    private LineGraphSeries<DataPoint> graphSeriesY;
    private LineGraphSeries<DataPoint> graphSeriesZ;

    private SensorsDataRepository sensorsData;

    private final float graphInitialMaxYAccelerometer = 40;
    private final float graphInitialMaxYGyroscope = 10;
    private final float graphInitialMaxYMagnetometer = 60;
    private float graphMaxYAccelerometer = graphInitialMaxYAccelerometer;
    private float graphMaxYGyroscope = graphInitialMaxYGyroscope;
    private float graphMaxYMagnetometer = graphInitialMaxYMagnetometer;
    private float graphMinYAccelerometer = (-1)*graphInitialMaxYAccelerometer;
    private float graphMinYGyroscope = (-1)*graphInitialMaxYGyroscope;
    private float graphMinYMagnetometer = (-1)*graphInitialMaxYMagnetometer;

    private int graphMaxXAccelerometer=3000;
    private int graphMaxXGyroscope=3000;
    private int graphMaxXMagnetometer=3000;

    public MainActivityViewModel() {
        sensorsData = SensorsDataRepository.getInstance();
        sensorsData.addObserver(this);
        initAccelerometerSeries();
        initGyroscopeSeries();
        initMagnetometerSeries();

        graphSeriesX = new LineGraphSeries<>();
        graphSeriesY = new LineGraphSeries<>();
        graphSeriesZ = new LineGraphSeries<>();
        graphSeriesX = graphAccelerometerSeriesX;
        graphSeriesY = graphAccelerometerSeriesY;
        graphSeriesZ = graphAccelerometerSeriesZ;
        graphSeriesXLiveData = new MutableLiveData<>();
        graphSeriesXLiveData.setValue(graphSeriesX);
        graphSeriesYLiveData = new MutableLiveData<>();
        graphSeriesYLiveData.setValue(graphSeriesY);
        graphSeriesZLiveData = new MutableLiveData<>();
        graphSeriesZLiveData.setValue(graphSeriesZ);
    }

    private void initAccelerometerSeries() {
        graphAccelerometerSeriesX = new LineGraphSeries<>();
        graphAccelerometerSeriesX.setThickness(2);
        graphAccelerometerSeriesX.setColor(Color.BLUE);
        graphAccelerometerSeriesX.setTitle("Oś X [m/s2]");

        graphAccelerometerSeriesY = new LineGraphSeries<>();
        graphAccelerometerSeriesY.setThickness(2);
        graphAccelerometerSeriesY.setColor(Color.RED);
        graphAccelerometerSeriesY.setTitle("Oś Y [m/s2]");

        graphAccelerometerSeriesZ = new LineGraphSeries<>();
        graphAccelerometerSeriesZ.setThickness(2);
        graphAccelerometerSeriesZ.setColor(Color.GREEN);
        graphAccelerometerSeriesZ.setTitle("Oś Z [m/s2]");
    }

    private void initGyroscopeSeries() {
        graphGyroscopeSeriesX = new LineGraphSeries<>();
        graphGyroscopeSeriesX.setThickness(2);
        graphGyroscopeSeriesX.setColor(Color.BLUE);
        graphGyroscopeSeriesX.setTitle("Oś X [rad/s]");

        graphGyroscopeSeriesY = new LineGraphSeries<>();
        graphGyroscopeSeriesY.setThickness(2);
        graphGyroscopeSeriesY.setColor(Color.RED);
        graphGyroscopeSeriesY.setTitle("Oś Y [rad/s]");

        graphGyroscopeSeriesZ = new LineGraphSeries<>();
        graphGyroscopeSeriesZ.setThickness(2);
        graphGyroscopeSeriesZ.setColor(Color.GREEN);
        graphGyroscopeSeriesZ.setTitle("Oś Z [rad/s]");
    }

    private void initMagnetometerSeries() {
        graphMagnetometerSeriesX = new LineGraphSeries<>();
        graphMagnetometerSeriesX.setThickness(2);
        graphMagnetometerSeriesX.setColor(Color.BLUE);
        graphMagnetometerSeriesX.setTitle("Oś X [uT]");

        graphMagnetometerSeriesY = new LineGraphSeries<>();
        graphMagnetometerSeriesY.setThickness(2);
        graphMagnetometerSeriesY.setColor(Color.RED);
        graphMagnetometerSeriesY.setTitle("Oś Y [uT]");

        graphMagnetometerSeriesZ = new LineGraphSeries<>();
        graphMagnetometerSeriesZ.setThickness(2);
        graphMagnetometerSeriesZ.setColor(Color.GREEN);
        graphMagnetometerSeriesZ.setTitle("Oś Z [uT]");
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof SensorsDataRepository) {

            if (arg.equals(1)) {
                float[] valuesAccelerometer = ((SensorsDataRepository) o).getAccelerometerValue();
                boolean scrollToEnd1 = false;
                if (graphAccelerometerSeriesY.getHighestValueX() >= graphMaxXAccelerometer)
                {
                    scrollToEnd1 = true;
                }
                graphAccelerometerSeriesX.appendData(new DataPoint(graphAccelerometerSeriesX.getHighestValueX() + 1, valuesAccelerometer[1]), scrollToEnd1, graphMaxXAccelerometer);
                graphAccelerometerSeriesY.appendData(new DataPoint(graphAccelerometerSeriesY.getHighestValueX() + 1, valuesAccelerometer[0]), scrollToEnd1, graphMaxXAccelerometer);
                graphAccelerometerSeriesZ.appendData(new DataPoint(graphAccelerometerSeriesZ.getHighestValueX() + 1, valuesAccelerometer[2]), scrollToEnd1, graphMaxXAccelerometer);
            }

            if (arg.equals(2)) {
                float[] valuesGyroscope = ((SensorsDataRepository) o).getGyroscopeValue();
                boolean scrollToEnd2 = false;
                if (graphGyroscopeSeriesY.getHighestValueX() >= graphMaxXGyroscope)
                {
                    scrollToEnd2 = true;
                }
                graphGyroscopeSeriesX.appendData(new DataPoint(graphGyroscopeSeriesX.getHighestValueX() + 1, valuesGyroscope[0]), scrollToEnd2, graphMaxXGyroscope);
                graphGyroscopeSeriesY.appendData(new DataPoint(graphGyroscopeSeriesY.getHighestValueX() + 1, valuesGyroscope[1]), scrollToEnd2, graphMaxXGyroscope);
                graphGyroscopeSeriesZ.appendData(new DataPoint(graphGyroscopeSeriesZ.getHighestValueX() + 1, valuesGyroscope[2]), scrollToEnd2, graphMaxXGyroscope);
            }

            if (arg.equals(3))
            {
                float[] valuesMagnetometer = ((SensorsDataRepository) o).getMagnetometerValue();
                boolean scrollToEnd3 = false;
                if (graphMagnetometerSeriesY.getHighestValueX() >= graphMaxXMagnetometer)
                {
                    scrollToEnd3 = true;
                }
                graphMagnetometerSeriesX.appendData(new DataPoint(graphMagnetometerSeriesX.getHighestValueX() + 1, valuesMagnetometer[0]), scrollToEnd3, graphMaxXMagnetometer);
                graphMagnetometerSeriesY.appendData(new DataPoint(graphMagnetometerSeriesY.getHighestValueX() + 1, valuesMagnetometer[1]), scrollToEnd3, graphMaxXMagnetometer);
                graphMagnetometerSeriesZ.appendData(new DataPoint(graphMagnetometerSeriesZ.getHighestValueX() + 1, valuesMagnetometer[2]), scrollToEnd3, graphMaxXMagnetometer);
            }

            if (arg.equals(4))
            {
                graphMaxXAccelerometer = (int)(15000/sensorsData.getMinDelayAccelerometer());
            }

            if (arg.equals(5))
            {
                graphMaxXGyroscope = (int)(15000/sensorsData.getMinDelayGyroscope());
            }

            if (arg.equals(6))
            {
                graphMaxXMagnetometer = (int)(15000/sensorsData.getMinDelayMagnetometer());
            }
        }
    }

    public void setSelectedSensor(int selectedSensor) {

        graphMaxYAccelerometer = graphInitialMaxYAccelerometer;
        graphMinYAccelerometer = (-1)*graphInitialMaxYAccelerometer;
        graphMaxYGyroscope = graphInitialMaxYGyroscope;
        graphMinYGyroscope = (-1)*graphInitialMaxYGyroscope;
        graphMaxYMagnetometer = graphInitialMaxYMagnetometer;
        graphMinYMagnetometer = (-1)*graphInitialMaxYMagnetometer;

        switch (selectedSensor) {
            case 1:
                graphSeriesX = graphAccelerometerSeriesX;
                graphSeriesY = graphAccelerometerSeriesY;
                graphSeriesZ = graphAccelerometerSeriesZ;
                break;
            case 2:
                graphSeriesX = graphGyroscopeSeriesX;
                graphSeriesY = graphGyroscopeSeriesY;
                graphSeriesZ = graphGyroscopeSeriesZ;
                break;
            case 3:
                graphSeriesX = graphMagnetometerSeriesX;
                graphSeriesY = graphMagnetometerSeriesY;
                graphSeriesZ = graphMagnetometerSeriesZ;
                break;
        }
        graphSeriesXLiveData.setValue(graphSeriesX);
        graphSeriesYLiveData.setValue(graphSeriesY);
        graphSeriesZLiveData.setValue(graphSeriesZ);

    }

    public LiveData<LineGraphSeries<DataPoint>> getGraphSeriesX() {
        return graphSeriesXLiveData;
    }

    public LiveData<LineGraphSeries<DataPoint>> getGraphSeriesY() {
        return graphSeriesYLiveData;
    }

    public LiveData<LineGraphSeries<DataPoint>> getGraphSeriesZ() {
        return graphSeriesZLiveData;
    }

    public int getGraphSeriesLength(int selectedSensor) {
        int graphSeriesLength=graphMaxXAccelerometer;

        switch (selectedSensor) {
            case 1:
                if (graphAccelerometerSeriesY.getHighestValueX() >= graphMaxXAccelerometer)
                {
                    graphSeriesLength = (int)(graphAccelerometerSeriesX.getHighestValueX());
                }
                else graphSeriesLength = graphMaxXAccelerometer;
                break;

            case 2:
                if (graphGyroscopeSeriesX.getHighestValueX() >= graphMaxXGyroscope)
                {
                    graphSeriesLength = (int)(graphGyroscopeSeriesX.getHighestValueX());
                }
                else graphSeriesLength = graphMaxXGyroscope;
                break;

            case 3:
                if (graphMagnetometerSeriesX.getHighestValueX() >= graphMaxXMagnetometer)
                {
                    graphSeriesLength = (int)(graphMagnetometerSeriesX.getHighestValueX());
                }
                else graphSeriesLength = graphMaxXMagnetometer;
                break;
        }
        return graphSeriesLength;
    }

    public int getGraphMinX(int selectedSensor) {

        switch (selectedSensor) {
            case 1:
                if (graphAccelerometerSeriesY.getHighestValueX() >= graphMaxXAccelerometer)
                {
                    return (int)(graphAccelerometerSeriesX.getLowestValueX());
                }
                else return 0;

            case 2:
                if (graphGyroscopeSeriesY.getHighestValueX() >= graphMaxXGyroscope)
                {
                    return (int)(graphGyroscopeSeriesX.getLowestValueX());
                }
                else return 0;

                case 3:
                    if (graphMagnetometerSeriesY.getHighestValueX() >= graphMaxXMagnetometer)
                    {
                        return (int)(graphMagnetometerSeriesX.getLowestValueX());
                    }
                    else return 0;
        }
        return (int)(graphAccelerometerSeriesX.getLowestValueX());
    }

    public void setGraphMinY(int selectedSensor, float newMinYValue) {
        switch(selectedSensor)
        {
            case 1:
                this.graphMinYAccelerometer = newMinYValue;
                break;
            case 2:
                this.graphMinYGyroscope = newMinYValue;
                break;
            case 3:
                this.graphMinYMagnetometer = newMinYValue;
                break;
        }
    }

    public float getGraphMinY(int selectedSensor) {
        switch(selectedSensor)
        {
            case 1:
                return graphMinYAccelerometer;
            case 2:
                return graphMinYGyroscope;
            case 3:
                return graphMinYMagnetometer;
        }
        return graphMinYAccelerometer;
    }

    public void setGraphMaxY(int selectedSensor, float newMaxYValue) {
        switch(selectedSensor)
        {
            case 1:
                this.graphMaxYAccelerometer = newMaxYValue;
                break;
            case 2:
                this.graphMaxYGyroscope = newMaxYValue;
                break;
            case 3:
                this.graphMaxYMagnetometer = newMaxYValue;
                break;
        }
    }

    public float getGraphMaxY(int selectedSensor) {
        switch(selectedSensor)
        {
            case 1:
                return graphMaxYAccelerometer;
            case 2:
                return graphMaxYGyroscope;
            case 3:
                return graphMaxYMagnetometer;
        }
        return graphMaxYAccelerometer;
    }

}