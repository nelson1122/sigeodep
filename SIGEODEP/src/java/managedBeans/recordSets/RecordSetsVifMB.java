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
import managedBeans.forms.VIFMB;
import model.dao.*;
import model.pojo.NonFatalDomesticViolence;
import model.pojo.NonFatalInjuries;
import model.pojo.Tags;
import model.pojo.Victims;
import org.apache.poi.hssf.usermodel.*;
import org.primefaces.model.LazyDataModel;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "recordSetsVifMB")
@SessionScoped
public class RecordSetsVifMB implements Serializable {

    //--------------------
    @EJB
    TagsFacade tagsFacade;    
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
    NonFatalDomesticViolenceFacade nonFatalDomesticViolenceFacade;
    @EJB
    NonFatalInjuriesFacade nonFatalInjuriesFacade;
    @EJB
    InjuriesFacade injuriesFacade;
    private List<Tags> tagsList;
    private Tags currentTag;
    private NonFatalDomesticViolence currentNonFatalDomesticViolence;
    private RowDataTable[] selectedRowsDataTable;
    private int currentSearchCriteria = 0;
    private String currentSearchValue = "";
    private String name = "";
    private String newName = "";
    private boolean btnEditDisabled = true;
    private boolean btnRemoveDisabled = true;
    private String data = "-";
    private VIFMB vifMB;
    private String openForm = "";
    private LazyDataModel<RowDataTable> table_model;
    private ArrayList<RowDataTable> rowsDataTableArrayList;
    private ConnectionJdbcMB connection;
    private String sqlTags = "";
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

