import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public abstract class Screen {
	protected Composite			composite;
	
	protected ClientModel		cModel;
	protected ClientController 	cControl;
	
	protected HostModel			hModel;
	protected HostController	hControl;
	
	public Screen(Shell shell, Display display, ClientController cCon, HostController hCon,
			ClientModel cMod, HostModel hMod) {
		composite = makeComposite(shell, display);
		cControl = cCon;
		hControl = hCon;
		
		cModel = cMod;
		hModel = hMod;
	}
	
	public Screen(Shell shell, Display display, ClientController cCon, HostController hCon,
			ClientModel cMod, HostModel hMod, int mapID, int tankID) {
		composite = makeCompositeAndMap(shell, display, mapID, tankID);
		cControl = cCon;
		hControl = hCon;
		
		cModel = cMod;
		hModel = hMod;
	}
	
	protected abstract Composite makeComposite(Shell shell, Display display);
	
	protected Composite makeCompositeAndMap(Shell shell, Display display, int mapID, int tankID) {
		return null;
	}
	
	public Composite getComposite() {
		return composite;
	}
}
