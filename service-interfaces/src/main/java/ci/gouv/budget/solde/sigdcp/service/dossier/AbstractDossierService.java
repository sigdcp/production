package ci.gouv.budget.solde.sigdcp.service.dossier;

import java.math.BigDecimal;
import java.util.Collection;

import ci.gouv.budget.solde.sigdcp.model.dossier.BulletinLiquidation.AspectLiquide;
import ci.gouv.budget.solde.sigdcp.model.dossier.Deplacement;
import ci.gouv.budget.solde.sigdcp.model.dossier.Dossier;
import ci.gouv.budget.solde.sigdcp.model.dossier.DossierDto;
import ci.gouv.budget.solde.sigdcp.model.dossier.NatureDeplacement;
import ci.gouv.budget.solde.sigdcp.model.dossier.Statut;
import ci.gouv.budget.solde.sigdcp.model.identification.AgentEtat;
import ci.gouv.budget.solde.sigdcp.model.identification.Personne;
import ci.gouv.budget.solde.sigdcp.service.AbstractService;
import ci.gouv.budget.solde.sigdcp.service.ActionType;
import ci.gouv.budget.solde.sigdcp.service.ServiceException;

public interface AbstractDossierService<DOSSIER extends Dossier> extends AbstractService<DOSSIER,String> {
	
	void enregistrer(ActionType actionType,DossierDto dossierDto) throws ServiceException;
	
	//void soumettre(DOSSIER dossier,Collection<PieceJustificative> pieceJustificatives) throws ServiceException;
	
	void deposer(Collection<DOSSIER> dossiers) throws ServiceException;
	
	void annuler(Collection<DOSSIER> dossiers) throws ServiceException;
	
	void valider(String natureOperationCode,Collection<DossierDto> dossiers)  throws ServiceException;
	
	//public void liquider(DOSSIER dossier) throws ServiceException;
	
	public DossierDto buildDto(DOSSIER dossier,String natureOperationCode);
	
	/**
	 * Lectures
	 */
	
	DossierDto findSaisieByPersonneByNatureDeplacement(Personne personne,NatureDeplacement natureDeplacement,String numero,String codeNatureOperation);
	
	Collection<DOSSIER> findByNatureDeplacementAndStatut(NatureDeplacement natureDeplacement,Statut statut);
	
	Collection<DOSSIER> findByNatureDeplacementsByStatut(Collection<NatureDeplacement> natureDeplacements,Statut statut);
	
	Collection<DOSSIER> findByStatut(Statut statut);
	
	Collection<DOSSIER> findByStatutId(String id);
	
	//Collection<DOSSIER> findByNatureOperationIdByStatutId(String natureOperationId,String id);
	
	Collection<DOSSIER> findByAgentEtat(AgentEtat agentEtat);
	
	Collection<DossierDto> findByAgentEtatAndAyantDroit(AgentEtat agentEtat);
	
	Collection<DOSSIER> findByNatureDeplacement(NatureDeplacement natureDeplacement);
	
	Collection<DOSSIER> findByDeplacement(Deplacement deplacement);
	
	DOSSIER findDernierCreeByAgentEtat(AgentEtat agentEtat);
	
	Collection<DossierDto> findByBordereauId(Long bordereauId);
	
	//Collection<DOSSIER> findALiquider();
	
	BigDecimal calculerMontantIndemnisation(DOSSIER dossier);
	
	Collection<DossierDto> findATraiterByNatureDeplacementsByNatureOperationId(Collection<NatureDeplacement> natureDeplacements,String natureOperationId);
	
	Collection<DossierDto> findALiquiderByNatureDeplacementsByAspectLiquide(Collection<NatureDeplacement> natureDeplacements,AspectLiquide aspectLiquide);
	
	DossierDto findDtoByDossier(Dossier dossier);
	
	/**
	 * Une message qui oriente sur les taches a faire pour le traitement du dossier
	 * @param dossier
	 * @return
	 */
	String findInstructions(DOSSIER dossier);

}
