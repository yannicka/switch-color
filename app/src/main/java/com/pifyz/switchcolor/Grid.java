package com.pifyz.switchcolor;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class Grid {
    public int nb_columns;
    public int nb_rows;
    public int nb_cells;
    public int nb_clicks_gold;
    public int nb_clicks_silver;
    public int nb_clicks_bronze;
    public int user_clicks;
    public int cell_spacing;
    public int cell_size_spacing;
    public Stack<Integer> actions;
    public ArrayList<Cell> cells;
    public ArrayList<Integer> orig_grid;
    public int cell_size;
    public int cell_color;

    public Grid(int nb_columns, int nb_rows, int nb_clicks, ArrayList<Integer> cells, int color) {
        this.nb_columns = nb_columns;
        this.nb_rows = nb_rows;
        this.nb_cells = this.nb_columns * this.nb_rows;

        nb_clicks_gold = nb_clicks;
        nb_clicks_silver = (int) Math.ceil(nb_clicks * 1.5);
        nb_clicks_bronze = nb_clicks * 2;
        user_clicks = 0;

        cell_spacing = 2;
        cell_size = 50;
        cell_size_spacing = cell_size + cell_spacing;
        cell_color = color;

        actions = new Stack<>();

        orig_grid = new ArrayList<>(cells);

        gen_grid();
    }

    // Génération des cellules de la grille
    public void gen_grid() {
        actions = new Stack<>();
        user_clicks = 0;

        cells = new ArrayList<>();

        for (int i : orig_grid) {
            boolean active = i != 0;

            cells.add(new Cell(active, cell_color));
        }
    }

    // Dessiner la grille
    public void draw(Canvas can, Paint p) {
        for (Cell cell : cells) {
            cell.draw(can, p);
        }
    }

    // Redimensionnement des cellules de la grille
    public void resize(int width, int height) {
        cell_size = (height - cell_spacing * (nb_rows - 1)) / nb_rows;

        int game_width = cell_size * nb_columns + (cell_spacing * (nb_rows - 1));

        if (game_width > width) {
            cell_size = (width - cell_spacing * (nb_columns - 1)) / nb_columns;
        }

        cell_size_spacing = cell_size + cell_spacing;

        int i = cells.size();
        for (Cell cell : cells) {
            i--;

            int x = (cells.size() - i - 1) % nb_columns * cell_size_spacing;
            int y = Math.round((cells.size() - i - 1) / nb_columns) * cell_size_spacing;

            cell.move(x, y);
            cell.resize(cell_size);
        }
    }

    // Appuie d'une cellule
    public boolean pushed(int x, int y) {
        for (Cell cell : cells) {
            if (x > cell.x &&
                    x <= cell.x + cell_size &&
                    y > cell.y &&
                    y <= cell.y + cell_size
            ) {
                cell_click(cells.indexOf(cell));

                return true;
            }
        }

        return false;
    }

    // Action au clic d'une cellule
    public void cell_click(int i) {
        actions.add(i);
        user_clicks++;

        ArrayList<Cell> neighbors = new ArrayList<>();

        if (i - nb_columns >= 0 && i - nb_columns < cells.size()) {
            neighbors.add(cells.get(i - nb_columns));
        }

        if (i >= 0 && i < cells.size()) {
            neighbors.add(cells.get(i));
        }

        if (i + nb_columns >= 0 && i + nb_columns < cells.size()) {
            neighbors.add(cells.get(i + nb_columns));
        }

        if (i % nb_columns != 0 && i - 1 >= 0 && i - 1 < cells.size()) {
            neighbors.add(cells.get(i - 1));
        }

        if (i % nb_columns != nb_columns - 1 && i + 1 >= 0 && i + 1 < cells.size()) {
            neighbors.add(cells.get(i + 1));
        }

        for (Cell neighbor : neighbors) {
            neighbor.active = !neighbor.active;
            neighbor.transition = 6;
        }
    }

    // Annuler la dernière action
    public void cancel_last_action() {
        int last_action = actions.pop();
        cell_click(last_action);
        user_clicks -= 2;
        actions.pop();
    }

    // Clic sur une cellule au hasard
    public void random_click(int nb_clicks) {
        for (int i = 0; i < nb_clicks; i++) {
            cell_click(new Random().nextInt(nb_cells - 1));
        }

        user_clicks -= nb_clicks;
    }

    // La grille est-elle vide ?
    public boolean is_empty() {
        boolean finished = true;

        for (Cell cell : cells) {
            if (cell.active) {
                finished = false;
                break;
            }
        }

        return finished;
    }

    // Récupérer la liste des cellules sous la forme d'un tableau d'entiers
    public ArrayList<Integer> cells_to_intarray() {
        ArrayList<Integer> cells = new ArrayList<>();

        for (Cell cell : this.cells)
            cells.add(cell.active ? 1 : 0);

        return cells;
    }

    // Récupérer le code de la médaille
    public Medal get_medal() {
        if (this.user_clicks <= this.nb_clicks_gold)
            return new Medal(3);
        else if (this.user_clicks <= this.nb_clicks_silver)
            return new Medal(2);
        else if (this.user_clicks <= this.nb_clicks_bronze)
            return new Medal(1);
        else
            return new Medal(0);
    }

    // Récupérer la largeur de la grille
    public int get_width() {
        int width = 0;

        for (int i = 0; i < nb_columns; i++)
            width += cell_size_spacing;

        return width;
    }

    // Récupérer la hauteur de la grille
    public int get_height() {
        int height = 0;

        for (int i = 0; i < nb_rows; i++)
            height += cell_size_spacing;

        return height;
    }
}
