package com.pifyz.switchcolor;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MenuActivity extends Activity {
    public static ArrayList<Grid> grids;
    public static int cur_level;
    public static SharedPreferences prefs;
    public static Resources resources;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences("sc_save", Context.MODE_PRIVATE);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_menu);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Fabrica.otf");

        resources = getResources();

        Button btn_play_states = findViewById(R.id.btn_play_show);
        if (btn_play_states != null) {
            btn_play_states.setText(btn_play_states.getText().toString().toUpperCase());
            btn_play_states.setTypeface(face);
            btn_play_states.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(MenuActivity.this, ChooseLevelActivity.class);
                    startActivity(intent);
                }
            });
        }

        Button btn_instructions_states = findViewById(R.id.btn_instructions_show);
        btn_instructions_states.setText(btn_instructions_states.getText().toString().toUpperCase());
        btn_instructions_states.setTypeface(face);
        btn_instructions_states.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, InstructionsActivity.class);
                startActivity(intent);
            }
        });

        Button btn_options_states = findViewById(R.id.btn_options_show);
        btn_options_states.setText(btn_options_states.getText().toString().toUpperCase());
        btn_options_states.setTypeface(face);
        btn_options_states.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, OptionsActivity.class);
                startActivity(intent);
            }
        });

        AssetManager assetManager = getAssets();
        InputStream input;
        try {
            input = assetManager.open("level.csv");

            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();

            String text = new String(buffer);

            String[] lines_levels = text.split("\r\n");

            grids = new ArrayList<Grid>();

            int[] colors = {
                    0xFF00C180, 0xFF00C12B, 0xFF009EC1,
                    0xFFC10A00, 0xFFC0C100, 0xFF06C100,
                    0xFF00C1B2, 0xFFC10027, 0xFFC19100,
                    0xFF38C100, 0xFF8DC100, 0xFF0049C1,
                    0xFF7000C1, 0xFFC1005A, 0xFFC15F00,
                    0xFF6AC100, 0xFF00C14E, 0xFFC12D00
            };

            int line = 0;
            for (String line_level : lines_levels) {
                String[] infos = line_level.split(",");
                String[] sizes = infos[0].split("x");

                ArrayList<Integer> cells = new ArrayList<>();

                for (int i = 0; i < infos[2].length(); i++)
                    cells.add(Character.getNumericValue(infos[2].charAt(i)));

                int color = colors[line++ % colors.length];

                grids.add(new Grid(Integer.parseInt(sizes[0]), Integer.parseInt(sizes[1]), Integer.parseInt(infos[1]), cells, color));
            }
        } catch (IOException ex) {
            Logger.getLogger(MenuActivity.class.getName()).log(Level.SEVERE, null, ex);
        }

        SharedPreferences.Editor editor = prefs.edit();

        int[] new_levels = {47, 63, 75, 82};

        StringBuilder levels = new StringBuilder();
        if (prefs.getString("levels", "").equals("")) {
            for (int i = 0; i < grids.size(); i++)
                levels.append(0);

            levels.replace(0, 1, "1");
            editor.putString("levels", levels.toString());
        }

        editor.apply();

        String cur_levels = prefs.getString("levels", "");
        if (cur_levels.length() != grids.size()) {
            levels = new StringBuilder(cur_levels);

            int cur_level = MenuActivity.prefs.getInt("cur_level", 0);

            for (int new_level : new_levels) {
                if (new_level < cur_levels.length())
                    levels.insert(new_level - 1, new_level <= cur_level + 1 ? 1 : 0);
                else
                    levels.append(0);
            }

            editor.putString("levels", levels.toString());
        }

        editor.commit();

        cur_levels = prefs.getString("levels", "");
        if (cur_levels.length() != grids.size()) {
            levels = new StringBuilder(cur_levels);

            for (int i = 0; i < grids.size() - cur_levels.length(); i++)
                levels.append(0);

            editor.putString("levels", levels.toString());
        }

        StringBuilder medals = new StringBuilder();
        if (prefs.getString("medals", "").equals("")) {
            for (int i = 0; i < grids.size(); i++)
                medals.append(0);

            editor.putString("medals", medals.toString());
        }

        editor.commit();

        String cur_medals = prefs.getString("medals", "");
        if (cur_medals.length() != grids.size()) {
            medals = new StringBuilder(cur_medals);

            for (int new_level : new_levels) {
                if (new_level < cur_medals.length())
                    medals.insert(new_level - 1, 0);
                else
                    medals.append(0);
            }

            editor.putString("medals", medals.toString());
        }

        editor.commit();

        cur_medals = prefs.getString("medals", "");
        if (cur_medals.length() != grids.size()) {
            medals = new StringBuilder(cur_medals);

            for (int i = 0; i < grids.size() - cur_medals.length(); i++)
                medals.append(0);

            editor.putString("medals", medals.toString());
        }

        editor.commit();

        editor.putInt("cur_level", prefs.getString("levels", "").lastIndexOf("1"));

        editor.commit();

        //AdBuddiz.setPublisherKey(getResources().getString(R.string.adbuddiz_key));
        //AdBuddiz.cacheAds(this);
    }
}
