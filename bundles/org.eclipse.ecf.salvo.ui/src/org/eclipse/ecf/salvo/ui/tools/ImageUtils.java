/*******************************************************************************
 *  Copyright (c) 2011 University Of Moratuwa
 *                                                                      
 * All rights reserved. This program and the accompanying materials     
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at             
 * http://www.eclipse.org/legal/epl-v10.html                            
 *                                                                      
 * Contributors:                                                        
 *    Isuru Udana - UI Integration in the Workbench
 *******************************************************************************/
package org.eclipse.ecf.salvo.ui.tools;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.Bundle;

/**
 * This class is responsible for providing utilities for adding images to UI
 * @author isuru
 *
 */
public class ImageUtils {
	private Map<String, ImageDescriptor> images;
	private static ImageUtils INSTANCE;
	
	/**
	 * @return an instance of ImageUtils
	 */
	public static ImageUtils getInstance(){
		if (INSTANCE==null){
			INSTANCE=new ImageUtils();
		}
		return INSTANCE;
	}
	
	/**
	 * Create image descriptors 
	 * @param imageName image file name with extension
	 * @return
	 */
	private ImageDescriptor createImageDescriptor(String imageName) {
		if (imageName == null)
			return null;
		ImageDescriptor imageDescriptor = null;
		IPath path = new Path(getImageLocation() + imageName);
		
		URL gifImageURL = FileLocator.find(getBundle(), path, null);
		if (gifImageURL != null){
			imageDescriptor = ImageDescriptor.createFromURL(gifImageURL);
		}
		return imageDescriptor;
	}

	/**
	 * Get image descriptor
	 * @param name image file name with extension
	 * @return ImageDescriptor
	 */
	public ImageDescriptor getImageDescriptor(String name) {
		if (images == null) {
			images = new HashMap<String, ImageDescriptor>();
		}
		if (!images.containsKey(name)){
			images.put(name, createImageDescriptor(name));
		}
		return images.get(name);
	}
	
	/**
	 * 
	 * @return bundle corresponds to the UI
	 */
	private Bundle getBundle(){
		return Platform.getBundle("org.eclipse.ecf.salvo.ui");
	}
	
	/**
	 * 
	 * @return Image location in the bundle
	 */
	private String getImageLocation() {
		return "icons/";
	}
	
}
