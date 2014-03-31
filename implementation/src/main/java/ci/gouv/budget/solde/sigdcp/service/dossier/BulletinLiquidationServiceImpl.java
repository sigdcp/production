package ci.gouv.budget.solde.sigdcp.service.dossier;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import ci.gouv.budget.solde.sigdcp.dao.dossier.BulletinLiquidationDao;
import ci.gouv.budget.solde.sigdcp.dao.dossier.TraitementDossierDao;
import ci.gouv.budget.solde.sigdcp.model.Code;
import ci.gouv.budget.solde.sigdcp.model.dossier.BordereauTransmission;
import ci.gouv.budget.solde.sigdcp.model.dossier.BulletinLiquidation;
import ci.gouv.budget.solde.sigdcp.model.dossier.BulletinLiquidationDto;
import ci.gouv.budget.solde.sigdcp.model.dossier.NatureDeplacement;
import ci.gouv.budget.solde.sigdcp.model.dossier.NatureOperation;
import ci.gouv.budget.solde.sigdcp.model.dossier.Operation;
import ci.gouv.budget.solde.sigdcp.model.dossier.OperationValidationConfig;
import ci.gouv.budget.solde.sigdcp.model.dossier.ValidationType;
import ci.gouv.budget.solde.sigdcp.service.ServiceException;

@Stateless
public class BulletinLiquidationServiceImpl extends AbstractPieceProuiteServiceImpl<BulletinLiquidation,BulletinLiquidationDto> implements BulletinLiquidationService , Serializable {

	private static final long serialVersionUID = -7601857525393731774L;
	
	@Inject private TraitementDossierDao traitementDossierDao;
	@Inject private DossierService dossierService;
	@Inject private OperationService operationService;
	@Inject private TraitementPieceProduiteService traitementPieceProduiteService;
	
