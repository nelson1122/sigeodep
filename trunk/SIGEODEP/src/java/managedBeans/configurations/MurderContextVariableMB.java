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
import model.dao.MurderContextsFacade;
import model.pojo.MurderContexts;
import org.apache.poi.hssf.usermodel.*;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "murderContextVariableMB")
@SessionScoped
public class MurderContextVariableMB implements Serializable {

    /**
     * CONTEXTO RELACIONADOS CON EL HECHO
     */
    private List<RowDataTable> rowDataTableList;
    private RowDataTable selectedRowDataTable;
    private int currentSearchCriteria = 0;
    private String currentSearchValue = "";
    @EJB
    MurderContextsFacade murderContextsFacade;
    private List<MurderContexts> murderContextsList;
    private MurderContexts currentMurderContexts;
    private String name = "";
    private String newName = "";
    private boolean btnEditDisabled=true;
    private boolean btnRemoveDisabled=true;
    
    public MurderContextVariableMB() {
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
        murderContextsList=murderContextsFacade.findAll();
        for (int i = 0; i < murderContextsList.size(); i++) {
            row = sheet.createRow(i + 1);
            createCell(row, 0, murderContextsList.get(i).getMurderContextId().toString());//CODIGO
            createCell(row, 1, murderContextsList.get(i).getMurderContextName());//NOMBRE            
        }
    }
    
    public void load() {
        currentMurderContexts = null;
        if (selectedRowDataTable != null) {
            currentMurderContexts = murderContextsFacade.find(Short.parseShort(selectedRowDataTable.getColumn1()));
        }
        if (currentMurderContexts != null) {
            btnEditDisabled=false;
            btnRemoveDisabled=false;
            if (currentMurderContexts.getMurderContextName() != null) {
                name = currentMurderContexts.getMurderContextName();
            } else {
                name = "";
            }
        }
    }

    public void deleteRegistry() {
        if (currentMurderContexts != null) {
            murderContextsFacade.remove(currentMurderContexts);
            currentMurderContexts=null;            
            selectedRowDataTable=null;
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "El registro fue eliminado");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        createDynamicTable(); btnEditDisabled=true; btnRemoveDisabled=true;
    }

    public void updateRegistry() {
        //determinar consecutivo
        if (currentMurderContexts != null) {
            if (name.trim().length() != 0) {
                name=name.toUpperCase();
                currentMurderContexts.setMurderContextName(name);
                murderContextsFacade.edit(currentMurderContexts);
                name = "";
                currentMurderContexts=null;                
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
            int max = murderContextsFacade.findMax() + 1;
            newName=newName.toUpperCase();
            MurderContexts newRegistry = new MurderContexts((short) max, newName);
            murderContextsFacade.create(newRegistry);
            newName = "";
            currentMurderContexts=null;
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
            rowDataTableList = new ArrayList<>();
            murderContextsList = murderContextsFacade.findCriteria(currentSearchCriteria, currentSearchValue);
            if (murderContextsList.isEmpty()) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "SIN DATOS", "No existen resultados para esta busqueda");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            for (int i = 0; i < murderContextsList.size(); i++) {
                rowDataTableList.add(new RowDataTable(
                        murderContextsList.get(i).getMurderContextId().toString(),
                        murderContextsList.get(i).getMurderContextName()));
            }
        }
    }

    public void reset() {
        rowDataTableList = new ArrayList<>();
        murderContextsList = murderContextsFacade.findAll();        
        for (int i = 0; i < murderContextsList.size(); i++) {
            rowDataTableList.add(new RowDataTable(
                    murderContextsList.get(i).getMurderContextId().toString(),
                    murderContextsList.get(i).getMurderContextName()));
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
