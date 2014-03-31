package ci.gouv.budget.solde.sigdcp.dao.dossier;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import javax.persistence.NoResultException;

import ci.gouv.budget.solde.sigdcp.dao.JpaDaoImpl;
import ci.gouv.budget.solde.sigdcp.model.Code;
import ci.gouv.budget.solde.sigdcp.model.dossier.BulletinLiquidation;
import ci.gouv.budget.solde.sigdcp.model.dossier.Deplacement;
import ci.gouv.budget.solde.sigdcp.model.dossier.Dossier;
import ci.gouv.budget.solde.sigdcp.model.dossier.NatureDeplacement;
import ci.gouv.budget.solde.sigdcp.model.dossier.Statut;
import ci.gouv.budget.solde.sigdcp.model.dossier.BulletinLiquidation.AspectLiquide;
import ci.gouv.budget.solde.sigdcp.model.identification.AgentEtat;
import ci.gouv.budget.solde.sigdcp.model.identification.Personne;

public abstract class AbstractDossierDaoImpl<DOSSIER extends Dossier> extends JpaDaoImpl<DOSSIER, String> implements AbstractDossierDao<DOSSIER>, Serializable {

	private static final long serialVersionUID = -2609724288199083806L;
	
	@Override
	public DOSSIER readSaisieByPersonneByNatureDeplacement(Personne personne, NatureDeplacement natureDeplacement) {
		try {
			return entityManager.createQuery("SELECT d FROM Dossier d WHERE d.dernierTraitement.operation.nature.code = :noCode"
					+ " AND d.dernierTraitement.operation.effectuePar = :personne AND d.dernierTraitement.dossier.deplacement.nature = :nature", clazz)
					.setParameter("personne", personne)
					.setParameter("nature", natureDeplacement)
					.setParameter("noCode", Code.NATURE_OPERATION_SAISIE)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
		/*
		
		List<Traitement> traitements = entityManager.createQuery("SELECT t FROM Traitement t WHERE t.dossier.deplacement.nature = :nature AND t.operation.effectuePar = :personne ORDER BY t.operation.date ASC"
				, Traitement.class)
				.setParameter("personne", personne)
				.setParameter("nature", natureDeplacement)
				.setMaxResults(2)
				.getResultList();
		
		//return (DOSSIER) (traitements.size()==1?traitements.get(0).getDossier():null);
		*/
	}
	
	@Override
	public Collection<DOSSIER> readByBordereauId(Long bordereauTransmissionId) {
		return entityManager.createQuery("SELECT d FROM Dossier d WHERE EXISTS "
				+ " ( SELECT bl FROM BulletinLiquidation bl WHERE bl.bordereauTransmission.id = :bordereauTransmissionId AND EXISTS "
				+ " ( SELECT td FROM TraitementDossier td WHERE td.dossier = d AND td.pieceProduite = bl ))"
				+ ""
				, clazz)
				.setParameter("bordereauTransmissionId",bordereauTransmissionId)
				.getResultList();
	}
	
	@Override
	public Collection<DOSSIER> readByAgentEtat(AgentEtat agentEtat) {
		return entityManager.createQuery("SELECT d FROM Dossier d WHERE d.beneficiaire = :agentEtat", clazz)
				.setParameter("agentEtat", agentEtat)
				.getResultList();
	}
	
	@Override
	public Collection<DOSSIER> readByAgentEtatAndAyantDroit(AgentEtat agentEtat) {
		return entityManager.createQuery("SELECT d FROM Dossier d WHERE d.beneficiaire = :agentEtat or d.beneficiaire.ayantDroit = :agentEtat", clazz)
				.setParameter("agentEtat", agentEtat)
				.getResultList();
	}
	
	@Override
	public Collection<DOSSIER> readByStatut(Statut statut) {
		return readByStatutId(statut.getCode());
	}
	 
	@Override
	public Collection<DOSSIER> readByStatutId(String id) {
		return entityManager.createQuery("SELECT d FROM Dossier d WHERE d.dernierTraitement.statut.code = :code"
				, clazz)
				.setParameter("code", id)
				.getResultList();
	}
	
	@Override
	public Collection<DOSSIER> readByNatureDeplacement(NatureDeplacement natureDeplacement) {
		return entityManager.createQuery("SELECT d FROM Dossier d "
				+ "WHERE d.deplacement.nature = :nature"
				, clazz)
				.setParameter("nature", natureDeplacement)
				.getResultList();
	}
	
	@Override
	public Collection<DOSSIER> readByDeplacement(Deplacement deplacement) {
		return entityManager.createQuery("SELECT d FROM Dossier d "
				+ "WHERE d.deplacement = :deplacement"
				, clazz)
				.setParameter("deplacement", deplacement)
				.getResultList();
	}
	
	@Override
	public Collection<DOSSIER> readByNatureDeplacementAndStatut(NatureDeplacement natureDeplacement, Statut statut) {
		/*return entityManager.createQuery("SELECT d FROM Dossier d "
				+ "WHERE d.deplacement.nature = :nature AND "
				+ "EXISTS("
				+ " SELECT t FROM Traitement t WHERE t.dossier = d AND t.statut = :statut GROUP BY t.dossier ORDER BY t.operation.date DESC"
				+ ")"
				, clazz)
				//.setParameter("nature", natureDeplacement)
				//.setParameter("statut", statut)
				.getResultList();
				*/
		
		return entityManager.createQuery("SELECT d FROM Dossier d WHERE d.deplacement.nature = :nature AND d.dernierTraitement.statut = :statut ORDER BY d.deplacement.dateCreation ASC"
				, clazz)
				.setParameter("nature", natureDeplacement)
				.setParameter("statut", statut)
				.getResultList();
	}
	
	@Override
	public Collection<DOSSIER> readByNatureDeplacementsByStatut(Collection<NatureDeplacement> natureDeplacements, Statut statut) {		
		return entityManager.createQuery("SELECT d FROM Dossier d WHERE d.deplacement.nature IN :natureDeplacements AND d.dernierTraitement.statut = :statut ORDER BY d.deplacement.dateCreation ASC"
				, clazz)
				.setParameter("natureDeplacements", natureDeplacements)
				.setParameter("statut", statut)
				.getResultList();
	}
	 
	@Override
	public DOSSIER readDernierCreeByAgentEtat(AgentEtat agentEtat) {
		Collection<DOSSIER> list = entityManager.createQuery("SELECT d FROM Dossier d WHERE d.beneficiaire = :agentEtat ORDER BY d.deplacement.dateCreation DESC"
				, clazz)
				.setParameter("agentEtat", agentEtat)
				.setMaxResults(1)
				.getResultList();
		return list.isEmpty()?null:((List<DOSSIER>)list).get(0);
		
	}
	
	@Override
	public Collection<DOSSIER> readByNatureDeplacementsByNatureOperationIdByStatutId(Collection<NatureDeplacement> natureDeplacements,String natureOperationId, String statutId) {
		return entityManager.createQuery("SELECT d FROM Dossier d WHERE d.deplacement.nature IN :natureDeplacements AND d.dernierTraitement.operation.nature.code = :natureOperationId AND "
				+ "d.dernierTraitement.statut.code = :statutId ORDER BY d.deplacement.dateCreation ASC"
				, clazz)
				.setParameter("natureDeplacements", natureDeplacements)
				.setParameter("natureOperationId", natureOperationId)
				.setParameter("statutId", statutId)
				.getResultList();
	}
	
	@Override //TODO ne marche pas encore a revoir
	public Collection<DOSSIER> readByNatureDeplacementsByNatureOperationIdByStatutIdByDisponible(Collection<NatureDeplacement> natureDeplacements,String natureOperationId, String statutId,BigDecimal disponible) {
		return entityManager.createQuery("SELECT d FROM Dossier d WHERE d.deplacement.nature IN :natureDeplacements AND d.dernierTraitement.operation.nature.code = :natureOperationId AND "
				+ "d.dernierTraitement.statut.code = :statutId AND EXISTS ( SELECT td FROM TraitementDossier td WHERE td.dossier = d AND "
				+ "EXISTS ( SELECT bl FROM BulletinLiquidation bl WHERE bl = td.pieceProduite AND bl.montant > :disponible ) ) ORDER BY d.deplacement.dateCreation ASC"
				, clazz)
				.setParameter("natureDeplacements", natureDeplacements)
				.setParameter("natureOperationId", natureOperationId)
				.setParameter("statutId", statutId)
				.setParameter("disponible", disponible)
				.getResultList();
	}
	
	@Override
	public Collection<DOSSIER> readCourrierNonNullByNatureDeplacementsByNatureOperationIdByStatutId(Collection<NatureDeplacement> natureDeplacements,String natureOperationId, String statutId) {
		return entityManager.createQuery("SELECT d FROM Dossier d WHERE d.deplacement.nature IN :natureDeplacements AND d.dernierTraitement.operation.nature.code = :natureOperationId AND "
				+ "d.dernierTraitement.statut.code = :statutId AND d.courrier IS NOT NULL ORDER BY d.deplacement.dateCreation ASC"
				, clazz)
				.setParameter("natureDeplacements", natureDeplacements)
				.setParameter("natureOperationId", natureOperationId)
				.setParameter("statutId", statutId)
				.getResultList();
	}
	/*
	@Override
	public Collection<DOSSIER> readBulletinNullByNatureDeplacements(Collection<NatureDeplacement> natureDeplacements) {
		return entityManager.createQuery("SELECT d FROM Dossier d WHERE d.deplacement.nature IN :natureDeplacements AND d.dernierTraitement.operation.nature.code = :natureOperationId AND "
				+ "d.dernierTraitement.statut.code = :statutId AND d.courrier IS NOT NULL ORDER BY d.deplacement.dateCreation ASC"
				, clazz)
				.setParameter("natureDeplacements", natureDeplacements)
				.setParameter("natureOperationId", natureOperationId)
				.setParameter("statutId", statutId)
				.getResultList();
	}
	*/
	
	@Override
	public DOSSIER readByBulletinLiquidation(BulletinLiquidation bulletinLiquidation) {
		try {
			return entityManager.createQuery("SELECT d FROM Dossier d WHERE EXISTS ( SELECT td FROM TraitementDossier td WHERE td.dossier = d AND td.pieceProduite = :bulletinLiquidation )"
					, clazz)
					.setParameter("bulletinLiquidation", bulletinLiquidation)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	@Override
	public Collection<DOSSIER> readByNatureDeplacementsByAspectLiquideNotExist(Collection<NatureDeplacement> natureDeplacements,AspectLiquide aspectLiquide) {
		return entityManager.createQuery("SELECT d FROM DossierMission d WHERE d.deplacement.nature IN :natureDeplacements AND NOT EXISTS("
				+ " SELECT bl FROM BulletinLiquidation bl WHERE bl.dossier = d AND bl.aspect = :aspectLiquide"
				+ ")"
				, clazz)
				.setParameter("natureDeplacements", natureDeplacements)
				.setParameter("aspectLiquide", aspectLiquide)
				.getResultList();
	}
	
	@Override
	public Collection<DOSSIER> readBulletinLiquidationExisteLiquidableByNatureDeplacements(Collection<NatureDeplacement> natureDeplacements,AspectLiquide aspectLiquide) {
		return entityManager.createQuery("SELECT d FROM Dossier d WHERE d.deplacement.nature IN :natureDeplacements AND EXISTS("
				+ " SELECT bl FROM BulletinLiquidation bl WHERE bl.dossier = d AND bl.aspect = :aspectLiquide"
				+ ") AND ( SELECT SUM(bl.pourcentage) FROM BulletinLiquidation bl WHERE bl.dossier = d AND bl.aspect = :aspectLiquide ) < 1"
				, clazz)
				.setParameter("natureDeplacements", natureDeplacements)
				.setParameter("aspectLiquide", aspectLiquide)
				.getResultList();
	}

}
 