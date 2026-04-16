package com.example.spacecolony.model;

import java.util.Random;

/**
 * Represents a system-generated threat that the crew fights on a mission.
 * Stats scale with the number of completed missions so difficulty increases over time.
 */
public class Threat {

    // All possible threat types — each maps to a specialization bonus in subclasses
    public static final String[] THREAT_TYPES = {
            "Asteroid Field",
            "Fuel Leak",
            "Reactor Meltdown",
            "Alien Attack",
            "Solar Flare",
            "Alien Virus",
            "Contamination",
            "Broken Heating",
            "Pirate Boarding",
            "Emergency Escape"
    };

    private final String name;
    private final String type;       // one of THREAT_TYPES, used for specialization bonuses
    private final int    skill;
    private final int    resilience;
    private int          energy;
    private final int    maxEnergy;

    /**
     * Factory method: generates a threat scaled to the current mission count.
     *
     * @param completedMissions total missions finished so far (drives scaling)
     * @return a new Threat instance
     */
    public static Threat generate(int completedMissions) {
        Random rng = new Random();

        // Pick a random type
        String type = THREAT_TYPES[rng.nextInt(THREAT_TYPES.length)];

        // Scaling formula: base + missions played
        int skill      = 4  + completedMissions;
        int resilience = 2  + (completedMissions / 3);
        int maxEnergy  = 20 + (completedMissions * 3);

        // Build a flavour name for this threat type
        String name = buildName(type, rng);

        return new Threat(name, type, skill, resilience, maxEnergy);
    }

    private Threat(String name, String type, int skill, int resilience, int maxEnergy) {
        this.name       = name;
        this.type       = type;
        this.skill      = skill;
        this.resilience = resilience;
        this.maxEnergy  = maxEnergy;
        this.energy     = maxEnergy;
    }

    // --- Combat ---

    /**
     * Threat attacks a crew member.
     *
     * @param target the crew member being attacked
     * @return damage dealt (after target's resilience)
     */
    public int attack(CrewMember target) {
        double randomBonus = Math.random() * 3;
        int damage = Math.max(1, (int)(skill + randomBonus));
        return target.defend(damage);
    }

    /**
     * Threat takes damage from a crew member's action.
     *
     * @param incomingDamage raw damage from crew member's act()
     * @return actual damage taken (after resilience)
     */
    public int takeDamage(int incomingDamage) {
        int damageTaken = Math.max(0, incomingDamage - resilience);
        energy = Math.max(0, energy - damageTaken);
        return damageTaken;
    }

    public boolean isDefeated() { return energy <= 0; }

    // --- Getters ---

    public String getName()      { return name; }
    public String getType()      { return type; }
    public int    getSkill()     { return skill; }
    public int    getResilience(){ return resilience; }
    public int    getEnergy()    { return energy; }
    public int    getMaxEnergy() { return maxEnergy; }

    // --- Helpers ---

    private static String buildName(String type, Random rng) {
        switch (type) {
            case "Asteroid Field":   return pick(new String[]{"Asteroid Storm","Meteorite Swarm","Rock Barrage"}, rng);
            case "Fuel Leak":        return pick(new String[]{"Critical Fuel Leak","Plasma Blowout","Engine Rupture"}, rng);
            case "Reactor Meltdown": return pick(new String[]{"Core Overload","Reactor Surge","Meltdown Warning"}, rng);
            case "Alien Attack":     return pick(new String[]{"Xeno Assault","Alien Raiders","Unknown Hostiles"}, rng);
            case "Solar Flare":      return pick(new String[]{"Solar Flare Surge","Radiation Burst","CME Impact"}, rng);
            case "Alien Virus":      return pick(new String[]{"Bio-Contaminant","Xeno-Pathogen","Mutant Spore"}, rng);
            case "Contamination":    return pick(new String[]{"Toxic Leak","Chemical Spill","Air Contamination"}, rng);
            case "Broken Heating":   return pick(new String[]{"Heating Collapse","Thermal Failure","Cryo-Breach"}, rng);
            case "Pirate Boarding":  return pick(new String[]{"Pirate Raid","Boarding Party","Space Brigands"}, rng);
            case "Emergency Escape": return pick(new String[]{"Collision Course","Debris Field","Hull Breach"}, rng);
            default:                 return "Unknown Threat";
        }
    }

    private static String pick(String[] options, Random rng) {
        return options[rng.nextInt(options.length)];
    }

    @Override
    public String toString() {
        return name + " [" + type + "]"
                + " skill:" + skill
                + " res:" + resilience
                + " energy:" + energy + "/" + maxEnergy;
    }
}
