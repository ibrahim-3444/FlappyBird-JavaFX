package ui;

import model.User;
import persistence.PasswordHasher;
import persistence.UserStore;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class LoginView {
    private final VBox root;

    public LoginView(UserStore userStore, Consumer<User> loginHandler, Runnable registerHandler) {
        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Label messageLabel = new Label();
        Button loginButton = new Button("Login");
        Button registerButton = new Button("Create new user");

        HBox buttons = new HBox(10, loginButton, registerButton);
        buttons.setAlignment(Pos.CENTER);

        root = new VBox(12, new Label("Tetris Login"), emailField, passwordField, buttons, messageLabel);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);

        loginButton.setOnAction(e -> {
            try {
                model.User user = userStore.findByEmail(emailField.getText().trim());
                if (user == null || !PasswordHasher.verify(passwordField.getText(), user.getPasswordHash())) {
                    messageLabel.setText("Invalid email or password.");
                    return;
                }
                loginHandler.accept(user);
            } catch (Exception ex) {
                messageLabel.setText("Login error: " + ex.getMessage());
            }
        });

        registerButton.setOnAction(e -> registerHandler.run());
    }

    public Parent getRoot() {
        return root;
    }
}