    public RecordSetsVifMB() {
        tagsList = new ArrayList<Tags>();
        table_model = new LazyRecordSetsDataModel(0, "", FormsEnum.SCC_F_033);
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
        vifMB = (VIFMB) context.getApplication().evaluateExpressionGet(context, "#{vifMB}", VIFMB.class);
        vifMB.loadValues(tagsList, currentNonFatalDomesticViolence);
        openForm = "VIF";
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
            sql = sql + " public.non_fatal_injuries";
            sql = sql + " WHERE ";
            sql = sql + " non_fatal_injuries.victim_id = victims.victim_id AND";
            for (int i = 0; i < tagsList.size(); i++) {
                sql = sql + " tag_id = " + String.valueOf(tagsList.get(i).getTagId()) + " AND ";
            }
            sql = sql + " non_fatal_injuries.injury_date >= to_date('" + initialDateStr + "','dd/MM/yyyy') AND";
            sql = sql + " non_fatal_injuries.injury_date <= to_date('" + endDateStr + "','dd/MM/yyyy') ";
            ResultSet resultSet = connection.consult(sql);
            totalRecords = "0";
            if (resultSet.next()) {
                totalRecords = String.valueOf(resultSet.getInt(1));
            }
            System.out.println("Total de registros = " + totalRecords);
            //DETERMINO EL ID DE CADA REGISTRO            
            sql = "";
            sql = sql + " SELECT ";
            sql = sql + " non_fatal_injuries.victim_id";
            sql = sql + " FROM ";
            sql = sql + " public.victims, ";
            sql = sql + " public.non_fatal_injuries";
            sql = sql + " WHERE ";
            sql = sql + " non_fatal_injuries.victim_id = victims.victim_id AND";
            for (int i = 0; i < tagsList.size(); i++) {
                sql = sql + " victims.tag_id = " + String.valueOf(tagsList.get(i).getTagId()) + " AND ";
            }
            sql = sql + " non_fatal_injuries.injury_date >= to_date('" + initialDateStr + "','dd/MM/yyyy') AND";
            sql = sql + " non_fatal_injuries.injury_date <= to_date('" + endDateStr + "','dd/MM/yyyy') ";

            //CONSTRUYO EL TABLE_MODEL
            table_model = new LazyRecordSetsDataModel(Integer.parseInt(totalRecords), sql, FormsEnum.SCC_F_033);

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
                rowsDataTableArrayList.add(connection.loadNonFatalDomesticViolenceRecord(resultSet.getString(1)));
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
            
            createCell(cellStyle, row, 0, "CODIGO INTERNO");
            createCell(cellStyle, row, 1, "INSTITUCION RECEPTORA");
            createCell(cellStyle, row, 2, "NOMBRES Y APELLIDOS");
            createCell(cellStyle, row, 3, "TIPO IDENTIFICACION");
            createCell(cellStyle, row, 4, "IDENTIFICACION");
            createCell(cellStyle, row, 5, "TIPO EDAD");
            createCell(cellStyle, row, 6, "EDAD CANT");              
            createCell(cellStyle, row, 7, "GENERO");//1           
            createCell(cellStyle, row, 8, "OCUPACION");
            createCell(cellStyle, row, 9, "ASEGURADORA");
            createCell(cellStyle, row, 10, "EXTRANJERO");
            createCell(cellStyle, row, 11, "DEPARTAMENTO RESIDENCIA");
            createCell(cellStyle, row, 12, "MUNICIPIO RESIDENCIA");//1
            createCell(cellStyle, row, 13, "BARRIO RESIDENCIA");//250"
            createCell(cellStyle, row, 14, "COMUNA BARRIO RESIDENCIA");

            createCell(cellStyle, row, 15, "DIRECCION RESIDENCIA");
            createCell(cellStyle, row, 16, "TELEFONO");//100">#{row
            createCell(cellStyle, row, 17, "BARRIO EVENTO");//250">
            createCell(cellStyle, row, 18, "COMUNA BARRIO EVENTO");

            createCell(cellStyle, row, 19, "DIRECCION EVENTO");//40
            //------------------------------------------------------------
            //SE CARGAN VARIABLES LESION DE CAUSA EXTERNA NO FATAL
            //------------------------------------------------------------        
            createCell(cellStyle, row, 20, "DIA EVENTO");//100">
            createCell(cellStyle, row, 21, "MES EVENTO");//100">
            createCell(cellStyle, row, 22, "AÑO EVENTO");//100">
            createCell(cellStyle, row, 23, "FECHA EVENTO");//100
            createCell(cellStyle, row, 24, "HORA EVENTO");//100"
            createCell(cellStyle, row, 25, "DIA SEMANA EVENTO");
            createCell(cellStyle, row, 26, "DIA CONSULTA");//100
            createCell(cellStyle, row, 27, "MES CONSULTA");//100
            createCell(cellStyle, row, 28, "AÑO CONSULTA");//100
            createCell(cellStyle, row, 29, "FECHA CONSULTA");//1
            createCell(cellStyle, row, 30, "HORA CONSULTA");//10
            createCell(cellStyle, row, 31, "REMITIDO");//100">#{
            createCell(cellStyle, row, 32, "REMITIDO DE DONDE");

            createCell(cellStyle, row, 33, "LUGAR DEL HECHO");//
            createCell(cellStyle, row, 34, "OTRO LUGAR DEL HECHO");
            createCell(cellStyle, row, 35, "ACTIVIDAD");//250">#{row
            createCell(cellStyle, row, 36, "OTRA ACTIVIDAD");//250">
            createCell(cellStyle, row, 37, "MECANISMO / OBJETO DE LA LESION");
            createCell(cellStyle, row, 38, "CUAL ALTURA");//100">#{rowX.column
            createCell(cellStyle, row, 39, "CUAL POLVORA");//100">#{rowX.colum        
            createCell(cellStyle, row, 40, "CUAL OTRO MECANISMO / OBJETO");//1                        
            createCell(cellStyle, row, 41, "USO DE ALCOHOL");//150">#{rowX.col
            createCell(cellStyle, row, 42, "USO DE DROGAS");//150">#{rowX.colu

            createCell(cellStyle, row, 43, "GRADO (QUEMADOS)");//100">#{rowX.c
            createCell(cellStyle, row, 44, "PORCENTAJE(QUEMADOS)");//100">#{ro


            createCell(cellStyle, row, 45, "GRUPO ETNICO");//100">#{
            createCell(cellStyle, row, 46, "OTRO GRUPO ETNICO");//10
            createCell(cellStyle, row, 47, "GRUPO VULNERABLE");//100
            createCell(cellStyle, row, 48, "OTRO GRUPO VULNERABLE");

            //tipo de agresor
            createCell(cellStyle, row, 49, "AG1 PADRE(VIF)");
            createCell(cellStyle, row, 50, "AG2 MADRE(VIF)");
            createCell(cellStyle, row, 51, "AG3 PADRASTRO(VIF)");
            createCell(cellStyle, row, 52, "AG4 MADRASTRA(VIF)");
            createCell(cellStyle, row, 53, "AG5 CONYUGE(VIF)");
            createCell(cellStyle, row, 54, "AG6 HERMANO(VIF)");
            createCell(cellStyle, row, 55, "AG7 HIJO(VIF)");
            createCell(cellStyle, row, 56, "AG8 OTRO(VIF)");
            createCell(cellStyle, row, 57, "CUAL OTRO AGRESOR(VIF)");
            createCell(cellStyle, row, 58, "AG9 SIN DATO(VIF)");
            createCell(cellStyle, row, 59, "AG10 NOVIO(VIF)");

            //tipo de maltrato
            createCell(cellStyle, row, 60, "MA1 FISICO(VIF)");
            createCell(cellStyle, row, 61, "MA2 PSICOLOGICO(VIF)");
            createCell(cellStyle, row, 62, "MA3 VIOLENCIA SEXUAL(VIF)");
            createCell(cellStyle, row, 63, "MA4 NEGLIGENCIA(VIF)");
            createCell(cellStyle, row, 64, "MA5 ABANDONO(VIF)");
            createCell(cellStyle, row, 65, "MA6 INSTITUCIONAL(VIF)");
            createCell(cellStyle, row, 66, "MA SIN DATO(VIF)");
            createCell(cellStyle, row, 67, "MA8 OTRO(VIF)");
            createCell(cellStyle, row, 68, "CUAL OTRO TIPO MALTRATO(VIF)");

            //acciones a realizar
            createCell(cellStyle, row, 69, "AR1 CONCILIACION");
            createCell(cellStyle, row, 70, "AR2 CAUCION");
            createCell(cellStyle, row, 71, "AR3 DICTAMEN MEDICINA LEGAL");
            createCell(cellStyle, row, 72, "AR4 REMISION FISCALIA");
            createCell(cellStyle, row, 73, "AR5 REMISION MEDICINA LEGAL");
            createCell(cellStyle, row, 74, "AR6 REMISION COM FAMILIA");
            createCell(cellStyle, row, 75, "AR7 REMISION ICBF");
            createCell(cellStyle, row, 76, "AR8 MEDIDAS PROTECCION");//100
            createCell(cellStyle, row, 77, "AR9 REMISION A SALUD");//100">
            createCell(cellStyle, row, 78, "AR10 ATENCION PSICOSOCIAL");//
            createCell(cellStyle, row, 79, "AR11 RESTABLECIMIENTO DERECHOS");
            createCell(cellStyle, row, 80, "AR12 OTRA?");//
            createCell(cellStyle, row, 81, "AR CUAL OTRA");
            createCell(cellStyle, row, 82, "AR13 SIN DATO");

            String[] splitDate;
            for (int i = 0; i < rowsDataTableArrayList.size(); i++) {

                RowDataTable rowDataTableList = rowsDataTableArrayList.get(i);
                rowPosition++;
                row = sheet.createRow(rowPosition);

                createCell(row, 0, rowDataTableList.getColumn1());//"CODIGO INTERNO");//50">#{rowX.column1}</p:column>
                createCell(row, 1, rowDataTableList.getColumn80());//"INSTITUCION RECEPTORA");//50">#{rowX.column80}</p:column>
                createCell(row, 2, rowDataTableList.getColumn4());//"NOMBRES Y APELLIDOS");//400">#{rowX.column4}</p:column>                                                
                createCell(row, 3, rowDataTableList.getColumn2());//"TIPO IDENTIFICACION");//200">#{rowX.column2}</p:column>
                createCell(row, 4, rowDataTableList.getColumn3());//"IDENTIFICACION");//100">#{rowX.column3}</p:column>                
                createCell(row, 5, rowDataTableList.getColumn6());//"TIP EDAD");//100">#{rowX.column6}</p:column>                
                createCell(row, 6, rowDataTableList.getColumn7());//"EDAD CANT");//100">#{rowX.column7}</p:column>                
                createCell(row, 7, rowDataTableList.getColumn8());//"GENERO");//100">#{rowX.column8}</p:column>                
                createCell(row, 8, rowDataTableList.getColumn9());//"OCUPACION");//100">#{rowX.column9}</p:column>
                createCell(row, 9, rowDataTableList.getColumn18());//"ASEGURADORA");//300">#{rowX.column18}</p:column>
                createCell(row, 10, rowDataTableList.getColumn5());//"EXTRANJERO");//100">#{rowX.column5}</p:column>  
                createCell(row, 11, rowDataTableList.getColumn13());//"DEPARTAMENTO RESIDENCIA");//100">#{rowX.column13}</p:column>
                createCell(row, 12, rowDataTableList.getColumn12());//"MUNICIPIO RESIDENCIA");//100">#{rowX.column12}</p:column>
                createCell(row, 13, rowDataTableList.getColumn15());//"BARRIO RESIDENCIA");//250">#{rowX.column15}</p:column>
                createCell(row, 14, rowDataTableList.getColumn81());//"COMUNA BARRIO RESIDENCIA");//250">#{rowX.column15}</p:column>

                createCell(row, 15, rowDataTableList.getColumn14());//"DIRECCION RESIDENCIA");//400">#{rowX.column14}</p:column>
                createCell(row, 16, rowDataTableList.getColumn11());//"TELEFONO");//100">#{rowX.column11}</p:column>

                createCell(row, 17, rowDataTableList.getColumn36());//"BARRIO EVENTO");//250">#{rowX.column36}</p:column>
                createCell(row, 18, rowDataTableList.getColumn82());//"COMUNA BARRIO EVENTO");//250">#{rowX.column36}</p:column>
                createCell(row, 19, rowDataTableList.getColumn35());//"DIRECCION EVENTO");//400">#{rowX.column35}</p:column>


                //------------------------------------------------------------
                //SE CARGAN VARIABLES LESION DE CAUSA EXTERNA NO FATAL
                //------------------------------------------------------------        
                if (rowDataTableList.getColumn33() != null) {
                    splitDate = rowDataTableList.getColumn33().split("/");
                    if (splitDate.length == 3) {
                        createCell(row, 20, splitDate[0]);//"DIA EVENTO");
                        createCell(row, 21, splitDate[1]);//"MES EVENTO");
                        createCell(row, 22, splitDate[2]);//"AÑO EVENTO");
                    }
                }
                createCell(row, 23, rowDataTableList.getColumn33());//"FECHA EVENTO");//100">#{rowX.column33}</p:column>
                createCell(row, 24, rowDataTableList.getColumn34());//"HORA EVENTO");//100">#{rowX.column48}</p:column>
                createCell(row, 25, rowDataTableList.getColumn48());//"DIA SEMANA EVENTO");//100">#{rowX.column57}</p:column>
                if (rowDataTableList.getColumn31() != null) {
                    splitDate = rowDataTableList.getColumn31().split("/");
                    if (splitDate.length == 3) {
                        createCell(row, 26, splitDate[0]);//"DIA CONSULTA");
                        createCell(row, 27, splitDate[1]);//"MES CONSULTA");
                        createCell(row, 28, splitDate[2]);//"AÑO CONSULTA");
                    }
                }
                createCell(row, 29, rowDataTableList.getColumn31());//"FECHA CONSULTA");//100">#{rowX.column31}</p:column>
                createCell(row, 30, rowDataTableList.getColumn32());//"HORA CONSULTA");//100">#{rowX.column32}</p:column>
                createCell(row, 31, rowDataTableList.getColumn43());//"REMITIDO");//100">#{rowX.column43}</p:column>
                createCell(row, 32, rowDataTableList.getColumn44());//"REMITIDO DE DONDE");//250">#{rowX.column44}</p:column>

                createCell(row, 33, rowDataTableList.getColumn37());//"LUGAR DEL HECHO");//200">#{rowX.column37}</p:column>
                createCell(row, 34, rowDataTableList.getColumn19());//"OTRO LUGAR DEL HECHO");//200">#{rowX.column19}</p:column>
                createCell(row, 35, rowDataTableList.getColumn38());//"ACTIVIDAD");//250">#{rowX.column38}</p:column>
                createCell(row, 36, rowDataTableList.getColumn20());//"OTRA ACTIVIDAD");//250">#{rowX.column20}</p:column>
                createCell(row, 37, rowDataTableList.getColumn46());//"MECANISMO / OBJETO DE LA LESION");//200">#{rowX.column46}</p:column>
                createCell(row, 38, rowDataTableList.getColumn21());//"CUAL ALTURA");//100">#{rowX.column21}</p:column>
                createCell(row, 39, rowDataTableList.getColumn22());//"CUAL POLVORA");//100">#{rowX.column22}</p:column>                                
                createCell(row, 40, rowDataTableList.getColumn24());//"CUAL OTRO MECANISMO / OBJETO");//100">#{rowX.column24}</p:column>                                
                createCell(row, 41, rowDataTableList.getColumn39());//"USO DE ALCOHOL");//150">#{rowX.column39}</p:column>
                createCell(row, 42, rowDataTableList.getColumn40());//"USO DE DROGAS");//150">#{rowX.column40}</p:column>

                createCell(row, 43, rowDataTableList.getColumn41());//"GRADO (QUEMADOS)");//100">#{rowX.column41}</p:column>
                createCell(row, 44, rowDataTableList.getColumn42());//"PORCENTAJE(QUEMADOS)");//100">#{rowX.column42}</p:column>

                createCell(row, 45, rowDataTableList.getColumn10());//"GRUPO ETNICO");//100">#{rowX.column10}</p:column>
                createCell(row, 46, rowDataTableList.getColumn26());//"OTRO GRUPO ETNICO");//100">#{rowX.column26}</p:column>
                createCell(row, 47, rowDataTableList.getColumn16());//"GRUPO VULNERABLE");//100">#{rowX.column16}</p:column>
                createCell(row, 48, rowDataTableList.getColumn27());//"OTRO GRUPO VULNERABLE");//100">#{rowX.column27}</p:column>

                //tipo de agresor
                createCell(row, 49, rowDataTableList.getColumn49());//"AG1 PADRE(VIF)");//100">#{rowX.column49}</p:column>
                createCell(row, 50, rowDataTableList.getColumn50());//"AG2 MADRE(VIF)");//100">#{rowX.column50}</p:column>
                createCell(row, 51, rowDataTableList.getColumn51());//"AG3 PADRASTRO(VIF)");//100">#{rowX.column51}</p:column>
                createCell(row, 52, rowDataTableList.getColumn52());//"AG4 MADRASTRA(VIF)");//100">#{rowX.column52}</p:column>
                createCell(row, 53, rowDataTableList.getColumn53());//"AG5 CONYUGE(VIF)");//100">#{rowX.column53}</p:column>
                createCell(row, 54, rowDataTableList.getColumn54());//"AG6 HERMANO(VIF)");//100">#{rowX.column54}</p:column>
                createCell(row, 55, rowDataTableList.getColumn55());//"AG7 HIJO(VIF)");//100">#{rowX.column55}</p:column>
                createCell(row, 56, rowDataTableList.getColumn56());//"AG8 OTRO(VIF)");//100">#{rowX.column56}</p:column>
                createCell(row, 57, rowDataTableList.getColumn28());//"CUAL OTRO AGRESOR(VIF)");//100">#{rowX.column28}</p:column>
                createCell(row, 58, rowDataTableList.getColumn57());//"AG9 SIN DATO(VIF)");//100">#{rowX.column57}</p:column>
                createCell(row, 59, rowDataTableList.getColumn58());//"AG10 NOVIO(VIF)");//100">#{rowX.column58}</p:column>                                

                //tipo de maltrato
                createCell(row, 60, rowDataTableList.getColumn59());//"MA1 FISICO(VIF)");//100">#{rowX.column59}</p:column>
                createCell(row, 61, rowDataTableList.getColumn60());//"MA2 PSICOLOGICO(VIF)");//100">#{rowX.column60}</p:column>
                createCell(row, 62, rowDataTableList.getColumn61());//"MA3 VIOLENCIA SEXUAL(VIF)");//100">#{rowX.column61}</p:column>
                createCell(row, 63, rowDataTableList.getColumn62());//"MA4 NEGLIGENCIA(VIF)");//100">#{rowX.column62}</p:column>
                createCell(row, 64, rowDataTableList.getColumn62());//"MA5 ABANDONO(VIF)");//100">#{rowX.column63}</p:column>
                createCell(row, 65, rowDataTableList.getColumn64());//"MA6 INSTITUCIONAL(VIF)");//100">#{rowX.column64}</p:column>
                createCell(row, 66, rowDataTableList.getColumn65());//"MA SIN DATO(VIF)");//100">#{rowX.column65}</p:column>
                createCell(row, 67, rowDataTableList.getColumn66());//"MA8 OTRO(VIF)");//100">#{rowX.column66}</p:column>
                createCell(row, 68, rowDataTableList.getColumn29());//"CUAL OTRO TIPO MALTRATO(VIF)");//100">#{rowX.column29}</p:column>

                //acciones a realizar
                createCell(row, 69, rowDataTableList.getColumn67());//"AR1 CONCILIACION");//100">#{rowX.column67}</p:column>
                createCell(row, 70, rowDataTableList.getColumn68());//"AR2 CAUCION");//100">#{rowX.column68}</p:column>
                createCell(row, 71, rowDataTableList.getColumn69());//"AR3 DICTAMEN MEDICINA LEGAL");//100">#{rowX.column69}</p:column>
                createCell(row, 72, rowDataTableList.getColumn70());//"AR4 REMISION FISCALIA");//100">#{rowX.column70}</p:column>
                createCell(row, 73, rowDataTableList.getColumn71());//"AR5 REMISION MEDICINA LEGAL");//100">#{rowX.column71}</p:column>
                createCell(row, 74, rowDataTableList.getColumn72());//"AR6 REMISION COM FAMILIA");//100">#{rowX.column72}</p:column>
                createCell(row, 75, rowDataTableList.getColumn73());//"AR7 REMISION ICBF");//100">#{rowX.column73}</p:column>
                createCell(row, 76, rowDataTableList.getColumn74());//"AR8 MEDIDAS PROTECCION");//100">#{rowX.column74}</p:column>
                createCell(row, 77, rowDataTableList.getColumn75());//"AR9 REMISION A SALUD");//100">#{rowX.column75}</p:column>
                createCell(row, 78, rowDataTableList.getColumn76());//"AR10 ATENCION PSICOSOCIAL");//100">#{rowX.column76}</p:column>
                createCell(row, 79, rowDataTableList.getColumn77());//"AR11 RESTABLECIMIENTO DERECHOS");//100">#{rowX.column77}</p:column>
                createCell(row, 80, rowDataTableList.getColumn78());//"AR12 OTRA?");//100">#{rowX.column78}</p:column>
                createCell(row, 81, rowDataTableList.getColumn30());//"AR CUAL OTRA");//100">#{rowX.column30}</p:column>
                createCell(row, 82, rowDataTableList.getColumn79());//"AR13 SIN DATO");//100">#{rowX.column79}</p:column>
            }
        } catch (Exception ex) {
            Logger.getLogger(RecordSetsHomicideMB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void load() {
        currentNonFatalDomesticViolence = null;
        btnEditDisabled = true;
        btnRemoveDisabled = true;
        if (selectedRowsDataTable != null) {

            if (selectedRowsDataTable.length == 1) {
                currentNonFatalDomesticViolence = nonFatalDomesticViolenceFacade.find(Integer.parseInt(selectedRowsDataTable[0].getColumn1()));
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
            List<NonFatalDomesticViolence> nonFatalDomesticViolenceList = new ArrayList<NonFatalDomesticViolence>();
            for (int j = 0; j < selectedRowsDataTable.length; j++) {
                nonFatalDomesticViolenceList.add(nonFatalDomesticViolenceFacade.find(Integer.parseInt(selectedRowsDataTable[j].getColumn1())));
            }
            if (nonFatalDomesticViolenceList != null) {
                for (int j = 0; j < nonFatalDomesticViolenceList.size(); j++) {
                    NonFatalInjuries auxNonFatalInjuries = nonFatalDomesticViolenceList.get(j).getNonFatalInjuries();
                    Victims auxVictims = nonFatalDomesticViolenceList.get(j).getNonFatalInjuries().getVictimId();
                    nonFatalDomesticViolenceFacade.remove(nonFatalDomesticViolenceList.get(j));
                    nonFatalInjuriesFacade.remove(auxNonFatalInjuries);
                    victimsFacade.remove(auxVictims);
                }
            }
//            //quito los elementos seleccionados de rowsDataTableList seleccion de 
//            for (int j = 0; j < selectedRowsDataTable.length; j++) {
//                for (int i = 0; i < rowDataTableList.size(); i++) {
//                    if (selectedRowsDataTable[j].getColumn1().compareTo(rowDataTableList.get(i).getColumn1()) == 0) {
//                        rowDataTableList.remove(i);
//                        break;
//                    }
//                }
//            }
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
    /*
     public void exportarExcel() {
        if (movimientos == null) {// Cargamos el ArrayList si esta nulo
            cargaTablaDinamica();
        }
        HSSFWorkbook workbook = new HSSFWorkbook();//Creamos el libro excel
 
        //Creamos la hoja de Excel llamada "Movimientos"
        HSSFSheet sheet = workbook.createSheet("Movimientos");
 
        //* Creamos la primera fila para colocar los titulos correspondientes a 
         * cada columna
 
        HSSFRow header = sheet.createRow(0);
        HSSFRow fila = null;
 
        for (int i = 0; i < movimientos.size(); i++) {
            //* Creamos las filas segun la cantidad de datos que contiene 
             * el arraylist
             
            fila = sheet.createRow(i + 1);
 
            /* Creamos las celdas, se sabe que son 5
            for (int j = 0; j < 5; j++) {
 
                fila.createCell(j);
 
            }
            //* Asignamos los titulos a la primera fila, 
             * en cada celda correspondiente 
 
            header.createCell(0).setCellValue(new HSSFRichTextString("Fecha"));
            header.createCell(1).setCellValue(new HSSFRichTextString("Descripcion"));
            header.createCell(2).setCellValue(new HSSFRichTextString("Serie"));
            header.createCell(3).setCellValue(new HSSFRichTextString("Monto"));
            header.createCell(4).setCellValue(new HSSFRichTextString("Saldo"));
 
            //* Seteamos los valores a cada celda correspondiente 
            fila.getCell(0).setCellValue("" + movimientos.get(i).getFecha().toLocaleString());
            fila.getCell(1).setCellValue(new HSSFRichTextString(movimientos.get(i).getDescripcion()));
            fila.getCell(2).setCellValue(new HSSFRichTextString(movimientos.get(i).getSerie()));
            fila.getCell(3).setCellValue(Integer.parseInt(String.valueOf(movimientos.get(i).getMonto()).replace(".0", "")));
            fila.getCell(4).setCellValue(Integer.parseInt(String.valueOf(movimientos.get(i).getSaldo()).replace(".0", "")));
 
            //*Modificamos el tamaño de las celdas segun el contenido de las celdas
 
            sheet.autoSizeColumn((short) (i));
 
        }
 
        sheet.autoSizeColumn((short) (0));//Arreglamos el tamaño al header
 
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
 
            // Le asignamos el tipo de fichero que abrirá
            response.setContentType("application/vnd.ms-excel");
            // El nombre que recibira el archivo a descargar 
            response.setHeader("Content-disposition", "attachment; filename=movimientos.xls");
 
            ServletOutputStream out = response.getOutputStream();
            //Escribimos el fichero al out 
            workbook.write(out);
            out.close(); // Cerramos el streaming
 
        } catch (Exception e) {
            e.printStackTrace();
 
 
        }
 
 
 
    }
     */
}

