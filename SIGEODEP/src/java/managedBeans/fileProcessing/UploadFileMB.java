/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.fileProcessing;

import beans.connection.ConnectionJdbcMB;
import java.io.*;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
@ManagedBean(name = "uploadFileMB")
@SessionScoped
public class UploadFileMB implements Serializable {

    @EJB
    FormsFacade formsFacade;
    @EJB
    SourcesFacade sourcesFacade;
    @EJB
    TagsFacade tagsFacade;
    @EJB
    RelationGroupFacade relationGroupFacade;
    @EJB
    UsersFacade usersFacade;
    private boolean tagNameDisabled = false;
    private boolean btnResetDisabled = true;
    private String tagName = "";
    private String currentForm = "";//ficha actual
    private SelectItem[] forms;
    private boolean selectFormDisabled = false;
    private int currentSource = 0;//proveedor actual    
    private SelectItem[] sources;
    private boolean selectSourceDisabled = false;
    private String errorTagName = "Campo Obligatorio";
    private SelectItem[] delimiters;
    private UploadedFile file;
    private boolean nameFileRendered = false;
    private boolean btnProcessFileDisabled = true;//si esta en true el boton esta
    private String nameFile = "";
    private String[] headerFile;//caberecera del archivo
    private List<String> variablesFound;
    private List<String> variablesExpected;
    private RelationshipOfVariablesMB relationshipOfVariablesMB;
    private RelationshipOfValuesMB relationshipOfValuesMB;
    private ErrorsControlMB errorsControlMB;
    private LoginMB loginMB;
    private RecordDataMB recordDataMB;
    private StoredRelationsMB storedRelationsMB;
    private ConnectionJdbcMB connectionJdbcMB;
    private CopyMB copyMB;
    private List<Tags> tagsList;
    private boolean inactiveTabs = true;
    private Integer progressUpload;
    private int tuplesNumber;
    private Integer tuplesProcessed;
    private String nameTableTemp = "temp";
    private String currentDelimiter = "";
    private boolean configurationLoaded = false;//determinar si la configuracion ya se cargo

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

    public UploadFileMB() {
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

    //@PostConstruct //ejecutar despues de el constructor
    public void reset() {
        /*
         * Cargar el formulario con los valores iniciales
         */
        errorTagName = "Campo Obligatorio";
        currentForm = "SCC-F-032";
        file = null;
        tagName = "";
        loadForms();
        loadSources();
        loadVarsExpected();
        loadDelimiters();
        tagsList = tagsFacade.findAll();
        variablesFound = new ArrayList<String>();
        nameFile = "";
        tagName = "";
        selectFormDisabled = false;
        nameFileRendered = false;
        selectSourceDisabled = false;
        btnProcessFileDisabled = true;
        tagNameDisabled = false;
        btnResetDisabled = true;
        progressUpload = 0;

    }
    @EJB
    FatalInjuriesFacade fatalInjuriesFacade;
    @EJB
    NonFatalInjuriesFacade nonFatalInjuriesFacade;
    @EJB
    NonFatalDomesticViolenceFacade nonFatalDomesticViolenceFacade;

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES QUE CARGAN VALORES -----------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
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
        try {
            List<Forms> formsList = formsFacade.findAll();
            forms = new SelectItem[formsList.size()];
            for (int i = 0; i < formsList.size(); i++) {
                forms[i] = new SelectItem(formsList.get(i).toString(), formsList.get(i).getFormName());
            }
        } catch (Exception e) {
            System.out.println("Error: ufMB_1 > " + e.toString());
        }
    }

    private void loadSources() {
        /*
         * cargar la lista de proveedores(fuentes) de datos dependiendo de un
         * determinado formulario
         */
        List<Sources> sourcesList = formsFacade.findSources(currentForm);
        sources = new SelectItem[sourcesList.size()];

        for (int i = 0; i < sourcesList.size(); i++) {
            sources[i] = new SelectItem(sourcesList.get(i).getSourceId().toString(), sourcesList.get(i).getSourceName());
            currentSource = Integer.parseInt(sources[0].getValue().toString());
        }
    }

