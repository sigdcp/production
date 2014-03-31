package ci.gouv.budget.solde.sigdcp.service.utils.validaton;

import lombok.Getter;

import org.apache.commons.lang3.StringUtils;

@Getter
public enum ValidationExceptionType {
	
	_DEFAULT(),
	MATRICULE_FORMAT(),
	DATE_NAISSANCE_MAJEUR(),
	PASSWORD(),
	PASSWORD_CONFIRMATION(),
	USERNAME_UNIQUE(),
	DATE_PRISE_SERVICE(),
	DATE_MISE_STAGE(),
	EMAIL_OCCUPE(),
	
	DEPART_RETRAITE_ENCOURS("departretraite.encours"),
	DEPART_RETRAITE_TRAITE("departretraite.valide"),
	
	FICHIER_EXTENSION("fichier.extension"),
	FICHIER_TAILLE("fichier.taille"),
	
	
	/*---------------------------------------------------------------------------------------*/
	;
	
	private String id;

	private ValidationExceptionType() {
		this(null);
	}
	
	private ValidationExceptionType(String id) {
		this.id = "error.validation."+(StringUtils.isEmpty(id)?"_default":id);
	}
	
	@Override
	public String toString() {
		return id;
	}
}
