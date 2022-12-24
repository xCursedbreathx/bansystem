package de.cursedbreath.bansystem.utils;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GlobalVariables {

    public static String PREFIX = "§7[§6BanSystem§7]§r ";
    public static String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
        return format.format(date);
    }

}
