/**
*    Syst�me Int�gr� de Gestion des D�penses Communes de Personnel - SIGDCP
*
*    Mod�le M�tier
*
**/


package ci.gouv.budget.solde.sigdcp.model.dossier;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import ci.gouv.budget.solde.sigdcp.model.DynamicEnumeration;

@Getter @Setter 
@Entity
public class CategorieDeplacement  extends DynamicEnumeration  implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private BigDecimal disponible;
	
	@NotNull @Column(nullable=false)
	private Integer nombreJourIndemniteJournaliere = 2;
	
	@NotNull @Column(nullable=false,precision=10,scale=2)
	private BigDecimal _c33_5 = new BigDecimal("33.5");
	
	@NotNull @Column(nullable=false,precision=10,scale=2)
	private BigDecimal _c1000 = new BigDecimal("1000");
	
	public CategorieDeplacement() {}

	public CategorieDeplacement(String code, String libelle) {
		super(code, libelle);
	}
	
	
}