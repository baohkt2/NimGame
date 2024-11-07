// encoding=UTF-8
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

import models.PlayerModel;
import models.GameStateModel;
import views.GamePlayView;
import models.GameModel;
import interfaces.NavigationInterface;
import interfaces.GamePlayInterface;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author ASUS
 */
public class GamePlayController implements GamePlayInterface {

    private PlayerModel player1; // Người chơi 1
    private PlayerModel player2; // Người chơi 2
    private GameStateModel gameStateModel; // Mô hình trạng thái trò chơi
    private GamePlayView gamePlayView; // Giao diện trò chơi
    private NavigationInterface navigationInterface; // Giao diện điều hướng
    private Machine machine; // Điều khiển AI
    private Boolean takesFirst; // Biến kiểm tra ai đi trước

    public GamePlayController(GamePlayView gamePlayView, GameStateModel gameStateModel, PlayerModel player1, PlayerModel player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.gameStateModel = gameStateModel;
        this.gamePlayView = gamePlayView;
        this.machine = new Machine(gameStateModel.getNumberOfRows(), gameStateModel.getSticksInRow());
        this.takesFirst = gameStateModel.getTakeFirst();
    }

    public void setNavigationInterface(NavigationInterface navigationInterface) {
        this.navigationInterface = navigationInterface; // Thiết lập giao diện điều hướng
    }

    private Boolean overGame() {
        // Kiểm tra xem trò chơi đã kết thúc chưa
        if (Arrays.equals(gameStateModel.getSticksInRow(), gameStateModel.getSticksTaken())) {
            String winnerName;

            // Xác định người thắng dựa trên khả năng lấy và chế độ trò chơi
            if (player1.getCanTake()) {
                winnerName = gameStateModel.isNormalPlay() ? player1.getName() : player2.getName();
            } else {
                winnerName = gameStateModel.isNormalPlay() ? player2.getName() : player1.getName();
            }

            // Thông báo giao diện trò chơi về trạng thái kết thúc
            gamePlayView.OverGame(winnerName);

            // Thử xóa trò chơi đã lưu
            deleteSaveGame();
            return true;
        }
        return false; // Trò chơi chưa kết thúc
    }

    public void setPriorities() {
        if (!takesFirst) {
            // Nếu người chơi 2 là máy và có thể lấy
            if (player2.getCanTake() && player2.getIsMachine()) {
                gamePlayView.setViewForAI(true); // Bật giao diện điều khiển của máy
                new Thread(() -> {
                    try {
                        Thread.sleep(1500); // Tạm dừng 1.5 giây
                        gamePlayView.doTurn(); // Thực hiện lượt của máy
                    } catch (InterruptedException e) {
                        e.printStackTrace(); // Xử lý ngoại lệ
                    }
                }).start();
            }
        } else {
            gamePlayView.setViewForAI(false); // False khi là người hoặc máy nhưng chưa tới lượt
        }
    }

    public void StartGame() {
        navigationInterface.showGamePlay(); // Hiển thị giao diện trò chơi
        gamePlayView.setGamePlayInterface(this); // Thiết lập giao diện trò chơi
        machine = new Machine(gameStateModel.getNumberOfRows(), gameStateModel.getSticksInRow()); // Khởi tạo AI
    }

    @Override
    public void takeSticks(int currentRow, int disabledCount) {
        int[] sticksTaken = gameStateModel.getSticksTaken();
        // Nếu người chơi 2 là máy, tính nước đi tiếp theo
        if (player2.getCanTake() && player2.getIsMachine()) {
            int[] nextMove = machine.calculateNextMove(sticksTaken, gameStateModel.isNormalPlay());
            currentRow = nextMove[0] - 1; // Chuyển sang chỉ số hàng 0
            disabledCount = nextMove[1]; // Số que lấy
        }
        if (currentRow != -1) {
            // Cập nhật số que đã lấy
            sticksTaken[currentRow] += disabledCount;
            gameStateModel.setSticksTaken(sticksTaken);
            if (!overGame()) { // Kiểm tra xem trò chơi đã kết thúc chưa
                swapTurns(true); // Đổi lượt
                updateHistoryTaken(currentRow, disabledCount); // Cập nhật lịch sử
                gamePlayView.updateHistoryTaken(); // Cập nhật giao diện lịch sử
                gamePlayView.refresh();
            }
        }
    }

