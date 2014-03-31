package ci.gouv.budget.solde.sigdcp.service.calendrier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import ci.gouv.budget.solde.sigdcp.dao.calendrier.MissionExecuteeDao;
import ci.gouv.budget.solde.sigdcp.dao.dossier.DeplacementDao;
import ci.gouv.budget.solde.sigdcp.dao.dossier.DossierDao;
import ci.gouv.budget.solde.sigdcp.dao.dossier.PieceJustificativeAFournirDao;
import ci.gouv.budget.solde.sigdcp.dao.dossier.PieceJustificativeDao;
import ci.gouv.budget.solde.sigdcp.model.Code;
import ci.gouv.budget.solde.sigdcp.model.calendrier.MissionExecutee;
import ci.gouv.budget.solde.sigdcp.model.calendrier.MissionExecuteeDto;
import ci.gouv.budget.solde.sigdcp.model.dossier.Deplacement;
import ci.gouv.budget.solde.sigdcp.model.dossier.Dossier;
import ci.gouv.budget.solde.sigdcp.model.dossier.DossierDto;
import ci.gouv.budget.solde.sigdcp.model.dossier.DossierMission;
import ci.gouv.budget.solde.sigdcp.model.dossier.NatureDeplacement;
import ci.gouv.budget.solde.sigdcp.model.dossier.Operation;
import ci.gouv.budget.solde.sigdcp.model.dossier.PieceJustificative;
import ci.gouv.budget.solde.sigdcp.model.dossier.TraitementDossier;
import ci.gouv.budget.solde.sigdcp.model.dossier.TypeDepense;
import ci.gouv.budget.solde.sigdcp.model.dossier.ValidationType;
import ci.gouv.budget.solde.sigdcp.model.fichier.Fichier;
import ci.gouv.budget.solde.sigdcp.model.identification.AgentEtat;
import ci.gouv.budget.solde.sigdcp.service.ActionType;
import ci.gouv.budget.solde.sigdcp.service.DefaultServiceImpl;
import ci.gouv.budget.solde.sigdcp.service.ServiceException;
import ci.gouv.budget.solde.sigdcp.service.ServiceExceptionType;
import ci.gouv.budget.solde.sigdcp.service.dossier.DeplacementService;
import ci.gouv.budget.solde.sigdcp.service.dossier.DossierMissionService;
import ci.gouv.budget.solde.sigdcp.service.dossier.OperationService;
import ci.gouv.budget.solde.sigdcp.service.dossier.TraitementDossierService;
import ci.gouv.budget.solde.sigdcp.service.utils.TransactionDebugger;
import ci.gouv.budget.solde.sigdcp.service.utils.validaton.MissionExecuteeValidator;

//@Stateless
public class MissionExecuteeServiceImpl extends DefaultServiceImpl<MissionExecutee, Long> implements MissionExecuteeService , Serializable {

	private static final long serialVersionUID = -7601857525393731774L;
	
	@Inject private DeplacementService deplacementService;
	@Inject private DossierMissionService dossierService;
	@Inject private DossierDao dossierDao;
	@Inject private DeplacementDao deplacementDao;
	@Inject private OperationService operationService;
	@Inject private MissionExecuteeValidator validator;
	@Inject private TraitementDossierService traitementService;
	@Inject private PieceJustificativeDao pieceJustificativeDao;
	@Inject private PieceJustificativeAFournirDao pieceJustificativeAFournirDao;
	
	@Inject 
	public MissionExecuteeServiceImpl(MissionExecuteeDao dao) {
		super(dao); 
	}

