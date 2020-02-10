package com.dandrzas.inertialsensorsviewer.View;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Switch;

import com.dandrzas.inertialsensorsviewer.SensorsDataReadService;
import com.dandrzas.inertialsensorsviewer.ViewModel.MainActivityViewModel;
import com.dandrzas.inertialsensorsviewer.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class MainActivity extends AppCompatActivity {

    private MainActivityViewModel activityViewModel;
    GraphView graph;
    private int bottomMenuSelectedItem = 1;
    private FloatingActionButton buttonStart;

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

        activityViewModel.getGraphSeriesX().observe(this, new GraphSeriesObserver());
        activityViewModel.getGraphSeriesY().observe(this, new GraphSeriesObserver());
        activityViewModel.getGraphSeriesZ().observe(this, new GraphSeriesObserver());

        graphInit();
        //SensorsDataReadService.start(getApplicationContext());

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
            //Log.d("testEnable", Boolean.toString(isEnable));

            if(!isEnable)
                {
                    SensorsDataReadService.start(getApplicationContext());
                    buttonStart.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_pause_white_24dp));
                    //buttonStart.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccentDark));
                }
                else {
                    SensorsDataReadService.stop(getApplicationContext());
                    buttonStart.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_play_white_24dp));
                    //buttonStart.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));



            }

        });

        button_zoom_in.setOnClickListener(view-> {
            graph.getViewport().setMinY(graph.getViewport().getMinY(false)/2);
            graph.getViewport().setMaxY(graph.getViewport().getMaxY(false)/2);
            graph.refreshDrawableState();
        });

        button_zoom_out.setOnClickListener(view-> {
            graph.getViewport().setMinY(graph.getViewport().getMinY(false)*2);
            graph.getViewport().setMaxY(graph.getViewport().getMaxY(false)*2);
            graph.refreshDrawableState();
        });

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

    private void graphInit()
    {
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-30);
        graph.getViewport().setMaxY(30);
        graph.getViewport().setScalableY(false);
        graph.getViewport().setScrollableY(false);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(activityViewModel.getGraphSeriesLength());
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[] {"", "", "","","","","","",""});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        graph.getGridLabelRenderer().setNumVerticalLabels(9);
        graph.getGridLabelRenderer().setNumHorizontalLabels(5);

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
