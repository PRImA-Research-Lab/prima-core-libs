/*
 * Copyright 2015 PRImA Research Lab, University of Salford, United Kingdom
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primaresearch.dla.page.io.xml.sax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.primaresearch.dla.page.MetaData;
import org.primaresearch.dla.page.Page;
import org.primaresearch.dla.page.Page.MeasurementUnit;
import org.primaresearch.dla.page.layout.GeometricObjectImpl;
import org.primaresearch.dla.page.layout.PageLayout;
import org.primaresearch.dla.page.layout.logical.Group;
import org.primaresearch.dla.page.layout.logical.ReadingOrder;
import org.primaresearch.dla.page.layout.physical.ContentIterator;
import org.primaresearch.dla.page.layout.physical.Region;
import org.primaresearch.dla.page.layout.physical.impl.ImageRegion;
import org.primaresearch.dla.page.layout.physical.impl.SeparatorRegion;
import org.primaresearch.dla.page.layout.physical.shared.RegionType;
import org.primaresearch.dla.page.layout.physical.text.impl.TextLine;
import org.primaresearch.dla.page.layout.physical.text.impl.TextRegion;
import org.primaresearch.dla.page.layout.physical.text.impl.Word;
import org.primaresearch.ident.IdRegister.InvalidIdException;
import org.primaresearch.io.xml.XmlFormatVersion;
import org.primaresearch.io.xml.XmlModelAndValidatorProvider;
import org.primaresearch.io.xml.XmlModelAndValidatorProvider.UnsupportedSchemaVersionException;
import org.primaresearch.maths.geometry.Polygon;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Experimental SAX XML handler to read ALTO XML files.
 * Can also be used for schema version 3.0 since there were no significant changes.
 * 
 * @author Christian Clausner
 *
 */
public class SaxPageHandler_Alto_2_1 extends SaxPageHandler {

	private static final String ELEMENT_alto						= "alto";
	//private static final String ELEMENT_Description					= "Description";
	//private static final String ELEMENT_Layout						= "Layout";
	private static final String ELEMENT_MeasurementUnit				= "MeasurementUnit";
	//private static final String ELEMENT_sourceImageInformation		= "sourceImageInformation";
	private static final String ELEMENT_fileName					= "fileName";
	private static final String ELEMENT_OCRProcessing				= "OCRProcessing";
	private static final String ELEMENT_preProcessingStep			= "preProcessingStep";
	private static final String ELEMENT_ocrProcessingStep			= "ocrProcessingStep";
	private static final String ELEMENT_postProcessingStep			= "postProcessingStep";
	private static final String ELEMENT_processingDateTime			= "processingDateTime";
	private static final String ELEMENT_processingAgency			= "processingAgency";
	private static final String ELEMENT_processingStepDescription	= "processingStepDescription";
	private static final String ELEMENT_processingStepSettings		= "processingStepSettings";
	private static final String ELEMENT_processingSoftware			= "processingSoftware";
	private static final String ELEMENT_softwareName				= "softwareName";
	private static final String ELEMENT_softwareCreator				= "softwareCreator";
	private static final String ELEMENT_softwareVersion				= "softwareVersion";
	private static final String ELEMENT_applicationDescription		= "applicationDescription";
	private static final String ELEMENT_Page				= "Page";
	private static final String ELEMENT_TopMargin			= "TopMargin";
	private static final String ELEMENT_LeftMargin			= "LeftMargin";
	private static final String ELEMENT_RightMargin			= "RightMargin";
	private static final String ELEMENT_BottomMargin		= "BottomMargin";
	private static final String ELEMENT_PrintSpace			= "PrintSpace";
	private static final String ELEMENT_TextBlock			= "TextBlock";
	private static final String ELEMENT_Illustration		= "Illustration";
	private static final String ELEMENT_GraphicalElement	= "GraphicalElement";
	private static final String ELEMENT_ComposedBlock		= "ComposedBlock";
	//private static final String ELEMENT_Shape				= "Shape";
	private static final String ELEMENT_Polygon				= "Polygon";
	private static final String ELEMENT_Ellipse				= "Ellipse";
	private static final String ELEMENT_Circle				= "Circle";
	private static final String ELEMENT_TextLine			= "TextLine";
	private static final String ELEMENT_String				= "String";
	private static final String ELEMENT_HYP					= "HYP";
	
	private static final String ATTR_ID					= "ID";
	private static final String ATTR_IDNEXT				= "IDNEXT";
	private static final String ATTR_PAGECLASS			= "PAGECLASS";
	private static final String ATTR_HEIGHT				= "HEIGHT";
	private static final String ATTR_WIDTH				= "WIDTH";
	private static final String ATTR_PHYSICAL_IMG_NR	= "PHYSICAL_IMG_NR";
	private static final String ATTR_PRINTED_IMG_NR		= "PRINTED_IMG_NR";
	private static final String ATTR_QUALITY			= "QUALITY";
	private static final String ATTR_QUALITY_DETAIL		= "QUALITY_DETAIL";
	private static final String ATTR_POSITION			= "POSITION";
	private static final String ATTR_PROCESSING			= "PROCESSING";
	private static final String ATTR_ACCURACY			= "ACCURACY";
	private static final String ATTR_PC					= "PC";
	private static final String ATTR_HPOS				= "HPOS";
	private static final String ATTR_VPOS				= "VPOS";
	private static final String ATTR_ROTATION			= "ROTATION";
	private static final String ATTR_POINTS				= "POINTS";
	private static final String ATTR_RADIUS				= "RADIUS";
	private static final String ATTR_HLENGTH			= "HLENGTH";
	private static final String ATTR_VLENGTH			= "VLENGTH";
	private static final String ATTR_CONTENT			= "CONTENT";
	private static final String ATTR_LANG				= "LANG";

	
	private Page page;
	private PageLayout layout = null;
	private MetaData metaData = null;
	private String comments;
	private StringBuffer currentTextBuffer;
	private String currentText;
	private MeasurementUnit measurementUnit = MeasurementUnit.MM_BY_10; //Default
	private boolean firstPageDone = false;
	private boolean hasMargins = false;
	private Region currentRegion = null;
	private TextLine currentTextLine = null;
	private Word currentWord = null;
	private Word lastWord = null;
	private Map<String, List<String>> idPartialReadingOrderMap = new HashMap<String, List<String>>();
	private List<List<String>> partialReadingOrder = new ArrayList<List<String>>();

