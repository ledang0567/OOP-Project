package com.example.spacecolony.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spacecolony.R;
import com.example.spacecolony.model.CrewMember;
import com.example.spacecolony.model.Storage;
import com.example.spacecolony.ui.CrewAdapter;

import java.util.Comparator;
import java.util.List;

public class StatsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Storage storage = Storage.getInstance();
        List<CrewMember> allCrew = storage.getAllCrew();

        // Colony-wide stats
        TextView tvColonyStats = view.findViewById(R.id.tv_colony_stats);
        int totalMissions  = storage.getCompletedMissions();
        int totalCrew      = allCrew.size();
        int totalXP        = allCrew.stream().mapToInt(CrewMember::getExperience).sum();
        int totalTraining  = allCrew.stream().mapToInt(CrewMember::getTrainingSessions).sum();
        int totalWins      = allCrew.stream().mapToInt(CrewMember::getMissionsWon).sum();

        tvColonyStats.setText(
                "Colony: " + storage.getName() + "\n" +
                        "Total crew:         " + totalCrew + "\n" +
                        "Missions completed: " + totalMissions + "\n" +
                        "Total crew wins:    " + totalWins + "\n" +
                        "Total XP earned:    " + totalXP + "\n" +
                        "Training sessions:  " + totalTraining
        );

        // Sort crew by missions won (descending) for leaderboard feel
        allCrew.sort(Comparator.comparingInt(CrewMember::getMissionsWon).reversed());

        // Show crew in a read-only RecyclerView (no checkboxes)
        RecyclerView recycler = view.findViewById(R.id.recycler_stats);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        CrewAdapter adapter = new CrewAdapter(allCrew, false);
        recycler.setAdapter(adapter);

        // Per-crew detail on click
        adapter.setOnCrewClickListener(cm -> showCrewDetail(view, cm));
    }

    private void showCrewDetail(View view, CrewMember cm) {
        TextView tvDetail = view.findViewById(R.id.tv_crew_detail);
        tvDetail.setVisibility(View.VISIBLE);
        tvDetail.setText(
                "— " + cm.getSpecialization() + ": " + cm.getName() + " —\n" +
                        "Missions completed: " + cm.getMissionsCompleted() + "\n" +
                        "Missions won:       " + cm.getMissionsWon() + "\n" +
                        "Training sessions:  " + cm.getTrainingSessions() + "\n" +
                        "Experience (XP):    " + cm.getExperience() + "\n" +
                        "Effective skill:    " + cm.getEffectiveSkill() + "\n" +
                        "Location:           " + cm.getLocation()
        );
    }
}
