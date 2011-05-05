/*	
 *  Copyright (c) 2011-@year@. The GUITAR group at the University of Maryland. Names of owners of this group may
 *  be obtained by sending an e-mail to atif@cs.umd.edu
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
 *  documentation files (the "Software"), to deal in the Software without restriction, including without 
 *  limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *	the Software, and to permit persons to whom the Software is furnished to do so, subject to the following 
 *	conditions:
 * 
 *	The above copyright notice and this permission notice shall be included in all copies or substantial 
 *	portions of the Software.
 *
 *	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT 
 *	LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO 
 *	EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *	IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *	THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */
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
 * Args4j {@code OptionHandler} to parse arrays of URLs.
 * 
 * @author Gabe Gorelick
 *
 */
public class URLArrayOptionHandler extends OptionHandler<URL[]> {

	/**
	 * Constructor used by Args4j.
	 * @param parser the command line parser
	 * @param option the option to handle
	 * @param setter value setter
	 */
	public URLArrayOptionHandler(CmdLineParser parser, OptionDef option,
			Setter<? super URL[]> setter) {
		
		super(parser, option, setter);
	}

	/**
	 * {@inheritDoc}
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
