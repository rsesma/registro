/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package registro;

import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import registro.FXMLregistroController.Forms;
import registro.model.Paciente;
import registro.model.getRegistroData;

/**
 * FXML Controller class
 *
 * @author r
 */
public class FXMLfechaController implements Initializable {

    @FXML
    private Label id;
    @FXML
    private Label nom;
    @FXML
    private Label repl;
    @FXML
    private ListView lista;

    private Paciente p;
    private getRegistroData d;
    private Forms type;
    
    public boolean changed;
    
    ObservableList<Date> dates =FXCollections.observableArrayList();
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        this.changed = false;
    }
    
    @FXML
    private void editFired(ActionEvent event) {
        Date sel = (Date) lista.getSelectionModel().getSelectedItem();
        if (sel!=null) {
            System.out.println(sel);
        }
    }

    @FXML
    private void newFired(ActionEvent event) {
    }

    @FXML
    private void deleteFired(ActionEvent event) {
    }
    
    @FXML
    private void cancelFired(ActionEvent event) {
        closeWindow();
    }
    
    public void SetData(Paciente p, getRegistroData data, Forms f) {
        this.p = p;
        this.d = data;
        this.type = f;
        
        this.id.setText(p.getId());
        this.nom.setText(p.getNom());
        
        switch (this.type) {
            case VISITAS:
                this.repl.setText("Visitas");
                try {
                    ResultSet rs = this.d.getVisitasById(this.p.getId());
                    while(rs.next()){
                        dates.add(rs.getDate("FECHA"));
                        lista.setItems(dates);
                    }

                } catch(Exception e) {
                    System.out.println(e.getMessage());
                }
                
                break;
                
        }
    }
    
    private void closeWindow() {
        Stage stage = (Stage) this.id.getScene().getWindow();
        stage.close();
    }    
}
