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
package org.primaresearch.dla.page.layout.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.primaresearch.dla.page.Page;
import org.primaresearch.dla.page.io.xml.PageXmlInputOutput;
import org.primaresearch.dla.page.layout.PageLayout;
import org.primaresearch.dla.page.layout.converter.ConversionMessage;
import org.primaresearch.dla.page.layout.converter.ConverterHub;
import org.primaresearch.dla.page.layout.physical.shared.RegionType;
import org.primaresearch.dla.page.layout.physical.text.impl.TextRegion;
import org.primaresearch.io.UnsupportedFormatVersionException;
import org.primaresearch.io.xml.XmlFormatVersion;
import org.primaresearch.io.xml.XmlModelAndValidatorProvider.NoSchemasException;
import org.primaresearch.shared.variable.StringValue;
import org.primaresearch.shared.variable.Variable;
import org.primaresearch.shared.variable.Variable.WrongVariableTypeException;

public class ConverterHubTest {

	@Test
	public void testConvert() {
		
		try {
			PageXmlInputOutput.setAdditionalSchemaLocation(null, null);
		} catch (NoSchemasException e1) {
			e1.printStackTrace();
		}
		
		//Convert from 2010-03-19 to 2010-01-12
		Page page = new Page();
		
		PageLayout layout =	page.getLayout();
		
		TextRegion reg = (TextRegion)layout.createRegion(RegionType.TextRegion);
		Variable textType = reg.getAttributes().get("type");
		try {
			textType.setValue(new StringValue("TOC-entry"));
		} catch (WrongVariableTypeException e) {
			e.printStackTrace();
		}
		
		List<ConversionMessage> messages = null;
		try {
			messages = ConverterHub.convert(page, PageXmlInputOutput.getInstance().getFormatModel(new XmlFormatVersion("2010-01-12")));
		} catch (UnsupportedFormatVersionException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertNotNull(messages);
		assertEquals(1, messages.size());
		assertNull(textType.getValue());
		
		//Convert from 2018-07-15 to 2010-03-19
		page = new Page();
		layout =	page.getLayout();
		
		// Text type
		reg = (TextRegion)layout.createRegion(RegionType.TextRegion);
		textType = reg.getAttributes().get("type");
		try {
			textType.setValue(new StringValue("endnote"));
		} catch (WrongVariableTypeException e) {
			e.printStackTrace();
		}
		
		// Music region
		layout.createRegion(RegionType.MusicRegion);
		
		messages = null;
		try {
			messages = ConverterHub.convert(page, PageXmlInputOutput.getInstance().getFormatModel(new XmlFormatVersion("2010-03-19")));
		} catch (UnsupportedFormatVersionException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertNotNull(messages);
		assertEquals(2, messages.size());
		assertNull(textType.getValue());
		
		// Text type
		reg = (TextRegion)layout.createRegion(RegionType.TextRegion);
		textType = reg.getAttributes().get("type");
		try {
			textType.setValue(new StringValue("endnote"));
			fail("Exception expected - endnote");
		} catch (WrongVariableTypeException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			//Expected
		}

		//Convert from 2018-07-15 to 2013-07-15
		try {
			page = PageXmlInputOutput.readPage("d:\\temp\\debug\\00787305.xml");
			messages = ConverterHub.convert(page, PageXmlInputOutput.getInstance().getFormatModel(new XmlFormatVersion("2013-07-15")));
			assertTrue(PageXmlInputOutput.writePage(page, "d:\\temp\\debug\\00787305_out.xml"));
		} catch (UnsupportedFormatVersionException e1) {
			e1.printStackTrace();
		}

		//Convert from 2019-07-15 to 2013-07-15
		try {
			File allFeatures2019_converted_to_2013 = new File("c:\\\\junit\\\\allFeatures2019_converted_to_2013.xml");
			if (allFeatures2019_converted_to_2013.exists()) 
				assertTrue(allFeatures2019_converted_to_2013.delete());
			
			page = PageXmlInputOutput.readPage("c:\\junit\\allFeatures2019.xml");
			messages = ConverterHub.convert(page, PageXmlInputOutput.getInstance().getFormatModel(new XmlFormatVersion("2013-07-15")));
			assertTrue(PageXmlInputOutput.writePage(page, allFeatures2019_converted_to_2013.getAbsolutePath()));
		} catch (UnsupportedFormatVersionException e1) {
			e1.printStackTrace();
		}
	}

	@Test
	public void testCheckForCompliance() {
		
		try {
			PageXmlInputOutput.setAdditionalSchemaLocation(null, null);
		} catch (NoSchemasException e1) {
			e1.printStackTrace();
		}
		
		//Check for compliance against 2010-01-12
		Page page = new Page();
		
		PageLayout layout =	page.getLayout();
		
		TextRegion reg = (TextRegion)layout.createRegion(RegionType.TextRegion);
		Variable textType = reg.getAttributes().get("type");
		try {
			textType.setValue(new StringValue("TOC-entry"));
		} catch (WrongVariableTypeException e) {
			e.printStackTrace();
		}
		
		List<ConversionMessage> messages = ConverterHub.checkForCompliance(page, new XmlFormatVersion("2010-01-12"));
		assertNotNull(messages);
		assertEquals(1, messages.size());
		assertNotNull(textType.getValue());
	}

}
