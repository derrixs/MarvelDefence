package com.example.marveldefence.game;

import static org.junit.Assert.*;

import java.util.Arrays;
import org.junit.Test;

public class GameEngineTest {
    // Os cenários de combate existentes mantêm sua geometria sintética calibrada.
    // Eles não validam a arte nem o trajeto do novo mapa de produção.
    private MapData fixtureMap() {
        MapData.Point[] corners = {new MapData.Point(0, 215), new MapData.Point(280, 215),
            new MapData.Point(280, 390), new MapData.Point(525, 390), new MapData.Point(525, 215),
            new MapData.Point(735, 215), new MapData.Point(735, 340), new MapData.Point(1000, 340)};
        java.util.List<MapData.Point> samples = new java.util.ArrayList<>();
        samples.add(corners[0]);
        for (int i = 1; i < corners.length - 1; i++) {
            MapData.Point a = corners[i - 1], b = corners[i], c = corners[i + 1];
            double ab = Math.hypot(b.x - a.x, b.y - a.y), bc = Math.hypot(c.x - b.x, c.y - b.y);
            double radius = Math.min(44, Math.min(ab, bc) / 3);
            MapData.Point enter = new MapData.Point(b.x + (a.x - b.x) * radius / ab, b.y + (a.y - b.y) * radius / ab);
            MapData.Point exit = new MapData.Point(b.x + (c.x - b.x) * radius / bc, b.y + (c.y - b.y) * radius / bc);
            samples.add(enter);
            for (int j = 1; j <= 10; j++) {
                double t = j / 10.0, s = 1 - t;
                samples.add(new MapData.Point(s * s * enter.x + 2 * s * t * b.x + t * t * exit.x,
                    s * s * enter.y + 2 * s * t * b.y + t * t * exit.y));
            }
        }
        samples.add(corners[corners.length - 1]);
        java.util.List<MapData.Obstacle> obstacles = Arrays.asList(
            new MapData.Obstacle(165, 90, 38, "tree"), new MapData.Obstacle(340, 98, 32, "tree"),
            new MapData.Obstacle(100, 480, 36, "tree"), new MapData.Obstacle(375, 492, 38, "tree"),
            new MapData.Obstacle(840, 85, 32, "tree"), new MapData.Obstacle(935, 125, 30, "tree"),
            new MapData.Obstacle(760, 468, 68, "pond"), new MapData.Obstacle(910, 485, 32, "rock"),
            new MapData.Obstacle(540, 95, 38, "building"), new MapData.Obstacle(978, 340, 52, "base"));
        return new MapData(1000, 600, 44, samples, obstacles, java.util.Collections.emptyList());
    }

