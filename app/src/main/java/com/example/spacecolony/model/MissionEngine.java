package com.example.spacecolony.model;

import java.util.ArrayList;
import java.util.List;

public class MissionEngine {
    //Action enum (Tactical Combat)
    public enum Action { ATTACK, DEFEND, SPECIAL }

    //Turn result (returned after each call to takeTurn / runAutoTurn)
    public static class TurnResult {
        public final String  actingCrewName;
        public final Action  actionTaken;
        public final int     damageDealt;       // crew → threat
        public final int     damageTaken;       // threat → crew
        public final boolean crewMemberFell;    // energy hit 0
        public final boolean threatDefeated;
        public final boolean missionFailed;     // all crew fell
        public final String  log;               // human-readable summary line

        public TurnResult(String actingCrewName, Action actionTaken,
                          int damageDealt, int damageTaken,
                          boolean crewMemberFell, boolean threatDefeated,
                          boolean missionFailed, String log) {
            this.actingCrewName  = actingCrewName;
            this.actionTaken     = actionTaken;
            this.damageDealt     = damageDealt;
            this.damageTaken     = damageTaken;
            this.crewMemberFell  = crewMemberFell;
            this.threatDefeated  = threatDefeated;
            this.missionFailed   = missionFailed;
            this.log             = log;
        }
    }

    // Fields
    private final List<CrewMember> activeCrew;   // crew still fighting
    private final Threat           threat;
    private final String           missionType;  // for specialization bonuses
    private int                    currentIndex; // whose turn it is
    private boolean                over;
    private boolean                won;

    private final StringBuilder    fullLog;      // accumulated battle log

    // Constructor
    public MissionEngine(List<CrewMember> crew, Threat threat) {
        this.activeCrew   = new ArrayList<>(crew);
        this.threat       = threat;
        this.missionType  = threat.getType();
        this.currentIndex = 0;
        this.over         = false;
        this.won          = false;
        this.fullLog      = new StringBuilder();

        // Header
        fullLog.append("=== MISSION: ").append(threat.getName()).append(" ===\n");
        fullLog.append(threat.toString()).append("\n");
        for (CrewMember cm : activeCrew) fullLog.append(cm.toString()).append("\n");
        fullLog.append("\n");
    }

    // Public API

