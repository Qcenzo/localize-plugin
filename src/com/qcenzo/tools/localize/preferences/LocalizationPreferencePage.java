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
package com.qcenzo.tools.localize.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.qcenzo.tools.localize.Activator;
import com.qcenzo.tools.localize.TranslatorManager;

public class LocalizationPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage 
{
	private String _translator;
	private DoubleCombosEditor _langOptionEditor;
	
	public LocalizationPreferencePage() 
	{
		super(FLAT);
	}

	public void init(IWorkbench workbench)
	{
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}
	
	public void createFieldEditors() 
	{
		// Field 'translation interfaces'
		RadioGroupFieldEditor radioGroup = new RadioGroupFieldEditor(LocalizationPreferenceConstants.TRANSLATOR, "Translation interface:", 1, 
				TranslatorManager.getInstance().labelAndValues, getFieldEditorParent());
		radioGroup.setPropertyChangeListener(this); 
		addField(radioGroup);
		
		// Field 'Language option'
		_langOptionEditor = new DoubleCombosEditor(LocalizationPreferenceConstants.FROMTO, "&Language option:", 
				TranslatorManager.getInstance().getItems(Activator.getDefault().getPreferenceStore().getString(LocalizationPreferenceConstants.TRANSLATOR)),
				getFieldEditorParent());
		addField(_langOptionEditor); 
		
		// Field 'File extensions'
		addField(new TextInputEditor(LocalizationPreferenceConstants.EXTENSIONS, "&File extensions:", "(separated by ;)", getFieldEditorParent()));
		
		// Field 'Regular expression'
		addField(new TextInputEditor(LocalizationPreferenceConstants.REGEX, "&Regular expression:", "(java regex)", getFieldEditorParent()));
	}
	
	protected void performDefaults()
	{
		_langOptionEditor.setItems(TranslatorManager.getInstance().getItems(TranslatorManager.getInstance().defaultTranslator));
		super.performDefaults(); 
	}
	
	public void propertyChange(PropertyChangeEvent event) 
	{
        Object src = event.getSource();
        Object val = event.getNewValue();
        
        if (src instanceof RadioGroupFieldEditor 
        		&& ((RadioGroupFieldEditor)src).getPreferenceName() == LocalizationPreferenceConstants.TRANSLATOR
        		&& _translator != val)
        {
        	_translator = (String)val;
        	_langOptionEditor.setItems(TranslatorManager.getInstance().getItems(_translator));
        	_langOptionEditor.select(0);
        }
    }
}