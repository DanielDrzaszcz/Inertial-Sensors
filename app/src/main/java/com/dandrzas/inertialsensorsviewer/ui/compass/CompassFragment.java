package com.dandrzas.inertialsensorsviewer.ui.compass;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import com.dandrzas.inertialsensorsviewer.R;
import com.dandrzas.inertialsensorsviewer.data.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CompassFragment extends Fragment {

    private CompassViewModel compassViewModel;
    @BindView(R.id.textView_orientation)
    TextView textViewOrientation;

    @BindView(R.id.imageView_compass)
    ImageView imageViewCompass;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        compassViewModel =
                ViewModelProviders.of(this).get(CompassViewModel.class);
        View root = inflater.inflate(R.layout.fragment_compass, container, false);
        ButterKnife.bind(this, root);
        compassViewModel.getOrientation().observe(this, value -> {
            String direction = "";
            imageViewCompass.setRotation((-1)*value);
            if((value>=315)||(value<45)){
                direction = "N";
            }
            else if((value>=45)&&(value<135)){
                direction = "E";
            }
            else if((value>=135)&&(value<225)){
                direction = "S";
            }
            else if((value>=225)&&(value<315)){
                direction = "W";
            }
            textViewOrientation.setText( value + " ° " + direction);
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        imageViewCompass.setRotation(0);
        textViewOrientation.setText("-°");
    }

}