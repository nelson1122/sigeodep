/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.indicators;

import beans.connection.ConnectionJdbcMB;
import beans.enumerators.VariablesEnum;
import beans.util.Variable;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
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
import model.dao.IndicatorsConfigurationsFacade;
import model.dao.IndicatorsFacade;
import model.pojo.Indicators;
import model.pojo.IndicatorsConfigurations;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
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
@ManagedBean(name = "indicatorsRateMB")
@SessionScoped
public class IndicatorsRateMB {

    @EJB
    IndicatorsFacade indicatorsFacade;
    @EJB
    IndicatorsConfigurationsFacade indicatorsConfigurationsFacade;
    private String currentConfigurationSelected = "";    
    private List<String> configurationsList = new ArrayList<String>();
    private String newConfigurationName = "";
    private Indicators currentIndicator;
    private StreamedContent chartImage;
    private SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
    private OutputPanel dynamicDataTableGroup; // Placeholder.
    private FacesMessage message = null;
    private ConnectionJdbcMB connectionJdbcMB;
    private String titlePage = "SIGEODEP -  INDICADORES GENERALES PARA LESIONES FATALES";
    private String titleIndicator = "SIGEODEP -  INDICADORES GENERALES PARA LESIONES FATALES";
    private String subTitleIndicator = "NUMERO DE CASOS POR LESION";
    //private String currentGraphType;
    private String currentVariableGraph;
    private String currentValueGraph;
    private String firstVariablesCrossSelected = null;
    private String initialValue = "";
    private String endValue = "";
    private String dataTableHtml;
    private String[] headers2;//CABECERA 2 CUANDO EL CRUCE SE REALIZA SOBRE 3 VARIABLES
    private String[][] matrixResult;//MATRIZ DE RESULTADOS
    private Date initialDate = new Date();
    private Date endDate = new Date();
    DecimalFormat formateador = new DecimalFormat("0.00");
    private String initialDateStr;
    private String endDateStr;
    private String pivotTableName;
    private String prepivotTableName;
    private String currentSpatialDisaggregation;
    private String currentTemporalDisaggregation;
    private String currentMultipler;
    private List<String> multiplers = new ArrayList<String>();
    private List<String> temporalDisaggregationTypes = new ArrayList<String>();
    private List<String> spatialDisaggregationTypes = new ArrayList<String>();
    private List<String> variablesGraph = new ArrayList<String>();
    private List<String> valuesGraph = new ArrayList<String>();
    private List<String> listOfAvailableVariablesNames = new ArrayList<String>();//lista de nombres de variables disponibles que sepueden cruzar(se visualizan en pagina)
    private List<String> listOfCrossVariablesNames = new ArrayList<String>();//ista de nombres de variables que se van a cruzar(se visualizan en pagina)
    private List<String> currentAvailableVariablesNamesSelected = new ArrayList<String>();//lista de nombres seleccionados en la lista de variables disponibles
    private List<String> currentVariablesCrossSelected = new ArrayList<String>();//lista de nombres seleccionados en la lista de variables a cruzar    
    private List<String> currentCategoricalValuesList = new ArrayList<String>();
    private List<String> currentCategoricalValuesSelected;
    private ArrayList<SpanColumns> headers1;//CABECERA 1 CUANDO EL CRUCE SE REALIZA SOBRE 3 VARIABLES
    private ArrayList<Variable> completeListOfVariableData;//lista de variables que tiene el indicador
    private ArrayList<Variable> variablesCrossData = new ArrayList<Variable>();//lista de variables a cruzar
    private ArrayList<String> valuesCategoryList;//lista de valores para una categoria
    private ArrayList<String> columNames;//NOMBRES DE LAS COLUMNAS, (SI EL CRUCE ES DE TRES VARIABLES ESTA SEPARADO POR EL CARACTER: }  )
    private ArrayList<String> rowNames;//NOMBRES DE LAS FILAS    
    //private ArrayList<String> totalsHorizontal = new ArrayList<String>();
    //private ArrayList<String> totalsVertical = new ArrayList<String>();
    private Variable currentVariableConfiguring;
    private int numberCross = 2;//maximo numero de variables a cruzar
    //private int grandTotal = 0;//total general de la matriz
    private int multiplierK = 0;
    private int currentYear = 0;
    //private boolean renderGraphOptions = false;//mostrar combos para grafico
    private boolean btnAddVariableDisabled = true;
    private boolean btnAddCategoricalValueDisabled = true;
    private boolean btnRemoveCategoricalValueDisabled = true;
    private boolean btnRemoveVariableDisabled = true;
    private boolean renderedDynamicDataTable = true;
    //private boolean showAll = false;//mostrar filas y columnas vacias
    private boolean showCalculation = false;//mostrar la division
    private boolean colorType = true;
    private boolean showItems = true;

