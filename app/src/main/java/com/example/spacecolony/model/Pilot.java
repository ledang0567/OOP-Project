package com.example.spacecolony.model;

public class Pilot extends CrewMember {
    public static final int    DEFAULT_SKILL      = 5;
    public static final int    DEFAULT_RESILIENCE = 4;
    public static final int    DEFAULT_MAX_ENERGY = 20;
    public static final String COLOR              = "#1565C0"; // blue

    public Pilot(String name) {
        super(name, "Pilot", DEFAULT_SKILL, DEFAULT_RESILIENCE, DEFAULT_MAX_ENERGY);
    }

    public Pilot(int id, String name, int skill, int resilience, int experience,
                 int energy, int maxEnergy, String location,
                 int missionsCompleted, int missionsWon, int trainingSessions) {
        super(id, name, "Pilot", skill, resilience, experience, energy, maxEnergy,
                location, missionsCompleted, missionsWon, trainingSessions);
    }

    @Override
    public int getSpecialBonus(String missionType) {
        if (missionType == null) return 0;
        switch (missionType) {
            case "Asteroid Field":
            case "Emergency Escape":
                return 2;
            default:
                return 0;
        }
    }

    @Override
    public String getColor() {
        return COLOR;
    }
}
