package ci.gouv.budget.solde.sigdcp.model.indemnite;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @Getter @Setter @NoArgsConstructor @Embeddable
public class IndemniteDetailsDD implements Serializable {

	private static final long serialVersionUID = -1136178932725559566L;

	private String formuleIkp,totalEnLettre;
	private Long nombreEnfant;
	private BigDecimal distance;
	
	@ManyToOne
	private GroupeDD groupe;
	private BigDecimal indemniteKilometrique;
	
	@AttributeOverrides({
		@AttributeOverride(name="formuleBagages",column=@Column(name="fiba")),
		@AttributeOverride(name="formuleJournaliere",column=@Column(name="fija")),
		@AttributeOverride(name="indemniteBagages",column=@Column(name="iba")),
		@AttributeOverride(name="indemniteJournaliere",column=@Column(name="ija"))
	})
	@Embedded private IndemniteDetailsPersonneDD agent = new IndemniteDetailsPersonneDD();
	
	@AttributeOverrides({
		@AttributeOverride(name="formuleBagages",column=@Column(name="fibc")),
		@AttributeOverride(name="formuleJournaliere",column=@Column(name="fijc")),
		@AttributeOverride(name="indemniteBagages",column=@Column(name="ibc")),
		@AttributeOverride(name="indemniteJournaliere",column=@Column(name="ijc"))
	})
	@Embedded private IndemniteDetailsPersonneDD conjoint=new IndemniteDetailsPersonneDD();
	
	@AttributeOverrides({
		@AttributeOverride(name="formuleBagages",column=@Column(name="fibe")),
		@AttributeOverride(name="formuleJournaliere",column=@Column(name="fije")),
		@AttributeOverride(name="indemniteBagages",column=@Column(name="ibe")),
		@AttributeOverride(name="indemniteJournaliere",column=@Column(name="ije"))
	})
	@Embedded private IndemniteDetailsPersonneDD enfants=new IndemniteDetailsPersonneDD();
	
	public BigDecimal getTotal(){
		return agent.getTotal().add(conjoint.getTotal()).add(enfants.getTotal());
	}
	
	@Override
	public String toString() {
		return "\r\n"
				+"I.K.P. = "+indemniteKilometrique+"\r\n"
				+"Agent    : "+agent+"\r\n"
				+"Conjoint : "+conjoint+"\r\n"
				+"Enfant   : "+enfants+"\r\n"
				+"TOTAL    :  "+getTotal()
				;
	}
	
}
