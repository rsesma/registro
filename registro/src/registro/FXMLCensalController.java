/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package registro;

import java.net.URL;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import registro.model.Controles;
import registro.model.Controles.TipoCtrl;
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
    @FXML
    private TextArea notes;
    @FXML
    private Button btExit;

    public String identifier;
    public Boolean edit;
    public Boolean changed;
    public String updatedID;
    
    public getRegistroData d;
    
    private Controles c = new Controles();
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        this.changed = false;
        
        // load controles object with data controls
        this.c.add("IDPAC", TipoCtrl.TXT, this.id,"3,n,0",true,false);
        this.c.add("NSS", TipoCtrl.TXT, this.nss,"0,0",false,true);
        this.c.add("NOMBRE", TipoCtrl.TXT, this.nom,"0,0",false,true);
        this.c.add("APE1", TipoCtrl.TXT, this.ape1,"0,0",false,true);
        this.c.add("APE2", TipoCtrl.TXT, this.ape2,"0,0",false,true);
        this.c.add("DOMIC", TipoCtrl.TXT, this.domic,"0,0",false,true);
        this.c.add("POBL", TipoCtrl.TXT, this.pobl,"0,0",false,true);
        this.c.add("PROF", TipoCtrl.TXT, this.prof,"0,0",false,true);
        this.c.add("CPOST", TipoCtrl.TXT, this.cp,"5,n,0",false,true);
        this.c.add("TALLA", TipoCtrl.TXT, this.talla,"0,n,1.5;2;2",false,false);
        this.c.add("TEL", TipoCtrl.TXT, this.telef,"9,n,0",false,true);
        this.c.add("FNAC", TipoCtrl.DATE, this.fnac,"",false,false);
        this.c.add("FENTR", TipoCtrl.DATE, this.fentr,"",false,false);        
        this.c.add("SEXO", TipoCtrl.RB, this.fem,"F",false,true);
        this.c.add("SEXO", TipoCtrl.RB, this.masc,"M",false,true);
        this.c.add("NOTAS", TipoCtrl.MEMO, this.notes,"",false,true);
    }

    public void SetData(String idPac, Boolean lEdit, getRegistroData data) {
        this.edit = lEdit;
        this.d = data;
        
        this.btExit.setDisable(true);

        if (this.edit) {
            this.btExit.setDisable(false);

            this.identifier = idPac;
            ResultSet rs;
            try {
                rs = this.d.getCensalRs(idPac);
                this.c.loadData(rs, this.d);
                rs.close();
            } catch (Exception e) {
                showMessage(e.getMessage(),"Error cargando los datos censales",AlertType.ERROR,"Error");
            }
        }
    }    
    
    @FXML
    void pbAceptar(ActionEvent event) {
        
        if (this.id.getText().isEmpty()) {
            showMessage("El identificador de paciente no puede quedar vacío","Identificador vacío",AlertType.ERROR,"Error");
        } else {
            Boolean idExists = this.d.CensalIdExists(this.id.getText());
            Boolean idChanged = this.edit && !this.identifier.equalsIgnoreCase(this.id.getText());
            if ((this.edit && idExists && idChanged) || (!this.edit && idExists)) {
                showMessage("El identificador de paciente ya existe","Identificador duplicado",AlertType.WARNING,"Atención");
            } else {
                Boolean ok = true;
                if (this.edit && idChanged) {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Se aplicarán los cambios en todos los registros relacionados.\n\n ¿Desea continuar?");
                    confirm.setTitle("Confirmar");
                    confirm.setHeaderText("Ha cambiado el identificador");
                    Optional<ButtonType> result = confirm.showAndWait();
                    ok = (result.get() == ButtonType.OK);
                }
                
                if (ok) {
                    if (this.c.validateData()) {
                        if (this.edit) this.d.update("Censal",this.c,"IDPAC = ".concat(this.identifier));
                        else this.d.add("Censal",this.c);
                        
                        this.changed = true;
                        this.updatedID = this.id.getText();
                        closeWindow();
                    }
                }
            }
        }
    }

    @FXML
    void pbCancelar(ActionEvent event) {
        closeWindow();
    }

    @FXML
    void pbExitus(ActionEvent event) {

    }

    private void closeWindow() {
        Stage stage = (Stage) id.getScene().getWindow();
        stage.close();
    }    

    public void showMessage(String message, String header, AlertType type, String title) {
        Alert a = new Alert(type, message);
        a.setTitle(title);
        a.setHeaderText(header);
        a.showAndWait();
    }
}
