package ci.gouv.budget.solde.sigdcp.controller;

import java.io.Serializable;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.java.Log;

import org.primefaces.model.UploadedFile;

import ci.gouv.budget.solde.sigdcp.controller.application.UserSessionManager;
import ci.gouv.budget.solde.sigdcp.model.dossier.PieceJustificativeAFournir;
import ci.gouv.budget.solde.sigdcp.model.geographie.Localite;
import ci.gouv.budget.solde.sigdcp.model.identification.AgentEtat;
import ci.gouv.budget.solde.sigdcp.model.identification.Fonction;
import ci.gouv.budget.solde.sigdcp.model.identification.Grade;
import ci.gouv.budget.solde.sigdcp.model.identification.Profession;
import ci.gouv.budget.solde.sigdcp.model.identification.Section;
import ci.gouv.budget.solde.sigdcp.model.identification.TypeAgentEtat;
import ci.gouv.budget.solde.sigdcp.service.utils.validaton.ValidationPolicy;
import ci.gouv.budget.solde.sigdcp.service.utils.validaton.ValidationPolicy.InfosFichierATelecharger;

/**
 * Ensemble de methode pour la validation des champs de saisie.<br/>
 * Une methode doit etre liee a un champ de saisie dans un formulaire en vue de capturer le message
 * dans son composant <b>message</b> associe. <b>f:attribute</b> devra etre utiliser si d'autres champs
 * de saisie sont necessaire lors de la validation du champ a valider
 * 
 *<br/><br/>
 *Aucune de ces methode ne doit implementer une logique de validation mais plutot solliciter l'une des methode du service dans la classe
 *<code>ValidationPolicy</code>
 * 
 * @author Komenan Y .Christian
 *
 */
@Named @Log
public class ValidationManager implements Serializable {

	private static final long serialVersionUID = -5187172212708031726L;
	
	@Inject private ValidationPolicy validation; 
	@Inject private UserSessionManager userSessionManager;
	
	/* Identification */
	
	public void validateMatricule(FacesContext facesContext,UIComponent uiComponent,Object value){
		try {
			validation.validateMatricule(attribute(uiComponent, TypeAgentEtat.class, "typePersonne"), (String) value);
		} catch (Exception e) {
			validationException(uiComponent,e);
		}		
	}
	
	public void validateDateNaissance(FacesContext facesContext,UIComponent uiComponent,Object value){
		try {
			validation.validateDateNaissance((Date) value);
		} catch (Exception e) {
			validationException(uiComponent,e);
		}
	}
	
	public void validatePassword(FacesContext facesContext,UIComponent uiComponent,Object value){
		try {
			validation.validatePassword((String) value);
		} catch (Exception e) {
			validationException((UIInput) uiComponent, e.getMessage());
		}
	}
	
	public void validateConfirmationPassword(FacesContext facesContext,UIComponent uiComponent,Object value){
		try {
			validation.validateConfirmationPassword(attribute(uiComponent, String.class, "password"),(String) value);
		} catch (Exception e) {
			validationException((UIInput) uiComponent, e.getMessage());
		}
	}
	
	public void validateCodeDeverouillage(FacesContext facesContext,UIComponent uiComponent,Object value){
		try {
			validation.validateCodeDeverouillage((String) value);
		} catch (Exception e) {
			validationException(uiComponent, e.getMessage());
		}
	}
	
	public void validateAdresseElectroniqueCompte(FacesContext facesContext,UIComponent uiComponent,Object value){
		try {
			validation.validateUsernameUnique((String) value);
		} catch (Exception e) {
			validationException(uiComponent, e.getMessage());
		}
	}
	
	/* Demande */
	
	public void validateGrade(FacesContext facesContext,UIComponent uiComponent,Object value){
		try {
			validation.validateGrade((Grade) value);
		} catch (Exception e) {
			validationException(uiComponent, e.getMessage());
		}
	}
	
	public void validateDatePriseService(FacesContext facesContext,UIComponent uiComponent,Object value){
		try {
			validation.validateDatePriseService( (AgentEtat)userSessionManager.getUser(), (Date)value);
		} catch (Exception e) {
			validationException(uiComponent, e.getMessage());
		}
	}
	
	public void validateDateArrivee(FacesContext facesContext,UIComponent uiComponent,Object value){
		try {
			validation.validateDateArrivee(attribute(uiComponent, Date.class, "dateDepart"),(Date) value);
		} catch (Exception e) {
			validationException(uiComponent, e.getMessage());
		}
	}
	
	public void validateLocaliteDepart(FacesContext facesContext,UIComponent uiComponent,Object value){
		try {
			validation.validateVilleDepart((AgentEtat)userSessionManager.getUser(),(Localite) value);
		} catch (Exception e) {
			validationException(uiComponent, e.getMessage());
		}
	}
	
	public void validateLocaliteArrivee(FacesContext facesContext,UIComponent uiComponent,Object value){
		try {
			validation.validateVilleArrivee(attribute(uiComponent, Localite.class, "localiteDepart"),(Localite) value);
		} catch (Exception e) {
			validationException(uiComponent, e.getMessage());
		}
	}
	
