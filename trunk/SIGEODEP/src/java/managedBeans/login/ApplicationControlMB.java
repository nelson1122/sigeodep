/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.login;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.annotation.Resource;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.sql.DataSource;
import javax.swing.Timer;

/**
 *
 * @author santos
 */
@ManagedBean(name = "applicationControlMB")
@ApplicationScoped
public class ApplicationControlMB {

    @Resource(name = "jdbc/od")
    private DataSource ds;//fuente de datos(es configurada por glassfish)
    Timer timer = new Timer(3600000, new ActionListener() {//cada hora
        //Timer timer = new Timer(60000, new ActionListener() {//cada minuto
        @Override
        public void actionPerformed(ActionEvent e) {
            actionsPerHour();
        }
    });
    private ResultSet rs;
    private Statement st;
    private String user;
    private String db;
    private String password;
    private String server;
    private String url = "";
    private ArrayList<String> currentIdSessions = new ArrayList<>();//lista de identificadores de sesiones
    private ArrayList<Integer> currentUserIdSessions = new ArrayList<>();//lista de id de usuarios logeados
    private String value = "-";
    private String realPath = "";
    public Connection conn;

    public ApplicationControlMB() {
        /*
         * Constuctor de la clase: obtiene la ruta real del servidor e 
         * inicia un timer que es llamado cada hora
         */
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        realPath = (String) servletContext.getRealPath("/");
        timer.start();
    }

    private void actionsPerHour() {
        /*
         * Metodo que se ejecuta cada hora, si no hay susuarios en el sistema realiza una copia de seguridad
         */
        try {
            if (currentUserIdSessions.isEmpty()) {
                Calendar a = new GregorianCalendar();
                TimeZone zonah = java.util.TimeZone.getTimeZone("GMT+1");
                Calendar Calendario = java.util.GregorianCalendar.getInstance(zonah, new java.util.Locale("es"));
                SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat df2 = new java.text.SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
                SimpleDateFormat df3 = new java.text.SimpleDateFormat("yyyy-MM-dd");

                if (connectToDb()) {
                    //determinar si existe una copia de seguridad para el dia actual
                    String sql = "SELECT * FROM backups WHERE date_backup::date = to_date('" + df3.format(Calendario.getTime()) + "','yyyy-MM-dd') AND id_backup < 11";
                    rs = consult(sql);
                    if (rs.next()) {
                        //System.out.println("Ya existe una copia de seguridad automatica para este dia");
                    } else {
                        String dateStr = df.format(Calendario.getTime());
                        String fileName = "backup_" + df2.format(Calendario.getTime());
                        if (backupPGSQL(realPath + "backups/", fileName)) {
                            saveBackupInfo(realPath + "backups/", fileName, dateStr);
                        }
                    }
                }
            } else {
                //System.out.println("no se realiza copia de seguridad por que existen usuarios en el sistema");
            }
        } catch (Exception e) {
            //System.out.println("Error 5 en " + this.getClass().getName() + ":" + e.getMessage());
        }
    }

    public boolean saveBackupInfo(String serverPath, String fileName, String dateStr) {
        /* 
         * almacena nombre, hubicacion y fecha de creacion de la copia de seguridad en la base de datos
         */
        try {
            rs = consult("SELECT * FROM backups WHERE id_backup < 11 ORDER BY id_backup");
            int max = 0;
            while (rs.next()) {
                if (rs.getInt(1) > max) {
                    max = rs.getInt(1);
                }
            }
            if (max == 10) {
                //ya existen 10 copias se debe sobreescribir una existente
                rs = consult("SELECT MIN(date_backup) FROM backups where id_backup < 11");
                String minDate = "";//fecha 
                if (rs.next()) {
                    minDate = rs.getString(1);
                }
                if (minDate.length() != 0) {
                    //elimino el archivo 
                    rs = consult("SELECT * FROM backups where date_backup = '" + rs.getString(1) + "'");
                    if (rs.next()) {
                        java.io.File ficherofile = new java.io.File(rs.getString("path_file") + rs.getString("name_backup") + ".backup");
                        if (ficherofile.exists()) {
                            ficherofile.delete();//Borramos archivo de la copia de seguridad
                        }
                        //actualizo el registro
                        non_query("UPDATE backups SET "
                                + " name_backup = '" + fileName + "',"
                                + " date_backup = '" + dateStr + "',"
                                + " type_backup = 'AUTOMATICO',"
                                + " path_file = '" + serverPath + "'"
                                + "WHERE "
                                + " id_backup = " + rs.getString(1));
                    }
                }
            } else {
                //hay menos de diez copias automaticas se crea un nuevo registro
                max++;
                non_query(" INSERT INTO backups VALUES (" + String.valueOf(max) + ",'" + fileName + "','" + dateStr + "','AUTOMATICO','" + serverPath + "')");
            }
            return true;
        } catch (Exception e) {
            System.out.println("Error 6 en " + this.getClass().getName() + ":" + e.getMessage());
            return false;
        }
    }

