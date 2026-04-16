package com.example.spacecolony.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.spacecolony.MainActivity;
import com.example.spacecolony.R;
import com.example.spacecolony.data.DataManager;
import com.example.spacecolony.model.CrewMember;
import com.example.spacecolony.model.Engineer;
import com.example.spacecolony.model.Medic;
import com.example.spacecolony.model.Pilot;
import com.example.spacecolony.model.Scientist;
import com.example.spacecolony.model.Soldier;
import com.example.spacecolony.model.Storage;

public class RecruitFragment extends Fragment {
    private EditText   etName;
    private RadioGroup rgSpecialization;
    private TextView   tvStatPreview;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recruit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etName           = view.findViewById(R.id.et_crew_name);
        rgSpecialization = view.findViewById(R.id.rg_specialization);
        tvStatPreview    = view.findViewById(R.id.tv_stat_preview);

        // Update stat preview whenever selection changes
        rgSpecialization.setOnCheckedChangeListener((group, checkedId) ->
                updateStatPreview(checkedId));
        updateStatPreview(rgSpecialization.getCheckedRadioButtonId());

        Button btnCreate = view.findViewById(R.id.btn_create);
        Button btnCancel = view.findViewById(R.id.btn_cancel);

        btnCreate.setOnClickListener(v -> createCrewMember());
        btnCancel.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());
    }

    private void updateStatPreview(int checkedId) {
        String preview;
        if      (checkedId == R.id.rb_pilot)
            preview = "SKL " + Pilot.DEFAULT_SKILL + "  RES " + Pilot.DEFAULT_RESILIENCE + "  HP " + Pilot.DEFAULT_MAX_ENERGY;
        else if (checkedId == R.id.rb_engineer)
            preview = "SKL " + Engineer.DEFAULT_SKILL + "  RES " + Engineer.DEFAULT_RESILIENCE + "  HP " + Engineer.DEFAULT_MAX_ENERGY;
        else if (checkedId == R.id.rb_medic)
            preview = "SKL " + Medic.DEFAULT_SKILL + "  RES " + Medic.DEFAULT_RESILIENCE + "  HP " + Medic.DEFAULT_MAX_ENERGY;
        else if (checkedId == R.id.rb_scientist)
            preview = "SKL " + Scientist.DEFAULT_SKILL + "  RES " + Scientist.DEFAULT_RESILIENCE + "  HP " + Scientist.DEFAULT_MAX_ENERGY;
        else
            preview = "SKL " + Soldier.DEFAULT_SKILL + "  RES " + Soldier.DEFAULT_RESILIENCE + "  HP " + Soldier.DEFAULT_MAX_ENERGY;
        tvStatPreview.setText(preview);
    }

    private void createCrewMember() {
        String name = etName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a name.", Toast.LENGTH_SHORT).show();
            return;
        }

        int checkedId = rgSpecialization.getCheckedRadioButtonId();
        CrewMember cm;

        if      (checkedId == R.id.rb_pilot)     cm = new Pilot(name);
        else if (checkedId == R.id.rb_engineer)  cm = new Engineer(name);
        else if (checkedId == R.id.rb_medic)     cm = new Medic(name);
        else if (checkedId == R.id.rb_scientist) cm = new Scientist(name);
        else                                      cm = new Soldier(name);

        Storage.getInstance().addCrewMember(cm);
        DataManager.save(requireContext());

        Toast.makeText(getContext(),
                cm.getSpecialization() + " " + name + " recruited!",
                Toast.LENGTH_SHORT).show();

        // Navigate back to home
        ((MainActivity) requireActivity()).showFragment(new HomeFragment());
    }
}
