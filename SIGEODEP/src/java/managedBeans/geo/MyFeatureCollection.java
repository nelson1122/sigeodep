/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package managedBeans.geo;

import java.io.StringWriter;
import java.io.Writer;
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

    public MyFeatureCollection(String variable, String value, String type) {
        try {
            GeoDBConnection connection = new GeoDBConnection();
            connection.connect();
            Collection<MfFeature> polygons = null;
            if (type.equals("1")) {
                polygons = connection.getPolygons(variable, value);
            } else if (type.equals("2")) {
                polygons = connection.getComunaPolygons(variable, value);
            } else if (type.equals("3")) {
                polygons = connection.getCorredorPolygons(variable, value);
            }
            MfFeatureCollection collection = new MfFeatureCollection(polygons);
            w = new StringWriter();
            JSONWriter writer = new JSONWriter(w);
            MfGeoJSONWriter gjw = new MfGeoJSONWriter(writer);
            gjw.encodeFeatureCollection(collection, connection.getMax(),
                    connection.getAvg(), connection.getSd());
            connection.disconnect();
        } catch (JSONException ex) {
            Logger.getLogger(MyFeatureCollection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Writer getGeoJSON() {
        return w;
    }

    public static void main(String[] args) throws Exception {
        MyFeatureCollection f = new MyFeatureCollection("diaev", "1", "2");
        System.out.println(f.getGeoJSON().toString());
    }
}