    private boolean backupPGSQL(String serverPath, String fileName) {
        /*
         * generacion de un archivo de copia de seguridad mediante la opcion:
         * pg_dump de postgres, recibe como parametro la carpeta en el servidor
         * y el nombre que tendra la copia de seguridad
         */
        try {
            Runtime r = Runtime.getRuntime();
            Process p;
            ProcessBuilder pb;
            java.io.File file = new java.io.File(serverPath);
            if (file.exists()) {//verificar que el directorio exista
                StringBuilder fechafile = new StringBuilder();
                fechafile.append(serverPath);
                fechafile.append(fileName);
                fechafile.append(".backup");
                java.io.File ficherofile = new java.io.File(fechafile.toString());
                if (ficherofile.exists()) {//Probamos a ver si existe ese ultimo dato                    
                    ficherofile.delete();//Lo Borramos
                }
                r = Runtime.getRuntime();
                //pb = new ProcessBuilder("pg_dump", "-f", fechafile.toString(), "-F", "c", "-Z", "9", "-v", "-o", "-h", IP, "-U", user, dbase);
                pb = new ProcessBuilder("pg_dump", "-i", "-h", server, "-p", "5432", "-U", user, "-F", "c", "-b", "-v", "-f", fechafile.toString(), db, "-T", "backups");
                pb.environment().put("PGPASSWORD", password);
                pb.redirectErrorStream(true);
//                System.out.println("Inicia creacion de copia de seguridad: " + fechafile.toString());
                p = pb.start();
                String outString = "";
                try {
                    //CODIGO PARA MOSTRAR EL PROGESO DE LA GENERACION DEL ARCHIVO
                    InputStream is = p.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    String ll;
                    while ((ll = br.readLine()) != null) {
                        outString = ll;
                    }
                    System.out.println("\nFinaliza creacion de copia de seguridad: " + fechafile.toString() + "   " + outString);
                    return true;
                } catch (IOException e) {
                    System.out.println("Error 7 en " + this.getClass().getName() + ":" + e.getMessage());
                    return false;
                }
                //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Correcto", "La copia de seguridad ha sido creada correctamente"));
            } else {
                System.out.println("No se encontro la carpeta backups en el servidor ");
                return false;
            }
        } catch (IOException x) {
            System.out.println("Error 8 en " + this.getClass().getName() + ":" + x.getMessage());
            return false;
        }
    }

