/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package interfaces;

import java.util.List;

/**
 *
 * @author ASUS
 */
public interface GamePlayInterface {
    String getNamePlayerCanTake();
    void takeSticks(int currentRow, int disabledCount);
    int getNumberOfSticksTakenInRow(int rowIndex);
    void backHome();
    String[] getHistoryTaken();
    void undoSticksTaken();
    void giveUp();
    void hint();
    void playAgain();
    void backHomeAfterOverGame();
}
