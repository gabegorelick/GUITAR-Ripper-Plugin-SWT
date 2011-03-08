package edu.umd.cs.guitar.ripper.test.aut;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

public class SWTCheckButtonApp {
	public SWTCheckButtonApp(Display display) {
		Shell shell = new Shell(display);
		shell.setText("Window");
		shell.setSize(250, 250);

		Button checkButton = new Button(shell, SWT.CHECK);
		checkButton.setText("This is a Check Button");
		checkButton.setLocation(50, 50);
		checkButton.setSelection(true);
		checkButton.pack();

		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	public static void main(String[] args) {
		Display display = new Display();
		new SWTCheckButtonApp(display);
		display.dispose();
	}
}
