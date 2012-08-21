/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.fileProcessing;

import beans.relations.RelationValue;
import beans.relations.RelationVar;
import beans.relations.RelationsGroup;
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
public class StoredRelationsMB implements Serializable{

    private String currentRelationGroupName;
    private List<String> relationGroups;
    private String txtOpenDialog;
    private String newConfigurationName = "";
    private boolean btnLoadConfigurationDisabled = true;
    private boolean btnSaveConfigurationDisabled = true;
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
    private RelationsGroup currentRelationsGroup;//grupo de relaciones actual

    public StoredRelationsMB() {
        /**
         * Creates a new instance of StoredRelationsMB
         */
    }

    public void reset() {
    }

    public String btnSaveConfiguration() {
        if (newConfigurationName.trim().length() == 0) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Debe digitar un nombre para el grupo de relaciones");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return "";
        }

        if (currentRelationsGroup != null) {
            List<RelationVar> relationVarList = currentRelationsGroup.getRelationVarList();
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
                exist = true;
                break;
            }
        }
        if (exist) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "El nombre digitado ya existe, primero debe eliminar la configuracion.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return "";
        } else {
            btnSaveConfigurationConfirmationClick();
            return "saveDialog.show()";
        }
        //return this.saveClick;
    }

    public void btnLoadConfigurationClick() {
        //se prueba que las relacion que se este abriendo corresponda a las variables encontradas
        //y variables esperadas
        RelationGroup selectRelationGroup = relationGroupFacade.findByName(currentRelationGroupName);
        List<RelationVariables> relationVariablesList = relationVariablesFacade.findByRelationGroup(selectRelationGroup.getIdRelationGroup());
        List<RelationValues> relationValuesList;
        currentRelationsGroup = new RelationsGroup(
                selectRelationGroup.getNameRelationGroup(),
                selectRelationGroup.getFormId().getFormId(),
                selectRelationGroup.getSourceId().toString());
        
        for (int i = 0; i < relationVariablesList.size(); i++) {
            RelationVar newRelationVar = new RelationVar(
                    relationVariablesList.get(i).getNameExpected(),
                    relationVariablesList.get(i).getNameFound(),
                    relationVariablesList.get(i).getFieldType(),
                    relationVariablesList.get(i).getComparisonForCode(),
                    relationVariablesList.get(i).getDateFormat());
            //cargo los valores descartados
            ArrayList<String> discardedValues=new ArrayList<String>();
            if(relationVariablesList.get(i).getRelationsDiscardedValuesList()!=null){
                for (int j = 0; j < relationVariablesList.get(i).getRelationsDiscardedValuesList().size(); j++) {
                    discardedValues.add(relationVariablesList.get(i).getRelationsDiscardedValuesList().get(j).getDiscardedValueName());
                }                
            }
            newRelationVar.setDiscardedValues(discardedValues);
            
            currentRelationsGroup.addRelationVar(newRelationVar);
            relationValuesList = relationValuesFacade.findByRelationVariables(relationVariablesList.get(i).getIdRelationVariables());
            for (int j = 0; j < relationValuesList.size(); j++) {
                newRelationVar.addRelationValue(
                        relationValuesList.get(j).getNameExpected(),
                        relationValuesList.get(j).getNameFound());
            }           
        }
        //recargo los controles de relackoan de variables
        relationshipOfVariablesMB.setCurrentRelationsGroup(currentRelationsGroup);
        relationshipOfVariablesMB.loadRelatedVars();
        relationshipOfVariablesMB.loadVarsExpectedAndFound();
        relationshipOfVariablesMB.setValuesExpected(new ArrayList<String>());
        relationshipOfVariablesMB.setValuesFound(new ArrayList<String>());

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Correcto!!", "El grupo de relaciones (" + currentRelationGroupName + ") se ha cargado satisfactoriamente"));

    }

    public void btnRemoveConfigurationConfirmationClick() {
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
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Correcto!!", "grupo de relaciones eliminado"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error", e.toString()));
        }
    }

    public void btnSaveConfigurationConfirmationClick() {

        RelationGroup newRelationGroup = new RelationGroup();
        //determino el form actual
        Forms selectedForm = formsFacade.findByFormId(uploadFileMB.getCurrentForm());//Form seleccionado actualmente
        Sources selectedSource = sourcesFacade.findBySourceName(uploadFileMB.getCurrentSource());//Form seleccionado actualmente
        int idRelationGroup;
        int idRelationVariables;
        int idRelationValues;
        int idRelationDiscarded;

        idRelationGroup = relationGroupFacade.findMaxId();
        idRelationVariables = relationVariablesFacade.findMaxId();
        idRelationValues = relationValuesFacade.findMaxId();
        idRelationDiscarded = relationDiscardedValuesFacade.findMaxId();

        newRelationGroup.setSourceId(selectedSource.getSourceId());
        newRelationGroup.setNameRelationGroup(newConfigurationName);
        newRelationGroup.setIdRelationGroup(idRelationGroup+1);
        newRelationGroup.setFormId(selectedForm);
        //persiste correctamente
        relationGroupFacade.create(newRelationGroup);//persistir en la tabla relation_group

        List<RelationVar> relationVarList = currentRelationsGroup.getRelationVarList();
        ArrayList<RelationValue> relationValuesList;
        ArrayList<String> relationDiscartedValuesList;


        for (int i = 0; i < relationVarList.size(); i++) {//recorrer lista de relaciones            
            idRelationVariables++;//por prueba

            RelationVariables newRelationVariables = new RelationVariables();
            newRelationVariables.setIdRelationVariables(idRelationVariables);
            newRelationVariables.setIdRelationGroup(newRelationGroup);
            newRelationVariables.setNameExpected(relationVarList.get(i).getNameExpected());
            newRelationVariables.setNameFound(relationVarList.get(i).getNameFound());
            newRelationVariables.setDateFormat(relationVarList.get(i).getDateFormat());
            newRelationVariables.setFieldType(relationVarList.get(i).getFieldType());
            newRelationVariables.setComparisonForCode(relationVarList.get(i).getTypeComparisonForCode());
            //persiste correctamente
            relationVariablesFacade.create(newRelationVariables);//persistir en la tabla relation_group

            relationValuesList = relationVarList.get(i).getRelationValueList();
            for (int j = 0; j < relationValuesList.size(); j++) {//recorrer lista de valores
                idRelationValues++;//por prueba
                RelationValues newRelationValues = new RelationValues();
                newRelationValues.setIdRelationValues(idRelationValues);
                newRelationValues.setIdRelationVariables(newRelationVariables);
                newRelationValues.setNameExpected(relationValuesList.get(j).getNameExpected());
                newRelationValues.setNameFound(relationValuesList.get(j).getNameFound());
                //aqui es donde no quiere persistir
                relationValuesFacade.create(newRelationValues);//persisto el objeto
            }
            relationDiscartedValuesList= relationVarList.get(i).getDiscardedValues();
            for (int j = 0; j < relationDiscartedValuesList.size(); j++) {//recorrer lista de valores
                idRelationDiscarded++;//por prueba
                RelationsDiscardedValues newRelationDiscardedValues = new RelationsDiscardedValues();
                newRelationDiscardedValues.setDiscardedValueName(relationDiscartedValuesList.get(j));
                newRelationDiscardedValues.setIdRelationVariables(newRelationVariables);
                newRelationDiscardedValues.setIdDiscardedValue(idRelationDiscarded);                
                relationDiscardedValuesFacade.create(newRelationDiscardedValues);//persisto el objeto
            }
        }
        newConfigurationName = "";
        //cargo los grupos de relaciones existentes
        loadRelatedGroups();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Guardado", "La configuración ha sido almacenada"));

    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES QUE CARGAN VALORES -----------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public void changeRelationGroup() {
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
        if (newConfigurationName.trim().length() == 0) {
            btnSaveConfigurationDisabled = true;
        } else {
            btnSaveConfigurationDisabled = false;
        }
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

    public void setCurrentRelationsGroup(RelationsGroup currentRelationsGroup) {
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

    public boolean isBtnSaveConfigurationDisabled() {
        return btnSaveConfigurationDisabled;
    }

    public void setBtnSaveConfigurationDisabled(boolean btnSaveConfigurationDisabled) {
        this.btnSaveConfigurationDisabled = btnSaveConfigurationDisabled;
    }
}
