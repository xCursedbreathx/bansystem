package de.cursedbreath.bansystem.utils;

import de.cursedbreath.bansystem.BanSystem;
import org.slf4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class UpdateChecker {

    private final Logger logger;
    private URL checkUrl;
    private String project;

    public UpdateChecker(String projectID, Logger logger) throws MalformedURLException {
        this.logger = logger;
        project = projectID;
        try {
            checkUrl = new URL("https://hangar.papermc.io/api/v1/projects/Cursedbreath/"+ projectID +"/latestrelease");
        } catch (MalformedURLException e) {
            throw new MalformedURLException("The URL is malformed!");
        }
        if(checkForUpdates()) {
            logger.info("There is a new update available!");
            logger.info("You can download it here: " + getLatestVersionURL());
        }
    }

    public String getLatestVersionURL() {
        return "https://hangar.papermc.io/Cursedbreath/" + project;
    }

    public boolean checkForUpdates() {
        try {
            String newVersion = new Scanner(checkUrl.openStream(), "UTF-8").useDelimiter("\\A").next();
            return !newVersion.equals(BanSystem.getVersion());
        } catch (Exception e) {
            logger.error("Failed to check for updates: " + e.getMessage());
            return false;
        }
    }
}
