package ci.gouv.budget.solde.sigdcp.service.dossier;

import java.util.Collection;

import ci.gouv.budget.solde.sigdcp.model.dossier.AbstractPieceProduiteDto;
import ci.gouv.budget.solde.sigdcp.model.dossier.NatureDeplacement;
import ci.gouv.budget.solde.sigdcp.model.dossier.PieceProduite;
import ci.gouv.budget.solde.sigdcp.service.ServiceException;

public interface AbstractPieceProduiteService<PIECE_PRODUITE extends PieceProduite,DTO extends AbstractPieceProduiteDto<PIECE_PRODUITE>> extends AbstractDocumentService<PIECE_PRODUITE> {

	Collection<DTO> findATraiterByNatureDeplacementsByNatureOperationId(Collection<NatureDeplacement> natureDeplacements,String natureOperationId);
	
	void valider(NatureDeplacement natureDeplacement,String natureOperationCode,Collection<DTO> dtos)  throws ServiceException;
	
}
