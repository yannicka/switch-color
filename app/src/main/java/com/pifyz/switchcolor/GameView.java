package com.pifyz.switchcolor;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

public class GameView extends View {
    public Grid grid;

    protected Paint p;
    protected ParticleSystem particles;
    protected Medal medal;
    protected OnWinLevelListener onWinLevel;
    protected int tx, ty;

    interface OnWinLevelListener {
        boolean onWinLevel(View v, int level);
    }

    public GameView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);

        p = new Paint();

        particles = new ParticleSystem();
        medal = null;

        if (MenuActivity.cur_level >= 0) {
            grid = MenuActivity.grids.get(MenuActivity.cur_level);
        } else {
            grid = gen_random_level();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        performClick();

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) ev.getX() - tx;
            int y = (int) ev.getY() - ty;

            if (grid.pushed(x, y)) {
                particles.add_particles(10, x, y);
            }

            if (grid.is_empty()) {
                if (MenuActivity.cur_level >= 0) {
                    medal = grid.get_medal();
                    grid.gen_grid();

                    MenuActivity.cur_level++;

                    SharedPreferences.Editor editor = MenuActivity.prefs.edit();

                    int cur_level_pref = MenuActivity.prefs.getInt("cur_level", 0);

                    if (cur_level_pref < MenuActivity.cur_level) {
                        editor.putInt("cur_level", MenuActivity.cur_level);
                    }

                    String medals = MenuActivity.prefs.getString("medals", "");
                    String levels = MenuActivity.prefs.getString("levels", "");

                    int prev_level = MenuActivity.cur_level - 1;

                    if (Character.getNumericValue(medals.charAt(prev_level)) < medal.level) {
                        medals = medals.substring(0, prev_level) + medal.level + medals.substring(MenuActivity.cur_level);
                    }

                    editor.putString("medals", medals);
                    editor.apply();

                    if (onWinLevel != null) {
                        boolean no_final = onWinLevel.onWinLevel(this, MenuActivity.cur_level);

                        if (!no_final) {
                            return true;
                        }
                    }

                    levels = levels.substring(0, MenuActivity.cur_level) + '1' + levels.substring(MenuActivity.cur_level + 1);

                    editor.putString("levels", levels);
                    editor.commit();

                    grid = MenuActivity.grids.get(MenuActivity.cur_level);
                    onSizeChanged(getWidth(), getHeight(), getWidth(), getHeight());
                } else {
                    grid = gen_random_level();

                    onSizeChanged(getWidth(), getHeight(), getWidth(), getHeight());

                    if (onWinLevel != null) {
                        onWinLevel.onWinLevel(this, MenuActivity.cur_level);
                    }
                }
            }
        }

        return true;
    }

    @Override
    public boolean performClick() {
        super.performClick();

        return true;
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        resize(w, h);
    }

    public void resize() {
        resize(getWidth(), getHeight());
    }

    public void resize(int w, int h) {
        grid.resize(w, h);

        tx = getWidth() / 2 - grid.get_width() / 2;
        ty = getHeight() / 2 - grid.get_height() / 2;
    }

    @Override
    protected void onDraw(Canvas can) {
        can.drawColor(getResources().getColor(R.color.bg_activities));

        can.save();
        can.translate(tx, ty);

        particles.update();

        if (medal != null) {
            medal.update();
        }

        grid.draw(can, p);
        particles.draw(can, p);

        can.restore();

        can.save();
        can.translate(0, ty);

        // Texte de victoire
        if (medal != null) {
            medal.draw(can, p);

            if (medal.life == 0) {
                medal = null;
            }
        }

        can.restore();

        invalidate();
    }

    public Grid gen_random_level() {
        Random rand = new Random();
        rand.setSeed(new Random().nextInt(360));
        float[] hsl = {rand.nextInt(360), 1, 0.6f};
        int color = Color.HSVToColor(hsl);

        int nb_columns, nb_rows;

        Random r = new Random();

        switch (MenuActivity.cur_level) {
            case -1:
                nb_columns = 2 + r.nextInt(3);
                nb_rows = 2 + r.nextInt(3);
                break;

            case -2:
                nb_columns = 3 + r.nextInt(2);
                nb_rows = 3 + r.nextInt(2);
                break;

            case -3:
                nb_columns = 3 + r.nextInt(3);
                nb_rows = 3 + r.nextInt(3);
                break;

            case -4:
                nb_columns = 4 + r.nextInt(2);
                nb_rows = 4 + r.nextInt(2);
                break;

            case -5:
            default:
                nb_columns = 5 + r.nextInt(2);
                nb_rows = 5 + r.nextInt(2);
        }

        ArrayList<Integer> cells = new ArrayList<>();
        for (int i = 0; i < nb_columns * nb_rows; i++) {
            cells.add(0);
        }

        grid = new Grid(nb_columns, nb_rows, 0, cells, color);

        int nb_clicks;

        switch (MenuActivity.cur_level) {
            case -1:
                nb_clicks = new Random().nextInt(3) + 2;
                break;

            case -2:
                nb_clicks = new Random().nextInt(2) + 5;
                break;

            case -3:
                nb_clicks = new Random().nextInt(3) + 6;
                break;

            case -4:
                nb_clicks = new Random().nextInt(7) + 18;
                break;

            case -5:
            default:
                nb_clicks = new Random().nextInt(15) + 26;
        }

        grid.gen_grid();

        while (grid.is_empty()) {
            grid.random_click(nb_clicks);
        }

        grid.orig_grid = grid.cells_to_intarray();

        return grid;
    }

    public void setOnWinLevel(OnWinLevelListener l) {
        onWinLevel = l;
    }
}
