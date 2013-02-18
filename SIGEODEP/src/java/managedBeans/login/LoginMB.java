/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.login;

import beans.connection.ConnectionJdbcMB;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    
    private String idSession="";//identificador de la session
    private String loginname = "";
    private String password = "";
    private String userLogin = "";
    private String userName = "";
    private String userJob = "";
    private Users currentUser;
    private String activeIndexAcoordion1 = "-1";
    private String activeIndexAcoordion2 = "-1";
    private int countLogout = 0;//si este valor llega a 10 se finaliza la sesion
    FacesContext context;
    //FormsAndFieldsDataMB formsAndFieldsDataMB;
    ConnectionJdbcMB connectionJdbcMB;
    UploadFileMB uploadFileMB;
    RelationshipOfVariablesMB relationshipOfVariablesMB;
    RelationshipOfValuesMB relationshipOfValuesMB;
    StoredRelationsMB storedRelationsMB;
    RecordDataMB recordDataMB;
    ErrorsControlMB errorsControlMB;
    @EJB
    UsersFacade usersFacade;
    //progreso de carga de la aplicacion ***********************************    
    private Integer progress;

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public void onComplete() {
        //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se ha realizado la adición de " + String.valueOf(tuplesProcessed)
        //       + "registros, para filalizar guarde si lo desea la configuración de relaciones actual o reinicie para realizar la carga de registros de otro acrchivo"));
    }

    public void cancel() {
        progress = null;
    }
    //progreso de carga de la aplicacion***********************************    

    @PreDestroy
    private void destruir() {
        logout1();
    }

    public void eliminarDatos() {
        try {
            ResultSet rs = connectionJdbcMB.consult("SELECT * FROM relation_values");
            while (rs.next()) {
                ResultSet rs2 = connectionJdbcMB.consult("SELECT * FROM relation_values");
                while (rs2.next()) {
                    if (rs.getString("name_expected").compareTo(rs2.getString("name_expected")) == 0
                            && rs.getString("name_found").compareTo(rs2.getString("name_found")) == 0) {
                        if (rs.getInt("id_relation_values") != rs2.getInt("id_relation_values")) {
                            connectionJdbcMB.remove("relation_values", "id_relation_values=" + String.valueOf(rs2.getInt("id_relation_values")));
                            System.out.println("son los mismos en: " + String.valueOf(rs.getInt("id_relation_values")) + " Y " + String.valueOf(rs2.getInt("id_relation_values")));
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(LoginMB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void logout1() {//fin de session por inactividad
        System.out.println("FINALIZA LA SESION: idSession="+idSession);
        if (connectionJdbcMB == null) {
            System.out.println("Sale de la aplicacion, no hay conexion a db");
        } else {
            ResultSet rs = connectionJdbcMB.consult("SELECT * FROM users");
            try {
                if (rs.next()) {
                    System.out.println("Termina session y se modifica base de datos");
                } else {
                    System.out.println("Sale de la aplicacion, hay conexion pero fallo la consulta");
                }
            } catch (Throwable t) {
                System.out.println("Sale de la aplicacion, fallo la consulta "+ t.toString());
            }
        }
    }

    public void logout2() {//fin de ssesion al terminar session
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        String ctxPath = ((ServletContext) ctx.getContext()).getContextPath();
        System.out.println("FINALIZA LA SESION: idSession="+idSession);
        try {
            ((HttpSession) ctx.getSession(false)).invalidate();
            ctx.redirect(ctxPath + "/index.html");
        } catch (Exception ex) {
            System.out.println("Exepcion cerrando sesion: " + ex.toString());
        }
    }

    public LoginMB() {
        /**
         * Creates a new instance of LoginMB
         */
        try {
            PrintStream out = new PrintStream(new FileOutputStream("output.txt"));
            System.setOut(out);
            PrintStream out2 = new PrintStream(new FileOutputStream("output2.txt"));
            System.setErr(out2);
        } catch (Exception booleanValue) {
            System.out.println("error:    " + booleanValue);
        }
    }

    public void reset() {
        uploadFileMB.reset();
        relationshipOfVariablesMB.reset();
        relationshipOfValuesMB.reset();
        storedRelationsMB.reset();
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

    public String CheckValidUser() {
        currentUser = usersFacade.findUser(loginname, password);
        if (currentUser != null) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("username", loginname);
            //ExternalContext ext = context.getExternalContext();
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
            idSession=session.getId();
            System.out.println("INICIA sesion: idSession="+idSession);

            userLogin = currentUser.getUserLogin();
            userName = currentUser.getUserName();
            userJob = currentUser.getUserJob();
            context = FacesContext.getCurrentInstance();
            //System.out.println("INICIA... carga de ManagedBeans");
            connectionJdbcMB = (ConnectionJdbcMB) context.getApplication().evaluateExpressionGet(context, "#{connectionJdbcMB}", ConnectionJdbcMB.class);
            if (connectionJdbcMB.connectToDb()) {
                uploadFileMB = (UploadFileMB) context.getApplication().evaluateExpressionGet(context, "#{uploadFileMB}", UploadFileMB.class);
                relationshipOfVariablesMB = (RelationshipOfVariablesMB) context.getApplication().evaluateExpressionGet(context, "#{relationshipOfVariablesMB}", RelationshipOfVariablesMB.class);
                relationshipOfValuesMB = (RelationshipOfValuesMB) context.getApplication().evaluateExpressionGet(context, "#{relationshipOfValuesMB}", RelationshipOfValuesMB.class);
                storedRelationsMB = (StoredRelationsMB) context.getApplication().evaluateExpressionGet(context, "#{storedRelationsMB}", StoredRelationsMB.class);
                recordDataMB = (RecordDataMB) context.getApplication().evaluateExpressionGet(context, "#{recordDataMB}", RecordDataMB.class);
                errorsControlMB = (ErrorsControlMB) context.getApplication().evaluateExpressionGet(context, "#{errorsControlMB}", ErrorsControlMB.class);

                //System.out.println("INICIA... carga de informacion formularios");

                uploadFileMB.reset();
                relationshipOfVariablesMB.reset();

                //System.out.println("INICIA... carga de valores iniciales");
                recordDataMB.setRelationshipOfVariablesMB(relationshipOfVariablesMB);
                recordDataMB.setErrorsControlMB(errorsControlMB);
                recordDataMB.setLoginMB(this);
                recordDataMB.setUploadFileMB(uploadFileMB);

                relationshipOfValuesMB.setRelationshipOfVariablesMB(relationshipOfVariablesMB);
                relationshipOfVariablesMB.setRelationshipOfValuesMB(relationshipOfValuesMB);
                relationshipOfVariablesMB.setUploadFileMB(uploadFileMB);

                errorsControlMB.setRelationshipOfVariablesMB(relationshipOfVariablesMB);
                storedRelationsMB.setUploadFileMB(uploadFileMB);
                storedRelationsMB.setRelationshipOfVariablesMB(relationshipOfVariablesMB);
                storedRelationsMB.setCurrentRelationsGroup(relationshipOfVariablesMB.getCurrentRelationsGroup());
                storedRelationsMB.loadRelatedGroups();

                uploadFileMB.setRelationshipOfVariablesMB(relationshipOfVariablesMB);

                uploadFileMB.setStoredRelationsMB(storedRelationsMB);
                reset();
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
        } else {
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
}
