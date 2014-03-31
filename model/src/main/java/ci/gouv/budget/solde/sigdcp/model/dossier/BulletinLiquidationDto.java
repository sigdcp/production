package ci.gouv.budget.solde.sigdcp.model.dossier;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BulletinLiquidationDto extends AbstractPieceProduiteDto<BulletinLiquidation> implements Serializable {

	private static final long serialVersionUID = -200004662036706414L;
	
	private DossierDto dossierDto;

	public BulletinLiquidationDto(BulletinLiquidation bulletinLiquidation,DossierDto dossierDto) {
		super(bulletinLiquidation);
		this.dossierDto = dossierDto;
	}
	
	public Dossier getDossier(){
		return dossierDto==null?null:dossierDto.getDossier();
	}
	
}
