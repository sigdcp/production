package ci.gouv.budget.solde.sigdcp.service.dossier;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import ci.gouv.budget.solde.sigdcp.dao.dossier.DossierDDDao;
import ci.gouv.budget.solde.sigdcp.dao.dossier.GroupeDDDao;
import ci.gouv.budget.solde.sigdcp.dao.dossier.PieceJustificativeDao;
import ci.gouv.budget.solde.sigdcp.dao.geographie.DistanceEntreLocaliteDao;
import ci.gouv.budget.solde.sigdcp.dao.indemnite.IndemniteTrancheDao;
import ci.gouv.budget.solde.sigdcp.model.Code;
import ci.gouv.budget.solde.sigdcp.model.dossier.CategorieDeplacement;
import ci.gouv.budget.solde.sigdcp.model.dossier.Deplacement;
import ci.gouv.budget.solde.sigdcp.model.dossier.Dossier;
import ci.gouv.budget.solde.sigdcp.model.dossier.DossierDD;
import ci.gouv.budget.solde.sigdcp.model.dossier.DossierDto;
import ci.gouv.budget.solde.sigdcp.model.dossier.PieceJustificative;
import ci.gouv.budget.solde.sigdcp.model.dossier.PieceJustificativeAFournir;
import ci.gouv.budget.solde.sigdcp.model.dossier.TypeDepense;
import ci.gouv.budget.solde.sigdcp.model.geographie.DistanceEntreLocalite;
import ci.gouv.budget.solde.sigdcp.model.identification.Sexe;
import ci.gouv.budget.solde.sigdcp.model.indemnite.GroupeDD;
import ci.gouv.budget.solde.sigdcp.model.indemnite.IndemniteDetailsDD;
import ci.gouv.budget.solde.sigdcp.model.indemnite.IndemniteDetailsPersonneDD;
import ci.gouv.budget.solde.sigdcp.model.indemnite.IndemniteTranche;
import ci.gouv.budget.solde.sigdcp.model.indemnite.TypeIndemniteTranche;
import ci.gouv.budget.solde.sigdcp.service.utils.validaton.DossierDDValidator;

@Stateless
public class DossierDDServiceImpl extends AbstractDossierServiceImpl<DossierDD> implements DossierDDService,Serializable {
	
	private static final long serialVersionUID = -7765679080076677680L;
	
	@Inject private DossierDDValidator dossierDDValidator;
	@Inject private PieceJustificativeDao pieceJustificativeDao;
	@Inject private DistanceEntreLocaliteDao distanceEntreLocaliteDao;
	@Inject private GroupeDDDao groupeDao;
	@Inject private IndemniteTrancheDao indemniteTrancheDao;
	
	@Inject
	public DossierDDServiceImpl(DossierDDDao dao) {
		super(dao);
	}
	
	
	/*	
	@Override
	protected void validationSaisie(DossierDD dossier,Collection<PieceJustificative> pieceJustificatives,Boolean soumission)throws ServiceException {
		//System.out.println("DossierDDServiceImpl.validationSaisie() : ");
		//System.out.println(ToStringBuilder.reflectionToString(dossier, ToStringStyle.MULTI_LINE_STYLE));
		dossierDDValidator.setPieceJustificatives(pieceJustificatives);
		dossierDDValidator.setSoumission(soumission);
		if(!dossierDDValidator.validate(dossier).isSucces())
			throw new ServiceException(dossierDDValidator.getMessagesAsString());
	}*/
	
	@Override
	protected void chargerPiecesJustificatives(DossierDto dto) {
		super.chargerPiecesJustificatives(dto);
		Dossier dossier = dto.getDossier();
		PieceJustificativeAFournir modelPiece = pieceJustificativeAFournirDao.readByNatureDeplacementIdByTypePieceIdByTypeDepenseId(dossier.getDeplacement().getNature().getCode(), Code.TYPE_PIECE_EXTRAIT_MARIAGE,
				dossier.getDeplacement().getTypeDepense().getCode());
		if(modelPiece!=null)
			dto.setMarie(pieceJustificativeDao.countByDossierByTypePieceJustificativeId(dossier, Code.TYPE_PIECE_EXTRAIT_MARIAGE)>0);
		modelPiece = pieceJustificativeAFournirDao.readByNatureDeplacementIdByTypePieceIdByTypeDepenseId(dossier.getDeplacement().getNature().getCode(), Code.TYPE_PIECE_EXTRAIT_NAISSANCE,
				dossier.getDeplacement().getTypeDepense().getCode());
		if(modelPiece!=null)
			dto.setNombreEnfant(pieceJustificativeDao.countByDossierByTypePieceJustificativeId(dossier, Code.TYPE_PIECE_EXTRAIT_NAISSANCE).intValue());
	}
	
