package ci.gouv.budget.solde.sigdcp.controller.dossier;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;
import org.omnifaces.util.Faces;

import ci.gouv.budget.solde.sigdcp.controller.NavigationManager;
import ci.gouv.budget.solde.sigdcp.controller.application.AbstractDemandeController;
import ci.gouv.budget.solde.sigdcp.model.Code;
import ci.gouv.budget.solde.sigdcp.model.dossier.Dossier;
import ci.gouv.budget.solde.sigdcp.model.dossier.DossierDto;
import ci.gouv.budget.solde.sigdcp.model.dossier.DossierMission;
import ci.gouv.budget.solde.sigdcp.model.dossier.NatureDeplacement;
import ci.gouv.budget.solde.sigdcp.model.dossier.PieceJustificative;
import ci.gouv.budget.solde.sigdcp.model.dossier.PieceJustificativeAFournir;
import ci.gouv.budget.solde.sigdcp.model.dossier.TypeDepense;
import ci.gouv.budget.solde.sigdcp.model.identification.AgentEtat;
import ci.gouv.budget.solde.sigdcp.model.identification.Personne;
import ci.gouv.budget.solde.sigdcp.service.ActionType;
import ci.gouv.budget.solde.sigdcp.service.dossier.AbstractDossierService;
import ci.gouv.budget.solde.sigdcp.service.resources.CRUDType;
import ci.gouv.budget.solde.sigdcp.service.utils.ServiceUtils;

@Getter @Setter
public abstract class AbstractFaireDemandeController<DOSSIER extends Dossier,DOSSIER_SERVICE extends AbstractDossierService<DOSSIER>> extends AbstractDemandeController<DOSSIER> implements Serializable {
	
	private static final long serialVersionUID = 6615049982603373278L;
	
	@Inject private ServiceUtils serviceUtils;
	
