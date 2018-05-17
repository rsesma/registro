/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package registro;

import java.net.URL;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import registro.model.Dic;
import registro.model.Farmaco;
import registro.model.getRegistroData;

/**
 * FXML Controller class
 *
 * @author r
 */
public class FXMLFarmacController implements Initializable {

    @FXML
    private TextField farmac;
    @FXML
    private Button pbSearch;
    @FXML
    private ComboBox<Dic> cumpli;
    @FXML
    private TableView<Farmaco> table;

    private Boolean edit;
    private getRegistroData d;
    
    public Boolean changed;
    public String farm;
    public String descFarm;
    public String cumpl;
    public String descCumpl;
    
    final ObservableList<Farmaco> farmacos = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        this.changed = false;
        
        this.farm = null;
        this.descFarm = null;
        this.cumpl = null;
        this.descCumpl = null;

        
        Image imgSearch = new Image(getClass().getResourceAsStream("search.png"));
	ImageView imgView = new ImageView(imgSearch);
	imgView.setFitWidth(15);
	imgView.setFitHeight(15);
        pbSearch.setGraphic(imgView);

        // cumplimiento comboBox
        cumpli.setConverter(new StringConverter<Dic>() {
            @Override
            public String toString(Dic d) {
                if (d != null) return d.getDescrip();
                else return "";
            }

            @Override
            public Dic fromString(String string) { return null; }
        });
        
        // build Farmacos subform
        TableColumn<Farmaco, String> codCol = new TableColumn<>("Código");
        codCol.setCellValueFactory(cellData -> cellData.getValue().codProperty());
        codCol.setMinWidth(70);

        TableColumn<Farmaco, String> descripCol = new TableColumn<>("");
        descripCol.setCellValueFactory(cellData -> cellData.getValue().descripProperty());
        descripCol.setMinWidth(800);            

        table.getColumns().add(codCol);
        table.getColumns().add(descripCol);
        table.setItems(farmacos);
    }
    
    @FXML
    private void searchFarm(ActionEvent event) {
        if (farmac.getText().length()>0) {
            try {
                farmacos.removeAll(farmacos);
                
                ResultSet rs = d.getFarmacByDescrip(farmac.getText());
                while (rs.next()) {
                    farmacos.add(new Farmaco(rs.getString("IDFARM"),rs.getString("FARM")));
                }
            } catch (Exception e) {
                showError(e.getMessage(),"Error buscando el fármaco");
            }
        }
    }
    
    @FXML
    private void pbCancelar(ActionEvent event) {
        closeWindow();
    }

    @FXML
    private void pbAceptar(ActionEvent event) {
        this.changed = true;
        Farmaco f = table.getSelectionModel().getSelectedItem();
        if (f!=null) {
            this.farm = f.getCod();
            this.descFarm = f.getDescrip();
            Dic d = cumpli.getSelectionModel().getSelectedItem();
            if (d!=null) {
                this.cumpl = d.getCod();
                this.descCumpl = d.getDescrip();
            }
            
            closeWindow();
        } else {
            Alert wf = new Alert(Alert.AlertType.WARNING, "Seleccione un fármaco");
            wf.setTitle("Seleccionar");
            wf.setHeaderText("Falta información");
            wf.showAndWait();
        }
    }
    
    public void SetData(String farm, String cumpl, Boolean lEdit, getRegistroData data) {
        this.d = data;
        this.edit = lEdit;
        
        try {
            ResultSet rsDic = d.getDic("DCumpl");
            while (rsDic.next()) {
                cumpli.getItems().add(new Dic(rsDic.getString("CODCMP"), rsDic.getString("DESCMP")));
            }
            rsDic.close();
            
            if (this.edit) {
                ResultSet rs = d.getFarmacById(farm);
                if (rs.next()) {
                    farmacos.add(new Farmaco(rs.getString("IDFARM"),rs.getString("FARM")));
                    this.farmac.setText(rs.getString("FARM"));
                    
                    table.getSelectionModel().selectFirst();
                    table.scrollTo(1);
                    
                    if (cumpl!=null) {
                        cumpli.setValue(new Dic(cumpl, d.getDescripFromCod("DCumpl", "CODCMP", Integer.parseInt(cumpl), "DESCMP")));
                    }
                }
            }
        } catch (Exception e) {
            showError(e.getMessage(),"Error obteniendo los datos");
        }
    }
    
    private void closeWindow() {
        Stage stage = (Stage) this.pbSearch.getScene().getWindow();
        stage.close();
    }

    public void showError(String message, String header) {
        Alert a = new Alert(Alert.AlertType.WARNING, message);
        a.setTitle("Error");
        a.setHeaderText(header);
        a.showAndWait();
    }
}
