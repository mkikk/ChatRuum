package GUI;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public abstract class Controller {
    abstract void PrimaryAction();
    abstract void selectLowerField();
    abstract void selectUpperField();
    public void selectField(TextField field){
        field.requestFocus();
        field.deselect();
        field.end();
    }
}
