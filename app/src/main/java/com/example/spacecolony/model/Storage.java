package com.example.spacecolony.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Central storage for all crew members.
 * Implemented as a singleton so every part of the app shares the same data.
 *
 * Internally uses HashMap<Integer, CrewMember> as suggested in the spec.
 */
public class Storage {

    // --- Singleton ---
    private static Storage instance;

    public static Storage getInstance() {
        if (instance == null) instance = new Storage("Colony Alpha");
        return instance;
    }

    /** Used by DataManager to replace the instance after loading saved data. */
    public static void setInstance(Storage storage) {
        instance = storage;
    }

    // --- Fields ---
    private String name;
    private final HashMap<Integer, CrewMember> crewMap;
    private int completedMissions; // used for threat scaling

    // --- Constructor ---
    public Storage(String name) {
        this.name             = name;
        this.crewMap          = new HashMap<>();
        this.completedMissions = 0;
    }

    // --- Crew management ---

    public void addCrewMember(CrewMember cm) {
        crewMap.put(cm.getId(), cm);
    }

    public CrewMember getCrewMember(int id) {
        return crewMap.get(id);
    }

    public void removeCrewMember(int id) {
        crewMap.remove(id);
    }

    /** Returns all crew members regardless of location. */
    public List<CrewMember> getAllCrew() {
        return new ArrayList<>(crewMap.values());
    }

    /** Returns crew members at a specific location. */
    public List<CrewMember> getCrewAt(String location) {
        List<CrewMember> result = new ArrayList<>();
        for (CrewMember cm : crewMap.values()) {
            if (cm.getLocation().equals(location)) result.add(cm);
        }
        return result;
    }

    public List<CrewMember> getCrewInQuarters() {
        return getCrewAt(CrewMember.LOCATION_QUARTERS);
    }

    public List<CrewMember> getCrewInSimulator() {
        return getCrewAt(CrewMember.LOCATION_SIMULATOR);
    }

    public List<CrewMember> getCrewInMissionControl() {
        return getCrewAt(CrewMember.LOCATION_MISSION_CONTROL);
    }

    public List<CrewMember> getCrewInMedbay() {
        return getCrewAt(CrewMember.LOCATION_MEDBAY);
    }

    // --- Mission counter ---

    public int getCompletedMissions()       { return completedMissions; }
    public void incrementCompletedMissions(){ completedMissions++; }
    public void setCompletedMissions(int n) { completedMissions = n; }

    // --- Colony name ---

    public String getName()              { return name; }
    public void   setName(String name)   { this.name = name; }

    // --- Raw map access (used by DataManager for serialization) ---

    public Map<Integer, CrewMember> getCrewMap() { return crewMap; }

    // --- Summary helpers for the home screen ---

    public int countAt(String location) {
        return getCrewAt(location).size();
    }

    public int totalCrew() {
        return crewMap.size();
    }
}
