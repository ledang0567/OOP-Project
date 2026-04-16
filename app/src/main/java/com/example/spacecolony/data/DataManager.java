package com.example.spacecolony.data;

import android.content.Context;
import android.util.Log;

import com.example.spacecolony.model.CrewMember;
import com.example.spacecolony.model.Engineer;
import com.example.spacecolony.model.Medic;
import com.example.spacecolony.model.Pilot;
import com.example.spacecolony.model.Scientist;
import com.example.spacecolony.model.Soldier;
import com.example.spacecolony.model.Storage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Saves and loads the entire Storage state as a JSON file in the app's
 * internal storage (no permissions required on Android).
 */
public class DataManager {
    private static final String TAG       = "DataManager";
    private static final String FILE_NAME = "colony_save.json";

    // --- Save ---

    public static void save(Context context) {
        try {
            JSONObject root = new JSONObject();
            Storage storage = Storage.getInstance();

            root.put("colonyName",         storage.getName());
            root.put("completedMissions",  storage.getCompletedMissions());

            JSONArray crewArray = new JSONArray();
            for (CrewMember cm : storage.getAllCrew()) {
                crewArray.put(crewMemberToJson(cm));
            }
            root.put("crew", crewArray);

            String json = root.toString(2);
            try (FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)) {
                fos.write(json.getBytes(StandardCharsets.UTF_8));
            }
            Log.d(TAG, "Saved " + storage.totalCrew() + " crew members.");

        } catch (Exception e) {
            Log.e(TAG, "Save failed: " + e.getMessage());
        }
    }

    // --- Load ---

    /**
     * Loads saved data and replaces the Storage singleton.
     * If no save file exists, does nothing (first launch).
     *
     * @return true if data was loaded successfully
     */
    public static boolean load(Context context) {
        try (FileInputStream fis = context.openFileInput(FILE_NAME);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8))) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);

            JSONObject root = new JSONObject(sb.toString());
            String colonyName = root.optString("colonyName", "Colony Alpha");
            int completedMissions = root.optInt("completedMissions", 0);

            Storage storage = new Storage(colonyName);
            storage.setCompletedMissions(completedMissions);

            JSONArray crewArray = root.optJSONArray("crew");
            if (crewArray != null) {
                for (int i = 0; i < crewArray.length(); i++) {
                    JSONObject obj = crewArray.getJSONObject(i);
                    CrewMember cm = crewMemberFromJson(obj);
                    if (cm != null) storage.addCrewMember(cm);
                }
            }

            Storage.setInstance(storage);
            Log.d(TAG, "Loaded " + storage.totalCrew() + " crew members.");
            return true;

        } catch (java.io.FileNotFoundException e) {
            Log.d(TAG, "No save file found — fresh start.");
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Load failed: " + e.getMessage());
            return false;
        }
    }

    // --- Delete save ---

    public static void deleteSave(Context context) {
        context.deleteFile(FILE_NAME);
        Log.d(TAG, "Save file deleted.");
    }

    // --- Serialisation helpers ---

    private static JSONObject crewMemberToJson(CrewMember cm) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("id",                cm.getId());
        obj.put("name",              cm.getName());
        obj.put("specialization",    cm.getSpecialization());
        obj.put("skill",             cm.getSkill());
        obj.put("resilience",        cm.getResilience());
        obj.put("experience",        cm.getExperience());
        obj.put("energy",            cm.getEnergy());
        obj.put("maxEnergy",         cm.getMaxEnergy());
        obj.put("location",          cm.getLocation());
        obj.put("missionsCompleted", cm.getMissionsCompleted());
        obj.put("missionsWon",       cm.getMissionsWon());
        obj.put("trainingSessions",  cm.getTrainingSessions());
        obj.put("medbayRounds",      cm.getMedbayRecoveryRounds());
        return obj;
    }

    private static CrewMember crewMemberFromJson(JSONObject obj) throws JSONException {
        int    id                = obj.getInt("id");
        String name              = obj.getString("name");
        String specialization    = obj.getString("specialization");
        int    skill             = obj.getInt("skill");
        int    resilience        = obj.getInt("resilience");
        int    experience        = obj.getInt("experience");
        int    energy            = obj.getInt("energy");
        int    maxEnergy         = obj.getInt("maxEnergy");
        String location          = obj.getString("location");
        int    missionsCompleted = obj.optInt("missionsCompleted", 0);
        int    missionsWon       = obj.optInt("missionsWon", 0);
        int    trainingSessions  = obj.optInt("trainingSessions", 0);
        int    medbayRounds      = obj.optInt("medbayRounds", 0);

        CrewMember cm;
        switch (specialization) {
            case "Pilot":
                cm = new Pilot(id, name, skill, resilience, experience, energy, maxEnergy,
                        location, missionsCompleted, missionsWon, trainingSessions);
                break;
            case "Engineer":
                cm = new Engineer(id, name, skill, resilience, experience, energy, maxEnergy,
                        location, missionsCompleted, missionsWon, trainingSessions);
                break;
            case "Medic":
                cm = new Medic(id, name, skill, resilience, experience, energy, maxEnergy,
                        location, missionsCompleted, missionsWon, trainingSessions);
                break;
            case "Scientist":
                cm = new Scientist(id, name, skill, resilience, experience, energy, maxEnergy,
                        location, missionsCompleted, missionsWon, trainingSessions);
                break;
            case "Soldier":
                cm = new Soldier(id, name, skill, resilience, experience, energy, maxEnergy,
                        location, missionsCompleted, missionsWon, trainingSessions);
                break;
            default:
                Log.w("DataManager", "Unknown specialization: " + specialization);
                return null;
        }

        cm.setMedbayRecoveryRounds(medbayRounds);
        return cm;
    }
}
