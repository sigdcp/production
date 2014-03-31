package ci.gouv.budget.solde.sigdcp.service.dossier;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;

import ci.gouv.budget.solde.sigdcp.dao.DynamicEnumerationDao;
import ci.gouv.budget.solde.sigdcp.dao.GenericDao;
import ci.gouv.budget.solde.sigdcp.dao.dossier.BordereauTransmissionDao;
import ci.gouv.budget.solde.sigdcp.dao.dossier.BulletinLiquidationDao;
import ci.gouv.budget.solde.sigdcp.dao.dossier.DocumentDao;
import ci.gouv.budget.solde.sigdcp.dao.dossier.TraitementPieceProduiteDao;
import ci.gouv.budget.solde.sigdcp.model.Code;
import ci.gouv.budget.solde.sigdcp.model.dossier.BordereauTransmission;
import ci.gouv.budget.solde.sigdcp.model.dossier.BordereauTransmissionDto;
import ci.gouv.budget.solde.sigdcp.model.dossier.BulletinLiquidation;
import ci.gouv.budget.solde.sigdcp.model.dossier.BulletinLiquidationDto;
import ci.gouv.budget.solde.sigdcp.model.dossier.CategorieDeplacement;
import ci.gouv.budget.solde.sigdcp.model.dossier.NatureDeplacement;
import ci.gouv.budget.solde.sigdcp.model.dossier.NatureOperation;
import ci.gouv.budget.solde.sigdcp.model.dossier.Operation;
import ci.gouv.budget.solde.sigdcp.model.dossier.OperationValidationConfig;
import ci.gouv.budget.solde.sigdcp.model.dossier.TypePieceProduite;
import ci.gouv.budget.solde.sigdcp.model.dossier.ValidationType;
import ci.gouv.budget.solde.sigdcp.service.ServiceException;

//@Stateless
public class BordereauTransmissionServiceImpl extends AbstractPieceProuiteServiceImpl<BordereauTransmission, BordereauTransmissionDto> implements BordereauTransmissionService, Serializable {

	private static final long				serialVersionUID	= -7601857525393731774L;

	@Inject
	private OperationService				operationService;
	@Inject
	DossierService							dossierService;
	@Inject
	private DynamicEnumerationDao			dynamicEnumerationDao;
	@Inject
	private DocumentDao						documentDao;
	@Inject
	private TraitementPieceProduiteService	traitementPieceProduiteService;
	@Inject
	private TraitementPieceProduiteDao		traitementBordereauDao;
	@Inject
	private BulletinLiquidationDao			bulletinLiquidationDao;
	@Inject
	private BulletinLiquidationService		bulletinLiquidationService;
	@Inject
	private GenericDao						genericDao;

	@Inject
	private CategorieDeplacementService		categorieDeplacementService;

	@Inject
	public BordereauTransmissionServiceImpl(BordereauTransmissionDao dao) {
		super(dao);
	}

	@Transactional(value=TxType.REQUIRED)
	@Override
	public void creer(NatureDeplacement natureDeplacement,Collection<BulletinLiquidationDto> dtos) throws ServiceException {

		Operation operation = operationService.creer(Code.NATURE_OPERATION_ETABLISSEMENT_BTBL);
	
		BordereauTransmission bordereauTransmission = new BordereauTransmission();
		bordereauTransmission.setNatureDeplacement(natureDeplacement);
		bordereauTransmission.setNumero(RandomStringUtils.randomAlphabetic(8));
		bordereauTransmission.setDateEtablissement(null);
		bordereauTransmission.setType(dynamicEnumerationDao.readByClass(TypePieceProduite.class, Code.TYPE_PIECE_PRODUITE_BT));
		bordereauTransmission.setMontant(BigDecimal.ZERO);

		((BordereauTransmissionDao) dao).create(bordereauTransmission);
		OperationValidationConfig config = operationValidationConfigDao.readByNatureOperationIdByValidationType(Code.NATURE_OPERATION_ETABLISSEMENT_BTBL, ValidationType.ACCEPTER);
		for (BulletinLiquidationDto dto : dtos)
			ajouterBl(operation, bordereauTransmission, dto);

		traitementPieceProduiteService.creer(operation, bordereauTransmission, null, config.getStatutResultat().getCode());

		// mis a jour du disponible
		categorieDeplacementService.debiterDisponible(natureDeplacement.getCategorie(), bordereauTransmission.getMontant());
	}

