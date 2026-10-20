package com.example.marveldefence;

import static org.junit.Assert.*;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;
import com.example.marveldefence.game.GameEngine;
import com.example.marveldefence.game.MapData;
import com.example.marveldefence.game.Viewport;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.junit.Test;
import org.junit.runner.RunWith;

/** Exercita controles Android e o motor reais; captura a tela renderizada no aparelho. */
@RunWith(AndroidJUnit4.class)
public class BattleLayoutInstrumentedTest {
    @Test
    public void improvementsAndSaleRemainVisibleAndPurchasesUpdateRealValues() throws Exception {
        try (ActivityHost scenario = new ActivityHost()) {
            settle();
            scenario.onActivity(a -> a.findViewById(R.id.campaign_platform).performClick());
            settle();
            scenario.onActivity(a -> a.findViewById(R.id.deploy_button).performClick());
            capture("closed");
            scenario.onActivity(a -> heroButton(a, "Homem de Ferro").performClick());
            capture("selected");
            scenario.onActivity(a -> {
                assertEquals(View.VISIBLE, a.findViewById(R.id.placement_actions).getVisibility());
                GameEngine engine = engine(a);
                MapData map = new MapData();
                View field = a.findViewById(R.id.battle_map);
                Viewport viewport = new Viewport();
                viewport.cover(field.getWidth(), field.getHeight(), map.width, map.height);
                float x = -1, y = -1;
                double density = a.getResources().getDisplayMetrics().density;
            search:
                for (double wy = 0; wy < map.height; wy += 12)
                    for (double wx = 0; wx < map.width; wx += 12) {
                        double sx = viewport.screenX(wx), sy = viewport.screenY(wy);
                        if (sx > 256 * density && sx < field.getWidth() - 48 * density && sy > 60 * density
                            && sy < field.getHeight() - 40 * density
                            && engine.placement("iron_man", wx, wy) == GameEngine.Placement.VALID) {
                            x = (float) sx;
                            y = (float) sy;
                            break search;
                        }
                    }
                assertTrue("A visible valid position must exist", x >= 0);
                long now = SystemClock.uptimeMillis();
                MotionEvent down = MotionEvent.obtain(now, now, MotionEvent.ACTION_DOWN, x, y, 0);
                MotionEvent up = MotionEvent.obtain(now, now + 80, MotionEvent.ACTION_UP, x, y, 0);
                field.dispatchTouchEvent(down);
                field.dispatchTouchEvent(up);
                down.recycle();
                up.recycle();
                assertTrue(a.findViewById(R.id.confirm_placement).isEnabled());
                a.findViewById(R.id.confirm_placement).performClick();
                assertEquals(680, engine.credits());
                heroButton(a, "Homem de Ferro").performClick();
            });
            capture("open");
            scenario.onActivity(a -> {
                assertFullyVisible(a.findViewById(R.id.upgrade_damage));
                assertFullyVisible(a.findViewById(R.id.upgrade_range));
                assertFullyVisible(a.findViewById(R.id.sell_unit));
                for (int id : new int[] {R.id.upgrade_damage, R.id.upgrade_range, R.id.sell_unit, R.id.close_upgrade,
                         R.id.unit_profile, R.id.start_wave, R.id.pause_button, R.id.battle_back, R.id.speed_button}) {
                    View view = a.findViewById(id);
                    float min = 48 * a.getResources().getDisplayMetrics().density;
                    assertTrue("Small touch target: " + id, view.getWidth() >= min - 1 && view.getHeight() >= min - 1);
                }
                for (int id : new int[] {R.id.lives, R.id.credits, R.id.wave, R.id.unit_name, R.id.unit_stats,
                         R.id.damage_price, R.id.range_price}) assertTextFits(a, id);
                ViewGroup levels = a.findViewById(R.id.damage_level);
                assertEquals(4, levels.getChildCount());
                assertTrue(levels.getChildAt(0).isActivated());
                assertFalse(levels.getChildAt(1).isActivated());
                Rect panel = rect(a.findViewById(R.id.upgrade_panel));
                assertTrue(rect(a.findViewById(R.id.battle_hud)).left >= panel.right);
                assertTrue(rect(a.findViewById(R.id.battle_hint)).left >= panel.right);
                a.findViewById(R.id.upgrade_damage).performClick();
                a.findViewById(R.id.upgrade_damage).performClick();
                assertEquals(340, engine(a).credits());
            });
            capture("long-prices");
            scenario.onActivity(a -> {
                assertEquals("$320", ((TextView) a.findViewById(R.id.damage_price)).getText().toString());
                assertTextFits(a, R.id.damage_price);
                ViewGroup levels = a.findViewById(R.id.damage_level);
                assertTrue(levels.getChildAt(2).isActivated());
                assertFalse(levels.getChildAt(3).isActivated());
                a.findViewById(R.id.upgrade_range).performClick();
                a.findViewById(R.id.upgrade_range).performClick();
                assertEquals(80, engine(a).credits());
                assertFalse(a.findViewById(R.id.upgrade_damage).isEnabled());
                assertFalse(a.findViewById(R.id.upgrade_range).isEnabled());
                assertFalse(heroButton(a, "Homem-Aranha").isEnabled());
                assertFalse(heroButton(a, "Hulk").isEnabled());
                assertTrue(heroButton(a, "Homem de Ferro").isEnabled());
            });
            capture("no-credits");
            scenario.onActivity(a -> {
                a.findViewById(R.id.close_upgrade).performClick();
                assertEquals(View.GONE, a.findViewById(R.id.upgrade_panel).getVisibility());
                heroButton(a, "Homem de Ferro").performClick();
                a.findViewById(R.id.sell_unit).performClick();
                assertEquals(0, engine(a).units().size());
                assertEquals(724, engine(a).credits());
            });
            capture("sold");
        }
    }

