/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.configurations;

import beans.util.RowDataTable;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author santos
 */
@ManagedBean(name = "backupsMB")
@SessionScoped
public class BackupsMB {

    private List<RowDataTable> rowDataTableList;
    private RowDataTable selectedRowDataTable;
    private boolean btnEditDisabled=true;
    private boolean btnRemoveDisabled=true;
    private String name = "";//Nombre del barrio.
    private String newName = "";//Nombre del barrio.
    
    public BackupsMB() {
    }
    
    public void newRegistry() {
        name = "";
        newName="";
    }
    
    public void reset(){
    }
    
    public void load() {
        
    }
    
    public void updateRegistry() {
        //determinar consecutivo
//        if (currentActivitiy != null) {
//            if (name.trim().length() != 0) {
//                name=name.toUpperCase();
//                currentActivitiy.setActivityName(name);
//                activitiesFacade.edit(currentActivitiy);
//                name = "";
//                currentActivitiy=null;                
//                selectedRowDataTable=null;
//                createDynamicTable(); btnEditDisabled=true; btnRemoveDisabled=true;
//                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "Registro actualizado");
//                FacesContext.getCurrentInstance().addMessage(null, msg);
//            } else {                
//                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "SIN NOMBRE", "Se debe digitar un nombre");
//                FacesContext.getCurrentInstance().addMessage(null, msg);
//            }
//        }
        
    }
    
    public void saveRegistry() {
        //determinar consecutivo
//        if (newName.trim().length() != 0) {
//            int max = activitiesFacade.findMax() + 1;
//            newName=newName.toUpperCase();
//            Activities newRegistry = new Activities((short) max, newName);
//            activitiesFacade.create(newRegistry);
//            newName = "";
//            currentActivitiy=null;
//            selectedRowDataTable=null;
//            createDynamicTable(); btnEditDisabled=true; btnRemoveDisabled=true;
//            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "Nuevo registro almacenado");
//            FacesContext.getCurrentInstance().addMessage(null, msg);
//        } else {
//            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "SIN NOMBRE", "Se debe digitar un nombre");
//            FacesContext.getCurrentInstance().addMessage(null, msg);
//        }        
    }
    
    public void deleteRegistry() {
//        if (currentActivitiy != null) {
//            activitiesFacade.remove(currentActivitiy);
//            currentActivitiy=null;            
//            selectedRowDataTable=null;
//            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "El registro fue eliminado");
//            FacesContext.getCurrentInstance().addMessage(null, msg);
//        }
//        createDynamicTable(); btnEditDisabled=true; btnRemoveDisabled=true;
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
    
    
}
