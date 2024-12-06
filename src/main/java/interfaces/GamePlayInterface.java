/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package interfaces;

/**
 *
 * @author ASUS
 */
public interface GamePlayInterface {

    String getNamePlayerCanTake();  // Lấy tên người chơi tới lượt

    void takeSticks(int currentRow, int disabledCount); // Lấy que

    int getNumberOfSticksTakenInRow(int rowIndex);  // Lấy số lượng que trên hàng

    void backHome();    // Về trang chủ

    String[] getHistoryTaken(); // Lấy lịch sử nước đi

    void undoSticksTaken(); // Hoàn tác nước đi

    void giveUp();  // Bỏ cuộc

    void hint();    // Gợi ý

    void playAgain();   // Chơi lại

    void backHomeAfterOverGame();   // Về trang chủ sau khi kết thúc ván 
}
