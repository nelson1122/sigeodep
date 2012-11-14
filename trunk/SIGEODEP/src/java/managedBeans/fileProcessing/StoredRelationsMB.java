/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.fileProcessing;

//import beans.relations.RelationValue;
//import beans.relations.RelationVar;
//import beans.relations.RelationsGroup;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import model.dao.*;
import model.pojo.*;

/**
 *
 * @author santos
 */
@ManagedBean(name = "storedRelationsMB")
@SessionScoped
public class StoredRelationsMB implements Serializable {

    private String currentRelationGroupName;
    private List<String> relationGroups;
    private String txtOpenDialog;
    private String newConfigurationName = "";
    private boolean btnLoadConfigurationDisabled = true;
    //private boolean btnSaveConfigurationDisabled = true;
    private boolean btnRemoveConfigurationDisabled = true;
    private String txtSaveDialog;
    private String saveClick = "";
    @EJB
    FormsFacade formsFacade;
    @EJB
    SourcesFacade sourcesFacade;
    @EJB
    RelationGroupFacade relationGroupFacade;
    @EJB
    RelationVariablesFacade relationVariablesFacade;
    @EJB
    RelationValuesFacade relationValuesFacade;
    @EJB
    RelationsDiscardedValuesFacade relationDiscardedValuesFacade;
    private UploadFileMB uploadFileMB;
    private RelationshipOfVariablesMB relationshipOfVariablesMB;
    private RelationGroup currentRelationsGroup;//grupo de relaciones actual
    ErrorsControlMB errorsControlMB;
    RecordDataMB recordDataMB;

    public StoredRelationsMB() {
        /**
         * Creates a new instance of StoredRelationsMB
         */
    }

    public void reset() {
        currentRelationGroupName = "";
        newConfigurationName = "";
//        btnSaveConfigurationDisabled = true;
        btnLoadConfigurationDisabled = true;
        btnRemoveConfigurationDisabled = true;
    }

