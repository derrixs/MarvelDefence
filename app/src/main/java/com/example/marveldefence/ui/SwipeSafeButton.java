package com.example.marveldefence.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import androidx.appcompat.widget.AppCompatButton;

/** Botão nativo cujo clique é cancelado quando o toque vira um arrasto. */
public final class SwipeSafeButton extends AppCompatButton {
    private final int touchSlop;
    private float downX, downY;
    private boolean dragged;

    public SwipeSafeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            downX = event.getX();
            downY = event.getY();
            dragged = false;
        } else if (!dragged && (event.getActionMasked() == MotionEvent.ACTION_MOVE || event.getActionMasked() == MotionEvent.ACTION_UP)
            && Math.hypot(event.getX() - downX, event.getY() - downY) > touchSlop) {
            dragged = true;
            MotionEvent cancel = MotionEvent.obtain(event);
            cancel.setAction(MotionEvent.ACTION_CANCEL);
            super.onTouchEvent(cancel);
            cancel.recycle();
        }
        if (!dragged && event.getActionMasked() == MotionEvent.ACTION_UP) {
            // A ScrollView atrasa o estado pressionado; um toque rápido pode terminar antes de ele ser definido.
            boolean click = isEnabled() && event.getX() >= 0 && event.getX() < getWidth()
                && event.getY() >= 0 && event.getY() < getHeight();
            MotionEvent cancel = MotionEvent.obtain(event);
            cancel.setAction(MotionEvent.ACTION_CANCEL);
            super.onTouchEvent(cancel);
            cancel.recycle();
            if (click)
                performClick();
            return true;
        }
        return dragged || super.onTouchEvent(event);
    }
}
