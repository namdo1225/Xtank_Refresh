import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public abstract class Screen {
	protected Composite		composite;
	
	public Screen(Shell shell, Display display) {
		composite = makeComposite(shell, display);
	}
	
	protected abstract Composite makeComposite(Shell shell, Display display);
}
