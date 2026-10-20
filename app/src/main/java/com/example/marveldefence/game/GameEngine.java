package com.example.marveldefence.game;

import com.example.marveldefence.data.HeroCatalog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** A thread da interface é a única responsável pelo estado. A simulação não depende do Android nem da rede. */
public final class GameEngine {
    public enum Status { READY,
        RUNNING,
        WON,
        LOST }
    public enum Placement { VALID,
        OUTSIDE,
        PATH,
        TOO_FAR,
        OBSTACLE,
        OVERLAP,
        NOT_IN_TEAM,
        ALREADY_PLACED,
        NO_CREDITS,
        FINISHED }
    public static final class Unit {
        public final HeroCatalog.Hero hero;
        public final double x, y;
        private int damageLevel, rangeLevel, invested;
        private double cooldown;
        Unit(HeroCatalog.Hero hero, double x, double y) {
            this.hero = hero;
            this.x = x;
            this.y = y;
            invested = hero.cost;
        }
        public double damage() {
            return hero.damage * (1 + .4 * damageLevel);
        }
        public double range() {
            return hero.range + 25 * rangeLevel;
        }
        public int damageLevel() {
            return damageLevel;
        }
        public int rangeLevel() {
            return rangeLevel;
        }
        public int sellValue() {
            return (int) Math.floor(invested * GameConfig.SELL_FRACTION);
        }
    }
    public static final class Enemy {
        public final int type;
        public final double maxHealth, speed;
        public final int reward, escapeDamage;
        private double health, progress, slowRemaining;
        private double x, y;
        Enemy(int type, int wave, MapData map) {
            this.type = type;
            maxHealth = (type == 2 ? 125 : type == 1 ? 28
                                                     : 45)
                * (1 + wave * .22);
            health = maxHealth;
            speed = type == 1 ? 120 : type == 2 ? 48
                                                : 72;
            reward = type == 2 ? 32 : 18;
            escapeDamage = type == 2 ? 12 : 5;
            MapData.Point p = map.position(0);
            x = p.x;
            y = p.y;
        }
        public double x() {
            return x;
        }
        public double y() {
            return y;
        }
        public double health() {
            return health;
        }
        public double progress() {
            return progress;
        }
        public boolean slowed() {
            return slowRemaining > 0;
        }
    }
    public static final class Effect {
        public final double x1, y1, x2, y2;
        public final int color;
        private double remaining = .16;
        Effect(Unit unit, Enemy target) {
            x1 = unit.x;
            y1 = unit.y;
            x2 = target.x;
            y2 = target.y;
            color = unit.hero.color;
        }
    }
    public final MapData map;
    private final Set<String> team;
    private final List<Unit> units = new ArrayList<>();
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Effect> effects = new ArrayList<>();
    private final List<Unit> unitView = Collections.unmodifiableList(units);
    private final List<Enemy> enemyView = Collections.unmodifiableList(enemies);
    private final List<Effect> effectView = Collections.unmodifiableList(effects);
    private Status status = Status.READY;
    private int lives = GameConfig.INITIAL_LIVES, credits = GameConfig.INITIAL_CREDITS;
    private int wave, spawned, kills;
    private double spawnTimer, simulationTime;
    private boolean paused;
    public GameEngine(MapData map, List<String> selectedTeam) {
        if (selectedTeam.isEmpty() || selectedTeam.size() > GameConfig.TEAM_LIMIT
            || new HashSet<>(selectedTeam).size() != selectedTeam.size())
            throw new IllegalArgumentException("Invalid team");
        for (String id : selectedTeam) HeroCatalog.find(id);
        this.map = map;
        team = Collections.unmodifiableSet(new HashSet<>(selectedTeam));
    }
    public List<Unit> units() {
        return unitView;
    }
    public List<Enemy> enemies() {
        return enemyView;
    }
    public List<Effect> effects() {
        return effectView;
    }
    public Status status() {
        return status;
    }
    public int lives() {
        return lives;
    }
    public int credits() {
        return credits;
    }
    public int wave() {
        return wave;
    }
    public int kills() {
        return kills;
    }
    public double simulationTime() {
        return simulationTime;
    }
    public boolean paused() {
        return paused;
    }
    public boolean finished() {
        return status == Status.WON || status == Status.LOST;
    }
    public void setPaused(boolean value) {
        paused = value;
    }
    public boolean placed(String id) {
        for (Unit u : units)
            if (u.hero.id.equals(id))
                return true;
        return false;
    }
    public Placement placement(String id, double x, double y) {
        if (finished())
            return Placement.FINISHED;
        if (!team.contains(id))
            return Placement.NOT_IN_TEAM;
        if (placed(id))
            return Placement.ALREADY_PLACED;
        double r = GameConfig.HERO_RADIUS;
        if (!Double.isFinite(x) || !Double.isFinite(y) || x < r || y < r || x > map.width - r || y > map.height - r)
            return Placement.OUTSIDE;
        double d = map.distanceToPath(x, y);
        if (d < map.pathWidth / 2 + r + GameConfig.PATH_MARGIN)
            return Placement.PATH;
        if (d > map.pathWidth / 2 + GameConfig.PLACEMENT_BAND)
            return Placement.TOO_FAR;
        if (map.blocked(x, y, r))
            return Placement.OBSTACLE;
        for (Unit u : units)
            if (Math.hypot(x - u.x, y - u.y) < 2 * r + GameConfig.PATH_MARGIN)
                return Placement.OVERLAP;
        if (credits < HeroCatalog.find(id).cost)
            return Placement.NO_CREDITS;
        return Placement.VALID;
    }
    public boolean place(String id, double x, double y) {
        if (placement(id, x, y) != Placement.VALID)
            return false;
        HeroCatalog.Hero hero = HeroCatalog.find(id);
        credits -= hero.cost;
        units.add(new Unit(hero, x, y));
        return true;
    }
    public boolean upgrade(Unit unit, boolean damage) {
        if (finished() || !units.contains(unit))
            return false;
        int level = damage ? unit.damageLevel : unit.rangeLevel;
        int cost = damage ? GameConfig.damageCost(level) : GameConfig.rangeCost(level);
        if (level >= GameConfig.UPGRADE_LIMIT || credits < cost)
            return false;
        credits -= cost;
        unit.invested += cost;
        if (damage)
            unit.damageLevel++;
        else
            unit.rangeLevel++;
        return true;
    }
    public boolean sell(Unit unit) {
        if (finished() || !units.remove(unit))
            return false;
        credits += unit.sellValue();
        return true;
    }
    public boolean startWave() {
        if (status != Status.READY || wave >= GameConfig.WAVE_COUNTS.length)
            return false;
        wave++;
        spawned = 0;
        spawnTimer = 0;
        paused = false;
        status = Status.RUNNING;
        return true;
    }
    public void update(double dt) {
        if (paused || status != Status.RUNNING || !Double.isFinite(dt) || dt <= 0)
            return;
        simulationTime += dt;
        for (int i = effects.size() - 1; i >= 0; i--)
            if ((effects.get(i).remaining -= dt) <= 0)
                effects.remove(i);
        spawnTimer -= dt;
        while (spawned < GameConfig.WAVE_COUNTS[wave - 1] && spawnTimer <= 0) {
            int type = wave == 1 ? 0 : wave >= 3 && spawned % 4 == 3 ? 2
                : spawned % 3 == 1                                   ? 1
                                                                     : 0;
            enemies.add(new Enemy(type, wave - 1, map));
            spawned++;
            spawnTimer += GameConfig.SPAWN_INTERVALS[wave - 1];
        }
        for (Unit unit : units) {
            unit.cooldown = Math.max(0, unit.cooldown - dt);
            if (unit.cooldown > 0)
                continue;
            Enemy target = null;
            for (Enemy e : enemies)
                if (e.health > 0 && Math.hypot(e.x - unit.x, e.y - unit.y) <= unit.range()
                    && (target == null || e.progress > target.progress))
                    target = e;
            if (target == null)
                continue;
            effects.add(new Effect(unit, target));
            if (unit.hero.attack == HeroCatalog.Attack.AREA) {
                for (Enemy e : enemies)
                    if (e.health > 0 && Math.hypot(e.x - target.x, e.y - target.y) <= 48
                        && Math.hypot(e.x - unit.x, e.y - unit.y) <= unit.range())
                        e.health -= unit.damage();
            } else {
                target.health -= unit.damage();
                // Aplicar lentidão novamente renova a duração, sem acumular o multiplicador.
                if (unit.hero.attack == HeroCatalog.Attack.SLOW)
                    target.slowRemaining = GameConfig.SLOW_DURATION;
            }
            unit.cooldown = unit.hero.interval;
        }
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy e = enemies.get(i);
            if (e.health <= 0) {
                credits += e.reward;
                kills++;
                enemies.remove(i);
                continue;
            }
            double slowedTime = Math.min(dt, e.slowRemaining);
            e.progress += e.speed * (slowedTime * GameConfig.SLOW_FACTOR + dt - slowedTime);
            e.slowRemaining = Math.max(0, e.slowRemaining - dt);
            MapData.Point point = map.position(e.progress);
            e.x = point.x;
            e.y = point.y;
            if (e.progress >= map.length) {
                lives -= e.escapeDamage;
                enemies.remove(i);
            }
        }
        if (lives <= 0) {
            lives = 0;
            status = Status.LOST;
            effects.clear();
            return;
        }
        if (spawned == GameConfig.WAVE_COUNTS[wave - 1] && enemies.isEmpty()) {
            credits += GameConfig.WAVE_BONUS;
            effects.clear();
            status = wave == GameConfig.WAVE_COUNTS.length ? Status.WON : Status.READY;
        }
    }
}
