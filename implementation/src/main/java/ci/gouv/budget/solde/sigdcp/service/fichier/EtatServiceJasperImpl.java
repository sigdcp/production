package ci.gouv.budget.solde.sigdcp.service.fichier;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import ci.gouv.budget.solde.sigdcp.dao.dossier.BulletinLiquidationDao;
import ci.gouv.budget.solde.sigdcp.dao.dossier.DossierDao;
import ci.gouv.budget.solde.sigdcp.dao.dossier.GroupeDDDao;
import ci.gouv.budget.solde.sigdcp.dao.dossier.PieceJustificativeDao;
import ci.gouv.budget.solde.sigdcp.model.Code;
import ci.gouv.budget.solde.sigdcp.model.dossier.BulletinLiquidation;
import ci.gouv.budget.solde.sigdcp.model.dossier.CategorieDeplacement;
import ci.gouv.budget.solde.sigdcp.model.dossier.Document;
import ci.gouv.budget.solde.sigdcp.model.dossier.Dossier;
import ci.gouv.budget.solde.sigdcp.model.dossier.DossierDD;
import ci.gouv.budget.solde.sigdcp.model.dossier.PieceJustificative;
import ci.gouv.budget.solde.sigdcp.model.dossier.PieceProduite;
import ci.gouv.budget.solde.sigdcp.model.identification.Sexe;
import ci.gouv.budget.solde.sigdcp.model.indemnite.GroupeDD;
import ci.gouv.budget.solde.sigdcp.model.indemnite.IndemniteDetailsDD;
import ci.gouv.budget.solde.sigdcp.model.template.etat.BondeTransportEtat;
import ci.gouv.budget.solde.sigdcp.model.template.etat.BulletinLiquidationDDEtat;
import ci.gouv.budget.solde.sigdcp.model.template.etat.FeuilleDeplacementEtat;
import ci.gouv.budget.solde.sigdcp.service.ServiceException;
import ci.gouv.budget.solde.sigdcp.service.dossier.DossierService;
import ci.gouv.budget.solde.sigdcp.service.resources.report.Report;

public class EtatServiceJasperImpl implements EtatService {

	private @Inject BulletinLiquidationDao bulletinLiquidationDao;
	@Inject private DossierDao dossierDao;
	@Inject private DossierService dossierService;
	@Inject private PieceJustificativeDao pieceJustificativeDao;
	@Inject private GroupeDDDao groupeDDDao;
	
