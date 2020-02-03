package com.dandrzas.inertialsensorsviewer.UI.gyroscope;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GyroscopeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public GyroscopeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}