	private XmlModelAndValidatorProvider validatorProvider;
	private XmlFormatVersion schemaVersion;
	
	/**
	 * Constructor 
	 */
	public SaxPageHandler_Alto_2_1(XmlModelAndValidatorProvider validatorProvider, XmlFormatVersion schemaVersion) {
		this.validatorProvider = validatorProvider;
		this.schemaVersion = schemaVersion;
	}

	
	@Override
	public Page getPageObject() {
		return page;
	}
	
	/**
	 * Creates a new page 
	 */
	private void createPageObject() {
    	if (validatorProvider != null && schemaVersion != null) {
    		try {
				page = new Page(validatorProvider.getSchemaParser(validatorProvider.getLatestSchemaVersion()));
			} catch (UnsupportedSchemaVersionException e) {
				e.printStackTrace();
				page = new Page();
			}
    	}
    	else
    		page = new Page();
    	
		layout = page.getLayout();
		metaData = page.getMetaData();
		comments = "Converted from ALTO";
		page.setMeasurementUnit(measurementUnit);
	}
	
	/**
	 * Receive notification of the start of an element.
	 * 
	 * @param namespaceURI - The Namespace URI, or the empty string if the element has no Namespace URI or if Namespace processing is not being performed.
	 * @param localName - The local name (without prefix), or the empty string if Namespace processing is not being performed.
	 * @param qName - The qualified name (with prefix), or the empty string if qualified names are not available.
	 * @param atts - The attributes attached to the element. If there are no attributes, it shall be an empty Attributes object.
	 * @throws SAXException - Any SAX exception, possibly wrapping another exception.
	 */
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
	      throws SAXException {

		if (firstPageDone) //No multi-page support
			return;
		
		//Handle accumulated text
		finishText();

	    if (ELEMENT_alto.equals(localName)){
	    	createPageObject();
	    }
	    else if (ELEMENT_OCRProcessing.equals(localName)){
	    	comments += "\nOCR Processing Information";
	    }
	    else if (ELEMENT_preProcessingStep.equals(localName)){
	    	comments += "\nPreprocessing:";
	    }
	    else if (ELEMENT_ocrProcessingStep.equals(localName)){
	    	comments += "\nOCR:";
	    }
	    else if (ELEMENT_postProcessingStep.equals(localName)){
	    	comments += "\nPostprocessing:";
	    }
	    else if (ELEMENT_Page.equals(localName)){
	    	handlePageNode(atts);
	    }
	    else if (ELEMENT_TopMargin.equals(localName)
	    		|| ELEMENT_LeftMargin.equals(localName)
	    		|| ELEMENT_BottomMargin.equals(localName)
	    		|| ELEMENT_RightMargin.equals(localName)){
	    	hasMargins = true;
	    }
	    else if (ELEMENT_PrintSpace.equals(localName)) {
	    	handlePrintSpaceNode(atts);
	    }
	    else if (ELEMENT_TextBlock.equals(localName)) {
	    	handleBlockNode(atts, RegionType.TextRegion);
	    	handleTextBlock(atts);
	    }
	    else if (ELEMENT_Illustration.equals(localName)) {
	    	handleBlockNode(atts, RegionType.ImageRegion);
	    	handleIllustrationBlock(atts);
	    }
	    else if (ELEMENT_GraphicalElement.equals(localName)) {
	    	handleBlockNode(atts, RegionType.SeparatorRegion);
	    	handleGraphicsBlock(atts);
	    }
	    else if (ELEMENT_ComposedBlock.equals(localName)) {
	    	//At the moment we do not create a region for composed blocks.
	    	//Only non-composed children get a region.
	    }
	    else if (ELEMENT_Polygon.equals(localName)) {
	    	handlePolygonNode(atts);
	    }
	    else if (ELEMENT_Ellipse.equals(localName)) {
	    	handleEllipseNode(atts);
	    }
	    else if (ELEMENT_Circle.equals(localName)) {
	    	handleCircleNode(atts);
	    }
	    else if (ELEMENT_TextLine.equals(localName)) {
	    	handleTextLineNode(atts);
	    }
	    else if (ELEMENT_String.equals(localName)) {
	    	handleWordNode(atts);
	    }
	    else if (ELEMENT_HYP.equals(localName)) {
	    	handleHyphenNode(atts);
	    }
	}
	
	
	/**
	 * Receive notification of the end of an element.
	 * 
	 * @param namespaceURI - The Namespace URI, or the empty string if the element has no Namespace URI or if Namespace processing is not being performed.
	 * @param localName - The local name (without prefix), or the empty string if Namespace processing is not being performed.
	 * @param qName - The qualified name (with prefix), or the empty string if qualified names are not available.
	 * @throws SAXException - Any SAX exception, possibly wrapping another exception.
	 */
	public void endElement(String namespaceURI, String localName, String qName)
	      throws SAXException {
		
	
		//Handle accumulated text
		finishText();

	    if (ELEMENT_alto.equals(localName)){
	    	metaData.setComments(comments);
	    	createReadingOrder();
	    	composehighLevelText();
	    }
	    
		if (firstPageDone) //No multi-page support
			return;

	    if (ELEMENT_MeasurementUnit.equals(localName)){
	    	handleMeasurementUnit(currentText);
	    }
	    else if (ELEMENT_fileName.equals(localName)){
	    	handleFilename(currentText);
	    }
	    else if (ELEMENT_processingDateTime.equals(localName)){
	    	comments += "\n  Date: "+currentText;
	    }
	    else if (ELEMENT_processingAgency.equals(localName)){
	    	comments += "\n  Agency: "+currentText;
	    }
	    else if (ELEMENT_processingStepDescription.equals(localName)){
	    	comments += "\n  Description: "+currentText;
	    }
	    else if (ELEMENT_processingStepSettings.equals(localName)){
	    	comments += "\n  Settings: "+currentText;
	    }
	    else if (ELEMENT_processingSoftware.equals(localName)){
	    	comments += "\n  Software: ";
	    }
	    else if (ELEMENT_softwareCreator.equals(localName)){
	    	comments += "\n    Creator: "+currentText;
	    }
	    else if (ELEMENT_softwareName.equals(localName)){
	    	comments += "\n    Name: "+currentText;
	    }
	    else if (ELEMENT_softwareVersion.equals(localName)){
	    	comments += "\n    Version: "+currentText;
	    }
	    else if (ELEMENT_applicationDescription.equals(localName)){
	    	comments += "\n    Application: "+currentText;
	    }
	    else if (ELEMENT_Page.equals(localName)){
	    	firstPageDone = true;
	    }
	    else if (ELEMENT_TextBlock.equals(localName)) {
	    	currentRegion = null;
	    }
	    else if (ELEMENT_Illustration.equals(localName)) {
	    	currentRegion = null;
	    }
	    else if (ELEMENT_GraphicalElement.equals(localName)) {
	    	currentRegion = null;
	    }
	    else if (ELEMENT_ComposedBlock.equals(localName)) {
	    	currentRegion = null;
	    }
	    else if (ELEMENT_TextLine.equals(localName)) {
	    	currentTextLine = null;
	    	lastWord = null;
	    }
	    else if (ELEMENT_String.equals(localName)) {
	    	currentWord = null;
	    }
	    
	}

