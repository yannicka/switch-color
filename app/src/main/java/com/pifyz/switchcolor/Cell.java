package com.pifyz.switchcolor;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public class Cell {
    public int x, y;
    public int size;
    public int color;
    public int transition;
    public boolean active;
    public String colorStr;

    // Création de la cellule avec son état initial
    public Cell(boolean active, int color) {
        this.active = active;
        this.color = color;

        colorStr = Integer.toHexString(color);

        transition = 0;
    }

    // Affichage de la cellule à sa position
    public void draw(Canvas can, Paint p) {
        if (transition > 0) {
            transition--;
        }

        Path effect = new Path();
        effect.moveTo(x, y);
        effect.lineTo(x + size, y);
        effect.lineTo(x + size, y + size);
        effect.lineTo((float) (x + size * 0.8), (float) (y + size * 0.2));
        effect.close();

        if (active) {
            p.setColor(color);
        } else {
            p.setColor(0xFFBBBBBB);
        }

        if (transition > 0) {
            int R1 = 187, G1 = 187, B1 = 187, R2 = 187, G2 = 187, B2 = 187;
            int RI, BI, GI;

            if (active) {
                R1 = Integer.parseInt(colorStr.substring(2, 4), 16);
                G1 = Integer.parseInt(colorStr.substring(4, 6), 16);
                B1 = Integer.parseInt(colorStr.substring(6, 8), 16);
            } else {
                R2 = Integer.parseInt(colorStr.substring(2, 4), 16);
                G2 = Integer.parseInt(colorStr.substring(4, 6), 16);
                B2 = Integer.parseInt(colorStr.substring(6, 8), 16);
            }

            RI = R1 + (R2 - R1) * transition / 6;
            BI = B1 + (B2 - B1) * transition / 6;
            GI = G1 + (G2 - G1) * transition / 6;

            p.setARGB(255, RI, GI, BI);
        }

        can.drawRect(x, y, x + size, y + size, p);

        p.setARGB(13, 0, 0, 0);
        can.drawPath(effect, p);
    }

    // Déplacement de la cellule sur les axes x et y
    public void move(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Redimensionner la cellule
    public void resize(int size) {
        this.size = size;
    }
}
