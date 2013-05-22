/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.fileProcessing;

import beans.connection.ConnectionJdbcMB;
import beans.util.RowDataTable;
import java.io.*;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import managedBeans.filters.CopyMB;
import managedBeans.login.LoginMB;
import model.dao.*;
import model.pojo.*;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 *
 * @author santos
 */
@ManagedBean(name = "projectsMB")
@SessionScoped
public class ProjectsMB implements Serializable {

    @EJB
    FormsFacade formsFacade;
    @EJB
    ProjectsFacade projectsFacade;
    @EJB
    SourcesFacade sourcesFacade;
    @EJB
    TagsFacade tagsFacade;
    @EJB
    RelationGroupFacade relationGroupFacade;
    @EJB
    RelationVariablesFacade relationVariablesFacade;
    @EJB
    RelationsDiscardedValuesFacade relationsDiscardedValuesFacade;
    @EJB
    RelationValuesFacade relationValuesFacade;
    @EJB
    UsersFacade usersFacade;
    @EJB
    FatalInjuriesFacade fatalInjuriesFacade;
    @EJB
    NonFatalInjuriesFacade nonFatalInjuriesFacade;
    @EJB
    NonFatalDomesticViolenceFacade nonFatalDomesticViolenceFacade;
    private String newProjectName = "";
    private String lastCreatedProjectName = "";
    private String toolTipText = "";
    private String newRelationsCopyName = "";
    private String currentProjectName = "";
    private String selectedRelationsNameInCopy = "";
    private String newRelationsGroupName = "";
    private String currentRelationsGroupNameInLoad = "";
    private String currentRelationsGroupName = "";
    private int currentRelationsGroupId = -1;
    private List<String> relationGroupsInLoad;
    private List<String> relationGroupsInCopy;
    private String currentFormName = "";//ficha actual
    private String currentFormId = "";//ficha actual
    private String newFormId = "";//ficha actual
    private String aceptedRelationsName = "";//nombre para el grupo de relaciones cuando el seleccionado no es valido
    private boolean exportFileNameDisabled = true;//xls donde exporta proyecto
    private int countNulos = 0;
    private SelectItem[] forms;
    private int newSourceName = 0;//proveedor actual    
    private String currentSourceName = "";//proveedor actual    
    private SelectItem[] sources;
    private boolean hubo_error = false;
    private SelectItem[] delimiters;
    private UploadedFile file;
    private String currentFileName = "";
    private String newFileName = "";
    private String exportFileName = "salida";
    private ArrayList<String> acceptedRelations;//caberecera del archivo
    private ArrayList<String> headerFileNames;//caberecera del archivo
    private ArrayList<Long> headerFileIds;//caberecera del archivo
    private List<String> variablesFound;
    private List<String> variablesExpected;
    private RelationshipOfVariablesMB relationshipOfVariablesMB;
    private RelationshipOfValuesMB relationshipOfValuesMB;
    private List<RowDataTable> rowProjectsTableList;
    private RowDataTable selectedProjectTable;
    private ErrorsControlMB errorsControlMB;
    private LoginMB loginMB;
    private RecordDataMB recordDataMB;
    //private StoredRelationsMB storedRelationsMB;
    private ConnectionJdbcMB connectionJdbcMB;
    private CopyMB copyMB;
    //private List<Tags> tagsList;
    private boolean inactiveTabs = true;
    private int currentProjectId = -1;//identificador de proyecto actual
    private int tuplesProcessed;
    //private String nameTableTemp = "temp";
    private String newDelimiter = "";
    private String currentDelimiter = "";
    private String newGroupRelationsName = "";
    //private String copyGroupRelationsName = "";//nombre usado cuando se realiza la copia de un grupo de relaciones al cargar
    private boolean configurationLoaded = false;//determinar si la configuracion ya se cargo
    private StringBuilder sb;
    private StringBuilder sb2;
    private CopyManager cpManager;
    private int maxNumberInserts = 1000000;//numero de insert por copy realizado
    private int currentNumberInserts = 0;//numero de inserts actual
    private long startColumnId = 0;//columna inicial en tabla 'project_columns'
    private long endColumnId = 0;//columna inicial en tabla 'project_columns'
    private String inconsistentRelationsDialog = "";
    private ArrayList<String> errorsList = new ArrayList<String>();
    String error = "";
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

    public void load() {
        if (selectedProjectTable != null) {
        }
    }

    public void changeRelationGroupInCopy() {
        newRelationsCopyName = "";
        if (selectedRelationsNameInCopy != null && selectedRelationsNameInCopy.trim().length() != 0) {
            String newName = "";
            int numberOfRelation = 2;
            boolean nameDetermined = false;
            boolean nameFound = false;

            List<RelationGroup> relationGroupList = relationGroupFacade.findAll();
            while (!nameDetermined) {
                newName = selectedRelationsNameInCopy + "(" + numberOfRelation + ")";

                nameFound = false;
                for (int i = 0; i < relationGroupList.size(); i++) {
                    if (relationGroupList.get(i).getNameRelationGroup().compareTo(newName) == 0) {
                        nameFound = true;
                        break;
                    }
                }
                if (!nameFound) {//si no esta repetido salgo
                    nameDetermined = true;
                }
                numberOfRelation++;
            }
            newRelationsCopyName = newName;
        }
    }

    public void changeForm() {
        System.out.println("Cambia el formulario");
        loadSources();
        //loadVarsExpected();
    }

    public ProjectsMB() {
        /*
         * Constructor de la clase
         */
        FacesContext context = FacesContext.getCurrentInstance();
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
        copyMB = (CopyMB) context.getApplication().evaluateExpressionGet(context, "#{copyMB}", CopyMB.class);
        relationshipOfVariablesMB = (RelationshipOfVariablesMB) context.getApplication().evaluateExpressionGet(context, "#{relationshipOfVariablesMB}", RelationshipOfVariablesMB.class);
        relationshipOfValuesMB = (RelationshipOfValuesMB) context.getApplication().evaluateExpressionGet(context, "#{relationshipOfValuesMB}", RelationshipOfValuesMB.class);
        errorsControlMB = (ErrorsControlMB) context.getApplication().evaluateExpressionGet(context, "#{errorsControlMB}", ErrorsControlMB.class);
        recordDataMB = (RecordDataMB) context.getApplication().evaluateExpressionGet(context, "#{recordDataMB}", RecordDataMB.class);
        loginMB = (LoginMB) context.getApplication().evaluateExpressionGet(context, "#{loginMB}", LoginMB.class);
        //nameTableTemp = "temp" + loginMB.getLoginname();
        //connectionJdbcMB.setTableName(nameTableTemp);

    }

    public void loadProjects() {
        List<Projects> projectsList = projectsFacade.findAll();
        rowProjectsTableList = new ArrayList<RowDataTable>();
        for (int i = 0; i < projectsList.size(); i++) {
            if (projectsList.get(i).getUserId() == loginMB.getCurrentUser().getUserId()) {
                rowProjectsTableList.add(new RowDataTable(
                        projectsList.get(i).getProjectId().toString(),
                        projectsList.get(i).getProjectName().toString(),
                        usersFacade.find(projectsList.get(i).getUserId()).getUserLogin(),
                        formsFacade.findByFormId(projectsList.get(i).getFormId()).getFormName(),
                        sourcesFacade.find(projectsList.get(i).getSourceId()).getSourceName(),
                        projectsList.get(i).getFileName()));
            }
        }
    }

    public void reset() {//@PostConstruct 
        /*
         * Cargar el formulario con los valores iniciales
         */

        currentProjectId = -1;
        inactiveTabs = true;
        newFormId = "SCC-F-032";
        file = null;
        currentProjectName = "";
        currentDelimiter = "";
        currentFileName = "";
        exportFileName = "salida";
        exportFileNameDisabled = true;
        currentFormName = "";
        currentFormId = "";
        currentRelationsGroupName = "";
        currentSourceName = "";
        newProjectName = "";
        newFileName = "";

        newRelationsGroupName = "";
        try {
            cpManager = new CopyManager((BaseConnection) connectionJdbcMB.getConn());
        } catch (SQLException ex) {
            System.out.println("Error 1 en " + this.getClass().getName() + ":" + ex.toString());
        }

        //tagName = "";
        loadForms();
        loadSources();
        loadVarsExpected();
        loadDelimiters();
        loadRelatedGroups();
        loadProjects();
        //tagsList = tagsFacade.findAll();
        variablesFound = new ArrayList<String>();
    }

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES QUE CARGAN VALORES -----------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    /*
     * Cargar los grupos relaciones existentes
     */
    public void loadRelatedGroups() {
        //-----------------------------------------------
        List<RelationGroup> relationGroupList = relationGroupFacade.findAll();
        currentRelationsGroupNameInLoad = "";
        selectedRelationsNameInCopy = "";
        relationGroupsInLoad = new ArrayList<String>();
        relationGroupsInCopy = new ArrayList<String>();
        for (int i = 0; i < relationGroupList.size(); i++) {
            if (relationGroupList.get(i).getUserId() == loginMB.getCurrentUser().getUserId()) {
                relationGroupsInLoad.add(relationGroupList.get(i).getNameRelationGroup());
            }
//            else{
//                relationGroupsInLoad.add("este no - "+relationGroupList.get(i).getNameRelationGroup());
//            }
            relationGroupsInCopy.add(relationGroupList.get(i).getNameRelationGroup());
        }
    }

    private void loadDelimiters() {
        /*
         * Cargar los delimitadores
         */
        delimiters = new SelectItem[]{
            new SelectItem("TAB", "TAB"),
            new SelectItem(";", ";"),
            new SelectItem(",", ","),};
    }

