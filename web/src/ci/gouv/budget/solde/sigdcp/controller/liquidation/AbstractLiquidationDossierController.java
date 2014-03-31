package ci.gouv.budget.solde.sigdcp.controller.liquidation;

import java.io.Serializable;
import java.util.Date;

import ci.gouv.budget.solde.sigdcp.controller.dossier.validation.AbstractValidationDossierController;
import ci.gouv.budget.solde.sigdcp.model.dossier.DossierDto;

@Deprecated
public abstract class AbstractLiquidationDossierController /*extends AbstractValidationDossierController*/ implements Serializable {

	private static final long serialVersionUID = -2412073347414420827L;
	/*
	@Override
	protected void initialisation() {
		super.initialisation();
		showMontant=Boolean.FALSE;
		showBulletinNumero=Boolean.TRUE;
		showNumero=true;
		listTitle="Liste des bulletins de liquidation";
		
	}
	
	@Override
	protected String[] defaultNatureDeplacmentCodeListe() {
		return touteLesDepenses();
	}
	
	@Override
	public String numero(DossierDto data) {
		return data.getBulletinLiquidation()==null?"---":data.getBulletinLiquidation().getNumero();
	}
	
	@Override
	public Date dateCreation(DossierDto data) {
		return data.getDateCreationBL();
	}*/
	
}
