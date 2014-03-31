/**
*    Syst�me Int�gr� de Gestion des D�penses Communes de Personnel - SIGDCP
*
*    Mod�le M�tier
*
**/


package ci.gouv.budget.solde.sigdcp.model.dossier;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;
import ci.gouv.budget.solde.sigdcp.model.identification.AgentEtat;
import ci.gouv.budget.solde.sigdcp.model.identification.Fonction;
import ci.gouv.budget.solde.sigdcp.model.identification.Grade;
import ci.gouv.budget.solde.sigdcp.model.identification.Profession;
import ci.gouv.budget.solde.sigdcp.model.indemnite.MontantIndemniteMission;

@Getter @Setter 
@Entity
public class DossierMission extends Dossier   implements Serializable{

	private static final long serialVersionUID = 1L;

	/**
	 * La fonction du beneficiaire
	 */
	@ManyToOne
	private Fonction fonction;
	
	@ManyToOne
	private Profession profession;
	
	@Embedded
	private MontantIndemniteMission indemnite = new MontantIndemniteMission();
	
	public DossierMission() {
		super();
	}

	public DossierMission(Deplacement deplacement) {
		super();
		setDeplacement(deplacement);
	}
	
	public DossierMission(String numero, Courrier courrier,
			Date datePriseService, Deplacement deplacement, Grade grade,
			AgentEtat beneficiaire, MontantIndemniteMission indemnite) {
		super(numero, courrier, datePriseService, deplacement, grade,
				beneficiaire);
		this.indemnite = indemnite;
	}

}