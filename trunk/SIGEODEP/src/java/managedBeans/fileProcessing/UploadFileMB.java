/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.fileProcessing;

import beans.connection.ConnectionJDBC;
import beans.relations.RelationsGroup;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import managedBeans.filters.CopyMB;
import managedBeans.preload.FormsAndFieldsDataMB;
import model.dao.*;
import model.pojo.Fields;
import model.pojo.Forms;
import model.pojo.Sources;
import model.pojo.Tags;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

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
    //private boolean btnProcessFileDisabled = true;
    private String tagName = "";
    private String currentForm = "";//ficha actual
    private SelectItem[] forms;
    private boolean selectFormDisabled = false;
    private String currentSource = "";//proveedor actual    
    private SelectItem[] sources;
    private boolean selectSourceDisabled = false;
    private String errorTagName = "Campo Obligatorio";
    //private SelectItem[] delimiters;
    //private boolean selectDelimiterDisabled = false;
    private UploadedFile file;
    //private boolean selectFileUploadDisabled = true;
    private boolean nameFileRendered = false;
    private boolean btnProcessFileDisabled = true;//si esta en true el boton esta
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
        errorTagName = "Campo Obligatorio";
        currentForm = "SCC-F-032";
        file = null;
        tagName = "";
        loadForms();
        loadSources();
        currentSource = sources[0].getLabel();
        loadVarsExpected();
        //loadDelimiters();
        tagsList = tagsFacade.findAll();
        variablesFound = new ArrayList<String>();
        nameFile = "";
        tagName = "";
        selectFormDisabled = false;
        //selectFileUploadDisabled = true;
        nameFileRendered = false;
        selectSourceDisabled = false;
        //selectDelimiterDisabled = false;
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

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //FUNCIONES QUE CARGAN VALORES -----------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
//    private void loadDelimiters() {
//        /*
//         * Cargar los delimitadores
//         */
//        delimiters = new SelectItem[]{
//            new SelectItem("TAB", "TAB"),
//            new SelectItem(";", ";"),
//            new SelectItem(",", ","),};
//    }
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
            currentSource = sources[0].getLabel();
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
    private void uploadXls() throws IOException {
        ArrayList<String> rowFileData;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy H:mm");
            FileInputStream fileInputStream = (FileInputStream) file.getInputstream();
            XSSFWorkbook workBook = new XSSFWorkbook(fileInputStream);
            XSSFSheet hssfSheet = workBook.getSheetAt(0);
            Iterator rowIterator = hssfSheet.rowIterator();
            XSSFRow hssfRow;
            Iterator columnIterator;
            XSSFCell hssfCell;
            tuplesNumber = 0;
            tuplesProcessed = 0;
            while (rowIterator.hasNext()) {
                rowIterator.next();
                tuplesNumber++;
            }
            System.out.println("Numero de Lineas: " + tuplesNumber);
            rowIterator = hssfSheet.rowIterator();
            while (rowIterator.hasNext()) {
                hssfRow = (XSSFRow) rowIterator.next();
                columnIterator = hssfRow.cellIterator();
                rowFileData = new ArrayList<String>();
                while (columnIterator.hasNext()) {
                    hssfCell = (XSSFCell) columnIterator.next();
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
        btnProcessFileDisabled = true;
        selectFormDisabled = true;
        selectSourceDisabled = true;
        //selectFileUploadDisabled = true;
        tagNameDisabled = true;
        nameFile = "Archivo cargado: " + file.getFileName();
        RelationsGroup newRelationsGroup = new RelationsGroup("TEMP", currentForm, currentSource);

        relationshipOfVariablesMB.setVarsFound(variablesFound);
        relationshipOfVariablesMB.setCurrentRelationsGroup(newRelationsGroup);
        relationshipOfValuesMB.setCurrentRelationsGroup(newRelationsGroup);

        formsAndFieldsDataMB.setNameForm(currentForm);//relationshipOfVariablesMB.set(variablesFound);
        recordDataMB.setNameForm(currentForm);
        recordDataMB.setCurrentSource(currentSource);
        //recordDataMB.setBtnValidateDisabled(false);
        storedRelationsMB.setCurrentRelationsGroup(newRelationsGroup);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Correcto!!", "Archivo procesado correctamente."));
        inactiveTabs = false;
        btnResetDisabled = false;
        copyMB.refresh();
        copyMB.cleanBackupTables();
        
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

    public void handleFileUpload(FileUploadEvent event) {
        file = event.getFile();
        if (errorTagName.length() == 0) {
            btnProcessFileDisabled = false;
        }
        FacesMessage msg = new FacesMessage("Archivo cargado", "Archivo cargado correctamente, presione procesar para que sea procesado");
        FacesContext.getCurrentInstance().addMessage(null, msg);
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
                    uploadXls();
                    //FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Archivo procesado correctamente");
                    //FacesContext.getCurrentInstance().addMessage(null, msg);
                    //redireccionar
//                    ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
//                    String ctxPath = ((ServletContext) ctx.getContext()).getContextPath();
//                    try {
//                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se ha cargado el archivo correctamente"));
//                        ctx.redirect(ctxPath + "/faces/web/fileProcessing/fileProcessing.xhtml");
//                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se ha cargado el archivo correctamente"));
//                        System.out.println("redirecciona a fileprocessing");
//                    } catch (Exception ex) {
//                        System.out.println("redirecciona a fileprocessing fallo: " + ex.toString());
//                    }
                }
            } catch (Exception ex) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ocurrió un error procesando el archivo", ex.toString()));
            }
        }
    }

    public void btnResetClick() {
        /*
         * click sobre el boton reset
         */
        //progress = 0;
        //progressValidate = 0;
        //loginMB.reset();
        progressUpload = 0;
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

//    public String getCurrentDelimiter() {
//        return currentDelimiter;
//    }
//    public void setCurrentDelimiter(String currentDelimiter) {
//        this.currentDelimiter = currentDelimiter;
//    }
//
//    public SelectItem[] getDelimiters() {
//        return delimiters;
//    }
//
//    public void setDelimiters(SelectItem[] delimiters) {
//        this.delimiters = delimiters;
//    }
//
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
}
