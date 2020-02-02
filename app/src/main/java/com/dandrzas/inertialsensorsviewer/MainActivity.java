package com.dandrzas.inertialsensorsviewer;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    GraphView graph;
    LineGraphSeries<DataPoint> graphSeriesX = new LineGraphSeries<>();
    LineGraphSeries<DataPoint> graphSeriesY = new LineGraphSeries<>();
    LineGraphSeries<DataPoint> graphSeriesZ = new LineGraphSeries<>();

    private int graphMaxY = 40;
    private final int GRAPH_LENGTH = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        graph =  findViewById(R.id.graph_accelerometer);
        Button button_zoom_in = findViewById(R.id.button_zoom_in);
        Button button_zoom_out = findViewById(R.id.button_zoom_out);

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
        graphSeriesX.setTitle("Oś X");
        graphSeriesY.setTitle("Oś Y");
        graphSeriesZ.setTitle("Oś Z");
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
            if(graphMaxY>100) graphMaxY=100;
            viewport.setMinY((-1)*graphMaxY);
            viewport.setMaxY(graphMaxY);
        });

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this,mAccelerometer,SensorManager.SENSOR_DELAY_FASTEST);
        Log.d("accelerometer type", mAccelerometer.getName());

    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        boolean scrollToEnd = false;
        if  (graphSeriesY.getHighestValueX()>=GRAPH_LENGTH)scrollToEnd = true;
        graphSeriesX.appendData(new DataPoint(graphSeriesX.getHighestValueX()+1, event.values[1]), scrollToEnd, GRAPH_LENGTH);
        graphSeriesY.appendData(new DataPoint(graphSeriesY.getHighestValueX()+1, event.values[0]), scrollToEnd, GRAPH_LENGTH);
        graphSeriesZ.appendData(new DataPoint(graphSeriesZ.getHighestValueX()+1, event.values[2]), scrollToEnd, GRAPH_LENGTH);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
