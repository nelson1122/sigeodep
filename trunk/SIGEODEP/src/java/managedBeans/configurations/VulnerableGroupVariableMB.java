/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.configurations;

import beans.util.RowDataTable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import model.dao.VulnerableGroupsFacade;
import model.pojo.VulnerableGroups;
import org.apache.poi.hssf.usermodel.*;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "vulnerableGroupVariableMB")
@SessionScoped
public class VulnerableGroupVariableMB implements Serializable {

    /**
     * GRUPOS VULNERABLES
     */
    private List<RowDataTable> rowDataTableList;
    private RowDataTable selectedRowDataTable;
    private int currentSearchCriteria = 0;
    private String currentSearchValue = "";
    @EJB
    VulnerableGroupsFacade vulnerableGroupsFacade;
    private List<VulnerableGroups> vulnerableGroupsList;
    private VulnerableGroups currentVulnerableGroups;
    private String name = "";
    private String newName = "";
    private boolean btnEditDisabled=true;
    private boolean btnRemoveDisabled=true;
    
    public VulnerableGroupVariableMB() {
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
        createCell(cellStyle, row, 0, "CODIGO");//"100">#{rowX.column1}</p:column>
        createCell(cellStyle, row, 1, "NOMBRE");//"100">#{rowX.column23}</p:column>                                
        vulnerableGroupsList=vulnerableGroupsFacade.findAll();
        for (int i = 0; i < vulnerableGroupsList.size(); i++) {
            row = sheet.createRow(i + 1);
            createCell(row, 0, vulnerableGroupsList.get(i).getVulnerableGroupId().toString());//CODIGO
            createCell(row, 1, vulnerableGroupsList.get(i).getVulnerableGroupName());//NOMBRE            
        }
    }

    public void load() {
        currentVulnerableGroups = null;
        if (selectedRowDataTable != null) {
            currentVulnerableGroups = vulnerableGroupsFacade.find(Short.parseShort(selectedRowDataTable.getColumn1()));
        }
        if (currentVulnerableGroups != null) {
            btnEditDisabled=false;
            btnRemoveDisabled=false;
            if (currentVulnerableGroups.getVulnerableGroupName() != null) {
                name = currentVulnerableGroups.getVulnerableGroupName();
            } else {
                name = "";
            }
        }
    }

    public void deleteRegistry() {
        if (currentVulnerableGroups != null) {
            vulnerableGroupsFacade.remove(currentVulnerableGroups);
            currentVulnerableGroups=null;            
            selectedRowDataTable=null;
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "El registro fue eliminado");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        createDynamicTable();
        btnEditDisabled=true; btnRemoveDisabled=true;
    }

    public void updateRegistry() {
        //determinar consecutivo
        if (currentVulnerableGroups != null) {
            if (name.trim().length() != 0) {
                name=name.toUpperCase();
                currentVulnerableGroups.setVulnerableGroupName(name);
                vulnerableGroupsFacade.edit(currentVulnerableGroups);
                name = "";
                currentVulnerableGroups=null;                
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
            int max = vulnerableGroupsFacade.findMax() + 1;
            newName=newName.toUpperCase();
            VulnerableGroups newRegistry = new VulnerableGroups((short) max);
            newRegistry.setVulnerableGroupName(newName);            
            vulnerableGroupsFacade.create(newRegistry);
            newName = "";
            currentVulnerableGroups=null;
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
            vulnerableGroupsList = vulnerableGroupsFacade.findCriteria(currentSearchCriteria, currentSearchValue);
            if (vulnerableGroupsList.isEmpty()) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "SIN DATOS", "No existen resultados para esta busqueda");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            for (int i = 0; i < vulnerableGroupsList.size(); i++) {                
                rowDataTableList.add(new RowDataTable(
                        vulnerableGroupsList.get(i).getVulnerableGroupId().toString(), 
                        vulnerableGroupsList.get(i).getVulnerableGroupName()));
            }
        }
    }

    public void reset() {
        rowDataTableList = new ArrayList<RowDataTable>();
            vulnerableGroupsList = vulnerableGroupsFacade.findAll();            
            for (int i = 0; i < vulnerableGroupsList.size(); i++) {                
                rowDataTableList.add(new RowDataTable(
                        vulnerableGroupsList.get(i).getVulnerableGroupId().toString(), 
                        vulnerableGroupsList.get(i).getVulnerableGroupName()));
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
