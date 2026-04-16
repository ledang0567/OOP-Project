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

public class SimulatorFragment extends Fragment {
    private CrewAdapter adapter;
    private TextView    tvEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_simulator, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvEmpty = view.findViewById(R.id.tv_empty_sim);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_simulator);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<CrewMember> crew = Storage.getInstance().getCrewInSimulator();
        adapter = new CrewAdapter(crew, true);
        recyclerView.setAdapter(adapter);
        updateEmptyState(crew);

        Button btnTrain     = view.findViewById(R.id.btn_train);
        Button btnToQuarters= view.findViewById(R.id.btn_sim_to_quarters);

        btnTrain.setOnClickListener(v      -> trainSelected());
        btnToQuarters.setOnClickListener(v -> sendToQuarters());
    }

    private void trainSelected() {
        List<Integer> ids = adapter.getSelectedIds();
        if (ids.isEmpty()) {
            Toast.makeText(getContext(), "Select crew members to train.", Toast.LENGTH_SHORT).show();
            return;
        }

        Storage storage = Storage.getInstance();
        for (int id : ids) {
            CrewMember cm = storage.getCrewMember(id);
            if (cm != null) cm.train();
        }
        DataManager.save(requireContext());

        adapter.updateList(storage.getCrewInSimulator());
        Toast.makeText(getContext(),
                ids.size() + " crew member(s) trained! +1 XP each.",
                Toast.LENGTH_SHORT).show();
    }

    private void sendToQuarters() {
        List<Integer> ids = adapter.getSelectedIds();
        if (ids.isEmpty()) {
            Toast.makeText(getContext(), "Select crew members to send home.", Toast.LENGTH_SHORT).show();
            return;
        }

        Storage storage = Storage.getInstance();
        for (int id : ids) {
            CrewMember cm = storage.getCrewMember(id);
            if (cm != null) cm.sendToQuarters();
        }
        DataManager.save(requireContext());

        List<CrewMember> updated = storage.getCrewInSimulator();
        adapter.updateList(updated);
        updateEmptyState(updated);

        Toast.makeText(getContext(),
                ids.size() + " crew member(s) sent to Quarters. Energy restored.",
                Toast.LENGTH_SHORT).show();
    }

    private void updateEmptyState(List<CrewMember> crew) {
        tvEmpty.setVisibility(crew.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
