/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.indicators;

import beans.util.LineLegendItemSource;
import beans.connection.ConnectionJdbcMB;
import beans.enumerators.VariablesEnum;
import beans.util.Variable;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Paint;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
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
import java.util.Locale;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import managedBeans.login.LoginMB;
import beans.util.SpanColumns;
import model.dao.IndicatorsConfigurationsFacade;
import model.dao.IndicatorsFacade;
import model.pojo.Indicators;
import model.pojo.IndicatorsConfigurations;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.RectangleEdge;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.primefaces.component.column.Column;
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
    @EJB
    IndicatorsConfigurationsFacade indicatorsConfigurationsFacade;
    private List<String> currentConfigurationSelected = new ArrayList<>();
    ;
    private List<String> configurationsList = new ArrayList<>();
    private String newConfigurationName = "";
    private Indicators currentIndicator;
    private StreamedContent chartImage;
    private SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy", new Locale("ES"));
    private FacesMessage message = null;
    private ConnectionJdbcMB connectionJdbcMB;
    private String titlePage = "SIGEODEP -  INDICADORES GENERALES PARA LESIONES FATALES";
    private String titleIndicator = "SIGEODEP -  INDICADORES GENERALES PARA LESIONES FATALES";
    private String subTitleIndicator = "NUMERO DE CASOS POR LESION";
    //private String currentVariableGraph;
    //private String currentValueGraph;
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
    private LoginMB loginMB;
