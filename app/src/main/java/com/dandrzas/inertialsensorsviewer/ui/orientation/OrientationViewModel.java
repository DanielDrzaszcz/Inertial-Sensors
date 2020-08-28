package com.dandrzas.inertialsensorsviewer.ui.orientation;

import android.graphics.Color;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dandrzas.inertialsensorslibrary.Constants;
import com.dandrzas.inertialsensorslibrary.data.DataManager;
import com.dandrzas.inertialsensorslibrary.data.OrientationAlgorithm;
import com.dandrzas.inertialsensorslibrary.data.SystemAlgorithm;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Observable;
import java.util.Observer;

public class OrientationViewModel extends ViewModel implements Observer {

    private LineGraphSeries<DataPoint> complementaryFilterSeriesX = new LineGraphSeries<>();
    private LineGraphSeries<DataPoint> complementaryFilterSeriesY = new LineGraphSeries<>();
    private LineGraphSeries<DataPoint> complementaryFilterSeriesZ = new LineGraphSeries<>();
    private LineGraphSeries<DataPoint> systemAlgorithmSeriesX = new LineGraphSeries<>();
    private LineGraphSeries<DataPoint> systemAlgorithmSeriesY = new LineGraphSeries<>();
    private LineGraphSeries<DataPoint> systemAlgorithmSeriesZ = new LineGraphSeries<>();
    private LineGraphSeries<DataPoint> algorithmWithoutFusionSeriesX = new LineGraphSeries<>();
    private LineGraphSeries<DataPoint> algorithmWithoutFusionSeriesY = new LineGraphSeries<>();
    private LineGraphSeries<DataPoint> algorithmWithoutFusionSeriesZ = new LineGraphSeries<>();
    private MutableLiveData<LineGraphSeries<DataPoint>> graphSeriesXLiveData;
    private MutableLiveData<LineGraphSeries<DataPoint>> graphSeriesYLiveData;
    private MutableLiveData<LineGraphSeries<DataPoint>> graphSeriesZLiveData;
    private LineGraphSeries<DataPoint> graphSeriesX;
    private LineGraphSeries<DataPoint> graphSeriesY;
    private LineGraphSeries<DataPoint> graphSeriesZ;
    private DataManager dataManager;
    private final float graphInitialMaxY = 180;
    private float graphMaxY = graphInitialMaxY;
    private float graphMinY = (-1) * graphInitialMaxY;
    private int graphMaxX = 3000;
    private int selectedAlgorithm;
    private final String TAG = OrientationViewModel.class.getSimpleName();

    public OrientationViewModel() {
        // Podłączenie do warstwy danych
        dataManager = DataManager.getInstance();
        dataManager.getAlgorithmComplementaryInstance().addObserver(this);
        dataManager.getSystemAlgrithmInstance().addObserver(this);
        dataManager.getAlgorithmWithoutFusionInstance().addObserver(this);

        // Utworzenie i konfiguracja serii danych
        initDataSeries(complementaryFilterSeriesX, complementaryFilterSeriesY, complementaryFilterSeriesZ);
        initDataSeries(systemAlgorithmSeriesX, systemAlgorithmSeriesY, systemAlgorithmSeriesZ);
        initDataSeries(algorithmWithoutFusionSeriesX, algorithmWithoutFusionSeriesY, algorithmWithoutFusionSeriesZ);

        // Ustawienie startowej serii danych
        graphSeriesX = complementaryFilterSeriesX;
        graphSeriesY = complementaryFilterSeriesY;
        graphSeriesZ = complementaryFilterSeriesZ;

        graphSeriesXLiveData = new MutableLiveData<>();
        graphSeriesXLiveData.setValue(graphSeriesX);
        graphSeriesYLiveData = new MutableLiveData<>();
        graphSeriesYLiveData.setValue(graphSeriesY);
        graphSeriesZLiveData = new MutableLiveData<>();
        graphSeriesZLiveData.setValue(graphSeriesZ);
    }
    // Utworzenie i konfiguracja serii danych
    private void initDataSeries(LineGraphSeries<DataPoint> seriesX, LineGraphSeries<DataPoint> seriesY, LineGraphSeries<DataPoint> seriesZ) {
        seriesX.setThickness(2);
        seriesX.setColor(Color.BLUE);
        seriesX.setTitle("Rotacja X [°]");

        seriesY.setThickness(2);
        seriesY.setColor(Color.RED);
        seriesY.setTitle("Rotacja Y [°]");

        seriesZ.setThickness(2);
        seriesZ.setColor(Color.GREEN);
        seriesZ.setTitle("Rotacja Z [°]");
    }

