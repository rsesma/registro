/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package registro.model;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author R
 */
public class Paciente {
    public SimpleStringProperty id = new SimpleStringProperty();
    public SimpleStringProperty nom = new SimpleStringProperty();
 
    public String getId() {
        return id.get();
    }
    
    public String getNom() {
        return nom.get();
    }

}
