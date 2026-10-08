package com.example.marveldefence.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.marveldefence.R;

/** Three native image pages. No campaign unlocks or battle state live in this view. */
public final class CampaignPager extends HorizontalScrollView {
    public interface SelectionListener { void changed(int region, boolean settled); }
    public static final int REGION_COUNT = 3;
    private static final int[] IMAGES = {R.drawable.campaign_tower,
        R.drawable.campaign_destroyed_base,R.drawable.campaign_airport};
    private static final int[] NAMES = {R.string.campaign_tower_name,
        R.string.campaign_base_name,R.string.campaign_airport_name};
    private final LinearLayout pages;
    private final ImageView[] images = new ImageView[REGION_COUNT];
    private final int touchSlop;
    private final ColorMatrixColorFilter lockedFilter;
    private int pageWidth = 1, selected;
    private boolean gestureActive, gestureMoved;
    private float downX, downY;
    private ValueAnimator snap;
    private SelectionListener listener;
    private ImageView leftPreview, rightPreview;

    private SwipeSafeButton platform;
    private Runnable missionAction;
    private boolean environmentSuspended;
    private boolean visualForeground = true;
    private final VisualClock visualClock = new VisualClock(seconds -> {
        for (int i=0;i<REGION_COUNT;i++) if (pageVisible(i))
            ((CampaignImageView)images[i]).visualTime(seconds);
    });

    private boolean pageVisible(int region) {
        return images[region]!=null && region*pageWidth < getScrollX()+getWidth()
            && (region+1)*pageWidth > getScrollX();
    }
    public void suspendEnvironment(boolean suspended) {
        environmentSuspended = suspended; syncEnvironment();
    }
    public void visualForeground(boolean foreground) { visualForeground = foreground; syncEnvironment(); }
    private void syncEnvironment() {
        if (visualClock!=null) visualClock.running(visualForeground && !environmentSuspended && isAttachedToWindow()
            && isShown() && getWindowVisibility()==VISIBLE && hasWindowFocus());
    }
    @Override protected void onAttachedToWindow() { super.onAttachedToWindow(); syncEnvironment(); }
    @Override public void onWindowFocusChanged(boolean focus) { super.onWindowFocusChanged(focus); syncEnvironment(); }
    @Override protected void onWindowVisibilityChanged(int visibility) { super.onWindowVisibilityChanged(visibility); syncEnvironment(); }
    @Override protected void onVisibilityChanged(View changed,int visibility) { super.onVisibilityChanged(changed,visibility); syncEnvironment(); }

