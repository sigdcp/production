package ci.gouv.budget.solde.sigdcp.controller.dossier;


import java.io.Serializable;

import javax.faces.event.ValueChangeEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import ci.gouv.budget.solde.sigdcp.model.Code;
import ci.gouv.budget.solde.sigdcp.model.dossier.DossierDD;
import ci.gouv.budget.solde.sigdcp.service.dossier.DossierDDService;
import ci.gouv.budget.solde.sigdcp.service.utils.validaton.AbstractDossierValidator;
import ci.gouv.budget.solde.sigdcp.service.utils.validaton.DossierDDValidator;
 
@Named @ViewScoped
public class FaireDemandeDDController extends AbstractFaireDemandeController<DossierDD,DossierDDService> implements Serializable {
	
	private static final long serialVersionUID = -611561465509440427L;
	
	/*
	 * Services
	 */  
	@Inject private DossierDDService dossierDDService;

	@Inject private DossierDDValidator validator;
	
	/*
	 * Attributs de parametrages de la vue
	 */
	@Getter private Boolean showDatePriseService = Boolean.FALSE;
	@Getter private Boolean showDateCessationService = Boolean.FALSE;
	@Getter private Boolean showDateMiseRetraite = Boolean.FALSE;
	@Getter private Boolean showServiceOrigine = Boolean.FALSE;
	@Getter private Boolean showServiceAcceuil = Boolean.FALSE;
	
	@Override
	protected DossierDDService getDossierService() {
		return dossierDDService;
	}
	
	@Override
	protected AbstractDossierValidator<DossierDD> validator() {
		return validator;
	}
	
	@Override
	protected void initialisation() {
		super.initialisation();
		if(Code.NATURE_DEPLACEMENT_AFFECTATION.equals(entity.getDeplacement().getNature().getCode())){
			showDatePriseService = Boolean.TRUE;
			showServiceAcceuil = Boolean.TRUE;
		}else if(Code.NATURE_DEPLACEMENT_MUTATION.equals(entity.getDeplacement().getNature().getCode())){
			showDatePriseService = Boolean.TRUE;
			showDateCessationService = Boolean.TRUE;
			showServiceOrigine = Boolean.TRUE;
			showServiceAcceuil = Boolean.TRUE;
		}else if(Code.NATURE_DEPLACEMENT_RETRAITE.equals(entity.getDeplacement().getNature().getCode())){
			showDateMiseRetraite = Boolean.TRUE;
			showServiceOrigine = Boolean.TRUE;	
		}
	}
	
	public void marieListener(ValueChangeEvent valueChangeEvent){
		dossierDto.setMarie((Boolean) valueChangeEvent.getNewValue());
		updatePieceJustificatives();
	}
	
	public void nombreEnfantListener(ValueChangeEvent valueChangeEvent){
		dossierDto.setNombreEnfant((Integer) valueChangeEvent.getNewValue());
		updatePieceJustificatives();
	}
		
}
