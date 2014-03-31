package ci.gouv.budget.solde.sigdcp.model.identification;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ci.gouv.budget.solde.sigdcp.model.AbstractModel;

@Entity @Getter @Setter 
@AllArgsConstructor @NoArgsConstructor @EqualsAndHashCode(of="matricule",callSuper=false)
public class AgentEtatReference extends AbstractModel<String> implements Serializable {

	private static final long serialVersionUID = 4242485379623860202L;

	@Id private String matricule;
	
	@Column(nullable=false)
	private String nomPrenoms;
	
	@Temporal(TemporalType.DATE)
	@Column(nullable=false)
	private Date dateNaissance;
	
	@Temporal(TemporalType.DATE)
	@Column(nullable=true)
	private Date dateRecrutement;
	
	@Temporal(TemporalType.DATE)
	@Column(nullable=true)
	private Date datePriseService;
	
}
