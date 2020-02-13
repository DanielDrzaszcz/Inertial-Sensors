package com.dandrzas.inertialsensorsviewer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Button;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class MainActivity extends AppCompatActivity {

    private MainActivityViewModel activityViewModel;
    GraphView graph;
    private int bottomMenuSelectedItem = 1;
    private FloatingActionButton buttonStart;
    private float previousTouchY;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        activityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        graph = findViewById(R.id.graph_view);
        Button button_zoom_in = findViewById(R.id.button_zoom_in);
        Button button_zoom_out = findViewById(R.id.button_zoom_out);
        buttonStart = findViewById(R.id.floatingActionButton_start);

        if (SensorsDataReadService.isEnable()) {
            buttonStart.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_pause_white_24dp));
        } else {
            buttonStart.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_play_white_24dp));
        }

        activityViewModel.getGraphSeriesX().observe(this, new GraphSeriesObserver());
        activityViewModel.getGraphSeriesY().observe(this, new GraphSeriesObserver());
        activityViewModel.getGraphSeriesZ().observe(this, new GraphSeriesObserver());

        graphInit();

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }

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

        buttonStart.setOnClickListener(view ->
        {
            boolean isEnable = SensorsDataReadService.isEnable();

            if (!isEnable) {
                SensorsDataReadService.start(getApplicationContext());
                buttonStart.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_pause_white_24dp));
            } else {
                SensorsDataReadService.stop(getApplicationContext());
                buttonStart.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_play_white_24dp));
            }

        });

        // Zoom in
        button_zoom_in.setOnClickListener(view -> {
            double actualMinY = activityViewModel.getGraphMinY(bottomMenuSelectedItem);
            double actualMaxY = activityViewModel.getGraphMaxY(bottomMenuSelectedItem);
            double actualYRange = actualMaxY - actualMinY;

            if (actualYRange > 4) {
                activityViewModel.setGraphMinY(bottomMenuSelectedItem, (float)(actualMinY + actualYRange / 10));
                activityViewModel.setGraphMaxY(bottomMenuSelectedItem,  (float)(actualMaxY - actualYRange / 10));
            }
            graph.getViewport().setMinY(activityViewModel.getGraphMinY(bottomMenuSelectedItem));
            graph.getViewport().setMaxY(activityViewModel.getGraphMaxY(bottomMenuSelectedItem));
            graph.onDataChanged(true, false);
        });

        button_zoom_out.setOnClickListener(view -> {
            double actualMinY = activityViewModel.getGraphMinY(bottomMenuSelectedItem);
            double actualMaxY = activityViewModel.getGraphMaxY(bottomMenuSelectedItem);
            double actualYRange = actualMaxY - actualMinY;

            activityViewModel.setGraphMinY(bottomMenuSelectedItem, (float)(actualMinY - actualYRange / 10));
            activityViewModel.setGraphMaxY(bottomMenuSelectedItem,  (float)(actualMaxY + actualYRange / 10));

            graph.getViewport().setMinY(activityViewModel.getGraphMinY(bottomMenuSelectedItem));
            graph.getViewport().setMaxY(activityViewModel.getGraphMaxY(bottomMenuSelectedItem));
            graph.onDataChanged(true, false);
        });

        graph.setOnTouchListener((v, event) ->
                {
                    float y = event.getY();

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_MOVE:

                            float dy = y - previousTouchY;
                            double actualMinY = activityViewModel.getGraphMinY(bottomMenuSelectedItem);
                            double actualMaxY = activityViewModel.getGraphMaxY(bottomMenuSelectedItem);
                            double actualYRange;
                            if ((actualMinY < 0) && actualMaxY > 0)
                                actualYRange = Math.abs(actualMaxY) + Math.abs(actualMinY);
                            else if ((actualMaxY >= 0) && (actualMinY >= 0))
                                actualYRange = actualMaxY - actualMinY;
                            else actualYRange = Math.abs(actualMinY) - Math.abs(actualMaxY);
                            Log.d("dandxtestmovexRange", Double.toString(actualYRange));

                            if (dy < -2) {
                                Log.d("dandxtestmove", " góra");
                                activityViewModel.setGraphMinY(bottomMenuSelectedItem, (float)(actualMinY - 0.02 * actualYRange));
                                activityViewModel.setGraphMaxY(bottomMenuSelectedItem,  (float)(actualMaxY - 0.02 * actualYRange));
                            }

                            if (dy > 2) {
                                Log.d("dandxtestmove", " dół‚");
                                activityViewModel.setGraphMinY(bottomMenuSelectedItem, (float)(actualMinY + 0.02 * actualYRange));
                                activityViewModel.setGraphMaxY(bottomMenuSelectedItem,  (float)(actualMaxY + 0.02 * actualYRange));
                            }

                            graph.getViewport().setMinY(activityViewModel.getGraphMinY(bottomMenuSelectedItem));
                            graph.getViewport().setMaxY(activityViewModel.getGraphMaxY(bottomMenuSelectedItem));
                            graph.onDataChanged(true, false);

                    }

                    previousTouchY = y;
                    return false;
                }
        );

    }

    @Override
    protected void onDestroy() {
        graph.getSeries().get(0).clearReference(graph);
        graph.getSeries().get(1).clearReference(graph);
        graph.getSeries().get(2).clearReference(graph);
        activityViewModel.getGraphSeriesX().removeObservers(this);
        activityViewModel.getGraphSeriesY().removeObservers(this);
        activityViewModel.getGraphSeriesZ().removeObservers(this);
        super.onDestroy();
    }

    private void graphInit() {
        graph.getGridLabelRenderer().setHighlightZeroLines(false);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(activityViewModel.getGraphMinY(bottomMenuSelectedItem));
        graph.getViewport().setMaxY(activityViewModel.getGraphMaxY(bottomMenuSelectedItem));
        graph.getViewport().setScalableY(false);
        graph.getViewport().setScrollableY(false);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(activityViewModel.getGraphSeriesLength(bottomMenuSelectedItem));
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
        graph.onDataChanged(false, false);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[]{"", "", "", "", "", "", "", "", ""});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        graph.getGridLabelRenderer().setNumVerticalLabels(9);
        graph.getGridLabelRenderer().setNumHorizontalLabels(5);
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setBackgroundColor(Color.LTGRAY);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);
    }

    private class SensorDataSwitch implements Runnable {
        @Override
        public void run() {
            graph.getSeries().get(0).clearReference(graph);
            graph.getSeries().get(1).clearReference(graph);
            graph.getSeries().get(2).clearReference(graph);
            graph.removeAllSeries();
            graphInit();
            activityViewModel.setSelectedSensor(bottomMenuSelectedItem);

        }
    }

    private class GraphSeriesObserver implements Observer<LineGraphSeries<DataPoint>> {
        @Override
        public void onChanged(LineGraphSeries<DataPoint> dataPointLineGraphSeries) {

            graph.addSeries(dataPointLineGraphSeries);
            graph.refreshDrawableState();
            graph.invalidate();

        }
    }
}
