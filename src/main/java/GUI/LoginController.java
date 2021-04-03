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


public class LoginController{
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


         loginButton.setText("Register");
     }


    public void createUser(ActionEvent actionEvent) {
        //send query to server to store username with password

    }


    public void setPasswordConfirmation(ActionEvent actionEvent) {
        loginButton.setDisable(!passwordText.getText().equals(passwordConfirmation.getText()));
        if (loginButton.isDisabled()) noMatch.setText("Passwords don't match!");
    }

    public void CheckLogin() throws IOException {
        /*
        * Check if username and password match
        * If not, then tell wrong password
         */
        noMatch.setText("Username and password do not match. Try again!");

        // switch to new window
        URL url = new File("src/main/resources/Main1.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);

        Stage window = (Stage) loginButton.getScene().getWindow();
        window.setScene(new Scene(root, 1080, 600));
    }


}
