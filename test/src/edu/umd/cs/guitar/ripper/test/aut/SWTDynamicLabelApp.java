package edu.umd.cs.guitar.ripper.test.aut;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.*;

public class SWTDynamicLabelApp {
	private int label_num = 0;

	public SWTDynamicLabelApp(Display display) {
		Shell shell = new Shell(display);
		shell.setSize(250, 250);
		shell.setText("Window");

		final Label label = new Label(shell, SWT.LEFT);
		label.setText("Change this text");
		Point p = label.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		label.setBounds(10, 10, p.x + 10, p.y + 10);

		Button changeText = new Button(shell, SWT.PUSH);
		changeText.setText("Change Text");
		changeText.setBounds(85, 110, 80, 30);
		changeText.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (label_num == 0) {
					label.setText("Text 1");
					label_num = 1;
				} else {
					label.setText("Text 0");
					label_num = 0;
				}
			}
		});
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public static void main(String[] args) {
		Display display = new Display();
		new SWTDynamicLabelApp(display);
		display.dispose();
	}

}
