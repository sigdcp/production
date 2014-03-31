/**
*    Syst�me Int�gr� de Gestion des D�penses Communes de Personnel - SIGDCP
*
*    Mod�le M�tier
*
**/


package ci.gouv.budget.solde.sigdcp.model.indemnite;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import ci.gouv.budget.solde.sigdcp.model.AbstractModel;

@Getter @Setter 
@Entity
public class IndemniteTranche  extends AbstractModel<Long>  implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue
	private Long id;
	
	private String libelle;
	
	@Column(precision=10,scale=2,nullable=false)
	@NotNull
	private BigDecimal montant;
	
	@Column(precision=10,scale=2,nullable=false)
	@NotNull
	private BigDecimal intervalleMin;
	
	@Column(precision=10,scale=2,nullable=false)
	@NotNull
	private BigDecimal intervalleMax;
	
	@Enumerated
	@Column(nullable=false) @NotNull
	private TypeIndemniteTranche type;

	public IndemniteTranche() {}
	
	public IndemniteTranche(BigDecimal montant, BigDecimal intervalleMin, BigDecimal intervalleMax, TypeIndemniteTranche type) {
		super();
		this.montant = montant;
		this.intervalleMin = intervalleMin;
		this.intervalleMax = intervalleMax;
		this.type = type;
	}
	
	
	
}