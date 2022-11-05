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
	
	public HostScreen(Shell shell, Display display, ClientController cCon, HostController hCon,
			ClientModel cMod, HostModel hMod) {
		super(shell, display, cCon, hCon, cMod, hMod);
	}
	
	@Override
	protected Composite makeComposite(Shell shell, Display display) {
		composite = new Composite(shell, SWT.COLOR_WHITE);
		
		String address = "";
		try {
			address = InetAddress.getLocalHost().toString();
		} catch (UnknownHostException e1) {}
		
		guide = new Label(composite, SWT.BALLOON);
		guide.setText("Click 'Update' to create new server connection with the port number"
				+ "defined in the text field. You cannot click 'Next' to start the game "
				+ "unless 2 or more players are in. Your input is validated. You will "
				+ "crash the program if your number is out of int's bounds."
				+ "\nWell-known port range: 1024-65,535\tLocalhost: 127.0.0.1\tYour (probably wrong) IP: "
				+ address);
		guide.setFont(new Font(display,"Times New Roman", 12, SWT.BOLD ));
		guide.setAlignment(SWT.CENTER);
		
		numPlayers = new Label(composite, SWT.BALLOON);
		numPlayers.setFont(new Font(display,"Times New Roman", 14, SWT.BOLD ));
		numPlayers.setText("Number of players:\t0");
		
		port = new Text(composite, SWT.LEFT | SWT.BORDER);
		port.setMessage("Enter port number:");
		port.setText("8080");
		
		update = new Button(composite, SWT.PUSH);
		update.setText("Update");
		update.addSelectionListener(
				SelectionListener.widgetSelectedAdapter(e-> hostServer()));
		
		next = new Button(composite, SWT.PUSH);
		next.setText("Next");
		next.addSelectionListener(SelectionListener.widgetSelectedAdapter(e-> validateInput()));
		
		back = new Button(composite, SWT.PUSH);
		back.setText("Back");
		back.addSelectionListener(
				SelectionListener.widgetSelectedAdapter(e-> hControl.updateScreen(Mode.MAIN)));
		
		composite.setLayout(new FillLayout(SWT.VERTICAL));
		
		return composite;
	}

	private void hostServer() {
		if (validateInput())
			try {
				hControl.createServerSocket(Integer.parseInt(port.getText()));
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
}
