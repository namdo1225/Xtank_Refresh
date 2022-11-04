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
	
	public HostScreen(Shell shell, Display display, ClientController cCon, HostController hCon) {
		super(shell, display, cCon, hCon);
	}
	
	@Override
	protected Composite makeComposite(Shell shell, Display display) {
		composite = new Composite(shell, SWT.COLOR_WHITE);
		
		String address = "";
		try {
			address = InetAddress.getLocalHost().toString();
		} catch (UnknownHostException e1) {}
		
		guide = new Label(composite, SWT.BALLOON);
		guide.setText("Your IPv4 address is displayed but might not be correct. A socket for port"
				+ " 8080 is created automatically. You can change the port by entering a new number"
				+ " and clicking 'Update'. When there are 2 or more players, you can start the game."
				+ "\nLocalhost: 127.0.0.1\tYour (probably wrong) IP: " + address);
		guide.setFont(new Font(display,"Times New Roman", 14, SWT.BOLD ));
		guide.setAlignment(SWT.CENTER);
		
		numPlayers = new Label(composite, SWT.BALLOON);
		numPlayers.setFont(new Font(display,"Times New Roman", 14, SWT.BOLD ));
		numPlayers.setText("Number of players:\t");
		
		port = new Text(composite, SWT.LEFT | SWT.BORDER);
		port.setMessage("Enter new port number (original is 8080):");
		
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
