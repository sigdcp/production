/**
*    Syst�me Int�gr� de Gestion des D�penses Communes de Personnel - SIGDCP
*
*    Mod�le M�tier
*
**/


package ci.gouv.budget.solde.sigdcp.model.calendrier;

import java.io.Serializable;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import ci.gouv.budget.solde.sigdcp.model.dossier.Deplacement;
import ci.gouv.budget.solde.sigdcp.model.dossier.DossierMission;
import ci.gouv.budget.solde.sigdcp.model.identification.AgentEtat;
import ci.gouv.budget.solde.sigdcp.model.indemnite.MontantIndemniteMission;

@Getter @Setter 
@Entity
public class MissionExecutee  extends Mission implements Serializable{

	private static final long serialVersionUID = 1L;

	@NotNull @JoinColumn(nullable=false)
	@ManyToOne private Deplacement deplacement = new Deplacement();
	
	@OneToOne @NotNull @JoinColumn(nullable=false)
	private DossierMission dossierDuResponsable;
	
	@ManyToOne
	private MissionProgrammee programmee;
	
	@Embedded
	private MontantIndemniteMission indemnite = new MontantIndemniteMission();
	
	@ManyToOne
	private AgentEtat organisateur;
	
	public MissionExecutee() {}

	public MissionExecutee(String designation, DossierMission dossierDuResponsable,AgentEtat organisateur) {
		super(designation);
		this.dossierDuResponsable = dossierDuResponsable;
		this.organisateur = organisateur;
	}
	
	
}