package com.example.marveldefence.ui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import com.example.marveldefence.R;

/** Decorative layers only. Caller applies the exact background transform and alpha. */
final class EnvironmentRenderer {
    private final EnvironmentScene scene;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private final Path wave = new Path(), cloud = new Path();
    private final RectF boatBounds = new RectF();
    private final Bitmap[] boats;
    EnvironmentRenderer(Resources resources,EnvironmentScene.Kind kind) {
        scene = new EnvironmentScene(kind);
        paint.setStrokeCap(Paint.Cap.ROUND);
        cloud.addOval(-65,-10,65,22,Path.Direction.CW);
        cloud.addCircle(-27,-7,26,Path.Direction.CW);
        cloud.addCircle(8,-17,32,Path.Direction.CW);
        cloud.addCircle(37,-4,24,Path.Direction.CW);
        boats = scene.boats ? new Bitmap[]{BitmapFactory.decodeResource(resources,R.drawable.environment_boat_one),
            BitmapFactory.decodeResource(resources,R.drawable.environment_boat_two)} : null;
    }
    void draw(Canvas c,double seconds) {
        c.save(); c.clipPath(scene.water);
        paint.setColor(0xffb9f4ff); paint.setAlpha(EnvironmentScene.WATER_ALPHA);
        paint.setStyle(Paint.Style.STROKE); paint.setStrokeWidth(2);
        // Stable grid, small drift and phase changes; clipped before any drawing.
        for (int row=0;row<EnvironmentScene.WAVE_ROWS;row++) for (int column=0;column<EnvironmentScene.WAVE_COLUMNS;column++) {
            float x = column*64 + (row%2)*27;
            float y = row*53 + 12;
            float shift = (float)Math.sin(seconds*.45 + row*.8 + column)*7;
            float phase = (float)((seconds*EnvironmentScene.WAVE_SPEED + column*11)%40);
            paint.setAlpha((int)(EnvironmentScene.WATER_ALPHA*Math.sin(phase*Math.PI/40)));
            wave.rewind(); wave.moveTo(x+shift,y+phase);
            wave.cubicTo(x+shift+8,y+phase-3,x+shift+16,y+phase+3,x+shift+27,y+phase);
            c.drawPath(wave,paint);
        }
        paint.setStyle(Paint.Style.FILL);
        for (int i=0;i<EnvironmentScene.SPARKLES;i++) {
            float pulse = (float)Math.max(0,Math.sin(seconds*.7+i*1.9));
            paint.setAlpha((int)(pulse*22));
            c.drawCircle(65+i*89,125+(i*137)%800,1.5f,paint);
        }
        c.restore();
        if (boats != null) drawBoats(c,seconds);
        c.save(); c.clipPath(scene.clouds);
        paint.setStyle(Paint.Style.FILL); paint.setColor(0xffffffff); paint.setAlpha(EnvironmentScene.CLOUD_ALPHA);
        for (int i=0;i<EnvironmentScene.CLOUD_COUNT;i++) {
            // The wrap occurs outside the source image. Clipping protects all information.
            float x = (float)(((seconds/EnvironmentScene.CLOUD_PERIOD+i*.5)%1)*1972-150);
            float y = i==0 ? scene.cloudHeight : 870;
            c.save(); c.translate(x,y); c.scale(1.15f,.7f); c.drawPath(cloud,paint); c.restore();
        }
        c.restore(); paint.setAlpha(255);
    }
    private void drawBoats(Canvas c,double seconds) {
        for (int i=0;i<boats.length;i++) {
            double phase = seconds*Math.PI*2/EnvironmentScene.BOAT_PERIOD+i*Math.PI;
            // Full sprite bounds stay in the lower exterior river, clear of pier/plaque.
            float[] loop = EnvironmentScene.BOAT_LOOPS[i];
            float x = loop[0]+(float)Math.cos(phase)*EnvironmentScene.BOAT_DRIFT_X;
            float bottom = loop[1]+(float)Math.sin(phase)*EnvironmentScene.BOAT_DRIFT_Y;
            float width = loop[2];
            float height = width*boats[i].getHeight()/boats[i].getWidth();
            paint.setColor(0xffb9f4ff); paint.setAlpha(40); paint.setStyle(Paint.Style.STROKE); paint.setStrokeWidth(2);
            c.drawOval(x-width*.55f,bottom-7,x+width*.6f,bottom+9,paint);
            paint.setStyle(Paint.Style.FILL); paint.setAlpha(255);
            boatBounds.set(-width/2,-height,width/2,0);
            c.save(); c.translate(x,bottom); c.rotate((float)Math.sin(phase*2)*EnvironmentScene.BOAT_ROCK,0,-height*.25f);
            c.drawBitmap(boats[i],null,boatBounds,paint); c.restore();
        }
    }
}
