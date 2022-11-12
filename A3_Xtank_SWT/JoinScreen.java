/**
 * A class to represent the UI portion of the screen to
 * that allows the player to join a server.
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class JoinScreen extends Screen {
	private Composite	compositeTank;

	private Label		titleTank;
	
	private Group		tankGroup;
	private Button		tank1;
	private Button		tank2;
	
	private Button		backTank;
	private Button		selectTank;
	
	private Composite	compositeJoin;

	private Label		titleJoin;
	private Label		error;
	
	private Text		iP;
	private Text		port;
	
	private Button		next;
	private Button		backJoin;
	
	private int			tankModel;
	
	/**
	 * A constructor for JoinScreen.
	 * 
	 * @param shell		a Shell from SWT to help use the graphic library.
	 * @param display	a Display from SWT to help use the graphic library.
	 * @param cCon		a ClientController for the controller of the client.
	 * @param hCon		a HostController for the controller of the host.
	 * @param cMod		a ClientModel for the model of the client.
	 * @param hMod		a HostModel for the model of the host.
	 */
	public JoinScreen(Shell shell, Display display, ClientController cCon, HostController hCon,
			ClientModel cMod, HostModel hMod) {
		super(shell, display, cCon, hCon, cMod, hMod);
		tankModel = 1;
	}
	
	/**
	 * A method to make the entire UI for this screen.
	 * 
	 * @see Screen.java
	 * 
	 * @param shell		a Shell from SWT to help use the graphic library.
	 * @param display	a Display from SWT to help use the graphic library.
	 */
	protected Composite makeComposite(Shell shell, Display display) {
		composite = new Composite(shell, SWT.COLOR_WHITE);
		
		compositeTank = new Composite(composite, SWT.TRANSPARENT);
		compositeJoin = new Composite(composite, SWT.TRANSPARENT);

		compositeTank.setLayout(new FillLayout(SWT.VERTICAL));
		compositeJoin.setLayout(new FillLayout(SWT.VERTICAL));
		compositeJoin.setEnabled(false);
		
		String address = "";
		try {
			address = InetAddress.getLocalHost().toString();
		} catch (UnknownHostException e1) {}
		
		titleTank = new Label(compositeTank, SWT.BALLOON);
		titleTank.setText("Set your IP address and port number and click 'Next'. \n'UAWiFi' "
				+ "does not work consistently for us. Your inputs are validated.\nFIRST, select "
				+ "your tank model. You cannot change your selection after clicking 'Continue'.");
		titleTank.setFont(new Font(display,"Times New Roman", 12, SWT.BOLD ));
		titleTank.setAlignment(SWT.CENTER);
		
		tankGroup = new Group(compositeTank, SWT.NONE);
		tankGroup.setLayout(new FillLayout(SWT.VERTICAL));
		
		tank1 = new Button(tankGroup, SWT.RADIO);
		tank1.setText("Tank 1, Weapon: Normal, Armor: Normal (3 pts)");
		tank1.setSelection(true);
		tank1.addSelectionListener(
				SelectionListener.widgetSelectedAdapter(e-> tankModel = 1));
		
		tank2 = new Button(tankGroup, SWT.RADIO);
		tank2.setText("Tank 2, Weapon: Fast, Armor: Weak (2 pts)");
		tank2.addSelectionListener(
				SelectionListener.widgetSelectedAdapter(e-> tankModel = 2));
		
		selectTank = new Button(compositeTank, SWT.PUSH);
		selectTank.setText("Continue");
		selectTank.addSelectionListener(
				SelectionListener.widgetSelectedAdapter(e-> tankSelected()));
		
		backTank = new Button(compositeTank, SWT.PUSH);
		backTank.setText("Back");
		backTank.addSelectionListener(
				SelectionListener.widgetSelectedAdapter(e-> goBack()));
		
		titleJoin = new Label(compositeJoin, SWT.BALLOON);
		titleJoin.setText("You will crash the program if the numbers entered are out of int's bounds.\n"
				+ "Well-known port range: 1024-65,535\tLocalhost: 127.0.0.1\tYour (probably wrong) IP: "
				+ address);
		titleJoin.setFont(new Font(display,"Times New Roman", 12, SWT.BOLD ));
		titleJoin.setAlignment(SWT.CENTER);
		
		error = new Label(compositeJoin, SWT.BALLOON);
		error.setFont(new Font(display, "Times New Roman", 12, SWT.BOLD ));
		error.setAlignment(SWT.CENTER);
		error.setForeground(display.getSystemColor(SWT.COLOR_RED));
		
		iP = new Text(compositeJoin, SWT.LEFT | SWT.BORDER);
		iP.setMessage("Enter IPv4 address:");
		iP.setText("127.0.0.1");
		
		port = new Text(compositeJoin, SWT.LEFT | SWT.BORDER);
		port.setMessage("Enter port number:");
		port.setText("8080");
		
		next = new Button(compositeJoin, SWT.PUSH);
		next.setText("Next");
		next.addSelectionListener(
				SelectionListener.widgetSelectedAdapter(e-> joinServer()));
		
		backJoin = new Button(compositeJoin, SWT.PUSH);
		backJoin.setText("Back");
		backJoin.addSelectionListener(
				SelectionListener.widgetSelectedAdapter(e-> goBack()));
		
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		return composite;
	}
	
	/**
	 * A method to go back to the previous screen and update the UI.
	 */
	private void goBack() {
		compositeJoin.setEnabled(false);
		compositeTank.setEnabled(true);
		selectTank.setText("Continue");
		error.setText("");
		cControl.updateScreen(Mode.MAIN);
	}
	
	/**
	 * A method to confirm the player's selection of the tank model.
	 */
	private void tankSelected() {
		compositeJoin.setEnabled(true);
		compositeTank.setEnabled(false);
		selectTank.setText("You can now click on the screen to the right.");
		cControl.setTankModel(tankModel);
	}
	
	/**
	 * A method to have the controller join to the server and update the UI.
	 */
	private void joinServer() {
		if (validateInput()) {
			try {
				cControl.createSocket(iP.getText(), Integer.parseInt(port.getText()));
				if (cModel.getSocket() != null && cModel.getSocket().isConnected())
					cControl.updateScreen(Mode.GAME);
				else
					error.setText("The server did not respond.");
			} catch (Exception e) {}
			
			compositeJoin.setEnabled(false);
			compositeTank.setEnabled(true);
			selectTank.setText("Continue");
		}
		else {
			iP.setMessage("Enter IPv4 address: One of your input was invalid.");
			iP.setText("");
			
			port.setMessage("Enter port number: One of your input was invalid.");
			port.setText("");
			error.setText("");
		}
	}
	
	/**
	 * A method to validate the user's inputs, specifically the IP address
	 * and port number.
	 * 
	 * @return	a boolean. true if IP address and port number are valid.
	 * 			false if not.
	 */
	private boolean validateInput() {
		String[] fourHex = iP.getText().split("\\.");
		
		if (fourHex.length != 4)
			return false;
		
		for (int i = 0; i < 4; i++) {
			int partIP = Integer.parseInt(fourHex[i]);
			if (partIP < 0 || partIP > 255)
				return false;
		}
		
		int portNum = Integer.parseInt(port.getText());
		if (portNum < 0 || portNum > 65535)
			return false;
		return true;
	}
}
