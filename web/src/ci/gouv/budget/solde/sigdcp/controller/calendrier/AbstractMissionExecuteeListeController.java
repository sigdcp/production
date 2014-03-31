package ci.gouv.budget.solde.sigdcp.controller.calendrier;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;

import org.omnifaces.util.Faces;

import ci.gouv.budget.solde.sigdcp.controller.ui.AbstractEntityListUIController;
import ci.gouv.budget.solde.sigdcp.model.calendrier.MissionExecuteeDto;
import ci.gouv.budget.solde.sigdcp.model.dossier.NatureDeplacement;
import ci.gouv.budget.solde.sigdcp.model.dossier.Statut;
import ci.gouv.budget.solde.sigdcp.service.calendrier.MissionExecuteeService;

public abstract class AbstractMissionExecuteeListeController extends AbstractEntityListUIController<MissionExecuteeDto> implements Serializable {

	private static final long serialVersionUID = -2412073347414420827L;

	/*
	 * Services
	 */
	
	@Inject protected MissionExecuteeService missionExecuteeService;
	/*
	 * Dto
	 */
	@Getter @Setter protected Boolean showMontant=Boolean.TRUE,showObservation=Boolean.FALSE,
			showValidation=Boolean.FALSE,showDateCreation=Boolean.TRUE;

	/*
	 * Paramètres url
	 */
	@Getter @Setter protected Statut statut;
	@Getter @Setter protected NatureDeplacement natureDeplacement;
	
	@Override
	protected void initialisation() {
		//statut = dynamicEnumerationService.findByClass(Statut.class, codeStatut());
		//natureDeplacement = dynamicEnumerationService.findByClass(NatureDeplacement.class, Code.NATURE_DEPLACEMENT_AFFECTATION);
		super.initialisation();	
		//defaultSubmitCommand.setValue(text("bouton.valider"));
		defaultSubmitCommand.setRendered(false);
		//enableSearch();
	}
	
	@Override
	protected String identifierFieldName() {
		return "id";
	}
	
	@Override
	protected List<MissionExecuteeDto> load() {
		return new LinkedList<>(missions());
	}
	
	protected abstract Collection<MissionExecuteeDto> missions();
	
	@Override
	protected void onRemoveDetailsCommandAction(MissionExecuteeDto dto) throws Exception {
		Faces.redirect(Faces.getServletContext().getContextPath()+"/"+_href(dto,webConstantResources.getRequestParamCrudDelete()));
	}
	
	@Override
	public String actionHref(MissionExecuteeDto dto){
		return _href(dto, webConstantResources.getRequestParamCrudUpdate());
	}
	
	@Override
	protected void hrefParameters(Map<String, Object> parameters, MissionExecuteeDto dto) {
		super.hrefParameters(parameters, dto);
		parameters.put(webConstantResources.getRequestParamMission(),dto.getMissionExecutee().getId());
	}
	
	@Override
	protected Object hrefObjectOutcome(MissionExecuteeDto dto) {
		return dto.getMissionExecutee();
	}
	
	@Override
	protected String detailsOutcome(MissionExecuteeDto object) {
		return navigationManager.outcome(object.getMissionExecutee());
	}
	
	@Override
	protected void detailsOutcomeParameters(Map<String, List<String>> parameters, MissionExecuteeDto dto) {
		addParameters(parameters, webConstantResources.getRequestParamMission(), dto.getMissionExecutee().getId()+"");
		addParameters(parameters, webConstantResources.getRequestParamCrudType(), webConstantResources.getRequestParamCrudRead());
	}
	
	@Override
	protected ProcessingType getProcessingType() {
		return ProcessingType.SINGLE;
	}
}
