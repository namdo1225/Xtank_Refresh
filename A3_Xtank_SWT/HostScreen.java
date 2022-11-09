/**
 * A class to represent the UI portion of a screen to host a server.
 *
 * For the parent class, @see Screen.java
 * 
 * @author	Nam Do
 * @version	1.0
 * @since	2022-11-12
 */

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;

public class HostScreen extends Screen {
	private Label		guide;
	private Label		numPlayers;

	private Text		port;
	
	private Button		update;
	private Button		backServer;
	private Button		updatePlayer;
	
	private Composite	compositeNetwork;
	
	
	private Composite	compositeMap;
	private Group		mapGroup;
	
	private Button		map1;
	private Button		map2;
	
	private Button		selectMap;
	private Button		backMap;
	
	private Label		livesText;
	private Slider		selectLives;
	
	private int			mapID;
	private int			maxLives;
	
	/**
	 * A constructor for HostScreen.
	 * 
	 * @param shell		a Shell from SWT to help use the graphic library.
	 * @param display	a Display from SWT to help use the graphic library.
	 * @param cCon		a ClientController for the controller of the client.
	 * @param hCon		a HostController for the controller of the host.
	 * @param cMod		a ClientModel for the model of the client.
	 * @param hMod		a HostModel for the model of the host.
	 */
	public HostScreen(Shell shell, Display display, ClientController cCon, HostController hCon,
			ClientModel cMod, HostModel hMod) {
		super(shell, display, cCon, hCon, cMod, hMod);
		
		mapID = 1;
	}
	
	/**
	 * A method to make the entire UI for this screen.
	 * 
	 * @see Screen.java
	 * 
	 * @param shell		a Shell from SWT to help use the graphic library.
	 * @param display	a Display from SWT to help use the graphic library.
	 */
	@Override
	protected Composite makeComposite(Shell shell, Display display) {
		composite = new Composite(shell, SWT.COLOR_WHITE);
		
		compositeMap = new Composite(composite, SWT.TRANSPARENT);
		compositeNetwork = new Composite(composite, SWT.TRANSPARENT);
		
		compositeMap.setLayout(new FillLayout(SWT.VERTICAL));
		compositeNetwork.setLayout(new FillLayout(SWT.VERTICAL));
		
		String address = "";
		try {
			address = InetAddress.getLocalHost().toString();
		} catch (UnknownHostException e1) {}
		
		guide = new Label(compositeMap, SWT.BALLOON);
		guide.setText("'Update' create new server socket with the typed-in port number."
				+ "\nProgram will crash if your number is out of int's bounds."
				+ "\nWell-known port range: 1024-65,535\tLocalhost: 127.0.0.1\nYour (probably wrong) IP: "
				+ address
				+ "\nPick maps and lives FIRST! You cannot change your selection once it is chosen:");
		guide.setFont(new Font(display,"Times New Roman", 8, SWT.BOLD ));
		guide.setAlignment(SWT.CENTER);
		
		mapGroup = new Group(compositeMap, SWT.NONE);
		mapGroup.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		map1 = new Button(mapGroup, SWT.RADIO);
		map1.setText("Map 1");
		map1.setSelection(true);
		map1.addSelectionListener(
				SelectionListener.widgetSelectedAdapter(e-> mapID = 1));
		
		map2 = new Button(mapGroup, SWT.RADIO);
		map2.setText("Map 2");
		map1.addSelectionListener(
				SelectionListener.widgetSelectedAdapter(e-> mapID = 2));

		livesText = new Label(compositeMap, SWT.BALLOON);
		livesText.setFont(new Font(display,"Times New Roman", 14, SWT.BOLD ));
		livesText.setText("Lives: 1");
		
		selectLives = new Slider(compositeMap, SWT.HORIZONTAL);
		selectLives.setMinimum(10);
		selectLives.setMaximum(60);
		selectLives.setIncrement(10); 
		selectLives.setSelection(10);
		selectLives.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event e) {
            	maxLives = selectLives.getSelection() / 10;
            	livesText.setText("Lives: " + maxLives);
            }
        });		
		selectMap = new Button(compositeMap, SWT.PUSH);
		selectMap.setText("Continue");
		selectMap.addSelectionListener(
				SelectionListener.widgetSelectedAdapter(e-> mapSelected()));
		
		backMap = new Button(compositeMap, SWT.PUSH);
		backMap.setText("Back");
		backMap.addSelectionListener(
				SelectionListener.widgetSelectedAdapter(e-> closeServer()));
		
		compositeNetwork.setEnabled(false);
		
		numPlayers = new Label(compositeNetwork, SWT.BALLOON);
		numPlayers.setFont(new Font(display,"Times New Roman", 14, SWT.BOLD ));
		numPlayers.setText("Number of players:\t0");
		
		updatePlayer = new Button(compositeNetwork, SWT.PUSH);
		updatePlayer.setText("Update # of players");
		updatePlayer.addSelectionListener(
				SelectionListener.widgetSelectedAdapter(e-> updatePlCount()));
		
		port = new Text(compositeNetwork, SWT.LEFT | SWT.BORDER);
		port.setMessage("Enter port number:");
		port.setText("8080");
		
		update = new Button(compositeNetwork, SWT.PUSH);
		update.setText("Update");
		update.addSelectionListener(
				SelectionListener.widgetSelectedAdapter(e-> hostServer()));
		
		backServer = new Button(compositeNetwork, SWT.PUSH);
		backServer.setText("Back");
		backServer.addSelectionListener(
				SelectionListener.widgetSelectedAdapter(e-> closeServer()));
		
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		return composite;
	}

	/**
	 * A method to set the UI so that the map selection is confirmed.
	 */
	private void mapSelected() {
		compositeNetwork.setEnabled(true);
		compositeMap.setEnabled(false);
		selectMap.setText("You can now click on the screen to the right.");
	}
	
	/**
	 * A method to call the server controller to start creating a server based on
	 * user input from the UI.
	 */
	private void hostServer() {
		if (validateInput()) {
			try {
				hControl.createServer(Integer.parseInt(port.getText()), mapID, maxLives);
			} catch (Exception e) {}
		
			update.setEnabled(false);
		}
		else {
			port.setMessage("Enter port number: Your input was invalid.");
			port.setText("");
		}
	}
	
	/**
	 * A method to validate the user input, specifically the port number
	 * given.
	 * 
	 * @return	a boolean. true if input is successfully validated. false
	 * 			if input is rejected.
	 */
	private boolean validateInput() {		
		if (port.getText().isBlank())
			return false;
		
		int portNum = Integer.parseInt(port.getText());
		if (portNum < 0 || portNum > 65535)
			return false;
		return true;
	}

	/**
	 * A method to update the number of players on the UI.
	 */
	private void updatePlCount() {
		numPlayers.setText("Number of players:\t" + hModel.getPlayer());
	}
	
	/**
	 * A method to to call the controller to close the server and
	 * return to the title screen.
	 */
	private void closeServer() {
		hControl.updateScreen(Mode.MAIN);
		hControl.closeServer();
		compositeNetwork.setEnabled(false);
		compositeMap.setEnabled(true);
		selectMap.setText("Continue");
		update.setEnabled(true);
	}
}
