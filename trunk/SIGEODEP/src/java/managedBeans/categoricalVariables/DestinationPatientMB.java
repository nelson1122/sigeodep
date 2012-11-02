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
import model.dao.DestinationsOfPatientFacade;
import model.pojo.DestinationsOfPatient;
import org.apache.poi.hssf.usermodel.*;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "destinationPatientMB")
@SessionScoped
public class DestinationPatientMB implements Serializable {

    /**
     * DESTINO DEL PACIENTE
     */
    private List<RowDataTable> rowDataTableList;
    private RowDataTable selectedRowDataTable;
    private int currentSearchCriteria = 0;
    private String currentSearchValue = "";
    @EJB
    DestinationsOfPatientFacade destinationsOfPatientFacade;
    private List<DestinationsOfPatient> destinationsOfPatientList;
    private DestinationsOfPatient currentDestinationsOfPatient;
    private String name = "";
    private String newName = "";
    private boolean btnEditDisabled=true;
    private boolean btnRemoveDisabled=true;
    

    public DestinationPatientMB() {
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
        destinationsOfPatientList=destinationsOfPatientFacade.findAll();
        for (int i = 0; i < destinationsOfPatientList.size(); i++) {
            row = sheet.createRow(i + 1);
            createCell(row, 0, destinationsOfPatientList.get(i).getDestinationPatientId().toString());//CODIGO
            createCell(row, 1, destinationsOfPatientList.get(i).getDestinationPatientName());//NOMBRE            
        }
    }

    public void load() {
        currentDestinationsOfPatient = null;
        if (selectedRowDataTable != null) {
            currentDestinationsOfPatient = destinationsOfPatientFacade.find(Short.parseShort(selectedRowDataTable.getColumn1()));
        }
        if (currentDestinationsOfPatient != null) {
            btnEditDisabled=false;
            btnRemoveDisabled=false;
            if (currentDestinationsOfPatient.getDestinationPatientName() != null) {
                name = currentDestinationsOfPatient.getDestinationPatientName();
            } else {
                name = "";
            }
        }
    }

    public void deleteRegistry() {
        if (currentDestinationsOfPatient != null) {
            destinationsOfPatientFacade.remove(currentDestinationsOfPatient);
            currentDestinationsOfPatient=null;            
            selectedRowDataTable=null;
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "El registro fue eliminado");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        createDynamicTable(); btnEditDisabled=true; btnRemoveDisabled=true;
    }

    public void updateRegistry() {
        //determinar consecutivo
        if (currentDestinationsOfPatient != null) {
            if (name.trim().length() != 0) {
                name=name.toUpperCase();
                currentDestinationsOfPatient.setDestinationPatientName(name);
                destinationsOfPatientFacade.edit(currentDestinationsOfPatient);
                name = "";
                currentDestinationsOfPatient=null;                
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
            int max = destinationsOfPatientFacade.findMax() + 1;
            newName=newName.toUpperCase();
            DestinationsOfPatient newRegistry = new DestinationsOfPatient((short) max, newName);
            destinationsOfPatientFacade.create(newRegistry);
            newName = "";
            currentDestinationsOfPatient=null;
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
            destinationsOfPatientList = destinationsOfPatientFacade.findCriteria(currentSearchCriteria, currentSearchValue);
            if (destinationsOfPatientList.isEmpty()) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "SIN DATOS", "No existen resultados para esta busqueda");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            for (int i = 0; i < destinationsOfPatientList.size(); i++) {
                rowDataTableList.add(new RowDataTable(
                        destinationsOfPatientList.get(i).getDestinationPatientId().toString(),
                        destinationsOfPatientList.get(i).getDestinationPatientName()));
            }
        }
    }

    public void reset() {
        rowDataTableList = new ArrayList<RowDataTable>();
        destinationsOfPatientList = destinationsOfPatientFacade.findAll();        
        for (int i = 0; i < destinationsOfPatientList.size(); i++) {
            rowDataTableList.add(new RowDataTable(
                    destinationsOfPatientList.get(i).getDestinationPatientId().toString(),
                    destinationsOfPatientList.get(i).getDestinationPatientName()));
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
