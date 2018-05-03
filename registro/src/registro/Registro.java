/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package registro;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import registro.model.getRegistroData;

/**
 *
 * @author R
 */
public class Registro extends Application {
    
    public getRegistroData d = new getRegistroData();
    
    @Override
    public void start(Stage stage) throws Exception {
        Boolean lContinue = true;

        // Launch login window
        if (false) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLlogin.fxml")); 
            Parent r2 = (Parent) fxmlLoader.load(); 
            Stage stage0 = new Stage(); 
            stage0.initModality(Modality.WINDOW_MODAL);
            stage0.setTitle("Log in");
            stage0.setScene(new Scene(r2));
            FXMLloginController login = fxmlLoader.<FXMLloginController>getController();
            login.d = d;
            login.lOk = false;
            stage0.showAndWait();
            lContinue = login.lOk;
        } else {
            d.getConnection("rsesma", "amsesr", "127.0.0.1");
        }
        
        if (lContinue) {
            // Launch main app window
            FXMLLoader fxmlReg = new FXMLLoader(getClass().getResource("FXMLregistro.fxml")); 
            Parent root = (Parent) fxmlReg.load(); 
            
            stage.setTitle("Registro de pacientes");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            
            FXMLregistroController reg = fxmlReg.<FXMLregistroController>getController();
            reg.SetData(d);
            
            stage.show();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
