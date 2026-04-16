package com.example.spacecolony.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.spacecolony.MainActivity;
import com.example.spacecolony.R;
import com.example.spacecolony.model.CrewMember;
import com.example.spacecolony.model.Storage;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Storage storage = Storage.getInstance();

        // Summary counts
        TextView tvColonyName   = view.findViewById(R.id.tv_colony_name);
        TextView tvTotalCrew    = view.findViewById(R.id.tv_total_crew);
        TextView tvQuarters     = view.findViewById(R.id.tv_count_quarters);
        TextView tvSimulator    = view.findViewById(R.id.tv_count_simulator);
        TextView tvMission      = view.findViewById(R.id.tv_count_mission);
        TextView tvMedbay       = view.findViewById(R.id.tv_count_medbay);
        TextView tvMissionsTotal= view.findViewById(R.id.tv_missions_completed);

        tvColonyName.setText(storage.getName());
        tvTotalCrew.setText("Total crew: " + storage.totalCrew());
        tvQuarters.setText(String.valueOf(storage.countAt(CrewMember.LOCATION_QUARTERS)));
        tvSimulator.setText(String.valueOf(storage.countAt(CrewMember.LOCATION_SIMULATOR)));
        tvMission.setText(String.valueOf(storage.countAt(CrewMember.LOCATION_MISSION_CONTROL)));
        tvMedbay.setText(String.valueOf(storage.countAt(CrewMember.LOCATION_MEDBAY)));
        tvMissionsTotal.setText("Missions completed: " + storage.getCompletedMissions());

        // Navigation buttons
        Button btnRecruit   = view.findViewById(R.id.btn_recruit);
        Button btnQuarters  = view.findViewById(R.id.btn_go_quarters);
        Button btnSimulator = view.findViewById(R.id.btn_go_simulator);
        Button btnMission   = view.findViewById(R.id.btn_go_mission);
        Button btnMedbay    = view.findViewById(R.id.btn_go_medbay);
        Button btnStats     = view.findViewById(R.id.btn_go_stats);

        MainActivity activity = (MainActivity) requireActivity();
        btnRecruit.setOnClickListener(v ->
                activity.showFragment(new RecruitFragment()));
        btnQuarters.setOnClickListener(v ->
                activity.showFragment(new QuartersFragment()));
        btnSimulator.setOnClickListener(v ->
                activity.showFragment(new SimulatorFragment()));
        btnMission.setOnClickListener(v ->
                activity.showFragment(new MissionFragment()));
        btnMedbay.setOnClickListener(v ->
                activity.showMedbay());
        btnStats.setOnClickListener(v ->
                activity.showFragment(new StatsFragment()));
    }
}
