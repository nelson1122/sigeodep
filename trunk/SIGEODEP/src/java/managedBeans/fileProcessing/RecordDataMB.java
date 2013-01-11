/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.fileProcessing;

import beans.connection.ConnectionJdbcMB;
import beans.enumerators.*;
import beans.errorsControl.ErrorControl;
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
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import managedBeans.forms.HomicideMB;
import managedBeans.login.LoginMB;
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
    private RelationGroup currentRelationsGroup;
    //private FormsAndFieldsDataMB formsAndFieldsDataMB;
    private StoredRelationsMB storedRelationsMB;
    private UploadFileMB uploadFileMB;
    private LoginMB loginMB;
    private ErrorsControlMB errorsControlMB;
    private String[] columnsNames;
    private String nameForm = "";
    //private ConnectionJDBC conx = null;//conexion sin persistencia a postgres   
    private RelationVariables relationVar;
    private int tuplesNumber;
    private int tuplesProcessed;
    private boolean btnRegisterDataDisabled = true;
    //private boolean btnValidateDisabled = true;
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
    private String nameTableTemp = "temp";
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //MANEJO E LA BARRA DE PROGRESO DEL ALMACENAMIENTO ---------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    private Integer progress;
    private Integer progressValidate;//
    private int errorsNumber = 0;
    private int currentSource = 0;
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
        progressValidate = 100;
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
    ConnectionJdbcMB connectionJdbcMB;

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES DE PROPOSITO GENERAL ---------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    /*
     * primer funcion que se ejecuta despues del constructor que inicializa
     * variables y carga la conexion por jdbc
     */
    @PostConstruct
    private void initialize() {
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
    }

    public RecordDataMB() {
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
        loginMB = (LoginMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{loginMB}", LoginMB.class);
        nameTableTemp = "temp" + loginMB.getLoginname();
    }

    public void reset() {
        /*
         * Cargar el formulario con los valores iniciales
         */
        progressValidate = 0;
        btnRegisterDataDisabled = true;
        //btnValidateDisabled = true;
    }

    private boolean relationshipsRequired() {
        boolean noErrors = true;
        currentRelationsGroup = relationshipOfVariablesMB.getCurrentRelationsGroup();//tomo el grupos_vulnerables de relaciones de valores y de variables
        errorsControlMB.setErrorControlArrayList(new ArrayList<ErrorControl>());//arreglo de errores            
        RelationVariables newRelationVar = new RelationVariables();//"", "", "error", false, ""
        switch (FormsEnum.convert(nameForm.replace("-", "_"))) {//tipo de relacion
            case SCC_F_028:
            case SCC_F_029:
            case SCC_F_030:
            case SCC_F_031:
                //RELACION PARA FECHA DE EVENTO
                if (currentRelationsGroup.findRelationVarByNameExpected("fecha_evento") == null) {
                    if (currentRelationsGroup.findRelationVarByNameExpected("dia_evento") == null) {
                        noErrors = false;
                    }
                    if (currentRelationsGroup.findRelationVarByNameExpected("mes_evento") == null) {
                        noErrors = false;
                    }
                    if (currentRelationsGroup.findRelationVarByNameExpected("año_evento") == null) {
                        noErrors = false;
                    }
                }
                if (noErrors == false) {//no se puede determinar la dia_evento
                    errorsControlMB.addError(new ErrorControl(newRelationVar, "REQUIRED VALIDATION", "No existe manera de determinar la fecha del evento", "Diríjase a la sección relacion de variables y realice la asociacion correspondiente para la variable esperada (fecha_evento) o las variables esperadas (dia,mes,ao)"));
                    errorsNumber++;
                }
                //RELACION PARA TIPO DE IDENTIFICACION                
                if (currentRelationsGroup.findRelationVarByNameExpected("numero_identificacion_victima") == null) {
                    noErrors = false;
                    errorsControlMB.addError(new ErrorControl(newRelationVar, "REQUIRED VALIDATION", "No existe manera de determinar la identificacion de la víctima", "Diríjase a la sección relacion de variables y realice la asociacion correspondiente para la variable esperada (nid)"));
                    errorsNumber++;
                }
                break;
            case SCC_F_033:
                //RELACION PARA FECHA DE EVENTO
                if (currentRelationsGroup.findRelationVarByNameExpected("fecha_evento") == null) {
                    if (currentRelationsGroup.findRelationVarByNameExpected("dia_evento") == null) {
                        noErrors = false;
                    }
                    if (currentRelationsGroup.findRelationVarByNameExpected("mes_evento") == null) {
                        noErrors = false;
                    }
                    if (currentRelationsGroup.findRelationVarByNameExpected("año_evento") == null) {
                        noErrors = false;
                    }
                }
                if (noErrors == false) {//no se puede determinar la dia_evento
                    errorsControlMB.addError(new ErrorControl(newRelationVar, "REQUIRED VALIDATION", "No existe manera de determinar la fecha del evento", "Diríjase a la sección relacion de variables y realice la asociacion correspondiente para la variable esperada (fecha1) o  las variables esperadas (fecha,mes,ao)"));
                    errorsNumber++;
                }

                //RELACION PARA NUMERO DE IDENTIFICACION                
                if (currentRelationsGroup.findRelationVarByNameExpected("numero_identificacion_victima") == null) {
                    noErrors = false;
                    errorsControlMB.addError(new ErrorControl(newRelationVar, "REQUIRED VALIDATION", "No existe manera de determinar la identificacion de la víctima", "Diríjase a la sección relacion de variables y realice la asociacion correspondiente para la variable esperada (numero)"));
                    errorsNumber++;
                }
                break;

            case SCC_F_032:
                //RELACION PARA FECHA DE EVENTO
                if (currentRelationsGroup.findRelationVarByNameExpected("fecha_evento") == null) {
                    if (currentRelationsGroup.findRelationVarByNameExpected("dia_evento") == null) {
                        noErrors = false;
                    }
                    if (currentRelationsGroup.findRelationVarByNameExpected("mes_evento") == null) {
                        noErrors = false;
                    }
                    if (currentRelationsGroup.findRelationVarByNameExpected("año_evento") == null) {
                        noErrors = false;
                    }
                }
                if (noErrors == false) {//no se puede determinar la dia_evento
                    errorsControlMB.addError(new ErrorControl(newRelationVar, "REQUIRED VALIDATION", "No existe manera de determinar la fecha del evento", "Diríjase a la sección relacion de variables y realice la asociacion correspondiente para la variable esperada (fechaev) o las variables esperadas (dia,mes,ao)"));
                    errorsNumber++;
                }
                //RELACION PARA LA INTENCIONALIDAD
                if (currentRelationsGroup.findRelationVarByNameExpected("intencionalidad") == null) {
                    noErrors = false;
                    errorsControlMB.addError(new ErrorControl(newRelationVar, "REQUIRED VALIDATION", "No existe manera de determinar la intencionalidad", "Diríjase a la sección relacion de variables y realice la asociacion correspondiente para la variable esperada (intenci)"));
                    errorsNumber++;
                }

                //RELACION PARA NUMERO DE IDENTIFICACION                
                if (currentRelationsGroup.findRelationVarByNameExpected("numero_identificacion_victima") == null) {
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

        currentRelationsGroup = relationshipOfVariablesMB.getCurrentRelationsGroup();//tomo el grupos_vulnerables de relaciones de valores y de variables

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
                resultSetFileData = connectionJdbcMB.consult("SELECT COUNT(*) FROM " + nameTableTemp + "; ");
                resultSetFileData.next();
                tuplesNumber = resultSetFileData.getInt(1);//numero de tuplas a procesar
                tuplesProcessed = 0;//numero de tuplas procesdas            
                progressValidate = 0;

                resultSetFileData = connectionJdbcMB.consult("SELECT * FROM " + nameTableTemp + " ORDER BY id; ");//resultSetFileData contendra todos los registros de el archivo(tabla temp)            
                //columnsNumber = resultSetFileData.getMetaData().getColumnCount();
                errorsNumber = 0;
                int pos = 0;
                columnsNames = new String[resultSetFileData.getMetaData().getColumnCount()];//creo un arreglo con los nombres_victima de las columnas
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
                        relationVar = currentRelationsGroup.findRelationVarByNameFound(columnsNames[i]);//determino la relacion de variables
                        if (relationVar != null && resultSetFileData.getString(columnsNames[i]) != null) {
                            value = "";
                            switch (DataTypeEnum.convert(relationVar.getFieldType())) {//tipo de relacion
                                case text:
                                    break;
                                case integer:
                                    value = isNumeric(resultSetFileData.getString(columnsNames[i]));
                                    //System.out.println("Validando Entero: " + resultSetFileData.getString(columnsNames[i]) + "   Resultado: " + value);
                                    if (value == null) {
                                        errorsNumber++;//error = "No es entero";
                                        errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), resultSetFileData.getString("id"), "integer"));
                                    }
                                    break;
                                case age:
                                    value = isAge(resultSetFileData.getString(columnsNames[i]));
                                    //System.out.println("Validando Age: " + resultSetFileData.getString(columnsNames[i]) + "   Resultado: " + value);
                                    if (value == null) {
                                        errorsNumber++;//error = "dia_evento no corresponde al formato";
                                        errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), resultSetFileData.getString("id"), "age"));
                                    }
                                    break;
                                case date:
                                    value = isDate(resultSetFileData.getString(columnsNames[i]), relationVar.getDateFormat());
                                    if (relationVar.getNameExpected().compareTo("fecha_evento") == 0) {
                                        fechaev = value;
                                    }
                                    if (relationVar.getNameExpected().compareTo("fecha_consulta") == 0) {
                                        fechacon = value;
                                    }
                                    if (value == null) {
                                        errorsNumber++;//error = "dia_evento no corresponde al formato";
                                        errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), resultSetFileData.getString("id"), "date"));
                                    }

                                    break;
                                case military:
                                    value = isMilitary(resultSetFileData.getString(columnsNames[i]));
                                    //System.out.println("Validando Militar: " + resultSetFileData.getString(columnsNames[i]) + "   Resultado: " + value);
                                    if (value == null) {
                                        errorsNumber++;//la hora_evento militar no puede ser determinada
                                        errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), resultSetFileData.getString("id"), "military"));
                                    }
                                    break;
                                case hour:
                                    value = isHour(resultSetFileData.getString(columnsNames[i]));
                                    //System.out.println("Validando Hora: " + resultSetFileData.getString(columnsNames[i]) + "   Resultado: " + value);
                                    if (value == null) {
                                        errorsNumber++;//la hora_evento militar no puede ser determinada
                                        errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), resultSetFileData.getString("id"), "hour"));
                                    }
                                    break;
                                case minute:
                                    value = isMinute(resultSetFileData.getString(columnsNames[i]));
                                    //System.out.println("Validando Minuto: " + resultSetFileData.getString(columnsNames[i]) + "   Resultado: " + value);
                                    if (value == null) {
                                        errorsNumber++;//la hora_evento militar no puede ser determinada
                                        errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), resultSetFileData.getString("id"), "minute"));
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
                                        errorsNumber++;//la hora_evento militar no puede ser determinada
                                        errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), resultSetFileData.getString("id"), "day"));
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
                                        errorsNumber++;//la hora_evento militar no puede ser determinada
                                        errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), resultSetFileData.getString("id"), "month"));
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
                                        errorsNumber++;//la hora_evento militar no puede ser determinada
                                        errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), resultSetFileData.getString("id"), "year"));
                                    }
                                    break;
                                case percentage:
                                    value = isPercentage(resultSetFileData.getString(columnsNames[i]));
                                    //System.out.println("Validando porcentaje: " + resultSetFileData.getString(columnsNames[i]) + "   Resultado: " + String.valueOf(value));
                                    if (value == null) {
                                        errorsNumber++;//el porcentaje no puede ser determinado
                                        errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), resultSetFileData.getString("id"), "percentage"));
                                    }
                                    break;
                                case NOVALUE:
                                    value = isCategorical(resultSetFileData.getString(relationVar.getNameFound()), relationVar);
                                    System.out.println("Validando Categoria: " + resultSetFileData.getString(relationVar.getNameFound()) + "   Resultado: " + value);
                                    if (relationVar.getNameExpected().compareTo("intencionalidad") == 0) {
                                        intencionality = resultSetFileData.getString(relationVar.getNameFound());
                                    }
                                    if (value == null) {
                                        errorsNumber++;//error = "no esta en la categoria ni es un valor descartado";
                                        String description = fieldsFacade.findFieldTypeByFieldNameAndFormId(relationVar.getNameExpected(), currentRelationsGroup.getFormId().getFormId()).getFieldDescription();
                                        errorsControlMB.addError(new ErrorControl(
                                                relationVar,
                                                resultSetFileData.getString(relationVar.getNameFound()),
                                                resultSetFileData.getString("id"),
                                                description));
                                    }
                                    break;
                            }
                        }
                    }

                    //..........................................................
                    //verifico que pueda ser determinada la fecha_evento e intencionalidad
                    boolean existDateEvent = true;
                    fechaev = haveData(fechaev);
                    fechacon = haveData(fechacon);
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
                        case SCC_F_032:
                            //RELACION PARA LA INTENCIONALIDAD
                            if (intencionality == null) {
                                relationVar = currentRelationsGroup.findRelationVarByNameExpected("intencionalidad");//determino la relacion de variables
                                errorsControlMB.addError(new ErrorControl(relationVar, " ", resultSetFileData.getString("id"), "intencionalidad"));
                                errorsNumber++;
                            }
                        case SCC_F_028:
                        case SCC_F_029:
                        case SCC_F_030:
                        case SCC_F_031:
                        case SCC_F_033:
                            //DETERMINAR FECHA DE EVENTO                                
                            if (existDateEvent == false) {//no se puede determinar la fecha_evento
                                relationVar = currentRelationsGroup.findRelationVarByNameExpected("fecha_evento");//determino la relacion de variables
                                errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), resultSetFileData.getString("id"), "fecha_evento"));
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
            } catch (SQLException ex) {
                System.out.println("error: " + ex.toString());
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
            resultSetFileData = connectionJdbcMB.consult("SELECT COUNT(*) FROM " + nameTableTemp + "; ");
            resultSetFileData.next();
            tuplesNumber = resultSetFileData.getInt(1);//NUMERO DE TUPLAS
            resultSetFileData = connectionJdbcMB.consult("SELECT * FROM " + nameTableTemp + " ORDER BY id; ");//resultSetFileData contendra todos los registros de el archivo(tabla temp)            
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
                    relationVar = currentRelationsGroup.findRelationVarByNameFound(columnsNames[posCol]);//determino la relacion de variables
                    //if (columnsNames[posCol].compareTo("edad_paciente") == 0 && tuplesProcessed > 309) { columnsNames[posCol] = "edad_paciente"; }
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
//                            case tipo_agresor_padre:
//                                break;
//                            case clave:
//                                break;
//                            case ficha:
//                                break;
                            case departamento_evento:
                                break;
//                            case codigo_departamento_evento:
//                                break;
                            case municipio_evento:
                                splitArray = value.split("-");
                                if (splitArray.length == 2) {
                                    newVictim.setResidenceDepartment(Short.parseShort(splitArray[0]));
                                    newVictim.setResidenceMunicipality(Short.parseShort(splitArray[1]));
                                }
                                break;
//                            case codigo_municipio_evento:
//                                splitArray = value.split("-");
//                                if (splitArray.length == 2) {
//                                    newVictim.setResidenceDepartment(Short.parseShort(splitArray[0]));
//                                    newVictim.setResidenceMunicipality(Short.parseShort(splitArray[1]));
//                                }
//                                break;
                            case certificado_defuncion:
                                newFatalInjurie.setCode(value);
                                break;
                            case dia_evento://dia del evento
                                dia = value;
                                break;
                            case mes_evento://mes evento
                                mes = value;
                                break;
                            case año_evento://año del evento
                                ao = value;
                                break;
                            case fecha_evento:
                                try {
                                    currentDate = dateFormat.parse(value);
                                    newFatalInjurie.setInjuryDate(currentDate);
                                } catch (ParseException ex) {
                                }
                                break;
                            case hora_evento:
                                horas = value;
                                break;
                            case minuto_evento:
                                minutos = value;
                                break;
                            case am_pm:
                                ampm = value;
                                break;
                            case hora_militar_evento:
                                n = new Date();
                                hourInt = Integer.parseInt(value.substring(0, 2));
                                minuteInt = Integer.parseInt(value.substring(2, 4));
                                n.setHours(hourInt);
                                n.setMinutes(minuteInt);
                                n.setSeconds(0);
                                newFatalInjurie.setInjuryTime(n);
                                break;
//                            case hora_sin_establecer://hora si determinar
//                                si no hay hora_evento es si determinar                                
//                                break;
                            case direccion_evento:
                                newFatalInjurie.setInjuryAddress(value);
                                break;
                            case barrio_evento:
                                newFatalInjurie.setInjuryNeighborhoodId(Integer.parseInt(value));
                                break;
//                            case codigo_barrio_evento:
//                                newFatalInjurie.setInjuryNeighborhoodId(Integer.parseInt(value));
//                                break;
                            case area_evento://ZONA URBANA O RURAL //SE DETERMINA CON EL BARRIO
                                newFatalInjurie.setAreaId(areasFacade.find(Short.parseShort(value)));
                                break;
                            case clase_lugar_evento:
                                newFatalInjurie.setInjuryPlaceId(placesFacade.find(Short.parseShort(value)));
                                break;
                            case dia_semana_evento:
                                newFatalInjurie.setInjuryDayOfWeek(daysFacade.find(Short.parseShort(value)).getDaysName());
                                break;
                            case numero_victimas_fatales:
                                newFatalInjurie.setVictimNumber(Short.parseShort(value));
                                break;
                            case nombres_victima:
                                name = value;
                                break;
                            case apellidos_victima:
                                surname = value;
                                break;
                            case sexo_victima:
                                newVictim.setGenderId(gendersFacade.find(Short.parseShort(value)));
                                break;
                            case tipo_edad_victima:
                                newVictim.setGenderId(gendersFacade.find(Short.parseShort(value)));
                                break;
                            case edad_victima:
                                newVictim.setVictimAge(Short.parseShort(value));
                                break;
                            case ocupacion_victima:
                                newVictim.setJobId(jobsFacade.find(Short.parseShort(value)));
                                break;
                            case municipio_residencia:
                                splitArray = value.split("-");
                                if (splitArray.length == 2) {
                                    newVictim.setResidenceDepartment(Short.parseShort(splitArray[0]));
                                    newVictim.setResidenceMunicipality(Short.parseShort(splitArray[1]));
                                }
                                break;
                            case barrio_residencia_victima:
                                newVictim.setVictimNeighborhoodId(neighborhoodsFacade.find(Integer.parseInt(value)));
                                break;
                            case tipo_identificacion_victima:
                                newVictim.setTypeId(idTypesFacade.find(Short.parseShort(value)));
                                break;
                            case numero_identificacion_victima:
                                newVictim.setVictimNid(value);
                                break;
                            case procedencia_victima:
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
                            case arma_o_causa_muerte:
                                newFatalInjuryMurder.setWeaponTypeId(weaponTypesFacade.find(Short.parseShort(value)));
                                break;
                            case contexto_evento:
                                newFatalInjuryMurder.setMurderContextId(murderContextsFacade.find(Short.parseShort(value)));
                                break;
                            case narracion_evento:
                            case narracion_evento_1:
                            case narracion_evento_2:
                                narrative = narrative + " " + value;
                                break;
                            case nivel_alcohol_victima:
                                newFatalInjurie.setAlcoholLevelVictim(Short.parseShort(value));
                                break;
                            case nivel_alcohol_sin_dato:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 2));
                                }
                                break;
                            case nivel_alcohol_pendiente:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 4));
                                }
                                break;
                            case nivel_alcohol_desconocido:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 3));
                                }
                                break;
                            case nivel_alcohol_negativo:
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

                //SI NO HAY FECHA DE EVENTO TRATAR DE CALCULAR MEDIANTE LAS VARIABLES dia_evento, mes_evento, año_evento
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

                //SI NO HAY HORA DE EVENTO TRATAR DE CALCULAR MEDIANTE LAS VARIABLES hora_evento,minuto_evento,am_pm
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
                                newVictim.setAgeTypeId((short) 1);//aqui por defecto seria sin dato, si no se conoce
