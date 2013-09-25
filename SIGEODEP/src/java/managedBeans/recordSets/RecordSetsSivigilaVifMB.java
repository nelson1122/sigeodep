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
import model.dao.*;
import model.pojo.NonFatalDomesticViolence;
import model.pojo.NonFatalInjuries;
import model.pojo.SivigilaAggresor;
import model.pojo.SivigilaEvent;
import model.pojo.SivigilaVictim;
import model.pojo.Tags;
import model.pojo.Victims;
import org.apache.poi.hssf.usermodel.*;
import org.primefaces.model.LazyDataModel;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "recordSetsSivigilaVifMB")
@SessionScoped
public class RecordSetsSivigilaVifMB implements Serializable {
    //--------------------
    @EJB
    TagsFacade tagsFacade;
    @EJB
    VictimsFacade victimsFacade;
    @EJB
    NonFatalDomesticViolenceFacade nonFatalDomesticViolenceFacade;
    @EJB
    NonFatalInjuriesFacade nonFatalInjuriesFacade;
    @EJB
    SivigilaEventFacade sivigilaEventFacade;
    @EJB
    SivigilaVictimFacade sivigilaVictimFacade;
    @EJB
    SivigilaAggresorFacade sivigilaAggresorFacade;
    private List<Tags> tagsList;
    private RowDataTable[] selectedRowsDataTable;
    private int currentSearchCriteria = 0;
    private String currentSearchValue = "";
    private String name = "";
    private String newName = "";    
    private String data = "-";
    private String exportFileName = "";
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

    public void onCompleteLoad() {
        //progress = 0;
        System.out.println("Termino generacion de XLSX");
    }

    public RecordSetsSivigilaVifMB() {
        tagsList = new ArrayList<>();
        table_model = new LazyRecordSetsDataModel(0, "", FormsEnum.SCC_F_033);
        connection = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
    }

