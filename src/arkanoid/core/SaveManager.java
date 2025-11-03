package arkanoid.core;

import java.io.*;

/**
 * Class quản lý việc Save/Load game (không dùng Gson, tự parse JSON)
 */
public class SaveManager {
    private static final String SAVE_FILE = "savegame.json";

    /**
     * Lưu game state vào file
     */
    public static boolean saveGame(SaveGame data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_FILE))) {
            // Tự tạo JSON string
            String json = String.format(
                    "{\n" +
                            "  \"score\": %d,\n" +
                            "  \"lives\": %d,\n" +
                            "  \"currentLevel\": %d,\n" +
                            "  \"highScore\": %d\n" +
                            "}",
                    data.getScore(),
                    data.getLives(),
                    data.getCurrentLevel(),
                    data.getHighScore()
            );

            writer.write(json);
            System.out.println("✅ Game saved successfully: " + data);
            return true;
        } catch (IOException e) {
            System.err.println("❌ Failed to save game: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Load game state từ file
     */
    public static SaveGame loadGame() {
        File file = new File(SAVE_FILE);

        // Kiểm tra file có tồn tại không
        if (!file.exists()) {
            System.out.println("⚠️ No save file found");
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(SAVE_FILE))) {
            // Đọc toàn bộ file
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            String json = jsonBuilder.toString();

            // Parse JSON thủ công
            int score = extractInt(json, "score");
            int lives = extractInt(json, "lives");
            int currentLevel = extractInt(json, "currentLevel");
            int highScore = extractInt(json, "highScore");

            // Validate
            if (currentLevel <= 0) {
                System.out.println("⚠️ Invalid save data");
                return null;
            }

            SaveGame data = new SaveGame(score, lives, currentLevel, highScore);
            System.out.println("✅ Game loaded successfully: " + data);
            return data;

        } catch (IOException | NumberFormatException e) {
            System.err.println("❌ Failed to load game: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Helper method: Trích xuất giá trị int từ JSON string
     */
    private static int extractInt(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*(\\d+)";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);

        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return 0;
    }

    /**
     * Xóa file save (dùng khi chơi mới)
     */
    public static void deleteSave() {
        File file = new File(SAVE_FILE);
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("🗑️ Save file deleted");
            }
        }
    }

    /**
     * Kiểm tra có file save không
     */
    public static boolean hasSaveFile() {
        return new File(SAVE_FILE).exists();
    }
}