//                                if (newVictim.getTypeId() == null) {
//                                    if (ageYears >= 18) {
//                                        newVictim.setTypeId(idTypesFacade.find((short) 1));
//                                    } else {
//                                        newVictim.setTypeId(idTypesFacade.find((short) 5));
//                                    }
//                                }
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(HomicideMB.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                if (newVictim.getVictimNid() == null) {//SI NO HAY NUMERO DE IDENTIFICACION 
                    newVictim.setVictimNid(String.valueOf(genNnFacade.findMax() + 1));//asigno un consecutivo a la identificacion
                    newVictim.setVictimClass((short) 2);//nn

                    if (newVictim.getTypeId() == null) {//no hay tipo de identificacion
                        if (newVictim.getVictimAge() != null) {//HAY EDAD Y HAY tipo de edad                            
                            if (newVictim.getVictimAge() > 17) {
                                newVictim.setTypeId(idTypesFacade.find((short) 6));//adulto sin identificacion                                
                            } else {
                                newVictim.setTypeId(idTypesFacade.find((short) 7));//menor sin identificacion
                            }
                        } else {//NO HAY EDAD
                            newVictim.setTypeId(idTypesFacade.find((short) 9));//tipo de identificacoin sin determinar
                        }
                    }
                    GenNn currentGenNn = genNnFacade.find(genNnFacade.findMax());
                    currentGenNn.setCodNn(genNnFacade.findMax() + 1);
                    genNnFacade.edit(currentGenNn);
                } else {//HAY NUMERO DE IDENTIFICACION
                    if (newVictim.getTypeId() == null) {//NO HAY TIPO DE IDENTIFICACION
                        newVictim.setTypeId(idTypesFacade.find((short) 9));//tipo de identificacoin sin determinar
                    }
                }

                //CORRESPONDENCIA ENTRE EDAD Y TIPO DE IDENTIFICACION
                if (newVictim.getTypeId() != null) {//no hay tipo de identificacion
                    if (newVictim.getVictimAge() != null) {//HAY EDAD Y HAY tipo de edad
                        if (newVictim.getVictimAge() < 18) {//menor de edad
                            if (newVictim.getTypeId().getTypeId() == (short) 1 ||//cedula de ciudadania
                                    newVictim.getTypeId().getTypeId() == (short) 2 ||//cedula de extranjeria
                                    newVictim.getTypeId().getTypeId() == (short) 3 ||//pasaporte
                                    newVictim.getTypeId().getTypeId() == (short) 6) {//adulto sin identificacion
                                newVictim.setTypeId(idTypesFacade.find((short) 9));//sin determinar
                            }
                        } else {//mayor de edad
                            if (newVictim.getTypeId().getTypeId() == (short) 5||//tarjeta de identidad
                                     newVictim.getTypeId().getTypeId() == (short) 4||//registro civil
                                     newVictim.getTypeId().getTypeId() == (short) 7) {//menor sin identificacion
                                newVictim.setTypeId(idTypesFacade.find((short) 9));//sin determinar
                            }
                        }

                    }
                }
                //PERSISTO
                try {
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
            resultSetFileData = connectionJdbcMB.consult("SELECT COUNT(*) FROM " + nameTableTemp + "; ");
            resultSetFileData.next();
            tuplesNumber = resultSetFileData.getInt(1);//NUMERO DE TUPLAS
            resultSetFileData = connectionJdbcMB.consult("SELECT * FROM " + nameTableTemp + " ORDER BY id; ");//resultSetFileData contendra todos los registros de el archivo(tabla temp)            
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
                newFatalInjurie.setInjuryId(injuriesFacade.find((short) 11));//es 10 transito
                newFatalInjurie.setUserId(usersFacade.find(1));//usuario que se encuentre logueado
                newFatalInjurie.setVictimId(newVictim);
                newFatalInjuryTraffic = new FatalInjuryTraffic();
                newFatalInjuryTraffic.setFatalInjuryId(newFatalInjurie.getFatalInjuryId());
                newVictim.setTagId(tagsFacade.find(newTag.getTagId()));


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
                    relationVar = currentRelationsGroup.findRelationVarByNameFound(columnsNames[posCol]);//determino la relacion de variables
                    //if (columnsNames[posCol].compareTo("edad_paciente") == 0 && tuplesProcessed > 309) { columnsNames[posCol] = "edad_paciente"; }
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
//                            case tipo_agresor_padre:
//                                break;
//                            case clave:
//                                break;
//                            case ficha:
//                                break;
                            case departamento_evento:
                                break;
//                            case codigo_departamento_evento:
//                                break;
                            case municipio_evento:
                                splitArray = value.split("-");
                                if (splitArray.length == 2) {
                                    newVictim.setResidenceDepartment(Short.parseShort(splitArray[0]));
                                    newVictim.setResidenceMunicipality(Short.parseShort(splitArray[1]));
                                }
                                break;
//                            case codigomuni:
//                                splitArray = value.split("-");
//                                if (splitArray.length == 2) {
//                                    newVictim.setResidenceDepartment(Short.parseShort(splitArray[0]));
//                                    newVictim.setResidenceMunicipality(Short.parseShort(splitArray[1]));
//                                }
//                                break;
                            case certificado_defuncion:
                                newFatalInjurie.setCode(value);
                                break;
                            case dia_evento://dia del evento
                                dia = value;
                                break;
                            case mes_evento://mes evento
                                mes = value;
                                break;
                            case año_evento://año del evento
                                ao = value;
                                break;
                            case fecha_evento:
                                try {
                                    currentDate = dateFormat.parse(value);
                                    newFatalInjurie.setInjuryDate(currentDate);
                                } catch (ParseException ex) {
                                }
                                break;
                            case hora_evento:
                                horas = value;
                                break;
                            case minuto_evento:
                                minutos = value;
                                break;
                            case am_pm:
                                ampm = value;
                                break;
                            case hora_militar_evento:
                                n = new Date();
                                hourInt = Integer.parseInt(value.substring(0, 2));
                                minuteInt = Integer.parseInt(value.substring(2, 4));
                                n.setHours(hourInt);
                                n.setMinutes(minuteInt);
                                n.setSeconds(0);
                                newFatalInjurie.setInjuryTime(n);
                                break;
//                            case hora_sin_establecer://hora si determinar
//                                si no hay hora_evento es si determinar                                
//                                break;
                            case direccion_evento:
                                newFatalInjurie.setInjuryAddress(value);
                                break;
                            case barrio_evento:
                                newFatalInjurie.setInjuryNeighborhoodId(Integer.parseInt(value));
                                break;
//                            case codbar:
//                                newFatalInjurie.setInjuryNeighborhoodId(Integer.parseInt(value));
//                                break;
                            case area_evento://ZONA URBANA O RURAL //SE DETERMINA CON EL BARRIO
                                newFatalInjurie.setAreaId(areasFacade.find(Short.parseShort(value)));
                                break;
                            case clase_accidente:
                                newFatalInjuryTraffic.setAccidentClassId(accidentClassesFacade.find(Short.parseShort(value)));
                                break;
                            case dia_semana_evento:
                                newFatalInjurie.setInjuryDayOfWeek(daysFacade.find(Short.parseShort(value)).getDaysName());
                                break;
                            case numero_victimas_fatales:
                                newFatalInjurie.setVictimNumber(Short.parseShort(value));
                                break;
                            case numero_lesionados_evento:
                                newFatalInjuryTraffic.setNumberNonFatalVictims(Short.parseShort(value));
                                break;
                            case nombres_victima:
                                name = value;
                                break;
                            case apellidos_victima:
                                surname = value;
                                break;
                            case sexo_victima:
                                newVictim.setGenderId(gendersFacade.find(Short.parseShort(value)));
                                break;
                            case tipo_edad_victima:
                                newVictim.setGenderId(gendersFacade.find(Short.parseShort(value)));
                                break;
                            case edad_victima:
                                newVictim.setVictimAge(Short.parseShort(value));
                                break;
                            case ocupacion_victima:
                                newVictim.setJobId(jobsFacade.find(Short.parseShort(value)));
                                break;
                            case municipio_residencia:
                                splitArray = value.split("-");
                                if (splitArray.length == 2) {
                                    newVictim.setResidenceDepartment(Short.parseShort(splitArray[0]));
                                    newVictim.setResidenceMunicipality(Short.parseShort(splitArray[1]));
                                }
                                break;
                            case barrio_residencia_victima:
                                newVictim.setVictimNeighborhoodId(neighborhoodsFacade.find(Integer.parseInt(value)));
                                break;
                            case tipo_identificacion_victima:
                                newVictim.setTypeId(idTypesFacade.find(Short.parseShort(value)));
                                break;
                            case numero_identificacion_victima:
                                newVictim.setVictimNid(value);
                                break;
                            case procedencia_victima:
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
                            case caracteristicas_victima:
                                newFatalInjuryTraffic.setVictimCharacteristicId(victimCharacteristicsFacade.find(Short.parseShort(value)));
                                break;
                            case medidas_proteccion:
                                newFatalInjuryTraffic.setProtectionMeasureId(protectiveMeasuresFacade.find(Short.parseShort(value)));
                                break;
                            case vehiculo_involucrado_victima:
                                newFatalInjuryTraffic.setInvolvedVehicleId(involvedVehiclesFacade.find(Short.parseShort(value)));
                                break;
                            case vehiculo_involucrado_contraparte_1:
                            case vehiculo_involucrado_contraparte_2:
                            case vehiculo_involucrado_contraparte_3:
                                newCounterpartInvolvedVehicle = new CounterpartInvolvedVehicle();
                                newCounterpartInvolvedVehicle.setInvolvedVehicleId(involvedVehiclesFacade.find(Short.parseShort(value)));
                                newCounterpartInvolvedVehicle.setFatalInjuryId(newFatalInjurie);
                                newCounterpartInvolvedVehicle.setCounterpartInvolvedVehicleId(counterpartInvolvedVehicleFacade.findMax() + 1);
                                involvedVehiclesList.add(newCounterpartInvolvedVehicle);
                                break;
                            case tipo_servicio_vehiculo_victima:
                                newFatalInjuryTraffic.setServiceTypeId(serviceTypesFacade.find(Short.parseShort(value)));
                                break;
                            case tipo_servicio_contraparte_1:
                            case tipo_servicio_contraparte_2:
                            case tipo_servicio_contraparte_3:
                                newCounterpartServiceType = new CounterpartServiceType();
                                newCounterpartServiceType.setServiceTypeId(serviceTypesFacade.find(Short.parseShort(value)));
                                newCounterpartServiceType.setFatalInjuryId(newFatalInjurie);
                                newCounterpartServiceType.setCounterpartServiceTypeId(counterpartServiceTypeFacade.findMax() + 1);
                                serviceTypesList.add(newCounterpartServiceType);
                                break;
                            case narracion_evento:
                            case narracion_evento_1:
                            case narracion_evento_2:
                                narrative = narrative + " " + value;
                                break;
                            case nivel_alcohol_victima:
                                newFatalInjurie.setAlcoholLevelVictim(Short.parseShort(value));
                                break;
                            case detalle_nivel_alcohol_victima:
                                newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find(Short.parseShort(value)));//con dato
                                break;
                            case nivel_alcohol_culpable:
                                newFatalInjuryTraffic.setAlcoholLevelCounterpart(Short.parseShort(value));
                                break;
                            case detalle_nivel_alcohol_culpable:
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

                //SI NO HAY FECHA DE EVENTO TRATAR DE CALCULAR MEDIANTE LAS VARIABLES dia_evento, mes_evento, año_evento
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

                //SI NO HAY HORA DE EVENTO TRATAR DE CALCULAR MEDIANTE LAS VARIABLES hora_evento,minuto_evento,am_pm
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
                                newVictim.setAgeTypeId((short) 1);//aqui por defecto seria sin dato, si no se conoce
//                                if (newVictim.getTypeId() == null) {
//                                    if (ageYears >= 18) {
//                                        newVictim.setTypeId(idTypesFacade.find((short) 1));
//                                    } else {
//                                        newVictim.setTypeId(idTypesFacade.find((short) 5));
//                                    }
//                                }
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(HomicideMB.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }

                if (newVictim.getVictimNid() == null) {//SI NO HAY NUMERO DE IDENTIFICACION 
                    newVictim.setVictimNid(String.valueOf(genNnFacade.findMax() + 1));//asigno un consecutivo a la identificacion
                    newVictim.setVictimClass((short) 2);//nn

                    if (newVictim.getTypeId() == null) {//no hay tipo de identificacion
                        if (newVictim.getVictimAge() != null) {//HAY EDAD Y HAY tipo de edad                            
                            if (newVictim.getVictimAge() > 17) {
                                newVictim.setTypeId(idTypesFacade.find((short) 6));//adulto sin identificacion                                
                            } else {
                                newVictim.setTypeId(idTypesFacade.find((short) 7));//menor sin identificacion
                            }
                        } else {//NO HAY EDAD
                            newVictim.setTypeId(idTypesFacade.find((short) 9));//tipo de identificacoin sin determinar
                        }
                    }
                    GenNn currentGenNn = genNnFacade.find(genNnFacade.findMax());
                    currentGenNn.setCodNn(genNnFacade.findMax() + 1);
                    genNnFacade.edit(currentGenNn);
                } else {//HAY NUMERO DE IDENTIFICACION
                    if (newVictim.getTypeId() == null) {//NO HAY TIPO DE IDENTIFICACION
                        newVictim.setTypeId(idTypesFacade.find((short) 9));//tipo de identificacoin sin determinar
                    }
                }
                
                //CORRESPONDENCIA ENTRE EDAD Y TIPO DE IDENTIFICACION
                if (newVictim.getTypeId() != null) {//no hay tipo de identificacion
                    if (newVictim.getVictimAge() != null) {//HAY EDAD Y HAY tipo de edad
                        if (newVictim.getVictimAge() < 18) {//menor de edad
                            if (newVictim.getTypeId().getTypeId() == (short) 1 ||//cedula de ciudadania
                                    newVictim.getTypeId().getTypeId() == (short) 2 ||//cedula de extranjeria
                                    newVictim.getTypeId().getTypeId() == (short) 3 ||//pasaporte
                                    newVictim.getTypeId().getTypeId() == (short) 6) {//adulto sin identificacion
                                newVictim.setTypeId(idTypesFacade.find((short) 9));//sin determinar
                            }
                        } else {//mayor de edad
                            if (newVictim.getTypeId().getTypeId() == (short) 5||//tarjeta de identidad
                                     newVictim.getTypeId().getTypeId() == (short) 4||//registro civil
                                     newVictim.getTypeId().getTypeId() == (short) 7) {//menor sin identificacion
                                newVictim.setTypeId(idTypesFacade.find((short) 9));//sin determinar
                            }
                        }

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
                    //newVictim.setTagId(tagsFacade.find(newTag));
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
            resultSetFileData = connectionJdbcMB.consult("SELECT COUNT(*) FROM " + nameTableTemp + "; ");
            resultSetFileData.next();
            tuplesNumber = resultSetFileData.getInt(1);//NUMERO DE TUPLAS
            resultSetFileData = connectionJdbcMB.consult("SELECT * FROM " + nameTableTemp + " ORDER BY id; ");//resultSetFileData contendra todos los registros de el archivo(tabla temp)            
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
                    relationVar = currentRelationsGroup.findRelationVarByNameFound(columnsNames[posCol]);//determino la relacion de variables
                    //if (columnsNames[posCol].compareTo("edad_paciente") == 0 && tuplesProcessed > 309) { columnsNames[posCol] = "edad_paciente"; }
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
//                            case tipo_agresor_padre:
//                                break;
//                            case clave:
//                                break;
//                            case ficha:
//                                break;
                            case departamento_evento:
                                break;
//                            case codigodepa:
//                                break;
                            case municipio_evento:
                                splitArray = value.split("-");
                                if (splitArray.length == 2) {
                                    newVictim.setResidenceDepartment(Short.parseShort(splitArray[0]));
                                    newVictim.setResidenceMunicipality(Short.parseShort(splitArray[1]));
                                }
                                break;
//                            case codigomuni:
//                                splitArray = value.split("-");
//                                if (splitArray.length == 2) {
//                                    newVictim.setResidenceDepartment(Short.parseShort(splitArray[0]));
//                                    newVictim.setResidenceMunicipality(Short.parseShort(splitArray[1]));
//                                }
//                                break;
                            case certificado_defuncion:
                                newFatalInjurie.setCode(value);
                                break;
                            case dia_evento://dia del evento
                                dia = value;
                                break;
                            case mes_evento://mes evento
                                mes = value;
                                break;
                            case año_evento://año del evento
                                ao = value;
                                break;
                            case fecha_evento:
                                try {
                                    currentDate = dateFormat.parse(value);
                                    newFatalInjurie.setInjuryDate(currentDate);
                                } catch (ParseException ex) {
                                }
                                break;
                            case hora_evento:
                                horas = value;
                                break;
                            case minuto_evento:
                                minutos = value;
                                break;
                            case am_pm:
                                ampm = value;
                                break;
                            case hora_militar_evento:
                                n = new Date();
                                hourInt = Integer.parseInt(value.substring(0, 2));
                                minuteInt = Integer.parseInt(value.substring(2, 4));
                                n.setHours(hourInt);
                                n.setMinutes(minuteInt);
                                n.setSeconds(0);
                                newFatalInjurie.setInjuryTime(n);
                                break;
//                            case hora_sin_establecer://hora si determinar
//                                si no hay hora_evento es si determinar                                
//                                break;
                            case direccion_evento:
                                newFatalInjurie.setInjuryAddress(value);
                                break;
                            case barrio_evento:
                                newFatalInjurie.setInjuryNeighborhoodId(Integer.parseInt(value));
                                break;
//                            case codbar:
//                                newFatalInjurie.setInjuryNeighborhoodId(Integer.parseInt(value));
//                                break;
                            case area_evento://ZONA URBANA O RURAL //SE DETERMINA CON EL BARRIO
                                newFatalInjurie.setAreaId(areasFacade.find(Short.parseShort(value)));
                                break;
                            case clase_lugar_evento:
                                newFatalInjurie.setInjuryPlaceId(placesFacade.find(Short.parseShort(value)));
                                break;
                            case dia_semana_evento:
                                newFatalInjurie.setInjuryDayOfWeek(daysFacade.find(Short.parseShort(value)).getDaysName());
                                break;
                            case numero_victimas_fatales:
                                newFatalInjurie.setVictimNumber(Short.parseShort(value));
                                break;
                            case nombres_victima:
                                name = value;
                                break;
                            case apellidos_victima:
                                surname = value;
                                break;
                            case sexo_victima:
                                newVictim.setGenderId(gendersFacade.find(Short.parseShort(value)));
                                break;
                            case tipo_edad_victima:
                                newVictim.setGenderId(gendersFacade.find(Short.parseShort(value)));
                                break;
                            case edad_victima:
                                newVictim.setVictimAge(Short.parseShort(value));
                                break;
                            case ocuacion_victima:
                                newVictim.setJobId(jobsFacade.find(Short.parseShort(value)));
                                break;
                            case municipio_residencia:
                                splitArray = value.split("-");
                                if (splitArray.length == 2) {
                                    newVictim.setResidenceDepartment(Short.parseShort(splitArray[0]));
                                    newVictim.setResidenceMunicipality(Short.parseShort(splitArray[1]));
                                }
                                break;
                            case barrio_residencia_victima:
                                newVictim.setVictimNeighborhoodId(neighborhoodsFacade.find(Integer.parseInt(value)));
                                break;
                            case tipo_identificacion_victima:
                                newVictim.setTypeId(idTypesFacade.find(Short.parseShort(value)));
                                break;
                            case numero_identificacion_victima:
                                newVictim.setVictimNid(value);
                                break;
                            case procedencia_victima:
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
                            case arma_o_causa_muerte:
                                newFatalInjurySuicide.setSuicideDeathMechanismId(suicideMechanismsFacade.find(Short.parseShort(value)));
                                break;
                            case eventos_relacionados_evento:
                                newFatalInjurySuicide.setRelatedEventId(relatedEventsFacade.find(Short.parseShort(value)));
                                break;
                            case intentos_previos:
                                newFatalInjurySuicide.setPreviousAttempt(boolean3Facade.find(Short.parseShort(value)));
                                break;
                            case antecedentes_salud_mental:
                                newFatalInjurySuicide.setMentalAntecedent(boolean3Facade.find(Short.parseShort(value)));
                                break;
                            case narracion_evento:
                            case narracion_evento_1:
                            case narracion_evento_2:
                                narrative = narrative + " " + value;
                                break;
                            case nivel_alcohol_victima:
                                newFatalInjurie.setAlcoholLevelVictim(Short.parseShort(value));
                                break;
                            case nivel_alcohol_sin_dato:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 2));
                                }
                                break;
                            case nivel_alcohol_pendiente:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 4));
                                }
                                break;
                            case nivel_alcohol_desconocido:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 3));
                                }
                                break;
                            case nivel_alcohol_negativo:
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

                //SI NO HAY FECHA DE EVENTO TRATAR DE CALCULAR MEDIANTE LAS VARIABLES dia_evento, mes_evento, año_evento
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

                //SI NO HAY HORA DE EVENTO TRATAR DE CALCULAR MEDIANTE LAS VARIABLES hora_evento,minuto_evento,am_pm
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
                                newVictim.setAgeTypeId((short) 1);//aqui por defecto seria sin dato, si no se conoce
