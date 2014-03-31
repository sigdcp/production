package ci.gouv.budget.solde.sigdcp.controller.paiement;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import lombok.Getter;
import ci.gouv.budget.solde.sigdcp.controller.ui.AbstractEffectuerOperationPersonnelController;
import ci.gouv.budget.solde.sigdcp.model.dossier.BordereauTransmissionDto;
import ci.gouv.budget.solde.sigdcp.model.dossier.Traitement;
import ci.gouv.budget.solde.sigdcp.service.dossier.BordereauTransmissionService;

@Getter
public abstract class AbstractBordereauTransmissionListeController extends AbstractEffectuerOperationPersonnelController<BordereauTransmissionDto> implements Serializable {

	private static final long serialVersionUID = -3441386175797582945L;
	
	@Inject protected BordereauTransmissionService bordereauTransmissionService;
	
	@Override
	protected void initialisation() {
		super.initialisation();
		listTitle = "Liste des bordereaux de transmissions";
	}
	
	@Override
	protected String[] defaultNatureDeplacmentCodeListe() {
		return touteLesDepenses();
	}
	
	@Override
	protected void detailsOutcomeParameters(Map<String, List<String>> parameters, BordereauTransmissionDto dto) {
		addParameters(parameters, webConstantResources.getRequestParamBordereau(), dto.getPiece().getId()+"");
	}
	
	@Override
	protected String detailsOutcome(BordereauTransmissionDto object) {
		return "realiserbtbl";
	}
	
	@Override
	public String getRecordIdentifierFieldName() {
		return "id";
	}
	
	@Override
	public BigDecimal depense(BordereauTransmissionDto data) {
		if(data==null || data.getPiece()==null || data.getPiece().getMontant()==null)
			return BigDecimal.ZERO;
		return data.getPiece().getMontant();
	}
	
	@Override
	public Traitement dernierTraitement(BordereauTransmissionDto data) {
		return data.getPiece().getDernierTraitement();
	}
	
	@Override
	public String numero(BordereauTransmissionDto data) {
		return data.getPiece().getNumero();
	}
	
	@Override
	public Date dateCreation(BordereauTransmissionDto data) {
		return data.getDateCreation();
	}

}
