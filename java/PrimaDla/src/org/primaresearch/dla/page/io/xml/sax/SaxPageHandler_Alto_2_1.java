/*
 * Copyright 2019 PRImA Research Lab, University of Salford, United Kingdom
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.primaresearch.dla.page.Page;
import org.primaresearch.dla.page.Page.MeasurementUnit;
import org.primaresearch.dla.page.io.xml.AltoXmlNames;
import org.primaresearch.dla.page.layout.GeometricObjectImpl;
import org.primaresearch.dla.page.layout.PageLayout;
import org.primaresearch.dla.page.layout.logical.Group;
import org.primaresearch.dla.page.layout.logical.ReadingOrder;
import org.primaresearch.dla.page.layout.physical.ContentIterator;
import org.primaresearch.dla.page.layout.physical.Region;
import org.primaresearch.dla.page.layout.physical.impl.ImageRegion;
import org.primaresearch.dla.page.layout.physical.impl.SeparatorRegion;
import org.primaresearch.dla.page.layout.physical.shared.RegionType;
import org.primaresearch.dla.page.layout.physical.text.impl.Glyph;
import org.primaresearch.dla.page.layout.physical.text.impl.TextLine;
import org.primaresearch.dla.page.layout.physical.text.impl.TextRegion;
import org.primaresearch.dla.page.layout.physical.text.impl.Word;
import org.primaresearch.dla.page.metadata.MetaData;
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
	private Glyph currentGlyph = null;
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

	    if (AltoXmlNames.ELEMENT_alto.equals(localName)){
	    	createPageObject();
	    }
	    else if (AltoXmlNames.ELEMENT_OCRProcessing.equals(localName)){
	    	comments += "\nOCR Processing Information";
	    }
	    else if (AltoXmlNames.ELEMENT_preProcessingStep.equals(localName)){
	    	comments += "\nPreprocessing:";
	    }
	    else if (AltoXmlNames.ELEMENT_ocrProcessingStep.equals(localName)){
	    	comments += "\nOCR:";
	    }
	    else if (AltoXmlNames.ELEMENT_postProcessingStep.equals(localName)){
	    	comments += "\nPostprocessing:";
	    }
	    else if (AltoXmlNames.ELEMENT_Page.equals(localName)){
	    	handlePageNode(atts);
	    }
	    else if (AltoXmlNames.ELEMENT_TopMargin.equals(localName)
	    		|| AltoXmlNames.ELEMENT_LeftMargin.equals(localName)
	    		|| AltoXmlNames.ELEMENT_BottomMargin.equals(localName)
	    		|| AltoXmlNames.ELEMENT_RightMargin.equals(localName)){
	    	hasMargins = true;
	    }
	    else if (AltoXmlNames.ELEMENT_PrintSpace.equals(localName)) {
	    	handlePrintSpaceNode(atts);
	    }
	    else if (AltoXmlNames.ELEMENT_TextBlock.equals(localName)) {
	    	handleBlockNode(atts, RegionType.TextRegion);
	    	handleTextBlock(atts);
	    }
	    else if (AltoXmlNames.ELEMENT_Illustration.equals(localName)) {
	    	handleBlockNode(atts, RegionType.ImageRegion);
	    	handleIllustrationBlock(atts);
	    }
	    else if (AltoXmlNames.ELEMENT_GraphicalElement.equals(localName)) {
	    	handleBlockNode(atts, RegionType.SeparatorRegion);
	    	handleGraphicsBlock(atts);
	    }
	    else if (AltoXmlNames.ELEMENT_ComposedBlock.equals(localName)) {
	    	//At the moment we do not create a region for composed blocks.
	    	//Only non-composed children get a region.
	    }
	    else if (AltoXmlNames.ELEMENT_Polygon.equals(localName)) {
	    	handlePolygonNode(atts);
	    }
	    else if (AltoXmlNames.ELEMENT_Ellipse.equals(localName)) {
	    	handleEllipseNode(atts);
	    }
	    else if (AltoXmlNames.ELEMENT_Circle.equals(localName)) {
	    	handleCircleNode(atts);
	    }
	    else if (AltoXmlNames.ELEMENT_TextLine.equals(localName)) {
	    	handleTextLineNode(atts);
	    }
	    else if (AltoXmlNames.ELEMENT_String.equals(localName)) {
	    	handleWordNode(atts);
	    }
	    else if (AltoXmlNames.ELEMENT_HYP.equals(localName)) {
	    	handleHyphenNode(atts);
	    }
	    else if (AltoXmlNames.ELEMENT_Glyph.equals(localName)) {
	    	handleglyphNode(atts);
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

	    if (AltoXmlNames.ELEMENT_alto.equals(localName)){
	    	metaData.setComments(comments);
	    	createReadingOrder();
	    	composehighLevelText();
	    }
	    
		if (firstPageDone) //No multi-page support
			return;

	    if (AltoXmlNames.ELEMENT_MeasurementUnit.equals(localName)){
	    	handleMeasurementUnit(currentText);
	    }
	    else if (AltoXmlNames.ELEMENT_fileName.equals(localName)){
	    	handleFilename(currentText);
	    }
	    else if (AltoXmlNames.ELEMENT_processingDateTime.equals(localName)){
	    	comments += "\n  Date: "+currentText;
	    }
	    else if (AltoXmlNames.ELEMENT_processingAgency.equals(localName)){
	    	comments += "\n  Agency: "+currentText;
	    }
	    else if (AltoXmlNames.ELEMENT_processingStepDescription.equals(localName)){
	    	comments += "\n  Description: "+currentText;
	    }
	    else if (AltoXmlNames.ELEMENT_processingStepSettings.equals(localName)){
	    	comments += "\n  Settings: "+currentText;
	    }
	    else if (AltoXmlNames.ELEMENT_processingSoftware.equals(localName)){
	    	comments += "\n  Software: ";
	    }
	    else if (AltoXmlNames.ELEMENT_softwareCreator.equals(localName)){
	    	comments += "\n    Creator: "+currentText;
	    }
	    else if (AltoXmlNames.ELEMENT_softwareName.equals(localName)){
	    	comments += "\n    Name: "+currentText;
	    }
	    else if (AltoXmlNames.ELEMENT_softwareVersion.equals(localName)){
	    	comments += "\n    Version: "+currentText;
	    }
	    else if (AltoXmlNames.ELEMENT_applicationDescription.equals(localName)){
	    	comments += "\n    Application: "+currentText;
	    }
	    else if (AltoXmlNames.ELEMENT_Page.equals(localName)){
	    	firstPageDone = true;
	    }
	    else if (AltoXmlNames.ELEMENT_TextBlock.equals(localName)) {
	    	currentRegion = null;
	    }
	    else if (AltoXmlNames.ELEMENT_Illustration.equals(localName)) {
	    	currentRegion = null;
	    }
	    else if (AltoXmlNames.ELEMENT_GraphicalElement.equals(localName)) {
	    	currentRegion = null;
	    }
	    else if (AltoXmlNames.ELEMENT_ComposedBlock.equals(localName)) {
	    	currentRegion = null;
	    }
	    else if (AltoXmlNames.ELEMENT_TextLine.equals(localName)) {
	    	currentTextLine = null;
	    	lastWord = null;
	    }
	    else if (AltoXmlNames.ELEMENT_String.equals(localName)) {
	    	currentWord = null;
	    }
	    else if (AltoXmlNames.ELEMENT_Glyph.equals(localName)) {
	    	currentGlyph = null;
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
		if ((i = atts.getIndex(AltoXmlNames.ATTR_ID)) >= 0) {
			try {
				page.setGtsId(atts.getValue(i));
			} catch (InvalidIdException e) {
				e.printStackTrace();
			}
		}
		//Page class
		if ((i = atts.getIndex(AltoXmlNames.ATTR_PAGECLASS)) >= 0) {
			comments += "\nPage class: "+atts.getValue(i);
		}
		//Width + height
		if ((i = atts.getIndex(AltoXmlNames.ATTR_WIDTH)) >= 0) {
			int width = (int)Double.parseDouble(atts.getValue(i));
			if ((i = atts.getIndex(AltoXmlNames.ATTR_HEIGHT)) >= 0)
				layout.setSize(width, (int)Double.parseDouble(atts.getValue(i)));
		}
		//Physical image number
		if ((i = atts.getIndex(AltoXmlNames.ATTR_PHYSICAL_IMG_NR)) >= 0) {
			comments += "\nPhysical image number: "+atts.getValue(i);
		}
		//Printed image number
		if ((i = atts.getIndex(AltoXmlNames.ATTR_PRINTED_IMG_NR)) >= 0) {
			comments += "\nPrinted image number: "+atts.getValue(i);
		}
		//Quality
		if ((i = atts.getIndex(AltoXmlNames.ATTR_QUALITY)) >= 0) {
			comments += "\nQuality: "+atts.getValue(i);
		}
		//Quality details
		if ((i = atts.getIndex(AltoXmlNames.ATTR_QUALITY_DETAIL)) >= 0) {
			comments += "\n Quality details: "+atts.getValue(i);
		}
		//Position
		if ((i = atts.getIndex(AltoXmlNames.ATTR_POSITION)) >= 0) {
			comments += "\nPosition: "+atts.getValue(i);
		}
		//Processing ID
		if ((i = atts.getIndex(AltoXmlNames.ATTR_PROCESSING)) >= 0) {
			comments += "\nProcessing ID: "+atts.getValue(i);
		}
		//Accuracy
		if ((i = atts.getIndex(AltoXmlNames.ATTR_ACCURACY)) >= 0) {
			comments += "\nAccuracy: "+atts.getValue(i);
		}
		//Confidence
		if ((i = atts.getIndex(AltoXmlNames.ATTR_PC)) >= 0) {
			comments += "\nConfidence: "+atts.getValue(i);
		}
	}
	
	private void handlePrintSpaceNode(Attributes atts) {
		int i;
		
		//Width + height
		if (!hasMargins && layout.getWidth() <= 0) { 
			//No width defined in page node
			//and there is no margin (meaning the print space equals the full page)
			if ((i = atts.getIndex(AltoXmlNames.ATTR_WIDTH)) >= 0) {
				int width = (int)Double.parseDouble(atts.getValue(i));
				if ((i = atts.getIndex(AltoXmlNames.ATTR_HEIGHT)) >= 0)
					layout.setSize(width, (int)Double.parseDouble(atts.getValue(i)));
			}
		}
		
		//Polygon
		if (hasMargins) {
			int w=0, h=0, l=0, t=0;
			if ((i = atts.getIndex(AltoXmlNames.ATTR_WIDTH)) >= 0) 
				w = (int)Double.parseDouble(atts.getValue(i));
			if ((i = atts.getIndex(AltoXmlNames.ATTR_HEIGHT)) >= 0) 
				h = (int)Double.parseDouble(atts.getValue(i));
			if ((i = atts.getIndex(AltoXmlNames.ATTR_HPOS)) >= 0) 
				l = (int)Double.parseDouble(atts.getValue(i));
			if ((i = atts.getIndex(AltoXmlNames.ATTR_VPOS)) >= 0) 
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
		if ((i = atts.getIndex(AltoXmlNames.ATTR_ID)) >= 0)
			id = atts.getValue(i);
		
		if (id != null)
			currentRegion = layout.createRegion(type, id);
		else
			currentRegion = layout.createRegion(type);
		
		//IdNext
		if ((i = atts.getIndex(AltoXmlNames.ATTR_IDNEXT)) >= 0) 
			addRelationToReadingOrder(id, atts.getValue(i));
		
		//Polygon from bounding box
		int w=0, h=0, l=0, t=0;
		if ((i = atts.getIndex(AltoXmlNames.ATTR_WIDTH)) >= 0) 
			w = (int)Double.parseDouble(atts.getValue(i));
		if ((i = atts.getIndex(AltoXmlNames.ATTR_HEIGHT)) >= 0) 
			h = (int)Double.parseDouble(atts.getValue(i));
		if ((i = atts.getIndex(AltoXmlNames.ATTR_HPOS)) >= 0) 
			l = (int)Double.parseDouble(atts.getValue(i));
		if ((i = atts.getIndex(AltoXmlNames.ATTR_VPOS)) >= 0) 
			t = (int)Double.parseDouble(atts.getValue(i));
		currentRegion.setCoords(createPolygonFromBoundingBox(l, t, w, h));
	}

	private void handleTextBlock(Attributes atts) {
		int i;
		TextRegion region = (TextRegion)currentRegion;
		
		//Rotation
		if ((i = atts.getIndex(AltoXmlNames.ATTR_ROTATION)) >= 0) {
			//ALTO's rotation is the counter-clockwise angle of the content within a block. PAGE's orientation is the clockwise angle to correct rotation/skew. Therefore the value is the same
			region.setOrientation(-Double.parseDouble(atts.getValue(i))); 
			if (region.getOrientation() > 180.0)
				region.setOrientation(region.getOrientation() - 360.0);
			if (region.getOrientation() <= -180.0)
				region.setOrientation(region.getOrientation() + 360.0);
		}
		//Language
		if ((i = atts.getIndex(AltoXmlNames.ATTR_LANG)) >= 0) {
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
		if ((i = atts.getIndex(AltoXmlNames.ATTR_LANG)) >= 0) {
			String language = altoToPrimaLanguage(atts.getValue(i));
			if (language != null)
				currentTextLine.setPrimaryLanguage(language);
		}
		
		//Polygon from bounding box
		int w=0, h=0, l=0, t=0;
		if ((i = atts.getIndex(AltoXmlNames.ATTR_WIDTH)) >= 0) 
			w = (int)Double.parseDouble(atts.getValue(i));
		if ((i = atts.getIndex(AltoXmlNames.ATTR_HEIGHT)) >= 0) 
			h = (int)Double.parseDouble(atts.getValue(i));
		if ((i = atts.getIndex(AltoXmlNames.ATTR_HPOS)) >= 0) 
			l = (int)Double.parseDouble(atts.getValue(i));
		if ((i = atts.getIndex(AltoXmlNames.ATTR_VPOS)) >= 0) 
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
		if ((i = atts.getIndex(AltoXmlNames.ATTR_LANG)) >= 0) {
			String language = altoToPrimaLanguage(atts.getValue(i));
			if (language != null)
				currentWord.setLanguage(language);
		}
		
		//Polygon from bounding box
		int w=0, h=0, l=0, t=0;
		if ((i = atts.getIndex(AltoXmlNames.ATTR_WIDTH)) >= 0) 
			w = (int)Double.parseDouble(atts.getValue(i));
		if ((i = atts.getIndex(AltoXmlNames.ATTR_HEIGHT)) >= 0) 
			h = (int)Double.parseDouble(atts.getValue(i));
		if ((i = atts.getIndex(AltoXmlNames.ATTR_HPOS)) >= 0) 
			l = (int)Double.parseDouble(atts.getValue(i));
		if ((i = atts.getIndex(AltoXmlNames.ATTR_VPOS)) >= 0) 
			t = (int)Double.parseDouble(atts.getValue(i));
		currentWord.setCoords(createPolygonFromBoundingBox(l, t, w, h));
		
		//Text content
		if ((i = atts.getIndex(AltoXmlNames.ATTR_CONTENT)) >= 0)
			currentWord.setText(atts.getValue(i));
	}
	
	private void handleglyphNode(Attributes atts) {
		if (currentWord == null)
			return;
		
		//Create glyph
		currentGlyph = currentWord.createGlyph();
		
		int i;
		
		//Polygon from bounding box
		int w=0, h=0, l=0, t=0;
		if ((i = atts.getIndex(AltoXmlNames.ATTR_WIDTH)) >= 0) 
			w = (int)Double.parseDouble(atts.getValue(i));
		if ((i = atts.getIndex(AltoXmlNames.ATTR_HEIGHT)) >= 0) 
			h = (int)Double.parseDouble(atts.getValue(i));
		if ((i = atts.getIndex(AltoXmlNames.ATTR_HPOS)) >= 0) 
			l = (int)Double.parseDouble(atts.getValue(i));
		if ((i = atts.getIndex(AltoXmlNames.ATTR_VPOS)) >= 0) 
			t = (int)Double.parseDouble(atts.getValue(i));
		currentGlyph.setCoords(createPolygonFromBoundingBox(l, t, w, h));
		
		//Text content
		if ((i = atts.getIndex(AltoXmlNames.ATTR_CONTENT)) >= 0)
			currentGlyph.setText(atts.getValue(i));
	}
	
	private void handleHyphenNode(Attributes atts) {
		if (lastWord == null)
			return;
		
		//Attach the hyphen to the last word
		// Text
		int i;
		String hypContent = "-";
		if ((i = atts.getIndex(AltoXmlNames.ATTR_CONTENT)) >= 0) {
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
		if (atts.getIndex(AltoXmlNames.ATTR_HPOS) >= 0 && atts.getIndex(AltoXmlNames.ATTR_WIDTH) >= 0) {
			int hpos = (int)Double.parseDouble(atts.getValue(AltoXmlNames.ATTR_HPOS));
			int width = (int)Double.parseDouble(atts.getValue(AltoXmlNames.ATTR_WIDTH));
			
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
		if ((i = atts.getIndex(AltoXmlNames.ATTR_ROTATION)) >= 0) {
			//ALTO's rotation is the counter-clockwise angle of the content within a block. PAGE's orientation is the clockwise angle to correct rotation/skew. Therefore the value is the same
			region.setOrientation(-Double.parseDouble(atts.getValue(i))); 
			if (region.getOrientation() > 180.0)
				region.setOrientation(region.getOrientation() - 360.0);
			if (region.getOrientation() <= -180.0)
				region.setOrientation(region.getOrientation() + 360.0);
		}
	}
	
	private void handleGraphicsBlock(Attributes atts) {
		int i;
		SeparatorRegion region = (SeparatorRegion)currentRegion;
		
		//Rotation
		if ((i = atts.getIndex(AltoXmlNames.ATTR_ROTATION)) >= 0) {
			//ALTO's rotation is the counter-clockwise angle of the content within a block. PAGE's orientation is the clockwise angle to correct rotation/skew. Therefore the value is the same
			region.setOrientation(-Double.parseDouble(atts.getValue(i))); 
			if (region.getOrientation() > 180.0)
				region.setOrientation(region.getOrientation() - 360.0);
			if (region.getOrientation() <= -180.0)
				region.setOrientation(region.getOrientation() + 360.0);
		}
	}
	
	private String altoToPrimaLanguage(String altoLanguageValue) {
		Map<String, String> PrimaLanguage = Stream.of(new String[][] {
			{"ab", "Abkhaz"},
			{"aa", "Afar"},
			{"af", "Afrikaans"},
			{"ak", "Akan"},
			{"sq", "Albanian"},
			{"am", "Amharic"},
			{"ar", "Arabic"},
			{"an", "Aragonese"},
			{"hy", "Armenian"},
			{"as", "Assamese"},
			{"av", "Avaric"},
			{"ae", "Avestan"},
			{"ay", "Aymara"},
			{"az", "Azerbaijani"},
			{"bm", "Bambara"},
			{"ba", "Bashkir"},
			{"eu", "Basque"},
			{"be", "Belarusian"},
			{"bn", "Bengali"},
			{"bh", "Bihari"},
			{"bi", "Bislama"},
			{"bs", "Bosnian"},
			{"br", "Breton"},
			{"bg", "Bulgarian"},
			{"my", "Burmese"},
			{"km", "Cambodian"},
			//{"", "Cantonese"},
			{"ca", "Catalan"},
			{"ch", "Chamorro"},
			{"ce", "Chechen"},
			{"ny", "Chichewa"},
			{"zh", "Chinese"},
			{"cv", "Chuvash"},
			{"kw", "Cornish"},
			{"co", "Corsican"},
			{"cr", "Cree"},
			{"hr", "Croatian"},
			{"cs", "Czech"},
			{"da", "Danish"},
			{"dv", "Divehi"},
			{"nl", "Dutch"},
			{"dz", "Dzongkha"},
			{"en", "English"},
			{"en-GB", "English"},
			{"en-US", "English"},
			{"eo", "Esperanto"},
			{"et", "Estonian"},
			{"ee", "Ewe"},
			{"fo", "Faroese"},
			{"fj", "Fijian"},
			{"fi", "Finnish"},
			{"fr", "French"},
			{"ff", "Fula"},
			{"gd", "Gaelic"},
			{"gl", "Galician"},
			{"lg", "Ganda"},
			{"ka", "Georgian"},
			{"de", "German"},
			{"el", "Greek"},
			{"gn", "Guaraní"},
			{"gu", "Gujarati"},
			{"ht", "Haitian"},
			{"ha", "Hausa"},
			{"he", "Hebrew"},
			{"hz", "Herero"},
			{"hi", "Hindi"},
			{"ho", "Hiri Motu"},
			{"hu", "Hungarian"},
			{"is", "Icelandic"},
			{"io", "Ido"},
			{"ig", "Igbo"},
			{"id", "Indonesian"},
			{"ia", "Interlingua"},
			{"ie", "Interlingue"},
			{"iu", "Inuktitut"},
			{"ik", "Inupiaq"},
			{"ga", "Irish"},
			{"it", "Italian"},
			{"ja", "Japanese"},
			{"jv", "Javanese"},
			{"kl", "Kalaallisut"},
			{"kn", "Kannada"},
			{"kr", "Kanuri"},
			{"ks", "Kashmiri"},
			{"kk", "Kazakh"},
			{"ki", "Kikuyu"},
			{"rw", "Kinyarwanda"},
			{"rn", "Kirundi"},
			{"kv", "Komi"},
			{"kg", "Kongo"},
			{"ko", "Korean"},
			{"ku", "Kurdish"},
			{"kj", "Kwanyama"},
			{"ky", "Kyrgyz"},
			{"lo", "Lao"},
			{"la", "Latin"},
			{"lv", "Latvian"},
			{"li", "Limburgish"},
			{"ln", "Lingala"},
			{"lt", "Lithuanian"},
			{"lu", "Luba-Katanga"},
			{"lb", "Luxembourgish"},
			{"mk", "Macedonian"},
			{"mg", "Malagasy"},
			{"ms", "Malay"},
			{"ml", "Malayalam"},
			{"mt", "Maltese"},
			{"gv", "Manx"},
			{"mi", "Māori"},
			{"mr", "Marathi"},
			{"mh", "Marshallese"},
			{"mn", "Mongolian"},
			{"na", "Nauru"},
			{"nv", "Navajo"},
			{"ng", "Ndonga"},
			{"ne", "Nepali"},
			{"nd", "North Ndebele"},
			{"se", "Northern Sami"},
			{"no", "Norwegian"},
			{"nb", "Norwegian Bokmål"},
			{"nn", "Norwegian Nynorsk"},
			{"ii", "Nuosu"},
			{"oc", "Occitan"},
			{"oj", "Ojibwe"},
			{"cu", "Old Church Slavonic"},
			{"or", "Oriya"},
			{"om", "Oromo"},
			{"os", "Ossetian"},
			{"pi", "Pāli"},
			{"pa", "Panjabi"},
			{"ps", "Pashto"},
			{"fa", "Persian"},
			{"pl", "Polish"},
			{"pt", "Portuguese"},
			{"qu", "Quechua"},
			{"ro", "Romanian"},
			{"rm", "Romansh"},
			{"ru", "Russian"},
			{"sm", "Samoan"},
			{"sg", "Sango"},
			{"sa", "Sanskrit"},
			{"sc", "Sardinian"},
			{"sr", "Serbian"},
			{"sn", "Shona"},
			{"sd", "Sindhi"},
			{"si", "Sinhala"},
			{"sk", "Slovak"},
			{"sl", "Slovene"},
			{"so", "Somali"},
			{"nr", "South Ndebele"},
			{"st", "Southern Sotho"},
			{"es", "Spanish"},
			{"su", "Sundanese"},
			{"sw", "Swahili"},
			{"ss", "Swati"},
			{"sv", "Swedish"},
			{"tl", "Tagalog"},
			{"ty", "Tahitian"},
			{"tg", "Tajik"},
			{"ta", "Tamil"},
			{"tt", "Tatar"},
			{"te", "Telugu"},
			{"th", "Thai"},
			{"bo", "Tibetan"},
			{"ti", "Tigrinya"},
			{"to", "Tonga"},
			{"ts", "Tsonga"},
			{"tn", "Tswana"},
			{"tr", "Turkish"},
			{"tk", "Turkmen"},
			{"tw", "Twi"},
			{"ug", "Uighur"},
			{"uk", "Ukrainian"},
			{"ur", "Urdu"},
			{"uz", "Uzbek"},
			{"ve", "Venda"},
			{"vi", "Vietnamese"},
			{"vo", "Volapük"},
			{"wa", "Walloon"},
			{"cy", "Welsh"},
			{"fy", "Western Frisian"},
			{"wo", "Wolof"},
			{"xh", "Xhosa"},
			{"yi", "Yiddish"},
			{"yo", "Yoruba"},
			{"za", "Zhuang"},
			{"zu", "Zulu"}
		}).collect(Collectors.toMap(data -> data[0], data -> data[1]));

		if(altoLanguageValue != null)
			return PrimaLanguage.getOrDefault(altoLanguageValue, "other");
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
		if ((i = atts.getIndex(AltoXmlNames.ATTR_POINTS)) < 0)
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
		
		if (polygon.getSize() >= 3) {
			if (currentGlyph != null)
				currentGlyph.setCoords(polygon);
			else if (currentWord != null)
				currentWord.setCoords(polygon);
			else if (currentTextLine != null)
				currentTextLine.setCoords(polygon);
			else if (currentRegion != null)
				currentRegion.setCoords(polygon);
		}
	}
	
	/**
	 * Parses an ellipse node.
	 */
	private void handleEllipseNode(Attributes atts) {
		if (currentRegion == null)
			return;
		
		int i;
		double x=0, y=0, horLength=0, vertLength=0;
		
		if ((i = atts.getIndex(AltoXmlNames.ATTR_HPOS)) < 0)
			x = Double.parseDouble(atts.getValue(i));
		if ((i = atts.getIndex(AltoXmlNames.ATTR_VPOS)) < 0)
			y = Double.parseDouble(atts.getValue(i));
		if ((i = atts.getIndex(AltoXmlNames.ATTR_HLENGTH)) < 0)
			horLength = Double.parseDouble(atts.getValue(i));
		if ((i = atts.getIndex(AltoXmlNames.ATTR_VLENGTH)) < 0)
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
		
		if ((i = atts.getIndex(AltoXmlNames.ATTR_HPOS)) < 0)
			x = Double.parseDouble(atts.getValue(i));
		if ((i = atts.getIndex(AltoXmlNames.ATTR_VPOS)) < 0)
			y = Double.parseDouble(atts.getValue(i));
		if ((i = atts.getIndex(AltoXmlNames.ATTR_RADIUS)) < 0)
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
