/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package registro;

import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
    
    private Boolean edit;
    private Boolean changed;
    private getRegistroData d;
    private Double talla;
        
    public List<CtrlCollection> list = new LinkedList<>();
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        this.changed = false;
        
        this.list.add(new CtrlCollection("IDPACV", CtrlType.TXT, this.id,""));
        this.list.add(new CtrlCollection("FECHA", CtrlType.DATE, this.fecha,""));
        
        this.list.add(new CtrlCollection("PESO", CtrlType.TXT, this.peso,""));
        this.peso.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) { // we only care about loosing focus
                if (ValidateNumber(this.peso,10.0,250.0,1,"Peso")) computeIMC();
            }
        });
        
        this.list.add(new CtrlCollection("GLUC", CtrlType.TXT, this.gluc,""));
        this.gluc.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) { // we only care about loosing focus
                ValidateNumber(this.gluc,20.0,1200.0,0,"Glucemia");
            }
        });
        
        this.list.add(new CtrlCollection("DIABET", CtrlType.COMBO, this.diab,"DSiNo;CODSN;DESCSN"));
        this.diab.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) { // we only care about loosing focus
                System.out.println(this.diab.getValue().toString());
                //CheckValueIsInList(this.diab);
            }
        });
    }
    
    public Boolean ValidateNumber(TextField o, Double min, Double max, Integer dec, String name) {
        Boolean ok = true;
        if (!o.getText().trim().isEmpty()) {
            String c = o.getText();
            try {
                Double v = Double.parseDouble(c);
                if (v < min | v > max) {
                    showError(name.concat(" debe estar entre ").concat(min.toString()).concat(" y ").concat(max.toString()), "Error de validación");
                    o.requestFocus();
                    ok = false;
                } else if ((c.indexOf('.')>0) && (c.length() - c.indexOf('.') - 1)>dec) {
                    showError("El número de decimales debe ser ".concat(dec.toString()), "Error de validación");
                    o.requestFocus();
                    ok = false;
                }
            } catch (Exception e) {
                showError(name.concat(" debe ser numérico"), "Error de validación");
                o.requestFocus();
                ok = false;
            }
        }
        return ok;
    }
    
    public void CheckValueIsInList(ComboBox o) {
        if (o.getValue() != null) {
            String c = o.getValue().toString();
            if (!o.getItems().contains(c)) {
                System.out.println("error");
                showError("El valor no está en la lista admitida", "Error de validación");
                o.requestFocus();
            } else {
                System.out.println("ok");
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
                }
            }
        } catch (Exception e) {
            showError(e.getMessage(),"Error obteniendo visita");
        }
    }
    
    public void computeIMC() {
        if (!this.peso.getText().trim().isEmpty()) {
            Double p = Double.parseDouble(this.peso.getText());
            Double IMC =(double) Math.round(p / Math.pow(this.talla,2) * 100) / 100;
            this.imc.setText(IMC.toString());
        }
    }
    
    public void showError(String message, String header) {
        Alert a = new Alert(Alert.AlertType.WARNING, message);
        a.setTitle("Error");
        a.setHeaderText(header);
        a.showAndWait();
    }
}
