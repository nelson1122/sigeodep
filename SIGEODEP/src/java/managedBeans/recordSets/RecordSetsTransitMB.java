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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import managedBeans.forms.TransitMB;
import model.dao.*;
import model.pojo.*;
import org.apache.poi.hssf.usermodel.*;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "recordSetsTransitMB")
@SessionScoped
public class RecordSetsTransitMB implements Serializable {

    @EJB
    NeighborhoodsFacade neighborhoodsFacade;
    @EJB
    CountriesFacade countriesFacade;    
    //--------------------
    @EJB
    TagsFacade tagsFacade;
    private List<Tags> tagsList;
    private Tags currentTag;
    private FatalInjuryTraffic currentFatalInjuryTraffic;
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
    FatalInjuryTrafficFacade fatalInjuryTrafficFacade;
    @EJB
    InjuriesFacade injuriesFacade;
    @EJB
    LoadsFacade loadsFacade;
    private List<RowDataTable> rowDataTableList;
    private RowDataTable selectedRowDataTable;
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
    private TransitMB transitMB;
    private String openForm = "";

    public RecordSetsTransitMB() {
    }

    public String openForm() {
        return openForm;
    }

    public void openInForm() {
        FacesContext context = FacesContext.getCurrentInstance();
        transitMB = (TransitMB) context.getApplication().evaluateExpressionGet(context, "#{transitMB}", TransitMB.class);
        transitMB.loadValues(tagsList, currentFatalInjuryTraffic);
        openForm = "transit";
    }

