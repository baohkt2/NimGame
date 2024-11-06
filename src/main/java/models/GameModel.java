// encoding=UTF-8
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package models;

/**
 *
 * @author ASUS
 */
public class GameModel {
    
    private int maxRows;
    private int minRows;
    private int maxSticksInRow;
    private int minSticksInRow;
    private boolean isNormalPlay;
    
    public GameModel() {
    }

    public GameModel(int maxRows, int minRows, int maxSticksInRow, int minSticksInRow, boolean isNormalPlay) {
        this.maxRows = maxRows;
        this.minRows = minRows;
        this.maxSticksInRow = maxSticksInRow;
        this.minSticksInRow = minSticksInRow;
        this.isNormalPlay = isNormalPlay;
    }

    public int getMaxRows() {
        return maxRows;
    }

    public int getMinRows() {
        return minRows;
    }

    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }

    public void setMinRows(int minRows) {
        this.minRows = minRows;
    }
    

    public int getMaxSticksInRow() {
        return maxSticksInRow;
    }

    public int getMinSticksInRow() {
        return minSticksInRow;
    }  

    public void setMaxSticksInRow(int maxSticksInRow) {
        this.maxSticksInRow = maxSticksInRow;
    }

    public void setMinSticksInRow(int minSticksInRow) {
        this.minSticksInRow = minSticksInRow;
    }

    public void setIsNormalPlay(boolean isNormalPlay) {
        this.isNormalPlay = isNormalPlay;
    }

    public boolean isNormalPlay() {
        return isNormalPlay;
    }
    
}