	public void validateServiceAccueil(FacesContext facesContext,UIComponent uiComponent,Object value){
		try {
			validation.validateServiceAccueil((Section) value);
		} catch (Exception e) {
			validationException(uiComponent, e.getMessage());
		}
	}
	
	public void validatePieceJustificativeNumero(FacesContext facesContext,UIComponent uiComponent,Object value){
		
		try {
			//System.out.println( uiComponent.getAttributes().get("fichier") );
			//UploadedFile file = (UploadedFile) attribute(uiComponent, FileUpload.class, "fichier").getValue();
			InfosFichierATelecharger fichier = null;//new InfosFichierATelecharger(file.getFileName(),file.getSize());
			Date dateEtatblissement = null;//attribute(uiComponent, Date.class, "dateEtablissement");
			Fonction signataire = null; //attribute(uiComponent, Fonction.class, "signataire")
			PieceJustificativeAFournir model = null;//(PieceJustificativeAFournir)uiComponent.getAttributes().get("model")
			//validation.validatePieceJustificativeNumero(false, (String) value, dateEtatblissement, signataire,fichier,model);
		} catch (Exception e) {
			validationException(uiComponent, e.getMessage());
		}
		
	}
	
	public void validatePieceJustificativeDateEtablissement(FacesContext facesContext,UIComponent uiComponent,Object value){
		
		try {
			//UploadedFile file = (UploadedFile) attribute(uiComponent, FileUpload.class, "fichier").getValue();
			InfosFichierATelecharger fichier = null;//new InfosFichierATelecharger(file.getFileName(),file.getSize());
			String numero = null;//attribute(uiComponent, String.class, "numero")
			Fonction signataire = null; //attribute(uiComponent, Fonction.class, "signataire")
			PieceJustificativeAFournir model = null;//(PieceJustificativeAFournir)uiComponent.getAttributes().get("model")
			//validation.validatePieceJustificativeDateEtablissement(false, numero, (Date) value, signataire,fichier,model);
		} catch (Exception e) {
			validationException(uiComponent, e.getMessage());
		}
		
	}
	
	public void validatePieceJustificativeFonctionSignataire(FacesContext facesContext,UIComponent uiComponent,Object value){
		
		try {
			//UploadedFile file = (UploadedFile) attribute(uiComponent, FileUpload.class, "fichier").getValue();
			InfosFichierATelecharger fichier = null;//new InfosFichierATelecharger(file.getFileName(),file.getSize());
			String numero = null;//attribute(uiComponent, String.class, "numero")
			Date dateEtatblissement = null;//attribute(uiComponent, Date.class, "dateEtablissement");
			PieceJustificativeAFournir model = null;//(PieceJustificativeAFournir)uiComponent.getAttributes().get("model")
			//validation.validatePieceJustificativeFonctionSignataire(false, numero, dateEtatblissement, (Fonction)value,fichier,model);
		} catch (Exception e) {
			validationException(uiComponent, e.getMessage());
		}
		
	}
	
	public void validatePieceJustificativeFichier(FacesContext facesContext,UIComponent uiComponent,Object value){
		
		UploadedFile file = (UploadedFile) value;
		String numero = null;//attribute(uiComponent, String.class, "numero")
		Date dateEtatblissement = null;//attribute(uiComponent, Date.class, "dateEtablissement");
		Fonction signataire = null; //attribute(uiComponent, Fonction.class, "signataire")
		PieceJustificativeAFournir model = null;//(PieceJustificativeAFournir)uiComponent.getAttributes().get("model")
		try {
			//validation.validatePieceJustificativeFichier(false, numero, dateEtatblissement, signataire,new InfosFichierATelecharger(file.getFileName(),file.getSize()),model);
		} catch (Exception e) {
			validationException(uiComponent, e.getMessage());
		}
		
	}
	
	public void validateNumeroDossier(FacesContext facesContext,UIComponent uiComponent,Object value){
		try {
			validation.validateNumeroDossier((String) value);
		} catch (Exception e) {
			validationException(uiComponent, e.getMessage());
		}
	}
	
	public void validateProfessionMission(FacesContext facesContext,UIComponent uiComponent,Object value){
		try {
			validation.validateProfessionMission(attribute(uiComponent, Fonction.class, "fonction"), (Profession) value);
		} catch (Exception e) {
			validationException(uiComponent, e.getMessage());
		}
	}
	
	/*----------------------------------------------------------------------------------------------------------*/
	
	private void validationException(UIInput input,String message){
		input.setValid(Boolean.FALSE);
		throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,message,message));
	}
	
	private void validationException(UIComponent uiComponent,String message){
		if(uiComponent instanceof UIInput)
			validationException((UIInput) uiComponent, message);
		log.severe("Validation of Component Type "+uiComponent.getClass()+" Not handled");
	}
	
	private void validationException(UIComponent uiComponent,Exception exception){
		validationException(uiComponent, exception.getMessage());
	}
	
	@SuppressWarnings("unchecked")
	private <T> T attribute(UIComponent uiComponent,Class<T> type,String name){
		return (T) ((UIInput)uiComponent.getAttributes().get(name)).getValue();
	}

}
