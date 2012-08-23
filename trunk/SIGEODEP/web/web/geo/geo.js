/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function(){
    var map, vectors;
    var bkey = "AnyGyd4GaAzToU0sDaA0NaXDD88yChcUh8ySoNc32_ddxkrxkl9K5SIATkA8EpMn"
    var selectsArray = new Array();
    var show_form = false;
    init();
    
    function init(){
        showForm();
        map = new OpenLayers.Map('map', {
            eventListeners: {
                "changelayer": mapLayerChanged
            }
        });
        var Gaerial = new OpenLayers.Layer.Google(
            "Google Satellite",
            {
                type: google.maps.MapTypeId.SATELLITE, 
                numZoomLevels: 22
            });
        var Ghybrid = new OpenLayers.Layer.Google(
            "Google Hybrid",
            {
                type: google.maps.MapTypeId.HYBRID, 
                numZoomLevels: 22
            });
        var Bhybrid = new OpenLayers.Layer.Bing({
            name: "Bing Hybrid",
            key: bkey,
            type: "AerialWithLabels"
        });
        var Baerial = new OpenLayers.Layer.Bing({
            name: "Bing Satellite",
            key: bkey,
            type: "Aerial"
        });
        var osm = new OpenLayers.Layer.OSM();
                                
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

        vectors = new OpenLayers.Layer.Vector("Barrios", {
            styleMap: initialStyles,
            projection: new OpenLayers.Projection("EPSG:900913"),
            strategies: [new OpenLayers.Strategy.Fixed()],
            protocol: new OpenLayers.Protocol.HTTP({
                url: "data.jsp",
                format: new OpenLayers.Format.GeoJSON(),
                callbackKey: "callback"
            }),
            eventListeners: {
                "featuresadded": function() {
                    this.map.zoomToExtent(this.getDataExtent());
                }
            }
        })
                
        map.addLayers([Baerial,Bhybrid,Gaerial,Ghybrid,osm,vectors]);
        map.addControl(new OpenLayers.Control.MousePosition());
        map.addControl(new OpenLayers.Control.LayerSwitcher());
        var options = {
            hover: true,
            onSelect: getNames,
            multiple: true
        };
        selectControl = new OpenLayers.Control.SelectFeature(vectors, options);
        map.addControl(selectControl);
        selectControl.activate();  
    }
    
    function mapLayerChanged(event) {
        nLayers = map.getLayersBy("visibility", true)
        llayer = nLayers[nLayers.length - 1]
        the_max = llayer.max  
        if(the_max != undefined){
                    
            drawRamp(0, llayer.max, llayer.avg, llayer.sd)
            document.getElementById('ramp2').style.display = "block"
        } else {
            document.getElementById('ramp2').style.display = "none"
        }
    }
            
    function serialize(feature) {
        var str = feature.data.name + ':' + feature.data.value;
        document.getElementById('info').innerHTML = str;
    }
            
    function getNames(feature) {
        document.getElementById('info').innerHTML = feature.data.name;
    }

    function drawMap(){
        the_variable = $("#geoForm\\:comboVariables").val()
        the_variable_name = $("#geoForm\\:comboVariables option\\:selected").text()
        the_value = $("#geoForm\\:comboValues").val()
        the_value_name = $("#geoForm\\:comboValues option\\:selected").text()
        option1 = $("#geoForm\\:options\\:0")
        if(option1[0].checked){
            the_option = 1
        } else {
            option2 = $("#geoForm\\:options\\:1")
            if(option2[0].checked){
                the_option = 2
            } else {
                the_option = 3
            }
        }
        var request = OpenLayers.Request.GET({
            url: "data.jsp",
            params: {
                variable: the_variable, 
                value: the_value, 
                option: the_option
            },
            callback: the_handler
        });    
    }
            
    function the_handler(request){
        var geojson = request.responseText
        var geojson_format = new OpenLayers.Format.GeoJSON();
        response = new OpenLayers.Format.JSON().read(geojson);
        the_variable_name = $("#geoForm\\:comboVariables option:selected").text()
        the_value_name = $("#geoForm\\:comboValues option:selected").text()
        var vector_layer = new OpenLayers.Layer.Vector(the_variable_name + "=" + the_value_name,
        {
            styleMap: customStyles,
            projection: new OpenLayers.Projection("EPSG:900913"),
            max: response.max,
            avg: response.avg,
            sd:  response.sd
        }); 
        map.addLayer(vector_layer);
        vector_layer.addFeatures(geojson_format.read(geojson));
        mapLayerChanged(null)
        var options = {
            onSelect: onFeatureSelect,
            onUnselect: onFeatureUnselect
        };
        var select_layer = new OpenLayers.Control.SelectFeature(vector_layer, options);
        map.addControl(select_layer);
        select_layer.activate(); 
    }
            
    function onPopupClose(evt) {
        selectControl.unselect(selectedFeature);
    }
            
    function onFeatureSelect(feature) {
        var str = feature.data.name + ':' + feature.data.value;
        var html = "Lugar: " + feature.data.name + "<br>N&uacutemero de casos: " + feature.data.value
        document.getElementById('info').innerHTML = html;
        popup = new OpenLayers.Popup.FramedCloud(feature.data.name, 
            feature.geometry.getBounds().getCenterLonLat(),
            null,
            html,
            null, true);
        feature.popup = popup;
        map.addPopup(popup);
    }

    function onFeatureUnselect(feature) {
        map.removePopup(feature.popup);
        feature.popup.destroy();
        feature.popup = null;
    } 
            
    function px2val(px,max){
        val = (px-4)*max/150;
        val = val.toFixed(0);
        return val
    }
            
    function drawRamp(value, max, avg, sd) {
        var avg_pos = (avg * 150) / max
        var sd_pos = (sd * 150) / max
        this.s = 0
        this.e = max
        try{
            c.remove()
            paper.remove()
        } catch(e) {}
        paper = Raphael("ramp2");
        c = paper.rect(4, 17, 150, 25);
        c.attr({
            "fill": "0-#fff:0-#f00:100"
        });  
        c.mousemove(function(event) {
            this.x = event.offsetX;
            this.y = event.offsetY;
            if(this.s == undefined){
                str = px2val(this.x,max)
            } else {
                str = px2val(this.s,max) + " " + px2val(this.x,max)
            }
            document.getElementById("start").innerHTML = str;
            document.getElementById("start").style.top=(event.offsetY + 15) + 'px';
            document.getElementById("start").style.left=(event.offsetX + 15) + 'px';
        });
                
        c.mouseout(function(event){
            document.getElementById("start").innerHTML = "";
        });

        c.dblclick(function(event) {
            if (this.rects) {
                this.rects.remove();
                this.rects = false;
                this.s = undefined
            }
        });

        var start = function() {
            draw = true;
            this.s = this.x
            selection = new OpenLayers.Layer.Vector("selection",{
                styleMap: selectionStyles
            })
        },
        move = function(dx, dy, x, y, event) {
            if (draw) {
                draw = false;
                if (this.rects) {
                    this.rects.remove();
                    this.rects = false;
                    this.s = this.x
                }
                this.rects = paper.rect(this.x, 18, 1, 15).attr({
                    fill: "#c8c8c8",
                    opacity: 0.5
                })
            }
            this.rects.attr({
                "width": "" + this.x - this.s,
                "title": px2val(this.s,max) + " a " + px2val(this.x,max)
            })
            this.e = px2val(this.x,max)
        },
        up = function() {
            nLayers = map.getLayersBy("visibility", true)
            llayer = nLayers[nLayers.length - 1]
            var features = llayer.features
            var value_min = px2val(this.s, max)
            var value_max = this.e
            for(var i=0;i<features.length;i++){
                if(features[i].data.value >= value_min && 
                    features[i].data.value <= value_max){
                    selection.addFeatures(features[i].clone())
                }
            }
            map.addLayer(selection)
            var options = {
                onSelect: onFeatureSelect,
                onUnselect: onFeatureUnselect
            };
            select_Selection = new OpenLayers.Control.SelectFeature(selection, options);
            map.addControl(select_Selection);
            select_Selection.activate(); 
            this.s = undefined
        };
        c.drag(move, start, up);
                
        paper.text(4, 7, "0").attr(
        {
            'fill': '#fff', 
            "font-size": 16, 
            "font-family": "Arial, Helvetica, sans-serif"
        });
        paper.text(154, 7, max).attr(
        {
            'fill': '#fff', 
            "font-size": 16, 
            "font-family": "Arial, Helvetica, sans-serif"
        })

        paper.path("M" + (4 + avg_pos) + " 17 L" + (4 + avg_pos) + " 43").attr({
            "stroke": "#808080",
            "stroke-dasharray": "- ",
            "title": "" + avg.toFixed(2)
        }); 
        var sd1 = avg - sd
        if(sd1 > 0){
            paper.path("M" + (4 + avg_pos - sd_pos) + " 17 L" + (4 + avg_pos - sd_pos) + " 43").attr({
                "stroke": "#808080",
                "stroke-dasharray": ".",
                "title": "" + sd1.toFixed(2)
            }); 
        }
        var sd2 =  avg + sd
        if(sd2 < 154){
            paper.path("M" + (4 + avg_pos + sd_pos) + " 17 L" + (4 + avg_pos + sd_pos) + " 43").attr({
                "stroke": "#808080",
                "stroke-dasharray": ".",
                "title": "" + sd2.toFixed(2)
            }); 
        }
        if(value != 0){
            var pos = (value * 150) / max
            var v = paper.path("M" + (4 + pos) + " 17 L" + (4 + pos) + " 43")
            v.attr({
                "fill": "#FF0000"
            });       
            paper.text(4 + pos, 47, value).attr(
            {
                "fill": "#fff", 
                "font-size": 12, 
                "font-family": "Arial, Helvetica, sans-serif"
            })
        }
    }
            
    function showForm(){
        $("#hidr").click(function () {
            if(show_form){
                $("#the_div").show("fast");
                $("#hidr").attr("src","resources/js/img/up_16.png");
                show_form = false
            } else {
                $("#the_div").hide("fast");
                $("#hidr").attr("src","resources/js/img/down_16.png");
                show_form = true
            }
        }); 
    }
    
    $('#geoForm\\:ver_mapa').click(function(){
        drawMap();
    });
});

