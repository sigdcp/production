package ci.gouv.budget.solde.sigdcp.service.dossier;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;

import javax.inject.Inject;

import ci.gouv.budget.solde.sigdcp.dao.dossier.DossierDao;
import ci.gouv.budget.solde.sigdcp.model.dossier.Dossier;
import ci.gouv.budget.solde.sigdcp.model.dossier.DossierDD;
import ci.gouv.budget.solde.sigdcp.model.dossier.DossierDto;
import ci.gouv.budget.solde.sigdcp.model.dossier.DossierMission;
import ci.gouv.budget.solde.sigdcp.model.dossier.DossierObseques;
import ci.gouv.budget.solde.sigdcp.model.dossier.DossierTransit;
import ci.gouv.budget.solde.sigdcp.model.dossier.NatureDeplacement;
import ci.gouv.budget.solde.sigdcp.model.dossier.BulletinLiquidation.AspectLiquide;

public class DossierServiceImpl extends AbstractDossierServiceImpl<Dossier> implements DossierService,Serializable {
	
	private static final long serialVersionUID = -7765679080076677680L;
	
	@Inject private DossierDDService ddService;
	@Inject private DossierTransitService dossierTransitService;
	@Inject private DossierObsequesService dossierObsequesService;
	@Inject private DossierMissionService dossierMissionService;
		
	@Inject
	public DossierServiceImpl(DossierDao dao) {
		super(dao);
	}
	
	@Override
	public BigDecimal calculerMontantIndemnisation(Dossier dossier) {
		if(dossier instanceof DossierDD)
			return ddService.calculerMontantIndemnisation((DossierDD) dossier);
		else if(dossier instanceof DossierMission)
			return dossierMissionService.calculerMontantIndemnisation((DossierMission) dossier);
		else if(dossier instanceof DossierTransit)
			return dossierTransitService.calculerMontantIndemnisation((DossierTransit) dossier);
		else if(dossier instanceof DossierObseques)
			return dossierObsequesService.calculerMontantIndemnisation((DossierObseques) dossier);
		return null;
	}
	
	@Override
	public Collection<DossierDto> findALiquiderByNatureDeplacementsByAspectLiquide(Collection<NatureDeplacement> natureDeplacements, AspectLiquide aspectLiquide) {
		if(AspectLiquide.TITRE_TRANSPORT.equals(aspectLiquide))
			return dossierMissionService.findALiquiderByNatureDeplacementsByAspectLiquide(natureDeplacements, aspectLiquide);
		return super.findALiquiderByNatureDeplacementsByAspectLiquide(natureDeplacements, aspectLiquide);
	}
	
	
	
	@Override
	protected Dossier createDossier() {
		return null;
	}
	
}