	@TransactionDebugger
	@Transactional(value=TxType.REQUIRED)
	@Override
	public void enregistrer(ActionType actionType,MissionExecutee missionExecutee,PieceJustificative communication, Collection<DossierDto> dossierDtos) {
		
		/*
		validator.validate(missionExecutee);
		if(!validator.isSucces())
			serviceException(validator.getMessagesAsString());
		*/
		throw new ServiceException("Dont care");
		/*
		switch(actionType){
		case ENREGISTRER:
			enregistrer(missionExecutee,communication, dossierDtos);
			break;
		case SOUMETTRE:
			soumettre(missionExecutee,communication, dossierDtos);
			break;
		}*/
	}
	
	private void enregistrer(MissionExecutee missionExecutee,PieceJustificative communication, Collection<DossierDto> dossierDtos){
		//validator.setDossiers(dossiers);
		//validator.setPieceJustificatives(pieceJustificatives);
		validate(validator, missionExecutee);
		
		//daoCreateHelper.init();
		if(((MissionExecuteeDao)dao).exist(missionExecutee.getId())){
			//Iterator<Collection<PieceJustificative>> iterator = pieceJustificatives.iterator();
			deplacementDao.update(missionExecutee.getDeplacement());
			for(DossierDto dossierDto : dossierDtos){
				if(dossierDao.exist(dossierDto.getDossier().getNumero())){
					for(PieceJustificative pj : dossierDto.getPieceJustificatives())
						if(pj.getNumero().equals(communication.getNumero())){
							pj.setNumero(communication.getNumero());
							pj.setDateEtablissement(communication.getDateEtablissement());
							pj.setFonctionSignataire(communication.getFonctionSignataire());
							if(communication.getFichier().getId()==null)
								pj.setFichier(communication.getFichier());
							break;
						}
					}
				else 
					creerDossier(dossierDto, communication);
				dossierService.enregistrer(ActionType.ENREGISTRER,dossierDto);
			}
			dao.update(missionExecutee);
		}else{
			deplacementService.creer(missionExecutee.getDeplacement());
			for(DossierDto dossierDto : dossierDtos){
				creerDossier(dossierDto, communication);
				dossierService.enregistrer(ActionType.ENREGISTRER,dossierDto);	
			}
			
			dao.create(missionExecutee);
		}	
	}
	
	private void creerDossier(DossierDto dossierDto,PieceJustificative communication){
		PieceJustificative p = new PieceJustificative();
		p.setDateEtablissement(communication.getDateEtablissement());
		p.setDossier(dossierDto.getDossier());
		p.setFichier(new Fichier(communication.getFichier()));
		p.setFonctionSignataire(communication.getFonctionSignataire());
		p.setModel(communication.getModel());
		p.setNumero(communication.getNumero());
		dossierDto.getPieceJustificatives().add(p);
	}
	
	private void soumettre(MissionExecutee missionExecutee,PieceJustificative communication, Collection<DossierDto> dossierDtos){
		enregistrer(missionExecutee,communication, dossierDtos);
		Operation operation = operationService.creer(Code.NATURE_OPERATION_SOUMISSION);
		Collection<DossierDto> dtos = new ArrayList<>();
		for(DossierDto dossierDto : dossierDtos){
			TraitementDossier td = new TraitementDossier();
			td.setValidationType(ValidationType.ACCEPTER);
			traitementService.creer(operation, dossierDto.getDossier(),td, Code.STATUT_ACCEPTE);
			//pour le traitement suivant
			DossierDto dto = new DossierDto(dossierDto.getDossier());
			dto.getTraitement().setValidationType(ValidationType.ACCEPTER);
			dtos.add(dto);
		}
		dossierService.valider(Code.NATURE_OPERATION_RECEVABILITE, dtos);
	}
	
	@Transactional(value=TxType.REQUIRED)
	@Override
	public void transmettreDossier(Collection<DossierMission> dossiers) {
		Operation operation = operationService.creer(Code.NATURE_OPERATION_TRANSMISSION_SAISIE_A_BENEFICIAIRE);
		for(Dossier dossier : dossiers){
			TraitementDossier td = new TraitementDossier();
			td.setValidationType(ValidationType.ACCEPTER);
			traitementService.creer(operation, dossier,td, Code.STATUT_ACCEPTE);
		}
	}
	
