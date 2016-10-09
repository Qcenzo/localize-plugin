/*
MIT License

Copyright (c) 2016 Qcenzo

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software. 

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package com.qcenzo.tools.localize;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.qcenzo.tools.localize.preferences.LocalizationPreferenceConstants;

public class Activator extends AbstractUIPlugin 
{
	public static final String PLUGIN_ID = "com.qcenzo.tools.localize"; 
	private static Activator _plugin;
	
	public Activator() 
	{
	}

	public void start(BundleContext context) throws Exception 
	{
		super.start(context);
		_plugin = this;
	}

	public void stop(BundleContext context) throws Exception 
	{
		_plugin = null;
		super.stop(context);
	}

	public static Activator getDefault() 
	{
		return _plugin;
	}

	public static ImageDescriptor getImageDescriptor(String path) 
	{
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	protected void initializeDefaultPreferences(IPreferenceStore store)
    {
		store.setDefault(LocalizationPreferenceConstants.TRANSLATOR, TranslatorManager.getInstance().defaultTranslator);
		store.setDefault(LocalizationPreferenceConstants.FROMTO, TranslatorManager.getInstance().defaultFromTo);
		store.setDefault(LocalizationPreferenceConstants.EXTENSIONS, "java");
		store.setDefault(LocalizationPreferenceConstants.REGEX, "/\\\\*((?:.|\\r|\\n)*?)\\\\*/|//(.*)");
    }
}
