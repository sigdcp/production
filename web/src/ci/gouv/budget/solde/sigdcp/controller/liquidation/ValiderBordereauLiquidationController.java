package ci.gouv.budget.solde.sigdcp.controller.liquidation;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import ci.gouv.budget.solde.sigdcp.controller.paiement.AbstractValidationBordereauTransmissionController;
import ci.gouv.budget.solde.sigdcp.model.Code;
import ci.gouv.budget.solde.sigdcp.model.dossier.BordereauTransmissionDto;

@Named @ViewScoped
public class ValiderBordereauLiquidationController extends AbstractValidationBordereauTransmissionController implements Serializable {

	private static final long serialVersionUID = -2412073347414420827L;

	@Override
	protected void initialisation() {
		//calculerDisponible=false;
		super.initialisation();
		showTotalDepense=Boolean.TRUE;
		showMontant=true;
		editDetailsCommand.setRendered(true);
		
	}
	
	@Override
	protected String natureOperationCode() {
		return Code.NATURE_OPERATION_VALIDATION_BTBL;
	}
	
	@Override
	protected String[] defaultNatureDeplacmentCodeListe() {
		return null;
	}
		
	@Override
	public void onDialogReturn() {
		BordereauTransmissionDto newDto = bordereauTransmissionService.findDtoById(dialogSelected.getPiece().getId());
		int index = list.indexOf(dialogSelected);
		list.set(index, newDto);
		natureDeplacement.getCategorie().setDisponible(newDto.getPiece().getNatureDeplacement().getCategorie().getDisponible());
		
		calculerTotalDepense();
		calculerDisponible();
		
		RequestContext.getCurrentInstance().update("form:disponible");
		RequestContext.getCurrentInstance().update("form:datatable:depensetotale");
		RequestContext.getCurrentInstance().update("form:datatable:"+index+":depense");
		
	}
}
