/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.fileProcessing;

import beans.connection.ConnectionJDBC;
import beans.relations.RelationsGroup;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import managedBeans.filters.CopyMB;
import managedBeans.preload.FormsAndFieldsDataMB;
import model.dao.FormsFacade;
import model.dao.SourcesFacade;
import model.pojo.Fields;
import model.pojo.Forms;
import model.pojo.Sources;
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
    private FormsAndFieldsDataMB formsAndFieldsDataMB;
    private StoredRelationsMB storedRelationsMB;
    private CopyMB copyMB;

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
        formsAndFieldsDataMB = (FormsAndFieldsDataMB) context.getApplication().evaluateExpressionGet(context, "#{formsAndFieldsDataMB}", FormsAndFieldsDataMB.class);
    }

    //@PostConstruct //ejecutar despues de el constructor
    public void reset() {
        /*
         * Cargar el formulario con los valores iniciales
         */
        loadFirstForm();
        loadForms();
        loadSources();
        loadVarsExpected();
        loadDelimiters();
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
    }

    public void changeTagName() {
        try {
            if (tagName.trim().length() == 0) {
                selectFileUploadDisabled = true;
                btnLoadFileDisabled = true;
            } else {
                selectFileUploadDisabled = false;
                btnLoadFileDisabled = false;
            }
        } catch (Exception e) {
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

    private void loadFirstForm() {
        /*
         * cargar el primer formulario retorna null si no existe ninguno
         */
        currentForm = "SCC-F-032";
    }

    private void loadForms() {
        /*
         * cargar la lista de formularios existentes
         */
        List<Forms> formsList = formsFacade.findAll();
        forms = new SelectItem[formsList.size()];
        for (int i = 0; i < formsList.size(); i++) {
            forms[i] = new SelectItem(formsList.get(i).toString(), formsList.get(i).getFormName());
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
        }
    }

    private void reloadVarsFound() {
        try {
            conx = new ConnectionJDBC();
            conx.connect();
            ResultSet resultSetFileData = conx.consult("SELECT * FROM temp;");
            int columnsNumber = resultSetFileData.getMetaData().getColumnCount();
            int pos = 0;
            headerFile = new String[columnsNumber];//creo un arreglo con los nombres de las columnas
            for (int i = 1; i <= columnsNumber; i++) {
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

    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    //CLIK SOBRE BOTONOES --------------------------------------------------
    //----------------------------------------------------------------------
    //----------------------------------------------------------------------
    public void upload() {
        /*
         * cargar un archivo y almacenarlo en una tabla temporal para verificar
         * sus datos e introducirlos a la DB
         */
        String line;
        InputStreamReader isr;
        BufferedReader buffer;
        boolean continuar = true;
        int numeroLineas = 0;
        headerFile = null;
        String[] tupla;
        String[] tupla2;
        try {
            if (file == null) {
                continuar = false;
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Debe Seleccionar un archivo");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            if (continuar) {
                if (file.getFileName().length() == 0) {
                    continuar = false;
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Debe Seleccionar el archivo");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }
            }
            if (continuar) {
                if (tagName.trim().length() == 0) {
                    continuar = false;
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "Debe Escribir el nombre de la carga de datos ");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }
            }
            if (continuar) {
                isr = new InputStreamReader(file.getInputstream());
                buffer = new BufferedReader(isr);
                //Leer el la informacion del archivo linea por linea                       
                while ((line = buffer.readLine()) != null) {

                    if (numeroLineas == 0) {
                        /*
                         * DEJAMOS UNA CABECERA VALIDA SIN ESPACIOS NI SIMBOLOS
                         * NO VALIDOS Y QUE NO INICIE CON UN NUMERO
                         */
                        String salida = "";

                        line = line.toLowerCase();//pasar a minusculas
                        line = line.replaceAll(" ", "_");//quitar espacion y acentos
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
                        //le asigno a los nombres un numero(cosecutivo) para que no se repitan los nombres                        
                        int count;
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
                        //si la cadena inicia con un numero, le antepongo una raya baja
                        for (int i = 0; i < headerFile.length; i++) {
                            if (headerFile[i].startsWith("0") || headerFile[i].startsWith("1") || headerFile[i].startsWith("2")
                                    || headerFile[i].startsWith("3") || headerFile[i].startsWith("4") || headerFile[i].startsWith("5")
                                    || headerFile[i].startsWith("6") || headerFile[i].startsWith("7") || headerFile[i].startsWith("8")
                                    || headerFile[i].startsWith("9")) {
                                headerFile[i] = "_" + headerFile[i];
                            }
                        }
                        if (!continuar) {
                            break;
                        }
                        variablesFound.addAll(Arrays.asList(headerFile));
                        numeroLineas++;
                    } else {
                        numeroLineas++;
                    }
                }
                //numeroLineas = numeroLineas - 1;//se quita la cabecera
                if (numeroLineas < 1 && continuar) {
                    continuar = false;
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "El archivo no contiene registros"));
                }
            }
            if (continuar) {//existen registros

                conx = new ConnectionJDBC();
                conx.connect();
                String sql = "DROP TABLE IF EXISTS temp;";//elimino si existe
                conx.non_query(sql);
                //creo tabla temporal
                sql = "CREATE TABLE temp ( id integer NOT NULL,";
                for (int i = 0; i < headerFile.length; i++) {
                    if (headerFile.length - 1 == i) {
                        sql = sql + " " + headerFile[i] + " text, "
                                + "CONSTRAINT pkey_temp PRIMARY KEY (id)); ";
                    } else {
                        sql = sql + " " + headerFile[i] + " text,";
                    }
                }
                conx.non_query(sql);
                //inserto los registros
                isr = new InputStreamReader(file.getInputstream());
                buffer = new BufferedReader(isr);
                int numLine = 0;
                while ((line = buffer.readLine()) != null) {
                    //separo la linea leida dependiendo del delimitador
                    if (currentDelimiter.compareTo("TAB") == 0) {
                        tupla = line.split("\t");
                    } else if (currentDelimiter.compareTo(",") == 0) {
                        tupla = line.split(",");
                    } else {
                        tupla = line.split(";");
                    }
                    //viene igual numero de campos que la cabecera                                
                    //if (headerFile.length != tupla.length) {
                    tupla2 = new String[headerFile.length];
                    for (int i = 0; i < tupla2.length; i++) {
                        tupla2[i] = "";
                        if (tupla.length > i) {
                            tupla2[i] = tupla[i];
                        }
                    }
                    tupla = tupla2;
                    //}

                    //if (headerFile.length == tupla.length && numLine != 0) {
                    if (numLine != 0) {
                        sql = "INSERT INTO temp VALUES (" + "'" + String.valueOf(numLine) + "',";
                        for (int i = 0; i < tupla.length; i++) {
                            if (tupla.length - 1 == i) {
                                sql = sql + "'" + tupla[i] + "');";
                            } else {
                                sql = sql + "'" + tupla[i] + "',";
                            }
                        }
                        conx.non_query(sql);
                    }
                    /*
                     * //llega un numero mayor de registros que la cabecera if
                     * (headerFile.length < tupla.length && numLine != 0) {
                     * continuar = false;
                     * FacesContext.getCurrentInstance().addMessage(null, new
                     * FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "En la
                     * linea: " + String.valueOf(numLine + 1) + " del archivo
                     * llega un numero mayor de registros que la cabecera"));
                     * System.out.println("Cabecera: " +
                     * String.valueOf(headerFile.length) + " tupla: " +
                     * String.valueOf(tupla.length)); break; } //llega un numero
                     * menor de registros que la cabecera if (headerFile.length
                     * > tupla.length && numLine != 0) { continuar = false;
                     * FacesContext.getCurrentInstance().addMessage(null, new
                     * FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR", "En la
                     * linea: " + String.valueOf(numLine + 1) + " del archivo
                     * llega un numero menor de registros que la cabecera"));
                     * System.out.println("Cabecera: " +
                     * String.valueOf(headerFile.length) + " tupla: " +
                     * String.valueOf(tupla.length)); break; }
                     */
                    numLine++;
                }
            }
            if (continuar) {
                btnLoadFileDisabled = true;
                selectDelimiterDisabled = true;
                selectFormDisabled = true;
                selectSourceDisabled = true;
                selectFileUploadDisabled = true;
                tagNameDisabled = true;
                nameFile = "Archivo cargado: " + file.getFileName();

                RelationsGroup newRelationsGroup = new RelationsGroup("TEMP", currentForm, currentSource);
                copyMB.refresh();
                copyMB.cleanBackupTables();
                relationshipOfVariablesMB.setVarsFound(variablesFound);
                relationshipOfVariablesMB.setCurrentRelationsGroup(newRelationsGroup);
                formsAndFieldsDataMB.setNameForm(currentForm);//relationshipOfVariablesMB.set(variablesFound);
                storedRelationsMB.setCurrentRelationsGroup(newRelationsGroup);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Correcto!!", "Archivo cargado correctamente."));
                btnResetDisabled = false;
            }
            if (conx
                    != null) {
                conx.disconnect();
            }
        } catch (IOException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ocurrió un error al cargar el archivo", e.toString()));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ocurrió un error", ex.toString()));
        }
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

    public void setCurrentSource(String currentSupplier) {
        this.currentSource = currentSupplier;
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
}
