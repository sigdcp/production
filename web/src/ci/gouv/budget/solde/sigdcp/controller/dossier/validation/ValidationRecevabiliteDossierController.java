package ci.gouv.budget.solde.sigdcp.controller.dossier.validation;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import ci.gouv.budget.solde.sigdcp.model.Code;

@Named @ViewScoped
public class ValidationRecevabiliteDossierController extends AbstractValidationDossierController implements Serializable {

	private static final long serialVersionUID = -2412073347414420827L;
		
	@Override
	protected String natureOperationCode() {
		return Code.NATURE_OPERATION_RECEVABILITE;
	}
	
	@Override
	protected String[] defaultNatureDeplacmentCodeListe() {
		return new String[]{Code.NATURE_DEPLACEMENT_AFFECTATION,Code.NATURE_DEPLACEMENT_MUTATION,Code.NATURE_DEPLACEMENT_RETRAITE
				,Code.NATURE_DEPLACEMENT_OBSEQUE_FRAIS,Code.NATURE_DEPLACEMENT_TRANSIT_BAGAGGES_MAE,Code.NATURE_DEPLACEMENT_TRANSIT_BAGAGGES_STAGIAIRE,
				Code.NATURE_DEPLACEMENT_MISSION_HCI};
	}
	
		
}
