package ci.gouv.budget.solde.sigdcp.controller.calendrier;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.omnifaces.util.Faces;

import ci.gouv.budget.solde.sigdcp.model.Code;
import ci.gouv.budget.solde.sigdcp.model.calendrier.MissionExecuteeDto;

@Named @ViewScoped
public class MissionExecuteeListeController extends AbstractMissionExecuteeListeController implements Serializable {

	private static final long serialVersionUID = -2520806200426979462L;

	@Override
	protected void initialisation() {
		super.initialisation();
		title="Liste des missions organisées";
		selectLabel = "bouton.consulter";
		//editDetailsCommand.setRendered(false);
	}
	
	@Override
	protected Collection<MissionExecuteeDto> missions() {
		return missionExecuteeService.findMissionOrganisees();
	}
	

	public String href(MissionExecuteeDto missionExecuteeDto){
		return navigationManager.url("demande_mission_pointfocal", new Object[]{webConstantResources.getRequestParamMission(),missionExecuteeDto.getMissionExecutee().getId()},false);
	}

	@Override
	protected void onRemoveDetailsCommandAction(MissionExecuteeDto dto) throws Exception {
		Faces.redirect(Faces.getServletContext().getContextPath()+"/"+_href(dto,webConstantResources.getRequestParamCrudDelete()));
	}
	
	@Override
	public Boolean canRemove(MissionExecuteeDto dto) {
		return true;
	}
	
	@Override
	public Boolean actionable(MissionExecuteeDto dto){
		return Code.NATURE_OPERATION_SAISIE.equals(dto.getNatureOperationCode()) || Code.NATURE_OPERATION_SOUMISSION.equals(dto.getNatureOperationCode()) 
			|| Code.NATURE_OPERATION_DEPOT.equals(dto.getNatureOperationCode());
	}
	
	@Override
	public String actionLabel(MissionExecuteeDto dto){
		
		switch(dto.getNatureOperationCode()){
		case Code.NATURE_OPERATION_SAISIE:case Code.NATURE_OPERATION_SOUMISSION:return "Modifier";
		case Code.NATURE_OPERATION_TRANSMISSION_SAISIE_A_ORGANISATEUR: return "Completer";
		case Code.NATURE_OPERATION_DEPOT: return "Déposer";
		}
		return null;
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
	protected Object hrefObjectOutcome(MissionExecuteeDto entity) {
		return entity.getMissionExecutee();
	}
}
