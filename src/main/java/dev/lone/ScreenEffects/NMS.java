package dev.lone.ScreenEffects;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.WeakHashMap;

public class NMS
{
    static WeakHashMap<UUID, Scoreboard> scoreboards = new WeakHashMap<>();

    static Object changeGamemode;
    static
    {
        try
        {
            // https://mappings.dev/1.21.4/net/minecraft/network/protocol/game/ClientboundGameEventPacket.html
            // Since 1.20 there is a STREAM_CODEC field at index 0, which we need to ignore.
            Field[] fields = PacketType.Play.Server.GAME_STATE_CHANGE.getPacketClass().getDeclaredFields();
            if(fields[0].getType() != fields[1].getType())
                changeGamemode = fields[4].get(null);
            else
                changeGamemode = fields[3].get(null);
        }
        catch (IllegalAccessException e)
        {
            System.err.println("Failed to load ScreenEffects NMS.");
            e.printStackTrace();
        }
    }

    public static void setGamemode(Player player, float gamemode)
    {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.GAME_STATE_CHANGE);
        packet.getModifier().write(0, changeGamemode);
        packet.getFloat().write(0, gamemode);
        sendPacket(player, packet);
    }

    public static void refreshAbilities(Player player)
    {
        // setAllowFlight() calls onUpdateAbilities() under the hood and onUpdateAbilities() sends the real player abilities.
        player.setAllowFlight(player.getAllowFlight());
    }

    public static void hideHUD(Player player)
    {
        scoreboards.put(player.getUniqueId(), player.getScoreboard());
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

        if (player.getGameMode() == GameMode.SPECTATOR)
            return;

        setGamemode(player, gamemodeToId(GameMode.SPECTATOR));
        refreshAbilities(player);
    }

    public static void showHUD(Player player)
    {
        Scoreboard scoreboard = scoreboards.get(player.getUniqueId());
        if (scoreboard != null)
            player.setScoreboard(scoreboard);

        if (player.getGameMode() == GameMode.SPECTATOR)
            return;
        setGamemode(player, gamemodeToId(player.getGameMode()));
        Bukkit.getScheduler().runTaskLater(Main.inst(), () -> refreshAbilities(player), 5L);
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

    public static void sendPacket(Player player, PacketContainer packet)
    {
        try
        {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        }
        catch (Throwable ignored) { }
    }

    public static boolean isPaper()
    {
        String name = Bukkit.getServer().getName();
        if(name.contains("Paper") || name.contains("Purpur"))
            return true;

        return
                hasClass("com.destroystokyo.paper.event.player.PlayerSetSpawnEvent$Cause") ||
                        hasClass("com.destroystokyo.paper.utils.PaperPluginLogger") ||
                        hasClass("io.papermc.paper.ServerBuildInfo") ||
                        hasClass("io.papermc.paper.text.PaperComponents");
    }

    public static boolean hasClass(String className)
    {
        try
        {
            Class.forName(className);
            return true;
        }
        catch (Throwable ignored) {}
        return false;
    }
}
