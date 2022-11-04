import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public abstract class Screen {
	protected Composite			composite;
	
	protected ClientController 	cControl;
	
	protected HostController	hControl;
	
	public Screen(Shell shell, Display display, ClientController cCon, HostController hCon) {
		composite = makeComposite(shell, display);
		cControl = cCon;
		hControl = hCon;
	}
	
	protected abstract Composite makeComposite(Shell shell, Display display);
	
	public Composite getComposite() {
		return composite;
	}
}
