package models;

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
import java.util.stream.Collectors;

public class SaveModel {

    private GameModel gameModel;
    private PlayerModel player1; // Người chơi 1
    private PlayerModel player2; // Người chơi 2
    private GameStateModel gameStateModel; // Mô hình trạng thái trò chơi
    private Boolean takesFirst; // Biến kiểm tra máy đi trước, true nếu máy đi trước
    private String namePlayer1 = new String("Player 1"); // Tên người chơi 1
    private String namePlayer2 = new String("Player 2"); // Tên người chơi 2
    private String nameMachine = new String("Machine"); // Tên máy

    public SaveModel() {
        player1 = new PlayerModel();
        player2 = new PlayerModel();
        gameStateModel = new GameStateModel();
        takesFirst = true;
    }

    public GameModel getGameModel() {
        return gameModel;
    }

    public void setGameModel(GameModel gameModel) {
        this.gameModel = gameModel;
    }

    public PlayerModel getPlayer1() {
        return player1;
    }

    public void setPlayer1(PlayerModel player1) {
        this.player1 = player1;
    }

    public PlayerModel getPlayer2() {
        return player2;
    }

    public void setPlayer2(PlayerModel player2) {
        this.player2 = player2;
    }

    public GameStateModel getGameStateModel() {
        return gameStateModel;
    }

    public void setGameStateModel(GameStateModel gameStateModel) {
        this.gameStateModel = gameStateModel;
    }

    public Boolean getTakesFirst() {
        return takesFirst;
    }

    public void setTakesFirst(Boolean takesFirst) {
        this.takesFirst = takesFirst;
    }

    public String getNamePlayer1() {
        return namePlayer1;
    }

    public void setNamePlayer1(String namePlayer1) {
        this.namePlayer1 = namePlayer1;
    }

    public String getNamePlayer2() {
        return namePlayer2;
    }

    public void setNamePlayer2(String namePlayer2) {
        this.namePlayer2 = namePlayer2;
    }

    public String getNameMachine() {
        return nameMachine;
    }

    public void setNameMachine(String nameMachine) {
        this.nameMachine = nameMachine;
    }

    public void saveGame() {
        Path path = Paths.get("src/main/resources/database/SaveGame.txt"); // Đường dẫn đến tệp lưu trò chơi

        // Tạo thư mục nếu chưa tồn tại
        try {
            Files.createDirectories(path.getParent());
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
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
            writer.write("SticksInRow: "
                    + java.util.Arrays.toString(gameStateModel.getSticksInRow()).replaceAll("[\\[\\]]", "") + "\n");
            writer.write("SticksTaken: "
                    + java.util.Arrays.toString(gameStateModel.getSticksTaken()).replaceAll("[\\[\\]]", "") + "\n");

            // Ghi lịch sử hành động
            writer.write("HistoryTaken: " + String.join(", ", gameStateModel.getHistoryTaken()) + "\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteSaveGame() {
        Path path = Paths.get("src/main/resources/database/SaveGame.txt"); // Đường dẫn đến tệp lưu trò chơi

        // Xóa tệp lưu trò chơi nếu nó tồn tại
        try {
            if (Files.exists(path)) {
                Files.delete(path);
                gameStateModel.deleteStateModel();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readSaveGame() throws URISyntaxException {
        gameStateModel = new GameStateModel();
        Path path = Paths.get("src/main/resources/database/SaveGame.txt"); // Đường dẫn đến tệp lưu trò chơi

        // Kiểm tra xem tệp có tồn tại trước khi cố gắng đọc
        if (!Files.exists(path)) {
            return;
        }

        // Đọc thông tin từ tệp
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(": "); // Tách dòng thành các phần
                if (parts.length < 2) {
                    continue; // Bỏ qua các dòng không có định dạng đúng yêu cầu
                }
                String key = parts[0].trim();
                String value = parts[1].trim();

                switch (key) {
                    case "GameMode":
                        if (value.equals("PvM")) {
                            player1 = new PlayerModel(namePlayer1, false);
                            player2 = new PlayerModel(nameMachine, true);
                        } else {
                            player1 = new PlayerModel(namePlayer1, false);
                            player2 = new PlayerModel(namePlayer2, false);
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
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int[] parseIntArray(String value) {
        String[] sticksStr = value.replaceAll("[\\[\\]]", "").split(", "); // Xóa dấu ngoặc và tách chuỗi
        return Arrays.stream(sticksStr) // Chuyển đổi thành stream
                .mapToInt(Integer::parseInt) // Chuyển đổi từng phần thành số nguyên
                .toArray(); // Trả về mảng số nguyên
    }

    public void SaveSetting(GameModel gameModel) {
        this.gameModel = gameModel;
        Path path = Paths.get("src/main/resources/database/Setting.txt"); // Đường dẫn đến tệp cài đặt

        // Tạo thư mục nếu chưa tồn tại
        try {
            Files.createDirectories(path.getParent());  
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Lưu thông tin vào tệp (ghi đè nếu đã tồn tại)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile(), false))) { // 'false' để ghi đè                   
            writer.write("MaxRows: " + gameModel.getMaxRows() + "\n");
            writer.write("MinRows: " + gameModel.getMinRows() + "\n");
            writer.write("MaxSticksInRow: " + gameModel.getMaxSticksInRow() + "\n");
            writer.write("MinSticksInRow: " + gameModel.getMinSticksInRow() + "\n");

            writer.write("GameRules: " + gameModel.isNormalPlay() + "\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ReadSetting() {
        Path path = Paths.get("src/main/resources/database/Setting.txt"); // Đường dẫn đến tệp cài đặt

        // Kiểm tra xem tệp có tồn tại trước khi cố gắng đọc
        if (!Files.exists(path)) {        
            gameModel = new GameModel(5, 1, 9, 1, Boolean.TRUE);
            SaveSetting(gameModel); // Tạo cài đặt mặc định nếu tệp không tồn tại
            return; // Thoát nếu tệp không tồn tại
        }
        gameModel = new GameModel();
        // Đọc thông tin từ tệp
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(": "); // Tách dòng thành các phần
                if (parts.length < 2) {
                    continue; // Bỏ qua các dòng không có định dạng đúng yêu cầu
                }
                String key = parts[0].trim();
                String value = parts[1].trim(); 

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
                }
            }      
        } catch (IOException e) {         
            e.printStackTrace(); 
        }
    }

}