    @Override
    public int getNumberOfSticksTakenInRow(int rowIndex) {
        int[] sticksTaken = gameStateModel.getSticksTaken();
        if (sticksTaken.length > 0) {
            return sticksTaken[rowIndex]; // Trả về số que đã lấy trong hàng chỉ định
        }
        return 0;
    }

    @Override
    public void backHome() {
        saveGame(); // Lưu trò chơi
        this.navigationInterface.hideGamePlay(); // Ẩn giao diện trò chơi
        this.navigationInterface.showHome(); // Hiển thị giao diện chính
    }

    @Override
    public void backHomeAfterOverGame() {
        deleteSaveGame(); // Xóa trò chơi đã lưu
        gameStateModel.deleteStateModel(); // Xóa mô hình trạng thái trò chơi
        this.navigationInterface.hideGamePlay(); // Ẩn giao diện trò chơi
        this.navigationInterface.showHome(); // Hiển thị giao diện chính
    }

    private void saveGame() {
        Path path = Paths.get("src/main/resources/database/SaveGame.txt"); // Đường dẫn đến tệp lưu trò chơi

        // Tạo thư mục nếu chưa tồn tại
        try {
            Files.createDirectories(path.getParent());
            // Tạo tệp chỉ khi nó chưa tồn tại
            if (!Files.exists(path)) {
                Files.createFile(path);
                System.out.println("Tệp đã được tạo tại: " + path.toString());
            }
        } catch (IOException e) {
            System.out.println("Lỗi khi tạo tệp hoặc thư mục: " + e.getMessage());
            e.printStackTrace(); // In thông tin lỗi để gỡ lỗi
            return; // Thoát nếu có lỗi
        }

        // Lưu thông tin vào tệp (ghi đè nếu đã tồn tại)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile(), false))) { // 'false' để ghi đè
            // Ghi chế độ trò chơi
            writer.write("GameMode: " + (player2.getIsMachine() ? "PvM" : "PvP") + "\n");

            writer.write("TakeFirst: " + this.takesFirst + "\n"); // Ghi thông tin ai đi trước
            // Lượt hiện tại
            writer.write("CurrentTurn: " + (player1.getCanTake() ? "player1" : "player2") + "\n");

            // Ghi thông tin trạng thái trò chơi
            writer.write("NumberOfRows: " + gameStateModel.getNumberOfRows() + "\n");
            writer.write("SticksInRow: " + java.util.Arrays.toString(gameStateModel.getSticksInRow()).replaceAll("[\\[\\]]", "") + "\n");
            writer.write("SticksTaken: " + java.util.Arrays.toString(gameStateModel.getSticksTaken()).replaceAll("[\\[\\]]", "") + "\n");

            // Ghi lịch sử hành động
            writer.write("HistoryTaken: " + String.join(", ", gameStateModel.getHistoryTaken()) + "\n");

            System.out.println("Trò chơi đã được lưu thành công!");
        } catch (IOException e) {
            System.out.println("Lỗi khi lưu trò chơi: " + e.getMessage());
            e.printStackTrace(); // In thông tin lỗi để gỡ lỗi
        }
    }

    private void deleteSaveGame() {
        Path path = Paths.get("src/main/resources/database/SaveGame.txt"); // Đường dẫn đến tệp lưu trò chơi

        // Xóa tệp lưu trò chơi nếu nó tồn tại
        try {
            if (Files.exists(path)) {
                Files.delete(path);
                gameStateModel.deleteStateModel(); // Xóa mô hình trạng thái trò chơi
                System.out.println("Trò chơi đã lưu đã được xóa thành công!");
            } else {
                System.out.println("Không tìm thấy tệp lưu trò chơi để xóa.");
            }
        } catch (IOException e) {
            System.out.println("Lỗi khi xóa trò chơi đã lưu: " + e.getMessage());
            e.printStackTrace(); // In thông tin lỗi để gỡ lỗi
        }
    }

    @Override
    public String getNamePlayerCanTake() {
        // Trả về tên người chơi có thể lấy
        if (player1.getCanTake()) {
            return player1.getName();
        } else {
            return player2.getName();
        }
    }

    // Đổi lượt chơi giữa hai người chơi và xử lý lượt của máy nếu cần
    void swapTurns(Boolean take) {
        // Đổi trạng thái có thể lấy que của hai người chơi
        player1.setCanTake(!player1.getCanTake());
        player2.setCanTake(!player2.getCanTake());

        // Cập nhật giao diện cho AI nếu người chơi hiện tại là máy
        gamePlayView.setViewForAI(player2.getCanTake() && player2.getIsMachine());

        // Nếu người chơi hiện tại là máy và được phép lấy que, thực hiện lượt của máy
        if (take && player2.getCanTake() && player2.getIsMachine()) {
            new Thread(() -> {
                try {
                    Thread.sleep(1500); // Đợi 1.5 giây trước khi máy thực hiện lượt
                    gamePlayView.doTurn(); // Máy thực hiện lượt
                } catch (InterruptedException e) {
                    e.printStackTrace(); // In lỗi nếu có
                }
            }).start();
        }
    }

    void updateHistoryTaken(int currentRow, int numberOfSticksTaken) {
        // Cập nhật lịch sử các nước đi
        String namePlayer = player1.getCanTake() ? player2.getName() : player1.getName();
        String historyEntry = String.format("%d. %s đã lấy %d que ở hàng %d",
                gameStateModel.getHistoryIndex(),
                namePlayer,
                numberOfSticksTaken,
                currentRow + 1);

        // Thêm mục lịch sử vào danh sách historyTaken
        gameStateModel.getHistoryTaken().add(historyEntry);

        // Tăng chỉ số lịch sử cho mục tiếp theo
        gameStateModel.incrementHistoryIndex();
    }

    @Override
    public String[] getHistoryTaken() {
        // Lấy danh sách lịch sử từ mô hình trạng thái trò chơi
        String[] historyArray;
        List<String> historyList = gameStateModel.getHistoryTaken();

        // Chuyển đổi danh sách thành mảng      
        historyArray = new String[historyList.size()];
        historyList.toArray(historyArray);

        // Trả về mảng       
        return historyArray;
    }

    @Override
    public void undoSticksTaken() {
        // Lấy lịch sử các hành động
        List<String> historyTaken = gameStateModel.getHistoryTaken();

        // Kiểm tra xem có hành động nào để hoàn tác không
        if (!historyTaken.isEmpty()) {
            // Lấy mục lịch sử cuối cùng
            String lastAction = historyTaken.remove(historyTaken.size() - 1);

            // Phân tích hành động cuối cùng để trích xuất chi tiết
            String[] parts = lastAction.split(" ");

            int row = Integer.parseInt(parts[parts.length - 1]) - 1; // Chỉ số hàng (đã điều chỉnh cho chỉ số bắt đầu từ 0)
            int sticksRemoved = Integer.parseInt(parts[parts.length - 5]); // Số que đã lấy

            // Hoàn tác số que đã lấy trong hàng chỉ định
            int[] sticksTaken = gameStateModel.getSticksTaken();
            sticksTaken[row] -= sticksRemoved;
            gameStateModel.setSticksTaken(sticksTaken);

            // Giảm chỉ số lịch sử
            gameStateModel.decrementHistoryIndex();

            // Đổi lượt về người chơi trước đó
            swapTurns(false);
        } else {
            System.out.println("Không có hành động nào để hoàn tác.");
        }
    }

    @Override
    public void giveUp() {
        // Xác định người thắng khi một người chơi bỏ cuộc
        String winner;
        if (player1.getCanTake()) {
            winner = player2.getName();
        } else {
            winner = player1.getName();
        }

        gamePlayView.OverGame(winner); // Thông báo kết thúc trò chơi
    }

    @Override
    public void hint() {
        // Cung cấp gợi ý cho người chơi
        int currentRow;
        int sticksCount;
        int[] sticksTaken = gameStateModel.getSticksTaken();
        int[] nextMove = machine.calculateNextMove(sticksTaken, gameStateModel.isNormalPlay());

        currentRow = nextMove[0] - 1; // Chuyển sang chỉ số mảng
        sticksCount = nextMove[1]; // Số que lấy
        gamePlayView.doHint(currentRow, sticksCount); // Hiển thị gợi ý
    }

    @Override
    public void playAgain() {
        // Thiết lập lại trò chơi để chơi lại
        player1.setCanTake(takesFirst);
        player2.setCanTake(!takesFirst);
        setPriorities(); // Thiết lập ưu tiên lượt chơi
        gameStateModel.restore(); // Khôi phục trạng thái trò chơi
    }
}
