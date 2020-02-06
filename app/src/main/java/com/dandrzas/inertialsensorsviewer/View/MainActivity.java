package com.dandrzas.inertialsensorsviewer.View;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;

import com.dandrzas.inertialsensorsviewer.SensorsDataReadService;
import com.dandrzas.inertialsensorsviewer.ViewModel.MainActivityViewModel;
import com.dandrzas.inertialsensorsviewer.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class MainActivity extends AppCompatActivity {

    private MainActivityViewModel activityViewModel;
    GraphView graph;
    private int graphMaxY = 40;
    private final int GRAPH_LENGTH = 2000;
    private int bottomMenuSelectedItem = 1;
    GraphSeriesObserver graphSeriesXObserver;
    GraphSeriesObserver graphSeriesYObserver;
    GraphSeriesObserver graphSeriesZObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        activityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        graph = findViewById(R.id.graph_accelerometer);
        Button button_zoom_in = findViewById(R.id.button_zoom_in);
        Button button_zoom_out = findViewById(R.id.button_zoom_out);
        graphSeriesXObserver = new GraphSeriesObserver();
        graphSeriesYObserver = new GraphSeriesObserver();
        graphSeriesZObserver = new GraphSeriesObserver();

        activityViewModel.getGraphSeriesX().observe(this, graphSeriesXObserver);
        activityViewModel.getGraphSeriesY().observe(this, graphSeriesYObserver);
        activityViewModel.getGraphSeriesZ().observe(this, graphSeriesZObserver);


        SensorsDataReadService.start(getApplicationContext());

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_accelerometer:
                        graph.removeAllSeries();
                        bottomMenuSelectedItem = 1;
                        activityViewModel.setSelectedSensor(1);
                        Log.d("TestBottomMenu ", "Accelerometer");
                        return true;

                    case R.id.navigation_gyroscope:
                        graph.removeAllSeries();
                        bottomMenuSelectedItem = 2;
                        activityViewModel.setSelectedSensor(2);
                        Log.d("TestBottomMenu ", "Gyroscope");
                        return true;

                    case R.id.navigation_magnetometer:
                        graph.removeAllSeries();
                        bottomMenuSelectedItem = 3;
                        activityViewModel.setSelectedSensor(3);
                        Log.d("TestBottomMenu ", "Magnetometer");
                        return true;
                }
                return false;
            }
        });


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

    }

    private class GraphSeriesObserver implements Observer<LineGraphSeries<DataPoint>>
    {
        @Override
        public void onChanged(LineGraphSeries<DataPoint> dataPointLineGraphSeries) {
            graph.addSeries(dataPointLineGraphSeries);
        }
    }
}
