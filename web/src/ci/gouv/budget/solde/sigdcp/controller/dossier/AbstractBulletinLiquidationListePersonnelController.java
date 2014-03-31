package ci.gouv.budget.solde.sigdcp.controller.dossier;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;
import ci.gouv.budget.solde.sigdcp.controller.ui.AbstractEffectuerOperationPersonnelController;
import ci.gouv.budget.solde.sigdcp.model.Code;
import ci.gouv.budget.solde.sigdcp.model.dossier.BulletinLiquidationDto;
import ci.gouv.budget.solde.sigdcp.model.dossier.DossierDto;
import ci.gouv.budget.solde.sigdcp.model.dossier.NatureDeplacement;
import ci.gouv.budget.solde.sigdcp.model.dossier.Traitement;
import ci.gouv.budget.solde.sigdcp.model.dossier.TraitementPieceProduite;
import ci.gouv.budget.solde.sigdcp.service.dossier.BulletinLiquidationService;

public abstract class AbstractBulletinLiquidationListePersonnelController extends AbstractEffectuerOperationPersonnelController<BulletinLiquidationDto> implements Serializable {

	private static final long serialVersionUID = -2412073347414420827L;

	/*
	 * Services
	 */
	@Inject protected BulletinLiquidationService bulletinLiquidationService;
	
	/*
	 * Dto
	 */
	@Getter @Setter protected Boolean showBordereauNumero=Boolean.FALSE;
	
	@Override
	protected void initialisation() {
		super.initialisation();
		listTitle = "Liste des bulletins de liquidation";
		selected = new BulletinLiquidationDto(null, null);
		selected.setTraitement(new TraitementPieceProduite());
	}
	
	@Override
	protected String defaultNatureDeplacementCode() {
		return Code.NATURE_DEPLACEMENT_AFFECTATION;
	}
		
	@Override
	protected String identifierFieldName() {
		return "id";
	}
			
	@Override
	protected String detailsOutcome(BulletinLiquidationDto object) {
		return navigationManager.outcome(object.getDossierDto().getDossier());
	}
	
	@Override
	protected void detailsOutcomeParameters(Map<String, List<String>> parameters, BulletinLiquidationDto dto) {
		addParameters(parameters, webConstantResources.getRequestParamDossier(), dto.getDossierDto().getDossier().getNumero());
		addParameters(parameters, webConstantResources.getRequestParamCrudType(), webConstantResources.getRequestParamCrudRead());
	}
	
	@Override
	protected String objectOutcome(Object entity) {
		return navigationManager.outcome( ((DossierDto) entity).getDossier());
	}
	
	@Override
	public BigDecimal depense(BulletinLiquidationDto data) {
		if(data==null || data.getPiece()==null || data.getPiece().getMontant()==null)
			return BigDecimal.ZERO;
		return data.getPiece().getMontant();
	}
	
	@Override
	public Traitement dernierTraitement(BulletinLiquidationDto data) {
		return data.getPiece().getDernierTraitement();
	}
	
	@Override
	public String numero(BulletinLiquidationDto data) {
		return data.getPiece().getNumero();
	}
	
	@Override
	public Date dateCreation(BulletinLiquidationDto data) {
		return data.getDateCreation();
	}
	
	@Override
	protected void valider(String natureOperationCode, Collection<BulletinLiquidationDto> datas) {
		bulletinLiquidationService.valider(natureDeplacement, natureOperationCode, datas);
	}
	
	@Override
	protected Collection<BulletinLiquidationDto> data(Collection<NatureDeplacement> natureDeplacements) {
		return bulletinLiquidationService.findATraiterByNatureDeplacementsByNatureOperationId(natureDeplacements, natureOperationCode());
	}

}
