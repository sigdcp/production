package ci.gouv.budget.solde.sigdcp.dao.dossier;

import java.util.Collection;

import ci.gouv.budget.solde.sigdcp.model.dossier.NatureDeplacement;
import ci.gouv.budget.solde.sigdcp.model.dossier.PieceProduite;

public interface AbstractPieceProduiteDao<PIECE_PRODUITE extends PieceProduite> extends AbstractDocumentDao<PIECE_PRODUITE> {

	Collection<PIECE_PRODUITE> readByNatureDeplacementsByNatureOperationIdByStatutId(Collection<NatureDeplacement> natureDeplacements,String natureOperationId, String statutId);
	
}
