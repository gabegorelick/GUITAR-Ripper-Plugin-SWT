package edu.umd.cs.guitar.ripper.test.aut;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

public class SWTButtonApp {

	public SWTButtonApp(Display display) {
		Shell shell = new Shell(display);
		shell.setText("Button Window");
		shell.setSize(250, 250);

		Button button = new Button(shell, SWT.PUSH);
		button.setText("Button");
		button.setBounds(85, 110, 80, 30);

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	public static void main(String[] args) {
		Display display = new Display();
		new SWTButtonApp(display);
		display.dispose();
	}

}
