package com.example.marveldefence.ui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import com.example.marveldefence.game.MapData;

/** Animação em Canvas dentro da abertura do portal; o mapa original permanece intacto. */
public final class PortalRenderer {
    private static final float IMAGE_X = 130, IMAGE_Y = 219;
    private static final float RADIUS_X = 43, RADIUS_Y = 66, TILT = 23;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path opening = new Path();
    private final Path spiral = new Path();
    private final Shader energy = new RadialGradient(0,0,1,
        new int[]{0xffeaffc9,0xffa6ff75,0xff45ed43,0xff20c831,0xff11912c},
        new float[]{0,.18f,.5f,.85f,1},Shader.TileMode.CLAMP);
    private final Shader ribbon = new RadialGradient(0,0,1,
        new int[]{0xfff4ffd6,0xffccffad,0xff83ff61,0x0059ff43},
        new float[]{0,.3f,.8f,1},Shader.TileMode.CLAMP);

    public PortalRenderer() {
        opening.addCircle(0,0,1,Path.Direction.CW);
        for (int i=0;i<=100;i++) {
            float radius = .055f+.91f*i/100;
            double angle = (1-radius)*Math.PI*2*1.18;
            float x = radius*(float)Math.cos(angle), y = radius*(float)Math.sin(angle);
            if (i==0) spiral.moveTo(x,y); else spiral.lineTo(x,y);
        }
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
    }

    public void draw(Canvas canvas, float degrees) {
        float scale = (float)(1000/MapData.IMAGE_WIDTH);
        canvas.save();
        canvas.translate(IMAGE_X*scale,IMAGE_Y*scale);
        canvas.rotate(TILT);
        canvas.scale(RADIUS_X*scale,RADIUS_Y*scale);
        canvas.clipPath(opening);
        paint.setStyle(Paint.Style.FILL); paint.setShader(energy); paint.setAlpha(255);
        canvas.drawCircle(0,0,1,paint);
        canvas.rotate(degrees);
        paint.setStyle(Paint.Style.STROKE); paint.setShader(ribbon);
        // Duas espirais luminosas mantêm a aparência de um portal girando.
        for (int arm=0;arm<2;arm++) {
            paint.setStrokeWidth(.18f); paint.setAlpha(65);
            canvas.drawPath(spiral,paint);
            paint.setStrokeWidth(.075f); paint.setAlpha(230);
            canvas.drawPath(spiral,paint);
            canvas.rotate(180);
        }
        paint.setShader(null); paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xffedffbe);
        float pulse = .075f+.01f*(float)Math.sin(Math.toRadians(degrees*2));
        canvas.drawCircle(0,0,pulse,paint);
        paint.setColor(0xccd6ff97);
        canvas.drawCircle(.60f,.14f,.024f,paint);
        canvas.drawCircle(-.31f,-.68f,.019f,paint);
        canvas.restore();
    }
}
