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
    Viewport viewport;
    private int graphMaxY = 40;
    private final int GRAPH_LENGTH = 2000;
    GraphSeriesObserver graphSeriesXObserver;
    GraphSeriesObserver graphSeriesYObserver;
    GraphSeriesObserver graphSeriesZObserver;
    private int bottomMenuSelectedItem = 1;
    SensorDataSwitch sensorDataSwitchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        activityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        graph = findViewById(R.id.graph_view);
        graphInit();
        Button button_zoom_in = findViewById(R.id.button_zoom_in);
        Button button_zoom_out = findViewById(R.id.button_zoom_out);
        graphSeriesXObserver = new GraphSeriesObserver();
        graphSeriesYObserver = new GraphSeriesObserver();
        graphSeriesZObserver = new GraphSeriesObserver();

        activityViewModel.getGraphSeriesX().observe(this, graphSeriesXObserver);
        activityViewModel.getGraphSeriesY().observe(this, graphSeriesYObserver);
        activityViewModel.getGraphSeriesZ().observe(this, graphSeriesZObserver);

        SensorsDataReadService.start(getApplicationContext());

        sensorDataSwitchRunnable = new SensorDataSwitch();

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.navigation_accelerometer:
                        bottomMenuSelectedItem = 1;
                        sensorDataSwitchRunnable.run();
                        return true;

                    case R.id.navigation_gyroscope:
                        bottomMenuSelectedItem = 2;
                       sensorDataSwitchRunnable.run();
                        return true;

                    case R.id.navigation_magnetometer:
                        bottomMenuSelectedItem = 3;
                        sensorDataSwitchRunnable.run();
                        return true;
                }
                return false;
            }
        });

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

    private void graphInit()
    {
        viewport = graph.getViewport();
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
    }

    private class SensorDataSwitch implements Runnable
    {
        @Override
        public void run() {
            graph.getSeries().get(0).clearReference(graph);
            graph.getSeries().get(1).clearReference(graph);
            graph.getSeries().get(2).clearReference(graph);
            graph.removeAllSeries();
            activityViewModel.setSelectedSensor(bottomMenuSelectedItem);
        }
    }

    private class GraphSeriesObserver  implements Observer<LineGraphSeries<DataPoint>>
    {
        @Override
        public void onChanged(LineGraphSeries<DataPoint> dataPointLineGraphSeries) {
            graph.addSeries(dataPointLineGraphSeries);
        }
    }
}
