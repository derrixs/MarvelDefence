package com.example.marveldefence.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.example.marveldefence.R;
import com.example.marveldefence.data.HeroCatalog;
import com.example.marveldefence.game.GameEngine;
import com.example.marveldefence.game.MapData;
import com.example.marveldefence.game.Viewport;

/** Apenas desenha; a simulação e seu tempo pertencem à Activity e ao motor em Java puro. */
public final class BattlefieldView extends View {
    public interface TouchListener { void touched(double x, double y); }
    private MapData map = new MapData();
    private final Viewport viewport = new Viewport();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final HeroRenderer heroes = new HeroRenderer();
    private final PortalRenderer portal = new PortalRenderer();
    private final VisualClock visualClock = new VisualClock(seconds -> {
        visualSeconds = seconds; portalDegrees = (float)((seconds%6)*60); invalidate();
    });
    private double visualSeconds;
    private boolean visualForeground = true;
    private EnvironmentRenderer environment;
    private float portalDegrees;
    private final Paint terrainPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private final RectF terrainBounds = new RectF();
    private Bitmap terrain;
    private GameEngine engine;
    private TouchListener listener;
    private String pendingHero;
    private double pendingX, pendingY;
    private boolean hasPosition;
    private GameEngine.Unit selected;
    public BattlefieldView(Context context, AttributeSet attrs) {
        super(context,attrs); setFocusable(true);
        terrain = BitmapFactory.decodeResource(getResources(),R.drawable.manhattan_map);
        environment = new EnvironmentRenderer(getResources(),EnvironmentScene.Kind.BATTLE);
    }
    public void bind(GameEngine engine, TouchListener listener) {
        this.engine = engine; this.map = engine.map; this.listener = listener; fit(); invalidate();
    }
    public void pending(String id, double x, double y, boolean positioned) {
        pendingHero = id; pendingX = x; pendingY = y; hasPosition = positioned; invalidate();
    }
    public void selected(GameEngine.Unit unit) { selected = unit; invalidate(); }
    public void visualForeground(boolean foreground) { visualForeground = foreground; syncPortalAnimation(); }
    private void fit() {
        if (engine==null) viewport.fit(getWidth(),Math.max(1,getHeight()),map.width,map.height);
        else viewport.cover(getWidth(),Math.max(1,getHeight()),map.width,map.height);
        terrainBounds.set(0,0,(float)map.width,(float)map.height);
    }
    @Override protected void onSizeChanged(int w,int h,int oldw,int oldh) { fit(); }
    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas); canvas.drawColor(0xff091928);
        canvas.save(); canvas.translate((float)viewport.left,(float)viewport.top);
        canvas.scale((float)viewport.scale,(float)viewport.scale);
        canvas.clipRect(terrainBounds);
        canvas.drawBitmap(terrain,null,terrainBounds,terrainPaint);
        canvas.save(); canvas.scale((float)(map.width/EnvironmentScene.WIDTH),(float)(map.height/EnvironmentScene.HEIGHT));
        environment.draw(canvas,visualSeconds); canvas.restore();
        portal.draw(canvas,portalDegrees);
        if (engine != null) {
            if (selected != null) range(canvas,selected.x,selected.y,selected.range(),0x337bdccd,0xffa6f5e0);
            if (pendingHero != null && hasPosition) {
                boolean valid = engine.placement(pendingHero,pendingX,pendingY) == GameEngine.Placement.VALID;
                range(canvas,pendingX,pendingY,HeroCatalog.find(pendingHero).range,valid ? 0x4460d98c : 0x44ef5265,valid ? 0xffb3f18c : 0xffff6f82);
            }
            for (GameEngine.Enemy enemy : engine.enemies()) drawDrone(canvas,enemy);
            for (GameEngine.Unit unit : engine.units()) heroes.draw(canvas,unit.hero,(float)unit.x,(float)unit.y+2,13);
            for (GameEngine.Effect effect : engine.effects()) {
                paint.setColor(effect.color); paint.setStrokeWidth(4); paint.setStyle(Paint.Style.STROKE);
                canvas.drawLine((float)effect.x1,(float)effect.y1,(float)effect.x2,(float)effect.y2,paint);
                paint.setStyle(Paint.Style.FILL); canvas.drawCircle((float)effect.x2,(float)effect.y2,7,paint);
            }
            if (pendingHero != null && hasPosition) heroes.draw(canvas,HeroCatalog.find(pendingHero),(float)pendingX,(float)pendingY+2,13);
        }
        paint.setStyle(Paint.Style.FILL); canvas.restore();
    }
    private void drawDrone(Canvas c,GameEngine.Enemy e) {
        float x = (float)e.x(), y = (float)e.y(), r = e.type == 2 ? 16 : 12;
        fill(0x44445244); c.drawOval(x-r-2,y+8,x+r+2,y+19,paint);
        fill(0xff334553); c.drawRoundRect(x-r-7,y-4,x+r+7,y+4,3,3,paint);
        fill(e.type == 2 ? 0xff7c658e : e.type == 1 ? 0xffe4b451 : 0xffe36c57); c.drawCircle(x,y,r,paint);
        fill(0xff193844); c.drawRoundRect(x-7,y-3,x+7,y+3,2,2,paint); fill(0xfff4e4a4); c.drawCircle(x,y,3,paint);
        fill(0xff1e3e3a); c.drawRoundRect(x-16,y-24,x+16,y-20,2,2,paint);
        fill(e.slowed() ? 0xff9ce4f1 : 0xffa0e387); c.drawRoundRect(x-16,y-24,x-16+32*(float)Math.max(0,e.health()/e.maxHealth),y-20,2,2,paint);
    }
    private void range(Canvas c,double x,double y,double radius,int inside,int border) {
        fill(inside); c.drawCircle((float)x,(float)y,(float)radius,paint);
        paint.setStyle(Paint.Style.STROKE); paint.setStrokeWidth(2); paint.setColor(border); c.drawCircle((float)x,(float)y,(float)radius,paint);
        paint.setStyle(Paint.Style.FILL);
    }
    private void fill(int color) { paint.setStyle(Paint.Style.FILL); paint.setColor(color); }
    @Override public boolean onTouchEvent(MotionEvent event) {
        if (listener == null || engine == null || engine.finished()) return false;
        double x = viewport.worldX(event.getX()), y = viewport.worldY(event.getY());
        boolean inside = x>=0 && y>=0 && x<=map.width && y<=map.height;
        if (event.getAction() == MotionEvent.ACTION_DOWN) return inside;
        if (event.getAction() == MotionEvent.ACTION_UP) {
            performClick(); if (inside) listener.touched(x,y); return true;
        }
        return true;
    }
    @Override public boolean performClick() { super.performClick(); return true; }
    private void syncPortalAnimation() {
        // Os callbacks de visibilidade podem chegar durante a construção da View, antes da inicialização dos campos.
        if (visualClock == null) return;
        boolean visible = visualForeground && isAttachedToWindow() && isShown()
            && getWindowVisibility() == VISIBLE && hasWindowFocus();
        visualClock.running(visible);
    }
    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (terrain==null) terrain = BitmapFactory.decodeResource(getResources(),R.drawable.manhattan_map);
        syncPortalAnimation();
    }
    @Override protected void onVisibilityChanged(View changedView,int visibility) {
        super.onVisibilityChanged(changedView,visibility); syncPortalAnimation();
    }
    @Override protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility); syncPortalAnimation();
    }
    @Override public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus); syncPortalAnimation();
    }
    @Override protected void onDetachedFromWindow() {
        visualClock.running(false);
        super.onDetachedFromWindow(); if (terrain != null) { terrain.recycle(); terrain = null; }
    }
}