    private GameEngine game() {
        return new GameEngine(fixtureMap(), Arrays.asList("iron_man", "spider_man", "hulk"));
    }
    private void advance(GameEngine game, double seconds) {
        for (int i = 0; i < (int) Math.round(seconds * 60); i++) game.update(GameConfig.FIXED_STEP);
    }
    @Test
    public void placementUsesPathEdgesObstaclesBoundsAndBand() {
        GameEngine game = game();
        assertEquals(GameEngine.Placement.VALID, game.placement("iron_man", 200, 145));
        assertEquals(GameEngine.Placement.PATH, game.placement("iron_man", 200, 215));
        assertEquals(GameEngine.Placement.PATH, game.placement("iron_man", 200, 165));
        assertEquals(GameEngine.Placement.TOO_FAR, game.placement("iron_man", 55, 560));
        assertEquals(GameEngine.Placement.OBSTACLE, game.placement("iron_man", 165, 90));
        assertEquals(GameEngine.Placement.OUTSIDE, game.placement("iron_man", -1, 150));
        assertEquals(GameEngine.Placement.OUTSIDE, game.placement("iron_man", Double.NaN, 150));
    }
    @Test
    public void invalidPlacementNeverSpendsAndValidPlacementChargesOnce() {
        GameEngine game = game();
        assertFalse(game.place("iron_man", 200, 215));
        assertEquals(1000, game.credits());
        assertTrue(game.place("iron_man", 200, 145));
        assertEquals(680, game.credits());
        assertFalse(game.place("iron_man", 400, 300));
        assertEquals(680, game.credits());
        assertEquals(GameEngine.Placement.OVERLAP, game.placement("spider_man", 220, 145));
        assertEquals(GameEngine.Placement.NOT_IN_TEAM, game.placement("thor", 650, 130));
    }
    @Test
    public void insufficientFundsDoNotPlaceUnit() {
        GameEngine game = new GameEngine(fixtureMap(), Arrays.asList("iron_man", "hulk", "thor"));
        assertTrue(game.place("iron_man", 200, 145));
        assertTrue(game.place("hulk", 400, 310));
        assertEquals(GameEngine.Placement.NO_CREDITS, game.placement("thor", 650, 130));
        assertFalse(game.place("thor", 650, 130));
        assertEquals(320, game.credits());
        assertEquals(2, game.units().size());
    }
    @Test
    public void damageUpgradeLimitsAndEffectsAreReal() {
        GameEngine game = game();
        game.place("iron_man", 200, 145);
        GameEngine.Unit unit = game.units().get(0);
        assertTrue(game.upgrade(unit, true));
        assertEquals(25.2, unit.damage(), .001);
        assertTrue(game.upgrade(unit, true));
        assertTrue(game.upgrade(unit, true));
        assertFalse(game.upgrade(unit, true));
        assertEquals(3, unit.damageLevel());
        assertEquals(20, game.credits());
        assertFalse(game.upgrade(unit, false));
        assertEquals(170, unit.range(), .001);
    }
    @Test
    public void rangeUpgradeIsIndependentAndSaleOnlyHappensOnce() {
        GameEngine game = game();
        game.place("iron_man", 200, 145);
        GameEngine.Unit unit = game.units().get(0);
        assertTrue(game.upgrade(unit, false));
        assertEquals(195, unit.range(), .001);
        assertEquals(18, unit.damage(), .001);
        assertEquals(287, unit.sellValue());
        assertTrue(game.sell(unit));
        assertEquals(877, game.credits());
        assertFalse(game.sell(unit));
        assertEquals(877, game.credits());
        assertTrue(game.place("iron_man", 200, 145));
        assertEquals(0, game.units().get(0).rangeLevel());
        assertFalse(game.upgrade(unit, true));
    }
    @Test
    public void pathDistanceAtCornersAndEndpointsIsConsistent() {
        MapData map = fixtureMap();
        assertEquals(0, map.distanceToPath(0, 215), .0001);
        for (MapData.Point p : map.path) assertEquals(0, map.distanceToPath(p.x, p.y), .0001);
        assertEquals(215, map.position(0).y, .0001);
        assertEquals(1000, map.position(map.length + 999).x, .0001);
        MapData.Point middle = map.position(500);
        assertTrue(middle.x >= 0 && middle.x <= 1000);
        assertEquals(50, map.distanceToPath(-50, 215), .0001);
    }
    @Test
    public void uniformTransformRoundTripsWideAndNarrowScreens() {
        for (double width : new double[] {560, 900, 1800}) {
            Viewport viewport = new Viewport();
            viewport.fit(width, 380, 1000, 600);
            assertEquals(735, viewport.worldX(viewport.screenX(735)), .000001);
            assertEquals(215, viewport.worldY(viewport.screenY(215)), .000001);
            assertEquals(viewport.scale, (viewport.screenX(100) - viewport.screenX(0)) / 100, .000001);
            assertEquals(viewport.scale, (viewport.screenY(100) - viewport.screenY(0)) / 100, .000001);
        }
    }
    @Test
    public void pausedSimulationDoesNotAdvance() {
        GameEngine game = game();
        game.startWave();
        advance(game, 2);
        double progress = game.enemies().get(0).progress();
        int count = game.enemies().size();
        game.setPaused(true);
        advance(game, 100);
        assertEquals(progress, game.enemies().get(0).progress(), .0001);
        assertEquals(count, game.enemies().size());
        game.setPaused(false);
        advance(game, 1);
        assertTrue(game.enemies().get(0).progress() > progress);
    }
    @Test
    public void equivalentTimeStepsProduceEquivalentTravel() {
        GameEngine fine = game(), coarse = game();
        fine.startWave();
        coarse.startWave();
        advance(fine, 4);
        for (int i = 0; i < 120; i++) coarse.update(1.0 / 30);
        // O instante de surgimento contribui com no máximo uma atualização de fase; o inimigo à frente é exato.
        assertEquals(fine.enemies().get(0).progress(), coarse.enemies().get(0).progress(), .00001);
        assertEquals(fine.wave(), coarse.wave());
    }
    @Test
    public void enemiesEscapingCauseDamageOnceAndNeverDropRewards() {
        GameEngine game = game();
        assertTrue(game.startWave());
        assertFalse(game.startWave());
        advance(game, 40);
        assertEquals(GameEngine.Status.READY, game.status());
        assertEquals(60, game.lives());
        assertEquals(1100, game.credits());
        assertEquals(0, game.kills());
        advance(game, 100);
        assertEquals(60, game.lives());
        assertEquals(1100, game.credits());
    }
    @Test
    public void heroesCannotAttackEnemiesOutsideTheirRange() {
        GameEngine game = game();
        assertTrue(game.place("hulk", 200, 145));
        game.startWave();
        advance(game, .4);
        assertEquals(game.enemies().get(0).maxHealth, game.enemies().get(0).health(), .00001);
        assertEquals(0, game.kills());
    }
    @Test
    public void automaticCombatPaysEachKillExactlyOnce() {
        GameEngine game = game();
        game.place("iron_man", 200, 145);
        game.startWave();
        advance(game, 40);
        assertTrue(game.kills() > 0);
        assertEquals(680 + game.kills() * 18 + 100, game.credits());
        int credits = game.credits();
        advance(game, 5);
        assertEquals(credits, game.credits());
    }
    @Test
    public void attackIntervalAndFirstTargetPriorityAreRespected() {
        GameEngine game = game();
        game.place("iron_man", 200, 145);
        game.startWave();
        advance(game, 1);
        assertEquals(27, game.enemies().get(0).health(), .00001);
        assertEquals(45, game.enemies().get(1).health(), .00001);
        advance(game, .4);
        assertEquals(9, game.enemies().get(0).health(), .00001);
        assertEquals(45, game.enemies().get(1).health(), .00001);
        assertEquals(0, game.kills());
    }
    @Test
    public void spiderAppliesTemporarySlow() {
        GameEngine game = game();
        assertTrue(game.place("spider_man", 200, 145));
        game.startWave();
        advance(game, 2);
        assertTrue(game.enemies().stream().anyMatch(GameEngine.Enemy::slowed));
        GameEngine.Unit unit = game.units().get(0);
        game.sell(unit);
        advance(game, 2);
        assertFalse(game.enemies().stream().anyMatch(GameEngine.Enemy::slowed));
    }
    @Test
    public void lossBlocksUpdatesAndNewGameIsClean() {
        GameEngine game = game();
        game.startWave();
        advance(game, 40);
        game.startWave();
        advance(game, 40);
        assertEquals(GameEngine.Status.LOST, game.status());
        assertEquals(0, game.lives());
        // A última fuga coincide com o fim da onda: a derrota tem prioridade e não concede bônus.
        assertEquals(1100, game.credits());
        int credits = game.credits();
        double time = game.simulationTime();
        advance(game, 20);
        assertEquals(credits, game.credits());
        assertEquals(time, game.simulationTime(), .00001);
        assertFalse(game.startWave());
        assertFalse(game.place("iron_man", 200, 145));
        GameEngine fresh = game();
        assertEquals(100, fresh.lives());
        assertEquals(1000, fresh.credits());
        assertEquals(0, fresh.wave());
        assertTrue(fresh.units().isEmpty());
        assertTrue(fresh.enemies().isEmpty());
    }
    @Test
    public void fiveWavesEndInVictory() {
        GameEngine game = new GameEngine(fixtureMap(), Arrays.asList("iron_man", "thor", "hulk"));
        assertTrue(game.place("iron_man", 200, 145));
        assertTrue(game.place("hulk", 450, 310));
        for (int wave = 0; wave < 5; wave++) {
            assertTrue(game.startWave());
            advance(game, 40);
            if (wave == 0) {
                assertTrue(game.place("thor", 650, 135));
                for (GameEngine.Unit unit : game.units()) {
                    game.upgrade(unit, true);
                    game.upgrade(unit, false);
                }
            }
            if (wave > 0 && wave < 4)
                for (GameEngine.Unit unit : game.units()) game.upgrade(unit, true);
        }
        assertEquals(GameEngine.Status.WON, game.status());
        assertTrue(game.lives() > 0);
        int credits = game.credits();
        advance(game, 40);
        assertEquals(credits, game.credits());
    }
}
