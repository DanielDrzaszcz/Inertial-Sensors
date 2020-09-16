package com.dandrzas.inertialsensors.ui.orientation;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.dandrzas.inertialsensors.R;
import com.dandrzas.inertialsensors.data.DataManager;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class OrientationFragment extends Fragment {

    private OrientationViewModel orientationViewModel;
    private float previousTouchY;
    private final String TAG =  OrientationFragment.class.getSimpleName();

    @BindView(R.id.graph_view)
    GraphView graph;
    @BindView(R.id.button_zoom_in)
    Button button_zoom_in;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        orientationViewModel =
                ViewModelProviders.of(this).get(OrientationViewModel.class);
        View root = inflater.inflate(R.layout.fragment_orientation, container, false);

        ButterKnife.bind(this, root);
        DataManager.getInstance().init(getContext());
        orientationViewModel.getGraphSeriesX().observe(this, new GraphSeriesObserver());
        orientationViewModel.getGraphSeriesY().observe(this, new GraphSeriesObserver());
        orientationViewModel.getGraphSeriesZ().observe(this, new GraphSeriesObserver());

        return root;
    }


    @Override
    public void onStart() {
        super.onStart();
        orientationViewModel.changeSelectedAlgorithm();
        graphConfig();
    }

    @Override
    public void onDestroy() {
        graph.getSeries().get(0).clearReference(graph); // clear reference to avoid memory leaks
        graph.getSeries().get(1).clearReference(graph);
        graph.getSeries().get(2).clearReference(graph);
        orientationViewModel.getGraphSeriesX().removeObservers(this);
        orientationViewModel.getGraphSeriesY().removeObservers(this);
        orientationViewModel.getGraphSeriesZ().removeObservers(this);
        super.onDestroy();
    }


    @OnClick(R.id.button_zoom_out)
    public void clickZoomOut(){
        double actualMinY = orientationViewModel.getGraphMinY();
        double actualMaxY = orientationViewModel.getGraphMaxY();
        double actualYRange = actualMaxY - actualMinY;

        orientationViewModel.setGraphMinY((float) (actualMinY - actualYRange / 10));
        orientationViewModel.setGraphMaxY((float) (actualMaxY + actualYRange / 10));

        graph.getViewport().setMinY(orientationViewModel.getGraphMinY());
        graph.getViewport().setMaxY(orientationViewModel.getGraphMaxY());
        graph.onDataChanged(true, false);
    }

    @OnClick(R.id.button_zoom_in)
    public void clickZoomIn() {
        double actualMinY = orientationViewModel.getGraphMinY();
        double actualMaxY = orientationViewModel.getGraphMaxY();
        double actualYRange = actualMaxY - actualMinY;

        if (actualYRange > 4) {
            orientationViewModel.setGraphMinY((float) (actualMinY + actualYRange / 10));
            orientationViewModel.setGraphMaxY((float) (actualMaxY - actualYRange / 10));
        }
        graph.getViewport().setMinY(orientationViewModel.getGraphMinY());
        graph.getViewport().setMaxY(orientationViewModel.getGraphMaxY());
        graph.onDataChanged(true, false);
    }

    // Axis Y range shift
    @OnTouch(R.id.graph_view)
    public boolean touchGraphView(MotionEvent event)
    {
        float y = event.getY();

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float dy = y - previousTouchY;
            double actualMinY = orientationViewModel.getGraphMinY();
            double actualMaxY = orientationViewModel.getGraphMaxY();
            double actualYRange;

            // Calc range
            if ((actualMinY < 0) && actualMaxY > 0)
                actualYRange = Math.abs(actualMaxY) + Math.abs(actualMinY);
            else if ((actualMaxY >= 0) && (actualMinY >= 0))
                actualYRange = actualMaxY - actualMinY;
            else actualYRange = Math.abs(actualMinY) - Math.abs(actualMaxY);

            // Down
            if (dy < -2) {
                orientationViewModel.setGraphMinY((float) (actualMinY - 0.02 * actualYRange));
                orientationViewModel.setGraphMaxY((float) (actualMaxY - 0.02 * actualYRange));
            }

            // Up
            if (dy > 2) {
                orientationViewModel.setGraphMinY((float) (actualMinY + 0.02 * actualYRange));
                orientationViewModel.setGraphMaxY((float) (actualMaxY + 0.02 * actualYRange));
            }

            graph.getViewport().setMinY(orientationViewModel.getGraphMinY());
            graph.getViewport().setMaxY(orientationViewModel.getGraphMaxY());
            graph.onDataChanged(true, false);
        }
        previousTouchY = y;
        return false;
    }

    // Axes and legend config
    private void graphConfig() {

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(orientationViewModel.getGraphMinY());
        graph.getViewport().setMaxY(orientationViewModel.getGraphMaxY());
        graph.getViewport().setScalableY(false);
        graph.getViewport().setScrollableY(false);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(orientationViewModel.getGraphMinX());
        graph.getViewport().setMaxX(orientationViewModel.getGraphMaxX());
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

    private class GraphSeriesObserver implements Observer<LineGraphSeries<DataPoint>> {
        @Override
        public void onChanged(LineGraphSeries<DataPoint> dataPointLineGraphSeries) {
            if(graph.getSeries().size()==3){
                graph.getSeries().get(0).clearReference(graph); // clean reference to avoid memory leaks
                graph.getSeries().get(1).clearReference(graph);
                graph.getSeries().get(2).clearReference(graph);
                graph.removeAllSeries();
            }
            graph.addSeries(dataPointLineGraphSeries);
            graphConfig();
            Log.d(TAG, " DataChanged: Max X: " + dataPointLineGraphSeries.getHighestValueX());
        }
    }
}