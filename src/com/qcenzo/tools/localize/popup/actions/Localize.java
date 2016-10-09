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
package com.qcenzo.tools.localize.popup.actions;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

import com.qcenzo.tools.localize.Activator;
import com.qcenzo.tools.localize.TranslatorManager;
import com.qcenzo.tools.localize.popup.actions.data.Node;
import com.qcenzo.tools.localize.preferences.LocalizationPreferenceConstants;

public class Localize implements IObjectActionDelegate 
{
	private Shell _shell;
	private IResource _selected;
	private Node _node;
	private Node _nodePicker;
	private Pattern _extensionsPattern;
	private Pattern _translatorPattern;

	public Localize() 
	{
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) 
	{
		_shell = targetPart.getSite().getShell();
	}

	public void run(IAction action)  
	{
		try 
		{
			if (_selected == null)
			{
				MessageDialog.openInformation(_shell, "Error", "Fail to localize.");
				return;
			}
			
			IPreferenceStore store = Activator.getDefault().getPreferenceStore();
			
			_extensionsPattern = Pattern.compile(store.getString(LocalizationPreferenceConstants.EXTENSIONS).replaceAll("[\\s" +
					"|¡¡]+", "").replaceAll(";+", "|").toLowerCase());
			
			_node = _nodePicker = new Node(); 
			findFile(Paths.get(_selected.getLocation().toOSString()));
			
			_translatorPattern = Pattern.compile(refine(store.getString(LocalizationPreferenceConstants.REGEX).getBytes()));
			 
			TranslatorManager.getInstance().init(store.getString(LocalizationPreferenceConstants.TRANSLATOR),
					store.getInt(LocalizationPreferenceConstants.FROMTO));

			new ProgressMonitorDialog(_shell).run(true, true, new IRunnableWithProgress()
			{
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException,
						InterruptedException
				{
					try
					{
						int current = 1;
						monitor.beginTask("Localizing...(" +  current + "/" + Node.number + ")", IProgressMonitor.UNKNOWN);
						
						while (_node.path != null)
						{
							if (monitor.isCanceled())
								monitor.done();

							monitor.setTaskName("Localizing...(" +  (current++) + "/" + Node.number + ")");
							monitor.subTask(_node.path.toString());
							
							translate(_node.path);
							
							_node = _node.next;
						}
						monitor.done();
					}
					catch (Throwable e) 
					{
						monitor.done();
					}
				}
			});	
			
			Node.number = 0;

			IHandlerService s = (IHandlerService)PlatformUI.getWorkbench().getService(IHandlerService.class);
			s.executeCommand("org.eclipse.ui.file.refresh", null); 
		}
		catch (Throwable e) {}
	}

	public void selectionChanged(IAction action, ISelection selection) 
	{
		try
		{
			IAdaptable adaptable = null;
            if(selection instanceof IStructuredSelection)
            {
                adaptable = (IAdaptable)((IStructuredSelection)selection).getFirstElement();
                if(adaptable instanceof IResource)
                    _selected = (IResource)adaptable;
                else
                    _selected = (IResource)adaptable.getAdapter(org.eclipse.core.resources.IResource.class);
            }
		}
		catch (Throwable e) {}
	}
	
	private void findFile(Path path) throws Exception
	{
		if (Files.isDirectory(path))
		{
			DirectoryStream<Path> paths = Files.newDirectoryStream(path);
			for (Path p: paths)
				findFile(p);
		}
		else
		{
			String name = path.getFileName().toString();
			name = name.substring(name.lastIndexOf(".") + 1).toLowerCase();
			if (_extensionsPattern.matcher(name).matches())
			{
				Node.number++;

				_nodePicker.path = path;
				_nodePicker.next = new Node();
				_nodePicker = _nodePicker.next;
			}
		}
	}

	private String refine(byte[] bytes)
	{
		String regex = "";
		int i, j, n;
		
		for (i = 0, j = 1, n = bytes.length - 1; i < n; i++, j++)
		{
			if (bytes[i] == '\\' && bytes[j] == '\\')
				continue;
			else if (bytes[i] == '\\' && bytes[j] == 'r')
			{
				bytes[j] = '\r';
				continue;
			}
			else if (bytes[i] == '\\' && bytes[j] == 'n')
			{
				bytes[j] = '\n';
				continue;
			}
			regex += (char)bytes[i];
		}
		regex += (char)bytes[i];
		
		return regex;  
	}
	
	private void translate(Path file) throws Throwable
	{
		String input = new String(Files.readAllBytes(file));
		Matcher matcher = _translatorPattern.matcher(input);
		if (matcher.groupCount() == 0)
			return;
		
		StringBuffer output = null;
		String group = null;
		int i, n;
		
		while (matcher.find())
		{
			if (output == null)
				output = new StringBuffer();
			
			for (i = 1, n = matcher.groupCount(); i <= n; i++)
			{
				group = matcher.group(i);
				if (group != null)
				{
					matcher.appendReplacement(output, 
							input.substring(matcher.start(), matcher.end()).replace(group, 
									TranslatorManager.getInstance().execute(group)));
				}
			}
		}
		matcher.appendTail(output);
		
		if (output != null)
			Files.write(file, output.toString().getBytes());
	} 
}
