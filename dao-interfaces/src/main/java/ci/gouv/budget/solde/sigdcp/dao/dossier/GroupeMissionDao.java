package ci.gouv.budget.solde.sigdcp.dao.dossier;

import ci.gouv.budget.solde.sigdcp.model.identification.Fonction;
import ci.gouv.budget.solde.sigdcp.model.indemnite.GroupeMission;

public interface GroupeMissionDao extends AbstractGroupeDao<GroupeMission> {

	GroupeMission readByFonction(Fonction fonction);

}
