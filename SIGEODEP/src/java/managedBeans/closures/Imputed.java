/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.closures;

import java.text.DecimalFormat;

/**
 *
 * @author and
 */
public class Imputed {

    private DecimalFormat f;
    private int order;
    private String actual;
    private String predicted;
    private double confidence;

    public Imputed() {
        f = new DecimalFormat("##.00");
    }

    /**
     * @return the order
     */
    public int getOrder() {
        return order;
    }

    /**
     * @param order the order to set
     */
    public void setOrder(int order) {
        this.order = order;
    }

    /**
     * @return the actual
     */
    public String getActual() {
        return actual;
    }

    /**
     * @param actual the actual to set
     */
    public void setActual(String actual) {
        this.actual = actual;
    }

    /**
     * @return the predicted
     */
    public String getPredicted() {
        return predicted;
    }

    /**
     * @param predicted the predicted to set
     */
    public void setPredicted(String predicted) {
        this.predicted = predicted;
    }

    /**
     * @return the confidence
     */
    public double getConfidence() {
        return confidence;
    }

    /**
     * @param confidence the confidence to set
     */
    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    @Override
    public String toString() {
        return "# " + order 
                + "\tActual: " + actual 
                + "\tPredicted: " + predicted 
                + "\tConfidence: " + f.format(confidence * 100) + "%";
    }
}