    public String btnSaveConfiguration() {
        if (newConfigurationName.trim().length() == 0) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Debe digitar un nombre para el grupo de relaciones");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return "";
        }
        if (currentRelationsGroup != null) {
            List<RelationVariables> relationVarList = currentRelationsGroup.getRelationVariablesList();
            if (relationVarList.isEmpty()) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "No existen relaciones creadas");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                return "";
            }
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "No existen relaciones creadas.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return "";
        }

        List<RelationGroup> relationGroupList = relationGroupFacade.findAll();//buscar si ya existe el nombre
        boolean exist = false;
        for (int i = 0; i < relationGroupList.size(); i++) {
            if (relationGroupList.get(i).getNameRelationGroup().compareTo(newConfigurationName) == 0) {
                currentRelationsGroup.setIdRelationGroup(relationGroupList.get(i).getIdRelationGroup());//obtengo el id del existente
                exist = true;
                break;
            }
        }
        if (exist) {
            currentRelationGroupName = newConfigurationName;
            removeConfiguration();
            saveConfiguration();
        } else {
            saveConfiguration();
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Guardado", "La configuración ha sido almacenada"));
        return "";
    }

    public void btnLoadConfigurationClick() {        
        if (currentRelationGroupName.trim().length() != 0) {
            RelationGroup selectRelationGroup = relationGroupFacade.findByName(currentRelationGroupName);
            List<RelationVariables> relationVariablesList = relationVariablesFacade.findByRelationGroup(selectRelationGroup.getIdRelationGroup());
            List<String> varsExpected = uploadFileMB.getVariablesExpected();//variables esperadas
            List<String> varsFound = uploadFileMB.getVariablesFound();//variables encontradas

            currentRelationsGroup = new RelationGroup();
            currentRelationsGroup.setIdRelationGroup(selectRelationGroup.getIdRelationGroup());
            currentRelationsGroup.setFormId(selectRelationGroup.getFormId());
            currentRelationsGroup.setSourceId(selectRelationGroup.getSourceId());
            currentRelationsGroup.setNameRelationGroup(selectRelationGroup.getNameRelationGroup());

            currentRelationsGroup.setRelationVariablesList(new ArrayList<RelationVariables>());
            int countNotFound = 0;//numero de relaciones que no se pudieron cargar
            int countFound = 0;//numero de relaciones que no se pudieron cargar
            for (int i = 0; i < relationVariablesList.size(); i++) {
                //verifico que tanto la variable eperada como la encontrada existan
                boolean existVarExpected = false;
                boolean existVarFound = false;

                for (int j = 0; j < varsExpected.size(); j++) {
                    if (varsExpected.get(j).compareTo(relationVariablesList.get(i).getNameExpected()) == 0) {
                        existVarExpected = true;
                        break;
                    }
                }
                for (int j = 0; j < varsFound.size(); j++) {
                    if (varsFound.get(j).compareTo(relationVariablesList.get(i).getNameFound()) == 0) {
                        existVarFound = true;
                        break;
                    }
                }
                if (existVarExpected && existVarFound) {//existe el nombre de la variable esperada
                    RelationVariables newRelationVariables = new RelationVariables();
                    newRelationVariables.setIdRelationVariables(relationVariablesList.get(i).getIdRelationVariables());
                    newRelationVariables.setIdRelationGroup(currentRelationsGroup);
                    newRelationVariables.setNameExpected(relationVariablesList.get(i).getNameExpected());
                    newRelationVariables.setNameFound(relationVariablesList.get(i).getNameFound());
                    newRelationVariables.setDateFormat(relationVariablesList.get(i).getDateFormat());
                    newRelationVariables.setFieldType(relationVariablesList.get(i).getFieldType());
                    newRelationVariables.setComparisonForCode(relationVariablesList.get(i).getComparisonForCode());
                    newRelationVariables.setRelationValuesList(new ArrayList<RelationValues>());
                    for (int j = 0; j < relationVariablesList.get(i).getRelationValuesList().size(); j++) {
                        RelationValues newRelationValues = new RelationValues();
                        newRelationValues.setIdRelationValues(relationVariablesList.get(i).getRelationValuesList().get(j).getIdRelationValues());
                        newRelationValues.setIdRelationVariables(relationVariablesList.get(i));
                        newRelationValues.setNameExpected(relationVariablesList.get(i).getRelationValuesList().get(j).getNameExpected());
                        newRelationValues.setNameFound(relationVariablesList.get(i).getRelationValuesList().get(j).getNameFound());
                        newRelationVariables.getRelationValuesList().add(newRelationValues);
                    }
                    newRelationVariables.setRelationsDiscardedValuesList(new ArrayList<RelationsDiscardedValues>());
                    for (int j = 0; j < relationVariablesList.get(i).getRelationsDiscardedValuesList().size(); j++) {
                        RelationsDiscardedValues newRelationsDiscardedValues = new RelationsDiscardedValues();
                        newRelationsDiscardedValues.setIdDiscardedValue(relationVariablesList.get(i).getRelationsDiscardedValuesList().get(j).getIdDiscardedValue());
                        newRelationsDiscardedValues.setIdRelationVariables(relationVariablesList.get(i));
                        newRelationsDiscardedValues.setDiscardedValueName(relationVariablesList.get(i).getRelationsDiscardedValuesList().get(j).getDiscardedValueName());
                        newRelationVariables.getRelationsDiscardedValuesList().add(newRelationsDiscardedValues);
                    }
                    countFound++;
                    currentRelationsGroup.getRelationVariablesList().add(newRelationVariables);
                } else {
                    countNotFound++;
                }
            }
            //recargo los controles de relacion de variables
            FacesContext context = FacesContext.getCurrentInstance();
            errorsControlMB = (ErrorsControlMB) context.getApplication().evaluateExpressionGet(context, "#{errorsControlMB}", ErrorsControlMB.class);
            recordDataMB = (RecordDataMB) context.getApplication().evaluateExpressionGet(context, "#{recordDataMB}", RecordDataMB.class);
            errorsControlMB.reset();
            recordDataMB.reset();
            relationshipOfVariablesMB.reset();
            relationshipOfVariablesMB.setCurrentRelationsGroup(currentRelationsGroup);
            relationshipOfVariablesMB.loadRelatedVars();
            relationshipOfVariablesMB.loadVarsExpectedAndFound();
            relationshipOfVariablesMB.setValuesExpected(new ArrayList<String>());
            relationshipOfVariablesMB.setValuesFound(new ArrayList<String>());
            if (countNotFound == 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Correcto!!", "El grupo de relaciones (" + currentRelationGroupName + ") se ha cargado satisfactoriamente"));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia!!", "Relaciones cargadas: (" + String.valueOf(countFound) + "). Relaciones que no pudieron ser cargadas (" + String.valueOf(countNotFound) + ")."));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia!!", "Debe seleccionar un grupo de relaciones de la lista"));
        }
    }

    public void btnRemoveConfigurationConfirmationClick() {
        if (currentRelationGroupName.trim().length() != 0) {
            removeConfiguration();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Correcto!!", "grupo de relaciones eliminado"));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia!!", "Debe seleccionar un grupo de relaciones de la lista"));
        }
    }

    public void saveConfiguration() {
        RelationGroup newRelationGroup = new RelationGroup();
        Forms selectedForm = formsFacade.findByFormId(uploadFileMB.getCurrentForm());//Formulario seleccionado actualmente
        Sources selectedSource = sourcesFacade.find(uploadFileMB.getCurrentSource());//Fuente seleccionada actualmente

        int idRelationVariables;
        int idRelationValues;
        int idRelationDiscarded;

        idRelationVariables = relationVariablesFacade.findMaxId();
        idRelationValues = relationValuesFacade.findMaxId();
        idRelationDiscarded = relationDiscardedValuesFacade.findMaxId();
        newRelationGroup.setSourceId(selectedSource.getSourceId());
        newRelationGroup.setNameRelationGroup(newConfigurationName);
        newRelationGroup.setIdRelationGroup(relationGroupFacade.findMaxId() + 1);
        newRelationGroup.setFormId(selectedForm);
        relationGroupFacade.create(newRelationGroup);//persistir en la tabla relation_group

        List<RelationVariables> relationVarList = currentRelationsGroup.getRelationVariablesList();
        for (int i = 0; i < relationVarList.size(); i++) {//recorrer lista de relaciones            
            idRelationVariables++;
            RelationVariables newRelationVariables = new RelationVariables();
            newRelationVariables.setIdRelationVariables(idRelationVariables);
            newRelationVariables.setIdRelationGroup(newRelationGroup);
            newRelationVariables.setNameExpected(relationVarList.get(i).getNameExpected());
            newRelationVariables.setNameFound(relationVarList.get(i).getNameFound());
            newRelationVariables.setDateFormat(relationVarList.get(i).getDateFormat());
            newRelationVariables.setFieldType(relationVarList.get(i).getFieldType());
            newRelationVariables.setComparisonForCode(relationVarList.get(i).getComparisonForCode());
            relationVariablesFacade.create(newRelationVariables);//persistir en la tabla relation_group

            if (relationVarList.get(i).getRelationValuesList() != null) {
                for (int j = 0; j < relationVarList.get(i).getRelationValuesList().size(); j++) {//recorrer lista de valores
                    idRelationValues++;//por prueba
                    RelationValues newRelationValues = new RelationValues();
                    newRelationValues.setIdRelationValues(idRelationValues);
                    newRelationValues.setIdRelationVariables(newRelationVariables);
                    newRelationValues.setNameExpected(relationVarList.get(i).getRelationValuesList().get(j).getNameExpected());
                    newRelationValues.setNameFound(relationVarList.get(i).getRelationValuesList().get(j).getNameFound());
                    //aqui es donde no quiere persistir
                    relationValuesFacade.create(newRelationValues);//persisto el objeto
                }
            }
            if (relationVarList.get(i).getRelationsDiscardedValuesList() != null) {
                for (int j = 0; j < relationVarList.get(i).getRelationsDiscardedValuesList().size(); j++) {//recorrer lista de valores
                    idRelationDiscarded++;//por prueba
                    RelationsDiscardedValues newRelationDiscardedValues = new RelationsDiscardedValues();
                    newRelationDiscardedValues.setDiscardedValueName(relationVarList.get(i).getRelationsDiscardedValuesList().get(j).getDiscardedValueName());
                    newRelationDiscardedValues.setIdRelationVariables(newRelationVariables);
                    newRelationDiscardedValues.setIdDiscardedValue(idRelationDiscarded);
                    relationDiscardedValuesFacade.create(newRelationDiscardedValues);//persisto el objeto
                }
            }
        }
        newConfigurationName = "";
        //cargo los grupos de relaciones existentes
        loadRelatedGroups();

    }

    public void removeConfiguration() {
        try {
            RelationGroup selectRelationGroup = relationGroupFacade.findByName(currentRelationGroupName);
            List<RelationVariables> relationVariablesList = selectRelationGroup.getRelationVariablesList();
            for (int i = 0; i < relationVariablesList.size(); i++) {
                List<RelationsDiscardedValues> relationDiscardedValuesList = relationVariablesList.get(i).getRelationsDiscardedValuesList();
                for (int j = 0; j < relationDiscardedValuesList.size(); j++) {
                    relationDiscardedValuesFacade.remove(relationDiscardedValuesList.get(j));
                }
                relationVariablesList.get(i).setRelationsDiscardedValuesList(null);

                List<RelationValues> relationValuesList = relationVariablesList.get(i).getRelationValuesList();
                for (int j = 0; j < relationValuesList.size(); j++) {
                    relationValuesFacade.remove(relationValuesList.get(j));
                }
                //relationVariablesFacade.findAll();
                relationVariablesList.get(i).setRelationValuesList(null);
                relationVariablesFacade.remove(relationVariablesList.get(i));
            }
            //relationGroupFacade.findAll();
            selectRelationGroup.setRelationVariablesList(null);
            relationGroupFacade.remove(selectRelationGroup);

            loadRelatedGroups();
            btnLoadConfigurationDisabled = true;
            btnRemoveConfigurationDisabled = true;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error", e.toString()));
        }
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES QUE CARGAN VALORES -----------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public void changeRelationGroup() {
        newConfigurationName = currentRelationGroupName;
        if (currentRelationGroupName.trim().length() != 0) {
            btnLoadConfigurationDisabled = false;
            btnRemoveConfigurationDisabled = false;
        }
    }

    /*
     * Cargar los grupos relaciones existentes
     */
    public void loadRelatedGroups() {
        //-----------------------------------------------
        List<RelationGroup> relationGroupList = relationGroupFacade.findAll();
        relationGroups = new ArrayList<String>();
        for (int i = 0; i < relationGroupList.size(); i++) {
            relationGroups.add(relationGroupList.get(i).getNameRelationGroup());
        }
    }
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES GET Y SET DE LAS VARIABLES ---------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------

    public String getCurrentRelationGroupName() {
        return currentRelationGroupName;
    }

    public void setCurrentRelationGroupName(String currentRelationGroupName) {
        this.currentRelationGroupName = currentRelationGroupName;
    }

    public List<String> getRelationGroups() {
        return relationGroups;
    }

    public void setRelationGroups(List<String> relationGroups) {
        this.relationGroups = relationGroups;
    }

    public String getTxtOpenDialog() {
        return txtOpenDialog;
    }

    public void setTxtOpenDialog(String txtOpenDialog) {
        this.txtOpenDialog = txtOpenDialog;
    }

    public boolean isBtnLoadConfigurationDisabled() {
        return btnLoadConfigurationDisabled;
    }

    public void setBtnLoadConfigurationDisabled(boolean btnLoadConfigurationDisabled) {
        this.btnLoadConfigurationDisabled = btnLoadConfigurationDisabled;
    }

    public String getNewConfigurationName() {
        return newConfigurationName;
    }

    public void setNewConfigurationName(String newConfigurationName) {
//        if (newConfigurationName.trim().length() == 0) {
//            btnSaveConfigurationDisabled = true;
//        } else {
//            btnSaveConfigurationDisabled = false;
//        }
        this.newConfigurationName = newConfigurationName;
    }

    public String getTxtSaveDialog() {
        return txtSaveDialog;
    }

    public void setTxtSaveDialog(String txtSaveDialog) {
        this.txtSaveDialog = txtSaveDialog;
    }

    public void setUploadFileMB(UploadFileMB uploadFileMB) {
        this.uploadFileMB = uploadFileMB;
    }

    public void setRelationshipOfVariablesMB(RelationshipOfVariablesMB relationshipOfVariablesMB) {
        this.relationshipOfVariablesMB = relationshipOfVariablesMB;
    }

    public void setCurrentRelationsGroup(RelationGroup currentRelationsGroup) {
        this.currentRelationsGroup = currentRelationsGroup;
    }

    public boolean isBtnRemoveConfigurationDisabled() {
        return btnRemoveConfigurationDisabled;
    }

    public void setBtnRemoveConfigurationDisabled(boolean btnRemoveConfigurationDisabled) {
        this.btnRemoveConfigurationDisabled = btnRemoveConfigurationDisabled;
    }

    public void saveClickResult() {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Saliiiii");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public String getSaveClick() {
        return this.saveClick;
    }

    public void setSaveClick(String saveClick) {
        this.saveClick = saveClick;
    }
//    public boolean isBtnSaveConfigurationDisabled() {
//        return btnSaveConfigurationDisabled;
//    }
//
//    public void setBtnSaveConfigurationDisabled(boolean btnSaveConfigurationDisabled) {
//        this.btnSaveConfigurationDisabled = btnSaveConfigurationDisabled;
//    }
}
