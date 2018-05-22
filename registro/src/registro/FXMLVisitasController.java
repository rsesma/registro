/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package registro;

import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import registro.model.Controles;
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
    public Boolean changed = false;
    private getRegistroData d;
    private Double talla;
    private LocalDate fentr;
    private LocalDate fnac;
    private Boolean farmChanged = false;

    public String idPac;
    public Date fVisita;
    
    private Controles c = new Controles();
    
    final ObservableList<Tratamiento> trat = FXCollections.observableArrayList();
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        this.c.add("IDPACV", Controles.TipoCtrl.TXT, this.id,"3,n,0",true,false);
        this.c.add("FECHA", Controles.TipoCtrl.DATE, this.fecha,"",true,false);
        this.fecha.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) computeEdad();
        });
        this.c.add("PESO", Controles.TipoCtrl.TXT, this.peso,"0,n,20;200;1",false,false);
        this.peso.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) computeIMC();
        });
        this.c.add("GLUC", Controles.TipoCtrl.TXT, this.gluc,"0,n,20;1200;0",false,false);
        this.c.add("DIABET", Controles.TipoCtrl.COMBO, this.diab,"DSiNo;CODSN;DESCSN",false,false);
        this.c.add("MENOP", Controles.TipoCtrl.COMBO, this.menop,"DSiNo;CODSN;DESCSN",false,false);
        this.c.add("HE", Controles.TipoCtrl.TXT, this.he,"0,n,0;60;0",false,false);
        this.c.add("EJER", Controles.TipoCtrl.TXT, this.ejer,"0,n,0;50;0",false,false);
        this.c.add("DIETA", Controles.TipoCtrl.COMBO, this.dieta,"DCumpl;CODCMP;DESCMP",false,false);
        this.c.add("TABACO", Controles.TipoCtrl.TXT, this.tabaco,"0,n,0;80;0",false,false);
        this.c.add("TIPOTA", Controles.TipoCtrl.COMBO, this.marca,"DTabaco;IDMARC;MARCA",false,false);
        this.marca.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) loadMarcaTab();
        });
        this.c.add("PULSO", Controles.TipoCtrl.TXT, this.pulso,"0,n,5;300;0",false,false);
        this.c.add("TAMAX", Controles.TipoCtrl.TXT, this.pas,"0,n,50;200;0",false,false);
        this.c.add("TAMIN", Controles.TipoCtrl.TXT, this.pad,"0,n,50;150;0",false,false);
        this.c.add("CT", Controles.TipoCtrl.TXT, this.ct,"0,n,50;300;0",false,false);
        this.ct.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) computeIA();
        });
        this.c.add("CLDL", Controles.TipoCtrl.TXT, this.ldl,"0,n,50;300;0",false,false);
        this.c.add("CHDL", Controles.TipoCtrl.TXT, this.hdl,"0,n,20;200;0",false,false);
        this.hdl.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) computeIA();
        });
        this.c.add("TG", Controles.TipoCtrl.TXT, this.trig,"0,n,20;600;0",false,false);

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
            
            // original id values
            this.idPac = id;
            this.fVisita = fecha;

            ResultSet rsC = this.d.getCensalRs(id);
            if (rsC.next()) {
                this.nom.setText(rsC.getString("NOMBRE") + " " + rsC.getString("APE1") + " " + rsC.getString("APE2"));
                this.talla = rsC.getDouble("TALLA");
                this.fentr = rsC.getDate("FENTR").toLocalDate();
                this.fnac = rsC.getDate("fnac").toLocalDate();
            }
            rsC.close();
            
            if (this.edit) {
                ResultSet rs = this.d.getVisitasByIdFecha(id, fecha);
                this.c.loadData(rs, this.d);

                rs.close();
                
                computeEdad();
                computeIMC();
                computeIA();
                loadMarcaTab();
                
                LoadFarmacos(rs.getString("IDPACV"),rs.getDate("FECHA"));
            } else {
                this.id.setText(this.idPac);
                this.edad.setText("");
                this.c.loadData(null, this.d);
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
                this.farmChanged = true;
                
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
                this.farmChanged = true;
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
    private void pbCancelar(ActionEvent event) {
        closeWindow();
    }

    @FXML
    private void pbAceptar(ActionEvent event) {
        
        // check identifier is right
        LocalDate f = this.fecha.getValue();
        if (f == null) {
            showError("La fecha de visita no puede quedar vacía", "Identificador vacío");
            this.fecha.requestFocus();
        } else if (!this.edit && this.d.VisitaExists(this.idPac,f)) {
            showError("La fecha de visita ya existe", "Identificador duplicado");
            this.fecha.requestFocus();
        } else if (this.edit && !this.fVisita.equals(java.sql.Date.valueOf(f)) && this.d.VisitaExists(this.idPac,f)) {
            showError("La fecha de visita ya existe", "Identificador duplicado");
            this.fecha.requestFocus();
        } else if (f.isBefore(fentr)) {
            showError("La fecha de visita debe ser posterior a la fecha de entrada", "Error de validación campo FECHA");
            this.fecha.requestFocus();
        } else {
            // validate fields
            Boolean ok = (this.c.validateData());
            
            // postval pas/pad
            if (!this.pas.getText().trim().isEmpty() && !this.pad.getText().trim().isEmpty()) {
                Integer sis = Integer.parseInt(this.pas.getText());
                Integer dia = Integer.parseInt(this.pad.getText());
                if (dia >= sis) {
                    ok = false;
                    showError("La presión arterial sistólica debe ser mayor que la diastólica", "Error de validación");
                    this.pas.requestFocus();
                }
            }
            
            if (ok) {
                if (this.edit && !this.fVisita.equals(java.sql.Date.valueOf(f))) {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Ha cambiado la fecha de la visita.\n\n ¿Desea continuar?");
                    confirm.setTitle("Confirmar");
                    confirm.setHeaderText("Ha cambiado el identificador");
                    Optional<ButtonType> result = confirm.showAndWait();
                    ok = (result.get() == ButtonType.OK);
                }
                
                // update or add Visitas row
                String where = "IDPACV = ".concat(this.idPac);
                where = where.concat(" AND FECHA = '".concat(this.fVisita.toString())).concat("'");
                if (this.edit) this.d.update("Visitas",this.c,where);
                else this.d.add("Visitas",this.c);

                this.changed = true;
                this.fVisita = java.sql.Date.valueOf(f);
                
                // update Farmacos
                if (this.farmChanged) this.d.updateFarmacosByIdFecha(this.idPac, this.fVisita, trat);
                
                closeWindow();
            }
        }
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
    
    public void computeEdad() {
        if (this.fecha.getValue()!=null) {
            Long age = ChronoUnit.YEARS.between(this.fnac,this.fecha.getValue());
            this.edad.setText(age.toString().concat(" años"));
        } else {
            this.edad.setText("");
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
