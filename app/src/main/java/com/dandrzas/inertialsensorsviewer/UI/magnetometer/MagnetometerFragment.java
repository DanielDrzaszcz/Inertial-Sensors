package com.dandrzas.inertialsensorsviewer.UI.magnetometer;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.dandrzas.inertialsensorsviewer.R;
import com.dandrzas.inertialsensorsviewer.UI.accelerometer.AccelerometerViewModel;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class MagnetometerFragment extends Fragment implements SensorEventListener {

    private MagnetometerViewModel magnetometerViewModel;
    private SensorManager mSensorManager;
    private Sensor mMagnetometer;
    GraphView graph;
    LineGraphSeries<DataPoint> graphSeriesX = new LineGraphSeries<>();
    LineGraphSeries<DataPoint> graphSeriesY = new LineGraphSeries<>();
    LineGraphSeries<DataPoint> graphSeriesZ = new LineGraphSeries<>();
    private int graphMaxY = 100;
    private final int GRAPH_LENGTH = 1000;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        magnetometerViewModel =
                ViewModelProviders.of(this).get(MagnetometerViewModel.class);
        View root = inflater.inflate(R.layout.fragment_magnetometer, container, false);

        graph =  root.findViewById(R.id.graph_magnetometer);
        Button button_zoom_in = root.findViewById(R.id.button_zoom_in);
        Button button_zoom_out = root.findViewById(R.id.button_zoom_out);

        // graph series config
        graphSeriesX.setThickness(2);
        graphSeriesX.setColor(Color.BLUE);
        graph.addSeries(graphSeriesX);
        graphSeriesY.setThickness(2);
        graph.addSeries(graphSeriesY);
        graphSeriesY.setColor(Color.RED);
        graphSeriesZ.setThickness(2);
        graphSeriesZ.setColor(Color.GREEN);
        graph.addSeries(graphSeriesZ);

        //graph config
        Viewport viewport = graph.getViewport();
        viewport.setMinY((-1)*graphMaxY);
        viewport.setMaxY(graphMaxY);
        viewport.setYAxisBoundsManual(true);
        viewport.setMinX(0);
        viewport.setMaxX(GRAPH_LENGTH);
        viewport.setXAxisBoundsManual(true);
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[] {"", "", "","","","","","",""});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        graph.getGridLabelRenderer().setNumVerticalLabels(9);
        // legend
        graphSeriesX.setTitle("Oś X [uT]");
        graphSeriesY.setTitle("Oś Y [uT]");
        graphSeriesZ.setTitle("Oś Z [uT]");
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setBackgroundColor(Color.LTGRAY);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);

        button_zoom_in.setOnClickListener(view-> {
            graphMaxY = graphMaxY/2;
            if(graphMaxY<1) graphMaxY=1;
            viewport.setMinY((-1)*graphMaxY);
            viewport.setMaxY(graphMaxY);
        });

        button_zoom_out.setOnClickListener(view-> {
            graphMaxY = graphMaxY*2;
            if(graphMaxY>200) graphMaxY=200;
            viewport.setMinY((-1)*graphMaxY);
            viewport.setMaxY(graphMaxY);
        });

        mSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_FASTEST);
        Log.d("magnetometer type", mMagnetometer.getName());

        return root;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        boolean scrollToEnd = false;
        if  (graphSeriesY.getHighestValueX()>=GRAPH_LENGTH)scrollToEnd = true;
        graphSeriesX.appendData(new DataPoint(graphSeriesX.getHighestValueX()+1, event.values[0]), scrollToEnd, GRAPH_LENGTH);
        graphSeriesY.appendData(new DataPoint(graphSeriesY.getHighestValueX()+1, event.values[1]), scrollToEnd, GRAPH_LENGTH);
        graphSeriesZ.appendData(new DataPoint(graphSeriesZ.getHighestValueX()+1, event.values[2]), scrollToEnd, GRAPH_LENGTH);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}