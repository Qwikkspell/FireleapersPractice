package com.Qwikkspell.FireleapersPractice;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class FireSpawner extends BukkitRunnable {
    private final JavaPlugin plugin;
    private Location startingPos;
    private final int length;
    private final double speed;
    private final Location startingPosOriginal;
    private final int stopAfter;
    private int onState;
    private final Timer timer;
    private final Map<Player, Integer> playerScores;
    private Set<Player> playersWhoLost;
    private Set<Player> playersInGame;
    private final ScoreboardManager scoreboardManager;
    private final Scoreboard scoreboard;
    private final Objective objective;

    public FireSpawner(final JavaPlugin plugin, Location startingPos, int length, double speed, int stopAfter) {
        this.plugin = plugin;
        this.startingPos = startingPos.clone();
        this.length = length;
        this.speed = speed;
        this.startingPosOriginal = startingPos.clone();
        this.stopAfter = stopAfter;
        this.onState = 0;
        this.timer = new Timer();
        this.playerScores = new HashMap<>();
        this.playersWhoLost = new HashSet<>();
        this.playersInGame = new HashSet<>();
        this.scoreboardManager = Bukkit.getScoreboardManager();
        this.scoreboard = scoreboardManager.getNewScoreboard();
        this.objective = scoreboard.registerNewObjective("PlayerScores", "dummy", ChatColor.YELLOW.toString() + ChatColor.BOLD + "    PARTY GAMES    ");
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }


    @Override
    public void run() {
        if (this.onState == 0) {
            for (double x = 0; x <= this.length; x += 0.3) {
                Location critPos = this.startingPos.clone().add(0, 0, x);
                Objects.requireNonNull(this.plugin.getServer().getWorld("world")).spawnParticle(Particle.CRIT, critPos, 10, 0, 0, 0, 0);
            }
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                if (isPlayerHere(player, player.getLocation())) {
                    this.playersInGame.add(player);
                }
                if (!playersWhoLost.contains(player) && isPlayerDead(player, startingPos)) {
                    player.sendMessage(ChatColor.RED + player.getDisplayName() + ChatColor.RED + "'s legs melted");
                    playersWhoLost.add(player);
                    playerScores.put(player, 0);
                    updateScoreboard();
                }
            }
            this.startingPos = startingPos.subtract(this.speed, 0, 0);
            if (this.startingPos.distance(this.startingPosOriginal) >= this.stopAfter) {
                this.startingPos = this.startingPosOriginal.clone();
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    if (!playersWhoLost.contains(player)) {
                        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                        int newScore = playerScores.getOrDefault(player, 0) + 1;
                        playerScores.put(player, newScore);
                    }
                    player.setFoodLevel(20);
                    int playerLoc = player.getLocation().getBlockX();
                    if ((playerLoc > -386 || playerLoc <-387) && !player.isOp()) {
                        player.teleport(new Location(player.getWorld(), -387, 89, 2155));
                    }
                }
                updateScoreboard();
                this.playersWhoLost.clear();

                double chanceToPause = Math.random() * 100;
                if (chanceToPause < 50) {
                    this.onState = 1;
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            onState = 0;
                        }
                    }, 1500);
                }
            }
        }
    }
    private boolean isPlayerDead(Player player, Location linepoint) {
        Location location = player.getLocation();
        double playerX = location.getX();
        double playerY = location.getY();
        double lineX = linepoint.getX();
        double lineY = linepoint.getY();

        return (Math.abs(playerX - lineX) < 1.25) && (Math.abs(playerY - lineY) < 1.25);
    }

    private boolean isPlayerHere(Player player, Location location) {
        if (player.getLocation().getBlockX() < -386 && player.getLocation().getBlockX() > -387) {
            return true;
        }
        return false;
    }

    public void updateScoreboard() {
        // Clear existing scores
        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }

        // Add current date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(new Date());
        Score date = objective.getScore(ChatColor.GRAY + currentDate);
        date.setScore(15);

        // Add space
        Score spacer1 = objective.getScore(" ");
        spacer1.setScore(14);

        // Add game title
        Score gameLabel = objective.getScore(ChatColor.WHITE + "Game:");
        gameLabel.setScore(13);
        Score gameName = objective.getScore(ChatColor.GREEN + "Fire Leapers");
        gameName.setScore(12);

        // Add space
        Score spacer2 = objective.getScore("  ");
        spacer2.setScore(11);

        // Add player scores (up to top 5)
        List<Map.Entry<Player, Integer>> topPlayers = playerScores.entrySet().stream()
                .sorted(Map.Entry.<Player, Integer>comparingByValue().reversed())
                .limit(5)
                .toList();

        int scorePosition = 10;
        for (Map.Entry<Player, Integer> entry : topPlayers) {
            Score score = objective.getScore(ChatColor.AQUA + entry.getKey().getName() + ChatColor.WHITE + ": " + ChatColor.GREEN + entry.getValue());
            score.setScore(scorePosition--);
        }

        // Add empty lines until the bottom
        while (scorePosition > 1) {
            Score spacer = objective.getScore(" ".repeat(16 - scorePosition)); // Ensure unique entries
            spacer.setScore(scorePosition--);
        }

        // Add footer
        Score footer = objective.getScore(ChatColor.YELLOW + "Fire Leapers Practice");
        footer.setScore(1);

        // Set scoreboard for each player
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            player.setScoreboard(scoreboard);
        }
    }
    public Map<Player, Integer> getPlayerScores() {
        return playerScores;
    }

    public void removePlayerScore(Player player) {
        playerScores.remove(player);
    }


}
