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
import model.dao.NonFatalPlacesFacade;
import model.pojo.NonFatalPlaces;
import org.apache.poi.hssf.usermodel.*;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "nonFatalPlacesVariableMB")
@SessionScoped
public class NonFatalPlacesVariableMB implements Serializable {

    /**
     * CLASE DE LUGR DE LOS HECHOS (PARA NO FATALES)
     */
    private List<RowDataTable> rowDataTableList;
    private RowDataTable selectedRowDataTable;
    private int currentSearchCriteria = 0;
    private String currentSearchValue = "";
    @EJB
    NonFatalPlacesFacade nonFatalPlacesFacade;
    private List<NonFatalPlaces> nonFatalPlacesList;
    private NonFatalPlaces currentNonFatalPlace;
    private String name = "";//Nombre del barrio.
    private String newName = "";//Nombre del barrio.
    private boolean btnEditDisabled=true;
    private boolean btnRemoveDisabled=true;
    
    public NonFatalPlacesVariableMB() {
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
        nonFatalPlacesList=nonFatalPlacesFacade.findAll();
        for (int i = 0; i < nonFatalPlacesList.size(); i++) {
            row = sheet.createRow(i + 1);
            createCell(row, 0, nonFatalPlacesList.get(i).getNonFatalPlaceId().toString());//CODIGO
            createCell(row, 1, nonFatalPlacesList.get(i).getNonFatalPlaceName());//NOMBRE            
        }
    }

    public void load() {
        currentNonFatalPlace = null;
        if (selectedRowDataTable != null) {
            currentNonFatalPlace = nonFatalPlacesFacade.find(Short.parseShort(selectedRowDataTable.getColumn1()));
        }
        if (currentNonFatalPlace != null) {
            btnEditDisabled=false;
            btnRemoveDisabled=false;
            if (currentNonFatalPlace.getNonFatalPlaceName() != null) {
                name = currentNonFatalPlace.getNonFatalPlaceName();
            } else {
                name = "";
            }
        }
    }

    public void deleteRegistry() {
        if (currentNonFatalPlace != null) {
            nonFatalPlacesFacade.remove(currentNonFatalPlace);
            currentNonFatalPlace=null;            
            selectedRowDataTable=null;
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "El registro fue eliminado");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        createDynamicTable(); btnEditDisabled=true; btnRemoveDisabled=true;
    }

    public void updateRegistry() {
        //determinar consecutivo
        if (currentNonFatalPlace != null) {
            if (name.trim().length() != 0) {
                name=name.toUpperCase();
                currentNonFatalPlace.setNonFatalPlaceName(name);
                nonFatalPlacesFacade.edit(currentNonFatalPlace);
                name = "";
                currentNonFatalPlace=null;                
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
            int max = nonFatalPlacesFacade.findMax() + 1;
            newName=newName.toUpperCase();
            NonFatalPlaces newRegistry = new NonFatalPlaces((short) max);
            newRegistry.setNonFatalPlaceName(newName);
            nonFatalPlacesFacade.create(newRegistry);
            newName = "";
            currentNonFatalPlace=null;
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
            nonFatalPlacesList = nonFatalPlacesFacade.findCriteria(currentSearchCriteria, currentSearchValue);
            if (nonFatalPlacesList.isEmpty()) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "SIN DATOS", "No existen resultados para esta busqueda");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            for (int i = 0; i < nonFatalPlacesList.size(); i++) {
                rowDataTableList.add(new RowDataTable(
                        nonFatalPlacesList.get(i).getNonFatalPlaceId().toString(),
                        nonFatalPlacesList.get(i).getNonFatalPlaceName()));
            }
        }
    }

    public void reset() {
        rowDataTableList = new ArrayList<RowDataTable>();
        nonFatalPlacesList = nonFatalPlacesFacade.findAll();
        for (int i = 0; i < nonFatalPlacesList.size(); i++) {
            rowDataTableList.add(new RowDataTable(
                    nonFatalPlacesList.get(i).getNonFatalPlaceId().toString(),
                    nonFatalPlacesList.get(i).getNonFatalPlaceName()));
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
