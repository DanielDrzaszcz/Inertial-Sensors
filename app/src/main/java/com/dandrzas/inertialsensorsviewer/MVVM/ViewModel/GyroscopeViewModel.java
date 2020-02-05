package com.dandrzas.inertialsensorsviewer.MVVM.ViewModel;

import android.app.Application;
import android.graphics.Color;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
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

public class GyroscopeViewModel extends ViewModel implements Observer {

    private MutableLiveData<LineGraphSeries<DataPoint>> graphSeriesXLiveData;
    private MutableLiveData<LineGraphSeries<DataPoint>> graphSeriesYLiveData;
    private MutableLiveData<LineGraphSeries<DataPoint>> graphSeriesZLiveData;
    private LineGraphSeries<DataPoint> graphSeriesX;
    private LineGraphSeries<DataPoint> graphSeriesY;
    private LineGraphSeries<DataPoint> graphSeriesZ;
    private final int GRAPH_LENGTH = 2000;
    private List<float[]> eventList = new ArrayList<>();
    private SensorsData sensorsData;

    public GyroscopeViewModel() {
        sensorsData = SensorsData.getInstance();
        sensorsData.addObserver(this);

        graphSeriesX = new LineGraphSeries<>();
        graphSeriesY = new LineGraphSeries<>();
        graphSeriesZ = new LineGraphSeries<>();

        graphSeriesX.setThickness(2);
        graphSeriesX.setColor(Color.BLUE);
        graphSeriesX.setTitle("Oś X [rad/s]");

        graphSeriesY.setThickness(2);
        graphSeriesY.setColor(Color.RED);
        graphSeriesY.setTitle("Oś Y [rad/s]");

        graphSeriesZ.setThickness(2);
        graphSeriesZ.setColor(Color.GREEN);
        graphSeriesZ.setTitle("Oś Z [rad/s]");

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

    @Override
    public void update(Observable o, Object arg) {
        if(o instanceof SensorsData) {

            float[] values = ((SensorsData) o).getGyroscopeValue();
            float[] eventToList = {values[0], values[1], values[2]};
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
    }
}