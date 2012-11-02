/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.login;

import beans.connection.ConnectionJdbcMB;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import managedBeans.fileProcessing.*;
import managedBeans.preload.FormsAndFieldsDataMB;
import model.dao.UsersFacade;
import model.pojo.Users;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author santos
 */
@ManagedBean(name = "loginMB")
@SessionScoped
public class LoginMB implements Serializable {

    private String loginname = "admin";
    private String password = "123";
    private String userLogin = "";
    private String userName = "";
    private String userJob = "";
    private Users currentUser;
    private String activeIndexAcoordion1="-1";
    private String activeIndexAcoordion2="-1";
    //funciones para crear un chart--------------------------
    private StreamedContent chartImage;

    private PieDataset createDataset() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Dato A", new Double(45.0));
        dataset.setValue("Dato B", new Double(15.0));
        dataset.setValue("Dato C", new Double(25.2));
        dataset.setValue("Daro E", new Double(14.8));
        return dataset;
    }

    public StreamedContent getChartImage() {
        return chartImage;
    }

    public void setChartImage(StreamedContent chartImage) {
        this.chartImage = chartImage;
    }
    //---------------------------------------------------------
    private int countLogout = 0;//si este valor llega a 10 se finaliza la sesion
    FacesContext context;
    FormsAndFieldsDataMB formsAndFieldsDataMB;
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

    public void logout1() {
        /*
         * incrementa un contador antes de salir
         */
        //countLogout = countLogout + 1;
        //System.out.println("Contador Logout: " + String.valueOf(countLogout));
        //if (countLogout > 10) {
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        String ctxPath = ((ServletContext) ctx.getContext()).getContextPath();
        try {
            ((HttpSession) ctx.getSession(false)).invalidate();
            ctx.redirect(ctxPath + "/index.html?v=XX");
            System.out.println("FINALIZA LA SESION");
        } catch (Exception ex) {
            System.out.println("Excepcion cerrando sesion: " + ex.toString());
        }
        //}
    }

    //public void resetLogout() {
    //    countLogout = 1;
    //    System.out.println("Contador reseteado: " + String.valueOf(countLogout));
    //}
    public void logout2() {
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        String ctxPath = ((ServletContext) ctx.getContext()).getContextPath();
        try {
            ((HttpSession) ctx.getSession(false)).invalidate();
            ctx.redirect(ctxPath + "/index.html");
            System.out.println("FINALIZA LA SESION");
        } catch (Exception ex) {
            System.out.println("Exepcion cerrando sesion: " + ex.toString());
        }
    }

    public LoginMB() {
        /**
         * Creates a new instance of LoginMB
         */
//        try {
//            PrintStream out = new PrintStream(new FileOutputStream("output.txt"));
//            System.setOut(out);
//            PrintStream out2 = new PrintStream(new FileOutputStream("output2.txt"));
//            System.setErr(out2);
//        } catch (Exception booleanValue) {
//            System.out.println("error:    " + booleanValue);
//        }
    }

    public void reset() {
        uploadFileMB.reset();
        formsAndFieldsDataMB.reset();
        relationshipOfVariablesMB.reset();
        relationshipOfValuesMB.reset();
        storedRelationsMB.reset();
        recordDataMB.reset();
        errorsControlMB.reset();
    }
