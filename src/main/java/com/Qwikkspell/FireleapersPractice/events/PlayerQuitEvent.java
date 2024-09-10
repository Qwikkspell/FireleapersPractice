package com.Qwikkspell.FireleapersPractice.events;

import com.Qwikkspell.FireleapersPractice.FireSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;

public class PlayerQuitEvent implements Listener {
    private final FireSpawner fireSpawner;

    public PlayerQuitEvent(FireSpawner fireSpawner) {
        this.fireSpawner = fireSpawner;
    }

    @EventHandler
    public void onPlayerQuit(org.bukkit.event.player.PlayerQuitEvent e) {
        Player player = e.getPlayer();
        fireSpawner.getPlayerScores().remove(player);
        fireSpawner.updateScoreboard();
    }
}
