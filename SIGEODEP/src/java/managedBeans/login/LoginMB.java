/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.login;

import managedBeans.filters.FilterMB;
import beans.connection.ConnectionJdbcMB;
import beans.util.StringEncryption;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import managedBeans.fileProcessing.*;
import model.dao.UsersFacade;
import model.pojo.Users;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author santos
 */
@ManagedBean(name = "loginMB")
@SessionScoped
public class LoginMB {

    private String idSession = "";//identificador de la session
    private String loginname = "";
    private String password = "";
    private String userLogin = "";
    private String userName = "";
    private String userJob = "";
    private Boolean userSystem = true;//true = usuario del sistema //false = usuario invitado
    private Users currentUser;
    private String activeIndexAcoordion1 = "-1";
    private String activeIndexAcoordion2 = "-1";
    private boolean activeSantos = false;
    private boolean permissionFatal = false;
    private boolean permissionNonFatal = false;
    private boolean permissionVif = false;
    private boolean permissionIndicators = false;
    private boolean permissionAdministrator = false;
    StringEncryption stringEncryption = new StringEncryption();
    private boolean permissionRegistryDataSection = true;
    FacesContext context;
    ConnectionJdbcMB connectionJdbcMB;
    ProjectsMB projectsMB;
    FilterMB filterMB;
    RelationshipOfVariablesMB relationshipOfVariablesMB;
    RelationshipOfValuesMB relationshipOfValuesMB;
    ApplicationControlMB applicationControlMB;
    RecordDataMB recordDataMB;
    ErrorsControlMB errorsControlMB;
    @EJB
    UsersFacade usersFacade;
    private String closeSessionDialog = "";
    private boolean autenticado = false;
    //private String realPath = "";

    public LoginMB() {
    }


    private StreamedContent fileHelp; 
    
    public StreamedContent getFileHelp() {  
        InputStream stream = ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream("/resources/help/manual.pdf");  
        fileHelp = new DefaultStreamedContent(stream, "application/pdf", "manual.pdf");
        return fileHelp;  
    } 

    @PreDestroy
    public void destroySession() {
        /*
         * antes de destruir esta clase se eliminan datos temporales a el usuario 
         * que ingreso al sistema, esto ocurre si se para el servidor o la sesion es
         * destruida por inactividad
         */
        try {
            applicationControlMB.removeSession(idSession);//elimino de la lista de usuarios actuales en el sistema
            if (connectionJdbcMB.getConn() != null && !connectionJdbcMB.getConn().isClosed()) {
                connectionJdbcMB.non_query("DELETE FROM indicators_records WHERE user_id = " + currentUser.getUserId());//elimino datos que este usuario tenga por realizacion de indicadores
                connectionJdbcMB.non_query("DELETE FROM project_history_filters WHERE project_id IN "
                        + "(SELECT project_id FROM projects WHERE user_id = " + currentUser.getUserId() + ")");            //Elimino historial de filtros para este ususario
            }
        } catch (Exception e) {
        }
    }

    public void logout1() {
        /*
         * fin de session por que se inicio una nueva session en otro equipo      
         */
        applicationControlMB.removeSession(idSession);//elimino de la lista de usuarios actuales en el sistema
        connectionJdbcMB.non_query("DELETE FROM indicators_records WHERE user_id = " + currentUser.getUserId());//elimino datos que este usuario tenga por realizacion de indicadores
        connectionJdbcMB.non_query("DELETE FROM project_history_filters WHERE project_id IN "
                + "(SELECT project_id FROM projects WHERE user_id = " + currentUser.getUserId() + ")");            //Elimino historial de filtros para este ususario
        try {
            ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
            String ctxPath = ((ServletContext) ctx.getContext()).getContextPath();
            ((HttpSession) ctx.getSession(false)).invalidate();  //System.out.println("se redirecciona");
            ctx.redirect(ctxPath + "/index.html?v=close");
        } catch (Exception ex) {//System.out.println("Excepcion cuando usuario cierra sesion sesion: " + ex.toString());
        }
    }

