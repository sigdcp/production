package ci.gouv.budget.solde.sigdcp.service.dossier;

import java.util.Collection;

import ci.gouv.budget.solde.sigdcp.model.dossier.BordereauTransmission;
import ci.gouv.budget.solde.sigdcp.model.dossier.BordereauTransmissionDto;
import ci.gouv.budget.solde.sigdcp.model.dossier.BulletinLiquidationDto;
import ci.gouv.budget.solde.sigdcp.model.dossier.NatureDeplacement;
import ci.gouv.budget.solde.sigdcp.service.ServiceException;

public interface BordereauTransmissionService extends AbstractPieceProduiteService<BordereauTransmission,BordereauTransmissionDto> {

	BordereauTransmissionDto findDtoById(Long id);
	
	void creer(NatureDeplacement natureDeplacement,Collection<BulletinLiquidationDto> bulletinLiquidationDtos) throws ServiceException ;
	
	void modifier(BordereauTransmission bordereauTransmission,Collection<BulletinLiquidationDto> bulletinLiquidationDtos) throws ServiceException ;
	
	void valider(NatureDeplacement natureDeplacement,String natureOperationCode,Collection<BordereauTransmissionDto> dtos) throws ServiceException ;
	
	Collection<BordereauTransmission> findByNatureDeplacement(NatureDeplacement natureDeplacement);
	
	Collection<BordereauTransmission> findByStatutId(String statutId);
	
}
 