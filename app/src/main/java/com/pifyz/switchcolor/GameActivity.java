package com.pifyz.switchcolor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

public class GameActivity extends Activity {
    protected GameView game;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game);

        TextView cur_level = findViewById(R.id.cur_level);
        game = findViewById(R.id.game_view);

        String text;

        if (MenuActivity.cur_level >= 0) {
            text = String.format(getResources().getString(R.string.cur_level), MenuActivity.cur_level + 1, MenuActivity.grids.size());
        } else {
            text = String.format(getResources().getString(R.string.cur_level_random), getResources().getStringArray(R.array.levels)[-1 * MenuActivity.cur_level - 1]);
        }

        cur_level.setText(text);

        game.setOnWinLevel(new GameView.OnWinLevelListener() {
            @Override
            public boolean onWinLevel(View v, int level) {
                if (level >= MenuActivity.grids.size()) {
                    Intent intent = new Intent(GameActivity.this, FinalActivity.class);
                    startActivity(intent);

                    return false;
                }

                TextView txt_cur_level = findViewById(R.id.cur_level);

                if (level >= 0) {
                    String text = String.format(getResources().getString(R.string.cur_level), MenuActivity.cur_level + 1, MenuActivity.grids.size());
                    txt_cur_level.setText(text);
                }

                return true;
            }
        });

        ImageButton btn_open_actions = findViewById(R.id.open_actions);
        btn_open_actions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOptionsMenu();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_game, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        int cur_level_pref = MenuActivity.prefs.getInt("cur_level", 0);

        MenuItem btn_cancel_last_action = menu.findItem(R.id.btn_cancel_last_action);
        btn_cancel_last_action.setVisible(game.grid.actions.size() != 0);

        MenuItem btn_go_next_level = menu.findItem(R.id.btn_go_next_level);
        btn_go_next_level.setVisible(MenuActivity.cur_level >= 0 && MenuActivity.cur_level + 1 <= cur_level_pref);

        MenuItem btn_restart_level = menu.findItem(R.id.btn_restart_level);
        btn_restart_level.setVisible(game.grid.actions.size() > 0);

        MenuItem btn_regen_level = menu.findItem(R.id.btn_regen_level);
        btn_regen_level.setVisible(MenuActivity.cur_level <= -1);

        return true;
    }

    @Override
    public void onStart() {
        game.grid.gen_grid();
        game.resize();

        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_cancel_last_action:
                game.grid.cancel_last_action();
                return true;

            case R.id.btn_go_next_level:
                game.grid.gen_grid();
                MenuActivity.cur_level++;

                TextView cur_level = findViewById(R.id.cur_level);
                String text = String.format(getResources().getString(R.string.cur_level), MenuActivity.cur_level + 1, MenuActivity.grids.size());
                cur_level.setText(text);

                game.grid = MenuActivity.grids.get(MenuActivity.cur_level);
                game.resize();
                return true;

            case R.id.btn_restart_level:
                game.grid.gen_grid();
                game.resize();
                return true;

            case R.id.btn_regen_level:
                game.grid = game.gen_random_level();
                game.resize();
                return true;

            case R.id.btn_go_menu:
                game.grid.gen_grid();
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
