package com.example.marveldefence.ui;

import android.graphics.Path;

/** Visual masks traced independently from gameplay collision geometry, in source pixels. */
final class EnvironmentScene {
    enum Kind { TOWER, BASE, AIRPORT, BATTLE }
    static final float WIDTH = 1672, HEIGHT = 941;
    static final int WATER_ALPHA = 28, CLOUD_ALPHA = 32;
    static final float WAVE_SPEED = 7, CLOUD_PERIOD = 90, BOAT_PERIOD = 40;
    static final int WAVE_ROWS = 19, WAVE_COLUMNS = 28, SPARKLES = 18, CLOUD_COUNT = 2;
    // x, bottom y, width; 23 x 7 pixel elliptical loops, 1.2 degree rocking.
    static final float[][] BOAT_LOOPS = {{155,865,95},{320,927,100}};
    static final float BOAT_DRIFT_X = 23, BOAT_DRIFT_Y = 7, BOAT_ROCK = 1.2f;
    final Path water = new Path(), clouds = new Path();
    final boolean boats;
    final float cloudHeight;
    EnvironmentScene(Kind kind) {
        boats = kind == Kind.TOWER;
        cloudHeight = kind == Kind.AIRPORT ? 230 : kind == Kind.BATTLE ? 870 : 92;
        if (kind == Kind.TOWER) {
            polygon(water,0,615,75,647,90,724,248,771,350,831,560,928,565,941,0,941);
            polygon(water,1120,0,1340,0,1320,145,1260,195,1210,160,1170,85);
            polygon(water,1260,560,1300,575,1340,690,1350,785,1470,941,1550,941,1510,820,1440,650,1400,565,1370,540,1355,500);
            // Pond interior only; the island, shore and tree canopy are deliberately excluded.
            polygon(water,820,520,900,535,914,563,975,582,1015,563,1040,496,1080,508,1100,556,1055,594,944,605,840,576);
            polygon(clouds,1100,0,1350,0,1290,145,1190,115);
        } else if (kind == Kind.BASE) {
            polygon(water,78,0,155,0,211,145,185,255,152,281,101,245,125,135);
            polygon(water,185,438,226,454,263,570,290,686,221,750,208,668,179,575,158,519);
            polygon(water,298,835,325,915,340,941,204,941,226,877,246,830,269,858);
            polygon(clouds,70,0,160,0,205,150,165,240,95,210);
        } else if (kind == Kind.AIRPORT) {
            // Empty water mask: this scene has no water.
            polygon(clouds,0,100,65,100,65,440,0,440);
        } else {
            polygon(water,0,670,90,723,240,817,405,899,436,941,0,941);
            polygon(water,155,0,310,0,218,70,198,93,155,99);
            polygon(water,1090,0,1370,0,1330,45,1250,17,1120,55);
            // No cloud can enter any placement area or the road; only lower open river.
            polygon(clouds,0,805,135,805,330,930,335,941,0,941);
        }
    }
    private static void polygon(Path path,float... xy) {
        path.moveTo(xy[0],xy[1]);
        for (int i=2;i<xy.length;i+=2) path.lineTo(xy[i],xy[i+1]);
        path.close();
    }
}
