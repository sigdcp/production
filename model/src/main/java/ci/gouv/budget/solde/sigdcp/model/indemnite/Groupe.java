/**
*    Syst�me Int�gr� de Gestion des D�penses Communes de Personnel - SIGDCP
*
*    Mod�le M�tier
*
**/


package ci.gouv.budget.solde.sigdcp.model.indemnite;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.Setter;
import ci.gouv.budget.solde.sigdcp.model.DynamicEnumeration;
import ci.gouv.budget.solde.sigdcp.model.identification.Grade;

@Getter @Setter 
@Entity @Inheritance(strategy=InheritanceType.JOINED)
public class Groupe  extends DynamicEnumeration  implements Serializable{

	private static final long serialVersionUID = 1L;

	@OneToMany(fetch=FetchType.EAGER)
	private Collection<Grade> grades = new HashSet<>();
	
	public Groupe() {}

	public Groupe(String code, String libelle) {
		super(code, libelle);
	}
	
	
}