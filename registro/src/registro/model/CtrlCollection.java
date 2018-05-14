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
    public CtrlType type;
    
    public TextField oTxt;
    public DatePicker oDate;
    public RadioButton oRB;
    public TextArea oMemo;
    public ComboBox oCombo;
    
    public CtrlCollection(String name, CtrlType t, Object o, String c){
        this.field = name;
        this.type = t;
        
        switch (this.type) {
            case TXT:
                this.oTxt = (TextField) o;
                break;
            case DATE:
                this.oDate = (DatePicker) o;
                break;
            case COMBO:
                this.oCombo = (ComboBox) o;
                this.dic = c.split(";")[0];
                this.cod = c.split(";")[1];
                this.descrip = c.split(";")[2];
                break;
            case RB:
                this.oRB = (RadioButton) o;
                this.value = c;
                break;
            case MEMO:
                this.oMemo = (TextArea) o;
                break;
            
        }
    }
}
