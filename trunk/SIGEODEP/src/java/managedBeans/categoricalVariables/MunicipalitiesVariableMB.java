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
import org.apache.poi.hssf.usermodel.*;

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
    DepartamentsFacade departamentsFacade;
    private List<Departaments> departamentsList;
    @EJB
    MunicipalitiesFacade municipalitiesFacade;
    private List<Municipalities> municipalitiesList;
    private Municipalities currentMunicipalities;
    private Short department = 52;
    private Short newDepartment = 52;
    private SelectItem[] departaments;
    private String name = "";
    private String newName = "";
    private boolean btnEditDisabled = true;
    private boolean btnRemoveDisabled = true;

    public MunicipalitiesVariableMB() {
    }

    private void createCell(HSSFCellStyle cellStyle, HSSFRow fila, int position, String value) {
        HSSFCell cell;
        cell = fila.createCell((short) position);// Se crea una cell dentro de la fila                        
        cell.setCellValue(new HSSFRichTextString(value));
        cell.setCellStyle(cellStyle);
    }

    private void createCell(HSSFRow fila, int position, String value) {
        HSSFCell cell;
        cell = fila.createCell((short) position);// Se crea una cell dentro de la fila                        
        cell.setCellValue(new HSSFRichTextString(value));
    }

    public void postProcessXLS(Object document) {
        HSSFWorkbook book = (HSSFWorkbook) document;
        HSSFSheet sheet = book.getSheetAt(0);// Se toma hoja del libro
        HSSFRow row;
        HSSFCellStyle cellStyle = book.createCellStyle();
        HSSFFont font = book.createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        cellStyle.setFont(font);

        row = sheet.createRow(0);// Se crea una fila dentro de la hoja        
        createCell(cellStyle, row, 0, "CODIGO DEPARTAMENTO");//"100">#{rowX.column1}</p:column>
        createCell(cellStyle, row, 1, "NOMBRE DEPARTAMENTO");//"100">#{rowX.column23}</p:column>                                
        createCell(cellStyle, row, 2, "CODIGO MUNICIPIO");//"100">#{rowX.column1}</p:column>
        createCell(cellStyle, row, 3, "NOMBRE MUNICIPIO");//"100">#{rowX.column23}</p:column>                                

        departamentsList = departamentsFacade.findAll();
        int pos = 1;
        for (int i = 0; i < departamentsList.size(); i++) {
            municipalitiesList = departamentsList.get(i).getMunicipalitiesList();
            for (int j = 0; j < municipalitiesList.size(); j++) {
                row = sheet.createRow(pos);
                createCell(row, 0, departamentsList.get(i).getDepartamentId().toString());//CODIGO
                createCell(row, 1, departamentsList.get(i).getDepartamentName());//CODIGO            
                createCell(row, 2, String.valueOf(municipalitiesList.get(j).getMunicipalitiesPK().getMunicipalityId()));//NOMBRE            
                createCell(row, 3, municipalitiesList.get(j).getMunicipalityName());//NOMBRE            
                pos++;
            }


        }
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
