package managedBeans.reports;

import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primefaces.model.DualListModel;

public class ReportConnection implements Serializable {

    String bd;
    String login;
    String password;
    String url;
    String table;
    public Connection conn;
    Statement st;
    ResultSet rs;
    String msj;

    public ReportConnection() {
    }
    
    

    public void conexion() {
        bd = "";
        login = "";
        password = "";
        url = "jdbc:postgresql://localhost/";
        conn = null;
        st = null;
        rs = null;
    }

    public void connect() {
        msj = "";
        bd = "od";
        login = "postgres";
        password = "1234";
        url = "jdbc:postgresql://" + "localhost" + "/" + bd;// Anadir a la url la bd user y contrasena
        try {

            try {
                Class.forName("org.postgresql.Driver").newInstance();
            } catch (Exception e) {
                System.out.println("Error: " + e.toString() + " --- Clase: " + this.getClass().getName());
                msj = "ERROR: " + e.getMessage();
            }
            conn = DriverManager.getConnection(url, login, password);
            if (conn != null) {
                System.out.println("Conexion a base de datos " + url + " ... OK");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.toString() + " --- Clase: " + this.getClass().getName());
            msj = "ERROR: " + e.getMessage();
        }
    }

    public void conectar(String BD, String Usuario, String Contrasena, String servidor) {
        msj = "";
        bd = BD;
        login = Usuario;
        password = Contrasena;
        url = "jdbc:postgresql://" + servidor + "/" + bd;
        try {

            try {
                Class.forName("org.postgresql.Driver").newInstance();
            } catch (Exception e) {
                System.out.println("Error: " + e.toString() + " --- Clase: " + this.getClass().getName());
                msj = "ERROR: " + e.getMessage();
            }
            conn = DriverManager.getConnection(url, login, password);
            if (conn != null) {
                System.out.println("Conexion a base de datos " + url + " ... OK");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.toString() + " --- Clase: " + this.getClass().getName());
            msj = "ERROR: " + e.getMessage();
        }
    }

