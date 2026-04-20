package com.example.spacecolony.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
import com.example.spacecolony.model.MissionEngine;
import com.example.spacecolony.model.Storage;
import com.example.spacecolony.model.Threat;
import com.example.spacecolony.ui.CrewAdapter;

import java.util.ArrayList;
import java.util.List;

public class MissionFragment extends Fragment {
    // Views
    private CrewAdapter   squadAdapter;
    private TextView      tvEmpty;
    private TextView      tvThreatInfo;
    private TextView      tvMissionLog;
    private ScrollView    scrollLog;
    private LinearLayout  layoutTactical;      // attack / defend / special buttons
    private LinearLayout  layoutLaunch;        // launch button
    private LinearLayout  layoutPostMission;   // return-to-quarters panel after mission ends
    private Button        btnLaunch;
    private Button        btnAutoTurn;
    private TextView      tvCurrentActor;
    private TextView      tvPostMissionResult;

    // State
    private MissionEngine engine;
    private Threat        pendingThreat;    // generated when squad is valid
    private List<Integer> lastSquadIds;     // ids of crew who just finished a mission

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mission, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvEmpty           = view.findViewById(R.id.tv_empty_mission);
        tvThreatInfo      = view.findViewById(R.id.tv_threat_info);
        tvMissionLog      = view.findViewById(R.id.tv_mission_log);
        scrollLog         = view.findViewById(R.id.scroll_log);
        layoutTactical    = view.findViewById(R.id.layout_tactical);
        layoutLaunch      = view.findViewById(R.id.layout_launch);
        layoutPostMission = view.findViewById(R.id.layout_post_mission);
        tvPostMissionResult = view.findViewById(R.id.tv_post_mission_result);
        btnLaunch         = view.findViewById(R.id.btn_launch_mission);
        btnAutoTurn       = view.findViewById(R.id.btn_auto_turn);
        tvCurrentActor    = view.findViewById(R.id.tv_current_actor);

        // Squad selection list
        RecyclerView recyclerSquad = view.findViewById(R.id.recycler_squad);
        recyclerSquad.setLayoutManager(new LinearLayoutManager(getContext()));
        List<CrewMember> available = Storage.getInstance().getCrewInMissionControl();
        squadAdapter = new CrewAdapter(available, true);
        recyclerSquad.setAdapter(squadAdapter);
        updateEmptyState(available);

        btnLaunch.setOnClickListener(v -> launchMission());
        btnAutoTurn.setOnClickListener(v -> autoTurn());

        // Tactical buttons
        view.findViewById(R.id.btn_attack).setOnClickListener(v ->
                executeTurn(MissionEngine.Action.ATTACK));
        view.findViewById(R.id.btn_defend).setOnClickListener(v ->
                executeTurn(MissionEngine.Action.DEFEND));
        view.findViewById(R.id.btn_special).setOnClickListener(v ->
                executeTurn(MissionEngine.Action.SPECIAL));

        // Post-mission buttons
        view.findViewById(R.id.btn_return_quarters).setOnClickListener(v ->
                returnSurvivorsToQuarters());
        view.findViewById(R.id.btn_stay_mission).setOnClickListener(v ->
                dismissPostMissionPanel());

