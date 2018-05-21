/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package registro.model;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
public class Controles {
    public enum TipoCtrl {
        TXT, DATE, RB, COMBO, MEMO
    }

    public List<Control> list = new LinkedList<>();

    public void Controles() {
    }
    
    public void add(String field, TipoCtrl t, Object o, String options, Boolean isid, Boolean varchar) {
        this.list.add(new Control(field, t, o, options, isid, varchar));
    }
    
    public void loadData(ResultSet rs) throws SQLException {
        String v;
        if (rs.next()) {
            for(Control c : list){
                switch (c.type) {
                    case TXT:
                    case MEMO:
                        v = rs.getString(c.field);
                        if (!rs.wasNull()) c.oTxt.setText(v); 
                        break;
                    case DATE:
                        java.sql.Date fecha = rs.getDate(c.field);
                        if (!rs.wasNull()) c.oDate.setValue(fecha.toLocalDate());
                        break;
                    case RB:
                        v = rs.getString(c.field);
                        if (!rs.wasNull()) if (v.equalsIgnoreCase(c.value)) c.oRB.setSelected(true);
                        break;
                }
            }
        }
    }
    
    public Boolean validateData() {
        Boolean ok = true;

        for(Control c : list){
            switch (c.type) {
                case TXT:
                    ok = c.validateText();
                    if (!ok) c.oTxt.requestFocus();
                    break;
            }
            
            if (!ok) break;
        }
        
        return ok;
    }
    
    public class Control {
        public String field;
        public String value;
        public String dic;
        public String cod;
        public String descrip;
        public Integer nchar;
        public Double min;
        public Double max;
        public Integer dec;
        public TipoCtrl type;
        public Boolean numeric;
        public Boolean isid;
        public Boolean varchar;
        public String valid;

        public TextField oTxt;
        public DatePicker oDate;
        public RadioButton oRB;
        public TextArea oMemo;
        public ComboBox oCombo;
        
        public Control(String name, TipoCtrl t, Object o, String options, Boolean isid, Boolean varchar){
            this.field = name;
            this.type = t;
            this.isid = isid;
            this.varchar = varchar;
                
            switch (this.type) {
                case TXT:
                    this.oTxt = (TextField) o;
                    
                    this.nchar = Integer.parseInt(options.split(",")[0]);
                    
                    this.numeric = (options.split(",")[1].equals("n"));
                    if (this.numeric) {
                        // validate values
                        this.dec = null;
                        this.min = null;
                        this.max = null;
                        this.valid = "0123456789";
                        String c = options.split(",")[2];
                        if (!c.equals("0")) {
                            this.min = Double.parseDouble(c.split(";")[0]);
                            this.max = Double.parseDouble(c.split(";")[1]);
                            this.dec = Integer.parseInt(c.split(";")[2]);
                            if (this.dec>0) this.valid = this.valid.concat(".");
                        }

                        // allow numeric input only
                        this.oTxt.setOnKeyTyped((KeyEvent e) -> { 
                            if (!this.valid.contains(e.getCharacter())) e.consume();
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
        
        public Boolean validateText() {
            Integer error = 0;
            String c = this.oTxt.getText();
            if (!c.trim().isEmpty()) {
                if (this.nchar > 0 && (c.length()>this.nchar)) error = 1;
                
                if (error == 0 && (this.min!=null || this.max!=null || this.dec!=null)) {
                    Double v = Double.parseDouble(c);
                    if (this.min != null) if (v < this.min) error = 2;
                    if (this.max != null) if (v > this.max) error = 3;

                    Integer nDec = c.indexOf('.') > 0 ? (c.length() - c.indexOf('.') - 1) : 0;
                    if (this.dec!=null) if (nDec > this.dec) error = 4;
                }
            }
            
            Boolean ok = (error == 0);
            if (!ok) {
                String header = "Error de validación campo " + field;
                if (error==1) showMessage("El número máximo de carácteres es ".concat(nchar.toString()), header, AlertType.WARNING, "Atención");
                if (error==2) showMessage("El valor debe ser mayor o igual que ".concat(min.toString()), header, AlertType.WARNING, "Atención");
                if (error==3) showMessage("El valor debe ser menor o igual que ".concat(max.toString()), header, AlertType.WARNING, "Atención");
                if (error==4) showMessage("El número de decimales debe ser ".concat(dec.toString()), header, AlertType.WARNING, "Atención");
            }

            return ok;
        }
        
        public String getValue() {
            String c = "";
            
            switch (this.type) {
                case TXT:
                    if (this.oTxt.getText().isEmpty()) {
                        c = "NULL";
                    } else {
                        c = this.oTxt.getText();
                        if (!this.varchar) c = "'".concat(c).concat("'");
                    }
                    break;
                case MEMO:
                    if (this.oMemo.getText() == null) c = "NULL";
                    else c = "'".concat(this.oMemo.getText()).concat("'");
                    break;
                case DATE:
                    if (this.oDate.getValue() == null) {
                        c = "NULL";
                    } else {
                        Date date = Date.valueOf(this.oDate.getValue());
                        c = "'".concat(date.toString()).concat("'");
                    }
                    break;
                case RB:
                    if (this.oRB.isSelected()) c = "'".concat(this.value).concat("'");
                    break;
            }
            
            return c;
        }
        
        public void showMessage(String message, String header, AlertType type, String title) {
            Alert a = new Alert(type, message);
            a.setTitle(title);
            a.setHeaderText(header);
            a.showAndWait();
        }
    }    
}
