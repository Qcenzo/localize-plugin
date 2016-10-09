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

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class DoubleCombosEditor extends FieldEditor
{
	private Combo _comboFrom;
	private Combo _comboTo; 
	
	public DoubleCombosEditor(String name, String labelText, String[] items, Composite parent)
	{
		init(name, labelText);
        createControl(parent);
        setItems(items);
	}
	
	public void setItems(String[] items)
	{
		_comboFrom.setItems(items);
		_comboTo.setItems(items); 
	}
	
	public void select(int index)
	{
		_comboFrom.select(index >> 8 & 0xFF); 
		_comboTo.select(index & 0xFF); 
	}
	
	@Override
	protected void adjustForNumColumns(int numColumns) {}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns)
	{
		// Label 'title' 
		GridData gd = new GridData();
		gd.horizontalSpan = numColumns;
		getLabelControl(parent).setLayoutData(gd);
        
		// Combo 'from'
		gd = new GridData();
		gd.horizontalSpan = 1;
        _comboFrom = new Combo(parent, SWT.READ_ONLY);
        _comboFrom.setLayoutData(gd);
        
        // Label 'arrow' 
        gd = new GridData();
        gd.horizontalSpan = 1;
        Label arrow = new Label(parent, SWT.NONE);
        arrow.setText("->");
        arrow.setLayoutData(gd); 
        
        // Combo 'to'
        gd = new GridData();
        gd.horizontalSpan = 1;
        _comboTo = new Combo(parent, SWT.READ_ONLY);
        _comboTo.setLayoutData(gd);
	}

	@Override
	protected void doLoad()
	{
		select(getPreferenceStore().getInt(getPreferenceName()));
	}

	@Override
	protected void doLoadDefault()
	{
		select(getPreferenceStore().getDefaultInt(getPreferenceName()));
	}

	@Override
	protected void doStore()
	{
		getPreferenceStore().setValue(getPreferenceName(), (_comboFrom.getSelectionIndex() << 8) + _comboTo.getSelectionIndex());
	}

	@Override
	public int getNumberOfControls()
	{
		return 3;
	}
}
