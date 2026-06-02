package ui;

import game.FlappyGame;
import game.GameConfig;
import game.GameListener;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import model.ScoreEntry;
import model.User;

import java.util.List;
import java.util.Optional;
import java.util.function.IntConsumer;

public class GameView implements GameListener {

    public static final int RIGHT_PANEL_WIDTH = 230;

    public static final int SCENE_WIDTH = GameConfig.CANVAS_WIDTH + RIGHT_PANEL_WIDTH;
    public static final int SCENE_HEIGHT = GameConfig.CANVAS_HEIGHT;

    private final User user;
    private final IntConsumer gameOverHandler;
    private final Runnable logoutHandler;
    private final Runnable deleteUserHandler;
    private final BorderPane root;
    private final Canvas canvas;
    private final Label currentScoreLabel;
    private final TextArea highScoresArea;

    private FlappyGame game;

    public GameView(User user, IntConsumer gameOverHandler, Runnable logoutHandler, Runnable deleteUserHandler) {
        this.user = user;
        this.gameOverHandler = gameOverHandler;
        this.logoutHandler = logoutHandler;
        this.deleteUserHandler = deleteUserHandler;

        canvas = new Canvas(GameConfig.CANVAS_WIDTH, GameConfig.CANVAS_HEIGHT);
        canvas.setFocusTraversable(true);

        currentScoreLabel = new Label();
        highScoresArea = new TextArea();
        highScoresArea.setEditable(false);
        highScoresArea.setPrefRowCount(7);
        highScoresArea.setFocusTraversable(false);

        Button newGameButton = new Button("New Game");
        Button logoutButton = new Button("Logout");
        Button deleteButton = new Button("Delete User Info");

        makeMouseOnly(newGameButton, logoutButton, deleteButton);

        VBox rightPanel = new VBox(12,
                new Label("Player: " + user.getNickname()),
                currentScoreLabel,
                new Label("Highest Scores"),
                highScoresArea,
                newGameButton,
                logoutButton,
                deleteButton,
                new Label("Controls: Space / Up arrow to Jump"));
        rightPanel.setPadding(new Insets(15));
        rightPanel.setPrefWidth(RIGHT_PANEL_WIDTH);

        root = new BorderPane();
        root.setCenter(canvas);
        root.setRight(rightPanel);

        newGameButton.setOnAction(e -> startNewGame());
        logoutButton.setOnAction(e -> logoutHandler.run());
        deleteButton.setOnAction(e -> confirmAndDeleteUser());

        canvas.setOnMouseClicked(e -> {
            if (game != null) {
                game.handleKey(KeyCode.SPACE);
            }
            refreshScoreBoard();
        });
        refreshScoreBoard();
    }

    public Parent getRoot() {
        return root;
    }

    public void attachKeyBoardControls(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            KeyCode key = e.getCode();
            if (isGameKey(key) && game != null) {
                game.handleKey(key);
                e.consume();
            }
        });
        canvas.requestFocus();
    }

    public void startNewGame() {
        stopGame();
        game = new FlappyGame(canvas, this);
        game.start();
        canvas.requestFocus();
    }

    public void stopGame() {
        if (game != null) {
            game.stop();
        }
    }

    public void refreshScoreBoard() {
        int currentScore = game == null ? 0 : game.getScore();
        currentScoreLabel.setText("Current Score: " + currentScore);

        StringBuilder sb = new StringBuilder();
        List<ScoreEntry> scores = user.getHighScores();
        if (scores == null || scores.isEmpty()) {
            sb.append("No saved score yet.");
        } else {
            for (int i = 0; i < scores.size(); i++) {
                ScoreEntry s = scores.get(i);
                sb.append(i + 1)
                        .append(".")
                        .append(s.getPoints())
                        .append("pts\n");
            }
        }
        highScoresArea.setText(sb.toString());
    }

    @Override
    public void onScoreChanged(int score) {
        currentScoreLabel.setText("Current Score: " + score);
    }

    @Override
    public void onGameOver(int finalScore) {
        gameOverHandler.accept(finalScore);
    }

    private void confirmAndDeleteUser() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete your user record and scores permanently?",
                ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("Delete Account");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            deleteUserHandler.run();
        }
    }

    private void makeMouseOnly(Button... buttons) {
        for (Button button : buttons) {
            button.setFocusTraversable(false);
        }
    }

    private boolean isGameKey(KeyCode key) {
        return key == KeyCode.SPACE || key == KeyCode.UP;
    }
}