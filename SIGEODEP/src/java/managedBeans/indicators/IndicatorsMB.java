/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.indicators;

import beans.util.Variable;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import managedBeans.reports.SpanColumns;
import managedBeans.reports.TableGroup;
import model.dao.IndicatorsFacade;
import model.pojo.Indicators;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.primefaces.component.outputpanel.OutputPanel;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author SANTOS
 */
@ManagedBean(name = "indicatorsMB")
@SessionScoped
public class IndicatorsMB {

    /**
     * Creates a new instance of IndicatorsMB
     */
    @EJB
    IndicatorsFacade indicatorsFacade;
    Indicators currentIndicator;
    IndicatorsDataMB indicatorsDataMB;
    private StreamedContent chartImage;
    private String titlePage = "SIGEODEP -  INDICADORES GENERALES PARA LESIONES FATALES";
    private String titleIndicator = "SIGEODEP -  INDICADORES GENERALES PARA LESIONES FATALES";
    private String subTitleIndicator = "NUMERO DE CASOS POR LESION";
    private String currentGraphType;
    private String currentVariableGraph;
    private String currentValueGraph;
    private String currentValue;
    private String firstVariablesCrossSelected = null;
    private String initialValue = "";
    private String endValue = "";
    private String[] dynamicHeaders2;
    private Date initialDate = new Date();
    private Date endDate = new Date();
    private List<String> graphTypes = new ArrayList<String>();
    private List<String> variablesGraph = new ArrayList<String>();
    private List<String> valuesGraph = new ArrayList<String>();
    private List<String> variablesList = new ArrayList<String>();//lista de nombres de variables disponibles que sepueden cruzar(se visualizan en pagina)
    private List<String> variablesCrossList = new ArrayList<String>();//ista de nombres de variables que se van a cruzar(se visualizan en pagina)
    private List<String> currentVariablesSelected = new ArrayList<String>();//lista de nombres seleccionados en la lista de variables disponibles
    private List<String> currentVariablesCrossSelected = new ArrayList<String>();//lista de nombres seleccionados en la lista de variables a cruzar    
    private List<String> currentCategoricalValuesList = new ArrayList<String>();
    private List<String> currentCategoricalValuesSelected;
    private ArrayList<Variable> variablesListData;//lista de variables que tiene el indicador
    private ArrayList<Variable> variablesCrossData = new ArrayList<Variable>();//lista de variables a cruzar
    private ArrayList<String> valuesCategoryList;//lista de valores para una categoria
    private ArrayList<SpanColumns> dynamicHeaders1;
    private ArrayList<TableGroup> listaDeGrupos;
    private Variable currentVariableConfiguring;
    private int size = 10;
    private int numberCross = 2;//maximo numero de variables a cruzar
    private boolean btnAddVariableDisabled = true;
    private boolean btnAddCategoricalValueDisabled = true;
    private boolean btnRemoveCategoricalValueDisabled = true;
    private boolean btnRemoveVariableDisabled = true;
    private boolean renderedDynamicDataTable = true;
    private boolean showAll = false;//mostrar filas y columnas vacias
    private boolean showTotals = false;//mostrar totales    
    private SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
    private OutputPanel dynamicDataTableGroup; // Placeholder.
    private FacesMessage message = null;
//    private SelectItem[] aligmentVariables;
//    private int currentAligment = 0;

