/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.login;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
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
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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
    private ResultSet rs;
    private Statement st;
    private String user;
    private String db;
    private String db_dwh;
    private String password;
    private String server;
    private String url = "";
    private ArrayList<String> currentIdSessions = new ArrayList<>();//lista de identificadores de sesiones
    private ArrayList<Integer> currentUserIdSessions = new ArrayList<>();//lista de id de usuarios logeados
    private ArrayList<Integer> fatalReservedIdentifiers = new ArrayList<>();//identificador de registros fatales reservados
    private ArrayList<Integer> victimsReservedIdentifiers = new ArrayList<>();//identificador de registros fatales reservados
    private ArrayList<Integer> nonfatalReservedIdentifiers = new ArrayList<>();//identificador de registros fatales reservados
    private ArrayList<Integer> sivigilaVictimReservedIdentifiers = new ArrayList<>();//identificador de registros fatales reservados
    private ArrayList<Integer> sivigilaAggresorReservedIdentifiers = new ArrayList<>();//identificador de registros fatales reservados
    private String value = "-";//desde pagina de login se llama a este valor para que el objeto applicationControlMB sea visible en el contexto 
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

    @PostConstruct
    private void event() {
        connectToDb();
    }

    @PreDestroy
    private void destroySession() {
        try {
            if (conn != null && !conn.isClosed()) {
                disconnect();
            }
        } catch (Exception e) {
            //System.out.println("Termina session por inactividad 003 " + e.toString());
        }
    }

    public void disconnect() {
        try {
            if (!conn.isClosed()) {
                conn.close();
                System.out.println("Cerrada conexion a base de datos " + url + " ... OK  " + this.getClass().getName());
            }
        } catch (Exception e) {
            System.out.println("Error al cerrar conexion a base de datos " + url + " ... " + e.toString());
        }
    }
    Timer timer = new Timer(3600000, new ActionListener() {//cada hora
        //Timer timer = new Timer(60000, new ActionListener() {//cada minuto
        @Override
        public void actionPerformed(ActionEvent e) {
            actionsPerHour();
        }
    });

    private void actionsPerHour() {
        /*
         * Metodo que se ejecuta cada hora, y si no hay usuarios logeados en el sistema realiza una copia de seguridad
         */
        boolean continueProcess;
        String dateStr = "";
        String fileName = "";

        try {
//            System.out.println(""
//                    + " Revision si se crea copia de seguridad: "
//                    + " currentUserIdSessions.size()=" + currentUserIdSessions.size()
//                    + " currentIdSessions.size()=" + currentIdSessions.size());
            if (currentUserIdSessions.isEmpty() && currentIdSessions.isEmpty()) {
                continueProcess = true;
            } else {
                continueProcess = false;//System.out.println("no se realiza copia de seguridad por que existen usuarios en el sistema");
            }
            if (continueProcess) {
                if (connectToDb()) {//determinar si existe una copia de seguridad para el dia actual
                    TimeZone zonah = java.util.TimeZone.getTimeZone("GMT+1");
                    Calendar Calendario = java.util.GregorianCalendar.getInstance(zonah, new java.util.Locale("es"));
                    SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    SimpleDateFormat df2 = new java.text.SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
                    SimpleDateFormat df3 = new java.text.SimpleDateFormat("yyyy-MM-dd");
                    dateStr = df.format(Calendario.getTime());
                    fileName = "backup_" + df2.format(Calendario.getTime());
                    String sql = "SELECT * FROM backups WHERE date_backup::date = to_date('" + df3.format(Calendario.getTime()) + "','yyyy-MM-dd') AND id_backup < 11";
                    rs = consult(sql);
                    if (rs.next()) {
                        continueProcess = false;//System.out.println("no se realiza copia de seguridad por que existe una copia de seguridad para este dia");
                    }
                }
            }

            if (continueProcess) {//almaceno la informacion de la copia de seguridad creada
                try {
                    int max = 0;
                    rs = consult("SELECT MAX(id_backup) FROM backups WHERE id_backup < 11 ");
                    if (rs.next()) {
                        max = rs.getInt(1);
                    }
                    if (max == 10) {//ya existen 10 copias se debe sobreescribir una existente
                        rs = consult("SELECT MIN(date_backup) FROM backups where id_backup < 11");
                        String minDate = "";//fecha 
                        if (rs.next()) {
                            minDate = rs.getString(1);
                        }
                        if (minDate.length() != 0) {//elimino los archivos asociados a la copia de seguridad
                            rs = consult("SELECT * FROM backups where date_backup = '" + rs.getString(1) + "'");
                            if (rs.next()) {
                                File backupFile = new java.io.File(rs.getString("path_file") + rs.getString("name_backup") + "_od.backup");
                                if (backupFile.exists()) {
                                    backupFile.delete();//Borramos archivo
                                }
                                backupFile = new java.io.File(rs.getString("path_file") + rs.getString("name_backup") + "_od_dwh.backup");
                                if (backupFile.exists()) {
                                    backupFile.delete();//Borramos archivo
                                }
                                //actualizo el registro
                                non_query("UPDATE backups SET "
                                        + " name_backup = '" + fileName + "',"
                                        + " date_backup = '" + dateStr + "',"
                                        + " type_backup = 'AUTOMATICO',"
                                        + " path_file = '" + realPath + "backups/" + "'"
                                        + "WHERE "
                                        + " id_backup = " + rs.getString(1));
                            }
                        }
                    } else {//hay menos de diez copias automaticas se crea un nuevo registro
                        max++;
                        non_query(" INSERT INTO backups VALUES (" + String.valueOf(max) + ",'" + fileName + "','" + dateStr + "','AUTOMATICO','" + realPath + "backups/" + "')");
                    }
                } catch (Exception x) {
                    System.out.println("Error 4 en " + this.getClass().getName() + ":" + x.getMessage());
                    continueProcess = false;
                }
            }

            if (continueProcess) {//realizo los archivos de copia de seguridad
                try {
                    Process p;
                    ProcessBuilder pb;
                    if (new java.io.File(realPath + "backups/").exists()) {//verificar que el directorio exista                    

                        File fiRcherofile = new java.io.File(realPath + "backups/" + fileName + "_od.backup");//si archivo od existe Lo eliminamos 
                        if (fiRcherofile.exists()) {
                            fiRcherofile.delete();
                        }
                        fiRcherofile = new java.io.File(realPath + "backups/" + fileName + "_od_dwh.backup");//si archivo od_dwh existe Lo eliminamos 
                        if (fiRcherofile.exists()) {
                            fiRcherofile.delete();
                        }

                        //copia de seguridad de od(sigeodep)
                        pb = new ProcessBuilder("pg_dump", "-i", "-h", server, "-p", "5432", "-U", user, "-F", "c", "-b", "-v", "-f", realPath + "backups/" + fileName + "_od.backup", db);
                        pb.environment().put("PGPASSWORD", password);
                        pb.redirectErrorStream(true);
                        p = pb.start();
                        printOutputFromProcces(p, " crear copia de seguridad automatica : " + realPath + "backups/" + fileName + "_od.backup");

                        //copia de seguridad de od_dwh(bodega)
                        pb = new ProcessBuilder("pg_dump", "-i", "-h", server, "-p", "5432", "-U", user, "-F", "c", "-b", "-v", "-f", realPath + "backups/" + fileName + "_od_dwh.backup", db_dwh);
                        pb.environment().put("PGPASSWORD", password);
                        pb.redirectErrorStream(true);
                        p = pb.start();
                        printOutputFromProcces(p, " crear copia de seguridad automatica : " + realPath + "backups/" + fileName + "_od_dwh.backup");
                    } else {
                        System.out.println("Error 4 en " + this.getClass().getName() + ": Directorio 'backups' no existe en el servidor");
                    }
                    System.out.println("Fin copia seguridad aotomatica" + realPath + "backups/" + fileName);
                } catch (IOException x) {
                    System.out.println("Error 13 en " + this.getClass().getName() + ":" + x.getMessage());
                }
            }

        } catch (Exception e) {
            //System.out.println("Error 5 en " + this.getClass().getName() + ":" + e.getMessage());
        }
    }

    private void printOutputFromProcces(Process p, String description) {
        /*
         * mostrar por consola el progreso de un proceso externo invocado
         */
        System.out.println("\nInicia proceso " + description + " /////////////////////////////////////////");
        try {
            //CODIGO PARA MOSTRAR EL PROGESO DE LA GENERACION DEL ARCHIVO
            InputStream is = p.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String ll;
            while ((ll = br.readLine()) != null) {
                System.out.println(ll);
            }
        } catch (IOException e) {
            System.out.println("Error 99 " + e.getMessage());
        }
        System.out.println("Termina proceso " + description + " /////////////////////////////////////////\n");
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
                    System.out.println("ERROR: No se obtubo data source... applicationControlMB");
                } else {
                    conn = ds.getConnection();
                    if (conn == null) {
                        System.out.println("Error: No se obtubo conexion");
                    } else {
                        ResultSet rs1 = consult("Select * from configurations");
                        if (rs1.next()) {
                            user = rs1.getString("user_db");
                            db = rs1.getString("name_db");
                            db_dwh = rs1.getString("name_db_dwh");
                            password = rs1.getString("password_db");
                            server = rs1.getString("server_db");
                            url = "jdbc:postgresql://" + server + "/" + db;
                            try {
                                Class.forName("org.postgresql.Driver").newInstance();// seleccionar SGBD
                            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                                System.out.println("Error 1 en " + this.getClass().getName() + ":" + e.getMessage());
                            }
                            //conn.close();
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
            //System.out.println("Error 3 en " + this.getClass().getName() + ":" + e.getMessage() + "---- CONSULTA:" + query);
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
         * determina si un usario tiene una sesion iniciada 
         * idUser= identificador del usuario en la base de datos
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

    public synchronized int addNonfatalReservedIdentifiers() {
        /*
         * se reserva el identificador para un nuevo registro asi dos 
         * usuarios no ingresen un registro con igual identificador
         */
        int intReturn = 0;
        try {
            rs = consult("SELECT MAX(non_fatal_injury_id) FROM non_fatal_injuries");
            if (rs.next()) {//determino el maximo non_fatal_injury_id
                intReturn = rs.getInt(1);
                intReturn++;
            }
            boolean foundId = false;
            while (true) {//si esta en la lista de reservado se aumenta                 
                for (int i = 0; i < nonfatalReservedIdentifiers.size(); i++) {
                    if (nonfatalReservedIdentifiers.get(i) == intReturn) {
                        foundId = true;
                        break;
                    }
                }
                if (foundId) {
                    intReturn++;
                    foundId = false;
                } else {
                    nonfatalReservedIdentifiers.add(intReturn);
                    return intReturn;
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(ApplicationControlMB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return intReturn;
    }

    public synchronized void removeNonfatalReservedIdentifiers(int id) {
        for (int i = 0; i < nonfatalReservedIdentifiers.size(); i++) {
            if (nonfatalReservedIdentifiers.get(i) == id) {
                nonfatalReservedIdentifiers.remove(i);
            }
        }
    }

    public synchronized int addFatalReservedIdentifiers() {
        /*
         * se reserva el identificador para un nuevo registro asi dos 
         * usuarios no ingresen un registro con igual identificador
         */
        int intReturn = 0;
        try {
            rs = consult("SELECT MAX(fatal_injury_id) FROM fatal_injuries");
            if (rs.next()) {//determino el maximo fatal_injury_id
                intReturn = rs.getInt(1);
                intReturn++;
            }
            boolean foundId = false;
            while (true) {//si esta en la lista de reservado se aumenta                 
                for (int i = 0; i < fatalReservedIdentifiers.size(); i++) {
                    if (fatalReservedIdentifiers.get(i) == intReturn) {
                        foundId = true;
                        break;
                    }
                }
                if (foundId) {
                    intReturn++;
                    foundId = false;
                } else {
                    fatalReservedIdentifiers.add(intReturn);
                    return intReturn;
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(ApplicationControlMB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return intReturn;
    }

    public synchronized void removeFatalReservedIdentifiers(int id) {
        for (int i = 0; i < fatalReservedIdentifiers.size(); i++) {
            if (fatalReservedIdentifiers.get(i) == id) {
                fatalReservedIdentifiers.remove(i);
            }
        }
    }

    public synchronized int addVictimsReservedIdentifiers() {
        /*
         * se reserva el identificador para un nuevo registro asi dos 
         * usuarios no ingresen un registro con igual identificador
         */
        int intReturn = 0;
        try {
            rs = consult("SELECT MAX(victim_id) FROM victims");
            if (rs.next()) {//determino el maximo victim_id
                intReturn = rs.getInt(1);
                intReturn++;
            }
            boolean foundId = false;
            while (true) {//si esta en la lista de reservado se aumenta                 
                for (int i = 0; i < victimsReservedIdentifiers.size(); i++) {
                    if (victimsReservedIdentifiers.get(i) == intReturn) {
                        foundId = true;
                        break;
                    }
                }
                if (foundId) {
                    intReturn++;
                    foundId = false;
                } else {
                    victimsReservedIdentifiers.add(intReturn);
                    return intReturn;
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(ApplicationControlMB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return intReturn;

    }

    public synchronized void removeVictimsReservedIdentifiers(int id) {
        for (int i = 0; i < victimsReservedIdentifiers.size(); i++) {
            if (victimsReservedIdentifiers.get(i) == id) {
                victimsReservedIdentifiers.remove(i);
            }
        }
    }

    public synchronized int addSivigilaVictimReservedIdentifiers() {
        /*
         * se reserva el identificador para un nuevo registro asi dos 
         * usuarios no ingresen un registro con igual identificador
         */
        int intReturn = 0;
        try {
            rs = consult("SELECT MAX(sivigila_victim_id) FROM sivigila_victim");
            if (rs.next()) {//determino el maximo sivigila_victim_id
                intReturn = rs.getInt(1);
                intReturn++;
            }
            boolean foundId = false;
            while (true) {//si esta en la lista de reservado se aumenta                 
                for (int i = 0; i < sivigilaVictimReservedIdentifiers.size(); i++) {
                    if (sivigilaVictimReservedIdentifiers.get(i) == intReturn) {
                        foundId = true;
                        break;
                    }
                }
                if (foundId) {
                    intReturn++;
                    foundId = false;
                } else {
                    sivigilaVictimReservedIdentifiers.add(intReturn);
                    return intReturn;
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(ApplicationControlMB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return intReturn;

    }

    public synchronized void removeSivigilaVictimReservedIdentifiers(int id) {
        for (int i = 0; i < sivigilaVictimReservedIdentifiers.size(); i++) {
            if (sivigilaVictimReservedIdentifiers.get(i) == id) {
                sivigilaVictimReservedIdentifiers.remove(i);
            }
        }
    }

    public synchronized int addSivigilaAggresorReservedIdentifiers() {
        /*
         * se reserva el identificador para un nuevo registro asi dos 
         * usuarios no ingresen un registro con igual identificador
         */
        int intReturn = 0;
        try {
            rs = consult("SELECT MAX(sivigila_agresor_id) FROM sivigila_aggresor");
            if (rs.next()) {//determino el maximo sivigila_agresor_id
                intReturn = rs.getInt(1);
                intReturn++;
            }
            boolean foundId = false;
            while (true) {//si esta en la lista de reservado se aumenta                 
                for (int i = 0; i < sivigilaAggresorReservedIdentifiers.size(); i++) {
                    if (sivigilaAggresorReservedIdentifiers.get(i) == intReturn) {
                        foundId = true;
                        break;
                    }
                }
                if (foundId) {
                    intReturn++;
                    foundId = false;
                } else {
                    sivigilaAggresorReservedIdentifiers.add(intReturn);
                    return intReturn;
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(ApplicationControlMB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return intReturn;

    }

    public synchronized void removeSivigilaAggresorReservedIdentifiers(int id) {
        for (int i = 0; i < sivigilaAggresorReservedIdentifiers.size(); i++) {
            if (sivigilaAggresorReservedIdentifiers.get(i) == id) {
                sivigilaAggresorReservedIdentifiers.remove(i);
            }
        }
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
