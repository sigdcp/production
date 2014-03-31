package ci.gouv.budget.solde.sigdcp.dao.dossier;

import java.util.Collection;

import ci.gouv.budget.solde.sigdcp.model.dossier.Dossier;
import ci.gouv.budget.solde.sigdcp.model.dossier.NatureDeplacement;
import ci.gouv.budget.solde.sigdcp.model.dossier.PieceProduite;
import ci.gouv.budget.solde.sigdcp.model.dossier.Statut;
import ci.gouv.budget.solde.sigdcp.model.dossier.TraitementDossier;
import ci.gouv.budget.solde.sigdcp.model.identification.AgentEtat;

public interface TraitementDossierDao extends AbstractTraitementDao<TraitementDossier> {

	Collection<TraitementDossier> readByDossier(Dossier dossier);
	
	Collection<TraitementDossier> readByDossierByNatureOperationIdByStatutId(Dossier dossier,String natureOperationId,String statutId);
	
	Collection<TraitementDossier> readByAgentEtat(AgentEtat agentEtat);
	
	Collection<TraitementDossier> readByNatureDeplacementByStatut(NatureDeplacement natureDeplacement,Statut statut);
	
	TraitementDossier readByPieceProduite(PieceProduite pieceProduite);
	
	Collection<TraitementDossier> readByPieceProduiteTypeId(String typePieceProduiteId);
	
	Collection<TraitementDossier> readByCategorieDeplacementIdByTypePieceProduiteIdByRecent(String categorieDeplacementId,String typePieceProduiteId,Boolean recent);
}
