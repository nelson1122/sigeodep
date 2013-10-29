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
import managedBeans.forms.LcenfMB;
import model.dao.*;
import model.pojo.NonFatalInjuries;
import model.pojo.Tags;
import org.apache.poi.hssf.usermodel.*;
import org.primefaces.model.LazyDataModel;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "recordSetsLcenfMB")
@SessionScoped
public class RecordSetsLcenfMB implements Serializable {

    //--------------------
    @EJB
    TagsFacade tagsFacade;
    @EJB
    NonFatalDomesticViolenceFacade nonFatalDomesticViolenceFacade;
    @EJB
    NonFatalInterpersonalFacade nonFatalInterpersonalFacade;
    @EJB
    NonFatalSelfInflictedFacade nonFatalSelfInflictedFacade;
    @EJB
    NonFatalTransportFacade nonFatalTransportFacade;
    @EJB
    VictimsFacade victimsFacade;
    @EJB
    NonFatalInjuriesFacade nonFatalInjuriesFacade;
    private List<Tags> tagsList;
    private NonFatalInjuries currentNonFatalInjury;
    private LazyDataModel<RowDataTable> table_model;
    private ArrayList<RowDataTable> rowsDataTableArrayList;
    private RowDataTable[] selectedRowsDataTable;
    private int currentSearchCriteria = 0;
    private String currentSearchValue = "";
    private String name = "";
    private String newName = "";
    private boolean btnEditDisabled = true;
    private String data = "-";
    private LcenfMB lcenfMB;
    private String openForm = "";
    private ConnectionJdbcMB connection;
    private String totalRecords = "0";
    private String initialDateStr = "";
    private String endDateStr = "";
    private int tuplesNumber;
    private Integer tuplesProcessed;
    private int progress = 0;//PROGRESO AL CREAR XLS
    private String sql = "";
    private String exportFileName = "";

//    public void onCompleteLoad() {
//        //progress = 0;
//        System.out.println("Termino generacion de XLSX");
//    }

    public RecordSetsLcenfMB() {
        tagsList = new ArrayList<>();
        table_model = new LazyRecordSetsDataModel(0, "", FormsEnum.SCC_F_032);
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
        lcenfMB = (LcenfMB) context.getApplication().evaluateExpressionGet(context, "#{lcenfMB}", LcenfMB.class);
        lcenfMB.loadValues(tagsList, currentNonFatalInjury);
        openForm = "LCENF";
    }

//    void loadValues(RowDataTable[] selectedRowsDataTableTags) {
//        try {
//            //CREO LA LISTA DE TAGS SELECCIONADOS   
//            exportFileName = "LCENF - " + initialDateStr + " - " + endDateStr;
//
//            //DETERMINO TOTAL DE REGISTROS
////            sql =  " "//mecanismo es transito
////                    + " SELECT \n"
////                    + " count(*) \n"
////                    + " FROM \n"
////                    + " public.victims, \n"
////                    + " public.non_fatal_injuries \n"
////                    + " WHERE \n"
////                    + " non_fatal_injuries.victim_id = victims.victim_id AND  \n"
////                    + " non_fatal_injuries.injury_id = 54 AND "
////                    + " non_fatal_injuries.mechanism_id = 1 ";
//            
////            sql =  " "
////                    + " SELECT \n"
////                    + " count(*) \n"
////                    + " FROM \n"
////                    + " public.victims, \n"
////                    + " public.non_fatal_injuries \n"
////                    + " WHERE \n"
////                    + " non_fatal_injuries.victim_id = victims.victim_id AND  \n"
////                    + " non_fatal_injuries.injury_id = 54 AND "
////                    + " non_fatal_injuries.intentionality_id = 2 ";//auntoinflingida
//            
//            sql =  " "
//                    + " SELECT \n"
//                    + " count(*) \n"
//                    + " FROM \n"
//                    + " public.victims, \n"
//                    + " public.non_fatal_injuries \n"
//                    + " WHERE \n"
//                    + " non_fatal_injuries.victim_id = victims.victim_id AND  \n"
//                    + " non_fatal_injuries.injury_id = 54 AND "
//                    + " non_fatal_injuries.intentionality_id = 3 ";//violencia agresion o sospecha
//
//            ResultSet resultSet = connection.consult(sql);
//            totalRecords = "0";
//            if (resultSet.next()) {
//                totalRecords = String.valueOf(resultSet.getInt(1));
//            }
//            //System.out.println("Total de registros = " + totalRecords);
//            //DETERMINO EL ID DE CADA REGISTRO                        
////            sql =  " "
////                    + " SELECT \n"
////                    + " non_fatal_injuries.victim_id \n"
////                    + " FROM \n"
////                    + " public.victims, \n"
////                    + " public.non_fatal_injuries \n"
////                    + " WHERE \n"
////                    + " non_fatal_injuries.victim_id = victims.victim_id AND  \n"
////                    + " non_fatal_injuries.injury_id = 54 AND "
////                    + " non_fatal_injuries.mechanism_id = 1 ";    
//            
////            sql =  " "
////                    + " SELECT \n"
////                    + " non_fatal_injuries.victim_id \n"
////                    + " FROM \n"
////                    + " public.victims, \n"
////                    + " public.non_fatal_injuries \n"
////                    + " WHERE \n"
////                    + " non_fatal_injuries.victim_id = victims.victim_id AND  \n"
////                    + " non_fatal_injuries.injury_id = 54 AND "
////                    + " non_fatal_injuries.intentionality_id = 2 ";//auntoinflingida
//            
//            sql =  " "
//                    + " SELECT \n"
//                    + " non_fatal_injuries.victim_id \n"
//                    + " FROM \n"
//                    + " public.victims, \n"
//                    + " public.non_fatal_injuries \n"
//                    + " WHERE \n"
//                    + " non_fatal_injuries.victim_id = victims.victim_id AND  \n"
//                    + " non_fatal_injuries.injury_id = 54 AND "
//                    + " non_fatal_injuries.intentionality_id = 3 ";//violencia agresion o sospecha
//
//            //CONSTRUYO EL TABLE_MODEL
//            table_model = new LazyRecordSetsDataModel(Integer.parseInt(totalRecords), sql, FormsEnum.SCC_F_032);
//        } catch (SQLException ex) {
//            Logger.getLogger(RecordSetsLcenfMB.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

