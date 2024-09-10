package com.Qwikkspell.FireleapersPractice;

import com.Qwikkspell.FireleapersPractice.events.BreakBlockEvent;
import com.Qwikkspell.FireleapersPractice.events.PlayerJoinEvent;
import com.Qwikkspell.FireleapersPractice.events.PlayerQuitEvent;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public final class FireLeapers extends JavaPlugin {
    private FireSpawner fireSpawner;

    @Override
    public void onEnable() {
        Location startingPos = new Location(this.getServer().getWorld("world"), -371, 89.1, 2143);
        fireSpawner = new FireSpawner(this, startingPos, 27, 0.55, 16);
        fireSpawner.runTaskTimer(this, 0, 1);

        // Register the player join listener
        getServer().getPluginManager().registerEvents(new PlayerQuitEvent(fireSpawner), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinEvent(fireSpawner), this);
        getServer().getPluginManager().registerEvents(new BreakBlockEvent(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