	private void ajouterBl(Operation operation, BordereauTransmission bordereauTransmission, BulletinLiquidationDto dto) {
		bordereauTransmission.setMontant(bordereauTransmission.getMontant().add(dto.getPiece().getMontant()));
		dto.getPiece().setBordereauTransmission(bordereauTransmission);
		traitementPieceProduiteService.creer(operation, dto.getPiece(), dto.getTraitement(), Code.STATUT_ACCEPTE);
		// traitementDao.create(new TraitementBordereau(operation,
		// operation.getNature().getStatutResultatAccepter(),
		// bordereauTransmission));
		documentDao.update(dto.getPiece());
	}

	@Transactional(value=TxType.REQUIRED)
	@Override
	public void modifier(BordereauTransmission bordereauTransmission, Collection<BulletinLiquidationDto> nouveauDtos) throws ServiceException {
		Collection<BulletinLiquidation> nouveaux = new ArrayList<>();
		for (BulletinLiquidationDto bulletinLiquidationDto : nouveauDtos)
			nouveaux.add(bulletinLiquidationDto.getPiece());
		Collection<BulletinLiquidation> anciens = bulletinLiquidationDao.readByBordereau(bordereauTransmission);

		if (CollectionUtils.isEqualCollection(nouveaux, anciens))
			return;

		BigDecimal montant = bordereauTransmission.getMontant();

		Operation operation = operationService.creer(Code.NATURE_OPERATION_MODIFICATION_BTBL);
		CategorieDeplacement categorieDeplacement = nouveauDtos.iterator().next().getDossierDto().getDossier().getDeplacement().getNature().getCategorie();

		for (BulletinLiquidationDto bulletinLiquidationDto : nouveauDtos) {
			if (anciens.contains(bulletinLiquidationDto.getPiece()))
				anciens.remove(bulletinLiquidationDto.getPiece());// le bl na
																	// pas ete
																	// supprimer
			else
				ajouterBl(operation, bordereauTransmission, bulletinLiquidationDto);// nouveau
																					// venu
		}

		for (BulletinLiquidation bulletinLiquidation : anciens) {
			traitementPieceProduiteService.creer(operation, bulletinLiquidation, null, Code.STATUT_REJETE);
			bulletinLiquidation.setBordereauTransmission(null);
			bordereauTransmission.setMontant(bordereauTransmission.getMontant().subtract(bulletinLiquidation.getMontant()));
		}
		traitementPieceProduiteService.creer(operation, bordereauTransmission, null, bordereauTransmission.getDernierTraitement().getStatut().getCode());
		// mis a jour du disponible
		/*
		 * System.out.println("Disponible : "+categorieDeplacement.getDisponible(
		 * )); System.out.println("Initial : "+montant);
		 * System.out.println("Actuel : "+bordereauTransmission.getMontant());
		 * System
		 * .out.println("Diff : "+bordereauTransmission.getMontant().subtract
		 * (montant));
		 */
		montant = bordereauTransmission.getMontant().subtract(montant);
		switch (montant.compareTo(BigDecimal.ZERO)) {
			case -1:
				categorieDeplacementService.crediterDisponible(categorieDeplacement, montant.abs());
				break;
			case 1:
				categorieDeplacementService.debiterDisponible(categorieDeplacement, montant);
				break;
		}
		// System.out.println("Disponible MAJ : "+categorieDeplacement.getDisponible());
	}

