/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package registro.model;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author r
 */
public class Farmaco {
    private Entry cumpl;
    //private final StringProperty cumpl = new SimpleStringProperty();
    
    public Farmaco(String descrip, Long cod) {
        this.cumpl = new SimpleEntry<>(descrip,cod);
    }

    public final Entry cumplProperty() {
        StringProperty c = new SimpleStringProperty(this.cumpl.getKey().toString());
        return this.cumpl;
    }

    public final Entry getCumpl() {
        return this.cumpl;
    }


    public final void setCumpl(final Entry c) {
        this.cumpl= c;
    }

}
