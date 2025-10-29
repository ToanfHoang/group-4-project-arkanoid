package arkanoid.core;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class LevelCompleteOverlay extends VBox {
    private Button continueButton;
    private Button exitButton;

    public LevelCompleteOverlay(int level, int score) {
        setAlignment(Pos.CENTER);
        setSpacing(20);
        setStyle("-fx-background-color: rgba(0,0,0,0.7);");

        Label title = new Label("LEVEL " + level + " COMPLETE!");
        title.setFont(Font.font("Arial", 48));
        title.setTextFill(Color.GOLD);
        title.setTextAlignment(TextAlignment.CENTER);

        Label scoreLabel = new Label("Score: " + score);
        scoreLabel.setFont(Font.font("Arial", 28));
        scoreLabel.setTextFill(Color.WHITE);

        continueButton = new Button("Continue");
        exitButton = new Button("Exit");

        continueButton.setStyle("-fx-font-size: 20px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        exitButton.setStyle("-fx-font-size: 20px; -fx-background-color: #E53935; -fx-text-fill: white;");

        getChildren().addAll(title, scoreLabel, continueButton, exitButton);
    }

    public Button getContinueButton() { return continueButton; }
    public Button getExitButton() { return exitButton; }
}