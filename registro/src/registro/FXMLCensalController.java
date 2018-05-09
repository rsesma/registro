/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package registro;

import registro.model.CtrlType;
import registro.model.CensalCollection;
import java.net.URL;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
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
    
    public List<CensalCollection> list = new LinkedList<>();
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        this.changed = false;
        
        this.list.add(new CensalCollection("IDPAC", CtrlType.TXT, this.id,""));
        this.list.add(new CensalCollection("NSS", CtrlType.TXT, this.nss,""));
        this.list.add(new CensalCollection("NOMBRE", CtrlType.TXT, this.nom,""));
        this.list.add(new CensalCollection("APE1", CtrlType.TXT, this.ape1,""));
        this.list.add(new CensalCollection("APE2", CtrlType.TXT, this.ape2,""));
        this.list.add(new CensalCollection("DOMIC", CtrlType.TXT, this.domic,""));
        this.list.add(new CensalCollection("POBL", CtrlType.TXT, this.pobl,""));
        this.list.add(new CensalCollection("PROF", CtrlType.TXT, this.prof,""));
        
        this.cp.addEventFilter(KeyEvent.KEY_TYPED, numeric_Validation(5,false));
        this.list.add(new CensalCollection("CPOST", CtrlType.TXT, this.cp,""));
        
        this.talla.addEventFilter(KeyEvent.KEY_TYPED, numeric_Validation(4,true));
        this.talla.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) { // we only care about loosing focus
                if (!this.talla.getText().trim().isEmpty()) {
                    Double v = Double.parseDouble(this.talla.getText());
                    if (v < 1.5 | v > 2.0) {
                        Alert alert = new Alert(AlertType.ERROR, "La talla debe estar entre 1.5 y 2.0 metros");
                        alert.setHeaderText("Error de validación");
                        alert.showAndWait();
                        this.talla.requestFocus();
                    }                    
                }
            }
        });
        this.list.add(new CensalCollection("TALLA", CtrlType.TXT, this.talla,""));
        
        this.telef.addEventFilter(KeyEvent.KEY_TYPED, numeric_Validation(9,false));
        this.list.add(new CensalCollection("TEL", CtrlType.TXT, this.telef,""));
        
        this.list.add(new CensalCollection("FNAC", CtrlType.DATE, this.fnac,""));
        this.list.add(new CensalCollection("FENTR", CtrlType.DATE, this.fentr,""));
        
        this.list.add(new CensalCollection("SEXO", CtrlType.RB, this.fem,"F"));
        this.list.add(new CensalCollection("SEXO", CtrlType.RB, this.masc,"M"));
        
        this.list.add(new CensalCollection("NOTAS", CtrlType.MEMO, this.notes,""));
    }

    public EventHandler<KeyEvent> numeric_Validation(final Integer max_Lengh, final Boolean dec) {
        return new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                TextField txt_TextField = (TextField) e.getSource();                
                if (txt_TextField.getText().length() >= max_Lengh) {                    
                    e.consume();
                }
                if (dec) {
                    if(e.getCharacter().matches("[0-9.]")){ 
                        if(txt_TextField.getText().contains(".") && e.getCharacter().matches("[.]")){
                            e.consume();
                        }else if(txt_TextField.getText().length() == 0 && e.getCharacter().matches("[.]")){
                            e.consume(); 
                        }
                    }else{
                        e.consume();
                    }
                } else {
                    if(!e.getCharacter().matches("[0-9]")){ 
                        e.consume();
                    }
                }
            }
        };
    }

    public void SetData(String idPac, Boolean lEdit, getRegistroData data) {
        this.edit = lEdit;
        this.d = data;
        
        try {
            if (this.edit) {
                this.btExit.setDisable(false);
                
                String v;
                ResultSet rs = this.d.getCensalRs(idPac);
                if (rs.next()) {
                    this.identifier = idPac;
                    for(CensalCollection c : list){
                        switch (c.type) {
                            case TXT:
                                v = rs.getString(c.field);
                                if (!rs.wasNull()) {
                                    c.oTxt.setText(v); 
                                }
                                break;
                            case MEMO:
                                v = rs.getString(c.field);
                                if (!rs.wasNull()) {
                                    c.oMemo.setText(v);
                                }
                                break;
                            case DATE:
                                java.sql.Date fecha = rs.getDate(c.field);
                                if (!rs.wasNull()) {
                                    c.oDate.setValue(fecha.toLocalDate());
                                }
                                break;
                            case RB:
                                v = rs.getString(c.field);
                                if (!rs.wasNull()) {
                                    if (v.equalsIgnoreCase(c.value)) c.oRB.setSelected(true);
                                }
                                break;
                        }
                    }
                }
                rs.close();
            }
            else {
                this.btExit.setDisable(true);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }    
    
    @FXML
    void pbAceptar(ActionEvent event) {
        
        if (this.id.getText().isEmpty()) {
            Alert error = new Alert(Alert.AlertType.ERROR, "El identificador de paciente no puede quedar vacío.");
            error.setTitle("Error");
            error.setHeaderText("Identificador vacío");
            error.showAndWait();
        } else {
            if (this.d.CensalIdExists(this.id.getText())) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "El identificador de paciente ya existe.");
                alert.setTitle("Atención");
                alert.setHeaderText("Identificador duplicado");
                alert.showAndWait();
            } else {
                Boolean ok = true;
                if (this.edit) {
                    if (!this.id.getText().equalsIgnoreCase(this.identifier)) {
                        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Se aplicarán los cambios en todos los registros relacionados.\n\n ¿Desea continuar?");
                        confirm.setTitle("Confirmar");
                        confirm.setHeaderText("Ha cambiado el identificador");
                        Optional<ButtonType> result = confirm.showAndWait();
                        ok = (result.get() == ButtonType.OK);
                    }

                    if (ok) ok = this.d.updateCensal(this.identifier, this.list);
                } else {
                    ok = this.d.addCensal(this.list);
                }
                
                if (ok) {
                    this.changed = true;
                    this.updatedID = this.id.getText();
                    closeWindow();
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

}