    public void logout2() {
        /*
         * fin de sesion dada por el usuario: botón "cerrar cesion"
         */
        applicationControlMB.removeSession(idSession);//System.out.println("Finaliza session "+loginname+" ID: "+idSession);
        connectionJdbcMB.non_query("DELETE FROM indicators_records WHERE user_id = " + currentUser.getUserId());//elimino datos que este usuario tenga por realizacion de indicadores
        connectionJdbcMB.non_query("DELETE FROM project_history_filters WHERE project_id IN "
                + "(SELECT project_id FROM projects WHERE user_id = " + currentUser.getUserId() + ")");            //Elimino historial de filtros para este ususario
        try {
            ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
            String ctxPath = ((ServletContext) ctx.getContext()).getContextPath();
            ((HttpSession) ctx.getSession(false)).invalidate();//System.out.println("se redirecciona");
            ctx.redirect(ctxPath + "/index.html");
        } catch (Exception ex) {
            System.out.println("Excepcion cuando usuario cierra sesion sesion: " + ex.toString());
        }
    }

    public void reset() {
        projectsMB.reset();
        relationshipOfVariablesMB.reset();
        relationshipOfValuesMB.reset();
        recordDataMB.reset();
        errorsControlMB.reset();
        filterMB.reset();
    }

    public String closeSessionAndLogin() {
        /*
         * terminar una session iniciada en otra terminal y continuar abriendo una nueva;
         * se usa cuando un mismo usuario intenta loguearse desde dos
         * terminales distintas, 
         */
        applicationControlMB.removeSession(currentUser.getUserId());
        return continueLogin();
    }

    private String continueLogin() {
        /*
         * instanciar todas las variables necesarias para que un usuario inicie una session
         */
        context = FacesContext.getCurrentInstance();
        connectionJdbcMB = (ConnectionJdbcMB) context.getApplication().evaluateExpressionGet(context, "#{connectionJdbcMB}", ConnectionJdbcMB.class);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("username", loginname);
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        //permisos
        String[] splitPermissions = currentUser.getPermissions().split("\t");
        for (int i = 0; i < splitPermissions.length; i++) {
            if (splitPermissions[i].compareTo("1") == 0) {
                permissionFatal = true;
            }
            if (splitPermissions[i].compareTo("2") == 0) {
                permissionNonFatal = true;
            }
            if (splitPermissions[i].compareTo("3") == 0) {
                permissionVif = true;
            }
            if (splitPermissions[i].compareTo("4") == 0) {
                permissionIndicators = true;
            }
            if (splitPermissions[i].compareTo("5") == 0) {
                permissionAdministrator = true;
            }
        }
        //determinar si se muestra o no la seccion de regsitro de datos
        if (!permissionFatal && !permissionNonFatal && !permissionVif) {
            permissionRegistryDataSection = false;
        }
        idSession = session.getId();
        userLogin = currentUser.getUserLogin();
        userName = currentUser.getUserName();
        userJob = currentUser.getUserJob();
        applicationControlMB.addSession(currentUser.getUserId(), idSession);
        activeSantos = false;
        if (userLogin.compareTo("santos") == 0) {
            activeSantos = true;
        }
        return inicializeVariables();
    }

