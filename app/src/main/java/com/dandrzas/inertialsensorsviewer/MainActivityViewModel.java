package com.dandrzas.inertialsensorsviewer;

import android.graphics.Color;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dandrzas.inertialsensorsviewer.MVVM.Model.SensorsData;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class MainActivityViewModel extends ViewModel implements Observer {

    private MutableLiveData<LineGraphSeries<DataPoint>> graphAccelerometerSeriesXLiveData;
    private MutableLiveData<LineGraphSeries<DataPoint>> graphAccelerometerSeriesYLiveData;
    private MutableLiveData<LineGraphSeries<DataPoint>> graphAccelerometerSeriesZLiveData;
    private LineGraphSeries<DataPoint> graphAccelerometerSeriesX;
    private LineGraphSeries<DataPoint> graphAccelerometerSeriesY;
    private LineGraphSeries<DataPoint> graphAccelerometerSeriesZ;
    private List<float[]> accelerometerEventList = new ArrayList<>();

    private MutableLiveData<LineGraphSeries<DataPoint>> graphGyroscopeSeriesXLiveData;
    private MutableLiveData<LineGraphSeries<DataPoint>> graphGyroscopeSeriesYLiveData;
    private MutableLiveData<LineGraphSeries<DataPoint>> graphGyroscopeSeriesZLiveData;
    private LineGraphSeries<DataPoint> graphGyroscopeSeriesX;
    private LineGraphSeries<DataPoint> graphGyroscopeSeriesY;
    private LineGraphSeries<DataPoint> graphGyroscopeSeriesZ;
    private List<float[]> gyroscopeEventList = new ArrayList<>();

    private MutableLiveData<LineGraphSeries<DataPoint>> graphMagnetometerSeriesXLiveData;
    private MutableLiveData<LineGraphSeries<DataPoint>> graphMagnetometerSeriesYLiveData;
    private MutableLiveData<LineGraphSeries<DataPoint>> graphMagnetometerSeriesZLiveData;
    private LineGraphSeries<DataPoint> graphMagnetometerSeriesX;
    private LineGraphSeries<DataPoint> graphMagnetometerSeriesY;
    private LineGraphSeries<DataPoint> graphMagnetometerSeriesZ;
    private List<float[]> magnetometerEventList = new ArrayList<>();

    private final int GRAPH_LENGTH = 2000;
    private SensorsData sensorsData;

    public MainActivityViewModel()
    {
        sensorsData = SensorsData.getInstance();
        sensorsData.addObserver(this);
        initAccelerometerSeries();
        initGyroscopeSeries();
        initMagnetometerSeries();
    }

    private void initAccelerometerSeries()
    {
        graphAccelerometerSeriesX = new LineGraphSeries<>();
        graphAccelerometerSeriesX.setThickness(2);
        graphAccelerometerSeriesX.setColor(Color.BLUE);
        graphAccelerometerSeriesX.setTitle("Oś X [m/s2]");
        graphAccelerometerSeriesXLiveData = new MutableLiveData<>();
        graphAccelerometerSeriesXLiveData.setValue(graphAccelerometerSeriesX);

        graphAccelerometerSeriesY = new LineGraphSeries<>();
        graphAccelerometerSeriesY.setThickness(2);
        graphAccelerometerSeriesY.setColor(Color.RED);
        graphAccelerometerSeriesY.setTitle("Oś Y [m/s2]");
        graphAccelerometerSeriesYLiveData = new MutableLiveData<>();
        graphAccelerometerSeriesYLiveData.setValue(graphAccelerometerSeriesY);

        graphAccelerometerSeriesZ = new LineGraphSeries<>();
        graphAccelerometerSeriesZ.setThickness(2);
        graphAccelerometerSeriesZ.setColor(Color.GREEN);
        graphAccelerometerSeriesZ.setTitle("Oś Z [m/s2]");
        graphAccelerometerSeriesZLiveData = new MutableLiveData<>();
        graphAccelerometerSeriesZLiveData.setValue(graphAccelerometerSeriesZ);
    }

    private void initGyroscopeSeries()
    {
        graphGyroscopeSeriesX = new LineGraphSeries<>();
        graphGyroscopeSeriesX.setThickness(2);
        graphGyroscopeSeriesX.setColor(Color.BLUE);
        graphGyroscopeSeriesX.setTitle("Oś X [rad/s]");
        graphGyroscopeSeriesXLiveData = new MutableLiveData<>();
        graphGyroscopeSeriesXLiveData.setValue(graphGyroscopeSeriesX);

        graphGyroscopeSeriesY = new LineGraphSeries<>();
        graphGyroscopeSeriesY.setThickness(2);
        graphGyroscopeSeriesY.setColor(Color.RED);
        graphGyroscopeSeriesY.setTitle("Oś Y [rad/s]");
        graphGyroscopeSeriesYLiveData = new MutableLiveData<>();
        graphGyroscopeSeriesYLiveData.setValue(graphGyroscopeSeriesY);

        graphGyroscopeSeriesZ = new LineGraphSeries<>();
        graphGyroscopeSeriesZ.setThickness(2);
        graphGyroscopeSeriesZ.setColor(Color.GREEN);
        graphGyroscopeSeriesZ.setTitle("Oś Z [rad/s]");
        graphGyroscopeSeriesZLiveData = new MutableLiveData<>();
        graphGyroscopeSeriesZLiveData.setValue(graphGyroscopeSeriesZ);
    }

    private void initMagnetometerSeries()
    {
        graphMagnetometerSeriesX = new LineGraphSeries<>();
        graphMagnetometerSeriesX.setThickness(2);
        graphMagnetometerSeriesX.setColor(Color.BLUE);
        graphMagnetometerSeriesX.setTitle("Oś X [rad/s]");
        graphMagnetometerSeriesXLiveData = new MutableLiveData<>();
        graphMagnetometerSeriesXLiveData.setValue(graphMagnetometerSeriesX);

        graphMagnetometerSeriesY = new LineGraphSeries<>();
        graphMagnetometerSeriesY.setThickness(2);
        graphMagnetometerSeriesY.setColor(Color.RED);
        graphMagnetometerSeriesY.setTitle("Oś Y [rad/s]");
        graphMagnetometerSeriesYLiveData = new MutableLiveData<>();
        graphMagnetometerSeriesYLiveData.setValue(graphMagnetometerSeriesY);

        graphMagnetometerSeriesZ = new LineGraphSeries<>();
        graphMagnetometerSeriesZ.setThickness(2);
        graphMagnetometerSeriesZ.setColor(Color.GREEN);
        graphMagnetometerSeriesZ.setTitle("Oś Z [rad/s]");
        graphMagnetometerSeriesZLiveData = new MutableLiveData<>();
        graphMagnetometerSeriesZLiveData.setValue(graphMagnetometerSeriesZ);
    }

    @Override
    public void update(Observable o, Object arg) {
        if(o instanceof SensorsData) {

            float[] valuesAccelerometer = ((SensorsData) o).getAccelerometerValue();
            float[] eventsAccelerometerToList = {valuesAccelerometer[0], valuesAccelerometer[1], valuesAccelerometer[2]};
            accelerometerEventList.add(eventsAccelerometerToList);
            boolean scrollToEnd1 = false;
            if  (graphAccelerometerSeriesY.getHighestValueX()>=GRAPH_LENGTH)scrollToEnd1 = true;
            if (accelerometerEventList.size()>=50)
            {
                for (int j = 0; j< accelerometerEventList.size()-1; j++)
                {
                    float[] eventFromList = accelerometerEventList.get(j);
                    graphAccelerometerSeriesX.appendData(new DataPoint(graphAccelerometerSeriesX.getHighestValueX()+1, eventFromList[1]), scrollToEnd1, GRAPH_LENGTH);
                    graphAccelerometerSeriesY.appendData(new DataPoint(graphAccelerometerSeriesY.getHighestValueX()+1, eventFromList[0]), scrollToEnd1, GRAPH_LENGTH);
                    graphAccelerometerSeriesZ.appendData(new DataPoint(graphAccelerometerSeriesZ.getHighestValueX()+1, eventFromList[2]), scrollToEnd1, GRAPH_LENGTH);
                    accelerometerEventList.remove(j);
                }
            }

            float[] valuesGyroscope = ((SensorsData) o).getGyroscopeValue();
            float[] eventsGyroscopeToList = {valuesGyroscope[0], valuesGyroscope[1], valuesGyroscope[2]};
            gyroscopeEventList.add(eventsGyroscopeToList);
            boolean scrollToEnd2 = false;
            if  (graphGyroscopeSeriesY.getHighestValueX()>=GRAPH_LENGTH)scrollToEnd2 = true;
            if (gyroscopeEventList.size()>=50)
            {
                for (int j = 0; j< gyroscopeEventList.size()-1; j++)
                {
                    float[] eventFromList = gyroscopeEventList.get(j);
                    graphGyroscopeSeriesX.appendData(new DataPoint(graphGyroscopeSeriesX.getHighestValueX()+1, eventFromList[0]), scrollToEnd2, GRAPH_LENGTH);
                    graphGyroscopeSeriesY.appendData(new DataPoint(graphGyroscopeSeriesY.getHighestValueX()+1, eventFromList[1]), scrollToEnd2, GRAPH_LENGTH);
                    graphGyroscopeSeriesZ.appendData(new DataPoint(graphGyroscopeSeriesZ.getHighestValueX()+1, eventFromList[2]), scrollToEnd2, GRAPH_LENGTH);
                    gyroscopeEventList.remove(j);
                }
            }

            float[] valuesMagnetometer = ((SensorsData) o).getMagnetometerValue();
            float[] eventsMagnetometerToList = {valuesMagnetometer[0], valuesMagnetometer[1], valuesMagnetometer[2]};
            magnetometerEventList.add(eventsMagnetometerToList);
            boolean scrollToEnd3 = false;
            if  (graphMagnetometerSeriesY.getHighestValueX()>=GRAPH_LENGTH)scrollToEnd3 = true;
            if (magnetometerEventList.size()>=50)
            {
                for (int j = 0; j< magnetometerEventList.size()-1; j++)
                {
                    float[] eventFromList = magnetometerEventList.get(j);
                    graphMagnetometerSeriesX.appendData(new DataPoint(graphMagnetometerSeriesX.getHighestValueX()+1, eventFromList[0]), scrollToEnd3, GRAPH_LENGTH);
                    graphMagnetometerSeriesY.appendData(new DataPoint(graphMagnetometerSeriesY.getHighestValueX()+1, eventFromList[1]), scrollToEnd3, GRAPH_LENGTH);
                    graphMagnetometerSeriesZ.appendData(new DataPoint(graphMagnetometerSeriesZ.getHighestValueX()+1, eventFromList[2]), scrollToEnd3, GRAPH_LENGTH);
                    magnetometerEventList.remove(j);
                }
            }
        }
    }

    public LiveData<LineGraphSeries<DataPoint>> getGraphAccelerometerSeriesX()
    {
        return graphAccelerometerSeriesXLiveData;
    }

    public LiveData<LineGraphSeries<DataPoint>> getGraphAccelerometerSeriesY()
    {
        return graphAccelerometerSeriesYLiveData;
    }

    public LiveData<LineGraphSeries<DataPoint>> getGraphAccelerometerSeriesZ()
    {
        return graphAccelerometerSeriesZLiveData;
    }

    public LiveData<LineGraphSeries<DataPoint>> getGraphGyroscopeSeriesXLiveData() {
        return graphGyroscopeSeriesXLiveData;
    }

    public LiveData<LineGraphSeries<DataPoint>> getGraphGyroscopeSeriesYLiveData() {
        return graphGyroscopeSeriesYLiveData;
    }

    public LiveData<LineGraphSeries<DataPoint>> getGraphGyroscopeSeriesZLiveData() {
        return graphGyroscopeSeriesZLiveData;
    }

    public LiveData<LineGraphSeries<DataPoint>> getGraphMagnetometerSeriesXLiveData() {
        return graphMagnetometerSeriesXLiveData;
    }

    public LiveData<LineGraphSeries<DataPoint>> getGraphMagnetometerSeriesYLiveData() {
        return graphMagnetometerSeriesYLiveData;
    }

    public LiveData<LineGraphSeries<DataPoint>> getGraphMagnetometerSeriesZLiveData() {
        return graphMagnetometerSeriesZLiveData;
    }
}