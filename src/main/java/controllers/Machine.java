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

        // Đếm số đống có nhiều hơn 1 que
        int heapsWithMoreThanOne = 0;
        int lastHeapWithMoreThanOne = -1;
        for (int i = 0; i < currentSticksInRow.length; i++) {
            if (currentSticksInRow[i] > 1) {
                heapsWithMoreThanOne++;
                lastHeapWithMoreThanOne = i;
            }
        }

        // Giai đoạn cuối: chỉ còn một đống có nhiều hơn 1 que
        if (heapsWithMoreThanOne == 1) {
            int remainingHeaps = 0;
            for (int sticks : currentSticksInRow) {
                if (sticks > 0) {
                    remainingHeaps++;
                }
            }

            // Quyết định số que cần lấy để để lại số đống lẻ
            int sticksToTake = currentSticksInRow[lastHeapWithMoreThanOne] - (remainingHeaps % 2 == 0 ? 0 : 1);
            return new int[]{lastHeapWithMoreThanOne + 1, sticksToTake};
        }

        // Giai đoạn giữa và đầu: áp dụng chiến lược nim-sum
        if (nimSum != 0) {
            for (int i = 0; i < currentSticksInRow.length; i++) {
                int heap = currentSticksInRow[i];
                int target = heap ^ nimSum;
                if (target < heap) {
                    return new int[]{i + 1, heap - target};
                }
            }
        }

        // Nếu không có nước đi tối ưu, chọn nước đi an toàn
        return makeRandomMove(currentSticksInRow);
    }

    /**
     * Tạo nước đi ngẫu nhiên nếu không có nước đi tối ưu.
     */
    private int[] makeRandomMove(int[] currentSticksInRow) {
        // Ưu tiên lấy từ đống lớn nhất, nhưng không lấy hết
        int maxHeap = 0;
        int maxHeapIndex = 0;
        for (int i = 0; i < currentSticksInRow.length; i++) {
            if (currentSticksInRow[i] > maxHeap) {
                maxHeap = currentSticksInRow[i];
                maxHeapIndex = i;
            }
        }
        int sticksToTake = maxHeap > 1 ? maxHeap - 1 : 1;
        return new int[]{maxHeapIndex + 1, sticksToTake};
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
