package com.dandrzas.inertialsensors.ui.movement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.dandrzas.inertialsensors.R;
import com.google.android.material.tabs.TabLayout;

import butterknife.ButterKnife;

public class MovementMainFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_movement_main, container, false);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getContext(), getChildFragmentManager());
        ViewPager viewPager = root.findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = root.findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        ButterKnife.bind(this, root);
        
        return root;
    }

}