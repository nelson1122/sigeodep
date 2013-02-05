/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.indicators;

import beans.connection.ConnectionJdbcMB;
import beans.enumerators.VariablesEnum;
import beans.util.Variable;
import java.io.File;
import java.io.FileInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import managedBeans.reports.SpanColumns;
import managedBeans.reports.TableGroup;
import model.dao.IndicatorsFacade;
import model.pojo.Indicators;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.primefaces.component.column.Column;
import org.primefaces.component.outputpanel.OutputPanel;
import org.primefaces.component.panelgrid.PanelGrid;
import org.primefaces.component.row.Row;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "indicatorsPercentageMB")
@SessionScoped
public class IndicatorsPercentageMB {

    /**
     * Creates a new instance of IndicatorsMB
     */
    @EJB
    IndicatorsFacade indicatorsFacade;
    private Indicators currentIndicator;
    private StreamedContent chartImage;
    private SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
    private FacesMessage message = null;
    private ConnectionJdbcMB connectionJdbcMB;
    private String titlePage = "SIGEODEP -  INDICADORES GENERALES PARA LESIONES FATALES";
    private String titleIndicator = "SIGEODEP -  INDICADORES GENERALES PARA LESIONES FATALES";
    private String subTitleIndicator = "NUMERO DE CASOS POR LESION";
    private String currentGraphType;
    private String currentVariableGraph;
    private String currentValueGraph;
    private String dataTableHtml;
    private String firstVariablesCrossSelected = null;
    private String initialValue = "";
    private String endValue = "";
    private String[] headers2;//CABECERA 2 CUANDO EL CRUCE SE REALIZA SOBRE 3 VARIABLES
    private String[][] matrixResult;//MATRIZ DE RESULTADOS
    private Date initialDate = new Date();
    private Date endDate = new Date();
    private String initialDateStr;
    private String endDateStr;
    private String pivotTableName;
    private String prepivotTableName;
    //private List<String> graphTypes = new ArrayList<String>();
    private List<String> variablesGraph = new ArrayList<String>();
    private List<String> valuesGraph = new ArrayList<String>();
    private List<String> variablesList = new ArrayList<String>();//lista de nombres de variables disponibles que sepueden cruzar(se visualizan en pagina)
    private List<String> variablesCrossList = new ArrayList<String>();//ista de nombres de variables que se van a cruzar(se visualizan en pagina)
    private List<String> currentVariablesSelected = new ArrayList<String>();//lista de nombres seleccionados en la lista de variables disponibles
    private List<String> currentVariablesCrossSelected = new ArrayList<String>();//lista de nombres seleccionados en la lista de variables a cruzar    
    private List<String> currentCategoricalValuesList = new ArrayList<String>();
    private List<String> currentCategoricalValuesSelected;
    private ArrayList<SpanColumns> headers1;//CABECERA 1 CUANDO EL CRUCE SE REALIZA SOBRE 3 VARIABLES
    private ArrayList<Variable> variablesListData;//lista de variables que tiene el indicador
    private ArrayList<Variable> variablesCrossData = new ArrayList<Variable>();//lista de variables a cruzar
    private ArrayList<String> valuesCategoryList;//lista de valores para una categoria
    private ArrayList<SpanColumns> dynamicHeaders1;
    private ArrayList<TableGroup> listaDeGrupos;
    private ArrayList<String> columNames;//NOMBRES DE LAS COLUMNAS, (SI EL CRUCE ES DE TRES VARIABLES ESTA SEPARADO POR EL CARACTER: }  )
    private ArrayList<String> rowNames;//NOMBRES DE LAS FILAS    
    private ArrayList<String> totalsHorizontal = new ArrayList<String>();
    private ArrayList<String> totalsVertical = new ArrayList<String>();
    private Variable currentVariableConfiguring;
    private int grandTotal = 0;//total general de la matriz
    private int numberCross = 2;//maximo numero de variables a cruzar
    private int currentYear = 0;
    private boolean btnAddVariableDisabled = true;
    private boolean btnAddCategoricalValueDisabled = true;
    private boolean btnRemoveCategoricalValueDisabled = true;
    private boolean btnRemoveVariableDisabled = true;
    //private boolean renderedDynamicDataTable = true;
    //private boolean showAll = true;//mostrar filas y columnas vacias
    private boolean showCount = true;//mostrar recuento
    private boolean showRowPercentage = true;//mostrar porcentaje por fila
    private boolean showColumnPercentage = true;//mostrar porcentaje por columna
    private boolean showTotalPercentage = true;//mostrar porcentaje del total
    boolean colorType = true;

    public IndicatorsPercentageMB() {
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
        Calendar c = Calendar.getInstance();
        currentYear = c.get(Calendar.YEAR);
        initialDate.setDate(1);
        initialDate.setMonth(0);
        initialDate.setYear(2003 - 1900);
        endDate.setDate(c.get(Calendar.DATE));
        endDate.setMonth(c.get(Calendar.MONTH));
        endDate.setYear(c.get(Calendar.YEAR) - 1900);
    }

    public void showMessage() {
        if (message != null) {
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void initialDateFocusLost() {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(initialDate);
        c2.setTime(endDate);
        if (c1.compareTo(c2) > 0) {
            initialDate = endDate;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La fecha inicial debe ser inferior o igual a la fecha final, el valor fue correjido."));
        }
    }

    public void endDateFocusLost() {
    }

    public void loadValuesGraph() {
        valuesGraph = new ArrayList<String>();
        for (int i = 0; i < variablesCrossData.size(); i++) {
            if (variablesCrossData.get(i).getName().compareTo(currentVariableGraph) == 0) {
                for (int j = 0; j < variablesCrossData.get(i).getValuesConfigured().size(); j++) {//cargo el combo de valores
                    valuesGraph.add(variablesCrossData.get(i).getValuesConfigured().get(j));
                    currentValueGraph = valuesGraph.get(0);
                }
                break;
            }
        }
        createImage();
    }

    public void process() {
        ArrayList<String> errorsList = new ArrayList<String>();
        variablesCrossData = new ArrayList<Variable>();//lista de variables a cruzar            
        boolean continueProcess;
        message = null;
        variablesGraph = new ArrayList<String>();
        valuesGraph = new ArrayList<String>();
        currentValueGraph = "";
        currentVariableGraph = "";
        //----------------------------------------------------------------------
        //NUMERO DE VARIABLES A CRUZAR SEA MENOR O IGUAL AL LIMITE ESTABLECIDO
        //----------------------------------------------------------------------
        if (currentIndicator.getIndicatorId() < 5) {//es un indicador general
            if (variablesCrossList.size() <= numberCross) {
                continueProcess = true;
            } else {
                continueProcess = false;
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "En la lista de variables a cruzar deben haber " + numberCross + " o menos variables");
            }
        } else {
            if (variablesCrossList.size() < 4 && variablesCrossList.size() > 0) {
                continueProcess = true;
            } else {
                continueProcess = false;
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "En la lista de variables a cruzar deben haber minimo 1 y maximo 3 variables");
            }
        }
        //----------------------------------------------------------------------
        //SI ES INDICADOR GENERAL AGREGO UNA NUEVA VARIABLE A CRUZAR(tipo lesion)
        //----------------------------------------------------------------------        
        if (continueProcess) {
            if (currentIndicator.getIndicatorId() == 1 || currentIndicator.getIndicatorId() == 2) {
                //agrego a la lista de variables a cruzar "tipo de lesion fatal"
                Variable newVariable = createVariable("Tipo Lesión", "injuries_fatal", false);
                variablesCrossData.add(newVariable);
            }
            if (currentIndicator.getIndicatorId() == 3 || currentIndicator.getIndicatorId() == 4) {
                //agrego a la lista de variables a cruzar "tipo de lesion fatal"
                Variable newVariable = createVariable("Tipo Lesión", "injuries_non_fatal", false);
                variablesCrossData.add(newVariable);
            }
        }
        //----------------------------------------------------------------------
        //AGREGO LAS VARIABLES INDICADAS POR EL USUARIO
        //----------------------------------------------------------------------        
        if (continueProcess) {
            for (int j = 0; j < variablesCrossList.size(); j++) {
                for (int i = 0; i < variablesListData.size(); i++) {
                    if (variablesListData.get(i).getName().compareTo(variablesCrossList.get(j)) == 0) {
                        variablesCrossData.add(variablesListData.get(i));
                    }
                }
            }
        }
        //----------------------------------------------------------------------
        //CADA VARIABLE A CRUZAR TENGA VALORES CONFIGURADOS
        //----------------------------------------------------------------------
        if (continueProcess) {
            for (int i = 0; i < variablesCrossData.size(); i++) {
                if (variablesCrossData.get(i).getValuesConfigured().isEmpty()) {
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La variable " + variablesListData.get(i).getName() + " no tiene valores configurados, para continuar debe ser configurada.");
                    continueProcess = false;
                }
            }
        }
        //----------------------------------------------------------------------
        //CARGO LOS COMBOS PARA EL GRAFICO
        //----------------------------------------------------------------------
        if (continueProcess) {
            if (variablesCrossData.size() == 3) {
                for (int i = 0; i < variablesCrossData.size(); i++) {
                    if (i == 0) {
                        variablesGraph.add(variablesCrossData.get(i).getName());
                        currentVariableGraph = variablesGraph.get(0);
                        //cargo el combo de valores
                        for (int j = 0; j < variablesCrossData.get(i).getValuesConfigured().size(); j++) {
                            valuesGraph.add(variablesCrossData.get(i).getValuesConfigured().get(j));
                            currentValueGraph = valuesGraph.get(0);
                        }
                    } else {
                        variablesGraph.add(variablesCrossData.get(i).getName());
                    }
                }
            }
        }
        //----------------------------------------------------------------------
        //CREO LAS TABLAS PIVOT Y PREPIVOT
        //----------------------------------------------------------------------
        if (continueProcess) {

            pivotTableName = "table_pivot";
            prepivotTableName = "table_prepivot";
            initialDateStr = formato.format(initialDate);
            endDateStr = formato.format(endDate);
            createPrepivotTable();//creo la tabla prepivot
            createPivotTable();//creo la tabla pivot
            createMatrixResult();//matriz de resultados
        }
        //----------------------------------------------------------------------
        //CREO LA TABLA DE RESULTADOS Y EL GRAFICO
        //----------------------------------------------------------------------
        if (continueProcess) {
            //dynamicDataTableGroup = new OutputPanel();//creo el panel grid
            dataTableHtml = createDataTableResult();
            createImage();//creo el grafico
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Cruze de porcentaje realizado");
        }
    }

    public void changeCategoticalList() {
        if (!currentCategoricalValuesSelected.isEmpty()) {
            btnRemoveCategoricalValueDisabled = false;
        }
    }

