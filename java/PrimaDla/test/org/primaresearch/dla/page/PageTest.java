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
package org.primaresearch.dla.page;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.primaresearch.dla.page.Page;
import org.primaresearch.dla.page.io.xml.PageXmlInputOutput;
import org.primaresearch.dla.page.layout.physical.shared.RegionType;
import org.primaresearch.dla.page.layout.physical.text.impl.TextRegion;
import org.primaresearch.io.UnsupportedFormatVersionException;
import org.primaresearch.io.xml.XmlFormatVersion;

public class PageTest {

	@Test
	public void testSetFormatVersion() {
		//Create new page (latest schema) 
		Page page = new Page();
		
		//Add a text region
		TextRegion region = (TextRegion)page.getLayout().createRegion(RegionType.TextRegion);
		
		//Set the text type to a valid value (according to the latest schema)
		region.setTextType("TOC-entry");
		
		//Change the format to an older version
		try {
			page.setFormatVersion(PageXmlInputOutput.getInstance().getFormatModel(new XmlFormatVersion("2009-03-16")));
		} catch (UnsupportedFormatVersionException e) {
			e.printStackTrace();
		}
		
		//try to set the text type again, this time it should be invalid 
		try {
			region.setTextType("TOC-entry");
			fail("Exception was expected");
		} catch(IllegalArgumentException exc) {
			//expected
		}
	}

}
