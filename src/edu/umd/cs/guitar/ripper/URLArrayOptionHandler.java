package edu.umd.cs.guitar.ripper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;



/**
 * A handler for options in url form. It will parse arguments for GUITAR.
 * 
 * @author Gabe Gorelick-Feldman
 */
public class URLArrayOptionHandler extends OptionHandler<URL[]> {

	public URLArrayOptionHandler(CmdLineParser parser, OptionDef option,
			Setter<? super URL[]> setter) {
		
		super(parser, option, setter);
	}

	/**
	 * parse the arguments and set the values for the ripper.
	 * 
	 * @param params
	 * 		parameters to set
	 * 
	 * @return	
	 * 		number of parameters set
	 */
	@Override
	public int parseArguments(Parameters params) throws CmdLineException {
		int counter = 0;
 
		ArrayList<URL> values = new ArrayList<URL>();
		while (true) {
			String param;
			try {
				param = params.getParameter(counter);
			} catch (CmdLineException ex) {
				break;
			}
			if (param.startsWith("-")) {
				break;
			}

			for (String str : param.split(" ")) {
				try {
					values.add(new URL(str));
				} catch (MalformedURLException e) {
					throw new CmdLineException("Illegal URL value: " + str);
				}
			}
			counter++;
		}
        this.setter.addValue(values.toArray(new URL[values.size()]));
		return counter;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return "URL[]"
	 */
	@Override
	public String getDefaultMetaVariable() {
		return "URL[]";
	}

}
