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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import registro.model.getRegistroData;

/**
 *
 * @author R
 */
public class FXMLregistroController implements Initializable {
    
    @FXML
    private Button pbCensal;
    
    public getRegistroData d;
    
    @FXML
    private void openCensal(ActionEvent event) {
        try {
            // Launch new window
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLid.fxml")); 
            Parent r = (Parent) fxmlLoader.load();
            Stage stage = new Stage(); 
            stage.initModality(Modality.APPLICATION_MODAL); 
            stage.setTitle("Escoger identificador");
            stage.setScene(new Scene(r));
            FXMLidController controller = fxmlLoader.<FXMLidController>getController();
            controller.d = d;
            stage.showAndWait();
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void openVisitas(ActionEvent event) {
        //System.out.println("You clicked me!");
    }

    @FXML
    private void openEventos(ActionEvent event) {
        //System.out.println("You clicked me!");
    }

    @FXML
    private void salir(ActionEvent event) {
        // close window
        Stage stage = (Stage) pbCensal.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
