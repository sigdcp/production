package ci.gouv.budget.solde.sigdcp.service.dossier;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import ci.gouv.budget.solde.sigdcp.dao.dossier.AbstractDossierDao;
import ci.gouv.budget.solde.sigdcp.dao.dossier.DocumentDao;
import ci.gouv.budget.solde.sigdcp.dao.dossier.DossierDao;
import ci.gouv.budget.solde.sigdcp.dao.dossier.OperationValidationConfigDao;
import ci.gouv.budget.solde.sigdcp.dao.dossier.PieceJustificativeAFournirDao;
import ci.gouv.budget.solde.sigdcp.dao.dossier.PieceJustificativeDao;
import ci.gouv.budget.solde.sigdcp.dao.dossier.TraitementDossierDao;
import ci.gouv.budget.solde.sigdcp.model.Code;
import ci.gouv.budget.solde.sigdcp.model.dossier.BulletinLiquidation;
import ci.gouv.budget.solde.sigdcp.model.dossier.BulletinLiquidation.AspectLiquide;
import ci.gouv.budget.solde.sigdcp.model.dossier.Courrier;
import ci.gouv.budget.solde.sigdcp.model.dossier.Deplacement;
import ci.gouv.budget.solde.sigdcp.model.dossier.Dossier;
import ci.gouv.budget.solde.sigdcp.model.dossier.DossierDto;
import ci.gouv.budget.solde.sigdcp.model.dossier.DossierMission;
import ci.gouv.budget.solde.sigdcp.model.dossier.NatureDeplacement;
import ci.gouv.budget.solde.sigdcp.model.dossier.NatureOperation;
import ci.gouv.budget.solde.sigdcp.model.dossier.Operation;
import ci.gouv.budget.solde.sigdcp.model.dossier.OperationValidationConfig;
import ci.gouv.budget.solde.sigdcp.model.dossier.PieceJustificative;
import ci.gouv.budget.solde.sigdcp.model.dossier.PieceJustificativeAFournir;
import ci.gouv.budget.solde.sigdcp.model.dossier.Statut;
import ci.gouv.budget.solde.sigdcp.model.dossier.TraitementDossier;
import ci.gouv.budget.solde.sigdcp.model.dossier.TypeDepense;
import ci.gouv.budget.solde.sigdcp.model.dossier.TypePieceProduite;
import ci.gouv.budget.solde.sigdcp.model.dossier.ValidationType;
import ci.gouv.budget.solde.sigdcp.model.identification.AgentEtat;
import ci.gouv.budget.solde.sigdcp.service.ActionType;
import ci.gouv.budget.solde.sigdcp.service.DefaultServiceImpl;
import ci.gouv.budget.solde.sigdcp.service.ServiceException;
import ci.gouv.budget.solde.sigdcp.service.ServiceExceptionType;


public abstract class AbstractDossierServiceImpl<DOSSIER extends Dossier> extends DefaultServiceImpl<DOSSIER, String> implements AbstractDossierService<DOSSIER>,Serializable {
	
	private static final long serialVersionUID = -7765679080076677680L;
	
	private static final String[] OPERATION_CORRIGER = {Code.NATURE_OPERATION_RECEVABILITE,Code.NATURE_OPERATION_CONFORMITE,Code.NATURE_OPERATION_ETABLISSEMENT_BL};
	
	@Inject private DeplacementService deplacementService;
	@Inject private DossierDao dossierDao;
	@Inject private PieceJustificativeDao pieceJustificativeDao;
	@Inject protected OperationService operationService;
	@Inject private TraitementDossierService traitementService;
	@Inject protected PieceJustificativeAFournirDao pieceJustificativeAFournirDao;
	@Inject private DocumentDao documentDao;
	@Inject private TraitementDossierDao traitementDossierDao;
	@Inject protected OperationValidationConfigDao operationValidationConfigDao;
		
	public AbstractDossierServiceImpl(AbstractDossierDao<DOSSIER> dao) {
		super(dao); 
	}
	 
	/*--------- Fonctions métiers ----------*/
	
	protected void validationSaisie(DossierDto dossierDto,Boolean soumission) throws ServiceException{
		
	}
	
