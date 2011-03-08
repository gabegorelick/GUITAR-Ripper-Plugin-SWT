package edu.umd.cs.guitar.ripper;

import org.kohsuke.args4j.CmdLineException;

public class SWTRipperRunner {

	public static void main(String[] args) {
		SWTRipperConfiguration config = new SWTRipperConfiguration();
		config.setMainClass("edu.umd.cs.guitar.ripper.test.aut.SWTHelloWorld");
		
		final SWTRipper swtRipper = new SWTRipper(config);

		try {
			swtRipper.execute();
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			System.err.println();
			System.err.println("Usage: java [JVM options] "
					+ SWTRipperMain.class.getName() + " [Ripper options] \n");
			System.err.println("where [Ripper options] include:");
			System.err.println();
		}
	}
}
