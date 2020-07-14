package com.pifyz.switchcolor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Medal {
    public int life;
    public int level;
    private Bitmap img;

    public Medal(int medal) {
        this.level = medal;

        life = 200;

        switch (level) {
            case 1:
                img = BitmapFactory.decodeResource(MenuActivity.resources, R.drawable.stars_1);
                break;

            case 2:
                img = BitmapFactory.decodeResource(MenuActivity.resources, R.drawable.stars_2);
                break;

            case 3:
                img = BitmapFactory.decodeResource(MenuActivity.resources, R.drawable.stars_3);
                break;
        }
    }

    // Mettre à jour la médaille
    public void update() {
        life -= 3;

        if (life < 0) {
            life = 0;
        }
    }

    // Dessiner la médaille
    public void draw(Canvas can, Paint p) {
        can.save();

        int goLevel = MenuActivity.curLevel;

        if (level >= 1 && level <= 3 && goLevel >= 0) {
            if (life < 100) {
                p.setAlpha(life * 255 / 140);
            }

            float tx = can.getWidth() / 2 - img.getWidth() / 2 - 10;

            can.translate(tx, 8);
            can.drawBitmap(img, 0, 0, p);
        }

        can.restore();
    }
}
