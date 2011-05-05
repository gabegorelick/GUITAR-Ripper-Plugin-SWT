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
package edu.umd.cs.guitar.ripper.filter;

import java.util.List;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GWindow;
import edu.umd.cs.guitar.model.data.ComponentType;
import edu.umd.cs.guitar.model.data.FullComponentType;
import edu.umd.cs.guitar.model.wrapper.AttributesTypeWrapper;

/**
 * A filter to ignore a widget. Adapted from {@code JFCIgnoreSignExpandFilter}.
 * 
 * @author Gabe Gorelick
 *
 */
public class SitarIgnoreWidgetFilter extends SitarFilter {

	private final List<FullComponentType> ignoredComponents;

	/**
	 * Sole constructor.
	 * 
	 * @param ignoredComponents
	 *            list of ignored components
	 */
	public SitarIgnoreWidgetFilter(List<FullComponentType> ignoredComponents) {
		super();
		this.ignoredComponents = ignoredComponents;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean shouldProcess(GComponent component, GWindow window) {
		ComponentType dComponent = component.extractProperties();
        ComponentType dWindow = window.extractWindow().getWindow();

        AttributesTypeWrapper compAttributesAdapter = new AttributesTypeWrapper(
                dComponent.getAttributes());
        AttributesTypeWrapper winAttributesAdapter = new AttributesTypeWrapper(
                dWindow.getAttributes());

        for (FullComponentType sign : ignoredComponents) {
        	ComponentType signComp = sign.getComponent();
        	ComponentType signWin = sign.getWindow();

            AttributesTypeWrapper dCompSignAttributes = new AttributesTypeWrapper(
                    signComp.getAttributes());

            if (signWin != null) {
                AttributesTypeWrapper signWinAttributes = new AttributesTypeWrapper(
                        signWin.getAttributes());

                if (!winAttributesAdapter.containsAll(signWinAttributes)) {
                    continue;
                }
            }

            if (compAttributesAdapter.containsAll(dCompSignAttributes)) {
                return true;
            }
        }
        
        return false;

	}

	/**
	 * Rips the given component. Since this filter is for ignored components,
	 * this method simply returns {@code null}.
	 * 
	 * @param component
	 *            the component to rip
	 * @param window
	 *            the window this component live in
	 * 
	 * @return {@code null}
	 */
	@Override
	public ComponentType ripComponent(GComponent component, GWindow window) {
		return null;
	}

}
