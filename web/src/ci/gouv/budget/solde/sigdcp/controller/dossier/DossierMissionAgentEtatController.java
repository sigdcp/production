package ci.gouv.budget.solde.sigdcp.controller.dossier;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;
import ci.gouv.budget.solde.sigdcp.model.Code;
import ci.gouv.budget.solde.sigdcp.model.calendrier.MissionExecutee;
import ci.gouv.budget.solde.sigdcp.model.dossier.DossierMission;
import ci.gouv.budget.solde.sigdcp.service.calendrier.MissionExecuteeService;
import ci.gouv.budget.solde.sigdcp.service.dossier.DossierMissionService;
import ci.gouv.budget.solde.sigdcp.service.resources.CRUDType;

@Getter @Setter @Named @ViewScoped
public class DossierMissionAgentEtatController extends AbstractDossierUIController<DossierMission, DossierMissionService> implements Serializable {

	private static final long serialVersionUID = -8840662624432472475L;

	@Inject private MissionExecuteeService missionExecuteeService;
	@Inject private DossierMissionService dossierMissionService;
	
	private MissionExecutee missionExecutee;

	@Override
	protected void initialisation() {
		super.initialisation();
		missionExecutee = missionExecuteeService.findByDossier(entity);
	}
		
	@Override
	protected DossierMissionService getDossierService() {
		return dossierMissionService;
	}
	
	@Override
	protected CRUDType operationSaisie() {
		if(Code.NATURE_OPERATION_TRANSMISSION_SAISIE_A_BENEFICIAIRE.equals(dossierDto.getDossier().getDernierTraitement().getOperation().getNature().getCode()))
			return CRUDType.UPDATE;
		return CRUDType.READ;
	}
	
	@Override
	protected String action() {
		if(entity.getDernierTraitement()==null || Code.NATURE_OPERATION_SAISIE.equals(entity.getDernierTraitement().getOperation().getNature().getCode()))
			return webConstantResources.getRequestParamCrudRead();
		return super.action();
	}
	
}
