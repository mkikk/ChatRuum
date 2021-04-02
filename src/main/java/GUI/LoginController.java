package GUI;

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

import java.io.File;
import java.io.IOException;
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


     public void checkUserExists(ActionEvent actionEvent) {
         /* Check from server if given user exists
         * If not, then enable confirm password
         *
         */
     }


    public void rememberUsername(ActionEvent actionEvent) {
        //send query to server to store username with password

    }

    public void setPassword(ActionEvent actionEvent) {
        //
    }

    public void setPasswordConfirmation(ActionEvent actionEvent) {
        loginButton.setDisable(!passwordText.getText().equals(passwordConfirmation.getText()));
    }

    public void CheckLogin() throws IOException {
        /*
        * Check if username and password match
        * If not, then tell wrong password
         */
        noMatch.setText("Username and password do not match. Try again!");

        URL url = new File("src/main/resources/main1.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);

        Stage window = (Stage) loginButton.getScene().getWindow();
        window.setScene(new Scene(root, 1080, 600));
    }
}
