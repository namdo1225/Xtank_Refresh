/**
 * A class to represent the UI portion of the title screen.
 * 
 * For the parent class, @see Screen.java
 * 
 * @author	Nam Do
 * @version	1.0
 * @since	2022-11-12
 */

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class MenuScreen extends Screen {
	private Label		title;
	private Label		winner;
	
	private Button		mainJoin;
	private Button		mainHost;
	private Button		mainExit;
	private Button		updateWinner;
	
	/**
	 * A constructor for MenuScreen.
	 * 
	 * @param shell		a Shell from SWT to help use the graphic library.
	 * @param display	a Display from SWT to help use the graphic library.
	 * @param cCon		a ClientController for the controller of the client.
	 * @param hCon		a HostController for the controller of the host.
	 * @param cMod		a ClientModel for the model of the client.
	 * @param hMod		a HostModel for the model of the host.
	 */
	public MenuScreen(Shell shell, Display display, ClientController cCon, HostController hCon,
			ClientModel cMod, HostModel hMod) {
		super(shell, display, cCon, hCon, cMod, hMod);
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
		
		title = new Label(composite, SWT.BALLOON);
		title.setText("XTank");
		title.setFont(new Font(display,"Times New Roman", 28, SWT.BOLD ));
		title.setAlignment(SWT.CENTER);
		
		mainJoin = new Button(composite, SWT.PUSH);
		mainJoin.setText("Join Game");
		mainJoin.addSelectionListener(
				SelectionListener.widgetSelectedAdapter(e-> cControl.updateScreen(Mode.JOIN)));
		
		mainHost = new Button(composite, SWT.PUSH);
		mainHost.setText("Host Game");
		mainHost.addSelectionListener(
				SelectionListener.widgetSelectedAdapter(e-> hControl.updateScreen(Mode.HOST)));
		
		mainExit = new Button(composite, SWT.PUSH);
		mainExit.setText("Exit Game");
		mainExit.addSelectionListener(
				SelectionListener.widgetSelectedAdapter(e-> System.exit(0)));

		updateWinner = new Button(composite, SWT.PUSH);
		updateWinner.setText("Update Winner");
		updateWinner.addSelectionListener(
				SelectionListener.widgetSelectedAdapter(e-> updateWinner()));
		
		winner = new Label(composite, SWT.BALLOON);
		winner.setText("No winner");
		winner.setFont(new Font(display,"Times New Roman", 14, SWT.BOLD ));
		winner.setAlignment(SWT.CENTER);
		
		composite.setLayout(new FillLayout(SWT.VERTICAL));
		
		return composite;
	}
	
	private void updateWinner() {
		if (cModel.getWinner() == -1)
			winner.setText("No winner");
		else
			winner.setText("Winner: Player " + cModel.getWinner());

	}
}
