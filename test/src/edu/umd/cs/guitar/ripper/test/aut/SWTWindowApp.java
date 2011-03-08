package edu.umd.cs.guitar.ripper.test.aut;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SWTWindowApp {
	public SWTWindowApp(Display display) {
		Shell shell = new Shell(display);
		shell.setText("Window");
		shell.setSize(250, 250);

		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}

		}
	}

	public static void main(String[] args) {
		Display display = new Display();
		new SWTWindowApp(display);
		display.dispose();
	}

}
