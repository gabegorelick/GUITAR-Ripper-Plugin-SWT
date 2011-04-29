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
public class SWTIgnoreWidgetFilter extends SWTGuitarFilter {

	private final List<FullComponentType> ignoredComponents;
	
	
	public SWTIgnoreWidgetFilter(List<FullComponentType> ignoredComponents) {
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

	@Override
	public ComponentType ripComponent(GComponent component, GWindow window) {
		return null;
	}

}
