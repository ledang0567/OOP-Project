package com.example.spacecolony.model;

public class Scientist extends CrewMember {
    public static final int    DEFAULT_SKILL      = 8;
    public static final int    DEFAULT_RESILIENCE = 1;
    public static final int    DEFAULT_MAX_ENERGY = 17;
    public static final String COLOR              = "#6A1B9A"; //purple

    public Scientist(String name) {
        super(name, "Scientist", DEFAULT_SKILL, DEFAULT_RESILIENCE, DEFAULT_MAX_ENERGY);
    }

    public Scientist(int id, String name, int skill, int resilience, int experience,
                     int energy, int maxEnergy, String location,
                     int missionsCompleted, int missionsWon, int trainingSessions) {
        super(id, name, "Scientist", skill, resilience, experience, energy, maxEnergy,
                location, missionsCompleted, missionsWon, trainingSessions);
    }

    @Override
    public int getSpecialBonus(String missionType) {
        if (missionType == null) return 0;
        switch (missionType) {
            case "Solar Flare":
            case "Alien Virus":
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
