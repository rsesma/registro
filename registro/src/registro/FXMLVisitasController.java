/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package registro;

import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import org.controlsfx.control.textfield.TextFields;
import registro.model.CtrlCollection;
import registro.model.CtrlType;
import registro.model.getRegistroData;

/**
 * FXML Controller class
 *
 * @author R
 */
public class FXMLVisitasController implements Initializable {

    @FXML
    private Label nom;
    @FXML
    private Label edad;
    @FXML
    private TextField id;
    @FXML
    private DatePicker fecha;
    @FXML
    private TextField peso;
    @FXML
    private TextField imc;
    @FXML
    private TextField gluc;
    @FXML
    private ComboBox diab;
    @FXML
    private ComboBox menop;
    @FXML
    private TextField he;
    @FXML
    private TextField ejer;
    @FXML
    private ComboBox dieta;
    @FXML
    private TextField tabaco;
    @FXML
    private ComboBox marca;
    @FXML
    private TextField alq;
    @FXML
    private TextField nic;
    @FXML
    private TextField co;
    
    private Boolean edit;
    private Boolean changed;
    private getRegistroData d;
    private Double talla;
        
    public List<CtrlCollection> list = new LinkedList<>();
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        this.changed = false;
        
        this.list.add(new CtrlCollection("IDPACV", CtrlType.TXT, this.id,""));
        this.list.add(new CtrlCollection("FECHA", CtrlType.DATE, this.fecha,""));
        
