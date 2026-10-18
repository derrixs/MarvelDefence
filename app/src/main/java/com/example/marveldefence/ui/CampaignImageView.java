package com.example.marveldefence.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.appcompat.widget.AppCompatImageView;

/** O fundo e os detalhes usam a mesma matriz da ImageView. Esta classe não trata toques. */
class CampaignImageView extends AppCompatImageView {
    private final EnvironmentRenderer environment;
    private final Paint effectFilter = new Paint();
    private double seconds;
    public CampaignImageView(Context context) { this(context,0); }
    CampaignImageView(Context context,int region) {
        super(context);
        environment = new EnvironmentRenderer(getResources(),EnvironmentScene.Kind.values()[region]);
    }
    void visualTime(double time) { seconds = time; invalidate(); }
    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        effectFilter.setColorFilter(getColorFilter());
        int layer = canvas.saveLayer(0,0,getWidth(),getHeight(),effectFilter);
        canvas.concat(getImageMatrix()); environment.draw(canvas,seconds);
        canvas.restoreToCount(layer);
    }
}
