// implementation of AR-Experience (aka "World")
var World = {
	// true, when poiData is loadfromJsonData
    key:false,
    visibilityKey:false,
	// different POI-Marker assets
 	markerDrawable_idle: null,
 	markerDrawable_selected: null,
 	markerDrawable_directionIndicator: null,
	// list of AR.GeoObjects that are currently shown in the scene / World
 	markerList: [],
	// The last selected marker
 	currentMarker: null,
 	pathMarker:null,
	locationUpdateCounter: 0,
	choice:0,
 	updatePlacemarkDistancesEveryXLocationUpdates: 1,
 	// called to inject new POI data
	loadPoisFromJsonData: function loadPoisFromJsonDataFn(poiData) {
		key = true;
		// destroys all existing AR-Objects (markers & radar)
		AR.context.destroyAll();
		// show radar & set click-listener
		PoiRadar.show();
		$('#radarContainer').unbind('click');
		$("#radarContainer").click(PoiRadar.clickedRadar);
        // empty list of visible markers
		World.markerList = [];
		// start loading marker assets
		World.markerDrawable_directionIndicator = new AR.ImageResource("assets/row.png");

		var islogg;
        // loop through POI-information and create an AR.GeoObject (=Marker) per POI
		for (var currentPlaceNr = 0; currentPlaceNr < poiData.length; currentPlaceNr++) {
			World.markerDrawable_idle = new AR.ImageResource("assets/"+poiData[currentPlaceNr].Image+".png");
			World.markerDrawable_selected = new AR.ImageResource("assets/"+poiData[currentPlaceNr].Image+"g.png");

            islogg = {
            "isloged":poiData[currentPlaceNr].isloged
            };

			var singlePoi = {
				"id": poiData[currentPlaceNr].id,
				"latitude": parseFloat(poiData[currentPlaceNr].latitude),
				"longitude": parseFloat(poiData[currentPlaceNr].longitude),
				"altitude": parseFloat(poiData[currentPlaceNr].altitude),
				"title": poiData[currentPlaceNr].name,
				"description": poiData[currentPlaceNr].description,
				"isfav":poiData[currentPlaceNr].isfav
			};
			World.markerList.push(new Marker(singlePoi));

		}


        if(islogg.isloged=="guest"){
        visibilityKey=true;
//        		console.log("fave is logg"+islogg.isloged);
//                AR.logger.debug("fave is logg"+islogg.isloged);
//        		document.getElementById("favhref").style.display="none";
//        		document.getElementById("ratehref").style.display="none";
        //		document.getElementsById("favhref").style.visibility="hidden";
        //		document.getElementsById("ratehref").style.visibility="hidden";
        		}else{
        		visibilityKey = false;
        		}


		// updates distance information of all placemarks
		World.updateDistanceToUserValues();
        World.updateStatusMessage(currentPlaceNr + ' places loaded');
        //update all placemarks
        World.updateVisMarkers();
		
	},
	
	// sets/updates distances of all makers so they are available way faster than calling (time-consuming) distanceToUser() method all the time
	updateDistanceToUserValues: function updateDistanceToUserValuesFn() {
		for (var i = 0; i < World.markerList.length; i++) {
			World.markerList[i].distanceToUser = World.markerList[i].markerObject.locations[0].distanceToUser();
		}
		
	},
	// updates status message shown in small "i"-button aligned bottom center
	updateStatusMessage: function updateStatusMessageFn(message, isWarning) {
		var themeToUse = isWarning ? "e" : "c";
		var iconToUse = isWarning ? "alert" : "info";
		
		$("#status-message").html(message);
		$("#popupInfoButton").buttonMarkup({
			theme: themeToUse
		});
		$("#popupInfoButton").buttonMarkup({
			icon: iconToUse
		});
	},
	
	// When user clicks on Path button in the panel this event will fire 
	onPoiDetailPathButtonClicked: function onPoiDetailpathButtonClickedFn() {
		$("#panel-poidetail").panel("close", 123);
		World.pathMarker = World.currentMarker;
		World.currentMarker.setSelected(World.pathMarker);
		World.currentMarker.setSelected(World.currentMarker);
		World.currentMarker.directionIndicatorDrawable.enabled = true;
	},
	
	// location updates, fired every time you call architectView.setLocation() in native environment
	locationChanged: function locationChangedFn(lat, lon, alt, acc) {
		
		if (World.locationUpdateCounter === 0) {
			// update placemark distance information frequently
			World.updateDistanceToUserValues();
		}
		// helper used to update placemark information every now
		World.locationUpdateCounter = (++World.locationUpdateCounter % World.updatePlacemarkDistancesEveryXLocationUpdates);
		
		if(key == true){
			World.updateVisMarkers();
		}
		
	},
	
	// fired when user pressed maker in cam
	onMarkerSelected: function onMarkerSelectedFn(marker) {
		
		World.currentMarker = marker;

		if( visibilityKey==true){
        //        		document.getElementById("favhref").style.display="none";
        //        		document.getElementById("ratehref").style.display="none";
        document.getElementById("favhref").style.visibility="hidden";
        document.getElementById("ratehref").style.visibility="hidden";
                		}



		if(World.currentMarker.poiData.isfav == "1"){
                 document.getElementById('favImg').src='assets/unlikesmall.png';
                 }else{
                 document.getElementById('favImg').src='assets/likesmall.png';
                			}
		// update panel values
		$("#poi-detail-title").html(marker.poiData.title);
		$("#poi-detail-description").html(marker.poiData.description);
		
		/* It's ok for AR.Location subclass objects to return a distance of `undefined`. In case such a distance was calculated when all distances were queried in `updateDistanceToUserValues`, we recalcualte this specific distance before we update the UI. */
		if( undefined == marker.distanceToUser ) {
			marker.distanceToUser = marker.markerObject.locations[0].distanceToUser();
		}
		
		var  distanceToUserValue = (marker.distanceToUser > 999) ? ((marker.distanceToUser / 1000).toFixed(2) + " km") : (Math.round(marker.distanceToUser) + " m");
		
		
		$("#poi-detail-distance").html(distanceToUserValue);
		
		// show panel
		$("#panel-poidetail").panel("open", 123);
		
		$(".ui-panel-dismiss").unbind("mousedown");
		
		$("#panel-poidetail").on("panelbeforeclose", function(event, ui) {
			World.currentMarker.setDeselected(World.currentMarker);
		});
		
		if(World.pathMarker!=null)
		World.currentMarker.setDeselected(World.pathMarker);





	},
	
	// screen was clicked but no geo-object was hit
	onScreenClick: function onScreenClickFn() {
		// you may handle clicks on empty AR space too
		World.currentMarker.setDeselected(World.currentMarker);
	},
	updateVisMarkers: function updateVisMarkersfn(){
		
		var maxRangeMeters = 500;
		
		// update culling distance, so only places within given range are rendered
		AR.context.scene.cullingDistance = Math.max(maxRangeMeters, 1);
		
		// update radar's maxDistance so radius of radar is updated too
		PoiRadar.setMaxDistance(Math.max(maxRangeMeters, 1));
	},
	// update position of radar
	handlePanelMovements: function handlePanelMovementsFn() {
		
		$("#panel-distance").on("panelclose", function(event, ui) {
			$("#radarContainer").addClass("radarContainer_left");
			$("#radarContainer").removeClass("radarContainer_right");
			PoiRadar.updatePosition();
		});
		
		$("#panel-distance").on("panelopen", function(event, ui) {
			$("#radarContainer").removeClass("radarContainer_left");
			$("#radarContainer").addClass("radarContainer_right");
			PoiRadar.updatePosition();
		});
	},
	
	
	// helper to sort places by distance
	sortByDistanceSorting: function(a, b) {
		return a.distanceToUser - b.distanceToUser;
	},
	
	// helper to sort places by distance, descending
	sortByDistanceSortingDescending: function(a, b) {
		return b.distanceToUser - a.distanceToUser;
	},
	
	// When user clicks on Favorite button in the panel this event will fire 
	onPoiDetailFavoriteButtonClicked: function onPoiDetailFavoriteButtonClickedFn() {


		 if(World.currentMarker.poiData.isfav == "2"){
         document.getElementById('favImg').src='assets/unlikesmall.png';
         World.currentMarker.poiData.isfav = "1";
         World.choice = 2;
                }else{
        		document.getElementById('favImg').src='assets/likesmall.png';
        		World.currentMarker.poiData.isfav = "2";
        		World.choice = 1;
        		}

		var JsObj = {
			"type":"favorite",
			"x": World.currentMarker.poiData.latitude,
			"y": World.currentMarker.poiData.longitude,
			"choice":World.currentMarker.poiData.isfav
            };
		AR.platform.sendJSONObject(JsObj);



	},
	// When user clicks on Rating button in the panel this event will fire 
	onPoiDetailRatingButtonClicked: function onPoiDetailRatingButtonClickedFn() {
    	var JsObj = {"type":"Rate",
			"x": World.currentMarker.poiData.longitude,
		"y": World.currentMarker.poiData.latitude };
		
		AR.platform.sendJSONObject(JsObj);
	},
	//call data from server , using architectView.callJavascript()
	dbData:function dbDatafn(datafromJSONarry){
		World.loadPoisFromJsonData(datafromJSONarry);
	}


	};
	
	/* forward locationChanges to custom function */
	AR.context.onLocationChanged = World.locationChanged;
	
	/* forward clicks in empty area to World */
	AR.context.onScreenClick = World.onScreenClick;
	
	// for debug on AR
  AR.logger.activateDebugMode();