	public void saisir(Operation operation,DOSSIER dossier,Collection<PieceJustificative> pieceJustificatives){
		//debug(operation);
		Date dateCourante = new Date();
		if(operation==null){//mise a jour
			dossierDao.update(dossier);
			updatePieceJustificatives(dossier, pieceJustificatives);
		}else{//creation
			if(!deplacementService.exist(dossier.getDeplacement().getId()))
				deplacementService.creer(dossier.getDeplacement());
			dossier.setNumero(numero(dossier, pieceJustificatives));
			onDaoCreate(dossier);
			updatePieceJustificatives(dossier, pieceJustificatives);//on enregistre les pieces justificatives
			//on cree les pieces derivees
			for(PieceJustificativeAFournir pieceAImprimer : pieceJustificativeAFournirDao.readDeriveeByNatureDeplacementIdByTypeDepenseId(dossier.getDeplacement().getNature().getCode(),
					dossier.getDeplacement().getTypeDepense().getCode())){
				PieceJustificative p = new PieceJustificative(dossier,RandomStringUtils.randomNumeric(4), pieceAImprimer,dateCourante);
				if(Code.TYPE_PIECE_FEUILLE_DEPLACEMENT.equals(pieceAImprimer.getTypePieceJustificative().getCode()))
					p.setNumero(dossier.getNumero());
				pieceJustificativeDao.create(p);
			}
			TraitementDossier td = new TraitementDossier();
			td.setValidationType(ValidationType.ACCEPTER);
			traitementService.creer(operation, dossier,td, Code.STATUT_ACCEPTE);
			
			//debug(dossier.getDernierTraitement());
		}
	}
	
	private void updatePieceJustificatives(Dossier dossier,Collection<PieceJustificative> pieceJustificatives){
		if(pieceJustificatives==null)
			return;
		for(PieceJustificative pieceJustificative : pieceJustificatives){
			pieceJustificative.setDossier(dossier);
			if(pieceJustificativeDao.exist(pieceJustificative.getId())){
				pieceJustificativeDao.update(pieceJustificative);
			}else if(StringUtils.isNotEmpty(pieceJustificative.getNumero()))
				pieceJustificativeDao.create(pieceJustificative);
				
		}
	}
	
	protected String numero(DOSSIER dossier,Collection<PieceJustificative> pieceJustificatives){
		return System.currentTimeMillis()+"";
	}
	
	@Override
	public void enregistrer(ActionType actionType,DossierDto dossierDto) throws ServiceException {
		switch(actionType){
			case ENREGISTRER:
				enregistrer(dossierDto);
				break;
			case SOUMETTRE:
				soumettre(dossierDto);
				break;
		}
	}
	
	protected void onDaoCreate(DOSSIER dossier){
		dao.create(dossier);
	}
	
	@SuppressWarnings("unchecked")
	private void enregistrer(DossierDto dossierDto){
		validationSaisie(dossierDto,false);
		if(!dao.exist(dossierDto.getDossier().getNumero())){
			Operation operation = operationService.creer(Code.NATURE_OPERATION_SAISIE);
			saisir(operation, (DOSSIER) dossierDto.getDossier(), dossierDto.getPieceJustificatives());
		}else{
			//Statut statutCourant = dossier.getDernierTraitement().getStatut();
			//if(statutCourant!=null && !Code.STATUT_ACCEPTE.equals(statutCourant.getCode()))
			//	serviceException(ServiceExceptionType.DOSSIER_STATUT_ILLELGAL);
			saisir(null, (DOSSIER) dossierDto.getDossier(), dossierDto.getPieceJustificatives());
		}
		
		if(Boolean.TRUE.equals(dossierDto.getTransmettreBeneficiaire())){
			Operation operation = operationService.creer(Code.NATURE_OPERATION_TRANSMISSION_SAISIE_A_BENEFICIAIRE);
			TraitementDossier td = new TraitementDossier();
			td.setValidationType(ValidationType.ACCEPTER);
			traitementService.creer(operation, dossierDto.getDossier(), td,Code.STATUT_ACCEPTE);
			dao.update((DOSSIER) dossierDto.getDossier());
		}
		
		if(Code.NATURE_OPERATION_TRANSMISSION_SAISIE_A_ORGANISATEUR.equals(dossierDto.getNatureOperationCode())){
			Operation operation = operationService.creer(dossierDto.getNatureOperationCode());
			TraitementDossier td = new TraitementDossier();
			td.setValidationType(ValidationType.ACCEPTER);
			traitementService.creer(operation, dossierDto.getDossier(), td,Code.STATUT_ACCEPTE);
			dao.update((DOSSIER) dossierDto.getDossier());
		}
	}
	
	private void soumettre(DossierDto dossierDto){
		enregistrer(dossierDto);
		//validationSaisie(dossier, pieceJustificatives,true);
		Operation operation = operationService.creer(Code.NATURE_OPERATION_SOUMISSION);
		TraitementDossier td = new TraitementDossier();
		td.setValidationType(ValidationType.ACCEPTER);
		traitementService.creer(operation, dossierDto.getDossier(), td,Code.STATUT_ACCEPTE);
	}

