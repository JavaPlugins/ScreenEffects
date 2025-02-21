package dev.lone.ScreenEffects;

import com.viaversion.viaversion.api.Via;
import org.bukkit.entity.Player;

public class ViaVersionWrapper
{
    public static boolean isVersionGreaterThan1_16_5(Player player)
    {
        return Via.getAPI().getPlayerVersion(player.getUniqueId()) > 754;//https://wiki.vg/Protocol_version_numbers
    }
}
