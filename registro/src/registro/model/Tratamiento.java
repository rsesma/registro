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
public class Tratamiento {
    private final String farm;
    private final SimpleStringProperty farmaco = new SimpleStringProperty();
    private final String cumpl;
    private final SimpleStringProperty cumplimiento = new SimpleStringProperty();

    public Tratamiento(String farm, String farmaco, String cumpl, String cumplimiento) {
        this.farm = farm;
        setFarmaco(farmaco);
        this.cumpl = cumpl;
        setCumplimiento(cumplimiento);
    }
    
    public final StringProperty cumplimientoProperty() {
        return this.cumplimiento;
    }

    public final String getCumplimiento() {
        return this.cumplimientoProperty().get();
    }

    public final void setCumplimiento(final String c) {
        this.cumplimientoProperty().set(c);
    }


    public final StringProperty farmacoProperty() {
        return this.farmaco;
    }

    public final String getFarmaco() {
        return this.farmacoProperty().get();
    }

    public final void setFarmaco(final String f) {
        this.farmacoProperty().set(f);
    }

    public final String getFarm() {
        return this.farm;
    }

    public final String getCumpl() {
        return this.cumpl;
    }
}