//                                if (newVictim.getTypeId() == null) {
//                                    if (ageYears >= 18) {
//                                        newVictim.setTypeId(idTypesFacade.find((short) 1));
//                                    } else {
//                                        newVictim.setTypeId(idTypesFacade.find((short) 5));
//                                    }
//                                }
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(HomicideMB.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }

                if (newVictim.getVictimNid() == null) {//SI NO HAY NUMERO DE IDENTIFICACION 
                    newVictim.setVictimNid(String.valueOf(genNnFacade.findMax() + 1));//asigno un consecutivo a la identificacion
                    newVictim.setVictimClass((short) 2);//nn

                    if (newVictim.getTypeId() == null) {//no hay tipo de identificacion
                        if (newVictim.getVictimAge() != null) {//HAY EDAD Y HAY tipo de edad                            
                            if (newVictim.getVictimAge() > 17) {
                                newVictim.setTypeId(idTypesFacade.find((short) 6));//adulto sin identificacion                                
                            } else {
                                newVictim.setTypeId(idTypesFacade.find((short) 7));//menor sin identificacion
                            }
                        } else {//NO HAY EDAD
                            newVictim.setTypeId(idTypesFacade.find((short) 9));//tipo de identificacoin sin determinar
                        }
                    }
                    GenNn currentGenNn = genNnFacade.find(genNnFacade.findMax());
                    currentGenNn.setCodNn(genNnFacade.findMax() + 1);
                    genNnFacade.edit(currentGenNn);
                } else {//HAY NUMERO DE IDENTIFICACION
                    if (newVictim.getTypeId() == null) {//NO HAY TIPO DE IDENTIFICACION
                        newVictim.setTypeId(idTypesFacade.find((short) 9));//tipo de identificacoin sin determinar
                    }
                }
                
                //CORRESPONDENCIA ENTRE EDAD Y TIPO DE IDENTIFICACION
                if (newVictim.getTypeId() != null) {//no hay tipo de identificacion
                    if (newVictim.getVictimAge() != null) {//HAY EDAD Y HAY tipo de edad
                        if (newVictim.getVictimAge() < 18) {//menor de edad
                            if (newVictim.getTypeId().getTypeId() == (short) 1 ||//cedula de ciudadania
                                    newVictim.getTypeId().getTypeId() == (short) 2 ||//cedula de extranjeria
                                    newVictim.getTypeId().getTypeId() == (short) 3 ||//pasaporte
                                    newVictim.getTypeId().getTypeId() == (short) 6) {//adulto sin identificacion
                                newVictim.setTypeId(idTypesFacade.find((short) 9));//sin determinar
                            }
                        } else {//mayor de edad
                            if (newVictim.getTypeId().getTypeId() == (short) 5||//tarjeta de identidad
                                     newVictim.getTypeId().getTypeId() == (short) 4||//registro civil
                                     newVictim.getTypeId().getTypeId() == (short) 7) {//menor sin identificacion
                                newVictim.setTypeId(idTypesFacade.find((short) 9));//sin determinar
                            }
                        }

                    }
                }

                //PERSISTO
                try {
                    //newVictim.setTagId(tagsFacade.find(newTag));
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
            resultSetFileData = connectionJdbcMB.consult("SELECT COUNT(*) FROM " + nameTableTemp + "; ");
            resultSetFileData.next();
            tuplesNumber = resultSetFileData.getInt(1);//NUMERO DE TUPLAS
            resultSetFileData = connectionJdbcMB.consult("SELECT * FROM " + nameTableTemp + " ORDER BY id; ");//resultSetFileData contendra todos los registros de el archivo(tabla temp)            
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
                    relationVar = currentRelationsGroup.findRelationVarByNameFound(columnsNames[posCol]);//determino la relacion de variables
                    //if (columnsNames[posCol].compareTo("edad_paciente") == 0 && tuplesProcessed > 309) { columnsNames[posCol] = "edad_paciente"; }
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
//                            case tipo_agresor_padre:
//                                break;
//                            case clave:
//                                break;
//                            case ficha:
//                                break;
                            case departamento_evento:
                                break;
//                            case codigodepa:
//                                break;
                            case municipio_evento:
                                splitArray = value.split("-");
                                if (splitArray.length == 2) {
                                    newVictim.setResidenceDepartment(Short.parseShort(splitArray[0]));
                                    newVictim.setResidenceMunicipality(Short.parseShort(splitArray[1]));
                                }
                                break;
//                            case codigomuni:
//                                splitArray = value.split("-");
//                                if (splitArray.length == 2) {
//                                    newVictim.setResidenceDepartment(Short.parseShort(splitArray[0]));
//                                    newVictim.setResidenceMunicipality(Short.parseShort(splitArray[1]));
//                                }
//                                break;
                            case certificado_defuncion:
                                newFatalInjurie.setCode(value);
                                break;
                            case dia_evento://dia del evento
                                dia = value;
                                break;
                            case mes_evento://mes evento
                                mes = value;
                                break;
                            case año_evento://año del evento
                                ao = value;
                                break;
                            case fecha_evento:
                                try {
                                    currentDate = dateFormat.parse(value);
                                    newFatalInjurie.setInjuryDate(currentDate);
                                } catch (ParseException ex) {
                                }
                                break;
                            case hora_evento:
                                horas = value;
                                break;
                            case minuto_evento:
                                minutos = value;
                                break;
                            case am_pm:
                                ampm = value;
                                break;
                            case hora_militar_evento:
                                n = new Date();
                                hourInt = Integer.parseInt(value.substring(0, 2));
                                minuteInt = Integer.parseInt(value.substring(2, 4));
                                n.setHours(hourInt);
                                n.setMinutes(minuteInt);
                                n.setSeconds(0);
                                newFatalInjurie.setInjuryTime(n);
                                break;
//                            case hora_sin_establecer://hora si determinar
//                                si no hay hora_evento es si determinar                                
//                                break;
                            case direccion_evento:
                                newFatalInjurie.setInjuryAddress(value);
                                break;
                            case barrio_evento:
                                newFatalInjurie.setInjuryNeighborhoodId(Integer.parseInt(value));
                                break;
//                            case codbar:
//                                newFatalInjurie.setInjuryNeighborhoodId(Integer.parseInt(value));
//                                break;
                            case area_evento://ZONA URBANA O RURAL //SE DETERMINA CON EL BARRIO
                                newFatalInjurie.setAreaId(areasFacade.find(Short.parseShort(value)));
                                break;
                            case clase_lugar_evento:
                                newFatalInjurie.setInjuryPlaceId(placesFacade.find(Short.parseShort(value)));
                                break;
                            case dia_semana_evento:
                                newFatalInjurie.setInjuryDayOfWeek(daysFacade.find(Short.parseShort(value)).getDaysName());
                                break;
                            case numero_victimas_fatales:
                                newFatalInjurie.setVictimNumber(Short.parseShort(value));
                                break;
                            case numero_lesionados_evento:
                                newFatalInjuryTraffic.setNumberNonFatalVictims(Short.parseShort(value));
                                break;
                            case nombres_victima:
                                name = value;
                                break;
                            case apellidos_victima:
                                surname = value;
                                break;
                            case sexo_victima:
                                newVictim.setGenderId(gendersFacade.find(Short.parseShort(value)));
                                break;
                            case tipo_edad_victima:
                                newVictim.setGenderId(gendersFacade.find(Short.parseShort(value)));
                                break;
                            case edad_victima:
                                newVictim.setVictimAge(Short.parseShort(value));
                                break;
                            case ocupacion_victima:
                                newVictim.setJobId(jobsFacade.find(Short.parseShort(value)));
                                break;
                            case municipio_residencia:
                                splitArray = value.split("-");
                                if (splitArray.length == 2) {
                                    newVictim.setResidenceDepartment(Short.parseShort(splitArray[0]));
                                    newVictim.setResidenceMunicipality(Short.parseShort(splitArray[1]));
                                }
                                break;
                            case barrio_residencia_victima:
                                newVictim.setVictimNeighborhoodId(neighborhoodsFacade.find(Integer.parseInt(value)));
                                break;
                            case tipo_identificacion_victima:
                                newVictim.setTypeId(idTypesFacade.find(Short.parseShort(value)));
                                break;
                            case numero_identificacion_victima:
                                newVictim.setVictimNid(value);
                                break;
                            case procedencia_victima:
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
                            case arma_o_causa_muerte:
                                newFatalInjuryAccident.setDeathMechanismId(accidentMechanismsFacade.find(Short.parseShort(value)));
                                break;
                            case narracion_evento:
                            case narracion_evento_1:
                            case narracion_evento_2:
                                narrative = narrative + " " + value;
                                break;
                            case nivel_alcohol_victima:
                                newFatalInjurie.setAlcoholLevelVictim(Short.parseShort(value));
                                break;
                            case nivel_alcohol_sin_dato:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 2));
                                }
                                break;
                            case nivel_alcohol_pendiente:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 4));
                                }
                                break;
                            case nivel_alcohol_desconocido:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    newFatalInjurie.setAlcoholLevelVictimId(alcoholLevelsFacade.find((short) 3));
                                }
                                break;
                            case nivel_alcohol_negativo:
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

                //SI NO HAY FECHA DE EVENTO TRATAR DE CALCULAR MEDIANTE LAS VARIABLES dia_evento, mes_evento, año_evento
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

                //SI NO HAY HORA DE EVENTO TRATAR DE CALCULAR MEDIANTE LAS VARIABLES hora_evento,minuto_evento,am_pm
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
                                newVictim.setAgeTypeId((short) 1);//aqui por defecto seria sin dato, si no se conoce
