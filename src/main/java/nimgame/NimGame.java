/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package nimgame;

import controllers.HomeController;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.GameModel;
import views.HomeView;

/**
 *
 * @author ASUS
 */
public class NimGame {

    public static void main(String[] args) {

        try {
            HomeView homeView = new HomeView();
            GameModel gameModel = new GameModel();
            HomeController homeController = new HomeController(gameModel, homeView);
            homeController.launch();
        } catch (URISyntaxException ex) {
            Logger.getLogger(NimGame.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
