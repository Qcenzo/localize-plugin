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

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class TextInputEditor extends StringFieldEditor
{
	public TextInputEditor(String name, String labelText, String comments, Composite parent) 
	{
		super(name, labelText, parent);
		if (comments != null)
			createCommentLabel(comments, parent); 
    }

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns)
	{
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		getLabelControl(parent).setLayoutData(gd); 

		gd = new GridData();
		gd.widthHint = 320;
		gd.horizontalSpan = 1;
        getTextControl(parent).setLayoutData(gd);
	}

	@Override
	public int getNumberOfControls()
	{
		return 2;
	}

	private void createCommentLabel(String comments, Composite parent)
	{
		GridData gd = new GridData();
		gd.horizontalSpan = 1;
		Label label = new Label(parent, SWT.NONE);
		label.setText(comments);
		label.setLayoutData(gd);
	}
}
