package com.pifyz.switchcolor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class OptionsActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_options);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        Button reset_game = findViewById(R.id.btn_reset);
        reset_game.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                builder
                        .setTitle(R.string.valid_reset_game)
                        .setMessage(R.string.valid_reset_game_text)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor editor = MenuActivity.prefs.edit();

                                StringBuilder levels = new StringBuilder();
                                StringBuilder medals = new StringBuilder();

                                for (int i = 0; i < MenuActivity.grids.size(); i++) {
                                    levels.append(0);
                                    medals.append(0);
                                }

                                levels.replace(0, 1, "1");

                                editor.putString("levels", levels.toString());
                                editor.putString("medals", medals.toString());
                                editor.apply();

                                editor.putInt("cur_level", 0);
                                editor.commit();

                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.txt_reset_done), Toast.LENGTH_LONG).show();

                                Button reset_game = findViewById(R.id.btn_reset);
                                reset_game.setEnabled(false);
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // ne rien faire
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        ImageButton btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }
}
