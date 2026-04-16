package com.example.spacecolony.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spacecolony.R;
import com.example.spacecolony.data.DataManager;
import com.example.spacecolony.model.CrewMember;
import com.example.spacecolony.model.Storage;
import com.example.spacecolony.ui.CrewAdapter;

import java.util.List;

public class QuartersFragment extends Fragment {
    private CrewAdapter adapter;
    private TextView    tvEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quarters, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvEmpty = view.findViewById(R.id.tv_empty);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_quarters);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<CrewMember> crew = Storage.getInstance().getCrewInQuarters();
        adapter = new CrewAdapter(crew, true); // checkbox mode on
        recyclerView.setAdapter(adapter);
        updateEmptyState(crew);

        Button btnToSimulator = view.findViewById(R.id.btn_to_simulator);
        Button btnToMission   = view.findViewById(R.id.btn_to_mission);

        btnToSimulator.setOnClickListener(v -> moveSelected(CrewMember.LOCATION_SIMULATOR));
        btnToMission.setOnClickListener(v   -> moveSelected(CrewMember.LOCATION_MISSION_CONTROL));
    }

    private void moveSelected(String destination) {
        List<Integer> ids = adapter.getSelectedIds();
        if (ids.isEmpty()) {
            Toast.makeText(getContext(), "Select at least one crew member.", Toast.LENGTH_SHORT).show();
            return;
        }

        Storage storage = Storage.getInstance();
        for (int id : ids) {
            CrewMember cm = storage.getCrewMember(id);
            if (cm != null) cm.setLocation(destination);
        }
        DataManager.save(requireContext());

        // Refresh list
        List<CrewMember> updated = storage.getCrewInQuarters();
        adapter.updateList(updated);
        updateEmptyState(updated);

        Toast.makeText(getContext(),
                ids.size() + " crew member(s) moved to " + destination,
                Toast.LENGTH_SHORT).show();
    }

    private void updateEmptyState(List<CrewMember> crew) {
        tvEmpty.setVisibility(crew.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
