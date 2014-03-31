package ci.gouv.budget.solde.sigdcp.service.dossier;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import ci.gouv.budget.solde.sigdcp.dao.dossier.BordereauTransmissionDao;
import ci.gouv.budget.solde.sigdcp.dao.dossier.CategorieDeplacementDao;
import ci.gouv.budget.solde.sigdcp.model.Code;
import ci.gouv.budget.solde.sigdcp.model.dossier.BordereauTransmission;
import ci.gouv.budget.solde.sigdcp.model.dossier.CategorieDeplacement;
import ci.gouv.budget.solde.sigdcp.service.AbstractDynamicEnumerationServiceImpl;
import ci.gouv.budget.solde.sigdcp.service.ServiceException;

public class CategorieDeplacementServiceImpl extends AbstractDynamicEnumerationServiceImpl<CategorieDeplacement> implements CategorieDeplacementService , Serializable {

	private static final long serialVersionUID = -7601857525393731774L;
	
	@Inject private BordereauTransmissionDao bordereauTransmissionDao;
	
	@Inject
	public CategorieDeplacementServiceImpl(CategorieDeplacementDao dao) {
		super(dao);
	}

	@Transactional(value=TxType.REQUIRED)
	@Override
	public void mettreAJourDisponibleTresor(CategorieDeplacement categorieDeplacement) throws ServiceException {
		((CategorieDeplacementDao)dao).update(categorieDeplacement);
	}
	
	@Transactional(value=TxType.REQUIRED)
	@Override
	public void mettreAJourDisponibleTresor(CategorieDeplacement categorieDeplacement, BordereauTransmission bordereauTransmission) throws ServiceException {
		BigDecimal montantEnCours = bordereauTransmissionDao.readMontantEnCoursById(bordereauTransmission.getId());
		switch(montantEnCours.compareTo(bordereauTransmission.getMontant())){
		case 0:
			break;
		case -1:
			categorieDeplacement.setDisponible(categorieDeplacement.getDisponible().subtract(bordereauTransmission.getMontant().subtract(montantEnCours) ));
			break;
		case 1:
			categorieDeplacement.setDisponible(categorieDeplacement.getDisponible().add(bordereauTransmission.getMontant().subtract(montantEnCours) ));
			break;
		}
	}
	
	@Transactional(value=TxType.REQUIRED)
	@Override
	public void debiterDisponible(CategorieDeplacement categorieDeplacement, BigDecimal montant) {
		if(Code.CATEGORIE_DEPLACEMENT_DEFINITIF.equals(categorieDeplacement.getCode())){
			categorieDeplacement.setDisponible(categorieDeplacement.getDisponible().subtract(montant));
			((CategorieDeplacementDao)dao).update(categorieDeplacement);
		}
	}
	
	@Transactional(value=TxType.REQUIRED)
	@Override
	public void crediterDisponible(CategorieDeplacement categorieDeplacement, BigDecimal montant) {
		if(Code.CATEGORIE_DEPLACEMENT_DEFINITIF.equals(categorieDeplacement.getCode())){
			categorieDeplacement.setDisponible(categorieDeplacement.getDisponible().add(montant));
			((CategorieDeplacementDao)dao).update(categorieDeplacement);
		}
	}
	
	

}
