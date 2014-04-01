package ci.gouv.budget.solde.sigdcp.model.dossier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ci.gouv.budget.solde.sigdcp.model.AbstractModel;

@Getter @Setter @EqualsAndHashCode(of="dossier",callSuper=false)
public class DossierDto extends AbstractModel<Long> implements Serializable {

	private static final long serialVersionUID = -5721536934192986176L;

	/**
	 * Infos pourvues par le système
	 */
	private Dossier dossier;
	private Date dateCreation;
	private Collection<BulletinLiquidation> bulletinLiquidations = new ArrayList<>();
	
	private TraitementDossier traitement = new TraitementDossier();
	private String courrier;
	private List<TraitementDossier> historiqueTraitements;
	private List<ValidationType> validationTypes = new ArrayList<>();
	private String natureOperationCode;
	private PieceJustificative pieceAdministrative;
	private Collection<PieceJustificative> pieceJustificativesNonEditable=new ArrayList<>();
	
	/**
	 * Infos pourvues par le client
	 */
	private BulletinLiquidation bulletinLiquidationSaisie;
	private Boolean transmettreBeneficiaire=false,annuler=false;
	private Collection<PieceJustificative> pieceJustificatives;
	private Boolean marie=false;
	private Integer nombreEnfant=0;
	private TypeDepense typeDepense;
	
	public DossierDto(Dossier dossier) {
		super();
		this.dossier = dossier;
		if(dossier!=null && dossier.getDeplacement()!=null)
			typeDepense = dossier.getDeplacement().getTypeDepense();
	}
	
	public String getNumero(){
		return dossier.getNumero();
	}
	/*
	@Deprecated
	public Boolean isNouveau(){
		return StringUtils.isEmpty(dossier.getNumero());
	}*/
	
	public DossierDD getAsDossierDD(){
		return (DossierDD) dossier;
	}
	
	public DossierMission getAsDossierMission(){
		return (DossierMission) dossier;
	}
	
	
}
