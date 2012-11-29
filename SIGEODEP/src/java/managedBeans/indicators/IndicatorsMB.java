/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.indicators;

import beans.connection.ConnectionJdbcMB;
import beans.util.Variable;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import model.dao.IndicatorsFacade;
import model.pojo.Indicators;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
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
    
    private String titlePage = "SIGEODEP -  INDICADORES GENERALES PARA LESIONES FATALES";
    private String titleIndicator = "SIGEODEP -  INDICADORES GENERALES PARA LESIONES FATALES";
    private String subTitleIndicator = "NUMERO DE CASOS POR LESION";
    private Date initialDate = new Date();
    private Date endDate = new Date();    
    private StreamedContent chartImage;
    
    private SelectItem[] variablesList;//lista de variables que sepueden cruzar(se visualizan en pagina)
    private SelectItem[] variablesCrossList;//ista de variables que se van a cruzar(se visualizan en pagina)
    private Short currentVariableSelected; 
    private Short currentVariableCrossSelected;
    private ArrayList<Variable> variablesListData;
    
    
    int size = 10;

    public IndicatorsMB() {
        //-------------------------------------------------
        try {
            JFreeChart jfreechart = ChartFactory.createPieChart("Prueba de CHART", createDataset(), true, true, false);
            File chartFile = new File("dynamichart");
            ChartUtilities.saveChartAsPNG(chartFile, jfreechart, 375, 300);
            chartImage = new DefaultStreamedContent(new FileInputStream(chartFile), "image/png");
        } catch (Exception e) {
        }
        //-------------------------------------------------
        indicatorsDataMB = (IndicatorsDataMB) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{indicatorsDataMB}", IndicatorsDataMB.class);
    }

    private PieDataset createDataset() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Dato A", new Double(45.0));
        dataset.setValue("Dato B", new Double(15.0));
        dataset.setValue("Dato C", new Double(25.2));
        dataset.setValue("Dato E", new Double(14.8));
        return dataset;
    }

    public void modifyImage() {
        try {
            JFreeChart jfreechart = ChartFactory.createPieChart("Prueba de CHART", createDataset(), true, true, false);
            File chartFile = new File("dynamichart");
            ChartUtilities.saveChartAsPNG(chartFile, jfreechart, 375, 300 + size);
            chartImage = new DefaultStreamedContent(new FileInputStream(chartFile), "image/png");
        } catch (Exception e) {
        }
        size = size + 10;
    }
       
    private void loadIndicator(int n) {
        currentIndicator=indicatorsFacade.find(n);
        titlePage = currentIndicator.getIndicatorGroup();
        titleIndicator = currentIndicator.getIndicatorGroup();
        subTitleIndicator = currentIndicator.getIndicatorName();                        
        variablesListData=indicatorsDataMB.getVariablesIndicator(n, 1);
        
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
}

