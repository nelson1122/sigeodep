/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.login;

import beans.connection.ConnectionJdbcMB;
import beans.util.StringEncryption;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
import managedBeans.filters.FilterMB;
import model.dao.UsersFacade;
import model.pojo.Users;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Years;
import org.primefaces.context.RequestContext;
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
    //private String closeSessionDialog = "";
    private boolean autenticado = false;
    //private String realPath = "";

    public LoginMB() {
    }
    private StreamedContent fileHelp;

    public StreamedContent getFileHelp() {
        InputStream stream = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream("/resources/help/manual.pdf");
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

        boolean continuar = true;
        double num;

        if (continuar) {
            try {
                ResultSet rs = connectionJdbcMB.consult(""
                        + " select "
                        + "    non_fatal_injury_id,"
                        + "    injury_date,"
                        + "    checkup_date "
                        + " from "
                        + "    non_fatal_injuries "
                        + " where "
                        + "    injury_date > checkup_date;");
                int count = 0;
                while (rs.next()) {
                    String nonFatalInjuryId = rs.getString(1);
                    String injuryDate = rs.getString(2);
                    String checkupDate = rs.getString(3);
                    connectionJdbcMB.non_query(""
                            + " UPDATE "
                            + "    non_fatal_injuries "
                            + " SET"
                            + "    injury_date = to_date('" + checkupDate + "','yyyy/MM/dd'), "
                            + "    checkup_date = to_date('" + injuryDate + "','yyyy/MM/dd') "
                            + " WHERE "
                            + "    non_fatal_injury_id = "+nonFatalInjuryId );
                    System.out.println(nonFatalInjuryId + "\t\t" + injuryDate + "\t" + checkupDate);
                    count++;
                }
                System.out.println("Registros prcesados: " + count);
            } catch (Exception e) {
            }

        }


//        if (continuar) {
//            int countNecesitaCalculo = 0;
//            int countNoNecesitaCalculo = 0;
//            int fuenteDeDatos = 0;
//
//            //--------------------------------------------------
//            //--------- accion a realizar en VIF ---21483------------
//            //--------------------------------------------------
//            //*
//            try {
//                ArrayList<String> conjuntosDeRegistrosBuscados = new ArrayList<>();
//                ResultSet rs = connectionJdbcMB.consult(""
//                        + " SELECT "
//                        + "   * "
//                        + " FROM "
//                        + "   public.ungrouped_tags "
//                        + " WHERE "
//                        + "   form_id like 'SCC-F-033'");
//                while (rs.next()) {
//                    conjuntosDeRegistrosBuscados.add(rs.getString("ungrouped_tag_id"));
//                }
//
//                for (String idConjunto : conjuntosDeRegistrosBuscados) {
//                    //determino la fuente que se uso para este registro  
//
//                    rs = connectionJdbcMB.consult(""
//                            + " SELECT "
//                            + "    projects.source_id "
//                            + " FROM "
//                            + "    projects, "
//                            + "    ungrouped_tags "
//                            + " WHERE "
//                            + "    projects.project_name like ungrouped_tags.ungrouped_tag_name AND "
//                            + "    ungrouped_tags.ungrouped_tag_id = " + idConjunto);
//                    if (rs.next()) {//puede ser que haya sido creado por formulario por lo que no tiene conjunto
//                        fuenteDeDatos = rs.getShort(1);//fuente de datos del proyecto
//                    } else {
//                        fuenteDeDatos = 14;//fuente de datos del proyecto ATENCION EN SALUD
//                    }
//
//
//                    //determino todos los registros para este conjunto
//                    rs = connectionJdbcMB.consult(""
//                            + " SELECT "
//                            + "    * "
//                            + " FROM "
//                            + "    victims, "
//                            + "    non_fatal_injuries "
//                            + " WHERE "
//                            + "    victims.victim_id = non_fatal_injuries.victim_id and "
//                            + "    victims.first_tag_id = " + idConjunto);
//
//                    while (rs.next()) {
//                        ResultSet rs2 = connectionJdbcMB.consult(""
//                                + " SELECT "
//                                + "   * "
//                                + " FROM "
//                                + "   domestic_violence_action_to_take "
//                                + " WHERE "
//                                + "   non_fatal_injury_id = " + rs.getString("non_fatal_injury_id"));
//                        ArrayList<String> encontrados = new ArrayList<>();
//                        while (rs2.next()) {
//                            encontrados.add(rs2.getString("action_id"));
//                        }
//
//
//                        boolean necesitaCalculo = false;
//                        boolean necesitaEliminacion = false;
//                        if (encontrados.isEmpty()) {
//                            necesitaCalculo = true;//necesita calculo de accion a realizar
//                        } else if (encontrados.size() == 1) {
//                            if (encontrados.get(0).compareTo("13") == 0) {
//                                necesitaEliminacion = true;//necesita Eliminacion de accion a realizar sin dato
//                                necesitaCalculo = true;//necesita calculo de accion a realizar                                
//                            }
//                        }
//
//
//                        if (necesitaEliminacion) {
//                            connectionJdbcMB.non_query("DELETE FROM domestic_violence_action_to_take WHERE non_fatal_injury_id = " + rs.getString("non_fatal_injury_id"));
//                        }
//                        if (necesitaCalculo) {
//                            countNecesitaCalculo++;
//                            boolean realizo = false;
//                            switch (fuenteDeDatos) {
//                                case 14://VIF
//                                    connectionJdbcMB.non_query("INSERT INTO domestic_violence_action_to_take VALUES (" + rs.getString("non_fatal_injury_id") + ",14)");//ATENCION EN SALUD                                    
//                                    realizo = true;
//                                    break;
//                                case 70://INSTITUTO DE MEDICINA LEGAL Y CIENCIAS FORENSES
//                                    connectionJdbcMB.non_query("INSERT INTO domestic_violence_action_to_take VALUES (" + rs.getString("non_fatal_injury_id") + ",3)");//dictamen medicina legal
//                                    realizo = true;
//                                    break;
//
//                                case 66://ZONAL 1 ICBF
//                                case 67://ZONAL 2 ICBF
//                                    connectionJdbcMB.non_query("INSERT INTO domestic_violence_action_to_take VALUES (" + rs.getString("non_fatal_injury_id") + ",8)");//medidas de proteccion
//                                    connectionJdbcMB.non_query("INSERT INTO domestic_violence_action_to_take VALUES (" + rs.getString("non_fatal_injury_id") + ",10)");//atencion psicosocial
//                                    connectionJdbcMB.non_query("INSERT INTO domestic_violence_action_to_take VALUES (" + rs.getString("non_fatal_injury_id") + ",11)");//restablecimiento de derechos                
//                                    realizo = true;
//                                    break;
//                                case 82://"CAIVAS 15 FISCALIA"
//                                case 80://CAIVAS 52"
//                                case 68://CAIVAS FISCALIA 15"
//                                case 71://CAIVAS FISCALIA 52"
//                                    connectionJdbcMB.non_query("INSERT INTO domestic_violence_action_to_take VALUES (" + rs.getString("non_fatal_injury_id") + ",5)");//remision a medicina legal
//                                    realizo = true;
//                                    break;
//                                case 69://CAVIF  FISCALIA 10"
//                                    connectionJdbcMB.non_query("INSERT INTO domestic_violence_action_to_take VALUES (" + rs.getString("non_fatal_injury_id") + ",12)");//OTRO
//                                    realizo = true;
//                                    break;
//                            }
//                            //si ids sigue vacio se verifica si la fuente 
//                            if (!realizo) {
//                                connectionJdbcMB.non_query("INSERT INTO domestic_violence_action_to_take VALUES (" + rs.getString("non_fatal_injury_id") + ",14)");//atencion en salud
//                            }
//                        } else {
//                            countNoNecesitaCalculo++;
//                        }
//                    }
//                }
//                System.out.println("---VIF------- SE ACTUALIZARON  " + String.valueOf(countNecesitaCalculo) + "No fueron necesarios " + String.valueOf(countNoNecesitaCalculo));
//            } catch (Exception e) {
//                System.out.println("ERROR 002: count:" + countNecesitaCalculo + "" + e.getMessage());
//            }
//            //--------------------------------------------------
//            //--------- accion a realizar en SIVIGILA ---------------
//            //--------------------------------------------------
//            countNoNecesitaCalculo = 0;
//            countNecesitaCalculo = 0;
//            try {
//                ArrayList<String> conjuntosDeRegistrosBuscados = new ArrayList<>();
//                ResultSet rs = connectionJdbcMB.consult(""
//                        + " SELECT "
//                        + "   * "
//                        + " FROM "
//                        + "   public.ungrouped_tags "
//                        + " WHERE "
//                        + "   form_id like 'SIVIGILA-VIF'");
//                while (rs.next()) {
//                    conjuntosDeRegistrosBuscados.add(rs.getString("ungrouped_tag_id"));
//                }
//
//                for (String idConjunto : conjuntosDeRegistrosBuscados) {
//                    //determino todos los registros para este conjunto
//                    rs = connectionJdbcMB.consult(""
//                            + " SELECT "
//                            + "    * "
//                            + " FROM "
//                            + "    victims, "
//                            + "    non_fatal_injuries "
//                            + " WHERE "
//                            + "    victims.victim_id = non_fatal_injuries.victim_id and "
//                            + "    victims.first_tag_id = " + idConjunto);
//
//                    while (rs.next()) {
//                        ResultSet rs2 = connectionJdbcMB.consult(""
//                                + " SELECT "
//                                + "   * "
//                                + " FROM "
//                                + "   sivigila_event_public_health "
//                                + " WHERE "
//                                + "   non_fatal_injury_id = " + rs.getString("non_fatal_injury_id"));
//                        ArrayList<String> encontrados = new ArrayList<>();
//                        while (rs2.next()) {
//                            encontrados.add(rs2.getString("action_id"));
//                        }
//
//
//                        boolean necesitaCalculo = false;
//                        boolean necesitaEliminacion = false;
//                        if (encontrados.isEmpty()) {
//                            necesitaCalculo = true;//necesita calculo de accion a realizar
//                        } else if (encontrados.size() == 1) {
//                            if (encontrados.get(0).compareTo("8") == 0) {
//                                necesitaEliminacion = true;//necesita Eliminacion de accion a realizar sin dato
//                                necesitaCalculo = true;//necesita calculo de accion a realizar                                
//                            }
//                        }
//
//                        if (necesitaEliminacion) {
//                            connectionJdbcMB.non_query("DELETE FROM sivigila_event_public_health WHERE non_fatal_injury_id = " + rs.getString("non_fatal_injury_id"));
//                        }
//                        if (necesitaCalculo) {
//                            connectionJdbcMB.non_query("INSERT INTO sivigila_event_public_health VALUES (" + rs.getString("non_fatal_injury_id") + ",9)");//public_healt_action ATENCION EN SALUD
//                            countNecesitaCalculo++;
//                        } else {
//                            countNoNecesitaCalculo++;
//                        }
//                    }
//                }
//                System.out.println("-------SIVIGILA VIF ---SE ACTUALIZARON  " + String.valueOf(countNecesitaCalculo) + "No fueron necesarios " + String.valueOf(countNoNecesitaCalculo));
//            } catch (Exception e) {
//                System.out.println("ERROR 002: count:" + countNecesitaCalculo + "" + e.getMessage());
//            }
//            //*/
//            //--------------------------------------------------
//            //--------- accion a realizar en LCENF ---------------
//            //--------------------------------------------------            
//            countNecesitaCalculo = 0;
//            countNoNecesitaCalculo = 0;
//            try {
//                ArrayList<String> conjuntosDeRegistrosBuscados = new ArrayList<>();
//                ResultSet rs = connectionJdbcMB.consult(""
//                        + " SELECT "
//                        + "   * "
//                        + " FROM "
//                        + "   public.ungrouped_tags "
//                        + " WHERE "
//                        + "   form_id like 'SCC-F-032'");
//                while (rs.next()) {
//                    conjuntosDeRegistrosBuscados.add(rs.getString("ungrouped_tag_id"));
//                }
//
//                for (String idConjunto : conjuntosDeRegistrosBuscados) {
//                    //determino todos los registros para este conjunto
//                    rs = connectionJdbcMB.consult(""
//                            + " SELECT "
//                            + "    * "
//                            + " FROM "
//                            + "    victims, "
//                            + "    non_fatal_injuries "
//                            + " WHERE "
//                            + "    victims.victim_id = non_fatal_injuries.victim_id and "
//                            + "    victims.first_tag_id = " + idConjunto + " AND "
//                            + "    non_fatal_injuries.destination_patient_id IS NULL");
//                    while (rs.next()) {
//                        connectionJdbcMB.non_query("UPDATE non_fatal_injuries SET destination_patient_id = 11 WHERE non_fatal_injury_id = " + rs.getString("non_fatal_injury_id"));//destination_patient_id = 11 ATENCION EN SALUD
//                        countNecesitaCalculo++;
//                    }
//                }
//                System.out.println("-------LCENF ---SE ACTUALIZARON  " + String.valueOf(countNecesitaCalculo));
//            } catch (Exception e) {
//                System.out.println("ERROR 002: count:" + countNecesitaCalculo + "" + e.getMessage());
//            }
//        }
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
        //closeSessionDialog = "-";
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
            RequestContext.getCurrentInstance().execute("closeSessionDialog.show();");
            //closeSessionDialog = "closeSessionDialog.show()";//dialog que permite terminar sesion desde otra terminal
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

//    public String getCloseSessionDialog() {
//        return closeSessionDialog;
//    }
//
//    public void setCloseSessionDialog(String closeSessionDialog) {
//        this.closeSessionDialog = closeSessionDialog;
//    }
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
