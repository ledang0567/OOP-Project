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

import java.util.List;

public class MedbayFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_medbay, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<CrewMember> medbay = Storage.getInstance().getCrewInMedbay();

        TextView tvEmpty = view.findViewById(R.id.tv_empty_medbay);
        tvEmpty.setVisibility(medbay.isEmpty() ? View.VISIBLE : View.GONE);

        RecyclerView recycler = view.findViewById(R.id.recycler_medbay);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        // Read-only list — no checkboxes needed
        recycler.setAdapter(new CrewAdapter(medbay, false));
    }
}
