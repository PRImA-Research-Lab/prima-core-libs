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
package org.primaresearch.io.xml.variable;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.MalformedURLException;

import org.junit.Test;
import org.primaresearch.shared.variable.BooleanValue;
import org.primaresearch.shared.variable.BooleanVariable;
import org.primaresearch.shared.variable.StringValue;
import org.primaresearch.shared.variable.StringVariable;
import org.primaresearch.shared.variable.VariableMap;
import org.primaresearch.shared.variable.constraints.ValidStringValues;

public class XmlVariableFileTest {

	@Test
	public void test() {
		
		//Output file
		File outputFile = new File("e:\\temp\\debug\\XmlVariableFileTest.xml");
		if (outputFile.exists())
			assertTrue(outputFile.delete());
		
		//Create variables
		// V1
		StringVariable v1 = new StringVariable("v1");
		v1.setCaption("c1");
		v1.setDescription("v1");
		v1.setId(1);
		v1.setReadOnly(true);
		v1.setSortIndex(1);
		v1.setTextType(1);
		v1.setVersion(1);
		v1.setVisible(false);
		try {
			v1.setValue(new StringValue("v"));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		ValidStringValues validValues = new ValidStringValues();
		validValues.addValidValue("v");
		validValues.addValidValue("w");
		v1.setConstraint(validValues);
		
		// V2
		BooleanVariable v2 = new BooleanVariable("v2");
		try {
			v2.setValue(new BooleanValue(true));
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
		
		//Create map
		VariableMap vars = new VariableMap();
		vars.add(v1);
		vars.add(v2);
		
		//Write
		XmlVariableFileWriter writer = new XmlVariableFileWriter();
		writer.write(vars, outputFile);
		
		//Read
		XmlVariableFileReader reader = new XmlVariableFileReader();
		try {
			vars = reader.read(outputFile.toURI().toURL());
		} catch (MalformedURLException e) {
			fail();
			e.printStackTrace();
		}
		assertNotNull(vars);
		
		//Check variables
		StringVariable r1 = (StringVariable)vars.getById(1);
		assertTrue(v1.getName().equals(r1.getName()));
		assertTrue(v1.getCaption().equals(r1.getCaption()));
		assertTrue(v1.getDescription().equals(r1.getDescription()));
		assertTrue(v1.getSortIndex() == r1.getSortIndex());
		assertTrue(v1.getTextType() == r1.getTextType());
		assertTrue(v1.getVersion() == r1.getVersion());
		assertTrue(((StringValue)v1.getValue()).val.equals(((StringValue)r1.getValue()).val));
		assertNotNull(r1.getConstraint());
		validValues = (ValidStringValues)r1.getConstraint();
		assertTrue(validValues.getValidValues().size() == 2);
		assertTrue(validValues.getValidValues().iterator().next().equals("v") || validValues.getValidValues().iterator().next().equals("w") );
		
		BooleanVariable r2 = (BooleanVariable)vars.get("v2");
		assertTrue(((BooleanValue)v2.getValue()).val == ((BooleanValue)r2.getValue()).val);
		
	}

}
