/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.fileProcessing;

import beans.connection.ConnectionJDBC;
import beans.relations.RelationsGroup;
import java.io.*;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import managedBeans.filters.CopyMB;
import managedBeans.preload.FormsAndFieldsDataMB;
import model.dao.*;
import model.pojo.Fields;
import model.pojo.Forms;
import model.pojo.Sources;
import model.pojo.Tags;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.primefaces.model.UploadedFile;
import java.io.File;
import java.io.FileInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.faces.context.ExternalContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;

/**
 *
 * @author santos
 */
@ManagedBean(name = "uploadFileMB")
@SessionScoped
public class UploadFileMB implements Serializable {

    ConnectionJDBC conx = null;//conexion sin persistencia a postgres   
    @EJB
    FormsFacade formsFacade;
    @EJB
    SourcesFacade sourcesFacade;
    @EJB
    TagsFacade tagsFacade;
    private boolean tagNameDisabled = false;
    private boolean btnResetDisabled = true;
    private String tagName = "";
    private String currentForm = "";//ficha actual
    private SelectItem[] forms;
    private boolean selectFormDisabled = false;
    private String currentSource = "";//proveedor actual    
    private SelectItem[] sources;
    private boolean selectSourceDisabled = false;
    private String currentDelimiter = "";//ficha actual
    private SelectItem[] delimiters;
    private boolean selectDelimiterDisabled = false;
    private UploadedFile file;
    private boolean selectFileUploadDisabled = true;
    private boolean nameFileRendered = false;
    private boolean btnLoadFileDisabled = false;//si esta en true el boton esta
    private String nameFile = "";
    private String[] headerFile;//caberecera del archivo
    private List<String> variablesFound;
    private List<String> variablesExpected;
    private RelationshipOfVariablesMB relationshipOfVariablesMB;
    private RelationshipOfValuesMB relationshipOfValuesMB;
    private ErrorsControlMB errorsControlMB;
    private FormsAndFieldsDataMB formsAndFieldsDataMB;
    //private UploadFileMB uploadFileMB;
    private RecordDataMB recordDataMB;
    private StoredRelationsMB storedRelationsMB;
    private CopyMB copyMB;
    private List<Tags> tagsList;
    private boolean inactiveTabs = true;
    private Integer progressUpload;
    private int tuplesNumber;
    private int tuplesProcessed;

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES DE PROPOSITO GENERAL ---------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public UploadFileMB() {
        /*
         * Constructor de la clase
         */
        FacesContext context = FacesContext.getCurrentInstance();
        copyMB = (CopyMB) context.getApplication().evaluateExpressionGet(context, "#{copyMB}", CopyMB.class);
        relationshipOfVariablesMB = (RelationshipOfVariablesMB) context.getApplication().evaluateExpressionGet(context, "#{relationshipOfVariablesMB}", RelationshipOfVariablesMB.class);
        relationshipOfValuesMB = (RelationshipOfValuesMB) context.getApplication().evaluateExpressionGet(context, "#{relationshipOfValuesMB}", RelationshipOfValuesMB.class);
        errorsControlMB = (ErrorsControlMB) context.getApplication().evaluateExpressionGet(context, "#{errorsControlMB}", ErrorsControlMB.class);
        formsAndFieldsDataMB = (FormsAndFieldsDataMB) context.getApplication().evaluateExpressionGet(context, "#{formsAndFieldsDataMB}", FormsAndFieldsDataMB.class);
        //uploadFileMB = (UploadFileMB) context.getApplication().evaluateExpressionGet(context, "#{uploadFileMB}", UploadFileMB.class);
        recordDataMB = (RecordDataMB) context.getApplication().evaluateExpressionGet(context, "#{recordDataMB}", RecordDataMB.class);
    }

