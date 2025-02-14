/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package interfaces;

/**
 *
 * @author ASUS
 */
public interface HomeInterface {

    void NewGamePvP();  // Tạo ván mới với chế độ người vs người

    void NewGamePvM(Boolean takesFirst);    // Tạo ván mới với chế độ người vs máy

    void Continue();    // Tiếp tục ván

    void SaveSetting(int maxRows, int minRows, int maxSticks, int minSticks, Boolean normalPlay);   // Lưu cài đặt
}
