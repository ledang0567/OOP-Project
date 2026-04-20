package com.example.spacecolony.ui;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spacecolony.R;
import com.example.spacecolony.model.CrewMember;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CrewAdapter extends RecyclerView.Adapter<CrewAdapter.CrewViewHolder> {

    // Interfaces
    public interface OnCrewClickListener {
        void onCrewClick(CrewMember crewMember);
    }

    // Fields
    private List<CrewMember>       crewList;
    private final Set<Integer>     selectedIds;   // crew IDs currently checked
    private boolean                checkboxMode;  // show checkboxes?
    private OnCrewClickListener    clickListener;

    // Constructor
    public CrewAdapter(List<CrewMember> crewList, boolean checkboxMode) {
        this.crewList     = new ArrayList<>(crewList);
        this.selectedIds  = new HashSet<>();
        this.checkboxMode = checkboxMode;
    }

    // RecyclerView boilerplate

    @NonNull
    @Override
    public CrewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_crew_member, parent, false);
        return new CrewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CrewViewHolder holder, int position) {
        CrewMember cm = crewList.get(position);
        holder.bind(cm, selectedIds.contains(cm.getId()), checkboxMode, clickListener);
    }

    @Override
    public int getItemCount() { return crewList.size(); }

    // Data updates

    public void updateList(List<CrewMember> newList) {
        crewList = new ArrayList<>(newList);
        selectedIds.clear();
        notifyDataSetChanged();
    }

    // Selection

    public void setCheckboxMode(boolean enabled) {
        checkboxMode = enabled;
        selectedIds.clear();
        notifyDataSetChanged();
    }

    public List<Integer> getSelectedIds() {
        return new ArrayList<>(selectedIds);
    }

    public int getSelectedCount() { return selectedIds.size(); }

    public void clearSelection() {
        selectedIds.clear();
        notifyDataSetChanged();
    }

    // Listener

    public void setOnCrewClickListener(OnCrewClickListener listener) {
        this.clickListener = listener;
    }

    // ViewHolder

    class CrewViewHolder extends RecyclerView.ViewHolder {

        private final View        colorBar;
        private final TextView    tvName;
        private final TextView    tvSpec;
        private final TextView    tvStats;
        private final TextView    tvLocation;
        private final ProgressBar energyBar;
        private final TextView    tvEnergy;
        private final CheckBox    checkBox;

        CrewViewHolder(@NonNull View itemView) {
            super(itemView);
            colorBar   = itemView.findViewById(R.id.view_color_bar);
            tvName     = itemView.findViewById(R.id.tv_crew_name);
            tvSpec     = itemView.findViewById(R.id.tv_specialization);
            tvStats    = itemView.findViewById(R.id.tv_stats);
            tvLocation = itemView.findViewById(R.id.tv_location);
            energyBar  = itemView.findViewById(R.id.progress_energy);
            tvEnergy   = itemView.findViewById(R.id.tv_energy);
            checkBox   = itemView.findViewById(R.id.checkbox_select);
        }

        void bind(CrewMember cm, boolean isChecked, boolean showCheckbox,
                  OnCrewClickListener listener) {

            // Color bar on the left
            try {
                colorBar.setBackgroundColor(Color.parseColor(cm.getColor()));
            } catch (IllegalArgumentException e) {
                colorBar.setBackgroundColor(Color.GRAY);
            }

            // Text fields
            tvName.setText(cm.getName());
            tvSpec.setText(cm.getSpecialization());
            tvStats.setText(
                    "SKL " + cm.getEffectiveSkill() +
                            "  RES " + cm.getResilience() +
                            "  XP " + cm.getExperience()
            );

            // Location badge — show Medbay recovery rounds if applicable
            if (cm.isInMedbay()) {
                tvLocation.setText("Medbay (" + cm.getMedbayRecoveryRounds() + " missions)");
            } else {
                tvLocation.setText(cm.getLocation());
            }

            // Energy bar
            energyBar.setMax(cm.getMaxEnergy());
            energyBar.setProgress(cm.getEnergy());
            tvEnergy.setText(cm.getEnergy() + "/" + cm.getMaxEnergy());

            // Checkbox
            checkBox.setVisibility(showCheckbox ? View.VISIBLE : View.GONE);
            checkBox.setChecked(isChecked);
            checkBox.setOnCheckedChangeListener((btn, checked) -> {
                if (checked) {
                    selectedIds.add(cm.getId());
                } else {
                    selectedIds.remove(cm.getId());
                }
            });

            // Row click: toggle checkbox if in checkbox mode, else fire listener
            itemView.setOnClickListener(v -> {
                if (showCheckbox) {
                    checkBox.setChecked(!checkBox.isChecked());
                } else if (listener != null) {
                    listener.onCrewClick(cm);
                }
            });
        }
    }
}
