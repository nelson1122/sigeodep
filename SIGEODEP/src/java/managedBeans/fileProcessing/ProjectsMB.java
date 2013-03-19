/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.fileProcessing;

import beans.connection.ConnectionJdbcMB;
import beans.util.RowDataTable;
import java.io.*;
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
    UsersFacade usersFacade;
    @EJB
    FatalInjuriesFacade fatalInjuriesFacade;
    @EJB
    NonFatalInjuriesFacade nonFatalInjuriesFacade;
    @EJB
    NonFatalDomesticViolenceFacade nonFatalDomesticViolenceFacade;
    private String newProjectName = "";
    private String newProjectCopyName = "";
    private String currentProjectName = "";
    private String copyRelationsGroupName = "";
    private String newRelationsGroupName = "";
    private String currentRelationsGroupNameInLoad = "";
    private String currentRelationsGroupName = "";
    private int currentRelationsGroupId = -1;
    private List<String> relationGroupsInLoad;
    private List<String> relationGroupsInCopy;
    private String currentFormName = "";//ficha actual
    private String currentFormId = "";//ficha actual
    private String newFormId = "";//ficha actual
    private SelectItem[] forms;
    private int newSourceName = 0;//proveedor actual    
    private String currentSourceName = "";//proveedor actual    
    private SelectItem[] sources;
    private boolean hubo_error = false;
    private SelectItem[] delimiters;
    private UploadedFile file;
    private String currentFileName = "";
    private String newFileName = "";
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
    private Integer tuplesProcessed;
    private String nameTableTemp = "temp";
    private String newDelimiter = "";
    private String currentDelimiter = "";
    private String newGroupRelationsName = "";
    //private String copyGroupRelationsName = "";//nombre usado cuando se realiza la copia de un grupo de relaciones al cargar
    private boolean configurationLoaded = false;//determinar si la configuracion ya se cargo
    private StringBuilder sb;
    private StringBuilder sb2;
    private CopyManager cpManager;
    private int maxNumberInserts = 1000000;//numero de insert por copy realizado
    private int currentNumberInserts = 0;//numero de insert actual

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
        if (copyRelationsGroupName != null && copyRelationsGroupName.trim().length() != 0) {
            newProjectCopyName = copyRelationsGroupName + " (Copia)";
        }
    }

    public void changeRelationGroupInLoad() {
////        newConfigurationName = currentRelationGroupName;
////        if (currentRelationGroupName.trim().length() != 0) {
////            btnLoadConfigurationDisabled = false;
////            btnRemoveConfigurationDisabled = false;
////        }
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
        nameTableTemp = "temp" + loginMB.getLoginname();
        connectionJdbcMB.setTableName(nameTableTemp);

    }

    public void loadProjects() {
        List<Projects> projectsList = projectsFacade.findAll();
        rowProjectsTableList = new ArrayList<RowDataTable>();
        for (int i = 0; i < projectsList.size(); i++) {
            rowProjectsTableList.add(new RowDataTable(
                    projectsList.get(i).getProjectId().toString(),
                    projectsList.get(i).getProjectName().toString(),
                    usersFacade.find(projectsList.get(i).getUserId()).getUserLogin(),
                    formsFacade.findByFormId(projectsList.get(i).getFormId()).getFormName(),
                    sourcesFacade.find(projectsList.get(i).getSourceId()).getSourceName(),
                    projectsList.get(i).getFileName()));
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
        currentFormName = "";
        currentFormId = "";
        currentRelationsGroupName = "";
        currentSourceName = "";
        newProjectName = "";
        newFileName = "";
        newGroupRelationsName = "";
        newRelationsGroupName = "";
        try {
            cpManager = new CopyManager((BaseConnection) connectionJdbcMB.getConn());
        } catch (SQLException ex) {
            Logger.getLogger(ProjectsMB.class.getName()).log(Level.SEVERE, null, ex);
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
        copyRelationsGroupName = "";
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
            System.out.println("Error 1 en " + this.getClass().getName() + ":" + e.toString());
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
            System.out.println("Error 2 en " + this.getClass().getName() + ":" + e.toString());
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

    public void copyFile(String fileName, InputStream in) {
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
            System.out.println(e.getMessage());
        }
    }

    private void uploadFileDelimiter() {
        /*
         * CARGA DE UN ARCHIVO CON DELIMITADOR
         */
        try {
            Long fileSize = file.getSize();
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
            System.out.println("Error 3 en " + this.getClass().getName() + ":" + e.toString());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ocurrió un error al cargar el archivo", e.toString()));
        } catch (Exception ex) {
            System.out.println("Error 4 en " + this.getClass().getName() + ":" + ex.toString());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ocurrió un error al cargar el archivo", ex.toString()));
        }
    }

    private void uploadXls() throws IOException {
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
                                rowFileData.add(formattedValue);
                            }

                            @Override
                            public void headerFooter(String text, boolean isHeader, String tagName) {
                            }
                        }, false);//means result instead of formula                                
                        sheetParser.setContentHandler(handler);
                        sheetParser.parse(sheetSource);
                    } catch (ParserConfigurationException e) {
                        throw new RuntimeException("SAX parser appears to be broken - " + e.getMessage());
                    }
                    stream.close();
                    break;
                }
            } catch (InvalidFormatException e) {
                System.out.println(e.toString());
            } catch (SAXException e) {
                System.out.println(e.toString());
            } catch (OpenXML4JException e) {
                System.out.println(e.toString());
            }
            addTableProjectRecords(null, -1);
            System.out.println("Fin de procesamiento: nulos:" + countNulos);
        } catch (Exception e) {
            System.out.println("Error 5 en " + this.getClass().getName() + ":" + e.toString());
        }
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

            for (int i = 0; i < headerFileNames.size(); i++) {
                max++;
                sb2.
                        append(max).append("\t").
                        append(headerFileNames.get(i)).append("\n");
                headerFileIds.add(max);
            }
            //reader2.unread(sb2.toString().toCharArray());
            cpManager.copyIn("COPY project_columns FROM STDIN", new StringReader(sb2.toString()));
            sb2.delete(0, sb2.length());
            System.out.println("Procesando... " + tuplesProcessed + " cargadas");

        } catch (Exception ex) {
            hubo_error = true;
            System.out.println("Error 6 en " + this.getClass().getName() + ":" + ex.toString());
        }
    }
    private int countNulos = 0;

    private void addTableProjectRecords(ArrayList<String> rowFileData, int numLine) {
        /*
         * AGREGA UN REGISTRO A LA TABLA project_records EN BASE A UN ARRAY LIST
         */

        //int va_i = 0;
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
                System.out.println("Error 6 en " + this.getClass().getName() + ":" + ex.toString());
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
            //COMO ESTA ES LA CABECERA SE DEBE DETERMINAR SI HAY NOMBRES REPETIDOS
            //O ESPACIOS O CARACTERES NO VALIDOS
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
            newFileName = file.getFileName();
        } catch (Exception ex) {

            System.out.println("Error 7 en " + this.getClass().getName() + ":" + ex.toString());
            FacesMessage msg = new FacesMessage("Error:", "error al realizar la carga del archivo" + ex.toString());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void createProject() {
        /*
         * CREACION DE UN NUEVO PROYECTO
         */
        currentNumberInserts = 0;
        ArrayList<String> errorsList = new ArrayList<String>();
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
        //createProyectMessage = "Iniciando procesamiento...";
        if (errorsList.isEmpty()) {
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
                projectsFacade.create(newProject);
                //PREPARO VARIABLES PARA LA CARGA DE REGISTROS----------------------
                if (file.getFileName().endsWith("xlsx")) {
                    uploadXls();
                } else {
                    uploadFileDelimiter();
                }

                //cargo las variables del proyecto actual                
                currentProjectName = newProjectName;
                currentDelimiter = newDelimiter;
                currentFileName = newFileName;
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
                //Asigno este proyecto al usuario que lo abrio
                Users currentUser = loginMB.getCurrentUser();
                currentUser.setProjectId(currentProjectId);
                usersFacade.edit(currentUser);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Correcto!!", "El proyecto ha sido creado, se cargaron " + tuplesProcessed + " registros."));
                //activo las pestañas
                inactiveTabs = false;
                relationshipOfVariablesMB.refresh();
                configurationLoaded = true;
                //actualizo pestaña (filtros)                
                copyMB.refresh();
                copyMB.cleanBackupTables();
                loadProjects();
            } catch (Exception ex) {
                System.out.println("Error 8 en " + this.getClass().getName() + ":" + ex.toString());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ocurrió un error procesando el archivo", ex.toString()));
            }
        } else {
            for (int i = 0; i < errorsList.size(); i++) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: ", errorsList.get(i)));
            }
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
        System.out.println("Se procede a cargar ultimo proyecto usuario");
        Projects openProject = projectsFacade.find(proyectId);
        if (openProject != null) {
            if (openProject.getUserId() == loginMB.getCurrentUser().getUserId()) {
                currentProjectId = proyectId;
                currentProjectName = openProject.getProjectName();
                currentDelimiter = openProject.getFileDelimiter();
                currentFileName = openProject.getFileName();
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
        String sql;
        if (selectedProjectTable != null) {
            Projects openProject = projectsFacade.find(Integer.parseInt(selectedProjectTable.getColumn1()));
            if (openProject != null) {
                if (openProject.getUserId() == loginMB.getCurrentUser().getUserId()) {
                    String nameProjet = openProject.getProjectName();           
                    sql = " \n"
                            + " delete from \n"
                            + "    project_columns \n"
                            + " where \n"
                            + "    column_id IN \n"
                            + "    (select \n"
                            + "        distinct(column_id) \n"
                            + "     from \n"
                            + "        project_records \n"
                            + "     where \n"
                            + "        project_id=" + openProject.getProjectId() + " \n"
                            + "    );";
                    //System.out.println("Eliminado project_columns" + sql);
                    connectionJdbcMB.non_query(sql);
                    sql = " \n"
                            + " delete from \n"
                            + "    project_records \n"
                            + " where \n"
                            + "    project_id=" + openProject.getProjectId() + " \n";
                    //System.out.println("Eliminado project_records" + sql);
                    connectionJdbcMB.non_query(sql);

                    sql = ""
                            + " delete from \n"
                            + "    projects \n"
                            + " where \n"
                            + "    project_id=" + openProject.getProjectId() + " \n";
                    //System.out.println("Eliminado projects" + sql);
                    connectionJdbcMB.non_query(sql);

                    if (openProject.getProjectId() == currentProjectId) {//EL PROYECTO ACTUAL ES EL ABIERTO 
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

    public void copyProject() {
        if (selectedProjectTable != null) {
            int id_project = projectsFacade.find(Integer.parseInt(selectedProjectTable.getColumn1())).getProjectId();
            printMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Proyecto a copiar es: " + id_project);
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

    public void createGroup() {
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
            }
        } else {
            printMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar un grupo de relaciones");
        }
    }

    public void createProjectCopy() {
        printMessage(FacesMessage.SEVERITY_WARN, "Alerta", "En construccion");
    }

    public void loadGroup() {
        if (currentRelationsGroupNameInLoad != null && currentRelationsGroupNameInLoad.trim().length() != 0) {
            newRelationsGroupName = currentRelationsGroupNameInLoad;
            printMessage(FacesMessage.SEVERITY_INFO, "Correcto", "El grupo de relaciones se ha cargado");
        } else {
            printMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar un grupo de relaciones");
        }
    }

//    public void btnResetClick() {
//        /*
//         * click sobre el boton reset
//         */
//        //progressUpload = 0;
//        relationshipOfVariablesMB.reset();
//        relationshipOfValuesMB.reset();
//        storedRelationsMB.reset();
//        recordDataMB.reset();
//        errorsControlMB.reset();
//        //RESETEO LA CONFIGURACION DEL USUARIO
//        //UsersConfiguration usersConfiguration = new UsersConfiguration(loginMB.getCurrentUser().getUserId());
//        //loginMB.getCurrentUser().setUsersConfiguration(usersConfiguration);
//        //usersFacade.edit(loginMB.getCurrentUser());
//        this.reset();
//        inactiveTabs = true;
//        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Correcto!!", "Se han reinicidado los controles"));
//    }
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

    public String getCopyRelationsGroupName() {
        return copyRelationsGroupName;
    }

    public void setCopyRelationsGroupName(String copyRelationsGroupName) {
        this.copyRelationsGroupName = copyRelationsGroupName;
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

    public String getNewProjectCopyName() {
        return newProjectCopyName;
    }

    public void setNewProjectCopyName(String newProjectCopyName) {
        this.newProjectCopyName = newProjectCopyName;
    }

    public String getCurrentRelationsGroupNameInLoad() {
        return currentRelationsGroupNameInLoad;
    }

    public void setCurrentRelationsGroupNameInLoad(String currentRelationsGroupNameInLoad) {
        this.currentRelationsGroupNameInLoad = currentRelationsGroupNameInLoad;
    }
}