    public void corregirDB() {
        /*
         * funcion exclusiva cuando accede el usuario santos
         * sirve para administracion del sistema
         */
        int accion = 0;
        if (accion == 2) {
            int count = 0;
            try {
                ResultSet rs = connectionJdbcMB.consult(""
                        + " SELECT non_fatal_injury_id "
                        + " FROM non_fatal_injuries "
                        + " WHERE "
                        + "    ("
                        + "    injury_id = 53 OR"//"VIOLENCIA INTRAFAMILIAR"
                        + "    injury_id = 55 OR"//"VIOLENCIA INTRAFAMILIAR LCENF"
                        + "    injury_id = 56 "//"SIVIGILA
                        + "    )"
                        + "    AND non_fatal_injury_id NOT IN "
                        + "    (SELECT DISTINCT (non_fatal_injury_id) FROM domestic_violence_action_to_take)");
                while (rs.next()) {
                    connectionJdbcMB.non_query("INSERT INTO domestic_violence_action_to_take VALUES (" + rs.getString(1) + ",13);");
                    count++;
                }

                System.out.println("Los cambios fueron: " + String.valueOf(count));
            } catch (Exception e) {
            }

        }

        if (accion == 0) {
            try {
                ResultSet rs = connectionJdbcMB.consult("Select victim_id from victims");
                int pos = 0;
                while (rs.next()) {
                    connectionJdbcMB.non_query(""
                            + " UPDATE victims "
                            + " SET "
                            + " victim_nid = " + pos + ", "
                            + " victim_name = '" + pos + "',"
                            + " victim_address = '" + pos + "',"
                            + " victim_telephone = '" + pos + "'"
                            + " WHERE"
                            + " victim_id = " + rs.getString("victim_id"));
                    System.out.println(pos);
                    pos++;
                    //break;
                }
            } catch (Exception e) {
            }
        }


        if (accion == 1) {
            try {
                //DETERMINAR EN CUANTO ESTA GEN_NN
            /*
                 ResultSet rs = connectionJdbcMB.consult("Select * from victims where victim_nid is null");
                 boolean determinada;
                 while (rs.next()) {
                 determinada = false;
                 if (rs.getString("victim_age") != null && rs.getString("victim_age").length() != 0) {
                 if (rs.getString("age_type_id") != null && rs.getString("age_type_id").length() != 0 && rs.getString("age_type_id").compareTo("1") == 0) {
                 }
                 }
                 if (determinada == false) {
                 connectionJdbcMB.consult("UPDATE victims SET  where victim_nid is null");
                 }
                 }*/
                //-------------------------------------------------------------
                //-----------correcion para sitio anatomico -------------------
                //-------------------------------------------------------------
                int count = 0;
                ResultSet rs = connectionJdbcMB.consult(""
                        + " SELECT non_fatal_injury_id "
                        + " FROM non_fatal_injuries "
                        + " WHERE "
                        + "    ("
                        + "    injury_id = 50 OR"//"VIOLENCIA INTERPERSONAL"
                        + "    injury_id = 51 OR"//"LESION EN ACCIDENTE DE TRANSITO"
                        + "    injury_id = 52 OR"//"INTENCIONAL AUTOINFLINGIDA"
                        + "    injury_id = 53 OR"//"VIOLENCIA INTRAFAMILIAR"
                        + "    injury_id = 54 OR"//"NO INTENCIONAL"
                        + "    injury_id = 55"//"VIOLENCIA INTRAFAMILIAR LCENF"
                        + "    )"
                        + "    AND non_fatal_injury_id NOT IN "
                        + "    (SELECT DISTINCT (non_fatal_injury_id) FROM non_fatal_anatomical_location)");
                while (rs.next()) {
                    //connectionJdbcMB.non_query("INSERT INTO non_fatal_anatomical_location VALUES (99," + rs.getString(1) + ");");
                    System.out.println("Operaciones (A): " + String.valueOf(count));
                    count++;
                }
                //-------------------------------------------------------------
                //-----------naturaleza de lesion -----------------------------
                //-------------------------------------------------------------
                rs = connectionJdbcMB.consult(""
                        + " SELECT non_fatal_injury_id "
                        + " FROM non_fatal_injuries "
                        + " WHERE "
                        + "    ("
                        + "    injury_id = 50 OR"//"VIOLENCIA INTERPERSONAL"
                        + "    injury_id = 51 OR"//"LESION EN ACCIDENTE DE TRANSITO"
                        + "    injury_id = 52 OR"//"INTENCIONAL AUTOINFLINGIDA"
                        + "    injury_id = 53 OR"//"VIOLENCIA INTRAFAMILIAR"
                        + "    injury_id = 54 OR"//"NO INTENCIONAL"
                        + "    injury_id = 55 "//"VIOLENCIA INTRAFAMILIAR LCENF"
                        + "    )"
                        + "    AND non_fatal_injury_id NOT IN "
                        + "    (SELECT DISTINCT (non_fatal_injury_id) FROM non_fatal_kind_of_injury)");
                while (rs.next()) {
                    //connectionJdbcMB.non_query("INSERT INTO non_fatal_kind_of_injury VALUES (99," + rs.getString(1) + ");");
                    System.out.println("Operaciones (B): " + String.valueOf(count));
                    count++;
                }
                //-------------------------------------------------------------
                //-----------tipo de maltrato -----------------------------
                //-------------------------------------------------------------
                rs = connectionJdbcMB.consult(""
                        + " SELECT non_fatal_injury_id "
                        + " FROM non_fatal_injuries "
                        + " WHERE "
                        + "    ("
                        + "    injury_id = 53 OR"//"VIOLENCIA INTRAFAMILIAR"
                        + "    injury_id = 55 OR"//"VIOLENCIA INTRAFAMILIAR LCENF"
                        + "    injury_id = 56"//"SIVIGILA-VIF" (tambien va esta)
                        + "    )"
                        + "    AND non_fatal_injury_id NOT IN "
                        + "    (SELECT DISTINCT (non_fatal_injury_id) FROM domestic_violence_abuse_type)");
                while (rs.next()) {
                    //connectionJdbcMB.non_query("INSERT INTO domestic_violence_abuse_type VALUES (" + rs.getString(1) + ",7);");
                    System.out.println("Operaciones (C): " + String.valueOf(count));
                    count++;
                }
                //-------------------------------------------------------------
                //-----------tipo de agresor -----------------------------
                //-------------------------------------------------------------
                rs = connectionJdbcMB.consult(""
                        + " SELECT non_fatal_injury_id "
                        + " FROM non_fatal_injuries "
                        + " WHERE "
                        + "    ("
                        + "    injury_id = 53 OR"//"VIOLENCIA INTRAFAMILIAR"
                        + "    injury_id = 55 "//"VIOLENCIA INTRAFAMILIAR LCENF"
                        + "    )"
                        + "    AND non_fatal_injury_id NOT IN "
                        + "    (SELECT DISTINCT (non_fatal_injury_id) FROM domestic_violence_aggressor_type)");
                while (rs.next()) {
                    //connectionJdbcMB.non_query("INSERT INTO domestic_violence_aggressor_type VALUES (" + rs.getString(1) + ",9);");
                    System.out.println("Operaciones (D): " + String.valueOf(count));
                    count++;
                }
                //-------------------------------------------------------------
                //-----------acciones a realizar -----------------------------
                //-------------------------------------------------------------
                rs = connectionJdbcMB.consult(""
                        + " SELECT non_fatal_injury_id "
                        + " FROM non_fatal_injuries "
                        + " WHERE "
                        + "    ("
                        + "    injury_id = 53 OR"//"VIOLENCIA INTRAFAMILIAR"
                        + "    injury_id = 55 "//"VIOLENCIA INTRAFAMILIAR LCENF"
                        + "    )"
                        + "    AND non_fatal_injury_id NOT IN "
                        + "    (SELECT DISTINCT (non_fatal_injury_id) FROM domestic_violence_action_to_take)");
                while (rs.next()) {
                    //connectionJdbcMB.non_query("INSERT INTO domestic_violence_action_to_take VALUES (" + rs.getString(1) + ",13);");
                    System.out.println("Operaciones (E): " + String.valueOf(count));
                    count++;
                }
                //-------------------------------------------------------------
                //-----------acciones en salud publica ------------------------
                //-------------------------------------------------------------
                rs = connectionJdbcMB.consult(""
                        + " SELECT non_fatal_injury_id "
                        + " FROM non_fatal_injuries "
                        + " WHERE "
                        + "    ("
                        + "    injury_id = 56"//"SIVIGILA-VIF"
                        + "    )"
                        + "    AND non_fatal_injury_id NOT IN "
                        + "    (SELECT DISTINCT (non_fatal_injury_id) FROM sivigila_event_public_health)");
                while (rs.next()) {
                    //connectionJdbcMB.non_query("INSERT INTO sivigila_event_public_health VALUES (" + rs.getString(1) + ",8);");
                    System.out.println("Operaciones (F): " + String.valueOf(count));
                    count++;
                }
                //-------------------------------------------------------------
                //-----------elementos de seguridad------------------------
                //-------------------------------------------------------------
                ResultSet rs2;
                rs = connectionJdbcMB.consult(""
                        + " SELECT non_fatal_injury_id "
                        + " FROM non_fatal_injuries "
                        + " WHERE "
                        + "    ("
                        + "    injury_id = 51 "//"LESION EN ACCIDENTE DE TRANSITO"
                        + "    )"
                        + "    AND non_fatal_injury_id NOT IN "
                        + "    (SELECT DISTINCT (non_fatal_injury_id) FROM non_fatal_transport_security_element)");
                while (rs.next()) {
//                rs2 = connectionJdbcMB.consult("SELECT * FROM non_fatal_transport WHERE non_fatal_injury_id = " + rs.getString(1));
//                if (!rs2.next()) {
//                    connectionJdbcMB.non_query("INSERT INTO non_fatal_transport VALUES (null,null,null," + rs.getString(1) + ");");
//                    connectionJdbcMB.non_query("INSERT INTO non_fatal_transport_security_element VALUES (8," + rs.getString(1) + ");");
//                } else {
//                    connectionJdbcMB.non_query("INSERT INTO non_fatal_transport_security_element VALUES (8," + rs.getString(1) + ");");
//                }

                    System.out.println("Operaciones (G): " + String.valueOf(count));
                    count++;
                }

                System.out.println("Proceso de mantenimiento de base de datos realizado, PROCESOS: " + String.valueOf(count));
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "correcto", "operaciones de correccion y mantenimiento realizadas");
            } catch (Exception e) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "" + e.getMessage());
            }
        }
    }

    private String inicializeVariables() {
        if (connectionJdbcMB.connectToDb()) {
            connectionJdbcMB.setCurrentUser(currentUser);
            projectsMB = (ProjectsMB) context.getApplication().evaluateExpressionGet(context, "#{projectsMB}", ProjectsMB.class);
            filterMB = (FilterMB) context.getApplication().evaluateExpressionGet(context, "#{filterMB}", FilterMB.class);
            relationshipOfVariablesMB = (RelationshipOfVariablesMB) context.getApplication().evaluateExpressionGet(context, "#{relationshipOfVariablesMB}", RelationshipOfVariablesMB.class);
            relationshipOfValuesMB = (RelationshipOfValuesMB) context.getApplication().evaluateExpressionGet(context, "#{relationshipOfValuesMB}", RelationshipOfValuesMB.class);
            recordDataMB = (RecordDataMB) context.getApplication().evaluateExpressionGet(context, "#{recordDataMB}", RecordDataMB.class);
            errorsControlMB = (ErrorsControlMB) context.getApplication().evaluateExpressionGet(context, "#{errorsControlMB}", ErrorsControlMB.class);

            projectsMB.reset();
            relationshipOfVariablesMB.reset();

            recordDataMB.setErrorsControlMB(errorsControlMB);
            recordDataMB.setLoginMB(this);
            recordDataMB.setProjectsMB(projectsMB);

            relationshipOfValuesMB.setProjectsMB(projectsMB);
            relationshipOfVariablesMB.setRelationshipOfValuesMB(relationshipOfValuesMB);
            relationshipOfVariablesMB.setProjectsMB(projectsMB);

            errorsControlMB.setRelationshipOfVariablesMB(relationshipOfVariablesMB);
            errorsControlMB.setProjectsMB(projectsMB);
            projectsMB.setRelationshipOfVariablesMB(relationshipOfVariablesMB);

            if (currentUser.getProjectId() != null) {
                projectsMB.openProject(currentUser.getProjectId());
            }

            filterMB.setProjectsMB(projectsMB);
            filterMB.reset();

            autenticado = true;

            ResultSet rs = connectionJdbcMB.consult("SELECT * FROM configurations");
            try {
                rs.next();
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("db_user", rs.getString("user_db"));
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("db_pass", rs.getString("password_db"));
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("db_host", rs.getString("server_db"));
                FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("db_name", rs.getString("name_db"));
            } catch (SQLException ex) {
                Logger.getLogger(LoginMB.class.getName()).log(Level.SEVERE, null, ex);
            }

            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto!!", "Se ha ingresado al sistema");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return "homePage";
        } else {
            FacesMessage msg2 = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Se debe crear una conexión");
            FacesContext.getCurrentInstance().addMessage(null, msg2);
            password = "";
            return "indexConfiguration";
        }
    }

    public String CheckValidInvited() {
        /*
         * permitir el acceso de un usuario como invitado
         * retorna la pagina a la cual se debe dirigir una 
         * ves se determine si se puede ingresar o no
         */
        //obtengo el bean de aplicacion                
        ExternalContext contexto = FacesContext.getCurrentInstance().getExternalContext();
        applicationControlMB = (ApplicationControlMB) contexto.getApplicationMap().get("applicationControlMB");
        context = FacesContext.getCurrentInstance();
        connectionJdbcMB = (ConnectionJdbcMB) context.getApplication().evaluateExpressionGet(context, "#{connectionJdbcMB}", ConnectionJdbcMB.class);
        userSystem = false;//es usuario invitado (no es usuario del sistema)
        HttpSession session = (HttpSession) contexto.getSession(false);

        //permisos
        permissionFatal = false;
        permissionNonFatal = false;
        permissionVif = false;
        permissionIndicators = true;
        permissionAdministrator = false;
        permissionRegistryDataSection = false;

        idSession = session.getId();

        if (applicationControlMB.findIdSession(idSession)) {//ya existe esta sesion solo se ingresa a la aplicacion
            return "homePage";
        } else {//no existe sesion se debe crear el usuario temporal         
            int max = applicationControlMB.getMaxUserId();
            if (max < 1001) {
                max = 1001;//le aumento mil para que no tenga un id de los usuarios registrados del sistema
            } else {
                max = max + 1;
            }
            currentUser = new Users(max, "Invitado: " + String.valueOf(max - 1000), "123", "USUARIO");//nuevo ususario temporal
            currentUser.setActive(true);
            loginname = "Invitado: " + String.valueOf(max - 1000);//el id de los invitados inicia en 1000, se les rest mil para que aparezcan como 1,2...etc y no como 1001,1002....etc
            contexto.getSessionMap().put("username", loginname);
            userLogin = currentUser.getUserLogin();
            userName = currentUser.getUserName();
            userJob = "Analista";
            applicationControlMB.addSession(currentUser.getUserId(), idSession);
            return inicializeVariables();
        }
    }

    public String CheckValidUser() {
        /*
         * determinar si el usuario puede acceder al sistema determinando si exite
         * el login, clave y la cuenta esta activa
         */
        closeSessionDialog = "-";
        password = stringEncryption.getStringMessageDigest(password, "SHA-1");
        currentUser = usersFacade.findUser(loginname, password);
        userSystem = true;//es usuario del sistema (no es usuario invitado)
        if (currentUser == null) {
            //FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "SESION FINALIZADA", "La sesión fue finalizada por que se inició una nueva sesión para el mismo usuario");
            //FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "SESION FINALIZADA", "Después de 20 minutos de inactividad el sistema finaliza la sesión automáticamente, por favor presione el botón: ' ingresar a la aplicación ' para acceder nuevamente.");
            //FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "ACCESO DENEGADO", "La sesión ha finalizado por inactividad o no se ha iniciado una sesión.");
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Incorrecto Usuario o Clave");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            password = "";
            return "";
        }
        if (!currentUser.getActive()) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "La cuenta de este usuario se encuentra Inactiva");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            password = "";
            return "";
        }

        ExternalContext contexto = FacesContext.getCurrentInstance().getExternalContext();
        applicationControlMB = (ApplicationControlMB) contexto.getApplicationMap().get("applicationControlMB");
        //determino si el usuario tiene una session alctiva
        if (applicationControlMB.hasLogged(currentUser.getUserId())) {//System.out.println("Ingreso rechazado, ya tiene otra session activa");
            closeSessionDialog = "closeSessionDialog.show()";//dialog que permite terminar sesion desde otra terminal
            return "";//no dirigir a ninguna pagina
        } else {
            return continueLogin();
        }
    }

    public String getLoginname() {
        return loginname;
    }

    public void setLoginname(String loginname) {
        this.loginname = loginname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserJob() {
        return userJob;
    }

    public void setUserJob(String userJob) {
        this.userJob = userJob;
    }

    public Users getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Users currentUser) {
        this.currentUser = currentUser;
    }

    public String getActiveIndexAcoordion1() {
        return activeIndexAcoordion1;
    }

    public void setActiveIndexAcoordion1(String activeIndexAcoordion1) {
        this.activeIndexAcoordion1 = activeIndexAcoordion1;
    }

    public String getActiveIndexAcoordion2() {
        return activeIndexAcoordion2;
    }

    public void setActiveIndexAcoordion2(String activeIndexAcoordion2) {
        this.activeIndexAcoordion2 = activeIndexAcoordion2;
    }

    public String getCloseSessionDialog() {
        return closeSessionDialog;
    }

    public void setCloseSessionDialog(String closeSessionDialog) {
        this.closeSessionDialog = closeSessionDialog;
    }

    public String getIdSession() {
        return idSession;
    }

    public void setIdSession(String idSession) {
        this.idSession = idSession;
    }

    public boolean isPermissionFatal() {
        return permissionFatal;
    }

    public void setPermissionFatal(boolean permissionFatal) {
        this.permissionFatal = permissionFatal;
    }

    public boolean isPermissionNonFatal() {
        return permissionNonFatal;
    }

    public void setPermissionNonFatal(boolean permissionNonFatal) {
        this.permissionNonFatal = permissionNonFatal;
    }

    public boolean isPermissionVif() {
        return permissionVif;
    }

    public void setPermissionVif(boolean permissionVif) {
        this.permissionVif = permissionVif;
    }

    public boolean isPermissionIndicators() {
        return permissionIndicators;
    }

    public void setPermissionIndicators(boolean permissionIndicators) {
        this.permissionIndicators = permissionIndicators;
    }

    public boolean isPermissionAdministrator() {
        return permissionAdministrator;
    }

    public void setPermissionAdministrator(boolean permissionAdministrator) {
        this.permissionAdministrator = permissionAdministrator;
    }