    public IndicatorsRateMB() {
        //-------------------------------------------------
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
                for (int j = 0; j < variablesCrossData.get(i).getValuesConfigured().size(); j++) {
                    valuesGraph.add(variablesCrossData.get(i).getValuesConfigured().get(j));
                    currentValueGraph = valuesGraph.get(0);
                }
                break;
            }
        }
        createImage();
    }

    public void process() {
        variablesCrossData = new ArrayList<Variable>();//lista de variables a cruzar            
        boolean continueProcess=true;
        message = null;
        variablesGraph = new ArrayList<String>();
        valuesGraph = new ArrayList<String>();
        currentValueGraph = "";
        currentVariableGraph = "";

        //----------------------------------------------------------------------
        //VALIDO LAS FECHAS
        //----------------------------------------------------------------------
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(initialDate);
        c2.setTime(endDate);
        if (c1.compareTo(c2) > 0) {
            continueProcess = false;
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La fecha inicial debe ser inferior o igual a la fecha final");
        }

        //----------------------------------------------------------------------
        //NUMERO DE VARIABLES A CRUZAR SEA MENOR O IGUAL AL LIMITE ESTABLECIDO
        //----------------------------------------------------------------------
        if (continueProcess) {
            if (listOfCrossVariablesNames.size() <= 2) {
                continueProcess = true;
            } else {
                continueProcess = false;
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "En la lista de variables a cruzar deben haber " + numberCross + " o menos variables");
            }
        }
        //----------------------------------------------------------------------
        //AGREGO LAS VARIABLES INDICADAS POR EL USUARIO
        //----------------------------------------------------------------------        
        if (continueProcess) {
            //agrego la variable de desagregacion temporal            
            variablesCrossData.add(createTemporalDisaggregationVariable(initialDate, endDate));//variable de desagregacion temporal
            for (int j = 0; j < listOfCrossVariablesNames.size(); j++) {
                for (int i = 0; i < completeListOfVariableData.size(); i++) {
                    if (completeListOfVariableData.get(i).getName().compareTo(listOfCrossVariablesNames.get(j)) == 0) {
                        variablesCrossData.add(completeListOfVariableData.get(i));
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
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La variable " + completeListOfVariableData.get(i).getName() + " no tiene valores configurados, para continuar debe ser configurada.");
                    continueProcess = false;
                }
            }
        }
        //----------------------------------------------------------------------
        //CARGO LOS COMBOS PARA EL GRAFICO
        //----------------------------------------------------------------------
        if (continueProcess) {
            if (variablesCrossData.size() == 3) {
                for (int i = 1; i < variablesCrossData.size(); i++) {//i es 1 por que en la posicion 0 esta la desagregacion y esta no entra en el combo                    
                    if (i == 1) {//si es el primero cargo valores
                        variablesGraph.add(variablesCrossData.get(i).getName());
                        currentVariableGraph = variablesGraph.get(0);
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
            dataTableHtml = createDataTableResult();
            createImage();//creo el grafico
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Cruze de conteo realizado");
        }
    }

    public void changeCategoticalList() {
        if (!currentCategoricalValuesSelected.isEmpty()) {
            btnRemoveCategoricalValueDisabled = false;
        }
    }

       public int btnRemoveConfigurationClick() {
        //System.out.println("currentConfigurationSelected es " + currentConfigurationSelected);
        if (currentConfigurationSelected == null || currentConfigurationSelected.trim().length() == 0) {//VALOR INICIAL INGRESADO
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar una configuración de la lista"));
            return 0;
        }
        List<IndicatorsConfigurations> indicatorsConfigurationsList = indicatorsConfigurationsFacade.findAll();
        for (int i = 0; i < indicatorsConfigurationsList.size(); i++) {
            if (indicatorsConfigurationsList.get(i).getConfigurationName().compareTo(currentConfigurationSelected) == 0) {
                indicatorsConfigurationsFacade.remove(indicatorsConfigurationsList.get(i));
                btnLoadConfigurationClick();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "La configuración ha sido eliminada"));
                return 0;
            }
        }
        return 0;
    }

    public int btnOpenConfigurationClick() {
        //realizar la carga de la configuracion indicada
        if (currentConfigurationSelected == null || currentConfigurationSelected.trim().length() == 0) {//VALOR INICIAL INGRESADO
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar una configuración de la lista"));
            return 0;
        }
        currentCategoricalValuesList = new ArrayList<String>();
        IndicatorsConfigurations indicatorsConfigurationsSelected = indicatorsConfigurationsFacade.findByName(currentConfigurationSelected);

        if (firstVariablesCrossSelected.compareTo(indicatorsConfigurationsSelected.getVariableName()) != 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La configuracion corresponde a la variable ("
                    + indicatorsConfigurationsSelected.getVariableName() + ") se debe abrir una configuracion para"
                    + " una variable de tipo (" + firstVariablesCrossSelected + ")"));
            return 0;
        }

        String[] splitConfiguration = indicatorsConfigurationsSelected.getConfiguredValues().split("\t");
        currentCategoricalValuesList.addAll(Arrays.asList(splitConfiguration));

        for (int i = 0; i < variablesCrossData.size(); i++) {
            if (variablesCrossData.get(i).getName().compareTo(firstVariablesCrossSelected) == 0) {
                variablesCrossData.get(i).setValuesConfigured(Arrays.asList(splitConfiguration));
                variablesCrossData.get(i).setValuesId(Arrays.asList(splitConfiguration));
                variablesCrossData.get(i).setValues(Arrays.asList(splitConfiguration));
                break;
            }
            
        }
        for (int i = 0; i < completeListOfVariableData.size(); i++) {
            if (completeListOfVariableData.get(i).getName().compareTo(firstVariablesCrossSelected) == 0) {
                completeListOfVariableData.get(i).setValuesConfigured(Arrays.asList(splitConfiguration));
                completeListOfVariableData.get(i).setValuesId(Arrays.asList(splitConfiguration));
                completeListOfVariableData.get(i).setValues(Arrays.asList(splitConfiguration));
                break;
            }
            
        }
        //completeListOfVariableData
        
        
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Configuración cargada"));
        return 0;
    }

    public void btnLoadConfigurationClick() {
        //recargar las configuraciones existentes
        //System.out.println("inicia carga de configuraciones");
        currentConfigurationSelected = null;
        configurationsList = new ArrayList<String>();
        List<IndicatorsConfigurations> indicatorsConfigurationsList = indicatorsConfigurationsFacade.findAll();
        for (int i = 0; i < indicatorsConfigurationsList.size(); i++) {
            if (currentVariablesCrossSelected.get(0).compareTo(indicatorsConfigurationsList.get(i).getVariableName()) == 0) {
                configurationsList.add(indicatorsConfigurationsList.get(i).getConfigurationName());
            }
            //System.out.println("inicia carga de configuraciones");
        }
    }

    public int btnSaveConfigurationClick() {
        if (newConfigurationName.trim().length() == 0) {//VALOR INICIAL INGRESADO
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Digite el nombre para la nueva configuración"));
            return 0;
        }
        //determino si el nombre ya esta ingresado
        boolean founConfiguration = false;
        List<IndicatorsConfigurations> indicatorsConfigurationsList = indicatorsConfigurationsFacade.findAll();
        for (int i = 0; i < indicatorsConfigurationsList.size(); i++) {
            if (indicatorsConfigurationsList.get(i).getConfigurationName().compareTo(newConfigurationName) == 0) {
                founConfiguration = true;
                break;
            }
        }
        if (!founConfiguration) {
            if (!currentCategoricalValuesList.isEmpty()) {
                IndicatorsConfigurations newIndicatorsConfigurations = new IndicatorsConfigurations(indicatorsConfigurationsFacade.findMax() + 1);
                //System.out.println("El valor de id_configuration es : " + newIndicatorsConfigurations.getConfigurationId());
                newIndicatorsConfigurations.setConfigurationName(newConfigurationName);
                newIndicatorsConfigurations.setVariableName(firstVariablesCrossSelected);
                String configuredValues = "";
                for (int i = 0; i < currentCategoricalValuesList.size(); i++) {
                    configuredValues = configuredValues + currentCategoricalValuesList.get(i);
                    if (i != currentCategoricalValuesList.size() - 1) {
                        configuredValues = configuredValues + "\t";
                    }
                }
                newIndicatorsConfigurations.setConfiguredValues(configuredValues);
                indicatorsConfigurationsFacade.create(newIndicatorsConfigurations);
                btnLoadConfigurationClick();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "La cofiguración ha sido almacenada"));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No existen categorias para almacenar"));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El nombre registrado ya fue ingresado, por favor digite uno diferente"));
        }

        return 0;
    }

    public int btnAddCategoricalValueClick() {
        int i;
        int e;
        if (initialValue.trim().length() == 0) {//VALOR INICIAL INGRESADO
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Digite un valor inicial"));
            return 0;
        }
        if (endValue.trim().length() == 0) {//VALOR FINAL INGRESADO
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Digite un valor final"));
            return 0;
        }
        if (endValue.compareToIgnoreCase("n") == 0) {
            endValue = "n";
        }

        if (endValue.compareTo("n") == 0) {
            try {//VALOR INICIAL NUMERICOS
                i = Integer.parseInt(initialValue);
                if (i < 0) {//VALOR INICIAL Y FINAL MAYOR O IGUAL A CERO
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Los valores deben ser iguales o mayores que cero"));
                    return 0;
                }
            } catch (Exception ex) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Los valores deben ser numéricos"));
                return 0;
            }
        } else {
            try {//VALOR INICIAL Y FINAL NUMERICOS
                i = Integer.parseInt(initialValue);
                e = Integer.parseInt(endValue);

                if (i < 0 && e < 0) {//VALOR INICIAL Y FINAL MAYOR O IGUAL A CERO
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Los valores deben ser iguales o mayores que cero"));
                    return 0;
                }
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
        }

        //EL RANGO NO ESTE DENTRO DE OTRO
        if (endValue.compareTo("n") != 0) {
            if (currentVariableConfiguring != null) {
                for (int j = 0; j < currentVariableConfiguring.getValuesConfigured().size(); j++) {
                    String[] splitValues = currentVariableConfiguring.getValuesConfigured().get(j).split("/");
                    int initialValueFoundInteger = Integer.parseInt(splitValues[0]);
                    int endValueFoundInteger = Integer.parseInt(splitValues[1]);
                    int initialValueAddInteger = Integer.parseInt(initialValue);
                    int endValueAddInteger = Integer.parseInt(endValue);
                    for (int k = initialValueFoundInteger; k < endValueFoundInteger; k++) {
                        for (int l = initialValueAddInteger; l < endValueAddInteger; l++) {
                            if (k == l) {
                                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Dentro del rango ingresado el valor (" + String.valueOf(k) + ") esta contenido en la lista de valores"));
                                return 0;
                            }
                        }
                    }
                }
            }
        }
        //ingreso el nuevo valor a la categoria
        if (currentVariableConfiguring != null) {

            if (initialValue.length() == 1) {
                initialValue = "0" + initialValue;
            }
            if (endValue.length() == 1 && endValue.compareTo("n") != 0) {
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
        //btnRemoveCategoricalValueDisabled = false;
        if (currentVariableConfiguring != null) {
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
        //btnRemoveCategoricalValueDisabled = false;
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
        if (currentAvailableVariablesNamesSelected != null) {
            if (!currentAvailableVariablesNamesSelected.isEmpty()) {
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
            for (int i = 0; i < completeListOfVariableData.size(); i++) {
                if (completeListOfVariableData.get(i).getName().compareTo(firstVariablesCrossSelected) == 0) {
                    if (completeListOfVariableData.get(i).getGeneric_table().compareTo("year") == 0) {
                        completeListOfVariableData.get(i).filterYear(initialDate, endDate);
                    }
                    currentCategoricalValuesList = completeListOfVariableData.get(i).getValuesConfigured();
                    currentVariableConfiguring = completeListOfVariableData.get(i);
                    btnAddCategoricalValueDisabled = !currentVariableConfiguring.isConfigurable();
                    break;
                }
            }
        }
    }

    public void addVariableClick() {
        String error = "";
        boolean nextStep = true;
        if (currentAvailableVariablesNamesSelected == null) {
            error = "Debe seleccionarse una variable";
            nextStep = false;
        }
        if (nextStep) {
            for (int i = 0; i < listOfAvailableVariablesNames.size(); i++) {
                for (int j = 0; j < currentAvailableVariablesNamesSelected.size(); j++) {
                    //System.out.println("comparar: " + listOfAvailableVariablesNames.get(i)+ " CON "+currentVariablesSelected.get(j));
                    if (listOfAvailableVariablesNames.get(i).compareTo(currentAvailableVariablesNamesSelected.get(j)) == 0) {//esta es la variable encontrada que saldra de la lista                    
                        //System.out.println("quitar: " + listOfAvailableVariablesNames.get(i));
                        listOfAvailableVariablesNames.remove(i);//la quito de este listado                    
                        listOfCrossVariablesNames.add(currentAvailableVariablesNamesSelected.get(j));//la agrego al otro                        
                        btnAddVariableDisabled = true;
                        i = -1;
                        break;
                    }
                }
            }
            currentAvailableVariablesNamesSelected = null;
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
            for (int i = 0; i < listOfCrossVariablesNames.size(); i++) {
                for (int j = 0; j < currentVariablesCrossSelected.size(); j++) {
                    if (listOfCrossVariablesNames.get(i).compareTo(currentVariablesCrossSelected.get(j)) == 0) {//esta es la variable encontrada que saldra de la lista                    
                        if (listOfCrossVariablesNames.get(i).compareTo("comuna") != 0
                                && listOfCrossVariablesNames.get(i).compareTo("zona") != 0
                                && listOfCrossVariablesNames.get(i).compareTo("cuadrante") != 0
                                && listOfCrossVariablesNames.get(i).compareTo("corredor") != 0
                                && listOfCrossVariablesNames.get(i).compareTo("barrio") != 0) {
                            listOfCrossVariablesNames.remove(i);//la quito de este listado                    
                            listOfAvailableVariablesNames.add(currentVariablesCrossSelected.get(j));//la agrego al otro                        

                            i = -1;
                        }
                        break;
                    }
                }
            }
            btnRemoveVariableDisabled = true;
            currentVariablesCrossSelected = null;
        }
        if (error.length() != 0) {//hay  errores al relacionar la variables 
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Alerta", error));
        }
    }

    public void createImage() {
        if (!variablesCrossData.isEmpty()) {
            try {
                JFreeChart chart = createLineChart();
                File chartFile = new File("dynamichart");
                ChartUtilities.saveChartAsPNG(chartFile, chart, 700, 500);
                chartImage = new DefaultStreamedContent(new FileInputStream(chartFile), "image/png");
            } catch (Exception e) {
                System.out.println("Error 1 en " + this.getClass().getName() + ":" + e.toString());
            }
        }
    }

    public void reset() {
        dataTableHtml = "";
        chartImage = null;
        currentVariableConfiguring = null;
        listOfCrossVariablesNames = new ArrayList<String>();
        currentAvailableVariablesNamesSelected = new ArrayList<String>();
        currentVariablesCrossSelected = new ArrayList<String>();
        firstVariablesCrossSelected = null;
        currentCategoricalValuesList = new ArrayList<String>();
        currentCategoricalValuesSelected = new ArrayList<String>();
        titlePage = currentIndicator.getIndicatorGroup();
        titleIndicator = currentIndicator.getIndicatorGroup();
        subTitleIndicator = currentIndicator.getIndicatorName();
        listOfCrossVariablesNames = new ArrayList<String>();//SelectItem[listOfAvailableVariablesNames.size()];
        listOfAvailableVariablesNames = new ArrayList<String>();//SelectItem[completeListOfVariableData.size()];        
        completeListOfVariableData = getVariablesIndicator();
        for (int i = 0; i < completeListOfVariableData.size(); i++) {
            listOfAvailableVariablesNames.add(completeListOfVariableData.get(i).getName());
        }
        completeListOfVariableData.add(createVariable("zona", "areas", false));//agrego de ultima la desagregacion espacial
        listOfCrossVariablesNames.add("zona");//agrego de primera en la lista de nombres a cruzar

        variablesGraph = new ArrayList<String>();
        valuesGraph = new ArrayList<String>();
        currentValueGraph = "";
        currentVariableGraph = "";
        numberCross = currentIndicator.getNumberCross();

        dynamicDataTableGroup = new OutputPanel();//creo el panel grid        
        btnAddVariableDisabled = true;
        btnRemoveVariableDisabled = true;
        currentAvailableVariablesNamesSelected = null;
        currentVariablesCrossSelected = null;

        spatialDisaggregationTypes = new ArrayList<String>();
        spatialDisaggregationTypes.add("Zona");
        spatialDisaggregationTypes.add("Cuadrante");
        spatialDisaggregationTypes.add("Comuna");
        spatialDisaggregationTypes.add("Corredor");
        spatialDisaggregationTypes.add("Barrio");
        currentSpatialDisaggregation = "Zona";

        temporalDisaggregationTypes = new ArrayList<String>();
        temporalDisaggregationTypes.add("Anual");
        temporalDisaggregationTypes.add("Mensual");
        temporalDisaggregationTypes.add("Diaria");
        currentTemporalDisaggregation = "Anual";

        multiplers = new ArrayList<String>();
        multiplers.add("1.000");
        multiplers.add("10.000");
        multiplers.add("100.000");
        multiplers.add("1'000.000");
        currentMultipler = "100.000";


    }

    public void changeSpatialDisaggregation() {

        btnAddVariableDisabled = true;
        currentAvailableVariablesNamesSelected = null;
        //elimino la primera de variables a cruzar y la ultima de listaVariblesData
        listOfCrossVariablesNames.remove(0);
        completeListOfVariableData.remove(completeListOfVariableData.size() - 1);
        //copeo las demas variables
        List<String> listOfCrossVariablesNamesAux = new ArrayList<String>();
        for (int i = 0; i < listOfCrossVariablesNames.size(); i++) {
            listOfCrossVariablesNamesAux.add(listOfCrossVariablesNames.get(i));
            listOfCrossVariablesNames.remove(0);
            i--;
        }

        //agrego la nueva variable
        if (currentSpatialDisaggregation.compareTo("Barrio") == 0) {
            completeListOfVariableData.add(createVariable("barrio", "neighborhoods", false));//agrego de ultima la desagregacion espacial
            listOfCrossVariablesNames.add("barrio");//agrego de primera en la lista de nombres a cruzar                        
        }
        if (currentSpatialDisaggregation.compareTo("Comuna") == 0) {
            completeListOfVariableData.add(createVariable("comuna", "communes", false));//agrego de ultima la desagregacion espacial
            listOfCrossVariablesNames.add("comuna");//agrego de primera en la lista de nombres a cruzar                        
        }
        if (currentSpatialDisaggregation.compareTo("Corredor") == 0) {
            completeListOfVariableData.add(createVariable("corredor", "corridors", false));//agrego de ultima la desagregacion espacial
            listOfCrossVariablesNames.add("corredor");//agrego de primera en la lista de nombres a cruzar                      
        }
        if (currentSpatialDisaggregation.compareTo("Cuadrante") == 0) {
            completeListOfVariableData.add(createVariable("cuadrante", "quadrants", false));//agrego de ultima la desagregacion espacial
            listOfCrossVariablesNames.add("cuadrante");//agrego de primera en la lista de nombres a cruzar                        
        }
        if (currentSpatialDisaggregation.compareTo("Zona") == 0) {
            completeListOfVariableData.add(createVariable("zona", "areas", false));//agrego de ultima la desagregacion espacial
            listOfCrossVariablesNames.add("zona");//agrego de primera en la lista de nombres a cruzar                                    
        }
        //agrego las variables que copie anteriormente(todas menos la primera que este en el listado de variables acruzar)
        for (int i = 0; i < listOfCrossVariablesNamesAux.size(); i++) {
            listOfCrossVariablesNames.add(listOfCrossVariablesNamesAux.get(i));
        }
        
    }

    private int getDateDifference(Date date1, Date date2, int typeDifference) {
        //obtiene la diferencia entre dos fechas //dias valor=1 //meses valor=2 //años valor=3
        int retorno = 0;
        try {
            Calendar cal1;
            cal1 = Calendar.getInstance();
            Calendar cal2;
            cal2 = Calendar.getInstance();
            // different date might have different offset 
            cal1.setTime(date1);
            long ldate1 = date1.getTime() + cal1.get(Calendar.ZONE_OFFSET) + cal1.get(Calendar.DST_OFFSET);
            cal2.setTime(date2);
            long ldate2 = date2.getTime() + cal2.get(Calendar.ZONE_OFFSET) + cal2.get(Calendar.DST_OFFSET);

            // Use integer calculation, truncate the decimals 
            int hr1 = (int) (ldate1 / 3600000); //60*60*1000 
            int hr2 = (int) (ldate2 / 3600000);

            int days1 = (int) hr1 / 24;
            int days2 = (int) hr2 / 24;

            int dateDiff = days2 - days1;
            int yearDiff = cal2.get(Calendar.YEAR) - cal1.get(Calendar.YEAR);
            int monthDiff = yearDiff * 12 + cal2.get(Calendar.MONTH) - cal1.get(Calendar.MONTH);

            if (typeDifference == 1) {//diferencia en dias
                if (dateDiff < 0) {
                    dateDiff = dateDiff * (-1);
                }
                retorno = dateDiff;
            } else if (typeDifference == 2) {//diferencia en meses
                if (monthDiff < 0) {
                    monthDiff = monthDiff * (-1);
                }
                retorno = monthDiff;
            } else if (typeDifference == 3) {//diferencia en años
                if (yearDiff < 0) {
                    yearDiff = yearDiff * (-1);
                }
                retorno = yearDiff;
            }
        } catch (Exception pe) {
            System.out.println("Error 2 en " + this.getClass().getName() + ":" + pe.toString());
        }
        return retorno;
    }

    private Variable createTemporalDisaggregationVariable(Date initialDate, Date endDate) {
        Variable newVariable = new Variable("Desagregación temporal", "temporalDisaggregation", false);
        int diferenceRank;
        int daysMax;
        Calendar cal1 = Calendar.getInstance();
        ArrayList<String> valuesName = new ArrayList<String>();//NOMBRE DE LOS VALORES QUE PUEDE TOMAR LA VARIABLE POR DEFECTO(NOMBRE EN LA CATEGORIA)
        ArrayList<String> valuesId = new ArrayList<String>();  //IDENTIFICADORES DE LOS VALORES QUE PUEDE TOMAR LA VARIABLE POR DEFECTO(ID EN LA CATEGORIA)
        ArrayList<String> valuesConf = new ArrayList<String>();//NOMBRE DE LOS VALORES CONFIGURADOS POR EL USUARIO QUE PUEDE TOMAR LA VARIABLE        
        // different date might have different offset 
        SimpleDateFormat sdf;

        if (currentTemporalDisaggregation.compareTo("Diaria") == 0) {
            diferenceRank = getDateDifference(initialDate, endDate, 1);
            //System.out.println("(DIFERENCIA EN DIAS) :" + diferenceRank);
            sdf = new SimpleDateFormat("dd MMM yyyy");
            for (int i = 0; i < diferenceRank + 1; i++) {//+1 por que la difrencia no toma el ultimo dia
                cal1.setTime(initialDate);
                cal1.add(Calendar.DATE, i);
                initialDateStr = formato.format(cal1.getTime());
                valuesName.add(sdf.format(cal1.getTime()));//agrego el dia en formato: 14 Junio 2013
                valuesId.add(initialDateStr + "}" + initialDateStr);
                valuesConf.add(sdf.format(cal1.getTime()));
            }
        }
        if (currentTemporalDisaggregation.compareTo("Mensual") == 0) {
            diferenceRank = getDateDifference(initialDate, endDate, 2);
            //System.out.println("(DIFERENCIA EN MESES) :" + diferenceRank);
            sdf = new SimpleDateFormat("MMM yyyy");
            for (int i = 0; i < diferenceRank + 1; i++) {//+1 por que la difrencia no toma el ultimo mes
                cal1.setTime(initialDate);
                cal1.set(Calendar.DATE, 1);//coloco el dia en 1
                cal1.add(Calendar.MONTH, i);//fecha inicial se la aumenta i meses                
                initialDateStr = formato.format(cal1.getTime());
                daysMax = cal1.getActualMaximum(Calendar.DAY_OF_MONTH); // numero maximo de dias del mes
                cal1.set(Calendar.DATE, daysMax);//coloco el dia en el maximo para el mes                
                endDateStr = formato.format(cal1.getTime());
                valuesName.add(sdf.format(cal1.getTime()));//agrego el dia en formato: Junio 2013
                valuesId.add(initialDateStr + "}" + endDateStr);
                valuesConf.add(sdf.format(cal1.getTime()));
            }
        }
        if (currentTemporalDisaggregation.compareTo("Anual") == 0) {
            diferenceRank = getDateDifference(initialDate, endDate, 3);
            //System.out.println("(DIFERENCIA EN AÑOS) :" + diferenceRank);
            sdf = new SimpleDateFormat("yyyy");
            for (int i = 0; i < diferenceRank + 1; i++) {//+1 por que la difrencia no toma el ultimo dia
                cal1.setTime(initialDate);
                cal1.set(Calendar.DATE, 1);//coloco el dia en 1
                cal1.set(Calendar.MONTH, 0);//coloco el mes en enero(0)
                cal1.add(Calendar.YEAR, i);//fecha inicial se la aumenta i años                
                initialDateStr = formato.format(cal1.getTime());
                cal1.set(Calendar.DATE, 31);//coloco el dia en 31
                cal1.set(Calendar.MONTH, 11);//coloco el mes en diciembre(11)
                endDateStr = formato.format(cal1.getTime());
                valuesName.add(sdf.format(cal1.getTime()));//agrego el dia en formato: Junio 2013
                valuesId.add(initialDateStr + "}" + endDateStr);
                valuesConf.add(sdf.format(cal1.getTime()));
            }
        }
        newVariable.setValues(valuesName);
        newVariable.setValuesId(valuesId);
        newVariable.setValuesConfigured(valuesConf);
//        for (int i = 0; i < newVariable.getValues().size(); i++) {
//            System.out.println(newVariable.getValues().get(i)+"\t:\t"+newVariable.getValuesId().get(i));
//        }
        return newVariable;
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
            case quadrants://cuadrante
            case NOVALUE://es una tabla categorica
                try {
                    //ResultSet rs = connectionJdbcMB.consult("Select * from " + generic_table);
                    ResultSet rs = connectionJdbcMB.consult("Select * from " + generic_table + " order by 1");
                    while (rs.next()) {
                        valuesName.add(rs.getString(2));
                        valuesConf.add(rs.getString(2));
                        valuesId.add(rs.getString(1));
                    }
                } catch (Exception e) {
                    System.out.println("Error 3 en " + this.getClass().getName() + ":" + e.toString());
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

//    public List<String> getGraphTypes() {
//        ArrayList<String> listReturn = new ArrayList<String>();
//        try {
//            ResultSet rs = connectionJdbcMB.consult("Select graph_type from indicators where indicator_id=" + String.valueOf(currentIndicator.getIndicatorId()));
//            if (rs.next()) {
//                String[] splitGraphType = rs.getString(1).split(",");
//                listReturn.addAll(Arrays.asList(splitGraphType));
//            }
//        } catch (Exception e) {
//            System.out.println("Error 4 en " + this.getClass().getName() + ":" + e.toString());
//        }
//        return listReturn;
//    }
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

    private String determineHeader(String value) {
        if (value.indexOf("SIN DATO") == -1) {
            if (value.indexOf("/") != -1) {
                if (value.indexOf(":") != -1) {
                    String newValue = value.replace("/", " a ");
                    return newValue + " Horas";
                } else {
                    String newValue = value.replace("/", " a ");
                    return newValue + " Años";
                }

            }
        }
        return value;
    }

    private String createDataTableResult() {
        PanelGrid panelGrid = new PanelGrid();
        headers1 = new ArrayList<SpanColumns>();
        headers2 = new String[columNames.size()];

        String strReturn = " ";
        strReturn = strReturn + "<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\">\r\n";
        strReturn = strReturn + "            <tr>\r\n";
        strReturn = strReturn + "                <td id=\"firstTd\" >\r\n";
        //strReturn = strReturn + "                Cifras por: " + String.valueOf(multiplierK) + " habitantes\r\n";
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
                strReturn = strReturn + "                                    <div class=\"tableHeader\" style=\"width:150px;\">" + determineHeader(columNames.get(i)) + "</div>\r\n";
                strReturn = strReturn + "                                </td>\r\n";
            }
//            strReturn = strReturn + "                                <td>\r\n";
//            strReturn = strReturn + "                                    <div class=\"tableHeader\">Total</div>\r\n";
//            strReturn = strReturn + "                                </td>\r\n";
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
                strReturn = strReturn + "                                    <div >" + determineHeader(headers1.get(i).getLabel()) + "</div>\r\n";
                strReturn = strReturn + "                                </td>\r\n";
            }
//            strReturn = strReturn + "                                <td >\r\n";
//            strReturn = strReturn + "                                    <div >-</div>\r\n";
//            strReturn = strReturn + "                                </td>\r\n";
            strReturn = strReturn + "                            </tr>\r\n";

            strReturn = strReturn + "                            <tr>\r\n";
            //AGREGO LA CABECERA 2 A El PANEL_GRID
            for (int i = 0; i < headers2.length; i++) {
                strReturn = strReturn + "                                <td>\r\n";
                strReturn = strReturn + "                                    <div class=\"tableHeader\" style=\"width:150px;\">" + determineHeader(headers2[i]) + "</div>\r\n";
                strReturn = strReturn + "                                </td>\r\n";
            }
//            strReturn = strReturn + "                                <td >\r\n";
//            strReturn = strReturn + "                                    <div class=\"tableHeader\">Total</div>\r\n";
//            strReturn = strReturn + "                                </td>\r\n";
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

        strReturn = strReturn + "                    <div id=\"firstcol\" style=\"overflow: hidden;height:280px\">\r\n";//tamaño del div izquierdo
        strReturn = strReturn + "                        <table width=\"200px\" cellspacing=\"0\" cellpadding=\"0\" border=\"1\" >\r\n";

        //rowNames.add("Totales");
        for (int j = 0; j < rowNames.size(); j++) {
            //----------------------------------------------------------------------
            //NOMBRE PARA CADA FILA            
            strReturn = strReturn + "                            <tr>\r\n";
            strReturn = strReturn + "                                <td class=\"tableFirstCol\">" + determineHeader(rowNames.get(j)) + "</td>\r\n";
            strReturn = strReturn + "                            </tr>\r\n";
        }
        strReturn = strReturn + "                        </table>\r\n";
        strReturn = strReturn + "                    </div>\r\n";
        strReturn = strReturn + "                </td>\r\n";
        strReturn = strReturn + "                <td valign=\"top\">\r\n";


        //-------------------------------------------------------------------
        //TABLA QUE CONTIENE LOS DATOS DE LA MATRIZ
        //-------------------------------------------------------------------      
        //int sizeTableMatrix = columNames.size() * 100;//que cada columna tenga 100px
        //sizeTableMatrix = sizeTableMatrix + 100;//de los totales
        strReturn = strReturn + "                    <div id=\"table_div\" style=\"overflow: scroll;width:450px;height:300px;position:relative\" onscroll=\"fnScroll()\" >\r\n";//div que maneja la tabla
        //strReturn = strReturn + "                        <table width=\"" + sizeTableMatrix + "px\" cellspacing=\"0\" cellpadding=\"0\" border=\"1\" >\r\n";//
        strReturn = strReturn + "                        <table cellspacing=\"0\" cellpadding=\"0\" border=\"1\" >\r\n";//
        //----------------------------------------------------------------------
        //AGREGO LOS REGISTROS DE LA MATRIZ        
        //for (int j = 0; j < rowNames.size() - 1; j++) {//-1 por que le agrege "TOTALES"
        for (int j = 0; j < rowNames.size(); j++) {
            if (j == 0) {
                strReturn = strReturn + "                            <tr id='firstTr'>\r\n";
            } else {
                strReturn = strReturn + "                            <tr " + getColorType() + " >\r\n";
            }
            for (int i = 0; i < columNames.size(); i++) {
                String value;
                if (showCalculation) {
                    value = matrixResult[i][j];
                } else {
                    String[] splitColumn = matrixResult[i][j].split("<br/>");
                    value = splitColumn[0];
                }
                strReturn = strReturn + "                                <td> \r\n";//mantenga dimension
                strReturn = strReturn + "                                <div style=\"width:150px;\">" + value + "</div>\r\n";
                strReturn = strReturn + "                                </td> \r\n";
                //strReturn = strReturn + "                                <td>" + matrixResult[i][j] + "</td>\r\n";
            }
            //strReturn = strReturn + "                                <td>" + totalsVertical.get(j) + "</td>\r\n";
            strReturn = strReturn + "                            </tr>\r\n";
            changeColorType();//cambiar de color las filas de blanco a azul
        }
        //-------------------------------------------------------------------
        //FINALIZA
        //-------------------------------------------------------------------        
        strReturn = strReturn + "                        </table>\r\n";
        strReturn = strReturn + "                    </div>\r\n";
        strReturn = strReturn + "                </td>\r\n";
        strReturn = strReturn + "            </tr>\r\n";
        strReturn = strReturn + "        </table>\r\n";
        //System.out.println(strReturn);
        return strReturn;
    }

    public JFreeChart createLineChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String indicatorName = currentIndicator.getIndicatorName();
        String categoryAxixLabel = "";
        try {
            int pos = 0;
            double value;
            String sql = "";
            sql = sql + "SELECT * FROM " + pivotTableName;
            ResultSet rs;

            if (variablesCrossData.size() == 2) {
                rs = connectionJdbcMB.consult(sql + " ORDER BY id");
                while (rs.next()) {
                    value = Double.parseDouble(formateador.format(Double.parseDouble("0")).replace(",", "."));
                    if (rs.getString("count").compareTo("0") != 0 && rs.getString("poblacion").compareTo("SIN DATO") != 0) {
                        value = Double.parseDouble(formateador.format((Double.parseDouble(rs.getString("count")) / Double.parseDouble(rs.getString("poblacion"))) * multiplierK).replace(",", "."));
                    }
                    dataset.setValue(value, rs.getString(2), rs.getString(1));
                }
            }
            if (variablesCrossData.size() == 3) {
                //determino los nombres de las columnas
                rs = connectionJdbcMB.consult(sql + " ORDER BY id");
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
                rs = connectionJdbcMB.consult(sql + " ORDER BY id");

                while (rs.next()) {
                    value = Double.parseDouble(formateador.format(Double.parseDouble("0")).replace(",", "."));
                    if (rs.getString("count").compareTo("0") != 0 && rs.getString("poblacion").compareTo("SIN DATO") != 0) {
                        value = Double.parseDouble(formateador.format((Double.parseDouble(rs.getString("count")) / Double.parseDouble(rs.getString("poblacion"))) * multiplierK).replace(",", "."));
                    }
                    if (pos == 1) {
                        dataset.setValue(value, rs.getString(3), rs.getString(1));
                    }
                    if (pos == 2) {
                        dataset.setValue(value, rs.getString(2), rs.getString(1));
                    }
                }
                categoryAxixLabel = variablesCrossData.get(0).getName();
                indicatorName = currentIndicator.getIndicatorName() + "\n(" + currentVariableGraph + " es " + currentValueGraph + ")";
            }
        } catch (SQLException ex) {
            System.out.println("Error 5 en " + this.getClass().getName() + ":" + ex.toString());
        }
        indicatorName = indicatorName + "\n Cifras por " + currentMultipler + " habitantes";
        final JFreeChart chartReturn = ChartFactory.createLineChart(
                indicatorName, // chart title
                categoryAxixLabel, // domain axis label
                "Tasa", // range axis label
                dataset, // data
                PlotOrientation.VERTICAL, // orientation
                true, // include legend
                true, // tooltips
                false // urls
                );
        //JFreeChart chartReturn = ChartFactory.createLineChart(indicatorName, categoryAxixLabel, "Conteo", dataset, PlotOrientation.VERTICAL, true, true, false);
        //COLORES DE FONDO Y TITULO----------------------------
        chartReturn.setBackgroundPaint(new Color(200, 200, 200));
        chartReturn.getTitle().setPaint(new Color(50, 50, 50));
        chartReturn.getTitle().setFont(new Font("SanSerif", Font.BOLD, 15));
        //COLOCAR LABELS A LOS GRAFICOS----------------------------
        CategoryPlot plot = (CategoryPlot) chartReturn.getPlot();
        if (showItems) {
            CategoryItemRenderer renderer = plot.getRenderer();
            CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat("0.00"));//DecimalFormat("0.00"));
            renderer.setItemLabelGenerator(generator);
            renderer.setItemLabelsVisible(true);
        }
        //ROTAR LASETIQUETAS DEL EJE X-----------------------------
        CategoryAxis xAxis = (CategoryAxis) plot.getDomainAxis();
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        return chartReturn;
    }

    public void createMatrixResult() {
        //System.out.println("INICIA CREAR MATRIZ xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        try {
            ArrayList<String> columnNamesPivot = new ArrayList<String>();
            columNames = new ArrayList<String>();
            rowNames = new ArrayList<String>();
            //---------------------------------------------------------
            //DETEMINO LOS NOMBRES DE LAS COLUMNAS DE TABLA PIVOT()
            //---------------------------------------------------------
            ResultSet rs = connectionJdbcMB.consult("SELECT * FROM " + pivotTableName + " ORDER BY id");
            int ncol = rs.getMetaData().getColumnCount();
            for (int i = 1; i <= ncol; i++) {
                columnNamesPivot.add(rs.getMetaData().getColumnName(i));
            }
            //---------------------------------------------------------            
            //DETERMINO NOMBRES DE COLUMNAS PARA MATIRZ SALIDA
            //---------------------------------------------------------            
            if (variablesCrossData.size() == 2 || variablesCrossData.size() == 1) {
                rs = connectionJdbcMB.consult(
                        "SELECT " + columnNamesPivot.get(0)
                        + " FROM " + pivotTableName
                        + " GROUP BY " + columnNamesPivot.get(0)
                        + " ORDER BY MIN(id)");
            }
            if (variablesCrossData.size() == 3) {
                String sql =
                        "SELECT "
                        + columnNamesPivot.get(0) + "||'}'||" + columnNamesPivot.get(1)
                        + " FROM " + pivotTableName
                        + " GROUP BY "
                        + columnNamesPivot.get(0) + "||'}'||" + columnNamesPivot.get(1)
                        + " ORDER BY MIN(id)";
                rs = connectionJdbcMB.consult(sql);

//                rs = connectionJdbcMB.consult(
//                        "SELECT DISTINCT ("
//                        + columnNamesPivot.get(0) + "||'}'||" + columnNamesPivot.get(1)
//                        + ") " + columnNamesPivot.get(0) + " , " + columnNamesPivot.get(1)
//                        + " FROM " + pivotTableName
//                        + " ORDER BY " + columnNamesPivot.get(0) + " , " + columnNamesPivot.get(1));
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
                        + " order by MIN(id)");

//                rs = connectionJdbcMB.consult(
//                        "SELECT DISTINCT " + columnNamesPivot.get(1)
//                        + " FROM " + pivotTableName
//                        + " ORDER BY " + columnNamesPivot.get(1));

            }
            if (variablesCrossData.size() == 3) {
                rs = connectionJdbcMB.consult(
                        "SELECT " + columnNamesPivot.get(2)
                        + " FROM " + pivotTableName
                        + " GROUP BY " + columnNamesPivot.get(2)
                        + " order by MIN(id)");
//                rs = connectionJdbcMB.consult(
//                        "SELECT DISTINCT " + columnNamesPivot.get(2)
//                        + " FROM " + pivotTableName
//                        + " ORDER BY " + columnNamesPivot.get(2));
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
            //rs = connectionJdbcMB.consult("SELECT MAX(poblacion) FROM " + pivotTableName + " WHERE poblacion NOT LIKE 'SIN DATO'");
            //rs.next();
            multiplierK = Integer.parseInt(currentMultipler.replace(".", "").replace("'", ""));
//            multiplierK = multiplierK - 1;
//            double m = Math.pow(10, multiplierK);
//            multiplierK = (int) m;
            //multiplierK=Math.pow(10, multiplierK);

            rs = connectionJdbcMB.consult("SELECT * FROM " + pivotTableName + " ORDER BY id");
            while (rs.next()) {
                boolean find = false;
                String value;
                for (int i = 0; i < columNames.size(); i++) {
                    for (int j = 0; j < rowNames.size(); j++) {
                        if (variablesCrossData.size() == 1) {//ES UNA VARIABLE                            
                            if (rs.getString(1).compareTo(columNames.get(i)) == 0) {
                                value = formateador.format(Double.parseDouble("0"));
                                if (rs.getString("count").compareTo("0") != 0 && rs.getString("poblacion").compareTo("SIN DATO") != 0) {
                                    value = formateador.format((Double.parseDouble(rs.getString("count")) / Double.parseDouble(rs.getString("poblacion"))) * multiplierK);
                                }
                                matrixResult[i][j] = "<b>" + value + "</b><br/>(" + rs.getString("count") + "/" + rs.getString("poblacion") + ")";
                                //matrixResult[i][j] = rs.getString("count");
                                find = true;
                            }
                        }
                        if (variablesCrossData.size() == 2) {//SON DOS VARIABLES                            
                            if (rs.getString(1).compareTo(columNames.get(i)) == 0 && rs.getString(2).compareTo(rowNames.get(j)) == 0) {
                                value = formateador.format(Double.parseDouble("0"));
                                if (rs.getString("count").compareTo("0") != 0 && rs.getString("poblacion").compareTo("SIN DATO") != 0) {
                                    value = formateador.format((Double.parseDouble(rs.getString("count")) / Double.parseDouble(rs.getString("poblacion"))) * multiplierK);
                                }
                                matrixResult[i][j] = "<b>" + value + "</b><br/>(" + rs.getString("count") + "/" + rs.getString("poblacion") + ")";
                                //matrixResult[i][j] = rs.getString("count");
                                find = true;
                            }
                        }
                        if (variablesCrossData.size() == 3) {//SON TRES VARIABLES                            
                            if (columNames.get(i).compareTo(rs.getString(1) + "}" + rs.getString(2)) == 0 && rs.getString(3).compareTo(rowNames.get(j)) == 0) {
                                value = formateador.format(Double.parseDouble("0"));
                                if (rs.getString("count").compareTo("0") != 0 && rs.getString("poblacion").compareTo("SIN DATO") != 0) {
                                    value = formateador.format((Double.parseDouble(rs.getString("count")) / Double.parseDouble(rs.getString("poblacion"))) * multiplierK);
                                }
                                matrixResult[i][j] = "<b>" + value + "</b><br/>(" + rs.getString("count") + "/" + rs.getString("poblacion") + ")";
                                //matrixResult[i][j] = rs.getString("count");
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
        } catch (Exception e) {
            System.out.println("Error 6 en " + this.getClass().getName() + ":" + e.toString());
        }
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
        connectionJdbcMB.non_query("ALTER TABLE " + pivotTableName + " DROP COLUMN poblacion;");//elimino columna poblacion
        connectionJdbcMB.non_query("ALTER TABLE " + pivotTableName + " ADD COLUMN poblacion text;");//coloco la poblacion al final
        connectionJdbcMB.non_query("ALTER TABLE " + pivotTableName + " ADD COLUMN id integer;");

        //---------------------------------------------------------
        //HAY QUE ARMAR LAS POSIBLES COMBINACIONES PARA QUE LOS DATOS QUEDEN ORDENADOS SEGUN COMO SE ENCUENTRE LA CONFIGURACION
        //---------------------------------------------------------
        try {
            ArrayList<String> fieldsNames = new ArrayList<String>();
            //---------------------------------------------------------
            //DETEMINO LOS NOMBRES DE LOS CAMPOS DE LA TABLA
            //---------------------------------------------------------
            ResultSet rs = connectionJdbcMB.consult("SELECT * FROM " + prepivotTableName);
            int ncol = rs.getMetaData().getColumnCount();
            for (int i = 1; i <= ncol; i++) {
                if (rs.getMetaData().getColumnName(i).compareTo("poblacion") != 0) {//no tomar poblacion
                    fieldsNames.add(rs.getMetaData().getColumnName(i));
                }
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
                    sql = sql + "'0','0'," + String.valueOf(id) + ")";
                    id++;
                    connectionJdbcMB.non_query(sql);
                }
                rowNames.add("Cantidad");
            } else if (variablesCrossData.size() == 2) {
                for (int i = 0; i < values1.size(); i++) {
                    columNames.add(values1.get(i));
                    for (int j = 0; j < values2.size(); j++) {
                        if (i == 0) {
                            rowNames.add(values2.get(j));
                        }
                        sql = "INSERT INTO " + pivotTableName + " VALUES (";
                        sql = sql + "'" + values1.get(i) + "',";
                        sql = sql + "'" + values2.get(j) + "',";
                        sql = sql + "'0','0'," + String.valueOf(id) + ")";
                        id++;
                        connectionJdbcMB.non_query(sql);
                    }
                }
            } else if (variablesCrossData.size() == 3) {
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
                            sql = sql + "'0','0'," + String.valueOf(id) + ")";
                            id++;
                            connectionJdbcMB.non_query(sql);
                        }
                    }
                }
            }
            //---------------------------------------------------------            
            //ACTUALZO EL VALOR COUNT DE TABLA PIVOT CON LOS COUNT DE TABLA PREPIVOT
            //---------------------------------------------------------
            sql = " UPDATE " + pivotTableName
                    + " SET count = " + prepivotTableName + ".count, \n\r"
                    + " poblacion = " + prepivotTableName + ".poblacion \n\r"
                    + " FROM " + prepivotTableName + " \n\r"
                    + " WHERE \n\r";
            for (int i = 0; i < fieldsNames.size() - 1; i++) {//-1 column count,-1 column poblacion
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
            System.out.println("Error 7 en " + this.getClass().getName() + ":" + e.toString());
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
                case temporalDisaggregation://DETERMINAR LA DESAGREGACION TEMPORAL -----------------------                   
                    sql = sql + "   CASE \n\r";
                    for (int j = 0; j < variablesCrossData.get(i).getValuesId().size(); j++) {
                        String[] splitDates = variablesCrossData.get(i).getValuesId().get(j).split("}");
                        sql = sql + "       WHEN ( \n\r";
                        sql = sql + "           " + currentIndicator.getInjuryType() + ".injury_date >= to_date('" + splitDates[0] + "','dd/MM/yyyy') AND \n\r";
                        sql = sql + "           " + currentIndicator.getInjuryType() + ".injury_date <= to_date('" + splitDates[1] + "','dd/MM/yyyy') \n\r";
                        sql = sql + "       ) THEN '" + variablesCrossData.get(i).getValues().get(j) + "'  \n\r";
                    }
                    sql = sql + "   END AS fecha";
                    break;
                case age://DETERMINAR EDAD -----------------------                   
                    sql = sql + "   CASE \n\r";
                    for (int j = 0; j < variablesCrossData.get(i).getValuesConfigured().size(); j++) {
                        String[] splitAge = variablesCrossData.get(i).getValuesConfigured().get(j).split("/");
                        if (splitAge[1].compareTo("n") == 0) {
                            splitAge[1] = "200";
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
                    sql = sql + "   (SELECT neighborhood_name FROM neighborhoods WHERE neighborhood_id=" + currentIndicator.getInjuryType() + ".injury_neighborhood_id) as barrio, \n\r";
                    sql = sql + "   CAST((SELECT population FROM neighborhoods WHERE neighborhood_id=" + currentIndicator.getInjuryType() + ".injury_neighborhood_id) as text) as poblacion \n\r";
                    break;
                case communes://COMUNA -----------------------
                    //sql = sql + "   CAST((SELECT neighborhood_suburb FROM neighborhoods WHERE neighborhood_id=" + currentIndicator.getInjuryType() + ".injury_neighborhood_id) as text) as comuna, \n\r";
                    sql = sql + "   (SELECT commune_name FROM communes WHERE commune_id IN( SELECT neighborhood_suburb FROM neighborhoods WHERE neighborhood_id=" + currentIndicator.getInjuryType() + ".injury_neighborhood_id)) as comuna, \n\r";
                    sql = sql + "   CAST((SELECT population FROM communes WHERE commune_id=(SELECT neighborhood_suburb FROM neighborhoods WHERE neighborhood_id=" + currentIndicator.getInjuryType() + ".injury_neighborhood_id)) as text) as poblacion \n\r";
                    break;
                case quadrants://CUADRANTE -----------------------
                    sql = sql + "   (SELECT quadrant_name FROM quadrants WHERE quadrant_id IN( SELECT neighborhood_quadrant FROM neighborhoods WHERE neighborhood_id=" + currentIndicator.getInjuryType() + ".injury_neighborhood_id)) as cuadrante, \n\r";
                    sql = sql + "   CAST((SELECT population FROM quadrants WHERE quadrant_id=(SELECT neighborhood_quadrant FROM neighborhoods WHERE neighborhood_id=" + currentIndicator.getInjuryType() + ".injury_neighborhood_id)) as text) as poblacion \n\r";
                    break;
                case corridors://CORREDOR -----------------------
                    sql = sql + "   (SELECT corridor_name FROM corridors WHERE corridor_id IN( SELECT neighborhood_corridor FROM neighborhoods WHERE neighborhood_id=" + currentIndicator.getInjuryType() + ".injury_neighborhood_id)) as corredor, \n\r";
                    sql = sql + "   CAST((SELECT population FROM corridors WHERE corridor_id=(SELECT neighborhood_corridor FROM neighborhoods WHERE neighborhood_id=" + currentIndicator.getInjuryType() + ".injury_neighborhood_id)) as text) as poblacion \n\r";
                    break;
                case areas://ZONA -----------------------        
                    sql = sql + "   CASE (SELECT neighborhood_area FROM neighborhoods WHERE neighborhood_id=" + currentIndicator.getInjuryType() + ".injury_neighborhood_id)  \n\r";
                    sql = sql + "       WHEN '1' THEN 'ZONA URBANA'  \n\r";
                    sql = sql + "       WHEN '2' THEN 'ZONA RURAL' \n\r";
                    sql = sql + "   END AS zona, \n\r";
                    sql = sql + "   CAST((SELECT population FROM areas WHERE area_id=(SELECT neighborhood_area FROM neighborhoods WHERE neighborhood_id=" + currentIndicator.getInjuryType() + ".injury_neighborhood_id)) as text) as poblacion \n\r";
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
        sql = sql + "       " + currentIndicator.getInjuryType() + ".injury_id = " + currentIndicator.getInjuryId().toString() + " AND \n\r";
        sql = sql + "       " + currentIndicator.getInjuryType() + ".injury_date >= to_date('" + initialDateStr + "','dd/MM/yyyy') AND \n\r";
        sql = sql + "       " + currentIndicator.getInjuryType() + ".injury_date <= to_date('" + endDateStr + "','dd/MM/yyyy'); ";
        //System.out.println("TABLA PREPIVOT:---" + sql);
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
            for (int i = 1; i < ncol + 1; i++) {
                sql = "UPDATE " + prepivotTableName_2 + " SET " + rs.getMetaData().getColumnName(i) + " = 'SIN DATO' "
                        + " WHERE " + rs.getMetaData().getColumnName(i) + " is null;";
                columnNames.add(rs.getMetaData().getColumnName(i));
                connectionJdbcMB.non_query(sql);
            }
        } catch (Exception e) {
            System.out.println("Error 8 en " + this.getClass().getName() + ":" + e.toString());
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

    private void addColumToRow(Row row1, String get, String style, int colSpan, int rowSpan) {
        Column column = new Column();
        HtmlOutputText text = new HtmlOutputText();
        column.setStyleClass(style);
        text.setValue(get);
        column.getChildren().add(text);
        column.setStyle("width: 200px;");
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
    public void loadIndicator9() {
        loadIndicator(9);
    }

    public void loadIndicator16() {
        loadIndicator(16);
    }

    public void loadIndicator23() {
        loadIndicator(23);
    }

    public void loadIndicator30() {
        loadIndicator(30);
    }

    public void loadIndicator37() {
        loadIndicator(37);
    }

    public void loadIndicator44() {
        loadIndicator(44);
    }

    public void loadIndicator51() {
        loadIndicator(51);
    }

    public void loadIndicator58() {
        loadIndicator(58);
    }

    public void loadIndicator65() {
        loadIndicator(65);
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

    public List<String> getListOfCrossVariablesNames() {
        return listOfCrossVariablesNames;
    }

    public void setListOfCrossVariablesNames(List<String> listOfCrossVariablesNames) {
        this.listOfCrossVariablesNames = listOfCrossVariablesNames;
    }

    public List<String> getListOfAvailableVariablesNames() {
        return listOfAvailableVariablesNames;
    }

    public void setListOfAvailableVariablesNames(List<String> listOfAvailableVariablesNames) {
        this.listOfAvailableVariablesNames = listOfAvailableVariablesNames;
    }

    public List<String> getCurrentVariablesCrossSelected() {
        return currentVariablesCrossSelected;
    }

    public void setCurrentVariablesCrossSelected(List<String> currentVariablesCrossSelected) {
        this.currentVariablesCrossSelected = currentVariablesCrossSelected;
    }

    public List<String> getCurrentAvailableVariablesNamesSelected() {
        return currentAvailableVariablesNamesSelected;
    }

    public void setCurrentAvailableVariablesNamesSelected(List<String> currentAvailableVariablesNamesSelected) {
        this.currentAvailableVariablesNamesSelected = currentAvailableVariablesNamesSelected;
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

//    public String getCurrentGraphType() {
//        return currentGraphType;
//    }
//
//    public void setCurrentGraphType(String currentGraphType) {
//        this.currentGraphType = currentGraphType;
//    }
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

    public String getDataTableHtml() {
        return dataTableHtml;
    }

    public void setDataTableHtml(String dataTableHtml) {
        this.dataTableHtml = dataTableHtml;
    }

//    public boolean isShowTotals() {
//        return showTotals;
//    }
//
//    public void setShowTotals(boolean showTotals) {
//        this.showTotals = showTotals;
//    }
//    public boolean isShowAll() {
//        return showAll;
//    }
//
//    public void setShowAll(boolean showAll) {
//        this.showAll = showAll;
//    }
    public OutputPanel getDynamicDataTableGroup() {
        return dynamicDataTableGroup;
    }

    public void setDynamicDataTableGroup(OutputPanel dynamicDataTableGroup) {
        this.dynamicDataTableGroup = dynamicDataTableGroup;
    }

    public boolean isRenderedDynamicDataTable() {
        return renderedDynamicDataTable;
    }

    public void setRenderedDynamicDataTable(boolean renderedDynamicDataTable) {
        this.renderedDynamicDataTable = renderedDynamicDataTable;
    }

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

    public String getCurrentSpatialDisaggregation() {
        return currentSpatialDisaggregation;
    }

    public void setCurrentSpatialDisaggregation(String currentSpatialDisaggregation) {
        this.currentSpatialDisaggregation = currentSpatialDisaggregation;
    }

    public List<String> getSpatialDisaggregationTypes() {
        return spatialDisaggregationTypes;
    }

    public void setSpatialDisaggregationTypes(List<String> spatialDisaggregationTypes) {
        this.spatialDisaggregationTypes = spatialDisaggregationTypes;
    }

    public boolean isShowCalculation() {
        return showCalculation;
    }

    public void setShowCalculation(boolean showCalculation) {
        this.showCalculation = showCalculation;
    }

    public String getCurrentTemporalDisaggregation() {
        return currentTemporalDisaggregation;
    }

    public void setCurrentTemporalDisaggregation(String currentTemporalDisaggregation) {
        this.currentTemporalDisaggregation = currentTemporalDisaggregation;
    }

    public String getCurrentMultipler() {
        return currentMultipler;
    }

    public void setCurrentMultipler(String currentMultipler) {
        this.currentMultipler = currentMultipler;
    }

    public List<String> getMultiplers() {
        return multiplers;
    }

    public void setMultiplers(List<String> multiplers) {
        this.multiplers = multiplers;
    }

    public List<String> getTemporalDisaggregationTypes() {
        return temporalDisaggregationTypes;
    }

    public void setTemporalDisaggregationTypes(List<String> temporalDisaggregationTypes) {
        this.temporalDisaggregationTypes = temporalDisaggregationTypes;
    }

    public boolean isShowItems() {
        return showItems;
    }

    public void setShowItems(boolean showItems) {
        this.showItems = showItems;
    }
    public String getNewConfigurationName() {
        return newConfigurationName;
    }

    public void setNewConfigurationName(String newConfigurationName) {
        this.newConfigurationName = newConfigurationName;
    }

    public String getCurrentConfigurationSelected() {
        return currentConfigurationSelected;
    }

    public void setCurrentConfigurationSelected(String currentConfigurationSelected) {
        this.currentConfigurationSelected = currentConfigurationSelected;
    }

    public List<String> getConfigurationsList() {
        return configurationsList;
    }

    public void setConfigurationsList(List<String> configurationsList) {
        this.configurationsList = configurationsList;
    }
}
