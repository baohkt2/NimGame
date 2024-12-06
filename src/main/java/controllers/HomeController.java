// encoding=UTF-8
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

import interfaces.NavigationInterface;
import java.util.Random;
import models.GameModel;
import models.GameStateModel;
import models.PlayerModel;
import views.GamePlayView;
import views.HomeView;
import interfaces.HomeInterface;

import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.SaveModel;

/**
 *
 * @author ASUS
 */
public class HomeController implements HomeInterface, NavigationInterface {

    private SaveModel saveModel;
    private GameModel gameModel; // Mô hình trò chơi
    private HomeView homeView; // Giao diện chính
    private GamePlayView gamePlayView; // Giao diện trò chơi
    private GameStateModel gameStateModel; // Mô hình trạng thái trò chơi
    private GamePlayController gamePlayController; // Điều khiển trò chơi
    private PlayerModel player1; // Người chơi 1
    private PlayerModel player2; // Người chơi 2
    private String namePlayer1;
    private String namePlayer2;
    private String nameMachine;

    public HomeController(GameModel gameModel, HomeView homeView) throws URISyntaxException {
        this.gameModel = gameModel;
        this.homeView = homeView;
        this.homeView.setHomeActions(this); // Thiết lập các hành động cho giao diện chính
        saveModel = new SaveModel();
        this.namePlayer1 = saveModel.getNamePlayer1();
        this.namePlayer2 = saveModel.getNamePlayer2();
        this.nameMachine = saveModel.getNameMachine();
        ReadSetting(); // Đọc cài đặt
        CheckSaveModel(); // Kiểm tra trò chơi đã lưu
    }

    private void NewGame() {
        int numberOfRows = gameStateModel.getNumberOfRows(); // Lấy số hàng
        int[] SticksInRow = gameStateModel.getSticksInRow(); // Lấy số que trong mỗi hàng
        gameStateModel.setIsNormalPlay(gameModel.isNormalPlay()); // Thiết lập chế độ chơi
        gamePlayView = new GamePlayView(numberOfRows, SticksInRow); // Khởi tạo giao diện trò chơi

        gamePlayController = new GamePlayController(gamePlayView, gameStateModel, player1, player2); // Khởi tạo điều khiển trò chơi
        gamePlayController.setSaveModel(saveModel);
        gamePlayController.setNavigationInterface(this); // Thiết lập giao diện điều hướng
        gamePlayController.StartGame(); // Bắt đầu trò chơi
        gamePlayController.setPriorities(); // Thiết lập ưu tiên
        hideHome(); // Ẩn giao diện chính
    }

    @Override
    public void NewGamePvP() {
        randomSticksInRow(); // Tạo số que ngẫu nhiên trong hàng
        gameStateModel.setTakeFirst(true); // Thiết lập người chơi 1 đi trước
        player1 = new PlayerModel(namePlayer1, false, gameStateModel.getTakeFirst()); // Khởi tạo người chơi 1
        player2 = new PlayerModel(namePlayer2, false, !gameStateModel.getTakeFirst()); // Khởi tạo người chơi 2
        NewGame(); // Bắt đầu trò chơi mới
    }

    @Override
    public void NewGamePvM(Boolean takesFirst) {
        randomSticksInRow(); // Tạo số que ngẫu nhiên trong hàng
        gameStateModel.setTakeFirst(takesFirst); // Thiết lập người đi trước
        player1 = new PlayerModel(namePlayer1, false, takesFirst); // Khởi tạo người chơi 1
        player2 = new PlayerModel(nameMachine, true, !takesFirst); // Khởi tạo máy
        NewGame(); // Bắt đầu trò chơi mới
    }

    @Override
    public void Continue() {
        try {
            readSaveGame(); // Đọc trò chơi đã lưu  
        } catch (URISyntaxException ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }
        NewGame(); // Bắt đầu trò chơi mới
    }

    public void launch() {
        showHome(); // Hiển thị giao diện chính
    }

    @Override
    public void showHome() {
        homeView.setVisible(true); // Hiển thị giao diện chính
        try {
            this.CheckSaveModel(); // Kiểm tra trò chơi đã lưu
        } catch (URISyntaxException ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void hideHome() {
        homeView.setVisible(false); // Ẩn giao diện chính
    }

    @Override
    public void showGamePlay() {
        gamePlayView.setVisible(true); // Hiển thị giao diện trò chơi
    }

    @Override
    public void hideGamePlay() {
        gamePlayView.setVisible(false); // Ẩn giao diện trò chơi
    }

    private void randomSticksInRow() {
        Random random = new Random(); // Khởi tạo đối tượng Random

        int maxRows = gameModel.getMaxRows(); // Lấy số hàng tối đa
        int minRows = gameModel.getMinRows(); // Lấy số hàng tối thiểu
        int numberOfRows = random.nextInt(maxRows - minRows + 1) + minRows; // Tạo số hàng ngẫu nhiên
        int[] sticksInRow = new int[numberOfRows]; // Mảng lưu số que trong mỗi hàng
        int MaxSticksInRow = gameModel.getMaxSticksInRow(); // Lấy số que tối đa trong hàng
        int MinSticksInRow = gameModel.getMinSticksInRow(); // Lấy số que tối thiểu trong hàng

        for (int i = 0; i < numberOfRows; i++) {
            sticksInRow[i] = random.nextInt(MaxSticksInRow - MinSticksInRow + 1) + MinSticksInRow; // Tạo số que ngẫu nhiên cho mỗi hàng
        }

        gameStateModel = new GameStateModel(numberOfRows, sticksInRow); // Khởi tạo mô hình trạng thái trò chơi
    }

    private void readSaveGame() throws URISyntaxException {
        saveModel.readSaveGame();
        this.player1 = saveModel.getPlayer1();
        this.player2 = saveModel.getPlayer2();
        this.gameStateModel = saveModel.getGameStateModel();
    }

    @Override
    public void SaveSetting(int maxRows, int minRows, int maxSticks, int minSticks, Boolean normalPlay) {
        gameModel = new GameModel(maxRows, minRows, maxSticks, minSticks, normalPlay); // Khởi tạo mô hình trò chơi với cài đặt mới
        saveModel.SaveSetting(gameModel);
    }

    private void ReadSetting() throws URISyntaxException {
        saveModel.ReadSetting();
        this.gameModel = saveModel.getGameModel();
        homeView.setSetting(gameModel.getMaxRows(), gameModel.getMinRows(), gameModel.getMaxSticksInRow(), gameModel.getMinSticksInRow(), gameModel.isNormalPlay()); // Cập nhật giao diện chính với cài đặt
    }

    private void CheckSaveModel() throws URISyntaxException {
        this.readSaveGame(); // Đọc trò chơi đã lưu
        if (gameStateModel.isEmpty()) {
            homeView.setContinue(false); // Ẩn nút tiếp tục nếu không có trò chơi đã lưu
        } else {
            homeView.setContinue(true); //  Hiển thị nút tiếp tục nếu có trò chơi đã lưu
        }
    }
}