    private void loadForms() {
        /*
         * cargar la lista de formularios existentes
         */
        //System.out.println("Se cargan los formularios");
        try {
            List<Forms> formsList = formsFacade.findAll();
            forms = new SelectItem[formsList.size()];
            for (int i = 0; i < formsList.size(); i++) {
                forms[i] = new SelectItem(formsList.get(i).getFormId(), formsList.get(i).getFormName());
            }
        } catch (Exception e) {
            System.out.println("Error 2 en " + this.getClass().getName() + ":" + e.toString());
        }
    }

    private void loadSources() {
        /*
         * cargar la lista de proveedores(fuentes) de datos dependiendo de un
         * determinado formulario
         */
        List<Sources> sourcesList = formsFacade.findSources(newFormId);
        sources = new SelectItem[sourcesList.size()];

        for (int i = 0; i < sourcesList.size(); i++) {
            sources[i] = new SelectItem(sourcesList.get(i).getSourceId().toString(), sourcesList.get(i).getSourceName());
            newSourceName = Integer.parseInt(sources[0].getValue().toString());
        }
    }

    private void reloadVarsFound() {
        /*
         * recargar las variables encontradas(vienen del archivo)
         */
        try {
            ResultSet rs = connectionJdbcMB.consult(""
                    + " SELECT "
                    + "    project_columns.column_name"
                    + " FROM "
                    + "    public.project_records, "
                    + "    public.project_columns"
                    + " WHERE "
                    + "    project_columns.column_id = project_records.column_id AND"
                    + "    project_records.project_id = " + currentProjectId + ";");

            //int columnsNumber = resultSetFileData.getMetaData().getColumnCount();
            //int pos = 0;

//            headerFile = new String[columnsNumber - 1];//creo un arreglo con los nombres de las columnas
//            for (int i = 2; i <= columnsNumber; i++) {
//                headerFile[pos] = resultSetFileData.getMetaData().getColumnName(i);
//                pos++;
//            }
        } catch (Exception e) {
            System.out.println("Error 3 en " + this.getClass().getName() + ":" + e.toString());
        }
    }

    private void loadVarsExpected() {
        /*
         * cargar las variables esperadas dependiendo de un determinado
         * formulario
         */
//        try {
//            ResultSet rs = connectionJdbcMB.consult(""
//                    + " SELECT"
//                    + "    fields.field_name"
//                    + " FROM "
//                    + "    public.fields"
//                    + " WHERE "
//                    + "    fields.form_id LIKE '" + currentFormName + "';");
//            while (rs.next()) {
//                variablesExpected.add(rs.getString(1));
//            }
//        } catch (Exception e) {
//            System.out.println("******PRIMER INGRESO******");
//        }
    }

