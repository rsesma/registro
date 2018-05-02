/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package registro;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import registro.model.getRegistroData;

/**
 * FXML Controller class
 *
 * @author R
 */
public class FXMLloginController implements Initializable {
    
    @FXML
    private TextField user;
    @FXML
    private PasswordField pswd;
    @FXML
    private TextField server;
    
    public getRegistroData d;
    
    public Boolean lOk;

    @FXML
    private void pbAceptarFired(ActionEvent event) {
        try {
            if (d.getConnection(user.getText(),pswd.getText(),server.getText())) {
                lOk = true;
                closeWindow();
            }
        } catch (Exception e) {
            //System.out.println(e.getMessage());
        }
        
    }
    
    @FXML
    private void pbCancelarFired(ActionEvent event) {
        closeWindow();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    private void closeWindow() {
        // close window
        Stage stage = (Stage) user.getScene().getWindow();
        stage.close();
    }    
}
