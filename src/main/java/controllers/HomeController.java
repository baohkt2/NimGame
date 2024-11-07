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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author ASUS
 */
public class HomeController implements HomeInterface, NavigationInterface {

    private GameModel gameModel; // Mô hình trò chơi
    private HomeView homeView; // Giao diện chính
    private GamePlayView gamePlayView; // Giao diện trò chơi
    private GameStateModel gameStateModel; // Mô hình trạng thái trò chơi
    private GamePlayController gamePlayController; // Điều khiển trò chơi
    private PlayerModel player1; // Người chơi 1
    private PlayerModel player2; // Người chơi 2
    private String namePlayer1 = new String("Player 1"); // Tên người chơi 1
    private String namePlayer2 = new String("Player 2"); // Tên người chơi 2
    private String nameMachine = new String("Machine"); // Tên máy

    public HomeController(GameModel gameModel, HomeView homeView) throws URISyntaxException {
        this.gameModel = gameModel;
        this.homeView = homeView;
        this.homeView.setHomeActions(this); // Thiết lập các hành động cho giao diện chính

        ReadSetting(); // Đọc cài đặt
        CheckSaveGame(); // Kiểm tra trò chơi đã lưu
    }

    void NewGame() {
        int numberOfRows = gameStateModel.getNumberOfRows(); // Lấy số hàng
        int[] SticksInRow = gameStateModel.getSticksInRow(); // Lấy số que trong mỗi hàng
        gameStateModel.setIsNormalPlay(gameModel.isNormalPlay()); // Thiết lập chế độ chơi
        gamePlayView = new GamePlayView(numberOfRows, SticksInRow); // Khởi tạo giao diện trò chơi

        gamePlayController = new GamePlayController(gamePlayView, gameStateModel, player1, player2); // Khởi tạo điều khiển trò chơi
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
            this.CheckSaveGame(); // Kiểm tra trò chơi đã lưu
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
        gameStateModel = new GameStateModel(); // Khởi tạo mô hình trạng thái trò chơi
        Path path = Paths.get("src/main/resources/database/SaveGame.txt"); // Đường dẫn đến tệp lưu trò chơi

        // Kiểm tra xem tệp có tồn tại trước khi cố gắng đọc
        if (!Files.exists(path)) {
            //    System.out.println("Tệp lưu trò chơi không tồn tại tại: " + path.toString());
            return; // Thoát nếu tệp không tồn tại
        }

        // Đọc thông tin từ tệp
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(": "); // Tách dòng thành các phần
                if (parts.length < 2) {
                    continue; // Bỏ qua các dòng không có định dạng mong đợi
                }
                String key = parts[0].trim(); // Lấy khóa
                String value = parts[1].trim(); // Lấy giá trị

                switch (key) {
                    case "GameMode":
                        if (value.equals("PvM")) {
                            player1 = new PlayerModel(namePlayer1, false); // Khởi tạo người chơi 1
                            player2 = new PlayerModel(nameMachine, true); // Khởi tạo máy
                        } else {
                            player1 = new PlayerModel(namePlayer1, false); // Khởi tạo người chơi 1
                            player2 = new PlayerModel(namePlayer2, false); // Khởi tạo người chơi 2
                        }
                        break;
                    case "TakeFirst":
                        gameStateModel.setTakeFirst(Boolean.parseBoolean(value)); // Thiết lập ai đi trước
                        break;
                    case "CurrentTurn":
                        player1.setCanTake(value.equals("player1")); // Thiết lập lượt hiện tại
                        player2.setCanTake(!player1.getCanTake());
                        break;
                    case "NumberOfRows":
                        gameStateModel.setNumberOfRows(Integer.parseInt(value)); // Thiết lập số hàng
                        break;
                    case "SticksInRow":
                        gameStateModel.setSticksInRow(parseIntArray(value)); // Thiết lập số que trong hàng
                        break;
                    case "SticksTaken":
                        gameStateModel.setSticksTaken(parseIntArray(value)); // Thiết lập số que đã lấy
                        break;
                    case "HistoryTaken":
                        List<String> historyList = Arrays.stream(value.split(","))
                                .map(String::trim)
                                .collect(Collectors.toList()); // Lưu lịch sử các hành động
                        gameStateModel.setHistoryIndex(historyList.size() - 1); // Thiết lập chỉ số lịch sử
                        gameStateModel.setHistoryTaken(historyList); // Lưu lịch sử
                        break;
                    default:
                        //        System.out.println("Khóa không xác định: " + key); // Xử lý khóa không xác định
                        break;
                }
            }
            //    System.out.println("Trò chơi đã được tải thành công!");
        } catch (IOException e) {
            System.out.println("Lỗi khi đọc trò chơi: " + e.getMessage());
            e.printStackTrace(); // In thông tin lỗi để gỡ lỗi
        }
    }

    // Phương thức trợ giúp để phân tích mảng số nguyên từ chu ```java
    // Phương thức trợ giúp để phân tích mảng số nguyên từ chuỗi
    private int[] parseIntArray(String value) {
        String[] sticksStr = value.replaceAll("[\\[\\]]", "").split(", "); // Xóa dấu ngoặc và tách chuỗi
        return Arrays.stream(sticksStr) // Chuyển đổi thành stream
                .mapToInt(Integer::parseInt) // Chuyển đổi từng phần thành số nguyên
                .toArray(); // Trả về mảng số nguyên
    }

    @Override
    public void SaveSetting(int maxRows, int minRows, int maxSticks, int minSticks, Boolean normalPlay) {
        gameModel = new GameModel(maxRows, minRows, maxSticks, minSticks, normalPlay); // Khởi tạo mô hình trò chơi với cài đặt mới
        Path path = Paths.get("src/main/resources/database/Setting.txt"); // Đường dẫn đến tệp cài đặt

        // Tạo thư mục nếu chưa tồn tại
        try {
            Files.createDirectories(path.getParent());
            // Tạo tệp chỉ khi nó chưa tồn tại
            if (!Files.exists(path)) {
                Files.createFile(path);
                //    System.out.println("Tệp đã được tạo tại: " + path.toString());
            }
        } catch (IOException e) {
            //    System.out.println("Lỗi khi tạo tệp hoặc thư mục: " + e.getMessage());
            e.printStackTrace(); // In thông tin lỗi để gỡ lỗi
            return; // Thoát nếu có lỗi
        }

        // Lưu thông tin vào tệp (ghi đè nếu đã tồn tại)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile(), false))) { // 'false' để ghi đè           
            // Ghi thông tin trạng thái trò chơi
            writer.write("MaxRows: " + gameModel.getMaxRows() + "\n");
            writer.write("MinRows: " + gameModel.getMinRows() + "\n");
            writer.write("MaxSticksInRow: " + gameModel.getMaxSticksInRow() + "\n");
            writer.write("MinSticksInRow: " + gameModel.getMinSticksInRow() + "\n");
            // Ghi quy tắc trò chơi
            writer.write("GameRules: " + gameModel.isNormalPlay() + "\n");

            //   System.out.println("Cài đặt đã được lưu thành công!");
        } catch (IOException e) {
            //    System.out.println("Lỗi khi lưu cài đặt: " + e.getMessage());
            e.printStackTrace(); // In thông tin lỗi để gỡ lỗi
        }
    }

    private void ReadSetting() {
        Path path = Paths.get("src/main/resources/database/Setting.txt"); // Đường dẫn đến tệp cài đặt

        // Kiểm tra xem tệp có tồn tại trước khi cố gắng đọc
        if (!Files.exists(path)) {
        //    System.out.println("Tệp cài đặt không tồn tại tại: " + path.toString());
            SaveSetting(5, 1, 9, 1, Boolean.TRUE); // Tạo cài đặt mặc định nếu tệp không tồn tại
            return; // Thoát nếu tệp không tồn tại
        }

        // Đọc thông tin từ tệp
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(": "); // Tách dòng thành các phần
                if (parts.length < 2) {
                    continue; // Bỏ qua các dòng không có định dạng mong đợi
                }
                String key = parts[0].trim(); // Lấy khóa
                String value = parts[1].trim(); // Lấy giá trị

                switch (key) {
                    case "MaxRows":
                        gameModel.setMaxRows(Integer.parseInt(value)); // Thiết lập số hàng tối đa
                        break;
                    case "MinRows":
                        gameModel.setMinRows(Integer.parseInt(value)); // Thiết lập số hàng tối thiểu
                        break;
                    case "MaxSticksInRow":
                        gameModel.setMaxSticksInRow(Integer.parseInt(value)); // Thiết lập số que tối đa trong hàng
                        break;
                    case "MinSticksInRow":
                        gameModel.setMinSticksInRow(Integer.parseInt(value)); // Thiết lập số que tối thiểu trong hàng
                        break;
                    case "GameRules":
                        gameModel.setIsNormalPlay(Boolean.parseBoolean(value)); // Thiết lập quy tắc trò chơi
                        break;
                    default:
                        System.out.println("Lỗi đọc giá trị: " + key); // Xử lý khóa không xác định
                        break;
                }
            }
        //    System.out.println("Cài đặt đã được tải thành công!");
            homeView.setSetting(gameModel.getMaxRows(), gameModel.getMinRows(), gameModel.getMaxSticksInRow(), gameModel.getMinSticksInRow(), gameModel.isNormalPlay()); // Cập nhật giao diện chính với cài đặt
        } catch (IOException e) {
        //    System.out.println("Lỗi khi đọc cài đặt: " + e.getMessage());
            e.printStackTrace(); // In thông tin lỗi để gỡ lỗi
        }
    }

    private void CheckSaveGame() throws URISyntaxException {
        this.readSaveGame(); // Đọc trò chơi đã lưu
        if (gameStateModel.isEmpty()) {
            homeView.setContinue(false); // Thiết lập tùy chọn tiếp tục nếu không có trò chơi đã lưu
        } else {
            homeView.setContinue(true); // Thiết lập tùy chọn tiếp tục nếu có trò chơi đã lưu
        }
    }
}
