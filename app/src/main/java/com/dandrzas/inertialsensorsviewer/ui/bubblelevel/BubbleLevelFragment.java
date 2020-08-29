package com.dandrzas.inertialsensorsviewer.ui.bubblelevel;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import com.dandrzas.inertialsensorsviewer.R;
import com.dandrzas.inertialsensorsviewer.data.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;


public class BubbleLevelFragment extends Fragment {

    private BubbleLevelViewModel bubbleLevelViewModel;
    private final String TAG = BubbleLevelFragment.class.getSimpleName();
    @BindView(R.id.imageView_bubbleLevelLineHorizontal)
    ImageView imageView_bubbleLevelLineHorizontal;

    @BindView(R.id.imageView_bubbleHorizontal)
    ImageView imageView_bubbleHorizontal;

    @BindView(R.id.imageView_bubbleLevelLineVertical)
    ImageView imageView_bubbleLevelLineVertical;

    @BindView(R.id.imageView_bubbleVertical)
    ImageView imageView_bubbleVertical;

    @BindView(R.id.imageView_bubbleLevelCircle)
    ImageView imageView_bubbleLevelCircle;

    @BindView(R.id.imageView_bubbleCircle)
    ImageView imageView_bubbleCircle;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        bubbleLevelViewModel =
                ViewModelProviders.of(this).get(BubbleLevelViewModel.class);
        View root = inflater.inflate(R.layout.fragment_bubblelevel, container, false);
        ButterKnife.bind(this, root);

        root.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if(!bubbleLevelViewModel.isViewsInitDone()){
            bubbleLevelViewModel.setLineVerticalRange(imageView_bubbleLevelLineVertical.getHeight());
            bubbleLevelViewModel.setLineVerticalBubblePos(imageView_bubbleVertical.getY());
            bubbleLevelViewModel.setLineHorizontalRange(imageView_bubbleLevelLineHorizontal.getWidth());
            bubbleLevelViewModel.setLineHorizontalBubblePos(imageView_bubbleHorizontal.getX());
            bubbleLevelViewModel.setCircleRange(imageView_bubbleLevelCircle.getWidth());
            bubbleLevelViewModel.setCircleBubblePosZero(imageView_bubbleCircle.getX(), imageView_bubbleCircle.getY());
            bubbleLevelViewModel.setViewsInitDone(true);
        }
        });

        bubbleLevelViewModel.getLineVerticalBubblePos().observe(this, value->{
            imageView_bubbleVertical.setY(value);
        });
        bubbleLevelViewModel.getLineHorizontalBubblePos().observe(this, value->{
            imageView_bubbleHorizontal.setX(value);
            Log.d(TAG, " przesuniecie: " + value);
        });
        bubbleLevelViewModel.getCircleBubblePos().observe(this, value ->{
            imageView_bubbleCircle.setX(value[0]);
            imageView_bubbleCircle.setY(value[1]);
        });
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

}