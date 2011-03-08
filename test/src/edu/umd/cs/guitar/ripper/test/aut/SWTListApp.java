package edu.umd.cs.guitar.ripper.test.aut;

//Credit to http://zetcode.com/tutorials/javaswttutorial/widgets/

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class SWTListApp {
	private Shell shell;

	public SWTListApp(Display display) {
		shell = new Shell(display);
		shell.setText("Window");
		shell.setSize(250, 250);

		initUI();

		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private void initUI() {
		final Label status = new Label(shell, SWT.BORDER);
		status.setText("Ready");

		FormLayout layout = new FormLayout();
		shell.setLayout(layout);

		FormData labelData = new FormData();
		labelData.left = new FormAttachment(0);
		labelData.right = new FormAttachment(100);
		labelData.bottom = new FormAttachment(100);

		status.setLayoutData(labelData);

		final List list = new List(shell, SWT.BORDER);

		list.add("Item 1");
		list.add("Item 2");
		list.add("Item 3");
		list.add("Item 4");
		list.add("Item 5");

		list.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				String[] items = list.getSelection();
				status.setText(items[0]);
			}
		});

		FormData listData = new FormData();
		listData.left = new FormAttachment(shell, 30, SWT.LEFT);
		listData.top = new FormAttachment(shell, 30, SWT.TOP);
		list.setLayoutData(listData);
	}

	public static void main(String[] args) {
		Display display = new Display();
		new SWTListApp(display);
		display.dispose();

	}

}
