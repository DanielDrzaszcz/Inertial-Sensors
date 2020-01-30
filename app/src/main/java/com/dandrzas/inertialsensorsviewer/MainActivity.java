package com.dandrzas.inertialsensorsviewer;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

   // DataPoint[] dataSeries = new DataPoint[200];
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    GraphView graph;
    long i =0;
    long startTime = -1;
    LineGraphSeries<DataPoint> lineGraphSeries= new LineGraphSeries<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        graph = (GraphView) findViewById(R.id.graph);
        Button button = findViewById(R.id.button);
        lineGraphSeries.setThickness(2);graph.addSeries(lineGraphSeries);
        Viewport viewport = graph.getViewport();
        viewport.setMinY(-10);
        viewport.setMaxY(10);
        viewport.setYAxisBoundsManual(true);

        viewport.setMinX(0);
        viewport.setMaxX(5000);
        viewport.setXAxisBoundsManual(true);
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[] {"", "", "","","","","","",""});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

        button.setOnClickListener(view-> {
        ;
                });

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this,mAccelerometer,SensorManager.SENSOR_DELAY_FASTEST);
        Log.d("accelerometer type", mAccelerometer.getName());

    }


    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        if (startTime == -1)  startTime=event.timestamp;
        double timeStamp = (double)(event.timestamp - startTime)/1000000;
        Log.d("sensorstest time", Double.toString(timeStamp));
        Log.d("sensorstest value", (event.values[0]) + "\n");

        boolean scrollToEnd = false;
        if  (i>=5000)scrollToEnd = true;
        lineGraphSeries.appendData(new DataPoint(i, event.values[0]), scrollToEnd, 5000);
        i++;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
