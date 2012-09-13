/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.categoricalVariables;

import beans.util.RowDataTable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import managedBeans.forms.*;
import model.dao.DepartamentsFacade;
import model.dao.MunicipalitiesFacade;
import model.pojo.Departaments;
import model.pojo.Municipalities;
import model.pojo.MunicipalitiesPK;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "municipalitiesVariableMB")
@SessionScoped
public class MunicipalitiesVariableMB implements Serializable {

    /**
     * BARRIOS
     */
    private List<RowDataTable> rowDataTableList;
    private RowDataTable selectedRowDataTable;
    private int currentSearchCriteria = 0;
    private String currentSearchValue = "";
    @EJB
    MunicipalitiesFacade municipalitiesFacade;
    private List<Municipalities> municipalitiesList;
    private Municipalities currentMunicipalities;
    @EJB
    DepartamentsFacade departamentsFacade;
    private Short department = 52;
    private Short newDepartment = 52;
    private SelectItem[] departaments;
    private String name = "";
    private String newName = "";
    private boolean btnEditDisabled = true;
    private boolean btnRemoveDisabled = true;
    
    public MunicipalitiesVariableMB() {
    }

    public void load() {
        currentMunicipalities = null;
        if (selectedRowDataTable != null) {
            //busco el codigo del departamento
            Departaments auxDepartment = departamentsFacade.findByName(selectedRowDataTable.getColumn3());
            MunicipalitiesPK munPk = new MunicipalitiesPK(auxDepartment.getDepartamentId(), Short.parseShort(selectedRowDataTable.getColumn1()));
            currentMunicipalities = municipalitiesFacade.find(munPk);
        }
        if (currentMunicipalities != null) {
            btnEditDisabled = false;
            btnRemoveDisabled = false;
            name = currentMunicipalities.getMunicipalityName();
            department = currentMunicipalities.getMunicipalitiesPK().getDepartamentId();
        }
    }

    public void deleteRegistry() {
        if (currentMunicipalities != null) {
            municipalitiesFacade.remove(currentMunicipalities);
            currentMunicipalities = null;
            selectedRowDataTable = null;
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "El registro fue eliminado");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        createDynamicTable();
        btnEditDisabled = true;
        btnRemoveDisabled = true;
    }

    public void updateRegistry() {
        //determinar consecutivo
        if (currentMunicipalities != null) {
            if (name.trim().length() != 0) {
                name = name.toUpperCase();
                currentMunicipalities.setMunicipalityName(name);
                municipalitiesFacade.edit(currentMunicipalities);
                name = "";
                currentMunicipalities = null;
                selectedRowDataTable = null;
                createDynamicTable();
                btnEditDisabled = true;
                btnRemoveDisabled = true;
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


            //int max = municipalitiesFacade.findMax() + 1;
            newName = newName.toUpperCase();
            //determino el codigo           
            short idMun = municipalitiesFacade.findMax(newDepartment);
            idMun = (short) (idMun + 1);
            Municipalities newRegistry = new Municipalities();
            MunicipalitiesPK munPk = new MunicipalitiesPK(newDepartment, idMun);
            newRegistry.setDepartaments(departamentsFacade.find(newDepartment));
            newRegistry.setMunicipalitiesPK(munPk);
            newRegistry.setMunicipalityName(newName);
            municipalitiesFacade.create(newRegistry);
            currentMunicipalities = null;
            selectedRowDataTable = null;
            createDynamicTable();
            btnEditDisabled = true;
            btnRemoveDisabled = true;
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "Nuevo registro almacenado");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "SIN NOMBRE", "Se debe digitar un nombre");
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
            municipalitiesList = municipalitiesFacade.findCriteria(currentSearchCriteria, currentSearchValue);
            if (municipalitiesList.isEmpty()) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "SIN DATOS", "No existen resultados para esta busqueda");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            for (int i = 0; i < municipalitiesList.size(); i++) {
                rowDataTableList.add(new RowDataTable(
                        String.valueOf(municipalitiesList.get(i).getMunicipalitiesPK().getMunicipalityId()),
                        municipalitiesList.get(i).getMunicipalityName(),
                        municipalitiesList.get(i).getDepartaments().getDepartamentName(),
                        String.valueOf(i)));
            }
        }
    }

    @PostConstruct
    public void reset() {
        rowDataTableList = new ArrayList<RowDataTable>();
        municipalitiesList = municipalitiesFacade.findAll();
        for (int i = 0; i < municipalitiesList.size(); i++) {
            rowDataTableList.add(new RowDataTable(
                    String.valueOf(municipalitiesList.get(i).getMunicipalitiesPK().getMunicipalityId()),
                    municipalitiesList.get(i).getMunicipalityName(),
                    municipalitiesList.get(i).getDepartaments().getDepartamentName(),
                    String.valueOf(i)));
        }
        //cargo los departamentos
        List<Departaments> departamentsList = departamentsFacade.findAll();
        departaments = new SelectItem[departamentsList.size() + 1];
        departaments[0] = new SelectItem(0, "");
        for (int i = 0; i < departamentsList.size(); i++) {
            departaments[i + 1] = new SelectItem(departamentsList.get(i).getDepartamentId(), departamentsList.get(i).getDepartamentName());
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

    public Short getDepartment() {
        return department;
    }

    public void setDepartment(Short department) {
        this.department = department;
    }

    public Short getNewDepartment() {
        return newDepartment;
    }

    public void setNewDepartment(Short newDepartment) {
        this.newDepartment = newDepartment;
    }

    public SelectItem[] getDepartaments() {
        return departaments;
    }

    public void setDepartaments(SelectItem[] departaments) {
        this.departaments = departaments;
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
