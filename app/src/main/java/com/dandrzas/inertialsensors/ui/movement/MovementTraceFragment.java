package com.dandrzas.inertialsensors.ui.movement;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.dandrzas.inertialsensors.R;
import com.github.mikephil.charting.charts.BubbleChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BubbleData;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovementTraceFragment extends Fragment {

    private MovementTraceViewModel movementTraceViewModel;

    @BindView(R.id.text_view_movement_x)
    TextView textViewMovementX;
    @BindView(R.id.text_view_movement_y)
    TextView textViewMovementY;
    @BindView(R.id.text_view_movement_z)
    TextView textViewMovementZ;
    @BindView(R.id.chart_bubble)
    BubbleChart bubbleChart;
    @BindView(R.id.text_view_velocity_x)
    TextView textViewVelocityX;
    @BindView(R.id.text_view_velocity_y)
    TextView textViewVelocityY;
    @BindView(R.id.text_view_velocity_z)
    TextView textViewVelocityZ;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    public static MovementTraceFragment newInstance() {
        MovementTraceFragment fragment = new MovementTraceFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        movementTraceViewModel = ViewModelProviders.of(this).get(MovementTraceViewModel.class);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_movement_trace, container, false);
        ButterKnife.bind(this, root);
        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.CEILING);

        movementTraceViewModel.getProgressBarVisible().observe(getViewLifecycleOwner(), value ->{
            if(value){
                progressBar.setVisibility(View.VISIBLE);
            }
            else{
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        movementTraceViewModel.getChartDataVisible().observe(getViewLifecycleOwner(), aBoolean ->{
            if(aBoolean){
                bubbleChart.notifyDataSetChanged();
                bubbleChart.invalidate();
                bubbleChart.setVisibility(View.VISIBLE);
            }
            else{
                bubbleChart.setVisibility(View.INVISIBLE);
            }
        });

        movementTraceViewModel.startView();

        textViewMovementX.setText(getResources().getString(R.string.movement_x) + " 0.000 m");
        textViewMovementY.setText(getResources().getString(R.string.movement_y) + " 0.000 m");
        textViewMovementZ.setText(getResources().getString(R.string.movement_z) + " 0.000 m");
        movementTraceViewModel.getMovementPos().observe(getViewLifecycleOwner(), value -> {
            textViewMovementX.setText(getResources().getString(R.string.movement_x) + " " + df.format(value[0]) + " m");
            textViewMovementY.setText(getResources().getString(R.string.movement_y) + " " + df.format(value[1]) + " m");
            textViewMovementZ.setText(getResources().getString(R.string.movement_z) + " " + df.format(value[2]) + " m");
        });

        textViewVelocityX.setText(getResources().getString(R.string.velocity_x) + " 0.000 m");
        textViewVelocityY.setText(getResources().getString(R.string.velocity_y) + " 0.000 m");
        textViewVelocityZ.setText(getResources().getString(R.string.velocity_z) + " 0.000 m");
        movementTraceViewModel.getVelocity().observe(getViewLifecycleOwner(), value -> {
            textViewVelocityX.setText(getResources().getString(R.string.velocity_x) + " " + df.format(value[0]) + " m");
            textViewVelocityY.setText(getResources().getString(R.string.velocity_y) + " " + df.format(value[1]) + " m");
            textViewVelocityZ.setText(getResources().getString(R.string.velocity_z) + " " + df.format(value[2]) + " m");
        });

        movementTraceViewModel.getChartXMax().observe(getViewLifecycleOwner(), value->{
            bubbleChart.getXAxis().setAxisMaximum(value);
        });

        movementTraceViewModel.getChartXMin().observe(getViewLifecycleOwner(), value->{
            bubbleChart.getXAxis().setAxisMinimum(value);
        });

        movementTraceViewModel.getChartYMax().observe(getViewLifecycleOwner(), value->{
            bubbleChart.getAxisLeft().setAxisMaximum(value);
            bubbleChart.getAxisRight().setAxisMaximum(value);
        });

        movementTraceViewModel.getChartYMin().observe(getViewLifecycleOwner(), value->{
            bubbleChart.getAxisLeft().setAxisMinimum(value);
            bubbleChart.getAxisRight().setAxisMinimum(value);
        });

        bubbleChartConfig();

        return root;
    }

    // chart view config
    private void bubbleChartConfig() {
        bubbleChart.setData(new BubbleData(movementTraceViewModel.getBubbleDataSet()));
        bubbleChart.getLegend().setEnabled(false);
        bubbleChart.setBorderColor(Color.BLACK);
        bubbleChart.setNoDataTextColor(Color.BLACK);
        bubbleChart.setScaleEnabled(false);
        bubbleChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        bubbleChart.getDescription().setEnabled(false);
    }
}