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
    private GameView game;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game);

        TextView curLevel = findViewById(R.id.cur_level);
        game = findViewById(R.id.game_view);

        String text;

        if (MenuActivity.curLevel >= 0) {
            text = String.format(getResources().getString(R.string.cur_level), MenuActivity.curLevel + 1, MenuActivity.grids.size());
        } else {
            text = String.format(getResources().getString(R.string.cur_level_random), getResources().getStringArray(R.array.levels)[-1 * MenuActivity.curLevel - 1]);
        }

        curLevel.setText(text);

        game.setOnWinLevel(new GameView.OnWinLevelListener() {
            @Override
            public boolean onWinLevel(View v, int level) {
                if (level >= MenuActivity.grids.size()) {
                    Intent intent = new Intent(GameActivity.this, FinalActivity.class);
                    startActivity(intent);

                    return false;
                }

                TextView txtCurLevel = findViewById(R.id.cur_level);

                if (level >= 0) {
                    String text = String.format(getResources().getString(R.string.cur_level), MenuActivity.curLevel + 1, MenuActivity.grids.size());
                    txtCurLevel.setText(text);
                }

                return true;
            }
        });

        ImageButton btnOpenActions = findViewById(R.id.open_actions);
        btnOpenActions.setOnClickListener(new View.OnClickListener() {
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
        int curLevelPref = MenuActivity.prefs.getInt("cur_level", 0);

        MenuItem btnCancelLastAction = menu.findItem(R.id.btn_cancel_last_action);
        btnCancelLastAction.setVisible(game.grid.actions.size() != 0);

        MenuItem btnGoNextLevel = menu.findItem(R.id.btn_go_next_level);
        btnGoNextLevel.setVisible(MenuActivity.curLevel >= 0 && MenuActivity.curLevel + 1 <= curLevelPref);

        MenuItem btnRestartLevel = menu.findItem(R.id.btn_restart_level);
        btnRestartLevel.setVisible(game.grid.actions.size() > 0);

        MenuItem btnRegenLevel = menu.findItem(R.id.btn_regen_level);
        btnRegenLevel.setVisible(MenuActivity.curLevel <= -1);

        return true;
    }

    @Override
    public void onStart() {
        game.grid.genGrid();
        game.resize();

        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.btn_cancel_last_action) {
            game.grid.cancelLastAction();

            return true;
        }

        if (itemId == R.id.btn_go_next_level) {
            game.grid.genGrid();
            MenuActivity.curLevel++;

            TextView curLevel = findViewById(R.id.cur_level);
            String text = String.format(getResources().getString(R.string.cur_level), MenuActivity.curLevel + 1, MenuActivity.grids.size());
            curLevel.setText(text);

            game.grid = MenuActivity.grids.get(MenuActivity.curLevel);
            game.resize();

            return true;
        }

        if (itemId == R.id.btn_restart_level) {
            game.grid.genGrid();
            game.resize();

            return true;
        }

        if (itemId == R.id.btn_regen_level) {
            game.grid = game.genRandomLevel();
            game.resize();

            return true;
        }

        if (itemId == R.id.btn_go_menu) {
            game.grid.genGrid();
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
