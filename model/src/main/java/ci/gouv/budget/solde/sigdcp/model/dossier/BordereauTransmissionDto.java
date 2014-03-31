package ci.gouv.budget.solde.sigdcp.model.dossier;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BordereauTransmissionDto extends AbstractPieceProduiteDto<BordereauTransmission> implements Serializable {

	private static final long serialVersionUID = -5721536934192986176L;

	private List<BulletinLiquidationDto> bulletinLiquidationDtos;
	
	public BordereauTransmissionDto(BordereauTransmission bordereauTransmission) {
		super(bordereauTransmission);
	}
	
}