	@Override
	public <T> byte[] build(Class<T> aClass,InputStream templateInputStream,Collection<T> dataSource) {
		JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(dataSource);
		try {
			JasperDesign jasperDesign = JRXmlLoader.load(templateInputStream);
			JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, beanColDataSource);	
			return JasperExportManager.exportReportToPdf(jasperPrint);
			//return JasperExportManager.exportReportToXml(jasperPrint).getBytes();
		} catch (JRException e) {
			throw new ServiceException(e);
		}
	}
	
	private Collection<PieceJustificative> pieceJustificatives(Collection<PieceJustificative> pieceJustificatives,String type){
		Collection<PieceJustificative> collection = new ArrayList<>();
		for(PieceJustificative p : pieceJustificatives)
			if(type.equals(p.getModel().getTypePieceJustificative().getCode()))
				collection.add(p);
		return collection;
	}
	
	@Override
	public byte[] build(Document document) {
		if(document instanceof PieceJustificative){
			PieceJustificative pieceJustificative = (PieceJustificative) document;
			if(Code.TYPE_PIECE_FEUILLE_DEPLACEMENT.equals(pieceJustificative.getModel().getTypePieceJustificative().getCode())){
				InputStream inputStream = Report.class.getResourceAsStream("feuilledeplacement1.jrxml");
				List<FeuilleDeplacementEtat> dataSource = new LinkedList<>();
				Collection<PieceJustificative> pieceJustificatives = pieceJustificativeDao.readByDossier(pieceJustificative.getDossier());
				
				GroupeDD groupeDD = groupeDDDao.readByGrade(pieceJustificative.getDossier().getGrade());
				Boolean male = Sexe.MASCULIN.equals(pieceJustificative.getDossier().getBeneficiaire().getSexe());
				Boolean marie = !pieceJustificatives(pieceJustificatives, Code.TYPE_PIECE_EXTRAIT_MARIAGE).isEmpty();
				PieceJustificative decision = pieceJustificatives(pieceJustificatives, Code.TYPE_PIECE_DECISION_AFFECTATION).iterator().next();
				int nbe = pieceJustificatives(pieceJustificatives, Code.TYPE_PIECE_EXTRAIT_NAISSANCE).size();
		
				StringBuilder accompaganteur = new StringBuilder();
				if(marie)
					accompaganteur.append("Epou"+(male?"x":"se"));
				if(nbe>0)
					accompaganteur.append(accompaganteur.length()==0?"":" et "+ (nbe+" enfant"+(nbe>1?"s":"")));
				
				dataSource.add(new FeuilleDeplacementEtat(pieceJustificative, "Décision", decision.getDateEtablissement()+"", groupeDD.getLibelle(), pieceJustificative.getDossier().getBeneficiaire().getIndice()+"",
						accompaganteur.toString()));
				return build(FeuilleDeplacementEtat.class, inputStream, dataSource);
			}
			if(Code.TYPE_PIECE_BON_TRANSPORT.equals(pieceJustificative.getModel().getTypePieceJustificative().getCode())){
				InputStream inputStream = Report.class.getResourceAsStream("bondetransport.jrxml");
				List<BondeTransportEtat> dataSource = new LinkedList<>();
				Collection<PieceJustificative> pieceJustificatives = pieceJustificativeDao.readByDossier(pieceJustificative.getDossier());
				PieceJustificative decision = pieceJustificatives(pieceJustificatives, Code.TYPE_PIECE_DECISION_AFFECTATION).iterator().next();
				BondeTransportEtat bte = new BondeTransportEtat(pieceJustificative, null, null, "Decision", decision.getNumero(), decision.getDateEtablissement()+"", 
						"Ministère", "Transporteur", "SDDCP", "RECPSERVICE", 
						new Date()+"", new Date()+"", new Date()+"", "Lieu Signature", "Budget général", "", "", "", "", "000410", new Date()+"", "332", "4101", "016289", "Lieu certif", 
						new Date()+"", decision.getFonctionSignataire().getLibelle(), "Materiel transporte", "150000");
				dataSource.add(bonTransportEtat(bte, "BON TRANSPORT", "ORIGINAL"));
				return build(BondeTransportEtat.class, inputStream, dataSource);
			}			
		}else if(document instanceof PieceProduite){
			PieceProduite pieceProduite = (PieceProduite) document;
			if(Code.TYPE_PIECE_PRODUITE_BL.equals(pieceProduite.getType().getCode())){
				Dossier dossier = dossierDao.readByBulletinLiquidation((BulletinLiquidation) pieceProduite);
				
				if(dossier instanceof DossierDD){
					return blDD((DossierDD) dossier, (BulletinLiquidation) document, "bulletinliquidationdd.jrxml");
					/*
					InputStream inputStream = Report.class.getResourceAsStream("bulletinliquidationdd.jrxml");
					List<BulletinLiquidationDDEtat> dataSource = new LinkedList<>();
					dataSource.add(new BulletinLiquidationDDEtat(pj(null), pj(null), pieceProduite, null, null, 2, 150, "G1", 2, 2, "A+b", "12", "C*2", "45", "d+6", "156", "r+6", "100", "r*2+6", "120", "f+h", "456", "er+6", "852", "100", "Cent"));
					return build(BulletinLiquidationDDEtat.class, inputStream, dataSource);
					*/
				}
				return null;
			}
			/*if(Code.TYPE_PIECE_PRODUITE_BT.equals(pieceProduite.getType().getCode())){
				PieceProduite pieceProduite = (PieceProduite) document;
				InputStream inputStream = Report.class.getResourceAsStream("bordereautest.jrxml");
				List<FeuilleDeplacementEtat> dataSource = new LinkedList<>();
				dataSource.add(new FeuilleDeplacementEtat(pieceJustificative, "Décision", new Date().toString(), "Groupe 1", pieceJustificative.getDossier().getBeneficiaire().getIndice()+"","Femme et deux(02) enfants"));
				return build(FeuilleDeplacementEtat.class, inputStream, dataSource);
			}	*/
		}
		throw new ServiceException("Aucun Etat disponible pour <"+document.getId()+">");
	}
	
	@Override
	public byte[] buildBulletinLiquidation(String numeroDossier) {
		Dossier dossier = dossierDao.read(numeroDossier);
		BulletinLiquidation bulletinLiquidation = null;//bulletinLiquidationDao.readByDossier(dossier);
		if(bulletinLiquidation==null){// Apercu
			
			if(dossier instanceof DossierDD){
				dossierService.calculerMontantIndemnisation(dossier);
				return blDD((DossierDD) dossier, bulletinLiquidation, "bulletinliquidationdd.jrxml");
				/*
				InputStream inputStream = Report.class.getResourceAsStream("bulletinliquidationdd.jrxml");
				List<BulletinLiquidationDDEtat> dataSource = new LinkedList<>();
				PieceProduite p = new PieceProduite("XXXXXX", null, null);
				dossierService.calculerMontantIndemnisation(dossier);
				IndemniteDetailsDD details = ((DossierDD)dossier).getIndemniteDetails();
				dataSource.add(new BulletinLiquidationDDEtat(pj(dossier), pj(dossier), p, null, null, details.getNombreEnfant().intValue(), details.getDistance().intValue(), details.getGroupe().getLibelle(), 
						2,categorieDeplacement.getNombreJourIndemniteJournaliere(),details.getFormuleIkp(),details.getIndemniteKilometrique()+"", 
						details.getAgent().getFormuleBagages(), details.getAgent().getIndemniteBagages()+"", details.getConjoint().getFormuleBagages(), details.getConjoint().getIndemniteBagages()+"", 
						details.getEnfants().getFormuleBagages(), details.getEnfants().getIndemniteBagages()+"", details.getAgent().getFormuleJournaliere(), details.getAgent().getIndemniteJournaliere()+"",
						details.getConjoint().getFormuleJournaliere(), details.getConjoint().getIndemniteJournaliere()+"", details.getEnfants().getFormuleJournaliere(), details.getEnfants().getIndemniteJournaliere()+"",
						details.getTotal()+"", details.getTotalEnLettre()));
				return build(BulletinLiquidationDDEtat.class, inputStream, dataSource);
				*/
			}
			return null;
		}else
			return build(bulletinLiquidation);
	}
	
	private byte[] blDD(DossierDD dossier,BulletinLiquidation bulletinLiquidation,String fichierEtat){
		InputStream inputStream = Report.class.getResourceAsStream(fichierEtat);
		CategorieDeplacement categorieDeplacement = dossier.getDeplacement().getNature().getCategorie();
		dossierService.calculerMontantIndemnisation(dossier);
		IndemniteDetailsDD details = ((DossierDD)dossier).getIndemniteDetails();
		List<BulletinLiquidationDDEtat> dataSource = new LinkedList<>();
		dataSource.add(new BulletinLiquidationDDEtat(pj(dossier), pj(dossier), bulletinLiquidation, null, null, details.getNombreEnfant().intValue(), details.getDistance().intValue(), details.getGroupe().getLibelle(), 
				2,categorieDeplacement.getNombreJourIndemniteJournaliere(),details.getFormuleIkp(),details.getIndemniteKilometrique()+"", 
				details.getAgent().getFormuleBagages(), details.getAgent().getIndemniteBagages()+"", details.getConjoint().getFormuleBagages(), details.getConjoint().getIndemniteBagages()+"", 
				details.getEnfants().getFormuleBagages(), details.getEnfants().getIndemniteBagages()+"", details.getAgent().getFormuleJournaliere(), details.getAgent().getIndemniteJournaliere()+"",
				details.getConjoint().getFormuleJournaliere(), details.getConjoint().getIndemniteJournaliere()+"", details.getEnfants().getFormuleJournaliere(), details.getEnfants().getIndemniteJournaliere()+"",
				details.getTotal()+"", details.getTotalEnLettre()));
		return build(BulletinLiquidationDDEtat.class, inputStream, dataSource);
	}
	
	private BondeTransportEtat bonTransportEtat(BondeTransportEtat bonTransportEtat,String libdoc,String statutdoc){
		bonTransportEtat.setLibdoc(libdoc);
		bonTransportEtat.setStatutdoc(statutdoc);
		return bonTransportEtat;
	}
	
	private PieceJustificative pj(Dossier dossier){
		return new PieceJustificative(dossier, "PJ001", null, new Date());
	}

}