	@Override
	public MissionExecuteeDto findSaisieByNumero(Long id) {
		MissionExecutee missionExecutee = null;
		MissionExecuteeDto dto = null;
		if(id!=null){//consultation de mission
			missionExecutee = ((MissionExecuteeDao)dao).read(id);
			if(missionExecutee==null)
				serviceException(ServiceExceptionType.RESOURCE_NOT_FOUND);
			dto = buildDto(missionExecutee);
			/*for(Dossier dossier : dossierService.findByDeplacement(missionExecutee.getDeplacement())){
				DossierDto dossierDto = new 
				dto.getDossierDtos().add(DossierServiceImpl.dto(dossier, personne));
				if(dossier.getCourrier()==null || StringUtils.isEmpty(dossier.getCourrier().getNumero()))
					dto.set
			}
			return dto;*/
		}else{
			missionExecutee = ((MissionExecuteeDao)dao).readSaisieByPersonne(utilisateur());
			
			if(missionExecutee==null){//nouvelle mission a saisir
				missionExecutee = new MissionExecutee();
				missionExecutee.setOrganisateur((AgentEtat) utilisateur());
				missionExecutee.setDeplacement(new Deplacement());
				missionExecutee.getDeplacement().setNature(genericDao.readByClass(NatureDeplacement.class, Code.NATURE_DEPLACEMENT_MISSION_HCI));
				missionExecutee.getDeplacement().setTypeDepense(genericDao.readByClass(TypeDepense.class, Code.TYPE_DEPENSE_PRISE_EN_CHARGE));
			}else{//mission en cours de saisie
				
			}
			dto =buildDto(missionExecutee);
		}
		
		
		return dto;
	}
	
	@Transactional(value=TxType.NOT_SUPPORTED)
	@Override
	public MissionExecutee findByDossier(DossierMission dossierMission) {
		return ((MissionExecuteeDao)dao).readByDossier(dossierMission);
	}
	
	@Transactional(value=TxType.NOT_SUPPORTED)
	@Override
	public Collection<MissionExecuteeDto> findMissionOrganisees() {
		Collection<MissionExecuteeDto> dtos = new LinkedList<>();
		for(MissionExecutee me : ((MissionExecuteeDao)dao).readByPersonne(utilisateur()))
			dtos.add(buildDto(me));

		return dtos;
	}
	
	public MissionExecuteeDto buildDto(MissionExecutee missionExecutee){
		MissionExecuteeDto dto = new MissionExecuteeDto(missionExecutee);
		for(Dossier dossier : dossierService.findByDeplacement(missionExecutee.getDeplacement())){
			dto.getDossierDtos().add(dossierService.buildDto((DossierMission) dossier, null));
			if(Code.NATURE_OPERATION_TRANSMISSION_SAISIE_A_BENEFICIAIRE.equals(dossier.getDernierTraitement().getOperation().getNature().getCode()))
				dto.setTousPresent(false);
		}
		dto.setNatureOperationCode(dto.getDossierDtos().isEmpty() || !dto.getTousPresent()?Code.NATURE_OPERATION_SAISIE:dto.getDossierDtos().iterator().next().getNatureOperationCode());
		Collection<PieceJustificative> communications = pieceJustificativeDao.readByDeplacementByTypeId(missionExecutee.getDeplacement(), Code.TYPE_PIECE_COMMUNICATION);
		
		dto.setCommunication(communications.isEmpty()?new PieceJustificative(pieceJustificativeAFournirDao.readAdministrativeByNatureDeplacementIdByTypeDepenseId(Code.NATURE_DEPLACEMENT_MISSION_HCI, Code.TYPE_DEPENSE_PRISE_EN_CHARGE) ):communications.iterator().next());
		
		return dto;
	}
}
