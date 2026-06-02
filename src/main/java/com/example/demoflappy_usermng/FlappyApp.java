package com.example.demoflappy_usermng;

import model.User;
import persistence.UserStore;
import ui.GameView;
import ui.LoginView;
import ui.RegisterView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class FlappyApp extends Application {
    private Stage primaryStage;
    private UserStore userStore;
    private User loggedInUser;
    private GameView gameView;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.userStore = new UserStore();
        primaryStage.setTitle("JavaFX Flappy - Login");
        showLoginScreen();
        primaryStage.show();
    }

    private void showLoginScreen() {
        LoginView loginView = new LoginView(
                userStore,
                user -> {
                    loggedInUser = user;
                    showGameScreen();
                },
                this::showRegisterScreen
        );
        primaryStage.setScene(new Scene(loginView.getRoot(), 420, 300));
    }

    private void showRegisterScreen() {
        RegisterView registerView = new RegisterView(
                userStore,
                user -> {
                    loggedInUser = user;
                    showGameScreen();
                },
                this::showLoginScreen
        );
        primaryStage.setScene(new Scene(registerView.getRoot(), 450, 420));
    }

    private void showGameScreen() {
        gameView = new GameView(
                loggedInUser,
                this::saveCurrentScore,
                this::logout,
                this::deleteLoggedInUser
        );

        Scene scene = new Scene(gameView.getRoot(), GameView.SCENE_WIDTH, GameView.SCENE_HEIGHT);
        gameView.attachKeyBoardControls(scene);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JavaFX Flappy - " + loggedInUser.getNickname());
        gameView.startNewGame();
    }

    private void saveCurrentScore(int score) {
        try {
            loggedInUser.addScore(score);
            userStore.save(loggedInUser);
            gameView.refreshScoreBoard();
        } catch (IOException ex) {
            showError("Could not save score: " + ex.getMessage());
        }
    }

    private void logout() {
        if (gameView != null) {
            gameView.stopGame();
        }
        loggedInUser = null;
        showLoginScreen();
    }

    private void deleteLoggedInUser() {
        try {
            if (gameView != null) {
                gameView.stopGame();
            }
            userStore.delete(loggedInUser.getEmail());
            loggedInUser = null;
            showLoginScreen();
        } catch (IOException ex) {
            showError("Could not delete user: " + ex.getMessage());
        }
    }

    private void showError(String message) {
        new Alert(Alert.AlertType.ERROR, message).showAndWait();
    }
}
