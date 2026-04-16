package com.example.spacecolony.model;

public class Soldier extends CrewMember {
    public static final int    DEFAULT_SKILL      = 9;
    public static final int    DEFAULT_RESILIENCE = 0;
    public static final int    DEFAULT_MAX_ENERGY = 16;
    public static final String COLOR              = "#B71C1C"; // red

    public Soldier(String name) {
        super(name, "Soldier", DEFAULT_SKILL, DEFAULT_RESILIENCE, DEFAULT_MAX_ENERGY);
    }

    public Soldier(int id, String name, int skill, int resilience, int experience,
                   int energy, int maxEnergy, String location,
                   int missionsCompleted, int missionsWon, int trainingSessions) {
        super(id, name, "Soldier", skill, resilience, experience, energy, maxEnergy,
                location, missionsCompleted, missionsWon, trainingSessions);
    }

    /**
     * Soldiers excel in direct combat situations.
     */
    @Override
    public int getSpecialBonus(String missionType) {
        if (missionType == null) return 0;
        switch (missionType) {
            case "Alien Attack":
            case "Pirate Boarding":
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
