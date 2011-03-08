package edu.umd.cs.guitar.ripper.test.aut;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

public class SWTMenuBarApp {
	public SWTMenuBarApp(Display display) {
		Shell shell = new Shell(display);
		shell.setSize(250, 250);
		shell.setText("Window");

		Menu menuBar = new Menu(shell, SWT.BAR);
		MenuItem cascadeMenu = new MenuItem(menuBar, SWT.CASCADE);
		cascadeMenu.setText("&File");

		Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
		cascadeMenu.setMenu(fileMenu);

		MenuItem exitButton = new MenuItem(fileMenu, SWT.PUSH);
		exitButton.setText("&Exit");
		shell.setMenuBar(menuBar);

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public static void main(String[] args) {
		Display display = new Display();
		new SWTMenuBarApp(display);
		display.dispose();

	}

}
