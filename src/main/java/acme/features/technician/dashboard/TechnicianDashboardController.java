
package acme.features.technician.dashboard;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import acme.client.controllers.AbstractGuiController;
import acme.client.controllers.GuiController;
import acme.forms.TechnicianDashboard;
import acme.realms.Technician;

@GuiController
@RequestMapping("/technician/dashboard")
public class TechnicianDashboardController extends AbstractGuiController<Technician, TechnicianDashboard> {

	@Autowired
	private TechnicianDashboardService tdService;


	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("show", this.tdService);

	}
}
