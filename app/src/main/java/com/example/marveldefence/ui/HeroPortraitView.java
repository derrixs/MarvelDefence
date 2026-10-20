package com.example.marveldefence.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import com.example.marveldefence.data.HeroCatalog;

public final class HeroPortraitView extends View {
    private final HeroCatalog.Hero hero;
    private final HeroRenderer renderer = new HeroRenderer();
    public HeroPortraitView(Context context) {
        this(context, HeroCatalog.ALL.get(0));
    }
    public HeroPortraitView(Context context, HeroCatalog.Hero hero) {
        super(context);
        this.hero = hero;
        setContentDescription(hero.name);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        renderer.draw(canvas, hero, getWidth() / 2f, getHeight() / 2f + 6, Math.min(getWidth(), getHeight()) * .31f);
    }
}
