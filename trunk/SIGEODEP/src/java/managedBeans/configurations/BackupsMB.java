/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.configurations;

import beans.connection.ConnectionJdbcMB;
import beans.util.RowDataTable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import managedBeans.login.ApplicationControlMB;

/**
 *
 * @author santos
 */
@ManagedBean(name = "backupsMB")
@SessionScoped
public class BackupsMB {

    private ConnectionJdbcMB connectionJdbcMB;
    ApplicationControlMB applicationControlMB;
    private List<RowDataTable> rowDataTableList;
    private RowDataTable selectedRowDataTable;
    private String newName = "";//Nombre del la copia de seguridad.
    private String realPath = "";
    String IP = "";
    String user = "";
    String dbase = "";
    String password = "";

    public BackupsMB() {
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        realPath = (String) servletContext.getRealPath("/"); // Sustituye "/" por el directorio ej: "/upload"
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);

        ExternalContext contexto = FacesContext.getCurrentInstance().getExternalContext();
        applicationControlMB = (ApplicationControlMB) contexto.getApplicationMap().get("applicationControlMB");

        HttpSession session = (HttpSession) contexto.getSession(false);
        user = session.getAttribute("db_user").toString();
        dbase = session.getAttribute("db_name").toString();
        IP = session.getAttribute("db_host").toString();
        password = session.getAttribute("db_pass").toString();
    }

    public void reset() {
        rowDataTableList = new ArrayList<>();
        selectedRowDataTable = null;
        newName = "";
        createDynamicTable();
    }

    private boolean backupPGSQL(String serverPath, String fileName) {
        boolean booleanReturn = false;
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
                pb = new ProcessBuilder("pg_dump", "-i", "-h", IP, "-p", "5432", "-U", user, "-F", "c", "-b", "-v", "-f", fechafile.toString(), dbase, "-T", "backups");
                pb.environment().put("PGPASSWORD", password);
                pb.redirectErrorStream(true);
                System.out.println("Inicia creacion de copia de seguridad: " + fechafile.toString());
                p = pb.start();
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
                    System.out.println("Error 1 en " + this.getClass().getName() + ":" + e.getMessage());
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
                }
                booleanReturn = true;
                System.out.println("Finaliza creacion de copia de seguridad: " + fechafile.toString());
                //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Correcto", "La copia de seguridad ha sido creada correctamente"));
            }
        } catch (IOException x) {
            System.out.println("Error 2 en " + this.getClass().getName() + ":" + x.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", x.getMessage()));
        }
        return booleanReturn;
    }

    public void saveBackupInfo(String serverPath, String fileName) {
        /* 
         * almacena nombre, hubicacion y fecha de creacion de la copia de seguridad en la base de datos
         */
        try {
            ResultSet rs = connectionJdbcMB.consult("SELECT MAX(id_backup) FROM backups");
            if (rs.next()) {
                int max = rs.getInt(1);
                if (max < 11) {
                    max = 11;
                } else {
                    max++;
                }
                TimeZone zonah = java.util.TimeZone.getTimeZone("GMT+1");
                Calendar Calendario = java.util.GregorianCalendar.getInstance(zonah, new java.util.Locale("es"));
                SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateStr = df.format(Calendario.getTime());
                connectionJdbcMB.non_query(" INSERT INTO backups VALUES (" + String.valueOf(max) + ",'" + fileName + "','" + dateStr + "','MANUAL','" + serverPath + "')");
            }
        } catch (Exception e) {
            System.out.println("Error 3 en " + this.getClass().getName() + ":" + e.getMessage());
        }
    }

    public void createBackupClick() {
        if (newName != null && newName.trim().length() != 0) {
            //determinar si el nombre ya esta ingresado
            ResultSet rs = connectionJdbcMB.consult("SELECT * FROM backups WHERE name_backup ILIKE '" + newName.trim() + "'");
            try {
                if (rs.next()) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Ya existe una copia de seguridad con el nombre ingresado"));
                } else {
                    if (backupPGSQL(realPath + "backups/", newName)) {
                        saveBackupInfo(realPath + "backups/", newName);
                        newName = "";
                        createDynamicTable();
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "La copia de seguridad ha sido creada correctamente"));
                    }
                }
            } catch (Exception x) {
                System.out.println("Error 4 en " + this.getClass().getName() + ":" + x.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", x.getMessage()));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe escribir un nombre para la copia de seguridad"));
        }
    }

    private void removeAllTables() {
        try {
            DatabaseMetaData metaData = connectionJdbcMB.getConn().getMetaData();
            String[] params = new String[1];
            params[0] = "TABLE";
            ResultSet rs = metaData.getTables(null, "public", null, params);
            ArrayList<String> tableNames = new ArrayList<>();
            while (rs.next()) {
                if (rs.getString("TABLE_NAME").compareTo("backups") != 0) {
                    tableNames.add(rs.getString("TABLE_NAME"));
                }
            }
            int errors = 1;
            connectionJdbcMB.setShowMessages(false);//no mostrar mensajes
            while (errors != 0) {
                errors = 0;
                for (int j = 0; j < tableNames.size(); j++) {
                    connectionJdbcMB.non_query("DROP TABLE IF EXISTS " + tableNames.get(j));
                    if (connectionJdbcMB.getMsj().startsWith("ERROR")) {
                        errors++;
                    } else {
                        tableNames.remove(j);
                        j--;
                    }
                }
                System.err.println("Errores :" + errors);
            }
        } catch (SQLException ex) {
            Logger.getLogger(BackupsMB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void restoreBackupClick() {
        if (selectedRowDataTable != null) {
            //reaizo la eliminacion de todas las tablas
            removeAllTables();
            //realizo la restauracion de la copia de seguridad
            restorePGSQL(selectedRowDataTable.getColumn5(), selectedRowDataTable.getColumn2());
            //finalizo todas las sessiones
            applicationControlMB.closeAllSessions();
            //me redirijo a la pagina inicial
            try {
                ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
                String ctxPath = ((ServletContext) ctx.getContext()).getContextPath();
                ((HttpSession) ctx.getSession(false)).invalidate();//System.out.println("se redirecciona");
                ctx.redirect(ctxPath + "/index.html");
            } catch (Exception ex) {
                System.out.println("Excepcion cuando usuario cierra sesion sesion: " + ex.toString());
            }

        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar una copia de seguridad para realizar la restauración"));
        }
    }

    public void restorePGSQL(String serverPath, String fileName) {
        try {
            Runtime r = Runtime.getRuntime();
            Process p;
            ProcessBuilder pb;
            java.io.File ficherofile = new java.io.File(serverPath + fileName + ".backup");
            String rutaCT = serverPath + fileName + ".backup";
            if (ficherofile.exists()) {//Probamos a ver si existe ese ultimo dato                    
                r = Runtime.getRuntime();
                pb = new ProcessBuilder("pg_restore", "-i", "-h", IP, "-p", "5432", "-U", user, "-d", dbase, "-v", rutaCT);
                pb.environment().put("PGPASSWORD", password);
                pb.redirectErrorStream(true);
                System.out.println("Inicia restauracion de copia de seguridad: " + rutaCT);
                p = pb.start();
                String outString = "";
                try {
                    //CODIGO PARA MOSTRAR EL PROGESO DE LA GENERACION DEL ARCHIVO
                    InputStream is = p.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    String ll;
                    while ((ll = br.readLine()) != null) {
                        outString = ll;//System.out.println(ll);
                    }
                } catch (IOException e) {
                    System.out.println("Error 10 en " + this.getClass().getName() + ":" + e.getMessage());
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", e.getMessage()));
                }
                System.out.println("\nFinaliza creacion de copia de seguridad.... " + rutaCT + "......   " + outString);
                //System.out.println("Finaliza restauracion de copia de seguridad: " + rutaCT);
            } else {
                System.err.println("No se encontro el archivo");
            }
        } catch (IOException x) {            
            System.err.println("Caught: " + x.getMessage());
        }
    }

    public void deleteBackupClick() {
        if (selectedRowDataTable != null) {

            String filePath = selectedRowDataTable.getColumn5() + selectedRowDataTable.getColumn2() + ".backup";
            java.io.File ficherofile = new java.io.File(filePath);
            if (ficherofile.exists()) {
                ficherofile.delete();//elimino el archivo
                try {
                    connectionJdbcMB.non_query("DELETE FROM backups WHERE id_backup = " + selectedRowDataTable.getColumn1());
                    createDynamicTable();
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "La copia de seguridad se ha eliminado correctamente"));
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            } else {
                System.out.println("No se localizo el archivo: " + filePath);
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar una copia de seguridad para realizar la eliminación"));
        }
    }

    private void createDynamicTable() {
        ResultSet rs = connectionJdbcMB.consult("SELECT * FROM backups ORDER BY id_backup");
        try {
            rowDataTableList = new ArrayList<>();
            while (rs.next()) {
                rowDataTableList.add(new RowDataTable(
                        rs.getString("id_backup"),
                        rs.getString("name_backup"),
                        rs.getString("date_backup"),
                        rs.getString("type_backup"),
                        rs.getString("path_file")));

            }
        } catch (Exception e) {
            System.out.println("Error 5 en " + this.getClass().getName() + ":" + e.getMessage());
        }
    }

    public List<RowDataTable> getRowDataTableList() {
        return rowDataTableList;
    }

    public void setRowDataTableList(List<RowDataTable> rowDataTableList) {
        this.rowDataTableList = rowDataTableList;
    }

    public RowDataTable getSelectedRowDataTable() {
        return selectedRowDataTable;
    }

    public void setSelectedRowDataTable(RowDataTable selectedRowDataTable) {
        this.selectedRowDataTable = selectedRowDataTable;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }
}
