package com.nxtdelivery.projectName.gui;

import com.nxtdelivery.projectName.ProjectMain;
import com.nxtdelivery.projectName.Reference;
import com.nxtdelivery.projectName.util.TickDelay;
import gg.essential.vigilance.Vigilant;
import gg.essential.vigilance.data.Property;
import gg.essential.vigilance.data.PropertyType;
import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.ReportedException;


import java.io.File;
import java.io.FileWriter;
import java.io.PrintStream;
import java.io.PrintWriter;


public class GUIConfig extends Vigilant {
    @Property(
            type = PropertyType.SWITCH, name = "Enable",
            description = "Enable/Disable the mod.",
            category = "General"
    )
    public static boolean modEnabled = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Send Update Messages",
            description = "Send update messages on startup if a new version is available.",
            category = "General", subcategory = "Updates"
    )
    public static boolean sendUp = true;
    @Property(
            type = PropertyType.SELECTOR,
            name = "Security Level",
            description = "Level of warning to issue if a mismatched hash is detected on startup, which could suggest modification of the mod and lead to possible data theft.",
            category = "General", subcategory = "Updates",
            options = {"Off", "Warn in Logger", "Warn on world join", "Halt startup"}
    )
    public static int securityLevel = 1;


    @Property(
            type = PropertyType.SWITCH, name = "Debug",
            description = "Enable/disable verbose logging to help with diagnostics.\n\u00A7eNote: You will see a lot of (useless) errors in logs with this active!",
            category = "Support", subcategory = "General"
    )
    public static boolean debugMode = false;

    @Property(
            type = PropertyType.BUTTON, name = "Reset Defaults",
            description = "Reset all values to their defaults.\n \u00A7cForcibly restarts your game!",
            category = "Support", subcategory = "General", placeholder = "Reset"
    )
    public static void reset() {
        mc.thePlayer.closeScreen();         //TODO
        try {
            FileWriter writer = new FileWriter("./config/projectExample.toml");
            writer.write("this was cleared so it will be reset on next restart.");
            writer.close();

            ProjectMain.LOGGER.warn("config file was cleared. Please restart your game.");
        } catch (Exception e) {
            ProjectMain.LOGGER.error("failed to clear config, " + e);
        }
        CrashReport report = CrashReport.makeCrashReport(new Throwable() {
            @Override
            public String getMessage() {
                return "[Example] Manually initiated crash: Cleaning configuration file. THIS IS NOT AN ERROR";
            }

            @Override
            public void printStackTrace(final PrintWriter s) {
                s.println(getMessage());
            }

            @Override
            public void printStackTrace(final PrintStream s) {
                s.println(getMessage());
            }
        }, "Cleaning Configuration file");
        new TickDelay(() -> {
            throw new ReportedException(report);
        }, 0);
    }

    public static final GUIConfig INSTANCE = new GUIConfig();
    private static final Minecraft mc = Minecraft.getMinecraft();

    public GUIConfig() {
        super(new File("./config/projectExample.toml"), Reference.NAME + " (v" + Reference.VERSION + ")");
        initialize();

        addDependency("winWidth", "sizeEnabled");
        addDependency("winTop", "sizeEnabled");
        addDependency("winBottom", "sizeEnabled");
        addDependency("winMiddle", "sizeEnabled");
        addDependency("doPartyDetectionPLUS", "doPartyDetection");

    }
}
