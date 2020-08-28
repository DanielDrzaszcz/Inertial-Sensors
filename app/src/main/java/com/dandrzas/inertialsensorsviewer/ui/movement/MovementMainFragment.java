package com.dandrzas.inertialsensorsviewer.ui.movement;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import com.dandrzas.inertialsensorslibrary.Constants;
import com.dandrzas.inertialsensorsviewer.R;
import com.google.android.material.tabs.TabLayout;

import butterknife.ButterKnife;

public class MovementMainFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_movement_main, container, false);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getContext(), getActivity().getSupportFragmentManager());
        ViewPager viewPager = root.findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = root.findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        ButterKnife.bind(this, root);


        return root;
    }


    @Override
    public void onStart() {
        super.onStart();

        // Switching source algorithm
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String selectedAlgorithm = preferences.getString("selected_algorithm", "system_default_algorithm");
       /* switch (selectedAlgorithm) {
            case "system_default_algorithm":
                dataManager.setSelectedAlgorithm(Constants.SYSTEM_ALGORITHM_ID);
                break;
            case "orientation_without_fusion":
                dataManager.setSelectedAlgorithm(Constants.ORIENTATION_WITHOUT_FUSION);
                break;
            case "complementary_filter":
                dataManager.setSelectedAlgorithm(Constants.COMPLEMENTARY_FILTER_ID);
                break;
            case "kalman_filter":
                dataManager.setSelectedAlgorithm(Constants.KALMAN_FILTER_ID);
                break;
            case "extended_kalman_filter":
                dataManager.setSelectedAlgorithm(Constants.EXTENDED_KALMAN_FILTER_ID);
                break;
            case "mahony_filter":
                dataManager.setSelectedAlgorithm(Constants.MAHONY_FILTER_ID);
                break;
            case "madgwick_filter":
                dataManager.setSelectedAlgorithm(Constants.MADGWICK_FILTER_ID);
                break;
        }
*/
    }
}