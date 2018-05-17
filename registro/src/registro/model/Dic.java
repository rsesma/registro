/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package registro.model;

/**
 *
 * @author r
 */
public class Dic {
    private String cod;
    private String descrip;
        
    public Dic(String cod, String descrip) {
        this.cod = cod;
        this.descrip = descrip;
    }

    public String getCod() {
        return this.cod;
    }

    public String getDescrip() {
        return this.descrip;
    }
    
    public void setCod(String c) {
        this.cod = c;
    }

    public void setDescrip(String d) {
        this.descrip = d;
    }
}