        // Initially hide combat and post-mission UI
        layoutTactical.setVisibility(View.GONE);
        layoutPostMission.setVisibility(View.GONE);
        btnAutoTurn.setVisibility(View.GONE);
        tvCurrentActor.setVisibility(View.GONE);
    }

    // Mission launch

    private void launchMission() {
        List<Integer> ids = squadAdapter.getSelectedIds();
        if (ids.size() < 2 || ids.size() > 3) {
            Toast.makeText(getContext(), "Select 2 or 3 crew members.", Toast.LENGTH_SHORT).show();
            return;
        }

        Storage storage = Storage.getInstance();
        List<CrewMember> squad = new ArrayList<>();
        lastSquadIds = new ArrayList<>(ids);   // remember for post-mission return
        for (int id : ids) {
            CrewMember cm = storage.getCrewMember(id);
            if (cm != null) squad.add(cm);
        }

        // Generate scaled threat
        pendingThreat = Threat.generate(storage.getCompletedMissions());
        engine        = new MissionEngine(squad, pendingThreat);

        // Show threat info
        tvThreatInfo.setText(
                "⚠ " + pendingThreat.getName() +
                        " [" + pendingThreat.getType() + "]" +
                        "\nSKL " + pendingThreat.getSkill() +
                        "  RES " + pendingThreat.getResilience() +
                        "  HP " + pendingThreat.getMaxEnergy());

        // Show initial log header
        tvMissionLog.setText(engine.getFullLog());

        // Swap UI to combat mode
        layoutLaunch.setVisibility(View.GONE);
        layoutTactical.setVisibility(View.VISIBLE);
        btnAutoTurn.setVisibility(View.VISIBLE);
        tvCurrentActor.setVisibility(View.VISIBLE);
        updateActorLabel();

        DataManager.save(requireContext());
    }

    // Tactical combat

    private void executeTurn(MissionEngine.Action action) {
        if (engine == null || engine.isOver()) return;

        MissionEngine.TurnResult result = engine.takeTurn(action);
        appendLog(result.log);

        if (result.threatDefeated || result.missionFailed) {
            endMission(result.threatDefeated);
        } else {
            updateActorLabel();
        }
    }

    private void autoTurn() {
        if (engine == null || engine.isOver()) return;

        MissionEngine.TurnResult result = engine.runAutoTurn();
        appendLog(result.log);

        if (result.threatDefeated || result.missionFailed) {
            endMission(result.threatDefeated);
        } else {
            updateActorLabel();
        }
    }

    private void endMission(boolean victory) {
        // Hide combat controls
        layoutTactical.setVisibility(View.GONE);
        btnAutoTurn.setVisibility(View.GONE);
        tvCurrentActor.setVisibility(View.GONE);

        DataManager.save(requireContext());

        // Refresh squad list (some crew may now be in Medbay)
        List<CrewMember> updated = Storage.getInstance().getCrewInMissionControl();
        squadAdapter.updateList(updated);
        updateEmptyState(updated);

        // Show post-mission panel with result message and return options
        String resultMsg = victory
                ? "Mission complete! Your crew survived."
                : "Mission failed. Survivors are still in Mission Control.";
        tvPostMissionResult.setText(resultMsg);
        layoutPostMission.setVisibility(View.VISIBLE);

        engine = null;
    }

    private void returnSurvivorsToQuarters() {
        Storage storage = Storage.getInstance();
        int count = 0;

        if (lastSquadIds != null) {
            for (int id : lastSquadIds) {
                CrewMember cm = storage.getCrewMember(id);
                // Only move crew that are still in Mission Control (not in Medbay)
                if (cm != null && cm.getLocation().equals(CrewMember.LOCATION_MISSION_CONTROL)) {
                    cm.sendToQuarters();  // restores full energy, keeps XP
                    count++;
                }
            }
        }

        DataManager.save(requireContext());

        // Refresh list and hide post-mission panel
        List<CrewMember> updated = storage.getCrewInMissionControl();
        squadAdapter.updateList(updated);
        updateEmptyState(updated);
        layoutPostMission.setVisibility(View.GONE);
        layoutLaunch.setVisibility(View.VISIBLE);

        Toast.makeText(getContext(),
                count + " crew member(s) returned to Quarters. Energy fully restored!",
                Toast.LENGTH_SHORT).show();
    }

    private void dismissPostMissionPanel() {
        layoutPostMission.setVisibility(View.GONE);
        layoutLaunch.setVisibility(View.VISIBLE);

        List<CrewMember> updated = Storage.getInstance().getCrewInMissionControl();
        squadAdapter.updateList(updated);
        updateEmptyState(updated);
    }

    // Helpers

    private void updateActorLabel() {
        if (engine == null) return;
        CrewMember actor = engine.getCurrentActor();
        if (actor != null) {
            tvCurrentActor.setText("Current turn: " + actor.getSpecialization()
                    + "(" + actor.getName() + ")"
                    + "  HP " + actor.getEnergy() + "/" + actor.getMaxEnergy());
        }
    }

    private void appendLog(String text) {
        tvMissionLog.append(text);
        // Auto-scroll to bottom
        scrollLog.post(() -> scrollLog.fullScroll(View.FOCUS_DOWN));
    }

    private void updateEmptyState(List<CrewMember> crew) {
        tvEmpty.setVisibility(crew.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
