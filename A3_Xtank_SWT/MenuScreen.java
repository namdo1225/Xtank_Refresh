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
	
	private Button		mainJoin;
	private Button		mainHost;
	private Button		mainExit;

	public MenuScreen(Shell shell, Display display) {
		super(shell, display);
	}

	protected Composite makeComposite(Shell shell, Display display) {
		composite = new Composite(shell, SWT.COLOR_WHITE);
		
		title = new Label(composite, SWT.BALLOON);
		title.setText("XTank");
		title.setFont(new Font(display,"Times New Roman", 28, SWT.BOLD ));
		title.setAlignment(SWT.CENTER);
		
		mainJoin = new Button(composite, SWT.PUSH);
		mainJoin.setText("Join Game");
		mainJoin.addSelectionListener(SelectionListener.widgetSelectedAdapter(e-> System.out.println("Pressed")));
		
		mainHost = new Button(composite, SWT.PUSH);
		mainHost.setText("Host Game");
		mainHost.addSelectionListener(SelectionListener.widgetSelectedAdapter(e-> System.out.println("Pressed")));
		
		mainExit = new Button(composite, SWT.PUSH);
		mainExit.setText("Exit Game");
		mainExit.addSelectionListener(SelectionListener.widgetSelectedAdapter(e-> System.exit(0)));

		composite.setLayout(new FillLayout(SWT.VERTICAL));
		
		return composite;
	}
}
