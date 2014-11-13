/**********************************************************************************
 * Copyright (c) 2010 MATERNA Information & Communications and TU Dortmund, Dpt.
 * of Computer Science, Chair 4, Distributed Systems All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 **********************************************************************************/
package com.zanivan;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;

import org.ws4d.java.DPWSFramework;

/**
 * This is just a frame to display the picture in the attachment. It stops the
 * example after 60 seconds.
 */
public class PictureFrame extends Frame implements Runnable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -7815163046017389091L;

	Image						img;

	Thread						th;

	Image						dbImage;

	Graphics					dbGraphics;

	long						x					= System.currentTimeMillis();

	private static PictureFrame	instance			= null;

	private PictureFrame(Image pic) {
		super("AttachmentEvent");
		setSize(978, 505);
		this.setUndecorated(true);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		img = pic;
		this.setVisible(true);
		th = new Thread(this);
		th.start();
	}

	public static void setEventImage(Image pic) {
		if (instance == null) {
			instance = new PictureFrame(pic);
			return;
		}

		instance.img = pic;
		instance.setName("AttachmentEvent");
	}

	public void update(Graphics g) {
		if (dbImage == null) {
			dbImage = createImage(this.getSize().width, this.getSize().height);
			dbGraphics = dbImage.getGraphics();
		}
		dbGraphics.clearRect(0, 0, this.getWidth(), this.getHeight());
		paint(dbGraphics);
		g.drawImage(dbImage, 0, 0, this);
	}

	public void paint(Graphics g) {
		g.drawImage(img, 0, 0, null);
	}

	public void run() {
		while (true) {
			repaint();
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if ((System.currentTimeMillis() - x) >= 60000) {
				DPWSFramework.stop();
				System.exit(0);
			}
		}
	}

}
