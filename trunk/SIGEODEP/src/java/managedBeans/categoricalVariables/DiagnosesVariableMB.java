/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.categoricalVariables;

import beans.util.RowDataTable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import managedBeans.forms.*;
import model.dao.DiagnosesFacade;
import model.pojo.Diagnoses;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "diagnosesVariableMB")
@SessionScoped
public class DiagnosesVariableMB implements Serializable {

    /**
     * DIAGNOSTICOS CIE
     */
    private List<RowDataTable> rowDataTableList;
    private RowDataTable selectedRowDataTable;
    private int currentSearchCriteria = 0;
    private String currentSearchValue = "";
    @EJB
    DiagnosesFacade diagnosesFacade;
    private List<Diagnoses> diagnosesList;
    private Diagnoses currentDiagnoses;
    private String name = "";
    private String newName = "";
    private String code = "";
    private String newCode = "";
    private boolean btnEditDisabled=true;
    private boolean btnRemoveDisabled=true;
    
    

    public DiagnosesVariableMB() {
    }

    public void load() {
        currentDiagnoses = null;
        if (selectedRowDataTable != null) {
            currentDiagnoses = diagnosesFacade.find(selectedRowDataTable.getColumn1());
        }
        if (currentDiagnoses != null) {
            btnEditDisabled=false;
            btnRemoveDisabled=false;
            if (currentDiagnoses.getDiagnosisName() != null) {
                name = currentDiagnoses.getDiagnosisName();
            } else {
                name = "";
            }
            if (currentDiagnoses.getDiagnosisId() != null) {
                code = currentDiagnoses.getDiagnosisId();
            } else {
                code = "";
            }
        }
    }

    public void deleteRegistry() {
        if (currentDiagnoses != null) {
            diagnosesFacade.remove(currentDiagnoses);
            currentDiagnoses = null;
            selectedRowDataTable = null;
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "El registro fue eliminado");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        createDynamicTable(); btnEditDisabled=true; btnRemoveDisabled=true;
    }

    public void updateRegistry() {
        //determinar consecutivo
        if (currentDiagnoses != null) {
            if (name.trim().length() != 0 && code.trim().length() != 0) {
                name = name.toUpperCase();
                currentDiagnoses.setDiagnosisName(name);
                diagnosesFacade.edit(currentDiagnoses);
                name = "";
                currentDiagnoses = null;
                selectedRowDataTable = null;
                createDynamicTable(); btnEditDisabled=true; btnRemoveDisabled=true;
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "Registro actualizado");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "SIN NOMBRE O CODIGO", "Se debe digitar el código y nombre");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }

    }

    public void saveRegistry() {
        //determinar consecutivo
        if (newName.trim().length() != 0 && newCode.trim().length() != 0) {
            newName = newName.toUpperCase();
            newCode = newCode.toUpperCase();
            //determino si ya existe
            if (diagnosesFacade.find(newCode) != null) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "CODIGO EXISTENTE", "Ya existe un diagnóstico con este código");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                Diagnoses newRegistry = new Diagnoses(newCode, newName);
                diagnosesFacade.create(newRegistry);
                newName = "";
                currentDiagnoses = null;
                selectedRowDataTable = null;
                createDynamicTable(); btnEditDisabled=true; btnRemoveDisabled=true;
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "Nuevo registro almacenado");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "SIN NOMBRE O CODIGO", "Se debe digitar el codigo y nombre");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void newRegistry() {
        name = "";
        newName = "";
    }

    public void createDynamicTable() {
        boolean s = true;
        if (currentSearchValue.trim().length() == 0) {
            reset();
        } else {
            currentSearchValue = currentSearchValue.toUpperCase();
            rowDataTableList = new ArrayList<RowDataTable>();
            diagnosesList = diagnosesFacade.findCriteria(currentSearchCriteria, currentSearchValue);
            if (diagnosesList.isEmpty()) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "SIN DATOS", "No existen resultados para esta busqueda");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            for (int i = 0; i < diagnosesList.size(); i++) {
                rowDataTableList.add(new RowDataTable(
                        diagnosesList.get(i).getDiagnosisId().toString(),
                        diagnosesList.get(i).getDiagnosisName()));
            }
        }
    }

    public void reset() {
        rowDataTableList = new ArrayList<RowDataTable>();
        diagnosesList = diagnosesFacade.findAll();
        for (int i = 0; i < diagnosesList.size(); i++) {
            rowDataTableList.add(new RowDataTable(
                    diagnosesList.get(i).getDiagnosisId().toString(),
                    diagnosesList.get(i).getDiagnosisName()));
        }
    }

    public List<RowDataTable> getRowDataTableList() {
        return rowDataTableList;
    }

    public void setRowDataTableList(List<RowDataTable> rowDataTableList) {
        this.rowDataTableList = rowDataTableList;
    }

    public RowDataTable getSelectedRowDataTable() {
        return selectedRowDataTable;
    }

    public void setSelectedRowDataTable(RowDataTable selectedRowDataTable) {
        this.selectedRowDataTable = selectedRowDataTable;

    }

    public int getCurrentSearchCriteria() {
        return currentSearchCriteria;
    }

    public void setCurrentSearchCriteria(int currentSearchCriteria) {
        this.currentSearchCriteria = currentSearchCriteria;
    }

    public String getCurrentSearchValue() {
        return currentSearchValue;
    }

    public void setCurrentSearchValue(String currentSearchValue) {
        this.currentSearchValue = currentSearchValue;
    }

    public Diagnoses getCurrentDiagnoses() {
        return currentDiagnoses;
    }

    public void setCurrentDiagnoses(Diagnoses currentDiagnoses) {
        this.currentDiagnoses = currentDiagnoses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNewCode() {
        return newCode;
    }

    public void setNewCode(String newCode) {
        this.newCode = newCode;
    }
    
    public boolean isBtnEditDisabled() {
        return btnEditDisabled;
    }

    public void setBtnEditDisabled(boolean btnEditDisabled) {
        this.btnEditDisabled = btnEditDisabled;
    }

    public boolean isBtnRemoveDisabled() {
        return btnRemoveDisabled;
    }

    public void setBtnRemoveDisabled(boolean btnRemoveDisabled) {
        this.btnRemoveDisabled = btnRemoveDisabled;
    }
    
}