	/**
	 * Receive notification of character data inside an element.
	 * @param ch - The characters.
	 * @param start - The start position in the character array.
	 * @param length - The number of characters to use from the character array.
	 * @throws SAXException - Any SAX exception, possibly wrapping another exception.
	 */
	public void characters(char[] ch, int start, int length)
	      throws SAXException {
		
		if (firstPageDone) //No multi-page support
			return;

		String strValue = new String(ch, start, length);
		
		//Text might be parsed bit by bit, so we have to accumulate until a closing tag is found.
		if (currentTextBuffer == null)
			currentTextBuffer = new StringBuffer();
		currentTextBuffer.append(strValue);

	}
	
	/**
	 * Writes accumulated text to the right object. 
	 */
	private void finishText() {
		if (firstPageDone) //No multi-page support
			return;
		if (currentTextBuffer != null) {
			currentText = currentTextBuffer.toString();

			currentTextBuffer = null;
		}
	}
	
	/**
	 * Creates the text region and text line text content from the words
	 */
	private void composehighLevelText() {
		for (ContentIterator it = layout.iterator(RegionType.TextRegion); it.hasNext(); ) {
			((TextRegion)it.next()).composeText(true, true);
		}
	}
	
	private void handleMeasurementUnit(String textContent) {
		comments += "\n\nMeasurement unit: "+textContent;

		if ("pixel".equals(textContent))
			measurementUnit = MeasurementUnit.PIXEL;
		else if ("mm10".equals(textContent))
			measurementUnit = MeasurementUnit.MM_BY_10;
		else if ("inch1200".equals(textContent))
			measurementUnit = MeasurementUnit.INCH_BY_1200;
		
		if (page != null)
			page.setMeasurementUnit(measurementUnit);
	}

	private void handleFilename(String textContent) {
		page.setImageFilename(textContent);
	}

	private void handlePageNode(Attributes atts) {
		int i;
		//Id
		if ((i = atts.getIndex(ATTR_ID)) >= 0) {
			try {
				page.setGtsId(atts.getValue(i));
			} catch (InvalidIdException e) {
				e.printStackTrace();
			}
		}
		//Page class
		if ((i = atts.getIndex(ATTR_PAGECLASS)) >= 0) {
			comments += "\nPage class: "+atts.getValue(i);
		}
		//Width + height
		if ((i = atts.getIndex(ATTR_WIDTH)) >= 0) {
			int width = (int)Double.parseDouble(atts.getValue(i));
			if ((i = atts.getIndex(ATTR_HEIGHT)) >= 0)
				layout.setSize(width, (int)Double.parseDouble(atts.getValue(i)));
		}
		//Physical image number
		if ((i = atts.getIndex(ATTR_PHYSICAL_IMG_NR)) >= 0) {
			comments += "\nPhysical image number: "+atts.getValue(i);
		}
		//Printed image number
		if ((i = atts.getIndex(ATTR_PRINTED_IMG_NR)) >= 0) {
			comments += "\nPrinted image number: "+atts.getValue(i);
		}
		//Quality
		if ((i = atts.getIndex(ATTR_QUALITY)) >= 0) {
			comments += "\nQuality: "+atts.getValue(i);
		}
		//Quality details
		if ((i = atts.getIndex(ATTR_QUALITY_DETAIL)) >= 0) {
			comments += "\n Quality details: "+atts.getValue(i);
		}
		//Position
		if ((i = atts.getIndex(ATTR_POSITION)) >= 0) {
			comments += "\nPosition: "+atts.getValue(i);
		}
		//Processing ID
		if ((i = atts.getIndex(ATTR_PROCESSING)) >= 0) {
			comments += "\nProcessing ID: "+atts.getValue(i);
		}
		//Accuracy
		if ((i = atts.getIndex(ATTR_ACCURACY)) >= 0) {
			comments += "\nAccuracy: "+atts.getValue(i);
		}
		//Confidence
		if ((i = atts.getIndex(ATTR_PC)) >= 0) {
			comments += "\nConfidence: "+atts.getValue(i);
		}
	}
	
