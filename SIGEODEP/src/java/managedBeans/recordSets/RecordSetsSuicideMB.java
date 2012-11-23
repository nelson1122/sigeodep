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
import managedBeans.forms.SuicideMB;
import model.dao.*;
import model.pojo.FatalInjuries;
import model.pojo.FatalInjurySuicide;
import model.pojo.Tags;
import model.pojo.Victims;
import org.apache.poi.hssf.usermodel.*;
import org.primefaces.model.LazyDataModel;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "recordSetsSuicideMB")
@SessionScoped
public class RecordSetsSuicideMB implements Serializable {

    @EJB
    NeighborhoodsFacade neighborhoodsFacade;
    @EJB
    CountriesFacade countriesFacade;
    //--------------------
    @EJB
    TagsFacade tagsFacade;
    private List<Tags> tagsList;
    private Tags currentTag;
    private FatalInjurySuicide currentFatalInjurySuicide;
    @EJB
    NonFatalDomesticViolenceFacade nonFatalDomesticViolenceFacade;
    @EJB
    NonFatalInterpersonalFacade nonFatalInterpersonalFacade;
    @EJB
    NonFatalSelfInflictedFacade nonFatalSelfInflictedFacade;
    @EJB
    NonFatalTransportFacade nonFatalTransportFacade;
    @EJB
    AgeTypesFacade ageTypesFacade;
    @EJB
    MunicipalitiesFacade municipalitiesFacade;
    @EJB
    DepartamentsFacade departamentsFacade;
    @EJB
    VictimsFacade victimsFacade;
    @EJB
    FatalInjurySuicideFacade fatalInjurySuicideFacade;
    @EJB
    InjuriesFacade injuriesFacade;
    @EJB
    FatalInjuriesFacade fatalInjuriesFacade;
    //private RowDataTable selectedRowDataTable;
    private RowDataTable[] selectedRowsDataTable;
    private int currentSearchCriteria = 0;
    private String currentSearchValue = "";
    private String name = "";
    private String newName = "";
    private boolean btnEditDisabled = true;
    private boolean btnRemoveDisabled = true;
    private String data = "-";
    private SuicideMB suicideMB;
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
    private String sql = "";
    private String sqlTags = "";

    public void onCompleteLoad() {
        //progress = 0;
        System.out.println("Termino generacion de XLSX");
    }
    