	@Setter @Getter protected NatureDeplacement natureDaplacement;
	protected DossierDto dossierDto;
	protected CourrierDto courrierDto;
	protected Boolean showHistoriqueTraitements=Boolean.TRUE,
			showBulletinLiquidation=Boolean.FALSE;
	
	
	@SuppressWarnings("unchecked")
	@Override
	protected void initialisation() {
		super.initialisation();
		
		dossierDto = getDossierService().findSaisieByNatureDeplacement(dtoNatureDeplacement(entity),
				Faces.getRequestParameter(webConstantResources.getRequestParamDossier()),Faces.getRequestParameter(webConstantResources.getRequestParamNatureOperation()));
		
		entity = (DOSSIER) dossierDto.getDossier();
		
		if(entity==null){
			crudType = CRUDType.READ;
			messageManager.addInfo("Vous n'avez aucun dossier à completer.");
			try {
				Faces.redirect(navigationManager.url("succes",new Object[]{webConstantResources.getRequestParamMessageId(),"notification.aucunedemandeacompleter"},false,false));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		
		//boolean ced = TypeSaisie.COURRIER.equals(dossierDto.getTypeSaisie());
		courrierDto = new CourrierDto(entity.getCourrier()/*, ced || dossierDto.getDossier().getCourrier()!=null && StringUtils.isNotEmpty(dossierDto.getDossier().getCourrier().getNumero()),ced*/);
		/*
		if(entity.getDeplacement().getTypeDepense()==null)
			parametres = new HashMap<String, Object>();
		else
			parametres = pieceJustificativeService.findParametresByDossier(entity, pieceJustificativeUploader.getPieceJustificatives());
		*/
		title = entity.getDeplacement().getNature().getLibelle();
		//instructions = getDossierService().findInstructions(entity);
		
		showHistoriqueTraitements = dossierDto.getHistoriqueTraitements()!=null && !dossierDto.getHistoriqueTraitements().isEmpty();
		
		// Affichage du formulaire
		
		String action = action();
			
		if(action.equals(webConstantResources.getRequestParamCrudRead())){
			crudType = CRUDType.READ;
			courrierDto.setShowCourrier(courrierDto.getCourrier()!=null && StringUtils.isNotEmpty(courrierDto.getCourrier().getNumero()));
			courrierDto.setCourrierEditable(false);
			
		}else if(action.equals(webConstantResources.getRequestParamCrudCreate()) || action.equals(webConstantResources.getRequestParamCrudUpdate())){
			switch(dossierDto.getNatureOperationCode()){
			case Code.NATURE_OPERATION_SAISIE:crudType =operationSaisie();break;
			case Code.NATURE_OPERATION_SOUMISSION:crudType = dossierDto.getDossier() instanceof DossierMission?CRUDType.READ:CRUDType.UPDATE;break;
			case Code.NATURE_OPERATION_TRANSMISSION_SAISIE_A_ORGANISATEUR:crudType = CRUDType.UPDATE;break;
			case Code.NATURE_OPERATION_DEPOT:
				crudType = CRUDType.READ;
				courrierDto.setCourrier(dossierDto.getDossier().getCourrier());
				courrierDto.setCourrierEditable(true); 
				courrierDto.setShowCourrier(true);
				break;
			default:crudType = CRUDType.READ;break;
			}
			//crudType = TypeSaisie.INFOS.equals(dossierDto.getTypeSaisie())?CRUDType.UPDATE:CRUDType.READ;	
		}else if(action.equals(webConstantResources.getRequestParamCrudDelete())){
			crudType = CRUDType.DELETE;
			courrierDto.setShowCourrier(courrierDto.getCourrier()!=null && StringUtils.isNotEmpty(courrierDto.getCourrier().getNumero()));
			courrierDto.setCourrierEditable(false);
		}
		
		updatePieceJustificatives();
		warnOnClosing(!CRUDType.READ.equals(crudType) && !CRUDType.DELETE.equals(crudType) || courrierDto.getCourrierEditable());
		defaultSubmitCommand.setValue(text("bouton.soumettre"));
	}
	
	protected NatureDeplacement dtoNatureDeplacement(DOSSIER dossier){
		return natureDaplacement;// dossier.getDeplacement().getNature();
	}
	
	protected CRUDType operationSaisie(){
		return CRUDType.CREATE;
	}
	
	@Override
	protected void afterInitialisation() {
		if(entity==null) return;
		super.afterInitialisation();
		switch(crudType){
		case CREATE:
			enregistrerCommand.setRendered(true);
			defaultSubmitCommand.setRendered(false);
			break;
		case READ:
			enregistrerCommand.setRendered(false);
			defaultSubmitCommand.setRendered(courrierDto.getCourrierEditable());
			break;
		case UPDATE:
			enregistrerCommand.setRendered(true);
			defaultSubmitCommand.setRendered(!Code.NATURE_OPERATION_TRANSMISSION_SAISIE_A_ORGANISATEUR.equals(dossierDto.getNatureOperationCode()));
			break;
		case DELETE:
			defaultSubmitCommand.setValue(text("bouton.annuler"));
			enregistrerCommand.setRendered(false);
			defaultSubmitCommand.setRendered(true);
			break;
		}
	}
	
	protected String action(){
		String action = Faces.getRequestParameter(webConstantResources.getRequestParamCrudType());
		if(StringUtils.isEmpty(action))
			action = webConstantResources.getRequestParamCrudRead();
		return action;
	}
		
	@Override
	protected void enregistrer() throws Exception {
		dossierDto.setPieceJustificatives( pieceJustificativeUploader.process());
		getDossierService().enregistrer(ActionType.ENREGISTRER,dossierDto);
	}
		
	@Override
	protected Collection<PieceJustificativeAFournir> onEnregistrerSuccessPieceJustificativeAFournir() {
		return pieceJustificativeAFournirService.findDeriveeRestantByDossier(entity, pieceJustificativeUploader.getPieceJustificatives());
	}
	
	protected Personne creerPar(DOSSIER dossier){
		return beneficiaire(dossier);
	}
	
	protected AgentEtat beneficiaire(DOSSIER dossier){
		return (AgentEtat) userSessionManager.getUser();
	}
	
	protected void updatePieceJustificatives(){
		getDossierService().mettreAJourPiecesJustificatives(dossierDto);
		pieceJustificativeUploader.clear();
		for(PieceJustificative pieceJustificative : dossierDto.getPieceJustificatives())
			pieceJustificativeUploader.addPieceJustificative(pieceJustificative,!dossierDto.getPieceJustificativesNonEditable().contains(pieceJustificative) && isEditable());
		pieceJustificativeUploader.update();
	}
	
	protected abstract DOSSIER_SERVICE getDossierService();
		
	@Override
	protected void onDefaultSubmitAction() throws Exception {
		switch(dossierDto.getNatureOperationCode()){
		case Code.NATURE_OPERATION_SOUMISSION:
			dossierDto.setPieceJustificatives( pieceJustificativeUploader.process());
			getDossierService().enregistrer(ActionType.SOUMETTRE, dossierDto);
			break;
		case Code.NATURE_OPERATION_DEPOT:
			getDossierService().deposer(Arrays.asList(entity));
			break;
		case Code.NATURE_OPERATION_ANNULATION_DEMANDE:
			getDossierService().annuler(Arrays.asList(entity));
			break;
		}		
	}
	
	@Override
	protected String onSoumettreSuccessOutcome() {
		return navigationManager.url(NavigationManager.OUTCOME_SUCCESS_VIEW,new Object[]{webConstantResources.getRequestParamMessageId(),"notification.demande.soumise",
				webConstantResources.getRequestParamUrl(),navigationManager.url("demandeliste",null,false,false)},true);
	}
	
	@Override
	public void typeDepenseListener(ValueChangeEvent valueChangeEvent) {
		dossierDto.setTypeDepense((TypeDepense) valueChangeEvent.getNewValue());
		updatePieceJustificatives();
	}
	
}
		
