package com.dandrzas.inertialsensorsviewer.UI.accelerometer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AccelerometerViewModel extends ViewModel {

private MutableLiveData<String> mText;

public AccelerometerViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
        }

public LiveData<String> getText() {
        return mText;
        }
        }