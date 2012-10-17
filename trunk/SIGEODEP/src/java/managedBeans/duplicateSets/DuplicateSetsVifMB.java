/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.duplicateSets;

import managedBeans.recordSets.*;
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
import managedBeans.forms.VIFMB;
import model.dao.*;
import model.pojo.*;
import org.apache.poi.hssf.usermodel.*;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "duplicateSetsVifMB")
@SessionScoped
public class DuplicateSetsVifMB implements Serializable {

    //--------------------
    @EJB
    TagsFacade tagsFacade;
    private List<Tags> tagsList;
    private Tags currentTag;
    private NonFatalDomesticViolence currentNonFatalDomesticViolence;
//    @EJB
//    NonFatalDomesticViolenceFacade nonFatalDomesticViolenceFacade;
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
    private VIFMB vifMB;
    private String openForm = "";
    private RecordSetsMB recordSetsMB;

    public DuplicateSetsVifMB() {
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

    public void loadValues(RowDataTable[] selectedRowsDataTableTags) {
                
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
        List<NonFatalDomesticViolence> nonFatalDomesticViolenceList;
        for (int i = 0; i < tagsList.size(); i++) {
            nonFatalDomesticViolenceList = nonFatalDomesticViolenceFacade.findFromTag(tagsList.get(i).getTagId());
            if (nonFatalDomesticViolenceList != null) {
                for (int j = 0; j < nonFatalDomesticViolenceList.size(); j++) {
                    rowDataTableList.add(loadValues(nonFatalDomesticViolenceList.get(j)));
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
        createCell(cellStyle, row, 0, "CODIGO INTERNO");//50">#{rowX.column1}</p:column>
        createCell(cellStyle, row, 1, "INSTITUCION RECEPTORA");//50">#{rowX.column80}</p:column>
        createCell(cellStyle, row, 2, "NOMBRES Y APELLIDOS");//400">#{rowX.column4}</p:column>                                                
        createCell(cellStyle, row, 3, "TIPO IDENTIFICACION");//200">#{rowX.column2}</p:column>
        createCell(cellStyle, row, 4, "IDENTIFICACION");//100">#{rowX.column3}</p:column>                
        createCell(cellStyle, row, 5, "TIP EDAD");//100">#{rowX.column6}</p:column>                
        createCell(cellStyle, row, 6, "EDAD CANT");//100">#{rowX.column7}</p:column>                
        createCell(cellStyle, row, 7, "GENERO");//100">#{rowX.column8}</p:column>                
        createCell(cellStyle, row, 8, "OCUPACION");//100">#{rowX.column9}</p:column>
        createCell(cellStyle, row, 9, "ASEGURADORA");//300">#{rowX.column18}</p:column>
        createCell(cellStyle, row, 10, "EXTRANJERO");//100">#{rowX.column5}</p:column>  
        createCell(cellStyle, row, 11, "DEPARTAMENTO RESIDENCIA");//100">#{rowX.column13}</p:column>
        createCell(cellStyle, row, 11, "MUNICIPIO RESIDENCIA");//100">#{rowX.column12}</p:column>
        createCell(cellStyle, row, 12, "BARRIO RESIDENCIA");//250">#{rowX.column15}</p:column>
        createCell(cellStyle, row, 13, "DIRECCION RESIDENCIA");//400">#{rowX.column14}</p:column>
        createCell(cellStyle, row, 14, "TELEFONO");//100">#{rowX.column11}</p:column>

        createCell(cellStyle, row, 15, "BARRIO EVENTO");//250">#{rowX.column36}</p:column>
        createCell(cellStyle, row, 16, "DIRECCION EVENTO");//400">#{rowX.column35}</p:column>


        //------------------------------------------------------------
        //SE CARGAN VARIABLES LESION DE CAUSA EXTERNA NO FATAL
        //------------------------------------------------------------        
        createCell(cellStyle, row, 17, "FECHA EVENTO");//100">#{rowX.column33}</p:column>
        createCell(cellStyle, row, 18, "HORA EVENTO");//100">#{rowX.column48}</p:column>
        createCell(cellStyle, row, 19, "DIA SEMANA EVENTO");//100">#{rowX.column57}</p:column>
        createCell(cellStyle, row, 20, "FECHA CONSULTA");//100">#{rowX.column31}</p:column>
        createCell(cellStyle, row, 21, "HORA CONSULTA");//100">#{rowX.column32}</p:column>
        createCell(cellStyle, row, 22, "REMITIDO");//100">#{rowX.column43}</p:column>
        createCell(cellStyle, row, 23, "REMITIDO DE DONDE");//250">#{rowX.column44}</p:column>

        createCell(cellStyle, row, 24, "LUGAR DEL HECHO");//200">#{rowX.column37}</p:column>
        createCell(cellStyle, row, 25, "OTRO LUGAR DEL HECHO");//200">#{rowX.column19}</p:column>
        createCell(cellStyle, row, 26, "ACTIVIDAD");//250">#{rowX.column38}</p:column>
        createCell(cellStyle, row, 27, "OTRA ACTIVIDAD");//250">#{rowX.column20}</p:column>
        createCell(cellStyle, row, 28, "MECANISMO / OBJETO DE LA LESION");//200">#{rowX.column46}</p:column>
        createCell(cellStyle, row, 29, "CUAL ALTURA");//100">#{rowX.column21}</p:column>
        createCell(cellStyle, row, 30, "CUAL POLVORA");//100">#{rowX.column22}</p:column>                                
        createCell(cellStyle, row, 31, "CUAL OTRO MECANISMO / OBJETO");//100">#{rowX.column24}</p:column>                                
        createCell(cellStyle, row, 32, "USO DE ALCOHOL");//150">#{rowX.column39}</p:column>
        createCell(cellStyle, row, 33, "USO DE DROGAS");//150">#{rowX.column40}</p:column>

        createCell(cellStyle, row, 34, "GRADO (QUEMADOS)");//100">#{rowX.column41}</p:column>
        createCell(cellStyle, row, 35, "PORCENTAJE(QUEMADOS)");//100">#{rowX.column42}</p:column>


        createCell(cellStyle, row, 36, "GRUPO ETNICO");//100">#{rowX.column10}</p:column>
        createCell(cellStyle, row, 37, "OTRO GRUPO ETNICO");//100">#{rowX.column26}</p:column>
        createCell(cellStyle, row, 38, "GRUPO VULNERABLE");//100">#{rowX.column16}</p:column>
        createCell(cellStyle, row, 39, "OTRO GRUPO VULNERABLE");//100">#{rowX.column27}</p:column>


        //tipo de agresor
        createCell(cellStyle, row, 40, "AG1 PADRE(VIF)");//100">#{rowX.column49}</p:column>
        createCell(cellStyle, row, 41, "AG2 MADRE(VIF)");//100">#{rowX.column50}</p:column>
        createCell(cellStyle, row, 42, "AG3 PADRASTRO(VIF)");//100">#{rowX.column51}</p:column>
        createCell(cellStyle, row, 43, "AG4 MADRASTRA(VIF)");//100">#{rowX.column52}</p:column>
        createCell(cellStyle, row, 44, "AG5 CONYUGE(VIF)");//100">#{rowX.column53}</p:column>
        createCell(cellStyle, row, 45, "AG6 HERMANO(VIF)");//100">#{rowX.column54}</p:column>
        createCell(cellStyle, row, 46, "AG7 HIJO(VIF)");//100">#{rowX.column55}</p:column>
        createCell(cellStyle, row, 47, "AG8 OTRO(VIF)");//100">#{rowX.column56}</p:column>
        createCell(cellStyle, row, 48, "CUAL OTRO AGRESOR(VIF)");//100">#{rowX.column28}</p:column>
        createCell(cellStyle, row, 49, "AG9 SIN DATO(VIF)");//100">#{rowX.column57}</p:column>
        createCell(cellStyle, row, 50, "AG10 NOVIO(VIF)");//100">#{rowX.column58}</p:column>                                

        //tipo de maltrato
        createCell(cellStyle, row, 51, "MA1 FISICO(VIF)");//100">#{rowX.column59}</p:column>
        createCell(cellStyle, row, 52, "MA2 PSICOLOGICO(VIF)");//100">#{rowX.column60}</p:column>
        createCell(cellStyle, row, 53, "MA3 VIOLENCIA SEXUAL(VIF)");//100">#{rowX.column61}</p:column>
        createCell(cellStyle, row, 54, "MA4 NEGLIGENCIA(VIF)");//100">#{rowX.column62}</p:column>
        createCell(cellStyle, row, 55, "MA5 ABANDONO(VIF)");//100">#{rowX.column63}</p:column>
        createCell(cellStyle, row, 56, "MA6 INSTITUCIONAL(VIF)");//100">#{rowX.column64}</p:column>
        createCell(cellStyle, row, 57, "MA SIN DATO(VIF)");//100">#{rowX.column65}</p:column>
        createCell(cellStyle, row, 58, "MA8 OTRO(VIF)");//100">#{rowX.column66}</p:column>
        createCell(cellStyle, row, 59, "CUAL OTRO TIPO MALTRATO(VIF)");//100">#{rowX.column29}</p:column>

        //acciones a realizar
        createCell(cellStyle, row, 60, "AR1 CONCILIACION");//100">#{rowX.column67}</p:column>
        createCell(cellStyle, row, 61, "AR2 CAUCION");//100">#{rowX.column68}</p:column>
        createCell(cellStyle, row, 62, "AR3 DICTAMEN MEDICINA LEGAL");//100">#{rowX.column69}</p:column>
        createCell(cellStyle, row, 63, "AR4 REMISION FISCALIA");//100">#{rowX.column70}</p:column>
        createCell(cellStyle, row, 64, "AR5 REMISION MEDICINA LEGAL");//100">#{rowX.column71}</p:column>
        createCell(cellStyle, row, 65, "AR6 REMISION COM FAMILIA");//100">#{rowX.column72}</p:column>
        createCell(cellStyle, row, 66, "AR7 REMISION ICBF");//100">#{rowX.column73}</p:column>
        createCell(cellStyle, row, 67, "AR8 MEDIDAS PROTECCION");//100">#{rowX.column74}</p:column>
        createCell(cellStyle, row, 68, "AR9 REMISION A SALUD");//100">#{rowX.column75}</p:column>
        createCell(cellStyle, row, 69, "AR10 ATENCION PSICOSOCIAL");//100">#{rowX.column76}</p:column>
        createCell(cellStyle, row, 70, "AR11 RESTABLECIMIENTO DERECHOS");//100">#{rowX.column77}</p:column>
        createCell(cellStyle, row, 71, "AR12 OTRA?");//100">#{rowX.column78}</p:column>
        createCell(cellStyle, row, 72, "AR CUAL OTRA");//100">#{rowX.column30}</p:column>
        createCell(cellStyle, row, 73, "AR13 SIN DATO");//100">#{rowX.column79}</p:column>



        for (int i = 0; i < rowDataTableList.size(); i++) {
            row = sheet.createRow(i + 1);

            createCell(row, 0, rowDataTableList.get(i).getColumn1());//"CODIGO INTERNO");//50">#{rowX.column1}</p:column>
            createCell(row, 1, rowDataTableList.get(i).getColumn80());//"INSTITUCION RECEPTORA");//50">#{rowX.column80}</p:column>
            createCell(row, 2, rowDataTableList.get(i).getColumn4());//"NOMBRES Y APELLIDOS");//400">#{rowX.column4}</p:column>                                                
            createCell(row, 3, rowDataTableList.get(i).getColumn2());//"TIPO IDENTIFICACION");//200">#{rowX.column2}</p:column>
            createCell(row, 4, rowDataTableList.get(i).getColumn3());//"IDENTIFICACION");//100">#{rowX.column3}</p:column>                
            createCell(row, 5, rowDataTableList.get(i).getColumn6());//"TIP EDAD");//100">#{rowX.column6}</p:column>                
            createCell(row, 6, rowDataTableList.get(i).getColumn7());//"EDAD CANT");//100">#{rowX.column7}</p:column>                
            createCell(row, 7, rowDataTableList.get(i).getColumn8());//"GENERO");//100">#{rowX.column8}</p:column>                
            createCell(row, 8, rowDataTableList.get(i).getColumn9());//"OCUPACION");//100">#{rowX.column9}</p:column>
            createCell(row, 9, rowDataTableList.get(i).getColumn18());//"ASEGURADORA");//300">#{rowX.column18}</p:column>
            createCell(row, 10, rowDataTableList.get(i).getColumn5());//"EXTRANJERO");//100">#{rowX.column5}</p:column>  
            createCell(row, 11, rowDataTableList.get(i).getColumn13());//"DEPARTAMENTO RESIDENCIA");//100">#{rowX.column13}</p:column>
            createCell(row, 11, rowDataTableList.get(i).getColumn12());//"MUNICIPIO RESIDENCIA");//100">#{rowX.column12}</p:column>
            createCell(row, 12, rowDataTableList.get(i).getColumn15());//"BARRIO RESIDENCIA");//250">#{rowX.column15}</p:column>
            createCell(row, 13, rowDataTableList.get(i).getColumn14());//"DIRECCION RESIDENCIA");//400">#{rowX.column14}</p:column>
            createCell(row, 14, rowDataTableList.get(i).getColumn11());//"TELEFONO");//100">#{rowX.column11}</p:column>

            createCell(row, 15, rowDataTableList.get(i).getColumn36());//"BARRIO EVENTO");//250">#{rowX.column36}</p:column>
            createCell(row, 16, rowDataTableList.get(i).getColumn35());//"DIRECCION EVENTO");//400">#{rowX.column35}</p:column>


            //------------------------------------------------------------
            //SE CARGAN VARIABLES LESION DE CAUSA EXTERNA NO FATAL
            //------------------------------------------------------------        
            createCell(row, 17, rowDataTableList.get(i).getColumn33());//"FECHA EVENTO");//100">#{rowX.column33}</p:column>
            createCell(row, 18, rowDataTableList.get(i).getColumn48());//"HORA EVENTO");//100">#{rowX.column48}</p:column>
            createCell(row, 19, rowDataTableList.get(i).getColumn57());//"DIA SEMANA EVENTO");//100">#{rowX.column57}</p:column>
            createCell(row, 20, rowDataTableList.get(i).getColumn31());//"FECHA CONSULTA");//100">#{rowX.column31}</p:column>
            createCell(row, 21, rowDataTableList.get(i).getColumn32());//"HORA CONSULTA");//100">#{rowX.column32}</p:column>
            createCell(row, 22, rowDataTableList.get(i).getColumn43());//"REMITIDO");//100">#{rowX.column43}</p:column>
            createCell(row, 23, rowDataTableList.get(i).getColumn44());//"REMITIDO DE DONDE");//250">#{rowX.column44}</p:column>

            createCell(row, 24, rowDataTableList.get(i).getColumn37());//"LUGAR DEL HECHO");//200">#{rowX.column37}</p:column>
            createCell(row, 25, rowDataTableList.get(i).getColumn19());//"OTRO LUGAR DEL HECHO");//200">#{rowX.column19}</p:column>
            createCell(row, 26, rowDataTableList.get(i).getColumn38());//"ACTIVIDAD");//250">#{rowX.column38}</p:column>
            createCell(row, 27, rowDataTableList.get(i).getColumn20());//"OTRA ACTIVIDAD");//250">#{rowX.column20}</p:column>
            createCell(row, 28, rowDataTableList.get(i).getColumn46());//"MECANISMO / OBJETO DE LA LESION");//200">#{rowX.column46}</p:column>
            createCell(row, 29, rowDataTableList.get(i).getColumn21());//"CUAL ALTURA");//100">#{rowX.column21}</p:column>
            createCell(row, 30, rowDataTableList.get(i).getColumn22());//"CUAL POLVORA");//100">#{rowX.column22}</p:column>                                
            createCell(row, 31, rowDataTableList.get(i).getColumn24());//"CUAL OTRO MECANISMO / OBJETO");//100">#{rowX.column24}</p:column>                                
            createCell(row, 32, rowDataTableList.get(i).getColumn39());//"USO DE ALCOHOL");//150">#{rowX.column39}</p:column>
            createCell(row, 33, rowDataTableList.get(i).getColumn40());//"USO DE DROGAS");//150">#{rowX.column40}</p:column>

            createCell(row, 34, rowDataTableList.get(i).getColumn41());//"GRADO (QUEMADOS)");//100">#{rowX.column41}</p:column>
            createCell(row, 35, rowDataTableList.get(i).getColumn42());//"PORCENTAJE(QUEMADOS)");//100">#{rowX.column42}</p:column>


            createCell(row, 36, rowDataTableList.get(i).getColumn10());//"GRUPO ETNICO");//100">#{rowX.column10}</p:column>
            createCell(row, 37, rowDataTableList.get(i).getColumn26());//"OTRO GRUPO ETNICO");//100">#{rowX.column26}</p:column>
            createCell(row, 38, rowDataTableList.get(i).getColumn16());//"GRUPO VULNERABLE");//100">#{rowX.column16}</p:column>
            createCell(row, 39, rowDataTableList.get(i).getColumn27());//"OTRO GRUPO VULNERABLE");//100">#{rowX.column27}</p:column>


            //tipo de agresor
            createCell(row, 40, rowDataTableList.get(i).getColumn49());//"AG1 PADRE(VIF)");//100">#{rowX.column49}</p:column>
            createCell(row, 41, rowDataTableList.get(i).getColumn50());//"AG2 MADRE(VIF)");//100">#{rowX.column50}</p:column>
            createCell(row, 42, rowDataTableList.get(i).getColumn51());//"AG3 PADRASTRO(VIF)");//100">#{rowX.column51}</p:column>
            createCell(row, 43, rowDataTableList.get(i).getColumn52());//"AG4 MADRASTRA(VIF)");//100">#{rowX.column52}</p:column>
            createCell(row, 44, rowDataTableList.get(i).getColumn53());//"AG5 CONYUGE(VIF)");//100">#{rowX.column53}</p:column>
            createCell(row, 45, rowDataTableList.get(i).getColumn54());//"AG6 HERMANO(VIF)");//100">#{rowX.column54}</p:column>
            createCell(row, 46, rowDataTableList.get(i).getColumn55());//"AG7 HIJO(VIF)");//100">#{rowX.column55}</p:column>
            createCell(row, 47, rowDataTableList.get(i).getColumn56());//"AG8 OTRO(VIF)");//100">#{rowX.column56}</p:column>
            createCell(row, 48, rowDataTableList.get(i).getColumn28());//"CUAL OTRO AGRESOR(VIF)");//100">#{rowX.column28}</p:column>
            createCell(row, 49, rowDataTableList.get(i).getColumn57());//"AG9 SIN DATO(VIF)");//100">#{rowX.column57}</p:column>
            createCell(row, 50, rowDataTableList.get(i).getColumn58());//"AG10 NOVIO(VIF)");//100">#{rowX.column58}</p:column>                                

            //tipo de maltrato
            createCell(row, 51, rowDataTableList.get(i).getColumn59());//"MA1 FISICO(VIF)");//100">#{rowX.column59}</p:column>
            createCell(row, 52, rowDataTableList.get(i).getColumn60());//"MA2 PSICOLOGICO(VIF)");//100">#{rowX.column60}</p:column>
            createCell(row, 53, rowDataTableList.get(i).getColumn61());//"MA3 VIOLENCIA SEXUAL(VIF)");//100">#{rowX.column61}</p:column>
            createCell(row, 54, rowDataTableList.get(i).getColumn62());//"MA4 NEGLIGENCIA(VIF)");//100">#{rowX.column62}</p:column>
            createCell(row, 55, rowDataTableList.get(i).getColumn62());//"MA5 ABANDONO(VIF)");//100">#{rowX.column63}</p:column>
            createCell(row, 56, rowDataTableList.get(i).getColumn64());//"MA6 INSTITUCIONAL(VIF)");//100">#{rowX.column64}</p:column>
            createCell(row, 57, rowDataTableList.get(i).getColumn65());//"MA SIN DATO(VIF)");//100">#{rowX.column65}</p:column>
            createCell(row, 58, rowDataTableList.get(i).getColumn66());//"MA8 OTRO(VIF)");//100">#{rowX.column66}</p:column>
            createCell(row, 59, rowDataTableList.get(i).getColumn29());//"CUAL OTRO TIPO MALTRATO(VIF)");//100">#{rowX.column29}</p:column>

            //acciones a realizar
            createCell(row, 60, rowDataTableList.get(i).getColumn67());//"AR1 CONCILIACION");//100">#{rowX.column67}</p:column>
            createCell(row, 61, rowDataTableList.get(i).getColumn68());//"AR2 CAUCION");//100">#{rowX.column68}</p:column>
            createCell(row, 62, rowDataTableList.get(i).getColumn69());//"AR3 DICTAMEN MEDICINA LEGAL");//100">#{rowX.column69}</p:column>
            createCell(row, 63, rowDataTableList.get(i).getColumn70());//"AR4 REMISION FISCALIA");//100">#{rowX.column70}</p:column>
            createCell(row, 64, rowDataTableList.get(i).getColumn71());//"AR5 REMISION MEDICINA LEGAL");//100">#{rowX.column71}</p:column>
            createCell(row, 65, rowDataTableList.get(i).getColumn72());//"AR6 REMISION COM FAMILIA");//100">#{rowX.column72}</p:column>
            createCell(row, 66, rowDataTableList.get(i).getColumn73());//"AR7 REMISION ICBF");//100">#{rowX.column73}</p:column>
            createCell(row, 67, rowDataTableList.get(i).getColumn74());//"AR8 MEDIDAS PROTECCION");//100">#{rowX.column74}</p:column>
            createCell(row, 68, rowDataTableList.get(i).getColumn75());//"AR9 REMISION A SALUD");//100">#{rowX.column75}</p:column>
            createCell(row, 69, rowDataTableList.get(i).getColumn76());//"AR10 ATENCION PSICOSOCIAL");//100">#{rowX.column76}</p:column>
            createCell(row, 70, rowDataTableList.get(i).getColumn77());//"AR11 RESTABLECIMIENTO DERECHOS");//100">#{rowX.column77}</p:column>
            createCell(row, 71, rowDataTableList.get(i).getColumn78());//"AR12 OTRA?");//100">#{rowX.column78}</p:column>
            createCell(row, 72, rowDataTableList.get(i).getColumn30());//"AR CUAL OTRA");//100">#{rowX.column30}</p:column>
            createCell(row, 73, rowDataTableList.get(i).getColumn79());//"AR13 SIN DATO");//100">#{rowX.column79}</p:column>

        }

    }

    private RowDataTable loadValues(NonFatalDomesticViolence currentNonFatalDomesticV) {
        //CARGO LOS DATOS DE UNA DETERMINA LESION NO FATAL EN UNA FILA PARA LA TABLA
        btnEditDisabled = true;
        btnRemoveDisabled = true;
        RowDataTable newRowDataTable = new RowDataTable();
        //------------------------------------------------------------
        //SE CARGAN VALORES PARA LA NUEVA VICTIMA
        //------------------------------------------------------------

        //******non_fatal_injury_id
        newRowDataTable.setColumn1(currentNonFatalDomesticV.getNonFatalInjuries().getNonFatalInjuryId().toString());
        //******type_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getTypeId() != null) {
                newRowDataTable.setColumn2(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getTypeId().getTypeName());
            }
        } catch (Exception e) {
        }
        //******victim_nid
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimNid() != null) {
                newRowDataTable.setColumn3(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimNid());
            }
        } catch (Exception e) {
        }
        //******victim_name
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimName() != null) {
                newRowDataTable.setColumn4(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimName());
            }
        } catch (Exception e) {
        }
        //******stranger
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getStranger() != null) {
                newRowDataTable.setColumn5(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getStranger().toString());
            }
        } catch (Exception e) {
        }
        //******age_type_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getAgeTypeId() != null) {
                newRowDataTable.setColumn6(ageTypesFacade.find(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getAgeTypeId()).getAgeTypeName());
            }
        } catch (Exception e) {
        }
        //******victim_age
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimAge() != null) {
                newRowDataTable.setColumn7(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimAge().toString());
            }
        } catch (Exception e) {
        }
        //******gender_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getGenderId() != null) {
                newRowDataTable.setColumn8(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getGenderId().getGenderName());
            }
        } catch (Exception e) {
        }
        //******job_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getJobId() != null) {
                newRowDataTable.setColumn9(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getJobId().getJobName());
            }
        } catch (Exception e) {
        }

        //******ethnic_group_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getEthnicGroupId() != null) {
                newRowDataTable.setColumn10(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getEthnicGroupId().getEthnicGroupName());
            }
        } catch (Exception e) {
        }
        //******victim_telephone
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimTelephone() != null) {
                newRowDataTable.setColumn11(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimTelephone());
            }
        } catch (Exception e) {
        }

        //******victim_date_of_birth
        //******eps_id
        //******victim_class
        //******victim_id        
        //******residence_municipality
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getResidenceDepartment() != null && (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getResidenceMunicipality() != null)) {
                short departamentId = currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getResidenceDepartment();
                short municipalityId = currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getResidenceMunicipality();
                MunicipalitiesPK mPk = new MunicipalitiesPK(departamentId, municipalityId);
                newRowDataTable.setColumn12(municipalitiesFacade.find(mPk).getMunicipalityName());
            }
        } catch (Exception e) {
        }
        //******residence_department
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getResidenceDepartment() != null) {
                newRowDataTable.setColumn13(departamentsFacade.find(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getResidenceDepartment()).getDepartamentName());
            }
        } catch (Exception e) {
        }
        //******victim_address
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimAddress() != null) {
                newRowDataTable.setColumn14(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimAddress());
            }
        } catch (Exception e) {
        }
        //******victim_neighborhood_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimNeighborhoodId() != null) {
                newRowDataTable.setColumn15(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVictimNeighborhoodId().getNeighborhoodName());
            }
        } catch (Exception e) {
        }
        //informacion de grupos vunerables
        if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVulnerableGroupsList() != null) {
            if (!currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVulnerableGroupsList().isEmpty()) {
                newRowDataTable.setColumn16(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getVulnerableGroupsList().get(0).getVulnerableGroupName());
            }
        }

        //******insurance_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getInsuranceId() != null) {
                newRowDataTable.setColumn18(currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getInsuranceId().getInsuranceName());
            }
        } catch (Exception e) {
        }

        //-----CARGAR CAMPOS OTROS----------------
        if (currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getOthersList() != null) {
            List<Others> othersList = currentNonFatalDomesticV.getNonFatalInjuries().getVictimId().getOthersList();
            for (int i = 0; i < othersList.size(); i++) {
                switch (othersList.get(i).getOthersPK().getFieldId()) {
                    case 1://1.	Otro lugar del hecho
                        newRowDataTable.setColumn19(othersList.get(i).getValueText());
                        break;
                    case 2://2.	Otra actividad
                        newRowDataTable.setColumn20(othersList.get(i).getValueText());
                        break;
                    case 3://3.	Altura
                        newRowDataTable.setColumn21(othersList.get(i).getValueText());
                        break;
                    case 4://4.	Cual polvora			
                        newRowDataTable.setColumn22(othersList.get(i).getValueText());
                        break;
                    case 5://5.	Cual desastre natural
                        newRowDataTable.setColumn23(othersList.get(i).getValueText());
                        break;
                    case 6://6.	Otro mecanismo objeto
                        newRowDataTable.setColumn24(othersList.get(i).getValueText());
                        break;
                    case 7://7.	Animal cual
                        newRowDataTable.setColumn25(othersList.get(i).getValueText());
                        break;
                    case 8://8.	Otro grupo étnico
                        newRowDataTable.setColumn26(othersList.get(i).getValueText());
                        break;
                    case 9://9.	Otro grupo vulnerable
                        newRowDataTable.setColumn27(othersList.get(i).getValueText());
                        break;
                    case 10://10.	Otro tipo de agresor
                        newRowDataTable.setColumn28(othersList.get(i).getValueText());
                        break;
                    case 11://11.	Otro tipo de maltrato
                        newRowDataTable.setColumn29(othersList.get(i).getValueText());
                        break;
                    case 12://12.	Otro tipo de agresion
                        newRowDataTable.setColumn30(othersList.get(i).getValueText());
                        break;
                }
            }
        }

        //------------------------------------------------------------
        //SE CARGAN VARIABLES LESION DE CAUSA EXTERNA NO FATAL
        //------------------------------------------------------------        
        //******checkup_date
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getCheckupDate() != null) {
                newRowDataTable.setColumn31(sdf.format(currentNonFatalDomesticV.getNonFatalInjuries().getCheckupDate()));
            }
        } catch (Exception e) {
        }
        //******checkup_time
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getCheckupTime() != null) {
                hours = String.valueOf(currentNonFatalDomesticV.getNonFatalInjuries().getCheckupTime().getHours());
                minutes = String.valueOf(currentNonFatalDomesticV.getNonFatalInjuries().getCheckupTime().getMinutes());
                if (hours.length() != 2) {
                    hours = "0" + hours;
                }
                if (minutes.length() != 2) {
                    minutes = "0" + minutes;
                }
                newRowDataTable.setColumn32(hours + minutes);
            }
        } catch (Exception e) {
        }
        //******injury_date
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getInjuryDate() != null) {
                newRowDataTable.setColumn33(sdf.format(currentNonFatalDomesticV.getNonFatalInjuries().getInjuryDate()));
            }
        } catch (Exception e) {
        }
        //******injury_time
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getInjuryTime() != null) {
                hours = String.valueOf(currentNonFatalDomesticV.getNonFatalInjuries().getInjuryTime().getHours());
                minutes = String.valueOf(currentNonFatalDomesticV.getNonFatalInjuries().getInjuryTime().getMinutes());
                if (hours.length() != 2) {
                    hours = "0" + hours;
                }
                if (minutes.length() != 2) {
                    minutes = "0" + minutes;
                }
                newRowDataTable.setColumn34(hours + minutes);
            }
        } catch (Exception e) {
        }
        //******injury_address
        if (currentNonFatalDomesticV.getNonFatalInjuries().getInjuryAddress() != null) {
            newRowDataTable.setColumn35(currentNonFatalDomesticV.getNonFatalInjuries().getInjuryAddress());
        }
        //******injury_neighborhood_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getInjuryNeighborhoodId() != null) {
                newRowDataTable.setColumn36(currentNonFatalDomesticV.getNonFatalInjuries().getInjuryNeighborhoodId().getNeighborhoodName());
            }
        } catch (Exception e) {
        }
        //******injury_place_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getInjuryPlaceId() != null) {
                newRowDataTable.setColumn37(currentNonFatalDomesticV.getNonFatalInjuries().getInjuryPlaceId().getNonFatalPlaceName());
            }
        } catch (Exception e) {
        }
        //******activity_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getActivityId() != null) {
                newRowDataTable.setColumn38(currentNonFatalDomesticV.getNonFatalInjuries().getActivityId().getActivityName());
            }
        } catch (Exception e) {
        }
        //******intentionality_id        
        //******use_alcohol_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getUseAlcoholId() != null) {
                newRowDataTable.setColumn39(currentNonFatalDomesticV.getNonFatalInjuries().getUseAlcoholId().getUseAlcoholDrugsName());
            }
        } catch (Exception e) {
        }
        //******use_drugs_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getUseDrugsId() != null) {
                newRowDataTable.setColumn40(currentNonFatalDomesticV.getNonFatalInjuries().getUseDrugsId().getUseAlcoholDrugsName());
            }
        } catch (Exception e) {
        }
        //******burn_injury_degree
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getBurnInjuryDegree() != null) {
                newRowDataTable.setColumn41(currentNonFatalDomesticV.getNonFatalInjuries().getBurnInjuryDegree().toString());
            }
        } catch (Exception e) {
        }
        //******burn_injury_percentage
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getBurnInjuryPercentage() != null) {
                newRowDataTable.setColumn42(currentNonFatalDomesticV.getNonFatalInjuries().getBurnInjuryPercentage().toString());
            }
        } catch (Exception e) {
        }
        //******submitted_patient

        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getSubmittedPatient() != null) {
                newRowDataTable.setColumn43(currentNonFatalDomesticV.getNonFatalInjuries().getSubmittedPatient().toString());
            }
        } catch (Exception e) {
        }
        //******eps_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getSubmittedDataSourceId() != null) {
                newRowDataTable.setColumn44(currentNonFatalDomesticV.getNonFatalInjuries().getSubmittedDataSourceId().getNonFatalDataSourceName());
            }
        } catch (Exception e) {
        }
        //******destination_patient_id        
        //******input_timestamp
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getInputTimestamp() != null) {
                newRowDataTable.setColumn45(sdf2.format(currentNonFatalDomesticV.getNonFatalInjuries().getInputTimestamp()));
            }
        } catch (Exception e) {
        }
        //******health_professional_id        
        //******non_fatal_data_source_id
        //******mechanism_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getMechanismId() != null) {
                newRowDataTable.setColumn46(currentNonFatalDomesticV.getNonFatalInjuries().getMechanismId().getMechanismName());
            }
        } catch (Exception e) {
        }
        //******user_id
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getUserId() != null) {
                newRowDataTable.setColumn47(currentNonFatalDomesticV.getNonFatalInjuries().getUserId().getUserName());
            }
        } catch (Exception e) {
        }
        //******injury_day_of_week
        try {
            if (currentNonFatalDomesticV.getNonFatalInjuries().getInjuryDayOfWeek() != null) {
                newRowDataTable.setColumn48(currentNonFatalDomesticV.getNonFatalInjuries().getInjuryDayOfWeek());
            }
        } catch (Exception e) {
        }
        //******non_fatal_data_source_id        
        //******injury_id        

        //------------------------------------------------------------
        //SE CARGA DATOS PARA VIOLENCIA INTRAFAMILIAR
        //------------------------------------------------------------
        //cargo la lista de agresores-----------------------------------
        try {
            if (currentNonFatalDomesticV.getAggressorTypesList() != null) {
                if (currentNonFatalDomesticV.getAggressorTypesList() != null) {
                    List<AggressorTypes> aggressorTypesList = currentNonFatalDomesticV.getAggressorTypesList();
                    for (int i = 0; i < aggressorTypesList.size(); i++) {
                        int caso = (int) aggressorTypesList.get(i).getAggressorTypeId();
                        switch (caso) {
                            case 1://isAG1
                                newRowDataTable.setColumn49("SI");
                                break;
                            case 2://isAG2
                                newRowDataTable.setColumn50("SI");
                                break;
                            case 3://isAG3
                                newRowDataTable.setColumn51("SI");
                                break;
                            case 4://isAG4
                                newRowDataTable.setColumn52("SI");
                                break;
                            case 5://isAG5
                                newRowDataTable.setColumn53("SI");
                                break;
                            case 6://isAG6
                                newRowDataTable.setColumn54("SI");
                                break;
                            case 7://isAG7
                                newRowDataTable.setColumn55("SI");
                                break;
                            case 8://isAG8
                                newRowDataTable.setColumn56("SI");
                                break;
                            case 9://isUnknownAG
                                newRowDataTable.setColumn57("SI");
                                break;
                            case 10://isAG10
                                newRowDataTable.setColumn58("SI");
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
            if (currentNonFatalDomesticV.getAbuseTypesList() != null) {
                if (currentNonFatalDomesticV.getAbuseTypesList() != null) {
                    List<AbuseTypes> abuseTypesList = currentNonFatalDomesticV.getAbuseTypesList();
                    for (int i = 0; i < abuseTypesList.size(); i++) {
                        int caso = (int) abuseTypesList.get(i).getAbuseTypeId();
                        switch (caso) {
                            case 1://isMA1
                                newRowDataTable.setColumn59("SI");
                                break;
                            case 2://isMA2
                                newRowDataTable.setColumn60("SI");
                                break;
                            case 3://isMA3
                                newRowDataTable.setColumn61("SI");
                                break;
                            case 4://isMA4
                                newRowDataTable.setColumn62("SI");
                                break;
                            case 5://isMA5
                                newRowDataTable.setColumn63("SI");
                                break;
                            case 6://isMA6
                                newRowDataTable.setColumn64("SI");
                                break;
                            case 7://isUnknowMA
                                newRowDataTable.setColumn65("SI");
                                break;
                            case 8://isMA8
                                newRowDataTable.setColumn66("SI");
                                break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            //System.out.println("no se cargo tipos de maltrato"+e.toString());
        }

        //cargo la lista de acciones a realizar
        try {
            if (currentNonFatalDomesticV.getActionsToTakeList() != null) {
                List<ActionsToTake> actionsToTakesList = currentNonFatalDomesticV.getActionsToTakeList();
                for (int i = 0; i < actionsToTakesList.size(); i++) {
                    int caso = (int) actionsToTakesList.get(i).getActionId();
                    switch (caso) {
                        case 1://AR1 Conciliación
                            newRowDataTable.setColumn67("SI");
                            break;
                        case 2://AR2 Caución
                            newRowDataTable.setColumn68("SI");
                            break;
                        case 3://AR3 Dictamén Medicina Legal
                            newRowDataTable.setColumn69("SI");
                            break;
                        case 4://AR4 Remisión a Fiscalía
                            newRowDataTable.setColumn70("SI");
                            break;
                        case 5://AR5 Remisión a Medicina Legal
                            newRowDataTable.setColumn71("SI");
                            break;
                        case 6://AR6 Remisión a Comisaría de Familia
                            newRowDataTable.setColumn72("SI");
                            break;
                        case 7://AR7 Remisión a ICBF
                            newRowDataTable.setColumn73("SI");
                            break;
                        case 8://AR8 Medidas de protección
                            newRowDataTable.setColumn74("SI");
                            break;
                        case 9://AR9 Remisión a salud
                            newRowDataTable.setColumn75("SI");
                            break;
                        case 10://AR10 Atención psicosocial
                            newRowDataTable.setColumn76("SI");
                            break;
                        case 11://AR11 Restablecimiento de derechos
                            newRowDataTable.setColumn77("SI");
                            break;
                        case 12://AR12 Otro, cual?
                            newRowDataTable.setColumn78("SI");
                            break;
                        case 13://AR13 Sin dato
                            newRowDataTable.setColumn79("SI");
                            break;
                    }
                }
            }
        } catch (Exception e) {
            //System.out.println("no se cargo naturaleza de la lesion"+e.toString());
        }

        if (currentNonFatalDomesticV.getDomesticViolenceDataSourceId() != null) {
            newRowDataTable.setColumn80(currentNonFatalDomesticV.getDomesticViolenceDataSourceId().getDomesticViolenceDataSourcesName());
        }

        return newRowDataTable;
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
