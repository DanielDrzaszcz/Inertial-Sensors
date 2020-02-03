package com.dandrzas.inertialsensorsviewer.UI.magnetometer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MagnetometerViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MagnetometerViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}