	@Transactional(value=TxType.REQUIRED)
	@Override
	public void valider(NatureDeplacement natureDeplacement, String natureOperationCode, Collection<BordereauTransmissionDto> dtos) throws ServiceException {
		// validator.setTraitements(traitements);
		// validator.validate(natureOperation);
		// if(!validator.isSucces())
		// serviceException(validator.getMessagesAsString());

		Operation operation = operationService.creer(natureOperationCode);
		String statutId;
		for (BordereauTransmissionDto dto : dtos) {
			// dto.getBordereauTransmission().setDernierTraitement(dto.getTraitement());
			dto.getTraitement().setPieceProduite(dto.getPiece());
			dto.getTraitement().setOperation(operation);
			// debug(dto.getTraitement());
			// debug(operationValidationConfigDao.readByNatureOperationIdByValidationType(natureOperationCode,
			// dto.getTraitement().getValidationType()));
			statutId = operationValidationConfigDao.readByNatureOperationIdByValidationType(natureOperationCode, dto.getTraitement().getValidationType()).getStatutResultat().getCode();
			// Statut statutCourant =
			// dossierDao.read(dossier.getNumero()).getDernierTraitement().getStatut();

			switch (dto.getTraitement().getValidationType()) {
				case ACCEPTER:
					if (Code.NATURE_OPERATION_VALIDATION_BTBL.equals(natureOperationCode))
						categorieDeplacementService.mettreAJourDisponibleTresor(natureDeplacement.getCategorie(), dto.getPiece());
					break;
				case DIFFERER:

					break;
				case REJETER:/*
							 * if(Code.NATURE_OPERATION_VALIDATION_BTBL.equals(
							 * natureOperationCode) ||
							 * Code.NATURE_OPERATION_PRISE_EN_CHARGE
							 * .equals(natureOperationCode))
							 * categorieDeplacementService
							 * .crediterDisponible(dto.getDossierDtos
							 * ().get(0).getDossier
							 * ().getDeplacement().getNature().getCategorie(),
							 * dto.getPiece());
							 */
					break;
				default:
					break;
			}

			traitementPieceProduiteService.creer(operation, dto.getPiece(), dto.getTraitement(), statutId);
			dao.update(dto.getPiece());

		}
	}

	/*
	 * private Statut statutSuivant(NatureOperation natureOperation){
	 * 
	 * if(Code.NATURE_OPERATION_PRISE_EN_CHARGE.equals(natureOperation.getCode())
	 * ) return dynamicEnumerationDao.readByClass(Statut.class,Code.STATUT_PEC);
	 * if
	 * (Code.NATURE_OPERATION_MIS_EN_REGLEMENT.equals(natureOperation.getCode()
	 * )) return
	 * dynamicEnumerationDao.readByClass(Statut.class,Code.STATUT_MER);
	 * log.severe("Aucun statut pour l'operation "+natureOperation); return
	 * null; }
	 */

	@Override
	public Collection<BordereauTransmission> findByNatureDeplacement(NatureDeplacement natureDeplacement) {
		return null;
	}

	/*
	 * @Override public Collection<BordereauTransmission> findAPrendreEnCharge()
	 * { return ((BordereauTransmissionDao)dao).readByDernierTraitementIsNull();
	 * }
	 */

	@Override
	public Collection<BordereauTransmission> findByStatutId(String statutId) {
		return ((BordereauTransmissionDao) dao).readByStatutId(statutId);
	}

