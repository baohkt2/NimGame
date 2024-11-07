// encoding=UTF-8
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

/**
 *
 * @author ASUS
 */
import java.util.Random;

public class Machine {

    private final int numberOfRows;
    private final int[] sticksInRow;

    public Machine(int numberOfRows, int[] sticksInRow) {
        this.numberOfRows = numberOfRows;
        this.sticksInRow = sticksInRow.clone(); // Clone to avoid external modification
    }

    public int[] calculateNextMove(int[] sticksTaken, boolean isNormalPlay) {
        return isNormalPlay ? normalPlay(sticksTaken) : miserePlay(sticksTaken);
    }

    /**
     * Normal-play: Tìm nước đi tối ưu để đạt nim_sum = 0 hoặc chọn ngẫu nhiên
     * nếu không có.
     */
    private int[] normalPlay(int[] sticksTaken) {
        int[] currentSticksInRow = getCurrentSticksInRow(sticksTaken);
        int nimSum = calculateNimSum(currentSticksInRow);

        if (nimSum == 0) {
            return makeRandomMove(currentSticksInRow);  // Nếu nim_sum = 0, chọn ngẫu nhiên vì không có nước đi tối ưu
        }

        // Tìm hàng để tạo nim_sum = 0
        for (int i = 0; i < numberOfRows; i++) {
            int heap = currentSticksInRow[i];
            int target = heap ^ nimSum;  // Số que còn lại trong đống nếu tạo nim_sum = 0

            if (target < heap) {
                return new int[]{i + 1, heap - target};  // Trả về hàng (1-based) và số que cần lấy
            }
        }

        return makeRandomMove(currentSticksInRow);  // Nếu không có nước đi tối ưu, chọn ngẫu nhiên
    }

    /**
     * Misère-play: Tìm nước đi tối ưu để đạt nim_sum = 1 hoặc chọn ngẫu nhiên
     * nếu không có.
     */
   private int[] miserePlay(int[] sticksTaken) {
        int[] currentSticksInRow = getCurrentSticksInRow(sticksTaken);
        int nimSum = calculateNimSum(currentSticksInRow);

        // Đếm số hàng có nhiều hơn 1 que
        int rowsWithMoreThanOne = 0;
        for (int sticks : currentSticksInRow) {
            if (sticks > 1) {
                rowsWithMoreThanOne++;
            }
        }

        // Nếu chỉ còn một hàng có hơn 1 que, giảm số que còn lại xuống 0
        if (rowsWithMoreThanOne <= 1) {
            for (int i = 0; i < numberOfRows; i++) {
                if (currentSticksInRow[i] > 1) {
                    return new int[]{i + 1, currentSticksInRow[i]}; // Giảm về 0
                }
            }
        }
        
        // Tìm nước đi tạo nim_sum = 1
        for (int i = 0; i < numberOfRows; i++) {
            int heap = currentSticksInRow[i];
            int target = heap ^ nimSum;

            if (target < heap) {
                return new int[]{i + 1, heap - target}; // Trả về nước đi tạo nim_sum = 1
            }
        }

        // Nếu không có nước đi tối ưu, chọn ngẫu nhiên
        return makeRandomMove(currentSticksInRow);
    }
    /**
     * Tính toán nim_sum cho các đống.
     */
    private int calculateNimSum(int[] piles) {
        int nimSum = 0;
        for (int pile : piles) {
            nimSum ^= pile;  // Phép XOR
        }
        return nimSum;
    }

    /**
     * Tạo nước đi ngẫu nhiên nếu không có nước đi tối ưu.
     */
    private int[] makeRandomMove(int[] currentSticksInRow) {
        Random random = new Random();
        int rowToTakeFrom;

        do {
            rowToTakeFrom = random.nextInt(numberOfRows);
        } while (currentSticksInRow[rowToTakeFrom] == 0);

        int sticksToTake = random.nextInt(currentSticksInRow[rowToTakeFrom]) + 1;
        return new int[]{rowToTakeFrom + 1, sticksToTake};
    }

    /**
     * Trạng thái hiện tại của các đống dựa trên số que đã lấy.
     */
    private int[] getCurrentSticksInRow(int[] sticksTaken) {
        int[] currentSticksInRow = new int[numberOfRows];
        for (int i = 0; i < numberOfRows; i++) {
            currentSticksInRow[i] = sticksInRow[i] - sticksTaken[i];
        }
        return currentSticksInRow;
    }
}
