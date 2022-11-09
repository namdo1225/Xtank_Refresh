/**
 * A class that represents the entire UI and the view of the MVC.
 * 
 * Pattern: the view of the MVC.
 * 
 * @author	Nam Do
 * @version	1.0
 * @since	2022-11-12
 */

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class XTankUI {
	private static XTankUI			ui;
	private Display					display;
	private Shell					shell;
	
	private ClientModel				clientModel;
	private ClientController		clientControl;
	
	private HostModel				hostModel;
	private HostController			hostControl;
	
	private	Screen[]				screens;
	private GridData[]				gridDatas;

	private GridLayout				layout;
	
	/**
	 * A getter to get the singular XTankUI object.
	 * 
	 * @return	a XTankUI object.
	 */
	public synchronized static XTankUI get() {
		if (ui == null)
			ui = new XTankUI();
		return ui;
	}

	/**
	 * A method to start the UI and create appropriate SWT objects.
	 */
	public void start() {
		display = new Display();
		shell = new Shell(display);
		shell.setText("A3_XTank");
		
		layout = new GridLayout();
		layout.numColumns = 1;
		shell.setLayout(layout);
		shell.setBackground(null);
		
		shell.addListener(SWT.Close, new Listener() {
			@Override
			public void handleEvent(Event event) {
				try {
					clientControl.closeInput();
					clientControl.closeOutput();
					clientControl.stopRunnable();
						
				} catch (Exception e) {
					e.printStackTrace();
				}
				shell.dispose();
				display.dispose();
				System.exit(0);
			}
		});
		
		screens = new Screen[4];
		gridDatas = new GridData[4];
		
		screens[0] = new MenuScreen(shell, display, clientControl, hostControl, clientModel, hostModel);
		screens[1] = new JoinScreen(shell, display, clientControl, hostControl, clientModel, hostModel);
		screens[2] = new HostScreen(shell, display, clientControl, hostControl, clientModel, hostModel);
		screens[3] = new GameScreen(shell, display, clientControl, hostControl, clientModel, hostModel);
		
		for (int i = 0; i < gridDatas.length; i++) {
			gridDatas[i] = new GridData();
			gridDatas[i].horizontalAlignment = GridData.FILL;
			gridDatas[i].grabExcessHorizontalSpace = true;

			gridDatas[i].verticalAlignment = GridData.FILL;
			gridDatas[i].grabExcessVerticalSpace = true;
						
			if (i > 0) {
				screens[i].getComposite().setVisible(false);
				gridDatas[i].exclude = true;
			}
			
			screens[i].getComposite().setLayoutData(gridDatas[i]);
		}
		//shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
		
	}
	
	/**
	 * A method to set the client controller and model so the UI would have a
	 * reference to it.
	 * 
	 * @param model		a ClientModel for the client model of the MVC.
	 * @param control	a ClientController for the client controller of the MVC.
	 */
	public void setClientMVC(ClientModel model, ClientController control) {
		clientModel = model;
		clientControl = control;
	}
	
	/**
	 * A method to set the server controller and model so the UI would have a
	 * reference to it.
	 * 
	 * @param model		a HostModel for the server model of the MVC.
	 * @param control	a HostController for the server controller of the MVC.
	 */
	public void setHostMVC(HostModel model, HostController control) {
		hostModel = model;
		hostControl = control;
	}
	
	/**
	 * A method to update the UI to the appropriate menu screen.
	 * 
	 * @param mode		a Mode enum that will have data about what menu screen
	 * 					that will be visible.
	 */
	public void updateScreen(Mode mode) {		
		for (int i = 0; i < screens.length; i++) {
			if (i == mode.ordinal()) {
				screens[i].getComposite().setVisible(true);
				gridDatas[i].exclude = false;
				screens[i].getComposite().setLayoutData(gridDatas[i]);
			}
			else {
				screens[i].getComposite().setVisible(false);
				gridDatas[i].exclude = true;
				screens[i].getComposite().setLayoutData(gridDatas[i]);
			}
		}
		shell.layout(true, true);
	}
	
	/**
	 * A method to recreate the GameScreen object with personalized client data.
	 * 
	 * @param mapID		an int for the map's id.
	 * @param tankModel	an int for the model of the tank.
	 * @param tankID	an int for the tank's id.
	 */
	public void recreateGameScreen(int mapID, int tankModel, int tankID) {
		screens[3].getComposite().dispose();
		screens[3] = new GameScreen(shell, display, clientControl, hostControl, clientModel, hostModel,
				mapID, tankModel, tankID);
	}
}
