/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.geo2;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONWriter;
import org.mapfish.geo.MfFeature;
import org.mapfish.geo.MfFeatureCollection;
import org.mapfish.geo.MfGeoJSONWriter;

/**
 *
 * @author and
 */
public class MyFeatureCollection {

    Writer w;
    int max;
    private GeoDBConnection geo;
    private int indicator_id;
    private int user_id;
    private String vars;
    private RangeFactory rf;
    private ArrayList<Range> ranges;

    public MyFeatureCollection(String user, String pass, String host, String name) {
        this.geo = new GeoDBConnection(user, pass, host, name);
    }

    public MyFeatureCollection(int indicator_id, int user_id, String vars, RangeFactory rf) {
        this.indicator_id = indicator_id;
        this.user_id = user_id;
        this.vars = vars;
        this.rf = rf;
    }

    public void setConnection(String user, String pass, String host, String name) {
        this.geo = new GeoDBConnection(user, pass, host, name);
    }

    public Writer getGeoJSON() {
        try {
            //GeoDBConnection geo = new GeoDBConnection();
            //geo = (GeoDBConnection) FacesContext.getCurrentInstance().getApplication().evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{geoDBConnectionMB}", GeoDBConnection.class);

            geo.setUser_id(user_id);
            geo.setIndicator_id(indicator_id);
            geo.setVars(vars);

            String[] columns = vars.split(",");
            int order = 1;
            String column_order = "column_";
            Collection<MfFeature> polygons = null;
            for (String column : columns) {
                if (column.equalsIgnoreCase("barrio")) {
                    column_order += order;
                    geo.setGeo_column(column_order);
                    polygons = geo.getPolygons(rf);
                    break;
                }
                if (column.equalsIgnoreCase("comuna")) {
                    column_order += order;
                    geo.setGeo_column(column_order);
                    polygons = geo.getCommunesPolygons(rf);
                    break;
                }
                if (column.equalsIgnoreCase("cuadrante")) {
                    column_order += order;
                    geo.setGeo_column(column_order);
                    polygons = geo.getQuadrantsPolygons(rf);
                    break;
                }
                if (column.equalsIgnoreCase("corredor")) {
                    column_order += order;
                    geo.setGeo_column(column_order);
                    polygons = geo.getCorridorsPolygons(rf);
                    break;
                }
                order++;
            }
            MfFeatureCollection collection = new MfFeatureCollection(polygons);
            w = new StringWriter();
            JSONWriter writer = new JSONWriter(w);
            MfGeoJSONWriter gjw = new MfGeoJSONWriter(writer);
            gjw.encodeFeatureCollection(collection, 0, 0.0, 0.0);
        } catch (JSONException ex) {
            Logger.getLogger(MyFeatureCollection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return w;
    }

    public Writer getNeighborhoodGeoJSON() {
        try {
            Collection<MfFeature> polygons = geo.getNeighborhoodPolygons();
            MfFeatureCollection collection = new MfFeatureCollection(polygons);
            w = new StringWriter();
            JSONWriter writer = new JSONWriter(w);
            MfGeoJSONWriter gjw = new MfGeoJSONWriter(writer);
            gjw.encodeFeatureCollection(collection, 0, 0.0, 0.0);
        } catch (JSONException ex) {
            Logger.getLogger(MyFeatureCollection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return w;
    }

    public String getPieData(String WHERE, String geo_column, String column) {
        return geo.getPieData(WHERE, geo_column, column, user_id, indicator_id);
    }

    public String getMapName() {
        return geo.getMapName(indicator_id);
    }

    public ArrayList<Range> getRanges() {
        return ranges;
    }

    public void setRanges(ArrayList<Range> ranges) {
        this.ranges = ranges;
    }
}
