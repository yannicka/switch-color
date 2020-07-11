package com.pifyz.switchcolor;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Random;

public class Particle {
    public int x;
    public int y;
    public int dx;
    public int dy;
    public int life;
    public int max_life;
    public int size;

    public Particle(int x, int y, int dx, int dy) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;

        life = 0;
        max_life = 10 + (new Random().nextInt(11));
        size = max_life;
    }

    // Mise à jour d'une particule
    public void update() {
        x += dx;
        y += dy;

        life++;
        size--;
    }

    // Affichage d'une particule
    public void draw(Canvas can, Paint p) {
        can.drawCircle(x, y, size / 2, p);
    }
}
