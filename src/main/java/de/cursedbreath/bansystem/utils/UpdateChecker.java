package de.cursedbreath.bansystem.utils;

import com.google.gson.JsonParser;
import de.cursedbreath.bansystem.BanSystem;
import org.slf4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;

public class UpdateChecker {

    private final Logger logger;
    JsonParser jsonParser;
    private URL checkUrl;
    private String project;

    public UpdateChecker(String projectID, Logger logger) throws MalformedURLException {
        this.logger = logger;
        jsonParser = new JsonParser();
        project = projectID;
        try {
            checkUrl = new URL("https://api.modrinth.com/v2/project/"+ projectID +"/version");
        } catch (MalformedURLException e) {
            throw new MalformedURLException("The URL is malformed!");
        }
        if(checkForUpdates()) {
            logger.info("There is a new update available!");
            logger.info("You can download it here: " + getLatestVersionURL());
        }
    }

    public String getLatestVersionURL() {
        return "https://modrinth.com/plugin/" + project;
    }

    public boolean checkForUpdates() {
        try {
            String version = jsonParser.parse(new java.util.Scanner(checkUrl.openStream()).useDelimiter("\\A").next()).getAsJsonArray().get(0).getAsJsonObject().get("version_number").getAsString();
            return !version.equals(BanSystem.getVersion());
        } catch (Exception e) {
            logger.error("Failed to check for updates: " + e.getMessage());
            return false;
        }
    }
}
