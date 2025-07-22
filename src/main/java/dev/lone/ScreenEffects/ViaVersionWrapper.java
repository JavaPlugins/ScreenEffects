package dev.lone.ScreenEffects;

import beer.devs.fastnbt.nms.Version;
import com.viaversion.viaversion.api.Via;
import org.bukkit.entity.Player;

public class ViaVersionWrapper
{
    public static Version getVersion(Player player)
    {
        int versionId = Via.getAPI().getPlayerVersion(player.getUniqueId());
        Version nearest = null;
        int minDiff = Integer.MAX_VALUE;
        for (Version value : Version.values())
        {
            int diff = Math.abs(value.id - versionId);
            if (diff < minDiff)
            {
                minDiff = diff;
                nearest = value;
            }
        }
        return nearest;
    }
}
