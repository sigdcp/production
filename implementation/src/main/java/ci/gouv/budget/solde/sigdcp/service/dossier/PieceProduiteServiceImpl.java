package ci.gouv.budget.solde.sigdcp.service.dossier;

import java.io.Serializable;
import java.util.Collection;

import javax.inject.Inject;

import ci.gouv.budget.solde.sigdcp.dao.dossier.PieceProduiteDao;
import ci.gouv.budget.solde.sigdcp.model.dossier.AbstractPieceProduiteDto;
import ci.gouv.budget.solde.sigdcp.model.dossier.NatureDeplacement;
import ci.gouv.budget.solde.sigdcp.model.dossier.PieceProduite;
import ci.gouv.budget.solde.sigdcp.service.ServiceException;

public class PieceProduiteServiceImpl extends AbstractPieceProuiteServiceImpl<PieceProduite,AbstractPieceProduiteDto<PieceProduite>> implements PieceProduiteService , Serializable {

	private static final long serialVersionUID = -7601857525393731774L;
	
	@Inject
	public PieceProduiteServiceImpl(PieceProduiteDao dao) {
		super(dao);
	}
	 
	@Override
	public Collection<PieceProduite> findByTypeId(String typeId) {
		return ((PieceProduiteDao)dao).readByTypeId(typeId);
	}
	
	@Override
	public Collection<PieceProduite> findByCategorieIdByTypePieceId(String cdid, String typeId) {
		return null;
	}
	
	@Override
	public void valider(NatureDeplacement natureDeplacement,String natureOperationCode, Collection<AbstractPieceProduiteDto<PieceProduite>> dtos) throws ServiceException {}

	@Override
	public Collection<AbstractPieceProduiteDto<PieceProduite>> findATraiterByNatureDeplacementsByNatureOperationId(Collection<NatureDeplacement> natureDeplacements, String natureOperationId) {
		return null;
	}

}
