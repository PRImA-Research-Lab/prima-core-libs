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
package org.primaresearch.io.ini;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.primaresearch.shared.variable.Variable;
import org.primaresearch.shared.variable.VariableMap;

/**
 * Writes variables to an .ini file.
 * 
 * @author Christian Clausner
 *
 */
public class IniVariableFileWriter {

	/**
	 * Writes the given variables to the specified file.<br>
	 * Uses <code>[main]</code> as section name.
	 * @param variables The variables to write
	 * @param file The target file
	 */
	public void write(VariableMap variables, File file) throws IOException {
		Writer out = new OutputStreamWriter(new FileOutputStream(file));
	    try {
	    	out.write("[main]");
	    	out.write(System.getProperty("line.separator"));
	    	for (int i=0; i<variables.getSize(); i++) {
	    		Variable v = variables.get(i);
	    		out.write(v.getName() + "=" + (v.getValue() != null ? v.getValue().toString() : "") + System.getProperty("line.separator"));
	    	}
	    }
	    finally {
	    	out.close();
	    }
	}
}
