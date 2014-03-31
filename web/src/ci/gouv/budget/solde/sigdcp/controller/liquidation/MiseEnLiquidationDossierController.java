package ci.gouv.budget.solde.sigdcp.controller.liquidation;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import ci.gouv.budget.solde.sigdcp.controller.dossier.validation.AbstractValidationDossierController;
import ci.gouv.budget.solde.sigdcp.model.Code;
import ci.gouv.budget.solde.sigdcp.model.dossier.BulletinLiquidation.AspectLiquide;
import ci.gouv.budget.solde.sigdcp.model.dossier.DossierDto;
import ci.gouv.budget.solde.sigdcp.model.dossier.NatureDeplacement;

@Named @ViewScoped
public class MiseEnLiquidationDossierController extends AbstractValidationDossierController implements Serializable {

	private static final long serialVersionUID = -2412073347414420827L;
	
	@Override
	protected void initialisation() {
		super.initialisation();
		showBulletinNumero=Boolean.FALSE;
		showMontant=true;
		
		showNumero=true;
		listTitle="Liste des demandes";
		aspectLiquides = Arrays.asList(AspectLiquide.values());
		montantPourcentages = new ArrayList<>();
		montantPourcentages.add(new SelectItem(new BigDecimal("0.2"),"20%"));
		montantPourcentages.add(new SelectItem(new BigDecimal("0.8"),"80%"));
		montantPourcentages.add(new SelectItem(new BigDecimal("1"),"100%"));
		updateAspectLiquide(natureDeplacement);
	}
	
	@Override
	protected String natureOperationCode() {
		return Code.NATURE_OPERATION_ETABLISSEMENT_BL;
	}
	
	@Override
	protected String defaultNatureDeplacementCode() {
		return Code.NATURE_DEPLACEMENT_MISSION_HCI;
	}
	
	@Override
	protected Collection<DossierDto> data(Collection<NatureDeplacement> natureDeplacements) {
		return dossierService.findALiquiderByNatureDeplacementsByAspectLiquide(natureDeplacements, aspectLiquide);
	}
		
	public void natureDeplacementChange(ValueChangeEvent valueChangeEvent){
		updateAspectLiquide((NatureDeplacement)valueChangeEvent.getNewValue());
		RequestContext.getCurrentInstance().update("form:crpg");
	}
	
	public void updateAspectLiquide(NatureDeplacement natureDeplacement){
		if(showAspectLiquide = natureDeplacement!=null && Code.NATURE_DEPLACEMENT_MISSION_HCI.equals( natureDeplacement.getCode() )){
			if(aspectLiquide==null)
				aspectLiquide = AspectLiquide.DEMANDE;
			showMontantPourcentage = AspectLiquide.DEMANDE.equals(aspectLiquide);
		}else{
			aspectLiquide = null;
			showMontantPourcentage = false;
		}
	}
	
	/*
	@Override
	public void aspetLiquideChange(ValueChangeEvent valueChangeEvent) {
		aspectLiquide = (AspectLiquide) valueChangeEvent.getNewValue();
		showMontantPourcentage = AspectLiquide.DEMANDE.equals(aspectLiquide);
	}
	
	@Override
	public Boolean getShowMontantPourcentage() {
		return AspectLiquide.DEMANDE.equals(aspectLiquide);
	}
	
	@Override
	protected void processLoaded(DossierDto dto) {
		if(getShowMontantPourcentage())
			dto.setMontantPourcentage(new BigDecimal("0.8"));
	}*/
		
}
