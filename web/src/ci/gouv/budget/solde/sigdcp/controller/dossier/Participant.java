package ci.gouv.budget.solde.sigdcp.controller.dossier;

import java.io.Serializable;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.Setter;
import ci.gouv.budget.solde.sigdcp.controller.fichier.PieceJustificativeUploader;
import ci.gouv.budget.solde.sigdcp.model.Code;
import ci.gouv.budget.solde.sigdcp.model.calendrier.MissionExecutee;
import ci.gouv.budget.solde.sigdcp.model.dossier.DossierDto;
import ci.gouv.budget.solde.sigdcp.model.dossier.DossierMission;
import ci.gouv.budget.solde.sigdcp.model.dossier.PieceJustificative;
import ci.gouv.budget.solde.sigdcp.model.identification.AgentEtat;
import ci.gouv.budget.solde.sigdcp.service.dossier.PieceJustificativeService;
import ci.gouv.budget.solde.sigdcp.service.fichier.FichierService;

@Getter @Setter
public class Participant implements Serializable {

	private static final long serialVersionUID = -4146491673047255659L;

	private DossierDto dossierDto;
	
	private PieceJustificativeUploader pieceJustificativeUploader = new PieceJustificativeUploader();
	private PieceJustificativeService pieceJustificativeService;
	
	private String libelle;
	private Boolean editable = Boolean.FALSE,pieceEditable,transmis;
	private String matricule;
	private CourrierDto courrierDto;

	public Participant(MissionExecutee missionExecutee,DossierDto dossierDto,Collection<PieceJustificative> pieceJustificatives,PieceJustificativeService pieceJustificativeService,FichierService fichierService,Boolean peditable) {
		this(missionExecutee,dossierDto.getDossier().getBeneficiaire().getMatricule(),dossierDto.getDossier().getBeneficiaire(),dossierDto,pieceJustificativeService,fichierService,peditable);
	}
	
	public Participant(MissionExecutee missionExecutee,String matricule,AgentEtat agentEtat,PieceJustificativeService pieceJustificativeService,FichierService fichierService,Boolean peditable) {
		this(missionExecutee,matricule,agentEtat,new DossierDto(new DossierMission(missionExecutee.getDeplacement())),pieceJustificativeService,fichierService,peditable);
	}
	
	public Participant(MissionExecutee missionExecutee,String matricule,AgentEtat agentEtat,DossierDto dossierDto ,PieceJustificativeService pieceJustificativeService,FichierService fichierService,Boolean peditable) {
		super();
		this.matricule=matricule;
		this.dossierDto = dossierDto;
		if(this.dossierDto==null)
			this.dossierDto = new DossierDto(null);
		
		if(this.dossierDto.getDossier().getDeplacement()==null)
			this.dossierDto.getDossier().setDeplacement(missionExecutee.getDeplacement());
		
		this.pieceEditable = peditable;
		this.editable = agentEtat==null;
		libelle=matricule;
		if(agentEtat==null){
			dossierDto.getDossier().setBeneficiaire(new AgentEtat());
			dossierDto.getDossier().getBeneficiaire().setMatricule(matricule);
		}else{
			dossierDto.getDossier().setBeneficiaire(agentEtat);
			libelle += " "+agentEtat.getNomPrenoms();
		}
		if(dossierDto.getDossier().getDernierTraitement()!=null)
			transmis = Code.NATURE_OPERATION_TRANSMISSION_SAISIE_A_BENEFICIAIRE.equals(dossierDto.getDossier().getDernierTraitement().getOperation().getNature().getCode());
		pieceJustificativeUploader.setFichierService(fichierService);
		pieceJustificativeUploader.setEditable(pieceEditable);
		this.pieceJustificativeService = pieceJustificativeService;
		updatePieceJustificatives();
	}
	
	protected void updatePieceJustificatives(boolean first){
		Collection<PieceJustificative> pieceJustificatives;
		//if(first)
			pieceJustificatives = pieceJustificativeService.findByDossier(dossierDto.getDossier(), null, null);
		//else
		//	pieceJustificatives = pieceJustificativeService.findByDossier(dossierMission,pieceJustificativeUploader.getPieceJustificatives(), null);
		
		pieceJustificativeUploader.clear();
		for(PieceJustificative pieceJustificative : pieceJustificatives)
			if(!pieceJustificative.getModel().getTypePieceJustificative().getCode().equals(Code.TYPE_PIECE_COMMUNICATION))
				pieceJustificativeUploader.addPieceJustificative(pieceJustificative,pieceEditable);
		pieceJustificativeUploader.update();
		
	}
	
	protected void updatePieceJustificatives(){
		updatePieceJustificatives(false);
	}

	public AgentEtat getBeneficiaire() {
		return dossierDto.getDossier().getBeneficiaire();
	}
	
	public DossierMission getDossierMission(){
		return (DossierMission) dossierDto.getDossier();
	}
	
	public Boolean getNouveau(){
		return StringUtils.isEmpty(dossierDto.getDossier().getNumero());
	}
	
	@Override
	public int hashCode() {
		return matricule.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return matricule.equalsIgnoreCase(((Participant)obj).getMatricule());
	}
	
}