	private void handlePrintSpaceNode(Attributes atts) {
		int i;
		
		//Width + height
		if (!hasMargins && layout.getWidth() <= 0) { 
			//No width defined in page node
			//and there is no margin (meaning the print space equals the full page)
			if ((i = atts.getIndex(ATTR_WIDTH)) >= 0) {
				int width = (int)Double.parseDouble(atts.getValue(i));
				if ((i = atts.getIndex(ATTR_HEIGHT)) >= 0)
					layout.setSize(width, (int)Double.parseDouble(atts.getValue(i)));
			}
		}
		
		//Polygon
		if (hasMargins) {
			int w=0, h=0, l=0, t=0;
			if ((i = atts.getIndex(ATTR_WIDTH)) >= 0) 
				w = (int)Double.parseDouble(atts.getValue(i));
			if ((i = atts.getIndex(ATTR_HEIGHT)) >= 0) 
				h = (int)Double.parseDouble(atts.getValue(i));
			if ((i = atts.getIndex(ATTR_HPOS)) >= 0) 
				l = (int)Double.parseDouble(atts.getValue(i));
			if ((i = atts.getIndex(ATTR_VPOS)) >= 0) 
				t = (int)Double.parseDouble(atts.getValue(i));
			layout.setPrintSpace(new GeometricObjectImpl(createPolygonFromBoundingBox(l, t, w, h)));
		}
	}
	
	private Polygon createPolygonFromBoundingBox(int left, int top, int width, int height) {
		int right = left+width-1;
		int bottom = top+height-1;
		Polygon polygon = new Polygon();
		
		polygon.addPoint(left, top);
		polygon.addPoint(right, top);
		polygon.addPoint(right, bottom);
		polygon.addPoint(left, bottom);
		return polygon;
	}
	
	private void handleBlockNode(Attributes atts, RegionType type) {
		int i;
		//Id
		String id = null;
		if ((i = atts.getIndex(ATTR_ID)) >= 0)
			id = atts.getValue(i);
		
		if (id != null)
			currentRegion = layout.createRegion(type, id);
		else
			currentRegion = layout.createRegion(type);
		
		//IdNext
		if ((i = atts.getIndex(ATTR_IDNEXT)) >= 0) 
			addRelationToReadingOrder(id, atts.getValue(i));
		
		//Polygon from bounding box
		int w=0, h=0, l=0, t=0;
		if ((i = atts.getIndex(ATTR_WIDTH)) >= 0) 
			w = (int)Double.parseDouble(atts.getValue(i));
		if ((i = atts.getIndex(ATTR_HEIGHT)) >= 0) 
			h = (int)Double.parseDouble(atts.getValue(i));
		if ((i = atts.getIndex(ATTR_HPOS)) >= 0) 
			l = (int)Double.parseDouble(atts.getValue(i));
		if ((i = atts.getIndex(ATTR_VPOS)) >= 0) 
			t = (int)Double.parseDouble(atts.getValue(i));
		currentRegion.setCoords(createPolygonFromBoundingBox(l, t, w, h));
	}

	private void handleTextBlock(Attributes atts) {
		int i;
		TextRegion region = (TextRegion)currentRegion;
		
		//Rotation
		if ((i = atts.getIndex(ATTR_ROTATION)) >= 0)
			region.setOrientation(Double.parseDouble(atts.getValue(i)));
		
		//Language
		if ((i = atts.getIndex(ATTR_LANG)) >= 0) {
			String language = altoToPrimaLanguage(atts.getValue(i));
			if (language != null)
				region.setPrimaryLanguage(language);
		}
	}
	
	private void handleTextLineNode(Attributes atts) {
		if (currentRegion == null || !(currentRegion instanceof TextRegion))
			return;
		
		
		//Create line
		currentTextLine = ((TextRegion)currentRegion).createTextLine();
		
		int i;
		
		//Language
		if ((i = atts.getIndex(ATTR_LANG)) >= 0) {
			String language = altoToPrimaLanguage(atts.getValue(i));
			if (language != null)
				currentTextLine.setPrimaryLanguage(language);
		}
		
		//Polygon from bounding box
		int w=0, h=0, l=0, t=0;
		if ((i = atts.getIndex(ATTR_WIDTH)) >= 0) 
			w = (int)Double.parseDouble(atts.getValue(i));
		if ((i = atts.getIndex(ATTR_HEIGHT)) >= 0) 
			h = (int)Double.parseDouble(atts.getValue(i));
		if ((i = atts.getIndex(ATTR_HPOS)) >= 0) 
			l = (int)Double.parseDouble(atts.getValue(i));
		if ((i = atts.getIndex(ATTR_VPOS)) >= 0) 
			t = (int)Double.parseDouble(atts.getValue(i));
		currentTextLine.setCoords(createPolygonFromBoundingBox(l, t, w, h));
	}
	
	private void handleWordNode(Attributes atts) {
		if (currentTextLine == null)
			return;
		
		//Create word
		currentWord = currentTextLine.createWord();
		lastWord = currentWord;
		
		int i;
		
		//Language
		if ((i = atts.getIndex(ATTR_LANG)) >= 0) {
			String language = altoToPrimaLanguage(atts.getValue(i));
			if (language != null)
				currentWord.setLanguage(language);
		}
		
		//Polygon from bounding box
		int w=0, h=0, l=0, t=0;
		if ((i = atts.getIndex(ATTR_WIDTH)) >= 0) 
			w = (int)Double.parseDouble(atts.getValue(i));
		if ((i = atts.getIndex(ATTR_HEIGHT)) >= 0) 
			h = (int)Double.parseDouble(atts.getValue(i));
		if ((i = atts.getIndex(ATTR_HPOS)) >= 0) 
			l = (int)Double.parseDouble(atts.getValue(i));
		if ((i = atts.getIndex(ATTR_VPOS)) >= 0) 
			t = (int)Double.parseDouble(atts.getValue(i));
		currentWord.setCoords(createPolygonFromBoundingBox(l, t, w, h));
		
		//Text content
		if ((i = atts.getIndex(ATTR_CONTENT)) >= 0)
			currentWord.setText(atts.getValue(i));
	}
	
