/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.indicators;

import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.LegendItemSource;

/**
 *
 * @author santos
 */
public class LineLegendItemSource implements LegendItemSource {

    LegendItemCollection itemCollection = new LegendItemCollection();

    public LineLegendItemSource(LegendItemCollection li) {
        itemCollection = li;
    }

    @Override
    public LegendItemCollection getLegendItems() {
        return itemCollection;
    }
}
