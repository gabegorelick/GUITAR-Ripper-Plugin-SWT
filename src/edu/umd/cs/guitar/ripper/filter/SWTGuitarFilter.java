package edu.umd.cs.guitar.ripper.filter;

import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GWindow;

/**
 * Filter class for SWT componenets
 * @author Gabe Gorelick-Feldman
 *
 */
public abstract class SWTGuitarFilter extends GComponentFilter {

	protected SWTGuitarFilter() {
		// this space intentionally left blank
	}
	
	/**
	 * @deprecated Use {@link #shouldProcess(GComponent, GWindow)} instead.
	 */
	@Override
	@Deprecated
	public final boolean isProcess(GComponent component, GWindow window) {
		return shouldProcess(component, window);
	}

	/**
	 * Return whether this component should be processed by this filter.
	 * 
	 * @param component
	 * @param window
	 * @return {@code true} if component should be processed
	 */
	public abstract boolean shouldProcess(GComponent component, GWindow window);

}
