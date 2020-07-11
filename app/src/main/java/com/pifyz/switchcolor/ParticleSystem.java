package com.pifyz.switchcolor;

import android.graphics.Canvas;
import android.graphics.Paint;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Random;

public class ParticleSystem {
    public ArrayList<Particle> particles;

    public ParticleSystem() {
        this.particles = new ArrayList<>();
    }

    // Ajouter des particules
    public void add_particles(int nb, int posx, int posy) {
        for (int i = 0; i < nb; i++) {
            this.particles.add(new Particle(
                posx,
                posy,
                -2 + (new Random().nextInt(5)), // [-2;2] dx
                5 + (new Random().nextInt(6)) // [5;10] dy
            ));
        }
    }

    // Mettre à jour les particules
    public void update() {
        int i = this.particles.size();

        while (i-- > 0) {
            Particle particle = this.particles.get(i);

            particle.update();

            if (particle.life >= particle.max_life) {
                this.particles.remove(i);
            }
        }
    }

    // Dessiner les particules
    public void draw(Canvas can, @NotNull Paint p) {
        p.setColor(0xFFFFFFFF);

        for (Particle particle : this.particles) {
            particle.draw(can, p);
        }
    }
}
