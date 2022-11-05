
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class XTankUI
{
	private static XTankUI			ui;
	private Display					display;
	private Shell					shell;
	
	private ClientModel				clientModel;
	private ClientController		clientControl;
	
	private HostModel				hostModel;
	private HostController			hostControl;
	
	private	Screen[]				screens;
	private GridData[]				gridDatas;

	private GridLayout				layout;
	
	public synchronized static XTankUI get() {
		if (ui == null)
			ui = new XTankUI();
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
		
		shell.addListener(SWT.Close, new Listener() {
			@Override
			public void handleEvent(Event event) {
				try {
					clientControl.closeInput();
					clientControl.closeOutput();
					clientControl.stopRunnable();
						
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				shell.dispose();
				display.dispose();
			}
		});
		
		screens = new Screen[4];
		gridDatas = new GridData[4];
		
		screens[0] = new MenuScreen(shell, display, clientControl, hostControl, clientModel, hostModel);
		screens[1] = new JoinScreen(shell, display, clientControl, hostControl, clientModel, hostModel);
		screens[2] = new HostScreen(shell, display, clientControl, hostControl, clientModel, hostModel);
		screens[3] = new GameScreen(shell, display, clientControl, hostControl, clientModel, hostModel);
		
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
}
