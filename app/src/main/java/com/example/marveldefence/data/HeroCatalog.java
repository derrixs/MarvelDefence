package com.example.marveldefence.data;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Balanceamento provisório editável. Os metadados da Comic Vine não alteram estes valores de combate. */
public final class HeroCatalog {
    public enum Attack { SINGLE,
        AREA,
        SLOW }
    public static final class Hero {
        public final String id, name, role;
        public final int comicVineId, color, cost;
        public final double damage, range, interval;
        public final Attack attack;
        Hero(String id, int apiId, String name, String role, int color, int cost,
            double damage, double range, double interval, Attack attack) {
            this.id = id;
            this.comicVineId = apiId;
            this.name = name;
            this.role = role;
            this.color = color;
            this.cost = cost;
            this.damage = damage;
            this.range = range;
            this.interval = interval;
            this.attack = attack;
        }
    }
    public static final List<Hero> ALL = Collections.unmodifiableList(Arrays.asList(
        new Hero("iron_man", 1455, "Homem de Ferro", "Repulsor • alvo único", 0xffe65140, 320, 18, 170, .7, Attack.SINGLE),
        new Hero("spider_man", 1443, "Homem-Aranha", "Teia • desaceleração", 0xffef5265, 250, 8, 145, .65, Attack.SLOW),
        new Hero("hulk", 2267, "Hulk", "Impacto • área próxima", 0xff78bd46, 360, 32, 100, 1.2, Attack.AREA),
        new Hero("thor", 2268, "Thor", "Trovão • área", 0xff82c9ef, 380, 22, 160, 1.0, Attack.AREA),
        new Hero("captain", 1442, "Capitão América", "Escudo • alvo único", 0xff498dde, 270, 16, 145, .8, Attack.SINGLE),
        new Hero("widow", 3200, "Viúva Negra", "Precisão • ataque rápido", 0xffbd80ec, 220, 10, 150, .4, Attack.SINGLE)));
    public static Hero find(String id) {
        for (Hero hero : ALL)
            if (hero.id.equals(id))
                return hero;
        throw new IllegalArgumentException("Unknown demo hero");
    }
    private HeroCatalog() {}
}
