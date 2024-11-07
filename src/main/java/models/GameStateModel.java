// encoding=UTF-8
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ASUS
 */
public class GameStateModel {

    private int numberOfRows;
    private int[] sticksInRow;
    private int[] sticksTaken;
    private List<String> historyTaken;
    private int historyIndex;
    private Boolean isNormalPlay;
    private Boolean takeFirst;

    public GameStateModel() {
    }

    public GameStateModel(int numberOfRows, int[] sticksInRow) {
        this.numberOfRows = numberOfRows;
        this.sticksInRow = sticksInRow;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public int[] getSticksInRow() {
        return sticksInRow;
    }

    public int[] getSticksTaken() {
        if (sticksTaken == null) {
            sticksTaken = new int[this.numberOfRows];
        }

        return sticksTaken;
    }

    public void setNumberOfRows(int numberOfRows) {
        this.numberOfRows = numberOfRows;
    }

    public void setSticksInRow(int[] sticksInRow) {
        this.sticksInRow = sticksInRow;
    }

    public void setSticksTaken(int[] sticksTaken) {
        this.sticksTaken = sticksTaken;
    }

    public List<String> getHistoryTaken() {
        if (historyTaken == null) {
            historyTaken = new ArrayList<>();
        }
        return historyTaken;
    }

    public void setHistoryIndex(int historyIndex) {
        this.historyIndex = historyIndex;
    }

    public void setHistoryTaken(List<String> historyTaken) {
        this.historyTaken = historyTaken;
    }

    public void setIsNormalPlay(Boolean isNormalPlay) {
        this.isNormalPlay = isNormalPlay;
    }

    public Boolean isNormalPlay() {
        return isNormalPlay;
    }

    public Boolean getTakeFirst() {
        return takeFirst;
    }

    public void setTakeFirst(Boolean takeFirst) {
        this.takeFirst = takeFirst;
    }

    public int getHistoryIndex() {
        return historyIndex;
    }

    public void incrementHistoryIndex() {
        historyIndex++; // Increment the index
    }

    public void decrementHistoryIndex() {
        historyIndex--; // Decrement the index
    }

    public void deleteStateModel() {
        this.historyIndex = 0;
        this.historyTaken = null;
        this.numberOfRows = 0;
        this.sticksInRow = null;
        this.sticksTaken = null;
    }

    public void restore() {
        this.historyIndex = 0;
        this.historyTaken = new ArrayList<>();
        this.sticksTaken = new int[this.numberOfRows];
    }

    public boolean isEmpty() {
        if (sticksTaken == null) {
            return true;
        }
        return false;
    }

}
