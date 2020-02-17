package com.dandrzas.inertialsensorsviewer.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Button;

import com.dandrzas.inertialsensorsviewer.R;
import com.dandrzas.inertialsensorsviewer.datasource.SensorsDataReadService;
import com.dandrzas.inertialsensorsviewer.viewmodel.MainActivityViewModel;
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
    private final int permissionMemoryWriteCode = 1;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bindowanie elementów UI
        BottomNavigationView navView = findViewById(R.id.nav_view);
        activityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        graph = findViewById(R.id.graph_view);
        Button button_zoom_in = findViewById(R.id.button_zoom_in);
        Button button_zoom_out = findViewById(R.id.button_zoom_out);
        buttonStart = findViewById(R.id.floatingActionButton_start);

        // Wyświetlenie stanu pracy Service na FOA po ponownym stworzeniu Activity
        if (SensorsDataReadService.isEnable()) {
            buttonStart.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_pause_white_24dp));
        } else {
            buttonStart.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_play_white_24dp));
        }

        // Podpięcie pod LiveData z ViewModel
        activityViewModel.getGraphSeriesX().observe(this, new GraphSeriesObserver());
        activityViewModel.getGraphSeriesY().observe(this, new GraphSeriesObserver());
        activityViewModel.getGraphSeriesZ().observe(this, new GraphSeriesObserver());

        graphConfig();

        // Obsługa bottom navigation menu
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

        // Obsługa kliknięcia w FOA - Service start
        buttonStart.setOnClickListener(view ->
        {
            boolean isEnable = SensorsDataReadService.isEnable();

            if (!isEnable) {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    SensorsDataReadService.start(getApplicationContext());
                    buttonStart.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_pause_white_24dp));
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, permissionMemoryWriteCode);
                    buttonStart.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_pause_white_24dp));
                }

            } else {
                SensorsDataReadService.stop(getApplicationContext());
                buttonStart.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_play_white_24dp));
            }
        });

        // Obsługa kliknięcia w przycisk Zoom in
        button_zoom_in.setOnClickListener(view -> {
            double actualMinY = activityViewModel.getGraphMinY(bottomMenuSelectedItem);
            double actualMaxY = activityViewModel.getGraphMaxY(bottomMenuSelectedItem);
            double actualYRange = actualMaxY - actualMinY;

            if (actualYRange > 4) {
                activityViewModel.setGraphMinY(bottomMenuSelectedItem, (float) (actualMinY + actualYRange / 10));
                activityViewModel.setGraphMaxY(bottomMenuSelectedItem, (float) (actualMaxY - actualYRange / 10));
            }
            graph.getViewport().setMinY(activityViewModel.getGraphMinY(bottomMenuSelectedItem));
            graph.getViewport().setMaxY(activityViewModel.getGraphMaxY(bottomMenuSelectedItem));
            graph.onDataChanged(true, true);
        });

        // Obsługa kliknięcia w przycisk Zoom out
        button_zoom_out.setOnClickListener(view -> {
            double actualMinY = activityViewModel.getGraphMinY(bottomMenuSelectedItem);
            double actualMaxY = activityViewModel.getGraphMaxY(bottomMenuSelectedItem);
            double actualYRange = actualMaxY - actualMinY;

            activityViewModel.setGraphMinY(bottomMenuSelectedItem, (float) (actualMinY - actualYRange / 10));
            activityViewModel.setGraphMaxY(bottomMenuSelectedItem, (float) (actualMaxY + actualYRange / 10));

            graph.getViewport().setMinY(activityViewModel.getGraphMinY(bottomMenuSelectedItem));
            graph.getViewport().setMaxY(activityViewModel.getGraphMaxY(bottomMenuSelectedItem));
            graph.onDataChanged(true, true);
        });

        // Touch eventy Graph View - przesunięcie wyświetlanego zakresu osi Y
        graph.setOnTouchListener((v, event) ->
                {
                    float y = event.getY();

                    if (event.getAction() == MotionEvent.ACTION_MOVE) {

                        float dy = y - previousTouchY;
                        double actualMinY = activityViewModel.getGraphMinY(bottomMenuSelectedItem);
                        double actualMaxY = activityViewModel.getGraphMaxY(bottomMenuSelectedItem);
                        double actualYRange;

                        // Wyliczenie aktualnie wyświetlanego zakresu
                        if ((actualMinY < 0) && actualMaxY > 0)
                            actualYRange = Math.abs(actualMaxY) + Math.abs(actualMinY);
                        else if ((actualMaxY >= 0) && (actualMinY >= 0))
                            actualYRange = actualMaxY - actualMinY;
                        else actualYRange = Math.abs(actualMinY) - Math.abs(actualMaxY);

                        // Przesunięcie w górę
                        if (dy < -2) {
                            activityViewModel.setGraphMinY(bottomMenuSelectedItem, (float) (actualMinY - 0.02 * actualYRange));
                            activityViewModel.setGraphMaxY(bottomMenuSelectedItem, (float) (actualMaxY - 0.02 * actualYRange));
                        }

                        // Przesunięcie w dół
                        if (dy > 2) {
                            activityViewModel.setGraphMinY(bottomMenuSelectedItem, (float) (actualMinY + 0.02 * actualYRange));
                            activityViewModel.setGraphMaxY(bottomMenuSelectedItem, (float) (actualMaxY + 0.02 * actualYRange));
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
        graph.getSeries().get(0).clearReference(graph); // usuń referencję aby zapobiec wyciekom pamięci
        graph.getSeries().get(1).clearReference(graph);
        graph.getSeries().get(2).clearReference(graph);
        activityViewModel.getGraphSeriesX().removeObservers(this);
        activityViewModel.getGraphSeriesY().removeObservers(this);
        activityViewModel.getGraphSeriesZ().removeObservers(this);
        super.onDestroy();
    }

    // Start serwisu po żądaniu od użytkownika uprawnienia do zapisu danych do pamięci zewnętrznej
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == permissionMemoryWriteCode)
            SensorsDataReadService.start(getApplicationContext());
    }

    // Konfiguracja osi i legendy wykresu
    private void graphConfig() {
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(activityViewModel.getGraphMinY(bottomMenuSelectedItem));
        graph.getViewport().setMaxY(activityViewModel.getGraphMaxY(bottomMenuSelectedItem));
        graph.getViewport().setScalableY(false);
        graph.getViewport().setScrollableY(false);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(activityViewModel.getGraphMinX(bottomMenuSelectedItem));
        graph.getViewport().setMaxX(activityViewModel.getGraphMaxX(bottomMenuSelectedItem));
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
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);
        graph.onDataChanged(true, true);
    }

    // Implementacja interfejsu Runnable do przełączenia wyświetlanych danych z czujników w osobnym wątku
    private class SensorDataSwitch implements Runnable {
        @Override
        public void run() {
            graph.getSeries().get(0).clearReference(graph); // usuń referencję aby zapobiec wyciekom pamięci
            graph.getSeries().get(1).clearReference(graph);
            graph.getSeries().get(2).clearReference(graph);
            graph.removeAllSeries();
            activityViewModel.setSelectedSensor(bottomMenuSelectedItem);
        }
    }

    // Implementacja obserwatora do odbierania danych z ViewModel
    private class GraphSeriesObserver implements Observer<LineGraphSeries<DataPoint>> {
        @Override
        public void onChanged(LineGraphSeries<DataPoint> dataPointLineGraphSeries) {
            graph.addSeries(dataPointLineGraphSeries);
            graphConfig();
        }
    }
}