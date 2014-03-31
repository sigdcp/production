/**
*    Syst�me Int�gr� de Gestion des D�penses Communes de Personnel - SIGDCP
*
*    Mod�le M�tier
*
**/


package ci.gouv.budget.solde.sigdcp.model.dossier;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ci.gouv.budget.solde.sigdcp.model.AbstractModel;
import ci.gouv.budget.solde.sigdcp.model.identification.AgentEtat;
import ci.gouv.budget.solde.sigdcp.model.identification.Grade;
import ci.gouv.budget.solde.sigdcp.model.identification.Section;

@Getter @Setter 
@Entity @EqualsAndHashCode(of={"numero"},callSuper=false)
@Inheritance(strategy=InheritanceType.JOINED)
public class Dossier  extends AbstractModel<String>  implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id private String numero;
	
	@Embedded private Courrier courrier = new Courrier();
	
	@Temporal(TemporalType.TIMESTAMP) private Date datePriseService;
	
	@JoinColumn(nullable=false)
	//@NotNull
	@ManyToOne private Deplacement deplacement;
	
	/**
	 * Le grade au moment de la demande
	 */
	@ManyToOne private Grade grade;
	
	@JoinColumn(nullable=false)
	//@NotNull
	@ManyToOne private AgentEtat beneficiaire;
	
	/**
	 * Le service dans lequel il est au moment ou il fait la demande
	 */
	@ManyToOne
	private Section service;
	
	/**
	 * Le dernier traitement qu'a subit le dossier
	 */
	@ManyToOne
	private TraitementDossier dernierTraitement;
	
	/**
	 * Le montant total à payer pour l'indemnisation de ce dossier
	 */
	private BigDecimal montantIndemnisation;
	
	public Dossier() {}

	public Dossier(String numero, Courrier courrier,Date datePriseService, Deplacement deplacement,Grade grade, AgentEtat beneficiaire) {
		super();
		this.numero = numero;
		this.courrier=courrier;
		this.datePriseService = datePriseService;
		this.deplacement = deplacement;
		this.grade = grade;
		this.beneficiaire = beneficiaire;
	}

	public Dossier(Deplacement deplacement) {
		super();
		this.deplacement = deplacement;
	}
	
	
	
}