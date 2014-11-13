/**********************************************************************************
 * Copyright (c) 2010 MATERNA Information & Communications and TU Dortmund, Dpt.
 * of Computer Science, Chair 4, Distributed Systems All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 **********************************************************************************/
package com.zanivan;

import org.ws4d.java.communication.TimeoutException;
import org.ws4d.java.schema.ComplexType;
import org.ws4d.java.schema.Element;
import org.ws4d.java.schema.SchemaUtil;
import org.ws4d.java.service.DefaultService;
import org.ws4d.java.service.InvocationException;
import org.ws4d.java.service.Operation;
import org.ws4d.java.service.parameter.ParameterValue;
import org.ws4d.java.types.QName;
import org.ws4d.java.util.ParameterUtil;

public class TestService extends DefaultService {

	TestService(int i) {
		// Give the configID to the super class to be processed.
		super(i);

		this.addOperation(new OneWayOperation());
		this.addOperation(new TwoWayOperation());
		this.addOperation(new SomaOperation());
	}
}

/**
 * This is an operation without any return value. It just displays the value
 * contained in its input.
 */
class OneWayOperation extends Operation {

	static String	VALUE_ELEMENT	= "OneWayIntValue";

	public OneWayOperation() {
		super("OneWay", new QName("SimpleService", StartExample.MY_NAMESPACE));

		// We define the input for this method.
		Element oneWay = new Element(new QName(VALUE_ELEMENT, StartExample.MY_NAMESPACE), SchemaUtil.getSchemaType(SchemaUtil.TYPE_INT));
		this.setInput(oneWay);
	}

	/**
	 * We don't want to answer - therefore null is returned.
	 */
	public ParameterValue invoke(ParameterValue parameterValues) throws InvocationException, TimeoutException {
		System.out.println("We received the following value: " + ParameterUtil.getString(parameterValues, null));
		return null;
	}
}

/**
 * TwoWayOperation TwoWayOperations get input from and return output to the
 * caller. This Operation must be called with a temperature in deg Fahrenheit as
 * double value and will return the Celsius value of the temperature.
 */

class TwoWayOperation extends Operation {

	// These are the names for our elements. They will contain the temperature
	// informations.
	static String	FAHRENHEIT_VALUE	= "deg_fahrenheit";

	static String	CELSIUS_VALUE		= "deg_celsius";

	TwoWayOperation() {
		super("TwoWay", new QName("SimpleService", StartExample.MY_NAMESPACE));

		// This defines the format of the output and input.
		Element twoWayIn = new Element(new QName(FAHRENHEIT_VALUE, StartExample.MY_NAMESPACE), SchemaUtil.getSchemaType(SchemaUtil.TYPE_DOUBLE));
		this.setInput(twoWayIn);

		Element twoWayOut = new Element(new QName(CELSIUS_VALUE, StartExample.MY_NAMESPACE), SchemaUtil.getSchemaType(SchemaUtil.TYPE_DOUBLE));
		this.setOutput(twoWayOut);
	}

	/**
	 * If the method is invoked by the client this method will be called. The
	 * returned ParameterValue is the answer. It will be sent to the client.
	 */
	public ParameterValue invoke(ParameterValue parameterValues) throws InvocationException, TimeoutException {
		double fahrenheit = Double.parseDouble(ParameterUtil.getString(parameterValues, null));

		ParameterValue returnValue = createOutputValue();
		ParameterUtil.setString(returnValue, null, String.valueOf((fahrenheit - 32) * 5 / 9));

		return returnValue;
	}

}


class SomaOperation extends Operation {
	static String	A_VALUE	= "a";
	static String	B_VALUE	= "b";

	static String	X_VALUE		= "x";

	SomaOperation() {
		super("Soma", new QName("SimpleService", StartExample.MY_NAMESPACE));

		Element somaInputWrapperElement = new Element(new QName("SInput", StartExample.MY_NAMESPACE));
		ComplexType somaInput = new ComplexType(new QName("SomaInputType", StartExample.MY_NAMESPACE), ComplexType.CONTAINER_SEQUENCE);
		somaInput.addElement(new Element(new QName(A_VALUE, StartExample.MY_NAMESPACE), SchemaUtil.getSchemaType(SchemaUtil.TYPE_DOUBLE)));
		somaInput.addElement(new Element(new QName(B_VALUE, StartExample.MY_NAMESPACE), SchemaUtil.getSchemaType(SchemaUtil.TYPE_DOUBLE)));
		somaInputWrapperElement.setType(somaInput);
		this.setInput(somaInputWrapperElement);

		Element twoWayOut = new Element(new QName(X_VALUE, StartExample.MY_NAMESPACE), SchemaUtil.getSchemaType(SchemaUtil.TYPE_DOUBLE));
		this.setOutput(twoWayOut);
	}

	public ParameterValue invoke(ParameterValue parameterValues) throws InvocationException, TimeoutException {
		double a = Double.parseDouble(ParameterUtil.getString(parameterValues, A_VALUE));
		double b = Double.parseDouble(ParameterUtil.getString(parameterValues, B_VALUE));
		
		ParameterValue returnValue = createOutputValue();
		ParameterUtil.setString(returnValue, null, String.valueOf(a + b));

		return returnValue;
	}

}
