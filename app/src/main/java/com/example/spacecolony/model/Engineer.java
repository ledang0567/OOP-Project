package com.example.spacecolony.model;

public class Engineer extends CrewMember {
    public static final int    DEFAULT_SKILL      = 6;
    public static final int    DEFAULT_RESILIENCE = 3;
    public static final int    DEFAULT_MAX_ENERGY = 19;
    public static final String COLOR              = "#F9A825"; // yellow

    public Engineer(String name) {
        super(name, "Engineer", DEFAULT_SKILL, DEFAULT_RESILIENCE, DEFAULT_MAX_ENERGY);
    }

    public Engineer(int id, String name, int skill, int resilience, int experience,
                    int energy, int maxEnergy, String location,
                    int missionsCompleted, int missionsWon, int trainingSessions) {
        super(id, name, "Engineer", skill, resilience, experience, energy, maxEnergy,
                location, missionsCompleted, missionsWon, trainingSessions);
    }

    /**
     * Engineers get a bonus on mechanical/systems failure missions.
     */
    @Override
    public int getSpecialBonus(String missionType) {
        if (missionType == null) return 0;
        switch (missionType) {
            case "Fuel Leak":
            case "Reactor Meltdown":
            case "Broken Heating":
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
