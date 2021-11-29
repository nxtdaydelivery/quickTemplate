/* Changelog v1.0
 *  first release!
 */

package com.nxtdelivery.projectName;

import com.nxtdelivery.projectName.command.ExampleCommand;
import com.nxtdelivery.projectName.gui.GUIConfig;
import com.nxtdelivery.projectName.util.AuthChecker;
import com.nxtdelivery.projectName.util.TickDelay;
import com.nxtdelivery.projectName.util.UpdateChecker;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION)
public class ProjectMain {

    @SuppressWarnings("unused")
    @Mod.Instance(Reference.MODID) // variables and things
    public static ProjectMain instance;
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static final Logger LOGGER = LogManager.getLogger(Reference.NAME);
    public static File JarFile;
    public static boolean updateCheck;
    public static boolean betaFlag = true;
    public static boolean corrupt = false;

    @EventHandler()
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("preloading config...");
        try {
            GUIConfig.INSTANCE.preload();
            LOGGER.info("config preload was successful");
        } catch (Exception e) {
            if (GUIConfig.debugMode) {
                e.printStackTrace();
            }
            corrupt = true;
            LOGGER.error("Config failed to read. File has been reset. If you just reset your config, ignore this message.");
        }
        JarFile = event.getSourceFile();
        if (GUIConfig.debugMode) {
            LOGGER.info("Got JAR File: " + JarFile.getPath());
        }
    }

    @EventHandler()
    public void init(FMLInitializationEvent event) {
        LOGGER.info("attempting to check update status and mod authenticity...");
        updateCheck = UpdateChecker.checkUpdate(Reference.VERSION);
        if (JarFile.getPath().endsWith(".jar")) {
            AuthChecker.checkAuth(JarFile.getPath());
        } else {
            LOGGER.warn("Mod isn't a JAR, assuming development environment. Not checking authenticity.");
        }
        LOGGER.info("registering settings...");
        MinecraftForge.EVENT_BUS.register(this);
        ClientCommandHandler.instance.registerCommand(new ExampleCommand());
        LOGGER.debug(instance.toString());        // please stop moaning at me intellij
        LOGGER.info("Complete!" + Reference.NAME + " loaded successfully.");
    }


    @SubscribeEvent
    @SuppressWarnings({"ConstantConditions", "MismatchedStringCase"})
    public void onWorldLoad(WorldEvent.Load event) {
        if (updateCheck && GUIConfig.sendUp && event.world.isRemote) {
            new TickDelay(this::sendUpdateMessage, 20);
            updateCheck = false;
        }
        if (Reference.VERSION.contains("beta") && betaFlag && event.world.isRemote) {
            try {
                new TickDelay(() -> sendMessages("",
                        "Beta build has been detected (ver. " + Reference.VERSION + ")",
                        "Note that some features might be unstable! Use at your own risk!"), 20);
                betaFlag = false;
            } catch (Exception e) {
                betaFlag = true;
                //if (GUIConfig.debugMode) { e.printStackTrace(); }
                LOGGER.error("skipping beta message, bad world return!");
            }
        }
        if (corrupt) {
            try {
                new TickDelay(() -> sendMessages("",
                        "An error occurred while trying to read your config file. You will have to reset it.",
                        "If you just reset your configuration file, ignore this message."), 20);
                corrupt = false;
            } catch (Exception e) {
                //if (GUIConfig.debugMode) { e.printStackTrace(); }
                LOGGER.error("skipping corrupt message, bad world return!");
            }
        }
        if (AuthChecker.mismatch && GUIConfig.securityLevel == 2) {
            try {
                new TickDelay(() -> sendMessages("The hash for the mod is incorrect. Check the logs for more info.",
                        "WARNING: This could mean your data is exposed to hackers! Make sure you got the mod from the OFFICIAL mirror, and try again.",
                        Reference.URL), 20);
                AuthChecker.mismatch = false;
            } catch (Exception e) {
                //if (GUIConfig.debugMode) { e.printStackTrace();}
                LOGGER.error("skipping hash mismatch message, bad world return!");
            }
        }
    }

    @SuppressWarnings({"ConstantConditions", "MismatchedStringCase"})
    public static void sendMessages(String... messages) {
        try {
            for (String message : messages) {
                mc.thePlayer.addChatMessage(new ChatComponentText(Reference.COLOR + "[" + Reference.NAME + "] " + message));
            }
        } catch (Exception e) {
            LOGGER.error("Didn't send message: " + e.getMessage());
            //if (GUIConfig.debugMode) { e.printStackTrace(); }
            if (Reference.VERSION.contains("beta")) {
                betaFlag = true;
            }
        }
    }

    private void sendUpdateMessage() {
        try {
            IChatComponent comp = new ChatComponentText("Click here to update it!");
            ChatStyle style = new ChatStyle().setChatClickEvent(new ClickEvent(Action.OPEN_URL, Reference.URL));
            style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ChatComponentText(Reference.COLOR + Reference.URL)));
            style.setColor(Reference.COLOR);
            style.setUnderlined(true);
            comp.setChatStyle(style);
            mc.thePlayer.playSound("minecraft:random.successful_hit", 1.0F, 1.0F);
            mc.thePlayer.addChatMessage(new ChatComponentText(
                    Reference.COLOR + "--------------------------------------"));
            mc.thePlayer.addChatMessage(new ChatComponentText(Reference.COLOR
                    + ("A newer version of " + Reference.NAME + " is available! (" + UpdateChecker.latestVersion + ")")));
            mc.thePlayer.addChatMessage(comp);
            mc.thePlayer.addChatMessage(new ChatComponentText(
                    Reference.COLOR + "--------------------------------------"));
        } catch (NullPointerException e) {
            //if (GUIConfig.debugMode) { e.printStackTrace(); }
            updateCheck = true;
            LOGGER.error("skipping update message, bad world return!");
        }
    }

}
