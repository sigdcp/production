package ci.gouv.budget.solde.sigdcp.dao.dossier;

import java.util.Collection;

import ci.gouv.budget.solde.sigdcp.model.dossier.BordereauTransmission;
import ci.gouv.budget.solde.sigdcp.model.dossier.BulletinLiquidation;
import ci.gouv.budget.solde.sigdcp.model.dossier.NatureDeplacement;

public interface BulletinLiquidationDao extends AbstractPieceProduiteDao<BulletinLiquidation> {

	Collection<BulletinLiquidation> readByDernierTraitementIsNull(Collection<NatureDeplacement> natureDeplacements);
	
	Collection<BulletinLiquidation> readByBordereau(BordereauTransmission bordereauTransmission);

	BulletinLiquidation readByNumero(String numero);
	
	Collection<BulletinLiquidation> readByNatureDeplacementsByBordereauNatureOperationIdByBordereauStatutId(Collection<NatureDeplacement> natureDeplacements,String natureOperationId,String statutId);
	
}
