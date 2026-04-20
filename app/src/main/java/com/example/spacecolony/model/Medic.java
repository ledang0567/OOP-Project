package com.example.spacecolony.model;

public class Medic extends CrewMember {
    public static final int    DEFAULT_SKILL      = 7;
    public static final int    DEFAULT_RESILIENCE = 2;
    public static final int    DEFAULT_MAX_ENERGY = 18;
    public static final String COLOR              = "#2E7D32"; // green

    public Medic(String name) {
        super(name, "Medic", DEFAULT_SKILL, DEFAULT_RESILIENCE, DEFAULT_MAX_ENERGY);
    }

    public Medic(int id, String name, int skill, int resilience, int experience,
                 int energy, int maxEnergy, String location,
                 int missionsCompleted, int missionsWon, int trainingSessions) {
        super(id, name, "Medic", skill, resilience, experience, energy, maxEnergy,
                location, missionsCompleted, missionsWon, trainingSessions);
    }

    @Override
    public int getSpecialBonus(String missionType) {
        if (missionType == null) return 0;
        switch (missionType) {
            case "Alien Virus":
            case "Contamination":
                return 2;
            default:
                return 0;
        }
    }

    @Override
    public int specialAbility(String missionType) {
        return act(missionType); // damage is normal; MissionEngine adds the heal
    }

    public static final int MEDIC_HEAL_AMOUNT = 5;

    @Override
    public String getColor() {
        return COLOR;
    }
}
