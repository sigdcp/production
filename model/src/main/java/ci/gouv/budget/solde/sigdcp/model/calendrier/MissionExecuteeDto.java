package ci.gouv.budget.solde.sigdcp.model.calendrier;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

import lombok.Getter;
import lombok.Setter;
import ci.gouv.budget.solde.sigdcp.model.AbstractModel;
import ci.gouv.budget.solde.sigdcp.model.dossier.DossierDto;
import ci.gouv.budget.solde.sigdcp.model.dossier.PieceJustificative;

@Getter @Setter
public class MissionExecuteeDto extends AbstractModel<Long> implements Serializable {

	private static final long serialVersionUID = 4202745380752353762L;

	private MissionExecutee missionExecutee;
	private Collection<DossierDto> dossierDtos = new LinkedList<>();
	private PieceJustificative communication;
	private String natureOperationCode;
	private Boolean tousPresent=true;
	
	public MissionExecuteeDto(MissionExecutee missionExecutee) {
		super();
		this.missionExecutee = missionExecutee;
	}
	
	public Long getId(){
		return missionExecutee.getDeplacement().getId();
	}
	
}
