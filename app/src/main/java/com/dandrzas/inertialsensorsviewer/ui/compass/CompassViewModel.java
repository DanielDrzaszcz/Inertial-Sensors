package com.dandrzas.inertialsensorsviewer.ui.compass;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dandrzas.inertialsensorslibrary.data.DataManager;
import com.dandrzas.inertialsensorslibrary.data.IFOrientationAlgorithm;

import java.util.Observable;
import java.util.Observer;

public class CompassViewModel extends ViewModel implements Observer{

    private DataManager dataManager;
    private int selectedAlgorithm;
    private final String TAG = CompassViewModel.class.getSimpleName();
    private MutableLiveData<Integer> orientation = new MutableLiveData<>();

    public CompassViewModel() {
        // Podłączenie do warstwy danych
        dataManager = DataManager.getInstance();
        dataManager.getAlgorithmComplementaryInstance().addObserver(this);
        dataManager.getSystemAlgrithmInstance().addObserver(this);
        dataManager.getAlgorithmWithoutFusionInstance().addObserver(this);
    }

    // Pobranie danych z warstwy danych przy zmianie wartości
    @Override
    public void update(Observable o, Object arg) {
        float orientation = 0;
        int orientationModRange;
        Log.d(TAG, " Selected Algorithm: " + selectedAlgorithm);

        orientation = ((IFOrientationAlgorithm) o).getRollPitchYaw(false)[2];


        if((int)arg==selectedAlgorithm) {
            if (orientation < 0) {
                orientationModRange = (int) (360 + orientation);
            } else {
                orientationModRange = (int) orientation;
            }
            this.orientation.setValue(orientationModRange);
            Log.d(TAG, " " + " " + orientationModRange);
        }
    }

    // Przełączenie przekazywanych do widoku serii danych
    public void setSelectedAlgorithm(int selectedAlgorithm) {
        this.selectedAlgorithm = selectedAlgorithm;
    }

    public int getSelectedAlgorithm() {
        return selectedAlgorithm;
    }

    public void startComputing() {
        dataManager.startComputing();
    }

    public void stopComputing() {
        dataManager.stopComputing();
    }

    public boolean isComputingRunning() {
        return dataManager.isComputingRunning();
    }

    public MutableLiveData<Integer> getOrientation() {
        return orientation;
    }

}