package ci.gouv.budget.solde.sigdcp.service.indemnite;

import ci.gouv.budget.solde.sigdcp.model.identification.Fonction;
import ci.gouv.budget.solde.sigdcp.model.identification.Grade;
import ci.gouv.budget.solde.sigdcp.model.indemnite.GroupeMission;

public interface GroupeMissionService extends AbstractGroupeService<GroupeMission> {
	
	GroupeMission findByFonctionOrGrade(Fonction fonction,Grade grade);
	 
}
 