	@Inject
	public BulletinLiquidationServiceImpl(BulletinLiquidationDao dao) {
		super(dao); 
	}
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	@Override
	public Collection<BulletinLiquidation> findByNatureDeplacement(NatureDeplacement natureDeplacement) {
		return null;
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	@Override
	public Collection<BulletinLiquidation> findByBordereau(BordereauTransmission bordereauTransmission) {
		return ((BulletinLiquidationDao)dao).readByBordereau(bordereauTransmission);
	} 

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	@Override
	public BulletinLiquidationDto findByNumero(String numero) {
		return dto(((BulletinLiquidationDao)dao).readByNumero(numero), null);
	}
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	@Override
	public Collection<BulletinLiquidationDto> findATraiterByNatureDeplacementsByNatureOperationId(Collection<NatureDeplacement> natureDeplacements, String natureOperationId) {
		NatureOperation natureOperation = genericDao.readByClass(NatureOperation.class, natureOperationId);
		Collection<BulletinLiquidation>  liste;
		Collection<BulletinLiquidationDto> dtos = new LinkedHashSet<>();
		String codeOpPrec = natureOperation.getPrecedent().getCode();
		String statutPrecAcc = operationValidationConfigDao.readByNatureOperationIdByValidationType(codeOpPrec, ValidationType.ACCEPTER).getStatutResultat().getCode();
		OperationValidationConfig differerConfig = operationValidationConfigDao.readByNatureOperationIdByValidationType(natureOperationId, ValidationType.DIFFERER);
	
		//if(Code.NATURE_OPERATION_PAIEMENT_PRISE_EN_CHARGE.equals(natureOperationId)){
			//bordereaux avec statut transmis et differe
			liste = ((BulletinLiquidationDao)dao).readByNatureDeplacementsByNatureOperationIdByStatutId(natureDeplacements,codeOpPrec,statutPrecAcc);
			if(differerConfig!=null)
				liste.addAll(((BulletinLiquidationDao)dao).readByNatureDeplacementsByNatureOperationIdByStatutId(natureDeplacements,natureOperationId,differerConfig.getStatutResultat().getCode() ));
		//}else{
		//	bordereauTransmissions = ((BordereauTransmissionDao)dao).readByNatureOperationIdByStatutId(natureOperation.getOperationPrecedente().getCode(),natureOperation.getOperationPrecedente().getStatutResultatAccepte().getCode() );
		//}
		
		switch(natureOperationId){
		case Code.NATURE_OPERATION_REGLER_BL: 
			liste = ((BulletinLiquidationDao)dao).readByNatureDeplacementsByBordereauNatureOperationIdByBordereauStatutId(natureDeplacements,Code.NATURE_OPERATION_MISE_EN_REGLEMENT,Code.STATUT_ACCEPTE);break;
		}
			
		if(Code.NATURE_OPERATION_VALIDATION_BL.equals(natureOperationId)){
			liste.addAll(((BulletinLiquidationDao)dao).readByDernierTraitementIsNull(natureDeplacements));
		}
			
		Long configCount = operationValidationConfigDao.countByNatureOperationId(natureOperationId);
		
		for(BulletinLiquidation piece : liste){
			BulletinLiquidationDto dto = dto(piece, natureOperationId);
			dtos.add(dto);
			if(configCount==1){
				dto.getTraitement().setValidationType(ValidationType.ACCEPTER);
			}
		}
		// on applique la notion de disponible disponible
		if(Code.NATURE_OPERATION_ETABLISSEMENT_BTBL.equals(natureOperationId)){
			NatureDeplacement natureDeplacement = natureDeplacements.iterator().next();
			if(Code.CATEGORIE_DEPLACEMENT_DEFINITIF.equals(natureDeplacement.getCategorie().getCode())){
				BigDecimal disponible = natureDeplacement.getCategorie().getDisponible();
				Collection<BulletinLiquidationDto> _dtos = new ArrayList<>();
				if(disponible==null || disponible.equals(BigDecimal.ZERO))
					return _dtos;
				for(BulletinLiquidationDto dto : dtos){
					disponible = disponible.subtract(dto.getPiece().getMontant());
					if(disponible.compareTo(BigDecimal.ZERO)>=0){
						_dtos.add(dto);
					}else
						disponible = disponible.add(dto.getPiece().getMontant());
				}
				dtos = _dtos;
			}else if(Code.CATEGORIE_DEPLACEMENT_MISSION.equals(natureDeplacement.getCode())){
				return dtos;
			}
		}
		
		return dtos;
	}

	@Override
	public void valider(NatureDeplacement natureDeplacement, String natureOperationCode, Collection<BulletinLiquidationDto> dtos) throws ServiceException {
		//validator.setTraitements(traitements);
		//validator.validate(natureOperation);
		//if(!validator.isSucces())
		//	serviceException(validator.getMessagesAsString());
		
		Operation operation = operationService.creer(natureOperationCode);
		String statutId;
		for(BulletinLiquidationDto dto : dtos){
			//dto.getBordereauTransmission().setDernierTraitement(dto.getTraitement());
			dto.getTraitement().setPieceProduite(dto.getPiece());
			dto.getTraitement().setOperation(operation);
			//debug(dto.getTraitement());
			//debug(operationValidationConfigDao.readByNatureOperationIdByValidationType(natureOperationCode, dto.getTraitement().getValidationType()));
			
			statutId = operationValidationConfigDao.readByNatureOperationIdByValidationType(natureOperationCode, dto.getTraitement().getValidationType()).getStatutResultat().getCode();
			//Statut statutCourant = dossierDao.read(dossier.getNumero()).getDernierTraitement().getStatut();
			
			switch(dto.getTraitement().getValidationType()){
			case ACCEPTER:			
				
				break;
			case DIFFERER:

				break;
			case REJETER:
				
				break;
			default:
				break;
			}
			
			traitementPieceProduiteService.creer(operation, dto.getPiece(),dto.getTraitement(),statutId);
			dao.update(dto.getPiece());
			
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public BulletinLiquidationDto dto(BulletinLiquidation bulletinLiquidation,String natureOperationId){
		BulletinLiquidationDto dto = new BulletinLiquidationDto(bulletinLiquidation,dossierService.buildDto(bulletinLiquidation.getDossier(), natureOperationId));
		OperationValidationConfig creerOpConfig = operationValidationConfigDao.readByNatureOperationIdByValidationType(Code.NATURE_OPERATION_ETABLISSEMENT_BL, ValidationType.ACCEPTER);
		dto.setDateCreation(traitementDossierDao.readByDossierByNatureOperationIdByStatutId(bulletinLiquidation.getDossier(), Code.NATURE_OPERATION_ETABLISSEMENT_BL, 
			creerOpConfig.getStatutResultat().getCode()).iterator().next().getOperation().getDate());
		return dto;
	}


}
