/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package registro.model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javafx.scene.control.Alert;

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

    public Boolean updateCensal(String id, List<CensalCollection> list) {
        StringBuilder b = new StringBuilder();
        b.append("UPDATE Censal \nSET \n");
        for(CensalCollection c : list){
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
}
