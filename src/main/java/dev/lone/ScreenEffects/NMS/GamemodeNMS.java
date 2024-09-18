package dev.lone.ScreenEffects.NMS;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import dev.lone.LoneLibs.nbt.nbtapi.utils.MinecraftVersion;
import dev.lone.ScreenEffects.Main;
import dev.lone.ScreenEffects.NMS.impl.*;
import lonelibs.dev.lone.fastnbt.nms.Version;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.UUID;
import java.util.WeakHashMap;

public class GamemodeNMS
{
    private static GamemodeNMS instance;
    private IGamemodeNMS nms;

    static WeakHashMap<UUID, Scoreboard> scoreboards = new WeakHashMap<>();

    private GamemodeNMS()
    {
        //<editor-fold desc="Legacy shit">
        switch (MinecraftVersion.getVersion())
        {
            case MC1_14_R1:
                nms = new GamemodeNMS_v1_14_R1();
                break;
            case MC1_15_R1:
                nms = new GamemodeNMS_v1_15_R1();
                break;
            case MC1_16_R3:
                nms = new GamemodeNMS_v1_16_R3();
                break;
            case MC1_17_R1:
                nms = new GamemodeNMS_v1_17_R1();
                break;
            case MC1_18_R2:
                nms = new GamemodeNMS_v1_18_R2();
                break;
            case MC1_19_R1:
                nms = new GamemodeNMS_v1_19_R1();
                break;
            case MC1_19_R2:
                nms = new GamemodeNMS_v1_19_R2();
                break;
            case MC1_19_R3:
                nms = new GamemodeNMS_v1_19_R3();
                break;
            case MC1_20_R1:
                nms = new GamemodeNMS_v1_20_R1();
                break;
            case MC1_20_R2:
                nms = new GamemodeNMS_v1_20_R2();
                break;
            case MC1_20_R3:
                nms = new GamemodeNMS_v1_20_R3();
                break;
        }
        //</editor-fold>

        //<editor-fold desc="Modern">
        switch (Version.get())
        {
            case v1_20_6:
                nms = new GamemodeNMS_v1_20_6();
                break;
            case v1_21_1:
                nms = new GamemodeNMS_v1_21_1();
                break;
        }
        //</editor-fold>
    }

    public static void init()
    {
        nms();
    }

    private static IGamemodeNMS nms()
    {
        if (instance == null)
            instance = new GamemodeNMS();
        return instance.nms;
    }

    public static void hideHUD(Player player)
    {
        scoreboards.put(player.getUniqueId(), player.getScoreboard());
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

        if (player.getGameMode() == GameMode.SPECTATOR)
            return;

        nms().setGamemode(player, gamemodeToId(GameMode.SPECTATOR));
        nms().refreshAbilities(player);
    }

    public static void showHUD(Player player)
    {
        Scoreboard scoreboard = scoreboards.get(player.getUniqueId());
        if(scoreboard != null)
            player.setScoreboard(scoreboard);

        if (player.getGameMode() == GameMode.SPECTATOR)
            return;
        nms().setGamemode(player, gamemodeToId(player.getGameMode()));
        Bukkit.getScheduler().runTaskLater(Main.inst(), () -> nms().refreshAbilities(player), 5L);
    }

    public static float gamemodeToId(GameMode gameMode)
    {
        switch (gameMode)
        {
            case SURVIVAL:
                return 0f;
            case CREATIVE:
                return 1f;
            case ADVENTURE:
                return 2f;
            case SPECTATOR:
                return 3f;
        }
        return -1;
    }

    public static boolean sendPacket(Player player, PacketContainer packet)
    {
        try
        {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        }
        catch (Throwable invocationTargetException)
        {
            return false;
        }
        return true;
    }
}