	@Transactional(value=TxType.REQUIRED)
	@Override
	public void deposer(Collection<DOSSIER> dossiers) throws ServiceException {
		Operation operation = operationService.creer(Code.NATURE_OPERATION_DEPOT);
		for(DOSSIER dossier : dossiers){
			TraitementDossier td = new TraitementDossier();
			td.setValidationType(ValidationType.ACCEPTER);
			traitementService.creer(operation, dossier, td,Code.STATUT_ACCEPTE);
			dao.update(dossier);
		}
	}
	
	@Transactional(value=TxType.REQUIRED)
	@Override
	public void annuler(Collection<DOSSIER> dossiers) throws ServiceException {
		Operation operation = operationService.creer(Code.NATURE_OPERATION_ANNULATION_DEMANDE);
		for(DOSSIER dossier : dossiers){
			TraitementDossier td = new TraitementDossier();
			td.setValidationType(ValidationType.ACCEPTER);
			traitementService.creer(operation, dossier, td,Code.STATUT_ACCEPTE);
			dao.update(dossier);
		}
	}
	
	@Override
	public void valider(String natureOperationCode,Collection<DossierDto> dtos) {
		
		//validator.setTraitements(traitements);
		//validator.validate(natureOperation);
		//if(!validator.isSucces())
		//	serviceException(validator.getMessagesAsString());
		OperationValidationConfig config;
		Operation operation = operationService.creer(natureOperationCode);
		//determiner le nouveau statut
		for(DossierDto dto : dtos){
			config = operationValidationConfigDao.readByNatureOperationIdByValidationType(natureOperationCode, dto.getTraitement().getValidationType());
			dto.getTraitement().setDossier(dto.getDossier());
			dto.getTraitement().setOperation(operation);
			dto.getTraitement().setStatut(config.getStatutResultat());
			//Statut statutCourant = dossierDao.read(dto.getDossier().getNumero()).getDernierTraitement().getStatut();
			switch(dto.getTraitement().getValidationType()){
			case ACCEPTER:			
				if(Code.NATURE_OPERATION_ETABLISSEMENT_BL.equals(natureOperationCode)){
					//dto.getDossier().setMontantIndemnisation(calculerMontantIndemnisation((DOSSIER) dto.getDossier()));
					BulletinLiquidation bl = dto.getBulletinLiquidationSaisie();
					bl.setNumero(RandomStringUtils.randomAlphabetic(4));
					documentDao.create(bl);
					dto.getTraitement().setPieceProduite(bl);
				}
				break;
			case DIFFERER:break;
			case REJETER:
	
				break;
			}
			//debug(dossier.getDernierTraitement());
			traitementDossierDao.create(dto.getTraitement());
			dto.getDossier().setDernierTraitement(dto.getTraitement());
			dossierDao.update(dto.getDossier());
		}
	}
		
	/* Fonctions techniques  */
	
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	@Override
	public DossierDto findSaisieByNatureDeplacement(NatureDeplacement natureDeplacement,String numero,String codeNatureOperation) {
		DOSSIER dossier = null;
		DossierDto dto;
		if(StringUtils.isNotEmpty(numero)){
			dossier = ((AbstractDossierDao<DOSSIER>)dao).read(numero);
			if(dossier==null)
				serviceException(ServiceExceptionType.RESOURCE_NOT_FOUND);
			dto = buildDto(dossier, codeNatureOperation);
		}else{
			dossier = ((AbstractDossierDao<DOSSIER>)dao).readSaisieByPersonneByNatureDeplacement(utilisateur(), natureDeplacement);
			
			if(dossier==null){// Nouveau dossier
				dossier = createDossier();
				if(dossier!=null){
					dossier.getDeplacement().setNature(natureDeplacement);
					dto = buildDto(dossier, dossier.getDernierTraitement()==null?Code.NATURE_OPERATION_SAISIE:dossier.getDernierTraitement().getOperation().getNature().getSuivant().getCode());
				}else
					dto = new DossierDto(dossier);
			}else
				dto = buildDto(dossier, codeNatureOperation);
		}
		
		return dto;
	}
	
