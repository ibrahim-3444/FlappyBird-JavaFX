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

import java.util.Locale;
import java.util.function.Consumer;

public class RegisterView {
    private final VBox root;

    public RegisterView(UserStore userStore, Consumer<User> registrationHandler, Runnable backHandler) {
        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        TextField surnameField = new TextField();
        surnameField.setPromptText("Surname");
        TextField nicknameField = new TextField();
        nicknameField.setPromptText("Nickname");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        Label messageLabel = new Label();

        Button saveButton = new Button("Register");
        Button backButton = new Button("Back");
        HBox buttons = new HBox(10, saveButton, backButton);
        buttons.setAlignment(Pos.CENTER);

        root = new VBox(10,
                new Label("Create Tetris User"),
                nameField, surnameField, nicknameField, emailField, passwordField,
                buttons, messageLabel);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);

        saveButton.setOnAction(e -> {
            try {
                String email = emailField.getText().trim().toLowerCase(Locale.ROOT);
                if (nameField.getText().isBlank() || surnameField.getText().isBlank() || nicknameField.getText().isBlank()
                        || email.isBlank() || passwordField.getText().isBlank()) {
                    messageLabel.setText("Please fill in all fields.");
                    return;
                }
                if (!email.contains("@")) {
                    messageLabel.setText("Please enter a valid email.");
                    return;
                }
                if (userStore.findByEmail(email) != null) {
                    messageLabel.setText("This email is already registered.");
                    return;
                }

                User user = new User(
                        nameField.getText().trim(),
                        surnameField.getText().trim(),
                        nicknameField.getText().trim(),
                        email,
                        PasswordHasher.hash(passwordField.getText())
                );
                userStore.save(user);
                registrationHandler.accept(user);
            } catch (Exception ex) {
                messageLabel.setText("Register error: " + ex.getMessage());
            }
        });

        backButton.setOnAction(e -> backHandler.run());
    }

    public Parent getRoot() {
        return root;
    }
}
