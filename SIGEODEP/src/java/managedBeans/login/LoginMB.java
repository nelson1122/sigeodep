/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.login;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import managedBeans.fileProcessing.*;
import managedBeans.forms.LcenfMB;
import managedBeans.preload.FormsAndFieldsDataMB;

/**
 *
 * @author santos
 */
@ManagedBean(name = "loginMB")
@RequestScoped
public class LoginMB {

    private String loginname;
    private String password;
    FacesContext context;
    FormsAndFieldsDataMB formsAndFieldsDataMB;
    UploadFileMB uploadFileMB;
    RelationshipOfVariablesMB relationshipOfVariablesMB;
    RelationshipOfValuesMB relationshipOfValuesMB;
    StoredRelationsMB storedRelationsMB;
    RecordDataMB recordDataMB;
    ErrorsControlMB errorsControlMB;
    LcenfMB lcenfMB;
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

    public String CheckValidUser() {
	if (loginname.equals("admin") && password.equals("123")) {
	    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto!!", "Se ha ingresado al sistema");
	    FacesContext.getCurrentInstance().addMessage(null, msg);

	    context = FacesContext.getCurrentInstance();
	    //tomar los managed beans existentes
	    formsAndFieldsDataMB = (FormsAndFieldsDataMB) context.getApplication().evaluateExpressionGet(context, "#{formsAndFieldsDataMB}", FormsAndFieldsDataMB.class);
	    uploadFileMB = (UploadFileMB) context.getApplication().evaluateExpressionGet(context, "#{uploadFileMB}", UploadFileMB.class);
	    relationshipOfVariablesMB = (RelationshipOfVariablesMB) context.getApplication().evaluateExpressionGet(context, "#{relationshipOfVariablesMB}", RelationshipOfVariablesMB.class);
	    relationshipOfValuesMB = (RelationshipOfValuesMB) context.getApplication().evaluateExpressionGet(context, "#{relationshipOfValuesMB}", RelationshipOfValuesMB.class);
	    storedRelationsMB = (StoredRelationsMB) context.getApplication().evaluateExpressionGet(context, "#{storedRelationsMB}", StoredRelationsMB.class);
	    recordDataMB = (RecordDataMB) context.getApplication().evaluateExpressionGet(context, "#{recordDataMB}", RecordDataMB.class);
	    errorsControlMB = (ErrorsControlMB) context.getApplication().evaluateExpressionGet(context, "#{errorsControlMB}", ErrorsControlMB.class);
	    lcenfMB = (LcenfMB) context.getApplication().evaluateExpressionGet(context, "#{lcenfMB}", LcenfMB.class);

	    recordDataMB.setRelationshipOfVariablesMB(relationshipOfVariablesMB);
	    recordDataMB.setFormsAndFieldsDataMB(formsAndFieldsDataMB);
	    recordDataMB.setErrorsControlMB(errorsControlMB);
	    recordDataMB.setLoginMB(this);

	    formsAndFieldsDataMB.loadFormsData();
	    formsAndFieldsDataMB.setNameForm("SCC-F-032");

	    lcenfMB.setFormsAndFieldsDataMB(formsAndFieldsDataMB);
	    lcenfMB.reset();
	    
	    uploadFileMB.reset();

	    relationshipOfVariablesMB.reset();

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
