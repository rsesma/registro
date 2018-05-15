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
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.TextFields;
import registro.model.CtrlCollection;
import registro.model.CtrlType;
import registro.model.Farmaco;
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
    @FXML
    private TextField pulso;
    @FXML
    private TextField pas;
    @FXML
    private TextField pad;
    @FXML
    private TextField ct;
    @FXML
    private TextField ldl;
    @FXML
    private TextField hdl;
    @FXML
    private TextField ia;
    @FXML
    private TextField trig;
    @FXML
    private TableView<Farmaco> SFarm;

    
    private Boolean edit;
    private Boolean changed;
    private getRegistroData d;
    private Double talla;
    
    public List<CtrlCollection> list = new LinkedList<>();
    
    private List<Map.Entry<String,Long>> cumplList = new ArrayList<>();
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        this.changed = false;
        String valid = "0123456789";
        String vdec = "0123456789.";
        
        this.list.add(new CtrlCollection("IDPACV", CtrlType.TXT, this.id,""));
        this.list.add(new CtrlCollection("FECHA", CtrlType.DATE, this.fecha,""));
        
        this.list.add(new CtrlCollection("PESO", CtrlType.TXT, this.peso,""));
        this.peso.setOnKeyTyped((KeyEvent e) -> { if (!vdec.contains(e.getCharacter())) e.consume(); });
        this.peso.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (ValidateNumber(this.peso, 20.0, 200.0, 1)) computeIMC();
            }
        });
        
        this.list.add(new CtrlCollection("GLUC", CtrlType.TXT, this.gluc,""));
        this.gluc.setOnKeyTyped((KeyEvent e) -> { if (!valid.contains(e.getCharacter())) e.consume(); });
        this.gluc.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) ValidateNumber(this.gluc, 20.0, 1200.0, 0);
        });
        
        this.list.add(new CtrlCollection("DIABET", CtrlType.COMBO, this.diab,"DSiNo;CODSN;DESCSN"));
        this.diab.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) CheckValueIsInList(this.diab);

        });

        this.list.add(new CtrlCollection("MENOP", CtrlType.COMBO, this.menop,"DSiNo;CODSN;DESCSN"));
        this.menop.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) CheckValueIsInList(this.menop);
        });
        
        this.list.add(new CtrlCollection("HE", CtrlType.TXT, this.he,""));
        this.he.setOnKeyTyped((KeyEvent e) -> { if (!valid.contains(e.getCharacter())) e.consume(); });
        this.he.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) ValidateNumber(this.he, 0.0, 60.0, 0);
        });

        this.list.add(new CtrlCollection("EJER", CtrlType.TXT, this.ejer,""));
        this.ejer.setOnKeyTyped((KeyEvent e) -> { if (!valid.contains(e.getCharacter())) e.consume(); });
        this.ejer.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) ValidateNumber(this.ejer, 0.0, 50.0, 0);
        });


        this.list.add(new CtrlCollection("DIETA", CtrlType.COMBO, this.dieta,"DCumpl;CODCMP;DESCMP"));
        this.dieta.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) CheckValueIsInList(this.dieta);
        });
        
        this.list.add(new CtrlCollection("TABACO", CtrlType.TXT, this.tabaco,""));
        this.tabaco.setOnKeyTyped((KeyEvent e) -> { if (!valid.contains(e.getCharacter())) e.consume(); });
        this.tabaco.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) ValidateNumber(this.tabaco, 0.0, 80.0, 0);
        });


        this.list.add(new CtrlCollection("TIPOTA", CtrlType.COMBO, this.marca,"DTabaco;IDMARC;MARCA"));
        this.marca.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) { // we only care about loosing focus
                CheckValueIsInList(this.marca);
                loadMarcaTab();
            }
        });
        
        this.list.add(new CtrlCollection("PULSO", CtrlType.TXT, this.pulso,""));
        this.pulso.setOnKeyTyped((KeyEvent e) -> { if (!valid.contains(e.getCharacter())) e.consume(); });
        this.pulso.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) ValidateNumber(this.pulso, 5.0, 300.0, 0);
        });

        this.list.add(new CtrlCollection("TAMAX", CtrlType.TXT, this.pas,""));
        this.pas.setOnKeyTyped((KeyEvent e) -> { if (!valid.contains(e.getCharacter())) e.consume(); });
        this.pas.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) ValidateNumber(this.pas, 50.0, 200.0, 0);
        });
        
        this.list.add(new CtrlCollection("TAMIN", CtrlType.TXT, this.pad,""));
        this.pad.setOnKeyTyped((KeyEvent e) -> { if (!valid.contains(e.getCharacter())) e.consume(); });
        this.pad.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) ValidateNumber(this.pad, 50.0, 150.0, 0);
        });

        this.list.add(new CtrlCollection("CT", CtrlType.TXT, this.ct,""));
        this.ct.setOnKeyTyped((KeyEvent e) -> { if (!valid.contains(e.getCharacter())) e.consume(); });
        this.ct.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (ValidateNumber(this.ct, 50.0, 300.0, 0)) computeIA();
            }
        });
        
        this.list.add(new CtrlCollection("CLDL", CtrlType.TXT, this.ldl,""));
        this.ldl.setOnKeyTyped((KeyEvent e) -> { if (!valid.contains(e.getCharacter())) e.consume(); });
        this.ldl.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) ValidateNumber(this.ldl, 50.0, 300.0, 0);
        });

        this.list.add(new CtrlCollection("CHDL", CtrlType.TXT, this.hdl,""));
        this.hdl.setOnKeyTyped((KeyEvent e) -> { if (!valid.contains(e.getCharacter())) e.consume(); });
        this.hdl.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (ValidateNumber(this.hdl, 20.0, 200.0, 0)) computeIA();
            }
        });

        this.list.add(new CtrlCollection("TG", CtrlType.TXT, this.trig,""));
        this.trig.setOnKeyTyped((KeyEvent e) -> { if (!valid.contains(e.getCharacter())) e.consume(); });
        this.trig.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) ValidateNumber(this.trig, 50.0, 600.0, 0);
        });
    }

    public Boolean ValidateNumber(TextField o, Double min, Double max, Integer dec) {
        Boolean ok = true;
        String c = o.getText();
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
            if (!ok) {
                o.undo();
                o.requestFocus();
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
    
    class EntryStringConverter extends StringConverter {
        @Override
        public String toString(Object object) {
            return object.toString();
        }
        
        @Override
        public Entry fromString(String string) {
            return null;
        }
    }
    
    public void SetData(String id, Date fecha, Boolean lEdit, getRegistroData data) {
        this.d = data;
        this.edit = lEdit;
        
        try {
            // build DCumpl dictionary
            ResultSet rsCumpli = d.getDic("DCumpl");
            while (rsCumpli.next()) {
                cumplList.add(new SimpleEntry<>(rsCumpli.getString("DESCMP"),rsCumpli.getLong("CODCMP")));
            }
            rsCumpli.close();

/*            // build Farmacos subform
            TableColumn<Farmaco, Entry> cumplCol = new TableColumn<>("Cumplimiento");
            cumplCol.setCellValueFactory(cellData -> cellData.getValue().cumplProperty());
            cumplCol.setCellFactory(ComboBoxTableCell.forTableColumn(new EntryStringConverter(), cumplList));
            cumplCol.
            SFarm.getColumns().add(cumplCol);
            
            // populate Farmacos subform
            SFarm.getItems().addAll(
                new Farmaco("Bueno",Long.getLong("2")),
                new Farmaco("Malo",Long.getLong("0")),
                new Farmaco("Aceptable",Long.getLong("1"))
            );*/
            
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
        } else {
            this.imc.setText("");
        }
    }
    
    public void computeIA() {
        if (!this.ct.getText().trim().isEmpty() & !this.hdl.getText().trim().isEmpty()) {
            Double ctotal = Double.parseDouble(this.ct.getText());
            Double chdl = Double.parseDouble(this.hdl.getText());
            Double IA =(double) Math.round((ctotal / chdl) * 100) / 100;
            this.ia.setText(IA.toString());
        } else {
            this.ia.setText("");
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
