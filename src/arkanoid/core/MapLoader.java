package arkanoid.core;

import arkanoid.entity.Brick;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MapLoader {

    public static List<Brick> loadMap(String filename, int brickWidth, int brickHeight) {
        List<Brick> bricks = new ArrayList<>();

        try {
            URL resource = MapLoader.class.getResource("/map/" + filename);
            if (resource == null) {
                System.err.println("Không tìm thấy map: " + filename);
                return bricks;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.openStream()));
            String line;
            int row = 0;

            while ((line = reader.readLine()) != null) {
                for (int col = 0; col < line.length(); col++) {
                    char c = line.charAt(col);
                    if (c != '0') { // 0 là ô trống
                        Brick.BrickType type = mapCharToType(c);
                        bricks.add(new Brick(100 + col * brickWidth, 40 + row * brickHeight,
                                brickWidth, brickHeight, type));
                    }
                }
                row++;
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return bricks;
    }

    private static Brick.BrickType mapCharToType(char c) {
        switch (c) {
            case '1': return Brick.BrickType.UNBREAKABLE;
            case '2': return Brick.BrickType.EXPLOSIVE;
            case '3': return Brick.BrickType.SUPER_STRONG;
            case '4': return Brick.BrickType.STRONG;
            default: return Brick.BrickType.NORMAL;
        }
    }
}
