/**
*    Syst�me Int�gr� de Gestion des D�penses Communes de Personnel - SIGDCP
*
*    Mod�le M�tier
*
**/


package ci.gouv.budget.solde.sigdcp.model.dossier;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.Setter;
import ci.gouv.budget.solde.sigdcp.model.AbstractModel;
import ci.gouv.budget.solde.sigdcp.model.identification.Role;

@Getter @Setter 
@Entity 
public class OperationValidationConfig  extends AbstractModel<Long>  implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue
	private Long id;
	
	@ManyToOne
	private NatureOperation natureOperation;
	
	@Enumerated(EnumType.STRING)
	private ValidationType validationType;
	
	@OneToMany
	private Set<Role> roles = new HashSet<>();
	
	private Boolean enabled = Boolean.TRUE;
	
	@ManyToOne
	private Statut statutResultat;
	
	public OperationValidationConfig() {}

	public OperationValidationConfig(NatureOperation natureOperation, ValidationType validationType, Statut statutResultat,Role...roles) {
		super();
		this.natureOperation = natureOperation;
		this.validationType = validationType;
		this.statutResultat = statutResultat;
		if(roles!=null)
			for(Role role : roles)
				this.roles.add(role);
	}
	
	

}