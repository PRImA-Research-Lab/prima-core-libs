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
package org.primaresearch.shared.variable;

import java.io.Serializable;

/**
 * Interface for formatters that convert double values to strings.<br>
 * This had to be decoupled from the default Java interface to be compatible with GWT
 * (which comes with it's own NumberFormat for client code).
 * 
 * @author Christian Clausner
 *
 */
public interface DoubleValueFormatter extends Serializable {

	/** Formats the given double value as a string. */
	String format(double value);
}
