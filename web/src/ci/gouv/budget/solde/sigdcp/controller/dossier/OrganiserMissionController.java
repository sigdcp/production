package ci.gouv.budget.solde.sigdcp.controller.dossier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import javax.faces.event.ValueChangeEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;
import org.omnifaces.util.Faces;

import ci.gouv.budget.solde.sigdcp.controller.NavigationManager;
import ci.gouv.budget.solde.sigdcp.controller.application.AbstractDemandeController;
import ci.gouv.budget.solde.sigdcp.model.Code;
import ci.gouv.budget.solde.sigdcp.model.calendrier.MissionExecutee;
import ci.gouv.budget.solde.sigdcp.model.calendrier.MissionExecuteeDto;
import ci.gouv.budget.solde.sigdcp.model.dossier.DossierDto;
import ci.gouv.budget.solde.sigdcp.model.dossier.DossierMission;
import ci.gouv.budget.solde.sigdcp.service.ActionType;
import ci.gouv.budget.solde.sigdcp.service.calendrier.MissionExecuteeService;
import ci.gouv.budget.solde.sigdcp.service.dossier.DossierMissionService;
import ci.gouv.budget.solde.sigdcp.service.identification.AgentEtatService;
import ci.gouv.budget.solde.sigdcp.service.indemnite.LocaliteGroupeMissionService;
import ci.gouv.budget.solde.sigdcp.service.resources.CRUDType;
import ci.gouv.budget.solde.sigdcp.service.utils.validaton.AbstractValidator;
import ci.gouv.budget.solde.sigdcp.service.utils.validaton.MissionExecuteeValidator;