    void loadValues(RowDataTable[] selectedRowsDataTable) {
        selectedRowDataTable = null;
        rowDataTableList = new ArrayList<RowDataTable>();
        data = "- ";
        //CREO LA LISTA DE TAGS SELECCIONADOS
        tagsList = new ArrayList<Tags>();
        for (int i = 0; i < selectedRowsDataTable.length; i++) {
            data = data + selectedRowsDataTable[i].getColumn2() + " -";
            tagsList.add(tagsFacade.find(Integer.parseInt(selectedRowsDataTable[i].getColumn1())));
        }
        //RECORRO CADA TAG Y CARGO UN LISTADO DE SUS REGISTROS
        List<FatalInjuryTraffic> fatalInjuryTrafficList;
        for (int i = 0; i < tagsList.size(); i++) {
            fatalInjuryTrafficList = fatalInjuryTrafficFacade.findFromTag(tagsList.get(i).getTagId());
            for (int j = 0; j < fatalInjuryTrafficList.size(); j++) {                
                rowDataTableList.add(loadValues(fatalInjuryTrafficList.get(j)));
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

        createCell(cellStyle, row, 0, "#");//50">#{rowX.column1}</p:column>
        createCell(cellStyle, row, 1, "TIPO IDENT");//150">#{rowX.column2}</p:column>
        createCell(cellStyle, row, 2, "NUMERO IDENTI");//100">#{rowX.column3}</p:column>                
        createCell(cellStyle, row, 3, "NOMBRES");//400">#{rowX.column4}</p:column>                
        createCell(cellStyle, row, 4, "EXTRANJERO");//100">#{rowX.column5}</p:column>                
        createCell(cellStyle, row, 5, "TIP EDAD");//100">#{rowX.column6}</p:column>                
        createCell(cellStyle, row, 6, "EDAD CANT");//100">#{rowX.column7}</p:column>                
        createCell(cellStyle, row, 7, "GENERO");//100">#{rowX.column8}</p:column>                
        createCell(cellStyle, row, 8, "OCUPACION");//100">#{rowX.column9}</p:column>
        createCell(cellStyle, row, 9, "GRUPO ETNICO");//100">#{rowX.column10}</p:column>
        createCell(cellStyle, row, 10, "TELEFONO");//100">#{rowX.column11}</p:column>
        //<!--p:column headerText="victim_date_of_birth" );//100">{rowX.column}</p:column-->
        //<!--p:column headerText="eps_id" );//100">{rowX.column}</p:column-->
        //<!--p:column headerText="victim_class" );//100">{rowX.column}</p:column-->
        //<!--p:column headerText="victim_id" );//100">{rowX.column}</p:column-->
        createCell(cellStyle, row, 11, "residence_municipality");//100">#{rowX.column12}</p:column>
        createCell(cellStyle, row, 12, "residence_department");//100">#{rowX.column13}</p:column>

        createCell(cellStyle, row, 13, "victim_address");//400">#{rowX.column14}</p:column>
        createCell(cellStyle, row, 14, "victim_neighborhood_id");//250">#{rowX.column15}</p:column>


        //informacion de grupos vunerables                                
        createCell(cellStyle, row, 15, "isDisplaced");//100">#{rowX.column16}</p:column>
        createCell(cellStyle, row, 16, "isHandicapped");//100">#{rowX.column17}</p:column>
        createCell(cellStyle, row, 17, "insurance_id");//300">#{rowX.column18}</p:column>

        createCell(cellStyle, row, 18, "otro grupo etnico");//100">#{rowX.column19}</p:column>
        createCell(cellStyle, row, 19, "otro de lugar del hecho");//100">#{rowX.column20}</p:column>
        createCell(cellStyle, row, 20, "otra actividad");//100">#{rowX.column21}</p:column>
        createCell(cellStyle, row, 21, "Cual altura");//100">#{rowX.column22}</p:column>
        createCell(cellStyle, row, 22, "Cual polvora");//100">#{rowX.column23}</p:column>
        createCell(cellStyle, row, 23, "Cual desastre natural");//100">#{rowX.column24}</p:column>
        createCell(cellStyle, row, 24, "Cual otro mecanismo de objeto");//100">#{rowX.column25}</p:column>
        createCell(cellStyle, row, 25, "Cual otro animal");//100">#{rowX.column26}</p:column>
        createCell(cellStyle, row, 26, "Cual otro factor precipitante(Autoinflingida intencional)");//100">#{rowX.column27}</p:column>
        createCell(cellStyle, row, 27, "Cual otro tipo de agresor(intrafamiliar)");//100">#{rowX.column28}</p:column>
        createCell(cellStyle, row, 28, "Cual otro tipo de maltrato(intrafamiliar)");//100">#{rowX.column29}</p:column>
        createCell(cellStyle, row, 29, "Cual otra relación (violencia interpersonal)");//100">#{rowX.column30}</p:column>
        createCell(cellStyle, row, 30, "Cual otro tipo de transporte(transporte)");//100">#{rowX.column31}</p:column>
        createCell(cellStyle, row, 31, "Cual otro tipo de transporte de contraparte(transporte)");//100">#{rowX.column32}</p:column>
        createCell(cellStyle, row, 32, "Cual otro tipo de transporte de usuario(transporte)");//100">#{rowX.column33}</p:column>
        createCell(cellStyle, row, 33, "Cual otro sitio anatomico");//100">#{rowX.column34}</p:column>
        createCell(cellStyle, row, 34, "Cual otra naturaleza de la lesión");//100">#{rowX.column35}</p:column>
        createCell(cellStyle, row, 35, "Cual otro destino del paciente");//100">#{rowX.column36}</p:column>

        //------------------------------------------------------------
        //SE CARGAN VARIABLES LESION DE CAUSA EXTERNA NO FATAL
        //------------------------------------------------------------        
        createCell(cellStyle, row, 36, "checkup_date");//100">#{rowX.column37}</p:column>
        createCell(cellStyle, row, 37, "checkup_time");//100">#{rowX.column38}</p:column>
        createCell(cellStyle, row, 38, "injury_date");//100">#{rowX.column39}</p:column>
        createCell(cellStyle, row, 39, "injury_time");//100">#{rowX.column40}</p:column>
        createCell(cellStyle, row, 40, "injury_address");//400">#{rowX.column41}</p:column>
        createCell(cellStyle, row, 41, "injury_neighborhood_id");//250">#{rowX.column42}</p:column>
        createCell(cellStyle, row, 42, "injury_place_id");//200">#{rowX.column43}</p:column>
        createCell(cellStyle, row, 43, "activity_id");//250">#{rowX.column44}</p:column>
        createCell(cellStyle, row, 44, "intentionality_id");//250">#{rowX.column45}</p:column>
        createCell(cellStyle, row, 45, "use_alcohol_id");//150">#{rowX.column46}</p:column>
        createCell(cellStyle, row, 46, "use_drugs_id");//150">#{rowX.column47}</p:column>
        createCell(cellStyle, row, 47, "burn_injury_degree");//100">#{rowX.column48}</p:column>
        createCell(cellStyle, row, 48, "burn_injury_percentage");//100">#{rowX.column49}</p:column>
        createCell(cellStyle, row, 49, "submitted_patient");//100">#{rowX.column50}</p:column>
        createCell(cellStyle, row, 50, "eps_id");//250">#{rowX.column51}</p:column>
        createCell(cellStyle, row, 51, "destination_patient_id");//250">#{rowX.column52}</p:column>
        createCell(cellStyle, row, 52, "input_timestamp");//150">#{rowX.column53}</p:column>
        createCell(cellStyle, row, 53, "health_professional_id");//300">#{rowX.column54}</p:column>

        //<!--p:column headerText="non_fatal_data_source_id" );//100">{rowX.column}</p:column-->
        createCell(cellStyle, row, 54, "mechanism_id");//200">#{rowX.column55}</p:column>
        createCell(cellStyle, row, 55, "user_id");//100">#{rowX.column56}</p:column>
        createCell(cellStyle, row, 56, "injury_day_of_week");//100">#{rowX.column57}</p:column>
        createCell(cellStyle, row, 57, "non_fatal_data_source_id");//200">#{rowX.column58}</p:column>
        createCell(cellStyle, row, 58, "injury_id");//100">#{rowX.column59}</p:column>


        //------------------------------------------------------------
        //SE CARGA VARIABLE PARA VIOLENCIA INTERPERSONAL
        //-----------------------------------------------------------
        createCell(cellStyle, row, 59, "previousAntecedent");//100">#{rowX.column60}</p:column>
        createCell(cellStyle, row, 60, "relationshipVictimId");//100">#{rowX.column61}</p:column>
        createCell(cellStyle, row, 61, "getContextId");//100">#{rowX.column62}</p:column>
        createCell(cellStyle, row, 62, "getAggressorGenderId");//100">#{rowX.column63}</p:column>

        //------------------------------------------------------------
        //SE CARGA DATOS PARA VIOLENCIA INTRAFAMILIAR
        //------------------------------------------------------------        
        //tipo de agresor
        createCell(cellStyle, row, 63, "AG1 Padre");//100">#{rowX.column64}</p:column>
        createCell(cellStyle, row, 64, "AG2 Madre");//100">#{rowX.column65}</p:column>
        createCell(cellStyle, row, 65, "AG3 Padrastro");//100">#{rowX.column67}</p:column>
        createCell(cellStyle, row, 66, "AG4 Madastra");//100">#{rowX.column67}</p:column>
        createCell(cellStyle, row, 67, "AG5 Cónyuge");//100">#{rowX.column68}</p:column>
        createCell(cellStyle, row, 68, "AG6 Hermano");//100">#{rowX.column69}</p:column>
        createCell(cellStyle, row, 69, "AG7 Hijo");//100">#{rowX.column70}</p:column>
        createCell(cellStyle, row, 70, "AG8 Otro");//100">#{rowX.column71}</p:column>
        createCell(cellStyle, row, 71, "AG Sin Dato");//100">#{rowX.column72}</p:column>
        createCell(cellStyle, row, 72, "AG9 Novio");//100">#{rowX.column73}</p:column>
        //tipo de maltrato
        createCell(cellStyle, row, 73, "MA1 FISICO");//100">#{rowX.column74}</p:column>
        createCell(cellStyle, row, 74, "MA2 PSICOLOGICO");//100">#{rowX.column75}</p:column>
        createCell(cellStyle, row, 75, "MA3 VIOLENCIA SEXUAL");//100">#{rowX.column76}</p:column>
        createCell(cellStyle, row, 76, "MA4 NEGLIGENCIA");//100">#{rowX.column77}</p:column>
        createCell(cellStyle, row, 77, "MA5 ABANDONO");//100">#{rowX.column78}</p:column>
        createCell(cellStyle, row, 78, "MA6 INSTITUCIONAL");//100">#{rowX.column79}</p:column>
        createCell(cellStyle, row, 79, "MA SIN DATO");//100">#{rowX.column80}</p:column>
        createCell(cellStyle, row, 80, "MA8 OTRO");//100">#{rowX.column81}</p:column>

        //sitios anatomicos
        createCell(cellStyle, row, 81, "SA1 SISTEMICO");//100">#{rowX.column82}</p:column>
        createCell(cellStyle, row, 82, "SA2 CRANEO");//100">#{rowX.column83}</p:column>
        createCell(cellStyle, row, 83, "SA3 OJOS");//100">#{rowX.column84}</p:column>
        createCell(cellStyle, row, 84, "SA4 MAXILOFACIAL / NARIZ / OIDOS");//100">#{rowX.column85}</p:column>
        createCell(cellStyle, row, 85, "SA5 CUELLO");//100">#{rowX.column86}</p:column>
        createCell(cellStyle, row, 86, "SA6 TORAX");//100">#{rowX.column87}</p:column>
        createCell(cellStyle, row, 87, "SA7 ABDOMEN");//100">#{rowX.column88}</p:column>
        createCell(cellStyle, row, 88, "SA8 COLUMNA");//100">#{rowX.column89}</p:column>
        createCell(cellStyle, row, 89, "SA9 PELVIS / GENITALES");//100">#{rowX.column90}</p:column>
        createCell(cellStyle, row, 90, "SA10 MIEMBROS SUPERIORES");//100">#{rowX.column91}</p:column>
        createCell(cellStyle, row, 91, "SA11 MIEMBROS INFERIORES");//100">#{rowX.column92}</p:column>
        createCell(cellStyle, row, 92, "SA OTRO");//100">#{rowX.column93}</p:column>

        //cargo la naturaleza de la lesion
        createCell(cellStyle, row, 93, "NOF1 LACERACION, ABRASION, LESION SUPERFICIAL");//100">#{rowX.column94}</p:column>
        createCell(cellStyle, row, 94, "NOF2 CORTADA, MORDIDA, HERIDA ABIERTA");//100">#{rowX.column95}</p:column>
        createCell(cellStyle, row, 95, "NOF3 LESION PROFUNDA / PENETRANTE");//100">#{rowX.column96}</p:column>
        createCell(cellStyle, row, 96, "NOF4 ESGUINCE, LUXACION");//100">#{rowX.column97}</p:column>
        createCell(cellStyle, row, 97, "NOF5 FRACTURA");//100">#{rowX.column98}</p:column>
        createCell(cellStyle, row, 98, "NOF6 QUEMADURA");//100">#{rowX.column99}</p:column>
        createCell(cellStyle, row, 99, "NOF7 CONTUSION A ORGANOS INTERNOS");//100">#{rowX.column100}</p:column>
        createCell(cellStyle, row, 100, "NOF8 LESION ORGANICA SISTEMICA");//100">#{rowX.column101}</p:column>
        createCell(cellStyle, row, 101, "NOF9 TRAUMA CRANEOENCEFALICO");//100">#{rowX.column102}</p:column>
        createCell(cellStyle, row, 102, "NOF OTRO");//100">#{rowX.column103}</p:column>
        createCell(cellStyle, row, 103, "NOF SIN DATO");//100">#{rowX.column104}</p:column>

        //cargo los diagnosticos
        createCell(cellStyle, row, 104, "CIE-10 1");//100">#{rowX.column105}</p:column>
        createCell(cellStyle, row, 105, "CIE-10 2");//100">#{rowX.column106}</p:column>
        createCell(cellStyle, row, 106, "CIE-10 3");//100">#{rowX.column107}</p:column>
        createCell(cellStyle, row, 107, "CIE-10 4");//100">#{rowX.column108}</p:column>

        //------------------------------------------------------------
        //AUTOINFLINGIDA INTENCIONAL
        //------------------------------------------------------------
        createCell(cellStyle, row, 108, "getPreviousAttempt");//100">#{rowX.column109}</p:column>
        createCell(cellStyle, row, 109, "getMentalAntecedent");//100">#{rowX.column110}</p:column>
        createCell(cellStyle, row, 110, "getPrecipitatingFactorId");//100">#{rowX.column111}</p:column>

        //------------------------------------------------------------
        //SE CARGA DATOS PARA TRANSITO
        //------------------------------------------------------------
        createCell(cellStyle, row, 111, "getTransportTypeId");//100">#{rowX.column112}</p:column>
        createCell(cellStyle, row, 112, "getTransportCounterpartId");//100">#{rowX.column113}</p:column>
        createCell(cellStyle, row, 113, "getTransportUserId");//100">#{rowX.column114}</p:column>
        createCell(cellStyle, row, 114, "isBeltUse");//100">#{rowX.column115}</p:column>
        createCell(cellStyle, row, 115, "isHelmetUse");//100">#{rowX.column116}</p:column>
        createCell(cellStyle, row, 116, "isBicycleHelmetUse");//100">#{rowX.column117}</p:column>
        createCell(cellStyle, row, 117, "isVestUse");//100">#{rowX.column118}</p:column>
        createCell(cellStyle, row, 118, "isOtherElementUse");//100">#{rowX.column119}</p:column>
        //<!--p:column headerText="currentSecurityElements  "NO"" );//100">{rowX.column}</p:column-->
        //<!--p:column headerText="currentSecurityElements = "NO SE SABE"" );//100">{rowX.column}</p:column-->        



        for (int i = 0; i < rowDataTableList.size(); i++) {
            row = sheet.createRow(i + 1);
            createCell(row, 0, rowDataTableList.get(i).getColumn1());//50">#{rowX.column1}</p:column>
            createCell(row, 1, rowDataTableList.get(i).getColumn2());//"TIPO IDENT");//150">#{rowX.column2}</p:column>
            createCell(row, 2, rowDataTableList.get(i).getColumn3());//"NUMERO IDENTI");//100">#{rowX.column3}</p:column>                
            createCell(row, 3, rowDataTableList.get(i).getColumn4());//"NOMBRES");//400">#{rowX.column4}</p:column>                
            createCell(row, 4, rowDataTableList.get(i).getColumn5());//"EXTRANJERO");//100">#{rowX.column5}</p:column>                
            createCell(row, 5, rowDataTableList.get(i).getColumn6());//"TIP EDAD");//100">#{rowX.column6}</p:column>                
            createCell(row, 6, rowDataTableList.get(i).getColumn7());//"EDAD CANT");//100">#{rowX.column7}</p:column>                
            createCell(row, 7, rowDataTableList.get(i).getColumn8());//"GENERO");//100">#{rowX.column8}</p:column>                
            createCell(row, 8, rowDataTableList.get(i).getColumn9());//"OCUPACION");//100">#{rowX.column9}</p:column>
            createCell(row, 9, rowDataTableList.get(i).getColumn10());//"GRUPO ETNICO");//100">#{rowX.column10}</p:column>
            createCell(row, 10, rowDataTableList.get(i).getColumn11());//"TELEFONO");//100">#{rowX.column11}</p:column>
            //<!--p:column headerText="victim_date_of_birth" );//100">{rowX.column}</p:column-->
            //<!--p:column headerText="eps_id" );//100">{rowX.column}</p:column-->
            //<!--p:column headerText="victim_class" );//100">{rowX.column}</p:column-->
            //<!--p:column headerText="victim_id" );//100">{rowX.column}</p:column-->
            createCell(row, 11, rowDataTableList.get(i).getColumn12());//"residence_municipality");//100">#{rowX.column12}</p:column>
            createCell(row, 12, rowDataTableList.get(i).getColumn13());//"residence_department");//100">#{rowX.column13}</p:column>

            createCell(row, 13, rowDataTableList.get(i).getColumn14());//"victim_address");//400">#{rowX.column14}</p:column>
            createCell(row, 14, rowDataTableList.get(i).getColumn15());//"victim_neighborhood_id");//250">#{rowX.column15}</p:column>


            //informacion de grupos vunerables                                
            createCell(row, 15, rowDataTableList.get(i).getColumn16());//"isDisplaced");//100">#{rowX.column16}</p:column>
            createCell(row, 16, rowDataTableList.get(i).getColumn17());//"isHandicapped");//100">#{rowX.column17}</p:column>
            createCell(row, 17, rowDataTableList.get(i).getColumn18());//"insurance_id");//300">#{rowX.column18}</p:column>

            createCell(row, 18, rowDataTableList.get(i).getColumn19());//"otro grupo etnico");//100">#{rowX.column19}</p:column>
            createCell(row, 19, rowDataTableList.get(i).getColumn20());//"otro de lugar del hecho");//100">#{rowX.column20}</p:column>
            createCell(row, 20, rowDataTableList.get(i).getColumn21());//"otra actividad");//100">#{rowX.column21}</p:column>
            createCell(row, 21, rowDataTableList.get(i).getColumn22());//"Cual altura");//100">#{rowX.column22}</p:column>
            createCell(row, 22, rowDataTableList.get(i).getColumn23());//"Cual polvora");//100">#{rowX.column23}</p:column>
            createCell(row, 23, rowDataTableList.get(i).getColumn24());//"Cual desastre natural");//100">#{rowX.column24}</p:column>
            createCell(row, 24, rowDataTableList.get(i).getColumn25());//"Cual otro mecanismo de objeto");//100">#{rowX.column25}</p:column>
            createCell(row, 25, rowDataTableList.get(i).getColumn26());//"Cual otro animal");//100">#{rowX.column26}</p:column>
            createCell(row, 26, rowDataTableList.get(i).getColumn27());//"Cual otro factor precipitante(Autoinflingida intencional)");//100">#{rowX.column27}</p:column>
            createCell(row, 27, rowDataTableList.get(i).getColumn28());//"Cual otro tipo de agresor(intrafamiliar)");//100">#{rowX.column28}</p:column>
            createCell(row, 28, rowDataTableList.get(i).getColumn29());//"Cual otro tipo de maltrato(intrafamiliar)");//100">#{rowX.column29}</p:column>
            createCell(row, 29, rowDataTableList.get(i).getColumn30());//"Cual otra relación (violencia interpersonal)");//100">#{rowX.column30}</p:column>
            createCell(row, 30, rowDataTableList.get(i).getColumn31());//"Cual otro tipo de transporte(transporte)");//100">#{rowX.column31}</p:column>
            createCell(row, 31, rowDataTableList.get(i).getColumn32());//"Cual otro tipo de transporte de contraparte(transporte)");//100">#{rowX.column32}</p:column>
            createCell(row, 32, rowDataTableList.get(i).getColumn33());//"Cual otro tipo de transporte de usuario(transporte)");//100">#{rowX.column33}</p:column>
            createCell(row, 33, rowDataTableList.get(i).getColumn34());//"Cual otro sitio anatomico");//100">#{rowX.column34}</p:column>
            createCell(row, 34, rowDataTableList.get(i).getColumn35());//"Cual otra naturaleza de la lesión");//100">#{rowX.column35}</p:column>
            createCell(row, 35, rowDataTableList.get(i).getColumn36());//"Cual otro destino del paciente");//100">#{rowX.column36}</p:column>

            //------------------------------------------------------------
            //SE CARGAN VARIABLES LESION DE CAUSA EXTERNA NO FATAL
            //------------------------------------------------------------        
            createCell(row, 36, rowDataTableList.get(i).getColumn37());//"checkup_date");//100">#{rowX.column37}</p:column>
            createCell(row, 37, rowDataTableList.get(i).getColumn38());//"checkup_time");//100">#{rowX.column38}</p:column>
            createCell(row, 38, rowDataTableList.get(i).getColumn39());//"injury_date");//100">#{rowX.column39}</p:column>
            createCell(row, 39, rowDataTableList.get(i).getColumn40());//"injury_time");//100">#{rowX.column40}</p:column>
            createCell(row, 40, rowDataTableList.get(i).getColumn41());//"injury_address");//400">#{rowX.column41}</p:column>
            createCell(row, 41, rowDataTableList.get(i).getColumn42());//"injury_neighborhood_id");//250">#{rowX.column42}</p:column>
            createCell(row, 42, rowDataTableList.get(i).getColumn43());//"injury_place_id");//200">#{rowX.column43}</p:column>
            createCell(row, 43, rowDataTableList.get(i).getColumn44());//"activity_id");//250">#{rowX.column44}</p:column>
            createCell(row, 44, rowDataTableList.get(i).getColumn45());//"intentionality_id");//250">#{rowX.column45}</p:column>
            createCell(row, 45, rowDataTableList.get(i).getColumn46());//"use_alcohol_id");//150">#{rowX.column46}</p:column>
            createCell(row, 46, rowDataTableList.get(i).getColumn47());//"use_drugs_id");//150">#{rowX.column47}</p:column>
            createCell(row, 47, rowDataTableList.get(i).getColumn48());//"burn_injury_degree");//100">#{rowX.column48}</p:column>
            createCell(row, 48, rowDataTableList.get(i).getColumn49());//"burn_injury_percentage");//100">#{rowX.column49}</p:column>
            createCell(row, 49, rowDataTableList.get(i).getColumn50());//"submitted_patient");//100">#{rowX.column50}</p:column>
            createCell(row, 50, rowDataTableList.get(i).getColumn51());//"eps_id");//250">#{rowX.column51}</p:column>
            createCell(row, 51, rowDataTableList.get(i).getColumn52());//"destination_patient_id");//250">#{rowX.column52}</p:column>
            createCell(row, 52, rowDataTableList.get(i).getColumn53());//"input_timestamp");//150">#{rowX.column53}</p:column>
            createCell(row, 53, rowDataTableList.get(i).getColumn54());//"health_professional_id");//300">#{rowX.column54}</p:column>

            //<!--p:column headerText="non_fatal_data_source_id" );//100">{rowX.column}</p:column-->
            createCell(row, 54, rowDataTableList.get(i).getColumn55());//"mechanism_id");//200">#{rowX.column55}</p:column>
            createCell(row, 55, rowDataTableList.get(i).getColumn56());//"user_id");//100">#{rowX.column56}</p:column>
            createCell(row, 56, rowDataTableList.get(i).getColumn57());//"injury_day_of_week");//100">#{rowX.column57}</p:column>
            createCell(row, 57, rowDataTableList.get(i).getColumn58());//"non_fatal_data_source_id");//200">#{rowX.column58}</p:column>
            createCell(row, 58, rowDataTableList.get(i).getColumn59());//"injury_id");//100">#{rowX.column59}</p:column>


            //------------------------------------------------------------
            //SE CARGA VARIABLE PARA VIOLENCIA INTERPERSONAL
            //-----------------------------------------------------------
            createCell(row, 59, rowDataTableList.get(i).getColumn60());//"previousAntecedent");//100">#{rowX.column60}</p:column>
            createCell(row, 60, rowDataTableList.get(i).getColumn61());//"relationshipVictimId");//100">#{rowX.column61}</p:column>
            createCell(row, 61, rowDataTableList.get(i).getColumn62());//"getContextId");//100">#{rowX.column62}</p:column>
            createCell(row, 62, rowDataTableList.get(i).getColumn63());//"getAggressorGenderId");//100">#{rowX.column63}</p:column>

            //------------------------------------------------------------
            //SE CARGA DATOS PARA VIOLENCIA INTRAFAMILIAR
            //------------------------------------------------------------        
            //tipo de agresor
            createCell(row, 63, rowDataTableList.get(i).getColumn64());//"AG1 Padre");//100">#{rowX.column64}</p:column>
            createCell(row, 64, rowDataTableList.get(i).getColumn65());//"AG2 Madre");//100">#{rowX.column65}</p:column>
            createCell(row, 65, rowDataTableList.get(i).getColumn66());//"AG3 Padrastro");//100">#{rowX.column67}</p:column>
            createCell(row, 66, rowDataTableList.get(i).getColumn67());//"AG4 Madastra");//100">#{rowX.column67}</p:column>
            createCell(row, 67, rowDataTableList.get(i).getColumn68());//"AG5 Cónyuge");//100">#{rowX.column68}</p:column>
            createCell(row, 68, rowDataTableList.get(i).getColumn69());//"AG6 Hermano");//100">#{rowX.column69}</p:column>
            createCell(row, 69, rowDataTableList.get(i).getColumn70());//"AG7 Hijo");//100">#{rowX.column70}</p:column>
            createCell(row, 70, rowDataTableList.get(i).getColumn71());//"AG8 Otro");//100">#{rowX.column71}</p:column>
            createCell(row, 71, rowDataTableList.get(i).getColumn72());//"AG Sin Dato");//100">#{rowX.column72}</p:column>
            createCell(row, 72, rowDataTableList.get(i).getColumn72());//"AG9 Novio");//100">#{rowX.column73}</p:column>
            //tipo de maltrato
            createCell(row, 73, rowDataTableList.get(i).getColumn74());//"MA1 FISICO");//100">#{rowX.column74}</p:column>
            createCell(row, 74, rowDataTableList.get(i).getColumn75());//"MA2 PSICOLOGICO");//100">#{rowX.column75}</p:column>
            createCell(row, 75, rowDataTableList.get(i).getColumn76());//"MA3 VIOLENCIA SEXUAL");//100">#{rowX.column76}</p:column>
            createCell(row, 76, rowDataTableList.get(i).getColumn77());//"MA4 NEGLIGENCIA");//100">#{rowX.column77}</p:column>
            createCell(row, 77, rowDataTableList.get(i).getColumn78());//"MA5 ABANDONO");//100">#{rowX.column78}</p:column>
            createCell(row, 78, rowDataTableList.get(i).getColumn79());//"MA6 INSTITUCIONAL");//100">#{rowX.column79}</p:column>
            createCell(row, 79, rowDataTableList.get(i).getColumn80());//"MA SIN DATO");//100">#{rowX.column80}</p:column>
            createCell(row, 80, rowDataTableList.get(i).getColumn81());//"MA8 OTRO");//100">#{rowX.column81}</p:column>

            //sitios anatomicos
            createCell(row, 81, rowDataTableList.get(i).getColumn82());//"SA1 SISTEMICO");//100">#{rowX.column82}</p:column>
            createCell(row, 82, rowDataTableList.get(i).getColumn83());//"SA2 CRANEO");//100">#{rowX.column83}</p:column>
            createCell(row, 83, rowDataTableList.get(i).getColumn84());//"SA3 OJOS");//100">#{rowX.column84}</p:column>
            createCell(row, 84, rowDataTableList.get(i).getColumn85());//"SA4 MAXILOFACIAL / NARIZ / OIDOS");//100">#{rowX.column85}</p:column>
            createCell(row, 85, rowDataTableList.get(i).getColumn86());//"SA5 CUELLO");//100">#{rowX.column86}</p:column>
            createCell(row, 86, rowDataTableList.get(i).getColumn87());//"SA6 TORAX");//100">#{rowX.column87}</p:column>
            createCell(row, 87, rowDataTableList.get(i).getColumn88());//"SA7 ABDOMEN");//100">#{rowX.column88}</p:column>
            createCell(row, 88, rowDataTableList.get(i).getColumn89());//"SA8 COLUMNA");//100">#{rowX.column89}</p:column>
            createCell(row, 89, rowDataTableList.get(i).getColumn90());//"SA9 PELVIS / GENITALES");//100">#{rowX.column90}</p:column>
            createCell(row, 90, rowDataTableList.get(i).getColumn91());//"SA10 MIEMBROS SUPERIORES");//100">#{rowX.column91}</p:column>
            createCell(row, 91, rowDataTableList.get(i).getColumn92());//"SA11 MIEMBROS INFERIORES");//100">#{rowX.column92}</p:column>
            createCell(row, 92, rowDataTableList.get(i).getColumn93());//"SA OTRO");//100">#{rowX.column93}</p:column>

            //cargo la naturaleza de la lesion
            createCell(row, 93, rowDataTableList.get(i).getColumn94());//"NOF1 LACERACION, ABRASION, LESION SUPERFICIAL");//100">#{rowX.column94}</p:column>
            createCell(row, 94, rowDataTableList.get(i).getColumn95());//"NOF2 CORTADA, MORDIDA, HERIDA ABIERTA");//100">#{rowX.column95}</p:column>
            createCell(row, 95, rowDataTableList.get(i).getColumn96());//"NOF3 LESION PROFUNDA / PENETRANTE");//100">#{rowX.column96}</p:column>
            createCell(row, 96, rowDataTableList.get(i).getColumn97());//"NOF4 ESGUINCE, LUXACION");//100">#{rowX.column97}</p:column>
            createCell(row, 97, rowDataTableList.get(i).getColumn98());//"NOF5 FRACTURA");//100">#{rowX.column98}</p:column>
            createCell(row, 98, rowDataTableList.get(i).getColumn99());//"NOF6 QUEMADURA");//100">#{rowX.column99}</p:column>
            createCell(row, 99, rowDataTableList.get(i).getColumn100());//"NOF7 CONTUSION A ORGANOS INTERNOS");//100">#{rowX.column100}</p:column>
            createCell(row, 100, rowDataTableList.get(i).getColumn101());//"NOF8 LESION ORGANICA SISTEMICA");//100">#{rowX.column101}</p:column>
            createCell(row, 101, rowDataTableList.get(i).getColumn102());//"NOF9 TRAUMA CRANEOENCEFALICO");//100">#{rowX.column102}</p:column>
            createCell(row, 102, rowDataTableList.get(i).getColumn103());//"NOF OTRO");//100">#{rowX.column103}</p:column>
            createCell(row, 103, rowDataTableList.get(i).getColumn104());//"NOF SIN DATO");//100">#{rowX.column104}</p:column>

            //cargo los diagnosticos
            createCell(row, 104, rowDataTableList.get(i).getColumn105());//"CIE-10 1");//100">#{rowX.column105}</p:column>
            createCell(row, 105, rowDataTableList.get(i).getColumn106());//"CIE-10 2");//100">#{rowX.column106}</p:column>
            createCell(row, 106, rowDataTableList.get(i).getColumn107());//"CIE-10 3");//100">#{rowX.column107}</p:column>
            createCell(row, 107, rowDataTableList.get(i).getColumn108());//"CIE-10 4");//100">#{rowX.column108}</p:column>

            //------------------------------------------------------------
            //AUTOINFLINGIDA INTENCIONAL
            //------------------------------------------------------------
            createCell(row, 108, rowDataTableList.get(i).getColumn109());//"getPreviousAttempt");//100">#{rowX.column109}</p:column>
            createCell(row, 109, rowDataTableList.get(i).getColumn110());//"getMentalAntecedent");//100">#{rowX.column110}</p:column>
            createCell(row, 110, rowDataTableList.get(i).getColumn111());//"getPrecipitatingFactorId");//100">#{rowX.column111}</p:column>

            //------------------------------------------------------------
            //SE CARGA DATOS PARA TRANSITO
            //------------------------------------------------------------
            createCell(row, 111, rowDataTableList.get(i).getColumn112());//"getTransportTypeId");//100">#{rowX.column112}</p:column>
            createCell(row, 112, rowDataTableList.get(i).getColumn113());//"getTransportCounterpartId");//100">#{rowX.column113}</p:column>
            createCell(row, 113, rowDataTableList.get(i).getColumn114());//"getTransportUserId");//100">#{rowX.column114}</p:column>
            createCell(row, 114, rowDataTableList.get(i).getColumn115());//"isBeltUse");//100">#{rowX.column115}</p:column>
            createCell(row, 115, rowDataTableList.get(i).getColumn116());//"isHelmetUse");//100">#{rowX.column116}</p:column>
            createCell(row, 116, rowDataTableList.get(i).getColumn117());//"isBicycleHelmetUse");//100">#{rowX.column117}</p:column>
            createCell(row, 117, rowDataTableList.get(i).getColumn118());//"isVestUse");//100">#{rowX.column118}</p:column>
            createCell(row, 118, rowDataTableList.get(i).getColumn119());//"isOtherElementUse");//100">#{rowX.column119}</p:column>
            //<!--p:column headerText="currentSecurityElements  "NO"" );//100">{rowX.column}</p:column-->
            //<!--p:column headerText="currentSecurityElements = "NO SE SABE"" );//100">{rowX.column}</p:column-->        

        }

    }

    private RowDataTable loadValues(FatalInjuryTraffic currentFatalInjuryT) {
        //CARGO LOS DATOS DE UNA DETERMINA LESION NO FATAL EN UNA FILA PARA LA TABLA
        btnEditDisabled = true;
        btnRemoveDisabled = true;
        RowDataTable newRowDataTable = new RowDataTable();
        //------------------------------------------------------------
        //SE CARGAN VALORES PARA LA NUEVA VICTIMA
        //------------------------------------------------------------

        //******non_fatal_injury_id
        newRowDataTable.setColumn1(currentFatalInjuryT.getFatalInjuries().getFatalInjuryId().toString());
        
        //******type_id
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getTypeId() != null) {
                newRowDataTable.setColumn2(currentFatalInjuryT.getFatalInjuries().getVictimId().getTypeId().getTypeName());
            }
        } catch (Exception e) {
        }
        //******victim_nid
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getVictimNid() != null) {
                newRowDataTable.setColumn3(currentFatalInjuryT.getFatalInjuries().getVictimId().getVictimNid());
            }
        } catch (Exception e) {
        }
        //******victim_name
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getVictimName() != null) {
                newRowDataTable.setColumn4(currentFatalInjuryT.getFatalInjuries().getVictimId().getVictimName());
            }
        } catch (Exception e) {
        }
        //******stranger
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getStranger() != null) {
                newRowDataTable.setColumn5(currentFatalInjuryT.getFatalInjuries().getVictimId().getStranger().toString());
            }
        } catch (Exception e) {
        }
        //******age_type_id
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getAgeTypeId() != null) {
                newRowDataTable.setColumn6(ageTypesFacade.find(currentFatalInjuryT.getFatalInjuries().getVictimId().getAgeTypeId()).getAgeTypeName());
            }
        } catch (Exception e) {
        }
        //******victim_age
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getVictimAge() != null) {
                newRowDataTable.setColumn7(currentFatalInjuryT.getFatalInjuries().getVictimId().getVictimAge().toString());
            }
        } catch (Exception e) {
        }
        //******gender_id
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getGenderId() != null) {
                newRowDataTable.setColumn8(currentFatalInjuryT.getFatalInjuries().getVictimId().getGenderId().getGenderName());
            }
        } catch (Exception e) {
        }
        //******job_id
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getJobId() != null) {
                newRowDataTable.setColumn9(currentFatalInjuryT.getFatalInjuries().getVictimId().getJobId().getJobName());
            }
        } catch (Exception e) {
        }


        //******vulnerable_group_id
        //******ethnic_group_id        
        //******victim_telephone
        //******victim_address


        //******victim_neighborhood_id
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getVictimNeighborhoodId() != null) {
                newRowDataTable.setColumn10(currentFatalInjuryT.getFatalInjuries().getVictimId().getVictimNeighborhoodId().getNeighborhoodName());
            }
        } catch (Exception e) {
        }

        //******victim_date_of_birth
        //******eps_id
        //******victim_class
        //******victim_id       

        //******residence_municipality
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getResidenceDepartment() != null && (currentFatalInjuryT.getFatalInjuries().getVictimId().getResidenceMunicipality() != null)) {
                short departamentId = currentFatalInjuryT.getFatalInjuries().getVictimId().getResidenceDepartment();
                short municipalityId = currentFatalInjuryT.getFatalInjuries().getVictimId().getResidenceMunicipality();
                MunicipalitiesPK mPk = new MunicipalitiesPK(departamentId, municipalityId);
                newRowDataTable.setColumn11(municipalitiesFacade.find(mPk).getMunicipalityName());
            }
        } catch (Exception e) {
        }
        //******residence_department
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimId().getResidenceDepartment() != null) {
                newRowDataTable.setColumn12(departamentsFacade.find(currentFatalInjuryT.getFatalInjuries().getVictimId().getResidenceDepartment()).getDepartamentName());
            }
        } catch (Exception e) {
        }

        //******insurance_id

        //------------------------------------------------------------
        //SE CARGAN VARIABLES LESION DE CAUSA EXTERNA FATAL
        //------------------------------------------------------------
        //******injury_id
        //******injury_date
        try {
            if (currentFatalInjuryT.getFatalInjuries().getInjuryDate() != null) {
                newRowDataTable.setColumn13(sdf.format(currentFatalInjuryT.getFatalInjuries().getInjuryDate()));
            }
        } catch (Exception e) {
        }
        //******injury_time
        try {
            if (currentFatalInjuryT.getFatalInjuries().getInjuryTime() != null) {
                hours = String.valueOf(currentFatalInjuryT.getFatalInjuries().getInjuryTime().getHours());
                minutes = String.valueOf(currentFatalInjuryT.getFatalInjuries().getInjuryTime().getMinutes());
                if (hours.length() != 2) {
                    hours = "0" + hours;
                }
                if (minutes.length() != 2) {
                    minutes = "0" + minutes;
                }
                newRowDataTable.setColumn14(hours + minutes);
            }
        } catch (Exception e) {
        }
        //******injury_address
        if (currentFatalInjuryT.getFatalInjuries().getInjuryAddress() != null) {
            newRowDataTable.setColumn15(currentFatalInjuryT.getFatalInjuries().getInjuryAddress());
        }
        //******injury_neighborhood_id
        try {
            if (currentFatalInjuryT.getFatalInjuries().getInjuryNeighborhoodId() != null) {
                newRowDataTable.setColumn16(neighborhoodsFacade.find(currentFatalInjuryT.getFatalInjuries().getInjuryNeighborhoodId()).getNeighborhoodName());
            }
        } catch (Exception e) {
        }
        //******injury_place_id
        try {
            if (currentFatalInjuryT.getFatalInjuries().getInjuryPlaceId() != null) {
                newRowDataTable.setColumn17(currentFatalInjuryT.getFatalInjuries().getInjuryPlaceId().getPlaceName());
            }
        } catch (Exception e) {
        }

        //******victim_number
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimNumber() != null) {
                newRowDataTable.setColumn18(currentFatalInjuryT.getFatalInjuries().getVictimNumber().toString());

            }
        } catch (Exception e) {
        }
        //******injury_description
        if (currentFatalInjuryT.getFatalInjuries().getInjuryDescription() != null) {
            newRowDataTable.setColumn19(currentFatalInjuryT.getFatalInjuries().getInjuryDescription());
        }
        //******user_id	
        //******input_timestamp	
        //******injury_day_of_week
        try {
            if (currentFatalInjuryT.getFatalInjuries().getInjuryDayOfWeek() != null) {
                newRowDataTable.setColumn20(currentFatalInjuryT.getFatalInjuries().getInjuryDayOfWeek());
            }
        } catch (Exception e) {
        }

        //******victim_id
        //******fatal_injury_id
        //******alcohol_level_victim
        try {
            if (currentFatalInjuryT.getFatalInjuries().getAlcoholLevelVictim() != null) {
                newRowDataTable.setColumn21(currentFatalInjuryT.getFatalInjuries().getAlcoholLevelVictim().toString());
            }
        } catch (Exception e) {
        }
        //******alcohol_level_victimId
        try {
            if (currentFatalInjuryT.getFatalInjuries().getAlcoholLevelVictimId() != null) {
                newRowDataTable.setColumn22(currentFatalInjuryT.getFatalInjuries().getAlcoholLevelVictimId().getAlcoholLevelName());
            }
        } catch (Exception e) {
        }
        //******code
        try {
            if (currentFatalInjuryT.getFatalInjuries().getCode() != null) {
                newRowDataTable.setColumn23(currentFatalInjuryT.getFatalInjuries().getCode());
            }
        } catch (Exception e) {
        }
        //******area_id
        try {
            if (currentFatalInjuryT.getFatalInjuries().getAreaId() != null) {
                newRowDataTable.setColumn24(currentFatalInjuryT.getFatalInjuries().getAreaId().getAreaName());
            }
        } catch (Exception e) {
        }        
        //******victim_place_of_origin
        try {
            if (currentFatalInjuryT.getFatalInjuries().getVictimPlaceOfOrigin() != null) {
                String source = currentFatalInjuryT.getFatalInjuries().getVictimPlaceOfOrigin();
                String[] sourceSplit = source.split("-");
                //determino pais
                newRowDataTable.setColumn25(countriesFacade.find(Short.parseShort(sourceSplit[0])).getName());                
                if (Short.parseShort(sourceSplit[0]) == 52) {//colombia
                    newRowDataTable.setColumn26(departamentsFacade.find(Short.parseShort(sourceSplit[1])).getDepartamentName());
                    MunicipalitiesPK municipalitiesPK=new MunicipalitiesPK(Short.parseShort(sourceSplit[1]), Short.parseShort(sourceSplit[2]));
                    newRowDataTable.setColumn27(municipalitiesFacade.find(municipalitiesPK).getMunicipalityName());
                } 
            }
        } catch (Exception e) {
        }
        //------------------------------------------------------------
        //SE CARGA DATOS PARA LA NUEVA LESION FATAL POR ACCIDENTE DE TRANSITO
        //------------------------------------------------------------
        //cargar vehiculos de la contraparte
        List<CounterpartInvolvedVehicle> involvedVehiclesList = currentFatalInjuryT.getFatalInjuries().getCounterpartInvolvedVehicleList();
        if (involvedVehiclesList != null) {
            for (int i = 0; i < involvedVehiclesList.size(); i++) {
                if (i == 0) {
                    newRowDataTable.setColumn28(involvedVehiclesList.get(0).getInvolvedVehicleId().getInvolvedVehicleName());
                }
                if (i == 1) {
                    newRowDataTable.setColumn29(involvedVehiclesList.get(1).getInvolvedVehicleId().getInvolvedVehicleName());
                }
                if (i == 2) {
                    newRowDataTable.setColumn30(involvedVehiclesList.get(2).getInvolvedVehicleId().getInvolvedVehicleName());
                }
            }
        }

        //cargar tipo de servcio de la contraparte
        List<CounterpartServiceType> serviceTypesList = currentFatalInjuryT.getFatalInjuries().getCounterpartServiceTypeList();

        if (involvedVehiclesList != null) {
            for (int i = 0; i < serviceTypesList.size(); i++) {
                if (i == 0) {
                    newRowDataTable.setColumn31(serviceTypesList.get(0).getServiceTypeId().getServiceTypeName());
                }
                if (i == 1) {
                    newRowDataTable.setColumn32(serviceTypesList.get(1).getServiceTypeId().getServiceTypeName());
                }
                if (i == 2) {
                    newRowDataTable.setColumn33(serviceTypesList.get(2).getServiceTypeId().getServiceTypeName());
                }
            }
        }


        //******number_non_fatal_victims
        if (currentFatalInjuryT.getNumberNonFatalVictims() != null) {
            newRowDataTable.setColumn34(currentFatalInjuryT.getNumberNonFatalVictims().toString());
        } 
        //******victim_characteristic_id
        if (currentFatalInjuryT.getVictimCharacteristicId() != null) {
            newRowDataTable.setColumn35(currentFatalInjuryT.getVictimCharacteristicId().getCharacteristicName());
        }
        //******protection_measure_id
        if (currentFatalInjuryT.getProtectionMeasureId() != null) {
            newRowDataTable.setColumn36(currentFatalInjuryT.getProtectionMeasureId().getProtectiveMeasuresName());
        }
        //******road_type_id
        if (currentFatalInjuryT.getRoadTypeId() != null) {
            newRowDataTable.setColumn37(currentFatalInjuryT.getRoadTypeId().getRoadTypeName());
        }
        //******accident_class_id
        if (currentFatalInjuryT.getAccidentClassId() != null) {
            newRowDataTable.setColumn38(currentFatalInjuryT.getAccidentClassId().getAccidentClassName());            
        }
        //******involved_vehicle_id
        if (currentFatalInjuryT.getInvolvedVehicleId() != null) {
            newRowDataTable.setColumn39(currentFatalInjuryT.getInvolvedVehicleId().getInvolvedVehicleName());            
        }
        //******service_type_id
        if (currentFatalInjuryT.getServiceTypeId() != null) {
            newRowDataTable.setColumn40(currentFatalInjuryT.getServiceTypeId().getServiceTypeName());            
        }
        
        //******alcohol_level_counterpart
        try {
            if (currentFatalInjuryT.getAlcoholLevelCounterpart() != null) {
                newRowDataTable.setColumn41(currentFatalInjuryT.getAlcoholLevelCounterpart().toString());
            }
        } catch (Exception e) {
        }
        //******alcohol_level_counterpart_id
        try {
            if (currentFatalInjuryT.getFatalInjuries().getAlcoholLevelVictimId() != null) {
                newRowDataTable.setColumn42(currentFatalInjuryT.getAlcoholLevelCounterpartId().getAlcoholLevelName());
            }
        } catch (Exception e) {
        }        
        //******fatal_injury_id

        return newRowDataTable;
    }

    public void load() {
        currentFatalInjuryTraffic = null;
        if (selectedRowDataTable != null) {
            currentFatalInjuryTraffic = fatalInjuryTrafficFacade.find(Integer.parseInt(selectedRowDataTable.getColumn1()));
        }
        if (currentFatalInjuryTraffic != null) {
            btnEditDisabled = false;
            btnRemoveDisabled = false;
        }
    }

    public void deleteRegistry() {
//        if (selectedRowDataTable != null) {
//            if (currentFatalInjuryTraffic.getNonFatalDomesticViolence() != null) {
//                nonFatalDomesticViolenceFacade.remove(currentFatalInjuryTraffic.getNonFatalDomesticViolence());
//            }
//            if (currentFatalInjuryTraffic.getNonFatalInterpersonal() != null) {
//                nonFatalInterpersonalFacade.remove(currentFatalInjuryTraffic.getNonFatalInterpersonal());
//            }
//            if (currentFatalInjuryTraffic.getNonFatalSelfInflicted() != null) {
//                nonFatalSelfInflictedFacade.remove(currentFatalInjuryTraffic.getNonFatalSelfInflicted());
//            }
//            if (currentFatalInjuryTraffic.getNonFatalTransport() != null) {
//                nonFatalTransportFacade.remove(currentFatalInjuryTraffic.getNonFatalTransport());
//            }
//            LoadsPK loadsPK;
//            Loads currentLoad;
//            for (int i = 0; i < tagsList.size(); i++) {
//                loadsPK = new LoadsPK(tagsList.get(i).getTagId(), currentFatalInjuryTraffic.getNonFatalInjuryId());
//                currentLoad = loadsFacade.find(loadsPK);
//                if (currentLoad != null) {
//                    loadsFacade.remove(currentLoad);
//                    fatalInjuryTrafficFacade.remove(currentFatalInjuryTraffic);
//                    victimsFacade.remove(currentFatalInjuryTraffic.getVictimId());
//                    rowDataTableList.remove(selectedRowDataTable);
//                    selectedRowDataTable = null;
//                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "CORRECTO", "El registro fue eliminado");
//                    FacesContext.getCurrentInstance().addMessage(null, msg);
//                    btnEditDisabled = true;
//                    btnRemoveDisabled = true;
//                    break;
//                }
//            }
//        }
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