    private void reloadVarsFound() {
        try {
            ResultSet resultSetFileData = connectionJdbcMB.consult("SELECT * FROM " + nameTableTemp + ";");
            int columnsNumber = resultSetFileData.getMetaData().getColumnCount();
            int pos = 0;
            headerFile = new String[columnsNumber - 1];//creo un arreglo con los nombres de las columnas
            for (int i = 2; i <= columnsNumber; i++) {
                headerFile[pos] = resultSetFileData.getMetaData().getColumnName(i);
                pos++;
            }
        } catch (Exception e) {
            System.out.println("Error: ufMB_2 > " + e.toString());
        }
    }

    private void loadVarsExpected() {
        /*
         * cargar las variables esperadas dependiendo de un determinado
         * formulario
         */
        List<Fields> varsList = formsFacade.findVarsExecpted(currentForm);
        variablesExpected = new ArrayList<String>();
        for (int i = 0; i < varsList.size(); i++) {
            variablesExpected.add(varsList.get(i).toString());
        }
        try {
            relationshipOfVariablesMB.setVariablesExpected(variablesExpected);
        } catch (Exception e) {
            System.out.println("******PRIMER INGRESO******");
        }
    }

    public void printMessage(FacesMessage.Severity s, String title, String messageStr) {
        FacesMessage msg = new FacesMessage(s, title, messageStr);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //CLIK SOBRE BOTONOES --------------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------    

    public void copyFile(String fileName, InputStream in) {
        try {
            OutputStream out = new FileOutputStream(new File(fileName));
            int read = 0;
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
            tuplesNumber = Integer.parseInt(String.valueOf(fileSize / 1000));
            tuplesProcessed = 0;
            String line;
            InputStreamReader isr;
            BufferedReader buffer;
            String salida = "";
            boolean continueProcess = true;

            headerFile = null;
            String[] tupla;
            String[] tupla2;
            isr = new InputStreamReader(file.getInputstream());
            buffer = new BufferedReader(isr);
            //Leer el la informacion del archivo linea por linea                       
            ArrayList<String> rowFileData;

            while ((line = buffer.readLine()) != null) {
                if (currentDelimiter.compareTo("TAB") == 0) {
                    tupla = line.split("\t");
                } else if (currentDelimiter.compareTo(",") == 0) {
                    tupla = line.split(",");
                } else {
                    tupla = line.split(";");
                }
                if (tuplesProcessed == 0) {
                    headerFile = tupla;
                    rowFileData = new ArrayList<String>();
                    rowFileData.addAll(Arrays.asList(headerFile));
                    createTableTemp(prepareArray(rowFileData));
                } else {
                    rowFileData = new ArrayList<String>();
                    if (headerFile.length == tupla.length) {//igual numero de columnas en cabecera y tupla
                        rowFileData.addAll(Arrays.asList(tupla));
                    } else {
                        if (headerFile.length > tupla.length) {//numero de columnas menor que cabecera
                            //rowFileData.addAll(Arrays.asList(tupla));
                            rowFileData.addAll(Arrays.asList(tupla));
                        } else {//numero de columnas mayor que cabecera
                            for (int i = 0; i < headerFile.length; i++) {
                                rowFileData.add(tupla[i]);
                            }
                        }
                    }
                    addTableTemp(rowFileData, tuplesProcessed);
                }
                tuplesProcessed++;
                progressUpload = (int) (tuplesProcessed * 100) / tuplesNumber;
                if (progressUpload == 0) {
                    progressUpload = 1;
                }
                System.out.println(progressUpload);
            }
            try {
                connectionJdbcMB.non_query("update " + connectionJdbcMB.getTableName() + " set dirres = baveres where dirres like '';");
                connectionJdbcMB.non_query("update " + connectionJdbcMB.getTableName() + " set baveres = dirres where baveres like  '';");
            } catch (Exception e) {
            }

        } catch (IOException e) {
            System.out.println("Error: ufMB_5 > " + e.toString());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ocurrió un error al cargar el archivo", e.toString()));
        } catch (Exception ex) {
            System.out.println("Error: ufMB_6 > " + ex.toString());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ocurrió un error al cargar el archivo", ex.toString()));
        }
    }

    private void uploadXls() throws IOException {
        try {
            File file2 = new File(file.getFileName());
            //Long fileSize = file2.length();
            tuplesNumber = 0;//Integer.parseInt(String.valueOf(fileSize / 400));
            tuplesProcessed = 0;

            //determinar numero de filas
            progressUpload = 1;
            try {
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
                            //ArrayList<String> rowFileData;
                            @Override
                            public void startRow(int rowNum) {
                                tuplesNumber++;
                            }

                            @Override
                            public void endRow() {
                            }

                            @Override
                            public void cell(String cellReference, String formattedValue) {
                            }

                            @Override
                            public void headerFooter(String text, boolean isHeader, String tagName) {
                            }
                        },
                                false//means result instead of formula
                                );
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

            System.out.println("TOTAL DE FILAS DEL ARCHIVO ES: " + String.valueOf(tuplesNumber));
            //procesar el archivo
            try {
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
                                    createTableTemp(prepareArray(rowFileData));
                                } else {
                                    addTableTemp(rowFileData, tuplesProcessed);
                                }
                                tuplesProcessed++;
                                progressUpload = (int) (tuplesProcessed * 100) / tuplesNumber;
                                if (progressUpload == 0) {
                                    progressUpload = 1;
                                }
                                System.out.println(progressUpload);
                            }

                            @Override
                            public void cell(String cellReference, String formattedValue) {
                                //System.out.println("CELDA:"+cellReference + "   VALOR." + formattedValue);
                                CellReference a = new CellReference(cellReference);
                                int empyColumns = a.getCol() - rowFileData.size();
                                for (int i = 0; i < empyColumns; i++) {
                                    rowFileData.add("");
                                }
                                rowFileData.add(formattedValue);
                            }

                            @Override
                            public void headerFooter(String text, boolean isHeader, String tagName) {
                            }
                        },
                                false//means result instead of formula
                                );
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

        } catch (Exception e) {
            System.out.println("Error: ufMB_3 > " + e.toString());
        }
    }

    public void tagNameKeyPress() {
        errorTagName = "";
        if (file != null) {
            btnProcessFileDisabled = false;
        }

        if (tagName.trim().length() != 0) {
            for (int i = 0; i < tagsList.size(); i++) {
                if (tagsList.get(i).getTagName() != null) {
                    if (tagsList.get(i).getTagName().toUpperCase().compareTo(tagName.toUpperCase().trim()) == 0) {
                        //selectFileUploadDisabled = true;
                        errorTagName = "El nombre ingresado ya esta registrado";
                        btnProcessFileDisabled = true;
                        //printMessage(FacesMessage.SEVERITY_ERROR, "Error", "El nombre ingresado ya se encuentra registrado, se debe digitar otro nombre para el conjunto de registros");
                    }
                }
            }
        } else {
            errorTagName = "Campo Obligatorio";
            btnProcessFileDisabled = true;
        }
    }

    private void addTableTemp(ArrayList<String> rowFileData, int numLine) {
        /*
         * AGREGA UN REGISTRO A LA TABLA TEMP EN BASE A UN ARRAY LIST
         */

        //conx.connect();
        String sql = "INSERT INTO " + nameTableTemp + " VALUES (" + "'" + String.valueOf(numLine) + "',";
        for (int i = 0; i < rowFileData.size(); i++) {
            //char a = 160;
            //char b = 32;
            //rowFileData.set(i,rowFileData.get(i).replace(a, b).trim());            
            if (rowFileData.size() - 1 == i) {
                sql = sql + "'" + rowFileData.get(i) + "');";
            } else {
                sql = sql + "'" + rowFileData.get(i) + "',";
            }
        }
        connectionJdbcMB.non_query(sql);
        if (connectionJdbcMB.getMsj().startsWith("ERROR")) {
            System.out.println("---------------------------------------------------------------");
            System.out.println("-------------------------------ERRROR--------------------------");
            System.out.println(sql);
            System.out.println("---------------------------------------------------------------");
        }

    }

    private ArrayList<String> prepareArray(ArrayList<String> rowFile) {
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

    private void createTableTemp(ArrayList<String> rowFileData) {
        /*
         * CREA LA TABLA TEMP EN BASE A UN ARRAY LIST
         */
        String sql = "DROP TABLE IF EXISTS " + nameTableTemp + ";";//elimino si existe
        connectionJdbcMB.non_query(sql);
        if (connectionJdbcMB.getMsj().startsWith("ERROR")) {
            System.out.println("---------------------------------------------------------------");
            System.out.println("-------------------------------ERRROR--------------------------");
            System.out.println(sql);
            System.out.println("---------------------------------------------------------------");
        }
        sql = "CREATE TABLE " + nameTableTemp + " ( id integer NOT NULL,";//creo tabla temporal        
        //inserto los registros----------------------------------------        
        if (rowFileData != null) {//VIENE DE UN XLS
            for (int i = 0; i < rowFileData.size(); i++) {
                if (rowFileData.size() - 1 == i) {
                    sql = sql + " " + rowFileData.get(i) + " text, CONSTRAINT pkey_" + nameTableTemp + " PRIMARY KEY (id)); ";
                } else {
                    sql = sql + " " + rowFileData.get(i) + " text,";
                }
            }
        }
        connectionJdbcMB.non_query(sql);
        if (connectionJdbcMB.getMsj().startsWith("ERROR")) {
            System.out.println("---------------------------------------------------------------");
            System.out.println("-------------------------------ERRROR--------------------------");
            System.out.println(sql);
            System.out.println("---------------------------------------------------------------");
        }

    }

    public void handleFileUpload(FileUploadEvent event) {
        try {
            file = event.getFile();
            //copyFile(event.getFile().getFileName(), event.getFile().getInputstream());
            if (errorTagName.length() == 0) {
                btnProcessFileDisabled = false;
            }
            FacesMessage msg = new FacesMessage("Archivo cargado", "Archivo cargado correctamente, presione procesar para que sea procesado");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            //} catch (IOException ex) {
        } catch (Exception ex) {
            System.out.println("Error: ufMB_7 > " + ex.toString());
        }
    }

    public void processFile() {
        /*
         * almacenar archivo cargado en una tabla temporal para verificar sus
         * datos e introducirlos a la DB
         */
        boolean continueProcess = true;
        if (continueProcess) {
            try {
                if (file == null) {
                    continueProcess = false;
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Primero debe subir un un archivo");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }
                if (continueProcess) {
                    if (file.getFileName().endsWith("xlsx")) {
                        uploadXls();
                    } else {
                        uploadFileDelimiter();
                    }
                    progressUpload = 100;
                    btnProcessFileDisabled = true;
                    selectFormDisabled = true;
                    selectSourceDisabled = true;
                    tagNameDisabled = true;
                    nameFile = "Archivo cargado: " + file.getFileName();
                    RelationGroup newRelationsGroup = new RelationGroup();
                    newRelationsGroup.setIdRelationGroup(0);
                    newRelationsGroup.setFormId(formsFacade.findByFormId(currentForm));
                    newRelationsGroup.setSourceId(currentSource);
                    newRelationsGroup.setNameRelationGroup("TEMP");
                    newRelationsGroup.setRelationVariablesList(new ArrayList<RelationVariables>());
                    relationshipOfVariablesMB.setVarsFound(variablesFound);
                    relationshipOfVariablesMB.setCurrentRelationsGroup(newRelationsGroup);
                    relationshipOfValuesMB.setCurrentRelationsGroup(newRelationsGroup);
                    recordDataMB.setNameForm(currentForm);
                    recordDataMB.setCurrentSource(currentSource);
                    storedRelationsMB.setCurrentRelationsGroup(newRelationsGroup);
                    //ALMACENO LOS DATOS DE ESTA CONFIGURACION
                    Users currentUser = loginMB.getCurrentUser();
                    UsersConfiguration usersConfiguration = new UsersConfiguration(currentUser.getUserId());
                    currentUser.setUsersConfiguration(usersConfiguration);
                    usersConfiguration.setTagName(tagName);
                    usersConfiguration.setFormId(currentForm);
                    usersConfiguration.setSourceId(currentSource);
                    usersConfiguration.setDelimiterFile(currentDelimiter);
                    usersConfiguration.setFileName(file.getFileName());
                    usersFacade.edit(currentUser);

                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Correcto!!", "Archivo procesado correctamente."));
                    inactiveTabs = false;
                    btnResetDisabled = false;
                    copyMB.refresh();
                    copyMB.cleanBackupTables();
                }
            } catch (Exception ex) {
                System.out.println("Error: ufMB_4 > " + ex.toString());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ocurrió un error procesando el archivo", ex.toString()));
            }
        }
    }

    public void loadConfigurationUser() {
        boolean continueProcess = true;
        if (configurationLoaded) {//la configuracion ya se cargo una vez, no hacer nada            
        } else {// es la primera vez que entra al sistema se debe cargar configuracion
            progressUpload = 0;
            relationshipOfVariablesMB.reset();
            relationshipOfValuesMB.reset();
            storedRelationsMB.reset();
            recordDataMB.reset();
            errorsControlMB.reset();
            this.reset();
            inactiveTabs = true;
            //VERIFICO SI EXISTE LA TABLA TEMP
            try {
                ResultSet rs = connectionJdbcMB.consult("Select * from " + nameTableTemp);
                if (rs.next()) {
                    //CARGO LOS NOMBRES DE LAS COLUMNAS(variables esperadas)
                    variablesFound = new ArrayList<String>();
                    int columnsNumber = rs.getMetaData().getColumnCount();
                    for (int i = 1; i < columnsNumber; i++) {
                        variablesFound.add(rs.getMetaData().getColumnName(i));
                    }
                    System.out.println("Existe la tabla");
                }
            } catch (Exception e) {
                System.out.println("No existe la tabla");
                continueProcess = false;
            }
            //VERIFICO SI SE PUEDE CARGAR LOS DATOS DE LA CONFIGURACION
            if (continueProcess) {
                UsersConfiguration usersConfiguration = loginMB.getCurrentUser().getUsersConfiguration();
                if (usersConfiguration.getTagName() != null) {
                    if (usersConfiguration.getTagName().trim().length() != 0) {
                        //cargar los datos de la configuracion
                        currentDelimiter = usersConfiguration.getDelimiterFile();
                        currentForm = usersConfiguration.getFormId();
                        currentSource = usersConfiguration.getSourceId();
                        nameFile = usersConfiguration.getFileName();
                        tagName = usersConfiguration.getTagName();
                        System.out.println("Se cargo la configuracion");
                        errorTagName = " ";
                        inactiveTabs = false;
                        btnResetDisabled = false;
                        copyMB.refresh();
                        copyMB.cleanBackupTables();
                        btnProcessFileDisabled = true;
                        selectFormDisabled = true;
                        selectSourceDisabled = true;
                        tagNameDisabled = true;
                        recordDataMB.setNameForm(currentForm);
                        recordDataMB.setCurrentSource(currentSource);
                        //cargar grupo de relaciones de ser posible
                        boolean relationLoaded = false;
                        if (usersConfiguration.getRelationGroupName() != null) {
                            if (usersConfiguration.getRelationGroupName().trim().length() != 0) {
                                //busco si existe este grupo de relaciones                            
                                if (relationGroupFacade.findByName(usersConfiguration.getRelationGroupName()) != null) {
                                    storedRelationsMB.setCurrentRelationGroupName(usersConfiguration.getRelationGroupName());
                                    storedRelationsMB.btnLoadConfigurationClick();
                                    relationLoaded = true;
                                    System.out.println("Se cargaron las relaciones");
                                }
                            }
                        }
                        if (!relationLoaded) {//no se pudieron cargar relaciones
                            //se crea una por defecto
                            RelationGroup newRelationsGroup = new RelationGroup();
                            newRelationsGroup.setIdRelationGroup(0);
                            newRelationsGroup.setFormId(formsFacade.findByFormId(currentForm));
                            newRelationsGroup.setSourceId(currentSource);
                            newRelationsGroup.setNameRelationGroup("TEMP");
                            newRelationsGroup.setRelationVariablesList(new ArrayList<RelationVariables>());
                            relationshipOfVariablesMB.setVarsFound(variablesFound);
                            relationshipOfVariablesMB.setCurrentRelationsGroup(newRelationsGroup);
                            relationshipOfValuesMB.setCurrentRelationsGroup(newRelationsGroup);

                            storedRelationsMB.setCurrentRelationsGroup(newRelationsGroup);
                            System.out.println("No se pudieron cargar la relaciones");
                        }
                    }
                } else {
                    System.out.println("No se pudo cargar la configuracion");
                }
            }
            configurationLoaded = true;//la carga solo realizarla la primera vez
        }
    }

    public void btnResetClick() {
        /*
         * click sobre el boton reset
         */
        progressUpload = 0;
        relationshipOfVariablesMB.reset();
        relationshipOfValuesMB.reset();
        storedRelationsMB.reset();
        recordDataMB.reset();
        errorsControlMB.reset();
        //RESETEO LA CONFIGURACION DEL USUARIO
        UsersConfiguration usersConfiguration = new UsersConfiguration(loginMB.getCurrentUser().getUserId());
        loginMB.getCurrentUser().setUsersConfiguration(usersConfiguration);
        usersFacade.edit(loginMB.getCurrentUser());


        this.reset();
        inactiveTabs = true;
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Correcto!!", "Se han reinicidado los controles"));
    }

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

    public String getCurrentForm() {
        return currentForm;
    }

    public void setCurrentForm(String currentF) {
        this.currentForm = currentF;
        loadSources();
        loadVarsExpected();
        //formsAndFieldsDataMB.setNameForm(currentForm);
    }

    public SelectItem[] getForms() {
        return forms;
    }

    public void setForms(SelectItem[] forms) {
        this.forms = forms;

    }

    public int getCurrentSource() {
        return currentSource;
    }

    public void setCurrentSource(int currentSource) {
        this.currentSource = currentSource;
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
        variablesFound.addAll(Arrays.asList(headerFile));
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

    public String getNameFile() {
        return nameFile;
    }

    public void setNameFile(String nameFile) {
        this.nameFile = nameFile;
    }

    public String getCurrentDelimiter() {
        return currentDelimiter;
    }

    public void setCurrentDelimiter(String currentDelimiter) {
        this.currentDelimiter = currentDelimiter;
    }

    public SelectItem[] getDelimiters() {
        return delimiters;
    }

    public void setDelimiters(SelectItem[] delimiters) {
        this.delimiters = delimiters;
    }

//    public boolean isSelectDelimiterDisabled() {
//        return selectDelimiterDisabled;
//    }
//
//    public void setSelectDelimiterDisabled(boolean selectDelimiterDisabled) {
//        this.selectDelimiterDisabled = selectDelimiterDisabled;
//    }
    public boolean isSelectFormDisabled() {
        return selectFormDisabled;
    }

    public void setSelectFormDisabled(boolean selectFormDisabled) {
        this.selectFormDisabled = selectFormDisabled;
    }

    public boolean isSelectSourceDisabled() {
        return selectSourceDisabled;
    }

    public void setSelectSourceDisabled(boolean selectSourceDisabled) {
        this.selectSourceDisabled = selectSourceDisabled;
    }

//    public boolean isSelectFileUploadDisabled() {
//        return selectFileUploadDisabled;
//    }
//
//    public void setSelectFileUploadDisabled(boolean selectFileUploadDisabled) {
//        this.selectFileUploadDisabled = selectFileUploadDisabled;
//    }
    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public boolean isTagNameDisabled() {
        return tagNameDisabled;
    }

    public void setTagNameDisabled(boolean tagNameDisabled) {
        this.tagNameDisabled = tagNameDisabled;
    }

//    public void setFormsAndFieldsDataMB(FormsAndFieldsDataMB formsAndFieldsDataMB) {
//        this.formsAndFieldsDataMB = formsAndFieldsDataMB;
//    }
    public void setRelationshipOfVariablesMB(RelationshipOfVariablesMB relationshipOfVariablesMB) {
        this.relationshipOfVariablesMB = relationshipOfVariablesMB;
    }

    public void setStoredRelationsMB(StoredRelationsMB storedRelationsMB) {
        this.storedRelationsMB = storedRelationsMB;
    }

    public boolean isNameFileRendered() {
        return nameFileRendered;
    }

    public void setNameFileRendered(boolean nameFileRendered) {
        this.nameFileRendered = nameFileRendered;
    }

    public boolean isBtnProcessFileDisabled() {
        return btnProcessFileDisabled;
    }

    public String getErrorTagName() {
        return errorTagName;
    }

    public void setErrorTagName(String errorTagName) {
        this.errorTagName = errorTagName;
    }

    public void setBtnProcessFileDisabled(boolean btnProcessFileDisabled) {
        this.btnProcessFileDisabled = btnProcessFileDisabled;
    }

    public boolean isBtnResetDisabled() {
        return btnResetDisabled;
    }

    public void setBtnResetDisabled(boolean btnResetDisabled) {
        this.btnResetDisabled = btnResetDisabled;
    }

    public boolean isInactiveTabs() {
        return inactiveTabs;
    }

    public void setInactiveTabs(boolean inactiveTabs) {
        this.inactiveTabs = inactiveTabs;
    }

    public Integer getProgressUpload() {
        return progressUpload;
    }

    public void setProgressUpload(Integer progressUpload) {
        this.progressUpload = progressUpload;
    }

    public Integer getTuplesProcessed() {
        return tuplesProcessed;
    }

    public void setTuplesProcessed(Integer tuplesProcessed) {
        this.tuplesProcessed = tuplesProcessed;
    }
}
