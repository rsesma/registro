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
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;
import registro.model.CtrlCollection;
import registro.model.CtrlType;
import registro.model.Tratamiento;
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
    private TableView<Tratamiento> SFarm;

    
    private Boolean edit;
    private Boolean changed;
    private getRegistroData d;
    private Double talla;
    
    public List<CtrlCollection> list = new LinkedList<>();
    
    final ObservableList<Tratamiento> trat = FXCollections.observableArrayList();
    
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
        
        this.list.add(new CtrlCollection("PESO", CtrlType.TXT, this.peso,"20;200;1"));
        this.peso.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) computeIMC();
        });

        this.list.add(new CtrlCollection("GLUC", CtrlType.TXT, this.gluc,"20;1200;0"));
        this.list.add(new CtrlCollection("DIABET", CtrlType.COMBO, this.diab,"DSiNo;CODSN;DESCSN"));
        this.list.add(new CtrlCollection("MENOP", CtrlType.COMBO, this.menop,"DSiNo;CODSN;DESCSN"));
        
        this.list.add(new CtrlCollection("HE", CtrlType.TXT, this.he,"0;60;0"));
        this.list.add(new CtrlCollection("EJER", CtrlType.TXT, this.ejer,"0;50;0"));
        this.list.add(new CtrlCollection("DIETA", CtrlType.COMBO, this.dieta,"DCumpl;CODCMP;DESCMP"));
        
        this.list.add(new CtrlCollection("TABACO", CtrlType.TXT, this.tabaco,"0;80;0"));
        this.list.add(new CtrlCollection("TIPOTA", CtrlType.COMBO, this.marca,"DTabaco;IDMARC;MARCA"));
        this.marca.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) loadMarcaTab();
        });
        
        this.list.add(new CtrlCollection("PULSO", CtrlType.TXT, this.pulso,"5;300;0"));
        this.list.add(new CtrlCollection("TAMAX", CtrlType.TXT, this.pas,"50;200;0"));
        this.list.add(new CtrlCollection("TAMIN", CtrlType.TXT, this.pad,"50;150;0"));
        this.list.add(new CtrlCollection("CT", CtrlType.TXT, this.ct,"50;300;0"));
        this.ct.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) computeIA();
        });
        this.list.add(new CtrlCollection("CLDL", CtrlType.TXT, this.ldl,"50;300;0"));
        this.list.add(new CtrlCollection("CHDL", CtrlType.TXT, this.hdl,"20;200;0"));
        this.hdl.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) computeIA();
        });
        this.list.add(new CtrlCollection("TG", CtrlType.TXT, this.trig,"50;600;0"));

        SFarm.setItems(trat);
    }
    
    public void SetData(String id, Date fecha, Boolean lEdit, getRegistroData data) {
        this.d = data;
        this.edit = lEdit;
        
        try {
            // build Farmacos subform
            TableColumn<Tratamiento, String> farmCol = new TableColumn<>("Fármaco");
            farmCol.setCellValueFactory(cellData -> cellData.getValue().farmacoProperty());
            farmCol.setMinWidth(500);

            TableColumn<Tratamiento, String> cumplCol = new TableColumn<>("Cumplimiento");
            cumplCol.setCellValueFactory(cellData -> cellData.getValue().cumplimientoProperty());
            cumplCol.setMinWidth(100);            
            
            SFarm.getColumns().add(farmCol);
            SFarm.getColumns().add(cumplCol);
                        
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
                    computeIA();
                    loadMarcaTab();
                    
                    LoadFarmacos(rs.getString("IDPACV"),rs.getDate("FECHA"));
                }
            }
        } catch (Exception e) {
            showError(e.getMessage(),"Error obteniendo visita");
        }
    }
    
    @FXML
    private void editTratamiento(ActionEvent event) {
        if (!this.trat.isEmpty()) {
            Tratamiento t = SFarm.getSelectionModel().getSelectedItem();
            if (t != null) {
                NewEditTratamiento(true, t);
            } else {
                Alert wf = new Alert(Alert.AlertType.WARNING, "Seleccione un fármaco");
                wf.setTitle("Seleccionar");
                wf.setHeaderText("Falta información");
                wf.showAndWait();
            }
        }
    }

    @FXML
    private void newTratamiento(ActionEvent event) {
        NewEditTratamiento(false, null);
    }
    
    public void NewEditTratamiento(Boolean edit, Tratamiento t) {
        try {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL); 
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("FXMLFarmac.fxml")); 
            Parent rFarm = (Parent) fxml.load(); 
            stage.setTitle("Fármaco");
            stage.setScene(new Scene(rFarm));
            FXMLFarmacController farm = fxml.<FXMLFarmacController>getController();
            if (edit) farm.SetData(t.getFarm(), t.getCumpl(), true, d);
            else farm.SetData("", "", false, d);

            stage.showAndWait();

            if (farm.changed) {
                // edited item to update
                Tratamiento edited = new Tratamiento(farm.farm, farm.descFarm, farm.cumpl, farm.descCumpl);

                // remove old selected item and add edited one
                if (edit) this.trat.remove(t);
                this.trat.add(edited);

                SortAndSelect(edited);
            }
        } catch (Exception e) {
            showError(e.getMessage(),"Error abriendo la ventana");
        }
    }

    @FXML
    private void delTratamiento(ActionEvent event) {
        if (!this.trat.isEmpty()) {
            Tratamiento t = SFarm.getSelectionModel().getSelectedItem();
            if (t != null) {
                this.trat.remove(t);
            } else {
                Alert wf = new Alert(Alert.AlertType.WARNING, "Seleccione el fármaco a eliminar");
                wf.setTitle("Seleccionar");
                wf.setHeaderText("Falta información");
                wf.showAndWait();
            }
        }
    }

    @FXML
    private void cancel(ActionEvent event) {
        closeWindow();
    }

    @FXML
    private void ok(ActionEvent event) {
        this.changed = true;
        
        closeWindow();
    }
    
    public void LoadFarmacos(String id, Date fecha) {
        try {
            ResultSet rs = d.getFarmacosByIdFecha(id,fecha);
            while (rs.next()) {
                trat.add(new Tratamiento(rs.getString("FARMAC"),rs.getString("FARM"),rs.getString("CUMPLI"),rs.getString("DESCMP")));
            }
            
        } catch (Exception e) {
            showError(e.getMessage(),"Error obteniendo fármaco");
        }
    }
    
    public void computeIMC() {
        if (!this.peso.getText().trim().isEmpty()) {
            try {
                Double p = Double.parseDouble(this.peso.getText());
                Double IMC =(double) Math.round((p / Math.pow(this.talla,2)) * 100) / 100;
                this.imc.setText(IMC.toString());
            } catch (Exception e) {
                this.imc.setText("");
            }
        } else {
            this.imc.setText("");
        }
    }
    
    public void computeIA() {
        if (!this.ct.getText().trim().isEmpty() & !this.hdl.getText().trim().isEmpty()) {
            try {
                Double ctotal = Double.parseDouble(this.ct.getText());
                Double chdl = Double.parseDouble(this.hdl.getText());
                Double IA =(double) Math.round((ctotal / chdl) * 100) / 100;
                this.ia.setText(IA.toString());
            } catch (Exception e) {
                this.ia.setText("");
            }
        } else {
            this.ia.setText("");
        }
    }
    
    public void loadMarcaTab() {
        if (this.marca.getValue() != null) {
            String c = this.marca.getEditor().getText();
            if (c.trim().isEmpty()) {
                c = this.marca.getValue().toString();
            }
            
            try {
                ResultSet rs = d.getDatosMarcaTab(c);
                if (rs.next()) {
                    this.alq.setText(rs.getString("ALQUIT"));
                    this.nic.setText(rs.getString("NICOT"));
                    this.co.setText(rs.getString("CO"));
                } else {
                    this.alq.setText("");
                    this.nic.setText("");
                    this.co.setText("");
                }
            } catch (Exception e) {
                showError(e.getMessage(),"Error cargando el diccionario");
            }
        } else {
            this.alq.setText("");
            this.nic.setText("");
            this.co.setText("");            
        }
    }
    
    public void SortAndSelect(Tratamiento item) {
        // sort list and select item
        Comparator<Tratamiento> comparator = Comparator.comparing(Tratamiento::getFarmaco); 
        FXCollections.sort(trat, comparator);

        if (item != null) {
            SFarm.getSelectionModel().select(item);
            SFarm.scrollTo(item);
        } else {
            SFarm.getSelectionModel().selectFirst();
            SFarm.scrollTo(1);
        }
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
    
    private void closeWindow() {
        Stage stage = (Stage) this.SFarm.getScene().getWindow();
        stage.close();
    }
    
    public void showError(String message, String header) {
        Alert a = new Alert(Alert.AlertType.WARNING, message);
        a.setTitle("Error");
        a.setHeaderText(header);
        a.showAndWait();
    }
}
