package com.example.spacecolony.model;

/**
 * Abstract base class for all crew member specializations.
 * Subclasses must implement getSpecialBonus() and getColor().
 */
public abstract class CrewMember {
    // --- Fields ---
    private static int idCounter = 0;

    private final int id;
    private String name;
    private String specialization;
    private int skill;
    private int resilience;
    private int experience;
    private int energy;
    private final int maxEnergy;

    // Location constants
    public static final String LOCATION_QUARTERS       = "Quarters";
    public static final String LOCATION_SIMULATOR      = "Simulator";
    public static final String LOCATION_MISSION_CONTROL = "MissionControl";
    public static final String LOCATION_MEDBAY         = "Medbay";

    private String location;

    // Statistics (tracked per crew member for the bonus stats feature)
    private int missionsCompleted;
    private int missionsWon;
    private int trainingSessions;

    // Medbay recovery: rounds remaining before crew member returns to Quarters
    private int medbayRecoveryRounds;
    public static final int MEDBAY_RECOVERY_ROUNDS = 3;

    // --- Constructor ---
    public CrewMember(String name, String specialization, int skill, int resilience, int maxEnergy) {
        this.id             = ++idCounter;
        this.name           = name;
        this.specialization = specialization;
        this.skill          = skill;
        this.resilience     = resilience;
        this.maxEnergy      = maxEnergy;
        this.energy         = maxEnergy;
        this.experience     = 0;
        this.location       = LOCATION_QUARTERS;
        this.missionsCompleted  = 0;
        this.missionsWon        = 0;
        this.trainingSessions   = 0;
        this.medbayRecoveryRounds = 0;
    }

    // Constructor used when loading from saved data (preserves existing id)
    public CrewMember(int id, String name, String specialization,
                      int skill, int resilience, int experience,
                      int energy, int maxEnergy, String location,
                      int missionsCompleted, int missionsWon, int trainingSessions) {
        this.id                   = id;
        this.name                 = name;
        this.specialization       = specialization;
        this.skill                = skill;
        this.resilience           = resilience;
        this.experience           = experience;
        this.energy               = energy;
        this.maxEnergy            = maxEnergy;
        this.location             = location;
        this.missionsCompleted    = missionsCompleted;
        this.missionsWon          = missionsWon;
        this.trainingSessions     = trainingSessions;
        this.medbayRecoveryRounds = 0;
        // Keep idCounter ahead of any loaded id so new recruits never clash
        if (id >= idCounter) idCounter = id + 1;
    }

    // --- Abstract methods (subclasses must implement) ---

    /**
     * Returns a skill bonus when the given mission type matches this
     * specialization's area of expertise. Used for Specialization Bonuses.
     *
     * @param missionType like "Asteroid Field", "Fuel Leak", "Alien Attack"
     * @return bonus points to add to effective skill for this mission
     */
    public abstract int getSpecialBonus(String missionType);

    /**
     * Returns the color string associated with this specialization,
     * used by the UI to tint crew cards.
     * e.g. "#1565C0" for Pilot (blue)
     */
    public abstract String getColor();

    // --- Core combat methods ---

    /**
     * Calculates the damage this crew member deals in one turn.
     * Effective skill = base skill + experience + optional random bonus.
     *
     * @param missionType passed through so specialization bonus can apply
     * @return damage output (always >= 1)
     */
    public int act(String missionType) {
        double randomBonus = Math.random() * 3; // Randomness bonus feature
        int effectiveSkill = skill + experience + getSpecialBonus(missionType);
        return Math.max(1, (int)(effectiveSkill + randomBonus));
    }

    /**
     * Applies incoming damage reduced by resilience. Energy cannot go below 0.
     *
     * @param incomingDamage raw damage from the threat
     * @return actual damage taken (after resilience reduction)
     */
    public int defend(int incomingDamage) {
        int damageTaken = Math.max(0, incomingDamage - resilience);
        energy = Math.max(0, energy - damageTaken);
        return damageTaken;
    }

    /**
     * Defend variant for Tactical Combat: crew member braces,
     * doubling resilience for this hit only.
     */
    public int defendBrace(int incomingDamage) {
        int damageTaken = Math.max(0, incomingDamage - (resilience * 2));
        energy = Math.max(0, energy - damageTaken);
        return damageTaken;
    }

    /**
     * Special ability attack: deals 150% of normal act() damage.
     * Subclasses can override for unique specials.
     */
    public int specialAbility(String missionType) {
        return (int)(act(missionType) * 1.5);
    }

    // --- Training ---

    /** Awards one experience point and records the training session. */
    public void train() {
        experience++;
        trainingSessions++;
    }

    // --- Location management ---

    /** Moves crew member to Quarters and fully restores energy. */
    public void sendToQuarters() {
        location = LOCATION_QUARTERS;
        energy   = maxEnergy;
    }

    /** Moves crew member to Medbay with reset stats. */
    public void sendToMedbay() {
        location              = LOCATION_MEDBAY;
        energy                = maxEnergy;
        experience            = 0;
        medbayRecoveryRounds  = MEDBAY_RECOVERY_ROUNDS;
        missionsCompleted++;   // counts as a completed (lost) mission
    }

    /**
     * Called each time a mission completes. Decrements medbay counter
     * and auto-returns to Quarters when recovery is done.
     *
     * @return true if the crew member just left Medbay
     */
    public boolean tickMedbay() {
        if (!location.equals(LOCATION_MEDBAY)) return false;
        medbayRecoveryRounds--;
        if (medbayRecoveryRounds <= 0) {
            location = LOCATION_QUARTERS;
            return true;
        }
        return false;
    }

    // --- Post-mission reward ---

    /** Called on surviving crew members after a successful mission. */
    public void awardMissionXP(boolean won) {
        experience++;
        missionsCompleted++;
        if (won) missionsWon++;
    }

    // --- Getters ---

    public int    getId()                 { return id; }
    public String getName()               { return name; }
    public String getSpecialization()     { return specialization; }
    public int    getSkill()              { return skill; }
    public int    getResilience()         { return resilience; }
    public int    getExperience()         { return experience; }
    public int    getEnergy()             { return energy; }
    public int    getMaxEnergy()          { return maxEnergy; }
    public String getLocation()           { return location; }
    public int    getMissionsCompleted()  { return missionsCompleted; }
    public int    getMissionsWon()        { return missionsWon; }
    public int    getTrainingSessions()   { return trainingSessions; }
    public int    getMedbayRecoveryRounds() { return medbayRecoveryRounds; }

    /** Effective skill shown in the UI: includes XP but not random/mission bonuses. */
    public int getEffectiveSkill()        { return skill + experience; }

    public boolean isAlive()              { return energy > 0; }
    public boolean isInMedbay()           { return location.equals(LOCATION_MEDBAY); }

    // --- Setters (used by DataManager when restoring saved state) ---

    public void setLocation(String location)        { this.location = location; }
    public void setEnergy(int energy)               { this.energy = Math.max(0, Math.min(energy, maxEnergy)); }
    public void setExperience(int experience)       { this.experience = Math.max(0, experience); }
    public void setMedbayRecoveryRounds(int rounds) { this.medbayRecoveryRounds = rounds; }

    // --- Utility ---

    @Override
    public String toString() {
        return specialization + "(" + name + ")"
                + " skill:" + getEffectiveSkill()
                + " res:" + resilience
                + " exp:" + experience
                + " energy:" + energy + "/" + maxEnergy;
    }
}
