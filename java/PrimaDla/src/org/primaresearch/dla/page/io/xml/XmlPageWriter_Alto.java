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
package org.primaresearch.dla.page.io.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import org.primaresearch.dla.page.Page;
import org.primaresearch.dla.page.io.FileTarget;
import org.primaresearch.dla.page.io.OutputTarget;
import org.primaresearch.dla.page.layout.PageLayout;
import org.primaresearch.dla.page.layout.converter.ConversionMessage;
import org.primaresearch.dla.page.layout.physical.Region;
import org.primaresearch.dla.page.layout.physical.shared.RegionType;
import org.primaresearch.dla.page.layout.physical.text.impl.Glyph;
import org.primaresearch.dla.page.layout.physical.text.impl.TextLine;
import org.primaresearch.dla.page.layout.physical.text.impl.TextRegion;
import org.primaresearch.dla.page.layout.physical.text.impl.Word;
import org.primaresearch.dla.page.layout.shared.GeometricObject;
import org.primaresearch.io.UnsupportedFormatVersionException;
import org.primaresearch.io.xml.IOError;
import org.primaresearch.io.xml.XmlValidator;
import org.primaresearch.maths.geometry.Point;
import org.primaresearch.maths.geometry.Polygon;
import org.primaresearch.maths.geometry.Rect;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 *  Page writer implementation for ALTO XML files.
 *  Experimental.
 *  
 * @author Christian Clausner
 *
 */
public class XmlPageWriter_Alto implements XmlPageWriter {

	private XmlValidator validator;
	private PageErrorHandler lastErrors;
	private Page page = null; 
	private PageLayout layout = null;
	private String namespace;
	private Document doc;
	private Map<String, String> propagatedWordTexts; //Map[ID, text]
	private Map<String, String> propagatedGlyphTexts; //Map[ID, text]

	/**
	 * Constructor
	 * 
	 * @param validator Optional schema validator (use null if not required).
	 */
	public XmlPageWriter_Alto(XmlValidator validator) {
		this.validator = validator;
	}

	@Override
	public boolean write(Page page, OutputTarget target) throws UnsupportedFormatVersionException {
		return run(page, target, false);
	}

	@Override
	public boolean validate(Page page) throws UnsupportedFormatVersionException {
		return run(page, null, true);
	}

	@Override
	public String getSchemaVersion() {
		return "http://www.loc.gov/standards/alto/ns-v4#";
	}

	@Override
	public String getSchemaLocation() {
		//return "http://www.loc.gov/standards/alto/v4";
		return "http://www.loc.gov/standards/alto/ns-v4#";
	}

	@Override
	public String getSchemaUrl() {
		return "http://www.loc.gov/standards/alto/v4/alto-4-1.xsd";
	}

	@Override
	public String getNamespace() {
		return "http://www.loc.gov/standards/alto/ns-v4#";
	}

	@Override
	public List<ConversionMessage> getConversionInformation() {
		return null;
	}
	
	/**
	 * Returns a list of writing errors
	 */
	public List<IOError> getErrors() {
		return lastErrors != null ? lastErrors.getErrors() : null;
	}

	/**
	 * Returns a list of writing warnings
	 */
	public List<IOError> getWarnings() {
		return lastErrors != null ? lastErrors.getWarnings() : null;
	}
	
