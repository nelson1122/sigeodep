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
    private ConnectionJDBC conx = null;//conexion sin persistencia a postgres   
    private RelationVar relationVar;
    //private Field fieldExepted;
    //private String type;
    //private String error;
    private String nameForm;
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
    BooleanPojoFacade booleanPojoFacade;
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
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //MANEJO E LA BARRA DE PROGRESO DEL ALMACENAMIENTO ---------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    private Integer progress;

    public Integer getProgress() {
        /*
         * if (progress == null) { progress = 0; } else { progress = progress +
         * 30 + (int) (Math.random() * 30); if (progress > 100) { progress =
         * 100; } }
         */
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public void onComplete() {
        //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se ha realizado la adición de " + String.valueOf(tuplesProcessed)
        //	+ "registros, para filalizar guarde si lo desea la configuración de relaciones actual o reinicie para realizar la carga de registros de otro acrchivo"));
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
        progress = null;
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
            resultSetFileData = conx.consult("SELECT * FROM temp ORDER BY id; ");//resultSetFileData contendra todos los registros de el archivo(tabla temp)
            resultSetFileData.next();
            progress = 0;
            currentRelationsGroup = relationshipOfVariablesMB.getCurrentRelationsGroup();//tomo el grupo de relaciones de valores y de variables

            int columnsNumber = resultSetFileData.getMetaData().getColumnCount();
            int errorsNumber = 0;
            int pos = 0;
            columnsNames = new String[columnsNumber];//creo un arreglo con los nombres de las columnas
            for (int i = 1; i <= columnsNumber; i++) {
                columnsNames[pos] = resultSetFileData.getMetaData().getColumnName(i);
                pos++;
            }
            errorsControlMB.setErrorControlArrayList(new ArrayList<ErrorControl>());//arreglo de errores            
            int count = 1;
            do {//recorro cada uno de los registros de la tabla temp                                                
                for (int i = 0; i < columnsNames.length; i++) {//recorro cada una de las columnas de cada registro                    
                    relationVar = currentRelationsGroup.findRelationVar(columnsNames[i]);//determino la relacion de variables
                    if (relationVar != null) {
                        switch (DataTypeEnum.convert(relationVar.getFieldType())) {//tipo de relacion
                            case text:
                                break;
                            case integer:
                                if (!isNumeric(resultSetFileData.getString(columnsNames[i]))) {
                                    errorsNumber++;//error = "No es entero";
                                    errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), String.valueOf(count), "integer"));
                                }
                                break;
                            case age:
                                if (!isAge(resultSetFileData.getString(columnsNames[i]))) {
                                    errorsNumber++;//error = "fecha no corresponde al formato";
                                    errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), String.valueOf(count), "age"));
                                }
                                break;
                            case date:
                                if (!isDate(resultSetFileData.getString(columnsNames[i]), relationVar.getDateFormat())) {
                                    errorsNumber++;//error = "fecha no corresponde al formato";
                                    errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), String.valueOf(count), "date"));
                                }
                                break;
                            case military:
                                errorStr = isMilitary(resultSetFileData.getString(columnsNames[i]));
                                if (errorStr != null) {
                                    errorsNumber++;//la hora militar no puede ser determinada
                                    errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), String.valueOf(count), "military"));
                                }
                                break;
                            case hour:
                                if (!isHour(resultSetFileData.getString(columnsNames[i]))) {
                                    errorsNumber++;//la hora militar no puede ser determinada
                                    errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), String.valueOf(count), "hour"));
                                }
                                break;
                            case minute:
                                if (!isMinute(resultSetFileData.getString(columnsNames[i]))) {
                                    errorsNumber++;//la hora militar no puede ser determinada
                                    errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), String.valueOf(count), "minute"));
                                }
                                break;
                            case day:
                                if (!isDay(resultSetFileData.getString(columnsNames[i]))) {
                                    errorsNumber++;//la hora militar no puede ser determinada
                                    errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), String.valueOf(count), "day"));
                                }
                                break;
                            case month:
                                if (!isMonth(resultSetFileData.getString(columnsNames[i]))) {
                                    errorsNumber++;//la hora militar no puede ser determinada
                                    errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), String.valueOf(count), "month"));
                                }
                                break;
                            case year:
                                if (!isYear(resultSetFileData.getString(columnsNames[i]))) {
                                    errorsNumber++;//la hora militar no puede ser determinada
                                    errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), String.valueOf(count), "year"));
                                }
                                break;
                            case NOVALUE:
                                if (!isCategorical(
                                        resultSetFileData.getString(relationVar.getNameFound()), relationVar.getNameExpected(),
                                        relationVar.getTypeComparisonForCode(), relationVar.getRelationValueList())) {
                                    //verifico si el valor se encuentra dentro de los valores descartados
                                    if (!isDiscarded(resultSetFileData.getString(columnsNames[i]), relationVar.getDiscardedValues())) {
                                        errorsNumber++;//error = "no esta en la categoria ni es un valor descartado";
                                        errorsControlMB.addError(
                                                new ErrorControl(
                                                relationVar,
                                                resultSetFileData.getString(relationVar.getNameFound()),
                                                String.valueOf(count),
                                                formsAndFieldsDataMB.variableDescription(relationVar.getNameExpected())));
                                    }

                                }
                                break;
                        }
                    }
                }
                count++;
            } while (resultSetFileData.next());
            errorsControlMB.setSizeErrorsList(errorsNumber);
            errorsControlMB.updateErrorsArrayList();
            progress = 100;
            conx.disconnect();
            if (errorsNumber != 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Errores", "Existen: " + String.valueOf(errorsNumber) + " valores que no superaron el proceso de validación, dirijase a la sección de errores para  especificar si los corrige o ignora."));
            } else {
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
        SimpleDateFormat textFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate;
        SimpleDateFormat formatoDelTexto;
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
            resultSetFileData.next();
            progress = 0;
            //creo un arreglo con los nombres de las columnas
            int columnsNumber = resultSetFileData.getMetaData().getColumnCount();
            columnsNames = new String[columnsNumber];
            int pos = 0;
            for (int i = 1; i <= columnsNumber; i++) {
                columnsNames[pos] = resultSetFileData.getMetaData().getColumnName(i);
                pos++;
            }
            do {//recorro cada uno de los registros de la tabla temp

                //***********************************************************creo una nueva victima
                VulnerableGroups vulnerableGroups = vulnerableGroupsFacade.find(Short.parseShort("1"));

                Victims newVictim = new Victims();
                newVictim.setVictimId(victimsFacade.findMax() + 1);

                List<VulnerableGroups> vulnerableGroupsList = new ArrayList<VulnerableGroups>();
                vulnerableGroupsList.add(vulnerableGroups);
                newVictim.setVulnerableGroupsList(vulnerableGroupsList);
                newVictim.setVictimClass(Short.parseShort("1"));
                try {
                    currentDate = textFormat.parse("2012-01-01");
                    newVictim.setVictimDateOfBirth(currentDate);
                } catch (ParseException ex) {
                }
                //*****************************************************creo un nuevo lesiones_no_fatales
                int MaxIdNFI = nonFatalInjuriesFacade.findMax();
                NonFatalInjuries newNonFatalInjuries = new NonFatalInjuries();
                newNonFatalInjuries.setNonFatalInjuryId(MaxIdNFI + 1);
                Injuries selectInjuries = injuriesFacade.find(Short.parseShort("54"));
                newNonFatalInjuries.setInjuryId(selectInjuries);
                newNonFatalInjuries.setInputTimestamp(new Date());

                //*****************************************************creo un nuevo non_fatal_transport
                NonFatalTransport newNonFatalTransport = new NonFatalTransport();
                newNonFatalTransport.setNonFatalInjuryId(newNonFatalInjuries.getNonFatalInjuryId());

                //*****************************************************creo un nuevo non_fatal_Interpersonal
                NonFatalInterpersonal newNonFatalInterpersonal = new NonFatalInterpersonal();
                newNonFatalInterpersonal.setNonFatalInjuryId(newNonFatalInjuries.getNonFatalInjuryId());

                //*****************************************************creo un nuevo non_fatal_Self-Inflicted
                NonFatalSelfInflicted newNonFatalSelfInflicted = new NonFatalSelfInflicted();
                newNonFatalSelfInflicted.setNonFatalInjuryId(newNonFatalInjuries.getNonFatalInjuryId());

                // ************************************************ vector non_fatal_transport_security_element
                List<SecurityElements> securityElementList = new ArrayList<SecurityElements>();

                // ************************************************ vector domestic_violence_abuse_type
                List<AbuseTypes> abuseTypesesList = new ArrayList<AbuseTypes>();

                // ************************************************ vector domestic_violence_aggressor_type
                List<AggressorTypes> aggressorTypesList = new ArrayList<AggressorTypes>();

                // ************************************************ vector non_fatal_anatomical_location
                List<AnatomicalLocations> anatomicalLocationsList = new ArrayList<AnatomicalLocations>();

                // ************************************************ vector non_fatal_kind_of_injury
                List<KindsOfInjury> kindsOfInjurysList = new ArrayList<KindsOfInjury>();

                // ************************************************ vector non_fatal_diagnosis
                List<Diagnoses> diagnosesList = new ArrayList<Diagnoses>();

                // ************************************************ vector victim_vulnerable_group
                List<VulnerableGroups> vulnerableGroupList = new ArrayList<VulnerableGroups>();
                for (int i = 0; i < columnsNames.length; i++) {
                    //AQUI SE DETERMINA QUE COLUMNA LLEGA Y QUE HACER CON ELLA
                    if (resultSetFileData.getString(columnsNames[i]).length() != 0) //validacion de que no sea vacio
                    {
                        switch (SCC_F_032Enum.convert(columnsNames[i])) {
                            // ************************************************DATOS PARA LA TABLA victims
                            case clave:
                                break;
                            case nombre1:
                                if (newVictim.getVictimName() != null) {
                                    if (newVictim.getVictimName().trim().length() != 0) {
                                        newVictim.setVictimName(newVictim.getVictimName() + " " + resultSetFileData.getString(columnsNames[i]));
                                    } else {
                                        newVictim.setVictimName(resultSetFileData.getString(columnsNames[i]));
                                    }
                                } else {
                                    newVictim.setVictimName(resultSetFileData.getString(columnsNames[i]));
                                }
                                break;
                            case nombre2:
                                if (newVictim.getVictimName() != null) {
                                    if (newVictim.getVictimName().trim().length() != 0) {
                                        newVictim.setVictimName(newVictim.getVictimName() + " " + resultSetFileData.getString(columnsNames[i]));
                                    } else {
                                        newVictim.setVictimName(resultSetFileData.getString(columnsNames[i]));
                                    }
                                } else {
                                    newVictim.setVictimName(resultSetFileData.getString(columnsNames[i]));
                                }
                                break;
                            case apellid1:
                                if (newVictim.getVictimName() != null) {
                                    if (newVictim.getVictimName().trim().length() != 0) {
                                        newVictim.setVictimName(newVictim.getVictimName() + " " + resultSetFileData.getString(columnsNames[i]));
                                    } else {
                                        newVictim.setVictimName(resultSetFileData.getString(columnsNames[i]));
                                    }
                                } else {
                                    newVictim.setVictimName(resultSetFileData.getString(columnsNames[i]));
                                }
                                break;
                            case apellid2:
                                if (newVictim.getVictimName() != null) {
                                    if (newVictim.getVictimName().trim().length() != 0) {
                                        newVictim.setVictimName(newVictim.getVictimName() + " " + resultSetFileData.getString(columnsNames[i]));
                                    } else {
                                        newVictim.setVictimName(resultSetFileData.getString(columnsNames[i]));
                                    }
                                } else {
                                    newVictim.setVictimName(resultSetFileData.getString(columnsNames[i]));
                                }
                                break;
                            case tid:
                                IdTypes selectIdType = idTypesFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                newVictim.setTypeId(selectIdType);
                                break;
                            case nid:
                                newVictim.setVictimNid(resultSetFileData.getString(columnsNames[i]));
                                newNonFatalInjuries.setVictimId(newVictim);
                                break;
                            case medad:
                                newVictim.setAgeTypeId(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                break;
                            case edadcantid:
                                newVictim.setVictimAge(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                break;
                            case sexo:
                                Genders selectGender = gendersFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                newVictim.setGenderId(selectGender);
                                break;
                            case ocupa:
                                Jobs selectJob = jobsFacade.find(Short.parseShort("1"));
                                newVictim.setJobId(selectJob);
                                break;
                            case getnico:
                                EthnicGroups selectEthnicGroup = ethnicGroupsFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                newVictim.setEthnicGroupId(selectEthnicGroup);
                                break;
                            case codigobarr:
                                newVictim.setVictimNeighborhoodId(neighborhoodsFacade.find(Integer.parseInt(resultSetFileData.getString(columnsNames[i]))));
                                break;
                            case dirres:
                                newVictim.setVictimAddress(resultSetFileData.getString(columnsNames[i]));
                                break;
                            case telres:
                                newVictim.setVictimTelephone(resultSetFileData.getString(columnsNames[i]));
                                break;
                            // ************************************************DATOS PARA LA TABLA non_fatal_transport
                            case ttrans:
                                TransportTypes selectTransportTypes = transportTypesFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                newNonFatalTransport.setTransportTypeId(selectTransportTypes);
                                selectInjuries = injuriesFacade.find(Short.parseShort("51"));
                                newNonFatalInjuries.setInjuryId(selectInjuries);
                                break;
                            case tcontp:
                                TransportCounterparts selectTransportCounterpart = transportCounterpartsFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                newNonFatalTransport.setTransportCounterpartId(selectTransportCounterpart);
                                selectInjuries = injuriesFacade.find(Short.parseShort("51"));
                                newNonFatalInjuries.setInjuryId(selectInjuries);
                                break;
                            case tusuar:
                                TransportUsers selectTransportUser = transportUsersFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                newNonFatalTransport.setTransportUserId(selectTransportUser);
                                selectInjuries = injuriesFacade.find(Short.parseShort("51"));
                                newNonFatalInjuries.setInjuryId(selectInjuries);
                                break;
                            // ************************************************DATOS PARA LA TABLA non_fatal_interpersonal
                            case anteca://boleano->previous_antecedent
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("TRUE") == 0) {
                                    newNonFatalInterpersonal.setPreviousAntecedent(booleanPojoFacade.find((short) 1));
                                } else {
                                    if (resultSetFileData.getString(columnsNames[i]).compareTo("FALSE") == 0) {
                                        newNonFatalInterpersonal.setPreviousAntecedent(booleanPojoFacade.find((short) 2));
                                    } else {
                                        //sin dato
                                        //newNonFatalInterpersonal.setPreviousAntecedent(null);
                                    }

                                }
                                selectInjuries = injuriesFacade.find(Short.parseShort("50"));
                                newNonFatalInjuries.setInjuryId(selectInjuries);
                                //}
                                break;
                            case relacav://categorico->relationships_to_victim
                                RelationshipsToVictim selectRelationshipsToVictim = relationshipsToVictimFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                newNonFatalInterpersonal.setRelationshipVictimId(selectRelationshipsToVictim);
                                selectInjuries = injuriesFacade.find(Short.parseShort("50"));
                                newNonFatalInjuries.setInjuryId(selectInjuries);
                                break;
                            case contex:
                                Contexts selectContexts = contextsFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                newNonFatalInterpersonal.setContextId(selectContexts);
                                selectInjuries = injuriesFacade.find(Short.parseShort("50"));
                                newNonFatalInjuries.setInjuryId(selectInjuries);
                                break;
                            case sexa:
                                AggressorGenders selectAggressorGenders = aggressorGendersFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                newNonFatalInterpersonal.setAggressorGenderId(selectAggressorGenders);
                                selectInjuries = injuriesFacade.find(Short.parseShort("50"));
                                newNonFatalInjuries.setInjuryId(selectInjuries);
                                break;
                            // ************************************************DATOS PARA LA TABLA non_fatal_selft-inflicted
                            case intpre:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("TRUE") == 0) {
                                    newNonFatalSelfInflicted.setPreviousAttempt(booleanPojoFacade.find((short) 1));//si
                                } else {
                                    newNonFatalSelfInflicted.setPreviousAttempt(booleanPojoFacade.find((short) 2));//no
                                }
                                selectInjuries = injuriesFacade.find(Short.parseShort("52"));
                                newNonFatalInjuries.setInjuryId(selectInjuries);
                                break;
                            case trment:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("TRUE") == 0) {
                                    newNonFatalSelfInflicted.setMentalAntecedent(booleanPojoFacade.find((short) 1));//si
                                } else {
                                    newNonFatalSelfInflicted.setMentalAntecedent(booleanPojoFacade.find((short) 2));//no
                                }
                                selectInjuries = injuriesFacade.find(Short.parseShort("52"));
                                newNonFatalInjuries.setInjuryId(selectInjuries);
                                break;
                            case fprec:
                                PrecipitatingFactors selectPrecipitatingFactors = precipitatingFactorsFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                newNonFatalSelfInflicted.setPrecipitatingFactorId(selectPrecipitatingFactors);
                                selectInjuries = injuriesFacade.find(Short.parseShort("52"));
                                newNonFatalInjuries.setInjuryId(selectInjuries);
                                break;
                            // ************************************************DATOS PARA LA TABLA non_fatal_transport_security_element
                            case tsegu:
                                selectSecurityElement = securityElementsFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                securityElementList.add(selectSecurityElement);
                                selectInjuries = injuriesFacade.find(Short.parseShort("51"));
                                newNonFatalInjuries.setInjuryId(selectInjuries);
                                break;
                            case cintu:
                                selectSecurityElement = securityElementsFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                securityElementList.add(selectSecurityElement);
                                selectInjuries = injuriesFacade.find(Short.parseShort("51"));
                                newNonFatalInjuries.setInjuryId(selectInjuries);
                                break;
                            case cascom:
                                selectSecurityElement = securityElementsFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                securityElementList.add(selectSecurityElement);
                                selectInjuries = injuriesFacade.find(Short.parseShort("51"));
                                newNonFatalInjuries.setInjuryId(selectInjuries);
                                break;
                            case cascob:
                                selectSecurityElement = securityElementsFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                securityElementList.add(selectSecurityElement);
                                selectInjuries = injuriesFacade.find(Short.parseShort("51"));
                                newNonFatalInjuries.setInjuryId(selectInjuries);
                                break;
                            case chale:
                                selectSecurityElement = securityElementsFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                securityElementList.add(selectSecurityElement);
                                selectInjuries = injuriesFacade.find(Short.parseShort("51"));
                                newNonFatalInjuries.setInjuryId(selectInjuries);
                                break;
                            case otroel:
                                selectSecurityElement = securityElementsFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                securityElementList.add(selectSecurityElement);
                                selectInjuries = injuriesFacade.find(Short.parseShort("51"));
                                newNonFatalInjuries.setInjuryId(selectInjuries);
                                break;
                            // ************************************************DATOS PARA LA TABLA domestic_violence_abuse_type
                            case ma1:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("VERDADERO") == 0) {
                                    AbuseTypes newAbuseType = new AbuseTypes(Short.parseShort("1"));
                                    abuseTypesesList.add(newAbuseType);
                                }
                                break;
                            case ma2:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("VERDADERO") == 0) {
                                    AbuseTypes newAbuseType = new AbuseTypes(Short.parseShort("2"));
                                    abuseTypesesList.add(newAbuseType);
                                }
                                break;
                            case ma3:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("VERDADERO") == 0) {
                                    AbuseTypes newAbuseType = new AbuseTypes(Short.parseShort("3"));
                                    abuseTypesesList.add(newAbuseType);
                                }
                                break;
                            case ma4:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("VERDADERO") == 0) {
                                    AbuseTypes newAbuseType = new AbuseTypes(Short.parseShort("4"));
                                    abuseTypesesList.add(newAbuseType);
                                }
                                break;
                            case ma5:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("VERDADERO") == 0) {
                                    AbuseTypes newAbuseType = new AbuseTypes(Short.parseShort("5"));
                                    abuseTypesesList.add(newAbuseType);
                                }
                                break;
                            case ma6:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("VERDADERO") == 0) {
                                    AbuseTypes newAbuseType = new AbuseTypes(Short.parseShort("6"));
                                    abuseTypesesList.add(newAbuseType);
                                }
                                break;
                            case ma7:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("VERDADERO") == 0) {
                                    AbuseTypes newAbuseType = new AbuseTypes(Short.parseShort("7"));
                                    abuseTypesesList.add(newAbuseType);
                                }
                                break;
                            // ************************************************DATOS PARA LA TABLA domestic_violence_aggressor_type
                            case ag1:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("VERDADERO") == 0) {
                                    AggressorTypes newAggressorTypes = new AggressorTypes(Short.parseShort("1"));
                                    aggressorTypesList.add(newAggressorTypes);
                                }
                                break;
                            case ag2:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("VERDADERO") == 0) {
                                    AggressorTypes newAggressorTypes = new AggressorTypes(Short.parseShort("2"));
                                    aggressorTypesList.add(newAggressorTypes);
                                }
                                break;
                            case ag3:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("VERDADERO") == 0) {
                                    AggressorTypes newAggressorTypes = new AggressorTypes(Short.parseShort("3"));
                                    aggressorTypesList.add(newAggressorTypes);
                                }
                                break;
                            case ag4:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("VERDADERO") == 0) {
                                    AggressorTypes newAggressorTypes = new AggressorTypes(Short.parseShort("4"));
                                    aggressorTypesList.add(newAggressorTypes);
                                }
                                break;
                            case ag5:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("VERDADERO") == 0) {
                                    AggressorTypes newAggressorTypes = new AggressorTypes(Short.parseShort("5"));
                                    aggressorTypesList.add(newAggressorTypes);
                                }
                                break;
                            case ag6:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("VERDADERO") == 0) {
                                    AggressorTypes newAggressorTypes = new AggressorTypes(Short.parseShort("6"));
                                    aggressorTypesList.add(newAggressorTypes);
                                }
                                break;
                            case ag7:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("VERDADERO") == 0) {
                                    AggressorTypes newAggressorTypes = new AggressorTypes(Short.parseShort("7"));
                                    aggressorTypesList.add(newAggressorTypes);
                                }
                                break;
                            case ag8:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("VERDADERO") == 0) {
                                    AggressorTypes newAggressorTypes = new AggressorTypes(Short.parseShort("8"));
                                    aggressorTypesList.add(newAggressorTypes);
                                }
                                break;
                            case ag9:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("VERDADERO") == 0) {
                                    AggressorTypes newAggressorTypes = new AggressorTypes(Short.parseShort("9"));
                                    aggressorTypesList.add(newAggressorTypes);
                                }
                                break;
                            // ************************************************DATOS PARA LA TABLA non_fatal_anatomical_location
                            case sistem:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("VERDADERO") == 0) {
                                    AnatomicalLocations newAnatomicalLocation = new AnatomicalLocations(Short.parseShort("1"));
                                    anatomicalLocationsList.add(newAnatomicalLocation);
                                }
                                break;
                            case craneo:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("VERDADERO") == 0) {
                                    AnatomicalLocations newAnatomicalLocation = new AnatomicalLocations(Short.parseShort("2"));
                                    anatomicalLocationsList.add(newAnatomicalLocation);
                                }
                                break;
                            case ojos:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("VERDADERO") == 0) {
                                    AnatomicalLocations newAnatomicalLocation = new AnatomicalLocations(Short.parseShort("3"));
                                    anatomicalLocationsList.add(newAnatomicalLocation);
                                }
                                break;
                            case maxilof:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("VERDADERO") == 0) {
                                    AnatomicalLocations newAnatomicalLocation = new AnatomicalLocations(Short.parseShort("4"));
                                    anatomicalLocationsList.add(newAnatomicalLocation);
                                }
                                break;
                            case cuello:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("VERDADERO") == 0) {
                                    AnatomicalLocations newAnatomicalLocation = new AnatomicalLocations(Short.parseShort("5"));
                                    anatomicalLocationsList.add(newAnatomicalLocation);
                                }
                                break;
                            case torax:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("VERDADERO") == 0) {
                                    AnatomicalLocations newAnatomicalLocation = new AnatomicalLocations(Short.parseShort("6"));
                                    anatomicalLocationsList.add(newAnatomicalLocation);
                                }
                                break;
                            case abdomen:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("VERDADERO") == 0) {
                                    AnatomicalLocations newAnatomicalLocation = new AnatomicalLocations(Short.parseShort("7"));
                                    anatomicalLocationsList.add(newAnatomicalLocation);
                                }
                                break;
                            case columna:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("VERDADERO") == 0) {
                                    AnatomicalLocations newAnatomicalLocation = new AnatomicalLocations(Short.parseShort("8"));
                                    anatomicalLocationsList.add(newAnatomicalLocation);
                                }
                                break;
                            case pelvis:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("VERDADERO") == 0) {
                                    AnatomicalLocations newAnatomicalLocation = new AnatomicalLocations(Short.parseShort("9"));
                                    anatomicalLocationsList.add(newAnatomicalLocation);
                                }
                                break;
                            case msup:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("VERDADERO") == 0) {
                                    AnatomicalLocations newAnatomicalLocation = new AnatomicalLocations(Short.parseShort("10"));
                                    anatomicalLocationsList.add(newAnatomicalLocation);
                                }
                                break;
                            case minf:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("VERDADERO") == 0) {
                                    AnatomicalLocations newAnatomicalLocation = new AnatomicalLocations(Short.parseShort("11"));//Short.parseShort("1")

                                    anatomicalLocationsList.add(newAnatomicalLocation);
                                }
                                break;
                            case ositio:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("VERDADERO") == 0) {
                                    AnatomicalLocations newAnatomicalLocation = new AnatomicalLocations(Short.parseShort("98"));//Short.parseShort("1")                                
                                    anatomicalLocationsList.add(newAnatomicalLocation);
                                }
                                break;
                            // ************************************************DATOS PARA LA TABLA non_fatal_kind_of_injury
                            case lacera:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("VERDADERO") == 0) {
                                    KindsOfInjury newKindsOfInjury = new KindsOfInjury(Short.parseShort("1"));
                                    kindsOfInjurysList.add(newKindsOfInjury);
                                }
                                break;
                            case cortada:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("VERDADERO") == 0) {
                                    KindsOfInjury newKindsOfInjury = new KindsOfInjury(Short.parseShort("2"));
                                    kindsOfInjurysList.add(newKindsOfInjury);
                                }
                                break;
                            case lesprof:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("VERDADERO") == 0) {
                                    KindsOfInjury newKindsOfInjury = new KindsOfInjury(Short.parseShort("3"));
                                    kindsOfInjurysList.add(newKindsOfInjury);
                                }
                                break;
                            case esglux:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("VERDADERO") == 0) {
                                    KindsOfInjury newKindsOfInjury = new KindsOfInjury(Short.parseShort("4"));
                                    kindsOfInjurysList.add(newKindsOfInjury);
                                }
                                break;
                            case fractura:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("VERDADERO") == 0) {
                                    KindsOfInjury newKindsOfInjury = new KindsOfInjury(Short.parseShort("5"));
                                    kindsOfInjurysList.add(newKindsOfInjury);
                                }
                                break;
                            case quemadur:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("VERDADERO") == 0) {
                                    KindsOfInjury newKindsOfInjury = new KindsOfInjury(Short.parseShort("6"));
                                    kindsOfInjurysList.add(newKindsOfInjury);
                                }
                                break;
                            case contusi:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("VERDADERO") == 0) {
                                    KindsOfInjury newKindsOfInjury = new KindsOfInjury(Short.parseShort("7"));
                                    kindsOfInjurysList.add(newKindsOfInjury);
                                }
                                break;
                            case orgsist:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("VERDADERO") == 0) {
                                    KindsOfInjury newKindsOfInjury = new KindsOfInjury(Short.parseShort("8"));
                                    kindsOfInjurysList.add(newKindsOfInjury);
                                }
                                break;
                            case tce:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("VERDADERO") == 0) {
                                    KindsOfInjury newKindsOfInjury = new KindsOfInjury(Short.parseShort("9"));
                                    kindsOfInjurysList.add(newKindsOfInjury);
                                }
                                break;
                            case olesi:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("VERDADERO") == 0) {
                                    KindsOfInjury newKindsOfInjury = new KindsOfInjury(Short.parseShort("10"));
                                    kindsOfInjurysList.add(newKindsOfInjury);
                                }
                                break;
                            case nosabe:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("VERDADERO") == 0) {
                                    KindsOfInjury newKindsOfInjury = new KindsOfInjury(Short.parseShort("11"));
                                    kindsOfInjurysList.add(newKindsOfInjury);
                                }
                                break;
                            // ************************************************DATOS PARA LA TABLA non_fatal_diagnosis
                            case cie10:
                                //if (resultSetFileData.getString(columnsNames[i]).length() != 0) {

                                selectDiagnoses = diagnosesFacade.find(resultSetFileData.getString(columnsNames[i]));
                                diagnosesList.add(selectDiagnoses);
                                //}
                                break;
                            case cie102:
                                //if (resultSetFileData.getString(columnsNames[i]).length() != 0) {
                                selectDiagnoses = diagnosesFacade.find(resultSetFileData.getString(columnsNames[i]));
                                diagnosesList.add(selectDiagnoses);
                                //}
                                break;
                            case cie103:
                                //if (resultSetFileData.getString(columnsNames[i]).length() != 0) {
                                selectDiagnoses = diagnosesFacade.find(resultSetFileData.getString(columnsNames[i]));
                                diagnosesList.add(selectDiagnoses);
                                //}
                                break;
                            case cie104:
                                //if (resultSetFileData.getString(columnsNames[i]).length() != 0) {
                                selectDiagnoses = diagnosesFacade.find(resultSetFileData.getString(columnsNames[i]));
                                diagnosesList.add(selectDiagnoses);
                                //}
                                break;
                            // ************************************************DATOS PARA LA TABLA victim_vulnerable_group
                            case despl:
                                //if (resultSetFileData.getString(columnsNames[i]).length() != 0) {
                                selectVulnerableGroups = vulnerableGroupsFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                vulnerableGroupList.add(selectVulnerableGroups);
                                //}
                                break;
                            case disca:
                                //if (resultSetFileData.getString(columnsNames[i]).length() != 0) {
                                selectVulnerableGroups = vulnerableGroupsFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                vulnerableGroupList.add(selectVulnerableGroups);
                                //}
                                break;

                            // ************************************************DATOS PARA LA TABLA non_fatal_injuries

                            case instisal:
                                //NonFatalDataSources selectNonFatalDataSource = nonFatalDataSourcesFacade.findByName(Integer.parseInt(resultSetFileData.getString(columnsNames[i])));
                                NonFatalDataSources selectNonFatalDataSource = nonFatalDataSourcesFacade.findByName(resultSetFileData.getString(columnsNames[i]));
                                newNonFatalInjuries.setNonFatalDataSourceId(selectNonFatalDataSource);
                                break;
                            case codbar:
                                Neighborhoods selectNeighborhoods = neighborhoodsFacade.find(Integer.parseInt(resultSetFileData.getString(columnsNames[i])));
                                newNonFatalInjuries.setInjuryNeighborhoodId(neighborhoodsFacade.find(selectNeighborhoods.getNeighborhoodId()));
                                break;
                            case direv:
                                newNonFatalInjuries.setInjuryAddress(resultSetFileData.getString(columnsNames[i]));
                                break;
                            case fechaev:
                                formatoDelTexto = new SimpleDateFormat("dd/MM/yyyy");
                                strDate = resultSetFileData.getString(columnsNames[i]);
                                currentDate = null;
                                try {
                                    currentDate = formatoDelTexto.parse(strDate);
                                    newNonFatalInjuries.setInjuryDate(currentDate);
                                } catch (ParseException ex) {
                                    newNonFatalInjuries.setInjuryDate(currentDate);
                                }
                                break;
                            case diaev:
                                newNonFatalInjuries.setInjuryDayOfWeek(resultSetFileData.getString(columnsNames[i]));
                                break;
                            case horaev:
                                //vene en formato militar

                                //currentDate =new Date
                                //newNonFatalInjuries.setInjuryTime(currentDate);
                                break;
                            case fechacon:
                                formatoDelTexto = new SimpleDateFormat("dd/MM/yyyy");
                                strDate = resultSetFileData.getString(columnsNames[i]);
                                currentDate = null;
                                try {
                                    currentDate = formatoDelTexto.parse(strDate);
                                    newNonFatalInjuries.setCheckupDate(currentDate);
                                } catch (ParseException ex) {
                                    newNonFatalInjuries.setCheckupDate(currentDate);
                                }
                                break;
                            case horacon:
                                break;
                            case remitido:
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("TRUE") == 0) {
                                    newNonFatalInjuries.setSubmittedPatient(true);
                                } else {
                                    newNonFatalInjuries.setSubmittedPatient(false);
                                }
                                break;
                            case deqips:
