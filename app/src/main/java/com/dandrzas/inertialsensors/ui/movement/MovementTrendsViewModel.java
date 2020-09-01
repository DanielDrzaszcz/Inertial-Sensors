package com.dandrzas.inertialsensors.ui.movement;

import android.graphics.Color;
import androidx.lifecycle.ViewModel;

import com.dandrzas.inertialsensors.data.DataManager;
import com.dandrzas.inertialsensors.data.InertialTrackingAlgorithm;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Observable;
import java.util.Observer;

public class MovementTrendsViewModel extends ViewModel implements Observer {
    private DataManager dataManager;
    private final String TAG = MovementTrendsViewModel.class.getSimpleName();
    private final float graphInitialMaxY = 1.2f;
    private final float graphInitialMinY = -0.8f;
    private float graphMaxY = graphInitialMaxY;
    private float graphMinY = graphInitialMinY;
    private int graphMaxX = 1500;
    private LineGraphSeries<DataPoint> rawAccelerationSeries = new LineGraphSeries<>();
    private LineGraphSeries<DataPoint> orientationSeries = new LineGraphSeries<>();
    private LineGraphSeries<DataPoint> globalAccelerationSeries = new LineGraphSeries<>();
    private LineGraphSeries<DataPoint> linearAccelerationSeries = new LineGraphSeries<>();
    private LineGraphSeries<DataPoint> gravitySeries = new LineGraphSeries<>();
    private LineGraphSeries<DataPoint> velocitySeries = new LineGraphSeries<>();
    private LineGraphSeries<DataPoint> movementSeries = new LineGraphSeries<>();
    private int selectedAxis = 0;

    public MovementTrendsViewModel() {
        dataManager = DataManager.getInstance();
        dataManager.getInertialTrackingAlgorithmInstance().addObserver(this);
        initDataSeries();
    }


    @Override
    public void update(Observable o, Object arg) {

        if (o instanceof InertialTrackingAlgorithm) {

            float[] rawAcceleration = dataManager.getAccelerometer().getSampleValue();
            float[] orientation = ((InertialTrackingAlgorithm) o).getOrientationAlgorithm().getRollPitchYaw(false);
            float[] globalAcceleration = ((InertialTrackingAlgorithm) o).getAccelerationGlobal();
            float[] linearAcceleration = ((InertialTrackingAlgorithm) o).getLinearAcceleration();
            float[] gravity = ((InertialTrackingAlgorithm) o).getGravity();
            float[] velocity = ((InertialTrackingAlgorithm) o).getCalculatedVelocity();
            float[] movement = ((InertialTrackingAlgorithm) o).getCalculatedMovement();

            boolean scrollToEnd1 = false;
            if (rawAccelerationSeries.getHighestValueX() >= graphMaxX) {
                scrollToEnd1 = true; // uruchom przesuwanie wartości w serii danych
            }

            rawAccelerationSeries.appendData(new DataPoint(rawAccelerationSeries.getHighestValueX() + 1, rawAcceleration[selectedAxis]), scrollToEnd1, graphMaxX);
            orientationSeries.appendData(new DataPoint(orientationSeries.getHighestValueX() + 1, orientation[selectedAxis]), scrollToEnd1, graphMaxX);
            globalAccelerationSeries.appendData(new DataPoint(globalAccelerationSeries.getHighestValueX() + 1, globalAcceleration[selectedAxis]), scrollToEnd1, graphMaxX);
            linearAccelerationSeries.appendData(new DataPoint(linearAccelerationSeries.getHighestValueX() + 1, linearAcceleration[selectedAxis]), scrollToEnd1, graphMaxX);
            gravitySeries.appendData(new DataPoint(gravitySeries.getHighestValueX() + 1, gravity[selectedAxis]), scrollToEnd1, graphMaxX);
            velocitySeries.appendData(new DataPoint(velocitySeries.getHighestValueX() + 1, velocity[selectedAxis]), scrollToEnd1, graphMaxX);
            movementSeries.appendData(new DataPoint(movementSeries.getHighestValueX() + 1, movement[selectedAxis]), scrollToEnd1, graphMaxX);
        }
    }


    private void initDataSeries() {
        rawAccelerationSeries.setThickness(2);
        rawAccelerationSeries.setColor(Color.BLACK);
        rawAccelerationSeries.setTitle("Przyspieszenie");

        orientationSeries.setThickness(2);
        orientationSeries.setColor(Color.YELLOW);
        orientationSeries.setTitle("Orientacja");

        globalAccelerationSeries.setThickness(2);
        globalAccelerationSeries.setColor(Color.CYAN);
        globalAccelerationSeries.setTitle("Przysp. globalne");

        linearAccelerationSeries.setThickness(2);
        linearAccelerationSeries.setColor(Color.GREEN);
        linearAccelerationSeries.setTitle("Przysp. liniowe");

        gravitySeries.setThickness(2);
        gravitySeries.setColor(Color.BLUE);
        gravitySeries.setTitle("Grawitacja");

        velocitySeries.setThickness(2);
        velocitySeries.setColor(Color.MAGENTA);
        velocitySeries.setTitle("Prędkość");

        movementSeries.setThickness(2);
        movementSeries.setColor(Color.RED);
        movementSeries.setTitle("Droga");
    }

    public int getGraphMaxX() {

        int graphSeriesMaxX = graphMaxX;

        if (rawAccelerationSeries.getHighestValueX() >= graphMaxX) {
            graphSeriesMaxX = (int) (rawAccelerationSeries.getHighestValueX()); // uwzględnia przesuwanie danych w serii
        } else {
            graphSeriesMaxX = graphMaxX;
        }


        return graphSeriesMaxX;
    }

    public int getGraphMinX() {

        if (rawAccelerationSeries.getHighestValueX() >= graphMaxX) {
            return (int) (rawAccelerationSeries.getLowestValueX()); // uwzględnia przesuwanie danych w serii
        } else return 0;
    }

    public void setGraphMinY(float newMinYValue) {

        this.graphMinY = newMinYValue;
    }

    public float getGraphMinY() {

        return graphMinY;
    }

    public void setGraphMaxY(float newMaxYValue) {

        this.graphMaxY = newMaxYValue;
    }

    public float getGraphMaxY() {

        return graphMaxY;
    }

    public LineGraphSeries<DataPoint> getRawAccelerationSeries() {
        return rawAccelerationSeries;
    }

    public LineGraphSeries<DataPoint> getOrientationSeries() {
        return orientationSeries;
    }

    public LineGraphSeries<DataPoint> getGlobalAccelerationSeries() {
        return globalAccelerationSeries;
    }

    public LineGraphSeries<DataPoint> getLinearAccelerationSeries() {
        return linearAccelerationSeries;
    }

    public LineGraphSeries<DataPoint> getVelocitySeries() {
        return velocitySeries;
    }

    public LineGraphSeries<DataPoint> getGravitySeries() {
        return gravitySeries;
    }

    public LineGraphSeries<DataPoint> getMovementSeries() {
        return movementSeries;
    }

    public void setSelectedAxis(int selectedAxis) {
        this.selectedAxis = selectedAxis;
    }
}