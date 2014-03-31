package ci.gouv.budget.solde.sigdcp.controller.dossier;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.omnifaces.util.Faces;

import ci.gouv.budget.solde.sigdcp.controller.ui.AbstractEntityListUIController;
import ci.gouv.budget.solde.sigdcp.model.Code;
import ci.gouv.budget.solde.sigdcp.model.dossier.DossierDto;
import ci.gouv.budget.solde.sigdcp.model.dossier.DossierMission;
import ci.gouv.budget.solde.sigdcp.model.identification.AgentEtat;
import ci.gouv.budget.solde.sigdcp.service.dossier.DossierService;

@Named @ViewScoped //@Log
public class DossierAgentEtatListeController extends AbstractEntityListUIController<DossierDto> implements Serializable {

	private static final long serialVersionUID = -2412073347414420827L;

	/*
	 * Services
	 */
	@Inject protected DossierService dossierService;

	@Override
	protected void initialisation() {
		super.initialisation();
		title = text("menu.consulter.demande");
		internalCode = "FS_DEM_01_Ecran_01";
		defaultSubmitCommand.setRendered(Boolean.FALSE);
		closeCommand.setRendered(Boolean.FALSE);
		//nextViewOutcome = "demande";
		removeDetailsCommand.setRendered(true);
		removeDetailsCommand.setValue(text("bouton.annuler"));
		selectLabel="bouton.afficher";
		
		selectLabel="bouton.consulter";
	}
	
	@Override
	protected ProcessingType getProcessingType() {
		return ProcessingType.SINGLE;
	}
		
	@Override
	protected String identifierFieldName() {
		return "numero";
	}
	
	@Override
	protected List<DossierDto> load() {
		return new LinkedList<>(dossierService.findByAgentEtatAndAyantDroit((AgentEtat) userSessionManager.getUser()));
	}
	
	@Override
	protected void onRemoveDetailsCommandAction(DossierDto dto) throws Exception {
		Faces.redirect(Faces.getServletContext().getContextPath()+"/"+_href(dto,webConstantResources.getRequestParamCrudDelete()));
	}
	
	@Override
	public Boolean canRemove(DossierDto dto) {
		return !(dto.getDossier() instanceof DossierMission) && actionable(dto);
	}
	
	@Override
	public Boolean actionable(DossierDto dto){
		if(dto.getDossier() instanceof DossierMission){
			return Code.NATURE_OPERATION_SAISIE.equals(dto.getNatureOperationCode()) ||  
					Code.NATURE_OPERATION_TRANSMISSION_SAISIE_A_ORGANISATEUR.equals(dto.getNatureOperationCode());
		}else{
			return Code.NATURE_OPERATION_SAISIE.equals(dto.getNatureOperationCode()) || 
					Code.NATURE_OPERATION_SOUMISSION.equals(dto.getNatureOperationCode())
				|| Code.NATURE_OPERATION_DEPOT.equals(dto.getNatureOperationCode());	
		}
		
	}
	
	@Override
	public String actionLabel(DossierDto dto){
		
		switch(dto.getNatureOperationCode()){
		case Code.NATURE_OPERATION_SAISIE:case Code.NATURE_OPERATION_SOUMISSION:return "Modifier";
		case Code.NATURE_OPERATION_TRANSMISSION_SAISIE_A_ORGANISATEUR: return "Completer";
		case Code.NATURE_OPERATION_DEPOT: return "Déposer";
		}
		return null;
	}
	
	@Override
	public String actionHref(DossierDto dto){
		return _href(dto, webConstantResources.getRequestParamCrudUpdate());
	}
	
	@Override
	protected void hrefParameters(Map<String, Object> parameters, DossierDto dto) {
		super.hrefParameters(parameters, dto);
		parameters.put(webConstantResources.getRequestParamDossier(),dto.getDossier().getNumero());
	}
	
	@Override
	protected Object hrefObjectOutcome(DossierDto entity) {
		return entity.getDossier();
	}
		
}