    public int btnAddCategoricalValueClick() {
        int i, e;
        if (initialValue.trim().length() == 0) {//VALOR INICIAL INGRESADO
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Digite un valor inicial"));
            return 0;
        }
        if (endValue.trim().length() == 0) {//VALOR FINAL INGRESADO
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Digite un valor final"));
            return 0;
        }
        try {//VALOR INICIAL Y FINAL NUMERICOS
            i = Integer.parseInt(initialValue);
            e = Integer.parseInt(endValue);
            if (i < 0 && e < 0) {//VALOR INICIAL Y FINAL MAYOR O IGUAL A CERO
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Los valores deben ser iguales o mayores que cero"));
                return 0;
            }
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Los valores deben ser numéricos"));
            return 0;
        }
        if (i > e) {//VALOR INICIAL MAYOR QUE FINAL
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El valor inicial debe ser menor que el valor final"));
            return 0;
        }
        //EL RANGO NO ESTE DENTRO DE OTRO
        if (currentVariableConfiguring != null) {
            for (int j = 0; j < currentVariableConfiguring.getValuesConfigured().size(); j++) {
                String[] splitValues = currentVariableConfiguring.getValuesConfigured().get(j).split("/");
                int initialValueFoundInteger = Integer.parseInt(splitValues[0]);
                int endValueFoundInteger = Integer.parseInt(splitValues[1]);
                int initialValueAddInteger = Integer.parseInt(initialValue);
                int endValueAddInteger = Integer.parseInt(endValue);
                for (int k = initialValueFoundInteger; k < endValueFoundInteger; k++) {
                    for (int l = initialValueAddInteger; l < endValueAddInteger; l++) {
                        //System.out.println("Comparacion de : " + String.valueOf(k) + " CON " + String.valueOf(l));
                        if (k == l) {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Dentro del rango ingresado el valor (" + String.valueOf(k) + ") esta contenido en la lista de valores"));
                            return 0;
                        }
                    }
                }
            }
        }
        if (currentVariableConfiguring != null) {//ingreso el nuevo valor a la categoria
            if (initialValue.length() == 1) {
                initialValue = "0" + initialValue;
            }
            if (endValue.length() == 1) {
                endValue = "0" + endValue;
            }
            currentVariableConfiguring.getValuesConfigured().add(initialValue + "/" + endValue);
            currentCategoricalValuesList = new ArrayList<String>();
            for (int j = 0; j < currentVariableConfiguring.getValuesConfigured().size(); j++) {
                currentCategoricalValuesList.add(currentVariableConfiguring.getValuesConfigured().get(j));
            }
            initialValue = "";
            endValue = "";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Se ha adicionado un nuevo valor a la categoría"));
            return 0;
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Alerta", "No hay categoria seleccionada"));
        return 0;
    }

    public void btnRemoveCategoryValueClick() {
        btnRemoveCategoricalValueDisabled = false;
        if (currentVariableConfiguring != null) {//determino cual es la variable actual:
            for (int i = 0; i < currentCategoricalValuesSelected.size(); i++) {
                for (int j = 0; j < currentVariableConfiguring.getValuesConfigured().size(); j++) {
                    if (currentVariableConfiguring.getValuesConfigured().get(j).compareTo(currentCategoricalValuesSelected.get(i)) == 0) {
                        currentVariableConfiguring.getValuesConfigured().remove(j);
                        break;
                    }
                }
            }
            //recargo la lista de valores de la categoria
            currentCategoricalValuesList = new ArrayList<String>();
            for (int j = 0; j < currentVariableConfiguring.getValuesConfigured().size(); j++) {
                currentCategoricalValuesList.add(currentVariableConfiguring.getValuesConfigured().get(j));
            }
        }
    }

    public void btnResetCategoryListClick() {
        currentCategoricalValuesSelected = new ArrayList<String>();
        btnRemoveCategoricalValueDisabled = false;
        if (currentVariableConfiguring != null) {
            //paso los elementos de la lista: values a valuesConfiguration
            currentVariableConfiguring.setValuesConfigured(new ArrayList<String>());
            for (int j = 0; j < currentVariableConfiguring.getValues().size(); j++) {
                currentVariableConfiguring.getValuesConfigured().add(currentVariableConfiguring.getValues().get(j));
            }

            //recargo la lista de valores de la categoria
            currentCategoricalValuesList = new ArrayList<String>();
            for (int j = 0; j < currentVariableConfiguring.getValues().size(); j++) {
                currentCategoricalValuesList.add(currentVariableConfiguring.getValues().get(j));
            }
        }
    }

    public void changeVariable() {
        btnAddVariableDisabled = true;
        btnRemoveVariableDisabled = true;
        if (currentVariablesSelected != null) {
            if (!currentVariablesSelected.isEmpty()) {
                btnAddVariableDisabled = false;
            }
        }
        if (currentVariablesCrossSelected != null) {
            if (!currentVariablesCrossSelected.isEmpty()) {
                btnRemoveVariableDisabled = false;
            }
        }
    }

    public void changeCrossVariable() {
        btnRemoveVariableDisabled = true;
        btnRemoveCategoricalValueDisabled = true;
        currentCategoricalValuesSelected = new ArrayList<String>();
        currentVariableConfiguring = null;
        initialValue = "";
        endValue = "";
        //cargo la lista de valores categoricos para la variable
        if (!currentVariablesCrossSelected.isEmpty()) {
            btnRemoveCategoricalValueDisabled = true;
            btnRemoveVariableDisabled = false;
            firstVariablesCrossSelected = currentVariablesCrossSelected.get(0);
            //filtro los años segun la fecha

            currentCategoricalValuesSelected = new ArrayList<String>();
            for (int i = 0; i < variablesListData.size(); i++) {
                if (variablesListData.get(i).getName().compareTo(firstVariablesCrossSelected) == 0) {
                    if (variablesListData.get(i).getGeneric_table().compareTo("year") == 0) {
                        variablesListData.get(i).filterYear(initialDate, endDate);
                    }
                    currentCategoricalValuesList = variablesListData.get(i).getValuesConfigured();
                    currentVariableConfiguring = variablesListData.get(i);
                    btnAddCategoricalValueDisabled = !currentVariableConfiguring.isConfigurable();
                    break;
                }
            }
        }
    }

    public void changeVariableCross() {
    }

    public void addVariableClick() {
        String error = "";
        boolean nextStep = true;
        if (currentVariablesSelected == null) {
            error = "Debe seleccionarse una variable";
            nextStep = false;
        }
        if (nextStep) {
            for (int i = 0; i < variablesList.size(); i++) {
                for (int j = 0; j < currentVariablesSelected.size(); j++) {
                    if (variablesList.get(i).compareTo(currentVariablesSelected.get(j)) == 0) {//esta es la variable encontrada que saldra de la lista                    
                        variablesList.remove(i);//la quito de este listado                    
                        variablesCrossList.add(currentVariablesSelected.get(j));//la agrego al otro                        
                        btnAddVariableDisabled = true;
                        i = -1;
                        break;
                    }
                }
            }
            currentVariablesSelected = null;
        }
        if (error.length() != 0) {//hay  errores al relacionar la variables 
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Alerta", error));
        }
    }

    public void removeVariableClick() {
        String error = "";
        boolean nextStep = true;
        if (currentVariablesCrossSelected == null) {
            error = "Debe seleccionarse una variable";
            nextStep = false;
        }
        if (nextStep) {
            for (int i = 0; i < variablesCrossList.size(); i++) {
                for (int j = 0; j < currentVariablesCrossSelected.size(); j++) {
                    if (variablesCrossList.get(i).compareTo(currentVariablesCrossSelected.get(j)) == 0) {//esta es la variable encontrada que saldra de la lista                    
                        variablesCrossList.remove(i);//la quito de este listado                    
                        variablesList.add(currentVariablesCrossSelected.get(j));//la agrego al otro                        
                        btnRemoveVariableDisabled = true;
                        i = -1;
                        break;
                    }
                }
            }
            currentVariablesCrossSelected = null;
        }
        if (error.length() != 0) {//hay  errores al relacionar la variables 
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Alerta", error));
        }
    }

    public void createImage() {
        //System.out.println("SE CREO IMAGEN");
        if (currentGraphType.compareTo("pastel") == 0) {
            try {
                JFreeChart jfreechart = ChartFactory.createPieChart(subTitleIndicator, createPieDataset(), true, true, false);
                File chartFile = new File("dynamichart");
                ChartUtilities.saveChartAsPNG(chartFile, jfreechart, 700, 500);
                chartImage = new DefaultStreamedContent(new FileInputStream(chartFile), "image/png");
            } catch (Exception e) {
            }
        }
        if (currentGraphType.compareTo("barras apiladas") == 0) {
            try {//JFreeChart 
                //String 
                JFreeChart chart = createStackedBarChart();
                File chartFile = new File("dynamichart");
                ChartUtilities.saveChartAsPNG(chartFile, chart, 650, 500);
                chartImage = new DefaultStreamedContent(new FileInputStream(chartFile), "image/png");
            } catch (Exception e) {
            }
        }
    }

    public void reset() {
        currentVariableConfiguring = null;
        variablesCrossList = new ArrayList<String>();
        currentVariablesSelected = new ArrayList<String>();
        currentVariablesCrossSelected = new ArrayList<String>();
        firstVariablesCrossSelected = null;
        currentCategoricalValuesList = new ArrayList<String>();
        currentCategoricalValuesSelected = new ArrayList<String>();
        titlePage = currentIndicator.getIndicatorGroup();
        titleIndicator = currentIndicator.getIndicatorGroup();
        subTitleIndicator = currentIndicator.getIndicatorName();
        variablesListData = getVariablesIndicator();
        //graphTypes = getGraphTypes();
        variablesGraph = new ArrayList<String>();
        valuesGraph = new ArrayList<String>();
        currentValueGraph = "";
        currentVariableGraph = "";
        numberCross = currentIndicator.getNumberCross();
        variablesList = new ArrayList<String>();//SelectItem[variablesListData.size()];
        for (int i = 0; i < variablesListData.size(); i++) {
            variablesList.add(variablesListData.get(i).getName());
        }
        //dynamicDataTableGroup = new OutputPanel();//creo el panel grid
        variablesCrossList = new ArrayList<String>();//SelectItem[variablesListData.size()];
        btnAddVariableDisabled = true;
        btnRemoveVariableDisabled = true;
        currentVariablesSelected = null;
        currentVariablesCrossSelected = null;
    }

    public Variable createVariable(String name, String generic_table, boolean conf) {
        //conf me indica si es permitida la configuracion de esta variable
        Variable newVariable = new Variable(name, generic_table, conf);
        //cargo la lista de valores posibles
        ArrayList<String> valuesName = new ArrayList<String>();//NOMBRE DE LOS VALORES QUE PUEDE TOMAR LA VARIABLE POR DEFECTO(NOMBRE EN LA CATEGORIA)
        ArrayList<String> valuesId = new ArrayList<String>();  //IDENTIFICADORES DE LOS VALORES QUE PUEDE TOMAR LA VARIABLE POR DEFECTO(ID EN LA CATEGORIA)
        ArrayList<String> valuesConf = new ArrayList<String>();//NOMBRE DE LOS VALORES CONFIGURADOS POR EL USUARIO QUE PUEDE TOMAR LA VARIABLE        
        int infInt;
        int supInt;
        String infStr;
        String supStr;
        switch (VariablesEnum.convert(generic_table)) {//
            case age:
                infInt = 0;
                supInt = 4;
                for (int i = 0; i < 16; i++) {
                    infStr = String.valueOf(infInt);
                    supStr = String.valueOf(supInt);
                    if (infStr.length() == 1) {
                        infStr = "0" + infStr;
                    }
                    if (supStr.length() == 1) {
                        supStr = "0" + supStr;
                    }
                    valuesName.add(infStr + "/" + supStr);
                    valuesConf.add(infStr + "/" + supStr);
                    valuesId.add(infStr + "/" + supStr);
                    infInt = infInt + 5;
                    supInt = supInt + 5;
                }
                valuesName.add("80/n");
                valuesConf.add("80/n");
                valuesId.add("80/n");
                break;
            case injuries_fatal:
                valuesId.add("10");
                valuesName.add("HOMICIDIO");
                valuesConf.add("HOMICIDIO");
                valuesId.add("11");
                valuesName.add("MUERTE EN ACCIDENTE DE TRANSITO");
                valuesConf.add("MUERTE EN ACCIDENTE DE TRANSITO");
                valuesId.add("12");
                valuesName.add("SUICIDIO");
                valuesConf.add("SUICIDIO");
                valuesId.add("13");
                valuesName.add("MUERTE ACCIDENTAL");
                valuesConf.add("MUERTE ACCIDENTAL");
                break;
            case injuries_non_fatal:
                valuesId.add("50");
                valuesName.add("VIOLENCIA INTERPERSONAL");
                valuesConf.add("VIOLENCIA INTERPERSONAL");
                valuesId.add("51");
                valuesName.add("LESION EN ACCIDENTE DE TRANSITO");
                valuesConf.add("LESION EN ACCIDENTE DE TRANSITO");
                valuesId.add("52");
                valuesName.add("INTENCIONAL AUTOINFLINGIDA");
                valuesConf.add("INTENCIONAL AUTOINFLINGIDA");
                valuesId.add("53");
                valuesName.add("VIOLENCIA INTRAFAMILIAR");
                valuesConf.add("VIOLENCIA INTRAFAMILIAR");
                valuesId.add("54");
                valuesName.add("NO INTENCIONAL");
                valuesConf.add("NO INTENCIONAL");
                valuesId.add("55");
                valuesName.add("VIOLENCIA INTRAFAMILIAR");
                valuesConf.add("VIOLENCIA INTRAFAMILIAR");
                break;
            case hour:
                infInt = 0;
                supInt = 2;
                for (int i = 0; i < 8; i++) {
                    valuesName.add(String.valueOf(infInt) + ":00/" + String.valueOf(supInt) + ":59");
                    valuesConf.add(String.valueOf(infInt) + ":00/" + String.valueOf(supInt) + ":59");
                    valuesId.add(String.valueOf(infInt) + ":00/" + String.valueOf(supInt) + ":59");
                    infInt = supInt + 1;
                    supInt = supInt + 3;
                }
                break;
            case day:
                break;
            case month:
                valuesId.add("1");
                valuesName.add("ENERO");
                valuesConf.add("ENERO");
                valuesId.add("2");
                valuesName.add("FEBRERO");
                valuesConf.add("FEBRERO");
                valuesId.add("3");
                valuesName.add("MARZO");
                valuesConf.add("MARZO");
                valuesId.add("4");
                valuesName.add("ABRIL");
                valuesConf.add("ABRIL");
                valuesId.add("5");
                valuesName.add("MAYO");
                valuesConf.add("MAYO");
                valuesId.add("6");
                valuesName.add("JUNIO");
                valuesConf.add("JUNIO");
                valuesId.add("7");
                valuesName.add("JULIO");
                valuesConf.add("JULIO");
                valuesId.add("8");
                valuesName.add("AGOSTO");
                valuesConf.add("AGOSTO");
                valuesId.add("9");
                valuesName.add("SEPTIEMBRE");
                valuesConf.add("SEPTIEMBRE");
                valuesId.add("10");
                valuesName.add("OCTUBRE");
                valuesConf.add("OCTUBRE");
                valuesId.add("11");
                valuesName.add("NOVIEMBRE");
                valuesConf.add("NOVIEMBRE");
                valuesId.add("12");
                valuesName.add("DICIEMBRE");
                valuesConf.add("DICIEMBRE");
                break;
            case year:
                //System.out.println("AÑO ACTUAL" + currentYear);
                for (int i = 2003; i <= currentYear; i++) {
                    valuesName.add(String.valueOf(i));
                    valuesConf.add(String.valueOf(i));
                    valuesId.add(String.valueOf(i));
                }
                break;
            case neighborhoods://barrio,
            case communes://comuna,
            case corridors://corredor,
            case areas://zona,
            case genders://genero,
            case days://dia semana
            case NOVALUE://es una tabla categorica
                try {
                    ResultSet rs = connectionJdbcMB.consult("Select * from " + generic_table);
                    while (rs.next()) {
                        valuesName.add(rs.getString(2));
                        valuesConf.add(rs.getString(2));
                        valuesId.add(rs.getString(1));
                    }
                } catch (Exception e) {
                }
                break;
        }
        newVariable.setValues(valuesName);
        newVariable.setValuesId(valuesId);
        newVariable.setValuesConfigured(valuesConf);
        return newVariable;
    }

    public ArrayList<Variable> getVariablesIndicator() {
        ArrayList<Variable> arrayReturn = new ArrayList<Variable>();
        currentIndicator = indicatorsFacade.find(currentIndicator.getIndicatorId());
        for (int i = 0; i < currentIndicator.getIndicatorsVariablesList().size(); i++) {
            arrayReturn.add(
                    createVariable(
                    currentIndicator.getIndicatorsVariablesList().get(i).getIndicatorsVariablesPK().getVariableName(),
                    currentIndicator.getIndicatorsVariablesList().get(i).getCategory(),
                    currentIndicator.getIndicatorsVariablesList().get(i).getAddValues()));
        }
        return arrayReturn;
    }

    public List<String> getGraphTypes() {
        ArrayList<String> listReturn = new ArrayList<String>();
        try {
            ResultSet rs = connectionJdbcMB.consult("Select graph_type from indicators where indicator_id=" + String.valueOf(currentIndicator.getIndicatorId()));
            if (rs.next()) {
                String[] splitGraphType = rs.getString(1).split(",");
                listReturn.addAll(Arrays.asList(splitGraphType));
            }
        } catch (Exception e) {
        }
        return listReturn;
    }

    private String getColorType() {
        if (colorType) {
            return "bgcolor=\"#DDDDFF\"";
        } else {
            return "bgcolor=\"#FFFFFF\"";
        }
    }

    private void changeColorType() {
        if (colorType) {
            colorType = false;
        } else {
            colorType = true;
        }
    }

    private String getMatrixValue(String type, int col, int row) {
        String strReturn = "-1";

        DecimalFormat formateador = new DecimalFormat("0.00");
        try {
            if (type.compareTo("countXY") == 0) {//valor en la matriz            
                return matrixResult[col][row];
            }
            if (type.compareTo("rowTotal") == 0) {//total por fila
                return totalsVertical.get(row);
            }
            if (type.compareTo("columnTotal") == 0) {//total columna
                return totalsHorizontal.get(col);
            }
            if (type.compareTo("columnPercentageXY") == 0) {//porcentaje segun columna para una posicion de la matriz
                if (Double.parseDouble(totalsHorizontal.get(col)) == 0) {
                    return "0";
                } else {
                    return formateador.format((Double.parseDouble(matrixResult[col][row]) * 100) / Double.parseDouble(totalsHorizontal.get(col)));
                }
            }

            if (type.compareTo("rowPercentageXY") == 0) {//porcentaje segun fila para una posicion de la matriz
                if (Double.parseDouble(totalsVertical.get(row)) == 0) {
                    return "0";
                } else {
                    return formateador.format((Double.parseDouble(matrixResult[col][row]) * 100) / Double.parseDouble(totalsVertical.get(row)));
                }
            }
//            if (type.compareTo("rowPercentageTotal") == 0) {//porcentaje segun fila para un total horizontal
//                if (Double.parseDouble(totalsHorizontal.get(col)) == 0) {
//                    return "0";
//                } else {
//                    return formateador.format((Double.parseDouble(totalsHorizontal.get(col)) * 100) / (double) grandTotal);
//                }
//            }
            if (type.compareTo("totalPercentageXY") == 0) {//porcentaje de una posicion de la matriz segun el total general 
                if (grandTotal == 0) {
                    return "0";
                } else {
                    return formateador.format((Double.parseDouble(matrixResult[col][row]) * 100) / (double) grandTotal);
                }
            }
            if (type.compareTo("percentageOfTotalRowAccordingTotalRow") == 0) {//porcentaje del total de una fila segun el total de la fila                
                if (Double.parseDouble(totalsVertical.get(row)) == 0) {
                    return "0";
                } else {
                    return "100";
                }
            }
            if (type.compareTo("percentageOfTotalColumnAccordingTotalColumn") == 0) {//porcentaje del total de una columna segun el total de la columna                
                if (Double.parseDouble(totalsHorizontal.get(col)) == 0) {
                    return "0";
                } else {
                    return "100";
                }
            }
            if (type.compareTo("percentageOfTotalRowAccordingGrandTotal") == 0) {//porcentaje del total de una fila segun el total general            
                if (grandTotal == 0) {
                    return "0";
                } else {
                    return formateador.format((Double.parseDouble(totalsVertical.get(row)) * 100) / (double) grandTotal);
                }
            }
            if (type.compareTo("percentageOfTotalColumnAccordingGrandTotal") == 0) {//porcentaje del total de una columna segun el total general
                if (grandTotal == 0) {
                    return "0";
                } else {
                    return formateador.format((Double.parseDouble(totalsHorizontal.get(col)) * 100) / (double) grandTotal);
                }
            }
            if (type.compareTo("percentageOfGrandTotalAccordingGrandTotal") == 0) {//porcentaje del gran total en base al gran total
                if (grandTotal == 0) {
                    return "0";
                } else {
                    return "100";
                }
            }
        } catch (Exception e) {
            System.out.println("Error sacando valores de la matriz: " + e.toString());
        }
        return strReturn;
    }

    private String createDataTableResult() {
        PanelGrid panelGrid = new PanelGrid();
        headers1 = new ArrayList<SpanColumns>();
        headers2 = new String[columNames.size()];

        String strReturn = " ";
        strReturn = strReturn + "<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\">\r\n";
        strReturn = strReturn + "            <tr>\r\n";
        strReturn = strReturn + "                <td id=\"firstTd\" >\r\n";
        strReturn = strReturn + "                </td>\r\n";
        strReturn = strReturn + "                <td class=\"ui-widget-header\">\r\n";
        //-------------------------------------------------------------------
        //TABLA QUE CONTIENE LA CABECERA
        //-------------------------------------------------------------------        
        strReturn = strReturn + "                    <div id=\"divHeader\" style=\"overflow:hidden;width:434px;\">\r\n";//484 TAMAÑO DIV HEADER SUPERIOR(16=heigth del scroll)
        strReturn = strReturn + "                        <table cellspacing=\"0\" cellpadding=\"0\" border=\"1\" >\r\n";
        if (variablesCrossData.size() == 2 || variablesCrossData.size() == 1) {
            //-------------------------------------------------------------------
            //CABECERA SIMPLE
            strReturn = strReturn + "                            <tr>\r\n";
            for (int i = 0; i < columNames.size(); i++) {
                strReturn = strReturn + "                                <td>\r\n";
                strReturn = strReturn + "                                    <div class=\"tableHeader\">" + columNames.get(i) + "</div>\r\n";
                strReturn = strReturn + "                                </td>\r\n";
            }
            strReturn = strReturn + "                                <td>\r\n";
            strReturn = strReturn + "                                    <div class=\"tableHeader\">Total</div>\r\n";
            strReturn = strReturn + "                                </td>\r\n";
            strReturn = strReturn + "                            </tr>\r\n";
        }
        if (variablesCrossData.size() == 3) {
            //-------------------------------------------------------------------
            //CABECERA COMPUESTA            
            String currentVar = "";
            String[] splitVars;
            for (int i = 0; i < columNames.size(); i++) {
                splitVars = columNames.get(i).split("\\}");//separo las dos variables
                if (splitVars[0].compareTo(currentVar) == 0) {//ya existe solo le aumento el numero de columnas unidas al ultimo de la lista "headers1"
                    int num = headers1.get(headers1.size() - 1).getColumns();
                    headers1.get(headers1.size() - 1).setColumns(num + 1);
                } else {//no existe la columna la debo crear y adicionar a la lista                    
                    currentVar = splitVars[0];
                    SpanColumns newSpanColumn = new SpanColumns();
                    newSpanColumn.setLabel(splitVars[0]);
                    newSpanColumn.setColumns(1);
                    headers1.add(newSpanColumn);
                }
                headers2[i] = splitVars[1];//a la segunda cabecera le agrego la segunda variable separada
            }
            //AGREGO LA CABECERA 1 A El PANEL_GRID
            strReturn = strReturn + "                            <tr>\r\n";
            for (int i = 0; i < headers1.size(); i++) {
                strReturn = strReturn + "                                <td colspan=\"" + headers1.get(i).getColumns() + "\">\r\n";
                strReturn = strReturn + "                                    <div >" + headers1.get(i).getLabel() + "</div>\r\n";
                strReturn = strReturn + "                                </td>\r\n";
            }
            strReturn = strReturn + "                                <td >\r\n";
            strReturn = strReturn + "                                    <div >-</div>\r\n";
            strReturn = strReturn + "                                </td>\r\n";
            strReturn = strReturn + "                            </tr>\r\n";

            strReturn = strReturn + "                            <tr>\r\n";
            //AGREGO LA CABECERA 2 A El PANEL_GRID
            for (int i = 0; i < headers2.length; i++) {
                strReturn = strReturn + "                                <td>\r\n";
                strReturn = strReturn + "                                    <div class=\"tableHeader\">" + headers2[i] + "</div>\r\n";
                strReturn = strReturn + "                                </td>\r\n";
            }
            strReturn = strReturn + "                                <td >\r\n";
            strReturn = strReturn + "                                    <div class=\"tableHeader\">Total</div>\r\n";
            strReturn = strReturn + "                                </td>\r\n";
            strReturn = strReturn + "                            </tr>\r\n";
        }
        strReturn = strReturn + "                        </table>\r\n";
        strReturn = strReturn + "                    </div>\r\n";
        strReturn = strReturn + "                </td>\r\n";
        strReturn = strReturn + "            </tr>\r\n";
        strReturn = strReturn + "            <tr>\r\n";
        strReturn = strReturn + "                <td valign=\"top\" class=\"ui-widget-header\">\r\n";

        //-------------------------------------------------------------------
        //TABLA QUE CONTIENE LA PRIMER COLUMNA
        //-------------------------------------------------------------------        
        int rowsForRecord = 0;//filas a crear por registro(inicia en 1 por el rowspan cuenta desde 1)
        if (showColumnPercentage) {
            rowsForRecord++;
        }
        if (showRowPercentage) {
            rowsForRecord++;
        }
        if (showCount) {
            rowsForRecord++;
        }
        if (showTotalPercentage) {
            rowsForRecord++;
        }

        strReturn = strReturn + "                    <div id=\"firstcol\" style=\"overflow: hidden;height:280px\">\r\n";//tamaño del div izquierdo
        strReturn = strReturn + "                        <table width=\"200px\" cellspacing=\"0\" cellpadding=\"0\" border=\"1\" >\r\n";

        rowNames.add("Totales");
        for (int j = 0; j < rowNames.size(); j++) {
            //----------------------------------------------------------------------
            //NOMBRE PARA CADA FILA            
            boolean showCountAdd = false;
            boolean showRowPercentageAdd = false;
            boolean showColumnPercentageAdd = false;
            boolean showTotalPercentageAdd = false;
            strReturn = strReturn + "                            <tr>\r\n";
            strReturn = strReturn + "                                <td rowspan=\"" + rowsForRecord + "\">" + rowNames.get(j) + "</td>\r\n";
            if (showCount && !showCountAdd && !showRowPercentageAdd && !showColumnPercentageAdd && !showTotalPercentageAdd) {
                strReturn = strReturn + "                                <td class=\"tableFirstCol\">Recuento</td>\r\n";
                showCountAdd = true;
            }
            if (showRowPercentage && !showCountAdd && !showRowPercentageAdd && !showColumnPercentageAdd && !showTotalPercentageAdd) {
                strReturn = strReturn + "                                <td class=\"tableFirstCol\">% por fila</td>\r\n";
                showRowPercentageAdd = true;
            }
            if (showColumnPercentage && !showCountAdd && !showRowPercentageAdd && !showColumnPercentageAdd && !showTotalPercentageAdd) {
                strReturn = strReturn + "                                <td class=\"tableFirstCol\">% por columna</td>\r\n";
                showColumnPercentageAdd = true;
            }
            if (showTotalPercentage && !showCountAdd && !showRowPercentageAdd && !showColumnPercentageAdd && !showTotalPercentageAdd) {
                strReturn = strReturn + "                                <td class=\"tableFirstCol\">% del total</td>\r\n";
                showTotalPercentageAdd = true;
            }
            strReturn = strReturn + "                            </tr>\r\n";

            if (showCount && !showCountAdd) {
                strReturn = strReturn + "                            <tr>\r\n";
                strReturn = strReturn + "                                <td class=\"tableFirstCol\">recuento</td>\r\n";
                strReturn = strReturn + "                            </tr>\r\n";
            }
            if (showRowPercentage && !showRowPercentageAdd) {
                strReturn = strReturn + "                            <tr>\r\n";
                strReturn = strReturn + "                                <td class=\"tableFirstCol\">% por fila</td>\r\n";
                strReturn = strReturn + "                            </tr>\r\n";
            }
            if (showColumnPercentage && !showColumnPercentageAdd) {
                strReturn = strReturn + "                            <tr>\r\n";
                strReturn = strReturn + "                                <td class=\"tableFirstCol\">% por columna</td>\r\n";
                strReturn = strReturn + "                            </tr>\r\n";
            }
            if (showTotalPercentage && !showTotalPercentageAdd) {
                strReturn = strReturn + "                            <tr>\r\n";
                strReturn = strReturn + "                                <td class=\"tableFirstCol\">% del total</td>\r\n";
                strReturn = strReturn + "                            </tr>\r\n";
            }
        }
        strReturn = strReturn + "                        </table>\r\n";
        strReturn = strReturn + "                    </div>\r\n";
        strReturn = strReturn + "                </td>\r\n";
        strReturn = strReturn + "                <td valign=\"top\">\r\n";
        
        
        //-------------------------------------------------------------------
        //TABLA QUE CONTIENE LOS DATOS DE LA MATRIZ
        //-------------------------------------------------------------------      
        int sizeTableMatrix = columNames.size() * 150;//que cada columna tenga 100px
        sizeTableMatrix = sizeTableMatrix + 100;//de los totales
        strReturn = strReturn + "                    <div id=\"table_div\" style=\"overflow: scroll;width:450px;height:300px;position:relative\" onscroll=\"fnScroll()\" >\r\n";//div que maneja la tabla
        strReturn = strReturn + "                        <table width=\"" + sizeTableMatrix + "px\" cellspacing=\"0\" cellpadding=\"0\" border=\"1\" >\r\n";//
        //----------------------------------------------------------------------
        //AGREGO LOS REGISTROS DE LA MATRIZ        
        for (int j = 0; j < rowNames.size() - 1; j++) {//-1 por que le agrege "TOTALES"
            if (showCount) {
                if (j == 0) {
                    strReturn = strReturn + "                            <tr "+ getColorType() + " id='firstTr'>\r\n";
                } else {
                    strReturn = strReturn + "                            <tr "+ getColorType() + " >\r\n";
                }
                for (int i = 0; i < columNames.size(); i++) {
                    strReturn = strReturn + "                                <td>" + getMatrixValue("countXY", i, j) + "</td>\r\n";
                }
                strReturn = strReturn + "                                <td>" + getMatrixValue("rowTotal", -1, j) + "</td>\r\n";
                strReturn = strReturn + "                            </tr>\r\n";
            }
            if (showRowPercentage) {
                if (j == 0) {//si es la primera fila tiene elidentificador 'firstTr'
                    strReturn = strReturn + "                            <tr "+ getColorType() + "  id='firstTr'>\r\n";
                } else {
                    strReturn = strReturn + "                            <tr "+ getColorType() + " >\r\n";
                }
                for (int i = 0; i < columNames.size(); i++) {
                    strReturn = strReturn + "                                <td>" + getMatrixValue("rowPercentageXY", i, j) + "</td>\r\n";
                }
                strReturn = strReturn + "                                <td>" + getMatrixValue("percentageOfTotalRowAccordingTotalRow", -1, j) + "</td>\r\n";                
                strReturn = strReturn + "                            </tr>\r\n";
            }
            //total = 0;
            if (showColumnPercentage) {
                if (j == 0) {
                    strReturn = strReturn + "                            <tr "+ getColorType() + "  id='firstTr'>\r\n";
                } else {
                    strReturn = strReturn + "                            <tr "+ getColorType() + " >\r\n";
                }
                for (int i = 0; i < columNames.size(); i++) {
                    strReturn = strReturn + "                                <td>" + getMatrixValue("columnPercentageXY", i, j) + "</td>\r\n";
                }
                strReturn = strReturn + "                                <td>" + getMatrixValue("percentageOfTotalRowAccordingGrandTotal", -1, j) + "</td>\r\n";
                strReturn = strReturn + "                            </tr>\r\n";
            }
            //total = 0;
            if (showTotalPercentage) {
                if (j == 0) {
                    strReturn = strReturn + "                            <tr "+ getColorType() + "  id='firstTr'>\r\n";
                } else {
                    strReturn = strReturn + "                            <tr "+ getColorType() + " >\r\n";
                }
                for (int i = 0; i < columNames.size(); i++) {
                    strReturn = strReturn + "                                <td>" + getMatrixValue("totalPercentageXY", i, j) + "</td>\r\n";
                }
                strReturn = strReturn + "                                <td>" + getMatrixValue("percentageOfTotalRowAccordingGrandTotal", 0, j) + "</td>\r\n";
                strReturn = strReturn + "                            </tr>\r\n";
            }
            changeColorType();//cambiar de color las filas de blanco a azul
        }

        //----------------------------------------------------------------------
        //AGREGO LA ULTIMA FILA CORRESPONDIENTE A LOS TOTALES
        //----------------------------------------------------------------------

        if (showCount) {
            strReturn = strReturn + "                            <tr "+ getColorType() + " >\r\n";
            for (int i = 0; i < totalsHorizontal.size(); i++) {
                strReturn = strReturn + "                                <td>" + getMatrixValue("columnTotal", i, 0) + "</td>\r\n";
            }
            strReturn = strReturn + "                                <td>" + String.valueOf(grandTotal) + "</td>\r\n";
            strReturn = strReturn + "                            </tr>\r\n";
        }

        if (showRowPercentage) {
            strReturn = strReturn + "                            <tr "+ getColorType() + " >\r\n";
            for (int i = 0; i < totalsHorizontal.size(); i++) {
                strReturn = strReturn + "                                <td>" + getMatrixValue("percentageOfTotalColumnAccordingGrandTotal", i, 0) + "</td>\r\n";
            }
            strReturn = strReturn + "                                <td>" + getMatrixValue("percentageOfGrandTotalAccordingGrandTotal", 0, 0) + "</td>\r\n";
            strReturn = strReturn + "                            </tr>\r\n";
        }
        if (showColumnPercentage) {
            strReturn = strReturn + "                            <tr "+ getColorType() + " >\r\n";
            for (int i = 0; i < totalsHorizontal.size(); i++) {
                strReturn = strReturn + "                                <td>" + getMatrixValue("percentageOfTotalColumnAccordingTotalColumn", i, 0) + "</td>\r\n";
            }
            strReturn = strReturn + "                                <td>" + getMatrixValue("percentageOfGrandTotalAccordingGrandTotal", 0, 0) + "</td>\r\n";
            strReturn = strReturn + "                            </tr>\r\n";
        }
        if (showTotalPercentage) {
            strReturn = strReturn + "                            <tr "+ getColorType() + " >\r\n";
            for (int i = 0; i < totalsHorizontal.size(); i++) {
                //strReturn = strReturn + "                                <td>" + getMatrixValue("percentageOfTotalColumnAccordingGrandTotal", i, 0) + "</td>\r\n";
                strReturn = strReturn + "                                <td>" + getMatrixValue("percentageOfTotalColumnAccordingGrandTotal", i, 0) + "</td>\r\n";
            }
            strReturn = strReturn + "                                <td>" + getMatrixValue("percentageOfGrandTotalAccordingGrandTotal", 0, 0) + "</td>\r\n";
            strReturn = strReturn + "                            </tr>\r\n";
        }
        //-------------------------------------------------------------------
        //FINALIZA
        //-------------------------------------------------------------------        
        strReturn = strReturn + "                        </table>\r\n";
        strReturn = strReturn + "                    </div>\r\n";
        strReturn = strReturn + "                </td>\r\n";
        strReturn = strReturn + "            </tr>\r\n";
        strReturn = strReturn + "        </table>\r\n";
        System.out.println(strReturn);
        return strReturn;
    }

    public PieDataset createPieDataset() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Dato A", new Double(45.0));
        dataset.setValue("Dato B", new Double(15.0));
        dataset.setValue("Dato C", new Double(25.2));
        dataset.setValue("Dato E", new Double(14.8));
        return dataset;
    }