//    private List<String> variablesGraph = new ArrayList<>();
//    private List<String> valuesGraph = new ArrayList<>();
    private List<String> variablesList = new ArrayList<>();//lista de nombres de variables disponibles que sepueden cruzar(se visualizan en pagina)
    private List<String> variablesCrossList = new ArrayList<>();//ista de nombres de variables que se van a cruzar(se visualizan en pagina)
    private List<String> currentVariablesSelected = new ArrayList<>();//lista de nombres seleccionados en la lista de variables disponibles
    private List<String> currentVariablesCrossSelected = new ArrayList<>();//lista de nombres seleccionados en la lista de variables a cruzar    
    private List<String> currentCategoricalValuesList = new ArrayList<>();
    private List<String> currentCategoricalValuesSelected;
    private ArrayList<SpanColumns> headers1;//CABECERA 1 CUANDO EL CRUCE SE REALIZA SOBRE 3 VARIABLES
    private ArrayList<Variable> variablesListData;//lista de variables que tiene el indicador
    private ArrayList<Variable> variablesCrossData = new ArrayList<>();//lista de variables a cruzar
    private ArrayList<String> valuesCategoryList;//lista de valores para una categoria
    private ArrayList<String> columNames;//NOMBRES DE LAS COLUMNAS, (SI EL CRUCE ES DE TRES VARIABLES ESTA SEPARADO POR EL CARACTER: }  )
    private ArrayList<String> rowNames;//NOMBRES DE LAS FILAS    
    private ArrayList<String> totalsHorizontal = new ArrayList<>();
    private ArrayList<String> totalsVertical = new ArrayList<>();
    private Variable currentVariableConfiguring;
    private int grandTotal = 0;//total general de la matriz
    private int numberCross = 3;//maximo numero de variables a cruzar
    private int currentYear = 0;
    private boolean btnAddVariableDisabled = true;
    private boolean btnAddCategoricalValueDisabled = true;
    private boolean btnRemoveCategoricalValueDisabled = true;
    private boolean btnRemoveVariableDisabled = true;
    int colorId = 0;
    int typeFill = 0;
    private String sql;//mostrar filas y columnas vacias
    private boolean showCount = false;//mostrar recuento
    private boolean showFrames = true;
    private boolean showItems = true;
    private boolean showRowPercentage = true;//mostrar porcentaje por fila
    private boolean showColumnPercentage = false;//mostrar porcentaje por columna
    private boolean showTotalPercentage = false;//mostrar porcentaje del total
    private boolean btnExportDisabled = true;
    private boolean showEmpty = false;
    boolean colorType = true;
    private Integer tuplesProcessed = 0;
    private StringBuilder sb;
    private CopyManager cpManager;
    private String sourceTable = "";//tabla adicional que se usara en la seccion "FROM" de la consulta sql
    private String filterSourceTable = "";//filtro adicional usado en la "WHERE" de la consulta sql
    private String currentVariableGraph1;
    private String currentVariableGraph2;
    private String currentValueGraph1;
    private String currentValueGraph2;
    private List<String> valuesGraph1 = new ArrayList<>();
    private List<String> valuesGraph2 = new ArrayList<>();
    private boolean separateRecords = false;
    private boolean addAbuseTypes = false;
    private int heightGraph = 460;
    private int widthGraph = 660;
    private List<String> typesGraph = new ArrayList<>();
    private String currentTypeGraph;
    DecimalFormat formateador = new DecimalFormat("0.00");
    String variablesName = "";
    String categoryAxixLabel = "";
    String indicatorName = "";
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy", new Locale("ES"));
    DefaultCategoryDataset defaultDataSet = null;
    DefaultPieDataset pieDataSet = null;

    public IndicatorsPercentageMB() {
        connectionJdbcMB = (ConnectionJdbcMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{connectionJdbcMB}", ConnectionJdbcMB.class);
        loginMB = (LoginMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{loginMB}", LoginMB.class);
        Calendar c = Calendar.getInstance();
        currentYear = c.get(Calendar.YEAR);
        initialDate.setDate(1);
        initialDate.setMonth(0);
        initialDate.setYear(2002 - 1900);
        endDate.setDate(c.get(Calendar.DATE));
        endDate.setMonth(c.get(Calendar.MONTH));
        endDate.setYear(c.get(Calendar.YEAR) - 1900);
    }

    public void showMessage() {
        if (message != null) {
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void loadValuesGraph() {
        valuesGraph1 = new ArrayList<>();
        valuesGraph2 = new ArrayList<>();
        currentVariableGraph1 = "";
        currentVariableGraph2 = "";
        currentValueGraph1 = null;
        currentValueGraph2 = null;

        if (variablesCrossData.size() == 2 && currentTypeGraph.compareTo("pastel") == 0) {
            currentVariableGraph1 = variablesCrossData.get(1).getName();
            for (int j = 0; j < variablesCrossData.get(1).getValuesConfigured().size(); j++) {
                valuesGraph1.add(variablesCrossData.get(1).getValuesConfigured().get(j));
                currentValueGraph1 = variablesCrossData.get(1).getValuesConfigured().get(j);
            }
        }
        if (variablesCrossData.size() == 3 && currentTypeGraph.compareTo("pastel") != 0) {
            currentVariableGraph1 = variablesCrossData.get(2).getName();
            for (int j = 0; j < variablesCrossData.get(2).getValuesConfigured().size(); j++) {
                valuesGraph1.add(variablesCrossData.get(2).getValuesConfigured().get(j));
                currentValueGraph1 = variablesCrossData.get(2).getValuesConfigured().get(j);
            }
        }
        if (variablesCrossData.size() == 3 && currentTypeGraph.compareTo("pastel") == 0) {
            currentVariableGraph1 = variablesCrossData.get(1).getName();
            for (int j = 0; j < variablesCrossData.get(1).getValuesConfigured().size(); j++) {
                valuesGraph1.add(variablesCrossData.get(1).getValuesConfigured().get(j));
                currentValueGraph1 = variablesCrossData.get(1).getValuesConfigured().get(j);
            }
            currentVariableGraph2 = variablesCrossData.get(2).getName();
            for (int j = 0; j < variablesCrossData.get(2).getValuesConfigured().size(); j++) {
                valuesGraph2.add(variablesCrossData.get(2).getValuesConfigured().get(j));
                currentValueGraph2 = variablesCrossData.get(2).getValuesConfigured().get(j);
            }
        }
        createImage();
    }

    public void createMatrixResult() {
        try {//System.out.println("INICIA CREAR MATRIZ xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
            ResultSet rs;
            //---------------------------------------------------------            
            //SE CREA LA MATRIZ DE RESULTADOS (iniciada en 0 )
            //---------------------------------------------------------
            matrixResult = new String[columNames.size()][rowNames.size()];
            for (int i = 0; i < columNames.size(); i++) {
                for (int j = 0; j < rowNames.size(); j++) {
                    matrixResult[i][j] = "0";
                }
            }
            //---------------------------------------------------------            
            //INSERTAMOS DATOS EN MATRIZ
            //---------------------------------------------------------
            sql = ""
                    + " SELECT \n"
                    + "    * \n"
                    + " FROM \n"
                    + "    indicators_records \n"
                    + " WHERE \n"
                    + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                    + "    indicator_id = " + currentIndicator.getIndicatorId() + "  \n";
            rs = connectionJdbcMB.consult(sql);
            while (rs.next()) {
                boolean find = false;
                for (int i = 0; i < columNames.size(); i++) {
                    for (int j = 0; j < rowNames.size(); j++) {
                        if (variablesCrossData.size() == 1) {//ES UNA VARIABLE                            
                            if (rs.getString("column_1").compareTo(columNames.get(i)) == 0) {
                                matrixResult[i][j] = rs.getString("count");
                                find = true;
                            }
                        }
                        if (variablesCrossData.size() == 2) {//SON DOS VARIABLES                            
                            if (rs.getString("column_1").compareTo(columNames.get(i)) == 0 && rs.getString("column_2").compareTo(rowNames.get(j)) == 0) {
                                matrixResult[i][j] = rs.getString("count");
                                find = true;
                            }
                        }
                        if (variablesCrossData.size() == 3) {//SON TRES VARIABLES                            
                            if (columNames.get(i).compareTo(rs.getString("column_1") + "}" + rs.getString("column_2")) == 0 && rs.getString("column_3").compareTo(rowNames.get(j)) == 0) {
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
            totalsHorizontal = new ArrayList<>();
            totalsVertical = new ArrayList<>();
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
        } catch (SQLException | NumberFormatException e) {
            System.out.println("Error 1 en " + this.getClass().getName() + ":" + e.toString());
        }
    }

    private void removeEmpty() {
        //------------------------------------------------------------------
        //SE ELIMINAN LOS VALORES VACIOS
        //------------------------------------------------------------------
        sql = ""
                + " DELETE FROM \n\r"
                + "    indicators_records \n\r"
                + " WHERE \n\r"
                + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n\r"
                + "    indicator_id = " + currentIndicator.getIndicatorId() + " AND \n\r"
                + "    count = 0 ";
        connectionJdbcMB.non_query(sql);
        //---------------------------------------------------------            
        //ELIMINO LOS NOMBRES DE COLUMNAS NO NECESARIOS
        //---------------------------------------------------------            
        ResultSet rs = null;
        if (variablesCrossData.size() < 3) { //una o dos variables
            sql = ""
                    + " SELECT column_1 FROM  indicators_records \n"
                    + " WHERE  user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                    + "        indicator_id = " + currentIndicator.getIndicatorId() + "  \n"
                    + " GROUP BY column_1  ORDER BY MIN(record_id) \n";

        }
        if (variablesCrossData.size() == 3) {
            sql = ""
                    + " SELECT column_1 ||'}'|| column_2 FROM indicators_records \n"
                    + " WHERE  user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                    + "        indicator_id = " + currentIndicator.getIndicatorId() + "  \n"
                    + " GROUP BY  column_1 ||'}'|| column_2  ORDER BY MIN(record_id) \n";
        }
        try {
            rs = connectionJdbcMB.consult(sql);
            ArrayList<String> columnNamesFound = new ArrayList<>();
            while (rs.next()) {
                columnNamesFound.add(rs.getString(1));
            }
            int columnFound;
            for (int i = 0; i < columNames.size(); i++) {
                columnFound = -1;
                for (int j = 0; j < columnNamesFound.size(); j++) {
                    if (columNames.get(i).compareTo(columnNamesFound.get(j)) == 0) {
                        columnFound = j;
                        break;
                    }
                }
                if (columnFound == -1) {
                    columNames.remove(i);
                    i = -1;//vuelvo a empezar
                }
            }
        } catch (Exception e) {
        }
        //---------------------------------------------------------            
        //ELIMINO LOS NOMBRES DE FILAS NO NECESARIOS
        //---------------------------------------------------------            
        sql = "";
        if (variablesCrossData.size() == 2) {
            sql = ""
                    + " SELECT column_2 FROM indicators_records \n"
                    + " WHERE user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                    + "       indicator_id = " + currentIndicator.getIndicatorId() + "  \n"
                    + " GROUP BY column_2 ORDER BY MIN(record_id) \n";
        }
        if (variablesCrossData.size() == 3) {
            sql = ""
                    + " SELECT column_3 FROM indicators_records \n"
                    + " WHERE user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                    + "       indicator_id = " + currentIndicator.getIndicatorId() + "  \n"
                    + " GROUP BY column_3 ORDER BY MIN(record_id) \n";

        }
        if (sql.length() != 0) {
            try {
                rs = connectionJdbcMB.consult(sql);
                ArrayList<String> rowsNamesFound = new ArrayList<>();
                while (rs.next()) {
                    rowsNamesFound.add(rs.getString(1));
                }
                int rowFound;
                for (int i = 0; i < rowNames.size(); i++) {
                    rowFound = -1;
                    for (int j = 0; j < rowsNamesFound.size(); j++) {
                        if (rowNames.get(i).compareTo(rowsNamesFound.get(j)) == 0) {
                            rowFound = j;
                            break;
                        }
                    }
                    if (rowFound == -1) {
                        rowNames.remove(i);
                        i = -1;//vuelvo a empezar
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    public void process() {
        btnExportDisabled = true;
        variablesCrossData = new ArrayList<>();//lista de variables a cruzar            
        boolean continueProcess = true;
        message = null;
        categoryAxixLabel = "";
//        variablesGraph = new ArrayList<>();
//        valuesGraph = new ArrayList<>();
//        currentValueGraph = "";
//        currentVariableGraph = "";

        if (continueProcess) {//VALIDACION DE FECHAS
            initialDateStr = formato.format(initialDate);
            endDateStr = formato.format(endDate);
        }
        if (continueProcess) {//NUMERO DE VARIABLES A CRUZAR SEA MENOR O IGUAL AL LIMITE ESTABLECIDO
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
        }
        if (continueProcess) {//SI ES INDICADOR GENERAL AGREGO UNA NUEVA VARIABLE A CRUZAR(tipo lesion)
            if (currentIndicator.getIndicatorId() == 1 || currentIndicator.getIndicatorId() == 2) {
                //agrego a la lista de variables a cruzar "tipo de lesion fatal"
                Variable newVariable = createVariable("Tipo Lesion", "injuries_fatal", false, "");
                variablesCrossData.add(newVariable);
            }
            if (currentIndicator.getIndicatorId() == 3 || currentIndicator.getIndicatorId() == 4) {
                //agrego a la lista de variables a cruzar "tipo de lesion fatal"
                Variable newVariable = createVariable("Tipo Lesion", "injuries_non_fatal", false, "");
                variablesCrossData.add(newVariable);
            }
        }
        if (continueProcess) {//AGREGO LAS VARIABLES INDICADAS POR EL USUARIO
            for (int j = 0; j < variablesCrossList.size(); j++) {
                for (int i = 0; i < variablesListData.size(); i++) {
                    if (variablesListData.get(i).getName().compareTo(variablesCrossList.get(j)) == 0) {
                        variablesCrossData.add(variablesListData.get(i));
                    }
                }
            }
        }
        if (continueProcess) {//CADA VARIABLE A CRUZAR TENGA VALORES CONFIGURADOS
            for (int i = 0; i < variablesCrossData.size(); i++) {
                if (variablesCrossData.get(i).getValuesConfigured().isEmpty()) {
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "La variable " + variablesListData.get(i).getName() + " no tiene valores configurados, para continuar debe ser configurada.");
                    continueProcess = false;
                }
            }
        }
//        if (continueProcess) {//CARGO LOS COMBOS PARA EL GRAFICO
//            if (variablesCrossData.size() == 3) {
//                for (int i = 0; i < variablesCrossData.size(); i++) {
//                    if (i == 0) {
//                        variablesGraph.add(variablesCrossData.get(i).getName());
//                        currentVariableGraph = variablesGraph.get(0);
//                        //cargo el combo de valores
//                        for (int j = 0; j < variablesCrossData.get(i).getValuesConfigured().size(); j++) {
//                            valuesGraph.add(variablesCrossData.get(i).getValuesConfigured().get(j));
//                            currentValueGraph = valuesGraph.get(0);
//                        }
//                    } else {
//                        variablesGraph.add(variablesCrossData.get(i).getName());
//                    }
//                }
//            }
//        }

        if (continueProcess) {
            removeIndicatorRecords();
        }
        if (continueProcess) {//ALMACENO EN BASE DE DATOS LOS REGISTROS DE ESTE CRUCE
            saveIndicatorRecords(createIndicatorConsult());
        }
        if (continueProcess) {//DE SER NECESARIO SEPARO LOS REGISTROS(cuando son variables con relaciones de uno a muchos)
            if (separateRecords) {
                separateRecordsFunction();
            }
        }
//        if (continueProcess) {//ELIMINO LOS VALORES QUE SEAN CONFIGURADOS POR EL USUARIO
//            removeValuesConfigured();
//        }
        if (continueProcess) {//CREO TODAS LAS POSIBLES COMBINACIONES
            createCombinations();
        }
        if (continueProcess) {//AGRUPO LOS VALORES
            groupingOfValues();
        }
        if (!showEmpty) {
            removeEmpty();
        }
        if (continueProcess) {//MATRIZ DE RESULTADOS
            createMatrixResult();
        }
        if (continueProcess) {//GENERO TABLA E IMAGEN
            dataTableHtml = createDataTableResult();
            //createImage();//creo el grafico
            loadValuesGraph();//creo el grafico
            btnExportDisabled = false;
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Cruze realizado");
        }

//        if (currentVariablesCrossSelected != null) {
//            System.err.println("currentVariablesCrossSelected:" + currentVariablesCrossSelected.toString());
//        } else {
//            System.err.println("currentVariablesCrossSelected es null");
//        }
//        if (variablesCrossList != null) {
//            System.err.println("variablesCrossList" + variablesCrossList.toString());
//        } else {
//            System.err.println("variablesCrossList es null");
//        }
        currentVariablesCrossSelected = new ArrayList<>();;
    }

    private void groupingOfValues() {
        //------------------------------------------------------------------
        //SE AGRUPAN LOS VALORES Y SE REALIZA EL CONTEO
        //------------------------------------------------------------------                
        sql = ""
                + " SELECT  \n\r"
                + "	column_1, \n\r"
                + "	column_2, \n\r"
                + "	column_3, \n\r"
                + "     count(*)  \n\r"
                + " FROM \n\r"
                + "	indicators_records \n\r"
                + " WHERE \n\r"
                + "     user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n\r"
                + "     indicator_id = " + (currentIndicator.getIndicatorId() + 100) + " \n\r"
                + " GROUP BY \n\r"
                + "	column_1, \n\r"
                + "	column_2, \n\r"
                + "	column_3 \n\r"
                + " ORDER BY \n\r"
                + "	column_1, \n\r"
                + "	column_2, \n\r"
                + "	column_3 \n\r";
        ResultSet rs = connectionJdbcMB.consult(sql);
        try {//actualizo el valor count de los registros currentIndicator.getIndicatorId() apartir de  currentIndicator.getIndicatorId()+100
            while (rs.next()) {
                sql = " "
                        + " UPDATE \n\r"
                        + "    indicators_records \n\r"
                        + " SET \n\r"
                        + "    count = " + rs.getString("count") + " \n\r"
                        + " WHERE \n\r"
                        + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n\r"
                        + "    indicator_id = " + currentIndicator.getIndicatorId() + " AND \n\r"
                        + "    column_1 like '" + rs.getString("column_1") + "' AND \n\r"
                        + "    column_2 like '" + rs.getString("column_2") + "' AND \n\r"
                        + "    column_3 like '" + rs.getString("column_3") + "' \n\r";
                connectionJdbcMB.non_query(sql);
            }
            sql = ""
                    + " DELETE FROM \n\r"
                    + "    indicators_records \n\r"
                    + " WHERE \n\r"
                    + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n\r"
                    + "    indicator_id = " + (currentIndicator.getIndicatorId() + 100) + " \n\r";
            connectionJdbcMB.non_query(sql);//elimino los valores del indicador 100
        } catch (Exception e) {
            System.out.println("Error 2 en " + this.getClass().getName() + ":" + e.toString());
        }
    }

    private void separateRecordsFunction() {
        ResultSet rs;
        ResultSet rs2;
        String valueColumn;
        String[] splitRegisters;
        String[] splitForSql;
        try {
            //determinar el maximo
            sql = ""
                    + " SELECT  \n\r"
                    + "	MAX(record_id) \n\r"
                    + " FROM \n\r"
                    + "	indicators_records \n\r"
                    + " WHERE \n\r"
                    + "     user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n\r"
                    + "     indicator_id = " + (currentIndicator.getIndicatorId() + 100) + " \n\r";
            rs = connectionJdbcMB.consult(sql);
            rs.next();
            tuplesProcessed = rs.getInt(1);
            sql = ""
                    + " SELECT  \n\r"
                    + "	* \n\r"
                    + " FROM \n\r"
                    + "	indicators_records \n\r"
                    + " WHERE \n\r"
                    + "     user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n\r"
                    + "     indicator_id = " + (currentIndicator.getIndicatorId() + 100) + " \n\r";
            cpManager = new CopyManager((BaseConnection) connectionJdbcMB.getConn());
            for (int col = 1; col < 4; col++) {//se repite tres veces por cada una de las columnas
                sb = new StringBuilder();
                rs = connectionJdbcMB.consult(sql);
                while (rs.next()) {
                    if (rs.getString("column_" + col).indexOf("<=>") != -1) {
                        valueColumn = rs.getString("column_" + col);
                        valueColumn = valueColumn.substring(1, valueColumn.length() - 1);
                        splitRegisters = valueColumn.split(",");
                        for (int i = 0; i < splitRegisters.length; i++) {
                            splitForSql = splitRegisters[i].split("<=>");
                            if (splitForSql.length == 4) {
                                tuplesProcessed++;
                                if (splitForSql[1].compareTo("abuse_types") == 0) {
                                    //para el caso de "tipos de maltrato"="abuse_types" se debe agrupar valores
                                    if (splitForSql[3].compareTo("9") == 0 //        9;"PORNOGRAFIA CON NNA"
                                            || splitForSql[3].compareTo("10") == 0// 10;"TRATA DE PERSONAL PARA EXPLOTACION SEXUAL"
                                            || splitForSql[3].compareTo("11") == 0// 11;"ABUSO SEXUAL"
                                            || splitForSql[3].compareTo("12") == 0// 12;"ACOSO SEXUAL"
                                            || splitForSql[3].compareTo("13") == 0// 13;"ASALTO SEXUAL"
                                            || splitForSql[3].compareTo("14") == 0// 14;"EXPLOTACION SEXUAL"
                                            || splitForSql[3].compareTo("15") == 0)//15;"TURISMO SEXUAL"
                                    {
                                        splitForSql[3] = "3";//3;"VIOLENCIA SEXUAL"
                                    }
                                }
                                rs2 = connectionJdbcMB.consult("SELECT " + splitForSql[0] + " FROM " + splitForSql[1] + " WHERE " + splitForSql[2] + " = " + splitForSql[3]);
                                rs2.next();
                                if (col == 1) {
                                    sb.
                                            append(rs.getInt("user_id")).append("\t"). //  user_id integer NOT NULL, -- usuario que genera el indicador
                                            append(rs.getInt("indicator_id")).append("\t"). //  indicator_id integer NOT NULL, -- identificador del indicador
                                            append(tuplesProcessed).append("\t"). //  record_id integer NOT NULL, -- identificador del registro para ordenamiento
                                            append(rs2.getString(1)).append("\t"). //  column_1 text, -- valor de la primer variable cruzada
                                            append(rs.getString("column_2")).append("\t"). //  column_2 text, -- valor de la segunda variable cruzada
                                            append(rs.getString("column_3")).append("\t"). //  column_3 text, -- valor de la tercer variable cruzada
                                            append(0).append("\t"). //  count integer, -- numero de coincidencias para este cruce
                                            append(0).append("\n");                             //  population integer,
                                }
                                if (col == 2) {
                                    sb.
                                            append(rs.getInt("user_id")).append("\t"). //  user_id integer NOT NULL, -- usuario que genera el indicador
                                            append(rs.getInt("indicator_id")).append("\t"). //  indicator_id integer NOT NULL, -- identificador del indicador
                                            append(tuplesProcessed).append("\t"). //  record_id integer NOT NULL, -- identificador del registro para ordenamiento
                                            append(rs.getString("column_1")).append("\t"). //  column_1 text, -- valor de la primer variable cruzada
                                            append(rs2.getString(1)).append("\t"). //  column_2 text, -- valor de la segunda variable cruzada
                                            append(rs.getString("column_3")).append("\t"). //  column_3 text, -- valor de la tercer variable cruzada
                                            append(0).append("\t"). //  count integer, -- numero de coincidencias para este cruce
                                            append(0).append("\n");                             //  population integer,
                                }
                                if (col == 3) {
                                    sb.
                                            append(rs.getInt("user_id")).append("\t"). //  user_id integer NOT NULL, -- usuario que genera el indicador
                                            append(rs.getInt("indicator_id")).append("\t"). //  indicator_id integer NOT NULL, -- identificador del indicador
                                            append(tuplesProcessed).append("\t"). //  record_id integer NOT NULL, -- identificador del registro para ordenamiento
                                            append(rs.getString("column_1")).append("\t"). //  column_1 text, -- valor de la primer variable cruzada
                                            append(rs.getString("column_2")).append("\t"). //  column_2 text, -- valor de la segunda variable cruzada
                                            append(rs2.getString(1)).append("\t"). //  column_3 text, -- valor de la tercer variable cruzada
                                            append(0).append("\t"). //  count integer, -- numero de coincidencias para este cruce
                                            append(0).append("\n");                             //  population integer,
                                }
                            }
                        }
                    }
                }
                //REALIZO LA INSERCION
                cpManager.copyIn("COPY indicators_records FROM STDIN", new StringReader(sb.toString()));
                sb.delete(0, sb.length()); //System.out.println("Procesando... filas " + tuplesProcessed + " cargadas");
                //elimino registro que contengan 
                String sqlRemove = ""
                        + " DELETE  \n\r"
                        + " FROM \n\r"
                        + "	    indicators_records \n\r"
                        + " WHERE \n\r"
                        + "     user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n\r"
                        + "     indicator_id = " + (currentIndicator.getIndicatorId() + 100) + " AND \n\r"
                        + "     ( column_" + col + " like '%<=>%')";
                connectionJdbcMB.non_query(sqlRemove);
            }
        } catch (SQLException | IOException e) {
            System.out.println("Error 5 en " + this.getClass().getName() + ":" + e.toString());
        }
    }

    private void createCombinations() {
        //---------------------------------------------------------
        //FORMAR POSIBLES COMBINACIONES PARA QUE LOS DATOS QUEDEN ORDENADOS SEGUN COMO SE ENCUENTRE LA CONFIGURACION
        //---------------------------------------------------------
        columNames = new ArrayList<>();
        rowNames = new ArrayList<>();
        try {
            List<String> values1, values2, values3;
            //---------------------------------------------------------
            //REALIZO LAS POSIBLES COMBINACIONES
            //---------------------------------------------------------            

            cpManager = new CopyManager((BaseConnection) connectionJdbcMB.getConn());
            sb = new StringBuilder();
            tuplesProcessed = 0;
            int id = 0;
            if (variablesCrossData.size() == 1) {
                values1 = variablesCrossData.get(0).getValuesConfigured();
                for (int i = 0; i < values1.size(); i++) {
                    columNames.add(values1.get(i));
                    sb.
                            append(loginMB.getCurrentUser().getUserId()).append("\t").
                            append(currentIndicator.getIndicatorId()).append("\t").
                            append(id).append("\t").
                            append(values1.get(i)).append("\t").
                            append("-").append("\t").
                            append("-").append("\t").
                            append(0).append("\t").
                            append(0).append("\n");
                    id++;

                }
                rowNames.add("Valor");
            } else if (variablesCrossData.size() == 2) {
                values1 = variablesCrossData.get(0).getValuesConfigured();
                values2 = variablesCrossData.get(1).getValuesConfigured();
                for (int i = 0; i < values2.size(); i++) {
                    rowNames.add(values2.get(i));
                    for (int j = 0; j < values1.size(); j++) {
                        if (i == 0) {
                            columNames.add(values1.get(j));
                        }
                        sb.
                                append(loginMB.getCurrentUser().getUserId()).append("\t").
                                append(currentIndicator.getIndicatorId()).append("\t").
                                append(id).append("\t").
                                append(values1.get(j)).append("\t").
                                append(values2.get(i)).append("\t").
                                append("-").append("\t").
                                append(0).append("\t").
                                append(0).append("\n");
                        id++;
                    }
                }
            } else if (variablesCrossData.size() == 3) {
                values1 = variablesCrossData.get(0).getValuesConfigured();
                values2 = variablesCrossData.get(1).getValuesConfigured();
                values3 = variablesCrossData.get(2).getValuesConfigured();
                for (int i = 0; i < values2.size(); i++) {
                    for (int j = 0; j < values1.size(); j++) {
                        columNames.add(values1.get(j) + "}" + values2.get(i));
                        for (int k = 0; k < values3.size(); k++) {
                            if (i == 0 && j == 0) {
                                rowNames.add(values3.get(k));
                            }
                            sb.
                                    append(loginMB.getCurrentUser().getUserId()).append("\t").
                                    append(currentIndicator.getIndicatorId()).append("\t").
                                    append(id).append("\t").
                                    append(values1.get(j)).append("\t").
                                    append(values2.get(i)).append("\t").
                                    append(values3.get(k)).append("\t").
                                    append(0).append("\t").
                                    append(0).append("\n");
                            id++;
                        }
                    }
                }
            }
            //REALIZO LA INSERCION
            cpManager.copyIn("COPY indicators_records FROM STDIN", new StringReader(sb.toString()));
            sb.delete(0, sb.length()); //System.out.println("Procesando... filas " + tuplesProcessed + " cargadas");
        } catch (SQLException | IOException e) {
            System.out.println("Error 4 en " + this.getClass().getName() + ":" + e.toString());
        }
    }

    private void saveIndicatorRecords(String sqlConsult) {
        //------------------------------------------------------------------
        //AGEGAR UNA CONSULTA A LA TABLA indicators_records 
        //------------------------------------------------------------------
        try {
            cpManager = new CopyManager((BaseConnection) connectionJdbcMB.getConn());
            ResultSet rs = connectionJdbcMB.consult(sqlConsult);
            sb = new StringBuilder();
            tuplesProcessed = 0;
            int ncol = rs.getMetaData().getColumnCount();
            if (addAbuseTypes) {
                ncol--;//SE QUITA LA COLUMNA DE NATURALEZA VIOLENCIA
            }
            boolean haveNulls;
            while (rs.next()) {
                haveNulls = false;
                for (int i = 1; i <= ncol; i++) {//agrego solo los que no tengan valores nulos
                    if (rs.getString(i) == null || rs.getString(i).length() == 0 || rs.getString(i).compareToIgnoreCase("null") == 0) {
                        haveNulls = true;
                    }
                }
                if (!haveNulls) {//si no tiene nullos es agregado
                    tuplesProcessed++;
                    sb.
                            append(loginMB.getCurrentUser().getUserId()).append("\t").
                            append(currentIndicator.getIndicatorId() + 100).append("\t").
                            append(tuplesProcessed).append("\t");

                    for (int i = 1; i <= ncol; i++) {//datos del cruce
                        sb.append(rs.getString(i)).append("\t");
                    }
                    for (int i = 0; i < 3 - ncol; i++) {//variables no usadas(vacias)
                        sb.append("-").append("\t");
                    }
                    sb.append(0).append("\t").append(0).append("\n");//count y poblacion quedan como cero                    
                }
            }
            //REALIZO LA INSERCION
            cpManager.copyIn("COPY indicators_records FROM STDIN", new StringReader(sb.toString()));
            sb.delete(0, sb.length()); //System.out.println("Procesando... filas " + tuplesProcessed + " cargadas");
        } catch (SQLException | IOException e) {
            System.out.println("Error 5 en " + this.getClass().getName() + ":" + e.toString());
        }
    }

    private void addToSourceTable(String tableName) {
        if (tableName.indexOf("sivigila_event") == 0 && sourceTable.indexOf("sivigila_event") == -1) {
            sourceTable = sourceTable + ", sivigila_event ";
            filterSourceTable = filterSourceTable + " non_fatal_injuries.non_fatal_injury_id = sivigila_event.non_fatal_injury_id AND \n\r";
        }
        if (tableName.indexOf("sivigila_aggresor") == 0 && sourceTable.indexOf("sivigila_aggresor") == -1) {
            sourceTable = sourceTable + ", sivigila_aggresor ";
            filterSourceTable = filterSourceTable + " sivigila_aggresor.sivigila_agresor_id = sivigila_event.sivigila_agresor_id AND \n\r";
        }
        if (tableName.indexOf("sivigila_victim") == 0 && sourceTable.indexOf("sivigila_victim") == -1) {
            sourceTable = sourceTable + ", sivigila_victim ";
            filterSourceTable = filterSourceTable + " sivigila_victim.sivigila_victim_id = sivigila_event.sivigila_victim_id AND \n\r";
        }
        if (tableName.indexOf("fatal_injury_murder") == 0 && sourceTable.indexOf("fatal_injury_murder") == -1) {
            sourceTable = sourceTable + ", fatal_injury_murder ";
            filterSourceTable = filterSourceTable + " fatal_injury_murder.fatal_injury_id = fatal_injuries.fatal_injury_id AND \n\r";
        }
        if (tableName.indexOf("fatal_injury_traffic") == 0 && sourceTable.indexOf("fatal_injury_traffic") == -1) {
            sourceTable = sourceTable + ", fatal_injury_traffic ";
            filterSourceTable = filterSourceTable + " fatal_injury_traffic.fatal_injury_id = fatal_injuries.fatal_injury_id AND \n\r";
        }
        if (tableName.indexOf("fatal_injury_suicide") == 0 && sourceTable.indexOf("fatal_injury_suicide") == -1) {
            sourceTable = sourceTable + ", fatal_injury_suicide ";
            filterSourceTable = filterSourceTable + " fatal_injury_suicide.fatal_injury_id = fatal_injuries.fatal_injury_id AND \n\r";
        }
        if (tableName.indexOf("fatal_injury_accident") == 0 && sourceTable.indexOf("fatal_injury_accident") == -1) {
            sourceTable = sourceTable + ", fatal_injury_accident ";
            filterSourceTable = filterSourceTable + " fatal_injury_accident.fatal_injury_id = fatal_injuries.fatal_injury_id AND \n\r";
        }
        if (tableName.indexOf("non_fatal_interpersonal") == 0 && sourceTable.indexOf("non_fatal_interpersonal") == -1) {
            sourceTable = sourceTable + ", non_fatal_interpersonal ";
            filterSourceTable = filterSourceTable + " non_fatal_interpersonal.non_fatal_injury_id = non_fatal_injuries.non_fatal_injury_id AND \n\r";
        }
        if (tableName.indexOf("non_fatal_self_inflicted") == 0 && sourceTable.indexOf("non_fatal_self_inflicted") == -1) {
            sourceTable = sourceTable + ", non_fatal_self_inflicted ";
            filterSourceTable = filterSourceTable + " non_fatal_self_inflicted.non_fatal_injury_id = non_fatal_injuries.non_fatal_injury_id AND \n\r";
        }
        if (tableName.indexOf("non_fatal_transport") == 0 && sourceTable.indexOf("non_fatal_transport") == -1) {
            sourceTable = sourceTable + ", non_fatal_transport ";
            filterSourceTable = filterSourceTable + " non_fatal_transport.non_fatal_injury_id = non_fatal_injuries.non_fatal_injury_id AND \n\r";
        }
        if (tableName.indexOf("non_fatal_domestic_violence") == 0 && sourceTable.indexOf("non_fatal_domestic_violence") == -1) {
            sourceTable = sourceTable + ", non_fatal_domestic_violence ";
            filterSourceTable = filterSourceTable + " non_fatal_domestic_violence.non_fatal_injury_id = non_fatal_injuries.non_fatal_injury_id AND \n\r";
        }
        if (tableName.indexOf("counterpart_service_type") == 0 && sourceTable.indexOf("counterpart_service_type") == -1) {
            sourceTable = sourceTable + ", counterpart_service_type ";
            filterSourceTable = filterSourceTable + " counterpart_service_type.fatal_injury_id = fatal_injuries.fatal_injury_id AND \n\r";
        }

    }

    private String createCase(String sqlReturn, String source_table, String column_whit_name, String category_table, String column_whit_id, String as_name) {
        String strReturn = sqlReturn + ""
                + " CASE \n\r"
                + "    WHEN " + source_table + " is null THEN 'SIN DATO' \n\r"
                + "    ELSE \n\r"
                + "    ( \n\r"
                + "       SELECT \n\r"
                + "          " + column_whit_name + " \n\r"
                + "       FROM \n\r"
                + "          " + category_table + " \n\r"
                + "       WHERE \n\r"
                + "          " + column_whit_id + " = " + source_table + " \n\r"
                + "    )"
                + " END AS " + as_name;
        return strReturn;
    }

    private String createIndicatorConsult() {

        String sqlReturn = "";
        if (currentIndicator.getIndicatorId() == 71 || currentIndicator.getIndicatorId() == 75) {
            //CASOS DE VIOLENCIA SEXUAL (VSX) EN EL SECTOR SALUD
            //PORCENTAJE DE CASOS DE SIVIGILA_VSX            
            sqlReturn = " "
                    + " SELECT *\n"
                    + " FROM \n"
                    + " ( \n";
        }
        sqlReturn = sqlReturn + " SELECT  \n\r";
        separateRecords = false;
        sourceTable = "";//tabla adicional que se usara en la seccion "FROM" de la consulta sql
        filterSourceTable = "";//filtro adicional usado en la "WHERE" de la consulta sql
        ResultSet rs = null;
        for (int i = 0; i < variablesCrossData.size(); i++) {
            switch (VariablesEnum.convert(variablesCrossData.get(i).getGeneric_table())) {//nombre de variable 
                case temporalDisaggregation://DETERMINAR LA DESAGREGACION TEMPORAL -----------------------                   
                    sqlReturn = sqlReturn + "   CASE \n\r";
                    for (int j = 0; j < variablesCrossData.get(i).getValuesId().size(); j++) {
                        String[] splitDates = variablesCrossData.get(i).getValuesId().get(j).split("}");
                        sqlReturn = sqlReturn + "       WHEN ( \n\r";
                        sqlReturn = sqlReturn + "           " + currentIndicator.getInjuryType() + ".injury_date >= to_date('" + splitDates[0] + "','dd/MM/yyyy') AND \n\r";
                        sqlReturn = sqlReturn + "           " + currentIndicator.getInjuryType() + ".injury_date <= to_date('" + splitDates[1] + "','dd/MM/yyyy') \n\r";
                        sqlReturn = sqlReturn + "       ) THEN '" + variablesCrossData.get(i).getValues().get(j) + "'  \n\r";
                    }
                    sqlReturn = sqlReturn + "   END AS fecha";
                    break;
                case injuries_fatal://TIPO DE LESION FATAL-----------------------
                    sqlReturn = sqlReturn + "   CASE (SELECT injury_id FROM injuries WHERE injury_id=" + currentIndicator.getInjuryType() + ".injury_id) \n\r";
                    for (int j = 0; j < variablesCrossData.get(i).getValues().size(); j++) {
                        sqlReturn = sqlReturn + "       WHEN '" + variablesCrossData.get(i).getValuesId().get(j) + "' THEN '" + variablesCrossData.get(i).getValues().get(j) + "'  \n\r";
                    }
                    sqlReturn = sqlReturn + "   END AS tipo_lesion";
                    break;
                case injuries_non_fatal://TIPO DE LESION NO FATAL-----------------------
                    sqlReturn = sqlReturn + "   CASE (SELECT injury_id FROM injuries WHERE injury_id=" + currentIndicator.getInjuryType() + ".injury_id) \n\r";
                    for (int j = 0; j < variablesCrossData.get(i).getValues().size(); j++) {
                        sqlReturn = sqlReturn + "       WHEN '" + variablesCrossData.get(i).getValuesId().get(j) + "' THEN '" + variablesCrossData.get(i).getValues().get(j) + "'  \n\r";
                    }
                    sqlReturn = sqlReturn + "       WHEN '55' THEN 'VIOLENCIA INTRAFAMILIAR'  \n\r";
                    sqlReturn = sqlReturn + "       WHEN '56' THEN 'VIOLENCIA INTRAFAMILIAR'  \n\r";
                    sqlReturn = sqlReturn + "   END AS tipo_lesion";
                    break;
                case age://DETERMINAR EDAD -----------------------          
                    if (variablesCrossData.get(i).getSource_table().compareTo("victims.victim_age") == 0) {
                        sqlReturn = sqlReturn + "   CASE \n\r";
                        sqlReturn = sqlReturn + "       WHEN (victims.victim_age is null)  THEN 'SIN DATO' \n\r";
                        for (int j = 0; j < variablesCrossData.get(i).getValuesConfigured().size(); j++) {
                            if (variablesCrossData.get(i).getValuesConfigured().get(j).compareTo("SIN DATO") != 0) {
                                String[] splitAge = variablesCrossData.get(i).getValuesConfigured().get(j).split("/");
                                if (splitAge[1].compareTo("n") == 0) {
                                    splitAge[1] = "200";
                                }
                                sqlReturn = sqlReturn + ""
                                        + "       WHEN (( \n\r"
                                        + "           CASE \n\r"
                                        + "               WHEN (victims.age_type_id = 2 or victims.age_type_id = 3) THEN 1 \n\r"
                                        + "               WHEN (victims.age_type_id = 1 or victims.age_type_id is null) THEN victims.victim_age \n\r"
                                        + "           END \n\r"
                                        + "       ) between " + splitAge[0] + " and " + splitAge[1] + ") THEN '" + variablesCrossData.get(i).getValuesConfigured().get(j) + "'  \n\r";
                            }
                        }
                        sqlReturn = sqlReturn + "   END AS edad";
                    }
                    if (variablesCrossData.get(i).getSource_table().compareTo("sivigila_aggresor.age") == 0) {
                        addToSourceTable("sivigila_event");
                        addToSourceTable("sivigila_aggresor");
                        sqlReturn = sqlReturn + "   CASE \n\r";
                        sqlReturn = sqlReturn + "       WHEN (sivigila_aggresor.age is null)  THEN 'SIN DATO' \n\r";
                        for (int j = 0; j < variablesCrossData.get(i).getValuesConfigured().size(); j++) {
                            if (variablesCrossData.get(i).getValuesConfigured().get(j).compareTo("SIN DATO") != 0) {
                                String[] splitAge = variablesCrossData.get(i).getValuesConfigured().get(j).split("/");
                                if (splitAge[1].compareTo("n") == 0) {
                                    splitAge[1] = "200";
                                }
                                sqlReturn = sqlReturn + ""
                                        + "       WHEN ( sivigila_aggresor.age between " + splitAge[0] + " and " + splitAge[1] + ") THEN '" + variablesCrossData.get(i).getValuesConfigured().get(j) + "'  \n\r";
                            }
                        }
                        sqlReturn = sqlReturn + "   END AS edad_agresor";
                    }
                    break;
                case hour://HORA -----------------------
                    sqlReturn = sqlReturn + ""
                            + "   CASE \n\r"
                            + "       WHEN (" + currentIndicator.getInjuryType() + ".injury_time is null)  THEN 'SIN DATO' \n\r";
                    for (int j = 0; j < variablesCrossData.get(i).getValuesConfigured().size(); j++) {
                        if (variablesCrossData.get(i).getValuesConfigured().get(j).compareTo("SIN DATO") != 0) {
                            String[] splitAge = variablesCrossData.get(i).getValuesConfigured().get(j).split("/");
                            String[] splitAge2 = splitAge[0].split(":");
                            String[] splitAge3 = splitAge[1].split(":");
                            sqlReturn = sqlReturn + ""
                                    + "       WHEN (extract(hour from " + currentIndicator.getInjuryType() + ".injury_time) \n\r"
                                    + "       between " + splitAge2[0] + " and " + splitAge3[0] + ") THEN '" + variablesCrossData.get(i).getValuesConfigured().get(j) + "'  \n\r";
                        }
                    }
                    sqlReturn = sqlReturn + "   END AS hora";
                    break;
                case neighborhoods://NOMBRE DEL BARRIO -----------------------
                    sqlReturn = sqlReturn + ""
                            + "   CASE \n\r"
                            + "      WHEN " + currentIndicator.getInjuryType() + ".injury_neighborhood_id is null THEN 'SIN DATO' \n\r"
                            + "      ELSE (SELECT neighborhood_name FROM neighborhoods WHERE neighborhood_id = " + currentIndicator.getInjuryType() + ".injury_neighborhood_id) \n\r"
                            //+ "           AND neighborhood_area != 2) \n\r"
                            + "   END AS barrio";
                    break;
                case communes://COMUNA -----------------------
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + currentIndicator.getInjuryType() + ".injury_neighborhood_id is null THEN 'SIN DATO' \n\r"
                            + "    ELSE \n\r"
                            + "    ( \n\r"
                            + "       SELECT \n\r"
                            + "          communes.commune_name \n\r"
                            + "       FROM \n\r"
                            + "          public.communes, \n\r"
                            + "          public.neighborhoods \n\r"
                            + "       WHERE \n\r"
                            + "          neighborhoods.neighborhood_suburb = communes.commune_id AND \n\r"
                            + "          neighborhoods.neighborhood_id=" + currentIndicator.getInjuryType() + ".injury_neighborhood_id \n\r"
                            + "    )"
                            + " END AS comuna";
                    break;
                case quadrants://CUADRANTE -----------------------
                    //sqlReturn = sqlReturn + "   CAST((SELECT neighborhood_quadrant FROM neighborhoods WHERE neighborhood_id=" + currentIndicator.getInjuryType() + ".injury_neighborhood_id) as text) as cuadrante \n\r";
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + currentIndicator.getInjuryType() + ".injury_neighborhood_id is null THEN 'SIN DATO' \n\r"
                            + "    ELSE \n\r"
                            + "    ( \n\r"
                            + "       SELECT \n\r"
                            + "          quadrants.quadrant_name \n\r"
                            + "       FROM \n\r"
                            + "          public.quadrants, \n\r"
                            + "          public.neighborhoods \n\r"
                            + "       WHERE \n\r"
                            + "          neighborhoods.neighborhood_quadrant = quadrants.quadrant_id AND \n\r"
                            + "          neighborhoods.neighborhood_id=" + currentIndicator.getInjuryType() + ".injury_neighborhood_id \n\r"
                            + "    )"
                            + " END AS cuadrante";
                    break;
                case corridors://CORREDOR -----------------------
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + currentIndicator.getInjuryType() + ".injury_neighborhood_id is null THEN 'SIN DATO' \n\r"
                            + "    ELSE \n\r"
                            + "    ( \n\r"
                            + "       SELECT \n\r"
                            + "          corridors.corridor_name \n\r"
                            + "       FROM \n\r"
                            + "          public.corridors, \n\r"
                            + "          public.neighborhoods \n\r"
                            + "       WHERE \n\r"
                            + "          neighborhoods.neighborhood_corridor = corridors.corridor_id AND \n\r"
                            + "          neighborhoods.neighborhood_id=" + currentIndicator.getInjuryType() + ".injury_neighborhood_id \n\r"
                            + "    )"
                            + " END AS corredor";
                    break;
                case areas://ZONA -----------------------        
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + currentIndicator.getInjuryType() + ".injury_neighborhood_id is null THEN 'SIN DATO' \n\r"
                            + "    ELSE \n\r"
                            + "    ( \n\r"
                            + "       SELECT \n\r"
                            + "          areas.area_name \n\r"
                            + "       FROM \n\r"
                            + "          public.areas, \n\r"
                            + "          public.neighborhoods \n\r"
                            + "       WHERE \n\r"
                            + "          neighborhoods.neighborhood_area = areas.area_id AND \n\r"
                            + "          neighborhoods.neighborhood_id=" + currentIndicator.getInjuryType() + ".injury_neighborhood_id \n\r"
                            + "    )"
                            + " END AS zona";
                    break;
                case genders://GENERO  ----------------------
                    if (variablesCrossData.get(i).getSource_table().compareTo("sivigila_aggresor.gender") == 0) {
                        addToSourceTable("sivigila_event");
                        addToSourceTable("sivigila_aggresor");
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "gender_name", "genders", "gender_id", "genero_agresor");
                    }
                    if (variablesCrossData.get(i).getSource_table().compareTo("victims.gender_id") == 0) {
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "gender_name", "genders", "gender_id", "genero_victima");
                    }
                    break;
                case days://DIA SEMANA ---------------------
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + currentIndicator.getInjuryType() + ".injury_day_of_week is null THEN 'SIN DATO' \n\r"
                            + "    ELSE ( " + currentIndicator.getInjuryType() + ".injury_day_of_week )"
                            + " END AS dia_semana";
                    break;
                case year://AÑO -----------------------
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + currentIndicator.getInjuryType() + ".injury_date is null THEN 'SIN DATO' \n\r"
                            + "    ELSE ( CAST(extract(year from " + currentIndicator.getInjuryType() + ".injury_date)::int as text) )"
                            + " END AS anyo";
                    break;
                case month://MES 
                    sqlReturn = sqlReturn + ""
                            + " CASE \n\r"
                            + "    WHEN " + currentIndicator.getInjuryType() + ".injury_date is null THEN 'SIN DATO' \n\r"
                            + "    ELSE \n\r"
                            + "    ( \n\r"
                            + "       SELECT \n\r"
                            + "          months.month_name \n\r"
                            + "       FROM \n\r"
                            + "          public.months \n\r"
                            + "       WHERE \n\r"
                            + "          (extract(month from " + currentIndicator.getInjuryType() + ".injury_date)::int) = months.month_id \n\r"
                            + "    )"
                            + " END AS mes";
                    break;
                case sivigila_educational_level://escolaridad agresor
                    if (variablesCrossData.get(i).getSource_table().compareTo("sivigila_aggresor.educational_level_id") == 0) {
                        addToSourceTable("sivigila_event");
                        addToSourceTable("sivigila_aggresor");
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "sivigila_educational_level_name", "sivigila_educational_level", "sivigila_educational_level_id", "escolaridad_agresor");
                    }
                    if (variablesCrossData.get(i).getSource_table().compareTo("sivigila_victim.educational_level_id") == 0) {
                        addToSourceTable("sivigila_event");
                        addToSourceTable("sivigila_victim");
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "sivigila_educational_level_name", "sivigila_educational_level", "sivigila_educational_level_id", "escolaridad_victima");
                    }
                    break;
                case sivigila_vulnerability://factor de vulnerabilidad
                    addToSourceTable("sivigila_event");
                    addToSourceTable("sivigila_victim");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "sivigila_vulnerability_name", "sivigila_vulnerability", "sivigila_vulnerability_id", "factor_vulnerabilidad");
                    break;
                case sivigila_group://";"grupo agresor"
                    addToSourceTable("sivigila_event");
                    addToSourceTable("sivigila_aggresor");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "sivigila_group_name", "sivigila_group", "sivigila_group_id", "grupo_agresor");
                    break;
                case degree:
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "name_degree", "degree", "degree_id", "grado_quemadura");
                    break;
                case kinds_of_injury://RELACION DE UNO A MUCHOS
                    separateRecords = true;
                    sqlReturn = sqlReturn + ""
                            + " ( \n"
                            + "   SELECT \n"
                            + "	     cast(array_agg('kind_injury_name<=>kinds_of_injury<=>kind_injury_id<=>'||kind_injury_id) as text \n)"
                            + "   FROM \n"
                            + "      non_fatal_kind_of_injury \n"
                            + "   WHERE \n"
                            + "      non_fatal_kind_of_injury.non_fatal_injury_id=non_fatal_injuries.non_fatal_injury_id ) as naturaleza_lesion \n";
                    break;
                case anatomical_locations://RELACION DE UNO A MUCHOS
                    separateRecords = true;
                    sqlReturn = sqlReturn + ""
                            + " ( \n"
                            + "   SELECT \n"
                            + "	     cast(array_agg('anatomical_location_name<=>anatomical_locations<=>anatomical_location_id<=>'||anatomical_location_id) as text \n)"
                            + "   FROM \n"
                            + "      non_fatal_anatomical_location \n"
                            + "   WHERE \n"
                            + "      non_fatal_anatomical_location.non_fatal_injury_id=non_fatal_injuries.non_fatal_injury_id ) as sitio_anatomico \n";
                    break;


                case actions_to_take://RELACION DE UNO A MUCHOS
                    separateRecords = true;
                    sqlReturn = sqlReturn + ""
                            + " ( \n"
                            + "   SELECT \n"
                            + "	     cast(array_agg('action_name<=>actions_to_take<=>action_id<=>'||action_id) as text \n)"
                            + "   FROM \n"
                            + "      domestic_violence_action_to_take \n"
                            + "   WHERE \n"
                            + "      domestic_violence_action_to_take.non_fatal_injury_id=non_fatal_injuries.non_fatal_injury_id ) as acciones_a_realizar \n";
                    break;
                case security_elements://RELACION DE UNO A MUCHOS
                    separateRecords = true;
                    sqlReturn = sqlReturn + ""
                            + " ( \n"
                            + "   SELECT \n"
                            + "	     cast(array_agg('security_element_name<=>security_elements<=>security_element_id<=>'||security_element_id) as text \n)"
                            + "   FROM \n"
                            + "      non_fatal_transport_security_element \n"
                            + "   WHERE \n"
                            + "      non_fatal_transport_security_element.non_fatal_injury_id=non_fatal_injuries.non_fatal_injury_id ) as elementos_seguridad \n";
                    break;
                case vulnerable_groups://";"grupo poblacional"//RELACION DE UNO A MUCHOS
                    separateRecords = true;
                    sqlReturn = sqlReturn + ""
                            + " ( \n"
                            + "   SELECT \n"
                            + "	     cast(array_agg('vulnerable_group_name<=>vulnerable_groups<=>vulnerable_group_id<=>'||vulnerable_group_id) as text \n)"
                            + "   FROM \n"
                            + "      victim_vulnerable_group \n"
                            + "   WHERE \n"
                            + "      victim_vulnerable_group.victim_id=victims.victim_id ) as grupo_poblacional \n";
                    break;
                case abuse_types://";"naturaleza violencia" //RELACION DE UNO A MUCHOS
                    separateRecords = true;
                    sqlReturn = sqlReturn + ""
                            + " ( \n"
                            + "   SELECT \n"
                            + "	     cast(array_agg('abuse_type_name<=>abuse_types<=>abuse_type_id<=>'||abuse_type_id) as text \n)"
                            + "   FROM \n"
                            + "      domestic_violence_abuse_type \n"
                            + "   WHERE \n"
                            + "      domestic_violence_abuse_type.non_fatal_injury_id=non_fatal_injuries.non_fatal_injury_id ) as naturaleza_violencia \n";
                    break;
                case ethnic_groups://";"pertenecia étnica"
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "ethnic_group_name", "ethnic_groups", "ethnic_group_id", "grupo_etnico");
                    break;
                case aggressor_types:
                    //tipo agresor vif
                    if (variablesCrossData.get(i).getSource_table().compareTo("domestic_violence_aggressor_type") == 0) {
                        separateRecords = true;
                        sqlReturn = sqlReturn + ""
                                + " ( \n"
                                + "   SELECT \n"
                                + "	     cast(array_agg('aggressor_type_name<=>aggressor_types<=>aggressor_type_id<=>'||aggressor_type_id) as text \n)"
                                + "   FROM \n"
                                + "      domestic_violence_aggressor_type \n"
                                + "   WHERE \n"
                                + "      domestic_violence_aggressor_type.non_fatal_injury_id=non_fatal_injuries.non_fatal_injury_id ) as tipo_agresor \n";
                    }
                    //relacion familiar victima sivigila"
                    if (variablesCrossData.get(i).getSource_table().compareTo("sivigila_aggresor.relative_id") == 0) {
                        addToSourceTable("sivigila_event");
                        addToSourceTable("sivigila_aggresor");
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "aggressor_type_name", "aggressor_types", "aggressor_type_id", "relacion_familiar_victima");
                    }
                    break;
                case sivigila_no_relative://";"relación no familiar víctima"
                    addToSourceTable("sivigila_event");
                    addToSourceTable("sivigila_aggresor");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "sivigila_no_relative_name", "sivigila_no_relative", "sivigila_no_relative_id", "relacion_no_familiar_victima");
                    break;
                case sivigila_tip_ss://";"tipo régimen"                    
                    addToSourceTable("sivigila_event");
                    addToSourceTable("sivigila_victim");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "sivigila_tip_ss_name", "sivigila_tip_ss", "sivigila_tip_ss_id", "tipo_regimen");
                    break;
                case public_health_actions://acciones en salud publica //RELACION DE UNO A MUCHOS
                    separateRecords = true;
                    sqlReturn = sqlReturn + ""
                            + " ( \n"
                            + "   SELECT \n"
                            + "	     cast(array_agg('action_name<=>public_health_actions<=>action_id<=>'||action_id) as text \n)"
                            + "   FROM \n"
                            + "      sivigila_event_public_health \n"
                            + "   WHERE \n"
                            + "      sivigila_event_public_health.non_fatal_injury_id=non_fatal_injuries.non_fatal_injury_id ) as aaciones_salud \n";
                    break;
                case jobs:
                    if (variablesCrossData.get(i).getSource_table().compareTo("sivigila_aggresor.occupation") == 0) {
                        addToSourceTable("sivigila_event");
                        addToSourceTable("sivigila_aggresor");
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "job_name", "jobs", "job_id", "ocupacion_agresor");
                    }
                    if (variablesCrossData.get(i).getSource_table().compareTo("victims.job_id") == 0) {
                        sqlReturn = createCase(sqlReturn, "victims.job_id", "job_name", "jobs", "job_id", "ocupacion_victima");
                    }
                    break;
                case sivigila_mechanism:
                    addToSourceTable("sivigila_event");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "sivigila_mechanism_name", "sivigila_mechanism", "sivigila_mechanism_id", "mecanismo_sivigila");
                    break;
                case insurance:
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "insurance_name", "insurance", "insurance_id", "aseguradora");
                    break;
                case contexts://(interpersonale en comunidad)
                    addToSourceTable("non_fatal_interpersonal");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "context_name", "contexts", "context_id", "contexto");
                    break;
                case aggressor_genders://(interpersonale en comunidad)
                    addToSourceTable("non_fatal_interpersonal");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "gender_name", "aggressor_genders", "gender_id", "sexo_agresor");
                    break;
                case relationships_to_victim://(interpersonale en comunidad)
                    addToSourceTable("non_fatal_interpersonal");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "relationship_victim_name", "relationships_to_victim", "relationship_victim_id", "relacion_agresor_victima");
                    break;
                case precipitating_factors://(violencia autoinflingida)
                    addToSourceTable("non_fatal_self_inflicted");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "precipitating_factor_name", "precipitating_factors", "precipitating_factor_id", "factor_precipitante");
                    break;
                case transport_users://(accidentes transito)
                    addToSourceTable("non_fatal_transport");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "transport_user_name", "transport_users", "transport_user_id", "tipo_usuario");
                    break;
                case transport_counterparts://(accidentes transito)
                    addToSourceTable("non_fatal_transport");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "transport_counterpart_name", "transport_counterparts", "transport_counterpart_id", "tipo_transporte_contraparte");
                    break;
                case transport_types://(accidentes transito)
                    addToSourceTable("non_fatal_transport");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "transport_type_name", "transport_types", "transport_type_id", "tipo_transporte_victima");
                    break;
                case destinations_of_patient://(Violencia Interpersonal en Familia)(lesiones en transporte)
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "destination_patient_name", "destinations_of_patient", "destination_patient_id", "destino_paciente");
                    break;
                case activities://(Violencia Interpersonal en Familia)(lesiones en transporte)
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "activity_name", "activities", "activity_id", "actividad_realizada");
                    break;
                case non_fatal_places://(Violencia Interpersonal en Familia)(lesiones en transporte)(interpersonal en comunidad)
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "non_fatal_place_name", "non_fatal_places", "non_fatal_place_id", "lugar_hecho");
                    break;
                case use_alcohol_drugs://(Violencia Interpersonal en Familia)(lesiones en transporte)                    
                    if (variablesCrossData.get(i).getSource_table().indexOf("use_alcohol_id") != -1) {
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "use_alcohol_drugs_name", "use_alcohol_drugs", "use_alcohol_drugs_id", "uso_alcohol");
                    }
                    if (variablesCrossData.get(i).getSource_table().indexOf("use_drugs_id") != -1) {
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "use_alcohol_drugs_name", "use_alcohol_drugs", "use_alcohol_drugs_id", "uso_drogas");
                    }
                    break;
                case mechanisms://(Violencia Interpersonal en Familia)(lesiones en transporte)
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "mechanism_name", "mechanisms", "mechanism_id", "mecanismo");
                    break;
                case protective_measures://(transito)
                    addToSourceTable("fatal_injury_traffic");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "protective_measures_name", "protective_measures", "protective_measures_id", "medidas_proteccion");
                    break;
                case victim_characteristics://(transito)
                    addToSourceTable("fatal_injury_traffic");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "characteristic_name", "victim_characteristics", "characteristic_id", "caracteristicas_victima");
                    break;
                case road_types://(transito)
                    addToSourceTable("fatal_injury_traffic");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "road_type_name", "road_types", "road_type_id", "tipo_via");
                    break;
