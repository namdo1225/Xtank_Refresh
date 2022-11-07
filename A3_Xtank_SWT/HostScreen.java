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

public class HostScreen extends Screen {
	private Label		guide;
	private Label		numPlayers;

	private Text		port;
	
	private Button		update;
	private Button		next;
	private Button		backServer;
	private Button		updatePlayer;
	
	private Composite	compositeNetwork;
	
	
	private Composite	compositeMap;
	private Group		mapGroup;
	
	private Button		map1;
	private Button		map2;
	
	private Button		selectMap;
	private Button		backMap;
	
	private int			mapID;
	
	public HostScreen(Shell shell, Display display, ClientController cCon, HostController hCon,
			ClientModel cMod, HostModel hMod) {
		super(shell, display, cCon, hCon, cMod, hMod);
		
		mapID = 1;
	}
	
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
				+ "\nYou cannot click 'Next' to start the game "
				+ "unless 2 or more players are in.\nYour input is validated. "
				+ "Program will crash if your number is out of int's bounds.\nYou cannot host AND play the game."
				+ "\n\nWell-known port range: 1024-65,535\tLocalhost: 127.0.0.1\nYour (probably wrong) IP: "
				+ address
				+ "\n\nPick a map FIRST! You cannot change your selection once it is chosen:");
		guide.setFont(new Font(display,"Times New Roman", 12, SWT.BOLD ));
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
		
		selectMap = new Button(compositeMap, SWT.PUSH);
		selectMap.setText("Select Map");
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
		
		next = new Button(compositeNetwork, SWT.PUSH);
		next.setText("Next");
		next.addSelectionListener(SelectionListener.widgetSelectedAdapter(e-> validateInput()));
		
		backServer = new Button(compositeNetwork, SWT.PUSH);
		backServer.setText("Back");
		backServer.addSelectionListener(
				SelectionListener.widgetSelectedAdapter(e-> closeServer()));
		
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		return composite;
	}

	private void mapSelected() {
		compositeNetwork.setEnabled(true);
		compositeMap.setEnabled(false);
		selectMap.setText("YOU CAN NOW CLICK ON THE SCREEN TO THE RIGHT.");
	}
	
	private void hostServer() {
		if (validateInput())
			try {
				hControl.createServer(Integer.parseInt(port.getText()), mapID);
			} catch (Exception e) {}
		else {
			port.setMessage("Enter port number: Your input was invalid.");
			port.setText("");
		}
	}
		
	private boolean validateInput() {		
		if (port.getText().isBlank())
			return false;
		
		int portNum = Integer.parseInt(port.getText());
		if (portNum < 0 || portNum > 65535)
			return false;
		return true;
	}

	private void updatePlCount() {
		numPlayers.setText("Number of players:\t" + hModel.getPlayer());
	}

	private void closeServer() {
		hControl.updateScreen(Mode.MAIN);
		hControl.closeServer();
		compositeNetwork.setEnabled(false);
		compositeMap.setEnabled(true);
		selectMap.setText("Select Map");
	}
}
