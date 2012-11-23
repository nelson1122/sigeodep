/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.recordSets;

import beans.connection.ConnectionJdbcMB;
import beans.enumerators.FormsEnum;
import beans.util.RowDataTable;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import managedBeans.forms.HomicideMB;
import model.dao.FatalInjuriesFacade;
import model.dao.FatalInjuryMurderFacade;
import model.dao.TagsFacade;
import model.dao.VictimsFacade;
import model.pojo.FatalInjuries;
import model.pojo.FatalInjuryMurder;
import model.pojo.Tags;
import model.pojo.Victims;
import org.apache.poi.hssf.usermodel.*;
import org.primefaces.model.LazyDataModel;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "recordSetsHomicideMB")
@SessionScoped
public class RecordSetsHomicideMB implements Serializable {

    //--------------------
    @EJB
    TagsFacade tagsFacade;
    @EJB
    VictimsFacade victimsFacade;
    @EJB
    FatalInjuryMurderFacade fatalInjuryMurderFacade;
    @EJB
    FatalInjuriesFacade fatalInjuriesFacade;
    private List<Tags> tagsList;
    private FatalInjuryMurder currentFatalInjuryMurder;
    private RowDataTable[] selectedRowsDataTable;
    private int currentSearchCriteria = 0;
    private String currentSearchValue = "";
    private String name = "";
    private String newName = "";
    private boolean btnEditDisabled = true;
    private boolean btnRemoveDisabled = true;
    private String data = "-";
    private HomicideMB homicideMB;
    private String openForm = "";    
    private LazyDataModel<RowDataTable> table_model;
    private ArrayList<RowDataTable> rowsDataTableArrayList;
    private ConnectionJdbcMB connection;
    private String totalRecords = "0";
    private String initialDateStr = "";
    private String endDateStr = "";
    private int tuplesNumber;
    private Integer tuplesProcessed;
    private int progress = 0;//PROGRESO AL CREAR XLS
    private String sqlTags = "";
    private String sql = "";

    public void onCompleteLoad() {
        //progress = 0;
        System.out.println("Termino generacion de XLSX");
    }

    public RecordSetsHomicideMB() {
        tagsList = new ArrayList<Tags>();
        table_model = new LazyRecordSetsDataModel(0, "", FormsEnum.SCC_F_028);
        connection = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
    }

