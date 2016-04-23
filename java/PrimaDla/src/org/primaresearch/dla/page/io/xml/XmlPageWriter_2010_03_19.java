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
package org.primaresearch.dla.page.io.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Validator;

import org.primaresearch.dla.page.MetaData;
import org.primaresearch.dla.page.Page;
import org.primaresearch.dla.page.io.FileTarget;
import org.primaresearch.dla.page.io.OutputTarget;
import org.primaresearch.dla.page.layout.PageLayout;
import org.primaresearch.dla.page.layout.converter.ConversionMessage;
import org.primaresearch.dla.page.layout.logical.Group;
import org.primaresearch.dla.page.layout.logical.GroupMember;
import org.primaresearch.dla.page.layout.logical.Layer;
import org.primaresearch.dla.page.layout.logical.Layers;
import org.primaresearch.dla.page.layout.logical.ReadingOrder;
import org.primaresearch.dla.page.layout.logical.RegionRef;
import org.primaresearch.dla.page.layout.physical.ContentObject;
import org.primaresearch.dla.page.layout.physical.text.LowLevelTextContainer;
import org.primaresearch.dla.page.layout.physical.text.TextObject;
import org.primaresearch.dla.page.layout.shared.GeometricObject;
import org.primaresearch.io.UnsupportedFormatVersionException;
import org.primaresearch.io.xml.IOError;
import org.primaresearch.io.xml.XmlValidator;
import org.primaresearch.maths.geometry.Point;
import org.primaresearch.maths.geometry.Polygon;
import org.primaresearch.shared.variable.Variable;
import org.primaresearch.shared.variable.VariableMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * Page writer implementation for XML files.
 * 
 * @author Christian Clausner
 */
public class XmlPageWriter_2010_03_19 implements XmlPageWriter {
	//TODO Rename class? It may be used to save files conform to other schemas.
	
	private static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	private Page page = null; 
	private PageLayout layout = null;
	private XmlNameProvider xmlNameProvider;
	private Document doc;
	private XmlValidator validator;
	private PageErrorHandler lastErrors;
	private List<ConversionMessage> lastConversionMessages;
	
	
	/**
	 * Constructor
	 * 
	 * @param validator Optional schema validator (use null if not required).
	 */
	public XmlPageWriter_2010_03_19(XmlValidator validator) {
		xmlNameProvider = new DefaultXmlNames();
		this.validator = validator;
	}
		
	public String getSchemaVersion() {
		return validator != null ? validator.getSchemaVersion().toString() : "2010-03-19";
	}

	//TODO Path and filename need to be variable.
	public String getSchemaLocation() {
		return "http://schema.primaresearch.org/PAGE/gts/pagecontent/"+getSchemaVersion();
	}
	
	//TODO Path and filename need to be variable.
	public String getSchemaUrl() {
		return "http://schema.primaresearch.org/PAGE/gts/pagecontent/"+getSchemaVersion()+"/pagecontent.xsd";
	}
	
	public String getNamespace() {
		return getSchemaLocation();
	}

	/**
	 * Writes the given Page object to an XML file.
	 * 
	 * @param page Page object
	 * @param target FileTarget representing an XML file
	 * @return Returns true if written successfully, false otherwise.
	 */
	@Override
	public boolean write(Page page, OutputTarget target) throws UnsupportedFormatVersionException {
		return run(page, target, false);
	}
	
	/**
	 * Validates the given Page object against the XML schema.
	 * 
	 * @param page Page object
	 * @return Returns true if valid, false otherwise.
	 */
	@Override
	public boolean validate(Page page) throws UnsupportedFormatVersionException {
		return run(page, null, true);
	}
	
