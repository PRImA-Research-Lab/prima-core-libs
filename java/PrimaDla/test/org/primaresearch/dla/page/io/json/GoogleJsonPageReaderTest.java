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
package org.primaresearch.dla.page.io.json;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.primaresearch.dla.page.Page;
import org.primaresearch.dla.page.io.FileInput;
import org.primaresearch.dla.page.io.xml.PageXmlInputOutput;
import org.primaresearch.io.UnsupportedFormatVersionException;
import org.primaresearch.io.xml.XmlModelAndValidatorProvider.UnsupportedSchemaVersionException;

public class GoogleJsonPageReaderTest {

	File reid2019json;

	@Before
	public void setUp() throws Exception {
		reid2019json = new File("c:/junit/json/279_2_A_28_0002.json");
		if (!reid2019json.exists())
			throw new Exception("Page JSON file not found: "+ reid2019json.getPath());

	}
	
	@Test
	public void testRead() {
		GoogleJsonPageReader reader = new GoogleJsonPageReader();
		Page page = null;
		try {
			page = reader.read(new FileInput(reid2019json));
		} catch (UnsupportedFormatVersionException e) {
			e.printStackTrace();
		}
		
		assertNotNull(page);
		
		assertEquals(9, page.getLayout().getRegionCount());
		
		try {
			PageXmlInputOutput.writePage(page, "c:/junit/json/279_2_A_28_0002_output.xml");
		} catch (UnsupportedSchemaVersionException e) {
			e.printStackTrace();
		}

	}

}