	protected void chargerPiecesJustificatives(DossierDto dto){
		if(dto==null || dto.getDossier()==null || dto.getDossier().getDeplacement()==null || dto.getDossier().getDeplacement().getTypeDepense()==null)
			return;
		dto.setPieceJustificatives(new ArrayList<PieceJustificative>());
		Dossier dossier = dto.getDossier();
		
		/*---------- Chargement des pieces de base et ...*/
		//quelles sont les pieces a fournir
		Collection<PieceJustificativeAFournir> pieceJustificativeAFournirs = pieceJustificativeAFournirDao.readBaseByNatureDeplacementIdByTypeDepenseId(dossier.getDeplacement().getNature().getCode(),
				dossier.getDeplacement().getTypeDepense().getCode());
		
		//quelles sont les pieces déja fournis
		if(dao.exist(dto.getDossier().getNumero()))
			dto.getPieceJustificatives().addAll(pieceJustificativeDao.readByDossier(dossier));
		
		//croisement
		for(PieceJustificativeAFournir pieceJustificativeAFournir : pieceJustificativeAFournirs){
			boolean trouve = false;
			for(PieceJustificative pieceJustificative : dto.getPieceJustificatives())
				if(pieceJustificative.getModel().equals(pieceJustificativeAFournir)){
					trouve = true;
					break;
				}
			if(!trouve)
				dto.getPieceJustificatives().add(new PieceJustificative(dossier,pieceJustificativeAFournir));
		}
		
	}
	
	@Override
	public void mettreAJourPiecesJustificatives(DossierDto...dossierDtos) {
		TypeDepense temp;
		for(DossierDto dossierDto : dossierDtos)
			if(!dossierDto.getDossier().getDeplacement().getTypeDepense().equals(dossierDto.getTypeDepense())){
				temp = dossierDto.getDossier().getDeplacement().getTypeDepense();
				dossierDto.getDossier().getDeplacement().setTypeDepense(dossierDto.getTypeDepense());
				chargerPiecesJustificatives(dossierDto);
				dossierDto.getDossier().getDeplacement().setTypeDepense(temp);
			}
		for(DossierDto dossierDto : dossierDtos)
			if(!dossierDto.getDossier().getDeplacement().getTypeDepense().equals(dossierDto.getTypeDepense()))
				dossierDto.getDossier().getDeplacement().setTypeDepense(dossierDto.getTypeDepense());
			
	}
		
