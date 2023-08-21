package com.pifyz.switchcolor;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class Grid {
    public Stack<Integer> actions;
    public ArrayList<Integer> origGrid;
    private int nbColumns;
    private int nbRows;
    private int nbCells;
    private int nbClicksGold;
    private int nbClicksSilver;
    private int nbClicksBronze;
    private int userClicks;
    private int cellSpacing;
    private int cellSizeSpacing;
    private ArrayList<Cell> cells;
    private int cellSize;
    private int cellColor;

    public Grid(int nbColumns, int nbRows, int nb_clicks, ArrayList<Integer> cells, int color) {
        this.nbColumns = nbColumns;
        this.nbRows = nbRows;
        this.nbCells = this.nbColumns * this.nbRows;

        nbClicksGold = nb_clicks;
        nbClicksSilver = (int) Math.ceil(nb_clicks * 1.5);
        nbClicksBronze = nb_clicks * 2;
        userClicks = 0;

        cellSpacing = 2;
        cellSize = 50;
        cellSizeSpacing = cellSize + cellSpacing;
        cellColor = color;

        actions = new Stack<>();

        origGrid = new ArrayList<>(cells);

        genGrid();
    }

    // Génération des cellules de la grille
    public void genGrid() {
        actions = new Stack<>();
        userClicks = 0;

        cells = new ArrayList<>();

        for (int i : origGrid) {
            boolean active = i != 0;

            cells.add(new Cell(active, cellColor));
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
        cellSize = (height - cellSpacing * (nbRows - 1)) / nbRows;

        int gameWidth = cellSize * nbColumns + (cellSpacing * (nbRows - 1));

        if (gameWidth > width) {
            cellSize = (width - cellSpacing * (nbColumns - 1)) / nbColumns;
        }

        cellSizeSpacing = cellSize + cellSpacing;

        int i = cells.size();
        for (Cell cell : cells) {
            i--;

            int x = (cells.size() - i - 1) % nbColumns * cellSizeSpacing;
            int y = Math.round((cells.size() - i - 1) / nbColumns) * cellSizeSpacing;

            cell.move(x, y);
            cell.resize(cellSize);
        }
    }

    // Appuie d'une cellule
    public boolean pushed(int x, int y) {
        for (Cell cell : cells) {
            if (x > cell.x &&
                    x <= cell.x + cellSize &&
                    y > cell.y &&
                    y <= cell.y + cellSize
            ) {
                cellClick(cells.indexOf(cell));

                return true;
            }
        }

        return false;
    }

    // Action au clic d'une cellule
    public void cellClick(int i) {
        actions.add(i);
        userClicks++;

        ArrayList<Cell> neighbors = new ArrayList<>();

        if (i - nbColumns >= 0 && i - nbColumns < cells.size()) {
            neighbors.add(cells.get(i - nbColumns));
        }

        if (i >= 0 && i < cells.size()) {
            neighbors.add(cells.get(i));
        }

        if (i + nbColumns >= 0 && i + nbColumns < cells.size()) {
            neighbors.add(cells.get(i + nbColumns));
        }

        if (i % nbColumns != 0 && i - 1 >= 0 && i - 1 < cells.size()) {
            neighbors.add(cells.get(i - 1));
        }

        if (i % nbColumns != nbColumns - 1 && i + 1 >= 0 && i + 1 < cells.size()) {
            neighbors.add(cells.get(i + 1));
        }

        for (Cell neighbor : neighbors) {
            neighbor.active = !neighbor.active;
            neighbor.transition = 6;
        }
    }

    // Annuler la dernière action
    public void cancelLastAction() {
        int lastAction = actions.pop();
        cellClick(lastAction);
        userClicks -= 2;
        actions.pop();
    }

    // Clique sur une cellule au hasard
    public void randomClick(int nbClicks) {
        for (int i = 0; i < nbClicks; i++) {
            cellClick(new Random().nextInt(nbCells - 1));
        }

        userClicks -= nbClicks;
    }

    // La grille est-elle vide ?
    public boolean isEmpty() {
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
    public ArrayList<Integer> cellsToIntarray() {
        ArrayList<Integer> cells = new ArrayList<>();

        for (Cell cell : this.cells) {
            cells.add(cell.active ? 1 : 0);
        }

        return cells;
    }

    // Récupérer le code de la médaille
    public Medal getMedal() {
        if (userClicks <= nbClicksGold) {
            return new Medal(3);
        } else if (userClicks <= nbClicksSilver) {
            return new Medal(2);
        } else if (userClicks <= nbClicksBronze) {
            return new Medal(1);
        } else {
            return new Medal(0);
        }
    }

    // Récupérer la largeur de la grille
    public int getWidth() {
        int width = 0;

        for (int i = 0; i < nbColumns; i++) {
            width += cellSizeSpacing;
        }

        return width;
    }

    // Récupérer la hauteur de la grille
    public int getHeight() {
        int height = 0;

        for (int i = 0; i < nbRows; i++) {
            height += cellSizeSpacing;
        }

        return height;
    }
}
