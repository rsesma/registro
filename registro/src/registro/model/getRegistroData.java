/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package registro.model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javafx.scene.control.Alert;
import registro.FXMLregistroController.Forms;

/**
 *
 * @author R
 */
public class getRegistroData {
    private static final String C_DRIVER = "jdbc:mariadb";
    public String server;
    public String user;
    public String pswd;
    private static Connection conn;
    
    
    public getRegistroData() {
        conn = null;
        server = "";
        user = "";
        pswd = "";
    }
    
    public Boolean getConnection(String u, String p, String s) throws SQLException {
        user = u;
        pswd = p;
        server = s;
        try {
            conn = DriverManager.getConnection(
                    C_DRIVER + "://" + server + ":3306/registro",
                    user,pswd);
            return true;
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, e.getMessage());
            alert.showAndWait();
            return false;
        }
    }
    
    public ResultSet getListaIdRs(String filter) throws SQLException {
        if (filter.length()>0) {
            return conn.prepareStatement("SELECT * FROM lista_id_nombre WHERE " + filter).executeQuery();
        }
        else {
            return conn.prepareStatement("SELECT * FROM lista_id_nombre").executeQuery();
        }
    }

    public ResultSet getCensalRs(String id) throws SQLException {
        return conn.prepareStatement("SELECT * FROM Censal WHERE IDPAC = " + id).executeQuery();
    }
    
    public Boolean CensalIdExists(String id) {
        try {
            ResultSet rs = conn.prepareStatement("SELECT COUNT(IDPAC) AS N FROM Censal WHERE IDPAC = ".concat(id)).executeQuery();
            rs.next();
            Integer count = rs.getInt("N");
            rs.close();
            return (count>0);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, e.getMessage());
            alert.showAndWait();
            return true;
        }
    }
    
    public Boolean updateCensal(String id, List<CtrlCollection> list) {
        StringBuilder b = new StringBuilder();
        b.append("UPDATE Censal \nSET \n");
        for(CtrlCollection c : list){
            switch (c.type) {
                case TXT:
                    if (c.oTxt.getText().isEmpty()) {
                        b.append("  ").append(c.field).append(" = NULL, \n");
                    } else {
                        if (c.field.equalsIgnoreCase("IDPAC") || c.field.equalsIgnoreCase("TALLA")) {
                            b.append("  ").append(c.field).append(" = ").append(c.oTxt.getText()).append(", \n");
                        } else {
                            b.append("  ").append(c.field).append(" = '").append(c.oTxt.getText()).append("', \n");
                        }
                    }
                    break;
                case MEMO:
                    if (c.oMemo.getText() == null) {
                        b.append("  ").append(c.field).append(" = NULL, \n");
                    } else {
                        b.append("  ").append(c.field).append(" = '").append(c.oMemo.getText()).append("', \n");
                    }
                    break;
                case DATE:
                    if (c.oDate.getValue() == null) {
                        b.append("  ").append(c.field).append(" = NULL, \n");
                    } else {
                        Date date = Date.valueOf(c.oDate.getValue());
                        b.append("  ").append(c.field).append(" = '").append(date.toString()).append("', \n");
                    }
                    break;
                case RB:
                    if (c.oRB.isSelected()) b.append("  ").append(c.field).append(" = '").append(c.value).append("', \n");
                    break;
            }
        }
        String sql = b.toString();
        sql = sql.substring(0,sql.length()-3).concat("\n").concat("WHERE IDPAC = ").concat(id).concat(";");
        
        try {
            conn.prepareStatement(sql).executeUpdate();
            return true;
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, e.getMessage());
            alert.showAndWait();
            return false;
        }
    }
    
    public Boolean addCensal(List<CtrlCollection> list) {
        StringBuilder f = new StringBuilder();
        StringBuilder v = new StringBuilder();
        String SexoVal = "";
        for(CtrlCollection c : list){
            if (c.type!= CtrlType.RB) f.append(c.field).append(",");
            switch (c.type) {
                case TXT:
                    if (c.oTxt.getText().isEmpty()) {
                        v.append("NULL").append(",");
                    } else {
                        if (c.field.equalsIgnoreCase("IDPAC") || c.field.equalsIgnoreCase("TALLA")) {
                            v.append(c.oTxt.getText()).append(",");
                        } else {
                            v.append("'").append(c.oTxt.getText()).append("',");
                        }
                    }
                    break;
                case MEMO:
                    if (c.oMemo.getText() == null) {
                        v.append("NULL").append(",");
                    } else {
                        v.append("'").append(c.oMemo.getText()).append("',");
                    }
                    break;
                case DATE:
                    if (c.oDate.getValue() == null) {
                        v.append("NULL,");
                    } else {
                        Date date = Date.valueOf(c.oDate.getValue());
                        v.append("'").append(date.toString()).append("',");
                    }
                    break;
                case RB:
                    if (c.oRB.isSelected()) SexoVal = c.value;
                    break;
            }
        }
        String fields = f.toString().concat("SEXO");
        String values = v.toString().concat("'" + SexoVal + "'");
        String sql = "INSERT INTO Censal\n  (".concat(fields).concat(") \n");
        sql = sql.concat("  VALUES(").concat(values).concat(");");
        
        try {
            conn.prepareStatement(sql).executeUpdate();
            return true;
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, e.getMessage());
            alert.showAndWait();
            return false;
        }
    }
    
    public Boolean deleteCensalbyID(String id) {
        try {
            conn.prepareStatement("DELETE FROM Censal WHERE IDPAC = ".concat(id)).execute();
            return true;
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, e.getMessage());
            alert.showAndWait();
            return false;            
        }
    }
    
    public ResultSet getFechasById(String id, Forms type) throws SQLException {
        if (type == Forms.VISITAS) {
            return conn.prepareStatement("SELECT FECHA FROM Visitas WHERE IDPACV = ".concat(id)).executeQuery();
        } else {
            return null;
        }
    }
    
    public ResultSet getVisitasByIdFecha(String id, Date fecha) throws SQLException {
        PreparedStatement q = conn.prepareStatement("SELECT * FROM CVisitas WHERE IDPACV = ? AND FECHA = ?");
        q.setString(1, id);
        q.setDate(2, fecha);
        return q.executeQuery();
    }

    public ResultSet getDic(String dic) throws SQLException {
        return conn.prepareStatement("SELECT * FROM ".concat(dic)).executeQuery();
    }

    public ResultSet getDatosMarcaTab(String marca) throws SQLException {
        PreparedStatement q = conn.prepareStatement("SELECT * FROM DTabaco WHERE MARCA = ?");
        q.setString(1, marca);
        return q.executeQuery();
    }
    
    public String getDescripFromCod(String dic, String cod, Integer value, String descrip) throws SQLException {
        String d = "";
        PreparedStatement q = conn.prepareStatement("SELECT * FROM " + dic + " WHERE " + cod + " = ?");
        q.setInt(1, value);
        ResultSet rs = q.executeQuery();
        if (rs.next()) {
            return rs.getString(descrip);
        }
        return d;
    }
    
    public ResultSet getFarmacosByIdFecha(String id, Date fecha) throws SQLException {
        PreparedStatement q = conn.prepareStatement("SELECT * FROM CFarmacos WHERE IDPACF = ? AND FECHAF = ?");
        q.setString(1, id);
        q.setDate(2, fecha);
        return q.executeQuery();
    }
    
    public ResultSet getFarmacById(String cod) throws SQLException {
        PreparedStatement q = conn.prepareStatement("SELECT * FROM DVadem WHERE IDFARM = ? ORDER BY FARM");
        q.setString(1, cod);
        return q.executeQuery();
    }

    public ResultSet getFarmacByDescrip(String c) throws SQLException {
        PreparedStatement q = conn.prepareStatement("SELECT * FROM DVadem WHERE FARM LIKE '%" + c +"%' ORDER BY FARM");
        return q.executeQuery();
    }
}
