
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class XTank2 
{
	public static void main(String[] args) throws Exception  {
		XTankUI2 ui = XTankUI2.get();
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