    //@PostConstruct //ejecutar despues de el constructor
    public void reset() {
        /*
         * Cargar el formulario con los valores iniciales
         */

        currentForm = "SCC-F-032";
        
        loadForms();
        loadSources();
        currentSource=sources[0].getLabel();
        loadVarsExpected();
        loadDelimiters();
        tagsList = tagsFacade.findAll();
        variablesFound = new ArrayList<String>();
        nameFile = "";
        tagName = "";
        selectFormDisabled = false;
        selectFileUploadDisabled = true;
        nameFileRendered = false;
        selectSourceDisabled = false;
        selectDelimiterDisabled = false;
        btnLoadFileDisabled = true;
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

//    public void crearCopia(Object document){
//        HSSFWorkbook book = (HSSFWorkbook) document;
//        BufferedWriter bw = new BufferedWriter(new FileWriter(sFichero));
//        HSSFSheet sheet = book.getSheetAt(0);// Se toma hoja del libro
//        HSSFRow row;
//        HSSFCellStyle cellStyle = book.createCellStyle();
//        HSSFFont font = book.createFont();
//        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
//        cellStyle.setFont(font);
//        row = sheet.createRow(0);// Se crea una fila dentro de la hoja        
//        createCell(cellStyle, row, 0, "CODIGO INTERNO");//"100">#{rowX.column1}</p:column>
//    }
    private void createCell(HSSFCellStyle cellStyle, HSSFRow fila, int position, String value) {
        HSSFCell cell;
        cell = fila.createCell((short) position);// Se crea una cell dentro de la fila                        
        cell.setCellValue(new HSSFRichTextString(value));
        cell.setCellStyle(cellStyle);
    }

    public void relatedRecordSets() {
    }

    public void changeTagName() {
        try {

            if (tagName.trim().length() != 0) {
                for (int i = 0; i < tagsList.size(); i++) {
                    if (tagsList.get(i).getTagName() != null) {
                        if (tagsList.get(i).getTagName().compareTo(tagName) == 0) {
                            selectFileUploadDisabled = true;
                            btnLoadFileDisabled = true;
                            printMessage(FacesMessage.SEVERITY_ERROR, "Nombre registrado", "El nombre ingresado ya se encuentra registrado, se debe digitar otro nombre para el conjunto de registros");
                            System.out.println("****YA esta*");
                        } else {
                            selectFileUploadDisabled = false;
                            btnLoadFileDisabled = false;
                            System.out.println("****No esta*");
                        }
                    }
                }
            } else {
                selectFileUploadDisabled = true;
                btnLoadFileDisabled = true;
                System.out.println("****no esta TA*");
            }
        } catch (Exception e) {
            printMessage(FacesMessage.SEVERITY_ERROR, "ERROR UploadFileMB_1", e.toString());
            selectFileUploadDisabled = true;
            btnLoadFileDisabled = true;
        }
    }

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
            sources[i] = new SelectItem(sourcesList.get(i).getSourceName(), sourcesList.get(i).toString());
            currentSource=sources[0].getLabel();
        }

    }

    private void reloadVarsFound() {
        try {
            conx = new ConnectionJDBC();
            conx.connect();
            ResultSet resultSetFileData = conx.consult("SELECT * FROM temp;");
            int columnsNumber = resultSetFileData.getMetaData().getColumnCount();
            int pos = 0;
            headerFile = new String[columnsNumber - 1];//creo un arreglo con los nombres de las columnas
            for (int i = 2; i <= columnsNumber; i++) {
                headerFile[pos] = resultSetFileData.getMetaData().getColumnName(i);
                pos++;
            }
            conx.disconnect();
        } catch (Exception e) {
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
    private void printToConsole(List cellDataList) {
        String stringCellValue;
        for (int i = 0; i < cellDataList.size(); i++) {
            List cellTempList = (List) cellDataList.get(i);
            for (int j = 0; j < cellTempList.size(); j++) {
                HSSFCell hssfCell = (HSSFCell) cellTempList.get(j);
                if (hssfCell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                    stringCellValue = hssfCell.toString().replaceAll(".0", "");
                } else {
                    stringCellValue = hssfCell.toString();
                }

                System.out.print(stringCellValue + "\t");
            }
            System.out.println();
        }
    }

    private void Leer(List cellDataList) {
        String txt;
        for (int i = 0; i < cellDataList.size(); i++) {
            txt = "";
            List cellTempList = (List) cellDataList.get(i);
            for (int j = 0; j < cellTempList.size(); j++) {
                XSSFCell hssfCell = (XSSFCell) cellTempList.get(j);
                String stringCellValue = hssfCell.toString();
                txt = txt + "    " + stringCellValue;
            }
            System.out.println(txt);
        }
    }

    private void uploadXls() throws IOException {


        //List cellDataList = new ArrayList();
        ArrayList<String> rowFileData;
        String txt;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy H:mm");
            FileInputStream fileInputStream = (FileInputStream) file.getInputstream();
            XSSFWorkbook workBook = new XSSFWorkbook(fileInputStream);
            XSSFSheet hssfSheet = workBook.getSheetAt(0);
            Iterator rowIterator = hssfSheet.rowIterator();
            tuplesNumber = 0;
            tuplesProcessed = 0;
            while (rowIterator.hasNext()) {
                rowIterator.next();
                tuplesNumber++;
            }
            System.out.println("Numero de Lineas: " + tuplesNumber);
            rowIterator = hssfSheet.rowIterator();
            while (rowIterator.hasNext()) {
                XSSFRow hssfRow = (XSSFRow) rowIterator.next();
                Iterator iterator = hssfRow.cellIterator();
                rowFileData = new ArrayList<String>();
                txt = "";
                //String cellText = "";
                while (iterator.hasNext()) {
                    XSSFCell hssfCell = (XSSFCell) iterator.next();
                    txt = "";
                    if (hssfCell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                        double d = hssfCell.getNumericCellValue();
                        if (HSSFDateUtil.isCellDateFormatted(hssfCell)) {
                            rowFileData.add(dateFormat.format(HSSFDateUtil.getJavaDate(d)));
                        } else {
                            rowFileData.add(hssfCell.toString().replaceAll(".0", ""));
                        }
                    } else {
                        rowFileData.add(hssfCell.toString());
                    }
                }
                tuplesProcessed++;
                if (tuplesProcessed == 1) {//ES LA PRIMER FILA QUE SE LEE
                    createTableTemp(prepareArray(rowFileData));
                } else {
                    addTableTemp(rowFileData, tuplesProcessed - 1);
                }
                System.out.println(progressUpload);
                progressUpload = (int) (tuplesProcessed * 100) / tuplesNumber;
            }
        } catch (Exception e) {
        }
        progressUpload = 100;
        btnLoadFileDisabled = true;
        selectDelimiterDisabled = true;
        selectFormDisabled = true;
        selectSourceDisabled = true;
        selectFileUploadDisabled = true;
        tagNameDisabled = true;
        nameFile = "Archivo cargado: " + file.getFileName();
        //currentSource=uploadFileMB.getCurrentSource();
        RelationsGroup newRelationsGroup = new RelationsGroup("TEMP", currentForm, currentSource);

        relationshipOfVariablesMB.setVarsFound(variablesFound);
        relationshipOfVariablesMB.setCurrentRelationsGroup(newRelationsGroup);
        relationshipOfValuesMB.setCurrentRelationsGroup(newRelationsGroup);

        formsAndFieldsDataMB.setNameForm(currentForm);//relationshipOfVariablesMB.set(variablesFound);
        recordDataMB.setNameForm(currentForm);
        recordDataMB.setCurrentSource(currentSource);
        recordDataMB.setBtnValidateDisabled(false);
        storedRelationsMB.setCurrentRelationsGroup(newRelationsGroup);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Correcto!!", "Archivo cargado correctamente."));
        inactiveTabs = false;
        btnResetDisabled = false;
        copyMB.refresh();
        copyMB.cleanBackupTables();
        progressUpload = 0;
    }

    private void addTableTemp(ArrayList<String> rowFileData, int numLine) {
        /*
         * AGREGA UN REGISTRO A LA TABLA TEMP EN BASE A UN ARRAY LIST
         */
        conx = new ConnectionJDBC();
        conx.connect();
        String sql = "INSERT INTO temp VALUES (" + "'" + String.valueOf(numLine) + "',";
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
        conx.non_query(sql);
        conx.disconnect();
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
        String currentName = "";
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
        variablesFound = rowFile;
        return rowFile;
    }

    private void createTableTemp(ArrayList<String> rowFileData) {
        /*
         * CREA LA TABLA TEMP EN BASE A UN ARRAY LIST
         */
        conx = new ConnectionJDBC();
        conx.connect();
        String sql = "DROP TABLE IF EXISTS temp;";//elimino si existe
        conx.non_query(sql);
        sql = "CREATE TABLE temp ( id integer NOT NULL,";//creo tabla temporal        
        //inserto los registros----------------------------------------        
        if (rowFileData != null) {//VIENE DE UN XLS
            for (int i = 0; i < rowFileData.size(); i++) {
                if (rowFileData.size() - 1 == i) {
                    sql = sql + " " + rowFileData.get(i) + " text, CONSTRAINT pkey_temp PRIMARY KEY (id)); ";
                } else {
                    sql = sql + " " + rowFileData.get(i) + " text,";
                }
            }
        }
        conx.non_query(sql);
        conx.disconnect();
    }

    private void uploadFileDelimiter() {
        /*
         * CARGA DE UN ARCHIVO CON DELIMITADOR
         */
        String line;
        InputStreamReader isr;
        BufferedReader buffer;
        String salida = "";
        boolean continueProcess = true;
        int currentNumberLine = 0;
        headerFile = null;
        String[] tupla;
        String[] tupla2;
        try {
            isr = new InputStreamReader(file.getInputstream());
            buffer = new BufferedReader(isr);
            //Leer el la informacion del archivo linea por linea                       
            while ((line = buffer.readLine()) != null) {
                /*
                 * DEJAMOS UNA CABECERA VALIDA SIN ESPACIOS NI SIMBOLOS NO
                 * VALIDOS Y QUE NO INICIE CON UN NUMERO
                 */
                if (currentNumberLine == 0) {

                    line = line.toLowerCase();//pasar a minusculas
                    line = line.replaceAll(" ", "_");//quitar espacioS y acentos
                    line = line.replaceAll("ñ", "n");
                    line = line.replaceAll("á", "a");
                    line = line.replaceAll("é", "e");
                    line = line.replaceAll("í", "i");
                    line = line.replaceAll("ó", "o");
                    line = line.replaceAll("ú", "u");
                    for (int i = 0; i < line.length(); i++) {//quitar caracteres no aceptados
                        int k = (int) line.charAt(i);
                        if (k >= 97 && k <= 122 || k >= 65 && k <= 90 || k >= 48 && k <= 57 || k == '\t' || k == '_') {
                            salida = salida + line.charAt(i);
                        }
                    }
                    line = salida;
                    line = line.replaceAll("__", "_");//eliminar dobles raya bajas
                    //creamos un vector con cada una de las cadenas separadas por un determinado delimitador
                    if (currentDelimiter.compareTo("TAB") == 0) {
                        headerFile = line.split("\t");
                    } else if (currentDelimiter.compareTo(",") == 0) {
                        headerFile = line.split(",");
                    } else {
                        headerFile = line.split(";");
                    }
                    int count;//le asigno a los nombres un numero(cosecutivo) para que no se repitan los nombres
                    String currentName = "";
                    for (int i = 0; i < headerFile.length; i++) {
                        currentName = headerFile[i];
                        count = 1;
                        for (int j = i + 1; j < headerFile.length; j++) {
                            if (currentName.compareTo(headerFile[j]) == 0) {
                                count++;
                                headerFile[j] = headerFile[j] + "_" + String.valueOf(count);
                            }
                        }
                        if (count != 1) {//hubo repetidos
                            headerFile[i] = headerFile[i] + "_1";
                        }
                    }
                    for (int i = 0; i < headerFile.length; i++) {//si la cadena inicia con un numero, le antepongo una raya baja
                        if (headerFile[i].startsWith("0") || headerFile[i].startsWith("1") || headerFile[i].startsWith("2")
                                || headerFile[i].startsWith("3") || headerFile[i].startsWith("4") || headerFile[i].startsWith("5")
                                || headerFile[i].startsWith("6") || headerFile[i].startsWith("7") || headerFile[i].startsWith("8")
                                || headerFile[i].startsWith("9")) {
                            headerFile[i] = "_" + headerFile[i];
                        }
                    }
//                    if (!continueProcess) {
//                        break;
//                    }
                    variablesFound.addAll(Arrays.asList(headerFile));
                    currentNumberLine++;
                } else {
                    currentNumberLine++;
                    if (currentNumberLine > 1) {
                        break;
                    }
                }
            }
            if (currentNumberLine < 2) {//solo hay cabecera
                continueProcess = false;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "El archivo no contiene registros"));
            }
            if (continueProcess) {//hay cabecera y datos
                /*
                 * EXISTEN REGISTROS POR TANTO SE DEBE CREAR LA TABLA TEMP
                 */
                conx = new ConnectionJDBC();
                conx.connect();
                String sql = "DROP TABLE IF EXISTS temp;";//elimino si existe
                conx.non_query(sql);
                sql = "CREATE TABLE temp ( id integer NOT NULL,";//creo tabla temporal
                for (int i = 0; i < headerFile.length; i++) {
                    if (headerFile.length - 1 == i) {
                        sql = sql + " " + headerFile[i] + " text, "
                                + "CONSTRAINT pkey_temp PRIMARY KEY (id)); ";
                    } else {
                        sql = sql + " " + headerFile[i] + " text,";
                    }
                }
                conx.non_query(sql);
                //inserto los registros----------------------------------------
                isr = new InputStreamReader(file.getInputstream());
                buffer = new BufferedReader(isr);
                int numLine = 0;
                boolean una = true;
                while ((line = buffer.readLine()) != null) {
                    //dejo la linea solo con caracteres validos
//                    for (int i = 0; i < line.length(); i++) {//quitar caracteres no aceptados
//                        int k = (int) line.charAt(i);
//                        if (k >= 32 && k <= 127
//                                || k == '\t'
//                                || k == 'á'
//                                || k == 'é'
//                                || k == 'í'
//                                || k == 'ó'
//                                || k == 'ú'
//                                || k == 'ü'
//                                || k == 'Ü'
//                                || k == 'Á'
//                                || k == 'É'
//                                || k == 'Í'
//                                || k == 'Ó'
//                                || k == 'Ú') {
//                            salida = salida + line.charAt(i);
//                        }
//                    }
//                    line = salida;
                    //separo la linea leida dependiendo del delimitador---------
                    if (currentDelimiter.compareTo("TAB") == 0) {
                        tupla = line.split("\t");
                    } else if (currentDelimiter.compareTo(",") == 0) {
                        tupla = line.split(",");
                    } else {
                        tupla = line.split(";");
                    }
                    //viene igual numero de campos que la cabecera                                
                    tupla2 = new String[headerFile.length];
                    for (int i = 0; i < tupla2.length; i++) {
                        tupla2[i] = "";
                        if (tupla.length > i) {
                            tupla2[i] = tupla[i];
                        }
                    }
                    tupla = tupla2;
                    if (numLine != 0) {
                        sql = "INSERT INTO temp VALUES (" + "'" + String.valueOf(numLine) + "',";
                        String value;
                        for (int i = 0; i < tupla.length; i++) {
                            char a = 160;
                            char b = 32;
                            tupla[i] = tupla[i].replace(a, b);
                            tupla[i] = tupla[i].trim();
                            if (tupla.length - 1 == i) {
                                sql = sql + "'" + tupla[i] + "');";
                            } else {
                                sql = sql + "'" + tupla[i] + "',";
                            }
                        }
                        conx.non_query(sql);
                    }
                    numLine++;
                }
            }
            if (continueProcess) {

                //le asigno la direccion de residencia si existe barrio de residencia
                try {
                    conx.non_query("update temp set dirres = baveres where dirres like '';");
                    conx.non_query("update temp set baveres = dirres where baveres like  '';");
                } catch (Exception e) {
                }


                btnLoadFileDisabled = true;
                selectDelimiterDisabled = true;
                selectFormDisabled = true;
                selectSourceDisabled = true;
                selectFileUploadDisabled = true;
                tagNameDisabled = true;
                nameFile = "Archivo cargado: " + file.getFileName();

                RelationsGroup newRelationsGroup = new RelationsGroup("TEMP", currentForm, currentSource);

                relationshipOfVariablesMB.setVarsFound(variablesFound);
                relationshipOfVariablesMB.setCurrentRelationsGroup(newRelationsGroup);
                relationshipOfValuesMB.setCurrentRelationsGroup(newRelationsGroup);

                formsAndFieldsDataMB.setNameForm(currentForm);//relationshipOfVariablesMB.set(variablesFound);
                recordDataMB.setNameForm(currentForm);
                recordDataMB.setCurrentSource(currentSource);
                recordDataMB.setBtnValidateDisabled(false);
                storedRelationsMB.setCurrentRelationsGroup(newRelationsGroup);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Correcto!!", "Archivo cargado correctamente."));
                inactiveTabs = false;
                btnResetDisabled = false;
                copyMB.refresh();
                copyMB.cleanBackupTables();
            }
            if (conx != null) {
                conx.disconnect();
            }
        } catch (IOException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ocurrió un error al cargar el archivo", e.toString()));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ocurrió un error", ex.toString()));
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        FacesMessage msg = new FacesMessage("Achivo cargado", "Se inicia con el procesamiento...");
        file = event.getFile();
        upload();
    }

    public void upload() {
        /*
         * cargar un archivo y almacenarlo en una tabla temporal para verificar
         * sus datos booleanValue introducirlos a la DB
         */
        // file = event.getFile();
        boolean continueProcess = true;
        try {
            if (file == null) {
                continueProcess = false;
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Debe Seleccionar un archivo");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            if (continueProcess) {
                if (file.getFileName().length() == 0) {
                    continueProcess = false;
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Debe Seleccionar el archivo");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }
            }
            if (continueProcess) {
                if (tagName.trim().length() == 0) {
                    continueProcess = false;
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Debe Escribir el nombre de la carga de datos ");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }
            }
            if (continueProcess) {
                if (file.getFileName().indexOf(".xls") != -1 || file.getFileName().indexOf(".XLS") != -1) {
                    uploadXls();
                } else {
                    uploadFileDelimiter();
                }

                //redireccionar

                ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
                String ctxPath = ((ServletContext) ctx.getContext()).getContextPath();
                try {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se ha cargado el archivo correctamente"));
                    ctx.redirect(ctxPath + "/faces/web/fileProcessing/fileProcessing.xhtml");
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se ha cargado el archivo correctamente"));
                    System.out.println("redirecciona a fileprocessing");
                } catch (Exception ex) {
                    System.out.println("redirecciona a fileprocessing fallo: " + ex.toString());
                }
            }
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ocurrió un error cargando el archivo", ex.toString()));
        }
    }

    public void btnResetClick() {
        /*
         * click sobre el boton reset
         */
        //progress = 0;
        //progressValidate = 0;
        //loginMB.reset();

        formsAndFieldsDataMB.reset();
        relationshipOfVariablesMB.reset();
        relationshipOfValuesMB.reset();
        storedRelationsMB.reset();
        recordDataMB.reset();
        errorsControlMB.reset();
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
        formsAndFieldsDataMB.setNameForm(currentForm);
    }

    public SelectItem[] getForms() {
        return forms;
    }

    public void setForms(SelectItem[] forms) {
        this.forms = forms;

    }

    public String getCurrentSource() {
        return currentSource;
    }

    public void setCurrentSource(String currentSource) {
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

    public boolean isBtnLoadFileDisabled() {
        return btnLoadFileDisabled;
    }

    public void setBtnLoadFileDisabled(boolean btnLoadFileDisabled) {
        this.btnLoadFileDisabled = btnLoadFileDisabled;
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

    public boolean isSelectDelimiterDisabled() {
        return selectDelimiterDisabled;
    }

    public void setSelectDelimiterDisabled(boolean selectDelimiterDisabled) {
        this.selectDelimiterDisabled = selectDelimiterDisabled;
    }

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

    public boolean isSelectFileUploadDisabled() {
        return selectFileUploadDisabled;
    }

    public void setSelectFileUploadDisabled(boolean selectFileUploadDisabled) {
        this.selectFileUploadDisabled = selectFileUploadDisabled;
    }

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

    public void setFormsAndFieldsDataMB(FormsAndFieldsDataMB formsAndFieldsDataMB) {
        this.formsAndFieldsDataMB = formsAndFieldsDataMB;
    }

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
}
