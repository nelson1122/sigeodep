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
import model.dao.HealthProfessionalsFacade;
import model.pojo.HealthProfessionals;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "healtProfessionalVariableMB")
@SessionScoped
public class HealtProfessionalVariableMB implements Serializable {

    /**
     * MEDICOS
     */
    private List<RowDataTable> rowDataTableList;
    private RowDataTable selectedRowDataTable;
    private int currentSearchCriteria = 0;
    private String currentSearchValue = "";
    
    @EJB
    HealthProfessionalsFacade healthProfessionalsFacade;
    
    private List<HealthProfessionals> healthProfessionalsList;
    private HealthProfessionals currentHealthProfessionals;
    private int healthProfessionalId = 0;//id
    private String healthProfessionalName = "";//nombre
    private String healthProfessionalIdentification = "";//identificacion
    private String healthProfessionalSpecialty = "";//especialidad
    private String newHealthProfessionalName = "";//nombre
    private String newHealthProfessionalIdentification = "";//identificacion
    private String newHealthProfessionalSpecialty = "";//especialidad
    private boolean btnEditDisabled=true;
    private boolean btnRemoveDisabled=true;
    
    public HealtProfessionalVariableMB() {
    }

    public void load() {
        currentHealthProfessionals = null;
        if (selectedRowDataTable != null) {
            currentHealthProfessionals = healthProfessionalsFacade.find(Integer.parseInt(selectedRowDataTable.getColumn1()));
            healthProfessionalId=Integer.parseInt(selectedRowDataTable.getColumn1());
        }
        if (currentHealthProfessionals != null) {
            btnEditDisabled=false;
            btnRemoveDisabled=false;
            if (currentHealthProfessionals.getHealthProfessionalName() != null) {
                healthProfessionalName = currentHealthProfessionals.getHealthProfessionalName();// integer NOT NULL, -- Código del barrio.
            } else {
                healthProfessionalName = "";
           }
            if (currentHealthProfessionals.getHealthProfessionalIdentification() != null) {
                healthProfessionalIdentification = currentHealthProfessionals.getHealthProfessionalIdentification();
            } else {
                healthProfessionalIdentification = "";
            }
            if (currentHealthProfessionals.getHealthProfessionalSpecialty() != null) {
                healthProfessionalSpecialty = currentHealthProfessionals.getHealthProfessionalSpecialty();
            } else {
                healthProfessionalSpecialty = "";
            }
        }
    }

    public void deleteRegistry() {
        if (currentHealthProfessionals != null) {
            healthProfessionalsFacade.remove(currentHealthProfessionals);
            currentHealthProfessionals=null;            
            selectedRowDataTable=null;
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "El registro fue eliminado");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        createDynamicTable(); btnEditDisabled=true; btnRemoveDisabled=true;
    }

    public void updateRegistry() {
        //determinar consecutivo
        if (currentHealthProfessionals != null) {
            if (healthProfessionalName.trim().length() != 0) {
                healthProfessionalName=healthProfessionalName.toUpperCase();
                currentHealthProfessionals.setHealthProfessionalName(healthProfessionalName);
                currentHealthProfessionals.setHealthProfessionalIdentification(healthProfessionalIdentification);
                currentHealthProfessionals.setHealthProfessionalSpecialty(healthProfessionalSpecialty);                
                healthProfessionalsFacade.edit(currentHealthProfessionals);
                healthProfessionalName = "";
                currentHealthProfessionals=null;                
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
        if (newHealthProfessionalName.trim().length() != 0) {
            int max = healthProfessionalsFacade.findMax() + 1;
            newHealthProfessionalName=newHealthProfessionalName.toUpperCase();
            HealthProfessionals newRegistry = new HealthProfessionals(max);
            newRegistry.setHealthProfessionalName(newHealthProfessionalName);
            newRegistry.setHealthProfessionalIdentification("");
            newRegistry.setHealthProfessionalSpecialty("");
            healthProfessionalsFacade.create(newRegistry);
            newHealthProfessionalName = "";
            currentHealthProfessionals=null;
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
        healthProfessionalName = "";
        newHealthProfessionalName = "";        
        newHealthProfessionalIdentification="";
        newHealthProfessionalSpecialty="";
    }

    public void createDynamicTable() {
        boolean s = true;
        if (currentSearchValue.trim().length() == 0) {
            reset();
        } else {
            currentSearchValue = currentSearchValue.toUpperCase();
            rowDataTableList = new ArrayList<RowDataTable>();
            healthProfessionalsList = healthProfessionalsFacade.findCriteria(currentSearchCriteria, currentSearchValue);
            if (healthProfessionalsList.isEmpty()) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "SIN DATOS", "No existen resultados para esta busqueda");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            for (int i = 0; i < healthProfessionalsList.size(); i++) {
                rowDataTableList.add(new RowDataTable(
                        healthProfessionalsList.get(i).getHealthProfessionalId().toString(),
                        healthProfessionalsList.get(i).getHealthProfessionalName(),
                        healthProfessionalsList.get(i).getHealthProfessionalIdentification()));
            }
        }
    }

    public void reset() {
        rowDataTableList = new ArrayList<RowDataTable>();
        healthProfessionalsList = healthProfessionalsFacade.findAll();        
        for (int i = 0; i < healthProfessionalsList.size(); i++) {
            rowDataTableList.add(new RowDataTable(
                    healthProfessionalsList.get(i).getHealthProfessionalId().toString(),
                    healthProfessionalsList.get(i).getHealthProfessionalName(),
                    healthProfessionalsList.get(i).getHealthProfessionalIdentification()));
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

    public String getHealthProfessionalIdentification() {
        return healthProfessionalIdentification;
    }

    public void setHealthProfessionalIdentification(String healthProfessionalIdentification) {
        this.healthProfessionalIdentification = healthProfessionalIdentification;
    }

    public String getHealthProfessionalName() {
        return healthProfessionalName;
    }

    public void setHealthProfessionalName(String healthProfessionalName) {
        this.healthProfessionalName = healthProfessionalName;
    }

    public String getHealthProfessionalSpecialty() {
        return healthProfessionalSpecialty;
    }

    public void setHealthProfessionalSpecialty(String healthProfessionalSpecialty) {
        this.healthProfessionalSpecialty = healthProfessionalSpecialty;
    }

    public String getNewHealthProfessionalIdentification() {
        return newHealthProfessionalIdentification;
    }

    public void setNewHealthProfessionalIdentification(String newHealthProfessionalIdentification) {
        this.newHealthProfessionalIdentification = newHealthProfessionalIdentification;
    }

    public String getNewHealthProfessionalName() {
        return newHealthProfessionalName;
    }

    public void setNewHealthProfessionalName(String newHealthProfessionalName) {
        this.newHealthProfessionalName = newHealthProfessionalName;
    }

    public String getNewHealthProfessionalSpecialty() {
        return newHealthProfessionalSpecialty;
    }

    public void setNewHealthProfessionalSpecialty(String newHealthProfessionalSpecialty) {
        this.newHealthProfessionalSpecialty = newHealthProfessionalSpecialty;
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
