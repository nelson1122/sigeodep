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
import model.dao.InsuranceFacade;
import model.pojo.Insurance;
import org.apache.poi.hssf.usermodel.*;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "insurancesVariableMB")
@SessionScoped
public class InsurancesVariableMB implements Serializable {

    /**
     * OCUPACIONES
     */
    private List<RowDataTable> rowDataTableList;
    private RowDataTable selectedRowDataTable;
    private int currentSearchCriteria = 0;
    private String currentSearchValue = "";
    @EJB
    InsuranceFacade insuranceFacade;
    private List<Insurance> insuranceList;
    private Insurance currentInsurance;
    private String name = "";
    private String newName = "";
    private String code = "";
    private String newCode = "";
    private boolean btnEditDisabled = true;
    private boolean btnRemoveDisabled = true;

    public InsurancesVariableMB() {
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
        insuranceList = insuranceFacade.findAll();
        for (int i = 0; i < insuranceList.size(); i++) {
            row = sheet.createRow(i + 1);
            createCell(row, 0, insuranceList.get(i).getInsuranceId().toString());//CODIGO
            createCell(row, 1, insuranceList.get(i).getInsuranceName());//NOMBRE            
        }
    }

    public void load() {
        currentInsurance = null;
        if (selectedRowDataTable != null) {
            currentInsurance = insuranceFacade.find(selectedRowDataTable.getColumn1());
        }
        if (currentInsurance != null) {
            btnEditDisabled = false;
            btnRemoveDisabled = false;
            if (currentInsurance.getInsuranceName() != null) {
                name = currentInsurance.getInsuranceName();
                code = currentInsurance.getInsuranceId();
            } else {
                name = "";
                code = "";
            }
        }
    }

    public void deleteRegistry() {
        if (currentInsurance != null) {
            insuranceFacade.remove(currentInsurance);
            currentInsurance = null;
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
//        if (currentInsurance != null) {
//            if (name.trim().length() != 0) {
//                if (newCode.trim().length() != 0) {
//                    if (insuranceFacade.find(newCode) == null) {
//                        name = name.toUpperCase();
//                        currentInsurance.setInsuranceName(name);
//                        insuranceFacade.edit(currentInsurance);
//                        name = "";
//                        currentInsurance = null;
//                        selectedRowDataTable = null;
//                        createDynamicTable();
//                        btnEditDisabled = true;
//                        btnRemoveDisabled = true;
//                        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "Registro actualizado");
//                        FacesContext.getCurrentInstance().addMessage(null, msg);
//                    } else {
//                        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "CODIGO EXISTENTE", "El código digitado ya se encuentra registrado");
//                        FacesContext.getCurrentInstance().addMessage(null, msg);
//                    }
//                } else {
//                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "SIN CODIGO", "Se debe digitar un código");
//                    FacesContext.getCurrentInstance().addMessage(null, msg);
//                }
//            } else {
//                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "SIN NOMBRE", "Se debe digitar un nombre");
//                FacesContext.getCurrentInstance().addMessage(null, msg);
//            }
//        }
    }

    public void saveRegistry() {
        //determinar consecutivo
        if (newName.trim().length() != 0) {
            if (newCode.trim().length() != 0) {
                
                if (insuranceFacade.findByCode(newCode) == null) {
                    if (insuranceFacade.findByName(newName) == null) {
                        newCode = newCode.toUpperCase();
                        newName = newName.toUpperCase();
                        Insurance newRegistry = new Insurance();
                        newRegistry.setInsuranceId(newCode);
                        newRegistry.setInsuranceName(newName);
                        insuranceFacade.create(newRegistry);
                        newName = "";
                        newCode = "";
                        currentInsurance = null;
                        selectedRowDataTable = null;
                        createDynamicTable();
                        btnEditDisabled = true;
                        btnRemoveDisabled = true;
                        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "Nuevo registro almacenado");
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                    } else {
                        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "NOMBRE EXISTENTE", "El nombre digitado ya se encuentra registrado");
                        FacesContext.getCurrentInstance().addMessage(null, msg);
                    }
                } else {
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "CODIGO EXISTENTE", "El código digitado ya se encuentra registrado");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }
            } else {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "SIN CODIGO", "Se debe digitar un código");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
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
            insuranceList = insuranceFacade.findCriteria(currentSearchCriteria, currentSearchValue);
            if (insuranceList.isEmpty()) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "SIN DATOS", "No existen resultados para esta busqueda");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            for (int i = 0; i < insuranceList.size(); i++) {
                rowDataTableList.add(new RowDataTable(
                        insuranceList.get(i).getInsuranceId().toString(),
                        insuranceList.get(i).getInsuranceName()));
            }
        }
    }

    public void reset() {
        rowDataTableList = new ArrayList<RowDataTable>();
        insuranceList = insuranceFacade.findAll();
        for (int i = 0; i < insuranceList.size(); i++) {
            rowDataTableList.add(new RowDataTable(
                    insuranceList.get(i).getInsuranceId().toString(),
                    insuranceList.get(i).getInsuranceName()));
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNewCode() {
        return newCode;
    }

    public void setNewCode(String newCode) {
        this.newCode = newCode;
    }
}
