/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package registro;

import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import registro.FXMLregistroController.Forms;
import registro.model.Paciente;
import registro.model.getRegistroData;

/**
 * FXML Controller class
 *
 * @author R
 */
public class FXMLidFechaController implements Initializable {

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
    @FXML
    private Label txFecha;
    @FXML
    private ListView fecha;
    
    public Integer count;
    public getRegistroData d;
    public Forms form;
    
    final ObservableList<Paciente> listaData = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        // Set up registro table
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
        
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Paciente sel = (Paciente) table.getSelectionModel().getSelectedItem();
                LoadFechas(sel.getId());
            }
        });
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
        this.fecha.getItems().clear();
    }
    
    @FXML
    private void cancelFired(ActionEvent event) {
        closeWindow();
    }

    @FXML
    private void editFired(ActionEvent event) {
        Paciente p = (Paciente) table.getSelectionModel().getSelectedItem();

        if (p != null) {
            Date f = (Date) this.fecha.getSelectionModel().getSelectedItem();
            if (f != null) {
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL); 
                FXMLLoader fxml;

                try {
                    switch (this.form) {
                        case VISITAS:
                            fxml = new FXMLLoader(getClass().getResource("FXMLVisitas.fxml")); 
                            Parent rVisitas = (Parent) fxml.load(); 
                            stage.setTitle("Visitas");
                            stage.setScene(new Scene(rVisitas));
                            FXMLVisitasController visitas = fxml.<FXMLVisitasController>getController();
                            visitas.SetData(p.getId(), f, true, d);

                            stage.showAndWait();

                            if (visitas.changed) {
                                LoadFechas(visitas.idPac);
                                this.fecha.getSelectionModel().select(visitas.fVisita);
                            }
                            
                            break;
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } else {
                Alert wf = new Alert(Alert.AlertType.WARNING, "Seleccione una visita");
                wf.setTitle("Seleccionar");
                wf.setHeaderText("Falta información");
                wf.showAndWait();
            }
        } else {
            Alert wp = new Alert(Alert.AlertType.WARNING, "Seleccione un paciente");
            wp.setTitle("Seleccionar");
            wp.setHeaderText("Falta información");
            wp.showAndWait();
        }
    }
    
    @FXML
    private void newFired(ActionEvent event) {
        Paciente p = (Paciente) table.getSelectionModel().getSelectedItem();
        if (p != null) {
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL); 
                FXMLLoader fxml;

                try {
                    switch (this.form) {
                        case VISITAS:
                            fxml = new FXMLLoader(getClass().getResource("FXMLVisitas.fxml")); 
                            Parent rVisitas = (Parent) fxml.load(); 
                            stage.setTitle("Visitas");
                            stage.setScene(new Scene(rVisitas));
                            FXMLVisitasController visitas = fxml.<FXMLVisitasController>getController();
                            visitas.SetData(p.getId(), null, false, d);

                            stage.showAndWait();

                            if (visitas.changed) {
                                LoadFechas(visitas.idPac);
                                this.fecha.getSelectionModel().select(visitas.fVisita);
                            }
                            
                            break;
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

        } else {
            Alert wp = new Alert(Alert.AlertType.WARNING, "Seleccione un paciente");
            wp.setTitle("Seleccionar");
            wp.setHeaderText("Falta información");
            wp.showAndWait();
        }
    }

    @FXML
    private void deleteFired(ActionEvent event) {
        Paciente p = (Paciente) table.getSelectionModel().getSelectedItem();
        if (p != null) {
/*            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Se borrará el paciente seleccionado y todos los registros relacionados.\n\n ¿Desea continuar?");
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
            }*/
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
    
    public void LoadFechas(String id) {
        this.fecha.getItems().clear();
        try {
            ResultSet rs = this.d.getFechasById(id, this.form);
            while(rs.next()){
                this.fecha.getItems().add(rs.getDate("FECHA"));
            }
        } catch(Exception e) {
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
    
    public void SetData(getRegistroData data, Forms f) {
        this.d = data;
        this.form = f;
        LoadTable("");
        switch (this.form) {
            case VISITAS:
                this.txFecha.setText("Visita:");
                break;
        }
    }
    
    private void closeWindow() {
        Stage stage = (Stage) this.pbSearch.getScene().getWindow();
        stage.close();
    }    
}
