/**
 * @(#)Connection.java
 *
 *
 * @author Proyecto *** CPDS
 ** @version 1.00 2007/1/22
 *
 * Esta clase sirve para connect a una BD desde Java generalmente sin recurrir a
 * persistencia
 *
 */
package beans.connection;

import java.io.Serializable;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class ConnectionJDBC{

    String bd;
    String login;
    String password;
    String url;
    String table;
    public Connection conn;
    Statement st;
    ResultSet rs;
    String msj;

//    public void connect2(){

//    }

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
        
//        Connection con = null;
//        try {
//            InitialContext ic = new InitialContext();
//            //en esta parte es donde ponemos el Nombre
//            //de JNDI para que traiga el datasource
//            DataSource ds = (DataSource) ic.lookup("java:comp/env/jdbc_od2");
//            con = ds.getConnection();
//            conn = ds.getConnection();
//            Statement st = con.createStatement();
//            System.out.println("Se ha realizado con exito la conexión a MySQL");
//            //el resultSet es el encargado de traer los datos de la consulta
//            ResultSet rs = st.executeQuery("select * from temp");
//            while (rs.next()) {
//                System.out.println(" " + rs.getString(1) + " " + rs.getString(2));
//            }
//        } catch (SQLException ex) {            
//            System.out.println("============"+ex.toString());
//        } catch (NamingException ex) {            
//            System.out.println("============"+ex.toString());
//        } finally {
//            try {
//                con.close();
//                System.out.println("Conexion Cerrada con Exito...");
//            } catch (SQLException ex) {
//                System.out.println("============"+ex.toString());
//            }
//        }
        
        
        
        try {

            try {
                Class.forName("org.postgresql.Driver").newInstance();
            } catch (Exception e) {
                System.out.println("Error: " + e.toString() + " --- Clase: " + this.getClass().getName());
                msj = "ERROR: " + e.getMessage();
            }
            conn = DriverManager.getConnection(url, login, password);
            if (conn != null) {
                //System.out.println("Conexion a base de datos " + url + " ... OK");
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
        PreparedStatement stmt = null;
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
        PreparedStatement stmt = null;
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
        PreparedStatement stmt = null;
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

    private DataSource getConexionJDBC1() throws NamingException {
        Context c = new InitialContext();
        return (DataSource) c.lookup("java:comp/env/conexionJDBC1");
    }
}
