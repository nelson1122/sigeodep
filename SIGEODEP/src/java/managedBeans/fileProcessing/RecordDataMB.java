/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.fileProcessing;

import beans.connection.ConnectionJDBC;
import beans.errorsControl.ErrorControl;
import beans.lists.Field;
import beans.registerData.SCC_F_032Enum;
import beans.relations.RelationValue;
import beans.relations.RelationVar;
import beans.relations.RelationsGroup;
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
public class RecordDataMB {

    private RelationshipOfVariablesMB relationshipOfVariablesMB;
    private RelationsGroup currentRelationsGroup;
    private FormsAndFieldsDataMB formsAndFieldsDataMB;
    private StoredRelationsMB storedRelationsMB;
    private LoginMB loginMB;
    private ErrorsControlMB errorsControlMB;
    private String[] columnsNames;
    private int tuplesNumber;
    private int tuplesProcessed;
    private ConnectionJDBC conx = null;//conexion sin persistencia a postgres   
    private RelationVar relationVar;
    private Field fieldExepted;
    private String type;
    private String error;
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
    EpsFacade epsFacade;
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
    //**************************************************************************
    //carga del archivo***********************************    
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
//carga del archivo***********************************    
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
            //determino el nombre de la columna
            conx = new ConnectionJDBC();
            conx.connect();
            //numero de tuplas
            ResultSet resultSetFileData = conx.consult("SELECT COUNT(*) FROM temp; ");
            resultSetFileData.next();
            tuplesNumber = resultSetFileData.getInt(1);//numero de tuplas a procesar
            tuplesProcessed = 0;//numero de tuplas procesdas
            //resultSetFileData contendra todos los registros de el archivo(tabla temp)
            resultSetFileData = conx.consult("SELECT * FROM temp; ");
            resultSetFileData.next();
            progress = 0;
            //tomo el grupo de relaciones de valores y de variables
            currentRelationsGroup = relationshipOfVariablesMB.getCurrentRelationsGroup();
            //creo un arreglo con los nombres de las columnas
            int columnsNumber = resultSetFileData.getMetaData().getColumnCount();
            int errorsNumber = 0;
            int pos = 0;
            columnsNames = new String[columnsNumber];
            for (int i = 1; i <= columnsNumber; i++) {
                columnsNames[pos] = resultSetFileData.getMetaData().getColumnName(i);
                pos++;
            }
            errorsControlMB.setErrorControlArrayList(new ArrayList<ErrorControl>());
            //recorro cada uno de los registros de la tabla temp
            int count = 1;
            do {
                //recorro cada una de las columnas de cada registro                
                for (int i = 0; i < columnsNames.length; i++) {
                    //determino la relacion de variables
                    relationVar = currentRelationsGroup.findRelationVar(columnsNames[i]);
                    if (relationVar != null) {
                        fieldExepted = formsAndFieldsDataMB.searchField(relationVar.getNameExpected());
                        type = relationVar.getFieldType();

                        if (type.compareTo("text") == 0) {//valor de tipo texto no se valida
                        } else if (type.compareTo("integer") == 0) {//si el dato no es numerico adiciono el error 
                            if (!isNumeric(resultSetFileData.getString(columnsNames[i]))) {
                                errorsNumber++;//error = "No es entero";
                                errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), String.valueOf(count), "Entero"));
                            }
                        } else if (type.compareTo("date") == 0) {//si el dato no es fecha validala adiciono el error 
                            if (!isDate(resultSetFileData.getString(columnsNames[i]), relationVar.getDateFormat())) {
                                errorsNumber++;//error = "fecha no corresponde al formato";
                                errorsControlMB.addError(new ErrorControl(relationVar, resultSetFileData.getString(relationVar.getNameFound()), String.valueOf(count), "Fecha"));
                            }
                        } else {//se espera un valorcategorico

                            if (!isCategorical(
                                    resultSetFileData.getString(relationVar.getNameFound()), relationVar.getNameExpected(),
                                    relationVar.getTypeComparisonForCode(), relationVar.getRelationValueList())) {
                                errorsNumber++;//error = "no esta en la categoria";
                                errorsControlMB.addError(
                                        new ErrorControl(
                                        relationVar,
                                        resultSetFileData.getString(relationVar.getNameFound()),
                                        String.valueOf(count),
                                        formsAndFieldsDataMB.variableDescription(relationVar.getNameExpected())));
                            }
                        }
                    }

                }
                count++;
            } while (resultSetFileData.next());
            errorsControlMB.setSizeErrorsList(errorsNumber);
            errorsControlMB.reload();
            progress = 100;
            conx.disconnect();
            if (errorsNumber != 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Errores", "Existen: " + String.valueOf(errorsNumber) + " que no superaron el proceso de validación, dirijase a la sección de errores para  especificar si los corrige o ignora."));
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

    public void btnRegisterDataClick() {
        //enconramos el maximo de lesiones no fatales
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
            //determino el nombre de la columna
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
                Eps eps = epsFacade.find(Short.parseShort("1"));
                Victims newVictim = new Victims();
                newVictim.setVictimId(victimsFacade.findMax() + 1);
                newVictim.setVulnerableGroupId(vulnerableGroups);
                newVictim.setEpsId(eps);
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
                                newVictim.setVictimFirstname(resultSetFileData.getString(columnsNames[i]));
                                break;
                            case nombre2:
                                String firstName = newVictim.getVictimFirstname();
                                firstName = firstName + " " + resultSetFileData.getString(columnsNames[i]);
                                newVictim.setVictimFirstname(firstName);
                                break;
                            case apellid1:
                                newVictim.setVictimLastname(resultSetFileData.getString(columnsNames[i]));
                                break;
                            case apellid2:
                                String lastName = newVictim.getVictimLastname();
                                lastName = lastName + " " + resultSetFileData.getString(columnsNames[i]);
                                newVictim.setVictimLastname(lastName);
                                break;
                            case tid:
                                IdTypes selectIdType = idTypesFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                newVictim.setTypeId(selectIdType);
                                break;
                            case nid://para dos tablas
                                newVictim.setVictimNid(resultSetFileData.getString(columnsNames[i]));
                                newNonFatalInjuries.setVictimId(newVictim);
                                break;
                            case medad:
                                //if (resultSetFileData.getString(columnsNames[i]).length() != 0) {
                                newVictim.setAgeTypeId(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                //}
                                break;
                            case edadcantid:
                                //if (resultSetFileData.getString(columnsNames[i]).length() != 0) {
                                newVictim.setVictimAge(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                //}
                                break;
                            case sexo:
                                Genders selectGender = gendersFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                newVictim.setGenderId(selectGender);
                                break;
                            case ocupa:
                                //Jobs selectJob = jobsFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                Jobs selectJob = jobsFacade.find(Short.parseShort("1"));
                                newVictim.setJobId(selectJob);
                                break;
                            case getnico:
                                //if (resultSetFileData.getString(columnsNames[i]).length() != 0) {
                                EthnicGroups selectEthnicGroup = ethnicGroupsFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                newVictim.setEthnicGroupId(selectEthnicGroup);
                                //}
                                break;
                            case codigobarr:
                                newVictim.setVictimNeighborhoodId(Integer.parseInt(resultSetFileData.getString(columnsNames[i])));
                                break;
                            case dirres:
                                newVictim.setVictimAddress(resultSetFileData.getString(columnsNames[i]));
                                break;
                            case telres:
                                newVictim.setVictimTelephone(resultSetFileData.getString(columnsNames[i]));
                                break;
                            // ************************************************DATOS PARA LA TABLA non_fatal_transport
                            case ttrans:
                                //if (resultSetFileData.getString(columnsNames[i]).length() != 0) {
                                TransportTypes selectTransportTypes = transportTypesFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                newNonFatalTransport.setTransportTypeId(selectTransportTypes);
                                selectInjuries = injuriesFacade.find(Short.parseShort("51"));
                                newNonFatalInjuries.setInjuryId(selectInjuries);
                                //}
                                break;
                            case tcontp:
                                //if (resultSetFileData.getString(columnsNames[i]).length() != 0) {
                                TransportCounterparts selectTransportCounterpart = transportCounterpartsFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                newNonFatalTransport.setTransportCounterpartId(selectTransportCounterpart);
                                selectInjuries = injuriesFacade.find(Short.parseShort("51"));
                                newNonFatalInjuries.setInjuryId(selectInjuries);
                                //}
                                break;
                            case tusuar:
                                //if (resultSetFileData.getString(columnsNames[i]).length() != 0) {
                                TransportUsers selectTransportUser = transportUsersFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                newNonFatalTransport.setTransportUserId(selectTransportUser);
                                selectInjuries = injuriesFacade.find(Short.parseShort("51"));
                                newNonFatalInjuries.setInjuryId(selectInjuries);
                                //}
                                break;
                            // ************************************************DATOS PARA LA TABLA non_fatal_interpersonal
                            case anteca://boleano->previous_antecedent
                                //if (resultSetFileData.getString(columnsNames[i]).length() != 0) {
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("TRUE") == 0) {
                                    newNonFatalInterpersonal.setPreviousAntecedent(true);
                                } else {
                                    newNonFatalInterpersonal.setPreviousAntecedent(false);
                                }
                                selectInjuries = injuriesFacade.find(Short.parseShort("50"));
                                newNonFatalInjuries.setInjuryId(selectInjuries);
                                //}
                                break;
                            case relacav://categorico->relationships_to_victim
                                //if (resultSetFileData.getString(columnsNames[i]).length() != 0) {
                                RelationshipsToVictim selectRelationshipsToVictim = relationshipsToVictimFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                newNonFatalInterpersonal.setRelationshipVictimId(selectRelationshipsToVictim);
                                selectInjuries = injuriesFacade.find(Short.parseShort("50"));
                                newNonFatalInjuries.setInjuryId(selectInjuries);
                                //}
                                break;
                            case contex:
                                //if (resultSetFileData.getString(columnsNames[i]).length() != 0) {
                                Contexts selectContexts = contextsFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                newNonFatalInterpersonal.setContextId(selectContexts);
                                selectInjuries = injuriesFacade.find(Short.parseShort("50"));
                                newNonFatalInjuries.setInjuryId(selectInjuries);
                                //}
                                break;
                            case sexa:
                                //if (resultSetFileData.getString(columnsNames[i]).length() != 0) {
                                AggressorGenders selectAggressorGenders = aggressorGendersFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                newNonFatalInterpersonal.setAggressorGenderId(selectAggressorGenders);
                                selectInjuries = injuriesFacade.find(Short.parseShort("50"));
                                newNonFatalInjuries.setInjuryId(selectInjuries);
                                //}
                                break;
                            // ************************************************DATOS PARA LA TABLA non_fatal_selft-inflicted
                            case intpre:
                                //if (resultSetFileData.getString(columnsNames[i]).length() != 0) {
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("TRUE") == 0) {
                                    newNonFatalSelfInflicted.setPreviousAttempt(true);
                                } else {
                                    newNonFatalSelfInflicted.setPreviousAttempt(false);
                                }
                                selectInjuries = injuriesFacade.find(Short.parseShort("52"));
                                newNonFatalInjuries.setInjuryId(selectInjuries);
                                //}
                                break;
                            case trment:
                                // if (resultSetFileData.getString(columnsNames[i]).length() != 0) {
                                if (resultSetFileData.getString(columnsNames[i]).compareTo("TRUE") == 0) {
                                    newNonFatalSelfInflicted.setMentalAntecedent(true);
                                } else {
                                    newNonFatalSelfInflicted.setMentalAntecedent(false);
                                }
                                selectInjuries = injuriesFacade.find(Short.parseShort("52"));
                                newNonFatalInjuries.setInjuryId(selectInjuries);
                                //}
                                break;
                            case fprec:
                                //if (resultSetFileData.getString(columnsNames[i]).length() != 0) {
                                PrecipitatingFactors selectPrecipitatingFactors = precipitatingFactorsFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                newNonFatalSelfInflicted.setPrecipitatingFactorId(selectPrecipitatingFactors);
                                selectInjuries = injuriesFacade.find(Short.parseShort("52"));
                                newNonFatalInjuries.setInjuryId(selectInjuries);
                                //}
                                break;
                            // ************************************************DATOS PARA LA TABLA non_fatal_transport_security_element
                            case tsegu:
                                //if (resultSetFileData.getString(columnsNames[i]).length() != 0) {
                                selectSecurityElement = securityElementsFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                securityElementList.add(selectSecurityElement);
                                selectInjuries = injuriesFacade.find(Short.parseShort("51"));
                                newNonFatalInjuries.setInjuryId(selectInjuries);
                                //}
                                break;
                            case cintu:
                                //if (resultSetFileData.getString(columnsNames[i]).length() != 0) {
                                selectSecurityElement = securityElementsFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                securityElementList.add(selectSecurityElement);
                                selectInjuries = injuriesFacade.find(Short.parseShort("51"));
                                newNonFatalInjuries.setInjuryId(selectInjuries);
                                //}
                                break;
                            case cascom:
                                //if (resultSetFileData.getString(columnsNames[i]).length() != 0) {
                                selectSecurityElement = securityElementsFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                securityElementList.add(selectSecurityElement);
                                selectInjuries = injuriesFacade.find(Short.parseShort("51"));
                                newNonFatalInjuries.setInjuryId(selectInjuries);
                                //}
                                break;
                            case cascob:
                                //if (resultSetFileData.getString(columnsNames[i]).length() != 0) {
                                selectSecurityElement = securityElementsFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                securityElementList.add(selectSecurityElement);
                                selectInjuries = injuriesFacade.find(Short.parseShort("51"));
                                newNonFatalInjuries.setInjuryId(selectInjuries);
                                //}
                                break;
                            case chale:
                                //if (resultSetFileData.getString(columnsNames[i]).length() != 0) {
                                selectSecurityElement = securityElementsFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                securityElementList.add(selectSecurityElement);
                                selectInjuries = injuriesFacade.find(Short.parseShort("51"));
                                newNonFatalInjuries.setInjuryId(selectInjuries);
                                //}
                                break;
                            case otroel:
                                //if (resultSetFileData.getString(columnsNames[i]).length() != 0) {
                                selectSecurityElement = securityElementsFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                securityElementList.add(selectSecurityElement);
                                selectInjuries = injuriesFacade.find(Short.parseShort("51"));
                                newNonFatalInjuries.setInjuryId(selectInjuries);
                                //}
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
                                newNonFatalInjuries.setNonFatalDataSourceId(selectNonFatalDataSource.getNonFatalDataSourceId());
                                break;
                            case codbar:
                                Neighborhoods selectNeighborhoods = neighborhoodsFacade.find(Integer.parseInt(resultSetFileData.getString(columnsNames[i])));
                                newNonFatalInjuries.setInjuryNeighborhoodId(selectNeighborhoods.getNeighborhoodId());
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
                                if (resultSetFileData.getString(columnsNames[i]).length() == 0) {
                                    Eps selectEps = epsFacade.find(Short.parseShort("1"));
                                    newNonFatalInjuries.setEpsId(selectEps.getEpsId());
                                } else {
                                    //ESTO NO IRIA HASTA QUE ESTE EL LISTADO COMPLETO DE DE EPS's
                                    Eps selectEps = epsFacade.find(Short.parseShort(resultSetFileData.getString(columnsNames[i])));
                                    newNonFatalInjuries.setEpsId(selectEps.getEpsId());
                                }

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


                newNonFatalInjuries.setAnatomicalLocationsList(anatomicalLocationsList);

                // ************************************************ vector non_fatal_transport_security_element
                //securityElementList
                // ************************************************ vector domestic_violence_abuse_type
                //abuseTypesesList
                // ************************************************ vector domestic_violence_aggressor_type
                //aggressorTypesList
                // ************************************************ vector non_fatal_kind_of_injury                
                //newNonFatalInjuries.setKindsOfInjuryList(kindsOfInjurysList);
                // ************************************************ vector non_fatal_diagnosis
                newNonFatalInjuries.setDiagnosesList(diagnosesList);
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

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //VALIDACIONES ---------------------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    private boolean isNumeric(String str) {
        /*
         * validacion de si un string es entero
         */
        if (str.trim().length() == 0) {
            return true;
        }
        try {
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

    private boolean isCategorical(String str, String category, boolean compareForCode, ArrayList<RelationValue> relationValueList) {
        /*
         * validacion de si un string esta dentro de una categoria
         */
        if (str.trim().length() == 0) {
            return true;
        }
        ArrayList<String> categoryList = new ArrayList<String>();
        //se valida con respecto a las relaciones de variables
        for (int i = 0; i < relationValueList.size(); i++) {//le paso a categori list los valores encontrados en la relacion de valores
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
    
    
}
