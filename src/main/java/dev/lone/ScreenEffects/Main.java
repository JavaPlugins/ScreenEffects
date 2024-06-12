package dev.lone.ScreenEffects;

import dev.lone.LoneLibs.nbt.nbtapi.utils.MinecraftVersion;
import dev.lone.ScreenEffects.NMS.GamemodeNMS;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.WeakHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class Main extends JavaPlugin implements Listener
{
    private static Main instance;
    public static boolean isPapermc = false;
    public static boolean showWarnOnJoin = false;

    public static boolean is_v_17_more;
    public static boolean hasViaVersion;
    public static boolean hasPlaceholderAPI;

    public WeakHashMap<Player, Boolean> frozen = new WeakHashMap<>();
    private ScreenEffectCommand screenEffectCommand;

    public static dev.lone.LoneLibs.chat.Msg msg;

    public static Main inst()
    {
        return instance;
    }

    @Override
    public void onEnable()
    {
        instance = this;

        msg = new dev.lone.LoneLibs.chat.Msg("[ScreenEffects] ");
        msg.setPrefix("[ScreenEffects] ");
        msg.setPrefixConsole("[ScreenEffects] ");

        try {
            isPapermc = Class.forName("com.destroystokyo.paper.VersionHistoryManager$VersionData") != null;
        } catch (ClassNotFoundException e) {}

        is_v_17_more = MinecraftVersion.isAtLeastVersion(MinecraftVersion.MC1_17_R1);
        hasViaVersion = Bukkit.getPluginManager().isPluginEnabled("ViaVersion");
        hasPlaceholderAPI = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");

        //<editor-fold desc="Check lonelibs compatibility">
        boolean isLoneLibsCompatible = false;
        try
        {
            //noinspection UnnecessaryFullyQualifiedName
            dev.lone.LoneLibs.LoneLibs.CompareVersionResult compareVersionResult = dev.lone.LoneLibs.LoneLibs.compareVersion("1.0.24");
            //noinspection UnnecessaryFullyQualifiedName
            isLoneLibsCompatible = compareVersionResult == dev.lone.LoneLibs.LoneLibs.CompareVersionResult.INSTALLED_IS_SAME
                    || compareVersionResult == dev.lone.LoneLibs.LoneLibs.CompareVersionResult.INSTALLED_IS_NEWER;
        }
        catch (Throwable ignored) {}
        if(!isLoneLibsCompatible)
        {
            getLogger().severe("Please update LoneLibs! https://www.spigotmc.org/resources/lonelibs.75974/");
            Bukkit.getPluginManager().disablePlugin(this);
            Bukkit.shutdown();
        }
        //</editor-fold>

        GamemodeNMS.init();
        screenEffectCommand = new ScreenEffectCommand();
        screenEffectCommand.register();

        Bukkit.getPluginManager().registerEvents(this, this);

        extractDefaultStuff();

        saveDefaultConfig();
    }

    /**
     * Tested on vanilla game
     * 1.15.2, 1.16.5, 1.19.3
     * title = message
     * subtitle = image
     *
     * 1.17.1, 1.18.2
     * title = image
     * subtitle = message
     *
     * On some versions the rendering order of title and subtitle layers are inverted.
     */
    public static boolean hasTitleBug(Player player)
    {
        if(Main.hasViaVersion)
            return !(ViaVersionCompat.isClientVersionGreaterThan1_16_5(player));
        return !Main.is_v_17_more;
    }

    private void extractDefaultStuff()
    {
        CodeSource src = Main.class.getProtectionDomain().getCodeSource();
        if (src != null)
        {
            URL jar = src.getLocation();
            ZipInputStream zip = null;
            try
            {
                msg.log(ChatColor.AQUA + "    Extracting default effects from .jar");

                zip = new ZipInputStream(jar.openStream());
                while (true)
                {
                    ZipEntry e = zip.getNextEntry();
                    if (e == null)
                        break;
                    String name = e.getName();
                    if (!e.isDirectory() && name.startsWith("contents/"))
                    {
                        File dest = new File((this.getDataFolder().getParent() + "/ItemsAdder/" + name).replace("/", File.separator));
                        if (!dest.exists())
                        {
                            FileUtils.copyInputStreamToFile(this.getResource(name), dest);
                            msg.log(ChatColor.AQUA + "       - Extracted " + name);
                            showWarnOnJoin = true;
                        }
                    }
                }
                msg.log(ChatColor.GREEN + "      DONE extracting default effects from .jar");

            } catch (IOException e)
            {
                msg.error("        ERROR EXTRACTING DEFAULT effects! StackTrace:");
                e.printStackTrace();
            }
        }

        if(showWarnOnJoin)
        {
            msg.warn("Please don't forget to regen your resourcepack using /iazip command.");
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e)
    {
        if(frozen.containsKey(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e)
    {
        if(!showWarnOnJoin)
            return;
        if(e.getPlayer().isOp())
        {
            Bukkit.getScheduler().runTaskLater(this, () -> {
                msg.send(e.getPlayer(), ChatColor.RED + "Please don't forget to regen your resourcepack using /iazip command.");
            }, 60L);
            showWarnOnJoin = false;
        }
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent e)
    {
        screenEffectCommand.sentTitles.remove(e.getPlayer());
    }
}
