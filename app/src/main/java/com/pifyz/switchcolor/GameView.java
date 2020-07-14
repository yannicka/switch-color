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

    private Paint p;
    private ParticleSystem particles;
    private Medal medal;
    private OnWinLevelListener onWinLevel;
    private int tx, ty;

    public GameView(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);

        p = new Paint();

        particles = new ParticleSystem();
        medal = null;

        if (MenuActivity.curLevel >= 0) {
            grid = MenuActivity.grids.get(MenuActivity.curLevel);
        } else {
            grid = genRandomLevel();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        performClick();

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) ev.getX() - tx;
            int y = (int) ev.getY() - ty;

            if (grid.pushed(x, y)) {
                particles.addParticles(10, x, y);
            }

            if (grid.isEmpty()) {
                if (MenuActivity.curLevel >= 0) {
                    medal = grid.getMedal();
                    grid.genGrid();

                    MenuActivity.curLevel++;

                    SharedPreferences.Editor editor = MenuActivity.prefs.edit();

                    int curLevelPref = MenuActivity.prefs.getInt("cur_level", 0);

                    if (curLevelPref < MenuActivity.curLevel) {
                        editor.putInt("cur_level", MenuActivity.curLevel);
                    }

                    String medals = MenuActivity.prefs.getString("medals", "");
                    String levels = MenuActivity.prefs.getString("levels", "");

                    int prevLevel = MenuActivity.curLevel - 1;

                    if (Character.getNumericValue(medals.charAt(prevLevel)) < medal.level) {
                        medals = medals.substring(0, prevLevel) + medal.level + medals.substring(MenuActivity.curLevel);
                    }

                    editor.putString("medals", medals);
                    editor.apply();

                    if (onWinLevel != null) {
                        boolean noFinal = onWinLevel.onWinLevel(this, MenuActivity.curLevel);

                        if (!noFinal) {
                            return true;
                        }
                    }

                    levels = levels.substring(0, MenuActivity.curLevel) + '1' + levels.substring(MenuActivity.curLevel + 1);

                    editor.putString("levels", levels);
                    editor.commit();

                    grid = MenuActivity.grids.get(MenuActivity.curLevel);
                    onSizeChanged(getWidth(), getHeight(), getWidth(), getHeight());
                } else {
                    grid = genRandomLevel();

                    onSizeChanged(getWidth(), getHeight(), getWidth(), getHeight());

                    if (onWinLevel != null) {
                        onWinLevel.onWinLevel(this, MenuActivity.curLevel);
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

        tx = getWidth() / 2 - grid.getWidth() / 2;
        ty = getHeight() / 2 - grid.getHeight() / 2;
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

    public Grid genRandomLevel() {
        Random rand = new Random();
        rand.setSeed(new Random().nextInt(360));
        float[] hsl = {rand.nextInt(360), 1, 0.6f};
        int color = Color.HSVToColor(hsl);

        int nbColumns, nbRows;

        Random r = new Random();

        switch (MenuActivity.curLevel) {
            case -1:
                nbColumns = 2 + r.nextInt(3);
                nbRows = 2 + r.nextInt(3);
                break;

            case -2:
                nbColumns = 3 + r.nextInt(2);
                nbRows = 3 + r.nextInt(2);
                break;

            case -3:
                nbColumns = 3 + r.nextInt(3);
                nbRows = 3 + r.nextInt(3);
                break;

            case -4:
                nbColumns = 4 + r.nextInt(2);
                nbRows = 4 + r.nextInt(2);
                break;

            case -5:
            default:
                nbColumns = 5 + r.nextInt(2);
                nbRows = 5 + r.nextInt(2);
        }

        ArrayList<Integer> cells = new ArrayList<>();
        for (int i = 0; i < nbColumns * nbRows; i++) {
            cells.add(0);
        }

        grid = new Grid(nbColumns, nbRows, 0, cells, color);

        int nbClicks;

        switch (MenuActivity.curLevel) {
            case -1:
                nbClicks = new Random().nextInt(3) + 2;
                break;

            case -2:
                nbClicks = new Random().nextInt(2) + 5;
                break;

            case -3:
                nbClicks = new Random().nextInt(3) + 6;
                break;

            case -4:
                nbClicks = new Random().nextInt(7) + 18;
                break;

            case -5:
            default:
                nbClicks = new Random().nextInt(15) + 26;
        }

        grid.genGrid();

        while (grid.isEmpty()) {
            grid.randomClick(nbClicks);
        }

        grid.origGrid = grid.cellsToIntarray();

        return grid;
    }

    public void setOnWinLevel(OnWinLevelListener l) {
        onWinLevel = l;
    }

    interface OnWinLevelListener {
        boolean onWinLevel(View v, int level);
    }
}
