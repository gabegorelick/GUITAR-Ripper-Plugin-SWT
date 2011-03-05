package edu.umd.cs.guitar.ripper.test;

import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SWTWindowTest {
	public SWTWindowTest (Display display){
		Shell shell = new Shell(display);
		shell.setText("Window");
		shell.setSize(250, 250);
		
		shell.open();
		
		while(!shell.isDisposed()){
			if(!display.readAndDispatch()){
				display.sleep();
			}
			
		}
	}
	
	public static void main(String[] args){
		Display display = new Display();
		new SWTWindowTest(display);
		display.dispose();
	}
	
}
