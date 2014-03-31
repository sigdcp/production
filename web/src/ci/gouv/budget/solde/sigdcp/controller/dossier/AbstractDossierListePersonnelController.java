package ci.gouv.budget.solde.sigdcp.controller.dossier;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;
import ci.gouv.budget.solde.sigdcp.controller.ui.AbstractEffectuerOperationPersonnelController;
import ci.gouv.budget.solde.sigdcp.model.Code;
import ci.gouv.budget.solde.sigdcp.model.dossier.DossierDto;
import ci.gouv.budget.solde.sigdcp.model.dossier.Traitement;
import ci.gouv.budget.solde.sigdcp.model.dossier.TraitementDossier;
import ci.gouv.budget.solde.sigdcp.service.dossier.DossierService;

public abstract class AbstractDossierListePersonnelController extends AbstractEffectuerOperationPersonnelController<DossierDto> implements Serializable {

	private static final long serialVersionUID = -2412073347414420827L;

	/*
	 * Services
	 */
	@Inject protected DossierService dossierService;
	/*
	 * Dto
	 */
	@Getter @Setter protected Boolean showBulletinNumero=Boolean.FALSE,showBordereauNumero=Boolean.FALSE;
	
	@Override
	protected void initialisation() {
		super.initialisation();
		listTitle = "Liste des demandes";
		selected = new DossierDto(null);
		selected.setTraitement(new TraitementDossier());
	}
	
	@Override
	protected String defaultNatureDeplacementCode() {
		return Code.NATURE_DEPLACEMENT_AFFECTATION;
	}
		
	@Override
	protected String identifierFieldName() {
		return "numero";
	}
			
	@Override
	protected String detailsOutcome(DossierDto object) {
		return navigationManager.outcome(object.getDossier());
	}
	
	@Override
	protected void detailsOutcomeParameters(Map<String, List<String>> parameters, DossierDto dto) {
		addParameters(parameters, webConstantResources.getRequestParamDossier(), dto.getDossier().getNumero());
		addParameters(parameters, webConstantResources.getRequestParamCrudType(), webConstantResources.getRequestParamCrudRead());
	}
	
	@Override
	protected String objectOutcome(Object entity) {
		return navigationManager.outcome( ((DossierDto) entity).getDossier());
	}
	
	@Override
	public BigDecimal depense(DossierDto data) {
		if(data==null || data.getDossier()==null || data.getDossier().getMontantIndemnisation()==null)
			return BigDecimal.ZERO;
		//return data.getDossier().getMontantIndemnisation();
		return data.getBulletinLiquidationSaisie().getMontant();
	}
	
	@Override
	public Traitement dernierTraitement(DossierDto data) {
		return data.getDossier().getDernierTraitement();
	}
	
	@Override
	public String numero(DossierDto data) {
		return data.getNumero();
	}
	
	@Override
	public Date dateCreation(DossierDto data) {
		return data.getDateCreation();
	}

}
