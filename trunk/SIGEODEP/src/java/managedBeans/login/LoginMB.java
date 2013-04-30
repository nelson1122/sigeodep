/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.login;

import beans.connection.ConnectionJdbcMB;
import java.sql.ResultSet;
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
    private Users currentUser;
    private String activeIndexAcoordion1 = "-1";
    private String activeIndexAcoordion2 = "-1";
    FacesContext context;
    ConnectionJdbcMB connectionJdbcMB;
    ProjectsMB projectsMB;
    RelationshipOfVariablesMB relationshipOfVariablesMB;
    RelationshipOfValuesMB relationshipOfValuesMB;
    ApplicationControlMB applicationControlMB;
    RecordDataMB recordDataMB;
    ErrorsControlMB errorsControlMB;
    @EJB
    UsersFacade usersFacade;
    private String closeSessionDialog = "";

    //progreso de carga de la aplicacion ***********************************    
//    private Integer progress;   
//
//    public Integer getProgress() {
//        return progress;
//    }
//
//    public void setProgress(Integer progress) {
//        this.progress = progress;
//    }
//
//    public void onComplete() {
//        //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se ha realizado la adición de " + String.valueOf(tuplesProcessed)
//        //       + "registros, para filalizar guarde si lo desea la configuración de relaciones actual o reinicie para realizar la carga de registros de otro acrchivo"));
//    }
//
//    public void cancel() {
//        progress = null;
//    }
    //progreso de carga de la aplicacion***********************************    
    @PreDestroy
    private void destroySession() {
        try {
            applicationControlMB.removeSession(idSession);
            if (!connectionJdbcMB.conn.isClosed()) {
                connectionJdbcMB.non_query("DELETE FROM indicators_records WHERE user_id = "+currentUser.getUserId());
                connectionJdbcMB.disconnect();
            }
        } catch (Exception e) {
            //System.out.println("Termina session por inactividad 003 " + e.toString());
        }
    }
    
    public void logout1() {//fin de session por que se inicio una nueva session en otro equipo      
        applicationControlMB.removeSession(idSession);
        try {
            ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
            String ctxPath = ((ServletContext) ctx.getContext()).getContextPath();
            ((HttpSession) ctx.getSession(false)).invalidate();  //System.out.println("se redirecciona");
            ctx.redirect(ctxPath + "/index.html?v=close");
        } catch (Exception ex) { //System.out.println("Excepcion cuando usuario cierra sesion sesion: " + ex.toString());
        }
    }

    public void logout2() {//fin de sesion al terminar session dada por el usuario
        applicationControlMB.removeSession(idSession);//System.out.println("Finaliza session "+loginname+" ID: "+idSession);
        try {
            ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
            String ctxPath = ((ServletContext) ctx.getContext()).getContextPath();
            ((HttpSession) ctx.getSession(false)).invalidate();//System.out.println("se redirecciona");
            ctx.redirect(ctxPath + "/index.html");
        } catch (Exception ex) {
            System.out.println("Excepcion cuando usuario cierra sesion sesion: " + ex.toString());
        }
    }

    public LoginMB() {
        /**
         * Creates a new instance of LoginMB
         */
        //sessionControlMB
        //System.out.println("ingreso a Login MB");
//        try {
//            System.out.println("DIRECTORIO TEMPORALES:    " + System.getProperty("java.io.tmpdir"));
//            PrintStream out = new PrintStream(new FileOutputStream(System.getProperty("java.io.tmpdir")+"output.txt"));
//            System.setOut(out);
//            PrintStream out2 = new PrintStream(new FileOutputStream(System.getProperty("java.io.tmpdir")+"output2.txt"));
//            System.setErr(out2);
//        } catch (Exception booleanValue) {
//            System.out.println("error:    " + booleanValue);
//        }
    }

    public void reset() {
        projectsMB.reset();
        relationshipOfVariablesMB.reset();
        relationshipOfValuesMB.reset();
        recordDataMB.reset();
        errorsControlMB.reset();
    }
    private boolean autenticado = false;

    public boolean isAutenticado() {
        return autenticado;
    }

    public void setAutenticado(boolean autenticado) {
        this.autenticado = autenticado;
    }

    public String closeSessionAndLogin() {
        /*
         * cerrar una session abierta y continuar abriendo una nueva
         */
        applicationControlMB.removeSession(currentUser.getUserId());
        return continueLogin();
    }

    private String continueLogin() {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("username", loginname);
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        idSession = session.getId();
        userLogin = currentUser.getUserLogin();
        userName = currentUser.getUserName();
        userJob = currentUser.getUserJob();
        applicationControlMB.addSession(currentUser.getUserId(), idSession);
        context = FacesContext.getCurrentInstance();
        //System.out.println("Usuario se logea " + loginname + " ID: " + idSession);
        connectionJdbcMB = (ConnectionJdbcMB) context.getApplication().evaluateExpressionGet(context, "#{connectionJdbcMB}", ConnectionJdbcMB.class);
        if (connectionJdbcMB.connectToDb()) {
            projectsMB = (ProjectsMB) context.getApplication().evaluateExpressionGet(context, "#{projectsMB}", ProjectsMB.class);
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
            autenticado = true;
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

    public String CheckValidUser() {
        closeSessionDialog = "";
        currentUser = usersFacade.findUser(loginname, password);
        if (currentUser != null) {
            ExternalContext contexto = FacesContext.getCurrentInstance().getExternalContext();
            applicationControlMB = (ApplicationControlMB) contexto.getApplicationMap().get("applicationControlMB");
            //determino si el usuario tiene una session alctiva
            if (applicationControlMB.hasLogged(currentUser.getUserId())) {//System.out.println("Ingreso rechazado, ya tiene otra session activa");
                closeSessionDialog = "closeSessionDialog.show()";
                return "";//no dirigir a ninguna pagina
            } else {
                return continueLogin();
            }
        } else {
            //FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "SESION FINALIZADA", "La sesión fue finalizada por que se inició una nueva sesión para el mismo usuario");
            //FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "SESION FINALIZADA", "Después de 20 minutos de inactividad el sistema finaliza la sesión automáticamente, por favor presione el botón: ' ingresar a la aplicación ' para acceder nuevamente.");
            //FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "ACCESO DENEGADO", "La sesión ha finalizado por inactividad o no se ha iniciado una sesión.");
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Incorrecto Usuario o Clave");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            password = "";
            return "";
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
}