	private void handleHyphenNode(Attributes atts) {
		if (lastWord == null)
			return;
		
		//Attach the hyphen to the last word
		// Text
		int i;
		String hypContent = "-";
		if ((i = atts.getIndex(ATTR_CONTENT)) >= 0) {
			char code = 0;
			//Is it a number?
			try {
				code = (char)Integer.parseInt(atts.getValue(i));
			} catch (NumberFormatException exc) {
				//No need to handle
			}
			if (code > 0) { //We got a number
				//Assuming Unicode
				hypContent = new String(Character.toString(code));
			}
			else //If the conversion to int doesn't return a positive number, we assume the content is the actual character
				hypContent = atts.getValue(i);
		}
		lastWord.setText(lastWord.getText() + hypContent);
		
		// Coords (extend word)
		int updatedRight = 0;
		Polygon polygon = lastWord.getCoords();
		if (atts.getIndex(ATTR_HPOS) >= 0 && atts.getIndex(ATTR_WIDTH) >= 0) {
			int hpos = (int)Double.parseDouble(atts.getValue(ATTR_HPOS));
			int width = (int)Double.parseDouble(atts.getValue(ATTR_WIDTH));
			
			updatedRight = hpos + width - 1;
		}
		//Missing coordinates
		else {
			//Use text line
			TextLine parentLine = (TextLine)lastWord.getParent();
			if (parentLine != null && parentLine.getCoords() != null) 
				updatedRight = parentLine.getCoords().getBoundingBox().right;
		}
		if (polygon.getBoundingBox().right < updatedRight) {
			polygon.getPoint(1).x = updatedRight;   
			polygon.getPoint(2).x = updatedRight;
			lastWord.getCoords().setBoundingBoxOutdated();
		}
	}
	
	private void handleIllustrationBlock(Attributes atts) {
		int i;
		ImageRegion region = (ImageRegion)currentRegion;
		
		//Rotation
		if ((i = atts.getIndex(ATTR_ROTATION)) >= 0)
			region.setOrientation(Double.parseDouble(atts.getValue(i)));
	}
	
	private void handleGraphicsBlock(Attributes atts) {
		int i;
		SeparatorRegion region = (SeparatorRegion)currentRegion;
		
		//Rotation
		if ((i = atts.getIndex(ATTR_ROTATION)) >= 0)
			region.setOrientation(Double.parseDouble(atts.getValue(i)));
	}
	
