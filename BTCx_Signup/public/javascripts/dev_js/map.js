//Map Functions

var addMarker;
var changeLocation;
var getLocation;
var initializeLayer;

//Map
var currentLocation;
var pannedTo = [];
var currentGeoJson = {  type: 'FeatureCollection', features: [] };
var streamMap = null;
var streamMapLayer = null;
var mapzoom = 14;


$(document).ready(function()
{	
	//changes Location on any given Map uses google api location LAT/LONG
	changeLocation = function(map,coordinates,zoom,callback)
	{		
		if(map == null) map = streamMap;
		currentLocation = coordinates;
		map.setView(coordinates, zoom);
		mapzoom = zoom;

		if(typeof(callback) == 'function') callback([currentLocation[1],currentLocation[0]]);
	}
 
	getLocation = function(location,callback)
	{
		var url = 'http://maps.googleapis.com/maps/api/geocode/json?address='+encodeURI(location)+'&sensor=true';
		$.ajax({
		  dataType: "json",
		  url: url,
		  }).done(function ( data ) {
				if(typeof(callback) == 'function') callback([data.results[0].geometry.location.lng,data.results[0].geometry.location.lat]);
		});
	}


	//Adds markers and initializes the map !currentGeoJson has to be complete before this call with addMarker()
	addLayer = function()
	{
		streamMap.eachLayer( function(l) {if(l.hasOwnProperty('feature')) streamMap.removeLayer(l);  } );
		streamMapLayer = L.mapbox.markerLayer(currentGeoJson);
	
		streamMapLayer.on('click',function(e) 
		{
			    var feature = e.layer.feature;
			    var type = feature.properties.type;
			    var title = feature.properties.title;

				this.eachLayer(function(marker)
		    	{
		    		if(marker.feature != feature) 
		    			{
		    				marker.feature.properties.active = false;
			    			currentGeoJson.features[marker.feature.properties.index].properties['active'] = false;
		    			}
		    	});

		    	if(!feature.properties.active)
		    		{
		    			feature.properties.active = true;
		    			
		    			currentGeoJson.features[feature.properties.index].properties['marker-color'] = "#000";
		    			currentGeoJson.features[feature.properties.index].properties['active'] = true;
		    		}
		    	else 
		    		{
		    			feature.properties.active = false;
		    			currentGeoJson.features[feature.properties.index].properties['active'] = false;
		    			e.layer.closePopup();
		    		}

		    		initializeLayer();

		    		streamMapLayer.setGeoJSON(currentGeoJson);
		    		
			    	var coordinates = feature.geometry.coordinates;
			    	streamMap.setView([parseFloat(coordinates[1]),parseFloat(coordinates[0])],mapzoom);
					pannedTo = [parseFloat(coordinates[1]),parseFloat(coordinates[0])];

		});	    
		
		initializeLayer();
	    streamMapLayer.addTo(streamMap);
    	streamMap.setView(currentLocation, mapzoom);
	}
	
	openMarkerPopup = function(marker)
	{
		var feature = marker.feature;
	    var popupContent =  '<div class="map-popup">';
	   popupContent += ' <a><h5>' + feature.properties.title + '</h5></a>';
	   popupContent += ' <h6>' + feature.properties.description + '</h6></div>';

	    marker.bindPopup(popupContent,{
	        closeButton: false,
	        minWidth: 100
	    });

	    marker.openPopup();
	}	
	initializeLayer = function()
	{
		streamMapLayer.eachLayer( 
		function(marker) 
			{
				openMarkerPopup(marker);
			} 
		);
		streamMapLayer.on('mouseover', function(e) {
				openMarkerPopup(e.layer);
			});
		streamMapLayer.on('mouseout', function(e) {
				if(e.layer.feature.hasOwnProperty('properties'))
				{
					if(!e.layer.feature.properties.active) e.layer.closePopup();
				}		
				else e.layer.closePopup();	
			});
	}

	addMarker = function(marker)
	{
		var json = {
				    type: 'Feature',
				    "geometry": { "type": "Point", "coordinates": marker.coordinates},
				    "properties": {
				    	"type":marker.type,
				    	"index":currentGeoJson.features.length,
				    	"title":marker.title,
				    	"description":marker.description,
				    	"active":marker.active,
				    	"location":marker.location,
				        "marker-symbol": marker.symbol,
				        "marker-color":marker.color,
				        "default-color":marker.color,
				        "marker-size":marker.size
				    }
				};
				
		currentGeoJson.features.push(json);
	}

});