    public void printMessage(FacesMessage.Severity s, String title, String messageStr) {
        FacesMessage msg = new FacesMessage(s, title, messageStr);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

//    public String openForm() {
//        return openForm;
//    }
    void loadValues(RowDataTable[] selectedRowsDataTableTags) {
        try {
            //CREO LA LISTA DE TAGS SELECCIONADOS        
            exportFileName = "SIVIGILA VIF - " + initialDateStr + " - " + endDateStr;
            tagsList = new ArrayList<>();
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
            sql = "\n";
            sql = sql + " SELECT \n";
            sql = sql + " count(*) \n";
            sql = sql + " FROM \n";
            sql = sql + " public.victims, \n";
            sql = sql + " public.non_fatal_injuries \n";
            sql = sql + " WHERE \n";
            sql = sql + " non_fatal_injuries.victim_id = victims.victim_id AND ( \n";
            for (int i = 0; i < tagsList.size(); i++) {
                if (i == tagsList.size() - 1) {
                    sql = sql + " victims.tag_id = " + String.valueOf(tagsList.get(i).getTagId()) + " \n";
                } else {
                    sql = sql + " victims.tag_id = " + String.valueOf(tagsList.get(i).getTagId()) + " OR \n";
                }
            }
            sql = sql + ") AND non_fatal_injuries.injury_date >= to_date('" + initialDateStr + "','dd/MM/yyyy') AND \n";
            sql = sql + " non_fatal_injuries.injury_date <= to_date('" + endDateStr + "','dd/MM/yyyy') \n";
            ResultSet resultSet = connection.consult(sql);
            totalRecords = "0";
            if (resultSet.next()) {
                totalRecords = String.valueOf(resultSet.getInt(1));
            }
            //System.out.println("Total de registros = " + totalRecords);
            //DETERMINO EL ID DE CADA REGISTRO            
            sql = "\n";
            sql = sql + " SELECT \n";
            sql = sql + " non_fatal_injuries.victim_id \n";
            sql = sql + " FROM \n";
            sql = sql + " public.victims, \n";
            sql = sql + " public.non_fatal_injuries \n";
            sql = sql + " WHERE \n";
            sql = sql + " non_fatal_injuries.victim_id = victims.victim_id AND ( \n";
            for (int i = 0; i < tagsList.size(); i++) {
                if (i == tagsList.size() - 1) {
                    sql = sql + " victims.tag_id = " + String.valueOf(tagsList.get(i).getTagId()) + " \n";
                } else {
                    sql = sql + " victims.tag_id = " + String.valueOf(tagsList.get(i).getTagId()) + " OR \n";
                }
            }
            sql = sql + ") AND non_fatal_injuries.injury_date >= to_date('" + initialDateStr + "','dd/MM/yyyy') AND \n";
            sql = sql + " non_fatal_injuries.injury_date <= to_date('" + endDateStr + "','dd/MM/yyyy') \n";

            //CONSTRUYO EL TABLE_MODEL
            table_model = new LazyRecordSetsDataModel(Integer.parseInt(totalRecords), sql, FormsEnum.SIVIGILA_VIF);

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
            rowsDataTableArrayList = new ArrayList<>();
            ResultSet resultSet = connection.consult(sql);
            while (resultSet.next()) {
                rowsDataTableArrayList.add(connection.loadSivigilaVifRecord(resultSet.getString(1)));
                tuplesProcessed++;
                progress = (int) (tuplesProcessed * 100) / tuplesNumber;
                System.out.println(progress);
            }
            progress = 100;
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

            createCell(cellStyle, row, 0, "CODIGO INTERNO");
            createCell(cellStyle, row, 1, "INSTITUCION RECEPTORA");
            createCell(cellStyle, row, 2, "NOMBRES Y APELLIDOS");
            createCell(cellStyle, row, 3, "TIPO IDENTIFICACION");
            createCell(cellStyle, row, 4, "IDENTIFICACION");
            createCell(cellStyle, row, 5, "TIPO EDAD");
            createCell(cellStyle, row, 6, "EDAD CANTIDAD");
            createCell(cellStyle, row, 7, "GENERO");
            createCell(cellStyle, row, 8, "OCUPACION");
            createCell(cellStyle, row, 9, "DIRECCION RESIDENCIA");
            createCell(cellStyle, row, 10, "ASEGURADORA");
            createCell(cellStyle, row, 11, "PERTENENCIA ETNICA");
            createCell(cellStyle, row, 12, "GRUPO POBLACIONAL");
            createCell(cellStyle, row, 13, "MUNICIPIO RESIDENCIA");
            createCell(cellStyle, row, 14, "DEPARTAMENTO RESIDENCIA");
            createCell(cellStyle, row, 15, "TELEFONO");
            createCell(cellStyle, row, 16, "FECHA NACIMIENTO");
            createCell(cellStyle, row, 17, "ESCOLARIDAD VICTIMA");
            createCell(cellStyle, row, 18, "FACTOR DE VULNERABILIDAD");
            createCell(cellStyle, row, 19, "OTRO FACTOR VULNERABILIDAD");
            createCell(cellStyle, row, 20, "ANTECEDENTES HECHO SIMILAR");
            createCell(cellStyle, row, 21, "PRESENCIA ALCOHOL VICTIMA");
            createCell(cellStyle, row, 22, "TIPO DE REGIMEN");
            //------------------------------------------------------------
            //SE CARGAN VARIABLES LESION DE CAUSA EXTERNA NO FATAL
            //------------------------------------------------------------      
            createCell(cellStyle, row, 23, "BARRIO EVENTO");
            createCell(cellStyle, row, 24, "COMUNA BARRIO EVENTO");
            createCell(cellStyle, row, 25, "DIRECCION EVENTO");
            createCell(cellStyle, row, 26, "AREA");
            createCell(cellStyle, row, 27, "ZONA DE CONFLICTO");
            createCell(cellStyle, row, 28, "FECHA EVENTO");
            createCell(cellStyle, row, 29, "HORA EVENTO");
            createCell(cellStyle, row, 30, "FECHA CONSULTA");
            createCell(cellStyle, row, 31, "HORA CONSULTA");
            createCell(cellStyle, row, 32, "ESCENARIO");
            //------------------------------------------------------------
            //DATOS AGRESOR
            //------------------------------------------------------------   
            createCell(cellStyle, row, 33, "EDAD AGRESOR");
            createCell(cellStyle, row, 34, "GENERO AGRESOR");
            createCell(cellStyle, row, 35, "OCUPACION AGRESOR");
            createCell(cellStyle, row, 36, "ESCOLARIDAD AGRESOR");
            createCell(cellStyle, row, 37, "RELACION FAMILIAR VICTIMA");
            createCell(cellStyle, row, 38, "OTRA RELACION FAMILIAR");
            createCell(cellStyle, row, 39, "CONVIVE CON AGRESOR");
            createCell(cellStyle, row, 40, "RELACION NO FAMILIAR VICTIMA");
            createCell(cellStyle, row, 41, "OTRA RELACION NO FAMILIAR");
            createCell(cellStyle, row, 42, "GRUPO AGRESOR");
            createCell(cellStyle, row, 43, "OTRO GRUPO AGRESOR");
            createCell(cellStyle, row, 44, "PRESENCIA ALCOHOL AGRESOR");
            createCell(cellStyle, row, 45, "ARMAS UTILIZADAS");
            createCell(cellStyle, row, 46, "SUSTANCIA INTOXICACION");
            createCell(cellStyle, row, 47, "OTRA ARMA");
            createCell(cellStyle, row, 48, "OTRO MECANISMO");
            createCell(cellStyle, row, 49, "NATURALEZA VIOLENCIA");
            createCell(cellStyle, row, 50, "ASP1 ATENCION PSICOSOCIAL");
            createCell(cellStyle, row, 51, "ASP2 PROFILAXIS ITS");
            createCell(cellStyle, row, 52, "ASP3 ANTICONCEPCION DE EMERGENCIA");
            createCell(cellStyle, row, 53, "ASP4 ORIENTACION IVE");
            createCell(cellStyle, row, 54, "ASP5 ATENCION EN SALUD MENTAL ESPECIALIZADA");
            createCell(cellStyle, row, 55, "ASP6 INFORME A LA AUTORIDAD");
            createCell(cellStyle, row, 56, "ASP7 OTRO");
            createCell(cellStyle, row, 57, "RECOMINEDA PROTECCION");
            createCell(cellStyle, row, 58, "TRABAJO DE CAMPO");
            createCell(cellStyle, row, 59, "PROFESIONAL SALUD");


            String[] splitDate;
            for (int i = 0; i < rowsDataTableArrayList.size(); i++) {

                RowDataTable rowDataTableList = rowsDataTableArrayList.get(i);
                rowPosition++;
                row = sheet.createRow(rowPosition);

                createCell(row, 0, rowDataTableList.getColumn1());//CODIGO INTERNO" width="50">#{rowX.column1}</p:column>
                createCell(row, 1, rowDataTableList.getColumn42());//INSTITUCION DE SALUD" width="200">#{rowX.column42}</p:column>
                createCell(row, 2, rowDataTableList.getColumn2());//NOMBRES Y APELLIDOS" width="400">#{rowX.column2}</p:column>                                                
                createCell(row, 3, rowDataTableList.getColumn3());//TIPO IDENTIFICACION" width="200">#{rowX.column3}</p:column>
                createCell(row, 4, rowDataTableList.getColumn4());//IDENTIFICACION" width="100">#{rowX.column4}</p:column>                
                createCell(row, 5, rowDataTableList.getColumn5());//TIPO EDAD" width="100">#{rowX.column5}</p:column>                
                createCell(row, 6, rowDataTableList.getColumn6());//EDAD CANTIDAD" width="100">#{rowX.column6}</p:column>                
                createCell(row, 7, rowDataTableList.getColumn7());//GENERO" width="100">#{rowX.column7}</p:column>                
                createCell(row, 8, rowDataTableList.getColumn8());//OCUPACION" width="300">#{rowX.column8}</p:column>
                createCell(row, 9, rowDataTableList.getColumn9());//DIRECCION RESIDENCIA" width="400">#{rowX.column9}</p:column>                                
                createCell(row, 10, rowDataTableList.getColumn10());//ASEGURADORA" width="300">#{rowX.column10}</p:column>                                
                createCell(row, 11, rowDataTableList.getColumn11());//PERTENENCIA ETNICA" width="100">#{rowX.column11}</p:column>  
                createCell(row, 12, rowDataTableList.getColumn12());//GRUPO POBLACIONAL" width="400">#{rowX.column12}</p:column>                                
                createCell(row, 13, rowDataTableList.getColumn13());//MUNICIPIO RESIDENCIA" width="100">#{rowX.column13}</p:column>
                createCell(row, 14, rowDataTableList.getColumn14());//DEPARTAMENTO RESIDENCIA" width="100">#{rowX.column14}</p:column>                                
                createCell(row, 15, rowDataTableList.getColumn15());//TELEFONO" width="100">#{rowX.column15}</p:column>
                createCell(row, 16, rowDataTableList.getColumn16());//FECHA NACIMIENTO" width="100">#{rowX.column16}</p:column>                                         
                createCell(row, 17, rowDataTableList.getColumn18());//ESCOLARIDAD VICTIMA" width="400">#{rowX.column18}</p:column>
                createCell(row, 18, rowDataTableList.getColumn19());//FACTOR DE VULNERABILIDAD" width="400">#{rowX.column19}</p:column>
                createCell(row, 19, rowDataTableList.getColumn20());//OTRO FACTOR VULNERABILIDAD" width="400">#{rowX.column20}</p:column>
                createCell(row, 20, rowDataTableList.getColumn21());//ANTECEDENTES HECHO SIMILAR" width="400">#{rowX.column21}</p:column>
                createCell(row, 21, rowDataTableList.getColumn41());//PRESENCIA ALCOHOL VICTIMA" width="400">#{rowX.column41}</p:column>
                createCell(row, 22, rowDataTableList.getColumn17());//TIPO DE REGIMEN" width="400">#{rowX.column17}</p:column>
                //------------------------------------------------------------
                //SE CARGAN VARIABLES LESION DE CAUSA EXTERNA NO FATAL
                //------------------------------------------------------------        
                createCell(row, 23, rowDataTableList.getColumn22());//BARRIO EVENTO" width="250">#{rowX.column22}</p:column>
                createCell(row, 24, rowDataTableList.getColumn23());//COMUNA BARRIO EVENTO" width="100">#{rowX.column23}</p:column>
                createCell(row, 25, rowDataTableList.getColumn49());//DIRECCION EVENTO" width="400">#{rowX.column40}</p:column>
                createCell(row, 26, rowDataTableList.getColumn43());//AREA" width="400">#{rowX.column43}</p:column>
                createCell(row, 27, rowDataTableList.getColumn60());//ZONA DE CONFLICTO" width="400">#{rowX.column60}</p:column>
                createCell(row, 28, rowDataTableList.getColumn26());//FECHA EVENTO" width="100">#{rowX.column26}</p:column>
                createCell(row, 29, rowDataTableList.getColumn27());//HORA EVENTO" width="100">#{rowX.column27}</p:column>                                
                createCell(row, 30, rowDataTableList.getColumn24());//FECHA CONSULTA" width="100">#{rowX.column24}</p:column>
                createCell(row, 31, rowDataTableList.getColumn25());//HORA CONSULTA" width="100">#{rowX.column25}</p:column>                                
                createCell(row, 32, rowDataTableList.getColumn39());//ESCENARIO" width="200">#{rowX.column39}</p:column>                                                                 
                //------------------------------------------------------------
                //DATOS AGRESOR
                //------------------------------------------------------------        
                createCell(row, 33, rowDataTableList.getColumn44());//EDAD AGRESOR" width="400">#{rowX.column44}</p:column>
                createCell(row, 34, rowDataTableList.getColumn45());//GENERO AGRESOR" width="400">#{rowX.column45}</p:column>
                createCell(row, 35, rowDataTableList.getColumn46());//OCUPACION AGRESOR" width="400">#{rowX.column46}</p:column>
                createCell(row, 36, rowDataTableList.getColumn47());//ESCOLARIDAD AGRESOR" width="400">#{rowX.column47}</p:column>
                createCell(row, 37, rowDataTableList.getColumn48());//RELACION FAMILIAR VICTIMA" width="400">#{rowX.column48}</p:column>
                createCell(row, 38, rowDataTableList.getColumn49());//OTRA RELACION FAMILIAR" width="400">#{rowX.column49}</p:column>
                createCell(row, 39, rowDataTableList.getColumn50());//CONVIVE CON AGRESOR" width="400">#{rowX.column50}</p:column>
                createCell(row, 40, rowDataTableList.getColumn51());//RELACION NO FAMILIAR VICTIMA" width="400">#{rowX.column51}</p:column>
                createCell(row, 41, rowDataTableList.getColumn52());//OTRA RELACION NO FAMILIAR" width="400">#{rowX.column52}</p:column>
                createCell(row, 42, rowDataTableList.getColumn53());//GRUPO AGRESOR" width="400">#{rowX.column53}</p:column>
                createCell(row, 43, rowDataTableList.getColumn54());//OTRO GRUPO AGRESOR" width="400">#{rowX.column54}</p:column>
                createCell(row, 44, rowDataTableList.getColumn55());//PRESENCIA ALCOHOL AGRESOR" width="400">#{rowX.column55}</p:column>
                createCell(row, 45, rowDataTableList.getColumn56());//ARMAS UTILIZADAS" width="400">#{rowX.column56}</p:column>
                createCell(row, 46, rowDataTableList.getColumn57());//SUSTANCIA INTOXICACION" width="400">#{rowX.column57}</p:column>
                createCell(row, 47, rowDataTableList.getColumn58());//OTRA ARMA" width="400">#{rowX.column58}</p:column>
                createCell(row, 48, rowDataTableList.getColumn59());//OTRO MECANISMO" width="400">#{rowX.column59}</p:column>
                createCell(row, 49, rowDataTableList.getColumn29());//NATURALEZA VIOLENCIA" width="100">#{rowX.column29}</p:column>
                createCell(row, 50, rowDataTableList.getColumn62());//ASP1 ATENCION PSICOSOCIAL" width="100">#{rowX.column62}</p:column>
                createCell(row, 51, rowDataTableList.getColumn63());//ASP2 PROFILAXIS ITS" width="100">#{rowX.column63}</p:column>
                createCell(row, 52, rowDataTableList.getColumn64());//ASP3 ANTICONCEPCION DE EMERGENCIA" width="100">#{rowX.column64}</p:column>
                createCell(row, 53, rowDataTableList.getColumn65());//ASP4 ORIENTACION IVE" width="100">#{rowX.column65}</p:column>
                createCell(row, 54, rowDataTableList.getColumn66());//ASP5 ATENCION EN SALUD MENTAL ESPECIALIZADA" width="100">#{rowX.column66}</p:column>
                createCell(row, 55, rowDataTableList.getColumn67());//ASP6 INFORME A LA AUTORIDAD" width="100">#{rowX.column67}</p:column>
                createCell(row, 56, rowDataTableList.getColumn68());//ASP7 OTRO" width="100">#{rowX.column68}</p:column>                                                
                createCell(row, 57, rowDataTableList.getColumn69());//RECOMINEDA PROTECCION" width="100">#{rowX.column69}</p:column>
                createCell(row, 58, rowDataTableList.getColumn70());//TRABAJO DE CAMPO" width="100">#{rowX.column70}</p:column>                                
                createCell(row, 59, rowDataTableList.getColumn28());//PROFESIONAL SALUD" width="100">#{rowX.column28}</p:column>                                
            }
        } catch (Exception ex) {
            Logger.getLogger(RecordSetsHomicideMB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void load() {
        //currentNonFatalDomesticViolence = null;
        //btnEditDisabled = true;
//        btnRemoveDisabled = true;
        if (selectedRowsDataTable != null) {

//            if (selectedRowsDataTable.length == 1) {
//                currentNonFatalDomesticViolence = nonFatalDomesticViolenceFacade.find(Integer.parseInt(selectedRowsDataTable[0].getColumn1()));
//            }
            if (selectedRowsDataTable.length > 1) {
                //btnEditDisabled = true;
//                btnRemoveDisabled = false;
            } else {
                //btnEditDisabled = false;
//                btnRemoveDisabled = false;
            }
        }
    }

    public void deleteRegistry() {
        if (selectedRowsDataTable != null && selectedRowsDataTable.length != 0) {
            for (int j = 0; j < selectedRowsDataTable.length; j++) {
                NonFatalInjuries auxNonFatalInjury = nonFatalInjuriesFacade.find(Integer.parseInt(selectedRowsDataTable[j].getColumn1()));
                Victims auxVictim = auxNonFatalInjury.getVictimId();
                NonFatalDomesticViolence auxDomesticViolence = auxNonFatalInjury.getNonFatalDomesticViolence();
                SivigilaEvent auxSivigilaEvent = auxDomesticViolence.getSivigilaEvent();
                SivigilaVictim auxSivigilaVictim = auxSivigilaEvent.getSivigilaVictimId();
                SivigilaAggresor auxSivigilaAggresor = auxSivigilaEvent.getSivigilaAgresorId();
                sivigilaEventFacade.remove(auxSivigilaEvent);
                //nnFatalDomesticViolenceFacade.remove(auxDomesticViolence);
                nonFatalDomesticViolenceFacade.remove(auxDomesticViolence);
                nonFatalInjuriesFacade.remove(auxNonFatalInjury);
                victimsFacade.remove(auxVictim);
                sivigilaVictimFacade.remove(auxSivigilaVictim);
                sivigilaAggresorFacade.remove(auxSivigilaAggresor);
            }//deselecciono los controles
            selectedRowsDataTable = null;
            totalRecords=String.valueOf(Integer.parseInt(totalRecords)-1);
            printMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se ha realizado la eliminacion de los registros seleccionados");
        } else {
            printMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar un o varios registros a eliminar");
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

//    public boolean isBtnEditDisabled() {
//        return btnEditDisabled;
//    }
//    public void setBtnEditDisabled(boolean btnEditDisabled) {
//        this.btnEditDisabled = btnEditDisabled;
//    }
//    public boolean isBtnRemoveDisabled() {
//        return btnRemoveDisabled;
//    }
//
//    public void setBtnRemoveDisabled(boolean btnRemoveDisabled) {
//        this.btnRemoveDisabled = btnRemoveDisabled;
//    }

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

    public String getExportFileName() {
        return exportFileName;
    }

    public void setExportFileName(String exportFileName) {
        this.exportFileName = exportFileName;
    }
}
