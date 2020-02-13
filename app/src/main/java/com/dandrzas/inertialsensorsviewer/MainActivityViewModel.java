package com.dandrzas.inertialsensorsviewer;

import android.graphics.Color;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class MainActivityViewModel extends ViewModel implements Observer {

    private LineGraphSeries<DataPoint> graphAccelerometerSeriesX;
    private LineGraphSeries<DataPoint> graphAccelerometerSeriesY;
    private LineGraphSeries<DataPoint> graphAccelerometerSeriesZ;
    private List<float[]> accelerometerEventList = new ArrayList<>();

    private LineGraphSeries<DataPoint> graphGyroscopeSeriesX;
    private LineGraphSeries<DataPoint> graphGyroscopeSeriesY;
    private LineGraphSeries<DataPoint> graphGyroscopeSeriesZ;
    private List<float[]> gyroscopeEventList = new ArrayList<>();

    private LineGraphSeries<DataPoint> graphMagnetometerSeriesX;
    private LineGraphSeries<DataPoint> graphMagnetometerSeriesY;
    private LineGraphSeries<DataPoint> graphMagnetometerSeriesZ;
    private List<float[]> magnetometerEventList = new ArrayList<>();

    private MutableLiveData<LineGraphSeries<DataPoint>> graphSeriesXLiveData;
    private MutableLiveData<LineGraphSeries<DataPoint>> graphSeriesYLiveData;
    private MutableLiveData<LineGraphSeries<DataPoint>> graphSeriesZLiveData;
    private LineGraphSeries<DataPoint> graphSeriesX;
    private LineGraphSeries<DataPoint> graphSeriesY;
    private LineGraphSeries<DataPoint> graphSeriesZ;

    private SensorsDataRepository sensorsData;
    private int graphSeriesLength = 3000;
    private float graphMaxYAccelerometer = 30;
    private float graphMaxYGyroscope = 10;
    private float graphMaxYMagnetometer = 50;
    private float graphMinYAccelerometer = -30;
    private float graphMinYGyroscope = -10;
    private float graphMinYMagnetometer = -50;
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
                float[] eventsAccelerometerToList = {valuesAccelerometer[0], valuesAccelerometer[1], valuesAccelerometer[2]};
                accelerometerEventList.add(eventsAccelerometerToList);
                boolean scrollToEnd1 = false;
                if (graphAccelerometerSeriesY.getHighestValueX() >= graphSeriesLength)
                    scrollToEnd1 = true;
                if (accelerometerEventList.size() >= 50) {
                    for (int j = 0; j < accelerometerEventList.size() - 1; j++) {
                        float[] eventFromList = accelerometerEventList.get(j);
                        graphAccelerometerSeriesX.appendData(new DataPoint(graphAccelerometerSeriesX.getHighestValueX() + 1, eventFromList[1]), scrollToEnd1, graphSeriesLength);
                        graphAccelerometerSeriesY.appendData(new DataPoint(graphAccelerometerSeriesY.getHighestValueX() + 1, eventFromList[0]), scrollToEnd1, graphSeriesLength);
                        graphAccelerometerSeriesZ.appendData(new DataPoint(graphAccelerometerSeriesZ.getHighestValueX() + 1, eventFromList[2]), scrollToEnd1, graphSeriesLength);
                        accelerometerEventList.remove(j);
                    }
                }
            }

            if (arg.equals(2)) {
                float[] valuesGyroscope = ((SensorsDataRepository) o).getGyroscopeValue();
                float[] eventsGyroscopeToList = {valuesGyroscope[0], valuesGyroscope[1], valuesGyroscope[2]};
                gyroscopeEventList.add(eventsGyroscopeToList);
                boolean scrollToEnd2 = false;
                if (graphGyroscopeSeriesY.getHighestValueX() >= graphSeriesLength)
                    scrollToEnd2 = true;
                if (gyroscopeEventList.size() >= 50) {
                    for (int j = 0; j < gyroscopeEventList.size() - 1; j++) {
                        float[] eventFromList = gyroscopeEventList.get(j);
                        graphGyroscopeSeriesX.appendData(new DataPoint(graphGyroscopeSeriesX.getHighestValueX() + 1, eventFromList[0]), scrollToEnd2, graphSeriesLength);
                        graphGyroscopeSeriesY.appendData(new DataPoint(graphGyroscopeSeriesY.getHighestValueX() + 1, eventFromList[1]), scrollToEnd2, graphSeriesLength);
                        graphGyroscopeSeriesZ.appendData(new DataPoint(graphGyroscopeSeriesZ.getHighestValueX() + 1, eventFromList[2]), scrollToEnd2, graphSeriesLength);
                        gyroscopeEventList.remove(j);
                    }
                }
            }

            if (arg.equals(3))
            {
                float[] valuesMagnetometer = ((SensorsDataRepository) o).getMagnetometerValue();
                float[] eventsMagnetometerToList = {valuesMagnetometer[0], valuesMagnetometer[1], valuesMagnetometer[2]};
                magnetometerEventList.add(eventsMagnetometerToList);
                boolean scrollToEnd3 = false;
                if (graphMagnetometerSeriesY.getHighestValueX() >= graphSeriesLength)
                    scrollToEnd3 = true;
                if (magnetometerEventList.size() >= 50) {
                    for (int j = 0; j < magnetometerEventList.size() - 1; j++) {
                        float[] eventFromList = magnetometerEventList.get(j);
                        graphMagnetometerSeriesX.appendData(new DataPoint(graphMagnetometerSeriesX.getHighestValueX() + 1, eventFromList[0]), scrollToEnd3, graphSeriesLength);
                        graphMagnetometerSeriesY.appendData(new DataPoint(graphMagnetometerSeriesY.getHighestValueX() + 1, eventFromList[1]), scrollToEnd3, graphSeriesLength);
                        graphMagnetometerSeriesZ.appendData(new DataPoint(graphMagnetometerSeriesZ.getHighestValueX() + 1, eventFromList[2]), scrollToEnd3, graphSeriesLength);
                        magnetometerEventList.remove(j);
                    }
                }
            }

            if (arg.equals(4))
            {
                graphMaxXAccelerometer = (int)(15000/sensorsData.getMinDelayAccelerometer());
                Log.d("maxDelayTestActivityVMLength", Integer.toString(graphMaxXAccelerometer));
            }

            if (arg.equals(5))
            {
                graphMaxXGyroscope = (int)(15000/sensorsData.getMinDelayGyroscope());
                Log.d("maxDelayTestActivityVMLength", Integer.toString(graphMaxXGyroscope));
            }

            if (arg.equals(6))
            {
                graphMaxXMagnetometer = (int)(15000/sensorsData.getMinDelayMagnetometer());
                Log.d("maxDelayTestActivityVMLength", Integer.toString(graphMaxXMagnetometer));

            }
        }
    }

    public void setSelectedSensor(int selectedSensor) {
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

        switch (selectedSensor) {
            case 1:
                graphSeriesLength = graphMaxXAccelerometer;
                break;
            case 2:
                graphSeriesLength = graphMaxXGyroscope;

                break;
            case 3:
                graphSeriesLength = graphMaxXMagnetometer;
                break;
        }
        return graphSeriesLength;
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