//    public boolean isDisableNonFatalSection() {
//        return disableNonFatalSection;
//    }
//
//    public void setDisableNonFatalSection(boolean disableNonFatalSection) {
//        this.disableNonFatalSection = disableNonFatalSection;
//    }
//
//    public boolean isDisableFatalSection() {
//        return disableFatalSection;
//    }
//
//    public void setDisableFatalSection(boolean disableFatalSection) {
//        this.disableFatalSection = disableFatalSection;
//    }
//
//    public boolean isDisableVifSection() {
//        return disableVifSection;
//    }
//
//    public void setDisableVifSection(boolean disableVifSection) {
//        this.disableVifSection = disableVifSection;
//    }
//
//    public boolean isDisableIndicatorsSection() {
//        return disableIndicatorsSection;
//    }
//
//    public void setDisableIndicatorsSection(boolean disableIndicatorsSection) {
//        this.disableIndicatorsSection = disableIndicatorsSection;
//    }
//
//    public boolean isDisableAdministratorSection() {
//        return disableAdministratorSection;
//    }
//
//    public void setDisableAdministratorSection(boolean disableAdministratorSection) {
//        this.disableAdministratorSection = disableAdministratorSection;
//    }
    public boolean isPermissionRegistryDataSection() {
        return permissionRegistryDataSection;
    }

    public void setPermissionRegistryDataSection(boolean permissionRegistryDataSection) {
        this.permissionRegistryDataSection = permissionRegistryDataSection;
    }

    public boolean isAutenticado() {
        return autenticado;
    }

    public void setAutenticado(boolean autenticado) {
        this.autenticado = autenticado;
    }

    public Boolean getUserSystem() {
        return userSystem;
    }

    public void setUserSystem(Boolean userSystem) {
        this.userSystem = userSystem;
    }

    public boolean isActiveSantos() {
        return activeSantos;
    }

    public void setActiveSantos(boolean activeSantos) {
        this.activeSantos = activeSantos;
    }
}
