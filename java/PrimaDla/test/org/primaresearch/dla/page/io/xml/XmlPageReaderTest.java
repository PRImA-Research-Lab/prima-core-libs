/*
 * Copyright 2014 PRImA Research Lab, University of Salford, United Kingdom
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.primaresearch.dla.page.MetaData;
import org.primaresearch.dla.page.Page;
import org.primaresearch.dla.page.io.FileInput;
import org.primaresearch.dla.page.io.xml.PageXmlInputOutput;
import org.primaresearch.dla.page.io.xml.XmlPageReader;
import org.primaresearch.dla.page.layout.PageLayout;
import org.primaresearch.dla.page.layout.logical.Group;
import org.primaresearch.dla.page.layout.logical.Layer;
import org.primaresearch.dla.page.layout.logical.Layers;
import org.primaresearch.dla.page.layout.logical.ReadingOrder;
import org.primaresearch.dla.page.layout.physical.ContentObject;
import org.primaresearch.dla.page.layout.physical.Region;
import org.primaresearch.dla.page.layout.physical.shared.RegionType;
import org.primaresearch.dla.page.layout.physical.text.TextObject;
import org.primaresearch.dla.page.layout.physical.text.impl.TextLine;
import org.primaresearch.dla.page.layout.physical.text.impl.TextRegion;
import org.primaresearch.dla.page.layout.physical.text.impl.Word;
import org.primaresearch.io.UnsupportedFormatVersionException;
import org.primaresearch.io.xml.XmlModelAndValidatorProvider.NoSchemasException;
import org.primaresearch.shared.variable.DoubleValue;
import org.primaresearch.shared.variable.StringValue;
import org.primaresearch.shared.variable.Variable;
import org.primaresearch.shared.variable.Variable.WrongVariableTypeException;

public class XmlPageReaderTest {
	File xmlPageFile;
	File xmlPageModFile;
	File xmlReadingOrderPageFile;
	File xmlLayersPageFile;
	File largeXmlPageFile;
	File invalidXmlPageFile;
	File schema_2013_07_15_File;

	@Before
	public void setUp() throws Exception {
		xmlPageFile = new File("c:/junit/page.xml");
		if (!xmlPageFile.exists())
			throw new Exception("Page XML file not found: "+ xmlPageFile.getPath());
		
		largeXmlPageFile = new File("c:/junit/largePage.xml");
		if (!largeXmlPageFile.exists())
			throw new Exception("Page XML file not found: "+ largeXmlPageFile.getPath());

		xmlReadingOrderPageFile = new File("c:/junit/readingOrder.xml");
		if (!xmlReadingOrderPageFile.exists())
			throw new Exception("Page XML file not found: "+ xmlReadingOrderPageFile.getPath());

		xmlLayersPageFile = new File("c:/junit/layers.xml");
		if (!xmlLayersPageFile.exists())
			throw new Exception("Page XML file not found: "+ xmlLayersPageFile.getPath());

		invalidXmlPageFile = new File("c:/junit/invalidPage.xml");
		if (!invalidXmlPageFile.exists())
			throw new Exception("Page XML file not found: "+ invalidXmlPageFile.getPath());

		xmlPageModFile = new File("c:/junit/pageMod.xml");
		if (!xmlPageModFile.exists())
			throw new Exception("Page XML file not found: "+ xmlPageModFile.getPath());

		schema_2013_07_15_File = new File("c:/junit/page_2013-07-15.xml");
		if (!schema_2013_07_15_File.exists())
			throw new Exception("Page XML file not found: "+ schema_2013_07_15_File.getPath());
	}

	@SuppressWarnings("unused")
	@Test
	public void testRead() {
		XmlPageReader reader = PageXmlInputOutput.getReader();
		
		Page page = null;
		try {
			page = reader.read(new FileInput(xmlPageFile));
		} catch (UnsupportedFormatVersionException e) {
			e.printStackTrace();
		}
		
		assertNotNull(page);
		
		//Meta data
		MetaData metaData = page.getMetaData();
		assertNotNull(metaData);
		
		assertEquals("TestCreator", metaData.getCreator());
		assertEquals("TestComments", metaData.getComments());
		
		//Layout
		PageLayout layout = page.getLayout();
		assertNotNull(layout);
		
		//Check width and height
		assertEquals(1000, layout.getWidth());
		assertEquals(500, layout.getHeight());
		
		//Border and print space
		assertNotNull(layout.getBorder());
		assertEquals(4, layout.getBorder().getCoords().getSize());
		assertNotNull(layout.getPrintSpace());
		assertEquals(4, layout.getPrintSpace().getCoords().getSize());
		
		//Regions
		assertEquals(11, layout.getRegionCount());
		
		//Low level text objects
		for (int i=0; i<layout.getRegionCount(); i++) {
			//Text region?
			if (layout.getRegion(i).getType() == RegionType.TextRegion) {
				
				//Id
				assertEquals("r1", layout.getRegion(i).getId().toString());
				
				//Attributes
				TextObject textObj = (TextObject)layout.getRegion(i);
				ContentObject contentObject = layout.getRegion(i);
				assertEquals(((DoubleValue)contentObject.getAttributes().get("fontSize").getValue()).val, 10.0, 0.01);
				
				//Text
				assertEquals("Test", ((TextObject)layout.getRegion(i)).getText());
				
				//Children
				assertEquals(((TextRegion)layout.getRegion(i)).getTextObjectCount(), 1);
				
				TextLine line = (TextLine)((TextRegion)layout.getRegion(i)).getTextObject(0);
				
				assertEquals(line.getTextObjectCount(), 1);
				
				Word word = (Word)(line.getTextObject(0));

				assertEquals(word.getTextObjectCount(), 1);
				break;
			}
		}
		
		
		//fail("Not yet implemented");
	}

	@Test
	public void testReadLargeFile() {
		
		//This test method is just for performance measuring 
		
		XmlPageReader reader = PageXmlInputOutput.getReader();
		
		Page page = null;
		try {
			page = reader.read(new FileInput(largeXmlPageFile));
		} catch (UnsupportedFormatVersionException e) {
			e.printStackTrace();
		}
		
		assertNotNull(page);
	}
	
	@Test
	public void testReadSchema20130715File() {
		
		//This test method is just for performance measuring 
		
		XmlPageReader reader = PageXmlInputOutput.getReader();
		
		Page page = null;
		try {
			page = reader.read(new FileInput(schema_2013_07_15_File));
		} catch (UnsupportedFormatVersionException e) {
			e.printStackTrace();
		}
		
		assertNotNull(page);
	}
	
	@Test
	public void testReadingOrder() {
		XmlPageReader reader = PageXmlInputOutput.getReader();
		
		Page page = null;
		try {
			page = reader.read(new FileInput(xmlReadingOrderPageFile));
		} catch (UnsupportedFormatVersionException e) {
			e.printStackTrace();
		}
		assertNotNull(page);

		//Layout
		PageLayout layout = page.getLayout();
		assertNotNull(layout);
		
		ReadingOrder order = layout.getReadingOrder();
		assertNotNull(order);
		
		Group root = order.getRoot();
		assertNotNull(order);
		assertEquals(2, root.getSize());
		assertFalse("Reading order root is supposed to be unordered", root.isOrdered());
		assertEquals("ro357564684568544579089", root.getId().toString());
		
		//The two children are ordered groups
		Group group = (Group)root.getMember(0);
		assertTrue("Reading order group is supposed to be ordered", group.isOrdered());
		group = (Group)root.getMember(1);
		assertTrue("Reading order group is supposed to be ordered", group.isOrdered());
	}
	
	@Test
	public void testLayers() {
		XmlPageReader reader = PageXmlInputOutput.getReader();
		
		Page page = null;
		try {
			page = reader.read(new FileInput(xmlLayersPageFile));
		} catch (UnsupportedFormatVersionException e) {
			e.printStackTrace();
		}
		assertNotNull(page);

		//Layout
		PageLayout layout = page.getLayout();
		assertNotNull(layout);
		
		Layers layers = layout.getLayers();
		assertNotNull(layers);
		
		assertEquals(2, layers.getSize());
		
		//Check sizes and zIndex of layers
		Layer layer = layers.getLayer(0);
		assertEquals(1, layer.getSize());
		assertEquals(0, layer.getZIndex());
		layer = layers.getLayer(1);
		assertEquals(3, layer.getSize());
		assertEquals(2, layer.getZIndex());
	}
	
	@SuppressWarnings("unused")
	@Test
	public void testValidate() {
		
		//try {
		//	XmlInputOutput.setSchemaLocation("c:/junit/schema");
		//} catch (NoSchemasException e) {
		//	fail(e.getMessage());
		//}
		
		XmlPageReader reader = PageXmlInputOutput.getReader();
		
		//Valid file
		Page page = null;
		try {
			page = reader.read(new FileInput(xmlPageFile));
		} catch (UnsupportedFormatVersionException e) {
			e.printStackTrace();
		}
		assertNotNull(page);

		//Try to set valid attribute value
		try {
			TextObject textObj = (TextObject)page.getLayout().getRegion(0);
			ContentObject contentObject = page.getLayout().getRegion(0);
			Variable attrib = contentObject.getAttributes().get("textColour");
			attrib.setValue(new StringValue("blue"));
		} catch (Exception e1) {
			e1.printStackTrace();
			fail(e1.getMessage());
		}
		//Try to set invalid attribute value
		try {
			TextObject textObj = (TextObject)page.getLayout().getRegion(0);
			ContentObject contentObject = page.getLayout().getRegion(0);
			Variable attrib = contentObject.getAttributes().get("textColour");
			attrib.setValue(new StringValue("somethingInvalid"));
			fail("Exception was expected");
		} catch (IllegalArgumentException e1) {
			//Expected
		} catch (WrongVariableTypeException e1) {
			e1.printStackTrace();
			fail(e1.getMessage());
		}

		//Invalid file
		try {
			page = reader.read(new FileInput(invalidXmlPageFile));
		} catch (UnsupportedFormatVersionException e) {
			e.printStackTrace();
		}
		
		assertNull(page);
	}
	
	@Test
	public void testValidateLargeFile() {
		
		//This test method is just for performance measuring 
		//try {
		//	XmlInputOutput.setSchemaLocation("c:/junit/schema");
		//} catch (NoSchemasException e) {
		//	fail(e.getMessage());
		//}
		
		XmlPageReader reader = PageXmlInputOutput.getReader();
		
		Page page = null;
		try {
			page = reader.read(new FileInput(largeXmlPageFile));
		} catch (UnsupportedFormatVersionException e) {
			e.printStackTrace();
		}
		
		assertNotNull(page);
	}

	@Test
	public void testValidateWithModifiedSchema() {
		//Try to open the modified page file using the normal schema 
		//try {
		//	XmlInputOutput.setSchemaLocation("c:/junit/schema");
		//} catch (NoSchemasException e) {
		//	fail(e.getMessage());
		//}
		XmlPageReader reader = PageXmlInputOutput.getReader();
		try {
			reader.read(new FileInput(xmlPageModFile));
			fail("Exception was expected");
		} catch (UnsupportedFormatVersionException exc) {
			//expected
		}
		
		//Now open the modified page file using the correct schema
		try {
			PageXmlInputOutput.setAdditionalSchemaLocation("c:/junit/schemamod");
		} catch (NoSchemasException e) {
			fail(e.getMessage());
		}
		reader = PageXmlInputOutput.getReader();
		Page page = null;
		try {
			page = reader.read(new FileInput(xmlPageModFile));
			assertNotNull(page);
			
		} catch (UnsupportedFormatVersionException exc) {
			fail(exc.getMessage());
		}
		//Check the 'dialect' attribute
		Region reg = page.getLayout().getRegion(0);
		assertNotNull(reg);
		assertNotNull(reg.getAttributes());
		assertNotNull(reg.getAttributes().get("dialect"));
		assertEquals("Saxon", reg.getAttributes().get("dialect").getValue().toString());
	}
}