    public RecordSetsSuicideMB() {
        tagsList = new ArrayList<Tags>();
        table_model = new LazyRecordSetsDataModel(0, "", FormsEnum.SCC_F_030);
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
        suicideMB = (SuicideMB) context.getApplication().evaluateExpressionGet(context, "#{suicideMB}", SuicideMB.class);
        suicideMB.loadValues(tagsList, currentFatalInjurySuicide);
        openForm = "suicide";
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
            table_model = new LazyRecordSetsDataModel(Integer.parseInt(totalRecords), sql, FormsEnum.SCC_F_030);

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
                rowsDataTableArrayList.add(connection.loadFatalInjurySuicideRecord(resultSet.getString(1)));
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

            int rowPosition = 0;
            HSSFWorkbook book = (HSSFWorkbook) document;
            HSSFSheet sheet = book.getSheetAt(0);// Se toma hoja del libro
            HSSFRow row;
            HSSFCellStyle cellStyle = book.createCellStyle();
            HSSFFont font = book.createFont();
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            cellStyle.setFont(font);

            row = sheet.createRow(rowPosition);// Se crea una fila dentro de la hoja
            
            createCell(cellStyle, row, 0, "CODIGO INTERNO");//"100">#{rowX.column1}</p:column>
            createCell(cellStyle, row, 1, "CODIGO");//"100">#{rowX.column23}</p:column>
            createCell(cellStyle, row, 2, "DIA HECHO");//100">#{rowX.column37}</p:column>
            createCell(cellStyle, row, 3, "MES HECHO");//100">#{rowX.column37}</p:column>
            createCell(cellStyle, row, 4, "AÑO HECHO");//100">#{rowX.column37}</p:column>
            createCell(cellStyle, row, 5, "FECHA HECHO");//"100">#{rowX.column13}</p:column>
            createCell(cellStyle, row, 6, "DIA EN SEMANA");//"100">#{rowX.column20}</p:column>
            createCell(cellStyle, row, 7, "HORA HECHO");//"100">#{rowX.column14}</p:column>
            createCell(cellStyle, row, 8, "DIRECCION HECHO");//"400">#{rowX.column15}</p:column>
            createCell(cellStyle, row, 9, "BARRIO HECHO");//"250">#{rowX.column16}</p:column>
            createCell(cellStyle, row, 10, "COMUNA BARRIO HECHO");//"250">#{rowX.column16}</p:column>        
            createCell(cellStyle, row, 11, "AREA HECHO");//"100">#{rowX.column24}</p:column>
            createCell(cellStyle, row, 12, "CLASE DE LUGAR");//"250">#{rowX.column17}</p:column>
            createCell(cellStyle, row, 13, "NUMERO VICTIMAS EN HECHO");//"100">#{rowX.column18}</p:column>
            createCell(cellStyle, row, 14, "NOMBRES Y APELLIDOS");//"400">#{rowX.column4}</p:column>
            createCell(cellStyle, row, 15, "SEXO");//"100">#{rowX.column8}</p:column>
            createCell(cellStyle, row, 16, "TIPO EDAD");//"100">#{rowX.column6}</p:column>
            createCell(cellStyle, row, 17, "EDAD");//"100">#{rowX.column7}</p:column>
            createCell(cellStyle, row, 18, "OCUPACION");//"100">#{rowX.column9}</p:column>
            createCell(cellStyle, row, 19, "TIPO IDENTIFICACION");//"200">#{rowX.column2}</p:column>
            createCell(cellStyle, row, 20, "IDENTIFICACION");//"100">#{rowX.column3}</p:column>
            createCell(cellStyle, row, 21, "EXTRANJERO");//"100">#{rowX.column5}</p:column>
            createCell(cellStyle, row, 22, "DEPARTAMENTO RESIDENCIA");//"100">#{rowX.column12}</p:column>
            createCell(cellStyle, row, 23, "MUNICIPIO RESIDENCIA");//"100">#{rowX.column11}</p:column>
            createCell(cellStyle, row, 24, "BARRIO RESIDENCIA");//"250">#{rowX.column10}</p:column>
            createCell(cellStyle, row, 25, "COMUNA BARRIO RESIDENCIA");//"250">#{rowX.column10}</p:column>
            createCell(cellStyle, row, 26, "PAIS PROCEDENCIA");//"100">#{rowX.column25}</p:column>
            createCell(cellStyle, row, 27, "DEPARTAMENTO PROCEDENCIA");//"100">#{rowX.column26}</p:column>
            createCell(cellStyle, row, 28, "MUNICIPIO PROCEDENCIA");//"100">#{rowX.column27}</p:column>        
            createCell(cellStyle, row, 29, "ARMA O CAUSA DE MUERTE");//"200">#{rowX.column31}</p:column>         
            createCell(cellStyle, row, 30, "EVENTOS RELACIONADOS");//"200">#{rowX.column30}</p:column>      
            createCell(cellStyle, row, 31, "INTENTO PREVIO SUICIDIO");//"250">#{rowX.column28}</p:column>        
            createCell(cellStyle, row, 32, "ANTECEDENTES DE SALUD MENTAL");//"200">#{rowX.column29}</p:column>        
            createCell(cellStyle, row, 33, "NARRACION DEL HECHO");//"700">#{rowX.column19}</p:column>
            createCell(cellStyle, row, 34, "NIVEL DE ALCOHOL");//"100">#{rowX.column21}</p:column>
            createCell(cellStyle, row, 35, "TIPO NIVEL DE ALCOHOL");//"100">#{rowX.column22}</p:column>

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
                        createCell(row, 2, splitDate[0]);//"DIA HECHO");
                        createCell(row, 3, splitDate[1]);//"MES HECHO");
                        createCell(row, 4, splitDate[2]);//"AÑO HECHO");
                    }
                }
                createCell(row, 5, rowDataTableList.getColumn13());//"FECHA HECHO");//"100">#{rowX.column13}</p:column>
                createCell(row, 6, rowDataTableList.getColumn20());//"DIA EN SEMANA");//"100">#{rowX.column20}</p:column>
                createCell(row, 7, rowDataTableList.getColumn14());//"HORA HECHO");//"100">#{rowX.column14}</p:column>
                createCell(row, 8, rowDataTableList.getColumn15());//"DIRECCION HECHO");//"400">#{rowX.column15}</p:column>
                createCell(row, 9, rowDataTableList.getColumn16());//"BARRIO HECHO");//"250">#{rowX.column16}</p:column>
                createCell(row, 10, rowDataTableList.getColumn33());//"COMUNA BARRIO HECHO");//"250">#{rowX.column16}</p:column>
                createCell(row, 11, rowDataTableList.getColumn24());//"AREA HECHO");//"100">#{rowX.column24}</p:column>
                createCell(row, 12, rowDataTableList.getColumn17());//"CLASE DE LUGAR");//"250">#{rowX.column17}</p:column>
                createCell(row, 13, rowDataTableList.getColumn18());//"NUMERO VICTIMAS EN HECHO");//"100">#{rowX.column18}</p:column>
                createCell(row, 14, rowDataTableList.getColumn4());//"NOMBRES Y APELLIDOS");//"400">#{rowX.column4}</p:column>
                createCell(row, 15, rowDataTableList.getColumn8());//"SEXO");//"100">#{rowX.column8}</p:column>
                createCell(row, 16, rowDataTableList.getColumn6());//"TIPO EDAD");//"100">#{rowX.column6}</p:column>
                createCell(row, 17, rowDataTableList.getColumn7());//"EDAD");//"100">#{rowX.column7}</p:column>
                createCell(row, 18, rowDataTableList.getColumn9());//"OCUPACION");//"100">#{rowX.column9}</p:column>
                createCell(row, 19, rowDataTableList.getColumn2());//"TIPO IDENTIFICACION");//"200">#{rowX.column2}</p:column>
                createCell(row, 20, rowDataTableList.getColumn3());//"IDENTIFICACION");//"100">#{rowX.column3}</p:column>
                createCell(row, 21, rowDataTableList.getColumn5());//"EXTRANJERO");//"100">#{rowX.column5}</p:column>
                createCell(row, 22, rowDataTableList.getColumn12());//"DEPARTAMENTO RESIDENCIA");//"100">#{rowX.column12}</p:column>
                createCell(row, 23, rowDataTableList.getColumn11());//"MUNICIPIO RESIDENCIA");//"100">#{rowX.column11}</p:column>
                createCell(row, 24, rowDataTableList.getColumn10());//"BARRIO RESIDENCIA");//"250">#{rowX.column10}</p:column>
                createCell(row, 25, rowDataTableList.getColumn32());//"COMUNA BARRIO RESIDENCIA");//"250">#{rowX.column10}</p:column>
                createCell(row, 26, rowDataTableList.getColumn25());//"PAIS PROCEDENCIA");//"100">#{rowX.column25}</p:column>
                createCell(row, 27, rowDataTableList.getColumn26());//"DEPARTAMENTO PROCEDENCIA");//"100">#{rowX.column26}</p:column>
                createCell(row, 28, rowDataTableList.getColumn27());//"MUNICIPIO PROCEDENCIA");//"100">#{rowX.column27}</p:column>        
                createCell(row, 29, rowDataTableList.getColumn31());//"ARMA O CAUSA DE MUERTE");//"200">#{rowX.column31}</p:column>         
                createCell(row, 30, rowDataTableList.getColumn30());//"EVENTOS RELACIONADOS");//"200">#{rowX.column30}</p:column>      
                createCell(row, 31, rowDataTableList.getColumn28());//"INTENTO PREVIO SUICIDIO");//"250">#{rowX.column28}</p:column>        
                createCell(row, 32, rowDataTableList.getColumn29());//"ANTECEDENTES DE SALUD MENTAL");//"200">#{rowX.column29}</p:column>        
                createCell(row, 33, rowDataTableList.getColumn19());//"NARRACION DEL HECHO");//"700">#{rowX.column19}</p:column>
                createCell(row, 34, rowDataTableList.getColumn21());//"NIVEL DE ALCOHOL");//"100">#{rowX.column21}</p:column>
                createCell(row, 35, rowDataTableList.getColumn22());//"TIPO NIVEL DE ALCOHOL");//"100">#{rowX.column22}</p:column>
            }
        } catch (Exception ex) {
            Logger.getLogger(RecordSetsHomicideMB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void load() {
        currentFatalInjurySuicide = null;
        btnEditDisabled = true;
        btnRemoveDisabled = true;
        if (selectedRowsDataTable != null) {
            if (selectedRowsDataTable.length == 1) {
                currentFatalInjurySuicide = fatalInjurySuicideFacade.find(Integer.parseInt(selectedRowsDataTable[0].getColumn1()));
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
            List<FatalInjurySuicide> fatalInjurySuicideList = new ArrayList<FatalInjurySuicide>();
            for (int j = 0; j < selectedRowsDataTable.length; j++) {
                fatalInjurySuicideList.add(fatalInjurySuicideFacade.find(Integer.parseInt(selectedRowsDataTable[j].getColumn1())));
            }
            if (fatalInjurySuicideList != null) {
                for (int j = 0; j < fatalInjurySuicideList.size(); j++) {
                    FatalInjuries auxFatalInjuries = fatalInjurySuicideList.get(j).getFatalInjuries();
                    Victims auxVictims = fatalInjurySuicideList.get(j).getFatalInjuries().getVictimId();
                    fatalInjurySuicideFacade.remove(fatalInjurySuicideList.get(j));
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

//    public List<RowDataTable> getRowDataTableList() {
//        return rowDataTableList;
//    }
//
//    public void setRowDataTableList(List<RowDataTable> rowDataTableList) {
//        this.rowDataTableList = rowDataTableList;
//    }
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
