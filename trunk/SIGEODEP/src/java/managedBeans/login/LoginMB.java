/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.login;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import managedBeans.categoricalVariables.NeighborhoodsVariableMB;
import managedBeans.fileProcessing.*;
import managedBeans.forms.*;
import managedBeans.preload.FormsAndFieldsDataMB;

/**
 *
 * @author santos
 */
@ManagedBean(name = "loginMB")
@RequestScoped
public class LoginMB implements Serializable{

    private String loginname="admin";
    private String password="123";
    FacesContext context;
    FormsAndFieldsDataMB formsAndFieldsDataMB;
    UploadFileMB uploadFileMB;
    RelationshipOfVariablesMB relationshipOfVariablesMB;
    RelationshipOfValuesMB relationshipOfValuesMB;
    StoredRelationsMB storedRelationsMB;
    RecordDataMB recordDataMB;
    ErrorsControlMB errorsControlMB;
    LcenfMB lcenfMB;
    AccidentalMB accidentalMB;
    HomicideMB homicideMB;
    SuicideMB suicideMB;
    TransitMB transitMB;
    VIFMB vifMB;
    NeighborhoodsVariableMB neighborhoodsVariableMB;
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

    public LoginMB() {
        /**
         * Creates a new instance of LoginMB
         */
//        try {
//            PrintStream out = new PrintStream(new FileOutputStream("output.txt"));
//            System.setOut(out);
//            PrintStream out2 = new PrintStream(new FileOutputStream("output2.txt"));
//            System.setErr(out2);
//        } catch (Exception e) {
//            System.out.println("error:    " + e);
//        }
    }

    public void reset() {
        uploadFileMB.reset();
        formsAndFieldsDataMB.reset();
        uploadFileMB.reset();
        relationshipOfVariablesMB.reset();
        relationshipOfValuesMB.reset();
        storedRelationsMB.reset();
        recordDataMB.reset();
        errorsControlMB.reset();
    }

    public void btnRegisterDataClick() {
        progress = 0;
        for (int i = 0; i < 100; i++) {
            progress++;
            for (int j = 0; j < 10000; j++) {
                for (int k = 0; k < 100; k++) {
                    if (progress > 100) {
                        progress = 0;
                    }
                }
            }
        }
        progress = 100;
    }
    
    public void closeSession(){
        
    }

    public String CheckValidUser() {
        if (loginname.equals("admin") && password.equals("123")) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto!!", "Se ha ingresado al sistema");
            FacesContext.getCurrentInstance().addMessage(null, msg);

            context = FacesContext.getCurrentInstance();
            System.out.println("INICIA... carga de ManagedBeans");
            formsAndFieldsDataMB = (FormsAndFieldsDataMB) context.getApplication().evaluateExpressionGet(context, "#{formsAndFieldsDataMB}", FormsAndFieldsDataMB.class);
            uploadFileMB = (UploadFileMB) context.getApplication().evaluateExpressionGet(context, "#{uploadFileMB}", UploadFileMB.class);
            relationshipOfVariablesMB = (RelationshipOfVariablesMB) context.getApplication().evaluateExpressionGet(context, "#{relationshipOfVariablesMB}", RelationshipOfVariablesMB.class);
            relationshipOfValuesMB = (RelationshipOfValuesMB) context.getApplication().evaluateExpressionGet(context, "#{relationshipOfValuesMB}", RelationshipOfValuesMB.class);
            storedRelationsMB = (StoredRelationsMB) context.getApplication().evaluateExpressionGet(context, "#{storedRelationsMB}", StoredRelationsMB.class);
            recordDataMB = (RecordDataMB) context.getApplication().evaluateExpressionGet(context, "#{recordDataMB}", RecordDataMB.class);
            errorsControlMB = (ErrorsControlMB) context.getApplication().evaluateExpressionGet(context, "#{errorsControlMB}", ErrorsControlMB.class);
            lcenfMB = (LcenfMB) context.getApplication().evaluateExpressionGet(context, "#{lcenfMB}", LcenfMB.class);
            accidentalMB = (AccidentalMB) context.getApplication().evaluateExpressionGet(context, "#{accidentalMB}", AccidentalMB.class);
            homicideMB = (HomicideMB) context.getApplication().evaluateExpressionGet(context, "#{homicideMB}", HomicideMB.class);
            suicideMB = (SuicideMB) context.getApplication().evaluateExpressionGet(context, "#{suicideMB}", SuicideMB.class);
            transitMB = (TransitMB) context.getApplication().evaluateExpressionGet(context, "#{transitMB}", TransitMB.class);
            vifMB = (VIFMB) context.getApplication().evaluateExpressionGet(context, "#{vifMB}", VIFMB.class);
            neighborhoodsVariableMB= (NeighborhoodsVariableMB) context.getApplication().evaluateExpressionGet(context, "#{neighborhoodsVariableMB}", NeighborhoodsVariableMB.class);
            
            
            System.out.println("INICIA... carga de informacion formularios");
            lcenfMB.reset();
            neighborhoodsVariableMB.reset();
            accidentalMB.reset();
            homicideMB.reset();
            suicideMB.reset();
            transitMB.reset();
            vifMB.reset();
            uploadFileMB.reset();
            relationshipOfVariablesMB.reset();
            
            System.out.println("INICIA... carga de valores iniciales");
            recordDataMB.setRelationshipOfVariablesMB(relationshipOfVariablesMB);
            recordDataMB.setFormsAndFieldsDataMB(formsAndFieldsDataMB);
            recordDataMB.setErrorsControlMB(errorsControlMB);
            recordDataMB.setLoginMB(this);

            formsAndFieldsDataMB.loadFormsData();
            formsAndFieldsDataMB.setNameForm("SCC-F-032");            

            relationshipOfValuesMB.setFormsAndFieldsDataMB(formsAndFieldsDataMB);
            relationshipOfValuesMB.setRelationshipOfVariablesMB(relationshipOfVariablesMB);

            relationshipOfVariablesMB.setRelationshipOfValuesMB(relationshipOfValuesMB);
            relationshipOfVariablesMB.setFormsAndFieldsDataMB(formsAndFieldsDataMB);
            relationshipOfVariablesMB.setUploadFileMB(uploadFileMB);

            errorsControlMB.setFormsAndFieldsDataMB(formsAndFieldsDataMB);
            errorsControlMB.setRelationshipOfVariablesMB(relationshipOfVariablesMB);

            uploadFileMB.setRelationshipOfVariablesMB(relationshipOfVariablesMB);
            uploadFileMB.setFormsAndFieldsDataMB(formsAndFieldsDataMB);

            storedRelationsMB.setUploadFileMB(uploadFileMB);
            storedRelationsMB.setRelationshipOfVariablesMB(relationshipOfVariablesMB);
            storedRelationsMB.setCurrentRelationsGroup(relationshipOfVariablesMB.getCurrentRelationsGroup());
            storedRelationsMB.loadRelatedGroups();

            uploadFileMB.setStoredRelationsMB(storedRelationsMB);

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
}
