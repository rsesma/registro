/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package registro;

import java.net.URL;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import registro.model.getRegistroData;

/**
 * FXML Controller class
 *
 * @author R
 */
public class FXMLCensalController implements Initializable {
    @FXML
    private DatePicker fentr;
    @FXML
    private TextField talla;
    @FXML
    private TextField domic;
    @FXML
    private TextField nom;
    @FXML
    private TextField cp;
    @FXML
    private TextField prof;
    @FXML
    private TextField telef;
    @FXML
    private TextField nss;
    @FXML
    private TextField ape2;
    @FXML
    private TextField ape1;
    @FXML
    private DatePicker fnac;
    @FXML
    private TextField id;
    @FXML
    private TextField pobl;
    @FXML
    private RadioButton masc;
    @FXML
    private RadioButton fem;

    public String identifier;
    public Boolean edit;
    
    public getRegistroData d;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    public void SetData(String idPac, Boolean lEdit, getRegistroData data) {
        this.identifier = idPac;
        this.edit = lEdit;
        this.d = data;
        
        try {
            ResultSet rs = this.d.getCensalRs(this.identifier);
            if (rs.next()) {
                id.setText(rs.getString("IDPAC"));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }    
    
    @FXML
    void pbAceptar(ActionEvent event) {

    }

    @FXML
    void pbCancelar(ActionEvent event) {

    }

    @FXML
    void pbExitus(ActionEvent event) {

    }
    
}