	@Override
	// @Transactional(value=TxType.NOT_SUPPORTED)
	public Collection<BordereauTransmissionDto> findATraiterByNatureDeplacementsByNatureOperationId(Collection<NatureDeplacement> natureDeplacements, String natureOperationId) {
		NatureOperation natureOperation = genericDao.readByClass(NatureOperation.class, natureOperationId);
		Collection<BordereauTransmission> bordereauTransmissions;
		Collection<BordereauTransmissionDto> dtos = new LinkedHashSet<>();
		String codeOpPrec = natureOperation.getPrecedent().getCode();
		String statutPrecAcc = operationValidationConfigDao.readByNatureOperationIdByValidationType(codeOpPrec, ValidationType.ACCEPTER).getStatutResultat().getCode();
		OperationValidationConfig differerConfig = operationValidationConfigDao.readByNatureOperationIdByValidationType(natureOperationId, ValidationType.DIFFERER);

		// if(Code.NATURE_OPERATION_PAIEMENT_PRISE_EN_CHARGE.equals(natureOperationId)){
		// bordereaux avec statut transmis et differe
		bordereauTransmissions = ((BordereauTransmissionDao) dao).readByNatureDeplacementsByNatureOperationIdByStatutId(natureDeplacements, codeOpPrec, statutPrecAcc);
		if (differerConfig != null)
			bordereauTransmissions.addAll(((BordereauTransmissionDao) dao).readByNatureDeplacementsByNatureOperationIdByStatutId(natureDeplacements, natureOperationId, differerConfig.getStatutResultat()
					.getCode()));
		// }else{
		// bordereauTransmissions =
		// ((BordereauTransmissionDao)dao).readByNatureOperationIdByStatutId(natureOperation.getOperationPrecedente().getCode(),natureOperation.getOperationPrecedente().getStatutResultatAccepte().getCode()
		// );
		// }

		if (Code.NATURE_OPERATION_VALIDATION_BTBL.equals(natureOperationId)) {
			bordereauTransmissions.addAll(((BordereauTransmissionDao) dao).readByNatureDeplacementsByNatureOperationIdByStatutId(natureDeplacements, Code.NATURE_OPERATION_MODIFICATION_BTBL,
					Code.STATUT_ACCEPTE));
		}

		Long configCount = operationValidationConfigDao.countByNatureOperationId(natureOperationId);

		for (BordereauTransmission bordereauTransmission : bordereauTransmissions) {
			BordereauTransmissionDto dto = dto(bordereauTransmission, natureOperationId);
			dtos.add(dto);
			if (configCount == 1) {
				dto.getTraitement().setValidationType(ValidationType.ACCEPTER);
			}
		}
		return dtos;
	}

	public BordereauTransmissionDto dto(BordereauTransmission bordereauTransmission, String natureOperationId) {
		BordereauTransmissionDto dto = new BordereauTransmissionDto(bordereauTransmission);
		OperationValidationConfig creerOpConfig = operationValidationConfigDao.readByNatureOperationIdByValidationType(Code.NATURE_OPERATION_ETABLISSEMENT_BTBL, ValidationType.ACCEPTER);
		dto.setDateCreation(traitementBordereauDao
				.readByPieceProduiteByNatureOperationIdByStatutId(bordereauTransmission, Code.NATURE_OPERATION_ETABLISSEMENT_BTBL, creerOpConfig.getStatutResultat().getCode()).iterator().next()
				.getOperation().getDate());
		return dto;
	}
	
	@Override
	public BordereauTransmissionDto findDtoById(Long id) {
		BordereauTransmission bordereauTransmission = ((BordereauTransmissionDao) dao).read(id);
		BordereauTransmissionDto dto = dto(bordereauTransmission, null);
		List<BulletinLiquidationDto> bulletinLiquidationDtos = new ArrayList<>();
		for (BulletinLiquidation bl : bulletinLiquidationDao.readByBordereau(bordereauTransmission)) {
			BulletinLiquidationDto blDto = bulletinLiquidationService.dto(bl, null);
			bulletinLiquidationDtos.add(blDto);
		}
		dto.setBulletinLiquidationDtos(bulletinLiquidationDtos);
		// dto.setDossierDtos(new
		// ArrayList<>(dossierService.findByBordereauId(id)));
		dto.setHistoriqueTraitements(new ArrayList<>(traitementBordereauDao.readByPiece(bordereauTransmission)));
		return dto;
	}

}
