/**
 * Copyright (c) 2008-2011 The Open Source Geospatial Foundation
 * 
 * Published under the BSD license.
 * See http://svn.geoext.org/core/trunk/geoext/license.txt for the full text
 * of the license.
 */

/** api: example[feature-grid]
 *  Grid with Features
 *  ------------------
 *  Synchronize selection of features between a grid and a layer.
 */

var mapPanel, mainPanel;
var bkey = "AnyGyd4GaAzToU0sDaA0NaXDD88yChcUh8ySoNc32_ddxkrxkl9K5SIATkA8EpMn"

function createLegend(rf){
    var paper = Raphael(document.getElementById('legend'), 320, 320);
    var ranges = rf.split("<end>");
    var nranges = ranges.length - 1;
    var width = 0;
    var x = 15;
    for(var i = 0; i < nranges; i++){
        var range = ranges[i].split("<tab>");
        var y = 15 + (i * 25);
        paper.circle(x, y, 10).attr(
        {
            "fill": "#" + range[0],
            "stroke":  "#000000"
        });
        var range_text = paper.text(x + 25, y, range[2]).attr(
        {
            "fill": "#000", 
            "font-size": 16, 
            "text-anchor": "start",
            "font-family": "Arial, Helvetica, sans-serif"
        });
        if(width < range_text.getBBox().width){
            width = range_text.getBBox().width 
        }
    }
    paper.setSize(x + 30 + width, 10 + (nranges * 25))
}

Ext.onReady(function() {
    var params = parseURLParams(window.location.href);
    createLegend(params["rf"][0]);
               
    var url_data = "data.jsp?" + window.location.href.split("?")[1];

    var map = new OpenLayers.Map();
    osm = new OpenLayers.Layer.OSM();
    hybrid = new OpenLayers.Layer.Bing({
        name: "Bing Hybrid",
        key: bkey,
        type: "AerialWithLabels"
    });
    aerial = new OpenLayers.Layer.Bing({
        name: "Bing Satellite",
        key: bkey,
        type: "Aerial"
    });
    // My code
                              
    initialStyles = new OpenLayers.StyleMap({
        "default": new OpenLayers.Style({
            fillColor: "#FFFFFF",
            'strokeWidth': 1,
            fillOpacity: 0.4,
            graphicZIndex: 1
        })
    });
                
    customStyles = new OpenLayers.StyleMap({
        "default": new OpenLayers.Style({
            fillColor: "\${colour}",
            'strokeWidth': 1,
            fillOpacity: 0.8,
            graphicZIndex: 1
        })
    });
                
    selectionStyles = new OpenLayers.StyleMap({
        "default": new OpenLayers.Style({
            fillColor: "#0000FF",
            'strokeWidth': 1,
            fillOpacity: 0.5
        })
    });

    vectors = new OpenLayers.Layer.Vector("Lesiones", {
        styleMap: customStyles,
        projection: new OpenLayers.Projection("EPSG:900913"),
        strategies: [new OpenLayers.Strategy.Fixed()],
        protocol: new OpenLayers.Protocol.HTTP({
            url: url_data,
            format: new OpenLayers.Format.GeoJSON(),
            callbackKey: "callback"
        }),
        eventListeners: {
            "featuresadded": function() {
                this.map.zoomToExtent(this.getDataExtent());
            }
        }
    })
    
    vectors.events.on({
        featureselected: function(e) {
            //alert(e.feature.data.name);
            WHERE = "(";
            for(var i = 0; i < this.selectedFeatures.length; i++){
                WHERE += "'" +this.selectedFeatures[i].data.name + "',";
            }
            WHERE = WHERE.substring(0, WHERE.length - 1) + ")";
            var uri = "getPieData.jsp?WHERE=" + WHERE + "&" + window.location.href.split("?")[1];
            OpenLayers.loadURL(uri, "", this, function onComplete(response) {
                if (response.responseText.indexOf('EPIC FAIL!!!') == -1) {
                    data = JSON.parse(response.responseText);
                    createPopup(e.feature);
                    setPie(e.feature.data.id);
                }
            });
        }
    });

    function createPopup(feature) {
        if(typeof popup != 'undefined' && Ext.get(popup.id) != null){
            if( typeof Ext.get(popup.id).select("div.x-tool-unpin").elements[0] != 'undefined'){
                var flag = Ext.get(popup.id).select("div.x-tool-unpin").elements[0].style.display
                if(flag != 'none'){
                    popup.destroy();
                }
            }
        }
        popup = new GeoExt.Popup({
            id: 'pie_window' + feature.data.id,
            title: 'My Popup',
            location: feature,
            width:600,
            html: "<div id='pie" + feature.data.id + "'></div>",
            maximizable: true,
            collapsible: true,
            unpinnable: true
        });
        popup.show();
    }
    
    neighborhoods = new OpenLayers.Layer.Vector("Barrios", {
        styleMap: initialStyles,
        projection: new OpenLayers.Projection("EPSG:900913"),
        strategies: [new OpenLayers.Strategy.Fixed()],
        protocol: new OpenLayers.Protocol.HTTP({
            url: "neighborhoods.jsp",
            format: new OpenLayers.Format.GeoJSON(),
            callbackKey: "callback"
        })
    })

    map.addLayers([osm, hybrid, neighborhoods, vectors]);
    map.addControl(new OpenLayers.Control.LayerSwitcher());
    
    // create map panel
    mapPanel = new GeoExt.MapPanel({
        title: "Map",
        id: "mappanel",
        region: "center",
        map: map
    });
 
    // create feature store, binding it to the vector layer
    var store = new GeoExt.data.FeatureStore({
        layer: vectors,
        fields: [
        {
            name: 'name', 
            type: 'string'
        },
        {
            name: 'value', 
            type: 'float'
        }
        ],
        autoLoad: true
    });

    // create grid panel configured with feature store
    var gridPanel = new Ext.grid.GridPanel({
        title: "Feature Grid",
        region: "east",
        collapsible: true,
        store: store,
        width: 320,        
        cm: new Ext.grid.ColumnModel([
        {
            id: "name", 
            header: "Nombre", 
            dataIndex: "name", 
            sortable: true
        },

        {
            id: "value", 
            header: "Valor", 
            dataIndex: "value", 
            sortable: true,
            direction: "desc"
        }
        ]),
        sm: new GeoExt.grid.FeatureSelectionModel(),
        autoExpandColumn: "name"
    });
    
    

    // create a panel and add the map panel and grid panel
    // inside it
    mainPanel = new Ext.Viewport({
        renderTo: "mainpanel",
        layout: "border",
        width: 1000,
        height: 800,
        items: [mapPanel, gridPanel]
    });
    
    legend = new Ext.Window({
        title: 'Legenda',
        collapsible: true,
        closable: false,
        layout: {
            type: 'absolute'
        },
        contentEl: 'legend'
    }).show().alignTo('mappanel', 'br-br', [-50, -50]);
});

