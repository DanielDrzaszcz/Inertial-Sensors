package com.dandrzas.inertialsensorsviewer;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class MainActivity extends AppCompatActivity {

    private MainActivityViewModel activityViewModel;
    GraphView graph;
    private int graphMaxY = 40;
    private final int GRAPH_LENGTH = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        SensorsAccessService.start(getApplicationContext());

        activityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        graph = findViewById(R.id.graph_accelerometer);
        Button button_zoom_in = findViewById(R.id.button_zoom_in);
        Button button_zoom_out = findViewById(R.id.button_zoom_out);

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
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setBackgroundColor(Color.LTGRAY);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);

        button_zoom_in.setOnClickListener(view-> {
            graphMaxY = graphMaxY/2;
            if(graphMaxY<1) graphMaxY=1;
            viewport.setMinY((-1)*graphMaxY);
            viewport.setMaxY(graphMaxY);
            graph.refreshDrawableState();
            graph.invalidate();
        });

        button_zoom_out.setOnClickListener(view-> {
            graphMaxY = graphMaxY*2;
            if(graphMaxY>100) graphMaxY=100;
            viewport.setMinY((-1)*graphMaxY);
            viewport.setMaxY(graphMaxY);
            graph.refreshDrawableState();
            graph.invalidate();
        });

        activityViewModel.getGraphAccelerometerSeriesX().observe(this, new Observer<LineGraphSeries<DataPoint>>() {

            @Override
            public void onChanged(LineGraphSeries<DataPoint> dataPointLineGraphSeries) {
                graph.addSeries(dataPointLineGraphSeries);
            }
        });

        activityViewModel.getGraphAccelerometerSeriesY().observe(this, new Observer<LineGraphSeries<DataPoint>>() {

            @Override
            public void onChanged(LineGraphSeries<DataPoint> dataPointLineGraphSeries) {
                graph.addSeries(dataPointLineGraphSeries);
            }
        });

        activityViewModel.getGraphAccelerometerSeriesZ().observe(this, new Observer<LineGraphSeries<DataPoint>>() {
            @Override
            public void onChanged(LineGraphSeries<DataPoint> dataPointLineGraphSeries) {
                graph.addSeries(dataPointLineGraphSeries);
            }
        });

    }

}
