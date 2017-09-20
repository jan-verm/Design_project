// Keeps track of all highlighted boxes.
var allHighlights = [];

// The document file url.
var notesUrl ="";

// Globals for document scrolling. Scrolling is unavailable if document is loaded. Save destination for later.
var globalScrollYCoord = null;
var globalScrollXCoord = null;
var globalScrollPageNumber = null;
var globalScrollId = null;

/**
* Function will return ALL text as joined boxes, without duplicates.
* @return [Object] This array has the coordinates (coords) and page index (pageIndex) for every box.
* The coordinates are dependent on the scale and rotation. For universal pdf coordinates, see convertToPdfPoints function.
*/
function getHightlightCoordsIndividual() {
	var pageIndex = PDFViewerApplication.pdfViewer.currentPageNumber - 1;
	var page = PDFViewerApplication.pdfViewer.getPageView(pageIndex);
	var pageRect = page.canvas.getClientRects()[0];
	
	if(window.getSelection().rangeCount===0){
		return [];
	}
	var selectionRects = window.getSelection().getRangeAt(0).getClientRects();
	var viewport = page.viewport;
	var selected = []
	
	// Filter out all duplicate and unwanted boxes.
	var selectionRectsNoDup = [];
	for( i=0; i < selectionRects.length; i++){
		var encountered = 0;
		var found = 0;
		for( j=i+1; j < selectionRects.length; j++){
			if( Math.abs(selectionRects[i].left - selectionRects[j].left) < 3 && Math.abs(selectionRects[i].top - selectionRects[j].top) < 3){
				encountered = 1;
				// Only keep the highest of the two.
				if(selectionRects[i].height < selectionRects[j].height){
					found = 0;
					break;
				} else {
					found = 1;
				}
			}
			// Filter out boxes of unwanted hight such as full page or footer-like.
			if(selectionRects[i].height > 50){
				encountered = 1;
			}
		}
		if(!encountered || (found&&!encountered)){
			if(selectionRects[i].height !== 0 && selectionRects[i].height < page.height){
				selectionRectsNoDup.push(selectionRects[i]);
			}
		}
	}
	var pagedBoxes = convertToPages(selectionRectsNoDup, pageRect, pageIndex, viewport, pageRect, 10);
	var selectionRectsNoDupBoxed = createBoxes(pagedBoxes, 20);
	return selectionRectsNoDupBoxed;
}

/**
* Split boxes to corresponding page number.
* Only works when ONE page transition is visible in the viewer.
*/
function convertToPages(rects, page, pageIndex, viewport, pageRect, transitionHeight){
	var pagedBoxes = {};
	pagedBoxes[pageIndex-1]=[];
	pagedBoxes[pageIndex+1]=[];
	pagedBoxes[pageIndex]=[];
	for( i=0; i < rects.length; i++){
		var rect = viewport.convertToPdfPoint(rects[i].left - pageRect.left, rects[i].top - pageRect.top)
		 	.concat(viewport.convertToPdfPoint(rects[i].right - pageRect.left, rects[i].bottom - pageRect.top))
		var bounds = viewport.convertToViewportRectangle(rect);

		if(Math.min(bounds[1], bounds[3])<0){
			bounds[1] += pageRect.height + transitionHeight;
			bounds[3] += pageRect.height + transitionHeight;
			pagedBoxes[pageIndex-1].push(bounds);
		} else if(Math.min(bounds[1], bounds[3])>pageRect.height){
			bounds[1] -= pageRect.height + transitionHeight;
			bounds[3] -= pageRect.height + transitionHeight;
			pagedBoxes[pageIndex+1].push(bounds);
		} else {
			pagedBoxes[pageIndex].push(bounds);
		}	
	}
	return pagedBoxes;
}

/**
* Join boxes into groups. Input is a list of individual boxes that are overlapping or have a small 'space' between them.
* This function will calculate the bounds of a bigger box to collect the small boxes.
* @return [Object] This array has the coordinates (coords) and page index (pageIndex) for every box.
*/
function createBoxes(rects, space){
	var boxes = [];
	
	for (var rect in rects){
		if(rects[rect].length<1){
			continue;
		}
		var previous = [rects[rect][0][0],rects[rect][0][1],rects[rect][0][2],rects[rect][0][3]];
		for ( i=1; i < rects[rect].length; i++){
			var bounds = rects[rect][i];
			if( Math.abs(bounds[1]-previous[3])<space){
				previous[0] = Math.min(previous[0], bounds[0]);
				previous[1] = Math.min(previous[1], bounds[1]);
				previous[2] = Math.max(previous[2], bounds[2]);
				previous[3] = Math.max(previous[3], bounds[3]);
			}
			else{
				boxes.push({"pageIndex":rect, coords:previous});
				previous = bounds;
			}
		}
		boxes.push({"pageIndex":rect, coords:previous});
	}
	return boxes;
}