	@Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void mettreAJourPiecesJustificatives(DossierDto...dtos) {
		super.mettreAJourPiecesJustificatives(dtos);
		DossierDto dto = dtos[0];
		PieceJustificativeAFournir modelPiece = null;
		int pieceJustificativeExistanteIndex;
		Dossier dossier = dto.getDossier();
		//Extrait de mariage
		modelPiece = pieceJustificativeAFournirDao.readByNatureDeplacementIdByTypePieceIdByTypeDepenseId(dossier.getDeplacement().getNature().getCode(), Code.TYPE_PIECE_EXTRAIT_MARIAGE,
				dossier.getDeplacement().getTypeDepense().getCode());
		if(modelPiece!=null){
			pieceJustificativeExistanteIndex = index(dto, modelPiece);
			if(Boolean.TRUE.equals(dto.getMarie())){//ajouter un extrait de mariage si il n'en existe pas
				if(pieceJustificativeExistanteIndex==-1)
					dto.getPieceJustificatives().add(new PieceJustificative(dossier, modelPiece));
			}else{//supprimer l'extrait de mariage si il en existe
				if(pieceJustificativeExistanteIndex!=-1)
					((List<PieceJustificative>)dto.getPieceJustificatives()).remove(pieceJustificativeExistanteIndex);
			}
		}
		
		//Extrait de naissances des enfants
		modelPiece = pieceJustificativeAFournirDao.readByNatureDeplacementIdByTypePieceIdByTypeDepenseId(dossier.getDeplacement().getNature().getCode(), Code.TYPE_PIECE_EXTRAIT_NAISSANCE,
				dossier.getDeplacement().getTypeDepense().getCode());
		if(modelPiece!=null){
			int n = dto.getNombreEnfant() - count(dto, modelPiece);
			if(n>0){// ajouter 
				for(int i=0;i<n;i++)
					dto.getPieceJustificatives().add(new PieceJustificative(dossier, modelPiece));
			}else{//supprimer
				List<PieceJustificative> list = (List<PieceJustificative>) dto.getPieceJustificatives();
				for(int j=0;n<0 && j<list.size();){
					if(list.get(j).getModel().equals(modelPiece)){
						((List<PieceJustificative>)dto.getPieceJustificatives()).remove(j);
						n++;
					}else
						j++;
				}
			}
		}
	}
	
