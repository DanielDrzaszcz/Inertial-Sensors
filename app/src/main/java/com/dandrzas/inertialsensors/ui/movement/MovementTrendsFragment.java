package com.dandrzas.inertialsensors.ui.movement;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.dandrzas.inertialsensors.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class MovementTrendsFragment extends Fragment {

    private MovementTrendsViewModel movementTrendsViewModel;
    private float previousTouchY;

    @BindView(R.id.graph_view)
    GraphView graph;

    @BindView(R.id.spinner)
    Spinner spinner;

    public static MovementTrendsFragment newInstance() {
        MovementTrendsFragment fragment = new MovementTrendsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        movementTrendsViewModel = ViewModelProviders.of(this).get(MovementTrendsViewModel.class);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_movement_trends, container, false);
        ButterKnife.bind(this, root);
        graph.addSeries(movementTrendsViewModel.getRawAccelerationSeries());
        graph.addSeries(movementTrendsViewModel.getOrientationSeries());
        graph.addSeries(movementTrendsViewModel.getGlobalAccelerationSeries());
        graph.addSeries(movementTrendsViewModel.getGravitySeries());
        graph.addSeries(movementTrendsViewModel.getLinearAccelerationSeries());
        graph.addSeries(movementTrendsViewModel.getVelocitySeries());
        graph.addSeries(movementTrendsViewModel.getMovementSeries());
        graphConfig();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.trends_spinner_axis, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                movementTrendsViewModel.setSelectedAxis((int)id);
                Log.d("Spinner: ", Long.toString(id));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return root;
    }

    // Zoom out button
    @OnClick(R.id.button_zoom_out)
    public void clickZoomOut(){
        double actualMinY = movementTrendsViewModel.getGraphMinY();
        double actualMaxY = movementTrendsViewModel.getGraphMaxY();
        double actualYRange = actualMaxY - actualMinY;

        movementTrendsViewModel.setGraphMinY((float) (actualMinY - actualYRange / 10));
        movementTrendsViewModel.setGraphMaxY((float) (actualMaxY + actualYRange / 10));

        graph.getViewport().setMinY(movementTrendsViewModel.getGraphMinY());
        graph.getViewport().setMaxY(movementTrendsViewModel.getGraphMaxY());
        graph.onDataChanged(true, true);
    }

    // Zoom in button
    @OnClick(R.id.button_zoom_in)
    public void clickZoomIn() {
        double actualMinY = movementTrendsViewModel.getGraphMinY();
        double actualMaxY = movementTrendsViewModel.getGraphMaxY();
        double actualYRange = actualMaxY - actualMinY;

        if (actualYRange > 0.5) {
            movementTrendsViewModel.setGraphMinY((float) (actualMinY + actualYRange / 10));
            movementTrendsViewModel.setGraphMaxY((float) (actualMaxY - actualYRange / 10));
        }
        graph.getViewport().setMinY(movementTrendsViewModel.getGraphMinY());
        graph.getViewport().setMaxY(movementTrendsViewModel.getGraphMaxY());
        graph.onDataChanged(true, true);
    }

    // Touch events -  Y axis range movement
    @OnTouch(R.id.graph_view)
    public boolean touchGraphView(MotionEvent event)
    {
        float y = event.getY();

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float dy = y - previousTouchY;
            double actualMinY = movementTrendsViewModel.getGraphMinY();
            double actualMaxY = movementTrendsViewModel.getGraphMaxY();
            double actualYRange;

            // Actual display range calculation
            if ((actualMinY < 0) && actualMaxY > 0)
                actualYRange = Math.abs(actualMaxY) + Math.abs(actualMinY);
            else if ((actualMaxY >= 0) && (actualMinY >= 0))
                actualYRange = actualMaxY - actualMinY;
            else actualYRange = Math.abs(actualMinY) - Math.abs(actualMaxY);

            // Up movement
            if (dy < -2) {
                movementTrendsViewModel.setGraphMinY((float) (actualMinY - 0.02 * actualYRange));
                movementTrendsViewModel.setGraphMaxY((float) (actualMaxY - 0.02 * actualYRange));
            }

            // Bottom movement
            if (dy > 2) {
                movementTrendsViewModel.setGraphMinY((float) (actualMinY + 0.02 * actualYRange));
                movementTrendsViewModel.setGraphMaxY((float) (actualMaxY + 0.02 * actualYRange));
            }

            graph.getViewport().setMinY(movementTrendsViewModel.getGraphMinY());
            graph.getViewport().setMaxY(movementTrendsViewModel.getGraphMaxY());
            graph.onDataChanged(true, false);
        }
        previousTouchY = y;
        return false;
    }

    // Axes and legend config
    private void graphConfig() {

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(movementTrendsViewModel.getGraphMinY());
        graph.getViewport().setMaxY(movementTrendsViewModel.getGraphMaxY());
        graph.getViewport().setScalableY(false);
        graph.getViewport().setScrollableY(false);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(movementTrendsViewModel.getGraphMinX());
        graph.getViewport().setMaxX(movementTrendsViewModel.getGraphMaxX());
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
}