/**
* Function takes a list of boxes in pdfPoints and their pageIndex and draws all the highlighted boxes.
*/
function showHighlight(selected) {
	// Start with removing all drawn highlights.
	removeElementsByClass("classicAnnotation");

	selected.forEach(function (rect) {
		if (!rect.visible){
			return;
		}
		var page = PDFViewerApplication.pdfViewer.getPageView(rect.pageIndex);
		if(page===undefined){
			return;
		}
		if(page.canvas===undefined){
			return;
		}
		var pageElement = page.canvas.parentElement;
		var viewport = page.viewport;
		var bounds = viewport.convertToViewportRectangle(rect.coords);
		var el = document.createElement('div');
		el.setAttribute('style', 'position: absolute; background-color: ' + rect.color + ';' + 
		'left:' + Math.min(bounds[0], bounds[2]) + 'px; top:' + Math.min(bounds[1], bounds[3]) + 'px;' +
		'width:' + Math.abs(bounds[0] - bounds[2]) + 'px; height:' + Math.abs(bounds[1] - bounds[3]) + 'px; opacity: 0.4');
		el.classList.add('classicAnnotation');
		el.setAttribute('commentId',rect.id);
		// When this div is clicked, scroll to the comment displayed in the parent.
		el.setAttribute('onclick', 'parent.scrollToComment(' + rect.id + ',0);');
		
		// Add the new div to the annotationLayer of this page.
		if(pageElement.parentNode.getElementsByClassName("annotationLayer")[0]===undefined){
			return;
		}

		pageElement.parentNode.getElementsByClassName("annotationLayer")[0].appendChild(el);
	});	
}

/**
* Function to convert all boxes coordinates to pdf coordinates.
* Function takes a list of boxes and their pageIndex.
* @return [Object] list of coordinates (coords) and their pageIndex
*/
function convertToPdfPoints(selected){
	var pdfCoords = [];
	for (i in selected){
		var box = selected[i];
		var viewport = PDFViewerApplication.pdfViewer.getPageView(box.pageIndex).viewport;
		var pdfCoord = viewport.convertToPdfPoint(box.coords[0],box.coords[1]).concat(viewport.convertToPdfPoint(box.coords[2],box.coords[3]));
    	pdfCoords.push({"pageIndex":box.pageIndex, coords:pdfCoord});
	}
	return pdfCoords;
}

/**
* Remove all elements with a class equal to className from the DOM.
*/
function removeElementsByClass(className){
    var elements = document.getElementsByClassName(className);
    while(elements.length > 0){
        elements[0].parentNode.removeChild(elements[0]);
    }
}

/**
* This function is called at the 'pagerendered' event to redraw all highlights after transform or scaling.
*/
function highlightUpdate(){
	showHighlight(allHighlights);

	// Do page scrolling if there is an action waiting.
	if (globalScrollPageNumber !== null){
		scroll(globalScrollPageNumber, globalScrollXCoord, globalScrollYCoord, globalScrollId);
		setTimeout(function(){ 
			globalScrollPageNumber = null; 
		}, 500);		
	}	
}

/**
* This function is called to redraw all highlights that are in the highlights parameter.
* @return [Object] list of a list of coordinates (coords), their pageIndex and color
*/
function highlightUpdateAll(highlights){
	if(Array.isArray(highlights)){
    	allHighlights = highlights;
    } else {
    	throw new TypeError("Array for existing highlights is not an Array.");
    }
    showHighlight(allHighlights);
}

/**
* This function is called to redraw all highlights that are in the highlights parameter.
* @param newHighlightColor The color of the new highlight.
* @return [Object] list of a list of coordinates (coords) and their pageIndex
*/
function highlightAll(highlights, newHighlightColor){
	if(Array.isArray(highlights)){
    	allHighlights = highlights;
    } else {
    	throw new TypeError("Array for existing highlights is not an Array.");
    }
    return highlight(newHighlightColor);
}

/**
* This function is called to redraw all highlights and add the current selection as a new highlight.
* @param newHighlightColor The color of the new highlight.
* @return [Object] list of a list of coordinates (coords) and their pageIndex
*/
function highlight(newHighlightColor){
	var selected = getHightlightCoordsIndividual();
    var pdfPoints = convertToPdfPoints(selected);
    for ( i=0; i < pdfPoints.length; i++){
    	pdfPoints[i].color = newHighlightColor;
    	pdfPoints[i].visible = true;
    	pdfPoints[i].type = "new";
    }
    allHighlights = allHighlights.concat(pdfPoints);
    highlightUpdate();
    return pdfPoints;
}

/**
* This function will scroll the viewer to the given pageNumber and coordinates on that page.
*/
function scroll(pageNumber, coordX, coordY, commentId){
	var dest = [];
	dest[0] = null;
	dest[1] = {name:"Classic"};
	dest[2] = coordX;
	dest[3] = coordY;

	try{
		PDFViewerApplication.pdfViewer.scrollPageIntoView(pageNumber, dest);
		indicateHighlight(commentId);
	}
	catch (err){
		globalScrollYCoord = coordY;
		globalScrollXCoord = coordX;
		globalScrollPageNumber = pageNumber;
		globalScrollId = commentId;
	}
}

function indicateHighlight(commentId){
	$("div[commentId=" + commentId + "]").addClass("classicAnnotationHovered");
	setTimeout(function(){
		$("div[commentId=" + commentId + "]").removeClass("classicAnnotationHovered");
	}, 3000);
}

function test(){
	console.log("test");
	allHighlights.forEach(function (rect) {
		console.log(rect.visible);
	});
}