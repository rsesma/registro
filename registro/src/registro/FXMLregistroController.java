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
        open(Forms.CENSAL);
    }

    @FXML
    private void openVisitas(ActionEvent event) {
        open(Forms.VISITAS);
    }

    @FXML
    private void openEventos(ActionEvent event) {
        open(Forms.EVENTOS);
    }
    
    public void open(Forms tipo) {
        try {
            // Launch new window
            FXMLLoader fxml;
            if (tipo == Forms.CENSAL) fxml = new FXMLLoader(getClass().getResource("FXMLid.fxml"));
            else fxml = new FXMLLoader(getClass().getResource("FXMLidFecha.fxml"));
            Parent r = (Parent) fxml.load();
            
            Stage stage = new Stage(); 
            stage.initModality(Modality.APPLICATION_MODAL); 
            stage.setScene(new Scene(r));            
            switch (tipo) {
                case CENSAL:
                    stage.setTitle("Escoger paciente");
                    break;
                case VISITAS:
                    stage.setTitle("Escoger visita");
                    break;
                case EVENTOS:
                    stage.setTitle("Escoger evento");
                    break;
                default:
                    break;
            }

            if (tipo == Forms.CENSAL) {
                FXMLidController id = fxml.<FXMLidController>getController();
                id.SetData(this.d);
            } else {
                FXMLidFechaController idFecha = fxml.<FXMLidFechaController>getController();
                idFecha.SetData(this.d,tipo);
            }
            
            stage.showAndWait();
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }        
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

    public void SetData(getRegistroData data) {
        this.d = data;
    }    

    public enum Forms {
        CENSAL, VISITAS, EVENTOS
    }
}
