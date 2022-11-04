
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class XTankUI2
{
	private static XTankUI2			ui;
	private Display					display;
	private Shell					shell;
	
	private ClientModel				clientModel;
	private ClientController		clientControl;
	
	private HostModel				hostModel;
	private HostController			hostControl;
	
	private	Screen[]				screens;
	private GridData[]				gridDatas;
	private Screen					joinScreen;
	private Screen					hostScreen;
	private Screen					menuScreen;
	private Screen					gameScreen;

	private GridLayout				layout;
	
	public synchronized static XTankUI2 get() {
		if (ui == null)
			ui = new XTankUI2();
		return ui;
	}
		
	public void start() {
		display = new Display();
		shell = new Shell(display);
		shell.setText("A3_XTank");
		
		layout = new GridLayout();
		layout.numColumns = 1;
		shell.setLayout(layout);
		shell.setBackground(null);
		
		screens = new Screen[4];
		gridDatas = new GridData[4];
		
		screens[0] = new MenuScreen(shell, display, clientControl, hostControl);
		screens[1] = new JoinScreen(shell, display, clientControl, hostControl);
		screens[2] = new HostScreen(shell, display, clientControl, hostControl);
		screens[3] = new GameScreen(shell, display, clientControl, hostControl);
		
		for (int i = 0; i < gridDatas.length; i++) {
			gridDatas[i] = new GridData();
			gridDatas[i].horizontalAlignment = GridData.FILL;
			gridDatas[i].grabExcessHorizontalSpace = true;

			gridDatas[i].verticalAlignment = GridData.FILL;
			gridDatas[i].grabExcessVerticalSpace = true;
						
			if (i > 0) {
				screens[i].getComposite().setVisible(false);
				gridDatas[i].exclude = true;
			}
			
			screens[i].getComposite().setLayoutData(gridDatas[i]);
		}
		//shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
		
	}
	
	public void setClientMVC(ClientModel model, ClientController control) {
		clientModel = model;
		clientControl = control;
	}
	
	public void setHostMVC(HostModel model, HostController control) {
		hostModel = model;
		hostControl = control;
	}
	
	public void updateScreen(Mode mode) {		
		for (int i = 0; i < screens.length; i++) {
			if (i == mode.ordinal()) {
				screens[i].getComposite().setVisible(true);
				gridDatas[i].exclude = false;
				screens[i].getComposite().setLayoutData(gridDatas[i]);
			}
			else {
				screens[i].getComposite().setVisible(false);
				gridDatas[i].exclude = true;
				screens[i].getComposite().setLayoutData(gridDatas[i]);
			}
		}
		shell.layout(true, true);
	}
	
	public void setServerMVC() {
	}
}


