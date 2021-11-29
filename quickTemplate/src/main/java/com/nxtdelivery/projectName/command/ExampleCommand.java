package com.nxtdelivery.projectName.command;

import com.nxtdelivery.projectName.ProjectMain;
import com.nxtdelivery.projectName.Reference;
import com.nxtdelivery.projectName.gui.GUIConfig;
import com.nxtdelivery.projectName.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static net.minecraft.command.CommandBase.getListOfStringsMatchingLastWord;

public class ExampleCommand implements ICommand {

    private final List<String> aliases;
    private static final Minecraft mc = Minecraft.getMinecraft();

    public ExampleCommand() {
        aliases = new ArrayList<>();
        aliases.add("example");
    }

    @Override
    public int compareTo(@NotNull ICommand o) {
        return 0;
    }

    @Override
    public String getCommandName() {
        return "example";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "example <>";
    }

    @Override
    public List<String> getCommandAliases() {
        return this.aliases;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        try {
            switch (args[0]) {
                case "configure":
                case "config":
                case "cfg":
                    try {
                        new TickDelay(() -> mc.displayGuiScreen(GUIConfig.INSTANCE.gui()), 1);
                    } catch (Exception e) {
                        if (GUIConfig.debugMode) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case "reload":
                    ProjectMain.LOGGER.info("Reloading config and version checker...");
                    ProjectMain.sendMessages("Reloading!");
                    GUIConfig.INSTANCE.initialize();
                    ProjectMain.updateCheck = UpdateChecker.checkUpdate(Reference.VERSION);
                    AuthChecker.checkAuth(ProjectMain.JarFile.getPath());
                    ProjectMain.sendMessages("Reloaded! Re-log and check logs for more information.");
                    Minecraft.getMinecraft().thePlayer.playSound("minecraft:random.successful_hit", 1.0F, 1.0F);
                    break;
                default:
		    break;
            }
        } catch (Exception e) {
            sender.addChatMessage(new ChatComponentText(Reference.COLOR
                    + "[EXAMPLE] Command menu (mod version " + Reference.VERSION + ")"));
            sender.addChatMessage(new ChatComponentText(Reference.COLOR
                    + "[EXAMPLE] Command usage: /example configure, /example "));
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        try {
            Collection<NetworkPlayerInfo> players = mc.getNetHandler().getPlayerInfoMap();
            List<String> list = new ArrayList<>();
            for (NetworkPlayerInfo info : players) {
                list.add(info.getGameProfile().getName());
            }

            if (args.length == 1) return getListOfStringsMatchingLastWord(args, list.toArray(new String[0]));
            else return null;
        } catch (Exception e) {
            if (GUIConfig.debugMode) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

}