//    public void btnRegisterDataClick() {
//        progress = 0;
//        for (int i = 0; i < 100; i++) {
//            progress++;
//            for (int j = 0; j < 10000; j++) {
//                for (int k = 0; k < 100; k++) {
//                    if (progress > 100) {
//                        progress = 0;
//                    }
//                }
//            }
//        }
//        progress = 100;
//    }
    ///public void closeSession() {
    //}

    public String CheckValidUser() {

        //get database connection
        

        currentUser = usersFacade.findUser(loginname, password);

        if (currentUser != null) {
            userLogin = currentUser.getUserLogin();
            userName = currentUser.getUserName();
            userJob = currentUser.getUserJob();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto!!", "Se ha ingresado al sistema");
            FacesContext.getCurrentInstance().addMessage(null, msg);

            context = FacesContext.getCurrentInstance();
            System.out.println("INICIA... carga de ManagedBeans");
            connectionJdbcMB = (ConnectionJdbcMB) context.getApplication().evaluateExpressionGet(context, "#{connectionJdbcMB}", ConnectionJdbcMB.class);
            connectionJdbcMB.connectToDb();
            
            formsAndFieldsDataMB = (FormsAndFieldsDataMB) context.getApplication().evaluateExpressionGet(context, "#{formsAndFieldsDataMB}", FormsAndFieldsDataMB.class);
            uploadFileMB = (UploadFileMB) context.getApplication().evaluateExpressionGet(context, "#{uploadFileMB}", UploadFileMB.class);
            relationshipOfVariablesMB = (RelationshipOfVariablesMB) context.getApplication().evaluateExpressionGet(context, "#{relationshipOfVariablesMB}", RelationshipOfVariablesMB.class);
            relationshipOfValuesMB = (RelationshipOfValuesMB) context.getApplication().evaluateExpressionGet(context, "#{relationshipOfValuesMB}", RelationshipOfValuesMB.class);
            storedRelationsMB = (StoredRelationsMB) context.getApplication().evaluateExpressionGet(context, "#{storedRelationsMB}", StoredRelationsMB.class);
            recordDataMB = (RecordDataMB) context.getApplication().evaluateExpressionGet(context, "#{recordDataMB}", RecordDataMB.class);
            errorsControlMB = (ErrorsControlMB) context.getApplication().evaluateExpressionGet(context, "#{errorsControlMB}", ErrorsControlMB.class);

            System.out.println("INICIA... carga de informacion formularios");

            uploadFileMB.reset();
            relationshipOfVariablesMB.reset();

            System.out.println("INICIA... carga de valores iniciales");
            recordDataMB.setRelationshipOfVariablesMB(relationshipOfVariablesMB);
            recordDataMB.setFormsAndFieldsDataMB(formsAndFieldsDataMB);
            recordDataMB.setErrorsControlMB(errorsControlMB);
            recordDataMB.setLoginMB(this);
            recordDataMB.setUploadFileMB(uploadFileMB);

            formsAndFieldsDataMB.loadFormsData();
            formsAndFieldsDataMB.setNameForm("SCC-F-032");

            relationshipOfValuesMB.setFormsAndFieldsDataMB(formsAndFieldsDataMB);
            relationshipOfValuesMB.setRelationshipOfVariablesMB(relationshipOfVariablesMB);

            relationshipOfVariablesMB.setRelationshipOfValuesMB(relationshipOfValuesMB);
            relationshipOfVariablesMB.setFormsAndFieldsDataMB(formsAndFieldsDataMB);
            relationshipOfVariablesMB.setUploadFileMB(uploadFileMB);

            errorsControlMB.setFormsAndFieldsDataMB(formsAndFieldsDataMB);
            errorsControlMB.setRelationshipOfVariablesMB(relationshipOfVariablesMB);

            storedRelationsMB.setUploadFileMB(uploadFileMB);
            storedRelationsMB.setRelationshipOfVariablesMB(relationshipOfVariablesMB);
            storedRelationsMB.setCurrentRelationsGroup(relationshipOfVariablesMB.getCurrentRelationsGroup());
            storedRelationsMB.loadRelatedGroups();

            uploadFileMB.setRelationshipOfVariablesMB(relationshipOfVariablesMB);
            uploadFileMB.setFormsAndFieldsDataMB(formsAndFieldsDataMB);



            uploadFileMB.setStoredRelationsMB(storedRelationsMB);

            reset();

            //-------------------------------------------------
//            try {
//                JFreeChart jfreechart = ChartFactory.createPieChart("Prueba de CHART", createDataset(), true, true, false);
//                File chartFile = new File("dynamichart");
//                ChartUtilities.saveChartAsPNG(chartFile, jfreechart, 375, 300);
//                chartImage = new DefaultStreamedContent(new FileInputStream(chartFile), "image/png");
//            } catch (Exception e) {
//            }


            //-------------------------------------------------


            return "homePage";
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