	private boolean run(Page page, OutputTarget target, boolean validateOnly) throws UnsupportedFormatVersionException {
		if (validator != null && !validator.getSchemaVersion().equals(page.getFormatVersion()))
			throw new UnsupportedFormatVersionException("XML page writer doesn't support format: "+page.getFormatVersion().toString());
		
		this.page = page;
		layout = page.getLayout();
		lastErrors = new PageErrorHandler();
		
		//Convert page file if necessary and possible
		//if (validator != null)
		//	lastConversionMessages = ConverterHub.convert(page, validator.getSchemaVersion());
		
        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        dbfac.setValidating(false);
        dbfac.setNamespaceAware(true);
        //if (validator != null)
        	//dbfac.setSchema(validator.getSchema());
        
        DocumentBuilder docBuilder;
		try {
			docBuilder = dbfac.newDocumentBuilder();
			//docBuilder.setErrorHandler(lastErrors);
			
	        doc = docBuilder.newDocument();
	        
	        writeRoot();
	        
	        //Validation errors?
	        if (validator != null) {
	        	Validator domVal = validator.getSchema().newValidator();
	        	domVal.setErrorHandler(lastErrors);

	        	try {
					domVal.validate(new DOMSource(doc));
				} catch (SAXException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
	        if (lastErrors.hasErrors()) {
	        	return false;
	        }
	        
	        //Write XML
	        if (!validateOnly) {
	        	
	            TransformerFactory transfac = TransformerFactory.newInstance();
	            Transformer trans = transfac.newTransformer();
	            DOMSource source = new DOMSource(doc);
	            
	            OutputStream os = null;
	            
	            if (target instanceof FileTarget) {
					File f = ((FileTarget)target).getFile();
	            	os = new FileOutputStream(f);
	            } else if (target instanceof StreamTarget)
	            	os = ((StreamTarget) target).getOutputStream();
	            
	            StreamResult result = new StreamResult(os);
	            trans.transform(source, result);
	            os.close();
	        }
            return true;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public List<IOError> getErrors() {
		return lastErrors != null ? lastErrors.getErrors() : null;
	}

	public List<IOError> getWarnings() {
		return lastErrors != null ? lastErrors.getWarnings() : null;
	}

	private void writeRoot() /*throws XMLStreamException*/ {
		String xmlns = getSchemaLocation();
		//String xsi = "http://www.w3.org/2001/XMLSchema-instance";
		
		Element root = doc.createElementNS(xmlns, DefaultXmlNames.ELEMENT_PcGts);
		doc.appendChild(root);
		
		//xmlns
		//addAttribute(root, "xmlns", xmlns);
				
		//xmlns:xsi
		//addAttribute(root, "xmlns:xsi", xsi);
		
		//Schema location
		String schemaLocation = getSchemaLocation() + " " + getSchemaUrl();
		root.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:schemaLocation", schemaLocation);
		//addAttribute(root, "xsi:schemaLocation", schemaLocation);
		
		//GtsID
		if (page.getGtsId() != null) 
			addAttribute(root, DefaultXmlNames.ATTR_pcGtsId, page.getGtsId().toString());
		
		addMetaData(root);
		addPage(root);
	}
	
	private void addAttribute(Element node, String name, String value) {
		node.setAttributeNS(null, name, value);
	}
	
	private void addMetaData(Element parent) /*throws XMLStreamException*/ {
		MetaData metaData = page.getMetaData();
		if (metaData == null)
			return;
		
		Element metaDataNode = doc.createElementNS(getNamespace(), DefaultXmlNames.ELEMENT_Metadata);
		parent.appendChild(metaDataNode);
		
		//Creator 
		addTextElement(metaDataNode, DefaultXmlNames.ELEMENT_Creator, metaData.getCreator());
		
		//Created
		addTextElement(metaDataNode, DefaultXmlNames.ELEMENT_Created, DATE_FORMAT.format(metaData.getCreationTime()));

		//Last modified
		addTextElement(metaDataNode, DefaultXmlNames.ELEMENT_LastChange, DATE_FORMAT.format(metaData.getLastModificationTime()));

		//Comments
		addTextElement(metaDataNode, DefaultXmlNames.ELEMENT_Comments, metaData.getComments());
	}
	
	private void addPage(Element parent) {
		Element pageNode = doc.createElementNS(getNamespace(), DefaultXmlNames.ELEMENT_Page);
		parent.appendChild(pageNode);
		
		//Image filename
		addAttribute(pageNode, DefaultXmlNames.ATTR_imageFilename, page.getImageFilename());
		
		//Width/height
		addAttribute(pageNode, DefaultXmlNames.ATTR_imageWidth, Integer.toString(layout.getWidth()));
		addAttribute(pageNode, DefaultXmlNames.ATTR_imageHeight, Integer.toString(layout.getHeight()));
		
		//Border
		GeometricObject border = layout.getBorder();
		if (border != null) {
			Element node = doc.createElementNS(getNamespace(), DefaultXmlNames.ELEMENT_Border);
			pageNode.appendChild(node);
			addCoords(node, border.getCoords());
		}

		//Print space
		GeometricObject printSpace = layout.getPrintSpace();
		if (printSpace != null) {
			Element node = doc.createElementNS(getNamespace(), DefaultXmlNames.ELEMENT_PrintSpace);
			pageNode.appendChild(node);
			addCoords(node, printSpace.getCoords());
		}
		
		//Reading order
		addReadingOrder(pageNode, layout.getReadingOrder());
		
		//Layers
		addLayers(pageNode, layout.getLayers());
		
		//Regions
		for (int i=0; i<layout.getRegionCount(); i++) {
			addContentObject(pageNode, layout.getRegion(i));
		}
	}
	
	private void addContentObject(Element parent, ContentObject contentObj) /*throws XMLStreamException*/ {
		String elementName = xmlNameProvider.getXmlName(contentObj.getType());
		
		Element regionNode = doc.createElementNS(getNamespace(), elementName);
		parent.appendChild(regionNode);
		
		//ID
		addAttribute(regionNode, DefaultXmlNames.ATTR_id, contentObj.getId().toString());

		//Attributes
		addContentObjectAttributes(regionNode, contentObj.getAttributes());
		
		//Coords
		addCoords(regionNode, contentObj.getCoords());

		//Children
		// Text low level text objects
		if (contentObj instanceof LowLevelTextContainer) {
			LowLevelTextContainer container = (LowLevelTextContainer)contentObj;
			for (int i=0; i<container.getTextObjectCount(); i++)
				addContentObject(regionNode, container.getTextObject(i));
		}
		
		// Nested regions

		//Text
		if (contentObj instanceof TextObject)
			addTextContent(regionNode, (TextObject)contentObj);
	}
	
	private void addTextContent(Element parent, TextObject textObj) /*throws XMLStreamException*/ {
		
		if (	(textObj.getText() == null || textObj.getText().isEmpty())
			&& 	(textObj.getPlainText() == null || textObj.getPlainText().isEmpty()))
			return;
		
		Element textEquiv = doc.createElementNS(getNamespace(), DefaultXmlNames.ELEMENT_TextEquiv);
		parent.appendChild(textEquiv);

		//Plain text
		addTextElement(textEquiv, DefaultXmlNames.ELEMENT_PlainText, textObj.getPlainText());
		//Unicode text
		addTextElement(textEquiv, DefaultXmlNames.ELEMENT_Unicode, textObj.getText());
	}
	
	private void addContentObjectAttributes(Element parent, VariableMap vars) /*throws XMLStreamException*/ {
		Variable v;
		for (int i=0; i<vars.getSize(); i++) {
			v = vars.get(i);
			if (v.getValue() != null)
				addAttribute(parent, v.getName(), v.getValue().toString());
		}
	}
	
	private void addCoords(Element parent, Polygon coords) /*throws XMLStreamException*/ {
		Element coordsNode = doc.createElementNS(getNamespace(), DefaultXmlNames.ELEMENT_Coords);
		parent.appendChild(coordsNode);
		
		Point p;
		for (int i=0; i<coords.getSize(); i++) {
			p = coords.getPoint(i);
			Element pointNode = doc.createElementNS(getNamespace(), DefaultXmlNames.ELEMENT_Point);
			coordsNode.appendChild(pointNode);
			addAttribute(pointNode, DefaultXmlNames.ATTR_x, Integer.toString(p.x));
			addAttribute(pointNode, DefaultXmlNames.ATTR_y, Integer.toString(p.y));
		}
	}
	
	private void addReadingOrder(Element parent, ReadingOrder order) /*throws XMLStreamException*/ {
		if (order == null || order.getRoot() == null || order.getRoot().getSize() == 0)
			return;
		
		Element node = doc.createElementNS(getNamespace(), DefaultXmlNames.ELEMENT_ReadingOrder);
		parent.appendChild(node);

		//Root group
		addReadingOrderGroup(node, order.getRoot(), -1);
	}
	
	/**
	 * Writes a reading order group including its members.
	 * @param index Index of the group in the parent group (use -1 if not indexed).
	 */
	private void addReadingOrderGroup(Element parent, Group group, int index) /*throws XMLStreamException*/ {
		String groupElementName;
		if (group.isOrdered())
			groupElementName = index >= 0 ? DefaultXmlNames.ELEMENT_OrderedGroupIndexed : DefaultXmlNames.ELEMENT_OrderedGroup;
		else
			groupElementName = index >= 0 ? DefaultXmlNames.ELEMENT_UnorderedGroupIndexed : DefaultXmlNames.ELEMENT_UnorderedGroup;
		
		
		Element groupNode = doc.createElementNS(getNamespace(), groupElementName);
		parent.appendChild(groupNode);
			
		//ID
		addAttribute(groupNode, DefaultXmlNames.ATTR_id, group.getId().toString());
		
		//Index
		if (index >= 0)
			addAttribute(groupNode, DefaultXmlNames.ATTR_index, Integer.toString(index));
			//eventWriter.add(eventFactory.createAttribute(DefaultXmlNames.ATTR_index, Integer.toString(index)));
	
		//Children
		GroupMember member;
		for (int i=0; i<group.getSize(); i++) {
			member = group.getMember(i);
			if (member instanceof Group)
				addReadingOrderGroup(groupNode, (Group)member, group.isOrdered() ? i : -1);
			else if (member instanceof RegionRef) 
				addRegionRef(groupNode, ((RegionRef)member).getRegionId().toString(), group.isOrdered() ? i : -1);
		}
	}
	
	/**
	 * Writes a region reference element.
	 * @param index Index of the region reference in the parent group (use -1 if not indexed).
	 */
	void addRegionRef(Element parent, String regionId, int index) /*throws XMLStreamException*/ {
		String elementName = index >= 0 ? DefaultXmlNames.ELEMENT_RegionRefIndexed : DefaultXmlNames.ELEMENT_RegionRef;

		Element refNode = doc.createElementNS(getNamespace(), elementName);
		parent.appendChild(refNode);
		
		//ID Ref
		addAttribute(refNode, DefaultXmlNames.ATTR_regionRef, regionId);
		
		//Index
		if (index >= 0)
			addAttribute(refNode, DefaultXmlNames.ATTR_index, Integer.toString(index));
	}
	
	private void addLayers(Element parent, Layers layers) /*throws XMLStreamException*/ {
		if (layers == null || layers.getSize() == 0)
			return;
		
		//Check if there are non-empty layers
		boolean foundNonEmptyLayer = false;
		for (int i=0; i<layers.getSize(); i++) {
			if (layers.getLayer(i).getSize() != 0)
			{
				foundNonEmptyLayer = true;
				break;
			}
		}
		if (!foundNonEmptyLayer) //Only empty layers -> skip the whole layers element
			return;

		
		Element layersNode = doc.createElementNS(getNamespace(), DefaultXmlNames.ELEMENT_Layers);
		parent.appendChild(layersNode);

		Layer layer;
		for (int i=0; i<layers.getSize(); i++) {
			layer = layers.getLayer(i);
			if (layer.getSize() > 0)
				addLayer(layersNode, layer);
		}
	}

	private void addLayer(Element parent, Layer layer) /*throws XMLStreamException*/ {
		
		Element layerNode = doc.createElementNS(getNamespace(), DefaultXmlNames.ELEMENT_Layer);
		parent.appendChild(layerNode);
		
		//ID
		addAttribute(layerNode, DefaultXmlNames.ATTR_id, layer.getId().toString());
		
		//Z-Index
		addAttribute(layerNode, DefaultXmlNames.ATTR_zIndex, Integer.toString(layer.getZIndex()));
				
		//Region Refs
		GroupMember member;
		for (int i=0; i<layer.getSize(); i++) {
			member = layer.getMember(i);
			if (member instanceof RegionRef) 
				addRegionRef(layerNode, ((RegionRef)member).getRegionId().toString(), -1);
		}
	}

	
	/**
	 * Writes a single element with text content.
	 */
	private void addTextElement(Element parent, String elementName, String text) /*throws XMLStreamException*/ {
		Element node = doc.createElementNS(getNamespace(), elementName);
		parent.appendChild(node);

		Text textNode = doc.createTextNode(text != null ? text : "");
		node.appendChild(textNode);
	}

	@Override
	public List<ConversionMessage> getConversionInformation() {
		return lastConversionMessages;
	}
}
