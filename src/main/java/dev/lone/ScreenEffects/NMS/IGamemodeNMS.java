package dev.lone.ScreenEffects.NMS;

import org.bukkit.entity.Player;

public interface IGamemodeNMS
{
    void setGamemode(Player player, float gamemode);
    void refreshAbilities(Player player);
}
