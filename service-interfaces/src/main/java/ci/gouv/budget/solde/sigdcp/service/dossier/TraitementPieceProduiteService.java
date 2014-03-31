package ci.gouv.budget.solde.sigdcp.service.dossier;

import ci.gouv.budget.solde.sigdcp.model.dossier.Operation;
import ci.gouv.budget.solde.sigdcp.model.dossier.PieceProduite;
import ci.gouv.budget.solde.sigdcp.model.dossier.TraitementPieceProduite;

public interface TraitementPieceProduiteService extends AbstractTraitementService<TraitementPieceProduite> {
	
	TraitementPieceProduite creer(Operation operation,PieceProduite pieceProduite,TraitementPieceProduite traitement,String statutId);
	
}