        this.list.add(new CtrlCollection("PESO", CtrlType.TXT, this.peso,""));
        this.peso.setOnKeyTyped(keyevent ->{
            if (!"0123456789.".contains(keyevent.getCharacter())) keyevent.consume();
        });
        this.peso.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) { // we only care about loosing focus
                if (ValidateNumber(this.peso.getText(),10.0,250.0,1)) computeIMC();
                else this.peso.requestFocus();
            }
        });
        
        this.list.add(new CtrlCollection("GLUC", CtrlType.TXT, this.gluc,""));
        this.gluc.setOnKeyTyped(keyevent ->{
            if (!"0123456789".contains(keyevent.getCharacter())) keyevent.consume();
        });
        this.gluc.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) { // we only care about loosing focus
                if (!ValidateNumber(this.gluc.getText(),20.0,1200.0,0)) this.gluc.requestFocus();
            }
        });
        
        this.list.add(new CtrlCollection("DIABET", CtrlType.COMBO, this.diab,"DSiNo;CODSN;DESCSN"));
        this.diab.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) { // we only care about loosing focus
                CheckValueIsInList(this.diab);
            }
        });

        this.list.add(new CtrlCollection("MENOP", CtrlType.COMBO, this.menop,"DSiNo;CODSN;DESCSN"));
        this.menop.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) { // we only care about loosing focus
                CheckValueIsInList(this.menop);
            }
        });
        
        this.list.add(new CtrlCollection("HE", CtrlType.TXT, this.he,""));
        this.he.setOnKeyTyped(keyevent ->{
            if (!"0123456789".contains(keyevent.getCharacter())) keyevent.consume();
        });
        this.he.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) { // we only care about loosing focus
                if (!ValidateNumber(this.he.getText(),0.0,60.0,0)) this.he.requestFocus();
            }
        });

        this.list.add(new CtrlCollection("EJER", CtrlType.TXT, this.ejer,""));
        this.ejer.setOnKeyTyped(keyevent ->{
            if (!"0123456789".contains(keyevent.getCharacter())) keyevent.consume();
        });
        this.ejer.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) { // we only care about loosing focus
                if (!ValidateNumber(this.ejer.getText(),0.0,50.0,0)) this.ejer.requestFocus();
            }
        });

        this.list.add(new CtrlCollection("DIETA", CtrlType.COMBO, this.dieta,"DCumpl;CODCMP;DESCMP"));
        this.dieta.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) { // we only care about loosing focus
                CheckValueIsInList(this.dieta);
            }
        });
        
        this.list.add(new CtrlCollection("TABACO", CtrlType.TXT, this.tabaco,""));
        this.tabaco.setOnKeyTyped(keyevent ->{
            if (!"0123456789".contains(keyevent.getCharacter())) keyevent.consume();
        });
        this.tabaco.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) { // we only care about loosing focus
                if (!ValidateNumber(this.tabaco.getText(),0.0,80.0,0)) this.ejer.requestFocus();
            }
        });

        this.list.add(new CtrlCollection("TIPOTA", CtrlType.COMBO, this.marca,"DTabaco;IDMARC;MARCA"));
        this.marca.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) { // we only care about loosing focus
                CheckValueIsInList(this.marca);
                loadMarcaTab();
            }
        });
        
    }
    
    public Boolean ValidateNumber(String c, Double min, Double max, Integer dec) {
        Boolean ok = true;
        if (!c.trim().isEmpty()) {
            Double v = Double.parseDouble(c);
            Integer nDec = c.indexOf('.') > 0 ? (c.length() - c.indexOf('.') - 1) : 0;
            if (v < min | v > max) {
                showError("El valor debe estar entre ".concat(min.toString()).concat(" y ").concat(max.toString()), "Error de validación");
                ok = false;
            } else if (nDec > dec) {
                showError("El número de decimales debe ser ".concat(dec.toString()), "Error de validación");
                ok = false;
            }
        }
        return ok;
    }
    
    public void CheckValueIsInList(ComboBox o) {
        String c = o.getEditor().getText();
        if (!c.trim().isEmpty()) {
            if (!o.getItems().contains(c)) {
                showError("El valor " + c + " no está en la lista", "Error de validación");
                o.requestFocus();
            }
        }
    }
    
    public void SetData(String id, Date fecha, Boolean lEdit, getRegistroData data) {
        this.d = data;
        this.edit = lEdit;

        try {
            if (this.edit) {
                ResultSet rs = d.getVisitasByIdFecha(id, fecha);
                if (rs.next()) {
                    this.nom.setText(rs.getString("NOM"));
                    Long age = ChronoUnit.YEARS.between(rs.getDate("FNAC").toLocalDate(),fecha.toLocalDate());
                    this.edad.setText(age.toString().concat(" años"));
                    this.talla = rs.getDouble("TALLA");
                    
                    String v;
                    for(CtrlCollection c : list){
                        switch (c.type) {
                            case TXT:
                                v = rs.getString(c.field);
                                if (!rs.wasNull()) {
                                    c.oTxt.setText(v); 
                                }
                                break;
                            case DATE:
                                java.sql.Date f = rs.getDate(c.field);
                                if (!rs.wasNull()) {
                                    c.oDate.setValue(f.toLocalDate());
                                }
                                break;
                            case COMBO:
                                try {
                                    ResultSet rsDic = d.getDic(c.dic);
                                    while (rsDic.next()) {
                                        c.oCombo.getItems().add(rsDic.getString(c.descrip));
                                    }
                                    rsDic.close();
                                    
                                    Integer val = rs.getInt(c.field);
                                    if (!rs.wasNull()) {
                                        c.oCombo.setValue(d.getDescripFromCod(c.dic, c.cod, val, c.descrip));
                                    }
                                    
                                    TextFields.bindAutoCompletion(c.oCombo.getEditor(), c.oCombo.getItems());
                                } catch (Exception e) {
                                    showError(e.getMessage(),"Error obteniendo diccionario");
                                }
                                break;
                        }
                    }
                    
                    computeIMC();
                    loadMarcaTab();
                }
            }
        } catch (Exception e) {
            showError(e.getMessage(),"Error obteniendo visita");
        }
    }
    
    public void computeIMC() {
        if (!this.peso.getText().trim().isEmpty()) {
            Double p = Double.parseDouble(this.peso.getText());
            Double IMC =(double) Math.round((p / Math.pow(this.talla,2)) * 100) / 100;
            this.imc.setText(IMC.toString());
        }
    }
    
    public void loadMarcaTab() {
        if (this.marca.getValue() != null) {
            String c = this.marca.getValue().toString();
            
            try {
                ResultSet rs = d.getDatosMarcaTab(c);
                if (rs.next()) {
                    this.alq.setText(rs.getString("ALQUIT"));
                    this.nic.setText(rs.getString("NICOT"));
                    this.co.setText(rs.getString("CO"));
                }
            } catch (Exception e) {
                showError(e.getMessage(),"Error cargando el diccionario");
            }
        }
    }
    
    public void showError(String message, String header) {
        Alert a = new Alert(Alert.AlertType.WARNING, message);
        a.setTitle("Error");
        a.setHeaderText(header);
        a.showAndWait();
    }
}
