/**
 * A class that has a main function to run the entire program.
 * It also links the model, view, and controller together.
 * 
 * @author	Patrick Comden
 * @version	1.0
 * @since	2022-11-12
 */

public class XTank {
	public static void main(String[] args) throws Exception  {
		XTankUI ui = XTankUI.get();
		ClientModel cModel = ClientModel.get();
		ClientController cController = ClientController.get();
		
		HostModel hModel = HostModel.get();
		HostController hController = HostController.get();
		
		ui.setClientMVC(cModel, cController);
		cController.setMVC(cModel);
		cModel.setMVC(ui);
		
		ui.setHostMVC(hModel, hController);
		hController.setMVC(hModel);
		hModel.setMVC(ui);
		
		ui.start();
    }
}


