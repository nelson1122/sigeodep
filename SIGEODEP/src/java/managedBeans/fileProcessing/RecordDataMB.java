/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.fileProcessing;

import beans.connection.ConnectionJDBC;
import beans.enumerators.*;
import beans.errorsControl.ErrorControl;
import beans.relations.RelationVar;
import beans.relations.RelationsGroup;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import managedBeans.forms.HomicideMB;
import managedBeans.login.LoginMB;
import managedBeans.preload.FormsAndFieldsDataMB;
import model.dao.*;
import model.pojo.*;

/**
 *
 * @author santos
 */
@ManagedBean(name = "recordDataMB")
@SessionScoped
public class RecordDataMB implements Serializable {

    @EJB
    FormsFacade formsFacade;
    @EJB
    SourcesFacade sourcesFacade;
    @EJB
    FieldsFacade fieldsFacade;
    @EJB
    VulnerableGroupsFacade vulnerableGroupsFacade;
    @EJB
    PlacesFacade placesFacade;
    @EJB
    ActivitiesFacade activitiesFacade;
    @EJB
    MurderContextsFacade murderContextsFacade;
    @EJB
    IdTypesFacade idTypesFacade;
    @EJB
    TransportUsersFacade transportUsersFacade;
    @EJB
    NeighborhoodsFacade neighborhoodsFacade;
    @EJB
    GendersFacade gendersFacade;
    @EJB
    TransportTypesFacade transportTypesFacade;
    @EJB
    PrecipitatingFactorsFacade precipitatingFactorsFacade;
    @EJB
    AggressorGendersFacade aggressorGendersFacade;
    @EJB
    VictimCharacteristicsFacade victimCharacteristicsFacade;
    @EJB
    NonFatalPlacesFacade nonFatalPlacesFacade;
    @EJB
    DomesticViolenceDataSourcesFacade domesticViolenceDataSourcesFacade;
    @EJB
    UseAlcoholDrugsFacade useAlcoholDrugsFacade;
    @EJB
    IntentionalitiesFacade intentionalitiesFacade;
    @EJB
    AreasFacade areasFacade;
    @EJB
    DiagnosesFacade diagnosesFacade;
    @EJB
    TransportCounterpartsFacade transportCounterpartsFacade;
    @EJB
    JobsFacade jobsFacade;
    @EJB
    RelatedEventsFacade relatedEventsFacade;
    @EJB
    InvolvedVehiclesFacade involvedVehiclesFacade;
    @EJB
    DaysFacade daysFacade;
    @EJB
    AmpmFacade ampmFacade;
    @EJB
    WeaponTypesFacade weaponTypesFacade;
    @EJB
    AccidentClassesFacade accidentClassesFacade;
    @EJB
    Boolean3Facade boolean3Facade;
    @EJB
    Boolean2Facade boolean2Facade;
    @EJB
    RelationshipsToVictimFacade relationshipsToVictimFacade;
    @EJB
    DestinationsOfPatientFacade destinationsOfPatientFacade;
    @EJB
    AlcoholLevelsFacade alcoholLevelsFacade;
    @EJB
    ServiceTypesFacade serviceTypesFacade;
    @EJB
    EthnicGroupsFacade ethnicGroupsFacade;
    @EJB
    AgeTypesFacade ageTypesFacade;
    @EJB
    ProtectiveMeasuresFacade protectiveMeasuresFacade;
    @EJB
    MechanismsFacade mechanismsFacade;
    @EJB
    ContextsFacade contextsFacade;
    @EJB
    RoadTypesFacade roadTypesFacade;
    @EJB
    RelationGroupFacade relationGroupFacade;
    @EJB
    RelationVariablesFacade relationVariablesFacade;
    @EJB
    RelationValuesFacade relationValuesFacade;
    @EJB
    FatalInjuryAccidentFacade fatalInjuryAccidentFacade;
    @EJB
    FatalInjuryMurderFacade fatalInjuryMurderFacade;
    @EJB
    FatalInjuryTrafficFacade fatalInjuryTrafficFacade;
    @EJB
    FatalInjurySuicideFacade fatalInjurySuicideFacade;
    @EJB
    FatalInjuriesFacade fatalInjuriesFacade;
    @EJB
    NonFatalInjuriesFacade nonFatalInjuriesFacade;
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
    InjuriesFacade injuriesFacade;
    @EJB
    SecurityElementsFacade securityElementsFacade;
    @EJB
    AbuseTypesFacade abuseTypesFacade;
    @EJB
    AggressorTypesFacade aggressorTypesFacade;
    @EJB
    AnatomicalLocationsFacade anatomicalLocationsFacade;
    @EJB
    KindsOfInjuryFacade kindsOfInjuryFacade;
    @EJB
    NonFatalDataSourcesFacade nonFatalDataSourcesFacade;
    @EJB
    InsuranceFacade insuranceFacade;
    @EJB
    TagsFacade tagsFacade;
    @EJB
    HealthProfessionalsFacade healthProfessionalsFacade;
    @EJB
    UsersFacade usersFacade;
    @EJB
    ActionsToTakeFacade actionsToTakeFacade;
    @EJB
    CounterpartInvolvedVehicleFacade counterpartInvolvedVehicleFacade;
    @EJB
    CounterpartServiceTypeFacade counterpartServiceTypeFacade;
    @EJB
    SuicideMechanismsFacade suicideMechanismsFacade;
    @EJB
    AccidentMechanismsFacade accidentMechanismsFacade;
    @EJB
    GenNnFacade genNnFacade;
    private RelationshipOfVariablesMB relationshipOfVariablesMB;
    private RelationsGroup currentRelationsGroup;
    private FormsAndFieldsDataMB formsAndFieldsDataMB;
    private StoredRelationsMB storedRelationsMB;
    private UploadFileMB uploadFileMB;
    private LoginMB loginMB;
    private ErrorsControlMB errorsControlMB;
    private String[] columnsNames;
    private String nameForm = "";
    private ConnectionJDBC conx = null;//conexion sin persistencia a postgres   
    private RelationVar relationVar;
    private int tuplesNumber;
    private int tuplesProcessed;
    private boolean btnRegisterDataDisabled = true;
    private boolean btnValidateDisabled = true;
    private Victims newVictim;
    //private int MaxId;
    private NonFatalInjuries newNonFatalInjury;
    private FatalInjuries newFatalInjurie;
    private FatalInjuryMurder newFatalInjuryMurder;
    private FatalInjurySuicide newFatalInjurySuicide;
    private FatalInjuryTraffic newFatalInjuryTraffic;
    private FatalInjuryAccident newFatalInjuryAccident;
    private NonFatalDomesticViolence newNonFatalDomesticViolence;
    private Injuries selectInjuryFile;//tipo de lesion que me dice el archivo
    private Injuries selectInjuryDetermined;//tipo de lesion que detemino por campos
    private NonFatalTransport newNonFatalTransport;
    private NonFatalInterpersonal newNonFatalInterpersonal;
    private NonFatalSelfInflicted newNonFatalSelfInflicted;
    private List<ActionsToTake> actionsToTakeList;
    private List<SecurityElements> securityElementList;
    private List<AbuseTypes> abuseTypesList;
    private List<AggressorTypes> aggressorTypesList;
    private List<AnatomicalLocations> anatomicalLocationsList;
    private List<KindsOfInjury> kindsOfInjurysList;
    private List<Diagnoses> diagnosesList;
    private List<VulnerableGroups> vulnerableGroupList;
    private List<Others> othersList;
    CounterpartInvolvedVehicle newCounterpartInvolvedVehicle;
    CounterpartServiceType newCounterpartServiceType;
    List<CounterpartServiceType> serviceTypesList;
    List<CounterpartInvolvedVehicle> involvedVehiclesList;
    private Others newOther;
    private String value = "";
    private String name = "";
    private String surname = "";
    private String intencionality = "";
    private String fechaev = "";//fecha
    private String fechacon = "";//fecha
    private String dia = "";//dia evento
    private String mes = "";//mes evento
    private String ao = "";//año del evento
    private String diacon = "";//dia de la semana cnsulta                                
    private String dia1 = "";//dia de la consulta
    private String mes1 = "";//mes de la consulta
    private String ao1 = "";//año de la consulta
    private String horas = "";//hora evento
    private String minutos = "";//minuto evento
    private String ampm = "";//ampm evento
    private String horas1 = "";//hora consulta
    private String minutos1 = "";//minuto consulta
    private String ampm1 = "";//ampm consulta                
    private String narrative = "";//narracion
    private int hourInt = 0;
    private int minuteInt = 0;
    private Date n;
    boolean booleanValue;
    private String[] splitArray;
    private ResultSet resultSetFileData;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Date currentDate;
    //private int maxTag;
    private Tags newTag;//(maxTag, uploadFileMB.getNameFile(), uploadFileMB.getNameFile());
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //MANEJO E LA BARRA DE PROGRESO DEL ALMACENAMIENTO ---------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    private Integer progress;
    private Integer progressValidate;
    private int errorsNumber = 0;
    private String currentSource = "";
    boolean continueProcces = false;

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public void onComplete() {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se ha realizado la adición de " + String.valueOf(tuplesProcessed)
                + " registros, para finalizar guarde si lo desea la configuración de relaciones actual o reinicie para realizar la carga de registros de otro archivo"));
    }

    

    public void onCompleteValidate() {
        if (errorsNumber != 0) {
            btnRegisterDataDisabled = true;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Errores", "Existen: " + String.valueOf(errorsNumber) + " valores que no superaron el proceso de validación, dirijase a la sección de errores para su corrección"));
        } else {
            btnRegisterDataDisabled = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se ha superado el proceso de validacion, presione el boton registrar datos para que sean almacenados."));
        }
    }

    public void cancel() {
        progress = null;
        progressValidate = null;
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES DE PROPOSITO GENERAL ---------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public RecordDataMB() {
    }

    //@PostConstruct //ejecutar despues de el constructor
    public void reset() {
        /*
         * Cargar el formulario con los valores iniciales
         */
        btnRegisterDataDisabled = true;
        btnValidateDisabled = true;
    }

    private boolean relationshipsRequired() {
        boolean noErrors = true;
        currentRelationsGroup = relationshipOfVariablesMB.getCurrentRelationsGroup();//tomo el grupo de relaciones de valores y de variables
        errorsControlMB.setErrorControlArrayList(new ArrayList<ErrorControl>());//arreglo de errores            
        RelationVar newRelationVar = new RelationVar("", "", "error", false, "");
        switch (FormsEnum.convert(nameForm.replace("-", "_"))) {//tipo de relacion
            case SCC_F_028:
            case SCC_F_029:
            case SCC_F_030:
            case SCC_F_031:
                //RELACION PARA FECHA DE EVENTO
                if (currentRelationsGroup.findRelationVar2("fechah") == null) {
                    if (currentRelationsGroup.findRelationVar2("dia") == null) {
                        noErrors = false;
                    }
                    if (currentRelationsGroup.findRelationVar2("mes") == null) {
                        noErrors = false;
                    }
                    if (currentRelationsGroup.findRelationVar2("ao") == null) {
                        noErrors = false;
                    }
                }
                if (noErrors == false) {//no se puede determinar la fecha
                    errorsControlMB.addError(new ErrorControl(newRelationVar, "REQUIRED VALIDATION", "No existe manera de determinar la fecha del evento", "Diríjase a la sección relacion de variables y realice la asociacion correspondiente para la variable esperada (fechah) o las variables esperadas (dia,mes,ao)"));
                    errorsNumber++;
                }
                //RELACION PARA TIPO DE IDENTIFICACION                
                if (currentRelationsGroup.findRelationVar2("nid") == null) {
                    noErrors = false;
                    errorsControlMB.addError(new ErrorControl(newRelationVar, "REQUIRED VALIDATION", "No existe manera de determinar la identificacion de la víctima", "Diríjase a la sección relacion de variables y realice la asociacion correspondiente para la variable esperada (nid)"));
                    errorsNumber++;
                }
                break;
            case SCC_F_033:
                //RELACION PARA FECHA DE EVENTO
                if (currentRelationsGroup.findRelationVar2("fecha1") == null) {
                    if (currentRelationsGroup.findRelationVar2("fecha") == null) {
                        noErrors = false;
                    }
                    if (currentRelationsGroup.findRelationVar2("mes") == null) {
                        noErrors = false;
                    }
                    if (currentRelationsGroup.findRelationVar2("ao") == null) {
                        noErrors = false;
                    }
                }
                if (noErrors == false) {//no se puede determinar la fecha
                    errorsControlMB.addError(new ErrorControl(newRelationVar, "REQUIRED VALIDATION", "No existe manera de determinar la fecha del evento", "Diríjase a la sección relacion de variables y realice la asociacion correspondiente para la variable esperada (fecha1) o  las variables esperadas (fecha,mes,ao)"));
                    errorsNumber++;
                }

                //RELACION PARA NUMERO DE IDENTIFICACION                
                if (currentRelationsGroup.findRelationVar2("numero") == null) {
                    noErrors = false;
                    errorsControlMB.addError(new ErrorControl(newRelationVar, "REQUIRED VALIDATION", "No existe manera de determinar la identificacion de la víctima", "Diríjase a la sección relacion de variables y realice la asociacion correspondiente para la variable esperada (numero)"));
                    errorsNumber++;
                }
                break;

            case SCC_F_032:
                //RELACION PARA FECHA DE EVENTO
                if (currentRelationsGroup.findRelationVar2("fechaev") == null) {
                    if (currentRelationsGroup.findRelationVar2("dia") == null) {
                        noErrors = false;
                    }
                    if (currentRelationsGroup.findRelationVar2("mes") == null) {
                        noErrors = false;
                    }
                    if (currentRelationsGroup.findRelationVar2("ao") == null) {
                        noErrors = false;
                    }
                }
                if (noErrors == false) {//no se puede determinar la fecha
                    errorsControlMB.addError(new ErrorControl(newRelationVar, "REQUIRED VALIDATION", "No existe manera de determinar la fecha del evento", "Diríjase a la sección relacion de variables y realice la asociacion correspondiente para la variable esperada (fechaev) o las variables esperadas (dia,mes,ao)"));
                    errorsNumber++;
                }
                //RELACION PARA LA INTENCIONALIDAD
                if (currentRelationsGroup.findRelationVar2("intenci") == null) {
                    noErrors = false;
                    errorsControlMB.addError(new ErrorControl(newRelationVar, "REQUIRED VALIDATION", "No existe manera de determinar la intencionalidad", "Diríjase a la sección relacion de variables y realice la asociacion correspondiente para la variable esperada (intenci)"));
                    errorsNumber++;
                }

                //RELACION PARA NUMERO DE IDENTIFICACION                
                if (currentRelationsGroup.findRelationVar2("nid") == null) {
                    noErrors = false;
                    errorsControlMB.addError(new ErrorControl(newRelationVar, "REQUIRED VALIDATION", "No existe manera de determinar la identificacion de la víctima", "Diríjase a la sección relacion de variables y realice la asociacion correspondiente para la variable esperada (nid)"));
                    errorsNumber++;
                }
                break;
        }
        return noErrors;
    }

    public void btnValidateClick() {
        /*
         * click sobre el boton iniciar validacion aqui se generaran los errores
         * que salgan de analizar el archivo
         */
        errorsNumber = 0;
        progressValidate = 0;
        continueProcces = true;

        currentRelationsGroup = relationshipOfVariablesMB.getCurrentRelationsGroup();//tomo el grupo de relaciones de valores y de variables

        if (!relationshipsRequired()) {
            continueProcces = false;
            progressValidate = 100;
        }
        if (currentRelationsGroup == null) {
            continueProcces = false;
            progressValidate = 100;
        }

        if (continueProcces) {
            try {
                conx = new ConnectionJDBC();
                conx.connect();
                resultSetFileData = conx.consult("SELECT COUNT(*) FROM temp; ");
                resultSetFileData.next();
                tuplesNumber = resultSetFileData.getInt(1);//numero de tuplas a procesar
                tuplesProcessed = 0;//numero de tuplas procesdas            
                progressValidate = 0;

                resultSetFileData = conx.consult("SELECT * FROM temp ORDER BY id; ");//resultSetFileData contendra todos los registros de el archivo(tabla temp)            
                //columnsNumber = resultSetFileData.getMetaData().getColumnCount();
                errorsNumber = 0;
                int pos = 0;
                columnsNames = new String[resultSetFileData.getMetaData().getColumnCount()];//creo un arreglo con los nombres de las columnas
                for (int i = 1; i <= resultSetFileData.getMetaData().getColumnCount(); i++) {
                    columnsNames[pos] = resultSetFileData.getMetaData().getColumnName(i);
                    pos++;
                }
                errorsControlMB.setErrorControlArrayList(new ArrayList<ErrorControl>());//arreglo de errores            
                int currentNumberOfRow = 1;
                while (resultSetFileData.next()) {//recorro cada uno de los registros de la tabla temp                                                
                    //VARIABLES PARA SABER SI ES POSIBLE DETERMINAR LA INTENCIONALIDAD, Y FECHA DEL EVENTO
                    fechaev = "";
                    dia = "";
                    mes = "";
                    ao = "";
                    fechacon = "";
                    dia1 = "";
                    mes1 = "";
                    ao1 = "";
                    intencionality = "";
//                    if(currentNumberOfRow==27){
//                        intencionality = "";
//                    }

                    for (int i = 0; i < columnsNames.length; i++) {//recorro cada una de las columnas de cada registro                    
                        relationVar = currentRelationsGroup.findRelationVar(columnsNames[i]);//determino la relacion de variables
                        if (relationVar != null) {
                            value = "";
                            switch (DataTypeEnum.convert(relationVar.getFieldType())) {//tipo de relacion
                                case text:
                                    break;
                                case integer:
                                    value = isNumeric(resultSetFileData.getString(columnsNames[i]));
                                    //System.out.println("Validando Entero: " + resultSetFileData.getString(columnsNames[i]) + "   Resultado: " + value);
                                    if (value == null) {
                                        errorsNumber++;//error = "No es entero";
                                        errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), String.valueOf(currentNumberOfRow), "integer"));
                                    }
                                    break;
                                case age:
                                    value = isAge(resultSetFileData.getString(columnsNames[i]));
                                    //System.out.println("Validando Age: " + resultSetFileData.getString(columnsNames[i]) + "   Resultado: " + value);
                                    if (value == null) {
                                        errorsNumber++;//error = "fecha no corresponde al formato";
                                        errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), String.valueOf(currentNumberOfRow), "age"));
                                    }
                                    break;
                                case date:
                                    value = isDate(resultSetFileData.getString(columnsNames[i]), relationVar.getDateFormat());
                                    if (relationVar.getNameExpected().compareTo("fechah") == 0
                                            || relationVar.getNameExpected().compareTo("fechaev") == 0
                                            || relationVar.getNameExpected().compareTo("fecha1") == 0) {
                                        fechaev = value;
                                    }
                                    if (relationVar.getNameExpected().compareTo("fechacon") == 0
                                            || relationVar.getNameExpected().compareTo("fecha3") == 0) {
                                        fechacon = value;
                                    }
                                    if (value == null) {
                                        errorsNumber++;//error = "fecha no corresponde al formato";
                                        errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), String.valueOf(currentNumberOfRow), "date"));
                                    }

                                    break;
                                case military:
                                    value = isMilitary(resultSetFileData.getString(columnsNames[i]));
                                    //System.out.println("Validando Militar: " + resultSetFileData.getString(columnsNames[i]) + "   Resultado: " + value);
                                    if (value == null) {
                                        errorsNumber++;//la hora militar no puede ser determinada
                                        errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), String.valueOf(currentNumberOfRow), "military"));
                                    }
                                    break;
                                case hour:
                                    value = isHour(resultSetFileData.getString(columnsNames[i]));
                                    //System.out.println("Validando Hora: " + resultSetFileData.getString(columnsNames[i]) + "   Resultado: " + value);
                                    if (value == null) {
                                        errorsNumber++;//la hora militar no puede ser determinada
                                        errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), String.valueOf(currentNumberOfRow), "hour"));
                                    }
                                    break;
                                case minute:
                                    value = isMinute(resultSetFileData.getString(columnsNames[i]));
                                    //System.out.println("Validando Minuto: " + resultSetFileData.getString(columnsNames[i]) + "   Resultado: " + value);
                                    if (value == null) {
                                        errorsNumber++;//la hora militar no puede ser determinada
                                        errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), String.valueOf(currentNumberOfRow), "minute"));
                                    }
                                    break;
                                case day:
                                    value = isDay(resultSetFileData.getString(columnsNames[i]));
                                    //System.out.println("Validando Dia: " + resultSetFileData.getString(columnsNames[i]) + "   Resultado: " + value);
                                    if (relationVar.getNameExpected().compareTo("dia") == 0 || relationVar.getNameExpected().compareTo("fecha") == 0) {
                                        dia = value;
                                    }
                                    if (relationVar.getNameExpected().compareTo("dia1") == 0) {
                                        dia1 = value;
                                    }
                                    if (value == null) {
                                        errorsNumber++;//la hora militar no puede ser determinada
                                        errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), String.valueOf(currentNumberOfRow), "day"));
                                    }
                                    break;
                                case month:
                                    value = isMonth(resultSetFileData.getString(columnsNames[i]));
                                    //System.out.println("Validando Mes: " + resultSetFileData.getString(columnsNames[i]) + "   Resultado: " + value);
                                    if (relationVar.getNameExpected().compareTo("mes") == 0) {
                                        mes = value;
                                    }
                                    if (relationVar.getNameExpected().compareTo("mes1") == 0) {
                                        mes1 = value;
                                    }
                                    if (value == null) {
                                        errorsNumber++;//la hora militar no puede ser determinada
                                        errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), String.valueOf(currentNumberOfRow), "month"));
                                    }
                                    break;
                                case year:
                                    if (relationVar.getNameExpected().compareTo("ao") == 0) {
                                        ao = value;
                                    }
                                    if (relationVar.getNameExpected().compareTo("ao1") == 0) {
                                        ao1 = value;
                                    }
                                    value = isYear(resultSetFileData.getString(columnsNames[i]));
                                    //System.out.println("Validando Año: " + resultSetFileData.getString(columnsNames[i]) + "   Resultado: " + value);
                                    if (value == null) {
                                        errorsNumber++;//la hora militar no puede ser determinada
                                        errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), String.valueOf(currentNumberOfRow), "year"));
                                    }
                                    break;
                                case percentage:
                                    value = isPercentage(resultSetFileData.getString(columnsNames[i]));
                                    //System.out.println("Validando porcentaje: " + resultSetFileData.getString(columnsNames[i]) + "   Resultado: " + String.valueOf(value));
                                    if (value == null) {
                                        errorsNumber++;//el porcentaje no puede ser determinado
                                        errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), String.valueOf(currentNumberOfRow), "percentage"));
                                    }
                                    break;
                                case NOVALUE:
                                    value = isCategorical(resultSetFileData.getString(relationVar.getNameFound()), relationVar);
                                    //System.out.println("Validando Categoria: " + resultSetFileData.getString(relationVar.getNameFound()) + "   Resultado: " + value);
                                    if (relationVar.getNameExpected().compareTo("intenci") == 0) {
                                        intencionality = value;
                                    }

                                    if (value == null) {
                                        errorsNumber++;//error = "no esta en la categoria ni es un valor descartado";
                                        errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), String.valueOf(currentNumberOfRow), formsAndFieldsDataMB.variableDescription(relationVar.getNameExpected())));
                                    }
                                    break;
                            }
                        }
                    }

                    //..........................................................
                    //verifico que pueda ser determinada la fecha e intencionalidad
                    boolean existDateEvent = true;
                    fechaev = haveData(fechaev);
                    dia = haveData(dia);
                    mes = haveData(mes);
                    ao = haveData(ao);
                    dia1 = haveData(dia1);
                    mes1 = haveData(mes1);
                    ao1 = haveData(ao1);
                    intencionality = haveData(intencionality);
                    if (fechaev == null) {
                        if (dia == null || mes == null || ao == null) {
                            if (fechacon == null) {
                                if (dia1 == null || mes1 == null || ao1 == null) {
                                    existDateEvent = false;
                                }
                            }
                        }
                    }

                    switch (FormsEnum.convert(nameForm.replace("-", "_"))) {//tipo de relacion                        
                        case SCC_F_028:
                        case SCC_F_029:
                        case SCC_F_030:
                        case SCC_F_031:
                            //DETERMINAR FECHA DE EVENTO                                
                            if (existDateEvent == false) {//no se puede determinar la fecha
                                relationVar = currentRelationsGroup.findRelationVar2("fechah");//determino la relacion de variables
                                errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), String.valueOf(currentNumberOfRow), "fechah"));
                                errorsNumber++;
                            }
                            break;
                        case SCC_F_032:
                            //RELACION PARA LA INTENCIONALIDAD
                            if (intencionality == null) {
                                relationVar = currentRelationsGroup.findRelationVar2("intenci");//determino la relacion de variables
                                errorsControlMB.addError(new ErrorControl(relationVar, " ", String.valueOf(currentNumberOfRow), "intencionalidad"));
                                errorsNumber++;
                            }
                        case SCC_F_033:
                            //DETERMINAR FECHA DE EVENTO                                
                            if (existDateEvent == false) {//no se puede determinar la fecha
                                relationVar = currentRelationsGroup.findRelationVar2("fechacon");//determino la relacion de variables
                                errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), String.valueOf(currentNumberOfRow), "fechacon"));
                                errorsNumber++;
                            }
                            break;
                    }

                    //..........................................................
                    currentNumberOfRow++;
                    tuplesProcessed++;
                    progressValidate = (int) (tuplesProcessed * 100) / tuplesNumber;
                    System.out.println("PROGRESO VALIDANDO: " + String.valueOf(progressValidate));
                }
                progress = 100;
                System.out.println("PROGRESO: " + String.valueOf(progressValidate));
                errorsControlMB.setSizeErrorsList(errorsNumber);
                errorsControlMB.updateErrorsArrayList();
                progress = 0;
                conx.disconnect();
            } catch (SQLException ex) {
                System.out.println("error: " + ex.toString());
                conx.disconnect();
            }
        }
    }

    private String haveData(String a) {
        if (a != null) {
            if (a.length() == 0) {
                a = null;
            }
        }
        return a;
    }

    public void registerSCC_F_028() {
        /**
         * *********************************************************************
         * CARGA DE REGISTROS DE LA FICHA HOMICIDIOS
         * ********************************************************************
         */
        tuplesNumber = 0;
        tuplesProcessed = 0;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            conx = new ConnectionJDBC();
            conx.connect();
            resultSetFileData = conx.consult("SELECT COUNT(*) FROM temp; ");
            resultSetFileData.next();
            tuplesNumber = resultSetFileData.getInt(1);//NUMERO DE TUPLAS
            resultSetFileData = conx.consult("SELECT * FROM temp ORDER BY id; ");//resultSetFileData contendra todos los registros de el archivo(tabla temp)
            conx.disconnect();
            progress = 0;
            columnsNames = new String[resultSetFileData.getMetaData().getColumnCount()];//NOMBRES DE LAS COLUMNAS
            int pos = 0;
            for (int i = 1; i <= resultSetFileData.getMetaData().getColumnCount(); i++) {
                columnsNames[pos] = resultSetFileData.getMetaData().getColumnName(i);
                pos++;
            }

            newTag = new Tags();//VARIABLES PARA CONJUNTOS DE REGISTROS
            newTag.setTagId(tagsFacade.findMax() + 1);
            newTag.setTagName(uploadFileMB.getTagName());
            newTag.setTagFileInput(uploadFileMB.getNameFile());
            newTag.setTagFileStored(uploadFileMB.getNameFile());
            newTag.setFormId(formsFacade.find(nameForm));
            tagsFacade.create(newTag);
            while (resultSetFileData.next()) {//recorro cada uno de los registros de la tabla temp                    
                newVictim = new Victims();
                newVictim.setVictimId(victimsFacade.findMax() + 1);
                newVictim.setVictimClass(Short.parseShort("1"));
                newFatalInjurie = new FatalInjuries();
                newFatalInjurie.setFatalInjuryId(fatalInjuriesFacade.findMax() + 1);
                newFatalInjurie.setInputTimestamp(new Date());
                newFatalInjurie.setInjuryId(injuriesFacade.find((short) 10));//es 10 por ser homicidio
                newFatalInjurie.setUserId(usersFacade.find(1));//usuario que se encuentre logueado
                newFatalInjurie.setVictimId(newVictim);
                newFatalInjuryMurder = new FatalInjuryMurder();
                newFatalInjuryMurder.setFatalInjuryId(newFatalInjurie.getFatalInjuryId());
                newVictim.setTagId(tagsFacade.find(newTag.getTagId()));
                value = "";
                name = "";
                surname = "";
                dia = "";//dia evento
                mes = "";//mes evento
                ao = "";//año del evento
                diacon = "";//dia de la semana cnsulta                                
                dia1 = "";//dia de la consulta
                mes1 = "";//mes de la consulta
                ao1 = "";//año de la consulta
                horas = "";//hora evento
                minutos = "";//minuto evento
                ampm = "";//ampm evento
                horas1 = "";//hora consulta
                minutos1 = "";//minuto consulta
                ampm1 = "";//ampm consulta
                hourInt = 0;
                minuteInt = 0;
                narrative = "";
                for (int posCol = 0; posCol < columnsNames.length; posCol++) {
                    value = null;
                    //DETERMINO QUE VALOR VOY A INGRESAR HACIENDO USO DE LAS FUNCIONES DE VALIDACION isNumeric,isAge... etc
                    relationVar = currentRelationsGroup.findRelationVar(columnsNames[posCol]);//determino la relacion de variables
                    //if (columnsNames[posCol].compareTo("edadcantid") == 0 && tuplesProcessed > 309) { columnsNames[posCol] = "edadcantid"; }
                    if (relationVar != null) {
                        switch (DataTypeEnum.convert(relationVar.getFieldType())) {//tipo de relacion
                            case text:
                                value = resultSetFileData.getString(columnsNames[posCol]);
                                break;
                            case integer:
                                value = isNumeric(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case age:
                                value = isAge(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case date:
                                value = isDate(resultSetFileData.getString(columnsNames[posCol]), relationVar.getDateFormat());
                                break;
                            case military:
                                value = isMilitary(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case hour:
                                value = isHour(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case minute:
                                value = isMinute(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case day:
                                value = isDay(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case month:
                                value = isMonth(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case year:
                                value = isYear(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case percentage:
                                value = isPercentage(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case level:
                                value = isLevel(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case NOVALUE:
                                value = isCategorical(resultSetFileData.getString(relationVar.getNameFound()), relationVar);
                                break;
                        }
                    }

                    continueProcces = false;
                    if (value != null) {
                        if (value.trim().length() != 0) {
                            continueProcces = true;
                        }
                    }
                    if (continueProcces) {
                        switch (SCC_F_028Enum.convert(relationVar.getNameExpected())) {
                            // ************************************************DATOS PARA LA TABLA victims                                
//                            case field1:
//                                break;
//                            case clave:
//                                break;
//                            case ficha:
//                                break;
                            case departamen:
                                break;
                            case codigodepa:
                                break;
                            case municipio:
                                splitArray = value.split("-");
                                if (splitArray.length == 2) {
                                    newVictim.setResidenceDepartment(Short.parseShort(splitArray[0]));
                                    newVictim.setResidenceMunicipality(Short.parseShort(splitArray[1]));
                                }
                                break;
                            case codigomuni:
                                splitArray = value.split("-");
                                if (splitArray.length == 2) {
                                    newVictim.setResidenceDepartment(Short.parseShort(splitArray[0]));
                                    newVictim.setResidenceMunicipality(Short.parseShort(splitArray[1]));
                                }
                                break;
                            case codigo:
                                newFatalInjurie.setCode(value);
                                break;
                            case dia://dia del evento
                                dia = value;
                                break;
                            case mes://mes evento
                                mes = value;
                                break;
                            case ao://año del evento
                                ao = value;
                                break;
                            case fechah:
                                try {
                                    currentDate = dateFormat.parse(value);
                                    newFatalInjurie.setInjuryDate(currentDate);
                                } catch (ParseException ex) {
                                }
                                break;
                            case horas:
                                horas = value;
                                break;
                            case minutos:
                                minutos = value;
                                break;
                            case ampm:
                                ampm = value;
                                break;
                            case horamil:
                                n = new Date();
                                hourInt = Integer.parseInt(value.substring(0, 2));
                                minuteInt = Integer.parseInt(value.substring(2, 4));
                                n.setHours(hourInt);
                                n.setMinutes(minuteInt);
                                n.setSeconds(0);
                                newFatalInjurie.setInjuryTime(n);
                                break;
//                            case hrshorasin://hora si determinar
//                                si no hay hora es si determinar                                
//                                break;
                            case direccion:
                                newFatalInjurie.setInjuryAddress(value);
                                break;
                            case barrio:
                                newFatalInjurie.setInjuryNeighborhoodId(Integer.parseInt(value));
                                break;
                            case codbar:
                                newFatalInjurie.setInjuryNeighborhoodId(Integer.parseInt(value));
                                break;
                            case area://ZONA URBANA O RURAL //SE DETERMINA CON EL BARRIO
                                newFatalInjurie.setAreaId(areasFacade.find(Short.parseShort(value)));
                                break;
                            case lugarhecho:
                                newFatalInjurie.setInjuryPlaceId(placesFacade.find(Short.parseShort(value)));
                                break;
                            case diasem:
                                newFatalInjurie.setInjuryDayOfWeek(daysFacade.find(Short.parseShort(value)).getDaysName());
                                break;
                            case victimas:
                                newFatalInjurie.setVictimNumber(Short.parseShort(value));
                                break;
                            case nombres:
                                name = value;
                                break;
                            case apellidos:
                                surname = value;
                                break;
                            case sexo:
                                newVictim.setGenderId(gendersFacade.find(Short.parseShort(value)));
                                break;
                            case tipoedad:
                                newVictim.setGenderId(gendersFacade.find(Short.parseShort(value)));
                                break;
                            case edad:
                                newVictim.setVictimAge(Short.parseShort(value));
                                break;
                            case ocupacion:
                                newVictim.setJobId(jobsFacade.find(Short.parseShort(value)));
                                break;
                            case munres:
                                splitArray = value.split("-");
                                if (splitArray.length == 2) {
                                    newVictim.setResidenceDepartment(Short.parseShort(splitArray[0]));
                                    newVictim.setResidenceMunicipality(Short.parseShort(splitArray[1]));
                                }
                                break;
                            case residencia:
                                newVictim.setVictimNeighborhoodId(neighborhoodsFacade.find(Integer.parseInt(value)));
                                break;
                            case tipoid:
                                newVictim.setTypeId(idTypesFacade.find(Short.parseShort(value)));
                                break;
                            case nroid:
                                newVictim.setVictimNid(value);
                                break;
                            case procedenci:
                                splitArray = value.split("-");
                                if (splitArray.length == 1) {
                                    newFatalInjurie.setVictimPlaceOfOrigin(value + "-0-0");
                                }
                                if (splitArray.length == 2) {
                                    newFatalInjurie.setVictimPlaceOfOrigin(value + "-0");
                                }
                                if (splitArray.length == 3) {
                                    newFatalInjurie.setVictimPlaceOfOrigin(value);
                                }

                                break;
                            case tipoarma:
                                newFatalInjuryMurder.setWeaponTypeId(weaponTypesFacade.find(Short.parseShort(value)));
                                break;
                            case contexto:
                                newFatalInjuryMurder.setMurderContextId(murderContextsFacade.find(Short.parseShort(value)));
                                break;
                            case narrac:
                            case narrac1:
                            case narrac2:
                                narrative = narrative + " " + value;
                                break;
                            case n24nivelde:
                                newFatalInjurie.setAlcoholLevelVictim(Short.parseShort(value));
                                break;
                            case mgsindato:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 2));
                                }
                                break;
                            case pendiente:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 4));
                                }
                                break;
                            case ns:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 3));
                                }
                                break;
                            case negativo:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 5));
                                }
                                break;
                            default:
                        }
                    }
                }

                //DETERMINAR AREA SI HAY BARRIO
                if (newFatalInjurie.getAreaId() == null) {
                    if (newFatalInjurie.getInjuryNeighborhoodId() != null) {
                        Neighborhoods ne = neighborhoodsFacade.find(newFatalInjurie.getInjuryNeighborhoodId());
                        short neType = Short.parseShort(ne.getNeighborhoodType().toString());
                        newFatalInjurie.setAreaId(areasFacade.find(neType));
                    }
                }

                //ASIGNO LA NARRACION DE LOS HECHOS
                if (narrative.trim().length() != 0) {
                    newFatalInjurie.setInjuryDescription(narrative);
                }

                //SI NO SE DETERMINA EL BARRIO SE COLOCA SIN DATO URBANO
                if (newVictim.getVictimNeighborhoodId() == null) {
                    newVictim.setVictimNeighborhoodId(neighborhoodsFacade.find((int) 52001));
                }
                //SI NO SE DETERMINA EL BARRIO SE COLOCA SIN DATO URBANO
                if (newFatalInjurie.getInjuryNeighborhoodId() == null) {
                    newFatalInjurie.setInjuryNeighborhoodId((int) 52001);
                }

                //SI NO HAY FECHA DE EVENTO TRATAR DE CALCULAR MEDIANTE LAS VARIABLES dia, mes, ao
                if (newFatalInjurie.getInjuryDate() == null) {
                    dia = haveData(dia);
                    mes = haveData(mes);
                    ao = haveData(ao);
                    if (dia != null && mes != null && ao != null) {
                        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                        Date fechaI;
                        fechaI = formato.parse(dia + "/" + mes + "/" + ao);
                        newFatalInjurie.setInjuryDate(fechaI);
                    }
                }

                //SI NO HAY HORA DE EVENTO TRATAR DE CALCULAR MEDIANTE LAS VARIABLES hora,minutos,ampm
                if (newFatalInjurie.getInjuryTime() == null) {
                    horas = haveData(horas);
                    minutos = haveData(minutos);
                    ampm = haveData(ampm);
                    if (horas != null && minutos != null && ampm != null) {
                        hourInt = Integer.parseInt(horas);
                        minuteInt = Integer.parseInt(minutos);
                        if (ampm.compareTo("2") == 0) {
                            hourInt = hourInt + 12;
                            if (hourInt == 24) {
                                hourInt = 0;
                            }
                        }
                        currentDate = new Date();
                        currentDate.setHours(hourInt);
                        currentDate.setMinutes(minuteInt);
                        currentDate.setSeconds(0);
                        newFatalInjurie.setInjuryTime(currentDate);
                    }
                }

                //SI NO HAY DIA DE LA SEMANA DEL EVENTO SE CALCULA
                if (newFatalInjurie.getInjuryDate() != null) {
                    if (newFatalInjurie.getInjuryDayOfWeek() == null) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(newFatalInjurie.getInjuryDate());
                        newFatalInjurie.setInjuryDayOfWeek(intToDay(cal.get(Calendar.DAY_OF_WEEK)));
                    }
                }

                //UNION DE NOMBRES Y APELLIOS
                name = name + " " + surname;
                if (name.trim().length() > 1) {
                    newVictim.setVictimName(name);
                }

                //SI NO SE DETERMINA LA EDAD VERIFICAR SI HAY FECHA DE NACIMIENTO
                if (newVictim.getVictimDateOfBirth() != null) {
                    if (newVictim.getVictimAge() == null) {
                        int birthMonths;
                        int eventMonths;

                        Calendar systemCalendar = Calendar.getInstance();
                        Calendar birthCalendar = Calendar.getInstance();
                        birthCalendar.setTime(newVictim.getVictimDateOfBirth());

                        try {//DETERMINO LA EDAD EN MESES
                            birthMonths = birthCalendar.get(Calendar.YEAR);
                            birthMonths = birthMonths * 12;
                            birthMonths = birthMonths + birthCalendar.get(Calendar.MONTH);
                            if (newFatalInjurie.getInjuryDate() != null) {//SE CALCULA EDAD SEGUN LA FECHA DE EVENTO
                                systemCalendar.setTime(newFatalInjurie.getInjuryDate());
                            }
                            eventMonths = systemCalendar.get(Calendar.YEAR);
                            eventMonths = eventMonths * 12;
                            eventMonths = eventMonths + systemCalendar.get(Calendar.MONTH);

                            int ageMonths = eventMonths - birthMonths;
                            if (ageMonths < 0) {
                                System.out.println("ERROR fecha de nacimiento mayor a la del sistema o evento: ");
                            } else {
                                //SI EXISTE EDAD Y NO HAY TIPO DE EDAD DETERMINARLA EN AÑOS
                                int ageYears = (int) (ageMonths / 12);
                                if (ageYears == 0) {
                                    ageYears = 1;
                                }
                                newVictim.setVictimAge((short) ageYears);
                                newVictim.setAgeTypeId((short) 1);
                                if (newVictim.getTypeId() == null) {
                                    if (ageYears >= 18) {
                                        newVictim.setTypeId(idTypesFacade.find((short) 1));
                                    } else {
                                        newVictim.setTypeId(idTypesFacade.find((short) 5));
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(HomicideMB.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                //DETERMINAR EL NUMERO DE IDENTIFICACION
                if (newVictim.getVictimNid() == null) {
                    newVictim.setVictimNid(String.valueOf(genNnFacade.findMax() + 1));
                    newVictim.setVictimClass((short) 2);//nn
                    newVictim.setTypeId(idTypesFacade.find((short) 6));//adulto sin identificacion
                    GenNn currentGenNn = genNnFacade.find(genNnFacade.findMax());
                    currentGenNn.setCodNn(genNnFacade.findMax() + 1);
                    genNnFacade.edit(currentGenNn);
                }

                //DETERMINAR TIPO DE IDENTIFICACION
                if (newVictim.getVictimNid() != null) {
                    if (newVictim.getTypeId() == null) {
                        newVictim.setTypeId(idTypesFacade.find((short) 1));//SI NI HAY EDAD DEJAR POR DEFECTO CEDULA
                    }
                }
                //TIPO DE LESION                
                newNonFatalInjury.setInjuryId(injuriesFacade.find((short) 10));//es 10 por ser homicidio
                //PERSISTO
                try {
                    //newFatalInjurie.setTagId(tagsFacade.find(newTag));
                    newVictim.setTagId(tagsFacade.find(newTag));
                    victimsFacade.create(newVictim);
                    fatalInjuriesFacade.create(newFatalInjurie);
                    fatalInjuryMurderFacade.create(newFatalInjuryMurder);
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
                tuplesProcessed++;
                progress = (int) (tuplesProcessed * 100) / tuplesNumber;
                System.out.println("PROGRESO INGRESANDO HOMICIDIO: " + String.valueOf(progress));
            }
            progress = 100;
            System.out.println("PROGRESO INGRESANDO HOMICIDIO: " + String.valueOf(progress));
        } catch (SQLException ex) {
            System.out.println("errorSQL INGRESADNDO HOMICIDIO: " + ex.toString());
        } catch (Exception ex) {
            System.out.println("EXCEPTION INGRESANDO HOMICIDIO: " + ex.toString() + "  " + relationVar.getNameExpected() + " " + String.valueOf(tuplesProcessed));
        }
    }

    public void registerSCC_F_029() {
        /**
         * ******************************************************************
         * CARGA DE REGISTROS DE LA FICHA MUERTES POR ACCIDENTE DE TRANSITO
         * *******************************************************************
         */
        tuplesNumber = 0;
        tuplesProcessed = 0;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            conx = new ConnectionJDBC();
            conx.connect();
            resultSetFileData = conx.consult("SELECT COUNT(*) FROM temp; ");
            resultSetFileData.next();
            tuplesNumber = resultSetFileData.getInt(1);//NUMERO DE TUPLAS
            resultSetFileData = conx.consult("SELECT * FROM temp ORDER BY id; ");//resultSetFileData contendra todos los registros de el archivo(tabla temp)
            conx.disconnect();
            progress = 0;
            columnsNames = new String[resultSetFileData.getMetaData().getColumnCount()];//NOMBRES DE LAS COLUMNAS
            int pos = 0;
            for (int i = 1; i <= resultSetFileData.getMetaData().getColumnCount(); i++) {
                columnsNames[pos] = resultSetFileData.getMetaData().getColumnName(i);
                pos++;
            }

            newTag = new Tags();//VARIABLES PARA CONJUNTOS DE REGISTROS
            newTag.setTagId(tagsFacade.findMax() + 1);
            newTag.setTagName(uploadFileMB.getTagName());
            newTag.setTagFileInput(uploadFileMB.getNameFile());
            newTag.setTagFileStored(uploadFileMB.getNameFile());
            newTag.setFormId(formsFacade.find(nameForm));
            tagsFacade.create(newTag);
            while (resultSetFileData.next()) {//recorro cada uno de los registros de la tabla temp                    
                newVictim = new Victims();
                newVictim.setVictimId(victimsFacade.findMax() + 1);
                newVictim.setVictimClass(Short.parseShort("1"));
                newFatalInjurie = new FatalInjuries();
                newFatalInjurie.setFatalInjuryId(fatalInjuriesFacade.findMax() + 1);
                newFatalInjurie.setInputTimestamp(new Date());
                newFatalInjurie.setInjuryId(injuriesFacade.find((short) 10));//es 10 por ser homicidio
                newFatalInjurie.setUserId(usersFacade.find(1));//usuario que se encuentre logueado
                newFatalInjurie.setVictimId(newVictim);
                newFatalInjuryTraffic = new FatalInjuryTraffic();
                newFatalInjuryTraffic.setFatalInjuryId(newFatalInjurie.getFatalInjuryId());
                newVictim.setTagId(tagsFacade.find(newTag.getTagId()));

                newFatalInjurie.setInjuryId(injuriesFacade.find((short) 12));//es 12 por ser suicidio
                serviceTypesList = new ArrayList<CounterpartServiceType>();
                involvedVehiclesList = new ArrayList<CounterpartInvolvedVehicle>();
                value = "";
                name = "";
                surname = "";
                dia = "";//dia evento
                mes = "";//mes evento
                ao = "";//año del evento
                diacon = "";//dia de la semana cnsulta                                
                dia1 = "";//dia de la consulta
                mes1 = "";//mes de la consulta
                ao1 = "";//año de la consulta
                horas = "";//hora evento
                minutos = "";//minuto evento
                ampm = "";//ampm evento
                horas1 = "";//hora consulta
                minutos1 = "";//minuto consulta
                ampm1 = "";//ampm consulta
                hourInt = 0;
                minuteInt = 0;
                narrative = "";
                for (int posCol = 0; posCol < columnsNames.length; posCol++) {
                    value = null;
                    //DETERMINO QUE VALOR VOY A INGRESAR HACIENDO USO DE LAS FUNCIONES DE VALIDACION isNumeric,isAge... etc
                    relationVar = currentRelationsGroup.findRelationVar(columnsNames[posCol]);//determino la relacion de variables
                    //if (columnsNames[posCol].compareTo("edadcantid") == 0 && tuplesProcessed > 309) { columnsNames[posCol] = "edadcantid"; }
                    if (relationVar != null) {
                        switch (DataTypeEnum.convert(relationVar.getFieldType())) {//tipo de relacion
                            case text:
                                value = resultSetFileData.getString(columnsNames[posCol]);
                                break;
                            case integer:
                                value = isNumeric(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case age:
                                value = isAge(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case date:
                                value = isDate(resultSetFileData.getString(columnsNames[posCol]), relationVar.getDateFormat());
                                break;
                            case military:
                                value = isMilitary(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case hour:
                                value = isHour(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case minute:
                                value = isMinute(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case day:
                                value = isDay(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case month:
                                value = isMonth(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case year:
                                value = isYear(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case percentage:
                                value = isPercentage(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case NOVALUE:
                                value = isCategorical(resultSetFileData.getString(relationVar.getNameFound()), relationVar);
                                break;
                        }
                    }

                    continueProcces = false;
                    if (value != null) {
                        if (value.trim().length() != 0) {
                            continueProcces = true;
                        }
                    }
                    if (continueProcces) {
                        switch (SCC_F_029Enum.convert(relationVar.getNameExpected())) {
                            // ************************************************DATOS PARA LA TABLA victims                                
//                            case field1:
//                                break;
//                            case clave:
//                                break;
//                            case ficha:
//                                break;
                            case departamen:
                                break;
                            case codigodepa:
                                break;
                            case municipio:
                                splitArray = value.split("-");
                                if (splitArray.length == 2) {
                                    newVictim.setResidenceDepartment(Short.parseShort(splitArray[0]));
                                    newVictim.setResidenceMunicipality(Short.parseShort(splitArray[1]));
                                }
                                break;
                            case codigomuni:
                                splitArray = value.split("-");
                                if (splitArray.length == 2) {
                                    newVictim.setResidenceDepartment(Short.parseShort(splitArray[0]));
                                    newVictim.setResidenceMunicipality(Short.parseShort(splitArray[1]));
                                }
                                break;
                            case codigo:
                                newFatalInjurie.setCode(value);
                                break;
                            case dia://dia del evento
                                dia = value;
                                break;
                            case mes://mes evento
                                mes = value;
                                break;
                            case ao://año del evento
                                ao = value;
                                break;
                            case fechah:
                                try {
                                    currentDate = dateFormat.parse(value);
                                    newFatalInjurie.setInjuryDate(currentDate);
                                } catch (ParseException ex) {
                                }
                                break;
                            case horas:
                                horas = value;
                                break;
                            case minutos:
                                minutos = value;
                                break;
                            case ampm:
                                ampm = value;
                                break;
                            case horamil:
                                n = new Date();
                                hourInt = Integer.parseInt(value.substring(0, 2));
                                minuteInt = Integer.parseInt(value.substring(2, 4));
                                n.setHours(hourInt);
                                n.setMinutes(minuteInt);
                                n.setSeconds(0);
                                newFatalInjurie.setInjuryTime(n);
                                break;
//                            case hrshorasin://hora si determinar
//                                si no hay hora es si determinar                                
//                                break;
                            case direccion:
                                newFatalInjurie.setInjuryAddress(value);
                                break;
                            case barrio:
                                newFatalInjurie.setInjuryNeighborhoodId(Integer.parseInt(value));
                                break;
                            case codbar:
                                newFatalInjurie.setInjuryNeighborhoodId(Integer.parseInt(value));
                                break;
                            case area://ZONA URBANA O RURAL //SE DETERMINA CON EL BARRIO
                                newFatalInjurie.setAreaId(areasFacade.find(Short.parseShort(value)));
                                break;
                            case claseacci:
                                newFatalInjuryTraffic.setAccidentClassId(accidentClassesFacade.find(Short.parseShort(value)));
                                break;
                            case diasem:
                                newFatalInjurie.setInjuryDayOfWeek(daysFacade.find(Short.parseShort(value)).getDaysName());
                                break;
                            case victimas:
                                newFatalInjurie.setVictimNumber(Short.parseShort(value));
                                break;
                            case lesionados:
                                newFatalInjuryTraffic.setNumberNonFatalVictims(Short.parseShort(value));
                                break;
                            case nombres:
                                name = value;
                                break;
                            case apellidos:
                                surname = value;
                                break;
                            case sexo:
                                newVictim.setGenderId(gendersFacade.find(Short.parseShort(value)));
                                break;
                            case tipoedad:
                                newVictim.setGenderId(gendersFacade.find(Short.parseShort(value)));
                                break;
                            case edad:
                                newVictim.setVictimAge(Short.parseShort(value));
                                break;
                            case ocupacion:
                                newVictim.setJobId(jobsFacade.find(Short.parseShort(value)));
                                break;
                            case munres:
                                splitArray = value.split("-");
                                if (splitArray.length == 2) {
                                    newVictim.setResidenceDepartment(Short.parseShort(splitArray[0]));
                                    newVictim.setResidenceMunicipality(Short.parseShort(splitArray[1]));
                                }
                                break;
                            case residencia:
                                newVictim.setVictimNeighborhoodId(neighborhoodsFacade.find(Integer.parseInt(value)));
                                break;
                            case tipoid:
                                newVictim.setTypeId(idTypesFacade.find(Short.parseShort(value)));
                                break;
                            case nroid:
                                newVictim.setVictimNid(value);
                                break;
                            case procedenci:
                                splitArray = value.split("-");
                                if (splitArray.length == 1) {
                                    newFatalInjurie.setVictimPlaceOfOrigin(value + "-0-0");
                                }
                                if (splitArray.length == 2) {
                                    newFatalInjurie.setVictimPlaceOfOrigin(value + "-0");
                                }
                                if (splitArray.length == 3) {
                                    newFatalInjurie.setVictimPlaceOfOrigin(value);
                                }

                                break;
                            case caracvict:
                                newFatalInjuryTraffic.setVictimCharacteristicId(victimCharacteristicsFacade.find(Short.parseShort(value)));
                                break;
                            case proteccion:
                                newFatalInjuryTraffic.setProtectionMeasureId(protectiveMeasuresFacade.find(Short.parseShort(value)));
                                break;
                            case vehic1:
                                newFatalInjuryTraffic.setInvolvedVehicleId(involvedVehiclesFacade.find(Short.parseShort(value)));
                                break;
                            case vehic2:
                            case vehic3:
                            case vehic4:
                                newCounterpartInvolvedVehicle = new CounterpartInvolvedVehicle();
                                newCounterpartInvolvedVehicle.setInvolvedVehicleId(involvedVehiclesFacade.find(Short.parseShort(value)));
                                newCounterpartInvolvedVehicle.setFatalInjuryId(newFatalInjurie);
                                newCounterpartInvolvedVehicle.setCounterpartInvolvedVehicleId(counterpartInvolvedVehicleFacade.findMax() + 1);
                                involvedVehiclesList.add(newCounterpartInvolvedVehicle);
                                break;
                            case tiposerv1:
                                newFatalInjuryTraffic.setServiceTypeId(serviceTypesFacade.find(Short.parseShort(value)));
                                break;
                            case tiposerv2:
                            case tiposerv3:
                            case tiposerv4:
                                newCounterpartServiceType = new CounterpartServiceType();
                                newCounterpartServiceType.setServiceTypeId(serviceTypesFacade.find(Short.parseShort(value)));
                                newCounterpartServiceType.setFatalInjuryId(newFatalInjurie);
                                newCounterpartServiceType.setCounterpartServiceTypeId(counterpartServiceTypeFacade.findMax() + 1);
                                serviceTypesList.add(newCounterpartServiceType);
                                break;
                            case narrac:
                            case narrac1:
                            case narrac2:
                                narrative = narrative + " " + value;
                                break;
                            case nivelvict:
                                newFatalInjurie.setAlcoholLevelVictim(Short.parseShort(value));
                                break;
                            case alcvict:
                                newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find(Short.parseShort(value)));//con dato
                                break;
                            case nivelculp:
                                newFatalInjuryTraffic.setAlcoholLevelCounterpart(Short.parseShort(value));
                                break;
                            case alcculp:
                                newFatalInjuryTraffic.setAlcoholLevelCounterpartId(alcoholLevelsFacade.find(Short.parseShort(value)));//con dato
                                break;
                            default:
                        }
                    }
                }

                //SI NO SE DETERMINA EL BARRIO SE COLOCA SIN DATO URBANO
                if (newVictim.getVictimNeighborhoodId() == null) {
                    newVictim.setVictimNeighborhoodId(neighborhoodsFacade.find((int) 52001));
                }
                //SI NO SE DETERMINA EL BARRIO SE COLOCA SIN DATO URBANO
                if (newFatalInjurie.getInjuryNeighborhoodId() == null) {
                    newFatalInjurie.setInjuryNeighborhoodId((int) 52001);
                }

                //SI NO HAY FECHA DE EVENTO TRATAR DE CALCULAR MEDIANTE LAS VARIABLES dia, mes, ao
                if (newFatalInjurie.getInjuryDate() == null) {
                    dia = haveData(dia);
                    mes = haveData(mes);
                    ao = haveData(ao);
                    if (dia != null && mes != null && ao != null) {
                        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                        Date fechaI;
                        fechaI = formato.parse(dia + "/" + mes + "/" + ao);
                        newFatalInjurie.setInjuryDate(fechaI);
                    }
                }

                //SI NO HAY HORA DE EVENTO TRATAR DE CALCULAR MEDIANTE LAS VARIABLES hora,minutos,ampm
                if (newFatalInjurie.getInjuryTime() == null) {
                    horas = haveData(horas);
                    minutos = haveData(minutos);
                    ampm = haveData(ampm);
                    if (horas != null && minutos != null && ampm != null) {
                        hourInt = Integer.parseInt(horas);
                        minuteInt = Integer.parseInt(minutos);
                        if (ampm.compareTo("2") == 0) {
                            hourInt = hourInt + 12;
                            if (hourInt == 24) {
                                hourInt = 0;
                            }
                        }
                        currentDate = new Date();
                        currentDate.setHours(hourInt);
                        currentDate.setMinutes(minuteInt);
                        currentDate.setSeconds(0);
                        newFatalInjurie.setInjuryTime(currentDate);
                    }
                }


                //SI NO HAY DIA DE LA SEMANA DEL EVENTO SE CALCULA
                if (newFatalInjurie.getInjuryDate() != null) {
                    if (newFatalInjurie.getInjuryDayOfWeek() == null) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(newFatalInjurie.getInjuryDate());
                        newFatalInjurie.setInjuryDayOfWeek(intToDay(cal.get(Calendar.DAY_OF_WEEK)));
                    }
                }

                //UNION DE NOMBRES Y APELLIOS
                name = name + " " + surname;
                if (name.trim().length() > 1) {
                    newVictim.setVictimName(name);
                }

                //SI NO SE DETERMINA LA EDAD VERIFICAR SI HAY FECHA DE NACIMIENTO
                if (newVictim.getVictimDateOfBirth() != null) {
                    if (newVictim.getVictimAge() == null) {
                        int birthMonths;
                        int eventMonths;

                        Calendar systemCalendar = Calendar.getInstance();
                        Calendar birthCalendar = Calendar.getInstance();
                        birthCalendar.setTime(newVictim.getVictimDateOfBirth());

                        try {//DETERMINO LA EDAD EN MESES
                            birthMonths = birthCalendar.get(Calendar.YEAR);
                            birthMonths = birthMonths * 12;
                            birthMonths = birthMonths + birthCalendar.get(Calendar.MONTH);
                            if (newFatalInjurie.getInjuryDate() != null) {//SE CALCULA EDAD SEGUN LA FECHA DE EVENTO
                                systemCalendar.setTime(newFatalInjurie.getInjuryDate());
                            }
                            eventMonths = systemCalendar.get(Calendar.YEAR);
                            eventMonths = eventMonths * 12;
                            eventMonths = eventMonths + systemCalendar.get(Calendar.MONTH);

                            int ageMonths = eventMonths - birthMonths;
                            if (ageMonths < 0) {
                                System.out.println("ERROR fecha de nacimiento mayor a la del sistema o evento: ");
                            } else {
                                //SI EXISTE EDAD Y NO HAY TIPO DE EDAD DETERMINARLA EN AÑOS
                                int ageYears = (int) (ageMonths / 12);
                                if (ageYears == 0) {
                                    ageYears = 1;
                                }
                                newVictim.setVictimAge((short) ageYears);
                                newVictim.setAgeTypeId((short) 1);
                                if (newVictim.getTypeId() == null) {
                                    if (ageYears >= 18) {
                                        newVictim.setTypeId(idTypesFacade.find((short) 1));
                                    } else {
                                        newVictim.setTypeId(idTypesFacade.find((short) 5));
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(HomicideMB.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }

                //DETERMINAR EL NUMERO DE IDENTIFICACION
                if (newVictim.getVictimNid() == null) {
                    newVictim.setVictimNid(String.valueOf(genNnFacade.findMax() + 1));
                    newVictim.setVictimClass((short) 2);//nn
                    newVictim.setTypeId(idTypesFacade.find((short) 6));//adulto sin identificacion
                    GenNn currentGenNn = genNnFacade.find(genNnFacade.findMax());
                    currentGenNn.setCodNn(genNnFacade.findMax() + 1);
                    genNnFacade.edit(currentGenNn);
                }

                //DETERMINAR TIPO DE IDENTIFICACION
                if (newVictim.getVictimNid() != null) {
                    if (newVictim.getTypeId() == null) {
                        newVictim.setTypeId(idTypesFacade.find((short) 1));//SI NI HAY EDAD DEJAR POR DEFECTO CEDULA
                    }
                }

                //AGREGO LAS LISTAS
                if (!involvedVehiclesList.isEmpty()) {
                    newFatalInjurie.setCounterpartInvolvedVehicleList(involvedVehiclesList);
                }
                if (!serviceTypesList.isEmpty()) {
                    newFatalInjurie.setCounterpartServiceTypeList(serviceTypesList);
                }

                //PERSISTO
                try {
                    newVictim.setTagId(tagsFacade.find(newTag));
                    victimsFacade.create(newVictim);
                    fatalInjuriesFacade.create(newFatalInjurie);
                    fatalInjuryTrafficFacade.create(newFatalInjuryTraffic);
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
                tuplesProcessed++;
                progress = (int) (tuplesProcessed * 100) / tuplesNumber;
                System.out.println("PROGRESO INGRESANDO TRANSITO: " + String.valueOf(progress));
            }
            progress = 100;
            System.out.println("PROGRESO INGRESANDO TRANSITO: " + String.valueOf(progress));
        } catch (SQLException ex) {
            System.out.println("errorSQL INGRESADNDO TRANSITO: " + ex.toString());
        } catch (Exception ex) {
            System.out.println("EXCEPTION INGRESANDO TRANSITO: " + ex.toString() + "  " + relationVar.getNameExpected() + " " + String.valueOf(tuplesProcessed));
        }
    }

    public void registerSCC_F_030() {
        /**
         * *********************************************************************
         * CARGA DE REGISTROS DE LA FICHA SUICIDIOS
         * ********************************************************************
         */
        tuplesNumber = 0;
        tuplesProcessed = 0;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            conx = new ConnectionJDBC();
            conx.connect();
            resultSetFileData = conx.consult("SELECT COUNT(*) FROM temp; ");
            resultSetFileData.next();
            tuplesNumber = resultSetFileData.getInt(1);//NUMERO DE TUPLAS
            resultSetFileData = conx.consult("SELECT * FROM temp ORDER BY id; ");//resultSetFileData contendra todos los registros de el archivo(tabla temp)
            conx.disconnect();
            progress = 0;
            columnsNames = new String[resultSetFileData.getMetaData().getColumnCount()];//NOMBRES DE LAS COLUMNAS
            int pos = 0;
            for (int i = 1; i <= resultSetFileData.getMetaData().getColumnCount(); i++) {
                columnsNames[pos] = resultSetFileData.getMetaData().getColumnName(i);
                pos++;
            }

            newTag = new Tags();//VARIABLES PARA CONJUNTOS DE REGISTROS
            newTag.setTagId(tagsFacade.findMax() + 1);
            newTag.setTagName(uploadFileMB.getTagName());
            newTag.setTagFileInput(uploadFileMB.getNameFile());
            newTag.setTagFileStored(uploadFileMB.getNameFile());
            newTag.setFormId(formsFacade.find(nameForm));
            tagsFacade.create(newTag);
            while (resultSetFileData.next()) {//recorro cada uno de los registros de la tabla temp                    
                newVictim = new Victims();
                newVictim.setVictimId(victimsFacade.findMax() + 1);
                newVictim.setVictimClass(Short.parseShort("1"));
                newFatalInjurie = new FatalInjuries();
                newFatalInjurie.setFatalInjuryId(fatalInjuriesFacade.findMax() + 1);
                newFatalInjurie.setInputTimestamp(new Date());
                newFatalInjurie.setInjuryId(injuriesFacade.find((short) 10));//es 10 por ser homicidio
                newFatalInjurie.setUserId(usersFacade.find(1));//usuario que se encuentre logueado
                newFatalInjurie.setVictimId(newVictim);
                newFatalInjurySuicide = new FatalInjurySuicide();
                newFatalInjurySuicide.setFatalInjuryId(newFatalInjurie.getFatalInjuryId());
                newVictim.setTagId(tagsFacade.find(newTag.getTagId()));
                newFatalInjurie.setInjuryId(injuriesFacade.find((short) 12));//es 12 por ser suicidio
                value = "";
                name = "";
                surname = "";
                dia = "";//dia evento
                mes = "";//mes evento
                ao = "";//año del evento
                diacon = "";//dia de la semana cnsulta                                
                dia1 = "";//dia de la consulta
                mes1 = "";//mes de la consulta
                ao1 = "";//año de la consulta
                horas = "";//hora evento
                minutos = "";//minuto evento
                ampm = "";//ampm evento
                horas1 = "";//hora consulta
                minutos1 = "";//minuto consulta
                ampm1 = "";//ampm consulta
                hourInt = 0;
                minuteInt = 0;
                narrative = "";
                for (int posCol = 0; posCol < columnsNames.length; posCol++) {
                    value = null;
                    //DETERMINO QUE VALOR VOY A INGRESAR HACIENDO USO DE LAS FUNCIONES DE VALIDACION isNumeric,isAge... etc
                    relationVar = currentRelationsGroup.findRelationVar(columnsNames[posCol]);//determino la relacion de variables
                    //if (columnsNames[posCol].compareTo("edadcantid") == 0 && tuplesProcessed > 309) { columnsNames[posCol] = "edadcantid"; }
                    if (relationVar != null) {
                        switch (DataTypeEnum.convert(relationVar.getFieldType())) {//tipo de relacion
                            case text:
                                value = resultSetFileData.getString(columnsNames[posCol]);
                                break;
                            case integer:
                                value = isNumeric(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case age:
                                value = isAge(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case date:
                                value = isDate(resultSetFileData.getString(columnsNames[posCol]), relationVar.getDateFormat());
                                break;
                            case military:
                                value = isMilitary(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case hour:
                                value = isHour(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case minute:
                                value = isMinute(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case day:
                                value = isDay(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case month:
                                value = isMonth(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case year:
                                value = isYear(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case percentage:
                                value = isPercentage(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case NOVALUE:
                                value = isCategorical(resultSetFileData.getString(relationVar.getNameFound()), relationVar);
                                break;
                        }
                    }

                    continueProcces = false;
                    if (value != null) {
                        if (value.trim().length() != 0) {
                            continueProcces = true;
                        }
                    }
                    if (continueProcces) {
                        switch (SCC_F_030Enum.convert(relationVar.getNameExpected())) {
                            // ************************************************DATOS PARA LA TABLA victims                                
//                            case field1:
//                                break;
//                            case clave:
//                                break;
//                            case ficha:
//                                break;
                            case departamen:
                                break;
                            case codigodepa:
                                break;
                            case municipio:
                                splitArray = value.split("-");
                                if (splitArray.length == 2) {
                                    newVictim.setResidenceDepartment(Short.parseShort(splitArray[0]));
                                    newVictim.setResidenceMunicipality(Short.parseShort(splitArray[1]));
                                }
                                break;
                            case codigomuni:
                                splitArray = value.split("-");
                                if (splitArray.length == 2) {
                                    newVictim.setResidenceDepartment(Short.parseShort(splitArray[0]));
                                    newVictim.setResidenceMunicipality(Short.parseShort(splitArray[1]));
                                }
                                break;
                            case codigo:
                                newFatalInjurie.setCode(value);
                                break;
                            case dia://dia del evento
                                dia = value;
                                break;
                            case mes://mes evento
                                mes = value;
                                break;
                            case ao://año del evento
                                ao = value;
                                break;
                            case fechah:
                                try {
                                    currentDate = dateFormat.parse(value);
                                    newFatalInjurie.setInjuryDate(currentDate);
                                } catch (ParseException ex) {
                                }
                                break;
                            case horas:
                                horas = value;
                                break;
                            case minutos:
                                minutos = value;
                                break;
                            case ampm:
                                ampm = value;
                                break;
                            case horamil:
                                n = new Date();
                                hourInt = Integer.parseInt(value.substring(0, 2));
                                minuteInt = Integer.parseInt(value.substring(2, 4));
                                n.setHours(hourInt);
                                n.setMinutes(minuteInt);
                                n.setSeconds(0);
                                newFatalInjurie.setInjuryTime(n);
                                break;
//                            case hrshorasin://hora si determinar
//                                si no hay hora es si determinar                                
//                                break;
                            case direccion:
                                newFatalInjurie.setInjuryAddress(value);
                                break;
                            case barrio:
                                newFatalInjurie.setInjuryNeighborhoodId(Integer.parseInt(value));
                                break;
                            case codbar:
                                newFatalInjurie.setInjuryNeighborhoodId(Integer.parseInt(value));
                                break;
                            case area://ZONA URBANA O RURAL //SE DETERMINA CON EL BARRIO
                                newFatalInjurie.setAreaId(areasFacade.find(Short.parseShort(value)));
                                break;
                            case lugarhecho:
                                newFatalInjurie.setInjuryPlaceId(placesFacade.find(Short.parseShort(value)));
                                break;
                            case diasem:
                                newFatalInjurie.setInjuryDayOfWeek(daysFacade.find(Short.parseShort(value)).getDaysName());
                                break;
                            case victimas:
                                newFatalInjurie.setVictimNumber(Short.parseShort(value));
                                break;
                            case nombres:
                                name = value;
                                break;
                            case apellidos:
                                surname = value;
                                break;
                            case sexo:
                                newVictim.setGenderId(gendersFacade.find(Short.parseShort(value)));
                                break;
                            case tipoedad:
                                newVictim.setGenderId(gendersFacade.find(Short.parseShort(value)));
                                break;
                            case edad:
                                newVictim.setVictimAge(Short.parseShort(value));
                                break;
                            case ocupacion:
                                newVictim.setJobId(jobsFacade.find(Short.parseShort(value)));
                                break;
                            case munres:
                                splitArray = value.split("-");
                                if (splitArray.length == 2) {
                                    newVictim.setResidenceDepartment(Short.parseShort(splitArray[0]));
                                    newVictim.setResidenceMunicipality(Short.parseShort(splitArray[1]));
                                }
                                break;
                            case residencia:
                                newVictim.setVictimNeighborhoodId(neighborhoodsFacade.find(Integer.parseInt(value)));
                                break;
                            case tipoid:
                                newVictim.setTypeId(idTypesFacade.find(Short.parseShort(value)));
                                break;
                            case nroid:
                                newVictim.setVictimNid(value);
                                break;
                            case procedenci:
                                splitArray = value.split("-");
                                if (splitArray.length == 1) {
                                    newFatalInjurie.setVictimPlaceOfOrigin(value + "-0-0");
                                }
                                if (splitArray.length == 2) {
                                    newFatalInjurie.setVictimPlaceOfOrigin(value + "-0");
                                }
                                if (splitArray.length == 3) {
                                    newFatalInjurie.setVictimPlaceOfOrigin(value);
                                }

                                break;
                            case tipoarma:
                                newFatalInjurySuicide.setSuicideDeathMechanismId(suicideMechanismsFacade.find(Short.parseShort(value)));
                                break;
                            case eventos:
                                newFatalInjurySuicide.setRelatedEventId(relatedEventsFacade.find(Short.parseShort(value)));
                                break;
                            case intentos:
                                newFatalInjurySuicide.setPreviousAttempt(boolean3Facade.find(Short.parseShort(value)));
                                break;
                            case saludmenta:
                                newFatalInjurySuicide.setMentalAntecedent(boolean3Facade.find(Short.parseShort(value)));
                                break;
                            case narrac:
                            case narrac1:
                            case narrac2:
                                narrative = narrative + " " + value;
                                break;
                            case n26nivelde:
                                newFatalInjurie.setAlcoholLevelVictim(Short.parseShort(value));
                                break;
                            case mgsindato:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 2));
                                }
                                break;
                            case pendiente:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 4));
                                }
                                break;
                            case ns:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 3));
                                }
                                break;
                            case negativo:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 5));
                                }
                                break;
                            default:
                        }
                    }
                }

                //SI NO SE DETERMINA EL BARRIO SE COLOCA SIN DATO URBANO
                if (newVictim.getVictimNeighborhoodId() == null) {
                    newVictim.setVictimNeighborhoodId(neighborhoodsFacade.find((int) 52001));
                }
                //SI NO SE DETERMINA EL BARRIO SE COLOCA SIN DATO URBANO
                if (newFatalInjurie.getInjuryNeighborhoodId() == null) {
                    newFatalInjurie.setInjuryNeighborhoodId((int) 52001);
                }

                //SI NO HAY FECHA DE EVENTO TRATAR DE CALCULAR MEDIANTE LAS VARIABLES dia, mes, ao
                if (newFatalInjurie.getInjuryDate() == null) {
                    dia = haveData(dia);
                    mes = haveData(mes);
                    ao = haveData(ao);
                    if (dia != null && mes != null && ao != null) {
                        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                        Date fechaI;
                        fechaI = formato.parse(dia + "/" + mes + "/" + ao);
                        newFatalInjurie.setInjuryDate(fechaI);
                    }
                }

                //SI NO HAY HORA DE EVENTO TRATAR DE CALCULAR MEDIANTE LAS VARIABLES hora,minutos,ampm
                if (newFatalInjurie.getInjuryTime() == null) {
                    horas = haveData(horas);
                    minutos = haveData(minutos);
                    ampm = haveData(ampm);
                    if (horas != null && minutos != null && ampm != null) {
                        hourInt = Integer.parseInt(horas);
                        minuteInt = Integer.parseInt(minutos);
                        if (ampm.compareTo("2") == 0) {
                            hourInt = hourInt + 12;
                            if (hourInt == 24) {
                                hourInt = 0;
                            }
                        }
                        currentDate = new Date();
                        currentDate.setHours(hourInt);
                        currentDate.setMinutes(minuteInt);
                        currentDate.setSeconds(0);
                        newFatalInjurie.setInjuryTime(currentDate);
                    }
                }


                //SI NO HAY DIA DE LA SEMANA DEL EVENTO SE CALCULA
                if (newFatalInjurie.getInjuryDate() != null) {
                    if (newFatalInjurie.getInjuryDayOfWeek() == null) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(newFatalInjurie.getInjuryDate());
                        newFatalInjurie.setInjuryDayOfWeek(intToDay(cal.get(Calendar.DAY_OF_WEEK)));
                    }
                }

                //UNION DE NOMBRES Y APELLIOS
                name = name + " " + surname;
                if (name.trim().length() > 1) {
                    newVictim.setVictimName(name);
                }

                //SI NO SE DETERMINA LA EDAD VERIFICAR SI HAY FECHA DE NACIMIENTO
                if (newVictim.getVictimDateOfBirth() != null) {
                    if (newVictim.getVictimAge() == null) {
                        int birthMonths;
                        int eventMonths;

                        Calendar systemCalendar = Calendar.getInstance();
                        Calendar birthCalendar = Calendar.getInstance();
                        birthCalendar.setTime(newVictim.getVictimDateOfBirth());

                        try {//DETERMINO LA EDAD EN MESES
                            birthMonths = birthCalendar.get(Calendar.YEAR);
                            birthMonths = birthMonths * 12;
                            birthMonths = birthMonths + birthCalendar.get(Calendar.MONTH);
                            if (newFatalInjurie.getInjuryDate() != null) {//SE CALCULA EDAD SEGUN LA FECHA DE EVENTO
                                systemCalendar.setTime(newFatalInjurie.getInjuryDate());
                            }
                            eventMonths = systemCalendar.get(Calendar.YEAR);
                            eventMonths = eventMonths * 12;
                            eventMonths = eventMonths + systemCalendar.get(Calendar.MONTH);

                            int ageMonths = eventMonths - birthMonths;
                            if (ageMonths < 0) {
                                System.out.println("ERROR fecha de nacimiento mayor a la del sistema o evento: ");
                            } else {
                                //SI EXISTE EDAD Y NO HAY TIPO DE EDAD DETERMINARLA EN AÑOS
                                int ageYears = (int) (ageMonths / 12);
                                if (ageYears == 0) {
                                    ageYears = 1;
                                }
                                newVictim.setVictimAge((short) ageYears);
                                newVictim.setAgeTypeId((short) 1);
                                if (newVictim.getTypeId() == null) {
                                    if (ageYears >= 18) {
                                        newVictim.setTypeId(idTypesFacade.find((short) 1));
                                    } else {
                                        newVictim.setTypeId(idTypesFacade.find((short) 5));
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(HomicideMB.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }

                //DETERMINAR EL NUMERO DE IDENTIFICACION
                if (newVictim.getVictimNid() == null) {
                    newVictim.setVictimNid(String.valueOf(genNnFacade.findMax() + 1));
                    newVictim.setVictimClass((short) 2);//nn
                    newVictim.setTypeId(idTypesFacade.find((short) 6));//adulto sin identificacion
                    GenNn currentGenNn = genNnFacade.find(genNnFacade.findMax());
                    currentGenNn.setCodNn(genNnFacade.findMax() + 1);
                    genNnFacade.edit(currentGenNn);
                }

                //DETERMINAR TIPO DE IDENTIFICACION
                if (newVictim.getVictimNid() != null) {
                    if (newVictim.getTypeId() == null) {
                        newVictim.setTypeId(idTypesFacade.find((short) 1));//SI NI HAY EDAD DEJAR POR DEFECTO CEDULA
                    }
                }

                //PERSISTO
                try {
                    newVictim.setTagId(tagsFacade.find(newTag));
                    victimsFacade.create(newVictim);
                    fatalInjuriesFacade.create(newFatalInjurie);
                    fatalInjurySuicideFacade.create(newFatalInjurySuicide);
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
                tuplesProcessed++;
                progress = (int) (tuplesProcessed * 100) / tuplesNumber;
                System.out.println("PROGRESO INGRESANDO SUICIDIOS: " + String.valueOf(progress));
            }
            progress = 100;
            System.out.println("PROGRESO INGRESANDO SUICIDIOS: " + String.valueOf(progress));
        } catch (SQLException ex) {
            System.out.println("errorSQL INGRESADNDO SUICIDIOS: " + ex.toString());
        } catch (Exception ex) {
            System.out.println("EXCEPTION INGRESANDO SUICIDIOS: " + ex.toString() + "  " + relationVar.getNameExpected() + " " + String.valueOf(tuplesProcessed));
        }
    }

    public void registerSCC_F_031() {
        /**
         * *********************************************************************
         * CARGA DE REGISTROS DE LA FICHA MUERTES ACCIDENTALES
         * ********************************************************************
         */
        tuplesNumber = 0;
        tuplesProcessed = 0;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            conx = new ConnectionJDBC();
            conx.connect();
            resultSetFileData = conx.consult("SELECT COUNT(*) FROM temp; ");
            resultSetFileData.next();
            tuplesNumber = resultSetFileData.getInt(1);//NUMERO DE TUPLAS
            resultSetFileData = conx.consult("SELECT * FROM temp ORDER BY id; ");//resultSetFileData contendra todos los registros de el archivo(tabla temp)
            conx.disconnect();
            progress = 0;
            columnsNames = new String[resultSetFileData.getMetaData().getColumnCount()];//NOMBRES DE LAS COLUMNAS
            int pos = 0;
            for (int i = 1; i <= resultSetFileData.getMetaData().getColumnCount(); i++) {
                columnsNames[pos] = resultSetFileData.getMetaData().getColumnName(i);
                pos++;
            }

            newTag = new Tags();//VARIABLES PARA CONJUNTOS DE REGISTROS
            newTag.setTagId(tagsFacade.findMax() + 1);
            newTag.setTagName(uploadFileMB.getTagName());
            newTag.setTagFileInput(uploadFileMB.getNameFile());
            newTag.setTagFileStored(uploadFileMB.getNameFile());
            newTag.setFormId(formsFacade.find(nameForm));
            tagsFacade.create(newTag);
            while (resultSetFileData.next()) {//recorro cada uno de los registros de la tabla temp                    
                newVictim = new Victims();
                newVictim.setVictimId(victimsFacade.findMax() + 1);
                newVictim.setVictimClass(Short.parseShort("1"));
                newFatalInjurie = new FatalInjuries();
                newFatalInjurie.setFatalInjuryId(fatalInjuriesFacade.findMax() + 1);
                newFatalInjurie.setInputTimestamp(new Date());
                newFatalInjurie.setInjuryId(injuriesFacade.find((short) 10));//es 10 por ser homicidio
                newFatalInjurie.setUserId(usersFacade.find(1));//usuario que se encuentre logueado
                newFatalInjurie.setVictimId(newVictim);
                newFatalInjuryAccident = new FatalInjuryAccident();
                newFatalInjuryAccident.setFatalInjuryId(newFatalInjurie.getFatalInjuryId());
                newVictim.setTagId(tagsFacade.find(newTag.getTagId()));
                newFatalInjurie.setInjuryId(injuriesFacade.find((short) 13));//es 13 por ser muerte accidental
                value = "";
                name = "";
                surname = "";
                dia = "";//dia evento
                mes = "";//mes evento
                ao = "";//año del evento
                diacon = "";//dia de la semana cnsulta                                
                dia1 = "";//dia de la consulta
                mes1 = "";//mes de la consulta
                ao1 = "";//año de la consulta
                horas = "";//hora evento
                minutos = "";//minuto evento
                ampm = "";//ampm evento
                horas1 = "";//hora consulta
                minutos1 = "";//minuto consulta
                ampm1 = "";//ampm consulta
                hourInt = 0;
                minuteInt = 0;
                narrative = "";
                for (int posCol = 0; posCol < columnsNames.length; posCol++) {
                    value = null;
                    //DETERMINO QUE VALOR VOY A INGRESAR HACIENDO USO DE LAS FUNCIONES DE VALIDACION isNumeric,isAge... etc
                    relationVar = currentRelationsGroup.findRelationVar(columnsNames[posCol]);//determino la relacion de variables
                    //if (columnsNames[posCol].compareTo("edadcantid") == 0 && tuplesProcessed > 309) { columnsNames[posCol] = "edadcantid"; }
                    if (relationVar != null) {
                        switch (DataTypeEnum.convert(relationVar.getFieldType())) {//tipo de relacion
                            case text:
                                value = resultSetFileData.getString(columnsNames[posCol]);
                                break;
                            case integer:
                                value = isNumeric(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case age:
                                value = isAge(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case date:
                                value = isDate(resultSetFileData.getString(columnsNames[posCol]), relationVar.getDateFormat());
                                break;
                            case military:
                                value = isMilitary(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case hour:
                                value = isHour(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case minute:
                                value = isMinute(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case day:
                                value = isDay(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case month:
                                value = isMonth(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case year:
                                value = isYear(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case percentage:
                                value = isPercentage(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case NOVALUE:
                                value = isCategorical(resultSetFileData.getString(relationVar.getNameFound()), relationVar);
                                break;
                        }
                    }

                    continueProcces = false;
                    if (value != null) {
                        if (value.trim().length() != 0) {
                            continueProcces = true;
                        }
                    }
                    if (continueProcces) {
                        switch (SCC_F_031Enum.convert(relationVar.getNameExpected())) {
                            // ************************************************DATOS PARA LA TABLA victims                                
//                            case field1:
//                                break;
//                            case clave:
//                                break;
//                            case ficha:
//                                break;
                            case departamen:
                                break;
                            case codigodepa:
                                break;
                            case municipio:
                                splitArray = value.split("-");
                                if (splitArray.length == 2) {
                                    newVictim.setResidenceDepartment(Short.parseShort(splitArray[0]));
                                    newVictim.setResidenceMunicipality(Short.parseShort(splitArray[1]));
                                }
                                break;
                            case codigomuni:
                                splitArray = value.split("-");
                                if (splitArray.length == 2) {
                                    newVictim.setResidenceDepartment(Short.parseShort(splitArray[0]));
                                    newVictim.setResidenceMunicipality(Short.parseShort(splitArray[1]));
                                }
                                break;
                            case codigo:
                                newFatalInjurie.setCode(value);
                                break;
                            case dia://dia del evento
                                dia = value;
                                break;
                            case mes://mes evento
                                mes = value;
                                break;
                            case ao://año del evento
                                ao = value;
                                break;
                            case fechah:
                                try {
                                    currentDate = dateFormat.parse(value);
                                    newFatalInjurie.setInjuryDate(currentDate);
                                } catch (ParseException ex) {
                                }
                                break;
                            case horas:
                                horas = value;
                                break;
                            case minutos:
                                minutos = value;
                                break;
                            case ampm:
                                ampm = value;
                                break;
                            case horamil:
                                n = new Date();
                                hourInt = Integer.parseInt(value.substring(0, 2));
                                minuteInt = Integer.parseInt(value.substring(2, 4));
                                n.setHours(hourInt);
                                n.setMinutes(minuteInt);
                                n.setSeconds(0);
                                newFatalInjurie.setInjuryTime(n);
                                break;
//                            case hrshorasin://hora si determinar
//                                si no hay hora es si determinar                                
//                                break;
                            case direccion:
                                newFatalInjurie.setInjuryAddress(value);
                                break;
                            case barrio:
                                newFatalInjurie.setInjuryNeighborhoodId(Integer.parseInt(value));
                                break;
                            case codbar:
                                newFatalInjurie.setInjuryNeighborhoodId(Integer.parseInt(value));
                                break;
                            case area://ZONA URBANA O RURAL //SE DETERMINA CON EL BARRIO
                                newFatalInjurie.setAreaId(areasFacade.find(Short.parseShort(value)));
                                break;
                            case lugarhecho:
                                newFatalInjurie.setInjuryPlaceId(placesFacade.find(Short.parseShort(value)));
                                break;
                            case diasem:
                                newFatalInjurie.setInjuryDayOfWeek(daysFacade.find(Short.parseShort(value)).getDaysName());
                                break;
                            case victimas:
                                newFatalInjurie.setVictimNumber(Short.parseShort(value));
                                break;
                            case lesionados:
                                newFatalInjuryTraffic.setNumberNonFatalVictims(Short.parseShort(value));
                                break;
                            case nombres:
                                name = value;
                                break;
                            case apellidos:
                                surname = value;
                                break;
                            case sexo:
                                newVictim.setGenderId(gendersFacade.find(Short.parseShort(value)));
                                break;
                            case tipoedad:
                                newVictim.setGenderId(gendersFacade.find(Short.parseShort(value)));
                                break;
                            case edad:
                                newVictim.setVictimAge(Short.parseShort(value));
                                break;
                            case ocupacion:
                                newVictim.setJobId(jobsFacade.find(Short.parseShort(value)));
                                break;
                            case munres:
                                splitArray = value.split("-");
                                if (splitArray.length == 2) {
                                    newVictim.setResidenceDepartment(Short.parseShort(splitArray[0]));
                                    newVictim.setResidenceMunicipality(Short.parseShort(splitArray[1]));
                                }
                                break;
                            case residencia:
                                newVictim.setVictimNeighborhoodId(neighborhoodsFacade.find(Integer.parseInt(value)));
                                break;
                            case tipoid:
                                newVictim.setTypeId(idTypesFacade.find(Short.parseShort(value)));
                                break;
                            case nroid:
                                newVictim.setVictimNid(value);
                                break;
                            case procedenci:
                                splitArray = value.split("-");
                                if (splitArray.length == 1) {
                                    newFatalInjurie.setVictimPlaceOfOrigin(value + "-0-0");
                                }
                                if (splitArray.length == 2) {
                                    newFatalInjurie.setVictimPlaceOfOrigin(value + "-0");
                                }
                                if (splitArray.length == 3) {
                                    newFatalInjurie.setVictimPlaceOfOrigin(value);
                                }

                                break;
                            case tipoarma:
                                newFatalInjuryAccident.setDeathMechanismId(accidentMechanismsFacade.find(Short.parseShort(value)));
                                break;
                            case narrac:
                            case narrac1:
                            case narrac2:
                                narrative = narrative + " " + value;
                                break;
                            case n24nivelal:
                                newFatalInjurie.setAlcoholLevelVictim(Short.parseShort(value));
                                break;
                            case mgsindato:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 2));
                                }
                                break;
                            case pendiente:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 4));
                                }
                                break;
                            case ns:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 3));
                                }
                                break;
                            case negativa:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 5));
                                }
                                break;
                            default:

                        }
                    }
                }

                //SI NO SE DETERMINA EL BARRIO SE COLOCA SIN DATO URBANO
                if (newVictim.getVictimNeighborhoodId() == null) {
                    newVictim.setVictimNeighborhoodId(neighborhoodsFacade.find((int) 52001));
                }
                //SI NO SE DETERMINA EL BARRIO SE COLOCA SIN DATO URBANO
                if (newFatalInjurie.getInjuryNeighborhoodId() == null) {
                    newFatalInjurie.setInjuryNeighborhoodId((int) 52001);
                }

                //SI NO HAY FECHA DE EVENTO TRATAR DE CALCULAR MEDIANTE LAS VARIABLES dia, mes, ao
                if (newFatalInjurie.getInjuryDate() == null) {
                    dia = haveData(dia);
                    mes = haveData(mes);
                    ao = haveData(ao);
                    if (dia != null && mes != null && ao != null) {
                        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                        Date fechaI;
                        fechaI = formato.parse(dia + "/" + mes + "/" + ao);
                        newFatalInjurie.setInjuryDate(fechaI);
                    }
                }

                //SI NO HAY HORA DE EVENTO TRATAR DE CALCULAR MEDIANTE LAS VARIABLES hora,minutos,ampm
                if (newFatalInjurie.getInjuryTime() == null) {
                    horas = haveData(horas);
                    minutos = haveData(minutos);
                    ampm = haveData(ampm);
                    if (horas != null && minutos != null && ampm != null) {
                        hourInt = Integer.parseInt(horas);
                        minuteInt = Integer.parseInt(minutos);
                        if (ampm.compareTo("2") == 0) {
                            hourInt = hourInt + 12;
                            if (hourInt == 24) {
                                hourInt = 0;
                            }
                        }
                        currentDate = new Date();
                        currentDate.setHours(hourInt);
                        currentDate.setMinutes(minuteInt);
                        currentDate.setSeconds(0);
                        newFatalInjurie.setInjuryTime(currentDate);
                    }
                }

                //SI NO HAY DIA DE LA SEMANA DEL EVENTO SE CALCULA
                if (newFatalInjurie.getInjuryDate() != null) {
                    if (newFatalInjurie.getInjuryDayOfWeek() == null) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(newFatalInjurie.getInjuryDate());
                        newFatalInjurie.setInjuryDayOfWeek(intToDay(cal.get(Calendar.DAY_OF_WEEK)));
                    }
                }

                //UNION DE NOMBRES Y APELLIOS
                name = name + " " + surname;
                if (name.trim().length() > 1) {
                    newVictim.setVictimName(name);
                }

                //SI NO SE DETERMINA LA EDAD VERIFICAR SI HAY FECHA DE NACIMIENTO
                if (newVictim.getVictimDateOfBirth() != null) {
                    if (newVictim.getVictimAge() == null) {
                        int birthMonths;
                        int eventMonths;

                        Calendar systemCalendar = Calendar.getInstance();
                        Calendar birthCalendar = Calendar.getInstance();
                        birthCalendar.setTime(newVictim.getVictimDateOfBirth());

                        try {//DETERMINO LA EDAD EN MESES
                            birthMonths = birthCalendar.get(Calendar.YEAR);
                            birthMonths = birthMonths * 12;
                            birthMonths = birthMonths + birthCalendar.get(Calendar.MONTH);
                            if (newFatalInjurie.getInjuryDate() != null) {//SE CALCULA EDAD SEGUN LA FECHA DE EVENTO
                                systemCalendar.setTime(newFatalInjurie.getInjuryDate());
                            }
                            eventMonths = systemCalendar.get(Calendar.YEAR);
                            eventMonths = eventMonths * 12;
                            eventMonths = eventMonths + systemCalendar.get(Calendar.MONTH);

                            int ageMonths = eventMonths - birthMonths;
                            if (ageMonths < 0) {
                                System.out.println("ERROR fecha de nacimiento mayor a la del sistema o evento: ");
                            } else {
                                //SI EXISTE EDAD Y NO HAY TIPO DE EDAD DETERMINARLA EN AÑOS
                                int ageYears = (int) (ageMonths / 12);
                                if (ageYears == 0) {
                                    ageYears = 1;
                                }
                                newVictim.setVictimAge((short) ageYears);
                                newVictim.setAgeTypeId((short) 1);
                                if (newVictim.getTypeId() == null) {
                                    if (ageYears >= 18) {
                                        newVictim.setTypeId(idTypesFacade.find((short) 1));
                                    } else {
                                        newVictim.setTypeId(idTypesFacade.find((short) 5));
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(HomicideMB.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }

                //DETERMINAR EL NUMERO DE IDENTIFICACION
                if (newVictim.getVictimNid() == null) {
                    newVictim.setVictimNid(String.valueOf(genNnFacade.findMax() + 1));
                    newVictim.setVictimClass((short) 2);//nn
                    newVictim.setTypeId(idTypesFacade.find((short) 6));//adulto sin identificacion
                    GenNn currentGenNn = genNnFacade.find(genNnFacade.findMax());
                    currentGenNn.setCodNn(genNnFacade.findMax() + 1);
                    genNnFacade.edit(currentGenNn);
                }

                //DETERMINAR TIPO DE IDENTIFICACION
                if (newVictim.getVictimNid() != null) {
                    if (newVictim.getTypeId() == null) {
                        newVictim.setTypeId(idTypesFacade.find((short) 1));//SI NI HAY EDAD DEJAR POR DEFECTO CEDULA
                    }
                }
                //PERSISTO
                try {
                    newVictim.setTagId(tagsFacade.find(newTag));
                    victimsFacade.create(newVictim);
                    fatalInjuriesFacade.create(newFatalInjurie);
                    fatalInjuryAccidentFacade.create(newFatalInjuryAccident);
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
                tuplesProcessed++;
                progress = (int) (tuplesProcessed * 100) / tuplesNumber;
                System.out.println("PROGRESO INGRESANDO ACCIDENTALES: " + String.valueOf(progress));
            }
            progress = 100;
            System.out.println("PROGRESO INGRESANDO ACCIDENTALES: " + String.valueOf(progress));
        } catch (SQLException ex) {
            System.out.println("errorSQL INGRESADNDO ACCIDENTALES: " + ex.toString());
        } catch (Exception ex) {
            System.out.println("EXCEPTION INGRESANDO ACCIDENTALES: " + ex.toString() + "  " + relationVar.getNameExpected() + " " + String.valueOf(tuplesProcessed));
        }
    }

    public void registerSCC_F_032() {
        /**
         * *********************************************************************
         * CARGA DE REGISTROS DE LA FICHA LCENF
         * ********************************************************************
         */
        tuplesNumber = 0;
        tuplesProcessed = 0;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            conx = new ConnectionJDBC();
            conx.connect();
            resultSetFileData = conx.consult("SELECT COUNT(*) FROM temp; ");
            resultSetFileData.next();
            tuplesNumber = resultSetFileData.getInt(1);//NUMERO DE TUPLAS
            resultSetFileData = conx.consult("SELECT * FROM temp ORDER BY id; ");//resultSetFileData contendra todos los registros de el archivo(tabla temp)
            conx.disconnect();
            progress = 0;
            columnsNames = new String[resultSetFileData.getMetaData().getColumnCount()];//NOMBRES DE LAS COLUMNAS
            int pos = 0;
            for (int i = 1; i <= resultSetFileData.getMetaData().getColumnCount(); i++) {
                columnsNames[pos] = resultSetFileData.getMetaData().getColumnName(i);
                pos++;
            }
            newTag = new Tags();//VARIABLES PARA CONJUNTOS DE REGISTROS
            newTag.setTagId(tagsFacade.findMax() + 1);
            newTag.setTagName(uploadFileMB.getTagName());
            newTag.setTagFileInput(uploadFileMB.getNameFile());
            newTag.setTagFileStored(uploadFileMB.getNameFile());
            newTag.setFormId(formsFacade.find(nameForm));
            tagsFacade.create(newTag);
            while (resultSetFileData.next()) {//recorro cada uno de los registros de la tabla temp                    
                newVictim = new Victims();
                newVictim.setVictimId(victimsFacade.findMax() + 1);
                newVictim.setVictimClass(Short.parseShort("1"));

                newNonFatalInjury = new NonFatalInjuries();
                newNonFatalDomesticViolence = new NonFatalDomesticViolence();
                newNonFatalInjury.setNonFatalInjuryId(nonFatalInjuriesFacade.findMax() + 1);
                selectInjuryFile = null;
                selectInjuryDetermined = null;
                newNonFatalInjury.setInputTimestamp(new Date());
                newNonFatalTransport = new NonFatalTransport();//nuevo non_fatal_transport
                newNonFatalTransport.setNonFatalInjuryId(newNonFatalInjury.getNonFatalInjuryId());
                newNonFatalInterpersonal = new NonFatalInterpersonal();//nuevo non_fatal_Interpersonal
                newNonFatalInterpersonal.setNonFatalInjuryId(newNonFatalInjury.getNonFatalInjuryId());
                newNonFatalSelfInflicted = new NonFatalSelfInflicted();//nuevo non_fatal_Self-Inflicted
                newNonFatalSelfInflicted.setNonFatalInjuryId(newNonFatalInjury.getNonFatalInjuryId());
                securityElementList = new ArrayList<SecurityElements>();//lista non_fatal_transport_security_element                
                abuseTypesList = new ArrayList<AbuseTypes>();//lista domestic_violence_abuse_type
                aggressorTypesList = new ArrayList<AggressorTypes>();//lista domestic_violence_aggressor_type                
                anatomicalLocationsList = new ArrayList<AnatomicalLocations>();//lista non_fatal_anatomical_location
                kindsOfInjurysList = new ArrayList<KindsOfInjury>();//lista non_fatal_kind_of_injury
                diagnosesList = new ArrayList<Diagnoses>();//lista non_fatal_diagnosis
                vulnerableGroupList = new ArrayList<VulnerableGroups>();// lista vector victim_vulnerable_group
                othersList = new ArrayList<Others>();
                value = "";
                name = "";
                surname = "";
                dia = "";//dia evento
                mes = "";//mes evento
                ao = "";//año del evento
                diacon = "";//dia de la semana cnsulta                                
                dia1 = "";//dia de la consulta
                mes1 = "";//mes de la consulta
                ao1 = "";//año de la consulta
                horas = "";//hora evento
                minutos = "";//minuto evento
                ampm = "";//ampm evento
                horas1 = "";//hora consulta
                minutos1 = "";//minuto consulta
                ampm1 = "";//ampm consulta
                hourInt = 0;
                minuteInt = 0;
                for (int posCol = 0; posCol < columnsNames.length; posCol++) {
                    value = null;
                    //DETERMINO QUE VALOR VOY A INGRESAR HACIENDO USO DE LAS FUNCIONES DE VALIDACION isNumeric,isAge... etc
                    relationVar = currentRelationsGroup.findRelationVar(columnsNames[posCol]);//determino la relacion de variables
                    //if (columnsNames[posCol].compareTo("edadcantid") == 0 && tuplesProcessed > 309) { columnsNames[posCol] = "edadcantid"; }
                    if (relationVar != null) {
                        switch (DataTypeEnum.convert(relationVar.getFieldType())) {//tipo de relacion
                            case text:
                                value = resultSetFileData.getString(columnsNames[posCol]);
                                break;
                            case integer:
                                value = isNumeric(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case age:
                                value = isAge(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case date:
                                value = isDate(resultSetFileData.getString(columnsNames[posCol]), relationVar.getDateFormat());
                                break;
                            case military:
                                value = isMilitary(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case hour:
                                value = isHour(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case minute:
                                value = isMinute(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case day:
                                value = isDay(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case month:
                                value = isMonth(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case year:
                                value = isYear(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case percentage:
                                value = isPercentage(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case NOVALUE:
                                value = isCategorical(resultSetFileData.getString(relationVar.getNameFound()), relationVar);
                                break;
                        }
                    }

                    continueProcces = false;
                    if (value != null) {
                        if (value.trim().length() != 0) {
                            continueProcces = true;
                        }
                    }
                    if (continueProcces) {
                        switch (SCC_F_032Enum.convert(relationVar.getNameExpected())) {
                            // ************************************************DATOS PARA LA TABLA victims                                
                            case nombre1:
                                if (name.trim().length() != 0) {
                                    name = name + " " + value;
                                } else {
                                    name = value;
                                }
                                break;
                            case nombre2:
                                if (name.trim().length() != 0) {
                                    name = name + " " + value;
                                } else {
                                    name = value;
                                }
                                break;
                            case apellid1:
                                if (surname.trim().length() != 0) {
                                    surname = surname + " " + value;
                                } else {
                                    surname = value;
                                }
                                break;
                            case apellid2:
                                if (surname.trim().length() != 0) {
                                    surname = surname + " " + value;
                                } else {
                                    surname = value;
                                }
                                break;
                            case tid:
                                newVictim.setTypeId(idTypesFacade.find(Short.parseShort(value)));
                                break;
                            case nid:
                                newVictim.setVictimNid(value);
                                break;
                            case asegu:
                                newVictim.setInsuranceId(insuranceFacade.find(Short.parseShort(value)));
                                break;
                            case fnacimiento:
                                try {
                                    currentDate = dateFormat.parse(value);
                                    newVictim.setVictimDateOfBirth(currentDate);
                                } catch (ParseException ex) {
                                }
                                break;
                            case medad:
                                newVictim.setAgeTypeId(Short.parseShort(value));
                                break;
                            case edadcantid:
                                newVictim.setVictimAge(Short.parseShort(value));
                                break;
                            case sexo:
                                newVictim.setGenderId(gendersFacade.find(Short.parseShort(value)));
                                break;
                            case ocupa:
                                try {
                                    newVictim.setJobId(jobsFacade.find(Short.parseShort(value)));
                                } catch (Exception e) {
                                }
                                break;
                            case getnico:
                                newVictim.setEthnicGroupId(ethnicGroupsFacade.find(Short.parseShort(value)));
                                break;
                            case baveres://barrio de residencia
                                newVictim.setVictimNeighborhoodId(neighborhoodsFacade.find(Integer.parseInt(value)));
                                break;
                            case dirres://direccion de residencia
                                newVictim.setVictimAddress(value);
                                break;
                            case telres:
                                newVictim.setVictimTelephone(value);
                                break;
                            case deptor:
                                newVictim.setResidenceDepartment(Short.parseShort(value));
                                break;
                            case municres://municipio residencia
                                splitArray = value.split("-");
                                if (splitArray.length == 2) {
                                    newVictim.setResidenceDepartment(Short.parseShort(splitArray[0]));
                                    newVictim.setResidenceMunicipality(Short.parseShort(splitArray[1]));
                                }
                                break;
                            // ************************************************DATOS PARA LA TABLA non_fatal_transport
                            case ttrans:
                                newNonFatalTransport.setTransportTypeId(transportTypesFacade.find(Short.parseShort(value)));
                                selectInjuryDetermined = injuriesFacade.find((short) 51);
                                break;
                            case tcontp:
                                newNonFatalTransport.setTransportCounterpartId(transportCounterpartsFacade.find(Short.parseShort(value)));
                                selectInjuryDetermined = injuriesFacade.find((short) 51);
                                break;
                            case tusuar:
                                newNonFatalTransport.setTransportUserId(transportUsersFacade.find(Short.parseShort(value)));
                                selectInjuryDetermined = injuriesFacade.find((short) 51);
                                break;
                            // ************************************************DATOS PARA LA TABLA non_fatal_interpersonal
                            case anteca://boleano->previous_antecedent                                    
                                newNonFatalInterpersonal.setPreviousAntecedent(boolean3Facade.find(Short.parseShort(value)));
                                selectInjuryDetermined = injuriesFacade.find((short) 50);
                                break;
                            case relacav://categorico->relationships_to_victim                                    
                                newNonFatalInterpersonal.setRelationshipVictimId(relationshipsToVictimFacade.find(Short.parseShort(value)));
                                selectInjuryDetermined = injuriesFacade.find((short) 50);
                                break;
                            case contex:
                                newNonFatalInterpersonal.setContextId(contextsFacade.find(Short.parseShort(value)));
                                selectInjuryDetermined = injuriesFacade.find((short) 50);
                                break;
                            case sexa:
                                newNonFatalInterpersonal.setAggressorGenderId(aggressorGendersFacade.find(Short.parseShort(value)));
                                selectInjuryDetermined = injuriesFacade.find((short) 50);
                                break;
                            // ************************************************DATOS PARA LA TABLA non_fatal_selft-inflicted
                            case intpre:
                                newNonFatalSelfInflicted.setPreviousAttempt(boolean3Facade.find(Short.parseShort(value)));//si                                    
                                selectInjuryDetermined = injuriesFacade.find((short) 52);
                                break;
                            case trment:
                                newNonFatalSelfInflicted.setMentalAntecedent(boolean3Facade.find(Short.parseShort(value)));//si                                    
                                selectInjuryDetermined = injuriesFacade.find((short) 52);
                                break;
                            case fprec:
                                newNonFatalSelfInflicted.setPrecipitatingFactorId(precipitatingFactorsFacade.find(Short.parseShort(value)));
                                selectInjuryDetermined = injuriesFacade.find((short) 52);
                                break;
                            // ************************************************DATOS PARA LA TABLA non_fatal_transport_security_element
                            case tsegu:
                                if (value.compareTo("2") == 0 || value.compareTo("NO") == 0) {
                                    securityElementList.add(securityElementsFacade.find((short) 6));
                                    selectInjuryDetermined = injuriesFacade.find((short) 51);
                                }
                                break;
                            case cintu:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    securityElementList.add(securityElementsFacade.find((short) 1));
                                    selectInjuryDetermined = injuriesFacade.find((short) 51);
                                }
                                break;
                            case cascom:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    securityElementList.add(securityElementsFacade.find((short) 2));
                                    selectInjuryDetermined = injuriesFacade.find((short) 51);
                                }
                                break;
                            case cascob:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    securityElementList.add(securityElementsFacade.find((short) 3));
                                    selectInjuryDetermined = injuriesFacade.find((short) 51);
                                }
                                break;
                            case chale:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    securityElementList.add(securityElementsFacade.find((short) 4));
                                    selectInjuryDetermined = injuriesFacade.find((short) 51);
                                }
                                break;
                            case otroel:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    securityElementList.add(securityElementsFacade.find((short) 5));
                                    selectInjuryDetermined = injuriesFacade.find((short) 51);
                                }
                                break;
                            // ************************************************DATOS PARA LA TABLA domestic_violence_abuse_type
                            case ma1:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 1));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case ma2:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 2));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case ma3:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 3));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case ma4:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 4));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case ma5:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 5));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case ma6:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 6));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case ma7:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 7));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            // ************************************************DATOS PARA LA TABLA domestic_violence_aggressor_type
                            case ag1:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 1));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case ag2:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 2));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case ag3:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 3));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case ag4:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 4));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case ag5:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 5));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case ag6:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 6));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case ag7:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 7));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case ag8:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 8));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case ag9:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 9));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            // ************************************************DATOS PARA LA TABLA non_fatal_anatomical_location
                            case sistem:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    anatomicalLocationsList.add(new AnatomicalLocations((short) 1));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case craneo:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    anatomicalLocationsList.add(new AnatomicalLocations((short) 2));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case ojos:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    anatomicalLocationsList.add(new AnatomicalLocations((short) 3));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case maxilof:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    anatomicalLocationsList.add(new AnatomicalLocations((short) 4));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case cuello:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    anatomicalLocationsList.add(new AnatomicalLocations((short) 5));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case torax:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    anatomicalLocationsList.add(new AnatomicalLocations((short) 6));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case abdomen:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    anatomicalLocationsList.add(new AnatomicalLocations((short) 7));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case columna:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    anatomicalLocationsList.add(new AnatomicalLocations((short) 8));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case pelvis:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    anatomicalLocationsList.add(new AnatomicalLocations((short) 9));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case msup:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    anatomicalLocationsList.add(new AnatomicalLocations((short) 10));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case minf:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    anatomicalLocationsList.add(new AnatomicalLocations((short) 11));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case ositio:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    anatomicalLocationsList.add(new AnatomicalLocations((short) 98));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            // ************************************************DATOS PARA LA TABLA non_fatal_kind_of_injury
                            case lacera:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    kindsOfInjurysList.add(new KindsOfInjury((short) 1));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case cortada:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    kindsOfInjurysList.add(new KindsOfInjury((short) 2));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case lesprof:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    kindsOfInjurysList.add(new KindsOfInjury((short) 3));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case esglux:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    kindsOfInjurysList.add(new KindsOfInjury((short) 4));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case fractura:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    kindsOfInjurysList.add(new KindsOfInjury((short) 5));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case quemadur:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    kindsOfInjurysList.add(new KindsOfInjury((short) 6));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case contusi:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    kindsOfInjurysList.add(new KindsOfInjury((short) 7));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case orgsist:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    kindsOfInjurysList.add(new KindsOfInjury((short) 8));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case tce:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    kindsOfInjurysList.add(new KindsOfInjury((short) 9));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case olesi:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    kindsOfInjurysList.add(new KindsOfInjury((short) 98));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case nosabe:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    kindsOfInjurysList.add(new KindsOfInjury((short) 99));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            // ************************************************DATOS PARA LA TABLA non_fatal_diagnosis
                            case cie10:
                                diagnosesList.add(diagnosesFacade.find(value));
                                break;
                            case cie102:
                                diagnosesList.add(diagnosesFacade.find(value));
                                break;
                            case cie103:
                                diagnosesList.add(diagnosesFacade.find(value));
                                break;
                            case cie104:
                                diagnosesList.add(diagnosesFacade.find(value));
                                break;
                            // ************************************************DATOS PARA LA TABLA victim_vulnerable_group
                            case despl:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    vulnerableGroupList.add(vulnerableGroupsFacade.find((short) 1));
                                }
                                break;
                            case disca:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    vulnerableGroupList.add(vulnerableGroupsFacade.find((short) 2));
                                }
                                break;
                            // ************************************************DATOS PARA LA TABLA non_fatal_injuries
                            case instisal:
                                newNonFatalInjury.setNonFatalDataSourceId(nonFatalDataSourcesFacade.find(Short.parseShort(value)));
                                break;
                            case baveevn://bario donde ocurrio el evento
                                newNonFatalInjury.setInjuryNeighborhoodId(neighborhoodsFacade.find(Integer.parseInt(value)));
                                break;
                            case direv://Direccion del evento
                                newNonFatalInjury.setInjuryAddress(value);
                                break;
                            case fechaev:
                                try {
                                    currentDate = dateFormat.parse(value);
                                    newNonFatalInjury.setInjuryDate(currentDate);
                                } catch (ParseException ex) {
                                }
                                break;
                            case diaev://duai de la semana evento
                                //newNonFatalInjuries.setInjuryDayOfWeek(value);
                                break;
                            case dia://dia del evento
                                dia = value;
                            case mes://mes evento
                                mes = value;
                                break;
                            case ao://año del evento
                                ao = value;
                                break;
                            case horaev:
                                n = new Date();
                                hourInt = Integer.parseInt(value.substring(0, 2));
                                minuteInt = Integer.parseInt(value.substring(2, 4));
                                n.setHours(hourInt);
                                n.setMinutes(minuteInt);
                                n.setSeconds(0);
                                newNonFatalInjury.setInjuryTime(n);
                                break;
                            case horas://hora evento
                                horas = value;
                                break;
                            case minutos://minuto evento
                                minutos = value;
                                break;
                            case ampm://ampm evento
                                ampm = value;
                                break;
                            case fechacon:
                                try {
                                    currentDate = dateFormat.parse(value);
                                    newNonFatalInjury.setCheckupDate(currentDate);
                                } catch (ParseException ex) {
                                }
                                break;
                            case diacon://dia de la semana cnsulta                                
                                newNonFatalInjury.setInjuryDayOfWeek(daysFacade.find(Short.parseShort(value)).getDaysName());
                                break;
                            case dia1://dia de la consulta
                                dia1 = value;
                                break;
                            case mes1://mes de la consulta
                                mes1 = value;
                                break;
                            case ao1://año de la consulta
                                ao1 = value;
                                break;
                            case horacon:
                                n = new Date();
                                hourInt = Integer.parseInt(value.substring(0, 2));
                                minuteInt = Integer.parseInt(value.substring(2, 4));
                                n.setHours(hourInt);
                                n.setMinutes(minuteInt);
                                n.setSeconds(0);
                                newNonFatalInjury.setCheckupTime(n);
                                break;
                            case horas1://hora consulta
                                horas1 = value;
                                break;
                            case minutos1://minuto consulta
                                minutos1 = value;
                                break;
                            case ampm1://ampm consulta
                                ampm1 = value;
                                break;
                            case remitido:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    newNonFatalInjury.setSubmittedPatient(true);
                                } else {
                                    newNonFatalInjury.setSubmittedPatient(false);
                                }
                                break;
                            case deqips:
                                newNonFatalInjury.setSubmittedDataSourceId(nonFatalDataSourcesFacade.find(Short.parseShort(value)));
                                break;
                            case intenci:
                                newNonFatalInjury.setIntentionalityId(intentionalitiesFacade.find(Short.parseShort(value)));
                                break;
                            case lugar:
                                newNonFatalInjury.setInjuryPlaceId(nonFatalPlacesFacade.find(Short.parseShort(value)));
                                break;
                            case activi:
                                newNonFatalInjury.setActivityId(activitiesFacade.find(Short.parseShort(value)));
                                break;
                            case mecobj:
                                newNonFatalInjury.setMechanismId(mechanismsFacade.find(Short.parseShort(value)));
                                break;
                            case alcohol:
                                newNonFatalInjury.setUseAlcoholId(useAlcoholDrugsFacade.find(Short.parseShort(value)));
                                break;
                            case drogas:
                                newNonFatalInjury.setUseDrugsId(useAlcoholDrugsFacade.find(Short.parseShort(value)));
                                break;
                            case gradogra:
                                newNonFatalInjury.setBurnInjuryDegree(Short.parseShort(value));
                                break;
                            case porcent:
                                newNonFatalInjury.setBurnInjuryPercentage(Short.parseShort(value));
                                break;
                            case destino:
                                DestinationsOfPatient selectDestinationsOfPatient = destinationsOfPatientFacade.find(Short.parseShort(value));
                                newNonFatalInjury.setDestinationPatientId(selectDestinationsOfPatient);
                                break;
                            //guardar campos otros
                            case cual://otro gupo etnico
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 1));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case olugar://otro lugar
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 2));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case oactivi://otra actividad
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 3));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case altura://caul altura
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 4));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case cpolvora:
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 5));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case cdntrl:
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 6));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case omecobj://otro mecanismo u objeto de la lesion
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 7));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
//                          case oanimal://cual animal 8
//                          break;
                            case ofactor://otro factor precipitante
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 9));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            //case cual4://otro tipo de agresor 10
                            //    break;
                            //case cual5://otro tipo de maltrato 11
                            //    break;                                
                            case orelaci://otro tipo de relacion con la victima
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 12));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case totrans://otro tipo de transporte
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 13));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case tocontp://otro tipo de contraparte
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 14));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case tosuar://otro tipo de usuario
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 15));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case cotro://cual otro sitio anatomico
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 16));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case colesi:
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 17));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case refer://otro destino del paciente
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 18));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case medico:
                                newNonFatalInjury.setHealthProfessionalId(healthProfessionalsFacade.find(Integer.parseInt(value)));
                                break;
                            case digita:
                                newNonFatalInjury.setUserId(usersFacade.find(Integer.parseInt(value)));
                                break;
                            default:
                        }
                    }

                }

                //SI NO SE DETERMINA EL BARRIO SE COLOCA SIN DATO URBANO
                if (newVictim.getVictimNeighborhoodId() == null) {
                    newVictim.setVictimNeighborhoodId(neighborhoodsFacade.find((int) 52001));
                }
                //SI NO SE DETERMINA EL BARRIO SE COLOCA SIN DATO URBANO
                if (newNonFatalInjury.getInjuryNeighborhoodId() == null) {
                    newNonFatalInjury.setInjuryNeighborhoodId(neighborhoodsFacade.find((int) 52001));
                }

                //DATOS DE LA CONLUSTA..........................................
                //SI NO HAY FECHA DE CONSULTA PASAR LA DE EVENTO
                if (newNonFatalInjury.getCheckupDate() == null) {
                    if (newNonFatalInjury.getInjuryDate() != null) {
                        newNonFatalInjury.setCheckupDate(newNonFatalInjury.getInjuryDate());
                    }
                }
                //SI NO HAY HORA DE CONSULTA PASAR LA DE EVENTO
                if (newNonFatalInjury.getCheckupTime() == null) {
                    if (newNonFatalInjury.getInjuryTime() != null) {
                        newNonFatalInjury.setCheckupTime(newNonFatalInjury.getInjuryTime());
                    }
                }

                //SI NO HAY FECHA DE CONSULTA TRATAR DE CALCULAR MEDIANTE LAS VARIABLES dia, mes, ao
                if (newNonFatalInjury.getCheckupDate() == null) {
                    dia1 = haveData(dia1);
                    mes1 = haveData(mes1);
                    ao1 = haveData(ao1);
                    if (dia1 != null && mes1 != null && ao1 != null) {
                        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                        Date fechaI;
                        fechaI = formato.parse(dia1 + "/" + mes1 + "/" + ao1);
                        newNonFatalInjury.setCheckupDate(fechaI);
                    }
                }
                //SI NO HAY HORA DE CONSULTA TRATAR DE CALCULAR MEDIANTE LAS VARIABLES hora,minutos,ampm
                if (newNonFatalInjury.getCheckupTime() == null) {
                    horas1 = haveData(horas1);
                    minutos1 = haveData(minutos1);
                    ampm1 = haveData(ampm1);
                    if (horas1 != null && minutos1 != null && ampm1 != null) {
                        hourInt = Integer.parseInt(horas1);
                        minuteInt = Integer.parseInt(minutos1);
                        if (ampm1.compareTo("2") == 0) {
                            hourInt = hourInt + 12;
                            if (hourInt == 24) {
                                hourInt = 0;
                            }
                        }
                        currentDate = new Date();
                        currentDate.setHours(hourInt);
                        currentDate.setMinutes(minuteInt);
                        currentDate.setSeconds(0);
                        newNonFatalInjury.setCheckupTime(currentDate);
                    }
                }

                //DATOS PARA EL EVENTO..........................................
                //SI NO HAY FECHA DE EVENTO PASAR LA DE CONSULTA
                if (newNonFatalInjury.getInjuryDate() == null) {
                    if (newNonFatalInjury.getCheckupDate() != null) {
                        newNonFatalInjury.setInjuryDate(newNonFatalInjury.getCheckupDate());
                    }
                }

                //SI NO HAY HORA DE EVENTO PASAR LA DE CONSULTA
                if (newNonFatalInjury.getInjuryTime() == null) {
                    if (newNonFatalInjury.getCheckupTime() != null) {
                        newNonFatalInjury.setInjuryTime(newNonFatalInjury.getCheckupTime());
                    }
                }


                //SI NO HAY FECHA DE EVENTO TRATAR DE CALCULAR MEDIANTE LAS VARIABLES dia, mes, ao
                if (newNonFatalInjury.getInjuryDate() == null) {
                    dia = haveData(dia);
                    mes = haveData(mes);
                    ao = haveData(ao);
                    if (dia != null && mes != null && ao != null) {
                        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                        Date fechaI;
                        fechaI = formato.parse(dia + "/" + mes + "/" + ao);
                        newNonFatalInjury.setInjuryDate(fechaI);
                    }
                }

                //SI NO HAY HORA DE EVENTO TRATAR DE CALCULAR MEDIANTE LAS VARIABLES hora,minutos,ampm
                if (newNonFatalInjury.getInjuryTime() == null) {
                    horas = haveData(horas);
                    minutos = haveData(minutos);
                    ampm = haveData(ampm);
                    if (horas != null && minutos != null && ampm != null) {
                        hourInt = Integer.parseInt(horas);
                        minuteInt = Integer.parseInt(minutos);
                        if (ampm.compareTo("2") == 0) {
                            hourInt = hourInt + 12;
                            if (hourInt == 24) {
                                hourInt = 0;
                            }
                        }
                        currentDate = new Date();
                        currentDate.setHours(hourInt);
                        currentDate.setMinutes(minuteInt);
                        currentDate.setSeconds(0);
                        newNonFatalInjury.setInjuryTime(currentDate);
                    }
                }


                //SI LA HORA DE LA CONSULTA ES 0000 PASAR LA HORA DEL EVENTO A LA DE LA CONSULTA
                if (newNonFatalInjury.getCheckupTime() != null) {
                    if (newNonFatalInjury.getInjuryTime() != null) {
                        int hour = newNonFatalInjury.getInjuryTime().getHours();
                        int minute = newNonFatalInjury.getInjuryTime().getMinutes();
                        if (hour == 0 && minute == 0) {
                            newNonFatalInjury.setInjuryTime(newNonFatalInjury.getCheckupTime());
                        }
                    } else {
                        newNonFatalInjury.setInjuryTime(newNonFatalInjury.getCheckupTime());
                    }
                }
                //SI NO HAY DIA DE LA SEMANA DEL EVENTO SE CALCULA
                if (newNonFatalInjury.getInjuryDate() != null) {
                    if (newNonFatalInjury.getInjuryDayOfWeek() == null) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(newNonFatalInjury.getInjuryDate());
                        newNonFatalInjury.setInjuryDayOfWeek(intToDay(cal.get(Calendar.DAY_OF_WEEK)));
                    }
                }

                //UNION DE NOMBRES Y APELLIOS
                name = name + " " + surname;
                if (name.trim().length() > 1) {
                    newVictim.setVictimName(name);
                }
                //VARIABLES PARA CONJUNTOS DE REGISTROS
                newVictim.setTagId(tagsFacade.find(newTag.getTagId()));

                //SI NO SE DETERMINA LA INSTITUCION DE SALUD SE ALMACENA LA QUE VIENE DEL FORMULARIO                
                if (newNonFatalInjury.getNonFatalDataSourceId() == null) {
                    if (currentSource.compareTo("OBSERVATORIO DEL DELITO") != 0) {
                        newNonFatalInjury.setNonFatalDataSourceId(nonFatalDataSourcesFacade.findByName(currentSource));
                    }
                }

                //SI NO SE DETERMINA LA EDAD VERIFICAR SI HAY FECHA DE NACIMIENTO
                if (newVictim.getVictimDateOfBirth() != null) {
                    if (newVictim.getVictimAge() == null) {
                        int birthMonths;
                        int eventMonths;

                        Calendar systemCalendar = Calendar.getInstance();
                        Calendar birthCalendar = Calendar.getInstance();
                        birthCalendar.setTime(newVictim.getVictimDateOfBirth());

                        try {//DETERMINO LA EDAD EN MESES
                            birthMonths = birthCalendar.get(Calendar.YEAR);
                            birthMonths = birthMonths * 12;
                            birthMonths = birthMonths + birthCalendar.get(Calendar.MONTH);
                            if (newNonFatalInjury.getInjuryDate() != null) {//SE CALCULA EDAD SEGUN LA FECHA DE EVENTO
                                systemCalendar.setTime(newNonFatalInjury.getInjuryDate());
                            }
                            eventMonths = systemCalendar.get(Calendar.YEAR);
                            eventMonths = eventMonths * 12;
                            eventMonths = eventMonths + systemCalendar.get(Calendar.MONTH);

                            int ageMonths = eventMonths - birthMonths;
                            if (ageMonths < 0) {
                                System.out.println("ERROR fecha de nacimiento mayor a la del sistema o evento: ");
                            } else {
                                int ageYears = (int) (ageMonths / 12);
                                if (ageYears == 0) {
                                    ageYears = 1;
                                }
                                newVictim.setVictimAge((short) ageYears);
                                newVictim.setAgeTypeId((short) 1);
                                if (newVictim.getTypeId() == null) {
                                    if (ageYears >= 18) {
                                        newVictim.setTypeId(idTypesFacade.find((short) 1));
                                    } else {
                                        newVictim.setTypeId(idTypesFacade.find((short) 5));
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(HomicideMB.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                //SI EXISTE EDAD Y NO HAY TIPO DE EDAD DETERMINARLA EN AÑOS

                //DETERMINAR EL NUMERO DE IDENTIFICACION
                if (newVictim.getVictimNid() == null) {
                    newVictim.setVictimNid(String.valueOf(genNnFacade.findMax() + 1));
                    newVictim.setVictimClass((short) 2);//nn
                    newVictim.setTypeId(idTypesFacade.find((short) 6));//adulto sin identificacion
                    GenNn currentGenNn = genNnFacade.find(genNnFacade.findMax());
                    currentGenNn.setCodNn(genNnFacade.findMax() + 1);
                    genNnFacade.edit(currentGenNn);
                }


                //DETERMINAR TIPO DE IDENTIFICACION
                if (newVictim.getVictimNid() != null) {
                    if (newVictim.getTypeId() == null) {
                        //DETERMINAR SEGUN EDAD SI ES POSIBLE
                        newVictim.setTypeId(idTypesFacade.find((short) 1));
                    }
                }
                //AGREGO LAS LISTAS NO VACIAS///////////////////////////////////
                if (!anatomicalLocationsList.isEmpty()) {
                    newNonFatalInjury.setAnatomicalLocationsList(anatomicalLocationsList);
                }
                if (!securityElementList.isEmpty()) {
                    newNonFatalTransport.setSecurityElementsList(securityElementList);
                }
                if (!abuseTypesList.isEmpty()) {
                    newNonFatalDomesticViolence.setAbuseTypesList(abuseTypesList);
                }
                if (!aggressorTypesList.isEmpty()) {
                    newNonFatalDomesticViolence.setAggressorTypesList(aggressorTypesList);
                }
                if (!kindsOfInjurysList.isEmpty()) {
                    newNonFatalInjury.setKindsOfInjuryList(kindsOfInjurysList);
                }
                if (!diagnosesList.isEmpty()) {
                    newNonFatalInjury.setDiagnosesList(diagnosesList);
                }
                if (!vulnerableGroupList.isEmpty()) {
                    newVictim.setVulnerableGroupsList(vulnerableGroupList);
                }

                //DETERMINO TIPO DE LESION//////////////////////////////////////
                if (selectInjuryFile == null) {
                    if (selectInjuryDetermined == null) {//no se pudo determinar se coloca por defecto //54. No intencional
                        newNonFatalInjury.setInjuryId(injuriesFacade.find((short) 54));
                    } else {//se determino segun los datos que llegaron                        
                        newNonFatalInjury.setInjuryId(selectInjuryDetermined);
                    }
                } else {//se detrmino por archivo
                    newNonFatalInjury.setInjuryId(selectInjuryFile);
                }

                if (newNonFatalInjury.getInjuryId().getInjuryId() == (short) 53) {//53 ES POR QUE ES VIF 
                    newNonFatalInjury.setInjuryId(injuriesFacade.find((short) 55));//CAMBIA A 55 PARA SER VIF INGRESADA DESDE LCENF
                }
                //PERSISTO//////////////////////////////////////////////////////
                try {

                    if (!othersList.isEmpty()) {
                        newVictim.setOthersList(othersList);
                    }
                    newNonFatalInjury.setVictimId(newVictim);
                    victimsFacade.create(newVictim);//PERSISTO LA VICTIMA                
                    nonFatalInjuriesFacade.create(newNonFatalInjury);//PERSISTO LA LESION NO FATAL                    
                    if (newNonFatalInjury.getInjuryId().getInjuryId().compareTo((short) 50) == 0) {//VIOLENCIA INTERPERSONAL
                        newNonFatalInterpersonal.setNonFatalInjuries(nonFatalInjuriesFacade.find(newNonFatalInjury.getNonFatalInjuryId()));
                        nonFatalInterpersonalFacade.create(newNonFatalInterpersonal);
                    } else if (newNonFatalInjury.getInjuryId().getInjuryId().compareTo((short) 51) == 0) {//ACCIDENTE DE TRANSITO 
                        newNonFatalTransport.setNonFatalInjuries(nonFatalInjuriesFacade.find(newNonFatalInjury.getNonFatalInjuryId()));
                        nonFatalTransportFacade.create(newNonFatalTransport);
                    } else if (newNonFatalInjury.getInjuryId().getInjuryId().compareTo((short) 52) == 0) {//INTENCIONAL AUTOINFLINGIDA 
                        newNonFatalSelfInflicted.setNonFatalInjuries(nonFatalInjuriesFacade.find(newNonFatalInjury.getNonFatalInjuryId()));
                        nonFatalSelfInflictedFacade.create(newNonFatalSelfInflicted);
                    } else if (newNonFatalInjury.getInjuryId().getInjuryId().compareTo((short) 53) == 0) {//VIOLENCIA INTRAFAMILIAR
                        newNonFatalDomesticViolence.setNonFatalInjuries(nonFatalInjuriesFacade.find(newNonFatalInjury.getNonFatalInjuryId()));
                        nonFatalDomesticViolenceFacade.create(newNonFatalDomesticViolence);
                    } else if (newNonFatalInjury.getInjuryId().getInjuryId().compareTo((short) 54) == 0) {//NO INTENCIONAL 
                        //POR DEFECTO ES INTENCIONAL
                    }
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
                tuplesProcessed++;
                progress = (int) (tuplesProcessed * 100) / tuplesNumber;
                System.out.println("PROGRESO INGRESANDO LCENF: " + String.valueOf(progress));
            }
            progress = 100;
            System.out.println("PROGRESO INGRESANDO LCENF: " + String.valueOf(progress));

        } catch (SQLException ex) {
            System.out.println("errorSQL INGRESANDO LCENF: " + ex.toString());
        } catch (Exception ex2) {
            System.out.println("EXCEPTION INGRESANDO LCENF: " + ex2.toString() + "  " + " " + String.valueOf(tuplesProcessed));
        }

    }

    public void registerSCC_F_033() {
        /**
         * *********************************************************************
         * CARGA DE REGISTROS DE LA FICHA MUERTES VIF
         * ********************************************************************
         */
        tuplesNumber = 0;
        tuplesProcessed = 0;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            conx = new ConnectionJDBC();
            conx.connect();
            resultSetFileData = conx.consult("SELECT COUNT(*) FROM temp; ");
            resultSetFileData.next();
            tuplesNumber = resultSetFileData.getInt(1);//NUMERO DE TUPLAS
            resultSetFileData = conx.consult("SELECT * FROM temp ORDER BY id; ");//resultSetFileData contendra todos los registros de el archivo(tabla temp)
            conx.disconnect();
            progress = 0;
            columnsNames = new String[resultSetFileData.getMetaData().getColumnCount()];//NOMBRES DE LAS COLUMNAS
            int pos = 0;
            for (int i = 1; i <= resultSetFileData.getMetaData().getColumnCount(); i++) {
                columnsNames[pos] = resultSetFileData.getMetaData().getColumnName(i);
                pos++;
            }
            //maxTag = tagsFacade.findMax() + 1;//VARIABLES PARA CONJUNTOS DE REGISTROS
            newTag = new Tags();//(maxTag, uploadFileMB.getNameFile(), uploadFileMB.getNameFile());
            newTag.setTagId(tagsFacade.findMax() + 1);
            newTag.setTagName(uploadFileMB.getTagName());
            newTag.setTagFileInput(uploadFileMB.getNameFile());
            newTag.setTagFileStored(uploadFileMB.getNameFile());
            newTag.setFormId(formsFacade.find(nameForm));
            tagsFacade.create(newTag);
            while (resultSetFileData.next()) {//recorro cada uno de los registros de la tabla temp                    
                newVictim = new Victims();
                newVictim.setVictimId(victimsFacade.findMax() + 1);
                newVictim.setVictimClass(Short.parseShort("1"));
                newNonFatalInjury = new NonFatalInjuries();
                newNonFatalDomesticViolence = new NonFatalDomesticViolence();
                newNonFatalInjury.setNonFatalInjuryId(nonFatalInjuriesFacade.findMax() + 1);
                newNonFatalInjury.setInputTimestamp(new Date());
                actionsToTakeList = new ArrayList<ActionsToTake>();
                diagnosesList = new ArrayList<Diagnoses>();//lista non_fatal_diagnosis
                vulnerableGroupList = new ArrayList<VulnerableGroups>();// lista vector victim_vulnerable_group
                othersList = new ArrayList<Others>();
                value = "";
                name = "";
                surname = "";
                dia = "";//dia evento
                mes = "";//mes evento
                ao = "";//año del evento
                diacon = "";//dia de la semana cnsulta                                
                dia1 = "";//dia de la consulta
                mes1 = "";//mes de la consulta
                ao1 = "";//año de la consulta
                horas = "";//hora evento
                minutos = "";//minuto evento
                ampm = "";//ampm evento
                horas1 = "";//hora consulta
                minutos1 = "";//minuto consulta
                ampm1 = "";//ampm consulta
                hourInt = 0;
                minuteInt = 0;
                for (int posCol = 0; posCol < columnsNames.length; posCol++) {
                    value = null;
                    //DETERMINO QUE VALOR VOY A INGRESAR HACIENDO USO DE LAS FUNCIONES DE VALIDACION isNumeric,isAge... etc
                    relationVar = currentRelationsGroup.findRelationVar(columnsNames[posCol]);//determino la relacion de variables
                    //if (columnsNames[posCol].compareTo("edadcantid") == 0 && tuplesProcessed > 309) { columnsNames[posCol] = "edadcantid"; }
                    if (relationVar != null) {
                        switch (DataTypeEnum.convert(relationVar.getFieldType())) {//tipo de relacion
                            case text:
                                value = resultSetFileData.getString(columnsNames[posCol]);
                                break;
                            case integer:
                                value = isNumeric(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case age:
                                value = isAge(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case date:
                                value = isDate(resultSetFileData.getString(columnsNames[posCol]), relationVar.getDateFormat());
                                break;
                            case military:
                                value = isMilitary(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case hour:
                                value = isHour(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case minute:
                                value = isMinute(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case day:
                                value = isDay(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case month:
                                value = isMonth(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case year:
                                value = isYear(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case percentage:
                                value = isPercentage(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case NOVALUE:
                                value = isCategorical(resultSetFileData.getString(relationVar.getNameFound()), relationVar);
                                break;
                        }
                    }

                    continueProcces = false;
                    if (value != null) {
                        if (value.trim().length() != 0) {
                            continueProcces = true;
                        }
                    }
                    if (continueProcces) {

                        switch (SCC_F_033Enum.convert(relationVar.getNameExpected())) {
                            // ************************************************DATOS PARA LA TABLA victims                                
                            //case registro:
                            //    break;
                            //case clave:
                            //    break;
                            case instituc:
                                newNonFatalDomesticViolence.setDomesticViolenceDataSourceId(domesticViolenceDataSourcesFacade.find(Short.parseShort(value)));
                                break;
                            case apellido:
                                if (surname.trim().length() != 0) {
                                    surname = surname + " " + value;
                                } else {
                                    surname = value;
                                }
                                break;
                            case apellido1:
                                if (surname.trim().length() != 0) {
                                    surname = surname + " " + value;
                                } else {
                                    surname = value;
                                }
                                break;
                            case nombre:
                                if (name.trim().length() != 0) {
                                    name = name + " " + value;
                                } else {
                                    name = value;
                                }
                                break;
                            case nombre1:
                                if (name.trim().length() != 0) {
                                    name = name + " " + value;
                                } else {
                                    name = value;
                                }
                                break;
                            case tipo:
                                newVictim.setTypeId(idTypesFacade.find(Short.parseShort(value)));
                                break;
                            case numero:
                                newVictim.setVictimNid(value);
                                break;
                            case medida:
                                newVictim.setAgeTypeId(Short.parseShort(value));
                                break;
                            case edad:
                                newVictim.setVictimAge(Short.parseShort(value));
                                break;
                            case sexo:
                                newVictim.setGenderId(gendersFacade.find(Short.parseShort(value)));
                                break;
                            case ocupacio:
                                newVictim.setJobId(jobsFacade.find(Short.parseShort(value)));
                                break;
                            case asegurad:
                                newVictim.setInsuranceId(insuranceFacade.find(Short.parseShort(value)));
                                break;
                            case departam:
                                newVictim.setResidenceDepartment(Short.parseShort(value));
                                break;
                            case codigo:
                                newVictim.setResidenceDepartment(Short.parseShort(value));
                                break;
                            case municipi:
                                splitArray = value.split("-");
                                if (splitArray.length == 2) {
                                    newVictim.setResidenceDepartment(Short.parseShort(splitArray[0]));
                                    newVictim.setResidenceMunicipality(Short.parseShort(splitArray[1]));
                                }
                                break;
                            case codigo1:
                                splitArray = value.split("-");
                                if (splitArray.length == 2) {
                                    newVictim.setResidenceDepartment(Short.parseShort(splitArray[0]));
                                    newVictim.setResidenceMunicipality(Short.parseShort(splitArray[1]));
                                }
                                break;
                            case barrio://barrio residencia
                                newVictim.setVictimNeighborhoodId(neighborhoodsFacade.find(Integer.parseInt(value)));
                                break;
                            case codigo2://barrio residencia
                                newVictim.setVictimNeighborhoodId(neighborhoodsFacade.find(Integer.parseInt(value)));
                                break;
                            case direccio://direccion residencia
                                newVictim.setVictimAddress(value);
                                break;
                            case telefono:
                                newVictim.setVictimTelephone(value);
                                break;
                            //case departam1://departamento evento
                            //    break;
                            //case codigo3://departamento evento
                            //    break;
                            //case municip://municpio evento
                            //    break;
                            //case codigo4://municipio evento
                            //    break;
                            case barrio1://barrio evento
                                newNonFatalInjury.setInjuryNeighborhoodId(neighborhoodsFacade.find(Integer.parseInt(value)));
                                break;
                            case codigo5://barrio evento
                                newNonFatalInjury.setInjuryNeighborhoodId(neighborhoodsFacade.find(Integer.parseInt(value)));
                                break;
                            case direccio1://direccion evento
                                newNonFatalInjury.setInjuryAddress(value);
                                break;
                            case fecha://dia del evento
                                dia = value;
                                break;
                            case mes:
                                mes = value;
                                break;
                            case ao:
                                ao = value;
                                break;
                            case fecha1://fecha del evento
                                try {
                                    currentDate = dateFormat.parse(value);
                                    newNonFatalInjury.setInjuryDate(currentDate);
                                } catch (ParseException ex) {
                                }
                                break;
                            case dia://dia de la semana del evento
                                newNonFatalInjury.setInjuryDayOfWeek(daysFacade.find(Short.parseShort(value)).getDaysName());
                                break;
                            case hora://hora del evento
                                horas = value;
                                break;
                            case minutos://minuto del evento
                                minutos = value;
                                break;
                            case ampm:
                                ampm = value;
                                break;
                            case hora1://hora militar del evento
                                n = new Date();
                                hourInt = Integer.parseInt(value.substring(0, 2));
                                minuteInt = Integer.parseInt(value.substring(2, 4));
                                n.setHours(hourInt);
                                n.setMinutes(minuteInt);
                                n.setSeconds(0);
                                newNonFatalInjury.setInjuryTime(n);
                                break;
                            case fecha2://dia de la consulta
                                dia1 = value;
                                break;
                            case mes1://mes de la consulta
                                mes1 = value;
                                break;
                            case ao1:
                                break;
                            case fecha3://fecha de la consulta
                                try {
                                    currentDate = dateFormat.parse(value);
                                    newNonFatalInjury.setCheckupDate(currentDate);
                                } catch (ParseException ex) {
                                }
                                break;
                            case dia1://dia de la semana de la consuta

                                break;
                            case hora2://hora de la consulta
                                horas1 = value;
                                break;
                            case minutos1://minuto de la consulta
                                minutos1 = value;
                                break;
                            case ampm1://ampm consulta
                                ampm1 = value;
                                break;
                            case hora3://hora militar de la consulta
                                n = new Date();
                                hourInt = Integer.parseInt(value.substring(0, 2));
                                minuteInt = Integer.parseInt(value.substring(2, 4));
                                n.setHours(hourInt);
                                n.setMinutes(minuteInt);
                                n.setSeconds(0);
                                newNonFatalInjury.setCheckupTime(n);
                                break;
                            case remitido:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    newNonFatalInjury.setSubmittedPatient(true);
                                } else {
                                    newNonFatalInjury.setSubmittedPatient(false);
                                }
                                break;
                            case de:
                                newNonFatalInjury.setSubmittedDataSourceId(nonFatalDataSourcesFacade.find(Short.parseShort(value)));
                                break;
                            case lugar:
                                newNonFatalInjury.setInjuryPlaceId(nonFatalPlacesFacade.find(Short.parseShort(value)));
                                break;

                            case activida:
                                newNonFatalInjury.setActivityId(activitiesFacade.find(Short.parseShort(value)));
                                break;

                            case mecanism:
                                newNonFatalInjury.setMechanismId(mechanismsFacade.find(Short.parseShort(value)));
                                break;

                            //CAMPOS OTROS
                            case cual://otro lugar
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 1));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case cual1://otra actividad
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 2));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case altura://"En caso de caida: a que altura fue"
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 3));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case cual2://"En caso de quemadura por polvora: que tipo de polvora fue"
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 4));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case cual3://"En caso de desastre natural: que tipo de desastre fue"
                                //AL PARECER NO SE USA
                                break;
                            case otro://"Otro tipo de mecanismo u objeto"
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 6));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case otro2://otro grupo etnico
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 8));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case otro1://otro grupo vulnerable
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 9));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case cual4://"En caso de otro tipo de agresor: Cual"
                                booleanValue = false;
                                for (int i = 0; i < aggressorTypesList.size(); i++) {
                                    if (aggressorTypesList.get(i).getAggressorTypeId() == (short) 10) {
                                        booleanValue = true;
                                        break;
                                    }
                                }
                                if (!booleanValue) {
                                    aggressorTypesList.add(new AggressorTypes((short) 10));
                                }
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 10));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case cual5://"En caso de otro tipo de maltrato: Cual"
                                booleanValue = false;
                                for (int i = 0; i < abuseTypesList.size(); i++) {
                                    if (abuseTypesList.get(i).getAbuseTypeId() == (short) 8) {
                                        booleanValue = true;
                                        break;
                                    }
                                }
                                if (!booleanValue) {
                                    abuseTypesList.add(new AbuseTypes((short) 8));
                                }
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 11));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case cual6://"En caso de otra acción a realizar: Cual"
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 12));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            //FIN CAMPOS OTROS---------------------------------------
                            case uso://"Uso de alcohol"
                                newNonFatalInjury.setUseAlcoholId(useAlcoholDrugsFacade.find(Short.parseShort(value)));
                                break;
                            case uso1://"Uso de drogas"
                                newNonFatalInjury.setUseDrugsId(useAlcoholDrugsFacade.find(Short.parseShort(value)));
                                break;
                            case grado://"En caso de quemadura: Grado más grave"
                                newNonFatalInjury.setBurnInjuryDegree(Short.parseShort(value));
                                break;
                            case porcente://"En caso de quemadura: Porcentaje del cuerpo quemado"
                                newNonFatalInjury.setBurnInjuryPercentage(Short.parseShort(value));
                                break;
                            case grupo://GRUPO VULNERABLE
                                VulnerableGroups auxVulnerableGroups = vulnerableGroupsFacade.find(Short.parseShort(value));
                                //List<VulnerableGroups> vulnerableGroupsList = new ArrayList<VulnerableGroups>();
                                vulnerableGroupList.add(auxVulnerableGroups);
                                newVictim.setVulnerableGroupsList(vulnerableGroupList);
                                break;
                            case grupo1://GRUPO ETNICO
                                newVictim.setEthnicGroupId(ethnicGroupsFacade.find(Short.parseShort(value)));
                                break;
                            case ma1:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 17));
                                }
                                break;
                            case ma2:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 2));
                                }
                                break;
                            case ma3:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 3));
                                }
                                break;
                            case ma4:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 4));
                                }
                                break;
                            case ma5:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 5));
                                }
                                break;
                            case ma6:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 6));
                                }
                                break;
                            case ma7:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 7));
                                }
                                break;
                            case ma8:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    booleanValue = false;
                                    for (int i = 0; i < abuseTypesList.size(); i++) {
                                        if (abuseTypesList.get(i).getAbuseTypeId() == (short) 8) {
                                            booleanValue = true;
                                            break;
                                        }
                                    }
                                    if (!booleanValue) {
                                        abuseTypesList.add(new AbuseTypes((short) 8));
                                    }
                                }
                                break;
                            case field1:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 1));
                                }
                                break;
                            case field2:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 2));
                                }
                                break;
                            case field3:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 3));
                                }
                                break;
                            case field4:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 4));
                                }
                                break;
                            case field5:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 5));
                                }
                                break;
                            case field6:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 6));
                                }
                                break;
                            case field7:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 7));
                                }
                                break;
                            case field8:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 8));
                                }
                                break;
                            case field9:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 9));
                                }
                                break;
                            case field10:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    booleanValue = false;
                                    for (int i = 0; i < aggressorTypesList.size(); i++) {
                                        if (aggressorTypesList.get(i).getAggressorTypeId() == (short) 10) {
                                            booleanValue = true;
                                            break;
                                        }
                                    }
                                    if (!booleanValue) {
                                        aggressorTypesList.add(new AggressorTypes((short) 10));
                                    }
                                }
                                break;
                            case concilia:
                                actionsToTakeList.add(actionsToTakeFacade.find((short) 1));
                                break;
                            case caucion:
                                actionsToTakeList.add(actionsToTakeFacade.find((short) 2));
                                break;
                            case dictamen:
                                actionsToTakeList.add(actionsToTakeFacade.find((short) 3));
                                break;
                            case remision:
                                actionsToTakeList.add(actionsToTakeFacade.find((short) 4));
                                break;
                            case remision1:
                                actionsToTakeList.add(actionsToTakeFacade.find((short) 5));
                                break;
                            case remision2:
                                actionsToTakeList.add(actionsToTakeFacade.find((short) 6));
                                break;
                            case remision3:
                                actionsToTakeList.add(actionsToTakeFacade.find((short) 7));
                                break;
                            case medidas:
                                actionsToTakeList.add(actionsToTakeFacade.find((short) 8));
                                break;
                            case remision4:
                                actionsToTakeList.add(actionsToTakeFacade.find((short) 9));
                                break;
                            case atencion:
                                actionsToTakeList.add(actionsToTakeFacade.find((short) 10));
                                break;
                            case restable:
                                actionsToTakeList.add(actionsToTakeFacade.find((short) 11));
                                break;
                            case otra:
                                actionsToTakeList.add(actionsToTakeFacade.find((short) 12));
                                break;
                            case sindat:
                                actionsToTakeList.add(actionsToTakeFacade.find((short) 13));
                                break;
                            case responsab:
                                newNonFatalInjury.setUserId(usersFacade.find(Integer.parseInt(value)));
                                break;
                            default:
                        }
                    }
                }

                //SI NO SE DETERMINA EL BARRIO SE COLOCA SIN DATO URBANO
                if (newVictim.getVictimNeighborhoodId() == null) {
                    newVictim.setVictimNeighborhoodId(neighborhoodsFacade.find((int) 52001));
                }
                //SI NO SE DETERMINA EL BARRIO SE COLOCA SIN DATO URBANO
                if (newNonFatalInjury.getInjuryNeighborhoodId() == null) {
                    newNonFatalInjury.setInjuryNeighborhoodId(neighborhoodsFacade.find((int) 52001));
                }
                //DATOS DE LA CONLUSTA..........................................
                //SI NO HAY FECHA DE CONSULTA PASAR LA DE EVENTO
                if (newNonFatalInjury.getCheckupDate() == null) {
                    if (newNonFatalInjury.getInjuryDate() != null) {
                        newNonFatalInjury.setCheckupDate(newNonFatalInjury.getInjuryDate());
                    }
                }
                //SI NO HAY HORA DE CONSULTA PASAR LA DE EVENTO
                if (newNonFatalInjury.getCheckupTime() == null) {
                    if (newNonFatalInjury.getInjuryTime() != null) {
                        newNonFatalInjury.setCheckupTime(newNonFatalInjury.getInjuryTime());
                    }
                }
                //SI NO HAY FECHA DE CONSULTA TRATAR DE CALCULAR MEDIANTE LAS VARIABLES dia, mes, ao
                if (newNonFatalInjury.getCheckupDate() == null) {
                    dia1 = haveData(dia1);
                    mes1 = haveData(mes1);
                    ao1 = haveData(ao1);
                    if (dia1 != null && mes1 != null && ao1 != null) {
                        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                        Date fechaI;
                        fechaI = formato.parse(dia1 + "/" + mes1 + "/" + ao1);
                        newNonFatalInjury.setCheckupDate(fechaI);
                    }
                }
                //SI NO HAY HORA DE CONSULTA TRATAR DE CALCULAR MEDIANTE LAS VARIABLES hora,minutos,ampm
                if (newNonFatalInjury.getCheckupTime() == null) {
                    horas1 = haveData(horas1);
                    minutos1 = haveData(minutos1);
                    ampm1 = haveData(ampm1);
                    if (horas1 != null && minutos1 != null && ampm1 != null) {
                        hourInt = Integer.parseInt(horas1);
                        minuteInt = Integer.parseInt(minutos1);
                        if (ampm1.compareTo("2") == 0) {
                            hourInt = hourInt + 12;
                            if (hourInt == 24) {
                                hourInt = 0;
                            }
                        }
                        currentDate = new Date();
                        currentDate.setHours(hourInt);
                        currentDate.setMinutes(minuteInt);
                        currentDate.setSeconds(0);
                        newNonFatalInjury.setCheckupTime(currentDate);
                    }
                }
                //DATOS PARA EL EVENTO..........................................
                //SI NO HAY FECHA DE EVENTO PASAR LA DE CONSULTA
                if (newNonFatalInjury.getInjuryDate() == null) {
                    if (newNonFatalInjury.getCheckupDate() != null) {
                        newNonFatalInjury.setInjuryDate(newNonFatalInjury.getCheckupDate());
                    }
                }
                //SI NO HAY HORA DE EVENTO PASAR LA DE CONSULTA
                if (newNonFatalInjury.getInjuryTime() == null) {
                    if (newNonFatalInjury.getCheckupTime() != null) {
                        newNonFatalInjury.setInjuryTime(newNonFatalInjury.getCheckupTime());
                    }
                }
                //SI NO HAY FECHA DE EVENTO TRATAR DE CALCULAR MEDIANTE LAS VARIABLES dia, mes, ao
                if (newNonFatalInjury.getInjuryDate() == null) {
                    dia = haveData(dia);
                    mes = haveData(mes);
                    ao = haveData(ao);
                    if (dia != null && mes != null && ao != null) {
                        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                        Date fechaI;
                        fechaI = formato.parse(dia + "/" + mes + "/" + ao);
                        newNonFatalInjury.setInjuryDate(fechaI);
                    }
                }
                //SI NO HAY HORA DE EVENTO TRATAR DE CALCULAR MEDIANTE LAS VARIABLES hora,minutos,ampm
                if (newNonFatalInjury.getInjuryTime() == null) {
                    horas = haveData(horas);
                    minutos = haveData(minutos);
                    ampm = haveData(ampm);
                    if (horas != null && minutos != null && ampm != null) {
                        hourInt = Integer.parseInt(horas);
                        minuteInt = Integer.parseInt(minutos);
                        if (ampm.compareTo("2") == 0) {
                            hourInt = hourInt + 12;
                            if (hourInt == 24) {
                                hourInt = 0;
                            }
                        }
                        currentDate = new Date();
                        currentDate.setHours(hourInt);
                        currentDate.setMinutes(minuteInt);
                        currentDate.setSeconds(0);
                        newNonFatalInjury.setInjuryTime(currentDate);
                    }
                }
                //SI LA HORA DE LA CONSULTA ES 0000 PASAR LA HORA DEL EVENTO A LA DE LA CONSULTA
                if (newNonFatalInjury.getCheckupTime() != null) {
                    if (newNonFatalInjury.getInjuryTime() != null) {
                        int hour = newNonFatalInjury.getInjuryTime().getHours();
                        int minute = newNonFatalInjury.getInjuryTime().getMinutes();
                        if (hour == 0 && minute == 0) {
                            newNonFatalInjury.setInjuryTime(newNonFatalInjury.getCheckupTime());
                        }
                    } else {
                        newNonFatalInjury.setInjuryTime(newNonFatalInjury.getCheckupTime());
                    }
                }
                //SI NO HAY DIA DE LA SEMANA DEL EVENTO SE CALCULA
                if (newNonFatalInjury.getInjuryDate() != null) {
                    if (newNonFatalInjury.getInjuryDayOfWeek() == null) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(newNonFatalInjury.getInjuryDate());
                        newNonFatalInjury.setInjuryDayOfWeek(intToDay(cal.get(Calendar.DAY_OF_WEEK)));
                    }
                }
                //UNION DE NOMBRES Y APELLIOS
                name = name + " " + surname;
                if (name.trim().length() > 1) {
                    newVictim.setVictimName(name);
                }
                //VARIABLES PARA CONJUNTOS DE REGISTROS
                newVictim.setTagId(tagsFacade.find(newTag.getTagId()));
                //SI NO SE DETERMINA LA INSTITUCION DE SALUD SE ALMACENA LA QUE VIENE DEL FORMULARIO                
                if (newNonFatalInjury.getNonFatalDataSourceId() == null) {
                    if (currentSource.compareTo("OBSERVATORIO DEL DELITO") != 0) {
                        newNonFatalInjury.setNonFatalDataSourceId(nonFatalDataSourcesFacade.findByName(currentSource));
                    }
                }
                //SI NO SE DETERMINA LA EDAD VERIFICAR SI HAY FECHA DE NACIMIENTO
                if (newVictim.getVictimDateOfBirth() != null) {
                    if (newVictim.getVictimAge() == null) {
                        int birthMonths;
                        int eventMonths;

                        Calendar systemCalendar = Calendar.getInstance();
                        Calendar birthCalendar = Calendar.getInstance();
                        birthCalendar.setTime(newVictim.getVictimDateOfBirth());

                        try {//DETERMINO LA EDAD EN MESES
                            birthMonths = birthCalendar.get(Calendar.YEAR);
                            birthMonths = birthMonths * 12;
                            birthMonths = birthMonths + birthCalendar.get(Calendar.MONTH);
                            if (newNonFatalInjury.getInjuryDate() != null) {//SE CALCULA EDAD SEGUN LA FECHA DE EVENTO
                                systemCalendar.setTime(newNonFatalInjury.getInjuryDate());
                            }
                            eventMonths = systemCalendar.get(Calendar.YEAR);
                            eventMonths = eventMonths * 12;
                            eventMonths = eventMonths + systemCalendar.get(Calendar.MONTH);

                            int ageMonths = eventMonths - birthMonths;
                            if (ageMonths < 0) {
                                System.out.println("ERROR fecha de nacimiento mayor a la del sistema o evento: ");
                            } else {
                                int ageYears = (int) (ageMonths / 12);
                                if (ageYears == 0) {
                                    ageYears = 1;
                                }
                                newVictim.setVictimAge((short) ageYears);
                                newVictim.setAgeTypeId((short) 1);
                                if (newVictim.getTypeId() == null) {
                                    if (ageYears >= 18) {
                                        newVictim.setTypeId(idTypesFacade.find((short) 1));
                                    } else {
                                        newVictim.setTypeId(idTypesFacade.find((short) 5));
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(HomicideMB.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                //DETERMINAR EL NUMERO DE IDENTIFICACION
                if (newVictim.getVictimNid() == null) {
                    newVictim.setVictimNid(String.valueOf(genNnFacade.findMax() + 1));
                    newVictim.setVictimClass((short) 2);//nn
                    newVictim.setTypeId(idTypesFacade.find((short) 6));//adulto sin identificacion
                    GenNn currentGenNn = genNnFacade.find(genNnFacade.findMax());
                    currentGenNn.setCodNn(genNnFacade.findMax() + 1);
                    genNnFacade.edit(currentGenNn);
                }
                //QUEDA TIPO DE IDENTIFICACION POR DEFECTO CEDULA SI NO HAY
                if (newVictim.getVictimNid() != null) {
                    if (newVictim.getTypeId() == null) {
                        //DETERMINAR SEGUN EDAD SI ES POSIBLE
                        newVictim.setTypeId(idTypesFacade.find((short) 1));
                    }
                }
                //agrego las listas las listas
                if (!anatomicalLocationsList.isEmpty()) {
                    newNonFatalInjury.setAnatomicalLocationsList(anatomicalLocationsList);
                }
                if (!abuseTypesList.isEmpty()) {
                    newNonFatalDomesticViolence.setAbuseTypesList(abuseTypesList);
                }
                if (!aggressorTypesList.isEmpty()) {
                    newNonFatalDomesticViolence.setAggressorTypesList(aggressorTypesList);
                }
                if (!actionsToTakeList.isEmpty()) {
                    newNonFatalDomesticViolence.setActionsToTakeList(actionsToTakeList);
                }
                if (!vulnerableGroupList.isEmpty()) {
                    newVictim.setVulnerableGroupsList(vulnerableGroupList);
                }
                newNonFatalInjury.setInjuryId(injuriesFacade.find(Short.parseShort("53")));

                //PERSISTO//////////////////////////////////////////////////////////////////
                try {
                    if (!othersList.isEmpty()) {
                        newVictim.setOthersList(othersList);
                    }
                    newNonFatalInjury.setVictimId(newVictim);
                    victimsFacade.create(newVictim);//PERSISTO LA VICTIMA                
                    nonFatalInjuriesFacade.create(newNonFatalInjury);//PERSISTO LA LESION NO FATAL                    
                    newNonFatalDomesticViolence.setNonFatalInjuries(nonFatalInjuriesFacade.find(newNonFatalInjury.getNonFatalInjuryId()));
                    nonFatalDomesticViolenceFacade.create(newNonFatalDomesticViolence);

                } catch (Exception e) {
                    System.out.println(e.toString());
                }
                tuplesProcessed++;
                progress = (int) (tuplesProcessed * 100) / tuplesNumber;
                System.out.println("PROGRESO INGRESANDO VIF: " + String.valueOf(progress));
            }
            progress = 100;
            System.out.println("PROGRESO INGRESANDO VIF: " + String.valueOf(progress));

        } catch (SQLException ex) {
            System.out.println("errorSQL INGRESANDO VIF: " + ex.toString());
        } catch (Exception ex) {
            System.out.println("EXCEPTION INGRESANDO VIF: " + ex.toString() + "  " + relationVar.getNameExpected() + " " + String.valueOf(tuplesProcessed));
        }
    }

    public void btnRegisterDataClick() {
        continueProcces = false;
        if (errorsControlMB.getErrorControlArrayList().isEmpty()) {
            continueProcces = true;
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se deben corregir todos los errores para poder realizar la carga."));
            progress = 100;
        }
        if (continueProcces == true) {
            switch (FormsEnum.convert(nameForm.replace("-", "_"))) {
                case SCC_F_028:
                    registerSCC_F_028();
                    break;
                case SCC_F_029:
                    registerSCC_F_029();
                    break;
                case SCC_F_030:
                    registerSCC_F_030();
                    break;
                case SCC_F_031:
                    registerSCC_F_031();
                    break;
                case SCC_F_032:
                    registerSCC_F_032();
                    break;
                case SCC_F_033:
                    registerSCC_F_033();
                    break;
            }
        }
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //VALIDACIONES ---------------------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    private String intToDay(int i) {
        if (i == Calendar.MONDAY) {
            return "Lunes";
        } else if (i == Calendar.TUESDAY) {
            return "Martes";
        } else if (i == Calendar.WEDNESDAY) {
            return "Miércoles";
        } else if (i == Calendar.THURSDAY) {
            return "Jueves";
        } else if (i == Calendar.FRIDAY) {
            return "Viernes";
        } else if (i == Calendar.SATURDAY) {
            return "Sábado";
        } else {//if (i == Calendar.SUNDAY) 
            return "Domingo";
        }
    }

    private String isPercentage(String str) {
        /*
         * validacion de si un numero es porcentaje 1-100
         */
        if (str.trim().length() == 0 || str.compareTo("0") == 0 || str.compareTo("NULL") == 0) {
            return "";
        }
        try {
            int i = Integer.parseInt(str);
            if (i == 0) {
                return "";
            }
            if (i < 1 || i > 100) {
                return null;
            }
            return String.valueOf(i);
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    private String isLevel(String str) {
        /*
         * validacion de si un numero es >= 0
         */
        if (str.trim().length() == 0 || str.compareTo("NULL") == 0) {
            return "";
        }
        try {
            int i = Integer.parseInt(str);
            if (i >= 0) {
                return String.valueOf(i);
            }
            return null;
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    private String isDay(String str) {
        /*
         * validacion de si un numero de 1 y 31 null=invalido ""=aceptado pero
         * vacio "valor"=aceptado y me dice el valor
         */
        if (str.trim().length() == 0) {
            return "";
        }
        try {
            int i = Integer.parseInt(str);
            if (i > 0 && i < 32) {
                return String.valueOf(i);
            }
            return null;
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    private String isMonth(String str) {
        /*
         * validacion de si un numero de 1 y 12 null=invalido ""=aceptado pero
         * vacio "valor"=aceptado y me dice el valor
         */
        if (str.trim().length() == 0) {
            return "";
        }
        try {
            int i = Integer.parseInt(str);
            if (i > 0 && i < 13) {
                return String.valueOf(i);
            }
            return null;
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    private String isYear(String str) {
        /*
         * validacion de si un numero de 1 y 12 null=invalido ""=aceptado pero
         * vacio "valor"=aceptado y me dice el valor
         */
        if (str.trim().length() == 0) {
            return "";
        }
        if (str.trim().length() != 2 && str.trim().length() != 4) {
            return null;
        }
        try {
            int i = Integer.parseInt(str);
            return String.valueOf(i);
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    private String isMinute(String str) {
        /*
         * validacion de si un numero de 1 y 12 null=invalido ""=aceptado pero
         * vacio "valor"=aceptado y me dice el valor
         */
        if (str.trim().length() == 0) {
            return "";
        }
        try {
            int i = Integer.parseInt(str);
            if (i > -1 && i < 60) {
                return String.valueOf(i);
            }
            return null;
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    private String isHour(String str) {
        /*
         * validacion de si un numero de 1 y 12 null=invalido ""=aceptado pero
         * vacio "valor"=aceptado y me dice el valor
         */
        if (str.trim().length() == 0) {
            return "";
        }
        try {
            int i = Integer.parseInt(str);
            if (i > 0 && i < 25) {
                return String.valueOf(i);
            }
            return null;
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    private String isNumeric(String str) {
        /*
         * validacion de si un string es entero null=invalido ""=aceptado pero
         * vacio "valor"=aceptado y me dice el valor
         */
        if (str.trim().length() == 0) {
            return "";
        }
        try {
            str.replaceAll(",", "");
            str.replaceAll(".", "");
            Integer.parseInt(str);
            return str;
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    private String isDate(String f, String format) {
        /*
         * validacion de si un string es una fecha de un determinado formato
         * null=invalido ""=aceptado pero vacio "valor"=aceptado y me dice el
         * valor
         */
        if (f.trim().length() == 0) {
            return "";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date fDate;
        String fStr;
        try {
            fDate = dateFormat.parse(f);
            //obtener año actual
            Calendar systemCalendar = Calendar.getInstance();
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.setTime(fDate);
            if (systemCalendar.compareTo(currentCalendar) < 0) {//current date es mayor a la des sistema
                currentCalendar.add(Calendar.YEAR, -100);
            }
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            fStr = dateFormat.format(currentCalendar.getTime());
            return fStr;
        } catch (ParseException ex) {
            return null;
        }
    }

    private String isMilitary(String strIn) {
        /*
         * validacion de si un string es un hora miitar null=invalido
         * ""=aceptado pero vacio "valor"=aceptado y me dice el valor
         */
        String str = strIn;

        //----------------------------------------------
        //determinar si hay caracteres
        if (str.trim().length() == 0) {
            return "";
            //return "no se acepta cadenas vacias";
        }

        //----------------------------------------------
        //quitar " AM A.M.
        str = str.toUpperCase();
        if (str.indexOf("AM") != -1) {
            int a = 0;
            a++;

        }
        str = str.replace(" ", "");
        str = str.replace("AM", "");
        str = str.replace("A.M.", "");
        str = str.replace("\"", "");

        //determinar si es un timestamp
        if (str.trim().length() == 12 || str.trim().length() == 8) {
            String[] splitMilitary = str.split(":");
            if (splitMilitary.length == 3) {
                try {
                    int h = Integer.parseInt(splitMilitary[0]);
                    int m = Integer.parseInt(splitMilitary[1]);
                    if (splitMilitary[0].length() == 1) {
                        splitMilitary[0] = "0" + splitMilitary[0];
                    }
                    if (splitMilitary[1].length() == 1) {
                        splitMilitary[1] = "0" + splitMilitary[1];
                    }
                    if (h > 24 || h < 0) {
                        return null;
                        //return "La hora debe estar entre 0 y 23";
                    }
                    if (m > 59 || m < 0) {
                        return null;
                        //return "los minutos deben estar entre 0 y 59";
                    }
                    if (h == 24 && m != 0) {
                        return null;
                        //return "Si la hora es 24 los minutos deben ser cero";
                    }
                    return splitMilitary[0] + splitMilitary[1];
                } catch (Exception ex) {
                }
            }
        }


        //----------------------------------------------
        //determinar si tiene como separador   ; + . , :
        String[] splitMilitary = null;
        if (str.split(":").length == 2) {
            splitMilitary = str.split(":");
        } else if (str.split(",").length == 2) {
            splitMilitary = str.split(",");
        } else if (str.split(";").length == 2) {
            splitMilitary = str.split(";");
        } else if (str.split("\\+").length == 2) {
            splitMilitary = str.split("\\+");
        } else if (str.split("\\.").length == 2) {
            splitMilitary = str.split("\\.");
        }
        if (splitMilitary != null) {
            try {
                int h = Integer.parseInt(splitMilitary[0]);
                int m = Integer.parseInt(splitMilitary[1]);
                if (splitMilitary[0].length() == 1) {
                    splitMilitary[0] = "0" + splitMilitary[0];
                }
                if (splitMilitary[1].length() == 1) {
                    splitMilitary[1] = "0" + splitMilitary[1];
                }
                if (h == 24) {
                    if (m == 0) {
                        return splitMilitary[0] + splitMilitary[1];
                    } else {
                        return null;
                        //return "Si la hora es 24 los minutos solo pueden ser 0";
                    }
                }
                if (h > 24 || h < 0) {
                    return null;
                    //return "La hora debe estar entre 0 y 24";
                }
                if (m > 59 || m < 0) {
                    return null;
                    //return "los minutos deben estar entre 0 y 59";
                }
                return splitMilitary[0] + splitMilitary[1];
            } catch (Exception ex) {
            }
        }

        //----------------------------------------------
        //determinar si tiene caracteres diferentes a    0123456789
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) != '0' && str.charAt(i) != '1' && str.charAt(i) != '2'
                    && str.charAt(i) != '3' && str.charAt(i) != '4' && str.charAt(i) != '5'
                    && str.charAt(i) != '6' && str.charAt(i) != '7' && str.charAt(i) != '8'
                    && str.charAt(i) != '9') {
                return null;
                //return "Valor no aceptado como hora militar";
            }
        }

        //----------------------------------------------
        //verificar si tiene menos de 4 cifras 
        if (str.trim().length() < 5) {
            //lo completo con ceros
            if (str.trim().length() == 3) {
                str = "0" + str;
            } else if (str.trim().length() == 2) {
                str = "00" + str;
            } else if (str.trim().length() == 1) {
                str = "000" + str;
            }
            try {
                int h = Integer.parseInt(str.substring(0, 2));
                int m = Integer.parseInt(str.substring(2, 4));
                if (h > 24 || h < 0) {
                    return null;
                    //return "La hora debe estar entre 0 y 23";
                }
                if (m > 59 || m < 0) {
                    return null;
                    //return "los minutos deben estar entre 0 y 59";
                }
                if (h == 24 && m != 0) {
                    return null;
                    //return "Si la hora es 24 los minutos deben ser cero";
                }
                return str;
            } catch (Exception ex) {
            }
        } else {
            return null;
            //return "Una hora militar debe tener menos de 4 digitos";
        }

        return null;
        //return "Valor no aceptado como hora militar";
    }

    private String isAge(String str) {
        /*
         * validacion de si un string es numero entero o edad definida en meses
         * y años null = invalido "" = aceptado pero vacio "valor" = aceptado y
         * me dice el valor
         */
        //String[] splitAge;
        if (str.trim().length() == 0) {
            //splitAge = new String[1];
            //splitAge[0] = "";
            return "";
        }
        try {//intento convertirlo en entero
            int a = Integer.parseInt(str);
            if (a > 199 || a < 0) {
                return null;
            }
            if (a == 0) {
                return "1";
            }
            return String.valueOf(a);
            //splitAge = new String[1];
            //splitAge[0] = str;
            //return splitAge;
        } catch (Exception ex) {
        }
        try {//determinar si esta definida en años meses
            String[] splitAge = str.split(" ");
            if (splitAge.length == 4) {
                int m = Integer.parseInt(splitAge[0]);
                int y = Integer.parseInt(splitAge[2]);
                if (y > 199) {
                    return null;
                }
                if (y == 0) {
                    return "1";
                }

                return String.valueOf(y);
            }
            return null;
        } catch (Exception ex) {
            return null;
        }
    }

    private String isCategorical(String valueFound, RelationVar relationVar) {
        /*
         * validacion de si un valor esta dentro de una categoria, o es
         * descartado, retorna el id respectivo a la tabla categorica
         */
        if (valueFound.trim().length() == 0) {
            return "";
        }

        //se valida con respecto a las relaciones de valores
        if (relationVar.getTypeComparisonForCode() == true) {
            for (int i = 0; i < relationVar.getRelationValueList().size(); i++) {
                if (relationVar.getRelationValueList().get(i).getNameFound().compareTo(valueFound) == 0) {
                    return formsAndFieldsDataMB.findIdByCategoricalCode(relationVar.getNameExpected(), relationVar.getRelationValueList().get(i).getNameExpected());
                }
            }
        } else {
            for (int i = 0; i < relationVar.getRelationValueList().size(); i++) {
                if (relationVar.getRelationValueList().get(i).getNameFound().compareTo(valueFound) == 0) {
                    return formsAndFieldsDataMB.findIdByCategoricalName(relationVar.getNameExpected(), relationVar.getRelationValueList().get(i).getNameExpected());
                }
            }
        }

        //verificar si es descartado
        for (int i = 0; i < relationVar.getDiscardedValues().size(); i++) {
            if (valueFound.compareTo(relationVar.getDiscardedValues().get(i)) == 0) {
                return "";
            }
        }
        //se valida con respecto a los valores esperados
        if (relationVar.getTypeComparisonForCode() == true) {
            return formsAndFieldsDataMB.findIdByCategoricalCode(relationVar.getNameExpected(), valueFound);
        } else {
            return formsAndFieldsDataMB.findIdByCategoricalName(relationVar.getNameExpected(), valueFound);
        }
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES GET Y SET DE LAS VARIABLES ---------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public void setFormsAndFieldsDataMB(FormsAndFieldsDataMB formsAndFieldsDataMB) {
        this.formsAndFieldsDataMB = formsAndFieldsDataMB;
    }

    public void setRelationshipOfVariablesMB(RelationshipOfVariablesMB relationshipOfVariablesMB) {
        this.relationshipOfVariablesMB = relationshipOfVariablesMB;
    }

    public void setStoredRelationsMB(StoredRelationsMB storedRelationsMB) {
        this.storedRelationsMB = storedRelationsMB;
    }

    public StoredRelationsMB getStoredRelationsMB() {
        return storedRelationsMB;
    }

    public ErrorsControlMB getErrorsControlMB() {
        return errorsControlMB;
    }

    public void setErrorsControlMB(ErrorsControlMB errorsControlMB) {
        this.errorsControlMB = errorsControlMB;
    }

    public LoginMB getLoginMB() {
        return loginMB;
    }

    public void setLoginMB(LoginMB loginMB) {
        this.loginMB = loginMB;
    }

    public String getNameForm() {
        return nameForm;
    }

    public void setNameForm(String nameForm) {
        this.nameForm = nameForm;
    }

    public boolean isBtnRegisterDataDisabled() {
        return btnRegisterDataDisabled;
    }

    public void setBtnRegisterDataDisabled(boolean btnRegisterDataDisabled) {
        this.btnRegisterDataDisabled = btnRegisterDataDisabled;
    }

    public boolean isBtnValidateDisabled() {
        return btnValidateDisabled;
    }

    public void setBtnValidateDisabled(boolean btnValidateDisabled) {
        this.btnValidateDisabled = btnValidateDisabled;
    }

    public Integer getProgressValidate() {
        return progressValidate;
    }

    public void setProgressValidate(Integer progressValidate) {
        this.progressValidate = progressValidate;
    }

    public UploadFileMB getUploadFileMB() {
        return uploadFileMB;
    }

    public void setUploadFileMB(UploadFileMB uploadFileMB) {
        this.uploadFileMB = uploadFileMB;
    }

    public String getCurrentSource() {
        return currentSource;
    }

    public void setCurrentSource(String currentSource) {
        this.currentSource = currentSource;
    }
}
