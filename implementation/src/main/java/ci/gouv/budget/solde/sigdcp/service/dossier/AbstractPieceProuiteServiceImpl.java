package ci.gouv.budget.solde.sigdcp.service.dossier;

import java.io.Serializable;

import javax.inject.Inject;

import ci.gouv.budget.solde.sigdcp.dao.dossier.AbstractPieceProduiteDao;
import ci.gouv.budget.solde.sigdcp.dao.dossier.OperationValidationConfigDao;
import ci.gouv.budget.solde.sigdcp.model.dossier.AbstractPieceProduiteDto;
import ci.gouv.budget.solde.sigdcp.model.dossier.PieceProduite;

public abstract class AbstractPieceProuiteServiceImpl<PIECE_PRODUITE extends PieceProduite,DTO extends AbstractPieceProduiteDto<PIECE_PRODUITE>> 
	extends AbstractDocumentServiceImpl<PIECE_PRODUITE> implements AbstractPieceProduiteService<PIECE_PRODUITE,DTO> , Serializable {

	private static final long serialVersionUID = -7601857525393731774L;
	
	@Inject protected OperationValidationConfigDao operationValidationConfigDao;
	
	public AbstractPieceProuiteServiceImpl(AbstractPieceProduiteDao<PIECE_PRODUITE> dao) {
		super(dao);
	}

}
