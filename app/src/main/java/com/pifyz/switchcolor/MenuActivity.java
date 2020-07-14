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
    public static int curLevel;
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

        Button btnPlayStates = findViewById(R.id.btn_play_show);
        if (btnPlayStates != null) {
            btnPlayStates.setText(btnPlayStates.getText().toString().toUpperCase());
            btnPlayStates.setTypeface(face);
            btnPlayStates.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(MenuActivity.this, ChooseLevelActivity.class);
                    startActivity(intent);
                }
            });
        }

        Button btnInstructionsStates = findViewById(R.id.btn_instructions_show);
        btnInstructionsStates.setText(btnInstructionsStates.getText().toString().toUpperCase());
        btnInstructionsStates.setTypeface(face);
        btnInstructionsStates.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, InstructionsActivity.class);
                startActivity(intent);
            }
        });

        Button btnOptionsStates = findViewById(R.id.btn_options_show);
        btnOptionsStates.setText(btnOptionsStates.getText().toString().toUpperCase());
        btnOptionsStates.setTypeface(face);
        btnOptionsStates.setOnClickListener(new View.OnClickListener() {
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

            String[] linesLevels = text.split("\r\n");

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
            for (String line_level : linesLevels) {
                String[] infos = line_level.split(",");
                String[] sizes = infos[0].split("x");

                ArrayList<Integer> cells = new ArrayList<>();

                for (int i = 0; i < infos[2].length(); i++) {
                    cells.add(Character.getNumericValue(infos[2].charAt(i)));
                }

                int color = colors[line++ % colors.length];

                grids.add(new Grid(Integer.parseInt(sizes[0]), Integer.parseInt(sizes[1]), Integer.parseInt(infos[1]), cells, color));
            }
        } catch (IOException ex) {
            Logger.getLogger(MenuActivity.class.getName()).log(Level.SEVERE, null, ex);
        }

        SharedPreferences.Editor editor = prefs.edit();

        int[] newLevels = {47, 63, 75, 82};

        StringBuilder levels = new StringBuilder();
        if (prefs.getString("levels", "").equals("")) {
            for (int i = 0; i < grids.size(); i++) {
                levels.append(0);
            }

            levels.replace(0, 1, "1");
            editor.putString("levels", levels.toString());
        }

        editor.apply();

        String curLevels = prefs.getString("levels", "");
        if (curLevels.length() != grids.size()) {
            levels = new StringBuilder(curLevels);

            int curLevel = MenuActivity.prefs.getInt("cur_level", 0);

            for (int newLevel : newLevels) {
                if (newLevel < curLevels.length()) {
                    levels.insert(newLevel - 1, newLevel <= curLevel + 1 ? 1 : 0);
                } else {
                    levels.append(0);
                }
            }

            editor.putString("levels", levels.toString());
        }

        editor.commit();

        curLevels = prefs.getString("levels", "");
        if (curLevels.length() != grids.size()) {
            levels = new StringBuilder(curLevels);

            for (int i = 0; i < grids.size() - curLevels.length(); i++) {
                levels.append(0);
            }

            editor.putString("levels", levels.toString());
        }

        StringBuilder medals = new StringBuilder();
        if (prefs.getString("medals", "").equals("")) {
            for (int i = 0; i < grids.size(); i++) {
                medals.append(0);
            }

            editor.putString("medals", medals.toString());
        }

        editor.commit();

        String curMedals = prefs.getString("medals", "");
        if (curMedals.length() != grids.size()) {
            medals = new StringBuilder(curMedals);

            for (int newLevel : newLevels) {
                if (newLevel < curMedals.length()) {
                    medals.insert(newLevel - 1, 0);
                } else {
                    medals.append(0);
                }
            }

            editor.putString("medals", medals.toString());
        }

        editor.commit();

        curMedals = prefs.getString("medals", "");
        if (curMedals.length() != grids.size()) {
            medals = new StringBuilder(curMedals);

            for (int i = 0; i < grids.size() - curMedals.length(); i++) {
                medals.append(0);
            }

            editor.putString("medals", medals.toString());
        }

        editor.commit();

        editor.putInt("cur_level", prefs.getString("levels", "").lastIndexOf("1"));

        editor.commit();
    }
}
