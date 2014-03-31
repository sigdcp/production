package ci.gouv.budget.solde.sigdcp.dao.dossier;

import java.math.BigDecimal;
import java.util.Collection;

import ci.gouv.budget.solde.sigdcp.dao.DataAccessObject;
import ci.gouv.budget.solde.sigdcp.model.dossier.BulletinLiquidation;
import ci.gouv.budget.solde.sigdcp.model.dossier.BulletinLiquidation.AspectLiquide;
import ci.gouv.budget.solde.sigdcp.model.dossier.Deplacement;
import ci.gouv.budget.solde.sigdcp.model.dossier.Dossier;
import ci.gouv.budget.solde.sigdcp.model.dossier.NatureDeplacement;
import ci.gouv.budget.solde.sigdcp.model.dossier.Statut;
import ci.gouv.budget.solde.sigdcp.model.identification.AgentEtat;
import ci.gouv.budget.solde.sigdcp.model.identification.Personne;

public interface AbstractDossierDao<DOSSIER extends Dossier> extends DataAccessObject<DOSSIER,String> {
	
	/**
	 * Ramene le dossier en saisie. Un dossier en saisie est un dossier avec un et un seul statut qui est SAISI
	 * @param agentEtat
	 * @param natureDeplacement
	 * @return
	 */
	DOSSIER readSaisieByPersonneByNatureDeplacement(Personne personne,NatureDeplacement natureDeplacement);
	
	/**
	 * Liste des dossiers appartenant a un agent de l'état
	 * @param personne
	 * @return
	 */
	Collection<DOSSIER> readByAgentEtat(AgentEtat agentEtat);
	
	Collection<DOSSIER> readByAgentEtatAndAyantDroit(AgentEtat agentEtat);
	
	Collection<DOSSIER> readByNatureDeplacementAndStatut(NatureDeplacement natureDeplacement,Statut statut);
	
	Collection<DOSSIER> readByNatureDeplacementsByStatut(Collection<NatureDeplacement> natureDeplacements,Statut statut);
	
	Collection<DOSSIER> readByStatut(Statut statut);
	
	Collection<DOSSIER> readByStatutId(String id);
	
	
	
	Collection<DOSSIER> readByNatureDeplacement(NatureDeplacement natureDeplacement);
	
	Collection<DOSSIER> readByDeplacement(Deplacement deplacement);

	DOSSIER readDernierCreeByAgentEtat(AgentEtat agentEtat);
	
	DOSSIER readByBulletinLiquidation(BulletinLiquidation bulletinLiquidation);
	
	Collection<DOSSIER> readByBordereauId(Long bordereauTransmissionId);
	
	Collection<DOSSIER> readByNatureDeplacementsByNatureOperationIdByStatutId(Collection<NatureDeplacement> natureDeplacements,String natureOperationId,String statutId);
	
	Collection<DOSSIER> readByNatureDeplacementsByNatureOperationIdByStatutIdByDisponible(Collection<NatureDeplacement> natureDeplacements,String natureOperationId,String statutId,BigDecimal disponible);
	
	Collection<DOSSIER> readCourrierNonNullByNatureDeplacementsByNatureOperationIdByStatutId(Collection<NatureDeplacement> natureDeplacements,String natureOperationId,String statutId);
	
	//Collection<DOSSIER> readConformeBulletinNullByNatureDeplacements(Collection<NatureDeplacement> natureDeplacements);
	
	Collection<DOSSIER> readBulletinLiquidationExisteLiquidableByNatureDeplacements(Collection<NatureDeplacement> natureDeplacements,AspectLiquide aspectLiquide);
	
	Collection<DOSSIER> readByNatureDeplacementsByAspectLiquideNotExist(Collection<NatureDeplacement> natureDeplacements,AspectLiquide aspectLiquide);
} 
   