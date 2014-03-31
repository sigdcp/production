package ci.gouv.budget.solde.sigdcp.service.dossier;

import java.io.Serializable;
import java.util.Collection;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import ci.gouv.budget.solde.sigdcp.dao.DynamicEnumerationDao;
import ci.gouv.budget.solde.sigdcp.dao.dossier.DossierDao;
import ci.gouv.budget.solde.sigdcp.dao.dossier.TraitementDossierDao;
import ci.gouv.budget.solde.sigdcp.model.dossier.Dossier;
import ci.gouv.budget.solde.sigdcp.model.dossier.NatureDeplacement;
import ci.gouv.budget.solde.sigdcp.model.dossier.Operation;
import ci.gouv.budget.solde.sigdcp.model.dossier.PieceProduite;
import ci.gouv.budget.solde.sigdcp.model.dossier.Statut;
import ci.gouv.budget.solde.sigdcp.model.dossier.TraitementDossier;
import ci.gouv.budget.solde.sigdcp.model.identification.AgentEtat;

public class TraitementDossierServiceImpl extends AbstractTraitementServiceImpl<TraitementDossier> implements TraitementDossierService , Serializable {

	private static final long serialVersionUID = -7601857525393731774L;
	
	@Inject private DynamicEnumerationDao dynamicEnumerationDao;
	@Inject private DossierDao dossierDao;
	
	@Inject
	public TraitementDossierServiceImpl(TraitementDossierDao dao) {
		super(dao);
	} 
	
	@Transactional(value=TxType.REQUIRED)
	@Override
	public TraitementDossier creer(Operation operation, Dossier dossier,TraitementDossier traitement , String statutId) {
		if(traitement==null)
			traitement = new TraitementDossier(); 
		traitement.setOperation(operation);
		traitement.setStatut(dynamicEnumerationDao.readByClass(Statut.class, statutId));
		traitement.setDossier(dossier);
		((TraitementDossierDao)dao).create(traitement);
		dossier.setDernierTraitement(traitement);
		dossierDao.update(dossier);
		return traitement;
	}

	@Override
	public Collection<TraitementDossier> findByDossier(Dossier dossier) {
		return ((TraitementDossierDao)dao).readByDossier(dossier);
	}
	
	@Override
	public Collection<TraitementDossier> findByAgentEtat(AgentEtat agentEtat) {
		return ((TraitementDossierDao)dao).readByAgentEtat(agentEtat);
	}
	
	@Override
	public TraitementDossier findByPieceProduite(PieceProduite pieceProduite) {
		return ((TraitementDossierDao)dao).readByPieceProduite(pieceProduite);
	}
	
	@Override
	public Collection<TraitementDossier> findByPieceProduiteTypeId(String typePieceProduiteId) {
		return ((TraitementDossierDao)dao).readByPieceProduiteTypeId(typePieceProduiteId);
	}
	
	@Override
	public Collection<TraitementDossier> findByNatureDeplacementByStatut(NatureDeplacement natureDeplacement, Statut statut) {
		return ((TraitementDossierDao)dao).readByNatureDeplacementByStatut(natureDeplacement, statut);
	}

}
