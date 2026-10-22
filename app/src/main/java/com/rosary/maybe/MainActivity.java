package com.rosary.maybe;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.media.MediaPlayer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends Activity {

    private final int BG = Color.rgb(8, 20, 16);
    private final int CARD = Color.rgb(22, 43, 33);
    private final int GREEN = Color.rgb(40, 190, 110);
    private final int WHITE = Color.WHITE;

    private LinearLayout root, content, nav;
    private int count = 0;
    private int goal = 33;
    private String dhikr = "سبحان الله";
    private TextView counterText, statusText;
    private Spinner reciterSpinner, surahSpinner;
    private Button playButton;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private MediaPlayer player;

    private final ArrayList<String> reciterNames = new ArrayList<>();
    private final ArrayList<String> reciterServers = new ArrayList<>();
    private final ArrayList<String> reciterSurahs = new ArrayList<>();

    private final String[] adhkar = {
        "سبحان الله",
        "الحمد لله",
        "الله أكبر",
        "أستغفر الله",
        "لا إله إلا الله",
        "سبحان الله وبحمده",
        "لا حول ولا قوة إلا بالله",
        "اللهم صل وسلم على نبينا محمد"
    };

    private final String[] surahNames = {
        "الفاتحة","البقرة","آل عمران","النساء","المائدة",
        "الأنعام","الأعراف","الأنفال","التوبة","يونس",
        "هود","يوسف","الرعد","إبراهيم","الحجر",
        "النحل","الإسراء","الكهف","مريم","طه",
        "الأنبياء","الحج","المؤمنون","النور","الفرقان",
        "الشعراء","النمل","القصص","العنكبوت","الروم",
        "لقمان","السجدة","الأحزاب","سبأ","فاطر",
        "يس","الصافات","ص","الزمر","غافر",
        "فصلت","الشورى","الزخرف","الدخان","الجاثية",
        "الأحقاف","محمد","الفتح","الحجرات","ق",
        "الذاريات","الطور","النجم","القمر","الرحمن",
        "الواقعة","الحديد","المجادلة","الحشر","الممتحنة",
        "الصف","الجمعة","المنافقون","التغابن","الطلاق",
        "التحريم","الملك","القلم","الحاقة","المعارج",
        "نوح","الجن","المزمل","المدثر","القيامة",
        "الإنسان","المرسلات","النبأ","النازعات","عبس",
        "التكوير","الانفطار","المطففين","الانشقاق","البروج",
        "الطارق","الأعلى","الغاشية","الفجر","البلد",
        "الشمس","الليل","الضحى","الشرح","التين",
        "العلق","القدر","البينة","الزلزلة","العاديات",
        "القارعة","التكاثر","العصر","الهمزة","الفيل",
        "قريش","الماعون","الكوثر","الكافرون","النصر",
        "المسد","الإخلاص","الفلق","الناس"
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showHome();
    }

    private TextView text(String value, int size) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(WHITE);
        t.setGravity(Gravity.CENTER);
        t.setPadding(12, 12, 12, 12);
        t.setTypeface(null, Typeface.BOLD);
        return t;
    }

    private Button button(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextColor(WHITE);
        b.setAllCaps(false);
        b.setBackgroundTintList(
            android.content.res.ColorStateList.valueOf(GREEN)
        );
        return b;
    }

    private LinearLayout column() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setPadding(18, 12, 18, 12);
        return l;
    }

    private void showHome() {
        root = column();
        root.setBackgroundColor(BG);

        TextView title = text("المسبحة الإلكترونية", 26);
        root.addView(title);

        nav = new LinearLayout(this);
        nav.setGravity(Gravity.CENTER);
        nav.setOrientation(LinearLayout.HORIZONTAL);

        Button tasbeehTab = button("📿 التسبيح");
        Button hadithTab = button("📜 الأحاديث");
        Button quranTab = button("📖 القرآن");

        nav.addView(tasbeehTab, new LinearLayout.LayoutParams(0, -2, 1));
        nav.addView(hadithTab, new LinearLayout.LayoutParams(0, -2, 1));
        nav.addView(quranTab, new LinearLayout.LayoutParams(0, -2, 1));
        root.addView(nav);

        content = column();
        root.addView(content, new LinearLayout.LayoutParams(-1, 0, 1));

        tasbeehTab.setOnClickListener(v -> showTasbeeh());
        hadithTab.setOnClickListener(v -> showHadiths());
        quranTab.setOnClickListener(v -> showQuran());

        setContentView(root);
        showTasbeeh();
    }

    private void clearContent() {
        content.removeAllViews();
    }

    private void showTasbeeh() {
        clearContent();

        content.addView(text("📿 التسبيح", 24));

        Spinner dhikrSpinner = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            adhkar
        );
        dhikrSpinner.setAdapter(adapter);
        content.addView(dhikrSpinner);

        dhikrSpinner.setOnItemSelectedListener(
            new AdapterView.OnItemSelectedListener() {
                public void onNothingSelected(AdapterView<?> p) {}

                public void onItemSelected(
                    AdapterView<?> p, View v, int position, long id
                ) {
                    dhikr = adhkar[position];
                }
            }
        );

        Spinner goalSpinner = new Spinner(this);
        String[] goals = {"33", "100", "1000"};
        goalSpinner.setAdapter(new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            goals
        ));
        content.addView(text("اختر هدف التسبيح", 16));
        content.addView(goalSpinner);

        goalSpinner.setOnItemSelectedListener(
            new AdapterView.OnItemSelectedListener() {
                public void onNothingSelected(AdapterView<?> p) {}

                public void onItemSelected(
                    AdapterView<?> p, View v, int position, long id
                ) {
                    goal = Integer.parseInt(goals[position]);
                }
            }
        );

        counterText = text("0", 54);
        content.addView(counterText);

        content.addView(text(dhikr, 22));

        Button countButton = button("سبّح");
        content.addView(countButton);

        countButton.setOnClickListener(v -> {
            count++;
            counterText.setText(String.valueOf(count));
            if (count == goal) {
                Toast.makeText(
                    this,
                    "أحسنت! وصلت إلى الهدف",
                    Toast.LENGTH_SHORT
                ).show();
            }
        });

        Button resetButton = button("تصفير العدّاد");
        content.addView(resetButton);
        resetButton.setOnClickListener(v -> {
            count = 0;
            counterText.setText("0");
        });
    }

    private void showHadiths() {
        clearContent();
        content.addView(text("📜 الأحاديث", 24));

        String[] hadiths = {
            "قال رسول الله ﷺ: «إنما الأعمال بالنيات» — رواه البخاري ومسلم.",
            "قال رسول الله ﷺ: «الدين النصيحة» — رواه مسلم.",
            "قال رسول الله ﷺ: «الكلمة الطيبة صدقة» — رواه البخاري ومسلم.",
            "قال رسول الله ﷺ: «يسروا ولا تعسروا» — رواه البخاري ومسلم.",
            "قال رسول الله ﷺ: «من كان يؤمن بالله واليوم الآخر فليقل خيرًا أو ليصمت» — رواه البخاري ومسلم."
        };

        for (String h : hadiths) {
            TextView item = text(h, 18);
            item.setBackgroundColor(CARD);
            LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(-1, -2);
            lp.setMargins(0, 8, 0, 8);
            content.addView(item, lp);
        }
    }

    private void showQuran() {
        clearContent();
        content.addView(text("📖 القرآن الكريم", 24));

        statusText = text("جارٍ تحميل القرّاء...", 16);
        content.addView(statusText);

        content.addView(text("اختر القارئ", 16));

        reciterSpinner = new Spinner(this);
        content.addView(reciterSpinner);

        content.addView(text("اختر السورة", 16));

        surahSpinner = new Spinner(this);
        ArrayAdapter<String> surahAdapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            surahNames
        );
        surahSpinner.setAdapter(surahAdapter);
        content.addView(surahSpinner);

        playButton = button("▶ تشغيل السورة");
        content.addView(playButton);
        playButton.setEnabled(false);

        Button stopButton = button("⏹ إيقاف التلاوة");
        content.addView(stopButton);

        playButton.setOnClickListener(v -> playSelectedSurah());
        stopButton.setOnClickListener(v -> stopAudio());

        loadReciters();
    }

    private void loadReciters() {
        new Thread(() -> {
            HttpURLConnection connection = null;

            try {
                URL url = new URL(
                    "https://www.mp3quran.net/api/v3/reciters?language=ar"
                );

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                connection.setRequestProperty(
                    "Accept", "application/json"
                );

                int code = connection.getResponseCode();

                if (code != 200) {
                    throw new Exception("HTTP " + code);
                }

                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "UTF-8")
                );

                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject json = new JSONObject(response.toString());
                JSONArray reciters = json.getJSONArray("reciters");

                ArrayList<String> names = new ArrayList<>();
                ArrayList<String> servers = new ArrayList<>();
                ArrayList<String> availableSurahs = new ArrayList<>();

                for (int i = 0; i < reciters.length(); i++) {
                    JSONObject reciter = reciters.getJSONObject(i);
                    String name = reciter.optString("name", "قارئ");

                    JSONArray moshafs = reciter.optJSONArray("moshaf");
                    if (moshafs == null) continue;

                    for (int j = 0; j < moshafs.length(); j++) {
                        JSONObject moshaf = moshafs.getJSONObject(j);
                        String server = moshaf.optString("server", "");
                        String list = moshaf.optString("surah_list", "");

                        if (!server.isEmpty() && !list.isEmpty()) {
                            names.add(name + " - " +
                                moshaf.optString("name", "رواية"));
                            servers.add(server);
                            availableSurahs.add(list);
                        }
                    }
                }

                handler.post(() -> {
                    if (isFinishing()) return;

                    reciterNames.clear();
                    reciterNames.addAll(names);
                    reciterServers.clear();
                    reciterServers.addAll(servers);
                    reciterSurahs.clear();
                    reciterSurahs.addAll(availableSurahs);

                    if (names.isEmpty()) {
                        statusText.setText(
                            "لم يصل أي قارئ من المصدر. جرّب اتصالًا آخر."
                        );
                        return;
                    }

                    reciterSpinner.setAdapter(new ArrayAdapter<>(
                        MainActivity.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        reciterNames
                    ));

                    statusText.setText("تم تحميل " + names.size() + " رواية");
                    playButton.setEnabled(true);
                });

            } catch (Exception e) {
                handler.post(() -> {
                    if (isFinishing()) return;
                    statusText.setText(
                        "تعذّر تحميل القرّاء. تأكد من الإنترنت ثم اضغط القرآن مجددًا."
                    );
                    Toast.makeText(
                        MainActivity.this,
                        "فشل تحميل القرّاء: " + e.getMessage(),
                        Toast.LENGTH_LONG
                    ).show();
                });

            } finally {
                if (connection != null) connection.disconnect();
            }
        }).start();
    }

    private void playSelectedSurah() {
        if (reciterServers.isEmpty()) {
            Toast.makeText(this, "قائمة القرّاء غير جاهزة", Toast.LENGTH_SHORT).show();
            return;
        }

        int reciterIndex = reciterSpinner.getSelectedItemPosition();
        int surahIndex = surahSpinner.getSelectedItemPosition();

        if (reciterIndex < 0 || reciterIndex >= reciterServers.size()) return;

        String surahNumber = String.format(
            java.util.Locale.US, "%03d", surahIndex + 1
        );

        String available = reciterSurahs.get(reciterIndex);
        boolean hasSurah = false;

        for (String s : available.split(",")) {
            if (s.trim().equals(String.valueOf(surahIndex + 1))) {
                hasSurah = true;
                break;
            }
        }

        if (!hasSurah) {
            Toast.makeText(
                this,
                "هذا القارئ لا يوفّر هذه السورة في الرواية المختارة",
                Toast.LENGTH_LONG
            ).show();
            return;
        }

        String server = reciterServers.get(reciterIndex);
        if (!server.endsWith("/")) server += "/";

        String audioUrl = server + surahNumber + ".mp3";

        stopAudio();
        statusText.setText("جارٍ تشغيل سورة " + surahNames[surahIndex] + "...");

        player = new MediaPlayer();

        try {
            player.setDataSource(audioUrl);

            player.setOnPreparedListener(mp -> {
                mp.start();
                statusText.setText(
                    "يتم تشغيل سورة " + surahNames[surahIndex]
                );
            });

            player.setOnErrorListener((mp, what, extra) -> {
                statusText.setText("تعذّر تشغيل التلاوة. جرّب قارئًا آخر.");
                stopAudio();
                return true;
            });

            player.prepareAsync();

        } catch (Exception e) {
            statusText.setText("تعذّر بدء التلاوة.");
            stopAudio();
        }
    }

    private void stopAudio() {
        if (player != null) {
            try {
                if (player.isPlaying()) player.stop();
            } catch (Exception ignored) {}

            player.release();
            player = null;
        }
    }

    @Override
    protected void onDestroy() {
        stopAudio();
        super.onDestroy();
    }
}