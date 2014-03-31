package ci.gouv.budget.solde.sigdcp.service.dossier;

import java.io.Serializable;

import javax.inject.Inject;

import ci.gouv.budget.solde.sigdcp.dao.dossier.NatureOperationDao;
import ci.gouv.budget.solde.sigdcp.dao.dossier.OperationValidationConfigDao;
import ci.gouv.budget.solde.sigdcp.model.dossier.NatureOperation;
import ci.gouv.budget.solde.sigdcp.model.dossier.NatureOperationDto;
import ci.gouv.budget.solde.sigdcp.model.dossier.OperationValidationConfig;
import ci.gouv.budget.solde.sigdcp.model.dossier.ValidationType;
import ci.gouv.budget.solde.sigdcp.service.DefaultServiceImpl;

public class NatureOperationServiceImpl extends DefaultServiceImpl<NatureOperation, String> implements NatureOperationService , Serializable {

	private static final long serialVersionUID = -7601857525393731774L;
	
	@Inject private OperationValidationConfigDao operationValidationConfigDao;
	
	@Inject
	public NatureOperationServiceImpl(NatureOperationDao dao) {
		super(dao);
	}

	@Override
	public NatureOperationDto findDtoById(String id) {
		NatureOperationDto dto = new NatureOperationDto(((NatureOperationDao)dao).read(id));
		dto.setConfigCount(operationValidationConfigDao.countByNatureOperationId(id));
		dto.setOperationValidationConfigAccepte(operationValidationConfigDao.readByNatureOperationIdByValidationType(id, ValidationType.ACCEPTER));
		dto.setOperationValidationConfigAcceptePrecedent(operationValidationConfigDao.readByNatureOperationIdByValidationType(dto.getNatureOperation().getPrecedent().getCode(), ValidationType.ACCEPTER));
		for(OperationValidationConfig config : operationValidationConfigDao.readByNatureOperationId(id))
			if(Boolean.TRUE.equals(config.getEnabled()))
				dto.getValidationTypes().add(config.getValidationType());
		return dto;
	}
}
