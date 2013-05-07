/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.login;

import beans.connection.ConnectionJdbcMB;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.faces.FacesException;
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
    private boolean permissionFatal = false;
    private boolean permissionNonFatal = false;
    private boolean permissionVif = false;
    private boolean permissionIndicators = false;
    private boolean permissionAdministrator = false;
    private boolean disableNonFatalSection = true;
    private boolean disableFatalSection = true;
    private boolean disableVifSection = true;
    private boolean disableIndicatorsSection = true;
    private boolean disableAdministratorSection = true;
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

    @PreDestroy
    private void destroySession() {
        try {
            applicationControlMB.removeSession(idSession);
            connectionJdbcMB.non_query("DELETE FROM indicators_records WHERE user_id = " + currentUser.getUserId());
            //System.out.println("Eliminadas variables de session para: "+currentUser.getUserLogin());
        } catch (Exception e) {
            //System.out.println("Termina session por inactividad 003 " + e.toString());
        }
    }

//    public void newWindow2() {
//        try {
//            ExternalContext ext = FacesContext.getCurrentInstance().getExternalContext();
//            String ctxPath = ((ServletContext) ext.getContext()).getContextPath();
//            //return ctxPath + "/index.html?v=timeout";//System.out.println("enviado a: " + ctxPath + "/index.html?v=timeout");        
//            ext.redirect(ctxPath + "/index.html?v=close");
//        } catch (Exception ex) {//System.out.println("Excepcion cuando usuario cierra sesion sesion: " + ex.toString());
//        }
//    }

    public void logout1() {//fin de session por que se inicio una nueva session en otro equipo      
        applicationControlMB.removeSession(idSession);
        try {
            ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
            String ctxPath = ((ServletContext) ctx.getContext()).getContextPath();
            ((HttpSession) ctx.getSession(false)).invalidate();  //System.out.println("se redirecciona");
            ctx.redirect(ctxPath + "/index.html?v=close");
        } catch (Exception ex) {//System.out.println("Excepcion cuando usuario cierra sesion sesion: " + ex.toString());
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
        String[] splitPermissions = currentUser.getPermissions().split("\t");
        for (int i = 0; i < splitPermissions.length; i++) {
            if (splitPermissions[i].compareTo("1") == 0) {
                permissionFatal = true;
                disableFatalSection = false;
            }
            if (splitPermissions[i].compareTo("2") == 0) {
                permissionNonFatal = true;
                disableNonFatalSection = false;
            }
            if (splitPermissions[i].compareTo("3") == 0) {
                permissionVif = true;
                disableVifSection = false;
            }
            if (splitPermissions[i].compareTo("4") == 0) {
                permissionIndicators = true;
                disableIndicatorsSection = false;
            }
            if (splitPermissions[i].compareTo("5") == 0) {
                permissionAdministrator = true;
                disableAdministratorSection = false;
            }
        }

        idSession = session.getId();
        userLogin = currentUser.getUserLogin();
        userName = currentUser.getUserName();
        userJob = currentUser.getUserJob();
        applicationControlMB.addSession(currentUser.getUserId(), idSession);
        context = FacesContext.getCurrentInstance();
        //System.out.println("Usuario se logea " + loginname + " ID: " + idSession);
        connectionJdbcMB = (ConnectionJdbcMB) context.getApplication().evaluateExpressionGet(context, "#{connectionJdbcMB}", ConnectionJdbcMB.class);
        if (connectionJdbcMB.connectToDb()) {
            connectionJdbcMB.setCurrentUser(currentUser);
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
            closeSessionDialog = "closeSessionDialog.show()";//permitir terminar sesion de otra terminal
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

    public boolean isDisableNonFatalSection() {
        return disableNonFatalSection;
    }

    public void setDisableNonFatalSection(boolean disableNonFatalSection) {
        this.disableNonFatalSection = disableNonFatalSection;
    }

    public boolean isDisableFatalSection() {
        return disableFatalSection;
    }

    public void setDisableFatalSection(boolean disableFatalSection) {
        this.disableFatalSection = disableFatalSection;
    }

    public boolean isDisableVifSection() {
        return disableVifSection;
    }

    public void setDisableVifSection(boolean disableVifSection) {
        this.disableVifSection = disableVifSection;
    }

    public boolean isDisableIndicatorsSection() {
        return disableIndicatorsSection;
    }

    public void setDisableIndicatorsSection(boolean disableIndicatorsSection) {
        this.disableIndicatorsSection = disableIndicatorsSection;
    }

    public boolean isDisableAdministratorSection() {
        return disableAdministratorSection;
    }

    public void setDisableAdministratorSection(boolean disableAdministratorSection) {
        this.disableAdministratorSection = disableAdministratorSection;
    }
}
