package edu.umd.cs.guitar.ripper.test;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.win32.POINT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class SWTLabelTest {


	public SWTLabelTest(Display display) {
		Shell shell = new Shell();
		shell.setText("Window");
		shell.setSize(250,250);
		
		Label label = new Label(shell, SWT.LEFT);
		label.setText("This is a label");
		Point p = label.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		label.setBounds(10, 10, p.x + 10, p.y +10);
		
		shell.open();
		
		while(!shell.isDisposed()){
			if(!display.readAndDispatch())
				display.sleep();
		}
	}

	public static void main(String[] args) {
		Display display = new Display();
		new SWTLabelTest(display);
		display.dispose();

	}

}