    public void printMessage(FacesMessage.Severity s, String title, String messageStr) {
        FacesMessage msg = new FacesMessage(s, title, messageStr);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //CLIK SOBRE BOTONES --------------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------    

    private void copyFile(String fileName, InputStream in) {
        try {
            OutputStream out = new FileOutputStream(new File(fileName));
            int read;
            byte[] bytes = new byte[1024];
            while ((read = in.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            in.close();
            out.flush();
            out.close();
            System.out.println("El nuevo fichero fue creado con éxito!");
        } catch (IOException e) {
            System.out.println("Error 4 en " + this.getClass().getName() + ":" + e.toString());
        }
    }

    private void uploadFileDelimiter() {
        /*
         * CARGA DE UN ARCHIVO CON DELIMITADOR
         */
        try {
            //Long fileSize = file.getSize();
            //tuplesNumber = Integer.parseInt(String.valueOf(fileSize / 1000));
            tuplesProcessed = 0;
            String line;
            InputStreamReader isr;
            BufferedReader buffer;
            //String salida = "";
            //boolean continueProcess = true;

            headerFileNames = new ArrayList<String>();
            String[] tupla;
            //String[] tupla2;
            isr = new InputStreamReader(file.getInputstream());
            buffer = new BufferedReader(isr);
            //Leer el la informacion del archivo linea por linea                       
            ArrayList<String> rowFileData;

            while ((line = buffer.readLine()) != null) {
                if (newDelimiter.compareTo("TAB") == 0) {
                    tupla = line.split("\t");
                } else if (newDelimiter.compareTo(",") == 0) {
                    tupla = line.split(",");
                } else {
                    tupla = line.split(";");
                }
                if (tuplesProcessed == 0) {
                    headerFileNames.addAll(Arrays.asList(tupla));
                    headerFileNames = prepareArray(headerFileNames);
                    addTableProjectColumns();//registrar en project_columns
                } else {
                    rowFileData = new ArrayList<String>();

                    if (headerFileNames.size() == tupla.length) {//igual numero de columnas en cabecera y tupla
                        rowFileData.addAll(Arrays.asList(tupla));
                    } else if (headerFileNames.size() > tupla.length) {//numero de columnas menor que cabecera
                        for (int i = 0; i < headerFileNames.size(); i++) {
                            if (i < tupla.length) {
                                rowFileData.add(tupla[i]);
                            } else {
                                rowFileData.add("");//se completa con vacios
                            }
                        }
                    } else {//numero de columnas mayor que cabecera
                        for (int i = 0; i < headerFileNames.size(); i++) {
                            rowFileData.add(tupla[i]);
                        }
                    }
                    addTableProjectRecords(rowFileData, tuplesProcessed);
                }
                tuplesProcessed++;
                //createProyectMessage = "Procesando... filas " + tuplesProcessed + " cargadas";                
            }
            addTableProjectRecords(null, -1);
            try {
                connectionJdbcMB.non_query("update " + connectionJdbcMB.getTableName() + " set dirres = baveres where dirres like '';");
                connectionJdbcMB.non_query("update " + connectionJdbcMB.getTableName() + " set baveres = dirres where baveres like  '';");
            } catch (Exception e) {
            }
        } catch (IOException e) {
            System.out.println("Error 5 en " + this.getClass().getName() + ":" + e.toString());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ocurrió un error al cargar el archivo", e.toString()));
        } catch (Exception ex) {
            System.out.println("Error 6 en " + this.getClass().getName() + ":" + ex.toString());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ocurrió un error al cargar el archivo", ex.toString()));
        }
    }

    private void determineInconsistentRelations() {
        /*
         * determina si las relaciones de variables corresponden al 
         * grupo de y archivo cargados devuleve true si hay inconsistencias
         */

        //System.out.println("actual nombre 1: " + newGroupRelationsName);
        acceptedRelations = new ArrayList<String>();

        if (file.getFileName().endsWith("xlsx")) {//validar las relaciones de variables desde excell
            //------------------------------------------------------------------
            //----OBTENER CABECERA DEL ARCHIVO EXCEL ---------------------------
            //------------------------------------------------------------------
            try {
                File file2 = new File(file.getFileName());
                OPCPackage container;
                container = OPCPackage.open(file2.getAbsolutePath());
                ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(container);
                XSSFReader xssfReader = new XSSFReader(container);
                StylesTable styles = xssfReader.getStylesTable();
                XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
                while (iter.hasNext()) {
                    InputStream stream = iter.next();
                    InputSource sheetSource = new InputSource(stream);
                    SAXParserFactory saxFactory = SAXParserFactory.newInstance();
                    try {
                        SAXParser saxParser = saxFactory.newSAXParser();
                        XMLReader sheetParser = saxParser.getXMLReader();
                        ContentHandler handler = new XSSFSheetXMLHandler(styles, strings, new SheetContentsHandler() {
                            ArrayList<String> rowFileData = new ArrayList<String>();
                            int pos = 0;

                            @Override
                            public void startRow(int rowNum) {
                            }

                            @Override
                            public void endRow() {
                                //System.out.println(" FINALIZA: -----------------------");
                                if (pos == 0) {
                                    headerFileNames = prepareArray(rowFileData);
                                    pos++;
                                }
                            }

                            @Override
                            public void cell(String cellReference, String formattedValue) {
                                //System.out.println("CELDA:"+cellReference + "   VALOR." + formattedValue);
                                if (pos == 0) {
                                    CellReference a = new CellReference(cellReference);
                                    int empyColumns = a.getCol() - rowFileData.size();
                                    for (int i = 0; i < empyColumns; i++) {
                                        rowFileData.add("");//completar casillas vacias
                                    }
                                    rowFileData.add(formattedValue);
                                }
                            }

                            @Override
                            public void headerFooter(String text, boolean isHeader, String tagName) {
                            }
                        }, false);//means result instead of formula                                
                        sheetParser.setContentHandler(handler);
                        sheetParser.parse(sheetSource);
                    } catch (ParserConfigurationException e) {
                        System.out.println("Error 7 en " + this.getClass().getName() + ":" + e.toString());
                    }
                    stream.close();
                    break;
                }
            } catch (Exception e) {
                System.out.println("Error 8 en " + this.getClass().getName() + ":" + e.toString());
                errorsList.add("El archivo especificado no pudo ser leido");
            }
            //System.out.println("actual nombre 2: " + newGroupRelationsName);
        } else {
            //------------------------------------------------------------------
            //----OBTENER CABECERA DEL ARCHIVO PLANO ---------------------------
            //------------------------------------------------------------------
            try {
                String line;
                InputStreamReader isr;
                BufferedReader buffer;
                headerFileNames = new ArrayList<String>();
                String[] tupla;
                isr = new InputStreamReader(file.getInputstream());
                buffer = new BufferedReader(isr);

                while ((line = buffer.readLine()) != null) {//Leer archivo linea por linea                       
                    if (newDelimiter.compareTo("TAB") == 0) {
                        tupla = line.split("\t");
                    } else if (newDelimiter.compareTo(",") == 0) {
                        tupla = line.split(",");
                    } else {
                        tupla = line.split(";");
                    }
                    if (tuplesProcessed == 0) {
                        headerFileNames.addAll(Arrays.asList(tupla));
                        headerFileNames = prepareArray(headerFileNames);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error 9 en " + this.getClass().getName() + ":" + e.toString());
                errorsList.add("Ocurrió un error al leer el archivo " + e.toString());
            } catch (Exception ex) {
                System.out.println("Error 10 en " + this.getClass().getName() + ":" + ex.toString());
                errorsList.add("Ocurrió un error al leer el archivo " + ex.toString());
            }
            //System.out.println("actual nombre 3: " + newGroupRelationsName);
        }
        //------------------------------------------------------------------
        //----VALIDAR RELACIONES -------------------------------------------
        //------------------------------------------------------------------
        if (errorsList.isEmpty()) {//en headerFileNames me queda determinada la cabecera                        
            ResultSet rs, rs2;
            boolean aceptedCurrent;
            int validRelationVariables = 0;//todas las relaciones se validaron
            try {
                rs = connectionJdbcMB.consult(""
                        + " SELECT \n"
                        + "    relation_variables.name_expected, \n"
                        + "    relation_variables.name_found \n"
                        + "FROM "
                        + "    public.relation_variables, \n"
                        + "    public.relation_group \n"
                        + "WHERE \n"
                        + "    relation_variables.id_relation_group = relation_group.id_relation_group AND \n"
                        + "    relation_group.name_relation_group LIKE '" + newRelationsGroupName + "' \n");
                while (rs.next()) {

                    //valor esperado de la relacion de variables este en la ficha correspondiente
                    rs2 = connectionJdbcMB.consult(""
                            + " SELECT \n"
                            + "    field_name \n"
                            + " FROM "
                            + "    public.fields \n"
                            + " WHERE \n"
                            + "    form_id LIKE '" + newFormId + "' \n");
                    aceptedCurrent = false;
                    while (rs2.next()) {
                        if (rs2.getString("field_name").compareTo(rs.getString("name_expected")) == 0) {
                            aceptedCurrent = true;
                            break;
                        }
                    }
                    if (!aceptedCurrent) {//nombre esperado no es aceptado
                        validRelationVariables++;//no se validaron todas
                    } else {//nombre esperado es aceptado verificar el encontrado                        
                        aceptedCurrent = false;
                        for (int i = 0; i < headerFileNames.size(); i++) {
                            if (headerFileNames.get(i).compareTo(rs.getString("name_found")) == 0) {
                                aceptedCurrent = true;
                                break;
                            }
                        }
                        if (!aceptedCurrent) {//nombre encontrado no es aceptado
                            validRelationVariables++;//no se validaron todas
                        } else {//valor encontrado y esperado son validos
                            acceptedRelations.add(rs.getString("name_expected") + "->" + rs.getString("name_found"));
                        }
                    }
                }
                //System.out.println("actual nombre 3: " + newGroupRelationsName);
                if (validRelationVariables != 0) {//si no se pudieron validar todas mostrar dialog                    
                    //determino el nombre para la copia
                    String newName = "";
                    int numberOfRelation = 2;
                    boolean nameDetermined = false;
                    List<RelationGroup> relationGroupList = relationGroupFacade.findAll();
                    boolean nameFound;
                    //System.out.println("actual nombre 20: " + newGroupRelationsName);
                    while (!nameDetermined) {
                        newName = newRelationsGroupName + "(" + numberOfRelation + ")";
                        nameFound = false;
                        for (int i = 0; i < relationGroupList.size(); i++) {
                            if (relationGroupList.get(i).getNameRelationGroup().compareTo(newName) == 0) {
                                nameFound = true;
                                break;
                            }
                        }
                        if (!nameFound) {//si no esta repetido salgo
                            nameDetermined = true;
                        }
                        numberOfRelation++;
                    }

                    //System.out.println("nombre aceptado " + newName);
                    aceptedRelationsName = newName;
                    inconsistentRelationsDialog = "inconsistentRelationsDialog.show()";
                }
            } catch (Exception e) {
                System.out.println("Error 11 en " + this.getClass().getName() + ":" + e.toString());
            }
        }
    }

    private void uploadXls() throws IOException {
        /*
         * realizar la carga de una archivo desde un xlsx
         */
        error = "";
        try {
            File file2 = new File(file.getFileName());
            tuplesProcessed = 0;
            countNulos = 0;
            try {//procesar el archivo
                OPCPackage container;
                container = OPCPackage.open(file2.getAbsolutePath());
                ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(container);
                XSSFReader xssfReader = new XSSFReader(container);
                StylesTable styles = xssfReader.getStylesTable();
                XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
                while (iter.hasNext()) {
                    InputStream stream = iter.next();
                    InputSource sheetSource = new InputSource(stream);
                    SAXParserFactory saxFactory = SAXParserFactory.newInstance();
                    try {
                        SAXParser saxParser = saxFactory.newSAXParser();
                        XMLReader sheetParser = saxParser.getXMLReader();
                        ContentHandler handler = new XSSFSheetXMLHandler(styles, strings, new SheetContentsHandler() {
                            ArrayList<String> rowFileData;

                            @Override
                            public void startRow(int rowNum) {
                                //System.out.println(" INICIA " + String.valueOf(rowNum) + "-----------------------");
                                rowFileData = new ArrayList<String>();
                            }

                            @Override
                            public void endRow() {
                                //System.out.println(" FINALIZA: -----------------------");
                                if (tuplesProcessed == 0) {
                                    headerFileNames = prepareArray(rowFileData);
                                    addTableProjectColumns();//registrar en project_columns
                                } else {
                                    int empyColumns = headerFileNames.size() - rowFileData.size();
                                    for (int i = 0; i < empyColumns; i++) {
                                        rowFileData.add("");//completar casillas vacias
                                    }
                                    addTableProjectRecords(rowFileData, tuplesProcessed);//registrar en project_records
                                }
                                tuplesProcessed++;
                            }

                            @Override
                            public void cell(String cellReference, String formattedValue) {
                                //System.out.println("CELDA:"+cellReference + "   VALOR." + formattedValue);
                                CellReference a = new CellReference(cellReference);
                                int empyColumns = a.getCol() - rowFileData.size();
                                for (int i = 0; i < empyColumns; i++) {
                                    rowFileData.add("");//completar casillas vacias
                                }
                                rowFileData.add(formattedValue.replace("\n", " "));
                            }

                            @Override
                            public void headerFooter(String text, boolean isHeader, String tagName) {
                            }
                        }, false);//means result instead of formula                                
                        sheetParser.setContentHandler(handler);
                        sheetParser.parse(sheetSource);
                    } catch (ParserConfigurationException e) {
                        System.out.println("Error 12 en " + this.getClass().getName() + ":" + e.toString());
                        //throw new RuntimeException("SAX parser appears to be broken - " + e.getMessage());
                    }
                    stream.close();
                    break;
                }
            } catch (InvalidFormatException e) {
                System.out.println("Error 13 en " + this.getClass().getName() + ":" + e.toString());
            } catch (SAXException e) {
                System.out.println("Error 14 en " + this.getClass().getName() + ":" + e.toString());
            } catch (OpenXML4JException e) {
                System.out.println("Error 15 en " + this.getClass().getName() + ":" + e.toString());
            }
            addTableProjectRecords(null, -1);
            System.out.println("Fin de procesamiento: nulos:" + countNulos);
        } catch (Exception e) {
            System.out.println("Error 16 en " + this.getClass().getName() + ":" + e.toString());
            error = "El archivo especificado no pudo ser leido 2";
        }
    }

    public void postProcessXLS(Object document) {
        /*
         * crear un archivo xlsx con los registros del proyecto actual
         */
        if (currentProjectId > 0) {

            HSSFWorkbook book = (HSSFWorkbook) document;
            HSSFSheet sheet = book.getSheetAt(0);// Se toma hoja del libro
            HSSFRow row;
            HSSFCellStyle cellStyle = book.createCellStyle();
            HSSFFont font = book.createFont();
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            cellStyle.setFont(font);

            //ArrayList<RowDataTable> moreInfoDataTableList = new ArrayList<RowDataTable>();
            ArrayList<String> titles = new ArrayList<String>();
            try {
                int rowNumber = 0;
                ResultSet rs = connectionJdbcMB.consult(""
                        + " SELECT "
                        + " 	project_columns.column_name, "
                        + "     project_columns.column_id "
                        + " FROM "
                        + " 	public.project_columns, "
                        + "     public.projects "
                        + " WHERE "
                        + "     project_columns.column_id >= projects.start_column_id AND "
                        + "     project_columns.column_id <= projects.end_column_id AND "
                        + "     projects.project_id = " + currentProjectId + " "
                        + " GROUP BY "
                        + "     project_columns.column_name, "
                        + "     project_columns.column_id "
                        + " ORDER BY "
                        + "     project_columns.column_id ");
                while (rs.next()) {
                    titles.add(rs.getString(1));
                }
                row = sheet.createRow(rowNumber);//se crea fila
                for (int i = 0; i < titles.size(); i++) {
                    createCell(cellStyle, row, i, titles.get(i));
                }
                rs = connectionJdbcMB.consult(""
                        + " SELECT "
                        + "    project_records.project_id, "
                        + "    project_records.record_id, "
                        + "    array_agg(project_columns.column_name || '<=>' || project_records.data_value) "
                        + " FROM "
                        + "    project_records,project_columns "
                        + " WHERE "
                        + "    project_records.project_id = " + currentProjectId + " AND "
                        + "    project_columns.column_id = project_records.column_id  "
                        + " GROUP BY "
                        + "    project_records.project_id, "
                        + "    project_records.record_id ");

                while (rs.next()) {
                    //en la tercer columna esta definido un arreglo con id_columna <=> valor encontrado
                    rowNumber++;
                    row = sheet.createRow(rowNumber);//se crea fila                
                    String[] newRow = new String[titles.size()];
                    Object[] arrayInJava = (Object[]) rs.getArray(3).getArray();
                    for (int i = 0; i < arrayInJava.length; i++) {
                        String splitElement[] = arrayInJava[i].toString().split("<=>");
                        for (int j = 0; j < titles.size(); j++) {
                            if (titles.get(j).compareTo(splitElement[0]) == 0) {
                                newRow[j] = splitElement[1];
                                break;
                            }
                        }
                    }
                    for (int i = 0; i < newRow.length; i++) {
                        createCell(row, i, newRow[i]);
                    }
                }
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Archivo exportado"));
            } catch (SQLException ex) {
                System.out.println("Error 17 en " + this.getClass().getName() + ":" + ex.toString());
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe cargar un proyecto"));
        }
    }

    private void createCell(HSSFRow fila, int position, String value) {
        /*
         * creacion de una celda en un xlsx determinada una fila y columna(position)
         */
        HSSFCell cell;
        cell = fila.createCell((short) position);// Se crea una cell dentro de la fila                        
        cell.setCellValue(new HSSFRichTextString(value));
    }

    private void createCell(HSSFCellStyle cellStyle, HSSFRow fila, int position, String value) {
        /*
         * creacion de una celda en un xlsx determinada una fila y columna(position) y un 
         * determinado estilo para la celda(normalmente el estilo es BOLD:negrita)
         */
        HSSFCell cell;
        cell = fila.createCell((short) position);// Se crea una cell dentro de la fila                        
        cell.setCellValue(new HSSFRichTextString(value));
        cell.setCellStyle(cellStyle);
    }

    private void addTableProjectColumns() {
        try {
            //determino el maximo de project_columns            
            if (sb2 != null) {
                sb2.delete(0, sb2.length());
            }
            //reader2 = new PushbackReader(new StringReader(""), 10000);
            headerFileIds = new ArrayList<Long>();
            sb2 = new StringBuilder();
            ResultSet rs = connectionJdbcMB.consult("select max(column_id) from project_columns;");
            rs.next();
            long max = 0;
            try {
                max = rs.getLong(1);
            } catch (Exception e) {
                max = 0;
            }
            startColumnId = max + 1;
            for (int i = 0; i < headerFileNames.size(); i++) {
                max++;
                sb2.
                        append(max).append("\t").
                        append(headerFileNames.get(i)).append("\n");
                headerFileIds.add(max);
                endColumnId = max;
            }
            //reader2.unread(sb2.toString().toCharArray());
            cpManager.copyIn("COPY project_columns FROM STDIN", new StringReader(sb2.toString()));
            sb2.delete(0, sb2.length());
            System.out.println("Procesando... " + tuplesProcessed + " cargadas");

        } catch (Exception ex) {
            hubo_error = true;
            System.out.println("Error 18 en " + this.getClass().getName() + ":" + ex.toString());
        }
    }

    private void addTableProjectRecords(ArrayList<String> rowFileData, int numLine) {
        /*
         * AGREGA UN REGISTRO A LA TABLA project_records EN BASE A UN ARRAY LIST
         */

        if (!hubo_error) {
            try {
                if (numLine == -1) {
                    //realizar la insercion de los que sobren
                    //reader.unread(sb.toString().toCharArray());
                    cpManager.copyIn("COPY project_records FROM STDIN", new StringReader(sb.toString()));
                    sb.delete(0, sb.length());
                    System.out.println("Procesando... filas " + tuplesProcessed + " cargadas");
                } else {//continuar agregando los valores para copy                
                    for (int i = 0; i < headerFileNames.size(); i++) {
                        //va_i = i;
                        if (rowFileData.get(i).trim().length() != 0
                                && rowFileData.get(i).compareToIgnoreCase("NULL") != 0
                                && rowFileData.get(i).compareToIgnoreCase("None") != 0) {
                            currentNumberInserts++;
                            sb.
                                    append(currentProjectId).append("\t").
                                    append(numLine).append("\t").
                                    append(headerFileIds.get(i)).append("\t").
                                    append(rowFileData.get(i).trim()).append("\n");
                            if (currentNumberInserts % maxNumberInserts == 0) {//se llego al limite de inserts
                                //reader.unread(sb.toString().toCharArray());
                                cpManager.copyIn("COPY project_records FROM STDIN", new StringReader(sb.toString()));
                                sb.delete(0, sb.length());
                                System.out.println("Procesando... " + tuplesProcessed + " cargadas");
                            }
                        } else {
                            countNulos++;
                        }
                    }
                }
            } catch (Exception ex) {
                hubo_error = true;
                System.out.println("Error 19 en " + this.getClass().getName() + ":" + ex.toString());
                //System.out.println("i esta en: " + va_i + "   tamaño de header: " + headerFileNames.size() + "   tamaño de vector: " + rowFileData.size());
                System.out.println(sb.toString());
            }
        }
    }

    private ArrayList<String> prepareArray(ArrayList<String> rowFile) {
        //preparacion de una cabecera: no se repitan nombres, no inicien con numeros ni tengan simbolos
        String data1, data2;
        for (int i = 0; i < rowFile.size(); i++) {
            if (rowFile.get(i) == null) {
                rowFile.set(i, "x");
            } else {
                if (rowFile.get(i).trim().length() == 0) {
                    rowFile.set(i, "x");
                }
            }
            //COMO ESTA ES LA CABECERA SE DEBE DETERMINAR SI HAY NOMBRES 
            //REPETIDOS O ESPACIOS O CARACTERES NO VALIDOS
            data1 = rowFile.get(i);
            data1 = data1.toLowerCase();//pasar a minusculas
            data1 = data1.replaceAll(" ", "_");//quitar espacioS y acentos
            data1 = data1.replaceAll("ñ", "n");
            data1 = data1.replaceAll("á", "a");
            data1 = data1.replaceAll("é", "e");
            data1 = data1.replaceAll("í", "i");
            data1 = data1.replaceAll("ó", "o");
            data1 = data1.replaceAll("ú", "u");
            data2 = "";
            for (int j = 0; j < data1.length(); j++) {//quitar caracteres no aceptados
                int k = (int) data1.charAt(j);
                if (k >= 97 && k <= 122 || k >= 65 && k <= 90 || k >= 48 && k <= 57 || k == '\t' || k == '_') {
                    data2 = data2 + data1.charAt(j);
                }
            }
            data1 = data2;
            data1 = data1.replaceAll("__", "_");//eliminar dobles raya bajas
            rowFile.set(i, data1);
        }
        //SI HAY NOMBRES REPETIDOS SE LES COLOCA _1 _2 _3.....                
        int count;
        String currentName;
        for (int i = 0; i < rowFile.size(); i++) {
            currentName = rowFile.get(i);
            count = 1;
            for (int j = i + 1; j < rowFile.size(); j++) {
                if (currentName.compareTo(rowFile.get(j)) == 0) {
                    count++;
                    rowFile.set(j, rowFile.get(j) + "_" + String.valueOf(count));
                }
            }
            if (count != 1) {//hubo repetidos
                rowFile.set(i, rowFile.get(i) + "_1");
            }
        }
        //SI EMPIEZA POR NUMERO LE ANTEPONGO RAYA BAJA
        for (int i = 0; i < rowFile.size(); i++) {//si la cadena inicia con un numero, le antepongo una raya baja
            if (rowFile.get(i).startsWith("0") || rowFile.get(i).startsWith("1") || rowFile.get(i).startsWith("2")
                    || rowFile.get(i).startsWith("3") || rowFile.get(i).startsWith("4") || rowFile.get(i).startsWith("5")
                    || rowFile.get(i).startsWith("6") || rowFile.get(i).startsWith("7") || rowFile.get(i).startsWith("8")
                    || rowFile.get(i).startsWith("9")) {
                rowFile.set(i, "_" + rowFile.get(i));
            }
        }

        for (int i = 0; i < rowFile.size(); i++) {//si la cadena inicia con un numero, le antepongo una raya baja
            if (rowFile.get(i).compareTo("id") == 0) {
                rowFile.set(i, rowFile.get(i) + "_" + String.valueOf(i));
                for (int j = 0; j < rowFile.size(); j++) {
                }
            }
        }

        //vuelvo a verificar que no haya nombres repetidos        
        for (int i = 0; i < rowFile.size(); i++) {
            currentName = rowFile.get(i);
            count = 1;
            for (int j = i + 1; j < rowFile.size(); j++) {
                if (currentName.compareTo(rowFile.get(j)) == 0) {
                    count++;
                    rowFile.set(j, rowFile.get(j) + "_" + String.valueOf(count));
                }
            }
            if (count != 1) {//hubo repetidos
                rowFile.set(i, rowFile.get(i) + "_1");
            }
        }
        variablesFound = rowFile;
        return rowFile;
    }

    public void handleFileUpload(FileUploadEvent event) {
        try {
            file = event.getFile();
            copyFile(event.getFile().getFileName(), event.getFile().getInputstream());
            newFileName = file.getFileName();
        } catch (Exception ex) {
            System.out.println("Error 20 en " + this.getClass().getName() + ":" + ex.toString());
            FacesMessage msg = new FacesMessage("Error:", "error al realizar la carga del archivo" + ex.toString());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void createProjectWithAcceptedRelationships() {
        ResultSet rs, rs2;
        if (aceptedRelationsName != null && aceptedRelationsName.length() != 0) {
            //determinar si el nombre ya esta registrado
            try {
                rs = connectionJdbcMB.consult(""
                        + " SELECT "
                        + "    name_relation_group "
                        + " FROM "
                        + "    relation_group "
                        + " WHERE "
                        + "    name_relation_group ILIKE '" + aceptedRelationsName + "'");
                if (rs.next()) {
                    printMessage(FacesMessage.SEVERITY_ERROR, "Error", "ya existe un grupo de relaciones con un nombre igual, se debe digitar otro nombre");
                } else {
                    //realizar la copia de relaciones
                    //buco el id de la relacion seleccionada
                    int sourceRelationsGroupId = relationGroupFacade.findByName(newRelationsGroupName).getIdRelationGroup();
                    RelationGroup newRelationGroup = new RelationGroup(relationGroupFacade.findMaxId() + 1);
                    newRelationGroup.setNameRelationGroup(aceptedRelationsName);
                    newRelationGroup.setUserId(loginMB.getCurrentUser().getUserId());
                    relationGroupFacade.create(newRelationGroup);

                    int maxIdRelationVariables = relationVariablesFacade.findMaxId();

                    //copio la relacion de variables
                    rs = connectionJdbcMB.consult("\n"
                            + " SELECT \n"
                            + "    * \n"
                            + " FROM \n"
                            + "    relation_variables \n"
                            + " WHERE \n"
                            + "    id_relation_group = " + sourceRelationsGroupId + " \n");

                    while (rs.next()) {
                        boolean continueCreation = false;
                        String nameRelation = rs.getString("name_expected") + "->" + rs.getString("name_found");
                        //determino si el grupo de relaciones a copiar es aceptado
                        for (int i = 0; i < acceptedRelations.size(); i++) {
                            if (nameRelation.compareTo(acceptedRelations.get(i)) == 0) {
                                continueCreation = true;
                                break;
                            }
                        }
                        if (continueCreation) {
                            maxIdRelationVariables++;
                            RelationVariables newRelationVariables = new RelationVariables();
                            newRelationVariables.setIdRelationVariables(maxIdRelationVariables);
                            newRelationVariables.setIdRelationGroup(newRelationGroup);
                            newRelationVariables.setNameExpected(rs.getString("name_expected"));
                            newRelationVariables.setNameFound(rs.getString("name_found"));
                            newRelationVariables.setDateFormat(rs.getString("date_format"));
                            newRelationVariables.setFieldType(rs.getString("field_type"));
                            newRelationVariables.setComparisonForCode(rs.getBoolean("comparison_for_code"));
                            relationVariablesFacade.create(newRelationVariables);//persistir en la tabla relation_group
                            //relacion de valores
                            rs2 = connectionJdbcMB.consult("\n"
                                    + " SELECT \n"
                                    + "    * \n"
                                    + " FROM \n"
                                    + "    relation_values \n"
                                    + " WHERE \n"
                                    + "    id_relation_variables = " + rs.getString("id_relation_variables") + " \n");

                            int maxIdRelationValues = relationValuesFacade.findMaxId();
                            while (rs2.next()) {
                                maxIdRelationValues++;
                                RelationValues newRelationValues = new RelationValues();
                                newRelationValues.setIdRelationValues(maxIdRelationValues);
                                newRelationValues.setIdRelationVariables(newRelationVariables);
                                newRelationValues.setNameExpected(rs2.getString("name_expected"));
                                newRelationValues.setNameFound(rs2.getString("name_found"));
                                relationValuesFacade.create(newRelationValues);//persisto el objeto
                            }
                            //valores descartados
                            rs2 = connectionJdbcMB.consult("\n"
                                    + " SELECT \n"
                                    + "    * \n"
                                    + " FROM \n"
                                    + "    relations_discarded_values \n"
                                    + " WHERE \n"
                                    + "    id_relation_variables = " + rs.getString("id_relation_variables") + " \n");
                            int maxIdRelationDiscardedValues = relationsDiscardedValuesFacade.findMaxId();
                            while (rs2.next()) {
                                maxIdRelationDiscardedValues++;
                                RelationsDiscardedValues newRelationDiscardedValues = new RelationsDiscardedValues();
                                newRelationDiscardedValues.setDiscardedValueName(rs2.getString("discarded_value_name"));
                                newRelationDiscardedValues.setIdRelationVariables(newRelationVariables);
                                newRelationDiscardedValues.setIdDiscardedValue(maxIdRelationDiscardedValues);
                                relationsDiscardedValuesFacade.create(newRelationDiscardedValues);//persisto el objeto
                            }
                        }
                    }
                    loadRelatedGroups();
                    newRelationsGroupName = aceptedRelationsName;
                    completeCreateProject();

                    printMessage(FacesMessage.SEVERITY_INFO, "Correcto", "El proyecto (" + lastCreatedProjectName + ") ha sido creado.");
                }
            } catch (Exception e) {
                System.out.println("Error 21 en " + this.getClass().getName() + ":" + e.toString());
                printMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo realizar la copia por: " + e.toString());
            }
        } else {
            printMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe digitar un nombre para el nuevo grupo de relaciones");
        }

    }

    public void createProject2() {
        if (!errorsList.isEmpty()) {
            for (int i = 0; i < errorsList.size(); i++) {
                if (errorsList.get(i).indexOf(", se cargaron") != -1) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto: ", errorsList.get(i)));
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: ", errorsList.get(i)));
                }
            }
        }
    }

    public void createProject() {
        /*
         * CREACION DE UN NUEVO PROYECTO
         */
        inconsistentRelationsDialog = "";
        currentNumberInserts = 0;
        errorsList = new ArrayList<String>();
        if (newProjectName.trim().length() != 0) {//verifico nombre del proyecto
            List<Projects> projectsList = projectsFacade.findAll();
            for (int i = 0; i < projectsList.size(); i++) {
                if (projectsList.get(i).getProjectName().compareTo(newProjectName) == 0) {
                    errorsList.add("El nombre para el proyecto ya se encuentra registrado, porfavor cambielo");
                    break;
                }
            }
        } else {
            errorsList.add("Se debe digitar un nombre para el proyecto");
        }
        //verifico el archivo
        if (file == null || newFileName.trim().length() == 0) {
            errorsList.add("Se debe realizar la seleccion y carga del archivo");
        }
        if (newRelationsGroupName.trim().length() == 0) {
            errorsList.add("Se debe crear o cargar un conjunto de relaciones");
        }
        //System.out.println("actual nombre 0: " + newGroupRelationsName);
        if (errorsList.isEmpty()) {//DETERMINAR SI LAS RELACIONES DE VARIABLES SON COINCIDENTES
            determineInconsistentRelations();//modifica errorsList y inconsistentRelationsDialog
        }
        if (errorsList.isEmpty() && inconsistentRelationsDialog.length() == 0) {//no hay errores ni relaciones inconsitentes
            completeCreateProject();
        }
    }

    private void completeCreateProject() {
        /*
         * crea el proyecto una vez se superaron todas las validaciones
         */
        sb = new StringBuilder();
        try {
            currentProjectId = projectsFacade.findMax() + 1;
            //PERSISTO EL NUEVO PROYECTO---------------------------
            Projects newProject = new Projects(currentProjectId);
            newProject.setFileDelimiter(newDelimiter);
            newProject.setFileName(newFileName);
            newProject.setFormId(newFormId);
            newProject.setProjectName(newProjectName);
            newProject.setRelationGroupName(newRelationsGroupName);
            newProject.setSourceId(newSourceName);
            newProject.setUserId(loginMB.getCurrentUser().getUserId());
            lastCreatedProjectName = newProjectName;
            projectsFacade.create(newProject);
            //PREPARO VARIABLES PARA LA CARGA DE REGISTROS----------------------
            if (file.getFileName().endsWith("xlsx")) {
                uploadXls();
            } else {
                uploadFileDelimiter();
            }
            if (error == null || error.trim().length() == 0) {
                //actualizo inicio y fin de columnas en el proyecto
                newProject = projectsFacade.find(currentProjectId);
                newProject.setStartColumnId(BigInteger.valueOf(startColumnId));
                newProject.setEndColumnId(BigInteger.valueOf(endColumnId));
                projectsFacade.edit(newProject);
                //cargo las variables del proyecto actual                
                currentProjectName = newProjectName;
                currentDelimiter = newDelimiter;
                currentFileName = newFileName;
                if (currentFileName != null && currentFileName.length() != 0) {
                    exportFileName = currentFileName.substring(0, currentFileName.lastIndexOf('.'));
                    exportFileNameDisabled = false;
                }
                Forms currentForm = formsFacade.findByFormId(newFormId);
                currentFormName = currentForm.getFormName();
                currentFormId = currentForm.getFormId();
                currentRelationsGroupName = newRelationsGroupName;
                currentRelationsGroupId = relationGroupFacade.findByName(currentRelationsGroupName).getIdRelationGroup();
                currentSourceName = sourcesFacade.find(newSourceName).getSourceName();
                newProjectName = "";
                newFileName = "";
                newGroupRelationsName = "";
                newRelationsGroupName = "";
                Users currentUser = loginMB.getCurrentUser();//Asigno este proyecto al usuario que lo abrio
                currentUser.setProjectId(currentProjectId);
                usersFacade.edit(currentUser);
                inactiveTabs = false;//activo las pestañas
                relationshipOfVariablesMB.refresh();
                configurationLoaded = true;
                copyMB.refresh();//actualizo pestaña (filtros)                
                copyMB.cleanBackupTables();
                errorsList.add("Proyecto creado correctamente, se cargaron " + tuplesProcessed + " registros.");
            } else {
                errorsList.add("Ocurrió un error procesando el archivo, " + error);
            }
            loadProjects();
        } catch (Exception ex) {
            System.out.println("Error 22 en " + this.getClass().getName() + ":" + ex.toString());
            errorsList.add("Ocurrió un error procesando el archivo, " + ex.toString());
            //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ocurrió un error procesando el archivo", ex.toString()));
        }
    }

    public void openProject() {
        /*
         * ABRIR PROYECTO DESDE "PROYECTOS ALMACENADOS"
         */
        if (selectedProjectTable != null) {
            Projects openProject = projectsFacade.find(Integer.parseInt(selectedProjectTable.getColumn1()));
            if (openProject != null) {
                if (openProject.getUserId() == loginMB.getCurrentUser().getUserId()) {
                    currentProjectId = projectsFacade.find(Integer.parseInt(selectedProjectTable.getColumn1())).getProjectId();
                    currentProjectName = openProject.getProjectName();
                    currentDelimiter = openProject.getFileDelimiter();
                    currentFileName = openProject.getFileName();
                    if (currentFileName != null && currentFileName.length() != 0) {
                        exportFileName = currentFileName.substring(0, currentFileName.lastIndexOf('.'));
                        exportFileNameDisabled = false;
                    }
                    currentFormName = formsFacade.findByFormId(openProject.getFormId()).getFormName();
                    currentFormId = formsFacade.findByFormId(openProject.getFormId()).getFormId();
                    currentRelationsGroupName = openProject.getRelationGroupName();
                    currentRelationsGroupId = relationGroupFacade.findByName(currentRelationsGroupName).getIdRelationGroup();
                    currentSourceName = sourcesFacade.find(openProject.getSourceId()).getSourceName();
                    newProjectName = "";
                    newFileName = "";

                    newGroupRelationsName = "";
                    newRelationsGroupName = "";
                    //Asigno este proyecto al usuario que lo abrio
                    Users currentUser = loginMB.getCurrentUser();
                    currentUser.setProjectId(currentProjectId);
                    usersFacade.edit(currentUser);
                    //activo las pestañas
                    inactiveTabs = false;
                    relationshipOfVariablesMB.refresh();
                    configurationLoaded = true;
                    //actualizo pestaña (filtros)                
                    copyMB.refresh();
                    copyMB.cleanBackupTables();


                    printMessage(FacesMessage.SEVERITY_INFO, "Correcto", "El proyecto " + currentProjectName + " ha sido cargado");
                } else {
                    printMessage(FacesMessage.SEVERITY_ERROR, "Error", "El proyecto pertenece a otro usuario, para hacer uso de este debe crear una copia y posteriormente abrirla");
                }
            } else {
                printMessage(FacesMessage.SEVERITY_ERROR, "Error", "El proyecto no pudo ser encontrado");
            }
        } else {
            printMessage(FacesMessage.SEVERITY_ERROR, "Error", "Debe seleccionarse un proyecto de la tabla");
        }
    }

    public void openProject(int proyectId) {
        /*
         * ABRIR PROYECTO CUANDO USUARIO INICIA SESION
         */
        //System.out.println("Se procede a cargar ultimo proyecto usuario");
        Projects openProject = projectsFacade.find(proyectId);
        if (openProject != null) {
            if (openProject.getUserId() == loginMB.getCurrentUser().getUserId()) {
                currentProjectId = proyectId;
                currentProjectName = openProject.getProjectName();
                currentDelimiter = openProject.getFileDelimiter();
                currentFileName = openProject.getFileName();
                if (currentFileName != null && currentFileName.length() != 0) {
                    exportFileName = currentFileName.substring(0, currentFileName.lastIndexOf('.'));
                    exportFileNameDisabled = false;
                }
                currentFormName = formsFacade.findByFormId(openProject.getFormId()).getFormName();
                currentFormId = formsFacade.findByFormId(openProject.getFormId()).getFormId();
                currentRelationsGroupName = openProject.getRelationGroupName();
                currentRelationsGroupId = relationGroupFacade.findByName(currentRelationsGroupName).getIdRelationGroup();
                currentSourceName = sourcesFacade.find(openProject.getSourceId()).getSourceName();
                newProjectName = "";
                newFileName = "";
                newGroupRelationsName = "";
                newRelationsGroupName = "";
                //Asigno este proyecto al usuario que lo abrio
                Users currentUser = loginMB.getCurrentUser();
                currentUser.setProjectId(currentProjectId);
                usersFacade.edit(currentUser);
                //activo las pestañas
                inactiveTabs = false;
                relationshipOfVariablesMB.refresh();
                configurationLoaded = true;
                //actualizo pestaña (filtros)                
                copyMB.refresh();
                copyMB.cleanBackupTables();
                //recargo los combos                
                printMessage(FacesMessage.SEVERITY_INFO, "Correcto", "El proyecto " + currentProjectName + " ha sido cargado");
            } else {
                printMessage(FacesMessage.SEVERITY_ERROR, "Error", "El proyecto pertenece a otro usuario, para hacer uso de este debe crear una copia y posteriormente abrirla");
            }
        } else {
            printMessage(FacesMessage.SEVERITY_ERROR, "Error", "El proyecto no pudo ser encontrado");
        }
    }

    public void removeProject() {
        String sql = "";
        if (selectedProjectTable != null) {
            Projects openProject = projectsFacade.find(Integer.parseInt(selectedProjectTable.getColumn1()));
            if (openProject != null) {
                if (openProject.getUserId() == loginMB.getCurrentUser().getUserId()) {
                    String nameProjet = openProject.getProjectName();
                    //---------------------------------------------------------
                    try {
                        //determino e inicio de la columnas
                        sql = " \n"
                                + " SELECT \n"
                                + "    start_column_id, \n"
                                + "    end_column_id \n"
                                + " FROM \n"
                                + "    projects \n"
                                + " WHERE \n"
                                + "        project_id=" + openProject.getProjectId() + " \n";
                        ResultSet rs = connectionJdbcMB.consult(sql);
                        if (rs.next()) {
                            sql = " \n"
                                    + " DELETE from \n"
                                    + "    project_columns \n"
                                    + " WHERE \n"
                                    + "    column_id IN \n"
                                    + "    (SELECT "
                                    + "        column_id "
                                    + "     FROM "
                                    + "        project_columns "
                                    + "     WHERE "
                                    + "        column_id between " + rs.getString(1) + " AND " + rs.getString(2) + " "
                                    + "    )";
                            //System.out.println("Eliminado project_columns" + sql);
                            connectionJdbcMB.non_query(sql);
                        }
                    } catch (SQLException ex) {
                        System.out.println("Error 23 en " + this.getClass().getName() + ":" + ex.toString());

                    }
                    //---------------------------------------------------------
                    sql = " \n"
                            + " delete from \n"
                            + "    project_records \n"
                            + " where \n"
                            + "    project_id=" + openProject.getProjectId() + " \n";
                    //System.out.println("Eliminado project_records" + sql);
                    connectionJdbcMB.non_query(sql);
                    //---------------------------------------------------------
                    sql = ""
                            + " delete from \n"
                            + "    projects \n"
                            + " where \n"
                            + "    project_id=" + openProject.getProjectId() + " \n";
                    //System.out.println("Eliminado projects" + sql);
                    connectionJdbcMB.non_query(sql);

                    if (openProject.getProjectId() == currentProjectId) {//proyecto actual es el actual
                        Users currentUser = loginMB.getCurrentUser();
                        currentUser.setProjectId(null);
                        usersFacade.edit(currentUser);
                        this.reset();
                    } else {
                        loadProjects();
                    }
                    printMessage(FacesMessage.SEVERITY_INFO, "Correcto", "El proyecto " + nameProjet + " se ha eliminado");
                } else {
                    printMessage(FacesMessage.SEVERITY_ERROR, "Error", "El proyecto pertenece a otro usuario.");
                }
            } else {
                printMessage(FacesMessage.SEVERITY_ERROR, "Error", "El proyecto no pudo ser encontrado");
            }
        } else {
            printMessage(FacesMessage.SEVERITY_ERROR, "Error", "Debe seleccionarse un proyecto de la tabla");
        }
    }

    public void loadConfigurationUser() {
        if (configurationLoaded) {//la configuracion ya se cargo una vez, no hacer nada            
        } else {// es la primera vez que entra al sistema se debe cargar configuracion
            configurationLoaded = true;
            relationshipOfVariablesMB.reset();
            relationshipOfValuesMB.reset();
            //storedRelationsMB.reset();
            recordDataMB.reset();
            errorsControlMB.reset();
            this.reset();
            inactiveTabs = true;
            //este metodo debe recargar los diferentes proyectos
        }
    }

    public void createRelationVariablesGroup() {
        boolean continueProcess = true;
        List<RelationGroup> relationGroupList = relationGroupFacade.findAll();
        //System.out.println("&&&&&&&" + newGroupRelationsName);
        if (newGroupRelationsName.trim().length() == 0) {
            continueProcess = false;
            printMessage(FacesMessage.SEVERITY_ERROR, "Error", "Debe digitar un nombre para el nuevo grupo de relaciones");
        }
        if (continueProcess) {
            for (int i = 0; i < relationGroupList.size(); i++) {
                if (relationGroupList.get(i).getNameRelationGroup().compareTo(newGroupRelationsName) == 0) {
                    printMessage(FacesMessage.SEVERITY_ERROR, "Error", "el nombre para el nuevo grupo de relaciones ya se encuentra registrado, se debe ingresar un nombre diferente");
                    continueProcess = false;
                    break;
                }
            }
        }
        if (continueProcess) {
            RelationGroup newRelationGroup = new RelationGroup(relationGroupFacade.findMaxId() + 1);
            newRelationGroup.setFormId(formsFacade.findByFormId(newFormId));
            newRelationGroup.setNameRelationGroup(newGroupRelationsName);
            newRelationGroup.setSourceId(newSourceName);
            newRelationGroup.setUserId(loginMB.getCurrentUser().getUserId());
            relationGroupFacade.create(newRelationGroup);
            newRelationsGroupName = newGroupRelationsName;
            loadRelatedGroups();
            printMessage(FacesMessage.SEVERITY_INFO, "Correcto", "El grupo de relaciones (" + newGroupRelationsName + ") ha sido creado.");
        }
    }

    public void removeRelationGroup() {
        if (currentRelationsGroupNameInLoad != null && currentRelationsGroupNameInLoad.trim().length() != 0) {
            try {
                //verificar que no se este usando en un proyecto
                ResultSet rs = connectionJdbcMB.consult(""
                        + " SELECT "
                        + "  * "
                        + " FROM "
                        + "  public.projects "
                        + " WHERE "
                        + "  relation_group_name ILIKE '" + currentRelationsGroupNameInLoad + "'");
                if (rs.next()) {
                    printMessage(FacesMessage.SEVERITY_ERROR, "Error", "El proyecto que desea eliminar se esta utilizando en otro proyecto");
                } else {
                    //encuentro el id de la relacio
                    rs = connectionJdbcMB.consult(""
                            + " SELECT "
                            + "    id_relation_group "
                            + " FROM "
                            + "    relation_group"
                            + " WHERE"
                            + "    name_relation_group ILIKE '" + currentRelationsGroupNameInLoad + "'");
                    if (rs.next()) {
                        String idRelation = rs.getString(1);
                        //elimino descartados
                        connectionJdbcMB.non_query(""
                                + " delete from "
                                + "	relations_discarded_values "
                                + " where "
                                + "	relations_discarded_values.id_discarded_value IN  "
                                + "	( "
                                + "	select "
                                + "		relations_discarded_values.id_discarded_value "
                                + "	from  "
                                + "		relation_variables, "
                                + "		relations_discarded_values, "
                                + "		relation_group "
                                + "	where "
                                + "		relation_variables.id_relation_variables = relations_discarded_values.id_relation_variables AND "
                                + "		relation_variables.id_relation_group = relation_group.id_relation_group AND "
                                + "		relation_group.id_relation_group = " + rs.getString(1) + " "
                                + "	)");
                        //elimino relaciones de valores
                        connectionJdbcMB.non_query(""
                                + " delete from "
                                + "	relation_values "
                                + " where "
                                + "	relation_values.id_relation_values IN  "
                                + "	( "
                                + "	select "
                                + "		relation_values.id_relation_values "
                                + "	from  "
                                + "		relation_variables, "
                                + "		relation_values, "
                                + "		relation_group "
                                + "	where "
                                + "		relation_variables.id_relation_variables = relation_values.id_relation_variables AND "
                                + "		relation_variables.id_relation_group = relation_group.id_relation_group AND "
                                + "		relation_group.id_relation_group = " + rs.getString(1) + " "
                                + "	)");
                        //elimino relaciones de variables
                        connectionJdbcMB.non_query(""
                                + " delete from "
                                + "	relation_variables "
                                + " where "
                                + "	relation_variables.id_relation_variables IN  "
                                + "	( "
                                + "	select "
                                + "		relation_variables.id_relation_variables "
                                + "	from  "
                                + "		relation_variables, "
                                + "		relation_group "
                                + "	where		 "
                                + "		relation_variables.id_relation_group = relation_group.id_relation_group AND "
                                + "		relation_group.id_relation_group = " + rs.getString(1) + " "
                                + "	)");
                        //elimino el conjunto de relaciones
                        connectionJdbcMB.non_query(""
                                + " delete from "
                                + " 	relation_group "
                                + " where "
                                + " 	relation_group.id_relation_group = " + rs.getString(1) + " ");
                        loadRelatedGroups();
                        printMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Grupo de relaciones eliminado");
                    } else {
                        printMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se encontro el grupo de relaciones");
                    }
                }
            } catch (Exception e) {
                System.out.println("Error 24 en " + this.getClass().getName() + ":" + e.toString());
            }
        } else {
            printMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar un grupo de relaciones");
        }
    }

    public void createRelationsCopy() {
        ResultSet rs, rs2;
        if (newRelationsCopyName != null && newRelationsCopyName.length() != 0) {
            //determinar si el nombre ya esta registrado
            try {
                rs = connectionJdbcMB.consult(""
                        + " SELECT "
                        + "    name_relation_group "
                        + " FROM "
                        + "    relation_group "
                        + " WHERE "
                        + "    name_relation_group ILIKE '" + newRelationsCopyName + "'");
                if (rs.next()) {
                    printMessage(FacesMessage.SEVERITY_ERROR, "Error", "ya existe un proyecto con un nombre igual, se debe digitar otro nombre");
                } else {
                    //realizar la copia de relaciones
                    //buco el id de la relacion seleccionada
                    int sourceRelationsGroupId = relationGroupFacade.findByName(selectedRelationsNameInCopy).getIdRelationGroup();
                    RelationGroup newRelationGroup = new RelationGroup(relationGroupFacade.findMaxId() + 1);
                    //newRelationGroup.setFormId(formsFacade.findByFormId(newFormId));
                    //newRelationGroup.setSourceId(newSourceName);
                    newRelationGroup.setNameRelationGroup(newRelationsCopyName);
                    newRelationGroup.setUserId(loginMB.getCurrentUser().getUserId());
                    relationGroupFacade.create(newRelationGroup);


                    int maxIdRelationVariables = relationVariablesFacade.findMaxId();

                    //copio la relacion de variables
                    rs = connectionJdbcMB.consult("\n"
                            + " SELECT \n"
                            + "    * \n"
                            + " FROM \n"
                            + "    relation_variables \n"
                            + " WHERE \n"
                            + "    id_relation_group = " + sourceRelationsGroupId + " \n");
                    while (rs.next()) {
                        maxIdRelationVariables++;
                        RelationVariables newRelationVariables = new RelationVariables();
                        newRelationVariables.setIdRelationVariables(maxIdRelationVariables);
                        newRelationVariables.setIdRelationGroup(newRelationGroup);
                        newRelationVariables.setNameExpected(rs.getString("name_expected"));
                        newRelationVariables.setNameFound(rs.getString("name_found"));
                        newRelationVariables.setDateFormat(rs.getString("date_format"));
                        newRelationVariables.setFieldType(rs.getString("field_type"));
                        newRelationVariables.setComparisonForCode(rs.getBoolean("comparison_for_code"));
                        relationVariablesFacade.create(newRelationVariables);//persistir en la tabla relation_group
                        //relacion de valores
                        rs2 = connectionJdbcMB.consult("\n"
                                + " SELECT \n"
                                + "    * \n"
                                + " FROM \n"
                                + "    relation_values \n"
                                + " WHERE \n"
                                + "    id_relation_variables = " + rs.getString("id_relation_variables") + " \n");

                        int maxIdRelationValues = relationValuesFacade.findMaxId();
                        while (rs2.next()) {
                            maxIdRelationValues++;
                            RelationValues newRelationValues = new RelationValues();
                            newRelationValues.setIdRelationValues(maxIdRelationValues);
                            newRelationValues.setIdRelationVariables(newRelationVariables);
                            newRelationValues.setNameExpected(rs2.getString("name_expected"));
                            newRelationValues.setNameFound(rs2.getString("name_found"));
                            relationValuesFacade.create(newRelationValues);//persisto el objeto
                        }
                        //valores descartados
                        rs2 = connectionJdbcMB.consult("\n"
                                + " SELECT \n"
                                + "    * \n"
                                + " FROM \n"
                                + "    relations_discarded_values \n"
                                + " WHERE \n"
                                + "    id_relation_variables = " + rs.getString("id_relation_variables") + " \n");
                        int maxIdRelationDiscardedValues = relationsDiscardedValuesFacade.findMaxId();
                        while (rs2.next()) {
                            maxIdRelationDiscardedValues++;
                            RelationsDiscardedValues newRelationDiscardedValues = new RelationsDiscardedValues();
                            newRelationDiscardedValues.setDiscardedValueName(rs2.getString("discarded_value_name"));
                            newRelationDiscardedValues.setIdRelationVariables(newRelationVariables);
                            newRelationDiscardedValues.setIdDiscardedValue(maxIdRelationDiscardedValues);
                            relationsDiscardedValuesFacade.create(newRelationDiscardedValues);//persisto el objeto
                        }
                    }
                    printMessage(FacesMessage.SEVERITY_INFO, "Correcto", "El grupo de relaciones \"" + newRelationsCopyName + "\" ha sido creado.");
                    newRelationsCopyName = "";
                    selectedRelationsNameInCopy = "";
                    loadRelatedGroups();
                }
            } catch (Exception e) {
                System.out.println("Error 25 en " + this.getClass().getName() + ":" + e.toString());
                printMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo realizar la copia por: " + e.toString());
            }
        } else {
            printMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe digitar un nombre para el nuevo grupo de relaciones");
        }
    }

    public void loadGroup() {
        if (currentRelationsGroupNameInLoad != null && currentRelationsGroupNameInLoad.trim().length() != 0) {
            newRelationsGroupName = currentRelationsGroupNameInLoad;
            printMessage(FacesMessage.SEVERITY_INFO, "Correcto", "El grupo de relaciones se ha cargado");
        } else {
            printMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar un grupo de relaciones");
        }
    }

    public void clearRelationGroup() {
        newRelationsGroupName = "";
    }

//    public void corregir() {
//        try {
//            String line;
//            InputStreamReader isr;
//            BufferedReader buffer;
//            headerFileNames = new ArrayList<String>();
//            String[] tupla;
//            isr = new InputStreamReader(file.getInputstream());
//            buffer = new BufferedReader(isr);
//            //Leer el la informacion del archivo linea por linea                       
//            ArrayList<String> descartarlos = new ArrayList<String>();
//            while ((line = buffer.readLine()) != null) {
//                tupla = line.split("\t");
//                boolean esta = false;
//                for (int i = 0; i < descartarlos.size(); i++) {
//                    if (descartarlos.get(i).compareTo(tupla[0]) == 0) {
//                        esta = true;
//                        break;
//                    }
//                }
//                if (!esta) {
//                    descartarlos.add(tupla[0]);
//                }
//                esta = false;
//                for (int i = 0; i < descartarlos.size(); i++) {
//                    if (descartarlos.get(i).compareTo(tupla[1]) == 0) {
//                        esta = true;
//                        break;
//                    }
//                }
//                if (!esta) {
//                    System.out.println("\n DELETE FROM relation_values WHERE id_relation_values = " + tupla[1] + ";");
//                    descartarlos.add(tupla[1]);
//                }
//            }
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "correcto", "finalizado"));
//        } catch (IOException e) {
//            System.out.println("Error 3 en " + this.getClass().getName() + ":" + e.toString());
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ocurrió un error al cargar el archivo", e.toString()));
//        } catch (Exception ex) {
//            System.out.println("Error 4 en " + this.getClass().getName() + ":" + ex.toString());
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ocurrió un error al cargar el archivo", ex.toString()));
//        }
//    }
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES GET Y SET DE LAS VARIABLES ---------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public String getCurrentFormName() {
        return currentFormName;
    }

    public void setCurrentFormName(String currentFormName) {
        this.currentFormName = currentFormName;

    }

    public String getNewFormId() {
        return newFormId;
    }

    public void setNewFormId(String newFormId) {
        this.newFormId = newFormId;
    }

    public SelectItem[] getForms() {
        return forms;
    }

    public void setForms(SelectItem[] forms) {
        this.forms = forms;
    }

    public int getNewSourceName() {
        return newSourceName;
    }

    public void setNewSourceName(int newSourceName) {
        this.newSourceName = newSourceName;
    }

    public String getCurrentSourceName() {
        return currentSourceName;
    }

    public void setCurrentSourceName(String currentSourceName) {
        this.currentSourceName = currentSourceName;
    }

    public SelectItem[] getSources() {
        return sources;
    }

    public void setSources(SelectItem[] suppliers) {
        this.sources = suppliers;
    }

    public List<String> getVariablesFound() {
        variablesFound = new ArrayList<String>();
        reloadVarsFound();
        variablesFound = headerFileNames;
        return variablesFound;
    }

    public void setVariablesFound(List<String> variablesFound) {
        this.variablesFound = variablesFound;
    }

    public List<String> getVariablesExpected() {
        loadVarsExpected();
        return variablesExpected;
    }

    public void setVariablesExpected(List<String> variablesExpected) {
        this.variablesExpected = variablesExpected;
    }

    public String getCurrentFileName() {
        return currentFileName;
    }

    public void setCurrentFileName(String currentFileName) {
        this.currentFileName = currentFileName;
    }

    public String getNewFileName() {
        return newFileName;
    }

    public void setNewFileName(String newFileName) {
        this.newFileName = newFileName;
    }

    public String getCurrentDelimiter() {
        return currentDelimiter;
    }

    public void setCurrentDelimiter(String currentDelimiter) {
        this.currentDelimiter = currentDelimiter;
    }

    public String getNewDelimiter() {
        return newDelimiter;
    }

    public void setNewDelimiter(String newDelimiter) {
        this.newDelimiter = newDelimiter;
    }

    public SelectItem[] getDelimiters() {
        return delimiters;
    }

    public void setDelimiters(SelectItem[] delimiters) {
        this.delimiters = delimiters;
    }

    public String getNewProjectName() {
        return newProjectName;
    }

    public void setNewProjectName(String newProjectName) {
        this.newProjectName = newProjectName;
    }

    public String getCurrentProjectName() {
        return currentProjectName;
    }

    public void setCurrentProjectName(String currentProjectName) {
        this.currentProjectName = currentProjectName;
    }

    public void setRelationshipOfVariablesMB(RelationshipOfVariablesMB relationshipOfVariablesMB) {
        this.relationshipOfVariablesMB = relationshipOfVariablesMB;
    }

//    public void setStoredRelationsMB(StoredRelationsMB storedRelationsMB) {
//        this.storedRelationsMB = storedRelationsMB;
//    }
    public boolean isInactiveTabs() {
        return inactiveTabs;
    }

    public void setInactiveTabs(boolean inactiveTabs) {
        this.inactiveTabs = inactiveTabs;
    }

    public Integer getTuplesProcessed() {
        return tuplesProcessed;
    }

    public void setTuplesProcessed(Integer tuplesProcessed) {
        this.tuplesProcessed = tuplesProcessed;
    }

    public List<RowDataTable> getRowDataTableList() {
        return rowProjectsTableList;
    }

    public void setRowDataTableList(List<RowDataTable> rowDataTableList) {
        this.rowProjectsTableList = rowDataTableList;
    }

    public RowDataTable getSelectedProjectTable() {
        return selectedProjectTable;
    }

    public void setSelectedProjectTable(RowDataTable selectedProjectTable) {
        this.selectedProjectTable = selectedProjectTable;
    }

    public String getNewRelationsGroupName() {
        return newRelationsGroupName;
    }

    public void setNewRelationsGroupName(String newRelationsGroupName) {
        this.newRelationsGroupName = newRelationsGroupName;
    }

    public String getSelectedRelationsNameInCopy() {
        return selectedRelationsNameInCopy;
    }

    public void setSelectedRelationsNameInCopy(String selectedRelationsNameInCopy) {
        this.selectedRelationsNameInCopy = selectedRelationsNameInCopy;
    }

    public String getCurrentRelationsGroupName() {
        return currentRelationsGroupName;
    }

    public void setCurrentRelationsGroupName(String currentRelationsGroupName) {
        this.currentRelationsGroupName = currentRelationsGroupName;
    }

    public String getNewGroupRelationsName() {
        return newGroupRelationsName;
    }

    public void setNewGroupRelationsName(String newGroupRelationsName) {
        this.newGroupRelationsName = newGroupRelationsName;
    }

    public List<String> getRelationGroupsInLoad() {
        return relationGroupsInLoad;
    }

    public void setRelationGroupsInLoad(List<String> relationGroupsInLoad) {
        this.relationGroupsInLoad = relationGroupsInLoad;
    }

    public int getCurrentProjectId() {
        return currentProjectId;
    }

    public void setCurrentProjectId(int currentProjectId) {
        this.currentProjectId = currentProjectId;
    }

    public String getCurrentFormId() {
        return currentFormId;
    }

    public void setCurrentFormId(String currentFormId) {
        this.currentFormId = currentFormId;
    }

    public int getCurrentRelationsGroupId() {
        return currentRelationsGroupId;
    }

    public void setCurrentRelationsGroupId(int currentRelationsGroupId) {
        this.currentRelationsGroupId = currentRelationsGroupId;
    }

    public List<String> getRelationGroupsInCopy() {
        return relationGroupsInCopy;
    }

    public void setRelationGroupsInCopy(List<String> relationGroupsInCopy) {
        this.relationGroupsInCopy = relationGroupsInCopy;
    }

    public String getNewRelationsCopyName() {
        return newRelationsCopyName;
    }

    public void setNewRelationsCopyName(String newRelationsCopyName) {
        this.newRelationsCopyName = newRelationsCopyName;
    }

    public String getCurrentRelationsGroupNameInLoad() {
        return currentRelationsGroupNameInLoad;
    }

    public void setCurrentRelationsGroupNameInLoad(String currentRelationsGroupNameInLoad) {
        this.currentRelationsGroupNameInLoad = currentRelationsGroupNameInLoad;
    }

    public String getExportFileName() {
        return exportFileName;
    }

    public void setExportFileName(String exportFileName) {
        this.exportFileName = exportFileName;
    }

    public boolean isExportFileNameDisabled() {
        return exportFileNameDisabled;
    }

    public void setExportFileNameDisabled(boolean exportFileNameDisabled) {
        this.exportFileNameDisabled = exportFileNameDisabled;
    }

    public String getAceptedRelationsName() {
        return aceptedRelationsName;
    }

    public void setAceptedRelationsName(String aceptedRelationsName) {
        this.aceptedRelationsName = aceptedRelationsName;
    }

    public String getInconsistentRelationsDialog() {
        return inconsistentRelationsDialog;
    }

    public void setInconsistentRelationsDialog(String inconsistentRelationsDialog) {
        this.inconsistentRelationsDialog = inconsistentRelationsDialog;
    }

    public String getToolTipText() {
        return toolTipText;
    }

    public void setToolTipText(String toolTipText) {
        this.toolTipText = toolTipText;
    }
}