function parseURLParams(url) {
    var queryStart = url.indexOf("?") + 1;
    var queryEnd   = url.indexOf("#") + 1 || url.length + 1;
    var query      = url.slice(queryStart, queryEnd - 1);

    if (query === url || query === "") return;

    var params  = {};
    var nvPairs = query.replace(/\+/g, " ").split("&");

    for (var i=0; i<nvPairs.length; i++) {
        var nv = nvPairs[i].split("=");
        var n  = decodeURIComponent(nv[0]);
        var v  = decodeURIComponent(nv[1]);
        if ( !(n in params) ) {
            params[n] = [];
        }
        params[n].push(nv.length === 2 ? v : null);
    }
    return params;
}

function setPie(id){
    var r = new Raphael("pie" + id)
    var pie = r.piechart(90, 110, 80, data.values, 
    {
        legend: data.labels, 
        legendpos: "east"
    });

    r.text(10, 10, "Pie").attr({
        font: "20px sans-serif",
        "text-anchor" : "start"
    });
    pie.hover(function () {
        this.sector.stop();
        this.sector.scale(1.1, 1.1, this.cx, this.cy);

        if (this.label) {
            this.label[0].stop();
            this.label[0].attr({
                r: 7.5
            });
            this.label[1].attr({
                "font-weight": 800
            });
        }
    }, function () {
        this.sector.animate({
            transform: 's1 1 ' + this.cx + ' ' + this.cy
        }, 500, "bounce");

        if (this.label) {
            this.label[0].animate({
                r: 5
            }, 500, "bounce");
            this.label[1].attr({
                "font-weight": 400
            });
        }
    });    
    popup.setSize(pie.getBBox().x2 + 30, pie.getBBox().y2 + 60);
}