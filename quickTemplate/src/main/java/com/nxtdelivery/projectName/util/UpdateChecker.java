package com.nxtdelivery.projectName.util;

import com.nxtdelivery.projectName.ProjectMain;
import com.nxtdelivery.projectName.Reference;

import java.net.URL;
import java.util.Properties;

public class UpdateChecker {
    public static String latestVersion;

    public static boolean checkUpdate(String currentVersion) {
        try {
            Properties prop = new Properties();
            prop.load(new URL(
                    "https://raw.githubusercontent.com/nxtdaydelivery/" + Reference.NAME + "/master/gradle.properties")
                    .openStream());
            latestVersion = prop.getProperty("mod_version");
            if (latestVersion.equals("0")) {
                ProjectMain.LOGGER.warn(
                        "version checker is 0. This is a feature added to prevent errors. Version checker disabled.");
                return false;
            }
            if (currentVersion.contains("beta")) {
                ProjectMain.LOGGER.warn("beta build detected. This build might be unstable, use at your own risk!");
                ProjectMain.betaFlag = true;
            }
            if (!currentVersion.equals(latestVersion)) {
                ProjectMain.LOGGER.warn("a newer version " + latestVersion + " is available! Please consider updating! ("
                        + currentVersion + ")");
                return true;
            } else {
                ProjectMain.LOGGER.info("already using the newest version (" + latestVersion + ")");
                return false;
            }
        } catch (Exception e) {
            // e.printStackTrace();
            ProjectMain.LOGGER.error(e);
            ProjectMain.LOGGER.error("failed to check version. assuming latest version.");
            return false;
        }
    }
}