//                case counterpart_service_type://(transito) //RELACION UNO A MUCHOS
//
//                    break;
                case service_types://(transito)
                    //contraparte
                    if (variablesCrossData.get(i).getSource_table().indexOf("counterpart_service_type.service_type_id") != -1) {
                        addToSourceTable("counterpart_service_type");
                        sqlReturn = sqlReturn + "   CASE (SELECT service_type_id  FROM counterpart_service_type  WHERE fatal_injury_id=" + currentIndicator.getInjuryType() + ".fatal_injury_id LIMIT 1)  \n\r";
                        for (int j = 0; j < variablesCrossData.get(i).getValues().size(); j++) {
                            sqlReturn = sqlReturn + "       WHEN '" + variablesCrossData.get(i).getValuesId().get(j) + "' THEN '" + variablesCrossData.get(i).getValues().get(j) + "'  \n\r";
                        }
                        sqlReturn = sqlReturn + "       ELSE 'SIN DATO' \n\r END AS servicio_contraparte";
                    }
                    //victima
                    if (variablesCrossData.get(i).getSource_table().indexOf("fatal_injury_traffic.service_type_id") != -1) {
                        addToSourceTable("fatal_injury_traffic");
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "service_type_name", "service_types", "service_type_id", "servicio_victima");
                    }
                    break;
                case involved_vehicles://(transito)
                    //contraparte
                    if (variablesCrossData.get(i).getSource_table().indexOf("counterpart_involved_vehicle.involved_vehicle_id") != -1) {
                        sqlReturn = sqlReturn + "   CASE (SELECT involved_vehicle_id  FROM counterpart_involved_vehicle  WHERE fatal_injury_id=" + currentIndicator.getInjuryType() + ".fatal_injury_id LIMIT 1)  \n\r";
                        for (int j = 0; j < variablesCrossData.get(i).getValues().size(); j++) {
                            sqlReturn = sqlReturn + "       WHEN '" + variablesCrossData.get(i).getValuesId().get(j) + "' THEN '" + variablesCrossData.get(i).getValues().get(j) + "'  \n\r";
                        }
                        sqlReturn = sqlReturn + "       ELSE 'SIN DATO' \n\r END AS vehiculo_contraparte";
                    }
                    //victima
                    if (variablesCrossData.get(i).getSource_table().indexOf("fatal_injury_traffic.involved_vehicle_id") != -1) {
                        addToSourceTable("fatal_injury_traffic");
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "involved_vehicle_name", "involved_vehicles", "involved_vehicle_id", "vehiculo_victima");
                    }
                    break;
                case accident_classes://(transito)
                    addToSourceTable("fatal_injury_traffic");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "accident_class_name", "accident_classes", "accident_class_id", "clase_accidente");
                    break;
                case boolean3:
                    if (variablesCrossData.get(i).getSource_table().compareTo("fatal_injury_suicide.previous_attempt") == 0) {
                        addToSourceTable("fatal_injury_suicide");
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "boolean_name", "boolean3", "boolean_id", "intento_previo");
                    }
                    if (variablesCrossData.get(i).getSource_table().compareTo("fatal_injury_suicide.mental_antecedent") == 0) {
                        addToSourceTable("fatal_injury_suicide");
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "boolean_name", "boolean3", "boolean_id", "antecedentes_mentales");
                    }
                    if (variablesCrossData.get(i).getSource_table().compareTo("non_fatal_self_inflicted.previous_attempt") == 0) {
                        addToSourceTable("non_fatal_self_inflicted");
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "boolean_name", "boolean3", "boolean_id", "intento_previo");
                    }
                    if (variablesCrossData.get(i).getSource_table().compareTo("non_fatal_self_inflicted.mental_antecedent") == 0) {
                        addToSourceTable("non_fatal_self_inflicted");
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "boolean_name", "boolean3", "boolean_id", "antecedentes_mentales");
                    }
                    if (variablesCrossData.get(i).getSource_table().compareTo("non_fatal_interpersonal.previous_antecedent") == 0) {
                        addToSourceTable("non_fatal_interpersonal");
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "boolean_name", "boolean3", "boolean_id", "antecedente_previo");
                    }
                    if (variablesCrossData.get(i).getSource_table().compareTo("sivigila_event.further_fieldwork") == 0) {
                        addToSourceTable("sivigila_event");
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "boolean_name", "boolean3", "boolean_id", "trabajo_campo");
                    }
                    if (variablesCrossData.get(i).getSource_table().compareTo("sivigila_victim.antecedent") == 0) {
                        addToSourceTable("sivigila_event");
                        addToSourceTable("sivigila_victim");
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "boolean_name", "boolean3", "boolean_id", "antecedentes_similares");
                    }
                    if (variablesCrossData.get(i).getSource_table().compareTo("sivigila_aggresor.live_together") == 0) {
                        addToSourceTable("sivigila_event");
                        addToSourceTable("sivigila_aggresor");
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "boolean_name", "boolean3", "boolean_id", "convive_con_agresor");
                    }
                    if (variablesCrossData.get(i).getSource_table().compareTo("sivigila_aggresor.alcohol_or_drugs") == 0) {
                        addToSourceTable("sivigila_event");
                        addToSourceTable("sivigila_aggresor");
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "boolean_name", "boolean3", "boolean_id", "presencia_alcohol_agresor");
                    }
                    if (variablesCrossData.get(i).getSource_table().compareTo("sivigila_event.recommended_protection") == 0) {
                        addToSourceTable("sivigila_event");
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "boolean3.boolean_name", "boolean3", "boolean_id", "recomienda_proteccion");
                    }
                    if (variablesCrossData.get(i).getSource_table().compareTo("sivigila_event.conflict_zone") == 0) {
                        addToSourceTable("sivigila_event");
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "boolean3.boolean_name", "boolean3", "boolean_id", "zona_conflicto");
                    }
                    break;
                case related_events://EVENTOS RELACIONADOS (suicidio)
                    addToSourceTable("fatal_injury_suicide");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "related_event_name", "related_events", "related_event_id", "eventos_relacionados");
                    break;
                case accident_mechanisms://MECANISMO (muerte accidental)                     
                    addToSourceTable("fatal_injury_accident");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "accident_mechanism_name", "accident_mechanisms", "accident_mechanism_id", "mecanismo_accidente");
                    break;
                case suicide_mechanisms://MECANISMO (suicidio)
                    addToSourceTable("fatal_injury_suicide");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "suicide_mechanism_name", "suicide_mechanisms", "suicide_mechanism_id", "mecanismo_suicidio");
                    break;
                case murder_contexts://CONTEXTO (homicidios)
                    addToSourceTable("fatal_injury_murder");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "murder_context_name", "murder_contexts", "murder_context_id", "contexto");
                    break;
                case alcohol_levels://CONSUMO ALCOHOL                     
                    if (variablesCrossData.get(i).getSource_table().compareTo("fatal_injury_traffic.alcohol_level_counterpart_id") == 0) {//(transito)
                        addToSourceTable("fatal_injury_traffic");
                        sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "alcohol_level_name", "alcohol_levels", "alcohol_level_id", "consumo_alcohol_contraparte");
                    } else {//(homicidios)(suicidios)(muerte accidental)
                        sqlReturn = createCase(sqlReturn, currentIndicator.getInjuryType() + ".alcohol_level_victim_id", "alcohol_level_name", "alcohol_levels", "alcohol_level_id", "consumo_alcohol");
                    }
                    break;
                case places://SITIO EVENTO (homicidios)(muerte accidental)                                       
                    sqlReturn = createCase(sqlReturn, currentIndicator.getInjuryType() + ".injury_place_id", "place_name", "places", "place_id", "lugar_hecho");
                    break;
                case weapon_types://TIPO DE ARMA (homicidios)
                    addToSourceTable("fatal_injury_murder");
                    sqlReturn = createCase(sqlReturn, variablesCrossData.get(i).getSource_table(), "weapon_type_name", "weapon_types", "weapon_type_id", "tipo_arma");
                    break;
            }
            if (i == variablesCrossData.size() - 1) {//si es la ultima instruccion se agrega salto de linea
                sqlReturn = sqlReturn + " \n\r";
            } else {//si no es la ultima instruccion se agrega coma y salto de linea
                sqlReturn = sqlReturn + ", \n\r";
            }
        }
        //----------------------------------------------------------------------
        //AL TRATARSE DE SIVIGILA HAY QUE REALIZAR ALGUNOS FILTROS
        //----------------------------------------------------------------------
        addAbuseTypes = false;
        if (currentIndicator.getIndicatorId() == 71 || currentIndicator.getIndicatorId() == 75) {
            //CASOS DE VIOLENCIA SEXUAL (VSX) EN EL SECTOR SALUD
            //PORCENTAJE DE CASOS DE SIVIGILA_VSX            
            if (sqlReturn.indexOf("naturaleza_violencia") == -1) {
                //SE AUMENTA NATURALEZA VIOLENCIA
                addAbuseTypes = true;
                separateRecords = true;
                sqlReturn = sqlReturn + ""
                        + ", ( \n"
                        + "   SELECT \n"
                        + "	     cast(array_agg('abuse_type_name<=>abuse_types<=>abuse_type_id<=>'||abuse_type_id) as text \n)"
                        + "   FROM \n"
                        + "      domestic_violence_abuse_type \n"
                        + "   WHERE \n"
                        + "      domestic_violence_abuse_type.non_fatal_injury_id=non_fatal_injuries.non_fatal_injury_id ) as naturaleza_violencia \n";
            }
        }

        if (currentIndicator.getIndicatorId() == 69 || currentIndicator.getIndicatorId() == 73) {//CASOS DE VIOLENCIA INTRAFAMILIAR (VIF) SECTOR SALUD            
            addToSourceTable("sivigila_event");
            addToSourceTable("sivigila_aggresor");
            filterSourceTable = filterSourceTable + " sivigila_aggresor.relative_id is not null AND \n\r";
            filterSourceTable = filterSourceTable + " sivigila_aggresor.relative_id != 9 AND \n\r";//no tomar los sin dato
        }
        if (currentIndicator.getIndicatorId() == 70 || currentIndicator.getIndicatorId() == 74) {//CASOS DE VIOLENCIA CONTRA LA MUJER (VCM) EN EL SECTOR SALUD
            filterSourceTable = filterSourceTable + " victims.gender_id = 2 AND \n\r";
        }

        sqlReturn = sqlReturn + ""
                + "   FROM  \n\r"
                + "       " + currentIndicator.getInjuryType() + ", victims" + sourceTable + " \n\r"
                + "   WHERE  \n\r"
                + "       " + currentIndicator.getInjuryType() + ".victim_id = victims.victim_id AND \n\r"
                //                + "       " + currentIndicator.getInjuryType() + ".injury_neighborhood_id IN "//filtro por area urbana
                //                + "       ( SELECT neighborhood_id FROM neighborhoods WHERE neighborhood_area = 1 ) AND"//filtro por area urbana
                + "       " + filterSourceTable;

        if (currentIndicator.getIndicatorId() > 4) { //si no es general se filtra por tipo de lesion
            if (currentIndicator.getIndicatorId() > 32 && currentIndicator.getIndicatorId() < 40) {//si es interpersonal en familia injury_id=53,55,56
                sqlReturn = sqlReturn + "       " + currentIndicator.getInjuryType() + ".injury_id IN (53,55,56) AND \n\r";
            } else {
                sqlReturn = sqlReturn + "       " + currentIndicator.getInjuryType() + ".injury_id = " + currentIndicator.getInjuryId().toString() + " AND \n\r";
            }
        }
        sqlReturn = sqlReturn + ""
                + "       " + currentIndicator.getInjuryType() + ".injury_date >= to_date('" + initialDateStr + "','dd/MM/yyyy') AND \n\r"
                + "       " + currentIndicator.getInjuryType() + ".injury_date <= to_date('" + endDateStr + "','dd/MM/yyyy') ";
        if (currentIndicator.getIndicatorId() == 71 || currentIndicator.getIndicatorId() == 75) {
            //CASOS DE VIOLENCIA SEXUAL (VSX) EN EL SECTOR SALUD
            //PORCENTAJE DE CASOS DE SIVIGILA_VSX            
            sqlReturn = sqlReturn + "\n"
                    + " ) as mmm \n"
                    + " WHERE "
                    + " naturaleza_violencia not like '%>1}' AND "
                    + " naturaleza_violencia not like '%>2}' AND "
                    + " naturaleza_violencia not like '%>4}' ";
        }
        System.out.println("CONSULTA (indicators percentage) \n " + sqlReturn);
        return sqlReturn;
    }

    private void removeIndicatorRecords() {
        //---------------------------------------------------------        
        //elimino los datos de este indicador
        //---------------------------------------------------------
        sql = ""
                + " DELETE FROM \n\r"
                + "    indicators_records \n\r"
                + " WHERE \n\r"
                + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n\r"
                + "    ( \n\r"
                + "       indicator_id = " + currentIndicator.getIndicatorId() + " OR \n\r" //datos ordenados completos(los que tienen y no tienen conteo )
                + "       indicator_id = " + (currentIndicator.getIndicatorId() + 100) + " \n\r" //ocurrencias
                + "    ) \n\r";
        //System.out.println("ELIMINACIONES \n " + sql);
        connectionJdbcMB.non_query(sql);
    }

    public void changeCategoticalList() {
        if (!currentCategoricalValuesSelected.isEmpty()) {
            btnRemoveCategoricalValueDisabled = false;
        }
    }

    public int btnRemoveConfigurationClick() {
        //System.out.println("currentConfigurationSelected es " + currentConfigurationSelected);
        if (currentConfigurationSelected == null || currentConfigurationSelected.isEmpty()) {//VALOR INICIAL INGRESADO
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar una configuración de la lista"));
            return 0;
        }
        List<IndicatorsConfigurations> indicatorsConfigurationsList = indicatorsConfigurationsFacade.findAll();
        for (int i = 0; i < indicatorsConfigurationsList.size(); i++) {
            for (int j = 0; j < currentConfigurationSelected.size(); j++) {
                if (indicatorsConfigurationsList.get(i).getConfigurationName().compareTo(currentConfigurationSelected.get(j)) == 0) {
                    indicatorsConfigurationsFacade.remove(indicatorsConfigurationsList.get(i));
                    btnLoadConfigurationClick();
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "La configuración ha sido eliminada"));
                    return 0;
                }
            }
        }
        return 0;
    }

    public int btnOpenConfigurationClick() {
        //realizar la carga de la configuracion indicada
        if (currentConfigurationSelected == null || currentConfigurationSelected.isEmpty()) {//VALOR INICIAL INGRESADO
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Se debe seleccionar una configuración de la lista"));
            return 0;
        }
        currentCategoricalValuesList = new ArrayList<>();
        IndicatorsConfigurations indicatorsConfigurationsSelected = indicatorsConfigurationsFacade.findByName(currentConfigurationSelected.get(0));

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
        for (int i = 0; i < variablesListData.size(); i++) {
            if (variablesListData.get(i).getName().compareTo(firstVariablesCrossSelected) == 0) {
                variablesListData.get(i).setValuesConfigured(Arrays.asList(splitConfiguration));
                variablesListData.get(i).setValuesId(Arrays.asList(splitConfiguration));
                variablesListData.get(i).setValues(Arrays.asList(splitConfiguration));
                break;
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", "Configuración cargada"));
        return 0;
    }

    public void btnLoadConfigurationClick() {
        //recargar las configuraciones existentes
        //System.out.println("inicia carga de configuraciones");
        currentConfigurationSelected = new ArrayList<>();
        configurationsList = new ArrayList<>();
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
            currentCategoricalValuesList = new ArrayList<>();
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
            currentCategoricalValuesList = new ArrayList<>();
            for (int j = 0; j < currentVariableConfiguring.getValuesConfigured().size(); j++) {
                currentCategoricalValuesList.add(currentVariableConfiguring.getValuesConfigured().get(j));
            }
        }
    }

    public void btnResetCategoryListClick() {
        currentCategoricalValuesSelected = new ArrayList<>();
        //btnRemoveCategoricalValueDisabled = false;
        if (currentVariableConfiguring != null) {
            //paso los elementos de la lista: values a valuesConfiguration
            currentVariableConfiguring.setValuesConfigured(new ArrayList<String>());
            for (int j = 0; j < currentVariableConfiguring.getValues().size(); j++) {
                currentVariableConfiguring.getValuesConfigured().add(currentVariableConfiguring.getValues().get(j));
            }
            //recargo la lista de valores de la categoria
            currentCategoricalValuesList = new ArrayList<>();
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
        try {
            btnRemoveVariableDisabled = true;
            btnRemoveCategoricalValueDisabled = true;
            currentCategoricalValuesSelected = new ArrayList<>();
            currentVariableConfiguring = null;
            initialValue = "";
            endValue = "";
            //cargo la lista de valores categoricos para la variable
            if (currentVariablesCrossSelected != null && !currentVariablesCrossSelected.isEmpty()) {
                btnRemoveCategoricalValueDisabled = true;
                btnRemoveVariableDisabled = false;
                firstVariablesCrossSelected = currentVariablesCrossSelected.get(0);
                //filtro los años segun la fecha
                for (int i = 0; i < variablesListData.size(); i++) {
                    if (variablesListData.get(i).getName().compareTo(firstVariablesCrossSelected) == 0) {
                        //if (variablesListData.get(i).getGeneric_table().compareTo("year") == 0) {
                        //variablesListData.get(i).filterYear(initialDate, endDate);
                        //}
                        currentCategoricalValuesList = variablesListData.get(i).getValuesConfigured();
                        currentVariableConfiguring = variablesListData.get(i);
                        btnAddCategoricalValueDisabled = !currentVariableConfiguring.isConfigurable();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error 6 en " + this.getClass().getName() + ":" + e.toString());
        }
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

    private TexturePaint createTexturePaint(Color c2, Color c1) {
        // 0,5  1,5  2,5  3,5  4,5  5,5
        // 0,4  1,4  2,4  3,4  4,4  5,4
        // 0,3  1,3  2,3  3,3  4,3  5,3
        // 0,2  1,2  2,2  3,2  4,2  5,2
        // 0,1  1,1  2,1  3,1  4,1  5,1
        // 0,0  1,0  2,0  3,0  4,0  5,0
        BufferedImage bi = new BufferedImage(6, 6, BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.getGraphics();
        g.setColor(c1);
        g.fillRect(0, 0, 6, 6);
        g.setColor(c2);
        TexturePaint tp = null;
        if (typeFill < 1 || typeFill > 7) {
            typeFill = 1;
        }
        switch (typeFill) {
            case 1:
                g.drawLine(0, 3, 5, 3);//cuadros
                g.drawLine(3, 5, 3, 0);//cuadros
                tp = new TexturePaint(bi, new Rectangle2D.Double(0, 0, 6, 6));
                break;
            case 2:
                g.drawLine(0, 0, 5, 5);// abajo izq > arriba derecha
                g.drawLine(1, 0, 4, 5);// abajo izq > arriba derecha
                tp = new TexturePaint(bi, new Rectangle2D.Double(0, 0, 6, 6));
                break;
            case 3:
                g.drawOval(2, 3, 2, 2);//circulos
                tp = new TexturePaint(bi, new Rectangle2D.Double(0, 0, 6, 6));
                break;
            case 4:
                g.drawLine(0, 5, 5, 5);//ladrillo
                g.drawLine(0, 5, 0, 2);
                g.drawLine(0, 2, 5, 2);
                g.drawLine(3, 0, 3, 2);
                tp = new TexturePaint(bi, new Rectangle2D.Double(0, 0, 6, 6));
                break;
            case 5:
                g.drawLine(1, 1, 4, 4);//cruz
                g.drawLine(1, 4, 4, 1);//cruz
                tp = new TexturePaint(bi, new Rectangle2D.Double(0, 0, 6, 6));
                break;
            case 6:
                g.drawLine(0, 0, 5, 5);//rombos
                g.drawLine(0, 5, 5, 0);//rombos
                tp = new TexturePaint(bi, new Rectangle2D.Double(0, 0, 6, 6));
                break;
            case 7:
                g.drawLine(0, 5, 5, 0);// abajo der > arriba izq
                g.drawLine(1, 5, 4, 0);// abajo der > arriba izq
                tp = new TexturePaint(bi, new Rectangle2D.Double(0, 0, 6, 6));
                break;
        }
        typeFill++;
        return tp;

    }

    private Color getColorById(int id) {
        switch (id) {
            case 2:
                return Color.RED;
            case 3:
                return Color.YELLOW;
            case 4:
                return Color.CYAN;
            case 5:
                return new Color(128, 0, 128);//MORADO                
            case 6:
                return Color.GREEN;
            case 7:
                return Color.MAGENTA;
            case 8:
                return Color.LIGHT_GRAY;
            case 9:
                return new Color(20, 20, 255);//AZUL
            case 0:
                return new Color(139, 69, 19);//CAFE
            case 1:
                return new Color(40, 200, 150);//VERDE PERLA                
        }
        return new Color(10, 10, 10);
    }

    private Paint determineColor() {
        /*
         * determinar con que color pintar de 99 posibles
         */
        colorId++;
        int modPos = 0;
        if (showFrames) {
            modPos = colorId % 99;
        } else {
            modPos = colorId % 20;
        }
        switch (modPos) {
            case 0:
                return new Color(255, 0, 0);//ROJO                
            case 1:
                return new Color(255, 255, 0);//AMARILLO
            case 2:
                return new Color(20, 20, 255);//AZUL
            case 3:
                return new Color(255, 0, 255);//MAGENTA                
            case 4:
                return new Color(0, 255, 0);//VERDE                
            case 5:
                return new Color(0, 255, 255);//CYAN                
            case 6:
                return new Color(0, 140, 0);//VERDE OSCURO
            case 7:
                return new Color(255, 130, 0);//NARANJA                
            case 8:
                return new Color(128, 0, 128);//MORADO                
            case 9:
                return new Color(160, 0, 0);//ROJO OSCURO
            case 10:
                return new Color(0, 0, 160);//AZUL OSCURO
            case 11:
                return new Color(0, 139, 139);//CYAN OSCURO
            case 12:
                return new Color(139, 69, 19);//CAFE
            case 13:
                return new Color(255, 192, 203);//ROJO CLARO
            case 14:
                return new Color(152, 252, 152);//VERDE CLARO
            case 15:
                return new Color(173, 216, 230);//AZUL CLARO
            case 16:
                return new Color(255, 255, 140);//AMARILLO CLARO                            
            case 17:
                return new Color(40, 200, 150);//VERDE PERLA
            case 18:
                return new Color(189, 183, 80);//AMARILLO OSCURO
            case 19:
                return new Color(190, 150, 210);//LILA            
            default://se debe crear una trama
                String posStr = String.valueOf(colorId);//tomo este valor y lo separo
                int color1;//color segun el primer digito de colorId
                int color2;//color segun el segundo digito de colorId
                color1 = Integer.parseInt(String.valueOf(posStr.charAt(0)));
                color2 = Integer.parseInt(String.valueOf(posStr.charAt(1)));
                if (color1 == color2) {//no pueden ser del mismo color
                    return determineColor();//se vuelve a llamar para que incremente el color
                }
                return createTexturePaint(getColorById(color1), getColorById(color2));
        }
    }

    public void createImage() {
        try {//JFreeChart 
            JFreeChart chart = null;
            if (currentTypeGraph.compareTo("pastel") == 0) {
                pieDataSet = createPieDataSet();
            }
            if (currentTypeGraph.compareTo("apiladas porcentual") == 0) {
                defaultDataSet = createDataSet();
            }
            //-----CREACION DEL TITULO
            indicatorName = currentIndicator.getIndicatorName() + " - Municipo de Pasto.\n";
            indicatorName = indicatorName + variablesName + "\nPeriodo " + sdf.format(initialDate) + " a " + sdf.format(endDate);
            //-----SELECCION DE TIPO DE GRAFICO            
            if (currentTypeGraph.compareTo("apiladas porcentual") == 0) {
                chart = ChartFactory.createStackedBarChart(indicatorName, categoryAxixLabel, "Porcentaje", defaultDataSet, PlotOrientation.VERTICAL, true, true, false);
                chart.getTitle().setFont(new Font("SanSerif", Font.BOLD, 15));
                //-------------------------------------------
                //--REORDENAMIENTO DE LAS SERIES
                CategoryPlot plot = (CategoryPlot) chart.getPlot();
                //if (!showEmpty) {                
                LegendItemCollection itemsLeyendaAnterior = plot.getLegendItems();
                LegendItemCollection itemsLeyendaNuevo = new LegendItemCollection();
                Paint c;
                LegendItem li;
                List<String> values = variablesCrossData.get(0).getValuesConfigured();
                colorId = -1;//color relleno
                typeFill = 1;//tipo de relleno
                for (int posValue = 0; posValue < values.size(); posValue++) {
                    for (int posLeyend = 0; posLeyend < itemsLeyendaAnterior.getItemCount(); posLeyend++) {
                        if (determineHeader(values.get(posValue)).compareTo(itemsLeyendaAnterior.get(posLeyend).getLabel()) == 0) {
                            c = determineColor();
                            li = itemsLeyendaAnterior.get(posLeyend);
                            li.setFillPaint(c);
                            li.setLinePaint(c);
                            li.setOutlinePaint(Color.BLACK);
                            li.setShape(new Rectangle2D.Float(0, 0, 10, 10));
                            plot.getRenderer().setSeriesPaint(posLeyend, c);//cambio color
                            itemsLeyendaNuevo.add(li);
                        }
                    }
                }
                LineLegendItemSource source = new LineLegendItemSource(itemsLeyendaNuevo);
                chart.removeLegend();
                LegendTitle legend = new LegendTitle(source);
                chart.addLegend(legend);
                chart.getLegend().setPosition(RectangleEdge.BOTTOM);
                //}
                //-----------------------------------------
                plot.setBackgroundPaint(Color.white);
                plot.setOutlineVisible(false);//grafico sin borde
                ((BarRenderer) plot.getRenderer()).setBarPainter(new StandardBarPainter());//sin gradiente                
                ((CategoryAxis) plot.getDomainAxis()).setCategoryLabelPositions(CategoryLabelPositions.UP_45);//rotar 45
                NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
                rangeAxis.setNumberFormatOverride(NumberFormat.getPercentInstance());
                StackedBarRenderer renderer = (StackedBarRenderer) plot.getRenderer();
                renderer.setRenderAsPercentages(true);
                if (showItems) {
                    renderer.setDrawBarOutline(false);
                    renderer.setBaseItemLabelsVisible(true);
                    renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{3}", NumberFormat.getIntegerInstance(), new DecimalFormat("0.00%")));
                    renderer.setItemLabelFont(new Font("arial", Font.BOLD, 12));
                }
            }
            if (currentTypeGraph.compareTo("pastel") == 0) {
                chart = ChartFactory.createPieChart(indicatorName, pieDataSet, true, true, false);
                chart.getTitle().setFont(new Font("arial", Font.BOLD, 15));//fuente titulo
                PiePlot plot = (PiePlot) chart.getPlot();
                plot.setBackgroundPaint(Color.white);
                plot.setOutlineVisible(false);//grafico sin borde

                //----REORDENAMIENTO SERIES------
                LegendItemCollection itemsLeyendaAnterior = plot.getLegendItems();
                LegendItemCollection itemsLeyendaNuevo = new LegendItemCollection();
                Paint c;
                LegendItem li;
                List<String> values = variablesCrossData.get(0).getValuesConfigured();
                colorId = -1;//color relleno
                typeFill = 1;//tipo de relleno
                for (int posValue = 0; posValue < values.size(); posValue++) {
                    for (int posLeyend = 0; posLeyend < itemsLeyendaAnterior.getItemCount(); posLeyend++) {
                        if (determineHeader(values.get(posValue)).compareTo(itemsLeyendaAnterior.get(posLeyend).getLabel()) == 0) {
                            c = determineColor();
                            li = itemsLeyendaAnterior.get(posLeyend);
                            li.setFillPaint(c);
                            li.setLinePaint(c);
                            li.setShape(new Rectangle2D.Float(0, 0, 10, 10));
                            li.setOutlinePaint(c);
                            plot.setSectionPaint(posLeyend, c);//cambio color
                            itemsLeyendaNuevo.add(li);
                        }
                    }
                }
                LineLegendItemSource source = new LineLegendItemSource(itemsLeyendaNuevo);
                chart.removeLegend();
                LegendTitle legend = new LegendTitle(source);
                chart.addLegend(legend);
                chart.getLegend().setPosition(RectangleEdge.BOTTOM);

                if (showItems) {
                    plot.setLabelFont(new Font("SansSerif", Font.BOLD, 11));
                    plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{2}", NumberFormat.getNumberInstance(), new DecimalFormat("0.00%")));
                    plot.setNoDataMessage("No data available");
                    plot.setLabelGap(0.02);
                }
            }
            //----------GENERAR PNG A PARTIR DEL JCHART
            File chartFile = new File("grafico");
            ChartUtilities.saveChartAsPNG(chartFile, chart, widthGraph, heightGraph);
            chartImage = new DefaultStreamedContent(new FileInputStream(chartFile), "image/png");
        } catch (Exception e) {
            System.out.println("Error 7 en " + this.getClass().getName() + ":" + e.toString());
        }
    }

    public void reset() {
        btnExportDisabled = true;
        dataTableHtml = "";
        chartImage = null;
        currentVariableConfiguring = null;
        variablesCrossList = null;
        //currentVariablesCrossSelected
        currentVariablesSelected = new ArrayList<>();
        currentVariablesCrossSelected = null;
        firstVariablesCrossSelected = null;
        currentCategoricalValuesList = new ArrayList<>();
        currentCategoricalValuesSelected = new ArrayList<>();
        titlePage = currentIndicator.getIndicatorGroup();
        titleIndicator = currentIndicator.getIndicatorGroup();
        subTitleIndicator = currentIndicator.getIndicatorName();
        variablesListData = getVariablesIndicator();
        //graphTypes = getGraphTypes();
//        variablesGraph = new ArrayList<>();
//        valuesGraph = new ArrayList<>();
//        currentValueGraph = "";
//        currentVariableGraph = "";

        currentVariableGraph1 = "";
        currentVariableGraph2 = "";
        valuesGraph1 = null;
        valuesGraph2 = null;
        currentValueGraph1 = null;
        currentValueGraph2 = null;

        //numberCross = currentIndicator.getNumberCross();

        typesGraph = new ArrayList<>();
        typesGraph.add("apiladas porcentual");
        typesGraph.add("pastel");

        variablesList = new ArrayList<>();//SelectItem[variablesListData.size()];
        for (int i = 0; i < variablesListData.size(); i++) {
            variablesList.add(variablesListData.get(i).getName());
        }
        //dynamicDataTableGroup = new OutputPanel();//creo el panel grid
        variablesCrossList = new ArrayList<>();//SelectItem[variablesListData.size()];
        btnAddVariableDisabled = true;
        btnRemoveVariableDisabled = true;
        currentVariablesSelected = null;
        currentVariablesCrossSelected = null;
    }

    private Variable createVariable(String name, String generic_table, boolean conf, String source_table) {
        //conf me indica si es permitida la configuracion de esta variable
        Variable newVariable = new Variable(name, generic_table, conf, source_table);
        //cargo la lista de valores posibles
        ArrayList<String> valuesName = new ArrayList<>();//NOMBRE DE LOS VALORES QUE PUEDE TOMAR LA VARIABLE POR DEFECTO(NOMBRE EN LA CATEGORIA)
        ArrayList<String> valuesId = new ArrayList<>();  //IDENTIFICADORES DE LOS VALORES QUE PUEDE TOMAR LA VARIABLE POR DEFECTO(ID EN LA CATEGORIA)
        ArrayList<String> valuesConf = new ArrayList<>();//NOMBRE DE LOS VALORES CONFIGURADOS POR EL USUARIO QUE PUEDE TOMAR LA VARIABLE        
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
//                valuesId.add("55");//si se agrgegan aqui aparecen duplicado en tabla de resultados y grafico
//                valuesName.add("VIOLENCIA INTRAFAMILIAR");//se agregan en la consulta sql
//                valuesConf.add("VIOLENCIA INTRAFAMILIAR");
//                valuesId.add("56");
//                valuesName.add("VIOLENCIA INTRAFAMILIAR");
//                valuesConf.add("VIOLENCIA INTRAFAMILIAR");
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
            case abuse_types:
                //1;"FISICO"
                valuesId.add("1");
                valuesName.add("FISICO");
                valuesConf.add("FISICO");
                //2;"PSICOLOGICO / VERBAL"
                valuesId.add("2");
                valuesName.add("PSICOLOGICO / VERBAL");
                valuesConf.add("PSICOLOGICO / VERBAL");
                //3;"VIOLENCIA SEXUAL"
                valuesId.add("3");
                valuesName.add("VIOLENCIA SEXUAL");
                valuesConf.add("VIOLENCIA SEXUAL");
                //4;"NEGLIGENCIA"
                valuesId.add("4");
                valuesName.add("NEGLIGENCIA");
                valuesConf.add("NEGLIGENCIA");
                //5;"ABANDONO"
                valuesId.add("5");
                valuesName.add("ABANDONO");
                valuesConf.add("ABANDONO");
                //6;"INSTITUCIONAL"
                valuesId.add("6");
                valuesName.add("INSTITUCIONAL");
                valuesConf.add("INSTITUCIONAL");
                //7;"SIN DATO"
                valuesId.add("7");
                valuesName.add("SIN DATO");
                valuesConf.add("SIN DATO");
                //8;"OTRO"
                valuesId.add("8");
                valuesName.add("OTRO");
                valuesConf.add("OTRO");

                //9;"PORNOGRAFIA CON NNA"
                //10;"TRATA DE PERSONAL PARA EXPLOTACION SEXUAL"
                //11;"ABUSO SEXUAL"
                //12;"ACOSO SEXUAL"
                //13;"ASALTO SEXUAL"
                //14;"EXPLOTACION SEXUAL"
                //15;"TURISMO SEXUAL"

                break;
            case neighborhoods://barrio,
            case communes://comuna,
            case corridors://corredor,
            case areas://zona,
            case genders://genero,
            case days://dia semana
            case quadrants://cuadrante
            case activities:
            case boolean3:
            case victim_characteristics:
            case accident_classes:
            case alcohol_levels:
            case use_alcohol_drugs:
            case alcohol_levels_counterparts:
            case alcohol_levels_victim:
            case murder_contexts:
            case contexts:
            case destinations_of_patient:
            case related_events:
            case precipitating_factors:
            case aggressor_genders:
            case places:
            case non_fatal_places:
            case mechanisms:
            case accident_mechanisms:
            case suicide_mechanisms:
            case protective_measures:
            case relationships_to_victim:
            case weapon_types:
            case degree:
            case service_types:
            case transport_counterparts:
            case transport_types:
            case transport_users:
            case involved_vehicles:
            case road_types:

            //case abuse_types:
            case aggressor_types:
            case ethnic_groups:
            case insurance:
            case jobs:
            case public_health_actions:
            case sivigila_educational_level:
            case sivigila_group:
            case sivigila_mechanism:
            case sivigila_no_relative:
            case sivigila_tip_ss:
            case sivigila_vulnerability:
            case vulnerable_groups:
            case kinds_of_injury:
            case anatomical_locations:
            case actions_to_take:
            case security_elements:
            case source_vif:
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
                }
                break;
        }
        //agregar valor "SIN DATO"
        if (generic_table.compareTo("injuries_fatal") != 0 && generic_table.compareTo("injuries_non_fatal") != 0) {
            //detemino si ya existe "SIN DATO" el los valores
            boolean existNoData = false;
            for (int i = 0; i < valuesName.size(); i++) {
                if (valuesName.get(i).compareTo("SIN DATO") == 0) {
                    existNoData = true;
                    break;
                }
            }
            if (!existNoData) {
                valuesName.add("SIN DATO");
                valuesConf.add("SIN DATO");
                valuesId.add("SIN DATO");
            }
        }
        newVariable.setValues(valuesName);
        newVariable.setValuesId(valuesId);
        newVariable.setValuesConfigured(valuesConf);
        return newVariable;
    }

    public ArrayList<Variable> getVariablesIndicator() {
        ArrayList<Variable> arrayReturn = new ArrayList<>();
        currentIndicator = indicatorsFacade.find(currentIndicator.getIndicatorId());
        for (int i = 0; i < currentIndicator.getIndicatorsVariablesList().size(); i++) {
            arrayReturn.add(
                    createVariable(
                    currentIndicator.getIndicatorsVariablesList().get(i).getIndicatorsVariablesPK().getVariableName(),
                    currentIndicator.getIndicatorsVariablesList().get(i).getCategory(),
                    currentIndicator.getIndicatorsVariablesList().get(i).getAddValues(),
                    currentIndicator.getIndicatorsVariablesList().get(i).getSourceTable()));
        }
        return arrayReturn;
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
            System.out.println("Error 9 en " + this.getClass().getName() + ":" + e.toString());
        }
        return strReturn;
    }

    private String determineHeader(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) != '0' && value.charAt(i) != '1' && value.charAt(i) != '2'
                    && value.charAt(i) != '3' && value.charAt(i) != '4' && value.charAt(i) != '5'
                    && value.charAt(i) != '6' && value.charAt(i) != '7' && value.charAt(i) != '8'
                    && value.charAt(i) != '9' && value.charAt(i) != ' ' && value.charAt(i) != 'n'
                    && value.charAt(i) != '-' && value.charAt(i) != ':' && value.charAt(i) != '/') {
                return value;
            }
        }
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

    public void postProcessXLS(Object document) {

        HSSFWorkbook book = (HSSFWorkbook) document;
        HSSFSheet sheet = book.getSheetAt(0);// Se toma hoja del libro
        HSSFRow fila;
        HSSFCell celda;
        HSSFRichTextString texto;

        headers1 = new ArrayList<>();
        headers2 = new String[columNames.size()];
        int posRow = 0;
        int posF;
        int posI;
        int posCol;
        //String strReturn = " ";
        //String value;
        //double totalA;
        //double totalB;
        int rowsForRecord = 0;//filas a crear por registro(inicia en 1 por el rowspan cuenta desde 1)        
        boolean nameColumnAdd;
        //-------------------------------------------------------------------
        //TABLA QUE CONTIENE LA CABECERA
        //-------------------------------------------------------------------                
        posCol = 2;// +2 por que faltal nombre de filas               
        if (variablesCrossData.size() == 2 || variablesCrossData.size() == 1) {
            fila = sheet.createRow(posRow++);// Se crea una fila dentro de la hoja            
            for (int i = 0; i < columNames.size(); i++) {
                celda = fila.createCell(posCol++);
                texto = new HSSFRichTextString(determineHeader(columNames.get(i)));// Se crea el contenido de la celda y se mete en ella.
                celda.setCellValue(texto);
            }
            celda = fila.createCell(posCol);
            celda.setCellValue("Total");
        }
        if (variablesCrossData.size() == 3) {
            //-------------------------------------------------------------------
            //CABECERA COMPUESTA            
            String currentVar = "";
            String[] splitVars;
            for (int i = 0; i < columNames.size(); i++) {
                splitVars = columNames.get(i).split("\\}");//separo las dos variables
                String first = splitVars[0];//invierto el orden de llegada
                splitVars[0] = splitVars[1];
                splitVars[1] = first;
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
            fila = sheet.createRow(posRow++);// Se crea una fila dentro de la hoja
            posF = 1;
            //posI = 1;
            for (int j = 0; j < headers1.size(); j++) {
                posI = posF + 1;
                for (int i = 0; i < headers1.get(j).getColumns(); i++) {
                    posF++;
                }
                sheet.addMergedRegion(new CellRangeAddress(0, 0, posI, posF));
                //posF++;
            }
            posCol = 2;// +2 por que faltal las filas               
            for (int i = 0; i < headers1.size(); i++) {
                celda = fila.createCell(posCol);
                posCol = posCol + headers1.get(i).getColumns();
                texto = new HSSFRichTextString(determineHeader(headers1.get(i).getLabel()));// Se crea el contenido de la celda y se mete en ella.
                celda.setCellValue(texto);
            }
            fila = sheet.createRow(posRow);// Se crea una fila dentro de la hoja
            posRow++;
            posCol = 2;// +2 por que faltal las filas               
            for (int i = 0; i < headers2.length; i++) {
                celda = fila.createCell(posCol++);
                texto = new HSSFRichTextString(determineHeader(headers2[i]));// Se crea el contenido de la celda y se mete en ella.
                celda.setCellValue(texto);
            }
            celda = fila.createCell(posCol);
            celda.setCellValue("Total");
        }

        //-------------------------------------------------------------------
        //TABLA QUE CONTIENE LOS DATOS DE LA MATRIZ
        //-------------------------------------------------------------------      

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

        posI = posRow;
        if (rowsForRecord > 0) {
            posF = posRow + rowsForRecord - 1;
        } else {
            posF = posRow + rowsForRecord;
        }

        for (int j = 0; j < rowNames.size() - 1; j++) {
            nameColumnAdd = false;
            sheet.addMergedRegion(new CellRangeAddress(posI, posF, 0, 0));

            posI = posI + rowsForRecord;
            posF = posF + rowsForRecord;

            if (showCount) {
                fila = sheet.createRow(posRow++);// Se crea una fila dentro de la hoja            
                if (!nameColumnAdd) {
                    celda = fila.createCell(0);
                    celda.setCellValue(determineHeader(rowNames.get(j)));
                    nameColumnAdd = true;
                }
                posCol = 1;
                celda = fila.createCell(posCol++);
                celda.setCellValue("Recuento");
                for (int i = 0; i < columNames.size(); i++) {
                    celda = fila.createCell(posCol++);
                    setValueCell(celda, getMatrixValue("countXY", i, j));//celda.setCellValue(getMatrixValue("countXY", i, j));
                }
                celda = fila.createCell(posCol++);
                setValueCell(celda, getMatrixValue("rowTotal", -1, j));//celda.setCellValue(getMatrixValue("rowTotal", -1, j));

            }
            if (showRowPercentage) {
                fila = sheet.createRow(posRow++);// Se crea una fila dentro de la hoja            
                if (!nameColumnAdd) {
                    celda = fila.createCell(0);
                    celda.setCellValue(determineHeader(rowNames.get(j)));
                    nameColumnAdd = true;
                }
                posCol = 1;
                celda = fila.createCell(posCol++);
                celda.setCellValue("% por fila");
                for (int i = 0; i < columNames.size(); i++) {
                    celda = fila.createCell(posCol++);
                    setValueCell(celda, getMatrixValue("rowPercentageXY", i, j));//celda.setCellValue(getMatrixValue("rowPercentageXY", i, j));
                }
                celda = fila.createCell(posCol++);
                setValueCell(celda, getMatrixValue("percentageOfTotalRowAccordingTotalRow", -1, j));//celda.setCellValue(getMatrixValue("percentageOfTotalRowAccordingTotalRow", -1, j));
            }
            if (showColumnPercentage) {
                fila = sheet.createRow(posRow++);// Se crea una fila dentro de la hoja            
                if (!nameColumnAdd) {
                    celda = fila.createCell(0);
                    celda.setCellValue(determineHeader(rowNames.get(j)));
                    nameColumnAdd = true;
                }
                posCol = 1;
                celda = fila.createCell(posCol++);
                celda.setCellValue("% por columna");

                for (int i = 0; i < columNames.size(); i++) {
                    celda = fila.createCell(posCol++);
                    setValueCell(celda, getMatrixValue("columnPercentageXY", i, j));//celda.setCellValue(getMatrixValue("columnPercentageXY", i, j));
                }
                celda = fila.createCell(posCol++);
                setValueCell(celda, getMatrixValue("percentageOfTotalRowAccordingGrandTotal", -1, j));//celda.setCellValue(getMatrixValue("percentageOfTotalRowAccordingGrandTotal", -1, j));
            }
            if (showTotalPercentage) {
                fila = sheet.createRow(posRow++);// Se crea una fila dentro de la hoja            
                if (!nameColumnAdd) {
                    celda = fila.createCell(0);
                    celda.setCellValue(determineHeader(rowNames.get(j)));
                    //nameColumnAdd = true;
                }
                posCol = 1;
                celda = fila.createCell(posCol++);
                celda.setCellValue("% del total");
                for (int i = 0; i < columNames.size(); i++) {
                    celda = fila.createCell(posCol++);
                    setValueCell(celda, getMatrixValue("totalPercentageXY", i, j));//celda.setCellValue(getMatrixValue("totalPercentageXY", i, j));
                }
                celda = fila.createCell(posCol++);
                setValueCell(celda, getMatrixValue("percentageOfTotalRowAccordingGrandTotal", 0, j));//celda.setCellValue(getMatrixValue("percentageOfTotalRowAccordingGrandTotal", 0, j));
            }
        }
        //---------------------------------------------------------------
        //fila final con los totales
        //---------------------------------------------------------------
        sheet.addMergedRegion(new CellRangeAddress(posI, posF, 0, 0));
        nameColumnAdd = false;
        if (showCount) {
            fila = sheet.createRow(posRow++);// Se crea una fila dentro de la hoja            
            if (!nameColumnAdd) {
                celda = fila.createCell(0);
                celda.setCellValue("Totales");
                nameColumnAdd = true;
            }
            posI = 1;
            celda = fila.createCell(posI++);
            celda.setCellValue("Recuento");
            for (int i = 0; i < totalsHorizontal.size(); i++) {
                celda = fila.createCell(posI++);
                setValueCell(celda, getMatrixValue("columnTotal", i, 0));//celda.setCellValue(getMatrixValue("columnTotal", i, 0));
            }
            celda = fila.createCell(posI++);
            setValueCell(celda, String.valueOf(grandTotal));//celda.setCellValue(String.valueOf(grandTotal));
        }
        if (showRowPercentage) {
            fila = sheet.createRow(posRow++);// Se crea una fila dentro de la hoja            
            if (!nameColumnAdd) {
                celda = fila.createCell(0);
                celda.setCellValue("Totales");
                nameColumnAdd = true;
            }
            posI = 1;
            celda = fila.createCell(posI++);
            celda.setCellValue("% por fila ");
            for (int i = 0; i < totalsHorizontal.size(); i++) {
                celda = fila.createCell(posI++);
                setValueCell(celda, getMatrixValue("percentageOfTotalColumnAccordingGrandTotal", i, 0));//celda.setCellValue(getMatrixValue("percentageOfTotalColumnAccordingGrandTotal", i, 0));
            }
            celda = fila.createCell(posI++);
            setValueCell(celda, getMatrixValue("percentageOfGrandTotalAccordingGrandTotal", 0, 0));//celda.setCellValue(getMatrixValue("percentageOfGrandTotalAccordingGrandTotal", 0, 0));
        }
        if (showColumnPercentage) {
            fila = sheet.createRow(posRow++);// Se crea una fila dentro de la hoja            
            if (!nameColumnAdd) {
                celda = fila.createCell(0);
                celda.setCellValue("Totales");
                nameColumnAdd = true;
            }
            posI = 1;
            celda = fila.createCell(posI++);
            celda.setCellValue("% por columna");
            for (int i = 0; i < totalsHorizontal.size(); i++) {
                celda = fila.createCell(posI++);
                setValueCell(celda, getMatrixValue("percentageOfTotalColumnAccordingTotalColumn", i, 0));//celda.setCellValue(getMatrixValue("percentageOfTotalColumnAccordingTotalColumn", i, 0));
            }
            celda = fila.createCell(posI++);
            setValueCell(celda, getMatrixValue("percentageOfGrandTotalAccordingGrandTotal", 0, 0));//celda.setCellValue(getMatrixValue("percentageOfGrandTotalAccordingGrandTotal", 0, 0));
        }
        if (showTotalPercentage) {
            fila = sheet.createRow(posRow++);// Se crea una fila dentro de la hoja            
            if (!nameColumnAdd) {
                celda = fila.createCell(0);
                celda.setCellValue("Totales");
                //nameColumnAdd = true;
            }
            posI = 1;
            celda = fila.createCell(posI++);
            celda.setCellValue("% del total");
            for (int i = 0; i < totalsHorizontal.size(); i++) {
                celda = fila.createCell(posI++);
                setValueCell(celda, getMatrixValue("percentageOfTotalColumnAccordingGrandTotal", i, 0));//celda.setCellValue(getMatrixValue("percentageOfTotalColumnAccordingGrandTotal", i, 0));
            }
            celda = fila.createCell(posI++);
            setValueCell(celda, getMatrixValue("percentageOfGrandTotalAccordingGrandTotal", 0, 0));//celda.setCellValue(getMatrixValue("percentageOfGrandTotalAccordingGrandTotal", 0, 0));
        }
    }

    private void setValueCell(HSSFCell celda, String strValue) {
        /*determina si el valor a almacenar en una celda del 
         archivo excell debe ser numerica o cadena*/
        try {
            double value = Double.parseDouble(strValue.replace(",", "."));
            celda.setCellValue(value);
        } catch (Exception e) {
            celda.setCellValue(new HSSFRichTextString(strValue));
        }
    }

    private String createDataTableResult() {
        headers1 = new ArrayList<>();
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
                strReturn = strReturn + "                                    <div class=\"tableHeader\" style=\"width:160px;\">" + determineHeader(columNames.get(i)) + "</div>\r\n";
                strReturn = strReturn + "                                </td>\r\n";
            }
            strReturn = strReturn + "                                <td>\r\n";
            strReturn = strReturn + "                                    <div class=\"tableHeader\" style=\"width:160px; \">Total</div>\r\n";
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
                String first = splitVars[0];//invierto el orden de llegada
                splitVars[0] = splitVars[1];
                splitVars[1] = first;
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
            strReturn = strReturn + "                                <td >\r\n";
            strReturn = strReturn + "                                    <div >-</div>\r\n";
            strReturn = strReturn + "                                </td>\r\n";
            strReturn = strReturn + "                            </tr>\r\n";

            strReturn = strReturn + "                            <tr>\r\n";
            //AGREGO LA CABECERA 2 A El PANEL_GRID
            for (int i = 0; i < headers2.length; i++) {
                strReturn = strReturn + "                                <td>\r\n";
                strReturn = strReturn + "                                    <div class=\"tableHeader\" style=\"width:160px; height:20px;\">" + determineHeader(headers2[i]) + "</div>\r\n";
                strReturn = strReturn + "                                </td>\r\n";
            }
            strReturn = strReturn + "                                <td >\r\n";
            strReturn = strReturn + "                                    <div class=\"tableHeader\" style=\"width:160px; height:20px;\">Total</div>\r\n";
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
            strReturn = strReturn + "                                <td rowspan=\"" + rowsForRecord + "\"><div style=\"overflow: hidden; width:110px; height:20px;\">" + determineHeader(rowNames.get(j)) + "</div></td>\r\n";
            if (showCount && !showCountAdd && !showRowPercentageAdd && !showColumnPercentageAdd && !showTotalPercentageAdd) {
                strReturn = strReturn + "                                <td class=\"tableFirstCol\"><div style=\"width:110px; height:20px;\">Recuento</div></td>\r\n";
                showCountAdd = true;
            }
            if (showRowPercentage && !showCountAdd && !showRowPercentageAdd && !showColumnPercentageAdd && !showTotalPercentageAdd) {
                strReturn = strReturn + "                                <td class=\"tableFirstCol\"><div style=\"width:110px; height:20px;\">% por fila</div></td>\r\n";
                showRowPercentageAdd = true;
            }

            if (showColumnPercentage && !showCountAdd && !showRowPercentageAdd && !showColumnPercentageAdd && !showTotalPercentageAdd) {
                strReturn = strReturn + "                                <td class=\"tableFirstCol\"><div style=\"width:110px; height:20px;\">% por columna</div></td>\r\n";
                showColumnPercentageAdd = true;
            }
            if (showTotalPercentage && !showCountAdd && !showRowPercentageAdd && !showColumnPercentageAdd && !showTotalPercentageAdd) {
                strReturn = strReturn + "                                <td class=\"tableFirstCol\"><div style=\"width:110px; height:20px;\">% del total</div></td>\r\n";
                showTotalPercentageAdd = true;
            }
            strReturn = strReturn + "                            </tr>\r\n";

            if (showCount && !showCountAdd) {
                strReturn = strReturn + "                            <tr>\r\n";
                strReturn = strReturn + "                                <td class=\"tableFirstCol\"><div style=\"width:110px; height:20px;\">recuento</div></td>\r\n";
                strReturn = strReturn + "                            </tr>\r\n";
            }
            if (showRowPercentage && !showRowPercentageAdd) {
                strReturn = strReturn + "                            <tr>\r\n";
                strReturn = strReturn + "                                <td class=\"tableFirstCol\"><div style=\"width:110px; height:20px;\">% por fila</div></td>\r\n";
                strReturn = strReturn + "                            </tr>\r\n";
            }
            if (showColumnPercentage && !showColumnPercentageAdd) {
                strReturn = strReturn + "                            <tr>\r\n";
                strReturn = strReturn + "                                <td class=\"tableFirstCol\"><div style=\"width:110px; height:20px;\">% por columna</div></td>\r\n";
                strReturn = strReturn + "                            </tr>\r\n";
            }
            if (showTotalPercentage && !showTotalPercentageAdd) {
                strReturn = strReturn + "                            <tr>\r\n";
                strReturn = strReturn + "                                <td class=\"tableFirstCol\"><div style=\"width:110px; height:20px;\">% del total</div></td>\r\n";

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
        //int sizeTableMatrix = columNames.size() * 150;//que cada columna tenga 100px
        //sizeTableMatrix = sizeTableMatrix + 100;//de los totales
        strReturn = strReturn + "                    <div id=\"table_div\" style=\"overflow: scroll;width:450px;height:300px;position:relative\" onscroll=\"fnScroll()\" >\r\n";//div que maneja la tabla
        //strReturn = strReturn + "                        <table width=\"" + sizeTableMatrix + "px\" cellspacing=\"0\" cellpadding=\"0\" border=\"1\" >\r\n";//
        strReturn = strReturn + "                        <table cellspacing=\"0\" cellpadding=\"0\" border=\"1\" >\r\n";//
        //----------------------------------------------------------------------
        //AGREGO LOS REGISTROS DE LA MATRIZ        
        for (int j = 0; j < rowNames.size() - 1; j++) {//-1 por que le agrege "TOTALES"
            if (showCount) {
                if (j == 0) {
                    strReturn = strReturn + "                            <tr " + getColorType() + " id='firstTr'>\r\n";
                } else {
                    strReturn = strReturn + "                            <tr " + getColorType() + " >\r\n";
                }
                for (int i = 0; i < columNames.size(); i++) {
                    strReturn = strReturn + "                                <td><div style=\"width:160px; height:20px;\">" + getMatrixValue("countXY", i, j) + "</div></td>\r\n";
                }
                strReturn = strReturn + "                                <td><div style=\"width:160px; height:20px;\">" + getMatrixValue("rowTotal", -1, j) + "</div></td>\r\n";
                strReturn = strReturn + "                            </tr>\r\n";
            }
            if (showRowPercentage) {
                if (j == 0) {//si es la primera fila tiene elidentificador 'firstTr'
                    strReturn = strReturn + "                            <tr " + getColorType() + "  id='firstTr'>\r\n";
                } else {
                    strReturn = strReturn + "                            <tr " + getColorType() + " >\r\n";
                }
                for (int i = 0; i < columNames.size(); i++) {
                    strReturn = strReturn + "                                <td><div style=\"width:160px; height:20px;\">" + getMatrixValue("rowPercentageXY", i, j) + "</div></td>\r\n";
                }
                strReturn = strReturn + "                                <td><div style=\"width:160px; height:20px;\">" + getMatrixValue("percentageOfTotalRowAccordingTotalRow", -1, j) + "</div></td>\r\n";
                strReturn = strReturn + "                            </tr>\r\n";
            }
            //total = 0;
            if (showColumnPercentage) {
                if (j == 0) {
                    strReturn = strReturn + "                            <tr " + getColorType() + "  id='firstTr'>\r\n";
                } else {
                    strReturn = strReturn + "                            <tr " + getColorType() + " >\r\n";
                }
                for (int i = 0; i < columNames.size(); i++) {
                    strReturn = strReturn + "                                <td><div style=\"width:160px; height:20px;\">" + getMatrixValue("columnPercentageXY", i, j) + "</div></td>\r\n";
                }
                strReturn = strReturn + "                                <td><div style=\"width:160px; height:20px;\">" + getMatrixValue("percentageOfTotalRowAccordingGrandTotal", -1, j) + "</div></td>\r\n";
                strReturn = strReturn + "                            </tr>\r\n";
            }
            //total = 0;
            if (showTotalPercentage) {
                if (j == 0) {
                    strReturn = strReturn + "                            <tr " + getColorType() + "  id='firstTr'>\r\n";
                } else {
                    strReturn = strReturn + "                            <tr " + getColorType() + " >\r\n";
                }
                for (int i = 0; i < columNames.size(); i++) {
                    strReturn = strReturn + "                                <td><div style=\"width:160px; height:20px;\">" + getMatrixValue("totalPercentageXY", i, j) + "</div></td>\r\n";
                }
                strReturn = strReturn + "                                <td><div style=\"width:160px; height:20px;\">" + getMatrixValue("percentageOfTotalRowAccordingGrandTotal", 0, j) + "</div></td>\r\n";
                strReturn = strReturn + "                            </tr>\r\n";
            }
            changeColorType();//cambiar de color las filas de blanco a azul
        }

        //----------------------------------------------------------------------
        //AGREGO LA ULTIMA FILA CORRESPONDIENTE A LOS TOTALES
        //----------------------------------------------------------------------

        if (showCount) {
            strReturn = strReturn + "                            <tr " + getColorType() + " >\r\n";
            for (int i = 0; i < totalsHorizontal.size(); i++) {
                strReturn = strReturn + "                                <td><div style=\"width:160px; height:20px;\">" + getMatrixValue("columnTotal", i, 0) + "</div></td>\r\n";
            }
            strReturn = strReturn + "                                <td><div style=\"width:160px; height:20px;\">" + String.valueOf(grandTotal) + "</div></td>\r\n";
            strReturn = strReturn + "                            </tr>\r\n";
        }

        if (showRowPercentage) {
            strReturn = strReturn + "                            <tr " + getColorType() + " >\r\n";
            for (int i = 0; i < totalsHorizontal.size(); i++) {
                strReturn = strReturn + "                                <td><div style=\"width:160px; height:20px;\">" + getMatrixValue("percentageOfTotalColumnAccordingGrandTotal", i, 0) + "</div></td>\r\n";
            }
            strReturn = strReturn + "                                <td><div style=\"width:160px; height:20px;\">" + getMatrixValue("percentageOfGrandTotalAccordingGrandTotal", 0, 0) + "</div></td>\r\n";
            strReturn = strReturn + "                            </tr>\r\n";
        }
        if (showColumnPercentage) {
            strReturn = strReturn + "                            <tr " + getColorType() + " >\r\n";
            for (int i = 0; i < totalsHorizontal.size(); i++) {
                strReturn = strReturn + "                                <td><div style=\"width:160px; height:20px;\">" + getMatrixValue("percentageOfTotalColumnAccordingTotalColumn", i, 0) + "</div></td>\r\n";
            }
            strReturn = strReturn + "                                <td><div style=\"width:160px; height:20px;\">" + getMatrixValue("percentageOfGrandTotalAccordingGrandTotal", 0, 0) + "</div></td>\r\n";
            strReturn = strReturn + "                            </tr>\r\n";
        }
        if (showTotalPercentage) {
            strReturn = strReturn + "                            <tr " + getColorType() + " >\r\n";
            for (int i = 0; i < totalsHorizontal.size(); i++) {
                //strReturn = strReturn + "                                <td>" + getMatrixValue("percentageOfTotalColumnAccordingGrandTotal", i, 0) + "</td>\r\n";
                strReturn = strReturn + "                                <td><div style=\"width:160px; height:20px;\">" + getMatrixValue("percentageOfTotalColumnAccordingGrandTotal", i, 0) + "</div></td>\r\n";
            }
            strReturn = strReturn + "                                <td><div style=\"width:160px; height:20px;\">" + getMatrixValue("percentageOfGrandTotalAccordingGrandTotal", 0, 0) + "</div></td>\r\n";
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
        //System.out.println(strReturn);
        return strReturn;
    }

    private DefaultPieDataset createPieDataSet() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        int pos = 0;
        try {
            ResultSet rs;
            sql = ""
                    + " SELECT \n"
                    + "    * \n"
                    + " FROM \n"
                    + "    indicators_records \n"
                    + " WHERE \n"
                    + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                    + "    indicator_id = " + currentIndicator.getIndicatorId() + "  \n";
            variablesName = "Desagregado por: " + variablesCrossData.get(0).getName();
            if (currentVariableGraph1 != null && currentVariableGraph1.length() != 0) {
                sql = sql + " AND column_2 LIKE '" + currentValueGraph1 + "' ";
                variablesName = variablesName + ", " + currentVariableGraph1 + " = " + determineHeader(currentValueGraph1);
            }
            if (currentVariableGraph2 != null && currentVariableGraph2.length() != 0) {
                sql = sql + " AND column_3 LIKE '" + currentValueGraph2 + "' ";
                variablesName = variablesName + ", " + currentVariableGraph2 + " = " + determineHeader(currentValueGraph2);
            }
            rs = connectionJdbcMB.consult(sql + " ORDER BY record_id");
            while (rs.next()) {
                dataset.setValue(determineHeader(rs.getString("column_1")), new Double(rs.getString("count")));
            }

//            if (variablesCrossData.size() == 1) {
//                //variablesName = "Desagregado por: " + variablesCrossData.get(0).getName();
//                rs = connectionJdbcMB.consult(sql + " ORDER BY record_id");
//                while (rs.next()) {
//                    dataset.setValue(determineHeader(rs.getString("column_1")), new Double(rs.getString("count")));
//                }
//            }
//            if (variablesCrossData.size() == 2) {
//                //variablesName = "Desagregado por: " + variablesCrossData.get(0).getName() + ", " + variablesCrossData.get(1).getName();
//                rs = connectionJdbcMB.consult(sql + " ORDER BY record_id");
//                while (rs.next()) {
//                    dataset.setValue(determineHeader(rs.getString("column_1")), new Double(rs.getString("count")));
//                }
//            }
//            if (variablesCrossData.size() == 3) {
//                //determino el numero de columna a filtrar (variable en variableCrossData                
//                for (int i = 0; i < variablesCrossData.size(); i++) {
//                    if (variablesCrossData.get(i).getName().compareTo(currentVariableGraph) == 0) {
//                        pos = i;
//                        break;
//                    }
//                }
//                //adiciono la instruccion WHERE a la consulta
//                sql = sql + " AND column_" + String.valueOf(pos + 1) + " LIKE '" + currentValueGraph + "' ";
//                rs = connectionJdbcMB.consult(sql + " ORDER BY record_id");
//                if (pos == 0) {
//                    variablesName = "Desagregado por: " + variablesCrossData.get(1).getName() + ", " + variablesCrossData.get(2).getName() + ", " + variablesCrossData.get(0).getName() + " = " + determineHeader(currentValueGraph);
//                    while (rs.next()) {
//                        dataset.setValue(determineHeader(rs.getString("column_1")), new Double(rs.getString("count")));
//                    }
//                    categoryAxixLabel = variablesCrossData.get(2).getName();
//                }
//                if (pos == 1) {
//                    variablesName = "Desagregado por: " + variablesCrossData.get(0).getName() + ", " + variablesCrossData.get(2).getName() + ", " + variablesCrossData.get(1).getName() + " = " + determineHeader(currentValueGraph);
//                    while (rs.next()) {
//                        dataset.setValue(determineHeader(rs.getString("column_1")), new Double(rs.getString("count")));
//                    }
//                    categoryAxixLabel = variablesCrossData.get(2).getName();
//                }
//                if (pos == 2) {
//                    variablesName = "Desagregado por: " + variablesCrossData.get(0).getName() + ", " + variablesCrossData.get(1).getName() + ", " + variablesCrossData.get(2).getName() + " = " + determineHeader(currentValueGraph);
//                    while (rs.next()) {
//                        dataset.setValue(determineHeader(rs.getString("column_1")), new Double(rs.getString("count")));
//                    }
//                    categoryAxixLabel = variablesCrossData.get(1).getName();
//                }
//            }
        } catch (SQLException ex) {
            System.out.println("Error 10 en " + this.getClass().getName() + ":" + ex.toString());
        }
        return dataset;
    }

    private DefaultCategoryDataset createDataSet() {
        /*
         * creacion del conjunto de datos para generar grafico
         */
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int pos = 0;
        try {
            sql = ""
                    + " SELECT \n"
                    + "    * \n"
                    + " FROM \n"
                    + "    indicators_records \n"
                    + " WHERE \n"
                    + "    user_id = " + loginMB.getCurrentUser().getUserId() + " AND \n"
                    + "    indicator_id = " + currentIndicator.getIndicatorId() + "  \n";
            ResultSet rs;
            if (variablesCrossData.size() == 1) {
                variablesName = "Desagregado por: " + variablesCrossData.get(0).getName();
                rs = connectionJdbcMB.consult(sql + " ORDER BY record_id");
                while (rs.next()) {
                    dataset.setValue(rs.getLong("count"), determineHeader(rs.getString("column_1")), "-");
                }
            }

            //IdSelectOneListCrossVariables: Error de validación: el valor no es válido

            if (variablesCrossData.size() == 2) {
                variablesName = "Desagregado por: " + variablesCrossData.get(0).getName() + ", " + variablesCrossData.get(1).getName();
                rs = connectionJdbcMB.consult(sql + " ORDER BY record_id");
                while (rs.next()) {
                    dataset.setValue(rs.getLong("count"), determineHeader(rs.getString("column_1")), determineHeader(rs.getString("column_2")));
                }
            }
            if (variablesCrossData.size() == 3) {
                sql = sql + " AND column_3 LIKE '" + currentValueGraph1 + "' ";
                variablesName = "Desagregado por: " + variablesCrossData.get(0).getName() + ", " + variablesCrossData.get(1).getName() + ", " + variablesCrossData.get(2).getName() + " = " + determineHeader(currentValueGraph1);
                rs = connectionJdbcMB.consult(sql + " ORDER BY record_id");



                while (rs.next()) {
                    dataset.setValue(rs.getLong("count"), determineHeader(rs.getString("column_1")), determineHeader(rs.getString("column_2")));
                }
                //categoryAxixLabel = variablesCrossData.get(1).getName();

            }
        } catch (SQLException ex) {
            System.out.println("Error 11 en " + this.getClass().getName() + ":" + ex.toString());
        }
        return dataset;
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

    public void loadIndicator73() {
        loadIndicator(73);
    }

    public void loadIndicator74() {
        loadIndicator(74);
    }

    public void loadIndicator75() {
        loadIndicator(75);
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

    public String getNewConfigurationName() {
        return newConfigurationName;
    }

    public void setNewConfigurationName(String newConfigurationName) {
        this.newConfigurationName = newConfigurationName;
    }

    public List<String> getCurrentConfigurationSelected() {
        return currentConfigurationSelected;
    }

    public void setCurrentConfigurationSelected(List<String> currentConfigurationSelected) {
        this.currentConfigurationSelected = currentConfigurationSelected;
    }

    public List<String> getConfigurationsList() {
        return configurationsList;
    }

    public void setConfigurationsList(List<String> configurationsList) {
        this.configurationsList = configurationsList;
    }

    public boolean isShowItems() {
        return showItems;
    }

    public void setShowItems(boolean showItems) {
        this.showItems = showItems;
    }

    public boolean isBtnExportDisabled() {
        return btnExportDisabled;
    }

    public void setBtnExportDisabled(boolean btnExportDisabled) {
        this.btnExportDisabled = btnExportDisabled;
    }

    public boolean isShowEmpty() {
        return showEmpty;
    }

    public void setShowEmpty(boolean showEmpty) {
        this.showEmpty = showEmpty;
    }

    public int getHeightGraph() {
        return heightGraph;
    }

    public void setHeightGraph(int heightGraph) {
        this.heightGraph = heightGraph;
    }

    public int getWidthGraph() {
        return widthGraph;
    }

    public void setWidthGraph(int widthGraph) {
        this.widthGraph = widthGraph;
    }

    public List<String> getTypesGraph() {
        return typesGraph;
    }

    public void setTypesGraph(List<String> typesGraph) {
        this.typesGraph = typesGraph;
    }

    public String getCurrentTypeGraph() {
        return currentTypeGraph;
    }

    public void setCurrentTypeGraph(String currentTypeGraph) {
        this.currentTypeGraph = currentTypeGraph;
    }

    public String getCurrentVariableGraph1() {
        return currentVariableGraph1;
    }

    public void setCurrentVariableGraph1(String currentVariableGraph1) {
        this.currentVariableGraph1 = currentVariableGraph1;
    }

    public String getCurrentVariableGraph2() {
        return currentVariableGraph2;
    }

    public void setCurrentVariableGraph2(String currentVariableGraph2) {
        this.currentVariableGraph2 = currentVariableGraph2;
    }

    public String getCurrentValueGraph1() {
        return currentValueGraph1;
    }

    public void setCurrentValueGraph1(String currentValueGraph1) {
        this.currentValueGraph1 = currentValueGraph1;
    }

    public String getCurrentValueGraph2() {
        return currentValueGraph2;
    }

    public void setCurrentValueGraph2(String currentValueGraph2) {
        this.currentValueGraph2 = currentValueGraph2;
    }

    public List<String> getValuesGraph1() {
        return valuesGraph1;
    }

    public void setValuesGraph1(List<String> valuesGraph1) {
        this.valuesGraph1 = valuesGraph1;
    }

    public List<String> getValuesGraph2() {
        return valuesGraph2;
    }

    public void setValuesGraph2(List<String> valuesGraph2) {
        this.valuesGraph2 = valuesGraph2;
    }

    public boolean isShowFrames() {
        return showFrames;
    }

    public void setShowFrames(boolean showFrames) {
        this.showFrames = showFrames;
    }
}