//                                if (newVictim.getTypeId() == null) {
//                                    if (ageYears >= 18) {
//                                        newVictim.setTypeId(idTypesFacade.find((short) 1));
//                                    } else {
//                                        newVictim.setTypeId(idTypesFacade.find((short) 5));
//                                    }
//                                }
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(HomicideMB.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }

                if (newVictim.getVictimNid() == null) {//SI NO HAY NUMERO DE IDENTIFICACION 
                    newVictim.setVictimNid(String.valueOf(genNnFacade.findMax() + 1));//asigno un consecutivo a la identificacion
                    newVictim.setVictimClass((short) 2);//nn

                    if (newVictim.getTypeId() == null) {//no hay tipo de identificacion
                        if (newVictim.getVictimAge() != null) {//HAY EDAD Y HAY tipo de edad                            
                            if (newVictim.getVictimAge() > 17) {
                                newVictim.setTypeId(idTypesFacade.find((short) 6));//adulto sin identificacion                                
                            } else {
                                newVictim.setTypeId(idTypesFacade.find((short) 7));//menor sin identificacion
                            }
                        } else {//NO HAY EDAD
                            newVictim.setTypeId(idTypesFacade.find((short) 9));//tipo de identificacoin sin determinar
                        }
                    }
                    GenNn currentGenNn = genNnFacade.find(genNnFacade.findMax());
                    currentGenNn.setCodNn(genNnFacade.findMax() + 1);
                    genNnFacade.edit(currentGenNn);
                } else {//HAY NUMERO DE IDENTIFICACION
                    if (newVictim.getTypeId() == null) {//NO HAY TIPO DE IDENTIFICACION
                        newVictim.setTypeId(idTypesFacade.find((short) 9));//tipo de identificacoin sin determinar
                    }
                }
                
                //CORRESPONDENCIA ENTRE EDAD Y TIPO DE IDENTIFICACION
                if (newVictim.getTypeId() != null) {//no hay tipo de identificacion
                    if (newVictim.getVictimAge() != null) {//HAY EDAD Y HAY tipo de edad
                        if (newVictim.getVictimAge() < 18) {//menor de edad
                            if (newVictim.getTypeId().getTypeId() == (short) 1 ||//cedula de ciudadania
                                    newVictim.getTypeId().getTypeId() == (short) 2 ||//cedula de extranjeria
                                    newVictim.getTypeId().getTypeId() == (short) 3 ||//pasaporte
                                    newVictim.getTypeId().getTypeId() == (short) 6) {//adulto sin identificacion
                                newVictim.setTypeId(idTypesFacade.find((short) 9));//sin determinar
                            }
                        } else {//mayor de edad
                            if (newVictim.getTypeId().getTypeId() == (short) 5||//tarjeta de identidad
                                     newVictim.getTypeId().getTypeId() == (short) 4||//registro civil
                                     newVictim.getTypeId().getTypeId() == (short) 7) {//menor sin identificacion
                                newVictim.setTypeId(idTypesFacade.find((short) 9));//sin determinar
                            }
                        }

                    }
                }
                
                //PERSISTO
                try {
                    //newVictim.setTagId(tagsFacade.find(newTag));
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

    public void registerSCC_F_032() throws ParseException {
        /**
         * *********************************************************************
         * CARGA DE REGISTROS DE LA FICHA LCENF
         * ********************************************************************
         */
        tuplesNumber = 0;
        tuplesProcessed = 0;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            resultSetFileData = connectionJdbcMB.consult("SELECT COUNT(*) FROM " + nameTableTemp + "; ");
            resultSetFileData.next();
            tuplesNumber = resultSetFileData.getInt(1);//NUMERO DE TUPLAS
            resultSetFileData = connectionJdbcMB.consult("SELECT * FROM " + nameTableTemp + " ORDER BY id; ");//resultSetFileData contendra todos los registros de el archivo(tabla temp)
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
                newVictim.setTagId(tagsFacade.find(newTag.getTagId()));
                newNonFatalInjury = new NonFatalInjuries();
                newNonFatalInjury.setNonFatalInjuryId(nonFatalInjuriesFacade.findMax() + 1);
                newNonFatalDomesticViolence = new NonFatalDomesticViolence();
                newNonFatalDomesticViolence.setNonFatalInjuryId(newNonFatalInjury.getNonFatalInjuryId());
                newNonFatalDomesticViolence.setNonFatalInjuries(newNonFatalInjury);
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
                    relationVar = currentRelationsGroup.findRelationVarByNameFound(columnsNames[posCol]);//determino la relacion de variables
                    //if (columnsNames[posCol].compareTo("edad_paciente") == 0 && tuplesProcessed > 309) { columnsNames[posCol] = "edad_paciente"; }
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
                            case primer_nombre:
                                if (name.trim().length() != 0) {
                                    name = name + " " + value;
                                } else {
                                    name = value;
                                }
                                break;
                            case segundo_nombre:
                                if (name.trim().length() != 0) {
                                    name = name + " " + value;
                                } else {
                                    name = value;
                                }
                                break;
                            case primer_apellido:
                                if (surname.trim().length() != 0) {
                                    surname = surname + " " + value;
                                } else {
                                    surname = value;
                                }
                                break;
                            case segundo_apellido:
                                if (surname.trim().length() != 0) {
                                    surname = surname + " " + value;
                                } else {
                                    surname = value;
                                }
                                break;
                            case tipo_identificacion_victima:
                                newVictim.setTypeId(idTypesFacade.find(Short.parseShort(value)));
                                break;
                            case numero_identificacion_victima:
                                newVictim.setVictimNid(value);
                                break;
                            case aseguradora:
                                newVictim.setInsuranceId(insuranceFacade.find(Short.parseShort(value)));
                                break;
                            case fecha_nacimiento:
                                try {
                                    currentDate = dateFormat.parse(value);
                                    newVictim.setVictimDateOfBirth(currentDate);
                                } catch (ParseException ex) {
                                }
                                break;
                            case medida_edad:
                                newVictim.setAgeTypeId(Short.parseShort(value));
                                break;
                            case edad_paciente:
                                newVictim.setVictimAge(Short.parseShort(value));
                                break;
                            case sexo_paciente:
                                newVictim.setGenderId(gendersFacade.find(Short.parseShort(value)));
                                break;
                            case ocupacion_paciente:
                                try {
                                    newVictim.setJobId(jobsFacade.find(Short.parseShort(value)));
                                } catch (Exception e) {
                                }
                                break;
                            case grupo_etnico:
                                newVictim.setEthnicGroupId(ethnicGroupsFacade.find(Short.parseShort(value)));
                                break;
                            case barrio_residencia_victima://barrio de barrio_residencia_victima
                                newVictim.setVictimNeighborhoodId(neighborhoodsFacade.find(Integer.parseInt(value)));
                                break;
                            case direccion_residencia://direccion de barrio_residencia_victima
                                newVictim.setVictimAddress(value);
                                break;
                            case telefono_residencia:
                                newVictim.setVictimTelephone(value);
                                break;
                            case departamento_residencia:
                                newVictim.setResidenceDepartment(Short.parseShort(value));
                                break;
                            case municipio_residencia://municipio barrio_residencia_victima
                                splitArray = value.split("-");
                                if (splitArray.length == 2) {
                                    newVictim.setResidenceDepartment(Short.parseShort(splitArray[0]));
                                    newVictim.setResidenceMunicipality(Short.parseShort(splitArray[1]));
                                }
                                break;
                            // ************************************************DATOS PARA LA TABLA non_fatal_transport
                            case tipo_transporte_lesionado:
                                newNonFatalTransport.setTransportTypeId(transportTypesFacade.find(Short.parseShort(value)));
                                selectInjuryDetermined = injuriesFacade.find((short) 51);
                                break;
                            case tipo_transporte_contraparte:
                                newNonFatalTransport.setTransportCounterpartId(transportCounterpartsFacade.find(Short.parseShort(value)));
                                selectInjuryDetermined = injuriesFacade.find((short) 51);
                                break;
                            case tipo_usuario_transporte:
                                newNonFatalTransport.setTransportUserId(transportUsersFacade.find(Short.parseShort(value)));
                                selectInjuryDetermined = injuriesFacade.find((short) 51);
                                break;
                            // ************************************************DATOS PARA LA TABLA non_fatal_interpersonal
                            case antecedentes_previos_agresion://boleano->previous_antecedent                                    
                                newNonFatalInterpersonal.setPreviousAntecedent(boolean3Facade.find(Short.parseShort(value)));
                                selectInjuryDetermined = injuriesFacade.find((short) 50);
                                break;
                            case relacion_agresor_victima://categorico->relationships_to_victim                                    
                                newNonFatalInterpersonal.setRelationshipVictimId(relationshipsToVictimFacade.find(Short.parseShort(value)));
                                selectInjuryDetermined = injuriesFacade.find((short) 50);
                                break;
                            case contexto_en_violencia_interpersonal:
                                newNonFatalInterpersonal.setContextId(contextsFacade.find(Short.parseShort(value)));
                                selectInjuryDetermined = injuriesFacade.find((short) 50);
                                break;
                            case sexo_agresores:
                                newNonFatalInterpersonal.setAggressorGenderId(aggressorGendersFacade.find(Short.parseShort(value)));
                                selectInjuryDetermined = injuriesFacade.find((short) 50);
                                break;
                            // ************************************************DATOS PARA LA TABLA non_fatal_selft-inflicted
                            case intento_previo_autoinflingida:
                                newNonFatalSelfInflicted.setPreviousAttempt(boolean3Facade.find(Short.parseShort(value)));//si                                    
                                selectInjuryDetermined = injuriesFacade.find((short) 52);
                                break;
                            case antecedentes_transtorno_mental:
                                newNonFatalSelfInflicted.setMentalAntecedent(boolean3Facade.find(Short.parseShort(value)));//si                                    
                                selectInjuryDetermined = injuriesFacade.find((short) 52);
                                break;
                            case factores_precipitantes:
                                newNonFatalSelfInflicted.setPrecipitatingFactorId(precipitatingFactorsFacade.find(Short.parseShort(value)));
                                selectInjuryDetermined = injuriesFacade.find((short) 52);
                                break;
                            // ************************************************DATOS PARA LA TABLA non_fatal_transport_security_element
                            case uso_elementos_seguridad:
                                if (value.compareTo("2") == 0 || value.compareTo("NO") == 0) {
                                    securityElementList.add(securityElementsFacade.find((short) 6));
                                    selectInjuryDetermined = injuriesFacade.find((short) 51);
                                }
                                break;
                            case usaba_cinturon:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    securityElementList.add(securityElementsFacade.find((short) 1));
                                    selectInjuryDetermined = injuriesFacade.find((short) 51);
                                }
                                break;
                            case usaba_casco_motocicleta:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    securityElementList.add(securityElementsFacade.find((short) 2));
                                    selectInjuryDetermined = injuriesFacade.find((short) 51);
                                }
                                break;
                            case usaba_casco_bicicleta:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    securityElementList.add(securityElementsFacade.find((short) 3));
                                    selectInjuryDetermined = injuriesFacade.find((short) 51);
                                }
                                break;
                            case usaba_chaleco:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    securityElementList.add(securityElementsFacade.find((short) 4));
                                    selectInjuryDetermined = injuriesFacade.find((short) 51);
                                }
                                break;
                            case otro_elemento_seguridad:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    securityElementList.add(securityElementsFacade.find((short) 5));
                                    selectInjuryDetermined = injuriesFacade.find((short) 51);
                                }
                                break;
                            // ************************************************DATOS PARA LA TABLA domestic_violence_abuse_type
                            case maltrato_fisico:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 1));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case maltrato_psicologico:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 2));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case maltrato_abuso_sexual:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 3));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case maltrato_negligencia:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 4));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case maltrato_abandono:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 5));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case maltrato_institucional:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 6));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case maltrato_sin_dato:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 7));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            // ************************************************DATOS PARA LA TABLA domestic_violence_aggressor_type
                            case agresor_padre:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 1));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case agresor_madre:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 2));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case agresor_padrastro:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 3));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case agresor_madrastra:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 4));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case agresor_conyuge:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 5));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case agresor_hermano:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 6));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case agresor_hijo:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 7));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case agresor_otro:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 8));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            case agresor_sin_dato:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 9));
                                    selectInjuryDetermined = injuriesFacade.find((short) 55);
                                }
                                break;
                            // ************************************************DATOS PARA LA TABLA non_fatal_anatomical_location
                            case sitio_anatomico_sistemico:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    anatomicalLocationsList.add(new AnatomicalLocations((short) 1));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case sitio_anatomico_craneo:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    anatomicalLocationsList.add(new AnatomicalLocations((short) 2));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case sitio_anatomico_afectado_ojos:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    anatomicalLocationsList.add(new AnatomicalLocations((short) 3));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case sitio_anatomico_maxilofacial:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    anatomicalLocationsList.add(new AnatomicalLocations((short) 4));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case sitio_anatomico_cuello:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    anatomicalLocationsList.add(new AnatomicalLocations((short) 5));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case sitio_anatomico_torax:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    anatomicalLocationsList.add(new AnatomicalLocations((short) 6));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case sitio_anatomico_abdomen:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    anatomicalLocationsList.add(new AnatomicalLocations((short) 7));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case sitio_anatomico_columna:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    anatomicalLocationsList.add(new AnatomicalLocations((short) 8));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case sitio_anatomico_pelvis:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    anatomicalLocationsList.add(new AnatomicalLocations((short) 9));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case sitio_anatomico_miembros_superiores:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    anatomicalLocationsList.add(new AnatomicalLocations((short) 10));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case sitio_anatomico_miembros_inferiores:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    anatomicalLocationsList.add(new AnatomicalLocations((short) 11));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case sitio_anatomico_otro:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    anatomicalLocationsList.add(new AnatomicalLocations((short) 98));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            // ************************************************DATOS PARA LA TABLA non_fatal_kind_of_injury
                            case naturaleza_lesion_laceracion:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    kindsOfInjurysList.add(new KindsOfInjury((short) 1));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case naturaleza_lesion_cortada:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    kindsOfInjurysList.add(new KindsOfInjury((short) 2));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case naturaleza_lesion_profunda:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    kindsOfInjurysList.add(new KindsOfInjury((short) 3));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case naturaleza_lesion_esgince:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    kindsOfInjurysList.add(new KindsOfInjury((short) 4));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case naturaleza_lesion_fractura:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    kindsOfInjurysList.add(new KindsOfInjury((short) 5));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case naturaleza_lesion_quemadura:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    kindsOfInjurysList.add(new KindsOfInjury((short) 6));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case naturaleza_lesion_contusion:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    kindsOfInjurysList.add(new KindsOfInjury((short) 7));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case naturaleza_lesion_organo_sitemica:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    kindsOfInjurysList.add(new KindsOfInjury((short) 8));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case naturaleza_lesion_trauma_craneoencefalico:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    kindsOfInjurysList.add(new KindsOfInjury((short) 9));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case naturaleza_lesion_otro_tipo:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    kindsOfInjurysList.add(new KindsOfInjury((short) 98));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            case naturaleza_lesion_no_se_sabe:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    kindsOfInjurysList.add(new KindsOfInjury((short) 99));
                                    selectInjuryDetermined = injuriesFacade.find((short) 50);
                                }
                                break;
                            // ************************************************DATOS PARA LA TABLA non_fatal_diagnosis
                            case CIE_1:
                                diagnosesList.add(diagnosesFacade.find(value));
                                break;
                            case CIE_2:
                                diagnosesList.add(diagnosesFacade.find(value));
                                break;
                            case CIE_3:
                                diagnosesList.add(diagnosesFacade.find(value));
                                break;
                            case CIE_4:
                                diagnosesList.add(diagnosesFacade.find(value));
                                break;
                            // ************************************************DATOS PARA LA TABLA victim_vulnerable_group
                            case desplazado:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    vulnerableGroupList.add(vulnerableGroupsFacade.find((short) 1));
                                }
                                break;
                            case discapacitado:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    vulnerableGroupList.add(vulnerableGroupsFacade.find((short) 2));
                                }
                                break;
                            // ************************************************DATOS PARA LA TABLA non_fatal_injuries
                            case institucion_salud:
                                newNonFatalInjury.setNonFatalDataSourceId(nonFatalDataSourcesFacade.find(Short.parseShort(value)));
                                break;
                            case barrio_evento://bario donde ocurrio el evento
                                newNonFatalInjury.setInjuryNeighborhoodId(neighborhoodsFacade.find(Integer.parseInt(value)));
                                break;
                            case direccion_evento://Direccion del evento
                                newNonFatalInjury.setInjuryAddress(value);
                                break;
                            case fecha_evento:
                                try {
                                    currentDate = dateFormat.parse(value);
                                    newNonFatalInjury.setInjuryDate(currentDate);
                                } catch (ParseException ex) {
                                }
                                break;
                            case dia_semana_evento://duai de la semana evento
                                //newNonFatalInjuries.setInjuryDayOfWeek(value);
                                break;
                            case dia_evento://dia del evento
                                dia = value;
                            case mes_evento://mes evento
                                mes = value;
                                break;
                            case año_evento://año del evento
                                ao = value;
                                break;
                            case hora_militar_evento:
                                n = new Date();
                                hourInt = Integer.parseInt(value.substring(0, 2));
                                minuteInt = Integer.parseInt(value.substring(2, 4));
                                n.setHours(hourInt);
                                n.setMinutes(minuteInt);
                                n.setSeconds(0);
                                newNonFatalInjury.setInjuryTime(n);
                                break;
                            case hora_evento://hora evento
                                horas = value;
                                break;
                            case minuto_evento://minuto evento
                                minutos = value;
                                break;
                            case am_pm://ampm evento
                                ampm = value;
                                break;
                            case fecha_consulta:
                                try {
                                    currentDate = dateFormat.parse(value);
                                    newNonFatalInjury.setCheckupDate(currentDate);
                                } catch (ParseException ex) {
                                }
                                break;
                            case dia_semana_consulta://dia de la semana cnsulta                                
                                newNonFatalInjury.setInjuryDayOfWeek(daysFacade.find(Short.parseShort(value)).getDaysName());
                                break;
                            case dia_consulta://dia de la consulta
                                dia1 = value;
                                break;
                            case mes_consulta://mes de la consulta
                                mes1 = value;
                                break;
                            case año_consulta://año de la consulta
                                ao1 = value;
                                break;
                            case hora_militar_consulta:
                                n = new Date();
                                hourInt = Integer.parseInt(value.substring(0, 2));
                                minuteInt = Integer.parseInt(value.substring(2, 4));
                                n.setHours(hourInt);
                                n.setMinutes(minuteInt);
                                n.setSeconds(0);
                                newNonFatalInjury.setCheckupTime(n);
                                break;
                            case hora_consulta://hora consulta
                                horas1 = value;
                                break;
                            case minuto_consulta://minuto consulta
                                minutos1 = value;
                                break;
                            case am_pm1://ampm consulta
                                ampm1 = value;
                                break;
                            case remitido:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    newNonFatalInjury.setSubmittedPatient(true);
                                } else {
                                    newNonFatalInjury.setSubmittedPatient(false);
                                }
                                break;
                            case de_que_ips:
                                newNonFatalInjury.setSubmittedDataSourceId(nonFatalDataSourcesFacade.find(Short.parseShort(value)));
                                break;
                            case intencionalidad:
                                newNonFatalInjury.setIntentionalityId(intentionalitiesFacade.find(Short.parseShort(value)));
                                break;
                            case lugar_ocurrio_lesion:
                                newNonFatalInjury.setInjuryPlaceId(nonFatalPlacesFacade.find(Short.parseShort(value)));
                                break;
                            case activida_que_realizaba:
                                newNonFatalInjury.setActivityId(activitiesFacade.find(Short.parseShort(value)));
                                break;
                            case mecanismo_objeto_lesion:
                                newNonFatalInjury.setMechanismId(mechanismsFacade.find(Short.parseShort(value)));
                                break;
                            case uso_alcohol:
                                newNonFatalInjury.setUseAlcoholId(useAlcoholDrugsFacade.find(Short.parseShort(value)));
                                break;
                            case uso_drogas:
                                newNonFatalInjury.setUseDrugsId(useAlcoholDrugsFacade.find(Short.parseShort(value)));
                                break;
                            case grado_mas_grave_quemadura:
                                newNonFatalInjury.setBurnInjuryDegree(Short.parseShort(value));
                                break;
                            case porcentaje_cuerpo_quemado:
                                newNonFatalInjury.setBurnInjuryPercentage(Short.parseShort(value));
                                break;
                            case destino_paciente:
                                DestinationsOfPatient selectDestinationsOfPatient = destinationsOfPatientFacade.find(Short.parseShort(value));
                                newNonFatalInjury.setDestinationPatientId(selectDestinationsOfPatient);
                                break;
                            //guardar campos otros
                            case otro_grupo_etnico://otro gupo etnico
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 1));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case otro_lugar://otro lugar_ocurrio_lesion
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 2));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case otra_actividad://otra actividad
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 3));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case altura://caul caida_a_que_altura
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 4));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case polvora_cual:
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 5));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case tipo_desastre_natural:
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 6));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case otro_tipo_mecanismo_objeto_lesion://otro mecanismo u objeto de la lesion
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 7));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
//                          case oanimal://cual animal 8
//                          break;
                            case otro_factor_precipitante://otro factor precipitante
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 9));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            //case cual_otro_tipo_agresor://otro tipo_identificacion_victima de agresor 10
                            //    break;
                            //case cual_otro_tipo_maltrato://otro tipo_identificacion_victima de maltrato 11
                            //    break;                                
                            case otro_tipo_relacion_victima://otro tipo_identificacion_victima de relacion con la victima
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 12));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case otro_tipo_transporte_usuario://otro tipo_identificacion_victima de transporte
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 13));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case otro_tipo_transporte_contraparte://otro tipo_identificacion_victima de contraparte
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 14));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case otro_tipo_de_usuario://otro tipo_identificacion_victima de usuario
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 15));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case sitio_anatomico_cual_otro://cual otro_tipo_mecanismo_objeto_lesion sitio anatomico
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 16));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case naturaleza_lesion_cual_otro:
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 17));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case otro_destino_paciente://otro destino_paciente del paciente
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 18));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case medico_diligencio_ficha:
                                newNonFatalInjury.setHealthProfessionalId(healthProfessionalsFacade.find(Integer.parseInt(value)));
                                break;
                            case digitador_ficha:
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

                //SI NO HAY FECHA DE CONSULTA TRATAR DE CALCULAR MEDIANTE LAS VARIABLES dia_evento, mes_evento, año_evento
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
                //SI NO HAY HORA DE CONSULTA TRATAR DE CALCULAR MEDIANTE LAS VARIABLES hora_evento,minuto_evento,am_pm
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


                //SI NO HAY FECHA DE EVENTO TRATAR DE CALCULAR MEDIANTE LAS VARIABLES dia_evento, mes_evento, año_evento
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

                //SI NO HAY HORA DE EVENTO TRATAR DE CALCULAR MEDIANTE LAS VARIABLES hora_evento,minuto_evento,am_pm
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

                //SI NO SE DETERMINA LA INSTITUCION DE SALUD SE ALMACENA LA QUE VIENE DEL FORMULARIO                
                if (newNonFatalInjury.getNonFatalDataSourceId() == null) {
                    if (currentSource != 21) {//"OBSERVATORIO DEL DELITO")
                        newNonFatalInjury.setNonFatalDataSourceId(nonFatalDataSourcesFacade.find((short) currentSource));
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
                                newVictim.setAgeTypeId((short) 1);//aqui por defecto seria sin dato, si no se conoce
//                                if (newVictim.getTypeId() == null) {
//                                    if (ageYears >= 18) {
//                                        newVictim.setTypeId(idTypesFacade.find((short) 1));
//                                    } else {
//                                        newVictim.setTypeId(idTypesFacade.find((short) 5));
//                                    }
//                                }
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(HomicideMB.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }


                if (newVictim.getVictimNid() == null) {//SI NO HAY NUMERO DE IDENTIFICACION 
                    newVictim.setVictimNid(String.valueOf(genNnFacade.findMax() + 1));//asigno un consecutivo a la identificacion
                    newVictim.setVictimClass((short) 2);//nn

                    if (newVictim.getTypeId() == null) {//no hay tipo de identificacion
                        if (newVictim.getVictimAge() != null) {//HAY EDAD Y HAY tipo de edad                            
                            if (newVictim.getVictimAge() > 17) {
                                newVictim.setTypeId(idTypesFacade.find((short) 6));//adulto sin identificacion                                
                            } else {
                                newVictim.setTypeId(idTypesFacade.find((short) 7));//menor sin identificacion
                            }
                        } else {//NO HAY EDAD
                            newVictim.setTypeId(idTypesFacade.find((short) 9));//tipo de identificacoin sin determinar
                        }
                    }
                    GenNn currentGenNn = genNnFacade.find(genNnFacade.findMax());
                    currentGenNn.setCodNn(genNnFacade.findMax() + 1);
                    genNnFacade.edit(currentGenNn);
                } else {//HAY NUMERO DE IDENTIFICACION
                    if (newVictim.getTypeId() == null) {//NO HAY TIPO DE IDENTIFICACION
                        newVictim.setTypeId(idTypesFacade.find((short) 9));//tipo de identificacoin sin determinar
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
                
                //CORRESPONDENCIA ENTRE EDAD Y TIPO DE IDENTIFICACION
                if (newVictim.getTypeId() != null) {//no hay tipo de identificacion
                    if (newVictim.getVictimAge() != null) {//HAY EDAD Y HAY tipo de edad
                        if (newVictim.getVictimAge() < 18) {//menor de edad
                            if (newVictim.getTypeId().getTypeId() == (short) 1 ||//cedula de ciudadania
                                    newVictim.getTypeId().getTypeId() == (short) 2 ||//cedula de extranjeria
                                    newVictim.getTypeId().getTypeId() == (short) 3 ||//pasaporte
                                    newVictim.getTypeId().getTypeId() == (short) 6) {//adulto sin identificacion
                                newVictim.setTypeId(idTypesFacade.find((short) 9));//sin determinar
                            }
                        } else {//mayor de edad
                            if (newVictim.getTypeId().getTypeId() == (short) 5||//tarjeta de identidad
                                     newVictim.getTypeId().getTypeId() == (short) 4||//registro civil
                                     newVictim.getTypeId().getTypeId() == (short) 7) {//menor sin identificacion
                                newVictim.setTypeId(idTypesFacade.find((short) 9));//sin determinar
                            }
                        }

                    }
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
        }
//        catch (Exception ex2) {
//            System.out.println("EXCEPTION INGRESANDO LCENF: " + ex2.toString() + "  " + " " + String.valueOf(tuplesProcessed));
//        }

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
            resultSetFileData = connectionJdbcMB.consult("SELECT COUNT(*) FROM " + nameTableTemp + "; ");
            resultSetFileData.next();
            tuplesNumber = resultSetFileData.getInt(1);//NUMERO DE TUPLAS
            resultSetFileData = connectionJdbcMB.consult("SELECT * FROM " + nameTableTemp + " ORDER BY id; ");//resultSetFileData contendra todos los registros de el archivo(tabla temp)
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
                newVictim.setTagId(tagsFacade.find(newTag.getTagId()));
                newNonFatalInjury = new NonFatalInjuries();
                newNonFatalDomesticViolence = new NonFatalDomesticViolence();
                newNonFatalInjury.setNonFatalInjuryId(nonFatalInjuriesFacade.findMax() + 1);
                newNonFatalInjury.setInputTimestamp(new Date());
                actionsToTakeList = new ArrayList<ActionsToTake>();
                anatomicalLocationsList = new ArrayList<AnatomicalLocations>();
                abuseTypesList = new ArrayList<AbuseTypes>();
                aggressorTypesList = new ArrayList<AggressorTypes>();
                othersList = new ArrayList<Others>();
                diagnosesList = new ArrayList<Diagnoses>();//lista non_fatal_diagnosis
                vulnerableGroupList = new ArrayList<VulnerableGroups>();// lista vector victim_vulnerable_group

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
                    relationVar = currentRelationsGroup.findRelationVarByNameFound(columnsNames[posCol]);//determino la relacion de variables
                    //if (columnsNames[posCol].compareTo("edad_paciente") == 0 && tuplesProcessed > 309) { columnsNames[posCol] = "edad_paciente"; }
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
                            case institucion_receptora:
                                newNonFatalDomesticViolence.setDomesticViolenceDataSourceId(domesticViolenceDataSourcesFacade.find(Short.parseShort(value)));
                                break;
                            case primer_apellido:
                                if (surname.trim().length() != 0) {
                                    surname = surname + " " + value;
                                } else {
                                    surname = value;
                                }
                                break;
                            case segundo_apellido:
                                if (surname.trim().length() != 0) {
                                    surname = surname + " " + value;
                                } else {
                                    surname = value;
                                }
                                break;
                            case primer_nombre:
                                if (name.trim().length() != 0) {
                                    name = name + " " + value;
                                } else {
                                    name = value;
                                }
                                break;
                            case segundo_nombre:
                                if (name.trim().length() != 0) {
                                    name = name + " " + value;
                                } else {
                                    name = value;
                                }
                                break;
                            case tipo_identificacion_victima:
                                newVictim.setTypeId(idTypesFacade.find(Short.parseShort(value)));
                                break;
                            case numero_identificacion_victima:
                                newVictim.setVictimNid(value);
                                break;
                            case medida_edad:
                                newVictim.setAgeTypeId(Short.parseShort(value));
                                break;
                            case edad_victima:
                                newVictim.setVictimAge(Short.parseShort(value));
                                break;
                            case sexo_victima:
                                newVictim.setGenderId(gendersFacade.find(Short.parseShort(value)));
                                break;
                            case ocupacion_victima:
                                newVictim.setJobId(jobsFacade.find(Short.parseShort(value)));
                                break;
                            case aseguradora:
                                newVictim.setInsuranceId(insuranceFacade.find(Short.parseShort(value)));
                                break;
                            case departamento_residencia:
                                newVictim.setResidenceDepartment(Short.parseShort(value));
                                break;
//                            case codigo:
//                                newVictim.setResidenceDepartment(Short.parseShort(value));
//                                break;
                            case municipio_residencia:
                                splitArray = value.split("-");
                                if (splitArray.length == 2) {
                                    newVictim.setResidenceDepartment(Short.parseShort(splitArray[0]));
                                    newVictim.setResidenceMunicipality(Short.parseShort(splitArray[1]));
                                }
                                break;
//                            case codigo1:
//                                splitArray = value.split("-");
//                                if (splitArray.length == 2) {
//                                    newVictim.setResidenceDepartment(Short.parseShort(splitArray[0]));
//                                    newVictim.setResidenceMunicipality(Short.parseShort(splitArray[1]));
//                                }
//                                break;
                            case barrio_residencia_victima://barrio barrio_residencia_victima
                                newVictim.setVictimNeighborhoodId(neighborhoodsFacade.find(Integer.parseInt(value)));
                                break;
//                            case codigo2://barrio barrio_residencia_victima
//                                newVictim.setVictimNeighborhoodId(neighborhoodsFacade.find(Integer.parseInt(value)));
//                                break;
                            case direccion_residencia://direccion barrio_residencia_victima
                                newVictim.setVictimAddress(value);
                                break;
                            case telefono_residencia:
                                newVictim.setVictimTelephone(value);
                                break;
                            //case departamento_evento://departamento evento
                            //    break;
                            //case codigo3://departamento evento
                            //    break;
                            //case municipio_evento://municpio evento
                            //    break;
                            //case codigo4://municipio evento
                            //    break;
                            case barrio_evento://barrio evento
                                newNonFatalInjury.setInjuryNeighborhoodId(neighborhoodsFacade.find(Integer.parseInt(value)));
                                break;
                            case fecha_nacimiento://fecha nacimiento
                                newNonFatalInjury.setInjuryNeighborhoodId(neighborhoodsFacade.find(Integer.parseInt(value)));
                                break;
                            case direccion_evento://direccion evento
                                newNonFatalInjury.setInjuryAddress(value);
                                break;
                            case dia_evento://dia del evento
                                dia = value;
                                break;
                            case mes_evento:
                                mes = value;
                                break;
                            case año_evento:
                                ao = value;
                                break;
                            case fecha_evento://fecha del evento
                                try {
                                    currentDate = dateFormat.parse(value);
                                    newNonFatalInjury.setInjuryDate(currentDate);
                                } catch (ParseException ex) {
                                }
                                break;
                            case dia_semana_evento://dia de la semana del evento
                                newNonFatalInjury.setInjuryDayOfWeek(daysFacade.find(Short.parseShort(value)).getDaysName());
                                break;
                            case hora_evento://hora del evento
                                horas = value;
                                break;
                            case minuto_evento://minuto del evento
                                minutos = value;
                                break;
                            case am_pm:
                                ampm = value;
                                break;
                            case hora_militar_evento://hora militar del evento
                                n = new Date();
                                hourInt = Integer.parseInt(value.substring(0, 2));
                                minuteInt = Integer.parseInt(value.substring(2, 4));
                                n.setHours(hourInt);
                                n.setMinutes(minuteInt);
                                n.setSeconds(0);
                                newNonFatalInjury.setInjuryTime(n);
                                break;
                            case dia_consulta://dia de la consulta
                                dia1 = value;
                                break;
                            case mes_consulta://mes de la consulta
                                mes1 = value;
                                break;
                            case año_consulta:
                                break;
                            case fecha_consulta://fecha de la consulta
                                try {
                                    currentDate = dateFormat.parse(value);
                                    newNonFatalInjury.setCheckupDate(currentDate);
                                } catch (ParseException ex) {
                                }
                                break;
                            case dia_semana_consulta://dia de la semana de la consuta

                                break;
                            case hora_consulta://hora de la consulta
                                horas1 = value;
                                break;
                            case minuto_consulta://minuto de la consulta
                                minutos1 = value;
                                break;
                            case am_pm1://ampm consulta
                                ampm1 = value;
                                break;
                            case hora_militar_consulta://hora militar de la consulta
                                n = new Date();
                                hourInt = Integer.parseInt(value.substring(0, 2));
                                minuteInt = Integer.parseInt(value.substring(2, 4));
                                n.setHours(hourInt);
                                n.setMinutes(minuteInt);
                                n.setSeconds(0);
                                newNonFatalInjury.setCheckupTime(n);
                                break;
                            case es_remitido:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    newNonFatalInjury.setSubmittedPatient(true);
                                } else {
                                    newNonFatalInjury.setSubmittedPatient(false);
                                }
                                break;
                            case de_que_ips:
                                newNonFatalInjury.setSubmittedDataSourceId(nonFatalDataSourcesFacade.find(Short.parseShort(value)));
                                break;
                            case lugar_ocurrio_evento:
                                newNonFatalInjury.setInjuryPlaceId(nonFatalPlacesFacade.find(Short.parseShort(value)));
                                break;

                            case activida_que_realizaba:
                                newNonFatalInjury.setActivityId(activitiesFacade.find(Short.parseShort(value)));
                                break;

                            case mecanismo_objeto_lesion:
                                newNonFatalInjury.setMechanismId(mechanismsFacade.find(Short.parseShort(value)));
                                break;

                            //CAMPOS OTROS
                            case otro_lugar://otro lugar_ocurrio_lesion
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 1));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case otra_actividad://otra actividad
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 2));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case caida_a_que_altura://"En caso de caida: a que caida_a_que_altura fue"
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 3));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case que_tipo_polvora://"En caso de quemadura por polvora: que tipo_identificacion_victima de polvora fue"
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 4));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case cual_desastre_natural://"En caso de desastre natural: que tipo_identificacion_victima de desastre fue"
                                //AL PARECER NO SE USA
                                break;
                            case otro_tipo_mecanismo_objeto_lesion://"Otro tipo_identificacion_victima de mecanismo u objeto"
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 6));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case otro_grupo_etnico://otro grupos_vulnerables etnico
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 8));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case otro_grupo_vulnerable://otro grupos_vulnerables vulnerable
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 9));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            case cual_otro_tipo_agresor://"En caso de otro_tipo_mecanismo_objeto_lesion tipo_identificacion_victima de agresor: Cual"
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
                            case cual_otro_tipo_maltrato://"En caso de otro_tipo_mecanismo_objeto_lesion tipo_identificacion_victima de maltrato: Cual"
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
                            case cual_otra_accion_realizar://"En caso de accion_realizar_otra acción a realizar: Cual"
                                newOther = new Others(new OthersPK(newVictim.getVictimId(), (short) 12));
                                newOther.setValueText(value);
                                othersList.add(newOther);
                                break;
                            //FIN CAMPOS OTROS---------------------------------------
                            case uso_alcohol://"Uso de uso_alcohol"
                                newNonFatalInjury.setUseAlcoholId(useAlcoholDrugsFacade.find(Short.parseShort(value)));
                                break;
                            case uso_drogas://"Uso de uso_drogas"
                                newNonFatalInjury.setUseDrugsId(useAlcoholDrugsFacade.find(Short.parseShort(value)));
                                break;
                            case grado_mas_grave_quemadura://"En caso de quemadura: Grado más grave"
                                newNonFatalInjury.setBurnInjuryDegree(Short.parseShort(value));
                                break;
                            case porcentaje_cuerpo_quemado://"En caso de quemadura: Porcentaje del cuerpo quemado"
                                newNonFatalInjury.setBurnInjuryPercentage(Short.parseShort(value));
                                break;
                            case grupos_vulnerables://GRUPO VULNERABLE
                                VulnerableGroups auxVulnerableGroups = vulnerableGroupsFacade.find(Short.parseShort(value));
                                //List<VulnerableGroups> vulnerableGroupsList = new ArrayList<VulnerableGroups>();
                                vulnerableGroupList.add(auxVulnerableGroups);
                                newVictim.setVulnerableGroupsList(vulnerableGroupList);
                                break;
                            case grupos_etnicos://GRUPO ETNICO
                                newVictim.setEthnicGroupId(ethnicGroupsFacade.find(Short.parseShort(value)));
                                break;
                            case maltrato_fisico:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 17));
                                }
                                break;
                            case maltrato_psicologico:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 2));
                                }
                                break;
                            case maltrato_abuso_sexual:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 3));
                                }
                                break;
                            case maltrato_negligencia:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 4));
                                }
                                break;
                            case maltrato_abandono:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 5));
                                }
                                break;
                            case maltrato_institucional:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 6));
                                }
                                break;
                            case maltrato_sin_dato:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    abuseTypesList.add(new AbuseTypes((short) 7));
                                }
                                break;
                            case maltrato_otro:
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
                            case tipo_agresor_padre:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 1));
                                }
                                break;
                            case tipo_agresor_madre:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 2));
                                }
                                break;
                            case tipo_agresor_padrastro:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 3));
                                }
                                break;
                            case tipo_agresor_madrastra:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 4));
                                }
                                break;
                            case tipo_agresor_conyuge:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 5));
                                }
                                break;
                            case tipo_agresor_hermano:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 6));
                                }
                                break;
                            case tipo_agresor_hijo:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 7));
                                }
                                break;
                            case tipo_agresor_otro:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 8));
                                }
                                break;
                            case tipo_agresor_sin_dato:
                                if (value.compareTo("1") == 0 || value.compareTo("SI") == 0) {
                                    aggressorTypesList.add(new AggressorTypes((short) 9));
                                }
                                break;
                            case tipo_agresor_novio:
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
                            case accion_realizar_conciliacion:
                                actionsToTakeList.add(actionsToTakeFacade.find((short) 1));
                                break;
                            case accion_realizar_caucion:
                                actionsToTakeList.add(actionsToTakeFacade.find((short) 2));
                                break;
                            case accion_realizar_dictamen:
                                actionsToTakeList.add(actionsToTakeFacade.find((short) 3));
                                break;
                            case accion_realizar_remision_fiscalia:
                                actionsToTakeList.add(actionsToTakeFacade.find((short) 4));
                                break;
                            case accion_realizar_remision_medicina_legal:
                                actionsToTakeList.add(actionsToTakeFacade.find((short) 5));
                                break;
                            case accion_realizar_remision_comisaria:
                                actionsToTakeList.add(actionsToTakeFacade.find((short) 6));
                                break;
                            case accion_realizar_remision_icbf:
                                actionsToTakeList.add(actionsToTakeFacade.find((short) 7));
                                break;
                            case accion_realizar_medidas_proteccion:
                                actionsToTakeList.add(actionsToTakeFacade.find((short) 8));
                                break;
                            case accion_realizar_resimison_salud:
                                actionsToTakeList.add(actionsToTakeFacade.find((short) 9));
                                break;
                            case accion_realizar_atencion_psicologica:
                                actionsToTakeList.add(actionsToTakeFacade.find((short) 10));
                                break;
                            case accion_realizar_restablecimiento_derechos:
                                actionsToTakeList.add(actionsToTakeFacade.find((short) 11));
                                break;
                            case accion_realizar_otra:
                                actionsToTakeList.add(actionsToTakeFacade.find((short) 12));
                                break;
                            case accion_realizar_sin_dato:
                                actionsToTakeList.add(actionsToTakeFacade.find((short) 13));
                                break;
                            case responsable:
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
                //SI NO HAY FECHA DE CONSULTA TRATAR DE CALCULAR MEDIANTE LAS VARIABLES dia_evento, mes_evento, año_evento
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
                //SI NO HAY HORA DE CONSULTA TRATAR DE CALCULAR MEDIANTE LAS VARIABLES hora_evento,minuto_evento,am_pm
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
                //SI NO HAY FECHA DE EVENTO TRATAR DE CALCULAR MEDIANTE LAS VARIABLES dia_evento, mes_evento, año_evento
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
                //SI NO HAY HORA DE EVENTO TRATAR DE CALCULAR MEDIANTE LAS VARIABLES hora_evento,minuto_evento,am_pm
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

                //SI NO SE DETERMINA LA INSTITUCION DE SALUD SE ALMACENA LA QUE VIENE DEL FORMULARIO                
                if (newNonFatalInjury.getNonFatalDataSourceId() == null) {
                    if (currentSource != 21) {//1=compareTo("OBSERVATORIO DEL DELITO")
                        newNonFatalInjury.setNonFatalDataSourceId(nonFatalDataSourcesFacade.find((short) currentSource));
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
                                newVictim.setAgeTypeId((short) 1);//aqui por defecto seria sin dato, si no se conoce
//                                if (newVictim.getTypeId() == null) {
//                                    if (ageYears >= 18) {
//                                        newVictim.setTypeId(idTypesFacade.find((short) 1));
//                                    } else {
//                                        newVictim.setTypeId(idTypesFacade.find((short) 5));
//                                    }
//                                }
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(HomicideMB.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }


                if (newVictim.getVictimNid() == null) {//SI NO HAY NUMERO DE IDENTIFICACION 
                    newVictim.setVictimNid(String.valueOf(genNnFacade.findMax() + 1));//asigno un consecutivo a la identificacion
                    newVictim.setVictimClass((short) 2);//nn

                    if (newVictim.getTypeId() == null) {//no hay tipo de identificacion
                        if (newVictim.getVictimAge() != null) {//HAY EDAD Y HAY tipo de edad                            
                            if (newVictim.getVictimAge() > 17) {
                                newVictim.setTypeId(idTypesFacade.find((short) 6));//adulto sin identificacion                                
                            } else {
                                newVictim.setTypeId(idTypesFacade.find((short) 7));//menor sin identificacion
                            }
                        } else {//NO HAY EDAD
                            newVictim.setTypeId(idTypesFacade.find((short) 9));//tipo de identificacoin sin determinar
                        }
                    }
                    GenNn currentGenNn = genNnFacade.find(genNnFacade.findMax());
                    currentGenNn.setCodNn(genNnFacade.findMax() + 1);
                    genNnFacade.edit(currentGenNn);
                } else {//HAY NUMERO DE IDENTIFICACION
                    if (newVictim.getTypeId() == null) {//NO HAY TIPO DE IDENTIFICACION
                        newVictim.setTypeId(idTypesFacade.find((short) 9));//tipo de identificacoin sin determinar
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
                if (!othersList.isEmpty()) {
                    newVictim.setOthersList(othersList);
                }
                newNonFatalInjury.setInjuryId(injuriesFacade.find(Short.parseShort("53")));
                
                //CORRESPONDENCIA ENTRE EDAD Y TIPO DE IDENTIFICACION
                if (newVictim.getTypeId() != null) {//no hay tipo de identificacion
                    if (newVictim.getVictimAge() != null) {//HAY EDAD Y HAY tipo de edad
                        if (newVictim.getVictimAge() < 18) {//menor de edad
                            if (newVictim.getTypeId().getTypeId() == (short) 1 ||//cedula de ciudadania
                                    newVictim.getTypeId().getTypeId() == (short) 2 ||//cedula de extranjeria
                                    newVictim.getTypeId().getTypeId() == (short) 3 ||//pasaporte
                                    newVictim.getTypeId().getTypeId() == (short) 6) {//adulto sin identificacion
                                newVictim.setTypeId(idTypesFacade.find((short) 9));//sin determinar
                            }
                        } else {//mayor de edad
                            if (newVictim.getTypeId().getTypeId() == (short) 5||//tarjeta de identidad
                                     newVictim.getTypeId().getTypeId() == (short) 4||//registro civil
                                     newVictim.getTypeId().getTypeId() == (short) 7) {//menor sin identificacion
                                newVictim.setTypeId(idTypesFacade.find((short) 9));//sin determinar
                            }
                        }

                    }
                }

                //PERSISTO//////////////////////////////////////////////////////////////////
                try {

                    newNonFatalInjury.setVictimId(newVictim);
                    victimsFacade.create(newVictim);//PERSISTO LA VICTIMA                
                    nonFatalInjuriesFacade.create(newNonFatalInjury);//PERSISTO LA LESION NO FATAL                    
                    newNonFatalDomesticViolence.setNonFatalInjuryId(newNonFatalInjury.getNonFatalInjuryId());
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

    public void btnRegisterDataClick() throws ParseException {
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
         * validacion de si un numero_identificacion_victima es porcentaje 1-100
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
         * validacion de si un numero_identificacion_victima es >= 0
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
         * validacion de si un numero_identificacion_victima de 1 y 31
         * null=invalido ""=aceptado pero vacio "valor"=aceptado y me dice el
         * valor
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
         * validacion de si un numero_identificacion_victima de 1 y 12
         * null=invalido ""=aceptado pero vacio "valor"=aceptado y me dice el
         * valor
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
         * validacion de si un numero_identificacion_victima de 1 y 12
         * null=invalido ""=aceptado pero vacio "valor"=aceptado y me dice el
         * valor
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
         * validacion de si un numero_identificacion_victima de 1 y 12
         * null=invalido ""=aceptado pero vacio "valor"=aceptado y me dice el
         * valor
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
         * validacion de si un numero_identificacion_victima de 1 y 12
         * null=invalido ""=aceptado pero vacio "valor"=aceptado y me dice el
         * valor
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
         * validacion de si un string es una dia_evento de un determinado
         * formato null=invalido ""=aceptado pero vacio "valor"=aceptado y me
         * dice el valor
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
         * validacion de si un string es un hora_evento miitar null=invalido
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
                        //return "La hora_evento debe estar entre 0 y 23";
                    }
                    if (m > 59 || m < 0) {
                        return null;
                        //return "los minuto_evento deben estar entre 0 y 59";
                    }
                    if (h == 24 && m != 0) {
                        return null;
                        //return "Si la hora_evento es 24 los minuto_evento deben ser cero";
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
                        //return "Si la hora_evento es 24 los minuto_evento solo pueden ser 0";
                    }
                }
                if (h > 24 || h < 0) {
                    return null;
                    //return "La hora_evento debe estar entre 0 y 24";
                }
                if (m > 59 || m < 0) {
                    return null;
                    //return "los minuto_evento deben estar entre 0 y 59";
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
                //return "Valor no aceptado como hora_evento militar";
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
                    //return "La hora_evento debe estar entre 0 y 23";
                }
                if (m > 59 || m < 0) {
                    return null;
                    //return "los minuto_evento deben estar entre 0 y 59";
                }
                if (h == 24 && m != 0) {
                    return null;
                    //return "Si la hora_evento es 24 los minuto_evento deben ser cero";
                }
                return str;
            } catch (Exception ex) {
            }
        } else {
            return null;
            //return "Una hora_evento militar debe tener menos de 4 digitos";
        }

        return null;
        //return "Valor no aceptado como hora_evento militar";
    }

    private String isAge(String str) {
        /*
         * validacion de si un string es numero_identificacion_victima entero o
         * edad_victima definida en meses y años null = invalido "" = aceptado
         * pero vacio "valor" = aceptado y me dice el valor
         */
        //String[] splitAge;
        if (str.trim().length() == 0) {
            //splitAge = new String[1];
            //splitAge[0] = "";
            return "";
        }
        try {//intento convertirlo en entero
            int a = Integer.parseInt(str);
            if (a > 150 || a < 0) {
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
                if (y > 150) {
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

    private String isCategorical(String valueFound, RelationVariables relationVar) {
        /*
         * validacion de si un valor esta dentro de una categoria, o es
         * descartado, retorna el id respectivo a la tabla categorica
         */
        if (valueFound.trim().length() == 0) {
            return "";
        }

        //se valida con respecto a las relaciones de valores
        if (relationVar.getComparisonForCode() == true) {
            for (int i = 0; i < relationVar.getRelationValuesList().size(); i++) {
                if (relationVar.getRelationValuesList().get(i).getNameFound().compareTo(valueFound) == 0) {
                    return connectionJdbcMB.findIdByCategoricalCode(relationVar.getFieldType(), relationVar.getRelationValuesList().get(i).getNameExpected());
                }
            }
        } else {
            for (int i = 0; i < relationVar.getRelationValuesList().size(); i++) {
                if (relationVar.getRelationValuesList().get(i).getNameFound().compareTo(valueFound) == 0) {
                    return connectionJdbcMB.findIdByCategoricalName(relationVar.getFieldType(), relationVar.getRelationValuesList().get(i).getNameExpected());
                }
            }
        }

        //verificar si es descartado
        for (int i = 0; i < relationVar.getRelationsDiscardedValuesList().size(); i++) {
            if (valueFound.compareTo(relationVar.getRelationsDiscardedValuesList().get(i).getDiscardedValueName()) == 0) {
                return "";
            }
        }
        //se valida con respecto a los valores esperados
        if (relationVar.getComparisonForCode() == true) {
            return connectionJdbcMB.findIdByCategoricalCode(relationVar.getFieldType(), valueFound);
        } else {
            return connectionJdbcMB.findIdByCategoricalName(relationVar.getFieldType(), valueFound);
        }
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES GET Y SET DE LAS VARIABLES ---------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
//    public void setFormsAndFieldsDataMB(FormsAndFieldsDataMB formsAndFieldsDataMB) {
//        this.formsAndFieldsDataMB = formsAndFieldsDataMB;
//    }
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

    public int getCurrentSource() {
        return currentSource;
    }

    public void setCurrentSource(int currentSource) {
        this.currentSource = currentSource;
    }
}
