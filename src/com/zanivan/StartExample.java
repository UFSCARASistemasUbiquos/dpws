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
import org.ws4d.java.service.DefaultDevice;
import org.ws4d.java.service.LocalService;
import org.ws4d.java.util.Log;

public class StartExample {

	//static String	MY_NAMESPACE	= "http://www.ws4d.org/jmeds/example/";
	static String	MY_NAMESPACE	= "example";

	StartExample() {
		setUpDevice();
	}

	public void setUpDevice() {
		// using the 1st device-configId from the properties file we
		// initialized.
		DefaultDevice device = new DefaultDevice(1);
		// Prepare our own services
		// 1st service-configId defines the TestService
		LocalService service = new TestService(1);
		
		// we have to add every service to our device
		device.addService(service);

		// 2nd service-configId defines the EventingService
		EventingService eventingService = new EventingService(2);
		device.addService(eventingService);

		try {
			device.start();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// this thread will fire our three events every 5 seconds.
		new FireThread(eventingService);

	}

	public static void main(String[] args) {
		// The first action we have to invoke is the start() method of the
		// DPWSFramework.
		DPWSFramework.start(new String[] { "E:\\worksd\\JMEDSServiceExpose\\src\\com\\zanivan\\example.properties" });

		// Here we initialize the properties file.
		// With properties it is possible to set e.g. bindings for services and
		// devices,
		// metadata for devices/services and ServicesIDs for services.
		// We don't need debug-output. So we disable it.

		Log.setLogLevel(Log.DEBUG_LEVEL_DEBUG);

		new StartExample();

		// We will have the client in the same VM as the service.
		// To test it remote you would have to comment the next
		// line of code out and look for the other main method in
		// the TestClient class.
		//new TestClient();
	}
}

class FireThread implements Runnable {

	Thread			th;

	EventingService	es;

	// reduce this value to stop the example after x times.
	long			times	= 90000;

	FireThread(EventingService es) {
		this.es = es;
		th = new Thread(this);
		th.start();
	}

	public void run() {
		while (times >= 0) {
			times--;
			// fire the events periodically to the subscribers.
			es.fireNotification();
			es.fireSolicitEvent();
			es.fireAttachmentEvent();
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		DPWSFramework.stop();
		System.exit(0);
	}

}