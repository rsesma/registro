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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import registro.model.Paciente;
import registro.model.getRegistroData;

/**
 * FXML Controller class
 *
 * @author R
 */
public class FXMLidController implements Initializable {
    
    @FXML
    private TableView lista;
    @FXML
    private TableColumn idCol;
    @FXML
    private TableColumn nomCol;
    
    public getRegistroData d;
    
    final ObservableList<Paciente> listaData = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        // Set up the alumnos table
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        
        lista.setItems(listaData);
        try {
            ResultSet rs = this.d.getListaIdRs("");
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        
        LoadTable("");
    }    
    
    public void LoadTable(String filter) {
        try{
            ResultSet rs = d.getListaIdRs(filter);
            while(rs.next()){
                Paciente p = new Paciente();
                
                p.id.set(rs.getString("IDPAC"));
                p.nom.set(rs.getString("NOMBRE"));
                
                listaData.add(p);
            }
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
    
    public void SetData(getRegistroData data) {
        this.d = data;
    }

}
