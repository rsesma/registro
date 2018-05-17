/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package registro.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author r
 */
public class Farmaco {
    private final SimpleStringProperty cod = new SimpleStringProperty();
    private final SimpleStringProperty descrip = new SimpleStringProperty();

    public Farmaco(String cod, String descrip) {
        setCod(cod);
        setDescrip(descrip);
    }
    
    public final StringProperty descripProperty() {
        return this.descrip;
    }

    public final String getDescrip() {
        return this.descripProperty().get();
    }

    public final void setDescrip(final String c) {
        this.descripProperty().set(c);
    }

    public final StringProperty codProperty() {
        return this.cod;
    }

    public final String getCod() {
        return this.codProperty().get();
    }

    public final void setCod(final String c) {
        this.codProperty().set(c);
    }
    
}
