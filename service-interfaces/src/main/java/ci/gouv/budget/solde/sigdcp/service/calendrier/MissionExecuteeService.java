package ci.gouv.budget.solde.sigdcp.service.calendrier;

import java.util.Collection;

import ci.gouv.budget.solde.sigdcp.model.calendrier.MissionExecutee;
import ci.gouv.budget.solde.sigdcp.model.calendrier.MissionExecuteeDto;
import ci.gouv.budget.solde.sigdcp.model.dossier.DossierDto;
import ci.gouv.budget.solde.sigdcp.model.dossier.DossierMission;
import ci.gouv.budget.solde.sigdcp.model.dossier.PieceJustificative;
import ci.gouv.budget.solde.sigdcp.service.AbstractService;
import ci.gouv.budget.solde.sigdcp.service.ActionType;

public interface MissionExecuteeService extends AbstractService<MissionExecutee,Long> {

	void enregistrer(ActionType actionType,MissionExecutee missionExecutee,PieceJustificative communication,Collection<DossierDto> dossierDtos);
	
	MissionExecuteeDto findSaisieByNumero(Long id);
	
	Collection<MissionExecuteeDto> findMissionOrganisees();
	
	MissionExecutee findByDossier(DossierMission dossierMission);
	
	void transmettreDossier(Collection<DossierMission> dossiers);
	
}
