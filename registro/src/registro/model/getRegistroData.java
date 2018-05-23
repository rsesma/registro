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
import java.time.LocalDate;
import javafx.collections.ObservableList;
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

    public Boolean VisitaExists(String id, LocalDate f) {
        Date fecha = java.sql.Date.valueOf(f);
        try {
            PreparedStatement q = conn.prepareStatement("SELECT COUNT(IDPACV) AS N FROM Visitas WHERE IDPACV = ? AND FECHA = ?");
            q.setString(1, id);
            q.setDate(2, fecha);
            ResultSet rs = q.executeQuery();
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
    
    public Boolean update(String table, Controles c, String where) {
        String sql = "UPDATE ".concat(table).concat(" SET \n");
        sql = sql.concat(c.getUpdateSQL(this)).concat("\n");
        sql = sql.concat("WHERE ").concat(where);
        return executeSQL(sql,true);
    }
    
    public Boolean add(String table, Controles c) {
        String sql = "INSERT INTO ".concat(table);
        sql = sql.concat("\n (").concat(c.getFieldsSQL()).concat(")");
        sql = sql.concat("\n VALUES(").concat(c.getAddSQL(this)).concat(")");
        return executeSQL(sql,true);
    }

    public Boolean delete(String table, String where) {
        String sql = "DELETE FROM ".concat(table);
        sql = sql.concat(" WHERE ").concat(where);
        return executeSQL(sql,false);
    }
    
    public Boolean executeSQL(String sql, Boolean update) {
        try {
            if (update) conn.prepareStatement(sql).executeUpdate();
            else conn.prepareStatement(sql).execute();
            return true;
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
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
        PreparedStatement q = conn.prepareStatement("SELECT * FROM " + dic + " WHERE " + cod + " = ?");
        q.setInt(1, value);
        ResultSet rs = q.executeQuery();
        if (rs.next()) return rs.getString(descrip);
        return "";
    }
    
    public String getCodFromDescrip(String dic, String descrip, String value, String cod) throws SQLException {
        PreparedStatement q = conn.prepareStatement("SELECT * FROM " + dic + " WHERE " + descrip + " = ?");
        q.setString(1, value);
        ResultSet rs = q.executeQuery();
        if (rs.next()) return rs.getString(cod);
        return "";
    }
    
    public ResultSet getFarmacosByIdFecha(String id, Date fecha) throws SQLException {
        PreparedStatement q = conn.prepareStatement("SELECT * FROM CFarmacos WHERE IDPACF = ? AND FECHAF = ?");
        q.setString(1, id);
        q.setDate(2, fecha);
        return q.executeQuery();
    }
    
    public Boolean ExisteCodMarcaTab(String cod) {
        try {
            PreparedStatement q = conn.prepareStatement("SELECT COUNT(IDMARC) AS N FROM DTabaco WHERE IDMARC = ?");
            q.setString(1, cod);
            ResultSet rs = q.executeQuery();
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

    public Boolean ExisteMarcaTab(String marca) {
        try {
            PreparedStatement q = conn.prepareStatement("SELECT COUNT(IDMARC) AS N FROM DTabaco WHERE MARCA = ?");
            q.setString(1, marca);
            ResultSet rs = q.executeQuery();
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

    public Boolean MarcaTabEnUso(String cod) {
        try {
            PreparedStatement q = conn.prepareStatement("SELECT COUNT(IDPACV) AS N FROM Visitas WHERE TIPOTA = ?");
            q.setString(1, cod);
            ResultSet rs = q.executeQuery();
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
    
    public void updateTabaco(String value, String field, String cod) {
        try {
            PreparedStatement q = conn.prepareStatement("UPDATE DTabaco SET ".concat(field).concat(" = ? WHERE IDMARC = ?"));
            q.setString(1, value);
            q.setString(2, cod);
            q.executeUpdate();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, e.getMessage());
            alert.showAndWait();
        }
    }

    public void addTabaco(MarcaTab m) {
        try {
            PreparedStatement q = conn.prepareStatement("INSERT INTO DTabaco (IDMARC,MARCA,ALQUIT,NICOT,CO) VALUES(?,?,?,?,?)");
            q.setString(1, m.getCod());
            q.setString(2, m.getMarca());
            q.setString(3, m.getAlq());
            q.setString(4, m.getNic());
            q.setString(5, m.getCO());
            q.executeUpdate();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, e.getMessage());
            alert.showAndWait();
        }
    }
    
    public void updateFarmacosByIdFecha(String id, Date fecha, ObservableList<Tratamiento> list) {
        
        try {
            PreparedStatement q = conn.prepareStatement("DELETE FROM Farmacos WHERE IDPACF = ? AND FECHAF = ?");
            q.setString(1, id);
            q.setDate(2, fecha);
            q.execute();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
        if (!list.isEmpty()) {
            list.forEach((t) -> { 
                try {
                    PreparedStatement q = conn.prepareStatement("INSERT INTO Farmacos (IDPACF, FECHAF, FARMAC, CUMPLI) VALUES(?,?,?,?)");
                    q.setString(1, id);
                    q.setDate(2, fecha);
                    q.setString(3, t.getFarm());
                    q.setString(4, t.getCumpl());
                    q.executeUpdate();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            });
        }
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