	private String altoToPrimaLanguage(String altoLanguageValue) {
		if ("ab".equals(altoLanguageValue)) return "Abkhaz";
		if ("aa".equals(altoLanguageValue)) return "Afar";
		if ("af".equals(altoLanguageValue)) return "Afrikaans";
		if ("ak".equals(altoLanguageValue)) return "Akan";
		if ("sq".equals(altoLanguageValue)) return "Albanian";
		if ("am".equals(altoLanguageValue)) return "Amharic";
		if ("ar".equals(altoLanguageValue)) return "Arabic";
		if ("an".equals(altoLanguageValue)) return "Aragonese";
		if ("hy".equals(altoLanguageValue)) return "Armenian";
		if ("as".equals(altoLanguageValue)) return "Assamese";
		if ("av".equals(altoLanguageValue)) return "Avaric";
		if ("ae".equals(altoLanguageValue)) return "Avestan";
		if ("ay".equals(altoLanguageValue)) return "Aymara";
		if ("az".equals(altoLanguageValue)) return "Azerbaijani";
		if ("bm".equals(altoLanguageValue)) return "Bambara";
		if ("ba".equals(altoLanguageValue)) return "Bashkir";
		if ("eu".equals(altoLanguageValue)) return "Basque";
		if ("be".equals(altoLanguageValue)) return "Belarusian";
		if ("bn".equals(altoLanguageValue)) return "Bengali";
		if ("bh".equals(altoLanguageValue)) return "Bihari";
		if ("bi".equals(altoLanguageValue)) return "Bislama";
		if ("bs".equals(altoLanguageValue)) return "Bosnian";
		if ("br".equals(altoLanguageValue)) return "Breton";
		if ("bg".equals(altoLanguageValue)) return "Bulgarian";
		if ("my".equals(altoLanguageValue)) return "Burmese";
		if ("km".equals(altoLanguageValue)) return "Cambodian";
		//if ("".equals(altoLanguageValue)) return "Cantonese";
		if ("ca".equals(altoLanguageValue)) return "Catalan";
		if ("ch".equals(altoLanguageValue)) return "Chamorro";
		if ("ce".equals(altoLanguageValue)) return "Chechen";
		if ("ny".equals(altoLanguageValue)) return "Chichewa";
		if ("zh".equals(altoLanguageValue)) return "Chinese";
		if ("cv".equals(altoLanguageValue)) return "Chuvash";
		if ("kw".equals(altoLanguageValue)) return "Cornish";
		if ("co".equals(altoLanguageValue)) return "Corsican";
		if ("cr".equals(altoLanguageValue)) return "Cree";
		if ("hr".equals(altoLanguageValue)) return "Croatian";
		if ("cs".equals(altoLanguageValue)) return "Czech";
		if ("da".equals(altoLanguageValue)) return "Danish";
		if ("dv".equals(altoLanguageValue)) return "Divehi";
		if ("nl".equals(altoLanguageValue)) return "Dutch";
		if ("dz".equals(altoLanguageValue)) return "Dzongkha";
		if ("en".equals(altoLanguageValue)) return "English";
		if ("en-GB".equals(altoLanguageValue)) return "English";
		if ("en-US".equals(altoLanguageValue)) return "English";
		if ("eo".equals(altoLanguageValue)) return "Esperanto";
		if ("et".equals(altoLanguageValue)) return "Estonian";
		if ("ee".equals(altoLanguageValue)) return "Ewe";
		if ("fo".equals(altoLanguageValue)) return "Faroese";
		if ("fj".equals(altoLanguageValue)) return "Fijian";
		if ("fi".equals(altoLanguageValue)) return "Finnish";
		if ("fr".equals(altoLanguageValue)) return "French";
		if ("ff".equals(altoLanguageValue)) return "Fula";
		if ("gd".equals(altoLanguageValue)) return "Gaelic";
		if ("gl".equals(altoLanguageValue)) return "Galician";
		if ("lg".equals(altoLanguageValue)) return "Ganda";
		if ("ka".equals(altoLanguageValue)) return "Georgian";
		if ("de".equals(altoLanguageValue)) return "German";
		if ("el".equals(altoLanguageValue)) return "Greek";
		if ("gn".equals(altoLanguageValue)) return "GuaranÃ­";
		if ("gu".equals(altoLanguageValue)) return "Gujarati";
		if ("ht".equals(altoLanguageValue)) return "Haitian";
		if ("ha".equals(altoLanguageValue)) return "Hausa";
		if ("he".equals(altoLanguageValue)) return "Hebrew";
		if ("hz".equals(altoLanguageValue)) return "Herero";
		if ("hi".equals(altoLanguageValue)) return "Hindi";
		if ("ho".equals(altoLanguageValue)) return "Hiri Motu";
		if ("hu".equals(altoLanguageValue)) return "Hungarian";
		if ("is".equals(altoLanguageValue)) return "Icelandic";
		if ("io".equals(altoLanguageValue)) return "Ido";
		if ("ig".equals(altoLanguageValue)) return "Igbo";
		if ("id".equals(altoLanguageValue)) return "Indonesian";
		if ("ia".equals(altoLanguageValue)) return "Interlingua";
		if ("ie".equals(altoLanguageValue)) return "Interlingue";
		if ("iu".equals(altoLanguageValue)) return "Inuktitut";
		if ("ik".equals(altoLanguageValue)) return "Inupiaq";
		if ("ga".equals(altoLanguageValue)) return "Irish";
		if ("it".equals(altoLanguageValue)) return "Italian";
		if ("ja".equals(altoLanguageValue)) return "Japanese";
		if ("jv".equals(altoLanguageValue)) return "Javanese";
		if ("kl".equals(altoLanguageValue)) return "Kalaallisut";
		if ("kn".equals(altoLanguageValue)) return "Kannada";
		if ("kr".equals(altoLanguageValue)) return "Kanuri";
		if ("ks".equals(altoLanguageValue)) return "Kashmiri";
		if ("kk".equals(altoLanguageValue)) return "Kazakh";
		if ("km".equals(altoLanguageValue)) return "Khmer";
		if ("ki".equals(altoLanguageValue)) return "Kikuyu";
		if ("rw".equals(altoLanguageValue)) return "Kinyarwanda";
		if ("rn".equals(altoLanguageValue)) return "Kirundi";
		if ("kv".equals(altoLanguageValue)) return "Komi";
		if ("kg".equals(altoLanguageValue)) return "Kongo";
		if ("ko".equals(altoLanguageValue)) return "Korean";
		if ("ku".equals(altoLanguageValue)) return "Kurdish";
		if ("kj".equals(altoLanguageValue)) return "Kwanyama";
		if ("ky".equals(altoLanguageValue)) return "Kyrgyz";
		if ("lo".equals(altoLanguageValue)) return "Lao";
		if ("la".equals(altoLanguageValue)) return "Latin";
		if ("lv".equals(altoLanguageValue)) return "Latvian";
		if ("li".equals(altoLanguageValue)) return "Limburgish";
		if ("ln".equals(altoLanguageValue)) return "Lingala";
		if ("lt".equals(altoLanguageValue)) return "Lithuanian";
		if ("lu".equals(altoLanguageValue)) return "Luba-Katanga";
		if ("lb".equals(altoLanguageValue)) return "Luxembourgish";
		if ("mk".equals(altoLanguageValue)) return "Macedonian";
		if ("mg".equals(altoLanguageValue)) return "Malagasy";
		if ("ms".equals(altoLanguageValue)) return "Malay";
		if ("ml".equals(altoLanguageValue)) return "Malayalam";
		if ("mt".equals(altoLanguageValue)) return "Maltese";
		if ("gv".equals(altoLanguageValue)) return "Manx";
		if ("mi".equals(altoLanguageValue)) return "MÄ?ori";
		if ("mr".equals(altoLanguageValue)) return "Marathi";
		if ("mh".equals(altoLanguageValue)) return "Marshallese";
		if ("mn".equals(altoLanguageValue)) return "Mongolian";
		if ("na".equals(altoLanguageValue)) return "Nauru";
		if ("nv".equals(altoLanguageValue)) return "Navajo";
		if ("ng".equals(altoLanguageValue)) return "Ndonga";
		if ("ne".equals(altoLanguageValue)) return "Nepali";
		if ("nd".equals(altoLanguageValue)) return "North Ndebele";
		if ("se".equals(altoLanguageValue)) return "Northern Sami";
		if ("no".equals(altoLanguageValue)) return "Norwegian";
		if ("nb".equals(altoLanguageValue)) return "Norwegian BokmÃ¥l";
		if ("nn".equals(altoLanguageValue)) return "Norwegian Nynorsk";
		if ("ii".equals(altoLanguageValue)) return "Nuosu";
		if ("oc".equals(altoLanguageValue)) return "Occitan";
		if ("oj".equals(altoLanguageValue)) return "Ojibwe";
		if ("cu".equals(altoLanguageValue)) return "Old Church Slavonic";
		if ("or".equals(altoLanguageValue)) return "Oriya";
		if ("om".equals(altoLanguageValue)) return "Oromo";
		if ("os".equals(altoLanguageValue)) return "Ossetian";
		if ("pi".equals(altoLanguageValue)) return "PÄ?li";
		if ("pa".equals(altoLanguageValue)) return "Panjabi";
		if ("ps".equals(altoLanguageValue)) return "Pashto";
		if ("fa".equals(altoLanguageValue)) return "Persian";
		if ("pl".equals(altoLanguageValue)) return "Polish";
		if ("pt".equals(altoLanguageValue)) return "Portuguese";
		if ("pa".equals(altoLanguageValue)) return "Punjabi";
		if ("qu".equals(altoLanguageValue)) return "Quechua";
		if ("ro".equals(altoLanguageValue)) return "Romanian";
		if ("rm".equals(altoLanguageValue)) return "Romansh";
		if ("ru".equals(altoLanguageValue)) return "Russian";
		if ("sm".equals(altoLanguageValue)) return "Samoan";
		if ("sg".equals(altoLanguageValue)) return "Sango";
		if ("sa".equals(altoLanguageValue)) return "Sanskrit";
		if ("sc".equals(altoLanguageValue)) return "Sardinian";
		if ("sr".equals(altoLanguageValue)) return "Serbian";
		if ("sn".equals(altoLanguageValue)) return "Shona";
		if ("sd".equals(altoLanguageValue)) return "Sindhi";
		if ("si".equals(altoLanguageValue)) return "Sinhala";
		if ("sk".equals(altoLanguageValue)) return "Slovak";
		if ("sl".equals(altoLanguageValue)) return "Slovene";
		if ("so".equals(altoLanguageValue)) return "Somali";
		if ("nr".equals(altoLanguageValue)) return "South Ndebele";
		if ("st".equals(altoLanguageValue)) return "Southern Sotho";
		if ("es".equals(altoLanguageValue)) return "Spanish";
		if ("su".equals(altoLanguageValue)) return "Sundanese";
		if ("sw".equals(altoLanguageValue)) return "Swahili";
		if ("ss".equals(altoLanguageValue)) return "Swati";
		if ("sv".equals(altoLanguageValue)) return "Swedish";
		if ("tl".equals(altoLanguageValue)) return "Tagalog";
		if ("ty".equals(altoLanguageValue)) return "Tahitian";
		if ("tg".equals(altoLanguageValue)) return "Tajik";
		if ("ta".equals(altoLanguageValue)) return "Tamil";
		if ("tt".equals(altoLanguageValue)) return "Tatar";
		if ("te".equals(altoLanguageValue)) return "Telugu";
		if ("th".equals(altoLanguageValue)) return "Thai";
		if ("bo".equals(altoLanguageValue)) return "Tibetan";
		if ("ti".equals(altoLanguageValue)) return "Tigrinya";
		if ("to".equals(altoLanguageValue)) return "Tonga";
		if ("ts".equals(altoLanguageValue)) return "Tsonga";
		if ("tn".equals(altoLanguageValue)) return "Tswana";
		if ("tr".equals(altoLanguageValue)) return "Turkish";
		if ("tk".equals(altoLanguageValue)) return "Turkmen";
		if ("tw".equals(altoLanguageValue)) return "Twi";
		if ("ug".equals(altoLanguageValue)) return "Uighur";
		if ("uk".equals(altoLanguageValue)) return "Ukrainian";
		if ("ur".equals(altoLanguageValue)) return "Urdu";
		if ("uz".equals(altoLanguageValue)) return "Uzbek";
		if ("ve".equals(altoLanguageValue)) return "Venda";
		if ("vi".equals(altoLanguageValue)) return "Vietnamese";
		if ("vo".equals(altoLanguageValue)) return "VolapÃ¼k";
		if ("wa".equals(altoLanguageValue)) return "Walloon";
		if ("cy".equals(altoLanguageValue)) return "Welsh";
		if ("fy".equals(altoLanguageValue)) return "Western Frisian";
		if ("wo".equals(altoLanguageValue)) return "Wolof";
		if ("xh".equals(altoLanguageValue)) return "Xhosa";
		if ("yi".equals(altoLanguageValue)) return "Yiddish";
		if ("yo".equals(altoLanguageValue)) return "Yoruba";
		if ("za".equals(altoLanguageValue)) return "Zhuang";
		if ("zu".equals(altoLanguageValue)) return "Zulu";
		if (!altoLanguageValue.isEmpty()) return "other";

		return null;	
	}

