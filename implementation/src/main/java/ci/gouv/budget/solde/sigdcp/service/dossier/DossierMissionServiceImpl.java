package ci.gouv.budget.solde.sigdcp.service.dossier;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.resource.spi.IllegalStateException;

import org.joda.time.DateTime;
import org.joda.time.Days;

import ci.gouv.budget.solde.sigdcp.dao.dossier.DossierMissionDao;
import ci.gouv.budget.solde.sigdcp.dao.indemnite.LocaliteGroupeMissionDao;
import ci.gouv.budget.solde.sigdcp.model.Code;
import ci.gouv.budget.solde.sigdcp.model.dossier.BulletinLiquidation;
import ci.gouv.budget.solde.sigdcp.model.dossier.BulletinLiquidation.AspectLiquide;
import ci.gouv.budget.solde.sigdcp.model.dossier.DossierDto;
import ci.gouv.budget.solde.sigdcp.model.dossier.DossierMission;
import ci.gouv.budget.solde.sigdcp.model.dossier.NatureDeplacement;
import ci.gouv.budget.solde.sigdcp.model.geographie.Localite;
import ci.gouv.budget.solde.sigdcp.model.indemnite.GroupeMission;
import ci.gouv.budget.solde.sigdcp.model.indemnite.LocaliteGroupeMission;
import ci.gouv.budget.solde.sigdcp.service.ServiceException;
import ci.gouv.budget.solde.sigdcp.service.indemnite.GroupeMissionService;
import ci.gouv.budget.solde.sigdcp.service.utils.validaton.DossierMissionValidator;

@Stateless
public class DossierMissionServiceImpl extends AbstractDossierServiceImpl<DossierMission> implements DossierMissionService,Serializable {
	
	private static final long serialVersionUID = -7765679080076677680L;
	
	@Inject private LocaliteGroupeMissionDao localiteGroupeMissionDao;
	@Inject private GroupeMissionService groupeMissionService;
	@Inject private DossierMissionValidator dossierMissionValidator;
	
	@Inject
	public DossierMissionServiceImpl(DossierMissionDao dao) {
		super(dao);
	}
	
	@Override
	protected void validationSaisie(DossierDto dto, Boolean soumission) throws ServiceException {
		dossierMissionValidator.setPieceJustificatives(dto.getPieceJustificatives());
		dossierMissionValidator.setSoumission(soumission);
		if(!dossierMissionValidator.validate((DossierMission) dto.getDossier()).isSucces())
			throw new ServiceException(dossierMissionValidator.getMessagesAsString());
	}
	
	/*
	@Override
	public void enregistrer(DossierMission dossier, Collection<PieceJustificative> pieceJustificatives) throws ServiceException {
		Operation operation = operationService.creer(Code.NATURE_OPERATION_TRANSMISSION_SAISIE_A_ORGANISATEUR);
		saisir(operation, dossier, pieceJustificatives);
	}*/
	
	
	@Override
	protected DossierMission createDossier() {
		return null;
		//throw new RuntimeException(new IllegalStateException());
	}
		
	@Override
	public BigDecimal calculerMontantIndemnisation(DossierMission dossier) {
		BigDecimal total = dossier.getIndemnite().getDivers();
		GroupeMission groupeMission = groupeMissionService.findByFonctionOrGrade(dossier.getFonction(),dossier.getProfession()==null?null:dossier.getProfession().getGrade());
		Localite zone = dossier.getDeplacement().getLocaliteArrivee();
		while(!zone.getType().getCode().equals(Code.TYPE_LOCALITE_ZONE))
			zone = zone.getParent();
		
		LocaliteGroupeMission lgm = localiteGroupeMissionDao.readByLocaliteByGroupe(zone, groupeMission);
		int nbJour = Days.daysBetween(new DateTime(dossier.getDeplacement().getDateDepart()), new DateTime(dossier.getDeplacement().getDateArrivee())).getDays()+1;
		
		total = total.add(lgm.getIndemnite().multiply(new BigDecimal(nbJour)));
		return total;
	}
	
	@Override
	public Collection<DossierDto> findALiquiderByNatureDeplacementsByAspectLiquide(Collection<NatureDeplacement> natureDeplacements, AspectLiquide aspectLiquide) {
		String natureOperationId = Code.NATURE_OPERATION_ETABLISSEMENT_BL;
		if(AspectLiquide.DEMANDE.equals(aspectLiquide))
			return super.findATraiterByNatureDeplacementsByNatureOperationId(natureDeplacements, natureOperationId );
		
		//les bons de transport à liquider
		
		DossierMissionDao dossierDao = ((DossierMissionDao)dao);
		Collection<DossierMission>  dossiers = dossierDao.readByNatureDeplacementsByAspectLiquideNotExist(natureDeplacements,AspectLiquide.TITRE_TRANSPORT); 
		Collection<DossierDto> dtos = dtos(natureOperationId,dossiers);
		
		for(DossierDto dto : dtos){
			BulletinLiquidation bl = dto.getBulletinLiquidationSaisie();
			bl.setAspect(AspectLiquide.TITRE_TRANSPORT);
			bl.setPourcentage(new BigDecimal("1"));
			bl.setMontant( ((DossierMission)dto.getDossier()).getIndemnite().getTransport() );
		}
		return dtos;
	}
	 
}
 