    private static GameEngine engine(MainActivity activity) {
        try {
            Field field = MainActivity.class.getDeclaredField("engine");
            field.setAccessible(true);
            return (GameEngine) field.get(activity);
        } catch (Exception error) {
            throw new AssertionError(error);
        }
    }
    private static Button heroButton(MainActivity activity, String name) {
        ViewGroup heroes = activity.findViewById(R.id.battle_heroes);
        for (int i = 0; i < heroes.getChildCount(); i++) {
            Button button = (Button) ((ViewGroup) heroes.getChildAt(i)).getChildAt(0);
            if (button.getContentDescription().toString().startsWith(name + " •"))
                return button;
        }
        throw new AssertionError("Hero missing: " + name);
    }
    private static Rect rect(View view) {
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        return rect;
    }
    private static void assertFullyVisible(View view) {
        Rect visible = rect(view);
        assertEquals("Clipped width", view.getWidth(), visible.width());
        assertEquals("Clipped height", view.getHeight(), visible.height());
    }
    private static void assertTextFits(MainActivity activity, int id) {
        TextView text = activity.findViewById(id);
        assertNotNull(text.getLayout());
        assertTrue("Clipped text: " + id, text.getLayout().getHeight() <= text.getHeight() - text.getCompoundPaddingTop() - text.getCompoundPaddingBottom());
        for (int line = 0; line < text.getLayout().getLineCount(); line++) assertEquals(0, text.getLayout().getEllipsisCount(line));
    }
    private static void settle() {
        // Telas Canvas animadas nunca ficam totalmente ociosas; aguarda o layout e alguns quadros.
        SystemClock.sleep(350);
    }
    private static void capture(String state) throws Exception {
        settle();
        Bitmap bitmap = InstrumentationRegistry.getInstrumentation().getUiAutomation().takeScreenshot();
        assertNotNull("Device screenshot", bitmap);
        File directory = new File(InstrumentationRegistry.getInstrumentation().getTargetContext().getExternalFilesDir(null), "battle-layout");
        assertTrue(directory.isDirectory() || directory.mkdirs());
        try (FileOutputStream output = new FileOutputStream(new File(directory, state + ".png"))) {
            assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG, 100, output));
        }
        bitmap.recycle();
    }

    private static final class ActivityHost implements AutoCloseable {
        private MainActivity activity;
        ActivityHost() {
            android.content.Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
            context.startActivity(new Intent(context, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            long deadline = SystemClock.uptimeMillis() + 10000;
            while (activity == null && SystemClock.uptimeMillis() < deadline) {
                SystemClock.sleep(150);
                InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
                    for (android.app.Activity candidate : ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED))
                        if (candidate instanceof MainActivity)
                            activity = (MainActivity) candidate;
                });
            }
            assertNotNull("MainActivity resumed", activity);
        }
        void onActivity(Consumer<MainActivity> action) {
            AtomicReference<Throwable> failure = new AtomicReference<>();
            InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
                try {
                    action.accept(activity);
                } catch (Throwable error) {
                    failure.set(error);
                }
            });
            if (failure.get() != null)
                throw new AssertionError(failure.get());
        }
        @Override
        public void close() {
            onActivity(MainActivity::finish);
        }
    }
}