	private boolean run(Page page, OutputTarget target, boolean validateOnly) throws UnsupportedFormatVersionException {
		//if (validator != null && !validator.getSchemaVersion().equals(page.getFormatVersion()))
		//	throw new UnsupportedFormatVersionException("XML page writer doesn't support format: "+page.getFormatVersion().toString());
		
		this.page = page;
		layout = page.getLayout();
		lastErrors = new PageErrorHandler();
		propagatedWordTexts = new HashMap<String, String>();
		propagatedGlyphTexts = new HashMap<String, String>();
		propagateText();
		
        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        dbfac.setValidating(false);
        dbfac.setNamespaceAware(true);
        //if (validator != null)
        	//dbfac.setSchema(validator.getSchema());
        
        DocumentBuilder docBuilder;
		try {
			docBuilder = dbfac.newDocumentBuilder();
			//docBuilder.setErrorHandler(lastErrors);
			
			DOMImplementation domImpl = docBuilder.getDOMImplementation();
	        //doc = docBuilder.newDocument();
			namespace = getNamespace();
			doc = domImpl.createDocument(namespace, AltoXmlNames.ELEMENT_alto, null);
	        
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
	
	private void addAttribute(Element node, String name, String value) {
		node.setAttributeNS(null, name, value);
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
	
	private void writeRoot() {
		
		Element root = doc.getDocumentElement();
		
		//Schema location
		String schemaLocation = getSchemaLocation() + " " + getSchemaUrl();
		root.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:schemaLocation", schemaLocation);
		
		addAttribute(root, AltoXmlNames.ATTR_SCHEMAVERSION, "4.1");
		
		addDescription(root);
		//TODO: Styles
		//TODO: Tags
		addLayout(root);
	}
	
	private void addDescription(Element parent) {
		
		Element descriptionNode = doc.createElementNS(getNamespace(), AltoXmlNames.ELEMENT_Description);
		parent.appendChild(descriptionNode);

		//Measurement unit
		addTextElement(descriptionNode, AltoXmlNames.ELEMENT_MeasurementUnit, "pixel");
		
		//Image
		Element imageNode = doc.createElementNS(getNamespace(), AltoXmlNames.ELEMENT_sourceImageInformation);
		descriptionNode.appendChild(imageNode);
		
		addTextElement(imageNode, AltoXmlNames.ELEMENT_fileName, page.getImageFilename());
		
		//TODO
		//PAGE->Metadata->MetadataItem(processingStep) => Processing->ProcessingStep
	}
	
	private void addLayout(Element parent) {
		
		Element layoutNode = doc.createElementNS(getNamespace(), AltoXmlNames.ELEMENT_Layout);
		parent.appendChild(layoutNode);

		//Page
		Element pageNode = doc.createElementNS(getNamespace(), AltoXmlNames.ELEMENT_Page);
		layoutNode.appendChild(pageNode);
		
		//ID
		String id = "p0";
		if (page.getGtsId() != null) 
			id = page.getGtsId().toString();
		addAttribute(pageNode, AltoXmlNames.ATTR_ID, id);
		
		//PHYSICAL_IMG_NR
		addAttribute(pageNode, AltoXmlNames.ATTR_PHYSICAL_IMG_NR, "0");
		
		//Width, height
		addAttribute(pageNode, AltoXmlNames.ATTR_WIDTH, ""+layout.getWidth());
		addAttribute(pageNode, AltoXmlNames.ATTR_HEIGHT, ""+layout.getHeight());
		
		//Page class
		if (page.getAttributes().get("type") != null && page.getAttributes().get("type").getValue() != null)
			addAttribute(pageNode, AltoXmlNames.ATTR_PAGECLASS, page.getAttributes().get("type").getValue().toString());
		
		//Confidence
		if (page.getAttributes().get("conf") != null && page.getAttributes().get("conf").getValue() != null)
			addAttribute(pageNode, AltoXmlNames.ATTR_PC, page.getAttributes().get("conf").getValue().toString());
		
		//TODO: TopMargin, LeftMargin, RightMargin, BottomMargin

		//Print space
		addPrintSpace(pageNode);
	}
	
	private void addPrintSpace(Element parent) {
		
		Element printSpaceNode = doc.createElementNS(getNamespace(), AltoXmlNames.ELEMENT_PrintSpace);
		parent.appendChild(printSpaceNode);
		
		addAttribute(printSpaceNode, AltoXmlNames.ATTR_ID, "PageSpaceTypeID"+0);
		
		//TODO
		//WIDTH
		//HEIGHT
		//HPOS
		//VPOS
		
		if (layout.getPrintSpace() != null)
			addShape(printSpaceNode, layout.getPrintSpace().getCoords());
		
		//Blocks
		for (int i=0; i<layout.getRegionCount(); i++) {
			addBlock(printSpaceNode, layout.getRegion(i));
		}
	}
	
	void addShape(Element parent, Polygon outline) {
		
		Element shapeNode = doc.createElementNS(getNamespace(), AltoXmlNames.ELEMENT_Shape);
		parent.appendChild(shapeNode);
		
		Element polygonNode = doc.createElementNS(getNamespace(), AltoXmlNames.ELEMENT_Polygon);
		shapeNode.appendChild(polygonNode);
		
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<outline.getSize(); i++) {
			if (sb.length() > 0)
				sb.append(' ');
			Point p = outline.getPoint(i);
			sb.append(p.x);
			sb.append(',');
			sb.append(p.y);
		}
		addAttribute(polygonNode, AltoXmlNames.ATTR_POINTS, sb.toString());
	}
	
	void addBlock(Element parent, Region region) {
		
		Element blockNode = doc.createElementNS(getNamespace(), getAltoBlockType(region));
		parent.appendChild(blockNode);

		//ID
		addAttribute(blockNode, AltoXmlNames.ATTR_ID, region.getId().toString());

		//TODO
		//STYLEREFS
		//TAGREFS
		//PROCESSINGREFS
		//HEIGHT
		//WIDTH
		//HPOS
		//VPOS
		//ROTATION
		//IDNEXT
		//CS
		
		addShape(blockNode, region.getCoords());
		
		if (region.getRegionCount() > 0) {
			//Child regions
			for (int i=0; i<region.getRegionCount(); i++)
				addBlock(blockNode, region.getRegion(i));
		} else {
			//Specialised content
			if (RegionType.TextRegion.equals(region.getType()))
				addTextBlockContent(blockNode, (TextRegion)region);
			else if (RegionType.SeparatorRegion.equals(region.getType()))
				addGraphicalBlockContent(blockNode, region);
			else //Illustration
				addIllustrationBlockContent(blockNode, region);
		}
	}
	
	String getAltoBlockType(Region region) {
		if (region.getRegionCount() > 0)
			return AltoXmlNames.ELEMENT_ComposedBlock;
		if (RegionType.TextRegion.equals(region.getType()))
			return AltoXmlNames.ELEMENT_TextBlock;
		if (RegionType.SeparatorRegion.equals(region.getType()))
			return AltoXmlNames.ELEMENT_GraphicalElement;

		return AltoXmlNames.ELEMENT_Illustration;
	}
	
	void addTextBlockContent(Element blockNode, TextRegion region) {
		
		//LANG
		if (region.getAttributes().get("primaryLanguage") != null && region.getAttributes().get("primaryLanguage").getValue() != null) {
			String lang = getAltoLanguage(region.getAttributes().get("primaryLanguage").getValue().toString());
			if (lang != null)
				addAttribute(blockNode, AltoXmlNames.ATTR_LANG, lang);
		}
		
		//Text lines
		for (int i=0; i<region.getTextObjectCount(); i++)
			addTextLine(blockNode, (TextLine)region.getTextObject(i));
	}

	void addGraphicalBlockContent(Element blockNode, Region region) {
		//No additional content
	}
	
	void addIllustrationBlockContent(Element blockNode, Region region) {
		
		//Type (use PAGE region type)
		addAttribute(blockNode, AltoXmlNames.ATTR_TYPE, region.getType().toString());
		
		//Alternative image
		if (!region.getAlternativeImages().isEmpty())
			addAttribute(blockNode, AltoXmlNames.ATTR_FILEID, region.getAlternativeImages().get(0).getFilename());
	}
	
	void addTextLine(Element blockNode, TextLine textLine) {
		
		if (textLine.getTextObjectCount() == 0)
			return; //We need words
		
		Element textLineNode = doc.createElementNS(getNamespace(), AltoXmlNames.ELEMENT_TextLine);
		blockNode.appendChild(textLineNode);

		//ID
		addAttribute(textLineNode, AltoXmlNames.ATTR_ID, textLine.getId().toString());

		//LANG
		if (textLine.getAttributes().get("primaryLanguage") != null && textLine.getAttributes().get("primaryLanguage").getValue() != null) {
			String lang = getAltoLanguage(textLine.getAttributes().get("primaryLanguage").getValue().toString());
			if (lang != null)
				addAttribute(textLineNode, AltoXmlNames.ATTR_LANG, lang);
		}

		//TODO
		//STYLEREFS
		//TAGREFS
		//PROCESSINGREFS
		//HEIGHT
		//WIDTH
		//HPOS
		//VPOS
		//BASELINE
		//CS
		
		addShape(textLineNode, textLine.getCoords());
		
		
		//Words (strings)
		for (int i=0; i<textLine.getTextObjectCount(); i++)
			addWord(textLineNode, (Word)textLine.getTextObject(i));

		//TODO
		//SP
		
		//HYP
	}
	
	void addWord(Element textLineNode, Word word) {
		
		Element wordNode = doc.createElementNS(getNamespace(), AltoXmlNames.ELEMENT_String);
		textLineNode.appendChild(wordNode);

		//ID
		addAttribute(wordNode, AltoXmlNames.ATTR_ID, word.getId().toString());

		//CONTENT
		String textContent = word.getText() != null ? word.getText() : word.composeText(false, false);
		if (textContent.isEmpty() && propagatedWordTexts.containsKey(word.getId().toString()))
			textContent = propagatedWordTexts.get(word.getId().toString());
		
		addAttribute(wordNode, AltoXmlNames.ATTR_CONTENT, textContent);

		//LANG
		if (word.getAttributes().get("language") != null && word.getAttributes().get("language").getValue() != null) {
			String lang = getAltoLanguage(word.getAttributes().get("language").getValue().toString());
			if (lang != null)
				addAttribute(wordNode, AltoXmlNames.ATTR_LANG, lang);
		}
		
		//TODO
		//STYLEREFS
		//TAGREFS
		//PROCESSINGREFS
		//HEIGHT
		//WIDTH
		//HPOS
		//VPOS
		//STYLE
		//SUBS_TYPE
		//SUBS_CONTENT
		//WC
		//CC
		//CS
		
		addShape(wordNode, word.getCoords());
		
		//TODO: ALTERNATIVE
		
		//Glyphs 
		for (int i=0; i<word.getTextObjectCount(); i++)
			addGlyph(wordNode, (Glyph)word.getTextObject(i));

	}
	
	void addGlyph(Element wordNode, Glyph glyph) {

		Element glyphNode = doc.createElementNS(getNamespace(), AltoXmlNames.ELEMENT_Glyph);
		wordNode.appendChild(glyphNode);

		//ID
		addAttribute(glyphNode, AltoXmlNames.ATTR_ID, glyph.getId().toString());

		//CONTENT
		String textContent = glyph.getText() != null ? glyph.getText() : "";
		if (textContent.isEmpty() && propagatedGlyphTexts.containsKey(glyph.getId().toString()))
			textContent = propagatedGlyphTexts.get(glyph.getId().toString());

		addAttribute(glyphNode, AltoXmlNames.ATTR_CONTENT, textContent.isEmpty() ? "?" : textContent); 

		//TODO
		//HEIGHT
		//WIDTH
		//HPOS
		//VPOS
		//GC

		addShape(glyphNode, glyph.getCoords());
		
		//TODO: Variant
	}
	
	String getAltoLanguage(String pageLanguage) {
		if ("Abkhaz".equals(pageLanguage)) return "ab";
		if ("Afar".equals(pageLanguage)) return "aa";
		if ("Afrikaans".equals(pageLanguage)) return "af";
		if ("Akan".equals(pageLanguage)) return "ak";
		if ("Albanian".equals(pageLanguage)) return "sq";
		if ("Amharic".equals(pageLanguage)) return "am";
		if ("Arabic".equals(pageLanguage)) return "ar";
		if ("Aragonese".equals(pageLanguage)) return "an";
		if ("Armenian".equals(pageLanguage)) return "hy";
		if ("Assamese".equals(pageLanguage)) return "as";
		if ("Avaric".equals(pageLanguage)) return "av";
		if ("Avestan".equals(pageLanguage)) return "ae";
		if ("Aymara".equals(pageLanguage)) return "ay";
		if ("Azerbaijani".equals(pageLanguage)) return "az";
		if ("Bambara".equals(pageLanguage)) return "bm";
		if ("Bashkir".equals(pageLanguage)) return "ba";
		if ("Basque".equals(pageLanguage)) return "eu";
		if ("Belarusian".equals(pageLanguage)) return "be";
		if ("Bengali".equals(pageLanguage)) return "bn";
		if ("Bihari".equals(pageLanguage)) return "bh";
		if ("Bislama".equals(pageLanguage)) return "bi";
		if ("Bosnian".equals(pageLanguage)) return "bs";
		if ("Breton".equals(pageLanguage)) return "br";
		if ("Bulgarian".equals(pageLanguage)) return "bg";
		if ("Burmese".equals(pageLanguage)) return "my";
		if ("Cambodian".equals(pageLanguage)) return "km";
		if ("Catalan".equals(pageLanguage)) return "ca";
		if ("Chamorro".equals(pageLanguage)) return "ch";
		if ("Chechen".equals(pageLanguage)) return "ce";
		if ("Chichewa".equals(pageLanguage)) return "ny";
		if ("Chinese".equals(pageLanguage)) return "zh";
		if ("Chuvash".equals(pageLanguage)) return "cv";
		if ("Cornish".equals(pageLanguage)) return "kw";
		if ("Corsican".equals(pageLanguage)) return "co";
		if ("Cree".equals(pageLanguage)) return "cr";
		if ("Croatian".equals(pageLanguage)) return "hr";
		if ("Czech".equals(pageLanguage)) return "cs";
		if ("Danish".equals(pageLanguage)) return "da";
		if ("Divehi".equals(pageLanguage)) return "dv";
		if ("Dutch".equals(pageLanguage)) return "nl";
		if ("Dzongkha".equals(pageLanguage)) return "dz";
		if ("English".equals(pageLanguage)) return "en";
		if ("English".equals(pageLanguage)) return "en";
		if ("English".equals(pageLanguage)) return "en";
		if ("Esperanto".equals(pageLanguage)) return "eo";
		if ("Estonian".equals(pageLanguage)) return "et";
		if ("Ewe".equals(pageLanguage)) return "ee";
		if ("Faroese".equals(pageLanguage)) return "fo";
		if ("Fijian".equals(pageLanguage)) return "fj";
		if ("Finnish".equals(pageLanguage)) return "fi";
		if ("French".equals(pageLanguage)) return "fr";
		if ("Fula".equals(pageLanguage)) return "ff";
		if ("Gaelic".equals(pageLanguage)) return "gd";
		if ("Galician".equals(pageLanguage)) return "gl";
		if ("Ganda".equals(pageLanguage)) return "lg";
		if ("Georgian".equals(pageLanguage)) return "ka";
		if ("German".equals(pageLanguage)) return "de";
		if ("Greek".equals(pageLanguage)) return "el";
		if ("Guaraní".equals(pageLanguage)) return "gn";
		if ("Gujarati".equals(pageLanguage)) return "gu";
		if ("Haitian".equals(pageLanguage)) return "ht";
		if ("Hausa".equals(pageLanguage)) return "ha";
		if ("Hebrew".equals(pageLanguage)) return "he";
		if ("Herero".equals(pageLanguage)) return "hz";
		if ("Hindi".equals(pageLanguage)) return "hi";
		if ("Hiri Motu".equals(pageLanguage)) return "ho";
		if ("Hungarian".equals(pageLanguage)) return "hu";
		if ("Icelandic".equals(pageLanguage)) return "is";
		if ("Ido".equals(pageLanguage)) return "io";
		if ("Igbo".equals(pageLanguage)) return "ig";
		if ("Indonesian".equals(pageLanguage)) return "id";
		if ("Interlingua".equals(pageLanguage)) return "ia";
		if ("Interlingue".equals(pageLanguage)) return "ie";
		if ("Inuktitut".equals(pageLanguage)) return "iu";
		if ("Inupiaq".equals(pageLanguage)) return "ik";
		if ("Irish".equals(pageLanguage)) return "ga";
		if ("Italian".equals(pageLanguage)) return "it";
		if ("Japanese".equals(pageLanguage)) return "ja";
		if ("Javanese".equals(pageLanguage)) return "jv";
		if ("Kalaallisut".equals(pageLanguage)) return "kl";
		if ("Kannada".equals(pageLanguage)) return "kn";
		if ("Kanuri".equals(pageLanguage)) return "kr";
		if ("Kashmiri".equals(pageLanguage)) return "ks";
		if ("Kazakh".equals(pageLanguage)) return "kk";
		if ("Khmer".equals(pageLanguage)) return "km";
		if ("Kikuyu".equals(pageLanguage)) return "ki";
		if ("Kinyarwanda".equals(pageLanguage)) return "rw";
		if ("Kirundi".equals(pageLanguage)) return "rn";
		if ("Komi".equals(pageLanguage)) return "kv";
		if ("Kongo".equals(pageLanguage)) return "kg";
		if ("Korean".equals(pageLanguage)) return "ko";
		if ("Kurdish".equals(pageLanguage)) return "ku";
		if ("Kwanyama".equals(pageLanguage)) return "kj";
		if ("Kyrgyz".equals(pageLanguage)) return "ky";
		if ("Lao".equals(pageLanguage)) return "lo";
		if ("Latin".equals(pageLanguage)) return "la";
		if ("Latvian".equals(pageLanguage)) return "lv";
		if ("Limburgish".equals(pageLanguage)) return "li";
		if ("Lingala".equals(pageLanguage)) return "ln";
		if ("Lithuanian".equals(pageLanguage)) return "lt";
		if ("Luba-Katanga".equals(pageLanguage)) return "lu";
		if ("Luxembourgish".equals(pageLanguage)) return "lb";
		if ("Macedonian".equals(pageLanguage)) return "mk";
		if ("Malagasy".equals(pageLanguage)) return "mg";
		if ("Malay".equals(pageLanguage)) return "ms";
		if ("Malayalam".equals(pageLanguage)) return "ml";
		if ("Maltese".equals(pageLanguage)) return "mt";
		if ("Manx".equals(pageLanguage)) return "gv";
		if ("Māori".equals(pageLanguage)) return "mi";
		if ("Marathi".equals(pageLanguage)) return "mr";
		if ("Marshallese".equals(pageLanguage)) return "mh";
		if ("Mongolian".equals(pageLanguage)) return "mn";
		if ("Nauru".equals(pageLanguage)) return "na";
		if ("Navajo".equals(pageLanguage)) return "nv";
		if ("Ndonga".equals(pageLanguage)) return "ng";
		if ("Nepali".equals(pageLanguage)) return "ne";
		if ("North Ndebele".equals(pageLanguage)) return "nd";
		if ("Northern Sami".equals(pageLanguage)) return "se";
		if ("Norwegian".equals(pageLanguage)) return "no";
		if ("Norwegian Bokmål".equals(pageLanguage)) return "nb";
		if ("Norwegian Nynorsk".equals(pageLanguage)) return "nn";
		if ("Nuosu".equals(pageLanguage)) return "ii";
		if ("Occitan".equals(pageLanguage)) return "oc";
		if ("Ojibwe".equals(pageLanguage)) return "oj";
		if ("Old Church Slavonic".equals(pageLanguage)) return "cu";
		if ("Oriya".equals(pageLanguage)) return "or";
		if ("Oromo".equals(pageLanguage)) return "om";
		if ("Ossetian".equals(pageLanguage)) return "os";
		if ("Pāli".equals(pageLanguage)) return "pi";
		if ("Panjabi".equals(pageLanguage)) return "pa";
		if ("Pashto".equals(pageLanguage)) return "ps";
		if ("Persian".equals(pageLanguage)) return "fa";
		if ("Polish".equals(pageLanguage)) return "pl";
		if ("Portuguese".equals(pageLanguage)) return "pt";
		if ("Punjabi".equals(pageLanguage)) return "pa";
		if ("Quechua".equals(pageLanguage)) return "qu";
		if ("Romanian".equals(pageLanguage)) return "ro";
		if ("Romansh".equals(pageLanguage)) return "rm";
		if ("Russian".equals(pageLanguage)) return "ru";
		if ("Samoan".equals(pageLanguage)) return "sm";
		if ("Sango".equals(pageLanguage)) return "sg";
		if ("Sanskrit".equals(pageLanguage)) return "sa";
		if ("Sardinian".equals(pageLanguage)) return "sc";
		if ("Serbian".equals(pageLanguage)) return "sr";
		if ("Shona".equals(pageLanguage)) return "sn";
		if ("Sindhi".equals(pageLanguage)) return "sd";
		if ("Sinhala".equals(pageLanguage)) return "si";
		if ("Slovak".equals(pageLanguage)) return "sk";
		if ("Slovene".equals(pageLanguage)) return "sl";
		if ("Somali".equals(pageLanguage)) return "so";
		if ("South Ndebele".equals(pageLanguage)) return "nr";
		if ("Southern Sotho".equals(pageLanguage)) return "st";
		if ("Spanish".equals(pageLanguage)) return "es";
		if ("Sundanese".equals(pageLanguage)) return "su";
		if ("Swahili".equals(pageLanguage)) return "sw";
		if ("Swati".equals(pageLanguage)) return "ss";
		if ("Swedish".equals(pageLanguage)) return "sv";
		if ("Tagalog".equals(pageLanguage)) return "tl";
		if ("Tahitian".equals(pageLanguage)) return "ty";
		if ("Tajik".equals(pageLanguage)) return "tg";
		if ("Tamil".equals(pageLanguage)) return "ta";
		if ("Tatar".equals(pageLanguage)) return "tt";
		if ("Telugu".equals(pageLanguage)) return "te";
		if ("Thai".equals(pageLanguage)) return "th";
		if ("Tibetan".equals(pageLanguage)) return "bo";
		if ("Tigrinya".equals(pageLanguage)) return "ti";
		if ("Tonga".equals(pageLanguage)) return "to";
		if ("Tsonga".equals(pageLanguage)) return "ts";
		if ("Tswana".equals(pageLanguage)) return "tn";
		if ("Turkish".equals(pageLanguage)) return "tr";
		if ("Turkmen".equals(pageLanguage)) return "tk";
		if ("Twi".equals(pageLanguage)) return "tw";
		if ("Uighur".equals(pageLanguage)) return "ug";
		if ("Ukrainian".equals(pageLanguage)) return "uk";
		if ("Urdu".equals(pageLanguage)) return "ur";
		if ("Uzbek".equals(pageLanguage)) return "uz";
		if ("Venda".equals(pageLanguage)) return "ve";
		if ("Vietnamese".equals(pageLanguage)) return "vi";
		if ("Volapük".equals(pageLanguage)) return "vo";
		if ("Walloon".equals(pageLanguage)) return "wa";
		if ("Welsh".equals(pageLanguage)) return "cy";
		if ("Western Frisian".equals(pageLanguage)) return "fy";
		if ("Wolof".equals(pageLanguage)) return "wo";
		if ("Xhosa".equals(pageLanguage)) return "xh";
		if ("Yiddish".equals(pageLanguage)) return "yi";
		if ("Yoruba".equals(pageLanguage)) return "yo";
		if ("Zhuang".equals(pageLanguage)) return "za";
		if ("Zulu".equals(pageLanguage)) return "zu";

		return null;
	}
	
	//Propagate text from regions to words / glyphs
	void propagateText() {
		//Regions
		for (int i=0; i<layout.getRegionCount(); i++) {
			if (layout.getRegion(i) instanceof TextRegion) {
				TextRegion textRegion = (TextRegion)layout.getRegion(i);
				if (textRegion.getTextObjectCount() == 0)
					continue;
				
				String regionText = textRegion.getText() != null && !textRegion.getText().isEmpty() ? textRegion.getText() : textRegion.composeText(false, true);
				regionText.replaceAll("\r\n", "\n");
				
				String[] regionTextSplit = regionText.split("\n");
				
				//Text lines
				List<TextLine> textLinesSorted = new ArrayList<TextLine>(textRegion.getTextObjectCount());
				for (int t=0; t<textRegion.getTextObjectCount(); t++)
					textLinesSorted.add((TextLine)textRegion.getTextObject(t));
				// Sort
				Collections.sort(textLinesSorted, new TextObjectComparator(true, true));
				
				for (int t=0; t<textLinesSorted.size(); t++) {
					TextLine textLine = textLinesSorted.get(t);
					
					String textLineText = textLine.getText() != null && !textLine.getText().isEmpty() ? textLine.getText() : textLine.composeText(false, true);
					if (textLineText.isEmpty() && t < regionTextSplit.length)
						textLineText = regionTextSplit[t];						
					
					String[] textLineTextSplit = textLineText.split(" ");
					
					//Words
					List<Word> wordsSorted = new ArrayList<Word>(textLine.getTextObjectCount());
					for (int w=0; w<textLine.getTextObjectCount(); w++)
						wordsSorted.add((Word)textLine.getTextObject(w));
					// Sort
					Collections.sort(wordsSorted, new TextObjectComparator(false, true));
					
					for (int w=0; w<wordsSorted.size(); w++) {
						Word word = wordsSorted.get(w);
						
						String popagatedWordText = "";
						if (w < textLineTextSplit.length) {
							popagatedWordText = textLineTextSplit[w];
							propagatedWordTexts.put(word.getId().toString(), popagatedWordText);
						}
						
						String wordText = word.getText() != null && !word.getText().isEmpty() ? word.getText() : word.composeText(false, true);
						if (wordText.isEmpty())
							wordText = popagatedWordText;
						
						//Glyphs
						List<Glyph> glyphsSorted = new ArrayList<Glyph>(word.getTextObjectCount());
						for (int g=0; g<word.getTextObjectCount(); g++)
							glyphsSorted.add((Glyph)word.getTextObject(g));
						// Sort
						Collections.sort(glyphsSorted, new TextObjectComparator(false, true));
						
						for (int g=0; g<glyphsSorted.size(); g++) {
							Glyph glyph = glyphsSorted.get(g);
							
							String popagatedGlyphText = "";
							if (g < wordText.length()) {
								popagatedGlyphText = "" + wordText.charAt(g);
								propagatedGlyphTexts.put(glyph.getId().toString(), popagatedGlyphText);
							}
						}
					}
				}
			}
		}
			
	}
	
	private static final class TextObjectComparator implements Comparator<GeometricObject> {
		
		private boolean sortVertically;
		private boolean ascending;
		
		public TextObjectComparator(boolean sortVertically, boolean ascending) {
			this.sortVertically = sortVertically;
			this.ascending = ascending;
		}

		@Override
		public int compare(GeometricObject obj1, GeometricObject obj2) {
			if (obj1 == null || obj2 == null || obj1.getCoords() == null || obj2.getCoords() == null)
				return 0;
			
			Rect box1 = obj1.getCoords().getBoundingBox();
			Rect box2 = obj2.getCoords().getBoundingBox();
			
			if (sortVertically) {
				int c1 = (box1.top + box1.bottom) / 2; 
				int c2 = (box2.top + box2.bottom) / 2;
				if (ascending)
					return c1 - c2;
				else //descending 
					return c2 - c1;
			}
			else { //horizontally
				int c1 = (box1.left + box1.right) / 2; 
				int c2 = (box2.left + box2.right) / 2;
				if (ascending)
					return c1 - c2;
				else //descending 
					return c2 - c1;
			}
		}
		
	}
}
