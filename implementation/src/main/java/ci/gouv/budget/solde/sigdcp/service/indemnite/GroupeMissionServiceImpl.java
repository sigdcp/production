package ci.gouv.budget.solde.sigdcp.service.indemnite;

import java.io.Serializable;

import javax.inject.Inject;

import ci.gouv.budget.solde.sigdcp.dao.dossier.GroupeMissionDao;
import ci.gouv.budget.solde.sigdcp.model.identification.Fonction;
import ci.gouv.budget.solde.sigdcp.model.identification.Grade;
import ci.gouv.budget.solde.sigdcp.model.indemnite.GroupeMission;

public class GroupeMissionServiceImpl extends AbstractGroupeServiceImpl<GroupeMission> implements GroupeMissionService,Serializable {
	
	private static final long serialVersionUID = -7765679080076677680L;
	
	@Inject
	public GroupeMissionServiceImpl(GroupeMissionDao dao) {
		super(dao);
	}

	@Override
	public GroupeMission findByFonctionOrGrade(Fonction fonction, Grade grade) {
		GroupeMission groupeMission = null;
		if(fonction!=null)
			groupeMission = ((GroupeMissionDao)dao).readByFonction(fonction);
		if(groupeMission==null)
			groupeMission = ((GroupeMissionDao)dao).readByGrade(grade);
		return groupeMission;
	}
	

	
}
