package edu.umd.cs.guitar.ripper.test.aut;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class SWTLabelApp {

	public SWTLabelApp(Display display) {
		Shell shell = new Shell();
		shell.setText("Window");
		shell.setSize(250, 250);

		Label label = new Label(shell, SWT.LEFT);
		label.setText("This is a label");
		Point p = label.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		label.setBounds(10, 10, p.x + 10, p.y + 10);

		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	public static void main(String[] args) {
		Display display = new Display();
		new SWTLabelApp(display);
		display.dispose();

	}

}