    public CampaignPager(Context context, AttributeSet attrs) {
        super(context,attrs);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setPadding(0,0,0,0);
        setClipToPadding(false); setHorizontalScrollBarEnabled(false);
        setOverScrollMode(OVER_SCROLL_NEVER); setFillViewport(false);
        setLayoutDirection(LAYOUT_DIRECTION_LTR); setFocusable(true);
        setContentDescription(context.getString(R.string.campaign_swipe_hint));
        ColorMatrix saturation = new ColorMatrix(); saturation.setSaturation(.70f);
        ColorMatrix shade = new ColorMatrix(new float[]{
            .88f,0,0,0,0, 0,.88f,0,0,0, 0,0,.88f,0,0, 0,0,0,1,0});
        saturation.postConcat(shade);
        lockedFilter = new ColorMatrixColorFilter(saturation);
        pages = new LinearLayout(context); pages.setOrientation(LinearLayout.HORIZONTAL);
        addView(pages,new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT));
        for (int region=0;region<REGION_COUNT;region++) {
            ImageView image = new CampaignImageView(context,region) {
                @Override protected void onSizeChanged(int w,int h,int oldw,int oldh) {
                    super.onSizeChanged(w,h,oldw,oldh);
                    if (getDrawable()==null || w==0 || h==0) return;
                    float scale = Math.max(w/(float)getDrawable().getIntrinsicWidth(),
                        h/(float)getDrawable().getIntrinsicHeight());
                    Matrix matrix = new Matrix(); matrix.setScale(scale,scale);
                    // Fill uniformly, keeping the campaign plaque at the bottom visible.
                    matrix.postTranslate((w-getDrawable().getIntrinsicWidth()*scale)/2,
                        h-getDrawable().getIntrinsicHeight()*scale);
                    setImageMatrix(matrix); post(() -> positionPlatform());
                }
            };
            image.setImageResource(region==0 ? R.drawable.campaign_tower_environment : IMAGES[region]);
            image.setScaleType(ImageView.ScaleType.MATRIX);
            image.setBackgroundColor(0xff081925);
            image.setContentDescription(context.getString(NAMES[region])+". "+context.getString(
                region==0 ? R.string.campaign_unlocked : R.string.campaign_locked));
            image.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
            if (region!=0) image.setColorFilter(lockedFilter);
            images[region] = image;
            pages.addView(image,new LinearLayout.LayoutParams(1,LayoutParams.MATCH_PARENT));
        }
    }

    public void bind(SelectionListener listener, ImageView leftPreview, ImageView rightPreview) {
        this.listener = listener; this.leftPreview = leftPreview; this.rightPreview = rightPreview;
        updatePreviews(); notifySelection();
    }
    public int selectedRegion() { return selected; }
    public void onMissionSelected(SwipeSafeButton target,Runnable action) {
        platform = target; missionAction = action;
        platform.setOnClickListener(v -> {
            if (canPrepareMission() && missionAction!=null) missionAction.run();
        });
        post(() -> positionPlatform());
    }
    private void positionPlatform() {
        if (platform==null || images[0]==null || images[0].getDrawable()==null) return;
        ImageView art = images[0];
        float[] point = {art.getDrawable().getIntrinsicWidth()*.3864f,
            art.getDrawable().getIntrinsicHeight()*.4750f};
        art.getImageMatrix().mapPoints(point);
        int size = Math.max(Math.round(48*getResources().getDisplayMetrics().density),
            Math.round(pageWidth*.064f));
        if (platform.getLayoutParams().width!=size) {
            platform.getLayoutParams().width = size; platform.getLayoutParams().height = size;
            platform.requestLayout();
        }
        platform.setTranslationX(point[0]-getScrollX()-size/2f);
        platform.setTranslationY(point[1]-size/2f);
        platform.setVisibility(canPrepareMission() ? View.VISIBLE : View.INVISIBLE);
    }
    public boolean settled() {
        return !gestureActive && snap==null && getScrollX()==selected*pageWidth;
    }
    public boolean canPrepareMission() { return selected==0 && settled(); }
    public void moveRegion(int delta) {
        if (settled()) snapTo(Math.max(0,Math.min(REGION_COUNT-1,selected+delta)));
    }

    @Override protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec) {
        // Size children BEFORE ScrollView measures them. Changing their params from
        // onSizeChanged during layout can leave the first frame at the initial 1 px.
        int measuredPageWidth = Math.max(1,MeasureSpec.getSize(widthMeasureSpec));
        if (pageWidth!=measuredPageWidth) {
            cancelSnap(); gestureActive = false; pageWidth = measuredPageWidth;
        }
        for (ImageView image:images) image.getLayoutParams().width = pageWidth;
        pages.getLayoutParams().width = pageWidth*REGION_COUNT;
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
    }
    @Override protected void onSizeChanged(int w,int h,int oldw,int oldh) {
        super.onSizeChanged(w,h,oldw,oldh);
        cancelSnap(); gestureActive = false;
        final int resizedWidth = pageWidth;
        post(() -> {
            if (isAttachedToWindow() && pageWidth==resizedWidth) {
                scrollTo(selected*pageWidth,0); updatePageAlpha(); notifySelection();
            }
        });
    }

    @Override public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                cancelSnap(); gestureActive = true; gestureMoved = false;
                downX = event.getX(); downY = event.getY(); notifySelection(); break;
            case MotionEvent.ACTION_MOVE:
                if (Math.hypot(event.getX()-downX,event.getY()-downY)>touchSlop) gestureMoved = true;
                break;
            default: break;
        }
        boolean consumed = super.dispatchTouchEvent(event);
        if (event.getActionMasked()==MotionEvent.ACTION_UP
                || event.getActionMasked()==MotionEvent.ACTION_CANCEL) {
            gestureActive = false;
            // Snap to the nearest region at release; velocity cannot skip regions.
            int nearest = Math.max(0,Math.min(REGION_COUNT-1,
                Math.round(getScrollX()/(float)pageWidth)));
            if (gestureMoved || nearest!=selected || getScrollX()!=nearest*pageWidth) snapTo(nearest);
            else notifySelection();
        }
        return consumed;
    }
    @Override public void fling(int velocityX) {
        // dispatchTouchEvent owns settling; suppress the default unbounded fling.
    }
    private void snapTo(int region) {
        cancelSnap();
        int target = region*pageWidth;
        if (getScrollX()==target) { select(region); return; }
        final ValueAnimator animation = ValueAnimator.ofInt(getScrollX(),target);
        snap = animation; notifySelection();
        animation.setDuration(240); animation.setInterpolator(new DecelerateInterpolator());
        animation.addUpdateListener(value -> scrollTo((int)value.getAnimatedValue(),0));
        animation.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animator) {
                if (snap!=animation) return;
                snap = null; scrollTo(target,0); select(region);
            }
        });
        animation.start();
    }
    private void cancelSnap() {
        if (snap==null) return;
        ValueAnimator previous = snap; snap = null;
        previous.removeAllUpdateListeners(); previous.removeAllListeners(); previous.cancel();
    }
    private void select(int region) {
        selected = region; updatePageAlpha(); updatePreviews(); notifySelection();
    }
    private void notifySelection() {
        positionPlatform();
        setStateDescription(getContext().getString(R.string.campaign_selection_description,
            selected+1,REGION_COUNT,getContext().getString(NAMES[selected]),
            getContext().getString(selected==0 ? R.string.campaign_unlocked : R.string.campaign_locked)));
        if (listener!=null) listener.changed(selected,settled());
    }
    @Override protected void onScrollChanged(int l,int t,int oldl,int oldt) {
        super.onScrollChanged(l,t,oldl,oldt);
        // HorizontalScrollView may invoke this during its own construction.
        if (images!=null) { updatePageAlpha(); positionPlatform(); }
    }
    private void updatePageAlpha() {
        for (int i=0;i<REGION_COUNT;i++) if (images[i]!=null) {
            float proximity = Math.max(0,1-Math.abs(getScrollX()-i*pageWidth)/(float)pageWidth);
            images[i].setAlpha(.32f+.68f*proximity);
        }
    }
    private void updatePreviews() {
        preview(leftPreview,selected-1); preview(rightPreview,selected+1);
    }
    private void preview(ImageView view,int region) {
        if (view==null) return;
        view.setVisibility(region<0 || region>=REGION_COUNT ? View.INVISIBLE : View.VISIBLE);
        if (region<0 || region>=REGION_COUNT) return;
        // Main pages fill the screen; this is a faded narrow hint of the neighboring region.
        view.setImageResource(IMAGES[region]); view.setAlpha(.30f);
        view.setColorFilter(region==0 ? null : lockedFilter);
    }
    @Override public boolean onKeyDown(int keyCode,KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_DPAD_LEFT || keyCode==KeyEvent.KEYCODE_DPAD_RIGHT) {
            moveRegion(keyCode==KeyEvent.KEYCODE_DPAD_LEFT ? -1 : 1); return true;
        }
        return super.onKeyDown(keyCode,event);
    }
    @Override public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setCollectionInfo(AccessibilityNodeInfo.CollectionInfo.obtain(1,REGION_COUNT,false));
        info.setScrollable(true);
        info.removeAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD);
        info.removeAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_BACKWARD);
        info.removeAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_LEFT);
        info.removeAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_RIGHT);
        if (selected>0) {
            info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_BACKWARD);
            info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_LEFT);
        }
        if (selected<REGION_COUNT-1) {
            info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD);
            info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_RIGHT);
        }
    }
    @Override public boolean performAccessibilityAction(int action,Bundle arguments) {
        if (action==AccessibilityNodeInfo.ACTION_SCROLL_FORWARD
                || action==AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD
                || action==AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_LEFT.getId()
                || action==AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_RIGHT.getId()) {
            boolean forward = action==AccessibilityNodeInfo.ACTION_SCROLL_FORWARD
                || action==AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_RIGHT.getId();
            moveRegion(forward ? 1 : -1); return true;
        }
        return super.performAccessibilityAction(action,arguments);
    }
    @Override public boolean onGenericMotionEvent(MotionEvent event) {
        if (event.getActionMasked()==MotionEvent.ACTION_SCROLL) {
            float axis = event.getAxisValue(MotionEvent.AXIS_HSCROLL);
            if (axis==0) axis = event.getAxisValue(MotionEvent.AXIS_VSCROLL);
            if (axis!=0) { moveRegion(axis>0 ? -1 : 1); return true; }
        }
        return super.onGenericMotionEvent(event);
    }
    @Override protected void onDetachedFromWindow() {
        visualClock.running(false);
        cancelSnap(); listener = null; leftPreview = null; rightPreview = null; missionAction = null; platform = null;
        super.onDetachedFromWindow();
    }
}