    public void disconnect() {
        msj = "";
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                    System.out.println("Se cerro conexion a: " + url + " ... OK");
                    msj = "Close conection " + url + " ... OK";
                }
            } catch (SQLException e) {
                System.out.println("Error: " + e.toString() + " --- Clase: " + this.getClass().getName());
                msj = "ERROR: " + e.getMessage();
            }
        }
    }

    public ResultSet consult(String query) {
        msj = "";
        try {
            if (conn != null) {
                st = conn.createStatement();
                rs = st.executeQuery(query);
                return rs;
            } else {
                msj = "There don't exist connection";
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.toString() + " --- Clase: " + this.getClass().getName());
            msj = "ERROR: " + e.getMessage();
            return null;
        }
    }

    public ResultSet consult1(String query) {
        msj = "";
        try {
            if (conn != null) {
                st = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                rs = st.executeQuery(query);
                return rs;
            } else {
                msj = "There don't exist connection";
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.toString() + " --- Clase: " + this.getClass().getName());
            msj = "ERROR: " + e.getMessage();
            return null;
        }
    }

    public int non_query(String query) {
        msj = "";
        PreparedStatement stmt;
        int reg;
        reg = 0;
        try {
            if (conn != null) {
                stmt = conn.prepareStatement(query);
                reg = stmt.executeUpdate();
                stmt.close();
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.toString() + " --- Clase: " + this.getClass().getName());
            msj = "ERROR: " + e.getMessage();
        }
        return reg;
    }

    public String insert(String Tabla, String elementos, String registro) {
        msj = "";
        int reg = 1;
        String success;
        try {
            if (conn != null) {
                st = conn.createStatement();
                st.execute("INSERT INTO " + Tabla + " (" + elementos + ") VALUES (" + registro + ")");
                if (reg > 0) {
                    success = "true";
                } else {
                    success = "false";
                }
                st.close();
            } else {
                success = "false";
                msj = "ERROR: There don't exist connection...";
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.toString() + " --- Clase: " + this.getClass().getName());
            System.out.println("numero: " + e.getErrorCode());
            success = e.getMessage();
            msj = "ERROR: " + e.getMessage();
        }
        return success;
    }

    public void remove(String Tabla, String condicion) {
        msj = "";
        PreparedStatement stmt;
        int reg;
        try {
            if (conn != null) {
                stmt = conn.prepareStatement("DELETE FROM " + Tabla + " WHERE " + condicion);
                reg = stmt.executeUpdate();
                if (reg > 0) {
                } else {
                }
                stmt.close();
            } else {
                msj = "ERROR: There don't exist connection";
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.toString() + " --- Clase: " + this.getClass().getName());
            msj = "ERROR: " + e.getMessage();
        }
    }

    public void update(String Tabla, String campos, String donde) {
        msj = "";
        PreparedStatement stmt;
        int reg;
        try {
            if (conn != null) {
                stmt = conn.prepareStatement("UPDATE " + Tabla + " SET " + campos + " WHERE " + donde);
                reg = stmt.executeUpdate();
                if (reg > 0) {
                } else {
                }
                stmt.close();
            } else {
                msj = "ERROR: There don't exist connection";
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.toString() + " --- Clase: " + this.getClass().getName());
            msj = "ERROR: " + e.getMessage();
        }
    }

    public String getMsj() {
        return msj;
    }

    public void setMsj(String mens) {
        msj = mens;
    }


    /*
     * New methods for reporting!!!
     */
    
    /*
     * Devuelve las variables disponibles para cruzar de acuerdo a un formulario
     */
    public List<Variable> getVariablesByFormID(String form_id) {
        try {
            List<Variable> variables = new ArrayList<Variable>();
            String query = "SELECT * FROM cross_variables "
                    + "WHERE form_id LIKE '" + form_id + "'";
            System.out.println(query);
            ResultSet records = this.consult(query);
            while (records.next()) {
                Variable variable = new Variable();
                variable.setId(records.getInt("id"));
                variable.setName(records.getString("name"));
                variable.setTable(records.getString("table"));
                variable.setField(records.getString("field"));
                variable.setGeneric_table(records.getString("generic_table"));
                variables.add(variable);
            }
            return variables;
        } catch (SQLException ex) {
            Logger.getLogger(ReportConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /*
     * Devuelve una variable de acuerdo a su id
     */
    public Variable getVariableById(int id) {
        try {
            String query = "SELECT * FROM cross_variables "
                    + "WHERE id = " + id;
            ResultSet record = this.consult(query);
            record.next();
            Variable variable = new Variable();
            variable.setId(record.getInt("id"));
            variable.setName(record.getString("name"));
            variable.setTable(record.getString("table"));
            variable.setField(record.getString("field"));
            variable.setGeneric_table(record.getString("generic_table"));
            return variable;
        } catch (SQLException ex) {
            Logger.getLogger(ReportConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /*
     * Construye el query para devolver la tabla pre-pivot
     */
    public String getPrepivotQuery(DualListModel<Variable> variables) {
        StringBuilder query = new StringBuilder();
        this.non_query("DROP TABLE IF EXISTS prepivot;");
        query.append("CREATE TABLE prepivot AS \n\t");
        query.append("SELECT \n\t");
        for (Variable variable : variables.getTarget()) {
            query.append(variable.getField()).append(", ");
        }
        query.append("count(*) ");
        query.append("\nFROM \n\ttest ");
        query.append("\nWHERE \n\t");
        String by = "";
        int i = 1;
        for (Variable variable : variables.getTarget()) {
            query.append(variable.getField()).append(" <> '' AND ");
            by += (i++) + ",";
        }
        query.delete(query.length() - 4, query.length());
        by = by.substring(0, by.length() - 1);
        query.append("\nGROUP BY \n\t").append(by);
        query.append("\nORDER BY \n\t").append(by);
        this.non_query(query.toString());
        System.out.println(query);
        return query.toString();
    }
    
    /*
     * Devuelve los valores distintos de una variable dentro de una consulta
     */
    public List<Integer> getValuesFromVariable(String variable, String subtable){
        try {
            List<Integer> values = new ArrayList<Integer>();
            String query = "SELECT DISTINCT CAST(" + variable + " AS INTEGER) "
                    + "FROM prepivot AS foo ORDER BY 1";
            ResultSet records = this.consult(query);
            while(records.next()){
                values.add(records.getInt(1));
            }
            return values;
        } catch (SQLException ex) {
            Logger.getLogger(ReportConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    /*
     * Retorna el nombre de un valor a partir de su id y su tabla generica
     */
    public String getValuesFromGenericTable(int id, String table){
        try {
            String query = "SELECT * FROM " + table;
            ResultSet values = this.consult(query);
            while(values.next()){
                if(values.getInt(1) == id){
                    return values.getString(2);
                }
            }
            System.out.println("Valor no encontrado en la tabla generica " + table);
            return null;
        } catch (SQLException ex) {
            Logger.getLogger(ReportConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    /*
     * Retorna la combinacion de columnas no nulas
     */
    public List<String> getCombinedColumns(String column1, String column2){
        try {
            List<String> indexes = new ArrayList<String>();
            String query = "SELECT\n\t " + column1 + "," + column2 + "\n "
                    + "FROM\n\t prepivot\n WHERE\n\t " + column1 + " <> '' AND "
                    + column2 + " <> ''\n GROUP BY\n\t 1,2\n ORDER BY\n\t1,2";
            ResultSet records = this.consult(query);
            while(records.next()){
                indexes.add(records.getString(1) + "|" + records.getString(2));
            }
            return indexes;
        } catch (SQLException ex) {
            Logger.getLogger(ReportConnection.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
