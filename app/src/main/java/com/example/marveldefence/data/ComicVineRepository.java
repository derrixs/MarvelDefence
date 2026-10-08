package com.example.marveldefence.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.example.marveldefence.BuildConfig;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Identity metadata only. Explicit refresh, one worker, cached responses, no combat dependency. */
public final class ComicVineRepository {
    public static final class Profile {
        public final String name, description, imageUrl, sourceUrl;
        public final List<String> powers;
        Profile(JSONObject json) {
            name = clean(json, "name"); description = clean(json, "deck");
            sourceUrl = clean(json, "site_detail_url");
            JSONObject image = json.optJSONObject("image");
            imageUrl = image == null ? "" : clean(image, "small_url");
            ArrayList<String> values = new ArrayList<>();
            JSONArray array = json.optJSONArray("powers");
            if (array != null) for (int i = 0; i < array.length(); i++) {
                JSONObject power = array.optJSONObject(i);
                if (power != null && !clean(power,"name").isEmpty()) values.add(clean(power,"name"));
            }
            powers = Collections.unmodifiableList(values);
        }
        private static String clean(JSONObject json, String key) {
            return json.isNull(key) ? "" : json.optString(key, "");
        }
    }
    public interface Callback { void finished(int loaded, int failed); }
    private final Context context;
    private final ExecutorService worker = Executors.newSingleThreadExecutor();
    private final Handler main = new Handler(Looper.getMainLooper());
    private final Map<String, Profile> profiles = new HashMap<>();
    private boolean loading, closed;
    public ComicVineRepository(Context context) {
        this.context = context.getApplicationContext();
        // Bundled records are real Comic Vine responses, fetched for this prototype.
        try (InputStream input = context.getAssets().open("comicvine_profiles.json")) {
            JSONObject data = new JSONObject(read(input));
            for (HeroCatalog.Hero hero : HeroCatalog.ALL) {
                JSONObject record = data.optJSONObject(hero.id);
                if (record != null) profiles.put(hero.id, new Profile(record));
            }
        } catch (Exception ignored) { /* Missing catalog stays explicitly unavailable. */ }
        for (HeroCatalog.Hero hero : HeroCatalog.ALL) {
            String cached = this.context.getSharedPreferences("comicvine",Context.MODE_PRIVATE).getString(hero.id, "");
            if (!cached.isEmpty()) try { profiles.put(hero.id,new Profile(new JSONObject(cached))); }
            catch (Exception ignored) { /* Keep bundled record if cache is corrupt. */ }
        }
    }
    public Profile profile(String id) { return profiles.get(id); }
    public boolean loading() { return loading; }
    public boolean hasKey() { return !BuildConfig.COMIC_VINE_KEY.isEmpty(); }
    public boolean refresh(Callback callback) {
        long now = System.currentTimeMillis();
        long lastAttempt = context.getSharedPreferences("comicvine",Context.MODE_PRIVATE).getLong("last_attempt",0);
        // Six records per refresh; two minutes keeps this client below 200 detail requests/hour.
        if (closed || loading || !hasKey() || now-lastAttempt < 120000 && lastAttempt != 0) return false;
        loading = true;
        context.getSharedPreferences("comicvine",Context.MODE_PRIVATE).edit().putLong("last_attempt",now).apply();
        worker.execute(() -> {
            Map<String, Profile> updated = new HashMap<>();
            int failures = 0;
            for (HeroCatalog.Hero hero : HeroCatalog.ALL) {
                if (Thread.currentThread().isInterrupted()) return;
                try {
                    JSONObject result = fetch(hero.comicVineId);
                    // Every playable identity is fixed and publisher-checked, not search-selected.
                    if (result.optInt("id") != hero.comicVineId || result.optString("name").isEmpty()) throw new Exception("Identity");
                    JSONObject publisher = result.optJSONObject("publisher");
                    if (publisher == null || !publisher.optString("name").startsWith("Marvel")) throw new Exception("Publisher");
                    updated.put(hero.id,new Profile(result));
                    context.getSharedPreferences("comicvine",Context.MODE_PRIVATE).edit().putString(hero.id,result.toString()).apply();
                } catch (Exception ignored) { failures++; /* Never log authenticated URLs. */ }
                try { Thread.sleep(1100); } catch (InterruptedException interrupted) { Thread.currentThread().interrupt(); return; }
            }
            final int failed = failures;
            main.post(() -> {
                if (closed) return;
                profiles.putAll(updated); loading = false; callback.finished(updated.size(),failed);
            });
        });
        return true;
    }
    private JSONObject fetch(int id) throws Exception {
        URL endpoint = new URL("https://comicvine.gamespot.com/api/character/4005-" + id
            + "/?format=json&field_list=id,name,deck,powers,publisher,image,site_detail_url&api_key="
            + URLEncoder.encode(BuildConfig.COMIC_VINE_KEY, "UTF-8"));
        HttpURLConnection connection = (HttpURLConnection) endpoint.openConnection();
        connection.setRequestProperty("User-Agent", "MarvelDefencePrototype/0.1 (Android)");
        connection.setConnectTimeout(10000); connection.setReadTimeout(12000);
        connection.setInstanceFollowRedirects(false);
        try {
            if (connection.getResponseCode() != 200) throw new Exception("HTTP");
            JSONObject response;
            try (InputStream input = connection.getInputStream()) { response = new JSONObject(read(input)); }
            if (response.optInt("status_code") != 1) throw new Exception("API");
            return response.getJSONObject("results");
        } finally { connection.disconnect(); }
    }
    private static String read(InputStream stream) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096]; int size;
        while ((size = stream.read(buffer)) != -1) {
            output.write(buffer,0,size);
            if (output.size() > 1024*1024) throw new Exception("Response too large");
        }
        return output.toString(StandardCharsets.UTF_8.name());
    }
    public void close() { closed = true; worker.shutdownNow(); main.removeCallbacksAndMessages(null); }
}
