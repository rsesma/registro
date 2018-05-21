package registro.model;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author r
 */

public class CtrlCollection implements java.io.Serializable {
    public String field;
    public String value;
    public String dic;
    public String cod;
    public String descrip;
    public Double min;
    public Double max;
    public Integer dec;
    public CtrlType type;
    public Boolean numeric;
    public String valid;
    
    public TextField oTxt;
    public DatePicker oDate;
    public RadioButton oRB;
    public TextArea oMemo;
    public ComboBox oCombo;
    
    public CtrlCollection(String name, CtrlType t, Object o, String options){
        this.field = name;
        this.type = t;
                
        switch (this.type) {
            case TXT:
                this.oTxt = (TextField) o;
                Boolean numeric = (options.split(",")[0]=="n");
                if (this.numeric) {
                    this.dec = 0;
                    this.min = null;
                    this.max = null;
                    this.valid = "0123456789";
                    
                    // validate values
                    String c = options.split(",")[1];
                    if (!c.trim().isEmpty()) {
                        this.min = Double.parseDouble(c.split(";")[0]);
                        this.max = Double.parseDouble(c.split(";")[1]);
                        this.dec = Integer.parseInt(c.split(";")[2]);
                        if (this.dec>0) this.valid.concat(".");
                    }
                    
                    // allow numeric input only
                    this.oTxt.setOnKeyTyped((KeyEvent e) -> { 
                        if (this.dec==0) if (!valid.contains(e.getCharacter())) e.consume(); 
                    });
                }
                    
                break;
            case DATE:
                this.oDate = (DatePicker) o;
                break;
            case COMBO:
                this.oCombo = (ComboBox) o;
                this.dic = options.split(";")[0];
                this.cod = options.split(";")[1];
                this.descrip = options.split(";")[2];
                break;
            case RB:
                this.oRB = (RadioButton) o;
                this.value = options;
                break;
            case MEMO:
                this.oMemo = (TextArea) o;
                break;
            
        }
    }
}
