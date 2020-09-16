package com.dandrzas.inertialsensors.ui.sensorsdata;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.dandrzas.inertialsensors.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class SensorsDataFragment extends Fragment {

    private SensorsDataViewModel sensorsDataViewModel;
    private GraphView graph;
    private int bottomMenuSelectedItem = 1;
    private float previousTouchY;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sensorsDataViewModel =
                ViewModelProviders.of(this).get(SensorsDataViewModel.class);
        View root = inflater.inflate(R.layout.fragment_sensorsdata, container, false);

        // UI binding
        BottomNavigationView navView = root.findViewById(R.id.bottom_nav_view);
        sensorsDataViewModel = ViewModelProviders.of(this).get(SensorsDataViewModel.class);
        graph = root.findViewById(R.id.graph_view);
        Button button_zoom_in = root.findViewById(R.id.button_zoom_in);
        Button button_zoom_out = root.findViewById(R.id.button_zoom_out);

        sensorsDataViewModel.getGraphSeriesX().observe(this, new GraphSeriesObserver());
        sensorsDataViewModel.getGraphSeriesY().observe(this, new GraphSeriesObserver());
        sensorsDataViewModel.getGraphSeriesZ().observe(this, new GraphSeriesObserver());

        graphConfig();

        // bottom navigation menu
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_accelerometer:
                        bottomMenuSelectedItem = 1;
                        new SensorDataSwitch().run();
                        return true;

                    case R.id.navigation_gyroscope:
                        bottomMenuSelectedItem = 2;
                        new SensorDataSwitch().run();
                        return true;

                    case R.id.navigation_magnetometer:
                        bottomMenuSelectedItem = 3;
                        new SensorDataSwitch().run();
                        return true;
                }
                return false;
            }
        });

        //  Zoom in
        button_zoom_in.setOnClickListener(view -> {
            double actualMinY = sensorsDataViewModel.getGraphMinY();
            double actualMaxY = sensorsDataViewModel.getGraphMaxY();
            double actualYRange = actualMaxY - actualMinY;

            if (actualYRange > 4) {
                sensorsDataViewModel.setGraphMinY((float) (actualMinY + actualYRange / 10));
                sensorsDataViewModel.setGraphMaxY((float) (actualMaxY - actualYRange / 10));
            }
            graph.getViewport().setMinY(sensorsDataViewModel.getGraphMinY());
            graph.getViewport().setMaxY(sensorsDataViewModel.getGraphMaxY());
            graph.onDataChanged(true, true);
        });

        // Zoom out
        button_zoom_out.setOnClickListener(view -> {
            double actualMinY = sensorsDataViewModel.getGraphMinY();
            double actualMaxY = sensorsDataViewModel.getGraphMaxY();
            double actualYRange = actualMaxY - actualMinY;

            sensorsDataViewModel.setGraphMinY((float) (actualMinY - actualYRange / 10));
            sensorsDataViewModel.setGraphMaxY((float) (actualMaxY + actualYRange / 10));

            graph.getViewport().setMinY(sensorsDataViewModel.getGraphMinY());
            graph.getViewport().setMaxY(sensorsDataViewModel.getGraphMaxY());
            graph.onDataChanged(true, true);
        });

        // Touch events -  Y axis range movement
        graph.setOnTouchListener((v, event) ->
                {
                    float y = event.getY();

                    if (event.getAction() == MotionEvent.ACTION_MOVE) {

                        float dy = y - previousTouchY;
                        double actualMinY = sensorsDataViewModel.getGraphMinY();
                        double actualMaxY = sensorsDataViewModel.getGraphMaxY();
                        double actualYRange;

                        // Wyliczenie aktualnie wyświetlanego zakresu
                        if ((actualMinY < 0) && actualMaxY > 0)
                            actualYRange = Math.abs(actualMaxY) + Math.abs(actualMinY);
                        else if ((actualMaxY >= 0) && (actualMinY >= 0))
                            actualYRange = actualMaxY - actualMinY;
                        else actualYRange = Math.abs(actualMinY) - Math.abs(actualMaxY);

                        // Up
                        if (dy < -2) {
                            sensorsDataViewModel.setGraphMinY((float) (actualMinY - 0.02 * actualYRange));
                            sensorsDataViewModel.setGraphMaxY((float) (actualMaxY - 0.02 * actualYRange));
                        }

                        // Down
                        if (dy > 2) {
                            sensorsDataViewModel.setGraphMinY((float) (actualMinY + 0.02 * actualYRange));
                            sensorsDataViewModel.setGraphMaxY((float) (actualMaxY + 0.02 * actualYRange));
                        }

                        graph.getViewport().setMinY(sensorsDataViewModel.getGraphMinY());
                        graph.getViewport().setMaxY(sensorsDataViewModel.getGraphMaxY());
                        graph.onDataChanged(true, false);
                    }
                    previousTouchY = y;
                    return false;
                }
        );

        return root;
    }

    @Override
    public void onDestroy() {
        graph.getSeries().get(0).clearReference(graph); // clean reference to avoid memory leaks
        graph.getSeries().get(1).clearReference(graph);
        graph.getSeries().get(2).clearReference(graph);
        sensorsDataViewModel.getGraphSeriesX().removeObservers(this);
        sensorsDataViewModel.getGraphSeriesY().removeObservers(this);
        sensorsDataViewModel.getGraphSeriesZ().removeObservers(this);
        super.onDestroy();
    }


    // Axes and legend config
    private void graphConfig() {
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(sensorsDataViewModel.getGraphMinY());
        graph.getViewport().setMaxY(sensorsDataViewModel.getGraphMaxY());
        graph.getViewport().setScalableY(false);
        graph.getViewport().setScrollableY(false);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(sensorsDataViewModel.getGraphMinX());
        graph.getViewport().setMaxX(sensorsDataViewModel.getGraphMaxX());
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[]{"", "", "", "", "", "", "", "", ""});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        graph.getGridLabelRenderer().setNumVerticalLabels(9);
        graph.getGridLabelRenderer().setNumHorizontalLabels(5);
        graph.getLegendRenderer().resetStyles();
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setBackgroundColor(Color.LTGRAY);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graph.onDataChanged(true, true);
    }

    // Runnable implementation to switching sensors data in the other thread
    private class SensorDataSwitch implements Runnable {
        @Override
        public void run() {
            graph.getSeries().get(0).clearReference(graph); // clean reference to avoid memory leaks
            graph.getSeries().get(1).clearReference(graph);
            graph.getSeries().get(2).clearReference(graph);
            graph.removeAllSeries();
            sensorsDataViewModel.setSelectedSensor(bottomMenuSelectedItem);
        }
    }

    private class GraphSeriesObserver implements Observer<LineGraphSeries<DataPoint>> {
        @Override
        public void onChanged(LineGraphSeries<DataPoint> dataPointLineGraphSeries) {
            graph.addSeries(dataPointLineGraphSeries);
            graphConfig();
        }
    }
}