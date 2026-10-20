package com.example.marveldefence.game;

/** Todos os valores numéricos aqui são regras provisórias do protótipo. */
public final class GameConfig {
    public static final int TEAM_LIMIT = 3, INITIAL_LIVES = 100, INITIAL_CREDITS = 1000;
    public static final int WAVE_BONUS = 100, UPGRADE_LIMIT = 3;
    public static final double HERO_RADIUS = 21, PATH_MARGIN = 9, PLACEMENT_BAND = 180;
    public static final double FIXED_STEP = 1.0 / 60, MAX_FRAME_TIME = .1;
    public static final double SELL_FRACTION = .7, SLOW_FACTOR = .55, SLOW_DURATION = 1.5;
    public static final int[] WAVE_COUNTS = {8, 12, 16, 20, 26};
    public static final double[] SPAWN_INTERVALS = {.85, .7, .6, .5, .42};
    public static int damageCost(int level) {
        return 120 + level * 100;
    }
    public static int rangeCost(int level) {
        return 90 + level * 80;
    }
    private GameConfig() {}
}
