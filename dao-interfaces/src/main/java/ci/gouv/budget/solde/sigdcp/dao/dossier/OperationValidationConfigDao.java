package ci.gouv.budget.solde.sigdcp.dao.dossier;

import java.util.Collection;

import ci.gouv.budget.solde.sigdcp.dao.DataAccessObject;
import ci.gouv.budget.solde.sigdcp.model.dossier.OperationValidationConfig;
import ci.gouv.budget.solde.sigdcp.model.dossier.ValidationType;

public interface OperationValidationConfigDao extends DataAccessObject<OperationValidationConfig,Long> {

	OperationValidationConfig readByNatureOperationIdByValidationType(String natureOperationId,ValidationType validationType);
	
	Collection<OperationValidationConfig> readByNatureOperationId(String natureOperationId);
	
	Long countByNatureOperationId(String natureOperationId);
}
