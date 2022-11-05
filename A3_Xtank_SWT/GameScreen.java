import java.net.InetAddress;
import java.net.UnknownHostException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class GameScreen extends Screen {
	private Composite 		compositePlayer;
	private Label			plHeader;
	private Button			quit;
	
	private Composite 		compositeGame;
	private Label			fill;
	
	private GridLayout layout;
	
	public GameScreen(Shell shell, Display display) {
		super(shell, display);
	}
	
	@Override
	protected Composite makeComposite(Shell shell, Display display) {
		composite = new Composite(shell, SWT.COLOR_WHITE);
		
		compositePlayer = new Composite(composite, SWT.COLOR_WHITE);
		compositePlayer.setLayout(new FillLayout(SWT.VERTICAL));

		plHeader = new Label(compositePlayer, SWT.BALLOON);
		plHeader.setText("Players:");
		plHeader.setFont(new Font(display,"Times New Roman", 14, SWT.BOLD ));
		plHeader.setAlignment(SWT.LEFT);
		
		quit = new Button(compositePlayer, SWT.PUSH);
		quit.setText("Quit");
		quit.addSelectionListener(SelectionListener.widgetSelectedAdapter(e-> System.out.println("Pressed")));
		
		compositeGame = new Composite(composite, SWT.COLOR_WHITE);
		
		fill = new Label(compositeGame, SWT.BALLOON);
		fill.setText("FILFEWFEIWOJWEIROPEWIOEW");
		fill.setFont(new Font(display,"Times New Roman", 48, SWT.BOLD ));
		fill.setAlignment(SWT.LEFT);
		
		
		layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);
		
		return composite;
	}
	
}
