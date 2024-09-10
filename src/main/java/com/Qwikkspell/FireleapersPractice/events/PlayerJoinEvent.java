package com.Qwikkspell.FireleapersPractice.events;

import com.Qwikkspell.FireleapersPractice.FireSpawner;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class PlayerJoinEvent implements Listener {
    private final FireSpawner fireSpawner;

    public PlayerJoinEvent(FireSpawner fireSpawner) {
        this.fireSpawner = fireSpawner;
    }

    @EventHandler
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (!player.isOp()) {
            player.teleport(new Location(player.getWorld(), -387, 89, 2155));
        }
        player.setInvulnerable(true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 3, false, false, false));
        fireSpawner.updateScoreboard(); // Call the updateScoreboard method
    }
}
