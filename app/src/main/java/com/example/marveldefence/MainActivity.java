package com.example.marveldefence;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.graphics.drawable.InsetDrawable;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Choreographer;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import com.example.marveldefence.data.ComicVineRepository;
import com.example.marveldefence.data.HeroCatalog;
import com.example.marveldefence.game.GameConfig;
import com.example.marveldefence.game.GameEngine;
import com.example.marveldefence.game.MapData;
import com.example.marveldefence.ui.BattlefieldView;
import com.example.marveldefence.ui.CampaignPager;
import com.example.marveldefence.ui.HeroPortraitView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/** Navegação e comandos da interface; esta thread controla todo o estado do jogo. */
public class MainActivity extends AppCompatActivity {
    private enum Screen { MENU, BATTLE }
    private Screen screen = Screen.MENU;
    private FrameLayout root;
    private ComicVineRepository repository;
    private final Set<String> team = new LinkedHashSet<>(Arrays.asList("iron_man", "spider_man", "hulk"));
    private final Map<String, Button> teamButtons = new LinkedHashMap<>();
    private final Map<String, View> teamCards = new LinkedHashMap<>();
    private final Map<String, Button> battleButtons = new LinkedHashMap<>();
    private final Map<String, View> battleCards = new LinkedHashMap<>();
    private String upgradePortraitId;
    private GameEngine engine;
    private BattlefieldView battlefield;
    private GameEngine.Unit selectedUnit;
    private String pendingHero;
    private double pendingX, pendingY;
    private boolean hasPosition, resumed, ticking, resultShown;
    private int speed = 1;
    private long lastFrame, lastHud;
    private double accumulator;
    private AlertDialog resultDialog;
    private View preparation;
    private View openingCover;

