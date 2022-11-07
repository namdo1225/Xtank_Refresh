import java.net.InetAddress;
import java.net.UnknownHostException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class HostScreen extends Screen {
	private Label		guide;
	private Label		numPlayers;

	private Text		port;
	
	private Button		update;
	private Button		next;
	private Button		back;
	private Button		updatePlayer;
	
	private Composite	compositeNetwork;
	
	
	private Composite	compositeMap;
	
	
	public HostScreen(Shell shell, Display display, ClientController cCon, HostController hCon,
			ClientModel cMod, HostModel hMod) {
		super(shell, display, cCon, hCon, cMod, hMod);
	}
	
	@Override
	protected Composite makeComposite(Shell shell, Display display) {
		composite = new Composite(shell, SWT.COLOR_WHITE);
		
		compositeNetwork = new Composite(composite, SWT.TRANSPARENT);
		compositeMap = new Composite(composite, SWT.TRANSPARENT);
		
		compositeNetwork.setLayout(new FillLayout(SWT.VERTICAL));
		compositeMap.setLayout(new FillLayout(SWT.VERTICAL));
		
		String address = "";
		try {
			address = InetAddress.getLocalHost().toString();
		} catch (UnknownHostException e1) {}
		
		guide = new Label(compositeMap, SWT.BALLOON);
		guide.setText("'Update' create new server socket with the typed-in port number."
				+ "\nYou cannot click 'Next' to start the game "
				+ "unless 2 or more players are in.\nYour input is validated. "
				+ "Program will crash if your number is out of int's bounds.\nYou cannot host AND play the game."
				+ "\nWell-known port range: 1024-65,535\tLocalhost: 127.0.0.1\nYour (probably wrong) IP: "
				+ address
				+ "\nPick a map:");
		guide.setFont(new Font(display,"Times New Roman", 12, SWT.BOLD ));
		guide.setAlignment(SWT.CENTER);
		
		
		
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
		
		back = new Button(compositeNetwork, SWT.PUSH);
		back.setText("Back");
		back.addSelectionListener(
				SelectionListener.widgetSelectedAdapter(e-> closeServer()));
		
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		return composite;
	}

	private void hostServer() {
		if (validateInput())
			try {
				hControl.createServer(Integer.parseInt(port.getText()), 2);
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
	}
}
