package ci.gouv.budget.solde.sigdcp.controller.paiement;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import ci.gouv.budget.solde.sigdcp.controller.liquidation.AbstractValidationLiquidationController;
import ci.gouv.budget.solde.sigdcp.model.Code;

@Named @ViewScoped
public class ReglerBulletinLiquidationController extends AbstractValidationLiquidationController implements Serializable {

	private static final long serialVersionUID = -2412073347414420827L;
	
	@Override
	protected void initialisation() {
		super.initialisation();
		showBordereauNumero=true;
		showTotalDepense=true;
		showMontant=true;
	}
	
	@Override
	protected String natureOperationCode() {
		return Code.NATURE_OPERATION_REGLER_BL;
	}
		
	
		
}
