package ci.gouv.budget.solde.sigdcp.service.dossier;

import java.util.Collection;

import ci.gouv.budget.solde.sigdcp.model.dossier.BordereauTransmission;
import ci.gouv.budget.solde.sigdcp.model.dossier.BulletinLiquidation;
import ci.gouv.budget.solde.sigdcp.model.dossier.BulletinLiquidationDto;
import ci.gouv.budget.solde.sigdcp.model.dossier.NatureDeplacement;

public interface BulletinLiquidationService extends AbstractPieceProduiteService<BulletinLiquidation, BulletinLiquidationDto> {

	//public void regler(Collection<BulletinLiquidation> bulletinLiquidations) throws ServiceException ;
	
	public Collection<BulletinLiquidation> findByNatureDeplacement(NatureDeplacement natureDeplacement);
	
	Collection<BulletinLiquidation> findByBordereau(BordereauTransmission bordereauTransmission);
	
	BulletinLiquidationDto findByNumero(String numero);

	BulletinLiquidationDto dto(BulletinLiquidation bulletinLiquidation,String natureOperationId);
	
}
