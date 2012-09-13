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
import model.dao.NonFatalDataSourcesFromWhereFacade;
import model.pojo.NonFatalDataSourcesFromWhere;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "fromWhereSumbitedVariableMB")
@SessionScoped
public class FromWhereSumbitedVariableMB implements Serializable {

    /**
     * DE DONDE ES REMITIDO PARA VIF
     */
    private List<RowDataTable> rowDataTableList;
    private RowDataTable selectedRowDataTable;
    private int currentSearchCriteria = 0;
    private String currentSearchValue = "";
    @EJB
    NonFatalDataSourcesFromWhereFacade nonFatalDataSourcesFromWhereFacade;
    private List<NonFatalDataSourcesFromWhere> nonFatalDataSourcesFromWhereList;
    private NonFatalDataSourcesFromWhere currentNonFatalDataSourcesFromWhere;
    private String name = "";
    private String newName = "";
    private boolean btnEditDisabled=true;
    private boolean btnRemoveDisabled=true;
    
    public FromWhereSumbitedVariableMB() {
    }

    public void load() {
        currentNonFatalDataSourcesFromWhere = null;
        if (selectedRowDataTable != null) {
            currentNonFatalDataSourcesFromWhere = nonFatalDataSourcesFromWhereFacade.find(Short.parseShort(selectedRowDataTable.getColumn1()));
        }
        if (currentNonFatalDataSourcesFromWhere != null) {
            btnEditDisabled=false;
            btnRemoveDisabled=false;
            if (currentNonFatalDataSourcesFromWhere.getNonFatalDataSourcesFromWhereName() != null) {
                name = currentNonFatalDataSourcesFromWhere.getNonFatalDataSourcesFromWhereName();
            } else {
                name = "";
            }        }
    }

    public void deleteRegistry() {
        if (currentNonFatalDataSourcesFromWhere != null) {
            nonFatalDataSourcesFromWhereFacade.remove(currentNonFatalDataSourcesFromWhere);
            currentNonFatalDataSourcesFromWhere=null;            
            selectedRowDataTable=null;
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "El registro fue eliminado");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        createDynamicTable(); btnEditDisabled=true; btnRemoveDisabled=true;
    }

    public void updateRegistry() {
        //determinar consecutivo
        if (currentNonFatalDataSourcesFromWhere != null) {
            if (name.trim().length() != 0) {
                name=name.toUpperCase();
                currentNonFatalDataSourcesFromWhere.setNonFatalDataSourcesFromWhereName(name);
                nonFatalDataSourcesFromWhereFacade.edit(currentNonFatalDataSourcesFromWhere);
                name = "";
                currentNonFatalDataSourcesFromWhere=null;                
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
            int max = nonFatalDataSourcesFromWhereFacade.findMax() + 1;
            newName=newName.toUpperCase();
            NonFatalDataSourcesFromWhere newRegistry = new NonFatalDataSourcesFromWhere((short) max, newName);
            nonFatalDataSourcesFromWhereFacade.create(newRegistry);
            newName = "";
            currentNonFatalDataSourcesFromWhere=null;
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
            nonFatalDataSourcesFromWhereList = nonFatalDataSourcesFromWhereFacade.findCriteria(currentSearchCriteria, currentSearchValue);
            if (nonFatalDataSourcesFromWhereList.isEmpty()) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "SIN DATOS", "No existen resultados para esta busqueda");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            for (int i = 0; i < nonFatalDataSourcesFromWhereList.size(); i++) {
                rowDataTableList.add(new RowDataTable(
                        nonFatalDataSourcesFromWhereList.get(i).getNonFatalDataSourcesFromWhereId().toString(),
                        nonFatalDataSourcesFromWhereList.get(i).getNonFatalDataSourcesFromWhereName()));
            }
        }
    }

    public void reset() {
        rowDataTableList = new ArrayList<RowDataTable>();
        nonFatalDataSourcesFromWhereList = nonFatalDataSourcesFromWhereFacade.findAll();        
        for (int i = 0; i < nonFatalDataSourcesFromWhereList.size(); i++) {
            rowDataTableList.add(new RowDataTable(
                    nonFatalDataSourcesFromWhereList.get(i).getNonFatalDataSourcesFromWhereId().toString(),
                    nonFatalDataSourcesFromWhereList.get(i).getNonFatalDataSourcesFromWhereName()));
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