    public IndicatorsMB() {
        //-------------------------------------------------
        indicatorsDataMB = (IndicatorsDataMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{indicatorsDataMB}", IndicatorsDataMB.class);
        Calendar c = Calendar.getInstance();
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
        //System.out.println("CAMBIO VARIABLE");
        valuesGraph = new ArrayList<String>();
        for (int i = 0; i < variablesCrossData.size(); i++) {
            System.out.println("COMPARA" + variablesCrossData.get(i).getName() + "    " + currentVariableGraph);
            if (variablesCrossData.get(i).getName().compareTo(currentVariableGraph) == 0) {
                //System.out.println("1");
                //currentVariableGraph = variablesGraph.get(0);
                //cargo el combo de valores
                for (int j = 0; j < variablesCrossData.get(i).getValuesConfigured().size(); j++) {
                    //System.out.println("2");
                    valuesGraph.add(variablesCrossData.get(i).getValuesConfigured().get(j));
                    currentValueGraph = valuesGraph.get(0);
                }
                ///System.out.println("3");
                break;
            }
            System.out.println("4");
        }
        createImage();
    }

    public void process() {
        boolean continueProcess;
        message = null;

        //reinicio los combos para el grafico
        //graphTypes = new ArrayList<String>();
        variablesGraph = new ArrayList<String>();
        valuesGraph = new ArrayList<String>();
        currentValueGraph = "";
        currentVariableGraph = "";

        if (numberCross == 2) {
            if (variablesCrossList.size() == 1 || variablesCrossList.size() == 2) {
                continueProcess = true;
            } else {
                continueProcess = false;
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "En la lista de variables a cruzar deben haber una o dos variables");
            }
        } else {
            if (variablesCrossList.size() == 2 || variablesCrossList.size() == 3) {
                continueProcess = true;
            } else {
                continueProcess = false;
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "En la lista de variables a cruzar deben haber dos o tres variables");
            }
        }

