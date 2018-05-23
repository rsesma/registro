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
public class MarcaTab {
    private final SimpleStringProperty cod;
    private final SimpleStringProperty marca;
    private final SimpleStringProperty alq;
    private final SimpleStringProperty nic;
    private final SimpleStringProperty co;

    public MarcaTab(String cod, String marca, String alq, String nic, String co) {
        this.cod = new SimpleStringProperty(cod);
        this.marca = new SimpleStringProperty(marca);
        this.alq = new SimpleStringProperty(alq);
        this.nic = new SimpleStringProperty(nic);
        this.co = new SimpleStringProperty(co);
    }

    public String getCod() {
        return this.cod.get();
    }

    public StringProperty codProperty() {
        return this.cod;
    }

    public void setCod(String cod) {
        this.cod.set(cod);
    }

    public String getMarca() {
        return this.marca.get();
    }

    public StringProperty marcaProperty() {
        return this.marca;
    }

    public void setMarca(String marca) {
        this.marca.set(marca);
    }
    
    public String getAlq() {
        return this.alq.get();
    }

    public StringProperty alqProperty() {
        return this.alq;
    }

    public void setAlq(String alq) {
        this.alq.set(alq);
    }

    public String getNic() {
        return this.nic.get();
    }

    public StringProperty nicProperty() {
        return this.nic;
    }

    public void setNic(String nic) {
        this.nic.set(nic);
    }

    public String getCO() {
        return this.co.get();
    }

    public StringProperty coProperty() {
        return this.co;
    }

    public void setCO(String co) {
        this.co.set(co);
    }
}
