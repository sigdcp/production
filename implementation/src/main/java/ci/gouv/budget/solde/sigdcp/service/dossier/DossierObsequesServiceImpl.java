package ci.gouv.budget.solde.sigdcp.service.dossier;

import java.io.Serializable;
import java.util.Collection;

import javax.inject.Inject;

import ci.gouv.budget.solde.sigdcp.dao.dossier.DossierObsequesDao;
import ci.gouv.budget.solde.sigdcp.dao.identification.PersonneDao;
import ci.gouv.budget.solde.sigdcp.model.Code;
import ci.gouv.budget.solde.sigdcp.model.dossier.Deplacement;
import ci.gouv.budget.solde.sigdcp.model.dossier.DossierObseques;
import ci.gouv.budget.solde.sigdcp.model.dossier.PieceJustificative;
import ci.gouv.budget.solde.sigdcp.model.dossier.TypeDepense;
import ci.gouv.budget.solde.sigdcp.service.ServiceException;
import ci.gouv.budget.solde.sigdcp.service.utils.validaton.DossierObsequesValidator;

public class DossierObsequesServiceImpl extends AbstractDossierServiceImpl<DossierObseques> implements DossierObsequesService,Serializable {
	
	private static final long serialVersionUID = -7765679080076677680L;
	
	@Inject private DossierObsequesValidator dossierObsequesValidator;
	@Inject private PersonneDao personneDao;
	
	@Inject
	public DossierObsequesServiceImpl(DossierObsequesDao dao) {
		super(dao);
	}
	/*
	@Override
	protected void validationSaisie(DossierObseques dossier,Collection<PieceJustificative> pieceJustificatives, Boolean soumission) throws ServiceException {
		dossierObsequesValidator.setPieceJustificatives(pieceJustificatives);
		dossierObsequesValidator.setSoumission(soumission);
		if(!dossierObsequesValidator.validate(dossier).isSucces())
			throw new ServiceException(dossierObsequesValidator.getMessagesAsString());
	}*/
	
	@Override
	protected void onDaoCreate(DossierObseques dossier) {
		//est ce que l'ayant droit est connu ?
		if(!personneDao.exist(dossier.getBeneficiaire().getAyantDroit().getId())){
			personneDao.create(dossier.getBeneficiaire().getAyantDroit());
		}
		//est ce que le defunt est connu ?
		if(!personneDao.exist(dossier.getBeneficiaire().getId())){
			personneDao.create(dossier.getBeneficiaire());
		}
		
		super.onDaoCreate(dossier);
	}
	
	@Override
	protected DossierObseques createDossier() {
		return new DossierObseques(new Deplacement(genericDao.readByClass(TypeDepense.class, String.class, Code.TYPE_DEPENSE_PRISE_EN_CHARGE)));
	}

}
