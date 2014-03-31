package ci.gouv.budget.solde.sigdcp.dao.dossier;

import java.util.Collection;

import ci.gouv.budget.solde.sigdcp.dao.DataAccessObject;
import ci.gouv.budget.solde.sigdcp.model.dossier.Traitement;
import ci.gouv.budget.solde.sigdcp.model.identification.Personne;

public interface AbstractTraitementDao<TRAITEMENT extends Traitement> extends DataAccessObject<TRAITEMENT,Long> {

	Collection<TRAITEMENT> readByEffectuePar(Personne personne);
	
}