    private final Choreographer.FrameCallback frame = new Choreographer.FrameCallback() {
        @Override public void doFrame(long time) {
            if (!ticking || !resumed || screen != Screen.BATTLE || engine == null) return;
            if (lastFrame != 0 && !engine.paused()) {
                accumulator += Math.min(GameConfig.MAX_FRAME_TIME, (time-lastFrame)/1_000_000_000.0)*speed;
                int steps = 0;
                while (accumulator >= GameConfig.FIXED_STEP && steps++ < 12) {
                    engine.update(GameConfig.FIXED_STEP);
                    accumulator -= GameConfig.FIXED_STEP;
                }
            } else accumulator = 0;
            lastFrame = time;
            battlefield.invalidate();
            if (time-lastHud > 150_000_000L) { updateBattleUi(); lastHud = time; }
            if (engine.finished()) { stopFrames(); showResult(); return; }
            Choreographer.getInstance().postFrameCallback(this);
        }
    };

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        root = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets safe = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            v.setPadding(screen==Screen.MENU ? 0 : safe.left,screen==Screen.MENU ? 0 : safe.top,
                screen==Screen.MENU ? 0 : safe.right,screen==Screen.MENU ? 0 : safe.bottom);
            View controls = findViewById(R.id.campaign_controls);
            if (controls!=null) controls.setPadding(safe.left,safe.top,safe.right,safe.bottom);
            if (preparation!=null) preparation.setPadding(safe.left,safe.top,safe.right,safe.bottom);
            return insets;
        });
        repository = new ComicVineRepository(this);
        getOnBackPressedDispatcher().addCallback(this,new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() {
                if (openingCover != null) dismissOpeningCover();
                else if (preparation != null) closePreparation();
                else if (screen == Screen.BATTLE) {
                    if (pendingHero != null) cancelPlacement();
                    else if (selectedUnit != null) closeUpgrade();
                    else leaveBattle();
                } else finish();
            }
        });
        // Ao recriar a Activity ou o processo, inicia uma sessão nova; não simula recuperação de partida.
        showMenu();
        if (savedInstanceState == null) showOpeningCover();
    }

    private void showOpeningCover() {
        openingCover = getLayoutInflater().inflate(R.layout.screen_opening_cover, root, false);
        root.addView(openingCover);
        View cover = openingCover;
        cover.setOnClickListener(v -> dismissOpeningCover());
        cover.postDelayed(() -> {
            if (openingCover == cover) dismissOpeningCover();
        }, 1800);
    }

    private void dismissOpeningCover() {
        if (openingCover == null) return;
        root.removeView(openingCover);
        openingCover = null;
    }

    private void showScreen(int layout) {
        stopFrames();
        preparation = null; teamButtons.clear(); teamCards.clear();
        root.removeAllViews();
        getLayoutInflater().inflate(layout,root,true);
        WindowInsetsControllerCompat bars = WindowCompat.getInsetsController(getWindow(),root);
        bars.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        if (screen==Screen.MENU) {
            root.setPadding(0,0,0,0);
            bars.hide(WindowInsetsCompat.Type.systemBars());
        } else bars.show(WindowInsetsCompat.Type.systemBars());
        ViewCompat.requestApplyInsets(root);
    }

    private void showMenu() {
        if (resultDialog != null) { resultDialog.dismiss(); resultDialog = null; }
        screen = Screen.MENU;
        engine = null; battlefield = null; selectedUnit = null; pendingHero = null;
        showScreen(R.layout.screen_campaign_menu);
        CampaignPager pager = findViewById(R.id.campaign_pager);

        View comingSoon = findViewById(R.id.campaign_coming_soon);
        View previous = findViewById(R.id.campaign_previous);
        View next = findViewById(R.id.campaign_next);
        TextView indicator = findViewById(R.id.campaign_indicator);
        pager.bind((region,settled) -> {


            comingSoon.setVisibility(region==0 ? View.GONE : View.VISIBLE);
            previous.setEnabled(settled && region>0); previous.setAlpha(region>0 ? 1 : .3f);
            next.setEnabled(settled && region<CampaignPager.REGION_COUNT-1);
            next.setAlpha(region<CampaignPager.REGION_COUNT-1 ? 1 : .3f);
            indicator.setText(getString(R.string.campaign_position,region+1,CampaignPager.REGION_COUNT));
            indicator.setContentDescription(pager.getStateDescription());
        },findViewById(R.id.campaign_previous_preview),findViewById(R.id.campaign_next_preview));
        pager.onMissionSelected(findViewById(R.id.campaign_platform),this::showPreparation);
        previous.setOnClickListener(v -> pager.moveRegion(-1));
        next.setOnClickListener(v -> pager.moveRegion(1));
    }

    private void showPreparation() {
        if (screen!=Screen.MENU || preparation!=null) return;
        CampaignPager pager = findViewById(R.id.campaign_pager);
        if (!pager.canPrepareMission()) return;
        pager.suspendEnvironment(true);
        preparation = getLayoutInflater().inflate(R.layout.popup_mission,root,false);
        root.addView(preparation);
        root.getChildAt(0).setAlpha(.5f);
        root.getChildAt(0).setRenderEffect(RenderEffect.createBlurEffect(dp(4),dp(4),Shader.TileMode.CLAMP));
        root.getChildAt(0).setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
        ViewCompat.setAccessibilityPaneTitle(preparation,getString(R.string.mission_headquarters));
        preparation.setOnClickListener(v -> closePreparation());
        findViewById(R.id.mission_close).setOnClickListener(v -> closePreparation());
        findViewById(R.id.mission_close).requestFocus();
        LinearLayout rows = findViewById(R.id.mission_hero_rows);
        teamButtons.clear(); teamCards.clear();
        for (int rowIndex=0;rowIndex<2;rowIndex++) {
            LinearLayout row = new LinearLayout(this); row.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(-1,0,1);
            rowParams.bottomMargin = dp(4); rows.addView(row,rowParams);
            for (int column=0;column<3;column++) {
                HeroCatalog.Hero hero = HeroCatalog.ALL.get(rowIndex*3+column);
                FrameLayout card = new FrameLayout(this);
                card.setBackgroundResource(R.drawable.mission_card);
                card.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
                LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(0,-1,1);
                cardParams.setMargins(dp(2),0,dp(2),0); row.addView(card,cardParams);
                LinearLayout content = new LinearLayout(this); content.setGravity(Gravity.CENTER_VERTICAL);
                content.setPadding(dp(7),dp(5),dp(24),dp(8));
                card.addView(content,new FrameLayout.LayoutParams(-1,-1));
                String[] symbols = getResources().getStringArray(R.array.mission_hero_symbols);
                TextView icon = label(symbols[rowIndex*3+column],24,Color.WHITE,true);
                icon.setGravity(Gravity.CENTER); icon.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
                GradientDrawable badge = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                    new int[]{hero.color,blendDark(hero.color)});
                badge.setCornerRadius(dp(5)); badge.setStroke(dp(2),0xff4876a1); icon.setBackground(badge);
                content.addView(icon,new LinearLayout.LayoutParams(dp(32),dp(42)));
                TextView name = label(getResources().getStringArray(R.array.mission_hero_labels)[rowIndex*3+column],11,0xff205788,true);
                name.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
                LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(0,-2,1);
                nameParams.leftMargin = dp(6); content.addView(name,nameParams);
                AppCompatButton select = new AppCompatButton(this);
                select.setAllCaps(false); select.setTextSize(16); select.setTypeface(null,Typeface.BOLD);
                select.setTextColor(0xff24668c); select.setMinWidth(0); select.setMinimumWidth(0);
                select.setMinHeight(0); select.setMinimumHeight(0); select.setPadding(0,0,0,0);
                select.setBackground(new InsetDrawable(AppCompatResources.getDrawable(this,R.drawable.mission_add),dp(10)));
                select.setBackgroundTintList(null);
                FrameLayout.LayoutParams plusParams = new FrameLayout.LayoutParams(dp(48),dp(48),Gravity.END|Gravity.BOTTOM);
                card.addView(select,plusParams);
                card.setOnClickListener(v -> select.performClick());
                teamCards.put(hero.id,card);
                select.setOnClickListener(v -> {
                    if (team.contains(hero.id)) team.remove(hero.id);
                    else if (team.size()<GameConfig.TEAM_LIMIT) team.add(hero.id);
                    else toast(R.string.team_full);
                    updateTeamUi();
                });
                teamButtons.put(hero.id,select);
            }
        }
        findViewById(R.id.mission_powers).setOnClickListener(v -> {
            String[] names = new String[HeroCatalog.ALL.size()];
            for (int i=0;i<names.length;i++) names[i] = HeroCatalog.ALL.get(i).name;
            new AlertDialog.Builder(this).setTitle(R.string.mission_powers)
                .setItems(names,(dialog,index) -> showProfile(HeroCatalog.ALL.get(index)))
                .setNegativeButton(R.string.cancel,null).show();
        });
        findViewById(R.id.deploy_button).setOnClickListener(v -> {
            if (team.isEmpty()) toast(R.string.select_team_first); else startBattle();
        });
        Button sync = findViewById(R.id.sync_button);
        sync.setEnabled(!repository.loading());
        if (repository.loading()) ((TextView)findViewById(R.id.api_status)).setText(R.string.sync_loading);
        sync.setOnClickListener(v -> {
            if (!repository.hasKey()) { toast(R.string.sync_no_key); return; }
            if (!repository.refresh((loaded,failed) -> {
                if (preparation!=null) {
                    ((TextView)findViewById(R.id.api_status)).setText(getString(R.string.sync_result,loaded,failed));
                    findViewById(R.id.sync_button).setEnabled(true);
                }
            })) { toast(R.string.sync_wait); return; }
            sync.setEnabled(false);
            ((TextView)findViewById(R.id.api_status)).setText(R.string.sync_loading);
        });
        ViewCompat.requestApplyInsets(root);
        updateTeamUi();
    }

    private void closePreparation() {
        if (preparation==null) return;
        root.removeView(preparation); preparation = null; teamButtons.clear(); teamCards.clear();
        root.getChildAt(0).setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_AUTO);
        root.getChildAt(0).setAlpha(1);
        root.getChildAt(0).setRenderEffect(null);
        ((CampaignPager)findViewById(R.id.campaign_pager)).suspendEnvironment(false);
        findViewById(R.id.campaign_pager).requestFocus();
    }

    private void updateTeamUi() {
        if (preparation==null) return;
        ((TextView)findViewById(R.id.team_count)).setText(getString(R.string.mission_team_count,team.size(),GameConfig.TEAM_LIMIT));
        findViewById(R.id.deploy_button).setEnabled(!team.isEmpty());
        for (Map.Entry<String,Button> entry:teamButtons.entrySet()) {
            HeroCatalog.Hero hero = HeroCatalog.find(entry.getKey());
            boolean selected = team.contains(hero.id);
            entry.getValue().setText(selected ? R.string.mission_check_symbol : R.string.mission_add_symbol);
            teamCards.get(hero.id).setSelected(selected);
            entry.getValue().setSelected(selected);
            entry.getValue().setContentDescription(getString(R.string.mission_hero_toggle,hero.name,hero.role,
                getString(selected ? R.string.mission_hero_selected : R.string.mission_hero_available)));

        }
    }
    private int blendDark(int color) {
        return Color.rgb((int)(Color.red(color)*.60f),(int)(Color.green(color)*.60f),(int)(Color.blue(color)*.60f));
    }
    private int heroEmblem(String id) {
        switch (id) {
            case "iron_man": return R.drawable.combat_reactor;
            case "captain": return R.drawable.combat_shield;
            case "thor": return R.drawable.combat_lightning;
            case "hulk": return R.drawable.combat_fist;
            case "spider_man": return R.drawable.combat_web;
            default: return R.drawable.combat_widow;
        }
    }
    private void startBattle() {
        resultShown = false; pendingHero = null; selectedUnit = null; hasPosition = false; speed = 1;
        screen = Screen.BATTLE;
        engine = new GameEngine(new MapData(),new ArrayList<>(team));
        showScreen(R.layout.screen_battle);
        battlefield = findViewById(R.id.battle_map);
        battlefield.bind(engine,this::mapTouched);
        findViewById(R.id.battle_hud).addOnLayoutChangeListener((v,l,t,r,b,ol,ot,or,ob) -> fitBattleHud());
        battleButtons.clear(); battleCards.clear(); upgradePortraitId = null;
        LinearLayout list = findViewById(R.id.battle_heroes);
        for (String id : team) {
            HeroCatalog.Hero hero = HeroCatalog.find(id);
            LinearLayout card = new LinearLayout(this); card.setGravity(Gravity.CENTER);
            card.setBackgroundResource(R.drawable.combat_card);
            card.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp(56),dp(56)); params.bottomMargin = dp(6);
            AppCompatButton button = new AppCompatButton(this);
            button.setBackgroundColor(Color.TRANSPARENT); button.setBackgroundTintList(null);
            button.setPadding(0,0,0,0); button.setMinWidth(0); button.setMinimumWidth(0); button.setMinHeight(dp(48));
            button.setCompoundDrawablesWithIntrinsicBounds(heroEmblem(id),0,0,0);
            button.setGravity(Gravity.CENTER); button.setCompoundDrawablePadding(0);
            // O espaçamento horizontal simétrico centraliza o emblema de 40 dp na área de toque nativa.
            button.setPadding(dp(8),dp(8),dp(8),dp(8));
            card.addView(button,new LinearLayout.LayoutParams(dp(56),dp(56)));
            button.setOnClickListener(v -> {
                for (GameEngine.Unit unit : engine.units()) if (unit.hero.id.equals(id)) { selectUnit(unit); return; }
                beginPlacement(id);
            });
            card.setOnClickListener(v -> button.performClick());
            list.addView(card,params); battleButtons.put(id,button); battleCards.put(id,card);
        }
        findViewById(R.id.battle_back).setOnClickListener(v -> leaveBattle());
        findViewById(R.id.confirm_placement).setOnClickListener(v -> {
            if (pendingHero != null && hasPosition && engine.place(pendingHero,pendingX,pendingY)) cancelPlacement();
            else updatePlacementUi();
            updateBattleUi();
        });
        findViewById(R.id.cancel_placement).setOnClickListener(v -> cancelPlacement());
        findViewById(R.id.start_wave).setOnClickListener(v -> { engine.startWave(); updateBattleUi(); });
        findViewById(R.id.pause_button).setOnClickListener(v -> {
            engine.setPaused(!engine.paused()); accumulator = 0; lastFrame = 0; updateBattleUi();
        });
        findViewById(R.id.speed_button).setOnClickListener(v -> { speed = speed == 1 ? 2 : 1; updateBattleUi(); });
        findViewById(R.id.upgrade_damage).setOnClickListener(v -> upgrade(true));
        findViewById(R.id.upgrade_range).setOnClickListener(v -> upgrade(false));
        findViewById(R.id.sell_unit).setOnClickListener(v -> {
            if (selectedUnit != null && engine.sell(selectedUnit)) { closeUpgrade(); updateBattleUi(); }
        });
        findViewById(R.id.close_upgrade).setOnClickListener(v -> closeUpgrade());
        findViewById(R.id.unit_profile).setOnClickListener(v -> { if (selectedUnit != null) showProfile(selectedUnit.hero); });
        updateBattleUi(); startFrames();
    }

    private void beginPlacement(String id) {
        closeUpgrade(); pendingHero = id; hasPosition = false;
        battlefield.pending(id,0,0,false); updateBattleUi();
    }
    private void mapTouched(double x,double y) {
        if (pendingHero != null) {
            pendingX = x; pendingY = y; hasPosition = true;
            battlefield.pending(pendingHero,x,y,true); updatePlacementUi(); return;
        }
        for (GameEngine.Unit unit : engine.units()) if (Math.hypot(unit.x-x,unit.y-y) <= GameConfig.HERO_RADIUS+12) {
            selectUnit(unit); return;
        }
        closeUpgrade();
    }
    private void cancelPlacement() {
        pendingHero = null; hasPosition = false;
        battlefield.pending(null,0,0,false);
        findViewById(R.id.placement_actions).setVisibility(View.GONE);
        updateBattleUi();
    }
    private void updatePlacementUi() {
        if (pendingHero == null) return;
        findViewById(R.id.placement_actions).setVisibility(View.VISIBLE);
        GameEngine.Placement validation = hasPosition ? engine.placement(pendingHero,pendingX,pendingY) : null;
        findViewById(R.id.confirm_placement).setEnabled(validation == GameEngine.Placement.VALID);
        TextView hint = findViewById(R.id.battle_hint);
        if (!hasPosition) { hint.setText(getString(R.string.placement_choose,HeroCatalog.find(pendingHero).name)); return; }
        switch (validation) {
            case VALID: hint.setText(getString(R.string.placement_valid,HeroCatalog.find(pendingHero).cost)); break;
            case PATH: hint.setText(R.string.placement_path); break;
            case TOO_FAR: hint.setText(R.string.placement_far); break;
            case OBSTACLE: hint.setText(R.string.placement_obstacle); break;
            case OVERLAP: hint.setText(R.string.placement_overlap); break;
            case OUTSIDE: hint.setText(R.string.placement_outside); break;
            case NO_CREDITS: hint.setText(R.string.placement_no_credits); break;
            default: hint.setText(R.string.placement_unavailable);
        }
    }
    private void selectUnit(GameEngine.Unit unit) {
        cancelPlacement(); selectedUnit = unit; battlefield.selected(unit);
        findViewById(R.id.upgrade_panel).setVisibility(View.VISIBLE); updateBattleUi();
        positionBattleOverlays(true);
    }
    private void closeUpgrade() {
        selectedUnit = null;
        if (battlefield != null) battlefield.selected(null);
        View panel = findViewById(R.id.upgrade_panel); if (panel != null) panel.setVisibility(View.GONE);
        positionBattleOverlays(false);
        if (screen==Screen.BATTLE) updateBattleUi();
    }
    private void upgrade(boolean damage) {
        if (selectedUnit != null && !engine.upgrade(selectedUnit,damage)) toast(R.string.upgrade_failed);
        updateBattleUi(); battlefield.invalidate();
    }
    private void fitBattleHud() {
        LinearLayout hud = findViewById(R.id.battle_hud);
        View wave = findViewById(R.id.wave);
        if (hud == null || hud.getWidth() == 0) return;
        boolean stacked = hudTextWidth(R.id.lives) + hudTextWidth(R.id.credits) + hudTextWidth(R.id.wave) + dp(12)
            > hud.getWidth() - hud.getPaddingLeft() - hud.getPaddingRight();
        int orientation = stacked ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL;
        if (hud.getOrientation() == orientation) return;
        hud.setOrientation(orientation);
        findViewById(R.id.hud_spacer).setVisibility(stacked ? View.GONE : View.VISIBLE);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) wave.getLayoutParams();
        params.setMarginStart(stacked ? 0 : dp(6));
        params.topMargin = stacked ? dp(4) : 0;
        params.gravity = stacked ? Gravity.END : Gravity.CENTER_VERTICAL;
        wave.setLayoutParams(params);
    }

    private int hudTextWidth(int id) {
        TextView text = findViewById(id);
        return (int)Math.ceil(text.getPaint().measureText(text.getText().toString()))
            + text.getCompoundPaddingLeft() + text.getCompoundPaddingRight();
    }

    private void positionBattleOverlays(boolean upgradeOpen) {
        for (int id:new int[]{R.id.battle_hud,R.id.battle_hint}) {
            View overlay = findViewById(id);
            if (overlay==null) continue;
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)overlay.getLayoutParams();
            params.setMarginStart(upgradeOpen ? getResources().getDimensionPixelSize(R.dimen.combat_upgrade_width) : 0);
            overlay.setLayoutParams(params);
        }
    }
    private void updateUpgradeUi() {
        if (selectedUnit == null) return;
        ((TextView)findViewById(R.id.unit_name)).setText(selectedUnit.hero.name);
        if (!selectedUnit.hero.id.equals(upgradePortraitId)) {
            FrameLayout avatar = findViewById(R.id.unit_avatar); avatar.removeAllViews();
            avatar.addView(new HeroPortraitView(this,selectedUnit.hero),new FrameLayout.LayoutParams(-1,-1));
            upgradePortraitId = selectedUnit.hero.id;
        }
        ((TextView)findViewById(R.id.unit_stats)).setText(getString(R.string.combat_stats,selectedUnit.damage(),selectedUnit.range()));
        Button damage = findViewById(R.id.upgrade_damage), range = findViewById(R.id.upgrade_range);
        boolean damageMax = selectedUnit.damageLevel() >= GameConfig.UPGRADE_LIMIT;
        boolean rangeMax = selectedUnit.rangeLevel() >= GameConfig.UPGRADE_LIMIT;
        TextView damagePrice = findViewById(R.id.damage_price), rangePrice = findViewById(R.id.range_price);
        damagePrice.setText(damageMax ? getString(R.string.upgrade_max) : getString(R.string.combat_upgrade_price,GameConfig.damageCost(selectedUnit.damageLevel())));
        rangePrice.setText(rangeMax ? getString(R.string.upgrade_max) : getString(R.string.combat_upgrade_price,GameConfig.rangeCost(selectedUnit.rangeLevel())));
        updateLevelSegments(R.id.damage_level,selectedUnit.damageLevel(),"+40% base");
        updateLevelSegments(R.id.range_level,selectedUnit.rangeLevel(),"+25");
        damage.setContentDescription(damageMax ? getString(R.string.upgrade_max) : getString(R.string.damage_upgrade,GameConfig.damageCost(selectedUnit.damageLevel())));
        range.setContentDescription(rangeMax ? getString(R.string.upgrade_max) : getString(R.string.range_upgrade,GameConfig.rangeCost(selectedUnit.rangeLevel())));
        damage.setEnabled(!damageMax && engine.credits() >= GameConfig.damageCost(selectedUnit.damageLevel()));
        range.setEnabled(!rangeMax && engine.credits() >= GameConfig.rangeCost(selectedUnit.rangeLevel()));
        damagePrice.setEnabled(damage.isEnabled()); rangePrice.setEnabled(range.isEnabled());
        damage.setContentDescription(damage.getContentDescription()+". "+findViewById(R.id.damage_level).getContentDescription());
        range.setContentDescription(range.getContentDescription()+". "+findViewById(R.id.range_level).getContentDescription());
        damage.setTooltipText(damage.getContentDescription()); range.setTooltipText(range.getContentDescription());
        findViewById(R.id.unit_profile).setTooltipText(getString(R.string.hero_details));
        ((Button)findViewById(R.id.sell_unit)).setText(getString(R.string.sell,selectedUnit.sellValue()));
    }
    private void updateLevelSegments(int id,int level,String bonus) {
        LinearLayout row = findViewById(id);
        if (row.getChildCount()==0) {
            // O primeiro quadrado é o nível inicial; cada quadrado restante representa uma melhoria comprável.
            for (int i=0;i<=GameConfig.UPGRADE_LIMIT;i++) {
                View square = new View(this); square.setBackgroundResource(R.drawable.combat_level_segment);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp(12),dp(12));
                if (i>0) params.setMarginStart(dp(2));
                row.addView(square,params);
            }
        }
        for (int i=0;i<row.getChildCount();i++) row.getChildAt(i).setActivated(i<=level);
        row.setContentDescription(getString(R.string.combat_level,level,GameConfig.UPGRADE_LIMIT,bonus));
    }

    private void updateBattleUi() {
        if (screen != Screen.BATTLE || engine == null) return;
        ((TextView)findViewById(R.id.lives)).setText(getString(R.string.hud_lives,engine.lives()));
        ((TextView)findViewById(R.id.credits)).setText(getString(R.string.hud_credits,engine.credits()));
        ((TextView)findViewById(R.id.wave)).setText(getString(R.string.hud_wave,engine.wave(),GameConfig.WAVE_COUNTS.length));
        boolean ready = engine.status() == GameEngine.Status.READY;
        Button start = findViewById(R.id.start_wave);
        start.setEnabled(ready); start.setText(R.string.combat_play);
        start.setContentDescription(getString(ready ? R.string.start_wave : R.string.running_wave));
        Button pause = findViewById(R.id.pause_button);
        pause.setEnabled(engine.status() == GameEngine.Status.RUNNING);
        pause.setText(engine.paused() ? R.string.combat_play : R.string.combat_pause);
        pause.setContentDescription(getString(engine.paused() ? R.string.resume : R.string.pause));
        ((Button)findViewById(R.id.speed_button)).setText(speed == 1 ? R.string.speed_normal : R.string.speed_fast);
        for (Map.Entry<String,Button> entry : battleButtons.entrySet()) {
            HeroCatalog.Hero hero = HeroCatalog.find(entry.getKey());
            boolean placed = engine.placed(hero.id);
            boolean selected = hero.id.equals(pendingHero) || (selectedUnit!=null && hero.id.equals(selectedUnit.hero.id));
            boolean available = placed || engine.credits()>=hero.cost;
            int state = placed ? R.string.unit_deployed : !available ? R.string.combat_no_credits
                : selected ? R.string.combat_selected : R.string.combat_available;
            String stateText = getString(state);
            entry.getValue().setText("");
            entry.getValue().setEnabled(available);
            entry.getValue().setTooltipText(getString(R.string.combat_card_state,hero.name,stateText,hero.cost));
            entry.getValue().setContentDescription(getString(R.string.combat_card_state,hero.name,stateText,hero.cost));
            View card = battleCards.get(hero.id); card.setEnabled(available); card.setSelected(selected); card.setActivated(placed);
        }
        if (pendingHero == null) ((TextView)findViewById(R.id.battle_hint)).setText(engine.wave() == 0 ? R.string.ready_hint : ready ? R.string.wave_ready : R.string.wave_running);
        else updatePlacementUi();
        updateUpgradeUi();
    }

    private void showProfile(HeroCatalog.Hero hero) {
        ComicVineRepository.Profile profile = repository.profile(hero.id);
        String message = profile == null ? getString(R.string.profile_unavailable)
            : getString(R.string.profile_body,profile.name,profile.description,
                profile.powers.isEmpty() ? getString(R.string.powers_unavailable) : android.text.TextUtils.join(", ",profile.powers),
                hero.role,hero.damage,hero.range,hero.cost);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this).setTitle(hero.name).setMessage(message).setPositiveButton(R.string.profile_ok,null);
        if (profile != null && safeSource(profile.sourceUrl)) dialog.setNeutralButton(R.string.profile_source,(d,w) -> {
            try { startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(profile.sourceUrl))); }
            catch (android.content.ActivityNotFoundException unavailable) { toast(R.string.profile_unavailable); }
        });
        // Abrir a ficha, assim como o painel de melhorias, não pausa a simulação.
        dialog.show();
    }
    private boolean safeSource(String url) {
        Uri uri = Uri.parse(url);
        return "https".equals(uri.getScheme()) && "comicvine.gamespot.com".equals(uri.getHost());
    }
    private void leaveBattle() {
        if (engine.finished()) { showMenu(); return; }
        boolean previouslyPaused = engine.paused(); engine.setPaused(true);
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(R.string.leave_title).setMessage(R.string.leave_body)
            .setPositiveButton(R.string.leave_yes,(d,w) -> showMenu()).setNegativeButton(R.string.cancel,null).create();
        dialog.setOnDismissListener(d -> {
            if (screen == Screen.BATTLE && engine != null && resumed) engine.setPaused(previouslyPaused);
        });
        dialog.show();
    }
    private void showResult() {
        if (resultShown) return; resultShown = true;
        closeUpgrade(); pendingHero = null; battlefield.pending(null,0,0,false);
        resultDialog = new AlertDialog.Builder(this)
            .setTitle(engine.status() == GameEngine.Status.WON ? R.string.victory : R.string.defeat)
            .setMessage(getString(R.string.result_body,engine.wave(),GameConfig.WAVE_COUNTS.length,engine.kills(),engine.lives()))
            .setPositiveButton(R.string.retry,(d,w) -> { resultDialog = null; startBattle(); })
            .setNegativeButton(R.string.menu,(d,w) -> showMenu()).setCancelable(false).create();
        resultDialog.show();
    }
    private void startFrames() {
        if (!resumed || ticking || screen != Screen.BATTLE || engine == null || engine.finished()) return;
        ticking = true; lastFrame = 0; lastHud = 0; accumulator = 0;
        Choreographer.getInstance().postFrameCallback(frame);
    }
    private void stopFrames() {
        ticking = false; lastFrame = 0; accumulator = 0;
        Choreographer.getInstance().removeFrameCallback(frame);
    }
    private void visualForeground(boolean foreground) {
        CampaignPager pager = findViewById(R.id.campaign_pager);
        if (pager!=null) pager.visualForeground(foreground);
        BattlefieldView preview = findViewById(R.id.mission_preview);
        if (preview!=null) preview.visualForeground(foreground);
        if (battlefield!=null) battlefield.visualForeground(foreground);
    }
    @Override protected void onResume() { super.onResume(); resumed = true; visualForeground(true); startFrames(); }
    @Override protected void onPause() {
        resumed = false; stopFrames();
        visualForeground(false);
        if (engine != null) engine.setPaused(true);
        super.onPause();
    }
    @Override protected void onDestroy() { stopFrames(); repository.close(); super.onDestroy(); }

    private TextView label(String value,int size,int color,boolean bold) {
        TextView text = new TextView(this); text.setText(value); text.setTextSize(size); text.setTextColor(color);
        if (bold) text.setTypeface(null,Typeface.BOLD); return text;
    }
    private int dp(int value) { return Math.round(value*getResources().getDisplayMetrics().density); }
    private void toast(int resource) { Toast.makeText(this,resource,Toast.LENGTH_SHORT).show(); }
}

