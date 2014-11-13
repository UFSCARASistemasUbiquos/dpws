/**********************************************************************************
 * Copyright (c) 2010 MATERNA Information & Communications and TU Dortmund, Dpt.
 * of Computer Science, Chair 4, Distributed Systems All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 **********************************************************************************/
package com.zanivan;

import java.io.IOException;

import org.ws4d.java.DPWSFramework;
import org.ws4d.java.schema.ComplexType;
import org.ws4d.java.schema.Element;
import org.ws4d.java.schema.SchemaUtil;
import org.ws4d.java.service.DefaultEventSource;
import org.ws4d.java.service.DefaultService;
import org.ws4d.java.service.ServiceSubscription;
import org.ws4d.java.service.parameter.ParameterValue;
import org.ws4d.java.types.QName;
import org.ws4d.java.util.Log;
import org.ws4d.java.util.ParameterUtil;

public class EventingService extends DefaultService {

	private SimpleNotification		mySimpleNotification;

	private SolicitResponseEvent	mySolicitResponseEvent;

	private AttachmentEvent			myAttachmentEvent;

	private int						countNotif		= 0;

	private int						countSR			= 0;

	private int						countAttachment	= 0;

	EventingService(int i) {
		// give the configID to the super class to be processed.
		super(i);

		mySimpleNotification = new SimpleNotification();
		this.addEventSource(mySimpleNotification);
		mySolicitResponseEvent = new SolicitResponseEvent();
		this.addEventSource(mySolicitResponseEvent);
		myAttachmentEvent = new AttachmentEvent();
		this.addEventSource(myAttachmentEvent);
	}

	public void fireSolicitEvent() {
		// To fire this event we have to create the appropriate ParameterValue
		// object.
		ParameterValue pv = mySolicitResponseEvent.createOutputValue();

		// we set the values called "notif_x" and "notif_y" to example values
		ParameterUtil.setString(pv, SolicitResponseEvent.NOTIF_X, "2");
		ParameterUtil.setString(pv, SolicitResponseEvent.NOTIF_Y, "3");

		// and just fire the event with the ParameterValue and the events
		// number.
		mySolicitResponseEvent.fire(pv, countSR);
		countSR++;
	}

	public void fireNotification() {
		// we don't need any content. This is only a notification.
		mySimpleNotification.fire(null, countNotif);
		countNotif++;
	}

	public void fireAttachmentEvent() {
		ParameterValue pv = myAttachmentEvent.createOutputValue();

		// We create a new FileAttachment. We construct this directly through
		// the
		// path to the file and the contentType (MIME-Type).
		try {
			ParameterUtil.setAttachment(pv, "Param", DPWSFramework.getAttachmentFactory().createFileAttachment(
					"E:\\worksd\\JMEDSServiceExpose\\src\\com\\zanivan\\002.jpg",
					"image/jpeg"));

			myAttachmentEvent.fire(pv, countAttachment);
		} catch (IOException e) {
			Log.error(e.getMessage());
		}
	}
}

/**
 * SimpleNotification This is an event. Clients may subscribe to receive this
 * event. In this example, there is no information in the notification, although
 * there can be.
 */
class SimpleNotification extends DefaultEventSource {

	public SimpleNotification() {
		super("SimpleNotification", new QName("EventingService", StartExample.MY_NAMESPACE));
	}

}

class SolicitResponseEvent extends DefaultEventSource {

	public static final String	NOTIF_X			= "notif_x";

	public static final String	NOTIF_Y			= "notif_y";

	public static final String	SOLICIT_MESSAGE	= "solicit_MESSAGE";

	public static final String	SOLICIT_X		= "solicit_x";

	public static final String	SOLICIT_Y		= "solicit_y";

	public SolicitResponseEvent() {
		super("SolicitResponse", new QName("EventingService", StartExample.MY_NAMESPACE));

		// This defines the format of the output and input.
		Element notifWrapperElement = new Element(new QName("SLNotification", StartExample.MY_NAMESPACE));
		// This complex type contains two elements. They will contain integers.
		ComplexType notifct = new ComplexType(new QName("SolicitNotifType", StartExample.MY_NAMESPACE), ComplexType.CONTAINER_SEQUENCE);
		notifct.addElement(new Element(new QName(NOTIF_X, StartExample.MY_NAMESPACE), SchemaUtil.getSchemaType(SchemaUtil.TYPE_INT)));
		notifct.addElement(new Element(new QName(NOTIF_Y, StartExample.MY_NAMESPACE), SchemaUtil.getSchemaType(SchemaUtil.TYPE_INT)));
		notifWrapperElement.setType(notifct);

		setOutput(notifWrapperElement);

		Element solicitRespWrapperElement = new Element(new QName("SResponse", StartExample.MY_NAMESPACE));
		ComplexType solicitRespct = new ComplexType(new QName("SolicitType", StartExample.MY_NAMESPACE), ComplexType.CONTAINER_SEQUENCE);
		solicitRespct.addElement(new Element(new QName(SOLICIT_MESSAGE, StartExample.MY_NAMESPACE), SchemaUtil.getSchemaType(SchemaUtil.TYPE_STRING)));
		solicitRespct.addElement(new Element(new QName(SOLICIT_X, StartExample.MY_NAMESPACE), SchemaUtil.getSchemaType(SchemaUtil.TYPE_INT)));
		solicitRespct.addElement(new Element(new QName(SOLICIT_Y, StartExample.MY_NAMESPACE), SchemaUtil.getSchemaType(SchemaUtil.TYPE_INT)));
		solicitRespWrapperElement.setType(solicitRespct);

		setInput(solicitRespWrapperElement);
	}

	public void solicitResponseReceived(final ParameterValue paramValue, final int eventNumber, ServiceSubscription subscription) {
		System.out.println("Solicit Response on Event " + eventNumber + " Message: " + ParameterUtil.getString(paramValue, SOLICIT_MESSAGE) + " - " + SOLICIT_X + " " + ParameterUtil.getString(paramValue, SOLICIT_X) + " " + SOLICIT_Y + " " + ParameterUtil.getString(paramValue, SOLICIT_Y));
	}
}

class AttachmentEvent extends DefaultEventSource {

	public AttachmentEvent() {
		super("AttachmentEvent", new QName("EventingService", StartExample.MY_NAMESPACE));

		Element notifWrapperElement = new Element(new QName("AttachmentEvent", StartExample.MY_NAMESPACE));
		ComplexType rqComplex = new ComplexType(new QName("AttachmentType", StartExample.MY_NAMESPACE), ComplexType.CONTAINER_SEQUENCE);
		rqComplex.addElement(new Element(new QName("Param", StartExample.MY_NAMESPACE), SchemaUtil.getSchemaType(SchemaUtil.TYPE_BASE64_BINARY)));
		notifWrapperElement.setType(rqComplex);

		setOutput(notifWrapperElement);
	}
}