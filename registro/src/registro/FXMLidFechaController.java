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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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
        Boolean ok = false;
        Date fvis = null;
        String fechaHora = "";

        Paciente p = (Paciente) table.getSelectionModel().getSelectedItem();        
        if (p != null) {
            if (this.form == Forms.VISITAS) {
                fvis = (Date) this.fecha.getSelectionModel().getSelectedItem();
                ok = (fvis!=null);
            }
            if (this.form == Forms.EVENTOS) {
                fechaHora = (String) this.fecha.getSelectionModel().getSelectedItem();
                ok = (fechaHora!=null);
            }
            
            if (ok) {
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
                            visitas.SetData(p.getId(), fvis, true, d);

                            stage.showAndWait();

                            if (visitas.changed) {
                                LoadFechas(visitas.idPac);
                                this.fecha.getSelectionModel().select(visitas.fVisita);
                            }
                            
                            break;
                        case EVENTOS:
                            fxml = new FXMLLoader(getClass().getResource("FXMLeventos.fxml")); 
                            Parent rEventos = (Parent) fxml.load(); 
                            stage.setTitle("Eventos");
                            stage.setScene(new Scene(rEventos));
                            FXMLeventosController eventos = fxml.<FXMLeventosController>getController();
                            eventos.SetData(p.getId(), fechaHora, true, d);

                            stage.showAndWait();

                            if (eventos.changed) {
                                LoadFechas(eventos.idPac);
                                SimpleDateFormat formatter = new SimpleDateFormat("HH:m");
                                this.fecha.getSelectionModel().select(eventos.date.toString() + " " + formatter.format(java.sql.Time.valueOf(eventos.time)));
                            }
                            
                            break;
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } else {
                Alert wf = null;
                if (this.form==Forms.VISITAS) wf = new Alert(Alert.AlertType.WARNING, "Seleccione una visita");
                if (this.form==Forms.EVENTOS) wf = new Alert(Alert.AlertType.WARNING, "Seleccione un evento");
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
                        case EVENTOS:
                            fxml = new FXMLLoader(getClass().getResource("FXMLeventos.fxml")); 
                            Parent rEventos = (Parent) fxml.load(); 
                            stage.setTitle("Eventos");
                            stage.setScene(new Scene(rEventos));
                            FXMLeventosController eventos = fxml.<FXMLeventosController>getController();
                            eventos.SetData(p.getId(), "", false, d);

                            stage.showAndWait();

                            if (eventos.changed) {
                                LoadFechas(eventos.idPac);
                                SimpleDateFormat formatter = new SimpleDateFormat("HH:m");
                                this.fecha.getSelectionModel().select(eventos.date.toString() + " " + formatter.format(java.sql.Time.valueOf(eventos.time)));
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
            try {
                switch (this.form) {
                    case VISITAS:
                        Date f = (Date) this.fecha.getSelectionModel().getSelectedItem();
                        if (f != null) {
                            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Se borrará la visita seleccionada y todos los registros relacionados.\n\n ¿Desea continuar?");
                            confirm.setTitle("Eliminar");
                            confirm.setHeaderText("Confirmar eliminación");
                            Optional<ButtonType> result = confirm.showAndWait();
                            if (result.get() == ButtonType.OK) {
                                String where = "IDPACV = ".concat(p.getId());
                                where = where.concat(" AND FECHA = '").concat(f.toString()).concat("'");
                                if (this.d.delete("Visitas",where)) {
                                    this.fecha.getItems().remove(f);
                                }
                            }
                        }
                        
                        break;
                    
                    case EVENTOS:
                        String fechaHora = (String) this.fecha.getSelectionModel().getSelectedItem();
                        if (fechaHora!=null) {
                            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Se borrará el evento seleccionado.\n\n ¿Desea continuar?");
                            confirm.setTitle("Eliminar");
                            confirm.setHeaderText("Confirmar eliminación");
                            Optional<ButtonType> result = confirm.showAndWait();
                            if (result.get() == ButtonType.OK) {
                                LocalDate date = LocalDate.parse(fechaHora.split(" ")[0], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                                LocalTime time = LocalTime.parse(fechaHora.split(" ")[1]);
                                this.d.delEventos(p.getId(), date, time);
                                this.fecha.getItems().remove(fechaHora);
                            }
                        }
                        
                        break;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }               

                
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
            ResultSet rs;
            rs = this.d.getFechasById(id, this.form);
            while(rs.next()){
                switch (this.form) {
                    case VISITAS:
                        this.fecha.getItems().add(rs.getDate("FECHA"));
                        break;
                    case EVENTOS:
                        SimpleDateFormat formatter = new SimpleDateFormat("HH:m");
                        this.fecha.getItems().add(rs.getDate("FECHAN").toString() + " " + formatter.format(rs.getTime("HORAN")));
                        break;
                }
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
            case EVENTOS:
                this.txFecha.setText("Eventos:");
                break;
        }
    }
    
    private void closeWindow() {
        Stage stage = (Stage) this.pbSearch.getScene().getWindow();
        stage.close();
    }    
}