    public final boolean connectToDb() {
        /*
         * Nos conectamos a la base de datos atraves 
         * de un DataSource = (conexion configurada por glassFish)
         * y generamos una conexion normal por JDBC
         */
        boolean returnValue = true;
        if (conn == null) {
            returnValue = false;
            try {
                if (ds == null) {
                    System.out.println("ERROR: No se obtubo data source");
                } else {
                    conn = ds.getConnection();
                    if (conn == null) {
                        System.out.println("Error: No se obtubo conexion");
                    } else {
                        ResultSet rs1 = consult("Select * from configurations");
                        if (rs1.next()) {
                            user = rs1.getString("user_db");
                            db = rs1.getString("name_db");
                            password = rs1.getString("password_db");
                            server = rs1.getString("server_db");
                            url = "jdbc:postgresql://" + server + "/" + db;
                            try {
                                Class.forName("org.postgresql.Driver").newInstance();// seleccionar SGBD
                            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                                System.out.println("Error 1 en " + this.getClass().getName() + ":" + e.getMessage());
                            }
                            conn.close();
                            conn = DriverManager.getConnection(url, user, password);// Realizar la conexion
                            if (conn != null) {
                                System.out.println("Conexion a base de datos " + url + " " + this.getClass().getName());
                                returnValue = true;
                            } else {
                                System.out.println("No se pudo conectar a base de datos " + url + " ... FAIL");
                            }
                        } else {
                            System.out.println("No se pudo conectar a base de datos " + url + " ... FAIL");
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Error 2 en " + this.getClass().getName() + ":" + e.getMessage());
            }
        }
        return returnValue;
    }

    public ResultSet consult(String query) {
        /*
         * se encarga de procesar una consulta que retorne una o varias tuplas
         * de la base de datos retornandolas en un ResultSet
         */
        //msj = "";
        try {
            if (conn != null) {
                st = conn.createStatement();
                rs = st.executeQuery(query);
                //System.out.println("---- CONSULTA: " + query);
                return rs;
            } else {
                System.out.println("There don't exist connection");
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Error 3 en " + this.getClass().getName() + ":" + e.getMessage() + "---- CONSULTA:" + query);
            return null;
        }
    }

    public int non_query(String query) {
        /*
         * se encarga de procesar una consulta que no retorne tuplas
         * ejemplo: INSERT, UPDATE, DELETE...
         * retorna 0 si se realizo correctamente
         * retorna 1 cuando la instuccion no pudo ejecutarse
         */
        int reg;
        reg = 0;
        try {
            if (conn != null) {
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    reg = stmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.out.println("Error 4 en " + this.getClass().getName() + ":" + e.getMessage());
        }
        return reg;
    }

    public boolean hasLogged(int idUser) {
        /*
         * determina si un usario tiene una sesion iniciada recibe como parametro 
         * el identificador del usuario en la base de datos
         */
        boolean foundIdUser = false;
        //determinar si el usuario ya tiene iniciada una sesion
        for (int i = 0; i < currentUserIdSessions.size(); i++) {
            if (currentUserIdSessions.get(i) == idUser) {
                foundIdUser = true;
                break;
            }
        }
        return foundIdUser;
    }

    public void addSession(int idUser, String idSession) {
        /*
         * adicionar a la lista de sesiones activas
         */
        currentIdSessions.add(idSession);
        currentUserIdSessions.add(idUser);//System.out.println("Agregada Nueva sesion: " + idSession + "  usuario: " + idUser);
    }

    public void removeSession(int idUser) {
        /*
         * eliminar de la lista de sesiones activas dependiento del id del usuario
         */
        try {
            for (int i = 0; i < currentUserIdSessions.size(); i++) {
                if (currentUserIdSessions.get(i) == idUser) {
                    currentUserIdSessions.remove(i);
                    currentIdSessions.remove(i);//System.out.println("Session eliminada usuario: " + idUser);
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error 9 en " + this.getClass().getName() + ":" + e.getMessage());
        }
    }

    public void removeSession(String idSession) {
        /*
         * eliminar de la lista de sesiones actuales dependiento del id de la sesion
         */
        try {
            for (int i = 0; i < currentUserIdSessions.size(); i++) {
                if (currentIdSessions.get(i).compareTo(idSession) == 0) {
                    currentUserIdSessions.remove(i);
                    currentIdSessions.remove(i);//System.out.println("Session eliminada sesion: " + idSession);
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error 10 en " + this.getClass().getName() + ":" + e.getMessage());
        }
    }

    public boolean findIdSession(String idSessionSearch) {
        /*
         * buscar una session segun su id
         */
        boolean booleanReturn = false;
        for (int i = 0; i < currentIdSessions.size(); i++) {
            if (currentIdSessions.get(i).compareTo(idSessionSearch) == 0) {
                booleanReturn = true;
                break;
            }
        }
        return booleanReturn;
    }

    public void closeAllSessions() {
        /*
         * eliminar todas las sessiones activas (se usa cuando se realiza una 
         * restauracion de la copia de seguridad)
         */
        for (int i = 0; i < currentUserIdSessions.size(); i++) {
            currentUserIdSessions.remove(0);
            currentIdSessions.remove(0);
            i = -1;
        }


    }

    public int getMaxUserId() {
        //deteminar cual es el maximo identificador de los usuarios que esten en el sistema
        //los invitados inician en 1000
        if (currentUserIdSessions != null && !currentUserIdSessions.isEmpty()) {
            int max = 0;
            for (int i = 0; i < currentUserIdSessions.size(); i++) {
                if (currentUserIdSessions.get(i) > max) {
                    max = currentUserIdSessions.get(i);
                }
            }
            return max;
        } else {
            return 0;
        }
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
