package ci.gouv.budget.solde.sigdcp.service.dossier;

import java.io.Serializable;
import java.util.Date;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import ci.gouv.budget.solde.sigdcp.dao.GenericDao;
import ci.gouv.budget.solde.sigdcp.dao.dossier.OperationDao;
import ci.gouv.budget.solde.sigdcp.model.dossier.NatureOperation;
import ci.gouv.budget.solde.sigdcp.model.dossier.Operation;
import ci.gouv.budget.solde.sigdcp.service.DefaultServiceImpl;
import ci.gouv.budget.solde.sigdcp.service.ServiceExceptionType;

public class OperationServiceImpl extends DefaultServiceImpl<Operation,Long> implements OperationService , Serializable {

	private static final long serialVersionUID = -7601857525393731774L;
	
	@Inject private GenericDao genericDao;
	
	@Inject
	public OperationServiceImpl(OperationDao dao) {
		super(dao);
	}

	@Transactional(value=TxType.REQUIRED)
	@Override
	public Operation creer(String natureId) {
		NatureOperation natureOperation = genericDao.readByClass(NatureOperation.class, natureId);
		if(natureOperation==null)
			serviceException(ServiceExceptionType.OPERATION_INCONNUE);
		Operation operation = new Operation(new Date(), natureOperation, utilisateur());
		((OperationDao)dao).create(operation);
		return operation;
	}
	
}