    public TurnResult takeTurn(Action action) {
        if (over) throw new IllegalStateException("Mission is already over.");

        CrewMember actor = activeCrew.get(currentIndex);
        StringBuilder turnLog = new StringBuilder();

        // Crew member acts
        int damageDealt = 0;
        String actionLabel;

        switch (action) {
            case DEFEND:
                // Crew member braces — skip dealing damage but doubles resilience for the retaliation hit
                actionLabel = "defends (bracing)";
                damageDealt = 0;
                turnLog.append(actor.getSpecialization()).append("(").append(actor.getName())
                        .append(") braces — no attack this turn.\n");
                break;

            case SPECIAL:
                actionLabel = "uses special ability";
                damageDealt = actor.specialAbility(missionType);
                int actualSpecial = threat.takeDamage(damageDealt);
                turnLog.append(actor.getSpecialization()).append("(").append(actor.getName())
                        .append(") uses special! Damage dealt: ").append(damageDealt)
                        .append(" - ").append(threat.getResilience())
                        .append(" = ").append(actualSpecial).append("\n");
                turnLog.append("  ").append(threat.getName())
                        .append(" energy: ").append(threat.getEnergy())
                        .append("/").append(threat.getMaxEnergy()).append("\n");

                // Medic special: also heal the next crew member
                if (actor instanceof Medic && activeCrew.size() > 1) {
                    int healTarget = (currentIndex + 1) % activeCrew.size();
                    CrewMember ally = activeCrew.get(healTarget);
                    int healed = Math.min(Medic.MEDIC_HEAL_AMOUNT,
                            ally.getMaxEnergy() - ally.getEnergy());
                    ally.setEnergy(ally.getEnergy() + healed);
                    turnLog.append("  Medic heals ").append(ally.getName())
                            .append(" for ").append(healed).append(" energy.\n");
                }
                break;

            default: // ATTACK
                actionLabel = "attacks";
                damageDealt = actor.act(missionType);
                int actualDamage = threat.takeDamage(damageDealt);
                turnLog.append(actor.getSpecialization()).append("(").append(actor.getName())
                        .append(") attacks ").append(threat.getName()).append("\n");
                turnLog.append("  Damage dealt: ").append(damageDealt)
                        .append(" - ").append(threat.getResilience())
                        .append(" = ").append(actualDamage).append("\n");
                turnLog.append("  ").append(threat.getName())
                        .append(" energy: ").append(threat.getEnergy())
                        .append("/").append(threat.getMaxEnergy()).append("\n");
                break;
        }

        //2.Check if threat is defeated
        if (threat.isDefeated()) {
            turnLog.append("\n=== MISSION COMPLETE ===\n");
            turnLog.append("The ").append(threat.getName()).append(" has been neutralized!\n");
            endMission(true, turnLog);
            fullLog.append(turnLog);
            return new TurnResult(actor.getName(), action, damageDealt, 0,
                    false, true, false, turnLog.toString());
        }

        // 3.Threat retaliates
        int damageTaken;
        if (action == Action.DEFEND) {
            damageTaken = actor.defendBrace(threat.getSkill()); // doubled resilience
        } else {
            damageTaken = threat.attack(actor);
        }

        turnLog.append(threat.getName()).append(" retaliates against ")
                .append(actor.getSpecialization()).append("(").append(actor.getName()).append(")\n");
        turnLog.append("  Damage dealt: ").append(threat.getSkill())
                .append(" - ").append(actor.getResilience())
                .append(" = ").append(damageTaken).append("\n");
        turnLog.append("  ").append(actor.getSpecialization()).append("(").append(actor.getName())
                .append(") energy: ").append(actor.getEnergy())
                .append("/").append(actor.getMaxEnergy()).append("\n");

        // 4.Check if actor fell
        boolean crewMemberFell = !actor.isAlive();
        if (crewMemberFell) {
            turnLog.append("  ").append(actor.getName())
                    .append(" has been incapacitated! Sending to Medbay.\n");
            actor.sendToMedbay();               // No Death bonus
            activeCrew.remove(currentIndex);
            if (currentIndex >= activeCrew.size()) currentIndex = 0;
        } else {
            // Advance to next crew member
            currentIndex = (currentIndex + 1) % activeCrew.size();
        }

        // 5.Check if all crew fell
        boolean missionFailed = activeCrew.isEmpty();
        if (missionFailed) {
            turnLog.append("\n=== MISSION FAILED ===\n");
            turnLog.append("All crew members have been incapacitated.\n");
            endMission(false, turnLog);
        }

        fullLog.append(turnLog);

        return new TurnResult(actor.getName(), action, damageDealt, damageTaken,
                crewMemberFell, false, missionFailed, turnLog.toString());
    }


    public TurnResult runAutoTurn() {
        if (over) throw new IllegalStateException("Mission is already over.");
        CrewMember actor = activeCrew.get(currentIndex);
        Action action;
        if (actor.getEnergy() < actor.getMaxEnergy() * 0.3) {
            action = Action.DEFEND;
        } else if (actor instanceof Medic) {
            // Medics use special when an ally is below half health
            boolean allyHurt = activeCrew.stream()
                    .anyMatch(c -> c != actor && c.getEnergy() < c.getMaxEnergy() / 2);
            action = allyHurt ? Action.SPECIAL : Action.ATTACK;
        } else {
            action = Action.ATTACK;
        }
        return takeTurn(action);
    }

    // Outcome

    private void endMission(boolean victory, StringBuilder log) {
        over = true;
        won  = victory;
        Storage storage = Storage.getInstance();
        storage.incrementCompletedMissions();

        if (victory) {
            for (CrewMember cm : activeCrew) {
                cm.awardMissionXP(true);
                log.append(cm.getSpecialization()).append("(").append(cm.getName())
                        .append(") gains 1 experience point. (exp: ").append(cm.getExperience()).append(")\n");
            }
        }

        // Tick medbay recovery for any crew members currently recovering
        for (CrewMember cm : storage.getAllCrew()) {
            if (cm.isInMedbay()) {
                boolean recovered = cm.tickMedbay();
                if (recovered) {
                    log.append(cm.getName()).append(" has recovered and returned to Quarters.\n");
                }
            }
        }
    }

    // Getters

    public boolean        isOver()        { return over; }
    public boolean        isWon()         { return won; }
    public Threat         getThreat()     { return threat; }
    public List<CrewMember> getActiveCrew() { return new ArrayList<>(activeCrew); }
    public CrewMember     getCurrentActor(){ return activeCrew.isEmpty() ? null : activeCrew.get(currentIndex); }
    public String         getFullLog()    { return fullLog.toString(); }
    public String         getMissionType(){ return missionType; }
}
