package com.pifyz.switchcolor;

import android.graphics.Canvas;
import android.graphics.Paint;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Random;

public class ParticleSystem {
    private ArrayList<Particle> particles;

    public ParticleSystem() {
        particles = new ArrayList<>();
    }

    // Ajouter des particules
    public void addParticles(int nb, int posx, int posy) {
        for (int i = 0; i < nb; i++) {
            particles.add(new Particle(
                posx,
                posy,
                -2 + (new Random().nextInt(5)), // [-2;2] dx
                5 + (new Random().nextInt(6)) // [5;10] dy
            ));
        }
    }

    // Mettre à jour les particules
    public void update() {
        int i = particles.size();

        while (i-- > 0) {
            Particle particle = particles.get(i);

            particle.update();

            if (particle.life >= particle.maxLife) {
                particles.remove(i);
            }
        }
    }

    // Dessiner les particules
    public void draw(Canvas can, @NotNull Paint p) {
        p.setColor(0xFFFFFFFF);

        for (Particle particle : particles) {
            particle.draw(can, p);
        }
    }
}
