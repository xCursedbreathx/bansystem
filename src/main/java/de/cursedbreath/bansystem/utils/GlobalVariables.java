package de.cursedbreath.bansystem.utils;

import de.cursedbreath.bansystem.BanSystem;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;

public class GlobalVariables {

    public static String PREFIX = "§7[§6BanSystem§7]§r ";

    public static Map<UUID, Map<Integer, HistoryObject>> history = new HashMap<>();

    public static String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
        return format.format(date);
    }

    public static Long calculateBanTime(int BannedTimes, String id) {
        List<Long> durations = BanSystem.getVelocityConfig().getDurations(id);
        if(durations.size() >= BannedTimes) {
            return (durations.get(BannedTimes-1) * 1000);
        }
        else {
            return (durations.get(durations.size()-1) * 1000);
        }
    }

}