    void loadValues(RowDataTable[] selectedRowsDataTableTags) {
        try {
            //CREO LA LISTA DE TAGS SELECCIONADOS   
            exportFileName = "LCENF - " + initialDateStr + " - " + endDateStr;
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
            sql = sql + " ) AND non_fatal_injuries.injury_date >= to_date('" + initialDateStr + "','dd/MM/yyyy') AND \n";
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
            sql = sql + " ) AND non_fatal_injuries.injury_date >= to_date('" + initialDateStr + "','dd/MM/yyyy') AND \n";
            sql = sql + " non_fatal_injuries.injury_date <= to_date('" + endDateStr + "','dd/MM/yyyy') \n";

            //CONSTRUYO EL TABLE_MODEL
            table_model = new LazyRecordSetsDataModel(Integer.parseInt(totalRecords), sql, FormsEnum.SCC_F_032);

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
                rowsDataTableArrayList.add(connection.loadNonFatalInjuryRecord(resultSet.getString(1)));
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


            createCell(cellStyle, row, 0, "CODIGO");//width="50">#{rowX.column1}</p:column>
            createCell(cellStyle, row, 1, "INSTITUCION DE SALUD");//250">#{rowX.column58}</p:column>
            createCell(cellStyle, row, 2, "NOMBRES Y APELLIDOS");//400">#{rowX.column4}</p:column>
            createCell(cellStyle, row, 3, "TIPO IDENTIFICACION");//200">#{rowX.column2}</p:column>
            createCell(cellStyle, row, 4, "IDENTIFICACION");//100">#{rowX.column3}</p:column>                
            createCell(cellStyle, row, 5, "TIPO EDAD");//100">#{rowX.column6}</p:column>                
            createCell(cellStyle, row, 6, "EDAD");//100">#{rowX.column7}</p:column>
            createCell(cellStyle, row, 7, "GENERO");//100">#{rowX.column8}</p:column>                
            createCell(cellStyle, row, 8, "OCUPACION");//100">#{rowX.column9}</p:column>
            createCell(cellStyle, row, 9, "ASEGURADORA");//300">#{rowX.column18}</p:column>
            createCell(cellStyle, row, 10, "DESPLAZADO");//100">#{rowX.column16}</p:column>
            createCell(cellStyle, row, 11, "DISCAPACITADO");//100">#{rowX.column17}</p:column>
            createCell(cellStyle, row, 12, "GRUPO ETNICO");//100">#{rowX.column10}</p:column>
            createCell(cellStyle, row, 13, "OTRO GRUPO ETNICO");//100">#{rowX.column19}</p:column>
            createCell(cellStyle, row, 14, "EXTRANJERO");//100">#{rowX.column5}</p:column>        
            createCell(cellStyle, row, 15, "DEPARTAMENTO RESIDENCIA");//100">#{rowX.column13}</p:column>
            createCell(cellStyle, row, 16, "MUNICIPIO RESIDENCIA");//100">#{rowX.column12}</p:column>
            createCell(cellStyle, row, 17, "BARRIO RESIDENCIA");//250">#{rowX.column15}</p:column>
            createCell(cellStyle, row, 18, "COMUNA BARRIO RESIDENCIA");//250">#{rowX.column15}</p:column>
            createCell(cellStyle, row, 19, "DIRECCION RESIDENCIA");//400">#{rowX.column14}</p:column>
            createCell(cellStyle, row, 20, "TELEFONO");//100">#{rowX.column11}</p:column>

            createCell(cellStyle, row, 21, "BARRIO EVENTO");//250">#{rowX.column42}</p:column>
            createCell(cellStyle, row, 22, "COMUNA BARRIO EVENTO");//250">#{rowX.column15}</p:column>
            createCell(cellStyle, row, 23, "DIRECCION EVENTO");//400">#{rowX.column41}</p:column>

            //------------------------------------------------------------
            //SE CARGAN VARIABLES LESION DE CAUSA EXTERNA NO FATAL
            //------------------------------------------------------------        
            createCell(cellStyle, row, 24, "DIA EVENTO");//100">#{rowX.column37}</p:column>
            createCell(cellStyle, row, 25, "MES EVENTO");//100">#{rowX.column37}</p:column>
            createCell(cellStyle, row, 26, "AÑO EVENTO");//100">#{rowX.column37}</p:column>
            createCell(cellStyle, row, 27, "FECHA EVENTO");//100">#{rowX.column39}</p:column>
            createCell(cellStyle, row, 28, "HORA EVENTO");//100">#{rowX.column40}</p:column>
            createCell(cellStyle, row, 29, "DIA SEMANA EVENTO");//100">#{rowX.column57}</p:column>

            createCell(cellStyle, row, 30, "DIA CONSULTA");//100">#{rowX.column37}</p:column>
            createCell(cellStyle, row, 31, "MES CONSULTA");//100">#{rowX.column37}</p:column>
            createCell(cellStyle, row, 32, "AÑO CONSULTA");//100">#{rowX.column37}</p:column>
            createCell(cellStyle, row, 33, "FECHA CONSULTA");//100">#{rowX.column37}</p:column>
            createCell(cellStyle, row, 34, "HORA CONSULTA");//100">#{rowX.column38}</p:column>
            createCell(cellStyle, row, 35, "REMITIDO");//100">#{rowX.column50}</p:column>
            createCell(cellStyle, row, 36, "REMITIDO DE DONDE");//250">#{rowX.column51}</p:column>
            createCell(cellStyle, row, 37, "INTENCIONALIDAD");//250">#{rowX.column45}</p:column>
            createCell(cellStyle, row, 38, "LUGAR DEL HECHO");//200">#{rowX.column43}</p:column>
            createCell(cellStyle, row, 39, "OTRO LUGAR DEL HECHO");//200">#{rowX.column20}</p:column>
            createCell(cellStyle, row, 40, "ACTIVIDAD");//250">#{rowX.column44}</p:column>
            createCell(cellStyle, row, 41, "OTRA ACTIVIDAD");//250">#{rowX.column21}</p:column>
            createCell(cellStyle, row, 42, "MECANISMO / OBJETO DE LA LESION");//200">#{rowX.column55}</p:column>
            createCell(cellStyle, row, 43, "CUAL ALTURA");//100">#{rowX.column22}</p:column>
            createCell(cellStyle, row, 44, "CUAL POLVORA");//100">#{rowX.column23}</p:column>
            createCell(cellStyle, row, 45, "CUAL DESASTRE NATURAL");//100">#{rowX.column24}</p:column>
            createCell(cellStyle, row, 46, "CUAL OTRO MECANISMO / OBJETO");//100">#{rowX.column25}</p:column>
            createCell(cellStyle, row, 47, "CUAL ANIMAL");//100">#{rowX.column26}</p:column>
            createCell(cellStyle, row, 48, "USO DE ALCOHOL");//150">#{rowX.column46}</p:column>
            createCell(cellStyle, row, 49, "USO DE DROGAS");//150">#{rowX.column47}</p:column>

            createCell(cellStyle, row, 50, "GRADO (QUEMADOS)");//100">#{rowX.column48}</p:column>
            createCell(cellStyle, row, 51, "PORCENTAJE(QUEMADOS)");//100">#{rowX.column49}</p:column>
            //<!-- p:column headerText="injury_id" );//250">{rowX.column59}</p:column -->
            //sitios anatomicos
            createCell(cellStyle, row, 52, "SA1 SISTEMICO");//100">#{rowX.column82}</p:column>
            createCell(cellStyle, row, 53, "SA2 CRANEO");//100">#{rowX.column83}</p:column>
            createCell(cellStyle, row, 54, "SA3 OJOS");//100">#{rowX.column84}</p:column>
            createCell(cellStyle, row, 55, "SA4 MAXILOFACIAL / NARIZ / OIDOS");//100">#{rowX.column85}</p:column>
            createCell(cellStyle, row, 56, "SA5 CUELLO");//100">#{rowX.column86}</p:column>
            createCell(cellStyle, row, 57, "SA6 TORAX");//100">#{rowX.column87}</p:column>
            createCell(cellStyle, row, 58, "SA7 ABDOMEN");//100">#{rowX.column88}</p:column>
            createCell(cellStyle, row, 59, "SA8 COLUMNA");//100">#{rowX.column89}</p:column>
            createCell(cellStyle, row, 60, "SA9 PELVIS / GENITALES");//100">#{rowX.column90}</p:column>
            createCell(cellStyle, row, 61, "SA10 MIEMBROS SUPERIORES");//100">#{rowX.column91}</p:column>
            createCell(cellStyle, row, 62, "SA11 MIEMBROS INFERIORES");//100">#{rowX.column92}</p:column>
            createCell(cellStyle, row, 63, "SA OTRO");//100">#{rowX.column93}</p:column>
            createCell(cellStyle, row, 64, "CUAL OTRO SITIO");//100">#{rowX.column34}</p:column>

            //cargo la naturaleza de la lesion
            createCell(cellStyle, row, 65, "NL1 LACERACION");//100">#{rowX.column94}</p:column>
            createCell(cellStyle, row, 66, "NL2 CORTADA");//100">#{rowX.column95}</p:column>
            createCell(cellStyle, row, 67, "NL3 LESION PROFUNDA");//100">#{rowX.column96}</p:column>
            createCell(cellStyle, row, 68, "NL4 ESGUINCE LUXACION");//100">#{rowX.column97}</p:column>
            createCell(cellStyle, row, 69, "NL5 FRACTURA");//100">#{rowX.column98}</p:column>
            createCell(cellStyle, row, 70, "NL6 QUEMADURA");//100">#{rowX.column99}</p:column>
            createCell(cellStyle, row, 71, "NL7 CONTUSION");//100">#{rowX.column100}</p:column>
            createCell(cellStyle, row, 72, "NL8 ORGANICA SISTEMICA");//100">#{rowX.column101}</p:column>
            createCell(cellStyle, row, 73, "NL9 TRAUMA CRANEOENCEFALICO");//100">#{rowX.column102}</p:column>
            createCell(cellStyle, row, 74, "NL SIN DATO");//100">#{rowX.column104}</p:column>
            createCell(cellStyle, row, 75, "NL OTRO");//100">#{rowX.column103}</p:column>
            createCell(cellStyle, row, 76, "CUAL OTRA NATURALEZA");//150">#{rowX.column35}</p:column>

            createCell(cellStyle, row, 77, "DESTINO DEL PACIENTE");//250">#{rowX.column52}</p:column>
            createCell(cellStyle, row, 78, "CUAL OTRO DESTINO");//100">#{rowX.column36}</p:column>

            //cargo los diagnosticos

            createCell(cellStyle, row, 79, "CIE10_1 CODIGO");//500">#{rowX.column105}</p:column>
            createCell(cellStyle, row, 80, "CIE10_1 DESCRIPCION");//500">#{rowX.column105}</p:column>
            createCell(cellStyle, row, 81, "CIE10_2 CODIGO");//500">#{rowX.column105}</p:column>
            createCell(cellStyle, row, 82, "CIE10_2 DESCRIPCION");//500">#{rowX.column106}</p:column>
            createCell(cellStyle, row, 83, "CIE10_3 CODIGO");//500">#{rowX.column105}</p:column>
            createCell(cellStyle, row, 84, "CIE10_3 DESCRIPCION");//500">#{rowX.column107}</p:column>
            createCell(cellStyle, row, 85, "CIE10_4 CODIGO");//500">#{rowX.column105}</p:column>
            createCell(cellStyle, row, 86, "CIE10_4 DESCRIPCION");//500">#{rowX.column108}</p:column>

            createCell(cellStyle, row, 87, "MEDICO");//300">#{rowX.column54}</p:column>
            createCell(cellStyle, row, 88, "DIGITADOR");//100">#{rowX.column56}</p:column>

            //------------------------------------------------------------
            //AUTOINFLINGIDA INTENCIONAL
            //------------------------------------------------------------
            createCell(cellStyle, row, 89, "INTENTO PREVIO (INTENCIONAL AUTOINFLIGIDA)");//100">#{rowX.column109}</p:column>
            createCell(cellStyle, row, 90, "ANTECEDENTES MENTALES (INTENCIONAL AUTOINFLIGIDA)");//100">#{rowX.column110}</p:column>
            createCell(cellStyle, row, 91, "FACTOR PRECIPITANTE (INTENCIONAL AUTOINFLIGIDA)");//100">#{rowX.column111}</p:column>
            createCell(cellStyle, row, 92, "CUAL OTRO FACTOR (INTENCIONAL AUTOINFLIGIDA)");//100">#{rowX.column27}</p:column>                                

            //------------------------------------------------------------
            //SE CARGA VARIABLE PARA VIOLENCIA INTERPERSONAL
            //-----------------------------------------------------------
            createCell(cellStyle, row, 93, "ANTECEDENTES AGRESION (INTERPERSONAL)");//100">#{rowX.column60}</p:column>
            createCell(cellStyle, row, 94, "RELACION AGRESOR-VICTIMA (INTERPERSONAL)");//150">#{rowX.column61}</p:column>
            createCell(cellStyle, row, 95, "CUAL OTRA RELACION (INTERPERSONAL)");//100">#{rowX.column30}</p:column>
            createCell(cellStyle, row, 96, "CONTEXTO (INTERPERSONAL)");//200">#{rowX.column62}</p:column>
            createCell(cellStyle, row, 97, "SEXO AGRESORES (INTERPERSONAL)");//100">#{rowX.column63}</p:column>

            //------------------------------------------------------------
            //SE CARGA DATOS PARA VIOLENCIA INTRAFAMILIAR
            //------------------------------------------------------------        
            //tipo de agresor
            createCell(cellStyle, row, 98, "AG1 PADRE(VIF)");//100">#{rowX.column64}</p:column>
            createCell(cellStyle, row, 99, "AG2 MADRE(VIF)");//100">#{rowX.column65}</p:column>
            createCell(cellStyle, row, 100, "AG3 PADRASTRO(VIF)");//100">#{rowX.column67}</p:column>
            createCell(cellStyle, row, 101, "AG4 MADRASTRA(VIF)");//100">#{rowX.column67}</p:column>
            createCell(cellStyle, row, 102, "AG5 CONYUGE(VIF)");//100">#{rowX.column68}</p:column>
            createCell(cellStyle, row, 103, "AG6 HERMANO(VIF)");//100">#{rowX.column69}</p:column>
            createCell(cellStyle, row, 104, "AG7 HIJO(VIF)");//100">#{rowX.column70}</p:column>
            createCell(cellStyle, row, 105, "AG8 OTRO(VIF)");//100">#{rowX.column71}</p:column>
            createCell(cellStyle, row, 106, "CUAL OTRO AGRESOR(VIF)");//100">#{rowX.column28}</p:column>
            createCell(cellStyle, row, 107, "AG9 SIN DATO(VIF)");//100">#{rowX.column72}</p:column>
            createCell(cellStyle, row, 108, "AG10 NOVIO(VIF)");//100">#{rowX.column73}</p:column>                                

            //tipo de maltrato
            createCell(cellStyle, row, 109, "MA1 FISICO(VIF)");//100">#{rowX.column74}</p:column>
            createCell(cellStyle, row, 110, "MA2 PSICOLOGICO(VIF)");//100">#{rowX.column75}</p:column>
            createCell(cellStyle, row, 111, "MA3 VIOLENCIA SEXUAL(VIF)");//100">#{rowX.column76}</p:column>
            createCell(cellStyle, row, 112, "MA4 NEGLIGENCIA(VIF)");//100">#{rowX.column77}</p:column>
            createCell(cellStyle, row, 113, "MA5 ABANDONO(VIF)");//100">#{rowX.column78}</p:column>
            createCell(cellStyle, row, 114, "MA6 INSTITUCIONAL(VIF)");//100">#{rowX.column79}</p:column>
            createCell(cellStyle, row, 115, "MA SIN DATO(VIF)");//100">#{rowX.column80}</p:column>
            createCell(cellStyle, row, 116, "MA8 OTRO(VIF)");//100">#{rowX.column81}</p:column>
            createCell(cellStyle, row, 117, "CUAL OTRO TIPO MALTRATO(VIF)");//100">#{rowX.column29}</p:column>


            //------------------------------------------------------------
            //SE CARGA DATOS PARA TRANSITO
            //------------------------------------------------------------
            createCell(cellStyle, row, 118, "TIPO DE TRANSPORTE");//100">#{rowX.column112}</p:column>
            createCell(cellStyle, row, 119, "CUAL OTRO TIPO DE TRANSPORTE");//100">#{rowX.column31}</p:column>                                
            createCell(cellStyle, row, 120, "TIPO TRANSPORTE CONTRAPARTE");//100">#{rowX.column113}</p:column>
            createCell(cellStyle, row, 121, "CUAL OTRO TIPO TRANSPORTE CONTRAPARTE");//100">#{rowX.column32}</p:column>                                
            createCell(cellStyle, row, 122, "TIPO DE TRANSPORTE DEL USUARIO");//100">#{rowX.column114}</p:column>
            createCell(cellStyle, row, 123, "CUAL OTRO TIPO DE TRANSPORTE DEL USUARIO");//100">#{rowX.column33}</p:column>

            createCell(cellStyle, row, 124, "CINTURON");//100">#{rowX.column115}</p:column>
            createCell(cellStyle, row, 125, "CASCO MOTO");//100">#{rowX.column116}</p:column>
            createCell(cellStyle, row, 126, "CASCO BICICLETA");//100">#{rowX.column117}</p:column>
            createCell(cellStyle, row, 127, "CHALECO");//100">#{rowX.column118}</p:column>
            createCell(cellStyle, row, 128, "OTRO ELEMENTO");//100">#{rowX.column119}</p:column>

            String[] splitDate;
            for (int i = 0; i < rowsDataTableArrayList.size(); i++) {

                RowDataTable rowDataTableList = rowsDataTableArrayList.get(i);
                rowPosition++;
                row = sheet.createRow(rowPosition);
                createCell(row, 0, rowDataTableList.getColumn1());//"CODIGO");
                createCell(row, 1, rowDataTableList.getColumn58());//"INSTITUCION DE SALUD");
                createCell(row, 2, rowDataTableList.getColumn4());//"NOMBRES Y APELLIDOS");
                createCell(row, 3, rowDataTableList.getColumn2());//"TIPO IDENTIFICACION");
                createCell(row, 4, rowDataTableList.getColumn3());//"IDENTIFICACION");
                createCell(row, 5, rowDataTableList.getColumn6());//"TIPO EDAD");
                createCell(row, 6, rowDataTableList.getColumn7());//"EDAD");
                createCell(row, 7, rowDataTableList.getColumn8());//"GENERO");
                createCell(row, 8, rowDataTableList.getColumn9());//"OCUPACION");
                createCell(row, 9, rowDataTableList.getColumn18());//"ASEGURADORA");
                createCell(row, 10, rowDataTableList.getColumn16());//"DESPLAZADO");
                createCell(row, 11, rowDataTableList.getColumn17());//"DISCAPACITADO");
                createCell(row, 12, rowDataTableList.getColumn10());//"GRUPO ETNICO");
                createCell(row, 13, rowDataTableList.getColumn19());//"OTRO GRUPO ETNICO");
                createCell(row, 14, rowDataTableList.getColumn5());//"EXTRANJERO");
                createCell(row, 15, rowDataTableList.getColumn13());//"DEPARTAMENTO RESIDENCIA");
                createCell(row, 16, rowDataTableList.getColumn12());//"MUNICIPIO RESIDENCIA");
                createCell(row, 17, rowDataTableList.getColumn15());//"BARRIO RESIDENCIA");
                createCell(row, 18, rowDataTableList.getColumn121());//"COMUNA BARRIO RESIDENCIA");
                createCell(row, 19, rowDataTableList.getColumn14());//"DIRECCION RESIDENCIA");
                createCell(row, 20, rowDataTableList.getColumn11());//"TELEFONO");
                createCell(row, 21, rowDataTableList.getColumn42());//"BARRIO EVENTO");
                createCell(row, 22, rowDataTableList.getColumn120());//"COMUNA BARRIO EVENTO");

                createCell(row, 23, rowDataTableList.getColumn41());//"DIRECCION EVENTO");
                //------------------------------------------------------------
                //SE CARGAN VARIABLES LESION DE CAUSA EXTERNA NO FATAL
                //------------------------------------------------------------        
                if (rowDataTableList.getColumn39() != null) {
                    splitDate = rowDataTableList.getColumn39().split("/");
                    if (splitDate.length == 3) {
                        createCell(row, 24, splitDate[0]);//"DIA EVENTO");
                        createCell(row, 25, splitDate[1]);//"MES EVENTO");
                        createCell(row, 26, splitDate[2]);//"AÑO EVENTO");
                    }
                }
                createCell(row, 27, rowDataTableList.getColumn39());//"FECHA EVENTO");
                createCell(row, 28, rowDataTableList.getColumn40());//"HORA EVENTO");
                createCell(row, 29, rowDataTableList.getColumn57());//"DIA SEMANA EVENTO");

                if (rowDataTableList.getColumn37() != null) {
                    splitDate = rowDataTableList.getColumn37().split("/");
                    if (splitDate.length == 3) {
                        createCell(row, 30, splitDate[0]);//"DIA CONSULTA");
                        createCell(row, 31, splitDate[1]);//"MES CONSULTA");
                        createCell(row, 32, splitDate[2]);//"AÑO CONSULTA");
                    }
                }
                createCell(row, 33, rowDataTableList.getColumn37());//"FECHA CONSULTA");
                createCell(row, 34, rowDataTableList.getColumn38());//"HORA CONSULTA");
                createCell(row, 35, rowDataTableList.getColumn50());//"REMITIDO");
                createCell(row, 36, rowDataTableList.getColumn51());//"REMITIDO DE DONDE");
                createCell(row, 37, rowDataTableList.getColumn45());//"INTENCIONALIDAD");
                createCell(row, 38, rowDataTableList.getColumn43());//"LUGAR DEL HECHO");
                createCell(row, 39, rowDataTableList.getColumn20());//"OTRO LUGAR DEL HECHO");
                createCell(row, 40, rowDataTableList.getColumn44());//"ACTIVIDAD");
                createCell(row, 41, rowDataTableList.getColumn21());//"OTRA ACTIVIDAD");
                createCell(row, 42, rowDataTableList.getColumn55());//"MECANISMO / OBJETO DE LA LESION" 
                createCell(row, 43, rowDataTableList.getColumn22());//"CUAL ALTURA");
                createCell(row, 44, rowDataTableList.getColumn23());//"CUAL POLVORA");
                createCell(row, 45, rowDataTableList.getColumn24());//"CUAL DESASTRE NATURAL");
                createCell(row, 46, rowDataTableList.getColumn25());//"CUAL OTRO MECANISMO / OBJETO");
                createCell(row, 47, rowDataTableList.getColumn26());//"CUAL ANIMAL");
                createCell(row, 48, rowDataTableList.getColumn46());//"USO DE ALCOHOL");
                createCell(row, 49, rowDataTableList.getColumn47());//"USO DE DROGAS");
                createCell(row, 50, rowDataTableList.getColumn48());//"GRADO (QUEMADOS)");
                createCell(row, 51, rowDataTableList.getColumn49());//"PORCENTAJE(QUEMADOS)");
                //sitios anatomicos
                createCell(row, 52, rowDataTableList.getColumn82());//"SA1 SISTEMICO");
                createCell(row, 53, rowDataTableList.getColumn83());//"SA2 CRANEO");
                createCell(row, 54, rowDataTableList.getColumn84());//"SA3 OJOS");
                createCell(row, 55, rowDataTableList.getColumn85());//"SA4 MAXILOFACIAL / NARIZ / OIDOS");
                createCell(row, 56, rowDataTableList.getColumn86());//"SA5 CUELLO");
                createCell(row, 57, rowDataTableList.getColumn87());//"SA6 TORAX");
                createCell(row, 58, rowDataTableList.getColumn88());//"SA7 ABDOMEN");
                createCell(row, 59, rowDataTableList.getColumn89());//"SA8 COLUMNA");
                createCell(row, 60, rowDataTableList.getColumn90());//"SA9 PELVIS / GENITALES");
                createCell(row, 61, rowDataTableList.getColumn91());//"SA10 MIEMBROS SUPERIORES");
                createCell(row, 62, rowDataTableList.getColumn92());//"SA11 MIEMBROS INFERIORES");
                createCell(row, 63, rowDataTableList.getColumn93());//"SA OTRO");
                createCell(row, 64, rowDataTableList.getColumn34());//"CUAL OTRO SITIO");
                //cargo la naturaleza de la lesion
                createCell(row, 65, rowDataTableList.getColumn94());//"NL1 LACERACION");
                createCell(row, 66, rowDataTableList.getColumn95());//"NL2 CORTADA");
                createCell(row, 67, rowDataTableList.getColumn96());//"NL3 LESION PROFUNDA");
                createCell(row, 68, rowDataTableList.getColumn97());//"NL4 ESGUINCE LUXACION");
                createCell(row, 69, rowDataTableList.getColumn98());//"NL5 FRACTURA");
                createCell(row, 70, rowDataTableList.getColumn99());//"NL6 QUEMADURA");
                createCell(row, 71, rowDataTableList.getColumn100());//"NL7 CONTUSION");
                createCell(row, 72, rowDataTableList.getColumn101());//"NL8 ORGANICA SISTEMICA");
                createCell(row, 73, rowDataTableList.getColumn102());//"NL9 TRAUMA CRANEOENCEFALICO");
                createCell(row, 74, rowDataTableList.getColumn104());//"NL SIN DATO");
                createCell(row, 75, rowDataTableList.getColumn103());//"NL OTRO");
                createCell(row, 76, rowDataTableList.getColumn35());//"CUAL OTRA NATURALEZA");
                createCell(row, 77, rowDataTableList.getColumn52());//"DESTINO DEL PACIENTE");
                createCell(row, 78, rowDataTableList.getColumn36());//"CUAL OTRO DESTINO");
                //cargo los diagnosticos
                createCell(row, 79, rowDataTableList.getColumn122());//"CIE-10 1");
                createCell(row, 80, rowDataTableList.getColumn105());//"CIE-10 1");

                createCell(row, 81, rowDataTableList.getColumn123());//"CIE-10 1");
                createCell(row, 82, rowDataTableList.getColumn106());//"CIE-10 2");

                createCell(row, 83, rowDataTableList.getColumn124());//"CIE-10 1");
                createCell(row, 84, rowDataTableList.getColumn107());//"CIE-10 3");

                createCell(row, 85, rowDataTableList.getColumn125());//"CIE-10 1");
                createCell(row, 86, rowDataTableList.getColumn108());//"CIE-10 4");



                createCell(row, 87, rowDataTableList.getColumn54());//"MEDICO");
                createCell(row, 88, rowDataTableList.getColumn56());//"DIGITADOR");
                //------------------------------------------------------------
                //AUTOINFLINGIDA INTENCIONAL
                //------------------------------------------------------------
                createCell(row, 89, rowDataTableList.getColumn109());//"INTENTO PREVIO (INTENCIONAL AUTOINFLIGIDA)");//100">#{rowX.column109}</p:column>
                createCell(row, 90, rowDataTableList.getColumn110());//"ANTECEDENTES MENTALES (INTENCIONAL AUTOINFLIGIDA)");//100">#{rowX.column110}</p:column>
                createCell(row, 91, rowDataTableList.getColumn111());//"FACTOR PRECIPITANTE (INTENCIONAL AUTOINFLIGIDA)");//100">#{rowX.column111}</p:column>
                createCell(row, 92, rowDataTableList.getColumn27());//"CUAL OTRO FACTOR (INTENCIONAL AUTOINFLIGIDA)");//100">#{rowX.column27}</p:column>                                
                //------------------------------------------------------------
                //SE CARGA VARIABLE PARA VIOLENCIA INTERPERSONAL
                //-----------------------------------------------------------
                createCell(row, 93, rowDataTableList.getColumn60());//"ANTECEDENTES AGRESION (INTERPERSONAL)");//100">#{rowX.column60}</p:column>
                createCell(row, 94, rowDataTableList.getColumn61());//"RELACION AGRESOR-VICTIMA (INTERPERSONAL)");//150">#{rowX.column61}</p:column>
                createCell(row, 95, rowDataTableList.getColumn30());//"CUAL OTRA RELACION (INTERPERSONAL)");//100">#{rowX.column30}</p:column>
                createCell(row, 96, rowDataTableList.getColumn62());//"CONTEXTO (INTERPERSONAL)");//200">#{rowX.column62}</p:column>
                createCell(row, 97, rowDataTableList.getColumn63());//"SEXO AGRESORES (INTERPERSONAL)");//100">#{rowX.column63}</p:c
                //------------------------------------------------------------
                //SE CARGA DATOS PARA VIOLENCIA INTRAFAMILIAR
                //------------------------------------------------------------        
                //tipo de agresor
                createCell(row, 98, rowDataTableList.getColumn64());//"AG1 PADRE(VIF)");//100">#{rowX.column64}</p:column>
                createCell(row, 99, rowDataTableList.getColumn65());//"AG2 MADRE(VIF)");//100">#{rowX.column65}</p:column>
                createCell(row, 100, rowDataTableList.getColumn66());//"AG3 PADRASTRO(VIF)");//100">#{rowX.column67}</p:column>
                createCell(row, 101, rowDataTableList.getColumn67());//"AG4 MADRASTRA(VIF)");//100">#{rowX.column67}</p:column>
                createCell(row, 102, rowDataTableList.getColumn68());//"AG5 CONYUGE(VIF)");//100">#{rowX.column68}</p:column>
                createCell(row, 103, rowDataTableList.getColumn69());//"AG6 HERMANO(VIF)");//100">#{rowX.column69}</p:column>
                createCell(row, 104, rowDataTableList.getColumn70());//"AG7 HIJO(VIF)");//100">#{rowX.column70}</p:column>
                createCell(row, 105, rowDataTableList.getColumn71());//"AG8 OTRO(VIF)");//100">#{rowX.column71}</p:column>
                createCell(row, 106, rowDataTableList.getColumn28());//"CUAL OTRO AGRESOR(VIF)");//100">#{rowX.column28}</p:column>
                createCell(row, 107, rowDataTableList.getColumn72());//"AG9 SIN DATO(VIF)");//100">#{rowX.column72}</p:column>
                createCell(row, 108, rowDataTableList.getColumn73());//"AG10 NOVIO(VIF)");//100">#{rowX.column73}</p:column>                                
                //tipo de maltrato
                createCell(row, 109, rowDataTableList.getColumn74());//"MA1 FISICO(VIF)");//100">#{rowX.column74}</p:column>
                createCell(row, 110, rowDataTableList.getColumn75());//"MA2 PSICOLOGICO(VIF)");//100">#{rowX.column75}</p:column>
                createCell(row, 111, rowDataTableList.getColumn76());//"MA3 VIOLENCIA SEXUAL(VIF)");//100">#{rowX.column76}</p:column>
                createCell(row, 112, rowDataTableList.getColumn77());//"MA4 NEGLIGENCIA(VIF)");//100">#{rowX.column77}</p:column>
                createCell(row, 113, rowDataTableList.getColumn78());//"MA5 ABANDONO(VIF)");//100">#{rowX.column78}</p:column>
                createCell(row, 114, rowDataTableList.getColumn79());//"MA6 INSTITUCIONAL(VIF)");//100">#{rowX.column79}</p:column>
                createCell(row, 115, rowDataTableList.getColumn80());//"MA SIN DATO(VIF)");//100">#{rowX.column80}</p:column>
                createCell(row, 116, rowDataTableList.getColumn81());//"MA8 OTRO(VIF)");//100">#{rowX.column81}</p:column>
                createCell(row, 117, rowDataTableList.getColumn29());//"CUAL OTRO TIPO MALTRATO(VIF)");//100">#{rowX.column29}</p:column>
                //------------------------------------------------------------
                //SE CARGA DATOS PARA TRANSITO
                //------------------------------------------------------------
                createCell(row, 118, rowDataTableList.getColumn112());//"TIPO DE TRANSPORTE");//100">#{rowX.column112}</p:column>
                createCell(row, 119, rowDataTableList.getColumn31());//"CUAL OTRO TIPO DE TRANSPORTE");//100">#{rowX.column31}</p:column>                                
                createCell(row, 120, rowDataTableList.getColumn113());//"TIPO TRANSPORTE CONTRAPARTE");//100">#{rowX.column113}</p:column>
                createCell(row, 121, rowDataTableList.getColumn32());//"CUAL OTRO TIPO TRANSPORTE CONTRAPARTE");//100">#{rowX.column32}</p:column>                                
                createCell(row, 122, rowDataTableList.getColumn114());//"TIPO DE TRANSPORTE DEL USUARIO");//100">#{rowX.column114}</p:column>
                createCell(row, 123, rowDataTableList.getColumn33());//"CUAL OTRO TIPO DE TRANSPORTE DEL USUARIO");//100">#{rowX.column33}</p:column>
                createCell(row, 124, rowDataTableList.getColumn115());//"CINTURON");//100">#{rowX.column115}</p:column>
                createCell(row, 125, rowDataTableList.getColumn116());//"CASCO MOTO");//100">#{rowX.column116}</p:column>
                createCell(row, 126, rowDataTableList.getColumn117());//"CASCO BICICLETA");//100">#{rowX.column117}</p:column>
                createCell(row, 127, rowDataTableList.getColumn118());//"CHALECO");//100">#{rowX.column118}</p:column>
                createCell(row, 128, rowDataTableList.getColumn119());//"OTRO ELEMENTO");//100">#{rowX.column119}</p:column>
                tuplesProcessed++;
                progress = (int) (tuplesProcessed * 100) / tuplesNumber;
                System.out.println(progress);
            }
        } catch (Exception ex) {
            Logger.getLogger(RecordSetsHomicideMB.class.getName()).log(Level.SEVERE, null, ex);
        }
        progress = 100;
    }