    public CategoryDataset createDefaultDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(32.4, "Series 1", "Category 1");
        dataset.addValue(17.8, "Series 2", "Category 1");
        dataset.addValue(27.7, "Series 3", "Category 1");
        dataset.addValue(43.2, "Series 1", "Category 2");
        dataset.addValue(15.6, "Series 2", "Category 2");
        dataset.addValue(18.3, "Series 3", "Category 2");
        dataset.addValue(23.0, "Series 1", "Category 3");
        dataset.addValue(111.3, "Series 2", "Category 3");
        dataset.addValue(25.5, "Series 3", "Category 3");
        dataset.addValue(13.0, "Series 1", "Category 4");
        dataset.addValue(11.8, "Series 2", "Category 4");
        dataset.addValue(29.5, "Series 3", "Category 4");
        return dataset;
    }

    public JFreeChart createStackedBarChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String indicatorName = currentIndicator.getIndicatorName();
        String categoryAxixLabel = "";
        try {

            int pos = 0;
            String sql = "";
            sql = sql + "SELECT * FROM " + pivotTableName;
            ResultSet rs;
            if (variablesCrossData.size() == 1) {
                rs = connectionJdbcMB.consult(sql);
                while (rs.next()) {
                    dataset.setValue(rs.getLong("count"), rs.getString(1), "-");
                }
            }
            if (variablesCrossData.size() == 2) {
                rs = connectionJdbcMB.consult(sql);
                while (rs.next()) {
                    dataset.setValue(rs.getLong("count"), rs.getString(1), rs.getString(2));
                }
            }
            if (variablesCrossData.size() == 3) {
                //determino los nombres de las columnas
                rs = connectionJdbcMB.consult(sql);
                int numberColumns = rs.getMetaData().getColumnCount();
                ArrayList<String> nameColumns = new ArrayList<String>();
                for (int i = 1; i <= numberColumns; i++) {//metadata cuenta desde 1
                    nameColumns.add(rs.getMetaData().getColumnName(i));
                }
                //determino el numero de columna a filtrar (variable en variableCrossData                
                for (int i = 0; i < variablesCrossData.size(); i++) {
                    if (variablesCrossData.get(i).getName().compareTo(currentVariableGraph) == 0) {
                        pos = i;
                        break;
                    }
                }
                //adicino la instruccion WHERE a la consulta
                sql = sql + " WHERE " + nameColumns.get(pos) + " LIKE '" + currentValueGraph + "' ";
                rs = connectionJdbcMB.consult(sql);
                if (pos == 0) {
                    while (rs.next()) {
                        dataset.setValue(rs.getLong("count"), rs.getString(2), rs.getString(3));
                    }
                    categoryAxixLabel = variablesCrossData.get(2).getName();
                }
                if (pos == 1) {
                    while (rs.next()) {
                        dataset.setValue(rs.getLong("count"), rs.getString(1), rs.getString(3));
                    }
                    categoryAxixLabel = variablesCrossData.get(2).getName();
                }
                if (pos == 2) {
                    while (rs.next()) {
                        dataset.setValue(rs.getLong("count"), rs.getString(1), rs.getString(2));
                    }
                    categoryAxixLabel = variablesCrossData.get(1).getName();
                }
                indicatorName = currentIndicator.getIndicatorName() + "\n(" + currentVariableGraph + " es " + currentValueGraph + ")";
            }
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.toString());
        }

        JFreeChart chartReturn = ChartFactory.createStackedBarChart(
                indicatorName,
                categoryAxixLabel,
                "porcentaje", // range axis label
                dataset, // data
                PlotOrientation.VERTICAL, // the plot orientation
                true, // legend
                true, // tooltips
                false // urls
                );

        CategoryPlot plot = (CategoryPlot) chartReturn.getPlot();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setNumberFormatOverride(NumberFormat.getPercentInstance());
        StackedBarRenderer renderer = (StackedBarRenderer) plot.getRenderer();
        renderer.setRenderAsPercentages(true);
        renderer.setDrawBarOutline(false);
        renderer.setBaseItemLabelsVisible(true);
        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        return chartReturn;
    }

    public void createMatrixResult() {

        try {//System.out.println("INICIA CREAR MATRIZ xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
            ArrayList<String> columnNamesPivot = new ArrayList<String>();
            columNames = new ArrayList<String>();
            rowNames = new ArrayList<String>();
            //---------------------------------------------------------
            //DETEMINO LOS NOMBRES DE LAS COLUMNAS DE TABLA PIVOT()
            //---------------------------------------------------------
            ResultSet rs = connectionJdbcMB.consult("SELECT * FROM " + pivotTableName);
            int ncol = rs.getMetaData().getColumnCount();
            for (int i = 1; i <= ncol; i++) {
                columnNamesPivot.add(rs.getMetaData().getColumnName(i));
            }
            //---------------------------------------------------------            
            //DETERMINO NOMBRES DE COLUMNAS PARA MATIRZ SALIDA
            //---------------------------------------------------------            
            if (variablesCrossData.size() == 2 || variablesCrossData.size() == 1) {
//                rs = connectionJdbcMB.consult(
//                        "SELECT DISTINCT " + columnNamesPivot.get(0)
//                        + " FROM " + pivotTableName
//                        + " ORDER BY " + columnNamesPivot.get(0));
                rs = connectionJdbcMB.consult(
                        "SELECT " + columnNamesPivot.get(0)
                        + " FROM " + pivotTableName
                        + " GROUP BY " + columnNamesPivot.get(0)
                        + " order by MIN(id);");
            }
            if (variablesCrossData.size() == 3) {
//                rs = connectionJdbcMB.consult(
//                        "SELECT DISTINCT ("
//                        + columnNamesPivot.get(0) + "||'}'||" + columnNamesPivot.get(1)
//                        + ") " + columnNamesPivot.get(0) + " , " + columnNamesPivot.get(1)
//                        + " FROM " + pivotTableName
//                        + " ORDER BY " + columnNamesPivot.get(0) + " , " + columnNamesPivot.get(1));
                String sql =
                        "SELECT "
                        + columnNamesPivot.get(0) + "||'}'||" + columnNamesPivot.get(1)
                        + " FROM " + pivotTableName
                        + " group by "
                        + columnNamesPivot.get(0) + "||'}'||" + columnNamesPivot.get(1)
                        + " order by "
                        + " MIN(id); ";
                rs = connectionJdbcMB.consult(sql);
            }
            while (rs.next()) {
                columNames.add(rs.getString(1));
            }
            //---------------------------------------------------------            
            //DETERMINO NOMBRES DE FILAS PARA MATIRZ SALIDA
            //---------------------------------------------------------            
            if (variablesCrossData.size() == 1) {
                rowNames.add("Valor");
            }
            if (variablesCrossData.size() == 2) {
                rs = connectionJdbcMB.consult(
                        "SELECT " + columnNamesPivot.get(1)
                        + " FROM " + pivotTableName
                        + " GROUP BY " + columnNamesPivot.get(1)
                        + " order by MIN(id);");
            }
            if (variablesCrossData.size() == 3) {
                rs = connectionJdbcMB.consult(
                        "SELECT " + columnNamesPivot.get(2)
                        + " FROM " + pivotTableName
                        + " GROUP BY " + columnNamesPivot.get(2)
                        + " order by MIN(id);");
            }
            while (rs.next()) {
                rowNames.add(rs.getString(1));
            }
            //---------------------------------------------------------            
            //SE CREA LA MATRIZ DE RESULTADOS (iniciada en 0 )
            //---------------------------------------------------------
            matrixResult = new String[columNames.size()][rowNames.size()];
            for (int i = 0; i < columNames.size(); i++) {
                for (int j = 0; j < rowNames.size(); j++) {
                    matrixResult[i][j] = "0";
                }
            }
            rs = connectionJdbcMB.consult("SELECT * FROM " + pivotTableName);
            while (rs.next()) {
                boolean find = false;
                for (int i = 0; i < columNames.size(); i++) {
                    for (int j = 0; j < rowNames.size(); j++) {
                        if (variablesCrossData.size() == 1) {//ES UNA VARIABLE                            
                            if (rs.getString(1).compareTo(columNames.get(i)) == 0) {
                                matrixResult[i][j] = rs.getString("count");
                                find = true;
                            }
                        }
                        if (variablesCrossData.size() == 2) {//SON DOS VARIABLES                            
                            if (rs.getString(1).compareTo(columNames.get(i)) == 0 && rs.getString(2).compareTo(rowNames.get(j)) == 0) {
                                matrixResult[i][j] = rs.getString("count");
                                find = true;
                            }
                        }
                        if (variablesCrossData.size() == 3) {//SON TRES VARIABLES                            
                            if (columNames.get(i).compareTo(rs.getString(1) + "}" + rs.getString(2)) == 0 && rs.getString(3).compareTo(rowNames.get(j)) == 0) {
                                matrixResult[i][j] = rs.getString("count");
                                find = true;
                            }
                        }
                        if (find) {
                            break;
                        }
                    }
                    if (find) {
                        break;
                    }
                }
            }
            //---------------------------------------------------------            
            //DETERMINO LOS VECTORES TOTALES DE FILAS Y TOTALES DE COLUMNAS
            //---------------------------------------------------------            
            //System.out.println("INICIA DETERMINO LOS VECTORES TOTALES DE FILAS Y TOTALES DE COLUMNAS xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
            totalsHorizontal = new ArrayList<String>();
            totalsVertical = new ArrayList<String>();
            for (int i = 0; i < columNames.size(); i++) {
                totalsHorizontal.add("0");
            }
            int total;
            for (int j = 0; j < rowNames.size(); j++) {
                //AGREGO LOS DATOS DE LA FILA
                total = 0;
                for (int i = 0; i < columNames.size(); i++) {
                    totalsHorizontal.set(i, String.valueOf(Integer.parseInt(totalsHorizontal.get(i)) + Integer.parseInt(matrixResult[i][j])));
                    total = total + Integer.parseInt(matrixResult[i][j]);
                }
                totalsVertical.add(String.valueOf(total));
            }
            //determino general total
            grandTotal = 0;
            for (int i = 0; i < totalsVertical.size(); i++) {
                grandTotal = grandTotal + Integer.parseInt(totalsVertical.get(i));
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        }
        //System.out.println("SQL5----------------\n\r" + sql);
    }

    public void createPivotTable() {
        columNames = new ArrayList<String>();
        rowNames = new ArrayList<String>();
        String sql;
        sql = "\n\r DROP TABLE IF EXISTS " + pivotTableName + ";\n\r";
        sql = sql + " CREATE TABLE  \n\r";
        sql = sql + "	" + pivotTableName + "  \n\r";
        sql = sql + " AS  \n\r";
        sql = sql + " SELECT * from " + prepivotTableName + " \n\r";
        connectionJdbcMB.non_query(sql);
        connectionJdbcMB.non_query("DELETE FROM " + pivotTableName);//elimino registros 
        connectionJdbcMB.non_query("ALTER TABLE " + pivotTableName + " ADD COLUMN id integer;");
        //---------------------------------------------------------
        //HAY QUE ARMAR LAS POSIBLES COMBINACIONES PARA QUE LOS DATOS QUEDEN ORDENADOS SEGUN COMO SE ENCUENTRE LA CONFIGURACION
        //---------------------------------------------------------
        try {
            ArrayList<String> fieldsNames = new ArrayList<String>();

            //---------------------------------------------------------
            //DETEMINO LOS NOMBRES DE LAS COLUMNAS
            //---------------------------------------------------------
            ResultSet rs = connectionJdbcMB.consult("SELECT * FROM " + prepivotTableName);
            int ncol = rs.getMetaData().getColumnCount();
            for (int i = 1; i <= ncol; i++) {
                fieldsNames.add(rs.getMetaData().getColumnName(i));
            }

            //---------------------------------------------------------
            //CREO NUEVOS VECTORES DE VALORES POR QUE PUEDE SER QUE HAYA QUE AGREGAR EL VALOR 'SIN DATO' QUE NO VIENE POR DEFECTO EN LOS VALORES                        
            //---------------------------------------------------------
            ArrayList<String> values1 = new ArrayList<String>();
            if (variablesCrossData.size() > 0) {
                for (int i = 0; i < variablesCrossData.get(0).getValuesConfigured().size(); i++) {
                    values1.add(variablesCrossData.get(0).getValuesConfigured().get(i));
                }
                rs = connectionJdbcMB.consult("SELECT * FROM " + prepivotTableName + " WHERE " + fieldsNames.get(0) + " like 'SIN DATO'");
                if (rs.next()) {
                    values1.add("SIN DATO");
                }
            }
            ArrayList<String> values2 = new ArrayList<String>();
            if (variablesCrossData.size() > 1) {
                for (int i = 0; i < variablesCrossData.get(1).getValuesConfigured().size(); i++) {
                    values2.add(variablesCrossData.get(1).getValuesConfigured().get(i));
                }
                rs = connectionJdbcMB.consult("SELECT * FROM " + prepivotTableName + " WHERE " + fieldsNames.get(1) + " like 'SIN DATO'");
                if (rs.next()) {
                    values2.add("SIN DATO");
                }
            }
            ArrayList<String> values3 = new ArrayList<String>();
            if (variablesCrossData.size() > 2) {
                for (int i = 0; i < variablesCrossData.get(2).getValuesConfigured().size(); i++) {
                    values3.add(variablesCrossData.get(2).getValuesConfigured().get(i));
                }
                rs = connectionJdbcMB.consult("SELECT * FROM " + prepivotTableName + " WHERE " + fieldsNames.get(2) + " like 'SIN DATO'");
                if (rs.next()) {
                    values3.add("SIN DATO");
                }
            }
            
            //---------------------------------------------------------
            //REALIZO LAS POSIBLES COMBINACIONES
            //---------------------------------------------------------            
            int id = 0;
            if (variablesCrossData.size() == 1) {
                for (int i = 0; i < values1.size(); i++) {
                    columNames.add(values1.get(i));
                    sql = "INSERT INTO " + pivotTableName + " VALUES (";
                    sql = sql + "'" + values1.get(i) + "',";
                    sql = sql + "'0'," + String.valueOf(id) + ")";
                    id++;
                    connectionJdbcMB.non_query(sql);
                }
                rowNames.add("Cantidad");
            } else if (variablesCrossData.size() == 2) {
//                for (int i = 0; i < values1.size(); i++) {
//                    columNames.add(values1.get(i));
//                    for (int j = 0; j < values2.size(); j++) {
//                        if (i == 0) {
//                            rowNames.add(values2.get(j));
//                        }
//                        sql = "INSERT INTO " + pivotTableName + " VALUES (";
//                        sql = sql + "'" + values1.get(i) + "',";
//                        sql = sql + "'" + values2.get(j) + "',";
//                        sql = sql + "'0')";
//                        connectionJdbcMB.non_query(sql);
//                    }
//                }
                for (int i = 0; i < values1.size(); i++) {
                    columNames.add(values1.get(i));
                    for (int j = 0; j < values2.size(); j++) {
                        if (i == 0) {
                            rowNames.add(values2.get(j));
                        }
                        sql = "INSERT INTO " + pivotTableName + " VALUES (";
                        sql = sql + "'" + values1.get(i) + "',";
                        sql = sql + "'" + values2.get(j) + "',";
                        sql = sql + "'0'," + String.valueOf(id) + ")";
                        connectionJdbcMB.non_query(sql);
                        id++;
                    }
                }
            } else if (variablesCrossData.size() == 3) {
//                for (int i = 0; i < values1.size(); i++) {
//                    for (int j = 0; j < values2.size(); j++) {
//                        columNames.add(values1.get(i) + "}" + values2.get(j));
//                        for (int k = 0; k < values3.size(); k++) {
//                            if (i == 0 && j == 0) {
//                                rowNames.add(values3.get(k));
//                            }
//                            sql = "INSERT INTO " + pivotTableName + " VALUES (";
//                            sql = sql + "'" + values1.get(i) + "',";
//                            sql = sql + "'" + values2.get(j) + "',";
//                            sql = sql + "'" + values3.get(k) + "',";
//                            sql = sql + "'0')";
//                            connectionJdbcMB.non_query(sql);
//                        }
//                    }
//                }
                for (int i = 0; i < values1.size(); i++) {
                    for (int j = 0; j < values2.size(); j++) {
                        columNames.add(values1.get(i) + "}" + values2.get(j));
                        for (int k = 0; k < values3.size(); k++) {
                            if (i == 0 && j == 0) {
                                rowNames.add(values3.get(k));
                            }
                            sql = "INSERT INTO " + pivotTableName + " VALUES (";
                            sql = sql + "'" + values1.get(i) + "',";
                            sql = sql + "'" + values2.get(j) + "',";
                            sql = sql + "'" + values3.get(k) + "',";
                            sql = sql + "'0'," + String.valueOf(id) + ")";
                            connectionJdbcMB.non_query(sql);
                            id++;
                        }
                    }
                }
            }
            //---------------------------------------------------------            
            //ACTUALZO EL VALOR COUNT DE TABLA PIVOT CON LOS COUNT DE TABLA PREPIVOT
            //---------------------------------------------------------
            sql = " UPDATE " + pivotTableName + " SET count = " + prepivotTableName + ".count \n\r"
                    + " FROM " + prepivotTableName + " \n\r"
                    + " WHERE \n\r";
            for (int i = 0; i < fieldsNames.size() - 1; i++) {//-1 por que la ultima column es count
                sql = sql + " " + pivotTableName + "." + fieldsNames.get(i) + " like " + prepivotTableName + "." + fieldsNames.get(i);
                if (i != fieldsNames.size() - 2) {//menos 2 para saber que es el ultima posicion
                    sql = sql + " AND \n\r";
                }
            }

            connectionJdbcMB.non_query(sql);
            //---------------------------------------------------------            
            //SI NO TOCA MOSTRAR TODOS LOS DATOS SE ELIMINA LOS QUE TENGAN CERO
            //---------------------------------------------------------
//            if (!showAll) {
//                connectionJdbcMB.non_query("DELETE FROM " + pivotTableName + " WHERE count = 0");//elimino los que tengan resultado=0
//            }
        } catch (Exception e) {
            System.out.println("EXCEPTION--------------------------" + e.toString());
        }
    }

    public void createPrepivotTable() {

        String prepivotTableName_2 = prepivotTableName + "_2";
        String sql = "\n\r DROP TABLE IF EXISTS " + prepivotTableName_2 + ";\n\r";
        sql = sql + " CREATE TABLE  \n\r";
        sql = sql + "	" + prepivotTableName_2 + "  \n\r";
        sql = sql + " AS  \n\r";
        sql = sql + " SELECT  \n\r";
        for (int i = 0; i < variablesCrossData.size(); i++) {
            switch (VariablesEnum.convert(variablesCrossData.get(i).getGeneric_table())) {//nombre de variable 
                case injuries_fatal://TIPO DE LESION -----------------------
                    sql = sql + "   CASE (SELECT injury_id FROM injuries WHERE injury_id=" + currentIndicator.getInjuryType() + ".injury_id) \n\r";
                    for (int j = 0; j < variablesCrossData.get(i).getValues().size(); j++) {
                        sql = sql + "       WHEN '" + variablesCrossData.get(i).getValuesId().get(j) + "' THEN '" + variablesCrossData.get(i).getValues().get(j) + "'  \n\r";
                    }
                    sql = sql + "   END AS tipo_lesion";
                    break;
                case age://DETERMINAR EDAD -----------------------                   
                    sql = sql + "   CASE \n\r";
                    for (int j = 0; j < variablesCrossData.get(i).getValuesConfigured().size(); j++) {
                        String[] splitAge = variablesCrossData.get(i).getValuesConfigured().get(j).split("/");
                        if(splitAge[1].compareTo("n")==0){
                            splitAge[1]="200";
                        }
                        sql = sql + "       WHEN (( \n\r";
                        sql = sql + "           CASE \n\r";
                        sql = sql + "               WHEN (victims.age_type_id = 2 or victims.age_type_id = 3) THEN 1 \n\r";
                        sql = sql + "               WHEN (victims.age_type_id = 1) THEN victims.victim_age \n\r";
                        sql = sql + "           END \n\r";
                        sql = sql + "       ) between " + splitAge[0] + " and " + splitAge[1] + ") THEN '" + variablesCrossData.get(i).getValuesConfigured().get(j) + "'  \n\r";
                    }
                    sql = sql + "   END AS edad";
                    break;
                case hour://HORA -----------------------
                    sql = sql + "   CASE \n\r";
                    for (int j = 0; j < variablesCrossData.get(i).getValuesConfigured().size(); j++) {
                        String[] splitAge = variablesCrossData.get(i).getValuesConfigured().get(j).split("/");
                        String[] splitAge2 = splitAge[0].split(":");
                        String[] splitAge3 = splitAge[1].split(":");
                        sql = sql + "       WHEN (extract(hour from fatal_injuries.injury_time) \n\r";
                        sql = sql + "       between " + splitAge2[0] + " and " + splitAge3[0] + ") THEN '" + variablesCrossData.get(i).getValuesConfigured().get(j) + "'  \n\r";
                    }
                    sql = sql + "   END AS hora";
                    break;
                case neighborhoods://NOMBRE DEL BARRIO -----------------------
                    sql = sql + "   (SELECT neighborhood_name FROM neighborhoods WHERE neighborhood_id=" + currentIndicator.getInjuryType() + ".injury_neighborhood_id) as barrio";
                    break;
                case communes://COMUNA -----------------------
                    sql = sql + "   CAST((SELECT suburb_id FROM neighborhoods WHERE neighborhood_id=" + currentIndicator.getInjuryType() + ".injury_neighborhood_id) as text) as comuna";
                    break;
                case corridors://CORREDOR -----------------------
                    sql = sql + "   CASE (SELECT neighborhood_corridor FROM neighborhoods WHERE neighborhood_id=" + currentIndicator.getInjuryType() + ".injury_neighborhood_id) \n\r";
                    sql = sql + "       WHEN '1' THEN 'CENTRAL'  \n\r";
                    sql = sql + "       WHEN '2' THEN 'OCCIDENTAL' \n\r";
                    sql = sql + "       WHEN '3' THEN 'ORIENTAL' \n\r";
                    sql = sql + "       WHEN '4' THEN 'SURORIENTAL' \n\r";
                    sql = sql + "   END AS corredor";
                    break;
                case areas://ZONA -----------------------        
                    sql = sql + "   CASE (SELECT neighborhood_type FROM neighborhoods WHERE neighborhood_id=" + currentIndicator.getInjuryType() + ".injury_neighborhood_id)  \n\r";
                    sql = sql + "       WHEN '1' THEN 'ZONA URBANA'  \n\r";
                    sql = sql + "       WHEN '2' THEN 'ZONA RURAL' \n\r";
                    sql = sql + "   END AS zona";
                    break;
                case genders://GENERO  ----------------------
                    sql = sql + "   CASE (victims.gender_id) \n\r";
                    sql = sql + "       WHEN 1 THEN 'MASCULINO'  \n\r";
                    sql = sql + "       WHEN 2 THEN 'FEMENINO' \n\r";
                    sql = sql + "   END AS genero";
                    break;
                case days://DIA SEMANA ----------------------
                    sql = sql + "   " + currentIndicator.getInjuryType() + ".injury_day_of_week as dia_semana";
                    break;
                case year://AÑO -----------------------
                    sql = sql + "   CAST(extract(year from " + currentIndicator.getInjuryType() + ".injury_date)::int as text) as anyo";
                    break;
                case month://MES -----------------------
                    sql = sql + "   CASE (extract(month from " + currentIndicator.getInjuryType() + ".injury_date)::int) \n\r";
                    sql = sql + "       WHEN 1  THEN 'ENERO' \n\r";
                    sql = sql + "       WHEN 2  THEN 'FEBRERO' \n\r";
                    sql = sql + "       WHEN 3  THEN 'MARZO' \n\r";
                    sql = sql + "       WHEN 4  THEN 'ABRIL' \n\r";
                    sql = sql + "       WHEN 5  THEN 'MAYO' \n\r";
                    sql = sql + "       WHEN 6  THEN 'JUNIO' \n\r";
                    sql = sql + "       WHEN 7  THEN 'JULIO' \n\r";
                    sql = sql + "       WHEN 8  THEN 'AGOSTO' \n\r";
                    sql = sql + "       WHEN 9  THEN 'SEPTIEMBRE' \n\r";
                    sql = sql + "       WHEN 10 THEN 'OCTUBRE' \n\r";
                    sql = sql + "       WHEN 11 THEN 'NOVIEMBRE' \n\r";
                    sql = sql + "       WHEN 12 THEN 'DICIEMBRE' \n\r";
                    sql = sql + "   END AS mes";
                    break;
            }
            if (i == variablesCrossData.size() - 1) {//si es la ultima instruccion se agrega salto de linea
                sql = sql + " \n\r";
            } else {//si no es la ultima instruccion se agrega coma y salto de linea
                sql = sql + ", \n\r";
            }
        }
        sql = sql + "   FROM  \n\r";
        sql = sql + "       " + currentIndicator.getInjuryType() + ", victims \n\r";
        sql = sql + "   WHERE  \n\r";
        sql = sql + "       " + currentIndicator.getInjuryType() + ".victim_id = victims.victim_id AND \n\r";
        if (currentIndicator.getIndicatorId() != 1 && currentIndicator.getIndicatorId() != 2
                && currentIndicator.getIndicatorId() != 3 && currentIndicator.getIndicatorId() != 4) {
            //si no es general se filtra por tipo de lesion
            sql = sql + "       " + currentIndicator.getInjuryType() + ".injury_id = " + currentIndicator.getInjuryId().toString() + " AND \n\r";
        }
        sql = sql + "       " + currentIndicator.getInjuryType() + ".injury_date >= to_date('" + initialDateStr + "','dd/MM/yyyy') AND \n\r";
        sql = sql + "       " + currentIndicator.getInjuryType() + ".injury_date <= to_date('" + endDateStr + "','dd/MM/yyyy'); ";
        connectionJdbcMB.non_query(sql);//CREO LA TABLA PREPIVOT
        //------------------------------------------------------------------
        //QUITAMOS LOS VALORES ELIMINADOS DE CADA CATEGORIA
        //------------------------------------------------------------------
        sql = "";
        List<String> valuesList;//todos los valores (puede y no puede tomar)
        List<String> valuesConfiguredList;//valores configurados (solo los que puede tomar, unos eliminados por el usuario)
        List<String> valuesDiscardedList;//valores descartados de la categoria(los valores que el usuario elimino de la categoria)
        for (int i = 0; i < variablesCrossData.size(); i++) {
            //DETERMINO SI SE HA REALIZADO UNA CONFIGURACION
            valuesDiscardedList = new ArrayList<String>();
            if (variablesCrossData.get(i).getValues().size() != variablesCrossData.get(i).getValuesConfigured().size()) {
                valuesList = variablesCrossData.get(i).getValues();
                valuesConfiguredList = variablesCrossData.get(i).getValuesConfigured();
                for (int j = 0; j < valuesList.size(); j++) {
                    boolean find = false;
                    for (int k = 0; k < valuesConfiguredList.size(); k++) {
                        if (valuesList.get(j).compareTo(valuesConfiguredList.get(k)) == 0) {
                            find = true;
                            break;
                        }
                    }
                    if (!find) {//si el valor no se encuentra es por que se descarto
                        valuesDiscardedList.add(valuesList.get(j));
                    }
                }
            }
            if (!valuesDiscardedList.isEmpty()) {
                switch (VariablesEnum.convert(variablesCrossData.get(i).getGeneric_table())) {//nombre de variable                
                    case age://ELIMINAR SEGUN EDAD -----------------------                                       
                        break;
                    case hour://ELIMINAR SEGUN HORA -----------------------                    
                        break;
                    case neighborhoods://ELIMINAR SEGUN NOMBRE DEL BARRIO -----------------------
                        for (int j = 0; j < valuesDiscardedList.size(); j++) {
                            sql = sql + "OR barrio LIKE '" + valuesDiscardedList.get(j) + "' \n\r";
                        }
                        break;
                    case communes://ELIMINAR SEGUN COMUNA -----------------------
                        for (int j = 0; j < valuesDiscardedList.size(); j++) {
                            sql = sql + "OR comuna LIKE '" + valuesDiscardedList.get(j) + "' \n\r";
                        }
                        break;
                    case corridors://ELIMINAR SEGUN CORREDOR --------------------
                        for (int j = 0; j < valuesDiscardedList.size(); j++) {
                            sql = sql + "OR corredor LIKE '" + valuesDiscardedList.get(j) + "' \n\r";
                        }
                        break;
                    case areas://ELIMINAR SEGUN ZONA -----------------------     
                        for (int j = 0; j < valuesDiscardedList.size(); j++) {
                            sql = sql + "OR zona LIKE '" + valuesDiscardedList.get(j) + "' \n\r";
                        }
                        break;
                    case genders://ELIMINAR SEGUN GENERO  -----------------------
                        for (int j = 0; j < valuesDiscardedList.size(); j++) {
                            sql = sql + "OR genero LIKE '" + valuesDiscardedList.get(j) + "' \n\r";
                        }
                        break;
                    case days://ELIMINAR SEGUN DIA SEMANA -----------------------
                        for (int j = 0; j < valuesDiscardedList.size(); j++) {
                            sql = sql + "OR dia_semana LIKE '" + valuesDiscardedList.get(j) + "' \n\r";
                        }
                        break;
                    case year://ELIMINAR SEGUN AÑO -----------------------
                        for (int j = 0; j < valuesDiscardedList.size(); j++) {
                            sql = sql + "OR anyo LIKE '" + valuesDiscardedList.get(j) + "' \n\r";
                        }
                        break;
                    case month://ELIMINAR SEGUN MES -----------------------
                        for (int j = 0; j < valuesDiscardedList.size(); j++) {
                            sql = sql + "OR mes LIKE '" + valuesDiscardedList.get(j) + "' \n\r";
                        }
                        break;
                }
            }
        }
        if (sql.trim().length() != 0) {
            sql = sql.substring(2, sql.length());//elimino primer "OR"                    
            sql = "\n\r DELETE FROM " + prepivotTableName_2 + " WHERE " + sql;
            connectionJdbcMB.non_query(sql);//REALIZO LAS ElIMINACIONES DE LA TABLA PIVOT
        }
        //------------------------------------------------------------------
        //REEMPLAZO NULL POR 'SIN DATO'
        //------------------------------------------------------------------
        ArrayList<String> columnNames = new ArrayList<String>();
        try {
            ResultSet rs = connectionJdbcMB.consult("SELECT * FROM " + prepivotTableName_2);
            int ncol = rs.getMetaData().getColumnCount();
            //System.out.println("COLUMNAS DE "+prepivotTableName_2+":-------------------------------------------- \n\r"+String.valueOf(ncol));
            for (int i = 1; i < ncol + 1; i++) {
                //System.out.println("AQUI:-------------------------------------------- \n\r");
                sql = "UPDATE " + prepivotTableName_2 + " SET " + rs.getMetaData().getColumnName(i) + " = 'SIN DATO' "
                        + " WHERE " + rs.getMetaData().getColumnName(i) + " is null;";
                //System.out.println("QUITAR VALORES NULOS:-------------------------------------------- \n\r"+sql);
                columnNames.add(rs.getMetaData().getColumnName(i));

                connectionJdbcMB.non_query(sql);
            }
        } catch (Exception e) {
            System.out.println("ERROR POR EXCEPTION:---" + e.toString());
        }
        //------------------------------------------------------------------
        //CREO LA TABLA PREPIVOT A PARTIR DE LA TABLA PREPIVOT_2(agrupando los valores)
        //------------------------------------------------------------------        
        String columns = "";//nombres de las columnas juntos 
        for (int i = 0; i < columnNames.size(); i++) {
            if (i == 0) {
                columns = columnNames.get(i);
            } else {
                columns = columns + ", " + columnNames.get(i);
            }
        }
        sql = "\n\r DROP TABLE IF EXISTS " + prepivotTableName + ";\n\r"
                + " CREATE TABLE  \n\r"
                + "	" + prepivotTableName + "  \n\r"
                + " AS  \n\r"
                + "select  \n\r"
                + "	" + columns + ",count(*)  \n\r"
                + "from  \n\r"
                + "	" + prepivotTableName_2 + "  \n\r"
                + "group by  \n\r"
                + "	" + columns + "  \n\r"
                + "order by  \n\r"
                + "	" + columns + "; \n\r";
        connectionJdbcMB.non_query(sql);//CREO LA TABLA PREPIVOT FINAL                
        sql = "\n\r DROP TABLE IF EXISTS " + prepivotTableName_2 + ";\n\r";
        connectionJdbcMB.non_query(sql);//ELIMINO TABLA PREPIVOT INICIAL
    }

    private void addColumToRow(Row row1, String get, String styleClass, String styleCSS, int colSpan, int rowSpan) {
        Column column = new Column();
        HtmlOutputText text = new HtmlOutputText();
        column.setStyleClass(styleClass);
        text.setValue(get);
        column.getChildren().add(text);
        column.setStyle(styleCSS);
        column.setRowspan(rowSpan);
        column.setColspan(colSpan);
        row1.getChildren().add(column);
    }

    private void loadIndicator(int n) {
        currentIndicator = indicatorsFacade.find(n);
        reset();
    }

    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    //FUNCIOES PARA REALIZAR LA CARGA DE UN INDICADOR
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    public void loadIndicator2() {
        loadIndicator(2);
    }

    public void loadIndicator4() {
        loadIndicator(4);
    }

    public void loadIndicator11() {
        loadIndicator(11);
    }

    public void loadIndicator18() {
        loadIndicator(18);
    }

    public void loadIndicator25() {
        loadIndicator(25);
    }

    public void loadIndicator32() {
        loadIndicator(32);
    }

    public void loadIndicator39() {
        loadIndicator(39);
    }

    public void loadIndicator46() {
        loadIndicator(46);
    }

    public void loadIndicator53() {
        loadIndicator(53);
    }

    public void loadIndicator60() {
        loadIndicator(60);
    }
    public void loadIndicator67() {
        loadIndicator(67);
    }

    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    //FUNCIONES GET AND SET
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    public String getSubTitleIndicator() {
        return subTitleIndicator;
    }

    public void setSubTitleIndicator(String subTitleIndicator) {
        this.subTitleIndicator = subTitleIndicator;
    }

    public String getTitleIndicator() {
        return titleIndicator;
    }

    public void setTitleIndicator(String titleIndicator) {
        this.titleIndicator = titleIndicator;
    }

    public String getTitlePage() {
        return titlePage;
    }

    public void setTitlePage(String titlePage) {
        this.titlePage = titlePage;
    }

    public StreamedContent getChartImage() {
        return chartImage;
    }

    public void setChartImage(StreamedContent chartImage) {
        this.chartImage = chartImage;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getInitialDate() {
        return initialDate;
    }

    public void setInitialDate(Date initialDate) {
        this.initialDate = initialDate;
    }

    public List<String> getVariablesCrossList() {
        return variablesCrossList;
    }

    public void setVariablesCrossList(List<String> variablesCrossList) {
        this.variablesCrossList = variablesCrossList;
    }

    public List<String> getVariablesList() {
        return variablesList;
    }

    public void setVariablesList(List<String> variablesList) {
        this.variablesList = variablesList;
    }

    public List<String> getCurrentVariablesCrossSelected() {
        return currentVariablesCrossSelected;
    }

    public void setCurrentVariablesCrossSelected(List<String> currentVariablesCrossSelected) {
        this.currentVariablesCrossSelected = currentVariablesCrossSelected;
    }

    public List<String> getCurrentVariablesSelected() {
        return currentVariablesSelected;
    }

    public void setCurrentVariablesSelected(List<String> currentVariablesSelected) {
        this.currentVariablesSelected = currentVariablesSelected;
    }

    public boolean isBtnAddVariableDisabled() {
        return btnAddVariableDisabled;
    }

    public void setBtnAddVariableDisabled(boolean btnAddVariableDisabled) {
        this.btnAddVariableDisabled = btnAddVariableDisabled;
    }

    public boolean isBtnRemoveVariableDisabled() {
        return btnRemoveVariableDisabled;
    }

    public void setBtnRemoveVariableDisabled(boolean btnRemoveVariableDisabled) {
        this.btnRemoveVariableDisabled = btnRemoveVariableDisabled;
    }

    public ArrayList<String> getValuesCategoryList() {
        return valuesCategoryList;
    }

    public void setValuesCategoryList(ArrayList<String> valuesCategoryList) {
        this.valuesCategoryList = valuesCategoryList;
    }

    public String getCurrentGraphType() {
        return currentGraphType;
    }

    public void setCurrentGraphType(String currentGraphType) {
        this.currentGraphType = currentGraphType;
    }

    public List<String> getCurrentCategoricalValuesList() {
        return currentCategoricalValuesList;
    }

    public void setCurrentCategoricalValuesList(List<String> currentCategoricalValuesList) {
        this.currentCategoricalValuesList = currentCategoricalValuesList;
    }

    public List<String> getCurrentCategoricalValuesSelected() {
        return currentCategoricalValuesSelected;
    }

    public void setCurrentCategoricalValuesSelected(List<String> currentCategoricalValuesSelected) {
        this.currentCategoricalValuesSelected = currentCategoricalValuesSelected;
    }

    public String getFirstVariablesCrossSelected() {
        return firstVariablesCrossSelected;
    }

    public void setFirstVariablesCrossSelected(String firstVariablesCrossSelected) {
        this.firstVariablesCrossSelected = firstVariablesCrossSelected;
    }

    public boolean isBtnRemoveCategoricalValueDisabled() {
        return btnRemoveCategoricalValueDisabled;
    }

    public void setBtnRemoveCategoricalValueDisabled(boolean btnRemoveCategoricalValueDisabled) {
        this.btnRemoveCategoricalValueDisabled = btnRemoveCategoricalValueDisabled;
    }

    public boolean isBtnAddCategoricalValueDisabled() {
        return btnAddCategoricalValueDisabled;
    }

    public void setBtnAddCategoricalValueDisabled(boolean btnAddCategoricalValueDisabled) {
        this.btnAddCategoricalValueDisabled = btnAddCategoricalValueDisabled;
    }

    public String getEndValue() {
        return endValue;
    }

    public void setEndValue(String endValue) {
        this.endValue = endValue;
    }

    public String getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(String initialValue) {
        this.initialValue = initialValue;
    }

//    public boolean isShowAll() {
//        return showAll;
//    }
//
//    public void setShowAll(boolean showAll) {
//        this.showAll = showAll;
//    }

//    public OutputPanel getDynamicDataTableGroup() {
//        return dynamicDataTableGroup;
//    }
//
//    public void setDynamicDataTableGroup(OutputPanel dynamicDataTableGroup) {
//        this.dynamicDataTableGroup = dynamicDataTableGroup;
//    }

//    public boolean isRenderedDynamicDataTable() {
//        return renderedDynamicDataTable;
//    }
//
//    public void setRenderedDynamicDataTable(boolean renderedDynamicDataTable) {
//        this.renderedDynamicDataTable = renderedDynamicDataTable;
//    }

    public String getCurrentValueGraph() {
        return currentValueGraph;
    }

    public void setCurrentValueGraph(String currentValueGraph) {
        this.currentValueGraph = currentValueGraph;
    }

    public String getCurrentVariableGraph() {
        return currentVariableGraph;
    }

    public void setCurrentVariableGraph(String currentVariableGraph) {
        this.currentVariableGraph = currentVariableGraph;
    }

    public List<String> getValuesGraph() {
        return valuesGraph;
    }

    public void setValuesGraph(List<String> valuesGraph) {
        this.valuesGraph = valuesGraph;
    }

    public List<String> getVariablesGraph() {
        return variablesGraph;
    }

    public void setVariablesGraph(List<String> variablesGraph) {
        this.variablesGraph = variablesGraph;
    }

    public boolean isShowCount() {
        return showCount;
    }

    public void setShowCount(boolean showCount) {
        this.showCount = showCount;
    }

    public boolean isShowRowPercentage() {
        return showRowPercentage;
    }

    public void setShowRowPercentage(boolean showRowPercentage) {
        this.showRowPercentage = showRowPercentage;
    }

    public boolean isShowColumnPercentage() {
        return showColumnPercentage;
    }

    public void setShowColumnPercentage(boolean showColumnPercentage) {
        this.showColumnPercentage = showColumnPercentage;
    }

    public boolean isShowTotalPercentage() {
        return showTotalPercentage;
    }

    public void setShowTotalPercentage(boolean showTotalPercentage) {
        this.showTotalPercentage = showTotalPercentage;
    }

    public String getDataTableHtml() {
        return dataTableHtml;
    }

    public void setDataTableHtml(String dataTableHtml) {
        this.dataTableHtml = dataTableHtml;
    }
}
