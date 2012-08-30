/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.fileProcessing;

import beans.connection.ConnectionJDBC;
import beans.enumerators.DataTypeEnum;
import beans.enumerators.SCC_F_032Enum;
import beans.errorsControl.ErrorControl;
import beans.relations.RelationValue;
import beans.relations.RelationVar;
import beans.relations.RelationsGroup;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
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

    private RelationshipOfVariablesMB relationshipOfVariablesMB;
    private RelationsGroup currentRelationsGroup;
    private FormsAndFieldsDataMB formsAndFieldsDataMB;
    private StoredRelationsMB storedRelationsMB;
    private LoginMB loginMB;
    private ErrorsControlMB errorsControlMB;
    private String errorStr;
    private String[] columnsNames;
    private int tuplesNumber;
    private int tuplesProcessed;
    private boolean btnRegisterDataDisabled = false;
    private ConnectionJDBC conx = null;//conexion sin persistencia a postgres   
    private RelationVar relationVar;
    //private Field fieldExepted;
    //private String type;
    //private String error;
    private String nameForm = "";
    //manejo de persistencia
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
    NonFatalInjuriesFacade nonFatalInjuriesFacade;
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
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //MANEJO E LA BARRA DE PROGRESO DEL ALMACENAMIENTO ---------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    private Integer progress;

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public void onComplete() {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se ha realizado la adición de " + String.valueOf(tuplesProcessed)
                + "registros, para filalizar guarde si lo desea la configuración de relaciones actual o reinicie para realizar la carga de registros de otro acrchivo"));
    }

    public void cancel() {
        progress = null;
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES DE PROPOSITO GENERAL ---------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public RecordDataMB() {
        /*
         * Constructor de la clase
         */
        //FacesContext context = FacesContext.getCurrentInstance();
        //relationshipOfVariablesMB = (RelationshipOfVariablesMB) context.getApplication().evaluateExpressionGet(context, "#{relationshipOfVariablesMB}", RelationshipOfVariablesMB.class);
        //formsAndFieldsDataMB=(FormsAndFieldsDataMB) context.getApplication().evaluateExpressionGet(context, "#{formsAndFieldsDataMB}", FormsAndFieldsDataMB.class);
    }

    public void btnResetClick() {
        /*
         * click sobre el boton reset
         */
        progress = 0;
        loginMB.reset();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Correcto!!", "Se han reinicidado los controles"));
    }

    public void btnValidateClick() {
        /*
         * click sobre el boton iniciar validacion aqui se generaran los errores
         * que salgan de analizar el archivo
         */
        try {
            conx = new ConnectionJDBC();
            conx.connect();
            ResultSet resultSetFileData = conx.consult("SELECT COUNT(*) FROM temp; ");
            resultSetFileData.next();
            tuplesNumber = resultSetFileData.getInt(1);//numero de tuplas a procesar
            tuplesProcessed = 0;//numero de tuplas procesdas            
            progress = 0;
            currentRelationsGroup = relationshipOfVariablesMB.getCurrentRelationsGroup();//tomo el grupo de relaciones de valores y de variables
            resultSetFileData = conx.consult("SELECT * FROM temp ORDER BY id; ");//resultSetFileData contendra todos los registros de el archivo(tabla temp)            
            int columnsNumber = resultSetFileData.getMetaData().getColumnCount();
            int errorsNumber = 0;
            int pos = 0;
            columnsNames = new String[columnsNumber];//creo un arreglo con los nombres de las columnas
            for (int i = 1; i <= columnsNumber; i++) {
                columnsNames[pos] = resultSetFileData.getMetaData().getColumnName(i);
                pos++;
            }
            errorsControlMB.setErrorControlArrayList(new ArrayList<ErrorControl>());//arreglo de errores            
            int countErrors = 1;
            while (resultSetFileData.next()) {//recorro cada uno de los registros de la tabla temp                                                
                for (int i = 0; i < columnsNames.length; i++) {//recorro cada una de las columnas de cada registro                    
                    relationVar = currentRelationsGroup.findRelationVar(columnsNames[i]);//determino la relacion de variables
                    if (relationVar != null) {
                        String value;
                        switch (DataTypeEnum.convert(relationVar.getFieldType())) {//tipo de relacion
                            case text:
                                break;
                            case integer:
                                value = isNumeric(resultSetFileData.getString(columnsNames[i]));
                                System.out.println("Validando Entero: " + resultSetFileData.getString(columnsNames[i]) + "   Resultado: " + value);
                                if (value == null) {
                                    errorsNumber++;//error = "No es entero";
                                    errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), String.valueOf(countErrors), "integer"));
                                }
                                break;
                            case age:
                                String[] ageResult = isAge(resultSetFileData.getString(columnsNames[i]));
                                value = null;
                                if (ageResult != null) {
                                    for (int j = 0; j < ageResult.length; j++) {
                                        value = value + "[" + ageResult[j] + "]";
                                    }
                                }
                                System.out.println("Validando Age: " + resultSetFileData.getString(columnsNames[i]) + "   Resultado: " + value);
                                if (value == null) {
                                    errorsNumber++;//error = "fecha no corresponde al formato";
                                    errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), String.valueOf(countErrors), "age"));
                                }
                                break;
                            case date:

                                //if (isDate(resultSetFileData.getString(columnsNames[i]), relationVar.getDateFormat()) == null) {
                                value = isDate(resultSetFileData.getString(columnsNames[i]), relationVar.getDateFormat());
                                System.out.println("Validando Fecha: " + resultSetFileData.getString(columnsNames[i]) + "   Resultado: " + value);
                                if (value == null) {
                                    errorsNumber++;//error = "fecha no corresponde al formato";
                                    errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), String.valueOf(countErrors), "date"));
                                }
                                break;
                            case military:

                                //if (isMilitary(resultSetFileData.getString(columnsNames[i])) == null) {
                                value = isMilitary(resultSetFileData.getString(columnsNames[i]));
                                System.out.println("Validando Militar: " + resultSetFileData.getString(columnsNames[i]) + "   Resultado: " + value);
                                if (value == null) {
                                    errorsNumber++;//la hora militar no puede ser determinada
                                    errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), String.valueOf(countErrors), "military"));
                                }
                                break;
                            case hour:
                                //if (isHour(resultSetFileData.getString(columnsNames[i])) == null) {
                                value = isHour(resultSetFileData.getString(columnsNames[i]));
                                System.out.println("Validando Hora: " + resultSetFileData.getString(columnsNames[i]) + "   Resultado: " + value);
                                if (value == null) {
                                    errorsNumber++;//la hora militar no puede ser determinada
                                    errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), String.valueOf(countErrors), "hour"));
                                }
                                break;
                            case minute:
                                //if (isMinute(resultSetFileData.getString(columnsNames[i])) == null) {
                                value = isMinute(resultSetFileData.getString(columnsNames[i]));
                                System.out.println("Validando Minuto: " + resultSetFileData.getString(columnsNames[i]) + "   Resultado: " + value);
                                if (value == null) {
                                    errorsNumber++;//la hora militar no puede ser determinada
                                    errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), String.valueOf(countErrors), "minute"));
                                }
                                break;
                            case day:
                                //if (isDay(resultSetFileData.getString(columnsNames[i])) == null) {
                                value = isDay(resultSetFileData.getString(columnsNames[i]));
                                System.out.println("Validando Dia: " + resultSetFileData.getString(columnsNames[i]) + "   Resultado: " + value);
                                if (value == null) {
                                    errorsNumber++;//la hora militar no puede ser determinada
                                    errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), String.valueOf(countErrors), "day"));
                                }
                                break;
                            case month:
                                //if (isMonth(resultSetFileData.getString(columnsNames[i])) == null) {
                                value = isMonth(resultSetFileData.getString(columnsNames[i]));
                                System.out.println("Validando Mes: " + resultSetFileData.getString(columnsNames[i]) + "   Resultado: " + value);
                                if (value == null) {
                                    errorsNumber++;//la hora militar no puede ser determinada
                                    errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), String.valueOf(countErrors), "month"));
                                }
                                break;
                            case year:
                                //if (isYear(resultSetFileData.getString(columnsNames[i])) == null) {
                                value = isYear(resultSetFileData.getString(columnsNames[i]));
                                System.out.println("Validando Año: " + resultSetFileData.getString(columnsNames[i]) + "   Resultado: " + value);
                                if (value == null) {
                                    errorsNumber++;//la hora militar no puede ser determinada
                                    errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), String.valueOf(countErrors), "year"));
                                }
                                break;
                            case NOVALUE:
                                //if (isCategorical(resultSetFileData.getString(relationVar.getNameFound()), relationVar) == null) {
                                value = isCategorical(resultSetFileData.getString(relationVar.getNameFound()), relationVar);
                                System.out.println("Validando Categoria: " + resultSetFileData.getString(relationVar.getNameFound()) + "   Resultado: " + value);
                                if (value == null) {
                                    errorsNumber++;//error = "no esta en la categoria ni es un valor descartado";
                                    errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), String.valueOf(countErrors), formsAndFieldsDataMB.variableDescription(relationVar.getNameExpected())));
                                }
                                break;
                        }
                    }
                }
                countErrors++;
            }
            errorsControlMB.setSizeErrorsList(errorsNumber);
            errorsControlMB.updateErrorsArrayList();
            progress = 0;
            conx.disconnect();
            if (errorsNumber != 0) {
                btnRegisterDataDisabled = true;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Errores", "Existen: " + String.valueOf(errorsNumber) + " valores que no superaron el proceso de validación, dirijase a la sección de errores para  especificar si los corrige o ignora."));
            } else {
                btnRegisterDataDisabled = false;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se ha superado el proceso de validacion, presione el boton registrar datos para que sean almacenados."));
            }
        } catch (SQLException ex) {
            System.out.println("error: " + ex.toString());
            conx.disconnect();
        }
    }

    //@PostConstruct //ejecutar despues de el constructor
    public void reset() {
        /*
         * Cargar el formulario con los valores iniciales
         */
    }

    public void registerSCC_F_028() {
    }

    public void registerSCC_F_029() {
    }

    public void registerSCC_F_030() {
    }

    public void registerSCC_F_031() {
    }

    public void registerSCC_F_032() {
        tuplesNumber = 0;
        tuplesProcessed = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate;
        String strDate;
        UseAlcoholDrugs selectUseAlcoholDrugs;
        SecurityElements selectSecurityElement;
        VulnerableGroups selectVulnerableGroups;
        Diagnoses selectDiagnoses;

        //ArrayList<String> array = new ArrayList<String>();
        try {
            conx = new ConnectionJDBC();
            conx.connect();
            //numero de tuplas
            ResultSet resultSetFileData = conx.consult("SELECT COUNT(*) FROM temp; ");
            resultSetFileData.next();
            tuplesNumber = resultSetFileData.getInt(1);
            //resultSetFileData contendra todos los registros de el archivo(tabla temp)
            resultSetFileData = conx.consult("SELECT * FROM temp; ");
            //resultSetFileData.next();
            progress = 0;
            //creo un arreglo con los nombres de las columnas
            int columnsNumber = resultSetFileData.getMetaData().getColumnCount();
            columnsNames = new String[columnsNumber];
            int pos = 0;
            for (int i = 1; i <= columnsNumber; i++) {
                columnsNames[pos] = resultSetFileData.getMetaData().getColumnName(i);
                pos++;
            }
            while (resultSetFileData.next()) {//recorro cada uno de los registros de la tabla temp
                //***********************************************************creo una nueva victima
                VulnerableGroups vulnerableGroups = vulnerableGroupsFacade.find(Short.parseShort("1"));
                Victims newVictim = new Victims();
                newVictim.setVictimId(victimsFacade.findMax() + 1);

                List<VulnerableGroups> vulnerableGroupsList = new ArrayList<VulnerableGroups>();
                vulnerableGroupsList.add(vulnerableGroups);

                newVictim.setVulnerableGroupsList(vulnerableGroupsList);
                newVictim.setVictimClass(Short.parseShort("1"));
                try {
                    currentDate = dateFormat.parse("2012-01-01");
                    newVictim.setVictimDateOfBirth(currentDate);
                } catch (ParseException ex) {
                }

                int MaxIdNFI = nonFatalInjuriesFacade.findMax();//nuevo lesiones_no_fatales
                //VARIABLES NECESARIAS 
                NonFatalInjuries newNonFatalInjuries = new NonFatalInjuries();
                newNonFatalInjuries.setNonFatalInjuryId(MaxIdNFI + 1);
                Injuries selectInjury = injuriesFacade.find(Short.parseShort("54"));
                newNonFatalInjuries.setInjuryId(selectInjury);
                newNonFatalInjuries.setInputTimestamp(new Date());
                NonFatalTransport newNonFatalTransport = new NonFatalTransport();//nuevo non_fatal_transport
                newNonFatalTransport.setNonFatalInjuryId(newNonFatalInjuries.getNonFatalInjuryId());
                NonFatalInterpersonal newNonFatalInterpersonal = new NonFatalInterpersonal();//nuevo non_fatal_Interpersonal
                newNonFatalInterpersonal.setNonFatalInjuryId(newNonFatalInjuries.getNonFatalInjuryId());
                NonFatalSelfInflicted newNonFatalSelfInflicted = new NonFatalSelfInflicted();//nuevo non_fatal_Self-Inflicted
                newNonFatalSelfInflicted.setNonFatalInjuryId(newNonFatalInjuries.getNonFatalInjuryId());
                List<SecurityElements> securityElementList = new ArrayList<SecurityElements>();//lista non_fatal_transport_security_element                
                List<AbuseTypes> abuseTypesesList = new ArrayList<AbuseTypes>();//lista domestic_violence_abuse_type
                List<AggressorTypes> aggressorTypesList = new ArrayList<AggressorTypes>();//lista domestic_violence_aggressor_type                
                List<AnatomicalLocations> anatomicalLocationsList = new ArrayList<AnatomicalLocations>();//lista non_fatal_anatomical_location
                List<KindsOfInjury> kindsOfInjurysList = new ArrayList<KindsOfInjury>();//lista non_fatal_kind_of_injury
                List<Diagnoses> diagnosesList = new ArrayList<Diagnoses>();//lista non_fatal_diagnosis
                List<VulnerableGroups> vulnerableGroupList = new ArrayList<VulnerableGroups>();// lista vector victim_vulnerable_group
                String value;
                String name = "";
                String surname = "";
                String[] value2;
                Date n;
                int hourInt;
                int minuteInt;
                for (int posCol = 0; posCol < columnsNames.length; posCol++) {

                    //DETERMINO QUE VALOR VOY A INGRESAR
                    value = "";
                    value2 = null;
                    //||||||||||||||||||||||||||||||||||||||||||||||||||||||
                    relationVar = currentRelationsGroup.findRelationVar(columnsNames[posCol]);//determino la relacion de variables
                    if (relationVar != null) {
                        switch (DataTypeEnum.convert(relationVar.getFieldType())) {//tipo de relacion
                            case text:
                                value = resultSetFileData.getString(columnsNames[posCol]);
                                break;
                            case integer:
                                value = isNumeric(resultSetFileData.getString(columnsNames[posCol]));
                                break;
                            case age:
                                value2 = isAge(resultSetFileData.getString(columnsNames[posCol]));
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
                            case NOVALUE:
                                value = isCategorical(resultSetFileData.getString(relationVar.getNameFound()), relationVar);
                                break;
                        }
                    }
                    //||||||||||||||||||||||||||||||||||||||||||||||||||||||
                    if (value != null) {
                        if (value.trim().length() != 0) {
                            switch (SCC_F_032Enum.convert(relationVar.getNameExpected())) {
                                // ************************************************DATOS PARA LA TABLA victims
                                case clave:
                                    break;
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
                                    IdTypes selectIdType = idTypesFacade.find(Short.parseShort(value));
                                    newVictim.setTypeId(selectIdType);
                                    break;
                                case nid:
                                    if (value.trim().length() != 0) {
                                        newVictim.setVictimNid(value);
                                        //newNonFatalInjuries.setVictimId(newVictim);
                                    }
                                    break;
                                case medad:

                                    break;
                                case asegu:
                                    Insurance selectInsurance = insuranceFacade.find(Short.parseShort(value));
                                    newVictim.setInsuranceId(selectInsurance);
                                    break;
                                case fnacimiento:
                                    try {
                                        currentDate = dateFormat.parse(value);
                                        newVictim.setVictimDateOfBirth(currentDate);
                                    } catch (ParseException ex) {
                                    }

                                    break;
                                case edadcantid:
                                    if (value.trim().length() != 0) {
                                        newVictim.setVictimAge(Short.parseShort(value));
                                    }
                                    break;
                                case sexo:
                                    Genders selectGender = gendersFacade.find(Short.parseShort(value));
                                    newVictim.setGenderId(selectGender);
                                    break;
                                case ocupa:
                                    Jobs selectJob = jobsFacade.find(value);
                                    newVictim.setJobId(selectJob);
                                    break;
                                case getnico:
                                    EthnicGroups selectEthnicGroup = ethnicGroupsFacade.find(Short.parseShort(value));
                                    newVictim.setEthnicGroupId(selectEthnicGroup);
                                    break;
                                case codigobarr:
                                    newVictim.setVictimNeighborhoodId(neighborhoodsFacade.find(Integer.parseInt(value)));
                                    break;
                                case dirres:
                                    newVictim.setVictimAddress(value);
                                    break;
                                case telres:
                                    newVictim.setVictimTelephone(value);
                                    break;
                                // ************************************************DATOS PARA LA TABLA non_fatal_transport
                                case ttrans:
                                    TransportTypes selectTransportTypes = transportTypesFacade.find(Short.parseShort(value));
                                    newNonFatalTransport.setTransportTypeId(selectTransportTypes);
                                    selectInjury = injuriesFacade.find(Short.parseShort("51"));
                                    newNonFatalInjuries.setInjuryId(selectInjury);
                                    break;
                                case tcontp:
                                    TransportCounterparts selectTransportCounterpart = transportCounterpartsFacade.find(Short.parseShort(value));
                                    newNonFatalTransport.setTransportCounterpartId(selectTransportCounterpart);
                                    selectInjury = injuriesFacade.find(Short.parseShort("51"));
                                    newNonFatalInjuries.setInjuryId(selectInjury);
                                    break;
                                case tusuar:
                                    TransportUsers selectTransportUser = transportUsersFacade.find(Short.parseShort(value));
                                    newNonFatalTransport.setTransportUserId(selectTransportUser);
                                    selectInjury = injuriesFacade.find(Short.parseShort("51"));
                                    newNonFatalInjuries.setInjuryId(selectInjury);
                                    break;
                                // ************************************************DATOS PARA LA TABLA non_fatal_interpersonal
                                case anteca://boleano->previous_antecedent                                    
                                    newNonFatalInterpersonal.setPreviousAntecedent(boolean3Facade.find(Short.parseShort(value)));
                                    selectInjury = injuriesFacade.find(Short.parseShort("50"));
                                    newNonFatalInjuries.setInjuryId(selectInjury);
                                    break;
                                case relacav://categorico->relationships_to_victim
                                    RelationshipsToVictim selectRelationshipsToVictim = relationshipsToVictimFacade.find(Short.parseShort(value));
                                    newNonFatalInterpersonal.setRelationshipVictimId(selectRelationshipsToVictim);
                                    selectInjury = injuriesFacade.find(Short.parseShort("50"));
                                    newNonFatalInjuries.setInjuryId(selectInjury);
                                    break;
                                case contex:
                                    Contexts selectContexts = contextsFacade.find(Short.parseShort(value));
                                    newNonFatalInterpersonal.setContextId(selectContexts);
                                    selectInjury = injuriesFacade.find(Short.parseShort("50"));
                                    newNonFatalInjuries.setInjuryId(selectInjury);
                                    break;
                                case sexa:
                                    AggressorGenders selectAggressorGenders = aggressorGendersFacade.find(Short.parseShort(value));
                                    newNonFatalInterpersonal.setAggressorGenderId(selectAggressorGenders);
                                    selectInjury = injuriesFacade.find(Short.parseShort("50"));
                                    newNonFatalInjuries.setInjuryId(selectInjury);
                                    break;
                                // ************************************************DATOS PARA LA TABLA non_fatal_selft-inflicted
                                case intpre:
                                    if (value.compareTo("TRUE") == 0) {
                                        newNonFatalSelfInflicted.setPreviousAttempt(boolean3Facade.find((short) 1));//si
                                    } else {
                                        newNonFatalSelfInflicted.setPreviousAttempt(boolean3Facade.find((short) 2));//no
                                    }
                                    selectInjury = injuriesFacade.find(Short.parseShort("52"));
                                    newNonFatalInjuries.setInjuryId(selectInjury);
                                    break;
                                case trment:
                                    if (value.compareTo("TRUE") == 0) {
                                        newNonFatalSelfInflicted.setMentalAntecedent(boolean3Facade.find((short) 1));//si
                                    } else {
                                        newNonFatalSelfInflicted.setMentalAntecedent(boolean3Facade.find((short) 2));//no
                                    }
                                    selectInjury = injuriesFacade.find(Short.parseShort("52"));
                                    newNonFatalInjuries.setInjuryId(selectInjury);
                                    break;
                                case fprec:
                                    PrecipitatingFactors selectPrecipitatingFactors = precipitatingFactorsFacade.find(Short.parseShort(value));
                                    newNonFatalSelfInflicted.setPrecipitatingFactorId(selectPrecipitatingFactors);
                                    selectInjury = injuriesFacade.find(Short.parseShort("52"));
                                    newNonFatalInjuries.setInjuryId(selectInjury);
                                    break;
                                // ************************************************DATOS PARA LA TABLA non_fatal_transport_security_element
                                case tsegu:
                                    selectSecurityElement = securityElementsFacade.find(Short.parseShort(value));
                                    securityElementList.add(selectSecurityElement);
                                    selectInjury = injuriesFacade.find(Short.parseShort("51"));
                                    newNonFatalInjuries.setInjuryId(selectInjury);
                                    break;
                                case cintu:
                                    selectSecurityElement = securityElementsFacade.find(Short.parseShort(value));
                                    securityElementList.add(selectSecurityElement);
                                    selectInjury = injuriesFacade.find(Short.parseShort("51"));
                                    newNonFatalInjuries.setInjuryId(selectInjury);
                                    break;
                                case cascom:
                                    selectSecurityElement = securityElementsFacade.find(Short.parseShort(value));
                                    securityElementList.add(selectSecurityElement);
                                    selectInjury = injuriesFacade.find(Short.parseShort("51"));
                                    newNonFatalInjuries.setInjuryId(selectInjury);
                                    break;
                                case cascob:
                                    selectSecurityElement = securityElementsFacade.find(Short.parseShort(value));
                                    securityElementList.add(selectSecurityElement);
                                    selectInjury = injuriesFacade.find(Short.parseShort("51"));
                                    newNonFatalInjuries.setInjuryId(selectInjury);
                                    break;
                                case chale:
                                    selectSecurityElement = securityElementsFacade.find(Short.parseShort(value));
                                    securityElementList.add(selectSecurityElement);
                                    selectInjury = injuriesFacade.find(Short.parseShort("51"));
                                    newNonFatalInjuries.setInjuryId(selectInjury);
                                    break;
                                case otroel:
                                    selectSecurityElement = securityElementsFacade.find(Short.parseShort(value));
                                    securityElementList.add(selectSecurityElement);
                                    selectInjury = injuriesFacade.find(Short.parseShort("51"));
                                    newNonFatalInjuries.setInjuryId(selectInjury);
                                    break;
                                // ************************************************DATOS PARA LA TABLA domestic_violence_abuse_type
                                case ma1:
                                    if (value.compareTo("VERDADERO") == 0) {
                                        AbuseTypes newAbuseType = new AbuseTypes(Short.parseShort("1"));
                                        abuseTypesesList.add(newAbuseType);
                                    }
                                    break;
                                case ma2:
                                    if (value.compareTo("VERDADERO") == 0) {
                                        AbuseTypes newAbuseType = new AbuseTypes(Short.parseShort("2"));
                                        abuseTypesesList.add(newAbuseType);
                                    }
                                    break;
                                case ma3:
                                    if (value.compareTo("VERDADERO") == 0) {
                                        AbuseTypes newAbuseType = new AbuseTypes(Short.parseShort("3"));
                                        abuseTypesesList.add(newAbuseType);
                                    }
                                    break;
                                case ma4:
                                    if (value.compareTo("VERDADERO") == 0) {
                                        AbuseTypes newAbuseType = new AbuseTypes(Short.parseShort("4"));
                                        abuseTypesesList.add(newAbuseType);
                                    }
                                    break;
                                case ma5:
                                    if (value.compareTo("VERDADERO") == 0) {
                                        AbuseTypes newAbuseType = new AbuseTypes(Short.parseShort("5"));
                                        abuseTypesesList.add(newAbuseType);
                                    }
                                    break;
                                case ma6:
                                    if (value.compareTo("VERDADERO") == 0) {
                                        AbuseTypes newAbuseType = new AbuseTypes(Short.parseShort("6"));
                                        abuseTypesesList.add(newAbuseType);
                                    }
                                    break;
                                case ma7:
                                    if (value.compareTo("VERDADERO") == 0) {
                                        AbuseTypes newAbuseType = new AbuseTypes(Short.parseShort("7"));
                                        abuseTypesesList.add(newAbuseType);
                                    }
                                    break;
                                // ************************************************DATOS PARA LA TABLA domestic_violence_aggressor_type
                                case ag1:
                                    if (value.compareTo("VERDADERO") == 0) {
                                        AggressorTypes newAggressorTypes = new AggressorTypes(Short.parseShort("1"));
                                        aggressorTypesList.add(newAggressorTypes);
                                    }
                                    break;
                                case ag2:
                                    if (value.compareTo("VERDADERO") == 0) {
                                        AggressorTypes newAggressorTypes = new AggressorTypes(Short.parseShort("2"));
                                        aggressorTypesList.add(newAggressorTypes);
                                    }
                                    break;
                                case ag3:
                                    if (value.compareTo("VERDADERO") == 0) {
                                        AggressorTypes newAggressorTypes = new AggressorTypes(Short.parseShort("3"));
                                        aggressorTypesList.add(newAggressorTypes);
                                    }
                                    break;
                                case ag4:
                                    if (value.compareTo("VERDADERO") == 0) {
                                        AggressorTypes newAggressorTypes = new AggressorTypes(Short.parseShort("4"));
                                        aggressorTypesList.add(newAggressorTypes);
                                    }
                                    break;
                                case ag5:
                                    if (value.compareTo("VERDADERO") == 0) {
                                        AggressorTypes newAggressorTypes = new AggressorTypes(Short.parseShort("5"));
                                        aggressorTypesList.add(newAggressorTypes);
                                    }
                                    break;
                                case ag6:
                                    if (value.compareTo("VERDADERO") == 0) {
                                        AggressorTypes newAggressorTypes = new AggressorTypes(Short.parseShort("6"));
                                        aggressorTypesList.add(newAggressorTypes);
                                    }
                                    break;
                                case ag7:
                                    if (value.compareTo("VERDADERO") == 0) {
                                        AggressorTypes newAggressorTypes = new AggressorTypes(Short.parseShort("7"));
                                        aggressorTypesList.add(newAggressorTypes);
                                    }
                                    break;
                                case ag8:
                                    if (value.compareTo("VERDADERO") == 0) {
                                        AggressorTypes newAggressorTypes = new AggressorTypes(Short.parseShort("8"));
                                        aggressorTypesList.add(newAggressorTypes);
                                    }
                                    break;
                                case ag9:
                                    if (value.compareTo("VERDADERO") == 0) {
                                        AggressorTypes newAggressorTypes = new AggressorTypes(Short.parseShort("9"));
                                        aggressorTypesList.add(newAggressorTypes);
                                    }
                                    break;
                                // ************************************************DATOS PARA LA TABLA non_fatal_anatomical_location
                                case sistem:
                                    if (value.compareTo("VERDADERO") == 0) {
                                        AnatomicalLocations newAnatomicalLocation = new AnatomicalLocations(Short.parseShort("1"));
                                        anatomicalLocationsList.add(newAnatomicalLocation);
                                    }
                                    break;
                                case craneo:
                                    if (value.compareTo("VERDADERO") == 0) {
                                        AnatomicalLocations newAnatomicalLocation = new AnatomicalLocations(Short.parseShort("2"));
                                        anatomicalLocationsList.add(newAnatomicalLocation);
                                    }
                                    break;
                                case ojos:
                                    if (value.compareTo("VERDADERO") == 0) {
                                        AnatomicalLocations newAnatomicalLocation = new AnatomicalLocations(Short.parseShort("3"));
                                        anatomicalLocationsList.add(newAnatomicalLocation);
                                    }
                                    break;
                                case maxilof:
                                    if (value.compareTo("VERDADERO") == 0) {
                                        AnatomicalLocations newAnatomicalLocation = new AnatomicalLocations(Short.parseShort("4"));
                                        anatomicalLocationsList.add(newAnatomicalLocation);
                                    }
                                    break;
                                case cuello:
                                    if (value.compareTo("VERDADERO") == 0) {
                                        AnatomicalLocations newAnatomicalLocation = new AnatomicalLocations(Short.parseShort("5"));
                                        anatomicalLocationsList.add(newAnatomicalLocation);
                                    }
                                    break;
                                case torax:
                                    if (value.compareTo("VERDADERO") == 0) {
                                        AnatomicalLocations newAnatomicalLocation = new AnatomicalLocations(Short.parseShort("6"));
                                        anatomicalLocationsList.add(newAnatomicalLocation);
                                    }
                                    break;
                                case abdomen:
                                    if (value.compareTo("VERDADERO") == 0) {
                                        AnatomicalLocations newAnatomicalLocation = new AnatomicalLocations(Short.parseShort("7"));
                                        anatomicalLocationsList.add(newAnatomicalLocation);
                                    }
                                    break;
                                case columna:
                                    if (value.compareTo("VERDADERO") == 0) {
                                        AnatomicalLocations newAnatomicalLocation = new AnatomicalLocations(Short.parseShort("8"));
                                        anatomicalLocationsList.add(newAnatomicalLocation);
                                    }
                                    break;
                                case pelvis:
                                    if (value.compareTo("VERDADERO") == 0) {
                                        AnatomicalLocations newAnatomicalLocation = new AnatomicalLocations(Short.parseShort("9"));
                                        anatomicalLocationsList.add(newAnatomicalLocation);
                                    }
                                    break;
                                case msup:
                                    if (value.compareTo("VERDADERO") == 0) {
                                        AnatomicalLocations newAnatomicalLocation = new AnatomicalLocations(Short.parseShort("10"));
                                        anatomicalLocationsList.add(newAnatomicalLocation);
                                    }
                                    break;
                                case minf:
                                    if (value.compareTo("VERDADERO") == 0) {
                                        AnatomicalLocations newAnatomicalLocation = new AnatomicalLocations(Short.parseShort("11"));//Short.parseShort("1")

                                        anatomicalLocationsList.add(newAnatomicalLocation);
                                    }
                                    break;
                                case ositio:
                                    if (value.compareTo("VERDADERO") == 0) {
                                        AnatomicalLocations newAnatomicalLocation = new AnatomicalLocations(Short.parseShort("98"));//Short.parseShort("1")                                
                                        anatomicalLocationsList.add(newAnatomicalLocation);
                                    }
                                    break;
                                // ************************************************DATOS PARA LA TABLA non_fatal_kind_of_injury
                                case lacera:
                                    if (value.compareTo("VERDADERO") == 0) {
                                        KindsOfInjury newKindsOfInjury = new KindsOfInjury(Short.parseShort("1"));
                                        kindsOfInjurysList.add(newKindsOfInjury);
                                    }
                                    break;
                                case cortada:
                                    if (value.compareTo("VERDADERO") == 0) {
                                        KindsOfInjury newKindsOfInjury = new KindsOfInjury(Short.parseShort("2"));
                                        kindsOfInjurysList.add(newKindsOfInjury);
                                    }
                                    break;
                                case lesprof:
                                    if (value.compareTo("VERDADERO") == 0) {
                                        KindsOfInjury newKindsOfInjury = new KindsOfInjury(Short.parseShort("3"));
                                        kindsOfInjurysList.add(newKindsOfInjury);
                                    }
                                    break;
                                case esglux:
                                    if (value.compareTo("VERDADERO") == 0) {
                                        KindsOfInjury newKindsOfInjury = new KindsOfInjury(Short.parseShort("4"));
                                        kindsOfInjurysList.add(newKindsOfInjury);
                                    }
                                    break;
                                case fractura:
                                    if (value.compareTo("VERDADERO") == 0) {
                                        KindsOfInjury newKindsOfInjury = new KindsOfInjury(Short.parseShort("5"));
                                        kindsOfInjurysList.add(newKindsOfInjury);
                                    }
                                    break;
                                case quemadur:
                                    if (value.compareTo("VERDADERO") == 0) {
                                        KindsOfInjury newKindsOfInjury = new KindsOfInjury(Short.parseShort("6"));
                                        kindsOfInjurysList.add(newKindsOfInjury);
                                    }
                                    break;
                                case contusi:
                                    if (value.compareTo("VERDADERO") == 0) {
                                        KindsOfInjury newKindsOfInjury = new KindsOfInjury(Short.parseShort("7"));
                                        kindsOfInjurysList.add(newKindsOfInjury);
                                    }
                                    break;
                                case orgsist:
                                    if (value.compareTo("VERDADERO") == 0) {
                                        KindsOfInjury newKindsOfInjury = new KindsOfInjury(Short.parseShort("8"));
                                        kindsOfInjurysList.add(newKindsOfInjury);
                                    }
                                    break;
                                case tce:
                                    if (value.compareTo("VERDADERO") == 0) {
                                        KindsOfInjury newKindsOfInjury = new KindsOfInjury(Short.parseShort("9"));
                                        kindsOfInjurysList.add(newKindsOfInjury);
                                    }
                                    break;
                                case olesi:
                                    if (value.compareTo("VERDADERO") == 0) {
                                        KindsOfInjury newKindsOfInjury = new KindsOfInjury(Short.parseShort("10"));
                                        kindsOfInjurysList.add(newKindsOfInjury);
                                    }
                                    break;
                                case nosabe:
                                    if (value.compareTo("VERDADERO") == 0) {
                                        KindsOfInjury newKindsOfInjury = new KindsOfInjury(Short.parseShort("11"));
                                        kindsOfInjurysList.add(newKindsOfInjury);
                                    }
                                    break;
                                // ************************************************DATOS PARA LA TABLA non_fatal_diagnosis
                                case cie10:
                                    //if (resultSetFileData.getString(columnsNames[i]).length() != 0) {

                                    selectDiagnoses = diagnosesFacade.find(value);
                                    diagnosesList.add(selectDiagnoses);
                                    //}
                                    break;
                                case cie102:
                                    //if (resultSetFileData.getString(columnsNames[i]).length() != 0) {
                                    selectDiagnoses = diagnosesFacade.find(value);
                                    diagnosesList.add(selectDiagnoses);
                                    //}
                                    break;
                                case cie103:
                                    //if (resultSetFileData.getString(columnsNames[i]).length() != 0) {
                                    selectDiagnoses = diagnosesFacade.find(value);
                                    diagnosesList.add(selectDiagnoses);
                                    //}
                                    break;
                                case cie104:
                                    //if (resultSetFileData.getString(columnsNames[i]).length() != 0) {
                                    selectDiagnoses = diagnosesFacade.find(value);
                                    diagnosesList.add(selectDiagnoses);
                                    //}
                                    break;
                                // ************************************************DATOS PARA LA TABLA victim_vulnerable_group
                                case despl:
                                    //if (resultSetFileData.getString(columnsNames[i]).length() != 0) {
                                    selectVulnerableGroups = vulnerableGroupsFacade.find(Short.parseShort(value));
                                    vulnerableGroupList.add(selectVulnerableGroups);
                                    //}
                                    break;
                                case disca:
                                    //if (resultSetFileData.getString(columnsNames[i]).length() != 0) {
                                    selectVulnerableGroups = vulnerableGroupsFacade.find(Short.parseShort(value));
                                    vulnerableGroupList.add(selectVulnerableGroups);
                                    //}
                                    break;
                                // ************************************************DATOS PARA LA TABLA non_fatal_injuries
                                case instisal:
                                    //NonFatalDataSources selectNonFatalDataSource = nonFatalDataSourcesFacade.findByName(Integer.parseInt(resultSetFileData.getString(columnsNames[i])));
                                    NonFatalDataSources selectNonFatalDataSource = nonFatalDataSourcesFacade.findByName(value);
                                    newNonFatalInjuries.setNonFatalDataSourceId(selectNonFatalDataSource);
                                    break;
                                case codbar:
                                    Neighborhoods selectNeighborhoods = neighborhoodsFacade.find(Integer.parseInt(value));
                                    newNonFatalInjuries.setInjuryNeighborhoodId(neighborhoodsFacade.find(selectNeighborhoods.getNeighborhoodId()));
                                    break;
                                case direv:
                                    newNonFatalInjuries.setInjuryAddress(value);
                                    break;
                                case fechaev:
                                    try {
                                        currentDate = dateFormat.parse(value);
                                        newNonFatalInjuries.setInjuryDate(currentDate);
                                    } catch (ParseException ex) {
                                    }
                                    break;
                                case diaev:
                                    newNonFatalInjuries.setInjuryDayOfWeek(value);
                                    break;
                                case horaev:
                                    n = new Date();
                                    hourInt = Integer.parseInt(value.substring(0, 1));
                                    minuteInt = Integer.parseInt(value.substring(2, 3));
                                    n.setHours(hourInt);
                                    n.setMinutes(minuteInt);
                                    n.setSeconds(0);
                                    newNonFatalInjuries.setInjuryTime(n);
                                    break;
                                case fechacon:
                                    strDate = value;
                                    currentDate = null;
                                    try {
                                        currentDate = dateFormat.parse(strDate);
                                        newNonFatalInjuries.setCheckupDate(currentDate);
                                    } catch (ParseException ex) {
                                        newNonFatalInjuries.setCheckupDate(currentDate);
                                    }
                                    break;
                                case horacon:
                                    n = new Date();
                                    hourInt = Integer.parseInt(value.substring(0, 1));
                                    minuteInt = Integer.parseInt(value.substring(2, 3));
                                    n.setHours(hourInt);
                                    n.setMinutes(minuteInt);
                                    n.setSeconds(0);
                                    newNonFatalInjuries.setInjuryTime(n);
                                    break;
                                case remitido:
                                    if (value.compareTo("TRUE") == 0) {
                                        newNonFatalInjuries.setSubmittedPatient(true);
                                    } else {
                                        newNonFatalInjuries.setSubmittedPatient(false);
                                    }
                                    break;
                                case deqips:

                                    break;
                                case intenci:
                                    Intentionalities selectIntentionalities = intentionalitiesFacade.find(Short.parseShort(value));
                                    newNonFatalInjuries.setIntentionalityId(selectIntentionalities);
                                    break;
                                case lugar:
                                    NonFatalPlaces selectNonFatalPlaces = nonFatalPlacesFacade.find(Short.parseShort(value));
                                    newNonFatalInjuries.setInjuryPlaceId(selectNonFatalPlaces);
                                    break;
                                case activi:
                                    //Activities selectActivities = activitiesFacade.find(Short.parseShort(value));
                                    //newNonFatalInjuries.setActivityId(selectActivities);
                                    break;
                                case mecobj:
                                    Mechanisms selectMechanisms = mechanismsFacade.find(Short.parseShort(value));
                                    newNonFatalInjuries.setMechanismId(selectMechanisms);
                                    break;
                                case alcohol:
                                    selectUseAlcoholDrugs = useAlcoholDrugsFacade.find(Short.parseShort(value));
                                    newNonFatalInjuries.setUseAlcoholId(selectUseAlcoholDrugs);
                                    break;
                                case drogas:
                                    selectUseAlcoholDrugs = useAlcoholDrugsFacade.find(Short.parseShort(value));
                                    newNonFatalInjuries.setUseDrugsId(selectUseAlcoholDrugs);
                                    break;
                                case gradogra:
                                    //if (resultSetFileData.getString(columnsNames[i]).length() != 0) {
                                    newNonFatalInjuries.setBurnInjuryDegree(Short.parseShort(value));
                                    //} else {
                                    //ESTO NO VA HASTA COMPLETAR(O COLOCAR EN NULL)
                                    //    newNonFatalInjuries.setBurnInjuryDegree(Short.parseShort("0"));
                                    // }
                                    break;
                                case porcent:
                                    //if (resultSetFileData.getString(columnsNames[i]).length() != 0) {
                                    newNonFatalInjuries.setBurnInjuryPercentage(Short.parseShort(value));
                                    //} else {
                                    //ESTO NO VA HASTA COMPLETAR(O COLOCAR EN NULL)
                                    //    newNonFatalInjuries.setBurnInjuryPercentage(Short.parseShort("0"));
                                    //}
                                    break;
                                case destino:
                                    DestinationsOfPatient selectDestinationsOfPatient = destinationsOfPatientFacade.find(Short.parseShort(value));
                                    newNonFatalInjuries.setDestinationPatientId(selectDestinationsOfPatient);
                                    break;

                                default:
                            }
                        }
                    }
                }

                name = name + " " + surname;
                if (name.trim().length() > 1) {
                    newVictim.setVictimName(name);
                }


                victimsFacade.create(newVictim);//PERSISTO LA VICTIMA


                //newNonFatalInjuries.setAnatomicalLocationsList(anatomicalLocationsList);

                // ************************************************ vector non_fatal_transport_security_element
                //securityElementList
                // ************************************************ vector domestic_violence_abuse_type
                //abuseTypesesList
                // ************************************************ vector domestic_violence_aggressor_type
                //aggressorTypesList
                // ************************************************ vector non_fatal_kind_of_injury                
                //newNonFatalInjuries.setKindsOfInjuryList(kindsOfInjurysList);
                // ************************************************ vector non_fatal_diagnosis
                //newNonFatalInjuries.setDiagnosesList(diagnosesList);
                // ************************************************ vector victim_vulnerable_group
                //vulnerableGroupList

                //nonFatalInjuriesFacade.create(newNonFatalInjuries);

                tuplesProcessed++;
                progress = (int) (tuplesProcessed * 100) / tuplesNumber;
                System.out.println("PROGRESO: =" + String.valueOf(progress));

//                if (newNonFatalInjuries.getInjuryId().getInjuryId().compareTo(Short.parseShort("50")) == 0) {
//                    //********************************************EL TIPO DE LESION VIOLENCIA INTERPERSONAL
//                    nonFatalInjuriesFacade.create(newNonFatalInjuries);
//                } else if (newNonFatalInjuries.getInjuryId().getInjuryId().compareTo(Short.parseShort("51")) == 0) {
//                    //********************************************EL TIPO DE LESION ES ACCIDENTE DE TRANSITO 
//                    nonFatalInjuriesFacade.create(newNonFatalInjuries);
//                } else if (newNonFatalInjuries.getInjuryId().getInjuryId().compareTo(Short.parseShort("52")) == 0) {
//                    //********************************************EL TIPO DE LESION ES INTENCIONAL AUTOINFLINGIDA 
//                    nonFatalInjuriesFacade.create(newNonFatalInjuries);
//                } else if (newNonFatalInjuries.getInjuryId().getInjuryId().compareTo(Short.parseShort("53")) == 0) {
//                    //********************************************EL TIPO DE LESION ES VIOLENCIA INTRAFAMILIAR 
//                    nonFatalInjuriesFacade.create(newNonFatalInjuries);
//                } else if (newNonFatalInjuries.getInjuryId().getInjuryId().compareTo(Short.parseShort("54")) == 0) {
//                    //********************************************EL TIPO DE LESION ES NO INTENCIONAL 
//                    nonFatalInjuriesFacade.create(newNonFatalInjuries);
//                }
            }
            progress = 100;
            System.out.println("PROGRESO: =" + String.valueOf(progress));
            conx.disconnect();
        } catch (SQLException ex) {
            System.out.println("error: " + ex.toString());
            conx.disconnect();
        }
    }

    public void registerSCC_F_033() {
    }

    public void btnRegisterDataClick() {
        if (nameForm.compareTo("SCC-F-028") == 0) {
            registerSCC_F_028();
        }
        if (nameForm.compareTo("SCC-F-029") == 0) {
            registerSCC_F_029();
        }
        if (nameForm.compareTo("SCC-F-030") == 0) {
            registerSCC_F_030();
        }
        if (nameForm.compareTo("SCC-F-031") == 0) {
            registerSCC_F_031();
        }
        if (nameForm.compareTo("SCC-F-032") == 0) {
            registerSCC_F_032();
        }
        if (nameForm.compareTo("SCC-F-033") == 0) {
            registerSCC_F_033();
        }
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //VALIDACIONES ---------------------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
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

    private String isDate(String fecha, String format) {
        /*
         * validacion de si un string es una fecha de un determinado formto
         * null=invalido ""=aceptado pero vacio "valor"=aceptado y me dice el
         * valor
         */
        if (fecha.trim().length() == 0) {
            return "";
        }
        SimpleDateFormat formato = new SimpleDateFormat(format);
        Date fechaDate;
        String fechaStr;
        try {
            fechaDate = formato.parse(fecha);
            formato = new SimpleDateFormat("yyyy-MM-dd");
            fechaStr = formato.format(fechaDate);
            return fechaStr;
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
                int h = Integer.parseInt(str.substring(0, 1));
                int m = Integer.parseInt(str.substring(2, 3));
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

    private String[] isAge(String str) {
        /*
         * validacion de si un string es numero entero o edad definida en meses
         * y años null = invalido "" = aceptado pero vacio "valor" = aceptado y
         * me dice el valor
         */
        String[] splitAge;
        if (str.trim().length() == 0) {
            splitAge = new String[1];
            splitAge[0] = "";
            return splitAge;
        }
        try {//intento convertirlo en entero
            int a = Integer.parseInt(str);
            splitAge = new String[1];
            splitAge[0] = str;
            return splitAge;
        } catch (Exception ex) {
        }
        try {//determinar si esta definida en años meses
            splitAge = str.split(" ");
            if (splitAge.length == 4) {
                int m = Integer.parseInt(splitAge[0]);
                int y = Integer.parseInt(splitAge[2]);
                splitAge = new String[2];
                splitAge[0] = String.valueOf(y);
                splitAge[1] = String.valueOf(m);
                return splitAge;
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
    //CLIK SOBRE BOTONOES --------------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
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
}
