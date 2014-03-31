package ci.gouv.budget.solde.sigdcp.controller.application;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;
import org.omnifaces.util.Faces;

import ci.gouv.budget.solde.sigdcp.model.identification.AgentEtat;
import ci.gouv.budget.solde.sigdcp.model.identification.CompteUtilisateur;
import ci.gouv.budget.solde.sigdcp.model.identification.Personne;
import ci.gouv.budget.solde.sigdcp.service.utils.TextService;

@Named @SessionScoped 
public class UserSessionManager implements Serializable{

	private static final long serialVersionUID = 258649685790992448L;

	/*
	 * Services
	 */
	@Inject transient private TextService textService;
	@Inject transient private RoleManager roleManager;
	@Getter @Setter
	private CompteUtilisateur compteUtilisateur; 
	
	/*------------------------------------- UI ----------------------------------------------------*/
	
	@Getter private Integer uiMenuTabLiquidationEtablirIndex=0;
	@Getter private Integer uiMenuTabLiquidationValiderIndex=0;
	@Getter private Integer uiMenuTabLiquidationTransmettreIndex=0;
	@Getter private Integer uiMenuTabLiquidationViserIndex=0;
	@Getter private Integer uiMenuTabLiquidationProduireBTIndex=0;
	@Getter private Integer uiMenuTabLiquidationValiderBTIndex=0;
	@Getter private Integer uiMenuTabLiquidationTransmettreBTIndex=0;
	
	@PostConstruct
	private void postConstruct(){
		initUiTabMenuLiquidationIndex();
	}
	
	public Boolean isLoggedIn(){
		return StringUtils.isNotEmpty(Faces.getRemoteUser());
	}
	
	/**
	 * The connected user
	 * @return
	 */
	@Named @SessionScoped @User
	public Personne getUser(){
		return compteUtilisateur==null?null:(Personne) compteUtilisateur.getUtilisateur();
	}
	
	public String getUserInfosLine(){
		if(compteUtilisateur==null)
			return null;
		if(compteUtilisateur.getUtilisateur() instanceof AgentEtat)
			/*return textService.find("userinfos.top.agentetat",new Object[]{((AgentEtat)compteUtilisateur.getUtilisateur()).getMatricule(),
					((AgentEtat)compteUtilisateur.getUtilisateur()).getNomPrenoms()});*/
			return textService.find("userinfos.top.agentetat",new Object[]{((AgentEtat)compteUtilisateur.getUtilisateur()).getNomPrenoms()});
		return null;
	}
	
	public String getAccueil(){
		if(roleManager.isDirecteur())
			return "viserbulletinliquidation";
		else if(roleManager.isResponsable())
			return "validationrecevabilite";
		else if(roleManager.isLiquidateur())
			return "mettreenliquidation";
		else if(roleManager.isPointFocal())
			return "missionexecuteeliste";
		else if(roleManager.isPayeur())
			return "priseenchargebordereau";
		else if(roleManager.isAgentEtat())
			return "espacePrivee"; 
		return "index";
	}

	/**/
	
	private void initUiTabMenuLiquidationIndex(){
		int l=0;
		//tab 1
		if(roleManager.isLiquidateur())
			uiMenuTabLiquidationEtablirIndex=l++;
		//tab 2
		if(roleManager.isResponsable())
			uiMenuTabLiquidationValiderIndex=l++;
		//tab 3
		if(roleManager.isResponsable())
			uiMenuTabLiquidationTransmettreIndex=l++;
		//tab 4
		if(roleManager.isDirecteur())
			uiMenuTabLiquidationViserIndex=l++;
		//tab 5
		if(roleManager.isResponsable())
			uiMenuTabLiquidationProduireBTIndex=l++;
		//tab 6
		if(roleManager.isDirecteur())
			uiMenuTabLiquidationValiderBTIndex=l++;
		//tab 7
		if(roleManager.isDirecteur())
			uiMenuTabLiquidationTransmettreBTIndex=l++;
	}
	
}