    public void load() {
        currentNonFatalInjury = null;
        btnEditDisabled = true;
//        btnRemoveDisabled = true;
        if (selectedRowsDataTable != null) {
            if (selectedRowsDataTable.length == 1) {
                currentNonFatalInjury = nonFatalInjuriesFacade.find(Integer.parseInt(selectedRowsDataTable[0].getColumn1()));
            }
            if (selectedRowsDataTable.length > 1) {

                btnEditDisabled = true;
//                btnRemoveDisabled = false;
            } else {
                btnEditDisabled = false;
//                btnRemoveDisabled = false;
            }
        }
    }

    public void deleteRegistry() {
        if (selectedRowsDataTable != null && selectedRowsDataTable.length != 0) {
            List<NonFatalInjuries> nonFatalInjuriesList = new ArrayList<NonFatalInjuries>();
            for (int j = 0; j < selectedRowsDataTable.length; j++) {
                nonFatalInjuriesList.add(nonFatalInjuriesFacade.find(Integer.parseInt(selectedRowsDataTable[j].getColumn1())));
            }
            if (nonFatalInjuriesList != null) {
                for (int j = 0; j < nonFatalInjuriesList.size(); j++) {
                    if (nonFatalInjuriesList.get(j).getNonFatalDomesticViolence() != null) {
                        nonFatalDomesticViolenceFacade.remove(nonFatalInjuriesList.get(j).getNonFatalDomesticViolence());
                    }
                    if (nonFatalInjuriesList.get(j).getNonFatalInterpersonal() != null) {
                        nonFatalInterpersonalFacade.remove(nonFatalInjuriesList.get(j).getNonFatalInterpersonal());
                    }
                    if (nonFatalInjuriesList.get(j).getNonFatalSelfInflicted() != null) {
                        nonFatalSelfInflictedFacade.remove(nonFatalInjuriesList.get(j).getNonFatalSelfInflicted());
                    }
                    if (nonFatalInjuriesList.get(j).getNonFatalTransport() != null) {
                        nonFatalTransportFacade.remove(nonFatalInjuriesList.get(j).getNonFatalTransport());
                    }
                    nonFatalInjuriesFacade.remove(nonFatalInjuriesList.get(j));
                    victimsFacade.remove(nonFatalInjuriesList.get(j).getVictimId());
                    //----------------------------------------------------------
                }
            }//deselecciono los controles
            selectedRowsDataTable = null;
            btnEditDisabled = true;
            totalRecords = String.valueOf(Integer.parseInt(totalRecords) - 1);
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

    public boolean isBtnEditDisabled() {
        return btnEditDisabled;
    }

    public void setBtnEditDisabled(boolean btnEditDisabled) {
        this.btnEditDisabled = btnEditDisabled;
    }

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
