//Map Functions

var MapOperator = null;
//http://maps.googleapis.com/maps/api/geocode/json?address='+encodeURI(location)+'&sensor=true
$(document).ready(function()
{	
	Map = function()
	{
		this.geoJSON = {  type: 'FeatureCollection', features: [] };
		this.zoom = 14;
		this.map = null;
		this.markerLayer = null;
		this.coordinates = [];
		this.userip = "";
		this.usercoordinates = [];

		//calculates height of map
		var height = $(document).height()-parseInt($(".footer").css('height'))-parseInt($(".navbar-fixed").css('height'))-11;
		$("#crypt-map-container").css('height',height+'px');
		$("#carousel-example-generic .carousel-inner").css('min-height',height+'px');

		this.getMarkers = function()
		{
			this.setView(this.usercoordinates,11,function(){});
			Tube.markers(function(data)
				{
					var markers = data.markers;
					if(markers != null)
					{
						for(var i = 0; i < markers.length;i++)
						{
							var marker = markers[i];
							marker['type'] = 'shop';
							marker['active']=true;
							marker['location']='Location';
							marker['symbol']='shop';
							marker['color']='#000';
							marker['size']='large';

							var coordinates = marker['coordinates'];
							marker['coordinates'] = [coordinates[1],coordinates[0]];

				    		if(marker.acceptsBTC) MapOperator.addBitcoinMarker(marker);
				    		else MapOperator.addMarker(marker);
						}	

						MapOperator.finish();
					}
				});
		}
		this.getUserLocation = function(callback)
		{
			$.getJSON( "https://smart-ip.net/geoip-json?callback=?",function(data)
			{
				MapOperator.userip = data.host;	
				$.get('https://freegeoip.net/json/'+MapOperator.userip,function(response)
				{
					MapOperator.usercoordinates = [response.latitude,response.longitude];
					if(typeof(callback) == 'function') callback();
 				});
			});
		}
		this.setView = function(coordinates,zoom,callback)
		{
			try
			{
				if(this.map != null)
				{
					this.map.setView(coordinates,zoom);
					this.zoom = zoom;
					this.coordinates = coordinates;
				}
				if(typeof(callback) == 'function') callback([coordinates[1],coordinates[0]]);
			}
			catch(error)
			{
				if(typeof(callback) == 'function') callback(null);
			}
		}
		this.addBitcoinMarker = function(marker)
		{
			
        	var json = {
				    type: 'Feature',
				    "geometry": { "type": "Point", "coordinates": marker.coordinates},
				    "properties": {
				    	"icon": 
				    	{ "iconUrl": "../public/images/dev_imgs/bmark.png",
			            "iconSize": [25, 35], // size of the icon
			            "iconAnchor": [25, 25], // point of the icon which will correspond to marker's location
			            "popupAnchor": [-10, -25], // point from which the popup should open relative to the iconAnchor
			            "className": "dot"
			        	},
				    	"type":marker.type,
				    	"index":this.geoJSON.features.length,
				    	"title":marker.title,
				    	"description":marker.description,
				    	"phoneNumber":marker.phoneNumber,
				    	"active":marker.active,
				    	"location":marker.location
				    }
				};
				
			this.geoJSON.features.push(json);
		}
		this.addMarker = function(marker)
		{	
			var json = {
				    type: 'Feature',
				    "geometry": { "type": "Point", "coordinates": marker.coordinates},
				    "properties": {
				    	"type":marker.type,
				    	"index":this.geoJSON.features.length,
				    	"title":marker.title,
				    	"description":marker.description,
				    	"phoneNumber": marker.phoneNumber,
				    	"active":marker.active,
				    	"location":marker.location,
				        "marker-symbol": marker.symbol,
				        "marker-color":marker.color,
				        "default-color":marker.color,
				        "marker-size":marker.size
				    }
				};
				
			this.geoJSON.features.push(json);
		}
		this.initializeMarkers = function()
		{
			this.markerLayer.eachLayer( 
			function(marker) 
				{
					MapOperator.openMarkerPopup(marker);
				} 
			);
			this.markerLayer.on('mouseover', function(e) {
					MapOperator.openMarkerPopup(e.layer);
				});
			this.markerLayer.on('mouseout', function(e) {
					if(e.layer.feature.hasOwnProperty('properties'))
					{
						if(!e.layer.feature.properties.active) e.layer.closePopup();
					}		
					else e.layer.closePopup();	
				});
		}
		this.openMarkerPopup = function(marker)
		{
		   var feature = marker.feature;
		   var popupContent =  '<div class="map-popup">';
		   popupContent += ' <div class="marker-title">' + feature.properties.title + '</div>';
		   popupContent += '<div class="marker-phone-number">'+feature.properties.phoneNumber+'</div>';
		   popupContent += ' <div class="marker-description">' + feature.properties.description + '</div></div>';

		    marker.bindPopup(popupContent,{
		        closeButton: false,
		        minWidth: 100
		    });
		    if(feature.properties.hasOwnProperty('icon'))
		    {
		  	  marker.setIcon(L.icon(feature.properties.icon));
		    }
		    marker.openPopup();
		}
		this.finish = function()
		{
			this.map.eachLayer( function(l) {if(l.hasOwnProperty('feature')) this.map.removeLayer(l);  } );
			this.markerLayer = L.mapbox.markerLayer(this.geoJSON);
		
			this.markerLayer.on('click',function(e) 
			{
				    var feature = e.layer.feature;
				    var type = feature.properties.type;
				    var title = feature.properties.title;

					this.eachLayer(function(marker)
			    	{
			    		if(marker.feature != feature) 
			    			{
			    				marker.feature.properties.active = false;
				    			this.geoJSON.features[marker.feature.properties.index].properties['active'] = false;
			    			}
			    	});

			    	if(!feature.properties.active)
			    		{
			    			feature.properties.active = true;
			    			
			    			this.geoJSON.features[feature.properties.index].properties['marker-color'] = "#000";
			    			this.geoJSON.features[feature.properties.index].properties['active'] = true;
			    		}
			    	else 
			    		{
			    			feature.properties.active = false;
			    			this.geoJSON.features[feature.properties.index].properties['active'] = false;
			    			e.layer.closePopup();
			    		}

			    		this.initializeMarkers();

			    		this.markerLayer.setGeoJSON(this.geoJSON);
			    		
				    	var coordinates = feature.geometry.coordinates;
				    	this.map.setView([parseFloat(coordinates[1]),parseFloat(coordinates[0])],this.zoom);

			});	   

			this.initializeMarkers();
		   	this.markerLayer.addTo(this.map);
	    	this.map.setView(this.coordinates, this.zoom);
		}
	}

});


