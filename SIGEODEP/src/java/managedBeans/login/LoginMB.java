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
        int accion = 100;
        boolean continuar;
        double num;

        if (accion == 100) {
            int count;
            //1.LAS QUE TENGAN FECHA SUPERIOR A LA FECHA ACTUAL RESTAR 100 Y CALCULAR EDAD ----------------------------------------------------------------------
            try {
                count = 0;
                ResultSet rs = connectionJdbcMB.consult(""
                        + " select "
                        + "    victims.victim_id, "
                        + "    victims.victim_date_of_birth, "
                        + "    to_date(((( "
                        + "       extract(year from victim_date_of_birth)::int-100)::text||'-'|| "
                        + "       extract(month from victim_date_of_birth)::text ||'-'|| "
                        + "       extract(day from victim_date_of_birth)::text)), 'yyyy-MM-DD') as newdate, "
                        + "    non_fatal_injuries.injury_date "
                        + " from "
                        + "    victims, "
                        + "    non_fatal_injuries "
                        + " where "
                        + "    non_fatal_injuries.victim_id = victims.victim_id AND"
                        + "    extract(year from victim_date_of_birth)::int > 2014 "
                        + " AND "
                        + "    victim_age is null ");
                while (rs.next()) {
                    //determino la edad segun la fecha de nacimiento y fecha de evento
                    Interval interval = new Interval(new DateTime(rs.getDate(3)), (new DateTime(rs.getDate(4))).plusDays(1));
                    //edad=Years.yearsIn(interval).getYears();
                    //System.out.println("Nacimiento: " + rs.getDate(3) + " Evento: " + rs.getDate(4) + " Edad: " + edad);
                    connectionJdbcMB.non_query(""
                            + " UPDATE "
                            + "   victims "
                            + " SET "
                            + "   victim_date_of_birth = '" + rs.getString("newdate") + "', "
                            + "   victim_age = '" + Years.yearsIn(interval).getYears() + "' "
                            + " WHERE "
                            + "  victim_id = " + rs.getString(1));

                    //LAS EDADES QUE ESTEN EN CERO PASAN A 1 POR QUE SE MIDE EN AÑOS NO EN MESES
                    connectionJdbcMB.non_query(" UPDATE victims SET victim_age = 1 WHERE victim_age = 0;");
                    count++;
                }

                System.out.println("SE ACTUALIZARON  " + String.valueOf(count) + " restar 100 años y actualizar edad en no fatales");
            } catch (Exception e) {
                System.out.println("ERROR 002: " + e.getMessage());
            }
            //2.LAS QUE TENGAN FECHA SUPERIOR A LA FECHA ACTUAL RESTAR 100 Y CALCULAR EDAD ----------------------------------------------------------------------
            try {
                count = 0;
                ResultSet rs = connectionJdbcMB.consult(""
                        + " select "
                        + "    victims.victim_id, "
                        + "    victims.victim_date_of_birth, "
                        + "    to_date(((( "
                        + "       extract(year from victim_date_of_birth)::int-100)::text||'-'|| "
                        + "       extract(month from victim_date_of_birth)::text ||'-'|| "
                        + "       extract(day from victim_date_of_birth)::text)), 'yyyy-MM-DD') as newdate, "
                        + "    fatal_injuries.injury_date "
                        + " from "
                        + "    victims, "
                        + "    fatal_injuries "
                        + " where "
                        + "    fatal_injuries.victim_id = victims.victim_id AND"
                        + "    extract(year from victim_date_of_birth)::int > 2014 "
                        + " AND "
                        + "    victim_age is null ");
                while (rs.next()) {
                    //determino la edad segun la fecha de nacimiento y fecha de evento
                    Interval interval = new Interval(new DateTime(rs.getDate(3)), (new DateTime(rs.getDate(4))).plusDays(1));
                    //edad=Years.yearsIn(interval).getYears();
                    //System.out.println("Nacimiento: " + rs.getDate(3) + " Evento: " + rs.getDate(4) + " Edad: " + edad);
                    connectionJdbcMB.non_query(""
                            + " UPDATE "
                            + "   victims "
                            + " SET "
                            + "   victim_date_of_birth = '" + rs.getString("newdate") + "', "
                            + "   victim_age = '" + Years.yearsIn(interval).getYears() + "' "
                            + " WHERE "
                            + "  victim_id = " + rs.getString(1));

                    //LAS EDADES QUE ESTEN EN CERO PASAN A 1 POR QUE SE MIDE EN AÑOS NO EN MESES
                    connectionJdbcMB.non_query(" UPDATE victims SET victim_age = 1 WHERE victim_age = 0;");
                    count++;
                }

                System.out.println("SE ACTUALIZARON  " + String.valueOf(count) + " restar 100 años y actualizar edad en fatales");
            } catch (Exception e) {
                System.out.println("ERROR 002: " + e.getMessage());
            }


            //3. FECHA DE NACIMIENTO > FECHA DE EVENTO SE ELIMINA FECHA DE NACIMIENTO EN NO FATALES            
            try {
                count = 0;
                ResultSet rs = connectionJdbcMB.consult(""
                        + " SELECT "
                        + "    victims.victim_id, "
                        + "    victims.victim_date_of_birth, "
                        + "    non_fatal_injuries.injury_date "
                        + " FROM"
                        + "    victims, non_fatal_injuries "
                        + " WHERE "
                        + "    victims.victim_id = non_fatal_injuries.victim_id AND "
                        + "    victims.victim_date_of_birth > non_fatal_injuries.injury_date;");
                while (rs.next()) {
                    connectionJdbcMB.non_query(""
                            + " UPDATE "
                            + "   victims "
                            + " SET "
                            + "   victim_date_of_birth = null "
                            + " WHERE "
                            + "  victim_id = " + rs.getString(1));
                    count++;
                }

                System.out.println("SE ELIMINARON " + String.valueOf(count) + " valores, por que fecha de nacimiento > fecha de evento en lesiones no fatales");
            } catch (Exception e) {
                System.out.println("ERROR 001: " + e.getMessage());
            }
            //4. FECHA DE NACIMIENTO > FECHA DE EVENTO SE ELIMINA FECHA DE NACIMIENTO EN FATALES            
            try {
                count = 0;
                ResultSet rs = connectionJdbcMB.consult(""
                        + " SELECT "
                        + "    victims.victim_id, "
                        + "    victims.victim_date_of_birth, "
                        + "    fatal_injuries.injury_date "
                        + " FROM"
                        + "    victims, fatal_injuries "
                        + " WHERE "
                        + "    victims.victim_id = fatal_injuries.victim_id AND "
                        + "    victims.victim_date_of_birth > fatal_injuries.injury_date;");
                while (rs.next()) {
                    connectionJdbcMB.non_query(""
                            + " UPDATE "
                            + "   victims "
                            + " SET "
                            + "   victim_date_of_birth = null"
                            + " WHERE "
                            + "  victim_id = " + rs.getString(1));
                    count++;
                }

                System.out.println("SE ELIMINARON " + String.valueOf(count) + " fechas de nacimiento, por que fecha de nacimiento > fecha de evento en lesiones fatales");
            } catch (Exception e) {
                System.out.println("ERROR 002: " + e.getMessage());
            }

            //5. SI NO SE TIENE EDAD SE SACA DE rup Y SE ACTUALIZA FECHA_NACIMIENTO Y EDAD (cedula < genNN)
            try {
                count = 0;
                ResultSet rs = connectionJdbcMB.consult(""
                        + " SELECT "
                        + "   victims.victim_id, "
                        + "   victims.victim_date_of_birth, "
                        + "   rup.dob, "
                        + "   non_fatal_injuries.injury_date, "
                        + "   rup.id "
                        + " FROM "
                        + "   public.victims, "
                        + "   public.rup, "
                        + "   public.non_fatal_injuries "
                        + " WHERE "
                        + "   non_fatal_injuries.victim_id = victims.victim_id AND "
                        + "   victims.victim_nid = rup.id AND "
                        + "   victim_age IS NULL; ");

                int aaa = 0;
                int ingreso = 0;
                while (rs.next()) {
                    ingreso++;
                    continuar = true;
                    try {//valido que la identificacion se pueda convertir a entero y sea menor que gen_nn
                        if (rs.getString(1).length() < 5) {
                            num = Double.parseDouble(rs.getString(1));
                            if (num < 6930) {
                                continuar = false;
                            }
                        }
                    } catch (SQLException | NumberFormatException e) {
                        aaa++;
                    }

                    if (continuar) {
                        //determino la edad segun la fecha de nacimiento y fecha de evento
                        Interval interval = new Interval(new DateTime(rs.getDate(3)), (new DateTime(rs.getDate(4))).plusDays(1));
                        //int  edad=Years.yearsIn(interval).getYears();
                        //System.out.println("Nacimiento: " + rs.getDate(3) + " Evento: " + rs.getDate(4) + " Edad: " + edad);
                        connectionJdbcMB.non_query(""
                                + " UPDATE "
                                + "   victims "
                                + " SET "
                                + "   victim_date_of_birth = '" + rs.getString(3) + "', "
                                + "   victim_age = '" + Years.yearsIn(interval).getYears() + "', "
                                + "   age_type_id = '1' "
                                + " WHERE "
                                + "  victim_id = " + rs.getString(1));

                        //LAS EDADES QUE ESTEN EN CERO PASAN A 1 POR QUE SE MIDE EN AÑOS NO EN MESES
                        
                        count++;
                    }
                }
                connectionJdbcMB.non_query(" UPDATE victims SET victim_age = 1 WHERE victim_age = 0;");
                System.out.println("SE ACTUALIZARON  " + String.valueOf(count) + " edades desde RUP para FATALES, errores de conversion a entero de identificacion = " + aaa + " Ingreso: " + ingreso);
            } catch (Exception e) {
                System.out.println("ERROR 002: " + e.getMessage());
            }
            //5. SI NO SE TIENE EDAD SE SACA DE rup Y SE ACTUALIZA FECHA_NACIMIENTO Y EDAD (cedula < genNN)
            try {
                count = 0;
                ResultSet rs = connectionJdbcMB.consult(""
                        + " SELECT "
                        + "   victims.victim_id, "
                        + "   victims.victim_date_of_birth, "
                        + "   rup.dob, "
                        + "   fatal_injuries.injury_date, "
                        + "   rup.id "
                        + " FROM "
                        + "   public.victims, "
                        + "   public.rup, "
                        + "   public.fatal_injuries "
                        + " WHERE "
                        + "   fatal_injuries.victim_id = victims.victim_id AND "
                        + "   victims.victim_nid = rup.id AND "
                        + "   victim_age IS NULL; ");

                int aaa = 0;
                int ingreso = 0;
                while (rs.next()) {
                    continuar = true;
                    ingreso++;
                    try {//valido que la identificacion se pueda convertir a entero y sea menor que gen_nn
                        if (rs.getString(1).length() < 5) {
                            num = Double.parseDouble(rs.getString(1));
                            if (num < 6930) {
                                continuar = false;
                            }
                        }
                    } catch (SQLException | NumberFormatException e) {
                        aaa++;
                    }

                    if (continuar) {
                        //determino la edad segun la fecha de nacimiento y fecha de evento
                        Interval interval = new Interval(new DateTime(rs.getDate(3)), (new DateTime(rs.getDate(4))).plusDays(1));
                        //int  edad=Years.yearsIn(interval).getYears();
                        //System.out.println("Nacimiento: " + rs.getDate(3) + " Evento: " + rs.getDate(4) + " Edad: " + edad);
                        connectionJdbcMB.non_query(""
                                + " UPDATE "
                                + "   victims "
                                + " SET "
                                + "   victim_date_of_birth = '" + rs.getString(3) + "', "
                                + "   victim_age = '" + Years.yearsIn(interval).getYears() + "', "
                                + "   age_type_id = '1' "
                                + " WHERE "
                                + "  victim_id = " + rs.getString(1));

                        //LAS EDADES QUE ESTEN EN CERO PASAN A 1 POR QUE SE MIDE EN AÑOS NO EN MESES
                        
                        count++;
                    }
                }
                connectionJdbcMB.non_query(" UPDATE victims SET victim_age = 1 WHERE victim_age = 0;");
                System.out.println("SE ACTUALIZARON  " + String.valueOf(count) + " edades desde RUP para FATALES, errores de conversion a entero de identificacion = " + aaa + " Ingreso: " + ingreso);
            } catch (Exception e) {
                System.out.println("ERROR 002: " + e.getMessage());
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
