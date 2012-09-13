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
import model.dao.MechanismsFacade;
import model.pojo.Mechanisms;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "mechanismVariableMB")
@SessionScoped
public class MechanismVariableMB implements Serializable {

    /**
     * MECANISMO / OBJETO DE LA LESION
     */
    private List<RowDataTable> rowDataTableList;
    private RowDataTable selectedRowDataTable;
    private int currentSearchCriteria = 0;
    private String currentSearchValue = "";
    @EJB
    MechanismsFacade mechanismsFacade;
    private List<Mechanisms> mechanismsList;
    private Mechanisms currentMechanisms;
    private String name = "";
    private String newName = "";
    private boolean btnEditDisabled=true;
    private boolean btnRemoveDisabled=true;
    
    public MechanismVariableMB() {
    }

    public void load() {
        currentMechanisms = null;
        if (selectedRowDataTable != null) {
            currentMechanisms = mechanismsFacade.find(Short.parseShort(selectedRowDataTable.getColumn1()));
        }
        if (currentMechanisms != null) {
            btnEditDisabled=false;
            btnRemoveDisabled=false;
            if (currentMechanisms.getMechanismName() != null) {
                name = currentMechanisms.getMechanismName();
            } else {
                name = "";
            }
        }
    }

    public void deleteRegistry() {
        if (currentMechanisms != null) {
            mechanismsFacade.remove(currentMechanisms);
            currentMechanisms=null;            
            selectedRowDataTable=null;
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "El registro fue eliminado");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        createDynamicTable(); btnEditDisabled=true; btnRemoveDisabled=true;
    }

    public void updateRegistry() {
        //determinar consecutivo
        if (currentMechanisms != null) {
            if (name.trim().length() != 0) {
                name=name.toUpperCase();
                currentMechanisms.setMechanismName(name);
                mechanismsFacade.edit(currentMechanisms);
                name = "";
                currentMechanisms=null;                
                selectedRowDataTable=null;
                createDynamicTable(); btnEditDisabled=true; btnRemoveDisabled=true;
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "Registro actualizado");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {                
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "SIN NOMBRE", "Se debe digitar un nombre");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
        
    }

    public void saveRegistry() {
        //determinar consecutivo
        if (newName.trim().length() != 0) {
            int max = mechanismsFacade.findMax() + 1;
            newName=newName.toUpperCase();
            Mechanisms newRegistry = new Mechanisms((short) max, newName);
            mechanismsFacade.create(newRegistry);
            newName = "";
            currentMechanisms=null;
            selectedRowDataTable=null;
            createDynamicTable(); btnEditDisabled=true; btnRemoveDisabled=true;
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "Nuevo registro almacenado");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "SIN NOMBRE", "Se debe digitar un nombre");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }        
    }

    public void newRegistry() {
        name = "";
        newName="";
    }

    public void createDynamicTable() {
        boolean s = true;
        if (currentSearchValue.trim().length() == 0) {
            reset();
        } else {
            currentSearchValue = currentSearchValue.toUpperCase();
            rowDataTableList = new ArrayList<RowDataTable>();
            mechanismsList = mechanismsFacade.findCriteria(currentSearchCriteria, currentSearchValue);
            if (mechanismsList.isEmpty()) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "SIN DATOS", "No existen resultados para esta busqueda");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            for (int i = 0; i < mechanismsList.size(); i++) {
                rowDataTableList.add(new RowDataTable(
                        mechanismsList.get(i).getMechanismId().toString(),
                        mechanismsList.get(i).getMechanismName()));
            }
        }
    }

    public void reset() {
        rowDataTableList = new ArrayList<RowDataTable>();
        mechanismsList = mechanismsFacade.findAll();        
        for (int i = 0; i < mechanismsList.size(); i++) {
            rowDataTableList.add(new RowDataTable(
                    mechanismsList.get(i).getMechanismId().toString(),
                    mechanismsList.get(i).getMechanismName()));
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
