/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package registro.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
        Boolean lOk = false;
        
        StringBuilder b = new StringBuilder();
        b.append("UPDATE Censal \nSET \n");
        for(CensalCollection c : list){
            if (c.type == CtrlType.TXT) {
                b.append("  " + c.field + " = '" + c.oTxt.getText() + "', \n");
            }
        }
        String c = b.toString();
        c = c.substring(0,c.length()-3) + "\n";
        c = c.concat("WHERE IDPAC = " + id + ";");
        System.out.println(c);

        return lOk;
    }
}
