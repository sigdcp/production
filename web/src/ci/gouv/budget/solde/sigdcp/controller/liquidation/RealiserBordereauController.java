package ci.gouv.budget.solde.sigdcp.controller.liquidation;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.omnifaces.util.Faces;

import ci.gouv.budget.solde.sigdcp.model.Code;
import ci.gouv.budget.solde.sigdcp.model.dossier.BordereauTransmissionDto;
import ci.gouv.budget.solde.sigdcp.model.dossier.BulletinLiquidationDto;
import ci.gouv.budget.solde.sigdcp.model.dossier.NatureDeplacement;
import ci.gouv.budget.solde.sigdcp.model.dossier.NatureOperation;
import ci.gouv.budget.solde.sigdcp.service.dossier.BordereauTransmissionService;
import ci.gouv.budget.solde.sigdcp.service.dossier.BulletinLiquidationService;
import ci.gouv.budget.solde.sigdcp.service.resources.CRUDType;

@Named @ViewScoped
public class RealiserBordereauController extends AbstractValidationLiquidationController implements Serializable {

	private static final long serialVersionUID = -2412073347414420827L;
	
	@Inject private BordereauTransmissionService bordereauTransmissionService;
	@Inject private BulletinLiquidationService bulletinLiquidationService;
	
	private BordereauTransmissionDto bordereauTransmissionDto;
		
	@Override
	protected void initialisation() {
		calculerDisponible=true;
		super.initialisation();
		showCheckBox = false;
		showMontant = true;
		showTotalDepense = Boolean.TRUE;
		numeroLibelle=text("bulletin.liquidation.numero");
		addDetailsCommand.setRendered(Faces.isUserInRole(Code.ROLE_DIRECTEUR));
		removeDetailsCommand.setRendered(isEditable() && Faces.isUserInRole(Code.ROLE_DIRECTEUR));
		
		if(Faces.isUserInRole(Code.ROLE_DIRECTEUR)){
			defaultSubmitCommand.setRendered(true);
		}
		defaultSubmitCommand.setRendered(isEditable());
		if(CRUDType.UPDATE.equals(crudType)){
			NatureOperation op = natureOperationService.findById(Code.NATURE_OPERATION_MODIFICATION_BTBL);
			title = op.getLibelle()+" - N° "+bordereauTransmissionDto.getPiece().getNumero();
		}
	}
	
	@Override
	protected String natureOperationCode() {
		return Code.NATURE_OPERATION_ETABLISSEMENT_BTBL;
	}
		
	@Override
	protected void onDefaultSubmitAction() throws Exception {
		switch(crudType){
		case CREATE:bordereauTransmissionService.creer(natureDeplacement,/*Faces.isUserInRole(Code.ROLE_DIRECTEUR)?selectedMultiple:*/list);break;
		case UPDATE:
			bordereauTransmissionService.modifier(bordereauTransmissionDto.getPiece(), list);
			break;
		default:break;
		}
	}
			
	@Override
	protected Collection<BulletinLiquidationDto> data(Collection<NatureDeplacement> natureDeplacements) {
		if(StringUtils.isEmpty(Faces.getRequestParameter(webConstantResources.getRequestParamBordereau())))
			return super.data(natureDeplacements);
		bordereauTransmissionDto = bordereauTransmissionService.findDtoById(Long.parseLong(Faces.getRequestParameter(webConstantResources.getRequestParamBordereau())));
		return bordereauTransmissionDto.getBulletinLiquidationDtos();
	}
	
	@Override
	protected void onAddDetailsCommandAction() throws Exception {
		//System.out.println("RealiserBordereauController.onAddDetailsCommandAction() : "+numeroLigne);
		for(BulletinLiquidationDto dto : list)
			if(dto.getPiece().getNumero().equals(numeroLigne))
				return;
	
		BulletinLiquidationDto dto = bulletinLiquidationService.findByNumero(numeroLigne);
		//System.out.println("Dossier : "+dossier);
		//System.out.println("Dto : "+dossierService.findDtoByDossier(dossier));
		if(disponible.subtract(dto.getPiece().getMontant()).compareTo(BigDecimal.ZERO)>=0){
			list.add(dto);
			numeroLigne = null;
			disponible = disponible.subtract(dto.getPiece().getMontant());
			calculerTotalDepense();
		}else{
			/*
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Impossible d'ajouter ce bulletin de liquidation", "Le disponible courant ne vous permet d'ajouter ce bulletin de liquidation."
					+ "Montant du bulletin de liquidation : "+dto.getPiece().getMontant()+". Disponible courant : "+disponible);  
	        RequestContext.getCurrentInstance().showMessageInDialog(message);
	        RequestContext.getCurrentInstance().execute("alert('Yes');");*/
		}
	}
	
	@Override
	protected void onRemoveDetailsCommandAction(BulletinLiquidationDto dto) throws Exception {
		super.onRemoveDetailsCommandAction(dto);
		disponible = disponible.add(dto.getPiece().getMontant());
		calculerTotalDepense();
	}
	
	@Override
	public Boolean actionable(BulletinLiquidationDto entity) {
		return true;
	}
				
}
