package com.pifyz.switchcolor;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Random;

public class Particle {
    public int life;
    public int maxLife;
    private int x;
    private int y;
    private int dx;
    private int dy;
    private int size;

    public Particle(int x, int y, int dx, int dy) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;

        life = 0;
        maxLife = 35 + (new Random().nextInt(35));
        size = maxLife;
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
