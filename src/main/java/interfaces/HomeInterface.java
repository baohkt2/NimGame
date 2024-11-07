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

    void NewGamePvP();

    void NewGamePvM(Boolean takesFirst);

    void Continue();

    void SaveSetting(int maxRows, int minRows, int maxSticks, int minSticks, Boolean normalPlay);
}
