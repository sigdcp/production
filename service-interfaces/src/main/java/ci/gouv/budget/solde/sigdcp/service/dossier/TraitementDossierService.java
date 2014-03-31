package ci.gouv.budget.solde.sigdcp.service.dossier;

import java.util.Collection;

import ci.gouv.budget.solde.sigdcp.model.dossier.Dossier;
import ci.gouv.budget.solde.sigdcp.model.dossier.NatureDeplacement;
import ci.gouv.budget.solde.sigdcp.model.dossier.Operation;
import ci.gouv.budget.solde.sigdcp.model.dossier.PieceProduite;
import ci.gouv.budget.solde.sigdcp.model.dossier.Statut;
import ci.gouv.budget.solde.sigdcp.model.dossier.TraitementDossier;
import ci.gouv.budget.solde.sigdcp.model.identification.AgentEtat;

public interface TraitementDossierService extends AbstractTraitementService<TraitementDossier> {
	
	TraitementDossier creer(Operation operation,Dossier dossier,TraitementDossier traitement,String statutId);
	
	Collection<TraitementDossier> findByDossier(Dossier dossier);
	
	Collection<TraitementDossier> findByAgentEtat(AgentEtat agentEtat);
	
	TraitementDossier findByPieceProduite(PieceProduite pieceProduite);
	
	Collection<TraitementDossier> findByPieceProduiteTypeId(String typePieceProduiteId);
	
	Collection<TraitementDossier> findByNatureDeplacementByStatut(NatureDeplacement natureDeplacement,Statut statut);
	
	/**
	 * Ramene les traitements les plus récents
	 * @param categorieDeplacementId 
	 * @param typePieceProduiteId
	 * @return
	 */
	//Collection<Traitement> findByCategorieDeplacementIdByTypePieceProduiteId(String categorieDeplacementId,String typePieceProduiteId);
}
