import java.net.InetAddress;
import java.net.UnknownHostException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class JoinScreen extends Screen {
	private Label		title;
	
	private Text		iP;
	private Text		port;
	
	private Button		next;
	private Button		back;
	
	public JoinScreen(Shell shell, Display display) {
		super(shell, display);
	}
	
	protected Composite makeComposite(Shell shell, Display display) {
		composite = new Composite(shell, SWT.COLOR_WHITE);
		
		String address = "";
		try {
			address = InetAddress.getLocalHost().toString();
		} catch (UnknownHostException e1) {}
		
		title = new Label(composite, SWT.BALLOON);
		title.setText("Set your IP address and port number and click 'Next'. \n'UAWiFi' "
				+ "does not work consistently for us."
				+ "\nLocalhost: 127.0.0.1\tYour (probably wrong) IP: " + address);
		title.setFont(new Font(display,"Times New Roman", 14, SWT.BOLD ));
		title.setAlignment(SWT.CENTER);
		
		iP = new Text(composite, SWT.LEFT | SWT.BORDER);
		iP.setMessage("Enter IPv4 address");
		
		port = new Text(composite, SWT.LEFT | SWT.BORDER);
		port.setMessage("Enter port number");
		
		next = new Button(composite, SWT.PUSH);
		next.setText("Next");
		next.addSelectionListener(SelectionListener.widgetSelectedAdapter(e-> validateInput()));
		
		back = new Button(composite, SWT.PUSH);
		back.setText("Back");
		back.addSelectionListener(SelectionListener.widgetSelectedAdapter(e-> System.out.println("Pressed")));
		
		composite.setLayout(new FillLayout(SWT.VERTICAL));
		
		return composite;
	}
	
	private void validateInput() {
		String[] fourHex = iP.getText().split("\\.");
		
		if (fourHex.length != 4)
			return;
		
		for (int i = 0; i < 4; i++) {
			int partIP = Integer.parseInt(fourHex[i]);
			if (partIP < 0 || partIP > 255)
				return;
		}
		
		int portNum = Integer.parseInt(port.getText());
		if (portNum < 0 || portNum > 65535)
			return;
	}
}