	/**
	 * Adds a relation (from region, to region) to the temporary reading order data structure.
	 */
	private void addRelationToReadingOrder(String fromRegion, String toRegion) {
		List<String> group = null;
		
		//Find reading order group of 'toRegion' to make sure its not pointing to 'fromRegion' (illegal loop)
		if (idPartialReadingOrderMap.containsKey(toRegion)) { //Found
			group = idPartialReadingOrderMap.get(toRegion);
			for (int i=0; i<group.size()-1; i++) {
				if (group.get(i).equals(toRegion)) {
					if (group.get(i+1).equals(fromRegion)) //toRegion points to fromRegion -> loop -> ignore and return
						return;
				}
			}
		}
		
		if (group != null) { //The toRegion already has a group
			//Find 'toRegion' in the group
			for (int i=0; i<group.size(); i++) {
				if (group.get(i).equals(toRegion)) {
					//Insert fromRegion
					group.add(i+1, fromRegion);
					break;
				}
			}
			idPartialReadingOrderMap.put(fromRegion, group);
		}
		else {
			//Find reading order group of 'fromRegion'
			if (!idPartialReadingOrderMap.containsKey(fromRegion)) { //Not found -> create a group
				group = new ArrayList<String>();
				partialReadingOrder.add(group);
				//Add fromRegion
				group.add(fromRegion);
				idPartialReadingOrderMap.put(fromRegion, group);
				//Add toRegion
				group.add(toRegion);
			}
			else { //Group exists
				group = idPartialReadingOrderMap.get(fromRegion);
				//Find 'fromRegion' in the group
				for (int i=0; i<group.size(); i++) {
					if (group.get(i).equals(fromRegion)) {
						//Insert toRegion
						if (i == group.size()-1) //End
							group.add(toRegion);
						else //Somewhere in the middle
							group.add(i+1, toRegion);
						break;
					}
				}
			}
		}
		
		//Add toRegion to map
		idPartialReadingOrderMap.put(toRegion, group);
	}

