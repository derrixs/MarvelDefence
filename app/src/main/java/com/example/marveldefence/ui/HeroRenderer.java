package com.example.marveldefence.ui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import com.example.marveldefence.data.HeroCatalog;

/** Temporary vector characters. Replace this renderer with the user's sprites later. */
public final class HeroRenderer {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path shape = new Path();
    public void draw(Canvas c, HeroCatalog.Hero hero, float x, float y, float radius) {
        c.save(); c.translate(x,y); c.scale(radius/24,radius/24);
        color(0x44091928); c.drawOval(-25,20,25,31,paint);
        // Feet, silhouette and suit.
        color(0xff0a2637); c.drawRoundRect(-17,12,-3,27,5,5,paint); c.drawRoundRect(3,12,17,27,5,5,paint);
        color(hero.color); c.drawRoundRect(-21,-2,21,20,9,9,paint);
        color(0xff0a2637); c.drawCircle(0,-13,24,paint);
        color(hero.color); c.drawCircle(0,-13,21,paint);
        switch (hero.id) {
            case "iron_man":
                color(0xfff6ca67); c.drawRoundRect(-15,-29,15,1,7,7,paint);
                color(0xffbc4c35); c.drawRect(-9,-4,9,0,paint);
                color(0xffccf8ff); c.drawRect(-13,-19,-3,-15,paint); c.drawRect(3,-19,13,-15,paint);
                c.drawCircle(0,10,6,paint); break;
            case "spider_man":
                paint.setStyle(Paint.Style.STROKE); paint.setStrokeWidth(1.3f); paint.setColor(0xff78283d);
                c.drawCircle(0,-13,14,paint); c.drawLine(0,-34,0,8,paint); c.drawLine(-20,-13,20,-13,paint);
                c.drawLine(-15,-28,15,2,paint); c.drawLine(15,-28,-15,2,paint);
                color(0xfff5faf1); eye(c,-13,-22,-3,-9); eye(c,13,-22,3,-9);
                color(0xff183448); c.drawCircle(0,10,3,paint); break;
            case "hulk":
                color(0xff315332); c.drawRoundRect(-19,-34,19,-20,5,5,paint);
                color(0xfff9eed5); c.drawRect(-13,-16,-4,-11,paint); c.drawRect(4,-16,13,-11,paint);
                color(0xff193a2a); c.drawRect(-14,-20,-3,-16,paint); c.drawRect(3,-20,14,-16,paint);
                c.drawRect(-9,-3,9,0,paint); color(0xff755394); c.drawRect(-18,15,18,21,paint); break;
            case "thor":
                color(0xffd54b51); c.drawRoundRect(-24,2,24,24,5,5,paint);
                color(0xffedd589); c.drawOval(-23,-32,23,7,paint);
                color(0xfff6cba0); c.drawOval(-14,-27,14,0,paint);
                color(0xffb8d7e3); c.drawRoundRect(-20,-34,20,-20,4,4,paint);
                color(0xff234254); c.drawRect(-10,-17,-4,-13,paint); c.drawRect(4,-17,10,-13,paint);
                color(0xffc9dfe1); c.drawRoundRect(17,2,34,13,2,2,paint); break;
            case "captain":
                color(0xfff6cba0); c.drawOval(-14,-15,14,1,paint);
                color(0xffeff4e6); paint.setTextSize(18); paint.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
                c.drawText("A",-6,-19,paint);
                color(0xffdc525c); c.drawCircle(16,15,17,paint);
                color(0xfff6eee0); c.drawCircle(16,15,12,paint);
                color(0xff498dde); c.drawCircle(16,15,8,paint);
                color(0xfff6eee0); c.drawCircle(16,15,3,paint); break;
            default:
                color(0xffb54138); c.drawOval(-23,-34,23,5,paint);
                color(0xfff6cba0); c.drawOval(-13,-26,13,-1,paint);
                color(0xff973e34); c.drawOval(-18,-33,12,-20,paint);
                color(0xff19323d); c.drawRect(-10,-16,-4,-12,paint); c.drawRect(4,-16,10,-12,paint);
                c.drawRoundRect(-17,0,17,20,6,6,paint); color(0xfff16b6b); c.drawCircle(0,11,4,paint);
        }
        c.restore();
    }
    private void color(int color) { paint.setStyle(Paint.Style.FILL); paint.setColor(color); }
    private void eye(Canvas c,float x1,float y1,float x2,float y2) {
        shape.reset(); shape.moveTo(x1,y1); shape.lineTo(x2,y1+3); shape.lineTo(x2,y2); shape.close(); c.drawPath(shape,paint);
    }
}