    // Pobranie danych z warstwy danych przy zmianie wartości
    @Override
    public void update(Observable o, Object arg) {

        Log.d(TAG, " Selected Algorithm: " + selectedAlgorithm);

        if (o instanceof OrientationAlgorithm) {
            if (arg.equals(Constants.COMPLEMENTARY_FILTER_ID)) {
                float[] valuesComplementaryFilter = ((OrientationAlgorithm) o).getRollPitchYaw(false);
                boolean scrollToEnd1 = false;
                if (complementaryFilterSeriesY.getHighestValueX() >= graphMaxX) {
                    scrollToEnd1 = true; // uruchom przesuwanie wartości w serii danych
                }
                complementaryFilterSeriesX.appendData(new DataPoint(complementaryFilterSeriesX.getHighestValueX() + 1, valuesComplementaryFilter[0]), scrollToEnd1, graphMaxX);
                complementaryFilterSeriesY.appendData(new DataPoint(complementaryFilterSeriesY.getHighestValueX() + 1, valuesComplementaryFilter[1]), scrollToEnd1, graphMaxX);
                complementaryFilterSeriesZ.appendData(new DataPoint(complementaryFilterSeriesZ.getHighestValueX() + 1, valuesComplementaryFilter[2]), scrollToEnd1, graphMaxX);

                Log.d(TAG, " DataChanged in Algorithm: " + valuesComplementaryFilter[0] + " " + valuesComplementaryFilter[1] + " " + valuesComplementaryFilter[2]);
            }
            else if(arg.equals(Constants.ORIENTATION_WITHOUT_FUSION)){
                float[] valuesAlgorithmWithoutFusion = ((OrientationAlgorithm) o).getRollPitchYaw(false);
                boolean scrollToEnd1 = false;
                if (algorithmWithoutFusionSeriesY.getHighestValueX() >= graphMaxX) {
                    scrollToEnd1 = true;
                }
                algorithmWithoutFusionSeriesX.appendData(new DataPoint(algorithmWithoutFusionSeriesX.getHighestValueX() + 1, valuesAlgorithmWithoutFusion[0]), scrollToEnd1, graphMaxX);
                algorithmWithoutFusionSeriesY.appendData(new DataPoint(algorithmWithoutFusionSeriesY.getHighestValueX() + 1, valuesAlgorithmWithoutFusion[1]), scrollToEnd1, graphMaxX);
                algorithmWithoutFusionSeriesZ.appendData(new DataPoint(algorithmWithoutFusionSeriesZ.getHighestValueX() + 1, valuesAlgorithmWithoutFusion[2]), scrollToEnd1, graphMaxX);
            }
        }
        else if (arg.equals(Constants.SYSTEM_ALGORITHM_ID)) {
            float[] valuesSystemAlgorithm = ((SystemAlgorithm) o).getRollPitchYaw(false);
            boolean scrollToEnd1 = false;
            if (systemAlgorithmSeriesY.getHighestValueX() >= graphMaxX) {
                scrollToEnd1 = true;
            }
            systemAlgorithmSeriesX.appendData(new DataPoint(systemAlgorithmSeriesX.getHighestValueX() + 1, valuesSystemAlgorithm[0]), scrollToEnd1, graphMaxX);
            systemAlgorithmSeriesY.appendData(new DataPoint(systemAlgorithmSeriesY.getHighestValueX() + 1, valuesSystemAlgorithm[1]), scrollToEnd1, graphMaxX);
            systemAlgorithmSeriesZ.appendData(new DataPoint(systemAlgorithmSeriesZ.getHighestValueX() + 1, valuesSystemAlgorithm[2]), scrollToEnd1, graphMaxX);

            Log.d(TAG, " DataChanged in Algorithm: " + valuesSystemAlgorithm[0] + " " + valuesSystemAlgorithm[1] + " " + valuesSystemAlgorithm[2]);
        }
    }

    // Przełączenie przekazywanych do widoku serii danych
    public void setSelectedAlgorithm(int selectedAlgorithm) {

        this.selectedAlgorithm = selectedAlgorithm;

        switch (selectedAlgorithm) {
            case Constants.SYSTEM_ALGORITHM_ID:
                graphSeriesX = systemAlgorithmSeriesX;
                graphSeriesY = systemAlgorithmSeriesY;
                graphSeriesZ = systemAlgorithmSeriesZ;
                break;
            case Constants.ORIENTATION_WITHOUT_FUSION:
                graphSeriesX = algorithmWithoutFusionSeriesX;
                graphSeriesY = algorithmWithoutFusionSeriesY;
                graphSeriesZ = algorithmWithoutFusionSeriesZ;
                break;
            case Constants.COMPLEMENTARY_FILTER_ID:
                graphSeriesX = complementaryFilterSeriesX;
                graphSeriesY = complementaryFilterSeriesY;
                graphSeriesZ = complementaryFilterSeriesZ;
                break;
        }
        graphSeriesXLiveData.setValue(graphSeriesX);
        graphSeriesYLiveData.setValue(graphSeriesY);
        graphSeriesZLiveData.setValue(graphSeriesZ);
    }

    public int getSelectedAlgorithm() {
        return selectedAlgorithm;
    }

    public LiveData<LineGraphSeries<DataPoint>> getGraphSeriesX() {
        return graphSeriesXLiveData;
    }

    public LiveData<LineGraphSeries<DataPoint>> getGraphSeriesY() {
        return graphSeriesYLiveData;
    }

    public LiveData<LineGraphSeries<DataPoint>> getGraphSeriesZ() {
        return graphSeriesZLiveData;
    }

    public int getGraphMaxX() {

        int graphSeriesMaxX = graphMaxX;

        if (graphSeriesX.getHighestValueX() >= graphMaxX) {
            graphSeriesMaxX = (int) (graphSeriesX.getHighestValueX()); // uwzględnia przesuwanie danych w serii
        } else {
            graphSeriesMaxX = graphMaxX;
        }


        return graphSeriesMaxX;
    }

    public int getGraphMinX() {

        if (graphSeriesX.getHighestValueX() >= graphMaxX) {
            return (int) (graphSeriesX.getLowestValueX()); // uwzględnia przesuwanie danych w serii
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

    public void startComputing()
    {
        dataManager.startComputing();
    }

    public void stopComputing()
    {
        dataManager.stopComputing();
    }

    public boolean isComputingRunning()
    {
        return dataManager.isComputingRunning();
    }

}