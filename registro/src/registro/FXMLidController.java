/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package registro;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @FXML
    private void deleteFired(ActionEvent event) {
        Paciente p = (Paciente) table.getSelectionModel().getSelectedItem();
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
    
    public void SetData(getRegistroData data) {
        this.d = data;
        LoadTable("");
    }
    
    private void closeWindow() {
        Stage stage = (Stage) this.pbSearch.getScene().getWindow();
        stage.close();
    }    
}