	@Override @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public BigDecimal calculerMontantIndemnisation(DossierDD dossier) {
		GroupeDD groupe = groupeDao.readByGrade(dossier.getGrade());
		CategorieDeplacement categorieDeplacement = dossier.getDeplacement().getNature().getCategorie();
		IndemniteDetailsDD details = new IndemniteDetailsDD();
		Long nombreEnfant=pieceJustificativeDao.countByDossierByTypePieceJustificativeId(dossier, Code.TYPE_PIECE_EXTRAIT_NAISSANCE) ,
				nombreConjoint=Sexe.MASCULIN.equals(dossier.getBeneficiaire().getSexe())?pieceJustificativeDao.countByDossierByTypePieceJustificativeId(dossier, Code.TYPE_PIECE_EXTRAIT_MARIAGE):0,
				nombreVoyageur=nombreEnfant+nombreConjoint+1;
		
		details.setNombreEnfant(nombreEnfant);	
		details.setGroupe(groupe);
		
		//System.out.println("Conjoint : "+nombreConjoint);
		DistanceEntreLocalite distanceEntreLocalite = distanceEntreLocaliteDao.readByLocalite1ByLocalite2(dossier.getDeplacement().getLocaliteDepart(), dossier.getDeplacement().getLocaliteArrivee());
		IndemniteTranche indemniteTranche = indemniteTrancheDao.readByValeurByType(distanceEntreLocalite.getDistanceKm(), TypeIndemniteTranche.DISTANCE);
		Integer distance = distanceEntreLocalite.getDistanceKm().intValue();
		
		details.setDistance(new BigDecimal(distance));
		
		//Indemnite Kilométrique
		details.setFormuleIkp("Tarif * nbvoyageur");
		if( distance<0 && distance>=150)
			details.setIndemniteKilometrique(indemniteTranche.getMontant().multiply(new BigDecimal(nombreVoyageur)));
		else
			details.setIndemniteKilometrique( distanceEntreLocalite.getDistanceKm().subtract(new BigDecimal(150)).multiply(new BigDecimal(8)).add(new BigDecimal(1550)).
					multiply(new BigDecimal(nombreVoyageur)) );
		
		formule(categorieDeplacement,details.getAgent(),null);
		details.getAgent().setIndemniteBagages(indemniteBagages(categorieDeplacement, distanceEntreLocalite, groupe.getPoidsAutoriseAgent()));
		details.getAgent().setIndemniteJournaliere(indemniteJournalieres(categorieDeplacement, distanceEntreLocalite, groupe.getMontantAgent()));
		
		formule(categorieDeplacement,details.getConjoint(),null);
		details.getConjoint().setIndemniteBagages(indemniteBagages(categorieDeplacement, distanceEntreLocalite, groupe.getPoidsAutoriseEpouse().multiply(new BigDecimal(nombreConjoint)) ));
		details.getConjoint().setIndemniteJournaliere(indemniteJournalieres(categorieDeplacement, distanceEntreLocalite, groupe.getMontantEpouse().multiply(new BigDecimal(nombreConjoint))));
		
		formule(categorieDeplacement,details.getEnfants(),"nbenfants");
		details.getEnfants().setIndemniteBagages(indemniteBagages(categorieDeplacement, distanceEntreLocalite, groupe.getPoidsAutoriseEnfant().multiply(new BigDecimal(nombreEnfant))));
		details.getEnfants().setIndemniteJournaliere(indemniteJournalieres(categorieDeplacement, distanceEntreLocalite, groupe.getMontantEnfant().multiply(new BigDecimal(nombreEnfant))));
		dossier.setIndemniteDetails(details);	
		//System.out.println(dossier.getIndemniteDetails());
		return details.getTotal();
	}
	
	private BigDecimal indemniteBagages(CategorieDeplacement categorieDeplacement,DistanceEntreLocalite distanceEntreLocalite,BigDecimal coefficient){
		return categorieDeplacement.get_c33_5().multiply(distanceEntreLocalite.getDistanceKm().multiply(coefficient)).divide(new BigDecimal(1000));
	}
	
	private BigDecimal indemniteJournalieres(CategorieDeplacement categorieDeplacement,DistanceEntreLocalite distanceEntreLocalite,BigDecimal coefficient){
		return coefficient.multiply(new BigDecimal(categorieDeplacement.getNombreJourIndemniteJournaliere()));
	}
	
	private void formule(CategorieDeplacement categorieDeplacement,IndemniteDetailsPersonneDD details,String facteur){
		details.setFormuleBagages("( "+categorieDeplacement.get_c33_5()+" * d * P "+(facteur==null?"":(" * "+facteur))+" ) / "+categorieDeplacement.get_c1000());
		details.setFormuleJournaliere("Ind * 2 "+(facteur==null?"":(" * "+facteur)));
	}
	
	@Override
	protected DossierDD createDossier() {
		return new DossierDD(new Deplacement(genericDao.readByClass(TypeDepense.class, String.class, Code.TYPE_DEPENSE_REMBOURSEMENT)));
	}

	@Override
	protected void initSaisie(Dossier source, DossierDD destination) {
		super.initSaisie(source, destination);
		if(Code.NATURE_DEPLACEMENT_MUTATION.equals(destination.getDeplacement().getNature().getCode()))
			((DossierDD)destination).setServiceOrigine(source.getService());
		else
			destination.setService(null);
	}
}