	/*
	 * Creates the reading order from the temporary data structure.
	 */
	private void createReadingOrder()
	{
		if (partialReadingOrder.isEmpty())
			return;
		
		ReadingOrder order = layout.createReadingOrder();

		if (order != null) //Should always be the case
		{
			Group root = order.getRoot();

			//Only one group
			if (partialReadingOrder.size() == 1) {
				root.setOrdered(true); //All region refs are added to the root

				List<String> group = partialReadingOrder.get(0);
				for (int i=0; i<group.size(); i++) {
					root.addRegionRef(group.get(i));
				}
			}
			else //multiple groups
			{
				root.setOrdered(false); //The root is used as an unordered list of ordered groups

				for (int i=0; i<partialReadingOrder.size(); i++) {
					Group parent;
					try {
						parent = root.createChildGroup();
						List<String> group = partialReadingOrder.get(i);
						for (int j=0; j<group.size(); j++) {
							parent.addRegionRef(group.get(j));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		}
	}
	
	/**
	 * Parses a polygon node.
	 */
	private void handlePolygonNode(Attributes atts) {
		if (currentRegion == null)
			return;
		
		int i;
		
		//Points
		if ((i = atts.getIndex(ATTR_POINTS)) < 0)
			return;
					
		String points = atts.getValue(i);
		
		if (points.indexOf(" ") < 0) //Spaces assumed for separating points
			return;
		if (points.indexOf(",") < 0) //Commas assumed for separating x and y of a point
			return;

		//Split into points
		String[] splitPoints = points.split(" ");
		
		if (splitPoints.length < 3)
			return;
		
		Polygon polygon = new Polygon();
		for (i=0; i<splitPoints.length; i++) {
			//Split into x and y
			String[] xy = splitPoints[i].split(",");
			
			if (xy.length != 2)
				continue;
			
			int x = (int)Double.parseDouble(xy[0]);
			int y = (int)Double.parseDouble(xy[1]);
			
			polygon.addPoint(x, y);
		}
		
		if (polygon.getSize() >= 3)
			currentRegion.setCoords(polygon);
	}
	
	/**
	 * Parses an ellipse node.
	 */
	private void handleEllipseNode(Attributes atts) {
		if (currentRegion == null)
			return;
		
		int i;
		double x=0, y=0, horLength=0, vertLength=0;
		
		if ((i = atts.getIndex(ATTR_HPOS)) < 0)
			x = Double.parseDouble(atts.getValue(i));
		if ((i = atts.getIndex(ATTR_VPOS)) < 0)
			y = Double.parseDouble(atts.getValue(i));
		if ((i = atts.getIndex(ATTR_HLENGTH)) < 0)
			horLength = Double.parseDouble(atts.getValue(i));
		if ((i = atts.getIndex(ATTR_VLENGTH)) < 0)
			vertLength = Double.parseDouble(atts.getValue(i));
		
		double radiusX = horLength/2;
		double radiusY = vertLength/2;

		Polygon polygon = ellipseToPolygon(x, y, radiusX, radiusY);
		if (polygon != null && polygon.getSize() >= 3)
			currentRegion.setCoords(polygon);
	}

	/**
	 * Parses a circle node.
	 */
	private void handleCircleNode(Attributes atts) {
		if (currentRegion == null)
			return;
		
		int i;
		double x=0, y=0, radius=0;
		
		if ((i = atts.getIndex(ATTR_HPOS)) < 0)
			x = Double.parseDouble(atts.getValue(i));
		if ((i = atts.getIndex(ATTR_VPOS)) < 0)
			y = Double.parseDouble(atts.getValue(i));
		if ((i = atts.getIndex(ATTR_RADIUS)) < 0)
			radius = Double.parseDouble(atts.getValue(i));

		Polygon polygon = ellipseToPolygon(x, y, radius, radius);
		if (polygon != null && polygon.getSize() >= 3)
			currentRegion.setCoords(polygon);
	}

	/**
	 * Converts an ellipse to a polygon.
	 */
	private Polygon ellipseToPolygon(double centerX, double centerY, double radiusX, double radiusY) {
		
		double step = 0.00873; //0.5 degrees
		
		Polygon polygon = new Polygon();
		int x,y,xold=-1,yold=-1;
		for (double angle = 0; angle < 6.2832; angle += step) { //0 to 360 degrees
			x = (int)(centerX + Math.cos(angle) * radiusX);
			y = (int)(centerY + Math.sin(angle) * radiusY);
			if (x != xold || y != yold) //Coords changed?
				polygon.addPoint(x, y);
			xold = x;
			yold = y;
		}

		//polygon.SimplifyPolygon();
		//polygon->ConvertToIsothetic();
		return polygon;
	}
}