    public void printMessage(FacesMessage.Severity s, String title, String messageStr) {
        FacesMessage msg = new FacesMessage(s, title, messageStr);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public String openForm() {
        return openForm;
    }

    public void openInForm() {
        FacesContext context = FacesContext.getCurrentInstance();
        homicideMB = (HomicideMB) context.getApplication().evaluateExpressionGet(context, "#{homicideMB}", HomicideMB.class);
        homicideMB.loadValues(tagsList, currentFatalInjuryMurder);
        openForm = "homicide";
    }

    void loadValues(RowDataTable[] selectedRowsDataTableTags) {
        try {
            //CREO LA LISTA DE TAGS SELECCIONADOS        
            tagsList = new ArrayList<Tags>();
            data = "";
            for (int i = 0; i < selectedRowsDataTableTags.length; i++) {
                if (i == 0) {
                    data = data + " " + selectedRowsDataTableTags[i].getColumn2() + "  ";
                } else {
                    data = data + " || " + selectedRowsDataTableTags[i].getColumn2();
                }
                tagsList.add(tagsFacade.find(Integer.parseInt(selectedRowsDataTableTags[i].getColumn1())));
            }
            //DETERMINO TOTAL DE REGISTROS
            sql = "";
            sql = sql + " SELECT ";
            sql = sql + " count(*)";
            sql = sql + " FROM ";
            sql = sql + " public.victims, ";
            sql = sql + " public.fatal_injuries";
            sql = sql + " WHERE ";
            sql = sql + " fatal_injuries.victim_id = victims.victim_id AND";
            for (int i = 0; i < tagsList.size(); i++) {
                sql = sql + " victims.tag_id = " + String.valueOf(tagsList.get(i).getTagId()) + " AND ";
            }
            sql = sql + " fatal_injuries.injury_date >= to_date('" + initialDateStr + "','dd/MM/yyyy') AND";
            sql = sql + " fatal_injuries.injury_date <= to_date('" + endDateStr + "','dd/MM/yyyy') ";
            ResultSet resultSet = connection.consult(sql);
            totalRecords = "0";
            if (resultSet.next()) {
                totalRecords = String.valueOf(resultSet.getInt(1));
            }
            System.out.println("Total de registros = " + totalRecords);
            //DETERMINO EL ID DE CADA REGISTRO            
            sql = "";
            sql = sql + " SELECT ";
            sql = sql + " fatal_injuries.victim_id";
            sql = sql + " FROM ";
            sql = sql + " public.victims, ";
            sql = sql + " public.fatal_injuries";
            sql = sql + " WHERE ";
            sql = sql + " fatal_injuries.victim_id = victims.victim_id AND";
            for (int i = 0; i < tagsList.size(); i++) {
                sql = sql + " victims.tag_id = " + String.valueOf(tagsList.get(i).getTagId()) + " AND ";
            }
            sql = sql + " fatal_injuries.injury_date >= to_date('" + initialDateStr + "','dd/MM/yyyy') AND";
            sql = sql + " fatal_injuries.injury_date <= to_date('" + endDateStr + "','dd/MM/yyyy') ";

            //CONSTRUYO EL TABLE_MODEL
            table_model = new LazyRecordSetsDataModel(Integer.parseInt(totalRecords), sql, FormsEnum.SCC_F_028);

        } catch (SQLException ex) {
            Logger.getLogger(RecordSetsLcenfMB.class.getName()).log(Level.SEVERE, null, ex);
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

    public void postProcessXLS1() {
        try {
            progress = 0;
            tuplesNumber = Integer.parseInt(totalRecords);
            tuplesProcessed = 0;
            rowsDataTableArrayList = new ArrayList<RowDataTable>();
            ResultSet resultSet = connection.consult(sql);
            while (resultSet.next()) {
                rowsDataTableArrayList.add(connection.loadFatalInjuryMurderRecord(resultSet.getString(1)));
                tuplesProcessed++;
                progress = (int) (tuplesProcessed * 100) / tuplesNumber;
                System.out.println(progress);
            }
        } catch (SQLException ex) {
            Logger.getLogger(RecordSetsHomicideMB.class.getName()).log(Level.SEVERE, null, ex);
        }
        progress = 100;
    }
    
    public void postProcessXLS(Object document) {
        try {
            progress = 0;
            tuplesNumber = Integer.parseInt(totalRecords);
            tuplesProcessed = 0;

            int rowPosition = 1;
            HSSFWorkbook book = (HSSFWorkbook) document;
            HSSFSheet sheet = book.getSheetAt(0);// Se toma hoja del libro
            HSSFRow row;
            HSSFCellStyle cellStyle = book.createCellStyle();
            HSSFFont font = book.createFont();
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            cellStyle.setFont(font);

            row = sheet.createRow(rowPosition);// Se crea una fila dentro de la hoja

            createCell(cellStyle, row, 0, "CODIGO INTERNO");
            createCell(cellStyle, row, 1, "CODIGO");
            createCell(cellStyle, row, 2, "DIA HECHO");
            createCell(cellStyle, row, 3, "MES HECHO");
            createCell(cellStyle, row, 4, "AÑO HECHO");
            createCell(cellStyle, row, 5, "FECHA HECHO");
            createCell(cellStyle, row, 6, "DIA EN SEMANA");
            createCell(cellStyle, row, 7, "HORA HECHO");
            createCell(cellStyle, row, 8, "DIRECCION HECHO");
            createCell(cellStyle, row, 9, "BARRIO HECHO");
            createCell(cellStyle, row, 10, "COMUNA BARRIO HECHO");
            createCell(cellStyle, row, 11, "AREA DEL HECHO");
            createCell(cellStyle, row, 12, "CLASE DE LUGAR");
            createCell(cellStyle, row, 13, "NUMERO DE VICTIMAS");
            createCell(cellStyle, row, 14, "NOMBRES APELLIDOS");
            createCell(cellStyle, row, 15, "SEXO");
            createCell(cellStyle, row, 16, "TIPO EDAD");
            createCell(cellStyle, row, 17, "EDAD");
            createCell(cellStyle, row, 18, "OCUPACION");
            createCell(cellStyle, row, 19, "TIPO IDENTIFICACION");
            createCell(cellStyle, row, 20, "IDENTIFICACION");
            createCell(cellStyle, row, 21, "EXTRANJERO");
            createCell(cellStyle, row, 22, "DEPARTAMENTO RESIDENCIA");
            createCell(cellStyle, row, 23, "MUNICIPIO RESIDENCIA");
            createCell(cellStyle, row, 24, "BARRIO RESIDENCIA");
            createCell(cellStyle, row, 25, "COMUNA BARRIO RESIDENCIA");
            createCell(cellStyle, row, 26, "PAIS PROCEDENCIA");
            createCell(cellStyle, row, 27, "DEPARTAMENTO PROCEDENCIA");
            createCell(cellStyle, row, 28, "MUNICIPIO PROCEDENCIA");
            createCell(cellStyle, row, 29, "ARMA O CAUSA DE MUERTE");
            createCell(cellStyle, row, 30, "CONTEXTO RELACIONADO CON EL HECHO");
            createCell(cellStyle, row, 31, "NARRACION DEL HECHO");
            createCell(cellStyle, row, 32, "NIVEL DE ALCOHOL");
            createCell(cellStyle, row, 33, "TIPO NIVEL DE ALCOHOL");

            String[] splitDate;
            for (int i = 0; i < rowsDataTableArrayList.size(); i++) {

                RowDataTable rowDataTableList = rowsDataTableArrayList.get(i);
                rowPosition++;
                row = sheet.createRow(rowPosition);
                createCell(row, 0, rowDataTableList.getColumn1());//"CODIGO INTERNO");//"100">#{rowX.column1}</p:column>
                createCell(row, 1, rowDataTableList.getColumn23());//"CODIGO");//"100">#{rowX.column23}</p:column>
                if (rowDataTableList.getColumn13() != null) {
                    splitDate = rowDataTableList.getColumn13().split("/");
                    if (splitDate.length == 3) {
                        createCell(row, 2, splitDate[0]);//"AÑO HECHO");
                        createCell(row, 3, splitDate[1]);//"MES HECHO");
                        createCell(row, 4, splitDate[2]);//"AÑO HECHO");
                    }
                }
                createCell(row, 5, rowDataTableList.getColumn13());//"FECHA HECHO"
                createCell(row, 6, rowDataTableList.getColumn20());//"DIA EN SEMANA"
                createCell(row, 7, rowDataTableList.getColumn14());//"HORA HECHO"
                createCell(row, 8, rowDataTableList.getColumn15());//"DIRECCION HECHO"
                createCell(row, 9, rowDataTableList.getColumn16());//"BARRIO HECHO"
                createCell(row, 10, rowDataTableList.getColumn30());//"COMUNA BARRIO HECHO"
                createCell(row, 11, rowDataTableList.getColumn24());//"AREA DEL HECHO"
                createCell(row, 12, rowDataTableList.getColumn17());//"CLASE DE LUGAR"
                createCell(row, 13, rowDataTableList.getColumn18());//"NUMERO DE VICTIMAS"
                createCell(row, 14, rowDataTableList.getColumn4());//"NOMBRES APELLIDOS"
                createCell(row, 15, rowDataTableList.getColumn8());//"SEXO"
                createCell(row, 16, rowDataTableList.getColumn6());//"TIPO EDAD"
                createCell(row, 17, rowDataTableList.getColumn7());//"EDAD"
                createCell(row, 18, rowDataTableList.getColumn9());//"OCUPACION"
                createCell(row, 19, rowDataTableList.getColumn2());//"TIPO IDENTIFICACION"
                createCell(row, 20, rowDataTableList.getColumn3());//"IDENTIFICACION"
                createCell(row, 21, rowDataTableList.getColumn5());//"EXTRANJERO"
                createCell(row, 22, rowDataTableList.getColumn12());//"DEPARTAMENTO RESIDENCIA"
                createCell(row, 23, rowDataTableList.getColumn11());//"MUNICIPIO RESIDENCIA"
                createCell(row, 24, rowDataTableList.getColumn10());//"BARRIO RESIDENCIA"
                createCell(row, 25, rowDataTableList.getColumn31());//"COMUNA BARRIO RESIDENCIA"
                createCell(row, 26, rowDataTableList.getColumn25());//"PAIS PROCEDENCIA"
                createCell(row, 27, rowDataTableList.getColumn26());//"DEPARTAMENTO PROCEDENCIA"
                createCell(row, 28, rowDataTableList.getColumn27());//"MUNICIPIO PROCEDENCIA"
                createCell(row, 29, rowDataTableList.getColumn29());//"ARMA O CAUSA DE MUERTE"
                createCell(row, 30, rowDataTableList.getColumn28());//"CONTEXTO RELACIONADO CON EL HECHO"
                createCell(row, 31, rowDataTableList.getColumn19());//"NARRACION DEL HECHO"
                createCell(row, 32, rowDataTableList.getColumn21());//"NIVEL DE ALCOHOL"
                createCell(row, 33, rowDataTableList.getColumn22());//"TIPO NIVEL DE ALCOHOL"                
            }
        } catch (Exception ex) {
            Logger.getLogger(RecordSetsHomicideMB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void load() {
        currentFatalInjuryMurder = null;
        btnEditDisabled = true;
        btnRemoveDisabled = true;
        if (selectedRowsDataTable != null) {
            if (selectedRowsDataTable.length == 1) {
                currentFatalInjuryMurder = fatalInjuryMurderFacade.find(Integer.parseInt(selectedRowsDataTable[0].getColumn1()));
            }
            if (selectedRowsDataTable.length > 1) {
                btnEditDisabled = true;
                btnRemoveDisabled = false;
            } else {
                btnEditDisabled = false;
                btnRemoveDisabled = false;
            }
        }
    }

    public void deleteRegistry() {
        if (selectedRowsDataTable != null) {
            List<FatalInjuryMurder> fatalInjuryMurderList = new ArrayList<FatalInjuryMurder>();
            for (int j = 0; j < selectedRowsDataTable.length; j++) {
                fatalInjuryMurderList.add(fatalInjuryMurderFacade.find(Integer.parseInt(selectedRowsDataTable[j].getColumn1())));
            }
            if (fatalInjuryMurderList != null) {
                for (int j = 0; j < fatalInjuryMurderList.size(); j++) {
                    FatalInjuries auxFatalInjuries = fatalInjuryMurderList.get(j).getFatalInjuries();
                    Victims auxVictims = fatalInjuryMurderList.get(j).getFatalInjuries().getVictimId();
                    fatalInjuryMurderFacade.remove(fatalInjuryMurderList.get(j));
                    fatalInjuriesFacade.remove(auxFatalInjuries);
                    victimsFacade.remove(auxVictims);
                }
            }
            //deselecciono los controles
            selectedRowsDataTable = null;
            btnEditDisabled = true;
            btnRemoveDisabled = true;
            printMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se ha realizado la eliminacion de los registros seleccionados");
        }
    }

    public RowDataTable[] getSelectedRowsDataTable() {
        return selectedRowsDataTable;
    }

    public void setSelectedRowsDataTable(RowDataTable[] selectedRowsDataTable) {
        this.selectedRowsDataTable = selectedRowsDataTable;
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public LazyDataModel<RowDataTable> getTable_model() {
        return table_model;
    }

    public void setTable_model(LazyDataModel<RowDataTable> table_model) {
        this.table_model = table_model;
    }
    
    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(String totalRecords) {
        this.totalRecords = totalRecords;
    }

    public String getEndDateStr() {
        return endDateStr;
    }

    public void setEndDateStr(String endDateStr) {
        this.endDateStr = endDateStr;
    }

    public String getInitialDateStr() {
        return initialDateStr;
    }

    public void setInitialDateStr(String initialDateStr) {
        this.initialDateStr = initialDateStr;
    }
}
