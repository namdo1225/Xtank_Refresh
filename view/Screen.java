/**
 * A parent, abstract class to provide to set the template
 * for derived classes that would form the UI of the game.
 * 
 * @author	Nam Do
 * @version	1.0
 * @since	2022-11-12
 */
package view;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import controller.ClientController;
import controller.HostController;
import model.ClientModel;
import model.HostModel;

public abstract class Screen {
	protected Composite			composite;
	
	protected ClientModel		cModel;
	protected ClientController 	cControl;
	
	protected HostModel			hModel;
	protected HostController	hControl;
	
	/**
	 * First constructor for Screen.
	 * 
	 * @param shell		a Shell from SWT to help use the graphic library.
	 * @param display	a Display from SWT to help use the graphic library.
	 * @param cCon		a ClientController for the controller of the client.
	 * @param hCon		a HostController for the controller of the host.
	 * @param cMod		a ClientModel for the model of the client.
	 * @param hMod		a HostModel for the model of the host.
	 */
	public Screen(Shell shell, Display display, ClientController cCon, HostController hCon,
			ClientModel cMod, HostModel hMod) {
		composite = makeComposite(shell, display);
		cControl = cCon;
		hControl = hCon;
		
		cModel = cMod;
		hModel = hMod;
	}
	
	/**
	 * Second constructor for Screen. Specifically for the client.
	 * 
	 * @param shell		a Shell from SWT to help use the graphic library.
	 * @param display	a Display from SWT to help use the graphic library.
	 * @param cCon		a ClientController for the controller of the client.
	 * @param hCon		a HostController for the controller of the host.
	 * @param cMod		a ClientModel for the model of the client.
	 * @param hMod		a HostModel for the model of the host.
	 * @param mapID		an int for the map's id.
	 * @param tankModel	an int for the model of the tank.
	 * @param tankID	an int for the tank's id.
	 */
	public Screen(Shell shell, Display display, ClientController cCon, HostController hCon,
			ClientModel cMod, HostModel hMod, int mapID, int tankModel, int tankID) {
		composite = makeCompositeAndMap(shell, display, mapID, tankModel, tankID);
		cControl = cCon;
		hControl = hCon;
		
		cModel = cMod;
		hModel = hMod;
	}
	
	/**
	 * An abstract method to have derived classes make the entire UI for 
	 * this screen.
	 *
	 * @param shell		a Shell from SWT to help use the graphic library.
	 * @param display	a Display from SWT to help use the graphic library.
	 */
	protected abstract Composite makeComposite(Shell shell, Display display);
	
	/**
	 * A method that can be overriden to make the entire UI for this screen 
	 * with data based on client and server. 
	 * 
	 * @param shell		a Shell from SWT to help use the graphic library.
	 * @param display	a Display from SWT to help use the graphic library.
	 * @param mapID		an int for the map's id.
	 * @param tankModel	an int for the model of the tank.
	 * @param tankID	an int for the tank's id.
	 */
	protected Composite makeCompositeAndMap(Shell shell, Display display, 
			int mapID, int tankModel, int tankID) {
		return null;
	}
	
	/**
	 * A getter for the Composite object that represents the entire UI
	 * components for a specific menu screen.
	 * 
	 * @return	a Composite object.
	 */
	public Composite getComposite() {
		return composite;
	}
}
