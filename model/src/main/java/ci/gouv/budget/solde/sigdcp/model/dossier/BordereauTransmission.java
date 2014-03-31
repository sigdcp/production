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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter 
@Entity
public class BordereauTransmission  extends PieceProduite  implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@ManyToOne
	private NatureDeplacement natureDeplacement;
	
	@Column(precision=15,scale=2)
	private BigDecimal montant;
	
	public BordereauTransmission() {
		super();
	}

	public BordereauTransmission(String numero, TypePieceProduite type, Date dateEtablissement) {
		super(numero, type,dateEtablissement);
	}
	
	public BordereauTransmission(BordereauTransmission bordereauTransmission) {
		this(bordereauTransmission.numero,bordereauTransmission.getType(),bordereauTransmission.getDateEtablissement());
		setId(bordereauTransmission.getId());
	}
	
	
}