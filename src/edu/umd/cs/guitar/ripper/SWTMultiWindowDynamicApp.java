package edu.umd.cs.guitar.ripper;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.*;

public class SWTMultiWindowDynamicApp {
	private ArrayList<Shell> shellList = new ArrayList<Shell>();
	private Display globalDisplay = null;

	public SWTMultiWindowDynamicApp(Display display) {
		final Shell shell = new Shell(display);
		shell.setSize(250, 250);
		shell.setText("Window");
		shellList.add(shell);
		globalDisplay = display;

		Button addWindow = new Button(shell, SWT.PUSH);
		addWindow.setText("Add Window");
		addWindow.setBounds(85, 110, 80, 30);
		
		addWindow.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Shell tempShell = new Shell(globalDisplay);
				tempShell.setSize(250, 250);
				tempShell.setText("Window " + shellList.size());
				shellList.add(tempShell);
				tempShell.open();
			}
		});
		
		Button changeColor = new Button(shell, SWT.PUSH);
		changeColor.setBounds(20, 20, 80, 30);
		changeColor.setText("Change Color");
		
		changeColor.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.setBackground(new Color(globalDisplay, 255, 0, 0));
			}
		});
		
		shell.open();
		for (int i = 0; i < shellList.size(); i++) {
			Shell currentShell = shellList.get(i);
			while (!currentShell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		}

	}

	public static void main(String[] args) {
		Display display = new Display();
		new SWTMultiWindowDynamicApp(display);
		display.dispose();
	}

}
