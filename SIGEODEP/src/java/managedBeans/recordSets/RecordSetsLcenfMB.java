/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.recordSets;

import beans.util.RowDataTable;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import managedBeans.forms.LcenfMB;
import model.dao.*;
import model.pojo.*;
import org.apache.poi.hssf.usermodel.*;

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
    private List<Tags> tagsList;
    private Tags currentTag;
    private NonFatalInjuries currentNonFatalInjury;
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
    NonFatalInjuriesFacade nonFatalInjuriesFacade;
    @EJB
    InjuriesFacade injuriesFacade;
    private List<RowDataTable> rowDataTableList;
    //private RowDataTable selectedRowDataTable;
    private RowDataTable[] selectedRowsDataTable;
    private int currentSearchCriteria = 0;
    private String currentSearchValue = "";
    private String name = "";
    private String newName = "";
    private boolean btnEditDisabled = true;
    private boolean btnRemoveDisabled = true;
    private String data = "-";
    private String hours = "";
    private String minutes = "";
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy hh:mm");
    private LcenfMB lcenfMB;
    private String openForm = "";
    private RecordSetsMB recordSetsMB;

    public RecordSetsLcenfMB() {
    }

    public String openForm() {
        return openForm;
    }

    public void printMessage(FacesMessage.Severity s, String title, String messageStr) {
        FacesMessage msg = new FacesMessage(s, title, messageStr);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void openInForm() {
        FacesContext context = FacesContext.getCurrentInstance();
        lcenfMB = (LcenfMB) context.getApplication().evaluateExpressionGet(context, "#{lcenfMB}", LcenfMB.class);
        lcenfMB.loadValues(tagsList, currentNonFatalInjury);
        openForm = "LCENF";
    }

    void loadValues(RowDataTable[] selectedRowsDataTableTags) {

        FacesContext context = FacesContext.getCurrentInstance();
        recordSetsMB = (RecordSetsMB) context.getApplication().evaluateExpressionGet(context, "#{recordSetsMB}", RecordSetsMB.class);
        recordSetsMB.setProgress(0);
        int totalRegisters = 0;
        int totalProcess = 0;

        selectedRowsDataTable = null;
        rowDataTableList = new ArrayList<RowDataTable>();
        data = "- ";
        //CREO LA LISTA DE TAGS SELECCIONADOS
        tagsList = new ArrayList<Tags>();
        for (int i = 0; i < selectedRowsDataTableTags.length; i++) {
            data = data + selectedRowsDataTableTags[i].getColumn2() + " -";
            tagsList.add(tagsFacade.find(Integer.parseInt(selectedRowsDataTableTags[i].getColumn1())));
        }
        //DETERMINO EL NUMERO DE REGISTROS 
        for (int i = 0; i < tagsList.size(); i++) {
            totalRegisters = totalRegisters + nonFatalInjuriesFacade.countFromTag(tagsList.get(i).getTagId());
        }
        //RECORRO CADA TAG Y CARGO UN LISTADO DE SUS REGISTROS
        List<NonFatalInjuries> nonFatalInjuriesList;
        for (int i = 0; i < tagsList.size(); i++) {
            nonFatalInjuriesList = nonFatalInjuriesFacade.findFromTag(tagsList.get(i).getTagId());
            if (nonFatalInjuriesList != null) {
                for (int j = 0; j < nonFatalInjuriesList.size(); j++) {
                    rowDataTableList.add(loadValues(nonFatalInjuriesList.get(j)));
                    totalProcess++;
                    if (totalRegisters != 0) {
                        recordSetsMB.setProgress((int) (totalProcess * 100) / totalRegisters);
                    }
                }
            }
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
        HSSFWorkbook book = (HSSFWorkbook) document;
        HSSFSheet sheet = book.getSheetAt(0);// Se toma hoja del libro
        HSSFRow row;
        HSSFCellStyle cellStyle = book.createCellStyle();
        HSSFFont font = book.createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        cellStyle.setFont(font);



        row = sheet.createRow(0);// Se crea una fila dentro de la hoja        

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
        createCell(cellStyle, row, 18, "DIRECCION RESIDENCIA");//400">#{rowX.column14}</p:column>
        createCell(cellStyle, row, 19, "TELEFONO");//100">#{rowX.column11}</p:column>

        createCell(cellStyle, row, 20, "BARRIO EVENTO");//250">#{rowX.column42}</p:column>
        createCell(cellStyle, row, 21, "DIRECCION EVENTO");//400">#{rowX.column41}</p:column>

        //------------------------------------------------------------
        //SE CARGAN VARIABLES LESION DE CAUSA EXTERNA NO FATAL
        //------------------------------------------------------------        
        createCell(cellStyle, row, 22, "DIA EVENTO");//100">#{rowX.column37}</p:column>
        createCell(cellStyle, row, 23, "MES EVENTO");//100">#{rowX.column37}</p:column>
        createCell(cellStyle, row, 24, "AÑO EVENTO");//100">#{rowX.column37}</p:column>
        createCell(cellStyle, row, 25, "FECHA EVENTO");//100">#{rowX.column39}</p:column>
        createCell(cellStyle, row, 26, "HORA EVENTO");//100">#{rowX.column40}</p:column>
        createCell(cellStyle, row, 27, "DIA SEMANA EVENTO");//100">#{rowX.column57}</p:column>

        createCell(cellStyle, row, 28, "DIA CONSULTA");//100">#{rowX.column37}</p:column>
        createCell(cellStyle, row, 29, "MES CONSULTA");//100">#{rowX.column37}</p:column>
        createCell(cellStyle, row, 30, "AÑO CONSULTA");//100">#{rowX.column37}</p:column>
        createCell(cellStyle, row, 31, "FECHA CONSULTA");//100">#{rowX.column37}</p:column>
        createCell(cellStyle, row, 32, "HORA CONSULTA");//100">#{rowX.column38}</p:column>
        createCell(cellStyle, row, 33, "REMITIDO");//100">#{rowX.column50}</p:column>
        createCell(cellStyle, row, 34, "REMITIDO DE DONDE");//250">#{rowX.column51}</p:column>
        createCell(cellStyle, row, 35, "INTENCIONALIDAD");//250">#{rowX.column45}</p:column>
        createCell(cellStyle, row, 36, "LUGAR DEL HECHO");//200">#{rowX.column43}</p:column>
        createCell(cellStyle, row, 37, "OTRO LUGAR DEL HECHO");//200">#{rowX.column20}</p:column>
        createCell(cellStyle, row, 38, "ACTIVIDAD");//250">#{rowX.column44}</p:column>
        createCell(cellStyle, row, 39, "OTRA ACTIVIDAD");//250">#{rowX.column21}</p:column>
        createCell(cellStyle, row, 40, "MECANISMO / OBJETO DE LA LESION");//200">#{rowX.column55}</p:column>
        createCell(cellStyle, row, 41, "CUAL ALTURA");//100">#{rowX.column22}</p:column>
        createCell(cellStyle, row, 42, "CUAL POLVORA");//100">#{rowX.column23}</p:column>
        createCell(cellStyle, row, 43, "CUAL DESASTRE NATURAL");//100">#{rowX.column24}</p:column>
        createCell(cellStyle, row, 44, "CUAL OTRO MECANISMO / OBJETO");//100">#{rowX.column25}</p:column>
        createCell(cellStyle, row, 45, "CUAL ANIMAL");//100">#{rowX.column26}</p:column>
        createCell(cellStyle, row, 46, "USO DE ALCOHOL");//150">#{rowX.column46}</p:column>
        createCell(cellStyle, row, 47, "USO DE DROGAS");//150">#{rowX.column47}</p:column>

        createCell(cellStyle, row, 48, "GRADO (QUEMADOS)");//100">#{rowX.column48}</p:column>
        createCell(cellStyle, row, 49, "PORCENTAJE(QUEMADOS)");//100">#{rowX.column49}</p:column>
        //<!-- p:column headerText="injury_id" );//250">{rowX.column59}</p:column -->
        //sitios anatomicos
        createCell(cellStyle, row, 50, "SA1 SISTEMICO");//100">#{rowX.column82}</p:column>
        createCell(cellStyle, row, 51, "SA2 CRANEO");//100">#{rowX.column83}</p:column>
        createCell(cellStyle, row, 52, "SA3 OJOS");//100">#{rowX.column84}</p:column>
        createCell(cellStyle, row, 53, "SA4 MAXILOFACIAL / NARIZ / OIDOS");//100">#{rowX.column85}</p:column>
        createCell(cellStyle, row, 54, "SA5 CUELLO");//100">#{rowX.column86}</p:column>
        createCell(cellStyle, row, 55, "SA6 TORAX");//100">#{rowX.column87}</p:column>
        createCell(cellStyle, row, 56, "SA7 ABDOMEN");//100">#{rowX.column88}</p:column>
        createCell(cellStyle, row, 57, "SA8 COLUMNA");//100">#{rowX.column89}</p:column>
        createCell(cellStyle, row, 58, "SA9 PELVIS / GENITALES");//100">#{rowX.column90}</p:column>
        createCell(cellStyle, row, 59, "SA10 MIEMBROS SUPERIORES");//100">#{rowX.column91}</p:column>
        createCell(cellStyle, row, 60, "SA11 MIEMBROS INFERIORES");//100">#{rowX.column92}</p:column>
        createCell(cellStyle, row, 61, "SA OTRO");//100">#{rowX.column93}</p:column>
        createCell(cellStyle, row, 62, "CUAL OTRO SITIO");//100">#{rowX.column34}</p:column>

        //cargo la naturaleza de la lesion
        createCell(cellStyle, row, 63, "NL1 LACERACION");//100">#{rowX.column94}</p:column>
        createCell(cellStyle, row, 64, "NL2 CORTADA");//100">#{rowX.column95}</p:column>
        createCell(cellStyle, row, 65, "NL3 LESION PROFUNDA");//100">#{rowX.column96}</p:column>
        createCell(cellStyle, row, 66, "NL4 ESGUINCE LUXACION");//100">#{rowX.column97}</p:column>
        createCell(cellStyle, row, 67, "NL5 FRACTURA");//100">#{rowX.column98}</p:column>
        createCell(cellStyle, row, 68, "NL6 QUEMADURA");//100">#{rowX.column99}</p:column>
        createCell(cellStyle, row, 69, "NL7 CONTUSION");//100">#{rowX.column100}</p:column>
        createCell(cellStyle, row, 70, "NL8 ORGANICA SISTEMICA");//100">#{rowX.column101}</p:column>
        createCell(cellStyle, row, 71, "NL9 TRAUMA CRANEOENCEFALICO");//100">#{rowX.column102}</p:column>
        createCell(cellStyle, row, 72, "NL SIN DATO");//100">#{rowX.column104}</p:column>
        createCell(cellStyle, row, 73, "NL OTRO");//100">#{rowX.column103}</p:column>
        createCell(cellStyle, row, 74, "CUAL OTRA NATURALEZA");//150">#{rowX.column35}</p:column>

        createCell(cellStyle, row, 75, "DESTINO DEL PACIENTE");//250">#{rowX.column52}</p:column>
        createCell(cellStyle, row, 76, "CUAL OTRO DESTINO");//100">#{rowX.column36}</p:column>

        //cargo los diagnosticos
        createCell(cellStyle, row, 77, "CIE-10 1");//500">#{rowX.column105}</p:column>
        createCell(cellStyle, row, 78, "CIE-10 2");//500">#{rowX.column106}</p:column>
        createCell(cellStyle, row, 79, "CIE-10 3");//500">#{rowX.column107}</p:column>
        createCell(cellStyle, row, 80, "CIE-10 4");//500">#{rowX.column108}</p:column>

        createCell(cellStyle, row, 81, "MEDICO");//300">#{rowX.column54}</p:column>
        createCell(cellStyle, row, 82, "DIGITADOR");//100">#{rowX.column56}</p:column>

        //------------------------------------------------------------
        //AUTOINFLINGIDA INTENCIONAL
        //------------------------------------------------------------
        createCell(cellStyle, row, 83, "INTENTO PREVIO (INTENCIONAL AUTOINFLIGIDA)");//100">#{rowX.column109}</p:column>
        createCell(cellStyle, row, 84, "ANTECEDENTES MENTALES (INTENCIONAL AUTOINFLIGIDA)");//100">#{rowX.column110}</p:column>
        createCell(cellStyle, row, 85, "FACTOR PRECIPITANTE (INTENCIONAL AUTOINFLIGIDA)");//100">#{rowX.column111}</p:column>
        createCell(cellStyle, row, 86, "CUAL OTRO FACTOR (INTENCIONAL AUTOINFLIGIDA)");//100">#{rowX.column27}</p:column>                                

        //------------------------------------------------------------
        //SE CARGA VARIABLE PARA VIOLENCIA INTERPERSONAL
        //-----------------------------------------------------------
        createCell(cellStyle, row, 87, "ANTECEDENTES AGRESION (INTERPERSONAL)");//100">#{rowX.column60}</p:column>
        createCell(cellStyle, row, 88, "RELACION AGRESOR-VICTIMA (INTERPERSONAL)");//150">#{rowX.column61}</p:column>
        createCell(cellStyle, row, 89, "CUAL OTRA RELACION (INTERPERSONAL)");//100">#{rowX.column30}</p:column>
        createCell(cellStyle, row, 90, "CONTEXTO (INTERPERSONAL)");//200">#{rowX.column62}</p:column>
        createCell(cellStyle, row, 91, "SEXO AGRESORES (INTERPERSONAL)");//100">#{rowX.column63}</p:column>

        //------------------------------------------------------------
        //SE CARGA DATOS PARA VIOLENCIA INTRAFAMILIAR
        //------------------------------------------------------------        
        //tipo de agresor
        createCell(cellStyle, row, 92, "AG1 PADRE(VIF)");//100">#{rowX.column64}</p:column>
        createCell(cellStyle, row, 93, "AG2 MADRE(VIF)");//100">#{rowX.column65}</p:column>
        createCell(cellStyle, row, 94, "AG3 PADRASTRO(VIF)");//100">#{rowX.column67}</p:column>
        createCell(cellStyle, row, 95, "AG4 MADRASTRA(VIF)");//100">#{rowX.column67}</p:column>
        createCell(cellStyle, row, 96, "AG5 CONYUGE(VIF)");//100">#{rowX.column68}</p:column>
        createCell(cellStyle, row, 97, "AG6 HERMANO(VIF)");//100">#{rowX.column69}</p:column>
        createCell(cellStyle, row, 98, "AG7 HIJO(VIF)");//100">#{rowX.column70}</p:column>
        createCell(cellStyle, row, 99, "AG8 OTRO(VIF)");//100">#{rowX.column71}</p:column>
        createCell(cellStyle, row, 100, "CUAL OTRO AGRESOR(VIF)");//100">#{rowX.column28}</p:column>
        createCell(cellStyle, row, 101, "AG9 SIN DATO(VIF)");//100">#{rowX.column72}</p:column>
        createCell(cellStyle, row, 102, "AG10 NOVIO(VIF)");//100">#{rowX.column73}</p:column>                                

        //tipo de maltrato
        createCell(cellStyle, row, 103, "MA1 FISICO(VIF)");//100">#{rowX.column74}</p:column>
        createCell(cellStyle, row, 104, "MA2 PSICOLOGICO(VIF)");//100">#{rowX.column75}</p:column>
        createCell(cellStyle, row, 105, "MA3 VIOLENCIA SEXUAL(VIF)");//100">#{rowX.column76}</p:column>
        createCell(cellStyle, row, 106, "MA4 NEGLIGENCIA(VIF)");//100">#{rowX.column77}</p:column>
        createCell(cellStyle, row, 107, "MA5 ABANDONO(VIF)");//100">#{rowX.column78}</p:column>
        createCell(cellStyle, row, 108, "MA6 INSTITUCIONAL(VIF)");//100">#{rowX.column79}</p:column>
        createCell(cellStyle, row, 109, "MA SIN DATO(VIF)");//100">#{rowX.column80}</p:column>
        createCell(cellStyle, row, 110, "MA8 OTRO(VIF)");//100">#{rowX.column81}</p:column>
        createCell(cellStyle, row, 111, "CUAL OTRO TIPO MALTRATO(VIF)");//100">#{rowX.column29}</p:column>


        //------------------------------------------------------------
        //SE CARGA DATOS PARA TRANSITO
        //------------------------------------------------------------
        createCell(cellStyle, row, 112, "TIPO DE TRANSPORTE");//100">#{rowX.column112}</p:column>
        createCell(cellStyle, row, 113, "CUAL OTRO TIPO DE TRANSPORTE");//100">#{rowX.column31}</p:column>                                
        createCell(cellStyle, row, 114, "TIPO TRANSPORTE CONTRAPARTE");//100">#{rowX.column113}</p:column>
        createCell(cellStyle, row, 115, "CUAL OTRO TIPO TRANSPORTE CONTRAPARTE");//100">#{rowX.column32}</p:column>                                
        createCell(cellStyle, row, 116, "TIPO DE TRANSPORTE DEL USUARIO");//100">#{rowX.column114}</p:column>
        createCell(cellStyle, row, 117, "CUAL OTRO TIPO DE TRANSPORTE DEL USUARIO");//100">#{rowX.column33}</p:column>

        createCell(cellStyle, row, 118, "CINTURON");//100">#{rowX.column115}</p:column>
        createCell(cellStyle, row, 119, "CASCO MOTO");//100">#{rowX.column116}</p:column>
        createCell(cellStyle, row, 120, "CASCO BICICLETA");//100">#{rowX.column117}</p:column>
        createCell(cellStyle, row, 121, "CHALECO");//100">#{rowX.column118}</p:column>
        createCell(cellStyle, row, 122, "OTRO ELEMENTO");//100">#{rowX.column119}</p:column>

        String[] splitDate;
        for (int i = 0; i < rowDataTableList.size(); i++) {
            row = sheet.createRow(i + 1);
            createCell(row, 0, rowDataTableList.get(i).getColumn1());//"CODIGO");
            createCell(row, 1, rowDataTableList.get(i).getColumn58());//"INSTITUCION DE SALUD");
            createCell(row, 2, rowDataTableList.get(i).getColumn4());//"NOMBRES Y APELLIDOS");
            createCell(row, 3, rowDataTableList.get(i).getColumn2());//"TIPO IDENTIFICACION");
            createCell(row, 4, rowDataTableList.get(i).getColumn3());//"IDENTIFICACION");
            createCell(row, 5, rowDataTableList.get(i).getColumn6());//"TIPO EDAD");
            createCell(row, 6, rowDataTableList.get(i).getColumn7());//"EDAD");
            createCell(row, 7, rowDataTableList.get(i).getColumn8());//"GENERO");
            createCell(row, 8, rowDataTableList.get(i).getColumn9());//"OCUPACION");
            createCell(row, 9, rowDataTableList.get(i).getColumn18());//"ASEGURADORA");
            createCell(row, 10, rowDataTableList.get(i).getColumn16());//"DESPLAZADO");
            createCell(row, 11, rowDataTableList.get(i).getColumn17());//"DISCAPACITADO");
            createCell(row, 12, rowDataTableList.get(i).getColumn10());//"GRUPO ETNICO");
            createCell(row, 13, rowDataTableList.get(i).getColumn19());//"OTRO GRUPO ETNICO");
            createCell(row, 14, rowDataTableList.get(i).getColumn5());//"EXTRANJERO");
            createCell(row, 15, rowDataTableList.get(i).getColumn13());//"DEPARTAMENTO RESIDENCIA");
            createCell(row, 16, rowDataTableList.get(i).getColumn12());//"MUNICIPIO RESIDENCIA");
            createCell(row, 17, rowDataTableList.get(i).getColumn15());//"BARRIO RESIDENCIA");
            createCell(row, 18, rowDataTableList.get(i).getColumn14());//"DIRECCION RESIDENCIA");
            createCell(row, 19, rowDataTableList.get(i).getColumn11());//"TELEFONO");
            createCell(row, 20, rowDataTableList.get(i).getColumn42());//"BARRIO EVENTO");
            createCell(row, 21, rowDataTableList.get(i).getColumn41());//"DIRECCION EVENTO");
            //------------------------------------------------------------
            //SE CARGAN VARIABLES LESION DE CAUSA EXTERNA NO FATAL
            //------------------------------------------------------------        
            if (rowDataTableList.get(i).getColumn39() != null) {
                splitDate = rowDataTableList.get(i).getColumn39().split("/");
                if (splitDate.length == 3) {
                    createCell(row, 22, splitDate[0]);//"DIA EVENTO");
                    createCell(row, 23, splitDate[1]);//"MES EVENTO");
                    createCell(row, 24, splitDate[2]);//"AÑO EVENTO");
                }
            }
            createCell(row, 25, rowDataTableList.get(i).getColumn39());//"FECHA EVENTO");
            createCell(row, 26, rowDataTableList.get(i).getColumn40());//"HORA EVENTO");
            createCell(row, 27, rowDataTableList.get(i).getColumn57());//"DIA SEMANA EVENTO");

            if (rowDataTableList.get(i).getColumn37() != null) {
                splitDate = rowDataTableList.get(i).getColumn37().split("/");
                if (splitDate.length == 3) {
                    createCell(row, 28, splitDate[0]);//"DIA CONSULTA");
                    createCell(row, 29, splitDate[1]);//"MES CONSULTA");
                    createCell(row, 30, splitDate[2]);//"AÑO CONSULTA");
                }
            }
            createCell(row, 31, rowDataTableList.get(i).getColumn37());//"FECHA CONSULTA");
            createCell(row, 32, rowDataTableList.get(i).getColumn38());//"HORA CONSULTA");
            createCell(row, 33, rowDataTableList.get(i).getColumn50());//"REMITIDO");
            createCell(row, 34, rowDataTableList.get(i).getColumn51());//"REMITIDO DE DONDE");
            createCell(row, 35, rowDataTableList.get(i).getColumn45());//"INTENCIONALIDAD");
            createCell(row, 36, rowDataTableList.get(i).getColumn43());//"LUGAR DEL HECHO");
            createCell(row, 37, rowDataTableList.get(i).getColumn20());//"OTRO LUGAR DEL HECHO");
            createCell(row, 38, rowDataTableList.get(i).getColumn44());//"ACTIVIDAD");
            createCell(row, 39, rowDataTableList.get(i).getColumn21());//"OTRA ACTIVIDAD");
            createCell(row, 40, rowDataTableList.get(i).getColumn55());//"MECANISMO / OBJETO DE LA LESION" 
            createCell(row, 41, rowDataTableList.get(i).getColumn22());//"CUAL ALTURA");
            createCell(row, 42, rowDataTableList.get(i).getColumn23());//"CUAL POLVORA");
            createCell(row, 43, rowDataTableList.get(i).getColumn24());//"CUAL DESASTRE NATURAL");
            createCell(row, 44, rowDataTableList.get(i).getColumn25());//"CUAL OTRO MECANISMO / OBJETO");
            createCell(row, 45, rowDataTableList.get(i).getColumn26());//"CUAL ANIMAL");
            createCell(row, 46, rowDataTableList.get(i).getColumn46());//"USO DE ALCOHOL");
            createCell(row, 47, rowDataTableList.get(i).getColumn47());//"USO DE DROGAS");
            createCell(row, 48, rowDataTableList.get(i).getColumn48());//"GRADO (QUEMADOS)");
            createCell(row, 49, rowDataTableList.get(i).getColumn49());//"PORCENTAJE(QUEMADOS)");
            //sitios anatomicos
            createCell(row, 50, rowDataTableList.get(i).getColumn82());//"SA1 SISTEMICO");
            createCell(row, 51, rowDataTableList.get(i).getColumn83());//"SA2 CRANEO");
            createCell(row, 52, rowDataTableList.get(i).getColumn84());//"SA3 OJOS");
            createCell(row, 53, rowDataTableList.get(i).getColumn85());//"SA4 MAXILOFACIAL / NARIZ / OIDOS");
            createCell(row, 54, rowDataTableList.get(i).getColumn86());//"SA5 CUELLO");
            createCell(row, 55, rowDataTableList.get(i).getColumn87());//"SA6 TORAX");
            createCell(row, 56, rowDataTableList.get(i).getColumn88());//"SA7 ABDOMEN");
            createCell(row, 57, rowDataTableList.get(i).getColumn89());//"SA8 COLUMNA");
            createCell(row, 58, rowDataTableList.get(i).getColumn90());//"SA9 PELVIS / GENITALES");
            createCell(row, 59, rowDataTableList.get(i).getColumn91());//"SA10 MIEMBROS SUPERIORES");
            createCell(row, 60, rowDataTableList.get(i).getColumn92());//"SA11 MIEMBROS INFERIORES");
            createCell(row, 61, rowDataTableList.get(i).getColumn93());//"SA OTRO");
            createCell(row, 62, rowDataTableList.get(i).getColumn34());//"CUAL OTRO SITIO");
            //cargo la naturaleza de la lesion
            createCell(row, 63, rowDataTableList.get(i).getColumn94());//"NL1 LACERACION");
            createCell(row, 64, rowDataTableList.get(i).getColumn95());//"NL2 CORTADA");
            createCell(row, 65, rowDataTableList.get(i).getColumn96());//"NL3 LESION PROFUNDA");
            createCell(row, 66, rowDataTableList.get(i).getColumn97());//"NL4 ESGUINCE LUXACION");
            createCell(row, 67, rowDataTableList.get(i).getColumn98());//"NL5 FRACTURA");
            createCell(row, 68, rowDataTableList.get(i).getColumn99());//"NL6 QUEMADURA");
            createCell(row, 69, rowDataTableList.get(i).getColumn100());//"NL7 CONTUSION");
            createCell(row, 70, rowDataTableList.get(i).getColumn101());//"NL8 ORGANICA SISTEMICA");
            createCell(row, 71, rowDataTableList.get(i).getColumn102());//"NL9 TRAUMA CRANEOENCEFALICO");
            createCell(row, 72, rowDataTableList.get(i).getColumn104());//"NL SIN DATO");
            createCell(row, 73, rowDataTableList.get(i).getColumn103());//"NL OTRO");
            createCell(row, 74, rowDataTableList.get(i).getColumn35());//"CUAL OTRA NATURALEZA");
            createCell(row, 75, rowDataTableList.get(i).getColumn52());//"DESTINO DEL PACIENTE");
            createCell(row, 76, rowDataTableList.get(i).getColumn36());//"CUAL OTRO DESTINO");
            //cargo los diagnosticos
            createCell(row, 77, rowDataTableList.get(i).getColumn105());//"CIE-10 1");
            createCell(row, 78, rowDataTableList.get(i).getColumn106());//"CIE-10 2");
            createCell(row, 79, rowDataTableList.get(i).getColumn107());//"CIE-10 3");
            createCell(row, 80, rowDataTableList.get(i).getColumn108());//"CIE-10 4");
            createCell(row, 81, rowDataTableList.get(i).getColumn54());//"MEDICO");
            createCell(row, 82, rowDataTableList.get(i).getColumn56());//"DIGITADOR");
            //------------------------------------------------------------
            //AUTOINFLINGIDA INTENCIONAL
            //------------------------------------------------------------
            createCell(row, 83, rowDataTableList.get(i).getColumn109());//"INTENTO PREVIO (INTENCIONAL AUTOINFLIGIDA)");//100">#{rowX.column109}</p:column>
            createCell(row, 84, rowDataTableList.get(i).getColumn110());//"ANTECEDENTES MENTALES (INTENCIONAL AUTOINFLIGIDA)");//100">#{rowX.column110}</p:column>
            createCell(row, 85, rowDataTableList.get(i).getColumn111());//"FACTOR PRECIPITANTE (INTENCIONAL AUTOINFLIGIDA)");//100">#{rowX.column111}</p:column>
            createCell(row, 86, rowDataTableList.get(i).getColumn27());//"CUAL OTRO FACTOR (INTENCIONAL AUTOINFLIGIDA)");//100">#{rowX.column27}</p:column>                                
            //------------------------------------------------------------
            //SE CARGA VARIABLE PARA VIOLENCIA INTERPERSONAL
            //-----------------------------------------------------------
            createCell(row, 87, rowDataTableList.get(i).getColumn60());//"ANTECEDENTES AGRESION (INTERPERSONAL)");//100">#{rowX.column60}</p:column>
            createCell(row, 88, rowDataTableList.get(i).getColumn61());//"RELACION AGRESOR-VICTIMA (INTERPERSONAL)");//150">#{rowX.column61}</p:column>
            createCell(row, 89, rowDataTableList.get(i).getColumn30());//"CUAL OTRA RELACION (INTERPERSONAL)");//100">#{rowX.column30}</p:column>
            createCell(row, 90, rowDataTableList.get(i).getColumn62());//"CONTEXTO (INTERPERSONAL)");//200">#{rowX.column62}</p:column>
            createCell(row, 91, rowDataTableList.get(i).getColumn63());//"SEXO AGRESORES (INTERPERSONAL)");//100">#{rowX.column63}</p:c
            //------------------------------------------------------------
            //SE CARGA DATOS PARA VIOLENCIA INTRAFAMILIAR
            //------------------------------------------------------------        
            //tipo de agresor
            createCell(row, 92, rowDataTableList.get(i).getColumn64());//"AG1 PADRE(VIF)");//100">#{rowX.column64}</p:column>
            createCell(row, 93, rowDataTableList.get(i).getColumn65());//"AG2 MADRE(VIF)");//100">#{rowX.column65}</p:column>
            createCell(row, 94, rowDataTableList.get(i).getColumn66());//"AG3 PADRASTRO(VIF)");//100">#{rowX.column67}</p:column>
            createCell(row, 95, rowDataTableList.get(i).getColumn67());//"AG4 MADRASTRA(VIF)");//100">#{rowX.column67}</p:column>
            createCell(row, 96, rowDataTableList.get(i).getColumn68());//"AG5 CONYUGE(VIF)");//100">#{rowX.column68}</p:column>
            createCell(row, 97, rowDataTableList.get(i).getColumn69());//"AG6 HERMANO(VIF)");//100">#{rowX.column69}</p:column>
            createCell(row, 98, rowDataTableList.get(i).getColumn70());//"AG7 HIJO(VIF)");//100">#{rowX.column70}</p:column>
            createCell(row, 99, rowDataTableList.get(i).getColumn71());//"AG8 OTRO(VIF)");//100">#{rowX.column71}</p:column>
            createCell(row, 100, rowDataTableList.get(i).getColumn28());//"CUAL OTRO AGRESOR(VIF)");//100">#{rowX.column28}</p:column>
            createCell(row, 101, rowDataTableList.get(i).getColumn72());//"AG9 SIN DATO(VIF)");//100">#{rowX.column72}</p:column>
            createCell(row, 102, rowDataTableList.get(i).getColumn73());//"AG10 NOVIO(VIF)");//100">#{rowX.column73}</p:column>                                
            //tipo de maltrato
            createCell(row, 103, rowDataTableList.get(i).getColumn74());//"MA1 FISICO(VIF)");//100">#{rowX.column74}</p:column>
            createCell(row, 104, rowDataTableList.get(i).getColumn75());//"MA2 PSICOLOGICO(VIF)");//100">#{rowX.column75}</p:column>
            createCell(row, 105, rowDataTableList.get(i).getColumn76());//"MA3 VIOLENCIA SEXUAL(VIF)");//100">#{rowX.column76}</p:column>
            createCell(row, 106, rowDataTableList.get(i).getColumn77());//"MA4 NEGLIGENCIA(VIF)");//100">#{rowX.column77}</p:column>
            createCell(row, 107, rowDataTableList.get(i).getColumn78());//"MA5 ABANDONO(VIF)");//100">#{rowX.column78}</p:column>
            createCell(row, 108, rowDataTableList.get(i).getColumn79());//"MA6 INSTITUCIONAL(VIF)");//100">#{rowX.column79}</p:column>
            createCell(row, 109, rowDataTableList.get(i).getColumn80());//"MA SIN DATO(VIF)");//100">#{rowX.column80}</p:column>
            createCell(row, 110, rowDataTableList.get(i).getColumn81());//"MA8 OTRO(VIF)");//100">#{rowX.column81}</p:column>
            createCell(row, 111, rowDataTableList.get(i).getColumn29());//"CUAL OTRO TIPO MALTRATO(VIF)");//100">#{rowX.column29}</p:column>
            //------------------------------------------------------------
            //SE CARGA DATOS PARA TRANSITO
            //------------------------------------------------------------
            createCell(row, 112, rowDataTableList.get(i).getColumn112());//"TIPO DE TRANSPORTE");//100">#{rowX.column112}</p:column>
            createCell(row, 113, rowDataTableList.get(i).getColumn31());//"CUAL OTRO TIPO DE TRANSPORTE");//100">#{rowX.column31}</p:column>                                
            createCell(row, 114, rowDataTableList.get(i).getColumn113());//"TIPO TRANSPORTE CONTRAPARTE");//100">#{rowX.column113}</p:column>
            createCell(row, 115, rowDataTableList.get(i).getColumn32());//"CUAL OTRO TIPO TRANSPORTE CONTRAPARTE");//100">#{rowX.column32}</p:column>                                
            createCell(row, 116, rowDataTableList.get(i).getColumn114());//"TIPO DE TRANSPORTE DEL USUARIO");//100">#{rowX.column114}</p:column>
            createCell(row, 117, rowDataTableList.get(i).getColumn33());//"CUAL OTRO TIPO DE TRANSPORTE DEL USUARIO");//100">#{rowX.column33}</p:column>
            createCell(row, 118, rowDataTableList.get(i).getColumn115());//"CINTURON");//100">#{rowX.column115}</p:column>
            createCell(row, 119, rowDataTableList.get(i).getColumn116());//"CASCO MOTO");//100">#{rowX.column116}</p:column>
            createCell(row, 120, rowDataTableList.get(i).getColumn117());//"CASCO BICICLETA");//100">#{rowX.column117}</p:column>
            createCell(row, 121, rowDataTableList.get(i).getColumn118());//"CHALECO");//100">#{rowX.column118}</p:column>
            createCell(row, 122, rowDataTableList.get(i).getColumn119());//"OTRO ELEMENTO");//100">#{rowX.column119}</p:column>
        }
    }

    private RowDataTable loadValues(NonFatalInjuries currentNonFatalI) {
        //CARGO LOS DATOS DE UNA DETERMINA LESION NO FATAL EN UNA FILA PARA LA TABLA
        btnEditDisabled = true;
        btnRemoveDisabled = true;
        RowDataTable newRowDataTable = new RowDataTable();
        //------------------------------------------------------------
        //SE CARGAN VALORES PARA LA NUEVA VICTIMA
        //------------------------------------------------------------

        //******non_fatal_injury_id
        newRowDataTable.setColumn1(currentNonFatalI.getNonFatalInjuryId().toString());
        //******type_id
        try {
            if (currentNonFatalI.getVictimId().getTypeId() != null) {
                newRowDataTable.setColumn2(currentNonFatalI.getVictimId().getTypeId().getTypeName());
            }
        } catch (Exception e) {
        }
        //******victim_nid
        try {
            if (currentNonFatalI.getVictimId().getVictimNid() != null) {
                newRowDataTable.setColumn3(currentNonFatalI.getVictimId().getVictimNid());
            }
        } catch (Exception e) {
        }
        //******victim_name
        try {
            if (currentNonFatalI.getVictimId().getVictimName() != null) {
                newRowDataTable.setColumn4(currentNonFatalI.getVictimId().getVictimName());
            }
        } catch (Exception e) {
        }
        //******stranger
        try {
            if (currentNonFatalI.getVictimId().getStranger() != null) {
                if (currentNonFatalI.getVictimId().getStranger()) {
                    newRowDataTable.setColumn5("SI");
                } else {
                    newRowDataTable.setColumn5("NO");
                }

            }
        } catch (Exception e) {
        }
        //******age_type_id
        try {
            if (currentNonFatalI.getVictimId().getAgeTypeId() != null) {
                newRowDataTable.setColumn6(ageTypesFacade.find(currentNonFatalI.getVictimId().getAgeTypeId()).getAgeTypeName());
            }
        } catch (Exception e) {
        }
        //******victim_age
        try {
            if (currentNonFatalI.getVictimId().getVictimAge() != null) {
                newRowDataTable.setColumn7(currentNonFatalI.getVictimId().getVictimAge().toString());
            }
        } catch (Exception e) {
        }
        //******gender_id
        try {
            if (currentNonFatalI.getVictimId().getGenderId() != null) {
                newRowDataTable.setColumn8(currentNonFatalI.getVictimId().getGenderId().getGenderName());
            }
        } catch (Exception e) {
        }
        //******job_id
        try {
            if (currentNonFatalI.getVictimId().getJobId() != null) {
                newRowDataTable.setColumn9(currentNonFatalI.getVictimId().getJobId().getJobName());
            }
        } catch (Exception e) {
        }

        //******ethnic_group_id
        try {
            if (currentNonFatalI.getVictimId().getEthnicGroupId() != null) {
                newRowDataTable.setColumn10(currentNonFatalI.getVictimId().getEthnicGroupId().getEthnicGroupName());
            }
        } catch (Exception e) {
        }
        //******victim_telephone
        try {
            if (currentNonFatalI.getVictimId().getVictimTelephone() != null) {
                newRowDataTable.setColumn11(currentNonFatalI.getVictimId().getVictimTelephone());
            }
        } catch (Exception e) {
        }

        //******victim_date_of_birth
        //******eps_id
        //******victim_class
        //******victim_id        
        //******residence_municipality
        try {
            if (currentNonFatalI.getVictimId().getResidenceDepartment() != null && (currentNonFatalI.getVictimId().getResidenceMunicipality() != null)) {
                short departamentId = currentNonFatalI.getVictimId().getResidenceDepartment();
                short municipalityId = currentNonFatalI.getVictimId().getResidenceMunicipality();
                MunicipalitiesPK mPk = new MunicipalitiesPK(departamentId, municipalityId);
                newRowDataTable.setColumn12(municipalitiesFacade.find(mPk).getMunicipalityName());
            }
        } catch (Exception e) {
        }
        //******residence_department
        try {
            if (currentNonFatalI.getVictimId().getResidenceDepartment() != null) {
                newRowDataTable.setColumn13(departamentsFacade.find(currentNonFatalI.getVictimId().getResidenceDepartment()).getDepartamentName());
            }
        } catch (Exception e) {
        }
        //******victim_address
        try {
            if (currentNonFatalI.getVictimId().getVictimAddress() != null) {
                newRowDataTable.setColumn14(currentNonFatalI.getVictimId().getVictimAddress());
            }
        } catch (Exception e) {
        }
        //******victim_neighborhood_id
        try {
            if (currentNonFatalI.getVictimId().getVictimNeighborhoodId() != null) {
                newRowDataTable.setColumn15(currentNonFatalI.getVictimId().getVictimNeighborhoodId().getNeighborhoodName());
            }
        } catch (Exception e) {
        }
        //informacion de grupos vunerables
        if (currentNonFatalI.getVictimId().getVulnerableGroupsList() != null) {
            if (!currentNonFatalI.getVictimId().getVulnerableGroupsList().isEmpty()) {
                for (int i = 0; i < currentNonFatalI.getVictimId().getVulnerableGroupsList().size(); i++) {
                    if (1 == currentNonFatalI.getVictimId().getVulnerableGroupsList().get(i).getVulnerableGroupId()) {
                        newRowDataTable.setColumn16("SI");//isDisplaced = true;
                    }
                    if (2 == currentNonFatalI.getVictimId().getVulnerableGroupsList().get(i).getVulnerableGroupId()) {
                        //isHandicapped = true;
                        newRowDataTable.setColumn17("SI");
                    }
                }
            }
        }

        //******insurance_id
        try {
            if (currentNonFatalI.getVictimId().getInsuranceId() != null) {
                newRowDataTable.setColumn18(currentNonFatalI.getVictimId().getInsuranceId().getInsuranceName());
            }
        } catch (Exception e) {
        }

        //-----CARGAR CAMPOS OTROS----------------
        if (currentNonFatalI.getVictimId().getOthersList() != null) {
            List<Others> othersList = currentNonFatalI.getVictimId().getOthersList();
            for (int i = 0; i < othersList.size(); i++) {
                switch (othersList.get(i).getOthersPK().getFieldId()) {
                    case 1://1.	Cual otro grupo etnico
                        newRowDataTable.setColumn19(othersList.get(i).getValueText());
                        break;
                    case 2://2.	Cual otro de lugar del hecho
                        newRowDataTable.setColumn20(othersList.get(i).getValueText());
                        break;
                    case 3://3.	Cual otra actividad
                        newRowDataTable.setColumn21(othersList.get(i).getValueText());
                        break;
                    case 4://4.	Cual altura
                        newRowDataTable.setColumn22(othersList.get(i).getValueText());
                        break;
                    case 5://5.	Cual polvora
                        newRowDataTable.setColumn23(othersList.get(i).getValueText());
                        break;
                    case 6://6.	Cual desastre natural
                        newRowDataTable.setColumn24(othersList.get(i).getValueText());
                        break;
                    case 7://7.	Cual otro mecanismo de objeto
                        newRowDataTable.setColumn25(othersList.get(i).getValueText());
                        break;
                    case 8://8.	Cual otro animal
                        newRowDataTable.setColumn26(othersList.get(i).getValueText());
                        break;
                    case 9://9.	Cual otro factor precipitante(Autoinflingida intencional)
                        newRowDataTable.setColumn27(othersList.get(i).getValueText());
                        break;
                    case 10://10.	Cual otro tipo de agresor(intrafamiliar)
                        newRowDataTable.setColumn28(othersList.get(i).getValueText());
                        break;
                    case 11://11.	Cual otro tipo de maltrato(intrafamiliar)
                        newRowDataTable.setColumn29(othersList.get(i).getValueText());
                        break;
                    case 12://12.	Cual otra relación (violencia interpersonal)
                        newRowDataTable.setColumn30(othersList.get(i).getValueText());
                        break;
                    case 13://13.	Cual otro tipo de transporte(transporte)
                        newRowDataTable.setColumn31(othersList.get(i).getValueText());
                        break;
                    case 14://14.	Cual otro tipo de transporte de contraparte(transporte)
                        newRowDataTable.setColumn32(othersList.get(i).getValueText());
                        break;
                    case 15://15.	Cual otro tipo de transporte de usuario(transporte)
                        newRowDataTable.setColumn33(othersList.get(i).getValueText());
                        break;
                    case 16://16.	Cual otro sitio anatomico
                        newRowDataTable.setColumn34(othersList.get(i).getValueText());
                        break;
                    case 17://17.	Cual otra naturaleza de la lesión
                        newRowDataTable.setColumn35(othersList.get(i).getValueText());
                        break;
                    case 18://18.	Cual otro destino del paciente
                        newRowDataTable.setColumn36(othersList.get(i).getValueText());
                        break;
                }
            }
        }

        //------------------------------------------------------------
        //SE CARGAN VARIABLES LESION DE CAUSA EXTERNA NO FATAL
        //------------------------------------------------------------        
        //******checkup_date
        try {
            if (currentNonFatalI.getCheckupDate() != null) {
                newRowDataTable.setColumn37(sdf.format(currentNonFatalI.getCheckupDate()));
            }
        } catch (Exception e) {
        }
        //******checkup_time
        try {
            if (currentNonFatalI.getCheckupTime() != null) {
                hours = String.valueOf(currentNonFatalI.getCheckupTime().getHours());
                minutes = String.valueOf(currentNonFatalI.getCheckupTime().getMinutes());
                if (hours.length() != 2) {
                    hours = "0" + hours;
                }
                if (minutes.length() != 2) {
                    minutes = "0" + minutes;
                }
                newRowDataTable.setColumn38(hours + minutes);
            }
        } catch (Exception e) {
        }
        //******injury_date
        try {
            if (currentNonFatalI.getInjuryDate() != null) {
                newRowDataTable.setColumn39(sdf.format(currentNonFatalI.getInjuryDate()));
            }
        } catch (Exception e) {
        }
        //******injury_time
        try {
            if (currentNonFatalI.getInjuryTime() != null) {
                hours = String.valueOf(currentNonFatalI.getInjuryTime().getHours());
                minutes = String.valueOf(currentNonFatalI.getInjuryTime().getMinutes());
                if (hours.length() != 2) {
                    hours = "0" + hours;
                }
                if (minutes.length() != 2) {
                    minutes = "0" + minutes;
                }
                newRowDataTable.setColumn40(hours + minutes);
            }
        } catch (Exception e) {
        }
        //******injury_address
        if (currentNonFatalI.getInjuryAddress() != null) {
            newRowDataTable.setColumn41(currentNonFatalI.getInjuryAddress());
        }
        //******injury_neighborhood_id
        try {
            if (currentNonFatalI.getInjuryNeighborhoodId() != null) {
                newRowDataTable.setColumn42(currentNonFatalI.getInjuryNeighborhoodId().getNeighborhoodName());
            }
        } catch (Exception e) {
        }
        //******injury_place_id
        try {
            if (currentNonFatalI.getInjuryPlaceId() != null) {
                newRowDataTable.setColumn43(currentNonFatalI.getInjuryPlaceId().getNonFatalPlaceName());
            }
        } catch (Exception e) {
        }
        //******activity_id
        try {
            if (currentNonFatalI.getActivityId() != null) {
                newRowDataTable.setColumn44(currentNonFatalI.getActivityId().getActivityName());
            }
        } catch (Exception e) {
        }
        //******intentionality_id
        try {
            if (currentNonFatalI.getIntentionalityId() != null) {
                newRowDataTable.setColumn45(currentNonFatalI.getIntentionalityId().getIntentionalityName());
            }
        } catch (Exception e) {
        }
        //******use_alcohol_id
        try {
            if (currentNonFatalI.getUseAlcoholId() != null) {
                newRowDataTable.setColumn46(currentNonFatalI.getUseAlcoholId().getUseAlcoholDrugsName());
            }
        } catch (Exception e) {
        }
        //******use_drugs_id
        try {
            if (currentNonFatalI.getUseDrugsId() != null) {
                newRowDataTable.setColumn47(currentNonFatalI.getUseDrugsId().getUseAlcoholDrugsName());
            }
        } catch (Exception e) {
        }
        //******burn_injury_degree
        try {
            if (currentNonFatalI.getBurnInjuryDegree() != null) {
                newRowDataTable.setColumn48(currentNonFatalI.getBurnInjuryDegree().toString());
            }
        } catch (Exception e) {
        }
        //******burn_injury_percentage
        try {
            if (currentNonFatalI.getBurnInjuryPercentage() != null) {
                newRowDataTable.setColumn49(currentNonFatalI.getBurnInjuryPercentage().toString());
            }
        } catch (Exception e) {
        }
        //******submitted_patient

        try {
            if (currentNonFatalI.getSubmittedPatient() != null) {
                if (currentNonFatalI.getSubmittedPatient()) {
                    newRowDataTable.setColumn50("SI");
                } else {
                    newRowDataTable.setColumn50("NO");
                }

            }
        } catch (Exception e) {
        }
        //******eps_id
        try {
            if (currentNonFatalI.getSubmittedDataSourceId() != null) {
                newRowDataTable.setColumn51(currentNonFatalI.getSubmittedDataSourceId().getNonFatalDataSourceName());
            }
        } catch (Exception e) {
        }
        //******destination_patient_id
        try {
            if (currentNonFatalI.getDestinationPatientId() != null) {
                newRowDataTable.setColumn52(currentNonFatalI.getDestinationPatientId().getDestinationPatientName());
            }
        } catch (Exception e) {
        }
        //******input_timestamp
        try {
            if (currentNonFatalI.getInputTimestamp() != null) {
                newRowDataTable.setColumn53(sdf2.format(currentNonFatalI.getInputTimestamp()));
            }
        } catch (Exception e) {
        }
        //******health_professional_id
        try {
            if (currentNonFatalI.getHealthProfessionalId() != null) {
                newRowDataTable.setColumn54(currentNonFatalI.getHealthProfessionalId().getHealthProfessionalName());
            }
        } catch (Exception e) {
        }
        //******non_fatal_data_source_id
        //******mechanism_id
        try {
            if (currentNonFatalI.getMechanismId() != null) {
                newRowDataTable.setColumn55(currentNonFatalI.getMechanismId().getMechanismName());
            }
        } catch (Exception e) {
        }
        //******user_id
        try {
            if (currentNonFatalI.getUserId() != null) {
                newRowDataTable.setColumn56(currentNonFatalI.getUserId().getUserName());
            }
        } catch (Exception e) {
        }
        //******injury_day_of_week
        try {
            if (currentNonFatalI.getInjuryDayOfWeek() != null) {
                newRowDataTable.setColumn57(currentNonFatalI.getInjuryDayOfWeek());
            }
        } catch (Exception e) {
        }
        //******non_fatal_data_source_id
        try {
            if (currentNonFatalI.getNonFatalDataSourceId() != null) {
                newRowDataTable.setColumn58(currentNonFatalI.getNonFatalDataSourceId().getNonFatalDataSourceName());
            }
        } catch (Exception e) {
        }
        //******injury_id
        try {
            if (currentNonFatalI.getInjuryId() != null) {
                //if (injuriesFacade.find(currentNonFatalI.getInjuryId().getInjuryId()) != null) {
                newRowDataTable.setColumn59(injuriesFacade.find(currentNonFatalI.getInjuryId().getInjuryId()).getInjuryName());
                //}
            }
        } catch (Exception e) {
            System.out.println("Error por" + e.toString());
        }
        //------------------------------------------------------------
        //SE CARGA VARIABLE PARA VIOLENCIA INTERPERSONAL
        //-----------------------------------------------------------

        try {
            if (currentNonFatalI.getNonFatalInterpersonal() != null) {
                if (currentNonFatalI.getNonFatalInterpersonal().getPreviousAntecedent() != null) {
                    newRowDataTable.setColumn60(currentNonFatalI.getNonFatalInterpersonal().getPreviousAntecedent().getBooleanName());
                }
            }
        } catch (Exception e) {
        }
        try {
            if (currentNonFatalI.getNonFatalInterpersonal() != null) {
                if (currentNonFatalI.getNonFatalInterpersonal().getRelationshipVictimId() != null) {
                    newRowDataTable.setColumn61(currentNonFatalI.getNonFatalInterpersonal().getRelationshipVictimId().getRelationshipVictimName());
                }
            }
        } catch (Exception e) {
        }

        try {
            if (currentNonFatalI.getNonFatalInterpersonal() != null) {
                if (currentNonFatalI.getNonFatalInterpersonal().getContextId() != null) {
                    newRowDataTable.setColumn62(currentNonFatalI.getNonFatalInterpersonal().getContextId().getContextName());
                }
            }
        } catch (Exception e) {
        }
        try {
            if (currentNonFatalI.getNonFatalInterpersonal() != null) {
                if (currentNonFatalI.getNonFatalInterpersonal().getRelationshipVictimId() != null) {
                    newRowDataTable.setColumn63(currentNonFatalI.getNonFatalInterpersonal().getAggressorGenderId().getGenderName());
                }
            }
        } catch (Exception e) {
        }
        //------------------------------------------------------------
        //SE CARGA DATOS PARA VIOLENCIA INTRAFAMILIAR
        //------------------------------------------------------------
        //cargo la lista de agresores-----------------------------------
        try {
            if (currentNonFatalI.getNonFatalDomesticViolence() != null) {
                if (currentNonFatalI.getNonFatalDomesticViolence().getAggressorTypesList() != null) {
                    List<AggressorTypes> aggressorTypesList = currentNonFatalI.getNonFatalDomesticViolence().getAggressorTypesList();
                    for (int i = 0; i < aggressorTypesList.size(); i++) {
                        int caso = (int) aggressorTypesList.get(i).getAggressorTypeId();
                        switch (caso) {
                            case 1://isAG1
                                newRowDataTable.setColumn64("SI");
                                break;
                            case 2://isAG2
                                newRowDataTable.setColumn65("SI");
                                break;
                            case 3://isAG3
                                newRowDataTable.setColumn66("SI");
                                break;
                            case 4://isAG4
                                newRowDataTable.setColumn67("SI");
                                break;
                            case 5://isAG5
                                newRowDataTable.setColumn68("SI");
                                break;
                            case 6://isAG6
                                newRowDataTable.setColumn69("SI");
                                break;
                            case 7://isAG7
                                newRowDataTable.setColumn70("SI");
                                break;
                            case 8://isAG8
                                newRowDataTable.setColumn71("SI");
                                break;
                            case 9://isUnknownAG
                                newRowDataTable.setColumn72("SI");
                                break;
                            case 10://isAG10
                                newRowDataTable.setColumn73("SI");
                                break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            //System.out.println("no se cargo violencia intrafamiliar"+e.toString());
        }
        //cargo la lista de abusos(tipos de maltrato)-----------------------------------
        try {
            if (currentNonFatalI.getNonFatalDomesticViolence() != null) {
                if (currentNonFatalI.getNonFatalDomesticViolence().getAbuseTypesList() != null) {
                    List<AbuseTypes> abuseTypesList = currentNonFatalI.getNonFatalDomesticViolence().getAbuseTypesList();
                    for (int i = 0; i < abuseTypesList.size(); i++) {
                        int caso = (int) abuseTypesList.get(i).getAbuseTypeId();
                        switch (caso) {
                            case 1://isMA1
                                newRowDataTable.setColumn74("SI");
                                break;
                            case 2://isMA2
                                newRowDataTable.setColumn75("SI");
                                break;
                            case 3://isMA3
                                newRowDataTable.setColumn76("SI");
                                break;
                            case 4://isMA4
                                newRowDataTable.setColumn77("SI");
                                break;
                            case 5://isMA5
                                newRowDataTable.setColumn78("SI");
                                break;
                            case 6://isMA6
                                newRowDataTable.setColumn79("SI");
                                break;
                            case 7://isUnknowMA
                                newRowDataTable.setColumn80("SI");
                                break;
                            case 8://isMA8
                                newRowDataTable.setColumn81("SI");
                                break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            //System.out.println("no se cargo tipos de maltrato"+e.toString());
        }

        //cargo la lista de abusos(tipos de maltrato)-----------------------------------
        try {

            if (currentNonFatalI.getAnatomicalLocationsList() != null) {
                List<AnatomicalLocations> anatomicalLocationsList = currentNonFatalI.getAnatomicalLocationsList();
                for (int i = 0; i < anatomicalLocationsList.size(); i++) {
                    int caso = (int) anatomicalLocationsList.get(i).getAnatomicalLocationId();
                    switch (caso) {
                        case 1://isAnatomicalSite1
                            newRowDataTable.setColumn82("SI");
                            break;
                        case 2://isAnatomicalSite1
                            newRowDataTable.setColumn83("SI");
                            break;
                        case 3://isAnatomicalSite1
                            newRowDataTable.setColumn84("SI");
                            break;
                        case 4://isAnatomicalSite1
                            newRowDataTable.setColumn85("SI");
                            break;
                        case 5://isAnatomicalSite1
                            newRowDataTable.setColumn86("SI");
                            break;
                        case 6://isAnatomicalSite1
                            newRowDataTable.setColumn87("SI");
                            break;
                        case 7://isAnatomicalSite1
                            newRowDataTable.setColumn88("SI");
                            break;
                        case 8://isAnatomicalSite1
                            newRowDataTable.setColumn89("SI");
                            break;
                        case 9://isAnatomicalSite1
                            newRowDataTable.setColumn90("SI");
                            break;
                        case 10://isAnatomicalSite1
                            newRowDataTable.setColumn91("SI");
                            break;
                        case 11://isAnatomicalSite1    
                            newRowDataTable.setColumn92("SI");
                            break;
                        case 98://checkOtherPlace  otherAnatomicalPlaceDisabled 
                            newRowDataTable.setColumn93("SI");
                            break;
                    }
                }
            }
        } catch (Exception e) {
            //System.out.println("no se cargo sitios anatomicos"+e.toString());
        }
        //cargo la naturaleza de la lesion
        try {

            if (currentNonFatalI.getKindsOfInjuryList() != null) {
                List<KindsOfInjury> kindsOfInjuryList = currentNonFatalI.getKindsOfInjuryList();
                for (int i = 0; i < kindsOfInjuryList.size(); i++) {
                    int caso = (int) kindsOfInjuryList.get(i).getKindInjuryId();
                    switch (caso) {
                        case 1://isNatureOfInjurye1
                            newRowDataTable.setColumn94("SI");
                            break;
                        case 2://isNatureOfInjurye1
                            newRowDataTable.setColumn95("SI");
                            break;
                        case 3://isNatureOfInjurye1
                            newRowDataTable.setColumn96("SI");
                            break;
                        case 4://isNatureOfInjurye1
                            newRowDataTable.setColumn97("SI");
                            break;
                        case 5://isNatureOfInjurye1
                            newRowDataTable.setColumn98("SI");
                            break;
                        case 6://isNatureOfInjurye1
                            newRowDataTable.setColumn99("SI");
                            break;
                        case 7://isNatureOfInjurye1
                            newRowDataTable.setColumn100("SI");
                            break;
                        case 8://isNatureOfInjurye1
                            newRowDataTable.setColumn101("SI");
                            break;
                        case 9://isNatureOfInjurye1
                            newRowDataTable.setColumn102("SI");
                            break;
                        case 98://checkOtherInjury
                            newRowDataTable.setColumn103("SI");
                            break;
                        case 99:// isUnknownNatureOfInjurye
                            newRowDataTable.setColumn104("SI");
                            break;
                    }
                }
            }
        } catch (Exception e) {
            //System.out.println("no se cargo naturaleza de la lesion"+e.toString());
        }
        //cargo los diagnosticos
        try {

            if (currentNonFatalI.getDiagnosesList() != null) {
                List<Diagnoses> diagnosesList = currentNonFatalI.getDiagnosesList();
                for (int i = 0; i < diagnosesList.size(); i++) {
                    switch (i) {
                        case 0:
                            newRowDataTable.setColumn105(diagnosesList.get(i).getDiagnosisName());
                            break;
                        case 1:
                            newRowDataTable.setColumn106(diagnosesList.get(i).getDiagnosisName());
                            break;
                        case 2:
                            newRowDataTable.setColumn107(diagnosesList.get(i).getDiagnosisName());
                            break;
                        case 3:
                            newRowDataTable.setColumn108(diagnosesList.get(i).getDiagnosisName());
                            break;
                    }
                }
            }
        } catch (Exception e) {
            //System.out.println("no se cargo codigo CIE"+e.toString());
        }

        //------------------------------------------------------------
        //AUTOINFLINGIDA INTENCIONAL
        //------------------------------------------------------------

        try {

            if (currentNonFatalI.getNonFatalSelfInflicted() != null) {
                if (currentNonFatalI.getNonFatalSelfInflicted().getPreviousAttempt() != null) {
                    newRowDataTable.setColumn109(currentNonFatalI.getNonFatalSelfInflicted().getPreviousAttempt().getBooleanName());
                }
            }
        } catch (Exception e) {
        }

        try {
            if (currentNonFatalI.getNonFatalSelfInflicted() != null) {
                if (currentNonFatalI.getNonFatalSelfInflicted().getMentalAntecedent() != null) {
                    newRowDataTable.setColumn110(currentNonFatalI.getNonFatalSelfInflicted().getMentalAntecedent().getBooleanName());
                }
            }
        } catch (Exception e) {
        }

        try {
            if (currentNonFatalI.getNonFatalSelfInflicted() != null) {
                if (currentNonFatalI.getNonFatalSelfInflicted().getPrecipitatingFactorId() != null) {
                    newRowDataTable.setColumn111(currentNonFatalI.getNonFatalSelfInflicted().getPrecipitatingFactorId().getPrecipitatingFactorName());
                }
            }
        } catch (Exception e) {
        }

        //------------------------------------------------------------
        //SE CARGA DATOS PARA TRANSITO
        //------------------------------------------------------------

        try {
            if (currentNonFatalI.getNonFatalTransport() != null) {
                if (currentNonFatalI.getNonFatalTransport().getTransportTypeId() != null) {
                    newRowDataTable.setColumn112(currentNonFatalI.getNonFatalTransport().getTransportTypeId().getTransportTypeName());
                }
            }
        } catch (Exception e) {
        }

        try {
            if (currentNonFatalI.getNonFatalTransport() != null) {
                if (currentNonFatalI.getNonFatalTransport().getTransportCounterpartId() != null) {
                    newRowDataTable.setColumn113(currentNonFatalI.getNonFatalTransport().getTransportCounterpartId().getTransportCounterpartName());
                }
            }
        } catch (Exception e) {
        }

        try {
            if (currentNonFatalI.getNonFatalTransport() != null) {
                if (currentNonFatalI.getNonFatalTransport().getTransportUserId() != null) {
                    newRowDataTable.setColumn114(currentNonFatalI.getNonFatalTransport().getTransportUserId().getTransportUserName());
                }
            }
        } catch (Exception e) {
        }


        try {
            if (currentNonFatalI.getNonFatalTransport() != null) {
                if (currentNonFatalI.getNonFatalTransport().getSecurityElementsList() != null) {
                    List<SecurityElements> securityElementsList = currentNonFatalI.getNonFatalTransport().getSecurityElementsList();
                    for (int i = 0; i < securityElementsList.size(); i++) {
                        switch (securityElementsList.get(i).getSecurityElementId()) {
                            case 1://isBeltUse
                                newRowDataTable.setColumn115("SI");
                                break;
                            case 2://isHelmetUse
                                newRowDataTable.setColumn116("SI");
                                break;
                            case 3://isBicycleHelmetUse
                                newRowDataTable.setColumn117("SI");
                                break;
                            case 4://isVestUse
                                newRowDataTable.setColumn118("SI");
                                break;
                            case 5://isOtherElementUse                        
                                break;
                            case 6://currentSecurityElements  "NO";
                                break;
                            case 7://currentSecurityElements = "NO SE SABE";
                                break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            //System.out.println("no se cargo elementos de seguridad"+e.toString());
        }

        return newRowDataTable;
    }

    public void load() {
        currentNonFatalInjury = null;
        btnEditDisabled = true;
        btnRemoveDisabled = true;
        if (selectedRowsDataTable != null) {
            if (selectedRowsDataTable.length == 1) {
                currentNonFatalInjury = nonFatalInjuriesFacade.find(Integer.parseInt(selectedRowsDataTable[0].getColumn1()));
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
            }
            //quito los elementos seleccionados de rowsDataTableList seleccion de 
            for (int j = 0; j < selectedRowsDataTable.length; j++) {
                for (int i = 0; i < rowDataTableList.size(); i++) {
                    if (selectedRowsDataTable[j].getColumn1().compareTo(rowDataTableList.get(i).getColumn1()) == 0) {
                        rowDataTableList.remove(i);
                        break;
                    }
                }
            }
            //deselecciono los controles
            selectedRowsDataTable = null;
            btnEditDisabled = true;
            btnRemoveDisabled = true;
            printMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se ha realizado la eliminacion de los registros seleccionados");
        }
    }

    public List<RowDataTable> getRowDataTableList() {
        return rowDataTableList;
    }

    public void setRowDataTableList(List<RowDataTable> rowDataTableList) {
        this.rowDataTableList = rowDataTableList;
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
}
