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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.swing.Timer;

/**
 *
 * @author santos
 */
@ManagedBean(name = "applicationControlMB")
@ApplicationScoped
public class ApplicationControlMB {

    private ArrayList<String> currentIdSessions = new ArrayList<String>();//lista de identificadores de sesiones
    private ArrayList<Integer> currentUserIdSessions = new ArrayList<Integer>();//lista de id de usuarios logeados
    private String value = "-";
    private String realPath = "";
    private boolean createCopy = false;
    
    Timer timer = new Timer(3000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            actionsPerHour();
        }
    });

    public ApplicationControlMB() {
        //System.out.println("Ingreso a controlador de sessiones");
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        realPath = (String) servletContext.getRealPath("/"); // Sustituye "/" por el directorio ej: "/upload"
        //timer.start();//quitar comentario para que se creen las copias de seguridad
    }

    private void actionsPerHour() {
        Calendar a = new GregorianCalendar();
        //String fileName = "backup_" + String.valueOf(a.get(Calendar.DATE)) + "_" + String.valueOf(a.get(Calendar.MONTH)) + "_" + String.valueOf(a.get(Calendar.YEAR)) + "_" + String.valueOf(a.get(Calendar.HOUR_OF_DAY)) + "_" + String.valueOf(a.get(Calendar.MINUTE)) + "_" + String.valueOf(a.get(Calendar.SECOND)) + ".slq";
        //System.out.println("backup_" + String.valueOf(a.get(Calendar.DATE)) + "_" + String.valueOf(a.get(Calendar.MONTH)) + "_" + String.valueOf(a.get(Calendar.YEAR)) + "_" + String.valueOf(a.get(Calendar.HOUR_OF_DAY)) + "_" + String.valueOf(a.get(Calendar.MINUTE)) + "_" + String.valueOf(a.get(Calendar.SECOND)) + ".slq");
        if (createCopy == false) {
            backupPGSQL();
            createCopy = true;
        }
    }

    private void backupPGSQL() {
        try {
            
            Runtime r = Runtime.getRuntime();            
            String rutaCT = realPath+"\\backups\\";
            String IP = "localhost";
            String user = "postgres";
            String dbase = "od";
            String password = "1234";
            Process p;
            ProcessBuilder pb;
            java.util.TimeZone zonah = java.util.TimeZone.getTimeZone("GMT+1");
            java.util.Calendar Calendario = java.util.GregorianCalendar.getInstance(zonah, new java.util.Locale("es"));
            java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyyMMdd");
            StringBuilder date = new StringBuilder();
            date.append(df.format(Calendario.getTime()));
            java.io.File file = new java.io.File(rutaCT);            
            if (file.exists()) {/*We test if the path to our programs exists*/
                /*We then test if the file weвЂ™re going to generate exists too. If it exists we will delete it*/
                StringBuilder fechafile = new StringBuilder();
                fechafile.append(rutaCT);
                fechafile.append(date.toString());
                fechafile.append(".backup");
                java.io.File ficherofile = new java.io.File(fechafile.toString());                
                if (ficherofile.exists()) {//Probamos a ver si existe ese ultimo dato                    
                    ficherofile.delete();//Lo Borramos
                }

                r = Runtime.getRuntime();                
                //pb = new ProcessBuilder("\"C:\\Program Files\\PostgreSQL\\9.2\\bin\\pg_dump.exe\"", "-f", fechafile.toString(), "-F", "c", "-Z", "9", "-v", "-o", "-h", IP, "-U", user, dbase);
                pb = new ProcessBuilder("pg_dump.exe", "-f", fechafile.toString(), "-F", "c", "-Z", "9", "-v", "-o", "-h", IP, "-U", user, dbase);
                pb.environment().put("PGPASSWORD", password);
                pb.redirectErrorStream(true);
                p = pb.start();
                try {
                    InputStream is = p.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    String ll;
                    while ((ll = br.readLine()) != null) {
                        System.out.println(ll);
                    }
                } catch (IOException e) {
                    System.out.println(e);
                }
            }

        } catch (IOException x) {
            System.err.println("Could not invoke browser, command=");
            System.err.println("Caught: " + x.getMessage());
        }
    }

    public boolean hasLogged(int idUser) {
        /*
         * determina si un usario tiene una sesion iniciada
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
         * adicionar a la lista de sesiones en proceso actualmente
         */
        currentIdSessions.add(idSession);
        currentUserIdSessions.add(idUser);//System.out.println("Agregada Nueva sesion: " + idSession + "  usuario: " + idUser);
    }

    public void removeSession(int idUser) {
        /*
         * eliminar de la lista de sesiones actuales dependiento del id del usuario
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
