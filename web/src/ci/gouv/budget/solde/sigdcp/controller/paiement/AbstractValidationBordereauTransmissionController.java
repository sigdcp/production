package ci.gouv.budget.solde.sigdcp.controller.paiement;

import java.io.Serializable;
import java.util.Collection;

import lombok.Getter;
import ci.gouv.budget.solde.sigdcp.model.dossier.BordereauTransmissionDto;
import ci.gouv.budget.solde.sigdcp.model.dossier.NatureDeplacement;

@Getter
public abstract class AbstractValidationBordereauTransmissionController extends AbstractBordereauTransmissionListeController implements Serializable {

	private static final long serialVersionUID = -3441386175797582945L;
	
	@Override
	protected void initialisation() {
		super.initialisation();
		showMontant=true;
	}
	
	@Override
	protected Collection<BordereauTransmissionDto> data(Collection<NatureDeplacement> natureDeplacements) {
		return bordereauTransmissionService.findATraiterByNatureDeplacementsByNatureOperationId(natureDeplacements,natureOperationCode());
	}
		
	@Override
	protected void valider(String natureOperationCode, Collection<BordereauTransmissionDto> datas) {
		bordereauTransmissionService.valider(natureDeplacement,natureOperationCode, datas);
	}
	
	
}