	protected abstract DOSSIER createDossier();
	
	
	protected void initSaisie(Dossier source,DOSSIER destination){
		destination.setGrade(source.getGrade());
		destination.setDatePriseService(source.getDatePriseService());
		destination.setService(source.getService());
	}
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	@Override
	public Collection<DOSSIER> findByNatureDeplacementAndStatut(NatureDeplacement natureDeplacement, Statut statut) {
		return ((AbstractDossierDao<DOSSIER>)dao).readByNatureDeplacementAndStatut(natureDeplacement, statut);
	}
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	@Override
	public Collection<DOSSIER> findByNatureDeplacementsByStatut(Collection<NatureDeplacement> natureDeplacements, Statut statut) {
		return ((AbstractDossierDao<DOSSIER>)dao).readByNatureDeplacementsByStatut(natureDeplacements, statut);
	}
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	@Override
	public Collection<DOSSIER> findByNatureDeplacement(NatureDeplacement natureDeplacement) {
		return ((AbstractDossierDao<DOSSIER>)dao).readByNatureDeplacement(natureDeplacement);
	}
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	@Override
	public Collection<DOSSIER> findByStatut(Statut statut) {
		return ((AbstractDossierDao<DOSSIER>)dao).readByStatut(statut);
	}
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	@Override
	public Collection<DOSSIER> findByStatutId(String statutId) {
		return ((AbstractDossierDao<DOSSIER>)dao).readByStatutId(statutId);
	}
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	@Override
	public Collection<DOSSIER> findByAgentEtat(AgentEtat agentEtat) {
		return ((AbstractDossierDao<DOSSIER>)dao).readByAgentEtat(agentEtat);
	}
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	@Override
	public Collection<DossierDto> findByAgentEtatAndAyantDroit(AgentEtat agentEtat) {
		Collection<DossierDto> dtos = new LinkedList<>();
		for(DOSSIER dossier : ((AbstractDossierDao<DOSSIER>)dao).readByAgentEtatAndAyantDroit(agentEtat))
			dtos.add(buildDto(dossier, null));
		return dtos;
	}
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	@Override
	public Collection<DOSSIER> findByDeplacement(Deplacement deplacement) {
		return ((AbstractDossierDao<DOSSIER>)dao).readByDeplacement(deplacement);
	}
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	@Override
	public DOSSIER findDernierCreeByAgentEtat(AgentEtat agentEtat) {
		return ((AbstractDossierDao<DOSSIER>)dao).readDernierCreeByAgentEtat(agentEtat);
	}
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	@Override
	public Collection<DossierDto> findByBordereauId(Long bordereauTransmissionId) {
		//System.out.println(bordereauTransmissionId);
		//System.out.println(((AbstractDossierDao<DOSSIER>)dao).readByBordereauId(bordereauTransmissionId));
		return dtos(null,((AbstractDossierDao<DOSSIER>)dao).readByBordereauId(bordereauTransmissionId));
	}
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	@Override
	public String findInstructions(DOSSIER dossier) {
		StringBuilder instructions = new StringBuilder();
		if(!((AbstractDossierDao<DOSSIER>)dao).exist(dossier.getNumero()) || dossier.getDernierTraitement()==null || dossier.getDernierTraitement().getStatut()==null)
			instructions.append("Veuillez enregistrer le dossier");
		
		if(dossier.getDernierTraitement()!=null)
			instructions.append(dossier.getDernierTraitement().getOperation().getNature().getLibelle()+" : "+dossier.getDernierTraitement().getStatut().getLibelle());

		return instructions.toString();
		
	}
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	@Override
	public BigDecimal calculerMontantIndemnisation(DOSSIER dossier) {
		return new BigDecimal("15000");
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	@Override
	public Collection<DossierDto> findATraiterByNatureDeplacementsByNatureOperationId(Collection<NatureDeplacement> natureDeplacements,String natureOperationId) {
		NatureOperation natureOperation = genericDao.readByClass(NatureOperation.class, natureOperationId);
		String codeOpPrec = natureOperation.getPrecedent().getCode();
		String codeStatutAcc = operationValidationConfigDao.readByNatureOperationIdByValidationType(codeOpPrec, ValidationType.ACCEPTER).getStatutResultat().getCode();
		
		AbstractDossierDao<DOSSIER> dossierDao = ((AbstractDossierDao<DOSSIER>)dao);

		Collection<DOSSIER>  dossiers = null;
		switch(natureOperationId){
		case Code.NATURE_OPERATION_CONFORMITE: dossiers =  dossierDao.readCourrierNonNullByNatureDeplacementsByNatureOperationIdByStatutId(natureDeplacements,codeOpPrec,codeStatutAcc);break;
		//case Code.NATURE_OPERATION_ETABLISSEMENT_BL: dossiers =  dossierDao.readCourrierNonNullByNatureDeplacementsByNatureOperationIdByStatutId(natureDeplacements,codeOpPrec,codeStatutAcc);break;
		default: dossiers = dossierDao.readByNatureDeplacementsByNatureOperationIdByStatutId(natureDeplacements,codeOpPrec,codeStatutAcc );break;
		}
		
		if(Code.NATURE_OPERATION_ETABLISSEMENT_BL.equals(natureOperationId)){
			dossiers.addAll(dossierDao.readBulletinLiquidationExisteLiquidableByNatureDeplacements(natureDeplacements, AspectLiquide.DEMANDE));
		}
		
		OperationValidationConfig differeConfig = operationValidationConfigDao.readByNatureOperationIdByValidationType(natureOperationId, ValidationType.DIFFERER);
		if(differeConfig!=null)
			dossiers.addAll(((AbstractDossierDao<DOSSIER>)dao).readByNatureDeplacementsByNatureOperationIdByStatutId(natureDeplacements,natureOperationId,differeConfig.getStatutResultat().getCode()));
		
		Collection<DossierDto> dtos = dtos(natureOperationId,dossiers);
		
		return dtos;
	}
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	@Override
	public Collection<DossierDto> findALiquiderByNatureDeplacementsByAspectLiquide(Collection<NatureDeplacement> natureDeplacements,AspectLiquide aspectLiquide){
		return findATraiterByNatureDeplacementsByNatureOperationId(natureDeplacements, Code.NATURE_OPERATION_ETABLISSEMENT_BL);
	}
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	@SuppressWarnings("unchecked")
	@Override
	public DossierDto findDtoByDossier(Dossier dossier){
		if(dossier==null || StringUtils.isEmpty(dossier.getNumero()))
			return null;
		return buildDto((DOSSIER) dossier,null);
	}
	
	/* fonction aidantes */
	
	private boolean corrigeable(Dossier dossier){
		if(ArrayUtils.contains(OPERATION_CORRIGER, dossier.getDernierTraitement().getOperation().getNature().getCode())){
			OperationValidationConfig config = operationValidationConfigDao.readByNatureOperationIdByValidationType(
					dossier.getDernierTraitement().getOperation().getNature().getCode(), ValidationType.REJETER);
			return config.getStatutResultat().getCode().equals(dossier.getDernierTraitement().getStatut().getCode());
		}
		return false;
	}
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public DossierDto buildDto(DOSSIER dossier,String natureOperationCode){
		DossierDto dto = new DossierDto(dossier);
		TraitementDossier td = dossier.getDernierTraitement();
		Boolean dossierExiste = dao.exist(dto.getDossier().getNumero());
		if(StringUtils.isNotEmpty(natureOperationCode)){
			//NatureOperation natureOperation = genericDao.readByClass(NatureOperation.class, natureOperationCode);
			dto.setNatureOperationCode(natureOperationCode);
			List<OperationValidationConfig> operationValidationConfigs = new ArrayList<>(operationValidationConfigDao.readByNatureOperationId(natureOperationCode));
			
			if(operationValidationConfigs.size()==1){
				dto.getTraitement().setValidationType(ValidationType.ACCEPTER);
			}
			
			for(OperationValidationConfig config : operationValidationConfigs){
				if(Code.STATUT_DIFFERE.equals(config.getStatutResultat().getCode()) && Code.STATUT_DIFFERE.equals(td.getStatut().getCode()))
					continue;
				dto.getValidationTypes().add(config.getValidationType());
			}
			//
			
		}else{
			if(corrigeable(dossier)){
				//System.out.println("AbstractDossierServiceImpl.buildDto()");
				dto.setNatureOperationCode(Code.NATURE_OPERATION_SOUMISSION);//l'utilisateur peut soumettre
			}else if(td.getOperation().getNature().getSuivant()!=null)
				dto.setNatureOperationCode(td.getOperation().getNature().getSuivant().getCode());
			
		}
		
		/*
		if( !(Code.NATURE_OPERATION_SAISIE.equals(dto.getNatureOperationCode()) || Code.NATURE_OPERATION_SOUMISSION.equals(dto.getNatureOperationCode())
				|| Code.NATURE_OPERATION_DEPOT.equals(dto.getNatureOperationCode()))){
			if(dto.getDossier().getCourrier()!=null)
				dto.setCourrier("Courrier N°"+dto.getDossier().getCourrier().getNumero()+" du "+formatDate(dto.getDossier().getCourrier().getDate()));
		}*/
		
		OperationValidationConfig creationOpConfig = operationValidationConfigDao.readByNatureOperationIdByValidationType(Code.NATURE_OPERATION_ETABLISSEMENT_BL, ValidationType.ACCEPTER);
		Collection<TraitementDossier> blTraitements = traitementDossierDao.readByDossierByNatureOperationIdByStatutId(dossier, Code.NATURE_OPERATION_ETABLISSEMENT_BL, 
				creationOpConfig.getStatutResultat().getCode());
		TraitementDossier creationBlTraitement = blTraitements.isEmpty()?null:blTraitements.iterator().next();
		if(creationBlTraitement!=null){
			//dto.setDateCreationBL(creationBlTraitement.getOperation().getDate());
			dto.getBulletinLiquidations().add((BulletinLiquidation) creationBlTraitement.getPieceProduite());
		}
		 
		chargerPiecesJustificatives(dto);
		
		if(Code.NATURE_OPERATION_SAISIE.equals(dto.getNatureOperationCode())){
			chargerPiecesJustificatives(dto);
			//dto.setNatureOperationCode(Code.NATURE_OPERATION_SAISIE);
			dossier.setBeneficiaire((AgentEtat) utilisateur());
			DOSSIER dernierCree = ((AbstractDossierDao<DOSSIER>)dao).readDernierCreeByAgentEtat((AgentEtat) utilisateur());
			if(dernierCree!=null)
				initSaisie(dernierCree, dossier);
			dto.setPieceAdministrative(new PieceJustificative(dossier, null, 
					pieceJustificativeAFournirDao.readAdministrativeByNatureDeplacementIdByTypeDepenseId(dossier.getDeplacement().getNature().getCode(), dossier.getDeplacement().getTypeDepense().getCode())
					, null));	
		}else{
			dto.setHistoriqueTraitements(new ArrayList<>(traitementDossierDao.readByDossier(dossier)));
			for(TraitementDossier traitementDossier : dto.getHistoriqueTraitements())
				traitementDossier.setStatutMessage(statutMessage(traitementDossier));
			dto.setPieceAdministrative(pieceJustificativeDao.readAdministrativeByDossier(dossier));	
			if(Code.NATURE_OPERATION_SOUMISSION.equals(dto.getNatureOperationCode())){
				
			}else if(Code.NATURE_OPERATION_DEPOT.equals(dto.getNatureOperationCode()))
				dossier.setCourrier(new Courrier());
			else if(Code.NATURE_OPERATION_TRANSMISSION_SAISIE_A_ORGANISATEUR.equals(dto.getNatureOperationCode())){
				Collection<PieceJustificative> ccms = pieceJustificativeDao.readByDossierByTypeId(dossier, Code.TYPE_PIECE_COMMUNICATION);
				System.out.println(ccms);
				if(!ccms.isEmpty())
					dto.getPieceJustificativesNonEditable().add(ccms.iterator().next());
			}else if(Code.NATURE_OPERATION_ETABLISSEMENT_BL.equals(dto.getNatureOperationCode())){
				dossier.setMontantIndemnisation(calculerMontantIndemnisation(dossier));
				BulletinLiquidation bl = new BulletinLiquidation();
				bl.setDossier(dossier);
				bl.setType(genericDao.readByClass(TypePieceProduite.class, Code.TYPE_PIECE_PRODUITE_BL));
				bl.setAspect(AspectLiquide.DEMANDE);
				
				if(Code.TYPE_DEPENSE_PRISE_EN_CHARGE.equals(dossier.getDeplacement().getTypeDepense().getCode()))
					if(dossier instanceof DossierMission){
						bl.setPourcentage(new BigDecimal(dto.getBulletinLiquidations().isEmpty()?"0.8":"0.2"));
					}else
						bl.setPourcentage(new BigDecimal("1"));
				else
					bl.setPourcentage(new BigDecimal("1"));
				bl.setMontant( dossier.getMontantIndemnisation().multiply(bl.getPourcentage()) );
				
				dto.setBulletinLiquidationSaisie(bl);
			}
		}
		
		//dto.setBulletinLiquidation(bulletinLiquidationDao.readByDossier(dossier));
		if(dossierExiste){
			OperationValidationConfig creerDossierOpConfig = operationValidationConfigDao.readByNatureOperationIdByValidationType(Code.NATURE_OPERATION_SAISIE, ValidationType.ACCEPTER);
			dto.setDateCreation(traitementDossierDao.readByDossierByNatureOperationIdByStatutId(dossier, Code.NATURE_OPERATION_SAISIE, 
					creerDossierOpConfig.getStatutResultat().getCode()).iterator().next().getOperation().getDate());
			
			OperationValidationConfig visaOpConfig = operationValidationConfigDao.readByNatureOperationIdByValidationType(Code.NATURE_OPERATION_VISA_BL, ValidationType.ACCEPTER);
			
			
			Boolean blVise = !traitementDossierDao.readByDossierByNatureOperationIdByStatutId(dossier, Code.NATURE_OPERATION_VISA_BL, visaOpConfig.getStatutResultat().getCode()).isEmpty();
			if(blVise || StringUtils.isNotEmpty(natureOperationCode) && (Code.NATURE_OPERATION_VALIDATION_BL.equals(natureOperationCode) || 
					Code.NATURE_OPERATION_TRANSMISSION_BL_VISA.equals(natureOperationCode) ||
					Code.NATURE_OPERATION_VISA_BL.equals(natureOperationCode))) {
				//dto.setBulletinLiquidation((BulletinLiquidation) creationBlTraitement.getPieceProduite());
			}
			
			dto.getDossier().getDernierTraitement().setStatutMessage(statutMessage(dto.getDossier().getDernierTraitement()));
		}
		return dto;
	}
		
	public Collection<DossierDto> dtos(String natureOperationId,Collection<DOSSIER> dossiers){
		Collection<DossierDto> dtos = new LinkedHashSet<>();
		for(DOSSIER dossier : dossiers)
			dtos.add(buildDto(dossier,natureOperationId));
		return dtos;
	}
	
	private String statutMessage(TraitementDossier td){
		NatureOperation no = td.getOperation().getNature();
		//Collection<OperationValidationConfig> configs = operationValidationConfigDao.readByNatureOperationId(no.getCode());
		
		switch(td.getStatut().getCode()){
			case Code.STATUT_ACCEPTE:return no.getMessage();
			case Code.STATUT_DIFFERE:return "Différé pour le motif suivant : "+td.getObservation();
			case Code.STATUT_REJETE:return "Rejeté pour le motif suivant : "+td.getObservation();
		}
		/*
		switch(td.getOperation().getNature().getCode()){
			case Code.NATURE_OPERATION_SAISIE:case Code.NATURE_OPERATION_SOUMISSION:return no.getMessage();
			case Code.NATURE_OPERATION_RECEVABILITE: 
				switch(td.getStatut().getCode()){
				case Code.STATUT_ACCEPTE:dto.setStatut("Reçevable");break;
				case Code.STATUT_DIFFERE:dto.setStatut("Différé pour le motif suivant : "+td.getObservation());break;
				case Code.STATUT_REJETE:dto.setStatut("Rejeté pour correction pour le motif suivant : "+td.getObservation());break;
				}break;
			case Code.NATURE_OPERATION_DEPOT: 
				switch(td.getStatut().getCode()){
				case Code.STATUT_ACCEPTE:dto.setStatut("Déposé");break;
				case Code.STATUT_DIFFERE:dto.setStatut("Différé pour le motif suivant : "+td.getObservation());break;
				case Code.STATUT_REJETE:dto.setStatut("Rejeté pour correction pour le motif suivant : "+td.getObservation());break;
				}break;
			case Code.NATURE_OPERATION_CONFORMITE: 
				switch(td.getStatut().getCode()){
				case Code.STATUT_ACCEPTE:dto.setStatut("Conforme");break;
				case Code.STATUT_DIFFERE:dto.setStatut("Différé pour le motif suivant : "+td.getObservation());break;
				case Code.STATUT_REJETE:dto.setStatut("Rejeté pour correction pour le motif suivant : "+td.getObservation());break;
				}break;
			case Code.NATURE_OPERATION_ETABLISSEMENT_BL:
			case Code.NATURE_OPERATION_VALIDATION_BL:
			case Code.NATURE_OPERATION_TRANSMISSION_BL_VISA:
				switch(td.getStatut().getCode()){
				case Code.STATUT_ACCEPTE:dto.setStatut("En cours de liquidation");break;
				case Code.STATUT_DIFFERE:dto.setStatut("Différé pour le motif suivant : "+td.getObservation());break;
				case Code.STATUT_REJETE:dto.setStatut("Rejeté pour correction pour le motif suivant : "+td.getObservation());break;
				}break;
			case Code.NATURE_OPERATION_VISA_BL:
				switch(td.getStatut().getCode()){
				case Code.STATUT_ACCEPTE:dto.setStatut("Liquidé");break;
				case Code.STATUT_DIFFERE:dto.setStatut("Différé pour le motif suivant : "+td.getObservation());break;
				case Code.STATUT_REJETE:dto.setStatut("Rejeté pour correction pour le motif suivant : "+td.getObservation());break;
				}break;
			//Le statut du dossier depend de celui du bordereau
			case Code.NATURE_OPERATION_ETABLISSEMENT_BTBL:
				BulletinLiquidation bulletinLiquidation = (BulletinLiquidation) creationBlTraitement.getPieceProduite();
				TraitementPieceProduite tb = bulletinLiquidation.getBordereauTransmission().getDernierTraitement();
				switch(tb.getOperation().getNature().getCode()){
				case Code.NATURE_OPERATION_ETABLISSEMENT_BTBL:
				case Code.NATURE_OPERATION_VALIDATION_BTBL:
					switch(tb.getStatut().getCode()){
					case Code.STATUT_ACCEPTE:dto.setStatut("Liquidé");break;
					case Code.STATUT_DIFFERE:dto.setStatut("Différé pour le motif suivant : "+td.getObservation());break;
					case Code.STATUT_REJETE:dto.setStatut("Rejeté pour correction pour le motif suivant : "+td.getObservation());break;
					}break;
				case Code.NATURE_OPERATION_TRANSMISSION_BTBL:
					switch(tb.getStatut().getCode()){
					case Code.STATUT_ACCEPTE:dto.setStatut("Liquidé");break;
					case Code.STATUT_DIFFERE:dto.setStatut("Différé pour le motif suivant : "+td.getObservation());break;
					case Code.STATUT_REJETE:dto.setStatut("Rejeté pour correction pour le motif suivant : "+td.getObservation());break;
					}break;
				case Code.NATURE_OPERATION_PRISE_EN_CHARGE:
				case Code.NATURE_OPERATION_MISE_EN_REGLEMENT:
					switch(tb.getStatut().getCode()){
					case Code.STATUT_ACCEPTE:dto.setStatut("En cours de paiement");break;
					case Code.STATUT_DIFFERE:dto.setStatut("Différé pour le motif suivant : "+td.getObservation());break;
					case Code.STATUT_REJETE:dto.setStatut("Rejeté pour correction pour le motif suivant : "+td.getObservation());break;
					}break;
				}
			case Code.NATURE_OPERATION_REGLER_BL:
				switch(td.getStatut().getCode()){
				case Code.STATUT_ACCEPTE:dto.setStatut("Payé");break;
				case Code.STATUT_DIFFERE:dto.setStatut("Différé pour le motif suivant : "+td.getObservation());break;
				case Code.STATUT_REJETE:dto.setStatut("Rejeté pour correction pour le motif suivant : "+td.getObservation());break;
				}break;	
			}*/
		return null;
	}
	

	/**/
	
	protected Integer index(DossierDto dto,PieceJustificativeAFournir model){
		int index = -1;
		for(PieceJustificative pieceJustificative : dto.getPieceJustificatives()){
			index++;
			if(pieceJustificative.getModel().equals(model))
				return index;
		}
		return -1;
	}
	
	protected Integer count(DossierDto dto,PieceJustificativeAFournir model){
		int n = 0;
		for(PieceJustificative pieceJustificative : dto.getPieceJustificatives()){
			if(pieceJustificative.getModel().equals(model))
				n++;
		}
		return n;
	}
	
	
}
