package com.example.spacecolony.model;


public abstract class CrewMember {
    // Fields
    private static int idCounter = 0;

    private final int id;
    private String name;
    private String specialization;
    private int skill;
    private int resilience;
    private int experience;
    private int energy;
    private final int maxEnergy;


    public static final String LOCATION_QUARTERS       = "Quarters";
    public static final String LOCATION_SIMULATOR      = "Simulator";
    public static final String LOCATION_MISSION_CONTROL = "MissionControl";
    public static final String LOCATION_MEDBAY         = "Medbay";

    private String location;


    private int missionsCompleted;
    private int missionsWon;
    private int trainingSessions;


    private int medbayRecoveryRounds;
    public static final int MEDBAY_RECOVERY_ROUNDS = 3;


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




    public abstract int getSpecialBonus(String missionType);


    public abstract String getColor();


    public int act(String missionType) {
        double randomBonus = Math.random() * 3; // Randomness bonus feature
        int effectiveSkill = skill + experience + getSpecialBonus(missionType);
        return Math.max(1, (int)(effectiveSkill + randomBonus));
    }


    public int defend(int incomingDamage) {
        int damageTaken = Math.max(0, incomingDamage - resilience);
        energy = Math.max(0, energy - damageTaken);
        return damageTaken;
    }


    public int defendBrace(int incomingDamage) {
        int damageTaken = Math.max(0, incomingDamage - (resilience * 2));
        energy = Math.max(0, energy - damageTaken);
        return damageTaken;
    }


    public int specialAbility(String missionType) {
        return (int)(act(missionType) * 1.5);
    }

    //Training


    public void train() {
        experience++;
        trainingSessions++;
    }

    //Location management

    public void sendToQuarters() {
        location = LOCATION_QUARTERS;
        energy   = maxEnergy;
    }

    public void sendToMedbay() {
        location              = LOCATION_MEDBAY;
        energy                = maxEnergy;
        experience            = 0;
        medbayRecoveryRounds  = MEDBAY_RECOVERY_ROUNDS;
        missionsCompleted++;   // counts as a completed (lost) mission
    }


    public boolean tickMedbay() {
        if (!location.equals(LOCATION_MEDBAY)) return false;
        medbayRecoveryRounds--;
        if (medbayRecoveryRounds <= 0) {
            location = LOCATION_QUARTERS;
            return true;
        }
        return false;
    }

    //Post-mission reward

    public void awardMissionXP(boolean won) {
        experience++;
        missionsCompleted++;
        if (won) missionsWon++;
    }

    //Getters

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

    public int getEffectiveSkill()        { return skill + experience; }

    public boolean isAlive()              { return energy > 0; }
    public boolean isInMedbay()           { return location.equals(LOCATION_MEDBAY); }

    //Setters (used by DataManager when restoring saved state)

    public void setLocation(String location)        { this.location = location; }
    public void setEnergy(int energy)               { this.energy = Math.max(0, Math.min(energy, maxEnergy)); }
    public void setExperience(int experience)       { this.experience = Math.max(0, experience); }
    public void setMedbayRecoveryRounds(int rounds) { this.medbayRecoveryRounds = rounds; }

    //Utility

    @Override
    public String toString() {
        return specialization + "(" + name + ")"
                + " skill:" + getEffectiveSkill()
                + " res:" + resilience
                + " exp:" + experience
                + " energy:" + energy + "/" + maxEnergy;
    }
}
