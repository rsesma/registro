/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package registro;

import java.net.URL;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;
import registro.model.getRegistroData;

/**
 * FXML Controller class
 *
 * @author r
 */
public class FXMLeventosController implements Initializable {

    @FXML
    private TextField id;
    @FXML
    private DatePicker fecha;
    @FXML
    private TextField hh;
    @FXML
    private TextField mm;
    @FXML
    private ComboBox cie1;
    @FXML
    private ComboBox cie2;
    @FXML
    private TextArea notas;

    private getRegistroData d;
    private Boolean edit;
    private String idPac;
    public LocalDate date;
    public LocalTime time;
    public Boolean changed = false;
    
    /**
     * Initializes the controller class.
     */    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        this.hh.setOnKeyTyped((KeyEvent e) -> { 
            if (!"0123456789".contains(e.getCharacter())) {
                e.consume();
            } else {
                TextField o = (TextField) e.getSource();
                String c = o.getText() + e.getCharacter();
                if (!c.trim().isEmpty()) {
                    Integer h = Integer.parseInt(c);
                    if (h>24) e.consume();
                }
            }
        });
        this.mm.setOnKeyTyped((KeyEvent e) -> { 
            if (!"0123456789".contains(e.getCharacter())) {
                e.consume();
            } else {
                TextField o = (TextField) e.getSource();
                String c = o.getText() + e.getCharacter();
                if (!c.trim().isEmpty()) {
                    Integer h = Integer.parseInt(c);
                    if (h>59) e.consume();
                }
            }
        });
        
        this.cie1.valueProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue ov, String t, String t1) {
                loadCIE10S();
            }
        });
    }
    
    @FXML
    private void pbCancelar(ActionEvent event) {
        closeWindow();
    }

    @FXML
    private void pbAceptar(ActionEvent event) {
        
        // build date and time of the event form dialog data
        LocalDate f = this.fecha.getValue();
        LocalTime t = null;
        if (!this.hh.getText().trim().isEmpty() && !this.mm.getText().trim().isEmpty()) {
            DecimalFormat fmt = new DecimalFormat("00");
            String c = fmt.format(Integer.parseInt(this.hh.getText().trim()));
            c = c.concat(":").concat(fmt.format(Integer.parseInt(this.mm.getText().trim())));
            t = LocalTime.parse(c);
        }
        System.out.println(t);
        
        if (f == null) {
            showError("La fecha del evento no puede quedar vacía", "Identificador vacío");
            this.fecha.requestFocus();
        } else if (t == null) {
            showError("La hora del evento no puede quedar vacía", "Identificador vacío");
            this.hh.requestFocus();
        } else {
            Boolean exist = this.d.EventoExists(this.idPac, f, t);            
            if (!this.edit && exist) {
                showError("La fecha del evento ya existe", "Identificador duplicado");
                this.fecha.requestFocus();
            } else if (this.edit && (!this.date.equals(f) || !this.time.equals(t)) && exist) {
                showError("La fecha del evento ya existe", "Identificador duplicado");
                this.fecha.requestFocus();                
            } else {
                //AQUÍ ME QUEDO: VALIDAR CONTENIDO COMBOS Y GRABAR
                
                closeWindow();
            }
        }            
    }
    
    public void SetData(String id, String fechaHora, Boolean lEdit, getRegistroData data) {
        this.d = data;
        this.edit = lEdit;
        
        // original id value, load data
        this.idPac = id;     
        this.id.setText(this.idPac);
        
        try {
            // load CIE1 combobox
            ResultSet rs = this.d.getDic("CIE10G");
            while (rs.next()) {
                this.cie1.getItems().add(rs.getString("COD1").concat("  ").concat(rs.getString("DESC1")));
            }
            rs.close();
            TextFields.bindAutoCompletion(this.cie1.getEditor(), this.cie1.getItems());
            
            if (this.edit) {
                // original date/time value, load data
                this.date = LocalDate.parse(fechaHora.split(" ")[0], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                this.time = LocalTime.parse(fechaHora.split(" ")[1]);
                this.fecha.setValue(this.date);
                this.hh.setText(Integer.toString(this.time.getHour()));
                this.mm.setText(Integer.toString(this.time.getMinute()));
                
                rs = this.d.getEventos(this.idPac, this.date, this.time);
                if (rs.next()) {
                    this.cie1.setValue(rs.getString("CIE1") + "  " + rs.getString("DESC1"));
                    loadCIE10S();
                    this.cie2.setValue(rs.getString("CIE2") + "  " + rs.getString("DESC2"));
                }
                rs.close();
                
                String c = rs.getString("NOTASN");
                if (!rs.wasNull()) this.notas.setText(c);
            }
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void loadCIE10S() {
        try {
            this.cie2.getItems().clear();
            this.cie2.setValue("");
            String c = this.cie1.getEditor().getText();
            if (!c.trim().isEmpty()) {
                ResultSet rs = this.d.getCIE10S(c.substring(0,3));
                if (rs.next()) {
                    do {
                        this.cie2.getItems().add(rs.getString("COD2").concat("  ").concat(rs.getString("DESC2")));
                    } while (rs.next());
                }
                rs.close();
            }
            
            if (!this.cie2.getItems().isEmpty()) TextFields.bindAutoCompletion(this.cie2.getEditor(), this.cie2.getItems());
        } catch (Exception e) {
            System.out.println("error");
            System.out.println(e.getMessage());
        }
    }
    
    private void closeWindow() {
        Stage stage = (Stage) this.id.getScene().getWindow();
        stage.close();
    }
    
    public void showError(String message, String header) {
        Alert a = new Alert(Alert.AlertType.WARNING, message);
        a.setTitle("Error");
        a.setHeaderText(header);
        a.showAndWait();
    }
}
