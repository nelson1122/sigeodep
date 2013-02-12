/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.configurations;

import beans.connection.ConnectionJdbcMB;
import beans.util.RowDataTable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import model.dao.AccidentClassesFacade;
import model.pojo.AccidentClasses;
import org.apache.poi.hssf.usermodel.*;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "populationsMB")
@SessionScoped
public class PopulationsMB implements Serializable {

    /**
     * CLASE DE ACCIDENTE(TRANSITO)
     */
    private List<RowDataTable> rowDataTableList;
    private RowDataTable selectedRowDataTable;
    //private int currentSearchCriteria = 0;
    //private String currentSearchValue = "";
    //@EJB
    //AccidentClassesFacade accidentClassesFacade;
    //private List<AccidentClasses> accidentClassesList;
    //private AccidentClasses accidentClass;
    //private String name = "";//Nombre del barrio.
    //private String newName = "";//Nombre del barrio.
    //private boolean btnEditDisabled = true;
    //private boolean btnRemoveDisabled = true;
    private UploadedFile file;
    ConnectionJdbcMB connectionJdbcMB;

    public PopulationsMB() {
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);        
    }

    public void handleFileUpload(FileUploadEvent event) {
        try {
            file = event.getFile();
            copyFile(event.getFile().getFileName(), event.getFile().getInputstream());
            FacesMessage msg = new FacesMessage("Archivo cargado", "Archivo cargado correctamente, presione procesar para que sea procesado");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (IOException ex) {
            System.out.println("Error: ufMB_7 > " + ex.toString());
        }
    }

    public void copyFile(String fileName, InputStream in) {        
        try {
            OutputStream out = new FileOutputStream(new File(fileName));
            String out2 = "bb";
            
            int read = 0;
            byte[] bytes = new byte[1024];
            
            while ((read = in.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            in.close();
            out.flush();
            out.close();
            System.out.println("El nuevo fichero fue creado con éxito!");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
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
//        HSSFWorkbook book = (HSSFWorkbook) document;
//        HSSFRow row;
//        HSSFCellStyle cellStyle = book.createCellStyle();
//        HSSFFont font = book.createFont();
//        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
//        cellStyle.setFont(font);
//        row = sheet.createRow(0);// Se crea una fila dentro de la hoja        
//        createCell(cellStyle, row, 0, "CODIGO");//"100">#{rowX.column1}</p:column>
//        createCell(cellStyle, row, 1, "NOMBRE");//"100">#{rowX.column23}</p:column>                                
//        accidentClassesList=accidentClassesFacade.findAll();
//        for (int i = 0; i < accidentClassesList.size(); i++) {
//            row = sheet.createRow(i + 1);
//            createCell(row, 0, accidentClassesList.get(i).getAccidentClassId().toString());//CODIGO
//            createCell(row, 1, accidentClassesList.get(i).getAccidentClassName());//NOMBRE            
//        }
    }

    public void createDynamicTable() {
        try {
            ResultSet rs = connectionJdbcMB.consult("Select * from populations order by 3,2,1");
            rowDataTableList=new ArrayList<RowDataTable>();
            while (rs.next()) {
                rowDataTableList.add(new RowDataTable(
                        rs.getString(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4)));
            }
        } catch (Exception e) {
            System.out.println("Error populationMB_1: " + e.toString());
        }
    }

    public void reset() {
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
}
