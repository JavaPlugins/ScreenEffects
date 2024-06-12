package dev.lone.ScreenEffects.NMS.impl;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import dev.lone.ScreenEffects.NMS.GamemodeNMS;
import dev.lone.ScreenEffects.NMS.IGamemodeNMS;
import net.minecraft.network.protocol.game.PacketPlayOutGameStateChange;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.player.EntityHuman;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class GamemodeNMS_v1_20_R1 implements IGamemodeNMS
{
    Method method;
    {
        try
        {
            method = EntityHuman.class.getMethod("w"); // onUpdateAbilities() sends PacketPlayOutAbilities:
        }
        catch (NoSuchMethodException e)
        {
            System.err.println("Failed to load ScreenEffects for 1.20.1");
            e.printStackTrace();
        }
    }

    @Override
    public void setGamemode(Player player, float gamemode)
    {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.GAME_STATE_CHANGE);
        packet.getSpecificModifier(PacketPlayOutGameStateChange.a.class).write(0, PacketPlayOutGameStateChange.d);
        packet.getFloat().write(0, gamemode);
        GamemodeNMS.sendPacket(player, packet);
    }

    @Override
    public void refreshAbilities(Player player)
    {
        if(method == null)
            return;
        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        try
        {
            method.invoke(nmsPlayer);
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {
            e.printStackTrace();
        }
    }
}
