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
import model.dao.ProtectiveMeasuresFacade;
import model.pojo.ProtectiveMeasures;
import org.apache.poi.hssf.usermodel.*;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "protectiveMeasuresVariableMB")
@SessionScoped
public class ProtectiveMeasuresVariableMB implements Serializable {

    /**
     * MEDIDAS DE PROTECCION(TRANSITO)
     */
    private List<RowDataTable> rowDataTableList;
    private RowDataTable selectedRowDataTable;
    private int currentSearchCriteria = 0;
    private String currentSearchValue = "";
    @EJB
    ProtectiveMeasuresFacade protectiveMeasuresFacade;
    private List<ProtectiveMeasures> protectiveMeasuresFacadeList;
    private ProtectiveMeasures currentProtectiveMeasure;
    private String name = "";
    private String newName = "";
    private boolean btnEditDisabled=true;
    private boolean btnRemoveDisabled=true;
    
    public ProtectiveMeasuresVariableMB() {
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
        protectiveMeasuresFacadeList=protectiveMeasuresFacade.findAll();
        for (int i = 0; i < protectiveMeasuresFacadeList.size(); i++) {
            row = sheet.createRow(i + 1);
            createCell(row, 0, protectiveMeasuresFacadeList.get(i).getProtectiveMeasuresId().toString());//CODIGO
            createCell(row, 1, protectiveMeasuresFacadeList.get(i).getProtectiveMeasuresName());//NOMBRE            
        }
    }

    public void load() {
        currentProtectiveMeasure = null;
        if (selectedRowDataTable != null) {
            currentProtectiveMeasure = protectiveMeasuresFacade.find(Short.parseShort(selectedRowDataTable.getColumn1()));
        }
        if (currentProtectiveMeasure != null) {
            btnEditDisabled=false;
            btnRemoveDisabled=false;
            if (currentProtectiveMeasure.getProtectiveMeasuresName() != null) {
                name = currentProtectiveMeasure.getProtectiveMeasuresName();
            } else {
                name = "";
            }
        }
    }

    public void deleteRegistry() {
        if (currentProtectiveMeasure != null) {
            protectiveMeasuresFacade.remove(currentProtectiveMeasure);
            currentProtectiveMeasure=null;            
            selectedRowDataTable=null;
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "El registro fue eliminado");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        createDynamicTable();
        btnEditDisabled=true; btnRemoveDisabled=true;
    }

    public void updateRegistry() {
        //determinar consecutivo
        if (currentProtectiveMeasure != null) {
            if (name.trim().length() != 0) {
                name=name.toUpperCase();
                currentProtectiveMeasure.setProtectiveMeasuresName(name);
                protectiveMeasuresFacade.edit(currentProtectiveMeasure);
                name = "";
                currentProtectiveMeasure=null;                
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
            int max = protectiveMeasuresFacade.findMax() + 1;
            newName=newName.toUpperCase();
            ProtectiveMeasures newRegistry = new ProtectiveMeasures((short) max, newName);
            protectiveMeasuresFacade.create(newRegistry);
            newName = "";
            currentProtectiveMeasure=null;
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
            protectiveMeasuresFacadeList = protectiveMeasuresFacade.findCriteria(currentSearchCriteria, currentSearchValue);
            if (protectiveMeasuresFacadeList.isEmpty()) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "SIN DATOS", "No existen resultados para esta busqueda");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            for (int i = 0; i < protectiveMeasuresFacadeList.size(); i++) {
                rowDataTableList.add(new RowDataTable(
                        protectiveMeasuresFacadeList.get(i).getProtectiveMeasuresId().toString(),
                        protectiveMeasuresFacadeList.get(i).getProtectiveMeasuresName()));
            }
        }
    }

    public void reset() {
        rowDataTableList = new ArrayList<RowDataTable>();
        protectiveMeasuresFacadeList = protectiveMeasuresFacade.findAll();
        for (int i = 0; i < protectiveMeasuresFacadeList.size(); i++) {
            rowDataTableList.add(new RowDataTable(
                    protectiveMeasuresFacadeList.get(i).getProtectiveMeasuresId().toString(),
                    protectiveMeasuresFacadeList.get(i).getProtectiveMeasuresName()));
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
