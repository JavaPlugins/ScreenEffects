package dev.lone.ScreenEffects.NMS.impl;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import dev.lone.ScreenEffects.NMS.GamemodeNMS;
import dev.lone.ScreenEffects.NMS.IGamemodeNMS;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.PacketPlayOutGameStateChange;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class GamemodeNMS_v1_16_R3 implements IGamemodeNMS
{
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
        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        nmsPlayer.updateAbilities();
    }
}
