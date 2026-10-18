package com.example.marveldefence.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Geometria traçada em pixels sobre manhattan_map.png e convertida uniformemente para unidades do jogo. */
public final class MapData {
    public static final double IMAGE_WIDTH = 1672, IMAGE_HEIGHT = 941;
    private static final double PIXEL_SCALE = 1000 / IMAGE_WIDTH;
    public static final class Point {
        public final double x, y;
        public Point(double x, double y) { this.x = x; this.y = y; }
    }
    public static final class Obstacle {
        public final double x, y, radius;
        public final String kind;
        public Obstacle(double x, double y, double radius, String kind) {
            this.x = x; this.y = y; this.radius = radius; this.kind = kind;
        }
    }
    /** Bloqueia toda a área ocupada pelo herói, inclusive bordas de polígonos e limites côncavos da água. */
    public static final class BlockedArea {
        public final String kind;
        public final List<Point> vertices;
        public BlockedArea(String kind, List<Point> vertices) {
            this.kind = kind;
            this.vertices = Collections.unmodifiableList(new ArrayList<>(vertices));
        }
        public boolean intersects(double x, double y, double radius) {
            boolean inside = false;
            for (int i = 0, j = vertices.size()-1; i < vertices.size(); j = i++) {
                Point a = vertices.get(j), b = vertices.get(i);
                if (segmentDistance(x,y,a,b) <= radius) return true;
                if ((a.y > y) != (b.y > y)
                        && x < (b.x-a.x)*(y-a.y)/(b.y-a.y)+a.x) inside = !inside;
            }
            return inside;
        }
    }
    public final double width, height, pathWidth, length;
    public final List<Point> path;
    public final List<Obstacle> obstacles;
    public final List<BlockedArea> blockedAreas;

    public MapData() {
        this(1000, IMAGE_HEIGHT*PIXEL_SCALE, 68, towerRoute(),
            Arrays.asList(
                tree(112,48,42), tree(320,128,39), tree(580,162,48),
                tree(725,60,37), tree(1070,155,48), tree(1300,258,42),
                tree(1612,418,47), tree(605,425,34), tree(876,414,43),
                tree(939,466,36), tree(975,513,28), tree(125,520,47),
                tree(450,728,49), tree(1340,630,53), tree(1608,550,41),
                tree(1530,740,39), tree(1640,745,51), tree(1410,854,49),
                tree(1030,910,47), tree(1520,635,48)),
            Arrays.asList(
                area("water",0,548,95,638,272,736,502,853,700,941,0,941),
                area("water",0,0,380,0,220,110,0,180),
                area("water",1030,0,1500,0,1450,135,1330,65,1240,30,1100,95,1040,95),
                area("building",325,23,390,0,553,0,620,67,605,168,400,211,345,157),
                area("building",750,0,1030,0,1065,48,1030,147,900,205,760,121),
                area("building",1100,77,1240,9,1405,112,1395,204,1250,275,1110,196),
                area("tower",1465,0,1672,0,1672,408,1552,399,1540,339,1425,225),
                area("building",1020,797,1150,690,1355,795,1355,941,1030,941),
                area("island",817,422,835,391,870,397,943,442,1004,499,1010,526,968,549,935,543,859,471),
                area("portal",0,60,143,35,215,123,250,257,201,319,75,379,0,360),
                area("car",175,593,211,572,283,630,258,657,199,632),
                area("car",498,758,538,737,612,790,582,825,520,798),
                area("dock",469,883,511,869,567,905,550,941,497,941)));
    }

    /** A geometria explícita também permite mapas futuros e cenários determinísticos nos testes do motor. */
    public MapData(double width, double height, double pathWidth, List<Point> path,
                   List<Obstacle> obstacles, List<BlockedArea> blockedAreas) {
        this.width = width; this.height = height; this.pathWidth = pathWidth;
        this.path = Collections.unmodifiableList(new ArrayList<>(path));
        this.obstacles = Collections.unmodifiableList(new ArrayList<>(obstacles));
        this.blockedAreas = Collections.unmodifiableList(new ArrayList<>(blockedAreas));
        double total = 0;
        for (int i = 1; i < path.size(); i++) total += distance(path.get(i-1),path.get(i));
        length = total;
    }

    private static List<Point> towerRoute() {
        List<Point> points = new ArrayList<>();
        points.add(pixel(148,287));
        // Os pontos de controle das curvas seguem o centro da rua: portal, volta inferior, volta superior e porta da torre.
        curve(points,185,335,255,326,345,333);
        curve(points,465,333,456,407,454,466);
        curve(points,450,563,498,629,650,638);
        curve(points,825,652,890,591,797,492);
        curve(points,759,450,706,426,710,384);
        curve(points,711,316,782,282,854,300);
        curve(points,942,301,995,382,1042,437);
        curve(points,1101,523,1200,515,1260,481);
        curve(points,1335,441,1430,391,1502,348);
        return points;
    }
    private static void curve(List<Point> points, double ax,double ay,double bx,double by,double cx,double cy) {
        Point start = points.get(points.size()-1), a = pixel(ax,ay), b = pixel(bx,by), end = pixel(cx,cy);
        for (int i = 1; i <= 24; i++) {
            double t = i/24.0, s = 1-t;
            points.add(new Point(s*s*s*start.x+3*s*s*t*a.x+3*s*t*t*b.x+t*t*t*end.x,
                s*s*s*start.y+3*s*s*t*a.y+3*s*t*t*b.y+t*t*t*end.y));
        }
    }
    private static Point pixel(double x,double y) { return new Point(x*PIXEL_SCALE,y*PIXEL_SCALE); }
    private static Obstacle tree(double x,double y,double radius) {
        return new Obstacle(x*PIXEL_SCALE,y*PIXEL_SCALE,radius*PIXEL_SCALE,"tree");
    }
    private static BlockedArea area(String kind, double... coordinates) {
        List<Point> vertices = new ArrayList<>();
        for (int i = 0; i < coordinates.length; i += 2) vertices.add(pixel(coordinates[i],coordinates[i+1]));
        return new BlockedArea(kind,vertices);
    }
    public boolean blocked(double x, double y, double radius) {
        for (Obstacle o : obstacles) if (Math.hypot(x-o.x,y-o.y) <= radius+o.radius) return true;
        for (BlockedArea area : blockedAreas) if (area.intersects(x,y,radius)) return true;
        return false;
    }
    public Point position(double progress) {
        double remaining = Math.max(0,progress);
        for (int i = 1; i < path.size(); i++) {
            Point a = path.get(i-1), b = path.get(i);
            double segment = distance(a,b);
            if (segment == 0) continue;
            if (remaining <= segment) {
                double t = remaining/segment;
                return new Point(a.x+(b.x-a.x)*t,a.y+(b.y-a.y)*t);
            }
            remaining -= segment;
        }
        return path.get(path.size()-1);
    }
    public double distanceToPath(double x,double y) {
        double best = Double.POSITIVE_INFINITY;
        for (int i = 1; i < path.size(); i++) best = Math.min(best,segmentDistance(x,y,path.get(i-1),path.get(i)));
        return best;
    }
    private static double segmentDistance(double x,double y,Point a,Point b) {
        double dx = b.x-a.x, dy = b.y-a.y, squared = dx*dx+dy*dy;
        double t = squared == 0 ? 0 : Math.max(0,Math.min(1,((x-a.x)*dx+(y-a.y)*dy)/squared));
        return Math.hypot(x-a.x-t*dx,y-a.y-t*dy);
    }
    private static double distance(Point a,Point b) { return Math.hypot(b.x-a.x,b.y-a.y); }
}