//                                if (resultSetFileData.getString(columnsNames[i]).length() == 0) {
//                                    Eps selectEps = epsFacade.find(Short.parseShort("1"));
//                                    newNonFatalInjuries.setEpsId(selectEps.getEpsId());
//                                } else {
//                                    //ESTO NO IRIA HASTA QUE ESTE EL LISTADO COMPLETO DE DE EPS's
//                                    Eps selectEps = epsFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
//                                    newNonFatalInjuries.setEpsId(selectEps.getEpsId());
//                                }

                                break;
                            case intenci:
                                Intentionalities selectIntentionalities = intentionalitiesFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                newNonFatalInjuries.setIntentionalityId(selectIntentionalities);
                                break;
                            case lugar:
                                NonFatalPlaces selectNonFatalPlaces = nonFatalPlacesFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                newNonFatalInjuries.setInjuryPlaceId(selectNonFatalPlaces);
                                break;
                            case activi:
                                Activities selectActivities = activitiesFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                newNonFatalInjuries.setActivityId(selectActivities);
                                break;
                            case mecobj:
                                Mechanisms selectMechanisms = mechanismsFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                newNonFatalInjuries.setMechanismId(selectMechanisms);
                                break;
                            case alcohol:
                                selectUseAlcoholDrugs = useAlcoholDrugsFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                newNonFatalInjuries.setUseAlcoholId(selectUseAlcoholDrugs);
                                break;
                            case drogas:
                                selectUseAlcoholDrugs = useAlcoholDrugsFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                newNonFatalInjuries.setUseDrugsId(selectUseAlcoholDrugs);
                                break;
                            case gradogra:
                                //if (resultSetFileData.getString(columnsNames[i]).length() != 0) {
                                newNonFatalInjuries.setBurnInjuryDegree(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                //} else {
                                //ESTO NO VA HASTA COMPLETAR(O COLOCAR EN NULL)
                                //    newNonFatalInjuries.setBurnInjuryDegree(Short.parseShort("0"));
                                // }
                                break;
                            case porcent:
                                //if (resultSetFileData.getString(columnsNames[i]).length() != 0) {
                                newNonFatalInjuries.setBurnInjuryPercentage(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                //} else {
                                //ESTO NO VA HASTA COMPLETAR(O COLOCAR EN NULL)
                                //    newNonFatalInjuries.setBurnInjuryPercentage(Short.parseShort("0"));
                                //}
                                break;
                            case destino:
                                DestinationsOfPatient selectDestinationsOfPatient = destinationsOfPatientFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                newNonFatalInjuries.setDestinationPatientId(selectDestinationsOfPatient);
                                break;

                            default:
                        }
                    }
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

                nonFatalInjuriesFacade.create(newNonFatalInjuries);

                tuplesProcessed++;
                progress = (int) (tuplesProcessed * 100) / tuplesNumber;

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

            } while (resultSetFileData.next());
            progress = 100;
            conx.disconnect();
        } catch (SQLException ex) {
            System.out.println("error: " + ex.toString());
            conx.disconnect();
        }
    }

    public void registerSCC_F_033() {
    }

    public void btnRegisterDataClick() {
        if (nameForm.compareTo("SCC_F_028") == 0) {
            registerSCC_F_028();
        }
        if (nameForm.compareTo("SCC_F_029") == 0) {
            registerSCC_F_029();
        }
        if (nameForm.compareTo("SCC_F_030") == 0) {
            registerSCC_F_030();
        }
        if (nameForm.compareTo("SCC_F_031") == 0) {
            registerSCC_F_031();
        }
        if (nameForm.compareTo("SCC_F_032") == 0) {
            registerSCC_F_032();
        }
        if (nameForm.compareTo("SCC_F_033") == 0) {
            registerSCC_F_033();
        }
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //VALIDACIONES ---------------------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    private boolean isDiscarded(String name, ArrayList<String> discardedValues) {
        /*
         * validacion de si un string se encuentra dentro de una lista.
         */
        for (int i = 0; i < discardedValues.size(); i++) {
            if (name.compareTo(discardedValues.get(i)) == 0) {
                return true;
            }
        }
        return false;
    }

    private boolean isDay(String str) {
        /*
         * validacion de si un numero de 1 y 31
         */
        if (str.trim().length() == 0) {
            return true;
        }
        try {
            int i = Integer.parseInt(str);
            if (i > 0 && i < 32) {
                return true;
            }
            return false;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    private boolean isMonth(String str) {
        /*
         * validacion de si un numero de 1 y 12
         */
        if (str.trim().length() == 0) {
            return true;
        }
        try {
            int i = Integer.parseInt(str);
            if (i > 0 && i < 13) {
                return true;
            }
            return false;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    private boolean isYear(String str) {
        /*
         * validacion de si un numero de 1 y 12
         */
        if (str.trim().length() == 0) {
            return true;
        }
        if (str.trim().length() != 2 && str.trim().length() != 4) {
            return false;
        }
        try {
            int i = Integer.parseInt(str);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    private boolean isMinute(String str) {
        /*
         * validacion de si un numero de 1 y 12
         */
        if (str.trim().length() == 0) {
            return true;
        }
        try {
            int i = Integer.parseInt(str);
            if (i > -1 && i < 60) {
                return true;
            }
            return false;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    private boolean isHour(String str) {
        /*
         * validacion de si un numero de 1 y 12
         */
        if (str.trim().length() == 0) {
            return true;
        }
        try {
            int i = Integer.parseInt(str);
            if (i > 0 && i < 25) {
                return true;
            }
            return false;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    private boolean isNumeric(String str) {
        /*
         * validacion de si un string es entero
         */
        if (str.trim().length() == 0) {
            return true;
        }
        try {
            str.replaceAll(",", "");
            str.replaceAll(".", "");
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    private boolean isDate(String fecha, String format) {
        /*
         * validacion de si un string es una fecha de un determinado formto
         */
        if (fecha.trim().length() == 0) {
            return true;
        }
        SimpleDateFormat formato = new SimpleDateFormat(format);
        Date fechaDate = null;
        try {
            fechaDate = formato.parse(fecha);
            return true;
        } catch (ParseException ex) {
            return false;
        }
    }

    private String isMilitary(String strIn) {
        /*
         * validacion de si un string es un hora miitar
         */
        String str = strIn;

        //----------------------------------------------
        //determinar si hay caracteres
        if (str.trim().length() == 0) {
            return "no se acepta cadenas vacias";
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
                    if (h > 24 || h < 0) {
                        return "La hora debe estar entre 0 y 23";
                    }
                    if (m > 59 || m < 0) {
                        return "los minutos deben estar entre 0 y 59";
                    }
                    if (h == 24 && m != 0) {
                        return "Si la hora es 24 los minutos deben ser cero";
                    }
                    return null;
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
                if (h == 24) {
                    if (m == 0) {
                        return null;
                    } else {
                        return "Si la hora es 24 los minutos solo pueden ser 0";
                    }
                }
                if (h > 24 || h < 0) {
                    return "La hora debe estar entre 0 y 24";
                }
                if (m > 59 || m < 0) {
                    return "los minutos deben estar entre 0 y 59";
                }
                return null;
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
                return "Valor no aceptado como hora militar";
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
                    return "La hora debe estar entre 0 y 23";
                }
                if (m > 59 || m < 0) {
                    return "los minutos deben estar entre 0 y 59";
                }
                if (h == 24 && m != 0) {
                    return "Si la hora es 24 los minutos deben ser cero";
                }
                return null;
            } catch (Exception ex) {
            }
        } else {
            return "Una hora militar debe tener menos de 4 digitos";
        }

        //----------------------------------------------
        //si llego a esta linea es que no supero ningun tipo de validacion
        return "Valor no aceptado como hora militar";
    }

    private boolean isAge(String str) {
        /*
         * validacion de si un string es numero entero o edad definida en meses
         * y años
         */
        if (str.trim().length() == 0) {
            return true;
        }
        try {//intento convertirlo en entero
            int a = Integer.parseInt(str);
            return true;
        } catch (Exception ex) {
        }
        try {//determinar si esta definida en años meses
            String[] splitAge = str.split(" ");
            if (splitAge.length == 4) {
                int m = Integer.parseInt(splitAge[0]);
                int y = Integer.parseInt(splitAge[2]);
                return true;
            }
            return false;
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean isCategorical(String str, String category, boolean compareForCode, ArrayList<RelationValue> relationValueList) {
        /*
         * validacion de si un string esta dentro de una categoria
         */
        if (str.trim().length() == 0) {
            return true;
        }
        ArrayList<String> categoryList = new ArrayList<String>();
        //se valida con respecto a las relaciones de valores
        for (int i = 0; i < relationValueList.size(); i++) {//le paso a categoriList los valores encontrados en la relacion de valores
            categoryList.add(relationValueList.get(i).getNameFound());
        }
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).compareTo(str) == 0) {
                return true;
            }
        }
        //se valida con respecto a los valores esperados
        if (compareForCode == true) {
            categoryList = formsAndFieldsDataMB.categoricalCodeList(category, 0);
        } else {
            categoryList = formsAndFieldsDataMB.categoricalNameList(category, 0);
        }
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).compareTo(str) == 0) {
                return true;
            }
        }
        return false;
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
}
