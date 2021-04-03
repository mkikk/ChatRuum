package GUI;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import networking.requests.CheckUsernameRequest;
import networking.requests.RegisterRequest;
import networking.responses.CheckUsernameResponse;
import networking.requests.PasswordLoginRequest;
import networking.responses.GenericResponse;
import networking.responses.Response;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


public class LoginController {
    @FXML
    Button loginButton;
    @FXML
    TextField usernameText;
    @FXML
    PasswordField passwordText, passwordConfirmation;
    @FXML
    Label noMatch;

    boolean isLogin = true;

    @FXML
    public void initialize() {
        usernameText.focusedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue) {
                checkUserExists();
            }
        });
    }

    public void onLoginButtonPressed(ActionEvent actionEvent) {
        if (isLogin) {
            loginUser();
        } else {
            registerUser();
        }
    }

    public void checkUserExists() {
        /* Check from server if given user exists
         * If not, then enable confirm password
         */
        Scene currentScene = usernameText.getScene();
        //if (currentScene.getFocusOwner() == usernameText.getStyleableNode())
        System.out.println("Checking user exists");
        var req = Main.getSession().sendRequest(new CheckUsernameRequest(usernameText.getText()));
        req.onResponse(CheckUsernameResponse.class, (s, r) -> {
            if (r.result == CheckUsernameResponse.Result.NAME_FREE) {
                Platform.runLater(() -> {
                    passwordConfirmation.setVisible(true);
                    loginButton.setText("Register");
                    loginButton.setDisable(false);
                    isLogin = false;

                });
            } else if (r.result == CheckUsernameResponse.Result.NAME_IN_USE) {
                Platform.runLater(() -> {
                    passwordConfirmation.setVisible(false);
                    loginButton.setText("Login");
                    loginButton.setDisable(false);
                    isLogin = true;
                });
            } else if (r.result == CheckUsernameResponse.Result.NAME_INVALID) {
                Platform.runLater(() -> {
                    noMatch.setText("Invalid username");
                    loginButton.setDisable(true);
                });
            }
        });
    }

    public void registerUser() {
        if (passwordText.getText().equals(passwordConfirmation.getText())) {
            noMatch.setText("");
            var req = Main.getSession().sendRequest(new RegisterRequest(usernameText.getText(), passwordText.getText()));
            req.onResponse(GenericResponse.class, (s, r) -> {
                System.out.println(r.response);
                if (r.response == Response.OK) {
                    noMatch.setText("User registered!");
                    checkUserExists();
                } else if (r.response == Response.FORBIDDEN) {
                    noMatch.setText("Registration failed!");
                }
            });
        } else {
            noMatch.setText("Passwords do not match!");
        }
    }

    public void loginUser() {
        /*
         * Check if username and password match
         * If not, then tell wrong password
         */
        var req = Main.getSession().sendRequest(new PasswordLoginRequest(usernameText.getText(), passwordText.getText()));
        req.onResponse(GenericResponse.class, (s, r) -> {
            if (r.response == Response.OK) {
                Platform.runLater(this::changeToMainMenu);
            } else {
                Platform.runLater(() -> noMatch.setText("Username and password do not match. Try again!"));
            }
        });
    }

    private void changeToMainMenu() {
        // If current scene is not active, do not change scene
        if (loginButton.getScene().getWindow() == null) return;

        URL url = null;
        Parent root = null;

        try {
            url = new File("src/main/resources/main1.fxml").toURI().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        try {
            root = FXMLLoader.load(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Stage window = (Stage) loginButton.getScene().getWindow();
        window.setScene(new Scene(root, 1080, 600));
    }
}