@Named @ViewScoped @Getter @Setter
public class OrganiserMissionController extends AbstractDemandeController<MissionExecutee> implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/*
	 * Services
	 */
	
	@Inject private AgentEtatService agentEtatService;
	@Inject private MissionExecuteeService missionService;
	@Inject private DossierMissionService dossierMissionService;
	@Inject private LocaliteGroupeMissionService localiteGroupeMissionService;
	
	@Inject private MissionExecuteeValidator missionExecuteeValidator;
	/*
	 * Dtos
	 */
	//private UploadedFile communicationFichier;
	private MissionExecuteeDto missionExecuteeDto;
	@Getter private Collection<Participant> participants = new LinkedList<>();
	
	private String matricule;
	boolean courrierEditable = false;
	
	@Override
	protected void initialisation() {
		super.initialisation();
		String action = Faces.getRequestParameter(webConstantResources.getRequestParamCrudType());
		Long id = null;
		try {
			id = Long.parseLong(Faces.getRequestParameter(webConstantResources.getRequestParamMission()));
		} catch (NumberFormatException e) {}
		missionExecuteeDto = missionService.findSaisieByNumero(id);
		entity = missionExecuteeDto.getMissionExecutee();
		
		title="Organiser une mission hors Côte d'Ivoire";
		
		if(StringUtils.isEmpty(action))
			action = webConstantResources.getRequestParamCrudRead();
		
		if(action.equals(webConstantResources.getRequestParamCrudRead())){
			crudType = CRUDType.READ;
		}else if(action.equals(webConstantResources.getRequestParamCrudCreate()) || action.equals(webConstantResources.getRequestParamCrudUpdate())){
			switch(missionExecuteeDto.getNatureOperationCode()){
			case Code.NATURE_OPERATION_SAISIE:crudType = CRUDType.CREATE;break;
			case Code.NATURE_OPERATION_SOUMISSION:crudType = CRUDType.UPDATE;break;
			case Code.NATURE_OPERATION_DEPOT:
				crudType = CRUDType.READ;
				courrierEditable=true;
				break;
			default:crudType = CRUDType.READ;break;
			}
		}else if(action.equals(webConstantResources.getRequestParamCrudDelete())){
			crudType = CRUDType.DELETE;
		}
		
		for(DossierDto dossierDto : missionExecuteeDto.getDossierDtos()){
			Participant participant = new Participant(entity,dossierDto,
					pieceJustificativeService.findByDossier(dossierDto.getDossier(), null, null), pieceJustificativeService, fichierService,isEditable() &&
					!Code.NATURE_OPERATION_TRANSMISSION_SAISIE_A_BENEFICIAIRE.equals(dossierDto.getDossier().getDernierTraitement().getOperation().getNature().getCode()));
			participant.setCourrierDto(new CourrierDto(dossierDto.getDossier().getCourrier()));
			if(courrierEditable){
				participant.getCourrierDto().setCourrierEditable(true); 
				participant.getCourrierDto().setShowCourrier(true);
			}else{
				participant.getCourrierDto().setShowCourrier(participant.getCourrierDto().getCourrier()!=null && StringUtils.isNotEmpty(participant.getCourrierDto().getCourrier().getNumero()));
				participant.getCourrierDto().setCourrierEditable(false);
				
				//dossierMissionDto.setEditable(!Code.NATURE_OPERATION_TRANSMISSION_SAISIE_A_BENEFICIAIRE.equals(dossierDto.getDossier().getDernierTraitement().getOperation().getNature().getCode()));	
			}
			participants.add(participant);
		}
		
		warnOnClosing(!CRUDType.READ.equals(crudType) && !CRUDType.DELETE.equals(crudType) || courrierEditable);
		defaultSubmitCommand.setValue(text("bouton.soumettre"));
		pieceJustificativeUploader.setTitle("Actes administratifs");
		pieceJustificativeUploader.addPieceJustificative(missionExecuteeDto.getCommunication(), isEditable());
		pieceJustificativeUploader.update();
		requiredEnabled=true;
	}
	
	@Override
	protected void afterInitialisation() {
		super.afterInitialisation();
		switch(crudType){
			case CREATE:
				enregistrerCommand.setRendered(true);
				defaultSubmitCommand.setRendered(false);
				break;
			case READ:
				enregistrerCommand.setRendered(false);
				defaultSubmitCommand.setRendered(courrierEditable);
				break;
			case UPDATE:
				enregistrerCommand.setRendered(true);
				defaultSubmitCommand.setRendered(missionExecuteeDto.getTousPresent());
				break;
			case DELETE:
				defaultSubmitCommand.setValue(text("bouton.annuler"));
				enregistrerCommand.setRendered(false);
				defaultSubmitCommand.setRendered(true);
				break;
		}
		
	}
	
	public Boolean estChezBeneficiaire(Participant tab){
		return tab.getDossierDto().getDossier().getDernierTraitement()!=null && 
				Code.NATURE_OPERATION_TRANSMISSION_SAISIE_A_BENEFICIAIRE.equals(tab.getDossierDto().getDossier().getDernierTraitement().getOperation().getNature().getCode());
	}
	
	private void enregistrer(ActionType actionType) throws Exception {
		pieceJustificativeUploader.process();
		missionExecuteeDto.getCommunication().setFichier(pieceJustificativeUploader.getPieceJustificatives().iterator().next().getFichier());
		//Collection<Collection<PieceJustificative>> pieceJustificatives = new LinkedList<>();
		Collection<DossierDto> dtos = new LinkedList<>();
		for(Participant participant : participants){
			dtos.add(participant.getDossierDto());
			//System.out.println("T : "+tab.getDossierDto().getTransmettreBeneficiaire());
			participant.getDossierDto().setPieceJustificatives(participant.getPieceJustificativeUploader().process());
		}
		
		
		missionService.enregistrer(actionType,entity,missionExecuteeDto.getCommunication(),dtos);
		
	}

	@Override
	protected void enregistrer() throws Exception {
		enregistrer(ActionType.ENREGISTRER);
	}
	
	@Override
	protected void onDefaultSubmitAction() throws Exception {
		if(Code.NATURE_OPERATION_DEPOT.equals(missionExecuteeDto.getNatureOperationCode())){
			Collection<DossierMission> d = new ArrayList<>();
			for(Participant participant : participants){
				if(!StringUtils.isEmpty(participant.getDossierDto().getDossier().getCourrier().getNumero()))
					d.add((DossierMission) participant.getDossierDto().getDossier());
			}
			dossierMissionService.deposer(d);
		}else
			enregistrer(ActionType.SOUMETTRE);
	}

	public void ajouterParticipant(){
		if(StringUtils.isEmpty(matricule))
			return;
		matricule = matricule.trim();
		boolean exist = false;
		for(Participant dto : participants)
			if(dto.getMatricule().equals(matricule)){
				exist=true;
				break;
			}
		Participant participant = new Participant(entity,matricule,agentEtatService.findByMatricule(matricule),pieceJustificativeService,fichierService,isEditable());
		if(!exist && participants.add(participant)){
			matricule = null;
			//participant.getDossierMission().setIndemnite(new MontantIndemniteMission());
		}
	}
	
	public void supprimerParticipant(Participant participant){
		//System.out.println(dossierMissionDtos.size());
		//((List)dossierMissionDtos).remove(0);
		participants.remove(participant);
	}
	
	public void transmettre(Participant participant){
		missionService.transmettreDossier(Arrays.asList((DossierMission)participant.getDossierDto().getDossier()));
	}
	
	@Override
	protected String onSoumettreSuccessOutcome() {
		return navigationManager.url(NavigationManager.OUTCOME_SUCCESS_VIEW,new Object[]{webConstantResources.getRequestParamMessageId(),"notification.demande.soumise",
				webConstantResources.getRequestParamUrl(),navigationManager.url("missionexecuteeliste",
						new Object[]{webConstantResources.getRequestParamNextViewOutcome(),"demande_mission_pointfocal"},false,false)},true);
	}
	
	@Override
	protected AbstractValidator<MissionExecutee> validator() {
		return missionExecuteeValidator;
	}
	
	public void destinationValueChange(ValueChangeEvent valueChangeEvent){
		
		
	}
	
	public void fonctionValueChange(ValueChangeEvent valueChangeEvent){
		/*
		Localite destination = attribute(valueChangeEvent, Localite.class);
		dossierMission.getIndemnite().setFraisMission(lgm.getIndemnite());
		LocaliteGroupeMission lgm = localiteGroupeMissionService.
				findByFonctionOrGradeByLocalite(attribute(valueChangeEvent, Fonction.class), attribute(valueChangeEvent, Grade.class), (Localite)valueChangeEvent.getNewValue());
		*/
		
	}
	
	public void professionValueChange(ValueChangeEvent valueChangeEvent){
		/*
		LocaliteGroupeMission lgm = localiteGroupeMissionService.
				findByFonctionOrGradeByLocalite(attribute(valueChangeEvent, Fonction.class), attribute(valueChangeEvent, Grade.class), (Localite)valueChangeEvent.getNewValue());
		DossierMission dossierMission = attribute(valueChangeEvent, DossierMission.class);
		dossierMission.getIndemnite().setFraisMission(lgm.getIndemnite());
		*/
	}
	
	
}
	