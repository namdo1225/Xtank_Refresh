
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class XTank 
{
	public static void main(String[] args) throws Exception  {
		XTankUI ui = XTankUI.get();
		ClientModel cModel = new ClientModel();
		ClientController cController = new ClientController();
		
		HostModel hModel = new HostModel();
		HostController hController = new HostController();
		
		ui.setClientMVC(cModel, cController);
		cController.setMVC(cModel);
		cModel.setMVC(ui);
		
		ui.setHostMVC(hModel, hController);
		hController.setMVC(hModel);
		hModel.setMVC(ui);
		
		ui.start();
    }
}


