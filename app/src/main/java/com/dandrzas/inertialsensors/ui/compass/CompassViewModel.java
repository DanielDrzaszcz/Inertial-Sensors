package com.dandrzas.inertialsensors.ui.compass;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dandrzas.inertialsensors.data.IFOrientationAlgorithm;
import com.dandrzas.inertialsensors.data.DataManager;

import java.util.Observable;
import java.util.Observer;

public class CompassViewModel extends ViewModel implements Observer{

    private DataManager dataManager;
    private final String TAG = CompassViewModel.class.getSimpleName();
    private MutableLiveData<Integer> orientation = new MutableLiveData<>();

    public CompassViewModel() {
        // Podłączenie do warstwy danych
        dataManager = DataManager.getInstance();
        dataManager.getAlgorithmComplementaryInstance().addObserver(this);
        dataManager.getSystemAlgrithmInstance().addObserver(this);
        dataManager.getAlgorithmWithoutFusionInstance().addObserver(this);
        dataManager.getAlgorithmMadgwickFilter().addObserver(this);
    }

    // Pobranie danych z warstwy danych przy zmianie wartości
    @Override
    public void update(Observable o, Object arg) {
        float orientation = 0;
        int orientationModRange;
        Log.d(TAG, " Selected Algorithm: " + dataManager.getSelectedAlgorithm());

        orientation = ((IFOrientationAlgorithm) o).getRollPitchYaw(false)[2];


        if((int)arg==dataManager.getSelectedAlgorithm()) {
            if (orientation < 0) {
                orientationModRange = (int) (360 + orientation);
            } else {
                orientationModRange = (int) orientation;
            }
            this.orientation.setValue(orientationModRange);
            Log.d(TAG, " " + " " + orientationModRange);
        }
    }

    public MutableLiveData<Integer> getOrientation() {
        return orientation;
    }

}