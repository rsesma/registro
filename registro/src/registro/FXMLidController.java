/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package registro;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import registro.model.Paciente;
import registro.model.getRegistroData;

/**
 * FXML Controller class
 *
 * @author R
 */
public class FXMLidController implements Initializable {

    @FXML
    private TextField search;
    @FXML
    private Label total;
    @FXML
    private Button pbSearch;
    @FXML
    private Button pbNoFilter;
    @FXML
    private TableView table;
    @FXML
    private TableColumn idCol;
    @FXML
    private TableColumn nomCol;
    
    public Integer count;
    public getRegistroData d;
    public Boolean lContinue;
    public String id;
    
    final ObservableList<Paciente> listaData = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        lContinue = false;
        id = "";
        
        // Set up the alumnos table
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        
        table.setItems(listaData);
        
        Image imgSearch = new Image(getClass().getResourceAsStream("search.png"));
	ImageView imgView = new ImageView(imgSearch);
	imgView.setFitWidth(15);
	imgView.setFitHeight(15);
        pbSearch.setGraphic(imgView);
        
        Image imgNoFilter = new Image(getClass().getResourceAsStream("no_filter.png"));
	ImageView imgView2 = new ImageView(imgNoFilter);
	imgView2.setFitWidth(15);
	imgView2.setFitHeight(15);
        pbNoFilter.setGraphic(imgView2);
    }
    
    @FXML
    private void searchFired(ActionEvent event) {
        if (search.getText().length()>0) {
            String c = search.getText();
            String filter = "IDPAC = '" + c + "' OR NOMBRE LIKE '%" + c + "%'";
            
            listaData.removeAll(listaData);
            LoadTable(filter);
        }
    }

    @FXML
    private void noFilterFired(ActionEvent event) {
        listaData.removeAll(listaData);
        LoadTable("");
    }
    
    @FXML
    private void cancelFired(ActionEvent event) {
        closeWindow();
    }

    @FXML
    private void editFired(ActionEvent event) {
        Paciente p = (Paciente) table.getSelectionModel().getSelectedItem();
        if (p != null) {
            id = p.getId();
            lContinue = true;
            
            try {
                FXMLLoader fxmlCensal;
                fxmlCensal = new FXMLLoader(getClass().getResource("FXMLCensal.fxml")); 
                Parent rCensal = (Parent) fxmlCensal.load(); 

                Stage stage = new Stage(); 
                stage.initModality(Modality.APPLICATION_MODAL); 
                stage.setTitle("Censal");
                stage.setScene(new Scene(rCensal));

                FXMLCensalController censal = fxmlCensal.<FXMLCensalController>getController();
                censal.SetData(id, true, d);

                stage.showAndWait();
                
                if (censal.changed) {
                    ResultSet rs = d.getListaIdRs("IDPAC = ".concat(censal.updatedID));
                    if (rs.next()){
                        Paciente edited = new Paciente();
                        edited.id.set(rs.getString("IDPAC"));
                        edited.nom.set(rs.getString("NOMBRE"));
                        
                        // remove old selected item and add edited one
                        this.listaData.remove(p);
                        this.listaData.add(edited);
                        
                        SortAndSelect(edited);
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @FXML
    private void deleteFired(ActionEvent event) {
        Paciente p = (Paciente) table.getSelectionModel().getSelectedItem();
        if (p != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Se borrará el paciente seleccionado y todos los registros relacionados.\n\n ¿Desea continuar?");
            confirm.setTitle("Eliminar");
            confirm.setHeaderText("Confirmar eliminación");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.get() == ButtonType.OK) {
                try {
                    if (this.d.deleteCensalbyID(p.getId())) {
                        listaData.remove(p);
                        count--;
                        total.setText(count + " paciente(s)");
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }               
            }
        }
    }
    
    @FXML
    private void newFired(ActionEvent event) {
        try {
            FXMLLoader fxmlCensal;
            fxmlCensal = new FXMLLoader(getClass().getResource("FXMLCensal.fxml")); 
            Parent rCensal = (Parent) fxmlCensal.load(); 

            Stage stage = new Stage(); 
            stage.initModality(Modality.APPLICATION_MODAL); 
            stage.setTitle("Censal");
            stage.setScene(new Scene(rCensal));

            FXMLCensalController censal = fxmlCensal.<FXMLCensalController>getController();
            censal.SetData("", false, d);

            stage.showAndWait();
            
            if (censal.changed) {
                ResultSet rs = d.getListaIdRs("IDPAC = ".concat(censal.updatedID));
                if (rs.next()){
                    Paciente edited = new Paciente();
                    edited.id.set(rs.getString("IDPAC"));
                    edited.nom.set(rs.getString("NOMBRE"));

                    // add new item
                    this.listaData.add(edited);
                    SortAndSelect(edited);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void LoadTable(String filter) {
        count = 0;
        try{
            ResultSet rs = d.getListaIdRs(filter);
            while(rs.next()){
                Paciente p = new Paciente();
                
                p.id.set(rs.getString("IDPAC"));
                p.nom.set(rs.getString("NOMBRE"));
                
                listaData.add(p);
                
                count++;
            }
            
            total.setText(count + " paciente(s)");
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
    
    public void SortAndSelect(Paciente item) {
        // sort list and select item
        Comparator<Paciente> comparator = Comparator.comparing(Paciente::getId); 
        FXCollections.sort(listaData, comparator);

        if (item != null) {
            table.getSelectionModel().select(item);
            table.scrollTo(item);
        } else {
            table.getSelectionModel().selectFirst();
            table.scrollTo(1);
        }
    }

    
    public void SetData(getRegistroData data) {
        this.d = data;
        LoadTable("");
    }
    
    private void closeWindow() {
        Stage stage = (Stage) this.pbSearch.getScene().getWindow();
        stage.close();
    }    
}
