package com.dandrzas.inertialsensorsviewer.UI.accelerometer;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;

public class AccelerometerViewModel extends AndroidViewModel implements SensorEventListener {

    private MutableLiveData<LineGraphSeries<DataPoint>> graphSeriesXLiveData;
    private MutableLiveData<LineGraphSeries<DataPoint>> graphSeriesYLiveData;
    private MutableLiveData<LineGraphSeries<DataPoint>> graphSeriesZLiveData;
    private LineGraphSeries<DataPoint> graphSeriesX;
    private LineGraphSeries<DataPoint> graphSeriesY;
    private LineGraphSeries<DataPoint> graphSeriesZ;
    private final int GRAPH_LENGTH = 2000;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private List<float[]> eventList = new ArrayList<>();

    public AccelerometerViewModel(Application application) {
        super(application);
        mSensorManager = (SensorManager) getApplication().getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        Log.d("magnetometer type", mAccelerometer.getName());

        graphSeriesX = new LineGraphSeries<>();
        graphSeriesY = new LineGraphSeries<>();
        graphSeriesZ = new LineGraphSeries<>();

        graphSeriesX.setThickness(2);
        graphSeriesX.setColor(Color.BLUE);
        graphSeriesX.setTitle("Oś X [m/s2]");

        graphSeriesY.setThickness(2);
        graphSeriesY.setColor(Color.RED);
        graphSeriesY.setTitle("Oś Y [m/s2]");

        graphSeriesZ.setThickness(2);
        graphSeriesZ.setColor(Color.GREEN);
        graphSeriesZ.setTitle("Oś Z [m/s2]");

        graphSeriesXLiveData = new MutableLiveData<>();
        graphSeriesXLiveData.setValue(graphSeriesX);

        graphSeriesYLiveData = new MutableLiveData<>();
        graphSeriesYLiveData.setValue(graphSeriesY);

        graphSeriesZLiveData = new MutableLiveData<>();
        graphSeriesZLiveData.setValue(graphSeriesZ);

    }

    public LiveData<LineGraphSeries<DataPoint>> getGraphSeriesX()
    {
        return graphSeriesXLiveData;
    }

    public LiveData<LineGraphSeries<DataPoint>> getGraphSeriesY()
    {
        return graphSeriesYLiveData;
    }

    public LiveData<LineGraphSeries<DataPoint>> getGraphSeriesZ()
    {
        return graphSeriesZLiveData;
    }

    public void addData(){

        for(int i=0; i<269; i++)
        {
            if (graphSeriesX.getHighestValueX()>=GRAPH_LENGTH)
            {
                graphSeriesX.appendData(new DataPoint(graphSeriesX.getHighestValueX()+1, 0.1*i), true, GRAPH_LENGTH);
                graphSeriesY.appendData(new DataPoint(graphSeriesX.getHighestValueX()+1, (-0.1)*i), true, GRAPH_LENGTH);
                graphSeriesZ.appendData(new DataPoint(graphSeriesX.getHighestValueX()+1, (-0.2)*i+1), true, GRAPH_LENGTH);

            }
            else
            {
                graphSeriesX.appendData(new DataPoint(graphSeriesX.getHighestValueX()+1, 0.1*i), false, GRAPH_LENGTH);
                graphSeriesY.appendData(new DataPoint(graphSeriesX.getHighestValueX()+1, (-0.1)*i), false, GRAPH_LENGTH);
                graphSeriesZ.appendData(new DataPoint(graphSeriesX.getHighestValueX()+1, (-0.2)*i+1), false, GRAPH_LENGTH);
            }
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] eventToList = {event.values[0], event.values[1], event.values[2]};
        eventList.add(eventToList);

        boolean scrollToEnd = false;
        if  (graphSeriesY.getHighestValueX()>=GRAPH_LENGTH)scrollToEnd = true;

        if (eventList.size()>=50)
        {
            for (int j=0; j<eventList.size()-1; j++)
            {
                float[] eventFromList = eventList.get(j);
                graphSeriesX.appendData(new DataPoint(graphSeriesX.getHighestValueX()+1, eventFromList[1]), scrollToEnd, GRAPH_LENGTH);
                graphSeriesY.appendData(new DataPoint(graphSeriesY.getHighestValueX()+1, eventFromList[0]), scrollToEnd, GRAPH_LENGTH);
                graphSeriesZ.appendData(new DataPoint(graphSeriesZ.getHighestValueX()+1, eventFromList[2]), scrollToEnd, GRAPH_LENGTH);
                eventList.remove(j);
            }
        }


}

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}