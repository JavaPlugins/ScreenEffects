package dev.lone.ScreenEffects;

import beer.devs.fastnbt.nms.Version;
import dev.lone.ScreenEffects.utils.Msg;
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
    private static Main inst;

    public static boolean IS_PAPER = false;
    public static boolean HAS_VIA_VERSION;
    public static boolean HAS_PLACEHOLDER_API;
    public static boolean is_v_17_more;

    public WeakHashMap<Player, Boolean> frozen = new WeakHashMap<>();
    private Command command;
    public static boolean showWarnOnJoin = false;

    public static Main inst()
    {
        return inst;
    }

    @Override
    public void onEnable()
    {
        inst = this;

        Msg.setPrefix("[ScreenEffects] ");
        Msg.setPrefixConsole("[ScreenEffects] ");

        IS_PAPER = NMS.isPaper();

        is_v_17_more = Version.isAtLeast(Version.v1_17_R1);
        HAS_VIA_VERSION = Bukkit.getPluginManager().isPluginEnabled("ViaVersion");
        HAS_PLACEHOLDER_API = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");

        command = new Command();
        command.register();

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
        if(Main.HAS_VIA_VERSION)
            return !ViaVersionWrapper.isVersionGreaterThan1_16_5(player);
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
                Msg.log(ChatColor.AQUA + "    Extracting default effects from .jar");

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
                            Msg.log(ChatColor.AQUA + "       - Extracted " + name);
                            showWarnOnJoin = true;
                        }
                    }
                }
                Msg.log(ChatColor.GREEN + "      DONE extracting default effects from .jar");

            } catch (IOException e)
            {
                Msg.error("        ERROR EXTRACTING DEFAULT effects! StackTrace:");
                e.printStackTrace();
            }
        }

        if(showWarnOnJoin)
        {
            Msg.warn("Please don't forget to regen your resourcepack using /iazip command.");
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
                Msg.message(e.getPlayer(), ChatColor.RED + "Please don't forget to regen your resourcepack using /iazip command.");
            }, 60L);
            showWarnOnJoin = false;
        }
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent e)
    {
        command.sentTitles.remove(e.getPlayer());
    }
}
