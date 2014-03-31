package ci.gouv.budget.solde.sigdcp.service.dossier;

import ci.gouv.budget.solde.sigdcp.model.dossier.NatureOperation;
import ci.gouv.budget.solde.sigdcp.model.dossier.NatureOperationDto;
import ci.gouv.budget.solde.sigdcp.service.AbstractService;

public interface NatureOperationService extends AbstractService<NatureOperation,String> {

	NatureOperationDto findDtoById(String id);
}