        if (continueProcess) {
            //revisar que las listas de valores configuradas tengan como minimo un valor para la categoria
            variablesCrossData = new ArrayList<Variable>();//lista de variables a cruzar
            ArrayList<String> errorsList = new ArrayList<String>();
            for (int i = 0; i < variablesCrossList.size(); i++) {
                for (int j = 0; j < variablesListData.size(); j++) {
                    if (variablesListData.get(j).getName().compareTo(variablesCrossList.get(i)) == 0) {
                        if (variablesListData.get(j).getValuesConfigured().isEmpty()) {
                            errorsList.add("La variable " + variablesListData.get(j).getName() + " no tiene valores configurados, para continuar debe ser configurada.");
                        }
                    }
                }
            }
            if (errorsList.isEmpty()) {
                //creo la lista de variables a cruzar
                if (currentIndicator.getIndicatorId() == 1) {//agrego a la lista de variables a cruzar "tipo de lesion"
                    Variable newVariable = indicatorsDataMB.createVariable("Tipo Lesión", "injuries_fatal", false);
                    variablesCrossData.add(newVariable);
                }
                for (int j = 0; j < variablesCrossList.size(); j++) {
                    for (int i = 0; i < variablesListData.size(); i++) {
                        if (variablesListData.get(i).getName().compareTo(variablesCrossList.get(j)) == 0) {
                            variablesCrossData.add(variablesListData.get(i));
                        }
                    }
                }
                //cargo el combo de variables para el grafico
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

                indicatorsDataMB.setCurrentIndicator(currentIndicator);
                indicatorsDataMB.setPivotTableName("table_pivot");
                indicatorsDataMB.setPrepivotTableName("table_prepivot");
                indicatorsDataMB.setVariablesCrossData(variablesCrossData);
                indicatorsDataMB.setShowAll(showAll);
                indicatorsDataMB.setShowTotals(showTotals);
                indicatorsDataMB.setInitialDateStr(formato.format(initialDate));
                indicatorsDataMB.setEndDateStr(formato.format(endDate));

                indicatorsDataMB.createPrepivotTable();//creo la tabla prepivot
                indicatorsDataMB.createPivotTable();//creo la tabla pivot
                dynamicDataTableGroup = new OutputPanel();//creo el panel grid
                dynamicDataTableGroup.getChildren().add(indicatorsDataMB.createDataTableResult());
                createImage();//creo el grafico
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Cruze realizado");

            } else {
                String errorsString = "";
                for (int i = 0; i < errorsList.size(); i++) {
                    errorsString = errorsString + ", " + errorsList.get(i);
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", errorsString);
                }
            }
        }
    }

    public void changeCategoticalList() {
        if (!currentCategoricalValuesSelected.isEmpty()) {
            btnRemoveCategoricalValueDisabled = false;
        }
    }

    public int btnAddCategoricalValueClick() {
        int i = 0;
        int e = 0;
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
                //System.out.println("COMPARANDO----------------------------");
                //System.out.println("COMPARANDO----------------------------");
                //System.out.println("VALOR INICIAL ENCONTRADO : " + String.valueOf(initialValueFoundInteger));
                //System.out.println("VALOR FINAL ENCONTRADO : " + String.valueOf(endValueFoundInteger));
                //System.out.println("VALOR INICIAL A ADICIONAR : " + String.valueOf(initialValueAddInteger));
                //System.out.println("VALOR INICIAL A ADICIONAR : " + String.valueOf(endValueAddInteger));
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
        //ingreso el nuevo valor a la categoria
        if (currentVariableConfiguring != null) {
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
        //currentCategoricalValuesSelected = new ArrayList<String>();
        btnRemoveCategoricalValueDisabled = false;
        //determino cual es la variable actual:
//        System.out.println("ELIMINANDO COMO ENTRA----------------------------");
//        System.out.println("VALUES A ELIMINAR : " + currentCategoricalValuesSelected);
//        System.out.println("VALUES NORMALES   : " + currentVariableConfiguring.getValues());
//        System.out.println("VALUES CONFIGUR   : " + currentVariableConfiguring.getValuesConfigured());
//        System.out.println("VALUES MOSTRADOS  : " + currentCategoricalList);
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
//            System.out.println("ELIMINANDO COMO SALE----------------------------");
//            System.out.println("VALUES A ELIMINAR : " + currentCategoricalValuesSelected);
//            System.out.println("VALUES NORMALES : " + currentVariableConfiguring.getValues());
//            System.out.println("VALUES CONFIGUR : " + currentVariableConfiguring.getValuesConfigured());
//            System.out.println("VALUES MOSTRADOS : " + currentCategoricalValuesList);
        }
    }

    public void btnResetCategoryListClick() {
        currentCategoricalValuesSelected = new ArrayList<String>();
        btnRemoveCategoricalValueDisabled = false;
//        System.out.println("RESETEANDO COMO ENTRA----------------------------");
//        System.out.println("VALUES NORMALES : " + currentVariableConfiguring.getValues());
//        System.out.println("VALUES CONFIGUR : " + currentVariableConfiguring.getValuesConfigured());
//        System.out.println("VALUES MOSTRADOS : " + currentCategoricalList);
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
//            System.out.println("REINICIANDO COMO SALE----------------------------");
//            System.out.println("VALUES NORMALES : " + currentVariableConfiguring.getValues());
//            System.out.println("VALUES CONFIGUR : " + currentVariableConfiguring.getValuesConfigured());
//            System.out.println("VALUES MOSTRADOS : " + currentCategoricalValuesList);
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
                    //System.out.println("comparar: " + variablesList.get(i)+ " CON "+currentVariablesSelected.get(j));
                    if (variablesList.get(i).compareTo(currentVariablesSelected.get(j)) == 0) {//esta es la variable encontrada que saldra de la lista                    
                        //System.out.println("quitar: " + variablesList.get(i));
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

//            if (currentGraphType.compareTo("pastel") == 0) {
//                try {
//                    JFreeChart jfreechart = ChartFactory.createPieChart(subTitleIndicator, indicatorsDataMB.createPieDataset(), true, true, false);
//                    File chartFile = new File("dynamichart");
//                    ChartUtilities.saveChartAsPNG(chartFile, jfreechart, 700, 500);
//                    chartImage = new DefaultStreamedContent(new FileInputStream(chartFile), "image/png");
//                } catch (Exception e) {
//                }
//            }
        if (currentGraphType.compareTo("barras vertical") == 0) {
            try {//JFreeChart 
                //String 
                JFreeChart chart = indicatorsDataMB.createChart(variablesCrossData, currentVariableGraph, currentValueGraph, currentIndicator);//ChartFactory.createBarChart(subTitleIndicator, variablesCrossData.get(1).getName(), "Conteo", indicatorsDataMB.createDefaultCategoryDataset(variablesCrossData,currentVariableGraph,currentValueGraph), PlotOrientation.VERTICAL, true, true, false);
                File chartFile = new File("dynamichart");
                ChartUtilities.saveChartAsPNG(chartFile, chart, 650, 500);
                chartImage = new DefaultStreamedContent(new FileInputStream(chartFile), "image/png");
            } catch (Exception e) {
            }

//            if (currentGraphType.compareTo("barras horizontal") == 0) {
//                try {
//                    JFreeChart chart = ChartFactory.createBarChart(subTitleIndicator, "Student", "Marks", indicatorsDataMB.createDefaultCategoryDataset(), PlotOrientation.HORIZONTAL, false, true, false);
//                    chart.setBackgroundPaint(new Color(200, 200, 200));
//                    chart.getTitle().setPaint(new Color(50, 50, 50));
//                    CategoryPlot p = chart.getCategoryPlot();
//                    p.setRangeGridlinePaint(Color.red);
//                    File chartFile = new File("dynamichart");
//                    ChartUtilities.saveChartAsPNG(chartFile, chart, 700, 500);
//                    chartImage = new DefaultStreamedContent(new FileInputStream(chartFile), "image/png");
//                } catch (Exception e) {
//                }
//            }
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
        variablesListData = indicatorsDataMB.getVariablesIndicator(currentIndicator.getIndicatorId());
        graphTypes = indicatorsDataMB.getGraphTypes(currentIndicator.getIndicatorId());
        variablesGraph = new ArrayList<String>();
        valuesGraph = new ArrayList<String>();
        currentValueGraph = "";
        currentVariableGraph = "";
        numberCross = currentIndicator.getNumberCross();
        variablesList = new ArrayList<String>();//SelectItem[variablesListData.size()];
        for (int i = 0; i < variablesListData.size(); i++) {
            variablesList.add(variablesListData.get(i).getName());
        }
        dynamicDataTableGroup = new OutputPanel();//creo el panel grid
        
        variablesCrossList = new ArrayList<String>();//SelectItem[variablesListData.size()];
        btnAddVariableDisabled = true;
        btnRemoveVariableDisabled = true;
        currentVariablesSelected = null;
        currentVariablesCrossSelected = null;
    }

    private void loadIndicator(int n) {        
        currentIndicator = indicatorsFacade.find(n);
        reset();
//        titlePage = currentIndicator.getIndicatorGroup();
//        titleIndicator = currentIndicator.getIndicatorGroup();
//        subTitleIndicator = currentIndicator.getIndicatorName();
//        variablesListData = indicatorsDataMB.getVariablesIndicator(n);
//        graphTypes = indicatorsDataMB.getGraphTypes(n);
//        numberCross = currentIndicator.getNumberCross();
//        variablesList = new ArrayList<String>();//SelectItem[variablesListData.size()];        
//        for (int i = 0; i < variablesListData.size(); i++) {
//            variablesList.add(variablesListData.get(i).getName());
//        }
//        btnAddVariableDisabled = true;
//        btnRemoveVariableDisabled = true;
//        currentVariablesSelected = null;
//        currentVariablesCrossSelected = null;
    }

    //FUNCIONES GET AND SET
    public void loadIndicator1() {
        loadIndicator(1);
    }

    public void loadIndicator2() {
        loadIndicator(2);
    }

    public void loadIndicator3() {
        loadIndicator(3);
    }

    public void loadIndicator4() {
        loadIndicator(4);
    }

    public void loadIndicator5() {
        loadIndicator(5);
    }

    public void loadIndicator6() {
        loadIndicator(6);
    }

    public void loadIndicator7() {
        loadIndicator(7);
    }

    public void loadIndicator8() {
        loadIndicator(8);
    }

    public void loadIndicator9() {
        loadIndicator(9);
    }

    public void loadIndicator10() {
        loadIndicator(10);
    }

    public void loadIndicator11() {
        loadIndicator(12);
    }

    public void loadIndicator12() {
        loadIndicator(12);
    }

    public void loadIndicator13() {
        loadIndicator(13);
    }

    public void loadIndicator14() {
        loadIndicator(14);
    }

    public void loadIndicator15() {
        loadIndicator(15);
    }

    public void loadIndicator16() {
        loadIndicator(16);
    }

    public void loadIndicator17() {
        loadIndicator(17);
    }

    public void loadIndicator18() {
        loadIndicator(18);
    }

    public void loadIndicator19() {
        loadIndicator(19);
    }

    public void loadIndicator20() {
        loadIndicator(20);
    }

    public void loadIndicator21() {
        loadIndicator(21);
    }

    public void loadIndicator22() {
        loadIndicator(22);
    }

    public void loadIndicator23() {
        loadIndicator(23);
    }

    public void loadIndicator24() {
        loadIndicator(24);
    }

    public void loadIndicator25() {
        loadIndicator(25);
    }

    public void loadIndicator26() {
        loadIndicator(26);
    }

    public void loadIndicator27() {
        loadIndicator(27);
    }

    public void loadIndicator28() {
        loadIndicator(28);
    }

    public void loadIndicator29() {
        loadIndicator(29);
    }

    public void loadIndicator30() {
        loadIndicator(30);
    }

    public void loadIndicator31() {
        loadIndicator(31);
    }

    public void loadIndicator32() {
        loadIndicator(32);
    }

    public void loadIndicator33() {
        loadIndicator(33);
    }

    public void loadIndicator34() {
        loadIndicator(34);
    }

    public void loadIndicator35() {
        loadIndicator(35);
    }

    public void loadIndicator36() {
        loadIndicator(36);
    }

    public void loadIndicator37() {
        loadIndicator(37);
    }

    public void loadIndicator38() {
        loadIndicator(38);
    }

    public void loadIndicator39() {
        loadIndicator(39);
    }

    public void loadIndicator40() {
        loadIndicator(40);
    }

    public void loadIndicator41() {
        loadIndicator(41);
    }

    public void loadIndicator42() {
        loadIndicator(42);
    }

    public void loadIndicator43() {
        loadIndicator(43);
    }

    public void loadIndicator44() {
        loadIndicator(44);
    }

    public void loadIndicator45() {
        loadIndicator(45);
    }

    public void loadIndicator46() {
        loadIndicator(46);
    }

    public void loadIndicator47() {
        loadIndicator(47);
    }

    public void loadIndicator48() {
        loadIndicator(48);
    }

    public void loadIndicator49() {
        loadIndicator(49);
    }

    public void loadIndicator50() {
        loadIndicator(50);
    }

    public void loadIndicator51() {
        loadIndicator(51);
    }

    public void loadIndicator52() {
        loadIndicator(52);
    }

    public void loadIndicator53() {
        loadIndicator(53);
    }

    public void loadIndicator54() {
        loadIndicator(54);
    }

    public void loadIndicator55() {
        loadIndicator(55);
    }

    public void loadIndicator56() {
        loadIndicator(56);
    }

    public void loadIndicator57() {
        loadIndicator(57);
    }

    public void loadIndicator58() {
        loadIndicator(58);
    }

    public void loadIndicator59() {
        loadIndicator(59);
    }

    public void loadIndicator60() {
        loadIndicator(60);
    }

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

    public List<String> getGraphTypes() {
        return graphTypes;
    }

    public void setGraphTypes(List<String> graphTypes) {
        this.graphTypes = graphTypes;
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

    public boolean isShowTotals() {
        return showTotals;
    }

    public void setShowTotals(boolean showTotals) {
        this.showTotals = showTotals;
    }

    public boolean isShowAll() {
        return showAll;
    }

    public void setShowAll(boolean showAll) {
        this.showAll = showAll;
    }

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
}
