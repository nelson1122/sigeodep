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
import model.dao.WeaponTypesFacade;
import model.pojo.WeaponTypes;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "weaponTypeVariableMB")
@SessionScoped
public class WeaponTypeVariableMB implements Serializable {

    /**
     * ARMA O CAUSA DE MUERTE
     */
    private List<RowDataTable> rowDataTableList;
    private RowDataTable selectedRowDataTable;
    private int currentSearchCriteria = 0;
    private String currentSearchValue = "";
    @EJB
    WeaponTypesFacade weaponTypesFacade;
    private List<WeaponTypes> weaponTypesList;
    private WeaponTypes currentWeaponType;
    private String name = "";
    private String newName = "";
    private boolean btnEditDisabled=true;
    private boolean btnRemoveDisabled=true;
    
    public WeaponTypeVariableMB() {
    }

    public void load() {
        currentWeaponType = null;
        if (selectedRowDataTable != null) {
            currentWeaponType = weaponTypesFacade.find(Short.parseShort(selectedRowDataTable.getColumn1()));
        }
        if (currentWeaponType != null) {
            btnEditDisabled=false;
            btnRemoveDisabled=false;
            if (currentWeaponType.getWeaponTypeName() != null) {
                name = currentWeaponType.getWeaponTypeName();
            } else {
                name = "";
            }
        }
    }

    public void deleteRegistry() {
        if (currentWeaponType != null) {
            weaponTypesFacade.remove(currentWeaponType);
            currentWeaponType=null;            
            selectedRowDataTable=null;
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "El registro fue eliminado");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        createDynamicTable();
        btnEditDisabled=true; btnRemoveDisabled=true;
    }

    public void updateRegistry() {
        //determinar consecutivo
        if (currentWeaponType != null) {
            if (name.trim().length() != 0) {
                name=name.toUpperCase();
                currentWeaponType.setWeaponTypeName(name);
                weaponTypesFacade.edit(currentWeaponType);
                name = "";
                currentWeaponType=null;                
                selectedRowDataTable=null;
                createDynamicTable();
                btnEditDisabled=true; btnRemoveDisabled=true;
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
            int max = weaponTypesFacade.findMax() + 1;
            newName=newName.toUpperCase();
            WeaponTypes newRegistry = new WeaponTypes((short) max, newName);
            weaponTypesFacade.create(newRegistry);
            newName = "";
            currentWeaponType=null;
            selectedRowDataTable=null;
            createDynamicTable();
            btnEditDisabled=true; btnRemoveDisabled=true;
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
            weaponTypesList = weaponTypesFacade.findCriteria(currentSearchCriteria, currentSearchValue);
            if (weaponTypesList.isEmpty()) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "SIN DATOS", "No existen resultados para esta busqueda");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            for (int i = 0; i < weaponTypesList.size(); i++) {
                rowDataTableList.add(new RowDataTable(
                        weaponTypesList.get(i).getWeaponTypeId().toString(),
                        weaponTypesList.get(i).getWeaponTypeName()));
            }
        }
    }

    public void reset() {
        rowDataTableList = new ArrayList<RowDataTable>();
        weaponTypesList = weaponTypesFacade.findAll();
        for (int i = 0; i < weaponTypesList.size(); i++) {
            rowDataTableList.add(new RowDataTable(
                    weaponTypesList.get(i).getWeaponTypeId().toString(),
                    weaponTypesList.get(i).getWeaponTypeName()));
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
