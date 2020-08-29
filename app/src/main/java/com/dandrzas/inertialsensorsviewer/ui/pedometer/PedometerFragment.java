package com.dandrzas.inertialsensorsviewer.ui.pedometer;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import com.dandrzas.inertialsensorsviewer.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class PedometerFragment extends Fragment {

    private PedometerViewModel pedometerViewModel;
    private final String TAG = PedometerFragment.class.getSimpleName();
    private float previousTouchY;

    @BindView(R.id.graph_view)
    GraphView graph;
    @BindView(R.id.stepsCount)
    TextView textViewStepsCount;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        pedometerViewModel =
                ViewModelProviders.of(this).get(PedometerViewModel.class);
        View root = inflater.inflate(R.layout.fragment_pedometer, container, false);
        ButterKnife.bind(this, root);

        textViewStepsCount.setText(getResources().getString(R.string.steps_count)+ " 0");
        pedometerViewModel.getStepsCounter().observe(this, value->{
            textViewStepsCount.setText( getResources().getString(R.string.steps_count)+ " " + value);
        });

        graph.addSeries(pedometerViewModel.getAccelerationMagnitudeSeries());
        graph.addSeries(pedometerViewModel.getAccelerationAverageSeries());
        graph.addSeries(pedometerViewModel.getAccelerationVarianceSeries());
        graph.addSeries(pedometerViewModel.getAccelerationTreshold1Series());
        graph.addSeries(pedometerViewModel.getAccelerationTreshold2Series());
        graph.addSeries(pedometerViewModel.getAccelerationStepDetectSeries());

        graphConfig();
        return root;
    }


    // Zoom out button
    @OnClick(R.id.button_zoom_out)
    public void clickZoomOut(){
        double actualMinY = pedometerViewModel.getGraphMinY();
        double actualMaxY = pedometerViewModel.getGraphMaxY();
        double actualYRange = actualMaxY - actualMinY;

        pedometerViewModel.setGraphMinY((float) (actualMinY - actualYRange / 10));
        pedometerViewModel.setGraphMaxY((float) (actualMaxY + actualYRange / 10));

        graph.getViewport().setMinY(pedometerViewModel.getGraphMinY());
        graph.getViewport().setMaxY(pedometerViewModel.getGraphMaxY());
        graph.onDataChanged(true, true);
    }

    // Zoom in button
    @OnClick(R.id.button_zoom_in)
    public void clickZoomIn() {
        double actualMinY = pedometerViewModel.getGraphMinY();
        double actualMaxY = pedometerViewModel.getGraphMaxY();
        double actualYRange = actualMaxY - actualMinY;

        if (actualYRange > 4) {
            pedometerViewModel.setGraphMinY((float) (actualMinY + actualYRange / 10));
            pedometerViewModel.setGraphMaxY((float) (actualMaxY - actualYRange / 10));
        }
        graph.getViewport().setMinY(pedometerViewModel.getGraphMinY());
        graph.getViewport().setMaxY(pedometerViewModel.getGraphMaxY());
        graph.onDataChanged(true, true);
    }

    // Touch events -  Y axis range movement
    @OnTouch(R.id.graph_view)
    public boolean touchGraphView(MotionEvent event)
    {
        float y = event.getY();

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float dy = y - previousTouchY;
            double actualMinY = pedometerViewModel.getGraphMinY();
            double actualMaxY = pedometerViewModel.getGraphMaxY();
            double actualYRange;

            // Actual display range calculation
            if ((actualMinY < 0) && actualMaxY > 0)
                actualYRange = Math.abs(actualMaxY) + Math.abs(actualMinY);
            else if ((actualMaxY >= 0) && (actualMinY >= 0))
                actualYRange = actualMaxY - actualMinY;
            else actualYRange = Math.abs(actualMinY) - Math.abs(actualMaxY);

            // Up movement
            if (dy < -2) {
                pedometerViewModel.setGraphMinY((float) (actualMinY - 0.02 * actualYRange));
                pedometerViewModel.setGraphMaxY((float) (actualMaxY - 0.02 * actualYRange));
            }

            // Bottom movement
            if (dy > 2) {
                pedometerViewModel.setGraphMinY((float) (actualMinY + 0.02 * actualYRange));
                pedometerViewModel.setGraphMaxY((float) (actualMaxY + 0.02 * actualYRange));
            }

            graph.getViewport().setMinY(pedometerViewModel.getGraphMinY());
            graph.getViewport().setMaxY(pedometerViewModel.getGraphMaxY());
            graph.onDataChanged(true, false);
        }
        previousTouchY = y;
        return false;
    }

    // Axes and legend config
    private void graphConfig() {

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(pedometerViewModel.getGraphMinY());
        graph.getViewport().setMaxY(pedometerViewModel.getGraphMaxY());
        graph.getViewport().setScalableY(false);
        graph.getViewport().setScrollableY(false);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(pedometerViewModel.getGraphMinX());
        graph.getViewport().setMaxX(pedometerViewModel.getGraphMaxX());
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