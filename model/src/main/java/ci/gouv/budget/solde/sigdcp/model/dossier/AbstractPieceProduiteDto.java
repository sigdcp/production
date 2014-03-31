package ci.gouv.budget.solde.sigdcp.model.dossier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ci.gouv.budget.solde.sigdcp.model.AbstractModel;

@Getter @Setter @EqualsAndHashCode(of="piece",callSuper=false)
public class AbstractPieceProduiteDto<PIECE_PRODUITE extends PieceProduite> extends AbstractModel<Long> implements Serializable {

	private static final long serialVersionUID = -5721536934192986176L;

	protected PIECE_PRODUITE piece;
	protected Date dateCreation;
	protected TraitementPieceProduite traitement = new TraitementPieceProduite();
	protected List<TraitementPieceProduite> historiqueTraitements = new ArrayList<>();
	
	public AbstractPieceProduiteDto(PIECE_PRODUITE piece) {
		super();
		this.piece = piece;
	}
	
	public Long getId(){
		return piece.getId();
	}
	
	
	
}
