package ci.gouv.budget.solde.sigdcp.model.indemnite;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @Getter @Setter @NoArgsConstructor @Embeddable
public class IndemniteDetailsPersonneDD implements Serializable {

	private static final long serialVersionUID = -1136178932725559566L;

	private String formuleBagages,formuleJournaliere;
	private BigDecimal indemniteBagages=new BigDecimal(0),indemniteJournaliere=new BigDecimal(0);
	
	public BigDecimal getTotal(){
		return indemniteBagages.add(indemniteJournaliere);
	}
	
	@Override
	public String toString() {
		return "I.B. = "+indemniteBagages+" , I.J. = "+indemniteJournaliere+" = "+getTotal();
	}
	
}
