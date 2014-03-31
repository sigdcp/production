package ci.gouv.budget.solde.sigdcp.service.sampledata;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;

import ci.gouv.budget.solde.sigdcp.model.Code;
import ci.gouv.budget.solde.sigdcp.model.calendrier.CalendrierMission;
import ci.gouv.budget.solde.sigdcp.model.calendrier.Exercice;
import ci.gouv.budget.solde.sigdcp.model.dossier.BordereauTransmission;
import ci.gouv.budget.solde.sigdcp.model.dossier.BulletinLiquidation;
import ci.gouv.budget.solde.sigdcp.model.dossier.CategorieDeplacement;
import ci.gouv.budget.solde.sigdcp.model.dossier.CauseDeces;
import ci.gouv.budget.solde.sigdcp.model.dossier.Courrier;
import ci.gouv.budget.solde.sigdcp.model.dossier.Deplacement;
import ci.gouv.budget.solde.sigdcp.model.dossier.Dossier;
import ci.gouv.budget.solde.sigdcp.model.dossier.DossierDD;
import ci.gouv.budget.solde.sigdcp.model.dossier.DossierMission;
import ci.gouv.budget.solde.sigdcp.model.dossier.DossierTransit;
import ci.gouv.budget.solde.sigdcp.model.dossier.GroupeTypePiece;
import ci.gouv.budget.solde.sigdcp.model.dossier.NatureDeplacement;
import ci.gouv.budget.solde.sigdcp.model.dossier.NatureOperation;
import ci.gouv.budget.solde.sigdcp.model.dossier.Operation;
import ci.gouv.budget.solde.sigdcp.model.dossier.OperationValidationConfig;
import ci.gouv.budget.solde.sigdcp.model.dossier.PieceJustificative;
import ci.gouv.budget.solde.sigdcp.model.dossier.PieceJustificativeAFournir;
import ci.gouv.budget.solde.sigdcp.model.dossier.PieceProduite;
import ci.gouv.budget.solde.sigdcp.model.dossier.Statut;
import ci.gouv.budget.solde.sigdcp.model.dossier.TraitementDossier;
import ci.gouv.budget.solde.sigdcp.model.dossier.TypeDepense;
import ci.gouv.budget.solde.sigdcp.model.dossier.TypePieceJustificative;
import ci.gouv.budget.solde.sigdcp.model.dossier.TypePieceProduite;
import ci.gouv.budget.solde.sigdcp.model.dossier.ValidationType;
import ci.gouv.budget.solde.sigdcp.model.geographie.Contact;
import ci.gouv.budget.solde.sigdcp.model.geographie.DistanceEntreLocalite;
import ci.gouv.budget.solde.sigdcp.model.geographie.Localite;
import ci.gouv.budget.solde.sigdcp.model.geographie.TypeLocalite;
import ci.gouv.budget.solde.sigdcp.model.identification.AgentEtat;
import ci.gouv.budget.solde.sigdcp.model.identification.AgentEtatReference;
import ci.gouv.budget.solde.sigdcp.model.identification.Categorie;
import ci.gouv.budget.solde.sigdcp.model.identification.CompteUtilisateur;
import ci.gouv.budget.solde.sigdcp.model.identification.Credentials;
import ci.gouv.budget.solde.sigdcp.model.identification.DelegueSotra;
import ci.gouv.budget.solde.sigdcp.model.identification.Echelon;
import ci.gouv.budget.solde.sigdcp.model.identification.Fonction;
import ci.gouv.budget.solde.sigdcp.model.identification.Grade;
import ci.gouv.budget.solde.sigdcp.model.identification.Party;
import ci.gouv.budget.solde.sigdcp.model.identification.Personne;
import ci.gouv.budget.solde.sigdcp.model.identification.Personnel;
import ci.gouv.budget.solde.sigdcp.model.identification.Position;
import ci.gouv.budget.solde.sigdcp.model.identification.Profession;
import ci.gouv.budget.solde.sigdcp.model.identification.QuestionSecrete;
import ci.gouv.budget.solde.sigdcp.model.identification.ReponseSecrete;
import ci.gouv.budget.solde.sigdcp.model.identification.Role;
import ci.gouv.budget.solde.sigdcp.model.identification.Section;
import ci.gouv.budget.solde.sigdcp.model.identification.Sexe;
import ci.gouv.budget.solde.sigdcp.model.identification.SituationMatrimoniale;
import ci.gouv.budget.solde.sigdcp.model.identification.TypeAgentEtat;
import ci.gouv.budget.solde.sigdcp.model.identification.TypePersonne;
import ci.gouv.budget.solde.sigdcp.model.identification.TypePrestataire;
import ci.gouv.budget.solde.sigdcp.model.identification.TypeSection;
import ci.gouv.budget.solde.sigdcp.model.identification.souscription.InfosSouscriptionComptePersonne;
import ci.gouv.budget.solde.sigdcp.model.identification.souscription.SouscriptionComptePersonne;
import ci.gouv.budget.solde.sigdcp.model.identification.souscription.SouscriptionGestionnaireCarteSotra;
import ci.gouv.budget.solde.sigdcp.model.indemnite.GroupeDD;
import ci.gouv.budget.solde.sigdcp.model.indemnite.GroupeMission;
import ci.gouv.budget.solde.sigdcp.model.indemnite.IndemniteTranche;
import ci.gouv.budget.solde.sigdcp.model.indemnite.LocaliteGroupeMission;
import ci.gouv.budget.solde.sigdcp.model.indemnite.LocaliteGroupeMissionId;
import ci.gouv.budget.solde.sigdcp.model.indemnite.TypeClasseVoyage;
import ci.gouv.budget.solde.sigdcp.model.indemnite.TypeIndemniteTranche;
import ci.gouv.budget.solde.sigdcp.model.prestation.Prestataire;
import ci.gouv.budget.solde.sigdcp.service.SampleDataService;
import ci.gouv.budget.solde.sigdcp.service.dossier.BordereauTransmissionService;
import ci.gouv.budget.solde.sigdcp.service.dossier.DossierService;
import ci.gouv.budget.solde.sigdcp.service.dossier.OperationService;
import ci.gouv.budget.solde.sigdcp.service.identification.AuthentificationInfos;


@Stateless//(mappedName="SampleDataService") @Remote
public class SampleDataServiceImpl implements SampleDataService {
	
	@Inject private DossierService dossierService;
	@Inject private OperationService operationService;
	@Inject private BordereauTransmissionService bordereauTransmissionService;
	@Inject private AuthentificationInfos authentificationInfos;
	
	@PersistenceContext
	private EntityManager em ;
	private SituationMatrimoniale situationMatrimonialeMarie,situationMatrimonialeCelibataire;
	private Profession professionIngInfo;
	private TypePrestataire pompesFunebres,agenceVoyages,transitaire;
	private Role roleAgentEtat,roleResponsable,rolePayeur,roleDirecteur,roleAgentSolde,roleLiquidateur,rolePF,roleGCS,rolePrestataire,roleAD;
	private Grade a1,a2,a3,a4,a5,a6,a7,b1,b2,b3,b4,b5,c1,c2,c3,d1,d2,d3;
	private List<Grade> grades = new ArrayList<>();
	private Exercice exercice2012,exercice2013,exercice2014;
	private TypeDepense priseEnCharge,remboursement;
	private TypeAgentEtat fonctionnaire,contractuel,policier,gendarme;
	private TypePersonne ayantDroit;
	private TypePieceJustificative extraitNaissance,extraitMariage,cni,feuilleDep,bonTransport,factprof,factdef,attestationTransport ;
	private Prestataire elohimVoyages,mistralVoyages;
	private Localite abidjan,bouake,paris,dakar,delhi,coteDivoire,texas,lagos,zoneAfrique,zoneResteMonde,europe,asie,amerique,afrique,australie;
	private NatureDeplacement mhci,natureDeplacementMutation,natureDeplacementAffectation,natureDeplacementDepartRetraite;
	private AgentEtat agentEtat1,agentEtat2,agentEtat3,agentEtat4,agentEtat5,agentEtat6,agentEtat7,agentEtatTest1,agentEtatTest2,agentEtatTest3,agentEtatTest4,agentEtatTest5;
	private Personnel personnel1,personnel2;
	private Section ministereEconomie,ministereBudget,ministereSante,serviceExploitation,serviceEtude;
	private List<CalendrierMission> calendrierMissions=new LinkedList<>();
	private List<DossierMission> dossierMissions = new LinkedList<>();
	private List<PieceProduite> pieceProduites = new LinkedList<>();
	private List<Object[]> bulletinLiquidations = new LinkedList<>();
	private NatureOperation noOrganisationDeplacement,noAnnulationDeplacement,noTransmissionSaisieBen,noTransmissionSaisieOrg,noSoumissionDeplacment,
		noCreationDemande,noSaisie,noSoumission,noValidationRecevabilite,noValidationConformite,noDepotDossier,noEtablissementBL,noValidationEtablissementBL,noTransmissionBLPourVisa,
		noVisaBL,noProductionBT,noModificationBT,noValidationProductionBT,noTransmissionBTPriseEnCharge,noRetourBT,noAnnulerDemande,
		noPriseEnChargeBT,noMiseEnReglementBT,noReglerBulletinLiquidation;
	private Statut statutAccepte,statutRejete,statutDiffere;
	private TypePieceProduite typePieceProduiteBL,typePieceProduiteBT;
	private QuestionSecrete  questionSecrete1,questionSecrete2,questionSecrete3;
	private Personnel personnelResponsable;
	private Collection<Fonction> fonctionsHorsGroupe=new ArrayList<>(),fonctionsGroupeA=new ArrayList<>();
	
	private Random random = new Random();
	
	@Override 
	public void create() {
		parametrageGeneral();
		parametrageLocalisation();
		parametrageIdentification();
		
		parametrageIndemnite();
		parametrageModeleDossier();
		
		parametrageOperation();
		
		creationUtilisateur();
		
		executionOperation();
	}

	/*-----------------------------------------------------------------------------------------------------------------------------------------------------*/
	
	private void parametrageGeneral(){
		em.persist(exercice2012 = new Exercice(2012, date(), date(), null, false));
		em.persist(exercice2013 = new Exercice(2013, date(), date(), null, false));
		em.persist(exercice2014 = new Exercice(2014, date(), date(), null, true));
		
		CauseDeces causeDeces1 = new CauseDeces("c1", "Maladie");
		em.persist(causeDeces1);
		CauseDeces causeDeces2 = new CauseDeces("c2", "Accident");
		em.persist(causeDeces2);
		CauseDeces causeDeces3 = new CauseDeces("c3", "Homicide");
		em.persist(causeDeces3);
	}
	
	private void parametrageLocalisation(){
		TypeLocalite continent = new TypeLocalite(Code.TYPE_LOCALITE_CONTINENT,"Continent");em.persist(continent);
		TypeLocalite pays = new TypeLocalite(Code.TYPE_LOCALITE_PAYS,"Pays");em.persist(pays);
		TypeLocalite ville = new TypeLocalite(Code.TYPE_LOCALITE_VILLE,"Ville");em.persist(ville);
		TypeLocalite mairie = new TypeLocalite(Code.TYPE_LOCALITE_MAIRIE,"Mairie");em.persist(mairie);
		TypeLocalite zone = new TypeLocalite(Code.TYPE_LOCALITE_ZONE,"Zone");em.persist(zone);
		
		em.persist(zoneAfrique = new Localite("ZA", "Zone afrique", null, zone));
		em.persist(zoneResteMonde = new Localite("ZR", "Reste du monde", null, zone));
		
		em.persist(afrique = new Localite("AFRIQUE", "Afrique", zoneAfrique, continent));
		em.persist(europe = new Localite("EUROPE", "Europe", zoneResteMonde, continent));
		em.persist(asie = new Localite("ASIE", "Asie", zoneResteMonde, continent));
		em.persist(amerique = new Localite("AMERIQUE", "Amérique", zoneResteMonde, continent));
		em.persist(australie = new Localite("AUSTRALIE", "Australie", zoneResteMonde, continent));
		
		em.persist(coteDivoire = new Localite(Code.LOCALITE_COTE_DIVOIRE, "Côte d'Ivoire", afrique, pays));
		em.persist(abidjan = new Localite(Code.LOCALITE_ABIDJAN, "Abidjan", coteDivoire,ville));
		em.persist(bouake = new Localite("BK", "Bouaké", coteDivoire,ville));
		
		em.persist(paris = new Localite("PAR", "Paris", europe,ville));
		
		em.persist(dakar = new Localite("DK", "Dakar", afrique,ville));
		em.persist(delhi = new Localite("DH", "New Dheli", asie,ville));
		em.persist(lagos = new Localite("LG", "Lagos", afrique,ville));
		em.persist(texas = new Localite("TX", "Texas", amerique,ville));
		
		//mairies d'abidjan
		em.persist(new Localite("CO", "Cocody", abidjan,mairie));
		em.persist(new Localite("ADJ", "Adjamé", abidjan,mairie));
		em.persist(new Localite("ATT", "Attécoubé", abidjan,mairie));
		em.persist(new Localite("MAC", "Marcory", abidjan,mairie));
		em.persist(new Localite("YOP", "Yopougon", abidjan,mairie));
		em.persist(new Localite("KOU", "Koumassi", abidjan,mairie));
		
		// distances
		em.persist(new DistanceEntreLocalite(abidjan, bouake, "400"));
	
	}
	
	private void parametrageIdentification(){
		
		em.persist(ayantDroit = new TypePersonne(Code.TYPE_PERSONNE_AYANT_DROIT, "Ayant droit"));
		em.persist(fonctionnaire = new TypeAgentEtat(Code.TYPE_AGENT_ETAT_FONCTIONNAIRE, "Fonctionnaire"));
		em.persist(contractuel = new TypeAgentEtat(Code.TYPE_AGENT_ETAT_CONTRACTUEL, "Contractuel"));
		em.persist(policier = new TypeAgentEtat(Code.TYPE_AGENT_ETAT_POLICIER, "Policier"));
		em.persist(gendarme = new TypeAgentEtat(Code.TYPE_AGENT_ETAT_GENDARME, "Gendarme"));
		
		em.persist(pompesFunebres = new TypePrestataire("PF", "Pompe Funèbres"));
		em.persist(agenceVoyages = new TypePrestataire("AV", "Agence de voyages"));
		em.persist(transitaire = new TypePrestataire("TR", "Transitaire"));
		
		em.persist(roleAgentEtat = new Role(Code.ROLE_AGENT_ETAT, "Agent etat"));
		em.persist(roleAgentSolde = new Role(Code.ROLE_AGENT_SOLDE, "Agent de la solde"));
		
		em.persist(roleLiquidateur = new Role(Code.ROLE_LIQUIDATEUR, "Liquidateur"));
		em.persist(roleResponsable = new Role(Code.ROLE_RESPONSABLE, "Responsable"));
		em.persist(rolePayeur = new Role(Code.ROLE_PAYEUR, "Payeur"));
		em.persist(roleDirecteur = new Role(Code.ROLE_DIRECTEUR, "Directeur"));
		em.persist(rolePF = new Role(Code.ROLE_POINT_FOCAL, "Point focal de mission"));
		em.persist(roleGCS = new Role(Code.ROLE_GESTIONNAIRE_CARTE_SOTRA, "Gestionnaire de carte sotra"));
		em.persist(rolePrestataire = new Role(Code.ROLE_PRESTATAIRE, "Prestataire"));
		em.persist(roleAD = new Role(Code.ROLE_AYANT_DROIT_FO, "Ayant Droit Obsèques"));
		
		Categorie categorieA = new Categorie("A", "A");
		em.persist(categorieA);
		Categorie categorieB = new Categorie("B", "B");
		em.persist(categorieB);
		Categorie categorieC = new Categorie("C", "C");
		em.persist(categorieC);
		Categorie categorieD = new Categorie("D", "D");
		em.persist(categorieD);
		
		a1 = creerGrade(categorieA, 1);
		a2 = creerGrade(categorieA, 2);
		a3 = creerGrade(categorieA, 3);
		a4 = creerGrade(categorieA, 4);
		a5 = creerGrade(categorieA, 5);
		a6 = creerGrade(categorieA, 6);
		a7 = creerGrade(categorieA, 7);
		
		b1 = creerGrade(categorieB, 1);
		b2 = creerGrade(categorieB, 2);
		b3 = creerGrade(categorieB, 3);
		
		c1 = creerGrade(categorieC, 1);
		c2 = creerGrade(categorieC, 2);
		c3 = creerGrade(categorieC, 3);
		
		d1 = creerGrade(categorieD, 1);
		d2 = creerGrade(categorieD, 2);
		d3 = creerGrade(categorieD, 3);
		
		em.persist(questionSecrete1 = new QuestionSecrete("Quel est le nom de votre 1er Chef de service"));
		em.persist(questionSecrete2 = new QuestionSecrete("Quel est le nom de votre fruit préféré?"));
		em.persist(questionSecrete3 = new QuestionSecrete("Quel direction vous visiez pour votre 1ere affection?"));
		
		Echelon echelon1 = new Echelon("ech01", "1er Echelon");em.persist(echelon1);
		Echelon echelon2 = new Echelon("ech02", "2ème Echelon");em.persist(echelon2);
		
		//Hors groupe
		for(String libelle : new String[]{"Ministre","Dir Cab du PR","Sécretaire G. du gouvernement","Sécretaire G. A. du gouvernement","Ambassadeur en fonction"} )
			fonctionsHorsGroupe.add(fonction(libelle));
		//Groupe A/1
		for(String libelle : new String[]{"Chef état major forces armées","Cmdt Sup. Gendarmerie","Dir. Cab. Ministériel","Prefet","Procureur de la republique","Recteur de l'université"} )
			fonctionsGroupeA.add(fonction(libelle));
		
		for(String libelle : new String[]{"Chargé d'études","Sous Directeur","Contrôleur budgetaire","DAAF","Contrôleur Financier","Trésorier"} )
			fonction(libelle);
		
		//Groupe B/2
		professionIngInfo = profession("Ingénieur des techniques",a3);
		for(String libelle : new String[]{"Démographe assistant","Urbaniste assisant"} )
			profession(libelle, a3);
		for(String libelle : new String[]{"Maitre-conseiller d'éducation surveillée","Infirmier spécialiste","Sage femme spécialiste"} )
			profession(libelle, a2);
		
		Position position1 = new Position("pos01", "Détaché");em.persist(position1);
		Position position2 = new Position("pos02", "Attaché");em.persist(position2);
		
		em.persist(situationMatrimonialeMarie = new SituationMatrimoniale(Code.SITUATION_MATRIMONIALE_MARIE, "Marié"));
		em.persist(situationMatrimonialeCelibataire = new SituationMatrimoniale(Code.SITUATION_MATRIMONIALE_CELIBATAIRE, "Célibataire"));
		
		TypeSection ministere = new TypeSection(Code.TYPE_SECTION_MINISTERE, "Ministère");
		em.persist(ministere);
		TypeSection service = new TypeSection(Code.TYPE_SECTION_SERVICE, "Service");
		em.persist(service);
		
		em.persist(ministereEconomie = new Section(null,Code.SECTION_MIN_MEF, "Economie et finances", ministere));
		em.persist(ministereBudget= new Section(null,Code.SECTION_MIN_MB, "Budget", ministere));
		
		em.persist(serviceExploitation = new Section(ministereBudget,Code.SECTION_SERV_EXP, "Exploitation", service));
		em.persist(serviceEtude = new Section(ministereBudget,Code.SECTION_SERV_ET, "Etude et développement", service));
	}
	
	private void parametrageModeleDossier(){
		em.persist(priseEnCharge = new TypeDepense(Code.TYPE_DEPENSE_PRISE_EN_CHARGE, "Prise en charge"));
		em.persist(remboursement = new TypeDepense(Code.TYPE_DEPENSE_REMBOURSEMENT, "Remboursement"));
		
		em.persist(extraitNaissance = new TypePieceJustificative(Code.TYPE_PIECE_EXTRAIT_NAISSANCE, "Extrait de naissance"));
		em.persist(extraitMariage = new TypePieceJustificative(Code.TYPE_PIECE_EXTRAIT_MARIAGE, "Extrait de mariage"));
		TypePieceJustificative decisionAffectation = new TypePieceJustificative(Code.TYPE_PIECE_DECISION_AFFECTATION, "Decision d'affectation");
		em.persist(decisionAffectation);
		TypePieceJustificative avisMutation = new TypePieceJustificative(Code.TYPE_PIECE_AVIS_MUTATION, "Avis de mutation");
		em.persist(avisMutation);
		TypePieceJustificative arreteMutation = new TypePieceJustificative(Code.TYPE_PIECE_ARRETE_MUTATION, "Arrêté de mutation");
		em.persist(arreteMutation);	
		em.persist(cni = new TypePieceJustificative(Code.TYPE_PIECE_CNI, "Carte nationale d'identité"));
		TypePieceJustificative com = new TypePieceJustificative(Code.TYPE_PIECE_COMMUNICATION, "Communication");
		em.persist(com);
		TypePieceJustificative om = new TypePieceJustificative(Code.TYPE_PIECE_ORDRE_MISSION, "Ordre de mission");
		em.persist(om);
		TypePieceJustificative attsg = new TypePieceJustificative(Code.TYPE_PIECE_ATTESTATION_SG, "Attestation du sécrétariat général du gouvernement");
		em.persist(attsg);
		attestationTransport = new TypePieceJustificative(Code.TYPE_PIECE_ATTESTATION_TRANSPORT, "Attestation de transport");
		em.persist(attestationTransport);
		TypePieceJustificative passport = new TypePieceJustificative(Code.TYPE_PIECE_PASSPORT, "Passport");
		em.persist(passport);
		TypePieceJustificative attms = new TypePieceJustificative(Code.TYPE_PIECE_ATTESTATION_MISE_STAGE, "Attestation de mise en stage");
		em.persist(attms);
		TypePieceJustificative attfs = new TypePieceJustificative(Code.TYPE_PIECE_ATTESTATION_FIN_STAGE, "Attestation de fin de stage");
		em.persist(attfs);
		TypePieceJustificative attmae = new TypePieceJustificative(Code.TYPE_PIECE_ATTESTATION_MAE, "Attestation du ministère des affaires étrangères");
		em.persist(attmae);
		TypePieceJustificative decisrappel = new TypePieceJustificative(Code.TYPE_PIECE_DECISION_RAPPEL, "Decision de rappel");
		em.persist(decisrappel);
		TypePieceJustificative cps = new TypePieceJustificative(Code.TYPE_PIECE_CERTIFICAT_PRISE_SERVICE, "Certificat de prise de service");
		em.persist(cps);
		TypePieceJustificative ccs = new TypePieceJustificative(Code.TYPE_PIECE_CERTIFICAT_CESSATION_SERVICE, "Certificat de cessation de service");
		em.persist(ccs);
		TypePieceJustificative arrmr = new TypePieceJustificative(Code.TYPE_PIECE_ARRETE_MISE_RETRAITE, "Arrêté de mise à la retraite");
		em.persist(arrmr);			
		TypePieceJustificative arrpref = new TypePieceJustificative(Code.TYPE_PIECE_ARRETE_PREFECTORAL, "Arrêté Préfectoral");
		em.persist(arrpref);
		TypePieceJustificative arradiation = new TypePieceJustificative(Code.TYPE_PIECE_ARRETE_RADIATION, "Arrêté de radiation");
		em.persist(arradiation);
		TypePieceJustificative bullsal = new TypePieceJustificative(Code.TYPE_PIECE_BULLETIN_SALAIRE, "Bulletin de salaire");
		em.persist(bullsal);
		TypePieceJustificative cpps = new TypePieceJustificative(Code.TYPE_PIECE_CERTIFICAT_PREMIERE_PRISE_SERVICE, "Certificat de première prise de service");
		em.persist(cpps);
		em.persist(factprof = new TypePieceJustificative(Code.TYPE_PIECE_FACTURE_PROFORMA, "Facture proforma"));
		em.persist(factdef = new TypePieceJustificative(Code.TYPE_PIECE_FACTURE_DEFINITIVE, "Facture définitive"));
		TypePieceJustificative extdeces = new TypePieceJustificative(Code.TYPE_PIECE_EXTRAIT_DECES, "Extrait du régistre des actes de décès");
		em.persist(extdeces);
		TypePieceJustificative cpc = new TypePieceJustificative(Code.TYPE_PIECE_CERTIFICAT_PRESENCE_CORPS, "Certificat de Présence au corps");
		em.persist(cpc);
		TypePieceJustificative cartprof = new TypePieceJustificative(Code.TYPE_PIECE_CARTE_PROFESSIONNELLE, "Carte professionnelle");
		em.persist(cartprof);
		TypePieceJustificative certdeces = new TypePieceJustificative(Code.TYPE_PIECE_CERTIFICAT_DECES, "Certificat de décès");
		em.persist(certdeces);
		TypePieceJustificative lettmin = new TypePieceJustificative(Code.TYPE_PIECE_LETTRE_MINISTERIELLE, "Lettre ministérielle");
		em.persist(lettmin);
		TypePieceJustificative acteDeces = new TypePieceJustificative(Code.TYPE_PIECE_ACTE_DECES, "Acte de décès");
		em.persist(acteDeces);
		TypePieceJustificative decisionMiseStage = new TypePieceJustificative(Code.TYPE_PIECE_DECISION_MISE_STAGE, "Décision de mise en stage");
		em.persist(decisionMiseStage);
		
		em.persist(typePieceProduiteBL = new TypePieceProduite(Code.TYPE_PIECE_PRODUITE_BL, "Bulletin de liquidation"));
		em.persist(typePieceProduiteBT = new TypePieceProduite(Code.TYPE_PIECE_PRODUITE_BT, "Bordereau de transmission"));
		
		GroupeTypePiece identitte = new GroupeTypePiece(Code.GROUPE_TYPE_PIECE_IDENTITE, "Identité");
		identitte.getTypePieces().add(cni);
		identitte.getTypePieces().add(passport);
		em.persist(identitte);
		
		em.persist(feuilleDep = new TypePieceJustificative(Code.TYPE_PIECE_FEUILLE_DEPLACEMENT, "Feuille de déplacement"));
		em.persist(bonTransport = new TypePieceJustificative(Code.TYPE_PIECE_BON_TRANSPORT, "Bon de transport"));
		
		CategorieDeplacement deplacementDefinitif = new CategorieDeplacement(Code.CATEGORIE_DEPLACEMENT_DEFINITIF, "Déplacement définitif");
		em.persist(deplacementDefinitif);
		CategorieDeplacement mission = new CategorieDeplacement(Code.CATEGORIE_DEPLACEMENT_MISSION, "Mission");
		em.persist(mission);
		CategorieDeplacement transit = new CategorieDeplacement(Code.CATEGORIE_DEPLACEMENT_TRANSIT, "Transit");
		em.persist(transit);
		CategorieDeplacement obseque = new CategorieDeplacement(Code.CATEGORIE_DEPLACEMENT_OBSEQUE, "Déplacement définitif");
		em.persist(obseque);
		CategorieDeplacement sotra = new CategorieDeplacement(Code.CATEGORIE_DEPLACEMENT_TRANSPORT_URBAIN, "Sotra");
		em.persist(sotra);
		
		PieceJustificativeAFournir p;
		
		natureDeplacementAffectation = creerNatureDeplacement(deplacementDefinitif,Code.NATURE_DEPLACEMENT_AFFECTATION,"Affectation");
		natureDeplacementAffectation.getCategorie().setDisponible(new BigDecimal("1000000"));
		em.persist(p = pjaf(natureDeplacementAffectation,remboursement,fonctionnaire, decisionAffectation));
		p.getConfig().setPrincipale(Boolean.TRUE);
		p.getConfig().setAdministrative(true);
		em.persist(p = pjaf(natureDeplacementAffectation,remboursement,fonctionnaire, cpps));
		communPieceJustificativeAFournir(natureDeplacementAffectation, remboursement, fonctionnaire);
		communDDPieceJustificativeAFournir(natureDeplacementAffectation);
		
		natureDeplacementMutation =creerNatureDeplacement(deplacementDefinitif,Code.NATURE_DEPLACEMENT_MUTATION,"Mutation");
		//em.persist(natureDeplacementMutation);
		em.persist(p = pjaf(natureDeplacementMutation,remboursement,fonctionnaire,arreteMutation));
		p.getConfig().setPrincipale(true);
		p.getConfig().setAdministrative(true);
		em.persist(pjaf(natureDeplacementMutation,remboursement,fonctionnaire,cps));

		communPieceJustificativeAFournir(natureDeplacementMutation, remboursement, fonctionnaire);
		communDDPieceJustificativeAFournir(natureDeplacementMutation);
			
		natureDeplacementDepartRetraite = creerNatureDeplacement(deplacementDefinitif,Code.NATURE_DEPLACEMENT_RETRAITE,"Départ à la retraite");
		//em.persist(natureDeplacementDepartRetraite);
		em.persist(p = pjaf(natureDeplacementDepartRetraite,remboursement,fonctionnaire,arradiation));
		p.getConfig().setPrincipale(true);
		p.getConfig().setAdministrative(true);
		em.persist(p = pjaf(natureDeplacementDepartRetraite,remboursement,fonctionnaire,ccs));
		communPieceJustificativeAFournir(natureDeplacementDepartRetraite, remboursement, fonctionnaire);
		communDDPieceJustificativeAFournir(natureDeplacementDepartRetraite);
		
		em.persist(mhci = creerNatureDeplacement(mission, Code.NATURE_DEPLACEMENT_MISSION_HCI,"Mission Hors Côte d'Ivoire"));
		em.persist(p = pjaf(mhci,priseEnCharge,null, com));
		p.getConfig().setCommune(Boolean.TRUE);
		p.getConfig().setPrincipale(Boolean.TRUE);
		p.getConfig().setAdministrative(true);
		em.persist(pjaf(mhci,priseEnCharge,null, attestationTransport));
		em.persist(pjaf(mhci,priseEnCharge,null, om));
		communPieceJustificativeAFournir(mhci, priseEnCharge, null);
		
		NatureDeplacement fo = creerNatureDeplacement(obseque, Code.NATURE_DEPLACEMENT_OBSEQUE_FRAIS,"Frais d'obsèques");
		
		em.persist(fo);
		for(TypeDepense typeDepense : new TypeDepense[]{priseEnCharge,remboursement}){
			em.persist(p = pjaf(fo,typeDepense,fonctionnaire,certdeces));
			p.getConfig().setPrincipale(Boolean.TRUE);
			em.persist(pjaf(fo,typeDepense,fonctionnaire, extdeces));
			em.persist(pjaf(fo,typeDepense,fonctionnaire, acteDeces));
			em.persist(pjaf(fo,typeDepense,fonctionnaire, bullsal));
			em.persist(p = pjaf(fo,typeDepense,ayantDroit, cni));
			p.setDescription("défunt");
			communPieceJustificativeAFournir(fo, priseEnCharge, fonctionnaire);	
		}
		em.persist(pjaf(fo,remboursement,fonctionnaire, lettmin));
		
		NatureDeplacement trmae =creerNatureDeplacement(transit, Code.NATURE_DEPLACEMENT_TRANSIT_BAGAGGES_MAE,"Transit des agents MAE");
		em.persist(trmae);
		for(TypeDepense typeDepense : new TypeDepense[]{priseEnCharge,remboursement}){
			em.persist(p = pjaf(trmae,typeDepense,null, decisrappel));
			p.getConfig().setPrincipale(true);
			p.getConfig().setAdministrative(true);
			em.persist(pjaf(trmae,typeDepense,null, cps));
			//em.persist(pjaf(trmae,typeDepense,null, factprof));
			//em.persist(pjaf(trmae,typeDepense,null, ccs));
			//em.persist(pjaf(trmae,typeDepense,null, attmae));
			
		}
		communTRPieceJustificativeAFournir(trmae);
		
		NatureDeplacement trstage =creerNatureDeplacement(transit, Code.NATURE_DEPLACEMENT_TRANSIT_BAGAGGES_STAGIAIRE,"Transit des statgiaires");
		em.persist(trstage);
		for(TypeDepense typeDepense : new TypeDepense[]{priseEnCharge,remboursement}){
			//em.persist(pjaf(trstage,typeDepense,fonctionnaire, attms));
			em.persist(pjaf(trstage,typeDepense,fonctionnaire, attfs));
			em.persist(p = pjaf(trstage,typeDepense,fonctionnaire, decisionMiseStage));
			p.getConfig().setPrincipale(true);
			p.getConfig().setAdministrative(true);
			//em.persist(pjaf(trstage,typeDepense,fonctionnaire, factprof));
		}
		communTRPieceJustificativeAFournir(trstage);
		
		NatureDeplacement carteBusSotra =creerNatureDeplacement(sotra, Code.NATURE_DEPLACEMENT_TRANSPORT_CARTE_SOTRA,"Carte de bus SOTRA");
	}
	
	private void parametrageIndemnite(){
		em.persist(new TypeClasseVoyage(Code.TYPE_CLASSE_VOYAGE_1ERE,"1ère classe"));
		em.persist(new TypeClasseVoyage(Code.TYPE_CLASSE_VOYAGE_2EME,"2ème classe"));
		em.persist(new TypeClasseVoyage(Code.TYPE_CLASSE_VOYAGE_TOURISTE,"classe touriste"));
		
		groupeDD(Code.GROUPE_DD_2, "12600", "500", "9450", "500", "6300", "500",a1,a2,a3);
		groupeDD(Code.GROUPE_DD_3, "9450", "500", "7090", "300", "4725", "150",b1,b2,b3);
		groupeDD(Code.GROUPE_DD_4, "8400", "500", "5550", "300", "4200", "150",c1,c2,c3);
		groupeDD(Code.GROUPE_DD_5, "5350", "400", "5515", "250", "3675", "150",d1,d2,d3);
		
		em.persist(new IndemniteTranche(new BigDecimal("100"), new BigDecimal("0"), new BigDecimal("10"), TypeIndemniteTranche.DISTANCE));
		em.persist(new IndemniteTranche(new BigDecimal("225"), new BigDecimal("11"), new BigDecimal("20"), TypeIndemniteTranche.DISTANCE));
		em.persist(new IndemniteTranche(new BigDecimal("325"), new BigDecimal("25"), new BigDecimal("30"), TypeIndemniteTranche.DISTANCE));
		em.persist(new IndemniteTranche(new BigDecimal("425"), new BigDecimal("35"), new BigDecimal("40"), TypeIndemniteTranche.DISTANCE));
		em.persist(new IndemniteTranche(new BigDecimal("525"), new BigDecimal("45"), new BigDecimal("50"), TypeIndemniteTranche.DISTANCE));
		em.persist(new IndemniteTranche(new BigDecimal("725"), new BigDecimal("65"), new BigDecimal("70"), TypeIndemniteTranche.DISTANCE));
		em.persist(new IndemniteTranche(new BigDecimal("925"), new BigDecimal("71"), new BigDecimal("90"), TypeIndemniteTranche.DISTANCE));
		em.persist(new IndemniteTranche(new BigDecimal("1125"), new BigDecimal("91"), new BigDecimal("110"), TypeIndemniteTranche.DISTANCE));
		em.persist(new IndemniteTranche(new BigDecimal("1350"), new BigDecimal("111"), new BigDecimal("130"), TypeIndemniteTranche.DISTANCE));
		em.persist(new IndemniteTranche(new BigDecimal("1550"), new BigDecimal("131"), new BigDecimal("150"), TypeIndemniteTranche.DISTANCE));
		
		GroupeMission gmHg = groupeMission(Code.GROUPE_MISSION_HORS_GROUPE, "Hors groupe");
		gmHg.setFonctions(fonctionsHorsGroupe);
		indemniteMission(gmHg, "170000","200000");
		
		GroupeMission gmA = groupeMission(Code.GROUPE_MISSION_A, "Groupe A", a3,a2,a1);
		gmA.setFonctions(fonctionsGroupeA);
		indemniteMission(gmA, "85000","140000");
		
		GroupeMission gmB = groupeMission(Code.GROUPE_MISSION_B, "Groupe B", d1,d2,c1,c2,c3,b1,b2,b3);
		indemniteMission(gmB, "75000","120000");
	}
	
	private void parametrageOperation(){
		em.persist(statutAccepte = new Statut(Code.STATUT_ACCEPTE, "Accepte"));
		em.persist(statutRejete = new Statut(Code.STATUT_REJETE, "Rejete"));
		em.persist(statutDiffere = new Statut(Code.STATUT_DIFFERE, "Differe"));
		/*------------------------------------------- Creation des Natures d'Opération possible ----------------------------------------------*/
		//Par l'organisateur d'un deplacement
		em.persist(noOrganisationDeplacement = new NatureOperation(Code.NATURE_OPERATION_ORGANISATION_DEPLACEMENT, "Organisation d'un déplacement","Mission en cours d'organisation"));
		em.persist(noTransmissionSaisieBen = new NatureOperation(Code.NATURE_OPERATION_TRANSMISSION_SAISIE_A_BENEFICIAIRE, "Transimission de la saisie au bénéficiaire","Demande à complèter"));
		em.persist(noSoumissionDeplacment = new NatureOperation(Code.NATURE_OPERATION_SOUMISSION_DEPLACEMENT, "Soumission d'un déplacment","Mission soumise"));
		
		//Par l'usager
		em.persist(noCreationDemande = new NatureOperation(Code.NATURE_OPERATION_CREATION_DEMANDE, "Création d'une demande","Demande créee"));
		em.persist(noTransmissionSaisieOrg = new NatureOperation(Code.NATURE_OPERATION_TRANSMISSION_SAISIE_A_ORGANISATEUR, "Transimission de la saisie à l'organisateur","Mission en cours d'organisation"));
		em.persist(noAnnulerDemande = new NatureOperation(Code.NATURE_OPERATION_ANNULATION_DEMANDE, "Annulation de la demande","Demande annulée"));
		em.persist(noSaisie = new NatureOperation(Code.NATURE_OPERATION_SAISIE, "Saisie de la demande","Demande initiée"));
		em.persist(noSoumission = new NatureOperation(Code.NATURE_OPERATION_SOUMISSION, "Soumission de la demande","Demande soumise"));
		em.persist(noDepotDossier = new NatureOperation(Code.NATURE_OPERATION_DEPOT, "Depot de dossier","Dossier deposé"));
		
		em.persist(noValidationRecevabilite = new NatureOperation(Code.NATURE_OPERATION_RECEVABILITE, "Juger la reçevabilité","Demande jugée reçevable"));
		em.persist(noValidationConformite = new NatureOperation(Code.NATURE_OPERATION_CONFORMITE, "Juger la conformité","Dossier jugé conforme"));
		
		//Liquidation
		em.persist(noEtablissementBL = new NatureOperation(Code.NATURE_OPERATION_ETABLISSEMENT_BL, "Etablissement de bulletin de liquidation","Bulletin de liquidation généré"));
		em.persist(noValidationEtablissementBL = new NatureOperation(Code.NATURE_OPERATION_VALIDATION_BL, "Validation de bulletin de liquidation","Bulletin de liquidation validé"));
		em.persist(noTransmissionBLPourVisa = new NatureOperation(Code.NATURE_OPERATION_TRANSMISSION_BL_VISA, "Transmission de bulletin de liquidation pour visa","Bulletin de liquidation transmis pour visa"));
		em.persist(noVisaBL = new NatureOperation(Code.NATURE_OPERATION_VISA_BL, "Visa de bulletin de liquidation","Bulletin de liquidation visé"));
	
		//Transmission BL
		em.persist(noProductionBT = new NatureOperation(Code.NATURE_OPERATION_ETABLISSEMENT_BTBL, "Production de bordereaux de transmission","Bordereau de bulletin de liquidation généré"));
		em.persist(noModificationBT = new NatureOperation(Code.NATURE_OPERATION_MODIFICATION_BTBL, "Modification d'un bordereau","Bordereau de bulletin de liquidation modifié"));
		em.persist(noValidationProductionBT = new NatureOperation(Code.NATURE_OPERATION_VALIDATION_BTBL, "Validation de bordereaux de transmission","Bordereau de bulletin de liquidation validé"));
		em.persist(noTransmissionBTPriseEnCharge = new NatureOperation(Code.NATURE_OPERATION_TRANSMISSION_BTBL, "Transmission de bordereaux","Bordereau de bulletin de liquidation tranmis au payeur"));
		
		//Paiement
		em.persist(noPriseEnChargeBT = new NatureOperation(Code.NATURE_OPERATION_PRISE_EN_CHARGE, "Prise en charge de bordereaux","Bordereau de bulletin de liquidation pris en charge"));
		em.persist(noMiseEnReglementBT = new NatureOperation(Code.NATURE_OPERATION_MISE_EN_REGLEMENT, "Mise en reglement de bordereaux","Bordereau de bulletin de liquidation mis en règlement"));
		em.persist(noReglerBulletinLiquidation = new NatureOperation(Code.NATURE_OPERATION_REGLER_BL, "Reglement de bulletin de liquidation","Bulletin de liquidation reglé"));
		
		/*-------------------------------------------- Configuration des opérations ------------------------------------------------------------*/
		
		personnelOperationConfig(noOrganisationDeplacement,null,noSoumission,false, rolePF);
		personnelOperationConfig(noSoumissionDeplacment,noOrganisationDeplacement,noValidationRecevabilite,false, rolePF);
		personnelOperationConfig(noTransmissionSaisieBen,null,noTransmissionSaisieOrg,false, rolePF);
		
		usagerOperationConfig(noTransmissionSaisieOrg, noTransmissionSaisieBen, noSoumission);
		usagerOperationConfig(noAnnulerDemande, null, null);
		usagerOperationConfig(noSaisie, null, noSoumission);
		usagerOperationConfig(noSoumission, noSaisie, noValidationRecevabilite);
		
		personnelOperationConfig(noValidationRecevabilite,noSoumission,noDepotDossier,true,false, roleResponsable);
		usagerOperationConfig(noDepotDossier,noValidationRecevabilite,noValidationConformite);
		personnelOperationConfig(noValidationConformite,noDepotDossier,noEtablissementBL, roleResponsable);
		
		personnelOperationConfig(noEtablissementBL,noValidationConformite,noValidationEtablissementBL, roleResponsable);
		personnelOperationConfig(noValidationEtablissementBL,noEtablissementBL,noVisaBL, roleResponsable);
		personnelOperationConfig(noTransmissionBLPourVisa,noValidationEtablissementBL,noVisaBL,false, roleResponsable);
		personnelOperationConfig(noVisaBL,noTransmissionBLPourVisa,noProductionBT, roleDirecteur);
		personnelOperationConfig(noProductionBT,noVisaBL,noValidationProductionBT,false, roleResponsable);
		personnelOperationConfig(noValidationProductionBT,noProductionBT,noTransmissionBTPriseEnCharge, roleDirecteur);
		personnelOperationConfig(noTransmissionBTPriseEnCharge,noValidationProductionBT,noPriseEnChargeBT,false, roleDirecteur);
		
		personnelOperationConfig(noPriseEnChargeBT, noTransmissionBTPriseEnCharge, noMiseEnReglementBT,rolePayeur);
		personnelOperationConfig(noMiseEnReglementBT, noPriseEnChargeBT, null,false,rolePayeur);
		personnelOperationConfig(noReglerBulletinLiquidation, noMiseEnReglementBT, null,false,rolePayeur);
		
		personnelOperationConfig(noModificationBT,null,null,false, roleDirecteur);
		
		/*
		noValidationLiquidation.setOperationPrecedente(noMiseEnLiquidation);
		noValidationLiquidation.setOperationSuivante(noTransmissionPourVisaLiquidation);
		
		noTransmissionPourVisaLiquidation.setOperationPrecedente(noValidationLiquidation);
		noTransmissionPourVisaLiquidation.setOperationSuivante(noVisaLiquidation);
		
		noVisaLiquidation.setOperationPrecedente(noTransmissionPourVisaLiquidation);
		//noValidationLiquidation.setOperationSuivante(noMi);
		
		noRealisationBT.setOperationPrecedente(noVisaLiquidation);
		noRealisationBT.setOperationSuivante(noValidationBordereauTransmission);*/
	}
	
	private void creationUtilisateur(){
		//chargement des agents de l'etat
		creerAgentEtatReference("096000T", "Fiellou", "N'Dri", date(1,1,1960));
		creerAgentEtatReference("101000G", "Edoh", "Vincent", date(1,1,1960));
		creerAgentEtatReference("201000L", "Losseni", "Diarrassouba", date(1,1,1960));
		creerAgentEtatReference("175000H", "Thio", "Bekpancha", date(1,1,1960));
		creerAgentEtatReference("360257X", "Nadi", "Boua Eric", date(1,1,1960));
		
		AgentEtatReference testAgentEtatReference1 = creerAgentEtatReference("900900A", "Komenan", "Yao christian", date(1,1,1960));
		AgentEtatReference testAgentEtatReference2 = creerAgentEtatReference("900900B", "Amichia", "Martial", date(1,1,1960));
		AgentEtatReference testAgentEtatReference3 = creerAgentEtatReference("900900C", "Irie", "Henok Roland", date(1,1,1960));
		AgentEtatReference testAgentEtatReference4 = creerAgentEtatReference("900900D", "N'Dri", "Aime", date(1,1,1960));
		
		AgentEtatReference testAgentEtatReference10 = creerAgentEtatReference("900800A", "N'Goran", "Albertine", date(1,1,1960));
		
		//personnel de la solde
		Personnel personnelPointFocal = creerPersonnel(creerAgentEtatReference("900400D", "Alain", "Boua", date(1,1,1960)),"pointfocal",rolePF);
		
		Personnel personnelLiquidateur = creerPersonnel(creerAgentEtatReference("900500D", "Jack", "Yves", date(1,1,1960)),"liquidateur",roleLiquidateur,roleGCS);
		
		personnelResponsable = creerPersonnel(creerAgentEtatReference("900600D", "Adou", "Bernard", date(1,1,1960)),"responsable",roleResponsable);
		Personnel personnelResponsable1 = creerPersonnel(creerAgentEtatReference("900601D", "Madou", "Zike", date(1,1,1960)),"responsable1",roleLiquidateur,roleResponsable);
		
		//Personnel personnelResponsableFemme = creerPersonnel(creerAgentEtatReference("900800D", "Adou", "Bernanrd", date(1,1,1960)),"responsablef",roleResponsable);
		
		Personnel personnelPayeur = creerPersonnel(creerAgentEtatReference("900700D", "N'Goran", "Albertine", date(1,1,1960)),"payeur",rolePayeur);
		personnelPayeur.getAgentEtat().setSexe(Sexe.FEMININ);
		
		Personnel personnelDirecteur = creerPersonnel(creerAgentEtatReference("900801D", "Yao", "Constant", date(1,1,1960)),"directeur",roleDirecteur);
		Personnel personnelDirecteur1 = creerPersonnel(creerAgentEtatReference("900802D", "Ali", "Bamba", date(1,1,1960)),"directeur1",roleDirecteur,roleLiquidateur);
		Personnel personnelDirecteur2 = creerPersonnel(creerAgentEtatReference("900803D", "Zadi", "Kacou", date(1,1,1960)),"directeur2",roleDirecteur,roleResponsable);
		Personnel personnelDirecteur3 = creerPersonnel(creerAgentEtatReference("900804D", "Molle", "Jules", date(1,1,1960)),"directeur3",roleDirecteur,roleResponsable,roleLiquidateur,rolePF,rolePayeur,roleGCS);
		
		/*
		agentEtat1 = creerAgentEtatReference(fonctionnaire,"096000T", "Fiellou", "N'Dri", date(1,1,1960), Sexe.MASCULIN,situationMatrimoniale1, coteDivoire, null,null,null,null,null,null,null);
		agentEtat2 = creerAgentEtatReference(fonctionnaire,"101000G", "Edoh", "Vincent", date(1,1,1960), Sexe.MASCULIN,situationMatrimoniale1, coteDivoire, null,null,null,null,null,null,null);
		agentEtat3 = creerAgentEtatReference(fonctionnaire,"201000L", "Losseni", "Diarrassouba", date(1,1,1960), Sexe.MASCULIN,situationMatrimoniale1, coteDivoire, null,null,null,null,null,null,null);
		agentEtat4 = creerAgentEtatReference(fonctionnaire,"175000H", "Thio", "Bekpancha", date(1,1,1960), Sexe.MASCULIN,situationMatrimoniale1, coteDivoire, null,null,null,null,null,null,null);
		agentEtat5 = creerAgentEtatReference(fonctionnaire,"360257X", "Nadi", "Boua Eric", date(1,1,1960), Sexe.MASCULIN,situationMatrimoniale1, coteDivoire, null,null,null,null,null,null,null);
		*/
		//agentEtat5 = creerAgentEtat(fonctionnaire,"360257X", "Nadi", "Boua Eric", date(1,1,1960), Sexe.MASCULIN,situationMatrimoniale1, coteDivoire, null,null,null,null,null,null,null);
		//agentEtat7 = creerAgentEtat(fonctionnaire,"360257X", "Nadi", "Boua Eric", date(1,1,1960), Sexe.MASCULIN,situationMatrimoniale1, coteDivoire, null,null,null,null,null,null,null);
		
		
		creerCompteUtilisateur(agentEtatTest1 = creerAgentEtat(testAgentEtatReference1), "christian.komenan@budget.gouv.ci", "12345678", new Object[][]{ {questionSecrete3,"Dgbf"} });
		creerCompteUtilisateur(agentEtatTest2 = creerAgentEtat(testAgentEtatReference2), "marcial.amichia@budget.gouv.ci", "12345678", new Object[][]{ {questionSecrete3,"Dgbf"} });
		creerCompteUtilisateur(agentEtatTest3 = creerAgentEtat(testAgentEtatReference3), "irie.serge@budget.gouv.ci", "12345678", new Object[][]{ {questionSecrete3,"Dgbf"} });
		creerCompteUtilisateur(agentEtatTest4 = creerAgentEtat(testAgentEtatReference4), "stephane.ndri@budget.gouv.ci", "12345678", new Object[][]{ {questionSecrete3,"Dgbf"} });
		
		Personne personne = new Personne("Jules", "Ferry", date(1, 1, 1960), contact(), Sexe.MASCULIN, situationMatrimonialeMarie, coteDivoire, professionIngInfo);
		em.persist(personne);
		creerCompteUtilisateur(personne, "ayantdroit", "12345678",new Object[][]{ {questionSecrete3,"Dgbf"} },roleAD);
		/*
		Prestataire prestataire;
		prestataire = new Prestataire(pompesFunebres,"IVOSEP", contact());
		creerCompteUtilisateur( prestataire , "ivosep", "12345678",new Object[][]{ {questionSecrete3,"Dgbf"} },rolePrestataire);
		prestataire = new Prestataire(agenceVoyages,"AIRIVOIRE", contact());
		creerCompteUtilisateur( prestataire , "airivoire", "12345678",new Object[][]{ {questionSecrete3,"Dgbf"} },rolePrestataire);
		prestataire = new Prestataire(transitaire,"TRANSIT", contact());
		creerCompteUtilisateur( prestataire , "transit", "12345678",new Object[][]{ {questionSecrete3,"Dgbf"} },rolePrestataire);
		*/
		/*
		creerCompteUtilisateur(agentEtat2, "sigdcp1", "sigdcp", new Role[]{Role.AGENT_ETAT}, new Object[][]{ {questionSecrete3,"tresor"} });
		creerCompteUtilisateur(agentEtat3, "sigdcp2", "sigdcp", new Role[]{Role.AGENT_ETAT}, new Object[][]{ {questionSecrete2,"orange"},{questionSecrete3,"Dgbf"} });
		creerCompteUtilisateur(agentEtat4, "sigdcp3", "sigdcp", new Role[]{Role.AGENT_ETAT}, new Object[][]{ {questionSecrete1,"Tata pion"},{questionSecrete2,"mangue"},{questionSecrete3,"Dgbf"} });
		*/
		
		/*
		inscrireAgentEtat("DZ12", "Zadi", "Alain", new Date(), new Contact("mail@yahoo.com", "123456", "01 BP Abidjan", "Rue des jardins", null), Sexe.MASCULIN, 
				situationMatrimoniale1, coteDivoire, null, null, null, null, null, ministereBudget, profession1,gendarme);
		
		inscrireAgentEtat("DZ44", "Tata", "Mole", new Date(), new Contact("mail@yahoo.com", "123456", "01 BP Abidjan", "Rue des jardins", null), Sexe.MASCULIN, 
				situationMatrimoniale1, coteDivoire, null, null, null, null, null, ministereBudget, profession1,gendarme);
		
		inscrireAgentEtat("DZ100", "Kadi", "mariam", new Date(), new Contact("mail@yahoo.com", "123456", "01 BP Abidjan", "Rue des jardins", null), Sexe.FEMININ, 
				situationMatrimoniale1, coteDivoire, null, null, null, null, null, ministereBudget, profession1,policier);
		
		
		inscrireGCS(agentEtat1, ministereEconomie,"Trésor",null, agentEtat2);
		inscrireGCS(agentEtat2, ministereBudget,"Direction générale du budget","DGBF", agentEtat3);
		inscrireGCS(agentEtat3, ministereEconomie, "Impots","DGI",agentEtat4);
		
		creerDossierDD(natureDeplacementAffectation, agentEtat2);
		creerDossierDD(natureDeplacementAffectation, agentEtat3);
				
		*/
	}
	
	private void executionOperation(){
		authentificationInfos.setUtilisateur(personnelResponsable.getAgentEtat());		
		Dossier dossierDDAff1 = creerDossierDD(natureDeplacementAffectation, personnelResponsable.getAgentEtat(),true),
				dossierDDAff2 = creerDossierDD(natureDeplacementAffectation, personnelResponsable.getAgentEtat(),true),
				dossierDDAff3 = creerDossierDD(natureDeplacementAffectation, personnelResponsable.getAgentEtat(),true),
				dossierDDMut1 = creerDossierDD(natureDeplacementAffectation, personnelResponsable.getAgentEtat(),true),
				dossierDDMut2 = creerDossierDD(natureDeplacementMutation, personnelResponsable.getAgentEtat(),true),
				dossierDDRet1 = creerDossierDD(natureDeplacementMutation, personnelResponsable.getAgentEtat(),true),
				dossierDDRet2 = creerDossierDD(natureDeplacementMutation, personnelResponsable.getAgentEtat(),true);
		/*
		creerDossierDD(natureDeplacementAffectation, agentEtatTest1,true);
		creerDossierDD(natureDeplacementMutation, agentEtatTest1,true);
		creerDossierDD(natureDeplacementMutation, agentEtatTest1,true);
		creerDossierDD(natureDeplacementMutation, agentEtatTest1,true);*/
		/*		
		creerCalendrierMission(10000000f, ministereEconomie, exercice2012);
		creerCalendrierMission(20000000f, ministereEconomie, exercice2013);
		creerCalendrierMission(15000000f, ministereEconomie, exercice2014);
		
		creerCalendrierMission(50000000f, ministereBudget, exercice2012);
		creerCalendrierMission(35000000f, ministereBudget, exercice2013);
		
		creerCalendrierMission(5000000f, ministereSante, exercice2012);
		creerCalendrierMission(47000000f, ministereSante, exercice2014);
		
		creerBordereauTransmission();
		creerBordereauTransmission();
		creerBordereauTransmission();
		*/
	}
	
	/*-----------------------------------------------------------------------------------------------------------------------------------------------------*/
	
	public AgentEtatReference creerAgentEtatReference(String matricule, String nom, String prenoms, Date dateNaissance){
		AgentEtatReference agentEtatReference = new AgentEtatReference(matricule, nom+" "+prenoms, dateNaissance, null, null);
		em.persist(agentEtatReference);
		return agentEtatReference;
	}
	
	public AgentEtat souscrireAgentEtat(TypeAgentEtat typeAgentEtat,String matricule, String nom, String prenoms, Date dateNaissance, Sexe sexe, SituationMatrimoniale situationMatrimoniale, 
			Localite nationalite, Grade grade, Echelon echelon, Position position, Integer indice, Fonction fonction, Section ministere, Profession profession){
		AgentEtat agentEtat = new AgentEtat(typeAgentEtat,matricule, nom, prenoms, dateNaissance, new Contact(), sexe, situationMatrimoniale, 
				nationalite, new Date(),  grade, echelon, position, indice, fonction, ministere, profession, null);
		em.persist(agentEtat);
		return agentEtat;
	}
	
	public DelegueSotra creerDelegueSotra(TypeAgentEtat typeAgentEtat,String matricule, String nom, String prenoms, Date dateNaissance, Contact contact, Sexe sexe, SituationMatrimoniale situationMatrimoniale, 
			Localite nationalite, Grade grade, Echelon echelon, Position position, Integer indice, Fonction fonction, Section ministere, Profession profession){
		/*
		DelegueSotra delegueSotra = new DelegueSotra(typeAgentEtat,matricule, nom, prenoms, dateNaissance, contact, sexe, situationMatrimoniale, 
				nationalite, new Date(),  grade, echelon, position, indice, fonction, ministere, profession, null,agentEtat1,ministereBudget);
		
		em.persist(delegueSotra);
		*/
		return null;//delegueSotra;
	}
	
	private Contact contact(){
		return new Contact("christian.komenan@budget.gouv.ci", "11223344", "01 BP Abidjan 01", null, abidjan);
	}
	
	public Personne creerPersonne(String nom, String prenoms,PieceJustificative pieceIdentite){
		Personne personne = new Personne(nom, prenoms, date(), contact(), Sexe.MASCULIN, null, coteDivoire,null);
		return personne;
	}
	
	public InfosSouscriptionComptePersonne creerInfosSouscriptionComptePersonne(String matricule, String nom, String prenoms,TypeAgentEtat typeAgentEtat,PieceJustificative pieceIdentite){
		return new InfosSouscriptionComptePersonne(null,creerPersonne(nom, prenoms, pieceIdentite),gendarme,matricule);
	}
	
	public SouscriptionComptePersonne inscrireAgentEtat(String matricule, String nom, String prenoms, Date dateNaissance, Contact contact, Sexe sexe, SituationMatrimoniale situationMatrimoniale, 
			Localite nationalite, Grade grade, Echelon echelon, Position position, Integer indice, Fonction fonction, Section ministere, Profession profession, TypeAgentEtat typeAgentEtat){
		SouscriptionComptePersonne inscription = new SouscriptionComptePersonne(nextIdString(),date(),null,null,creerInfosSouscriptionComptePersonne(matricule, nom, prenoms, typeAgentEtat, null),null,null);
		em.persist(inscription);
		return inscription;
	}
	
	public SouscriptionGestionnaireCarteSotra inscrireGCS(AgentEtat gestionnaire,Section ministere,String libelle,String sigle,AgentEtat interimaire){
		SouscriptionGestionnaireCarteSotra inscription = new SouscriptionGestionnaireCarteSotra(nextIdString(),date(),null,null,gestionnaire,ministere,libelle,sigle,interimaire);
		em.persist(inscription);
		return inscription;
	}
	
	public void creerMission(CalendrierMission calendrierMission,String designation,Integer mois,Integer dureeJour,Localite lieu,AgentEtat[] agentEtats){
		/*
		Mission mission = new Mission(calendrierMission,date(),date(),mhci,abidjan,lieu,designation,mois,dureeJour,"Environnement","Bonne gestion");
		em.persist(mission);
		List<DossierMission> dossiers = new ArrayList<>();
		for(AgentEtat agentEtat : agentEtats){
			DossierMission dossier = new DossierMission("DM"+nextIdString(),courrier(),mission,agentEtat.getGrade(),agentEtat);
			em.persist(dossier);
			dossiers.add(dossier);
		}
		mission.setDossierDuResponsable(dossiers.get(0));
		em.merge(mission);
		*/
	}
	
	public void creerCalendrierMission(Float montant,Section ministere,Exercice exercice){
		CalendrierMission calendrierMission = new CalendrierMission(montant,false,ministere,exercice);
		em.persist(calendrierMission);
		calendrierMissions.add(calendrierMission);
		
		creerMission(calendrierMission,"Conférence panafricaine des ministres de la fonction publique",3,7,paris,new AgentEtat[]{ agentEtat1,agentEtat2 });
		creerMission(calendrierMission,"Forum sur la modernisation de l'administration publique et des institutions de l'Etat",5,7,dakar,new AgentEtat[]{ agentEtat1,agentEtat3 });
		creerMission(calendrierMission,"Suivi des stagiaires en formation à l'étranger",9,12,delhi,new AgentEtat[]{ agentEtat3,agentEtat2 });
		
	}
	
	public DossierDD creerDossierDD(NatureDeplacement natureDeplacement,AgentEtat agentEtat,boolean soumettre){
		//Deplacement deplacement = new Deplacement(date(), date(), date(), null, natureDeplacement, abidjan, bouake);
		Deplacement deplacement = new Deplacement(priseEnCharge, date(), date(), natureDeplacement,  abidjan, bouake);
		
		DossierDD dossier = new DossierDD(null, soumettre?courrier():new Courrier(), date(), deplacement, agentEtat.getGrade(), agentEtat, 500, date(), numero(), abidjan, date(), date());
		dossier.setGrade(grades.get(random.nextInt(grades.size()-1)));
		dossier.setServiceOrigine(serviceExploitation);
		dossier.setService(serviceEtude);
		Party party = authentificationInfos.getUtilisateur();
		authentificationInfos.setUtilisateur(agentEtat);
		//dossierService.enregistrer(dossier, null);
		//if(soumettre)
		//	dossierService.soumettre(dossier, null);
		authentificationInfos.setUtilisateur(party);
		return dossier;
	}
	
	public DossierTransit creerDossierTR(NatureDeplacement natureDeplacement,AgentEtat agentEtat){
		Deplacement deplacement = new Deplacement(date(), date(), date(), null, natureDeplacement, abidjan, bouake);
		em.persist(deplacement);
		DossierTransit dossier = new DossierTransit(numero(), courrier(), date(), deplacement, agentEtat.getGrade(), agentEtat, date(),date(),500f, 500f);
		em.persist(dossier);
		return dossier;
	}
	/*
	public void operation(NatureOperation natureOperation,Dossier dossier,Statut statut){
		Operation operation = new Operation(date(), natureOperation,null);
		em.persist(operation);
		TraitementDossier traitement = new TraitementDossier(operation,null,statut,dossier);
		em.persist(traitement);
		if(natureOperationLiquidation.equals(natureOperation)){
			BulletinLiquidation bl = new BulletinLiquidation(numero(), typePieceProduiteBL, date(), null, null);
			em.persist(bl);
			bulletinLiquidations.add(new Object[]{dossier,bl});
			traitement.setPieceProduite(bl);
			em.merge(traitement);
		}else if(natureOperationRBTBL.equals(natureOperation))
			;//em.persist(new PieceProduite(numero(), typePieceProduiteBTBL));
		else if(natureOperationRBTF.equals(natureOperation))
			;//em.persist(new PieceProduite(numero(), typePieceProduiteBTF));
	}*/
	
	public BordereauTransmission creerBordereauTransmission(){
		BordereauTransmission bt = new BordereauTransmission(numero(), typePieceProduiteBT, date());
		em.persist(bt);
		Operation operation = new Operation(date(), null,null);
		em.persist(operation);
		for(Object[] d : bulletinLiquidations){
			((BulletinLiquidation)d[1]).setBordereauTransmission(bt);
			em.merge(d[1]);
			em.persist(new TraitementDossier(operation,bt,null,(Dossier) d[0]));
		}
		return bt;
	}
	
	static long ID = 0;
	
	static String nextIdString(){
		return ++ID+"";
	}
	
	static Date date(){
		return new Date();
	}
	
	static Date date(int j,int m,int a){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, j);
		calendar.set(Calendar.MONTH, m);
		calendar.set(Calendar.YEAR, a);
		return calendar.getTime();
	}
	
	static String numero(){
		return UUID.randomUUID().toString();
	}
	
	static Courrier courrier(){
		return new Courrier(UUID.randomUUID().toString(),new Date());
	}
	
	public PieceJustificativeAFournir pjaf(NatureDeplacement natureDeplacement,TypeDepense typeDepense,TypePersonne typePersonne,
			TypePieceJustificative typePieceJustificative,Integer quantite,Boolean original,Integer periodeValiditeEnMois,Boolean conditionnee,String description){
		PieceJustificativeAFournir p = new PieceJustificativeAFournir(typePieceJustificative,natureDeplacement,typePersonne, typeDepense,original,periodeValiditeEnMois, quantite,description);
		p.getConfig().setConditionnee(conditionnee);
		return p;
	}
	
	public PieceJustificativeAFournir pjaf(NatureDeplacement natureDeplacement,TypeDepense typeDepense,TypePersonne typePersonne,TypePieceJustificative typePieceJustificative,Boolean conditionnee){
		return pjaf(natureDeplacement, typeDepense, typePersonne, typePieceJustificative, 1,Boolean.TRUE, -6,conditionnee,null);
	}
	
	
	
	public PieceJustificativeAFournir pjaf(NatureDeplacement natureDeplacement,TypeDepense typeDepense,TypePersonne typePersonne,TypePieceJustificative typePieceJustificative){
		return pjaf(natureDeplacement, typeDepense, typePersonne, typePieceJustificative, 1,Boolean.TRUE, -6,Boolean.FALSE,null);
	}
	
	public void communPieceJustificativeAFournir(NatureDeplacement natureDeplacement,TypeDepense typeDepense,TypePersonne typePersonne){
		//em.persist(pjaf(natureDeplacement, typeDepense, typePersonne, cni));
	}
	
	public void communDDPieceJustificativeAFournir(NatureDeplacement natureDeplacement){
		em.persist(pjaf(natureDeplacement, remboursement, fonctionnaire, extraitNaissance,Boolean.TRUE));
		em.persist(pjaf(natureDeplacement, remboursement, fonctionnaire, extraitMariage,Boolean.TRUE));
		PieceJustificativeAFournir p;
		em.persist(p = pjaf(natureDeplacement, remboursement, fonctionnaire, feuilleDep));
		p.getConfig().setDerivee(Boolean.TRUE);
		em.persist(p = pjaf(natureDeplacement, remboursement, fonctionnaire, bonTransport));
		p.getConfig().setDerivee(Boolean.TRUE);
		
		em.persist(p = pjaf(natureDeplacement, remboursement, fonctionnaire, cni));
		em.persist(p = pjaf(natureDeplacement, remboursement, fonctionnaire, attestationTransport));
	}
	
	public void communTRPieceJustificativeAFournir(NatureDeplacement natureDeplacement){
		em.persist(pjaf(natureDeplacement,priseEnCharge,null, factprof));
		em.persist(pjaf(natureDeplacement,remboursement,null, factdef));
	}
	
	private NatureDeplacement creerNatureDeplacement(CategorieDeplacement categorie,String code,String libelle){
		NatureDeplacement natureDeplacement = new NatureDeplacement(categorie, code, libelle,0);
		
		try {
			natureDeplacement.setDescription(IOUtils.toString(this.getClass().getResourceAsStream("naturedep/"+code+".html"),"UTF-8"));
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		em.persist(natureDeplacement);
		return natureDeplacement;
	}
	
	private void creerCompteUtilisateur(Party utilisateur,String username,String password,Object[][] rs,Role...roles){
		Set<Role> r = new HashSet<Role>();
		if(utilisateur instanceof AgentEtat)
			r.add(roleAgentEtat);
		if(roles!=null)
			r.addAll(Arrays.asList(roles));
		CompteUtilisateur compteUtilisateur = new CompteUtilisateur(new Credentials(username, password),utilisateur,r);
		for(Object[] o : rs)
			compteUtilisateur.getReponseSecretes().add(new ReponseSecrete((QuestionSecrete)o[0], (String)o[1]));
		
		em.persist(compteUtilisateur);
	}
	
	private AgentEtat creerAgentEtat(AgentEtatReference agentEtatReference){
		AgentEtat agentEtat = new AgentEtat(fonctionnaire, agentEtatReference.getMatricule(), agentEtatReference.getNomPrenoms(), null, date(1, 1, 1960), contact(), Sexe.MASCULIN, null, coteDivoire, date(), null, null, null, null, null, null, null, null);
		em.persist(agentEtat);
		return agentEtat;
	}
	
	private Personnel creerPersonnel(AgentEtatReference agentEtatReference,String username,Role...roles){
		Personnel personnel = new Personnel(creerAgentEtat(agentEtatReference));
		em.persist(personnel);
		creerCompteUtilisateur(personnel.getAgentEtat(), username, "12345678", new Object[][]{ {questionSecrete3,"Dgbf"}},ArrayUtils.addAll(roles, roleAgentSolde));
		return personnel;
	}
	
	/*
	private Personnel creerPersonnel(AgentEtatReference agentEtatReference,Role...roles){
		return creerPersonnel(agentEtatReference, role.getCode().toLowerCase(), roles);
	}*/
	
	private BigDecimal bigdecimal(String value){
		return new BigDecimal(value);
	}
	
	private GroupeDD groupeDD(String code,String ma,String pa,String me,String pe,String mef,String pef,Grade...grades){
		GroupeDD g = new GroupeDD(code, code, new BigDecimal(ma),  new BigDecimal(pa),  new BigDecimal(me),  new BigDecimal(pe),  new BigDecimal(mef),  new BigDecimal(pef));
		for(Grade grade : grades)
			g.getGrades().add(grade);
		em.persist(g);
		return g;
	}
	
	private GroupeMission groupeMission(String code,String libelle,Grade...grades){
		GroupeMission g = new GroupeMission(code, libelle);
		for(Grade grade : grades)
			g.getGrades().add(grade);
		em.persist(g);
		return g;
	}
	/*
	private GroupeMission groupeMission(String code,String libelle,Fonction...fonctions){
		GroupeMission g = new GroupeMission(code, libelle);
		for(Fonction fonction : fonctions)
			g.getFonctions().add(fonction);
		em.persist(g);
		return g;
	}*/
	/*
	private PieceJustificative pj(String code,String libelle){
		return new PieceJustificative(null, numero(), new PieceJustificativeAFournir(typePieceJustificative, natureDeplacement, typePersonne, typeDepense, original, periodeValiditeEnMois, quantite, description), date());
	}*/
		
	private void personnelOperationConfig(NatureOperation natureOperation,NatureOperation precedent,NatureOperation suivant,boolean choix,boolean differer,Role role){
		em.persist(new OperationValidationConfig(natureOperation, ValidationType.ACCEPTER, statutAccepte, role));
		if(choix){
			em.persist(new OperationValidationConfig(natureOperation, ValidationType.REJETER, statutRejete,role));
			if(differer)
				em.persist(new OperationValidationConfig(natureOperation, ValidationType.DIFFERER, statutDiffere,role));
		}
		natureOperation.setPrecedent(precedent);
		natureOperation.setSuivant(suivant);
	}
	
	private void personnelOperationConfig(NatureOperation natureOperation,NatureOperation precedent,NatureOperation suivant,boolean choix,Role role){
		personnelOperationConfig(natureOperation, precedent, suivant, choix, true, role);
	}
	
	private void personnelOperationConfig(NatureOperation natureOperation,NatureOperation precedent,NatureOperation suivant,Role role){
		personnelOperationConfig(natureOperation, precedent, suivant, true,true, role);
	}
	
	private void usagerOperationConfig(NatureOperation natureOperation,NatureOperation precedent,NatureOperation suivant){
		em.persist(new OperationValidationConfig(natureOperation, ValidationType.ACCEPTER, statutAccepte,roleAgentEtat));
		natureOperation.setPrecedent(precedent);
		natureOperation.setSuivant(suivant);
	}
	
	private void validerDossier(String operationCode,Personne personne,Dossier...dossiers){
		for(Dossier dossier : dossiers){
			dossier.setDernierTraitement(new TraitementDossier());
			dossier.getDernierTraitement().setValidationType(ValidationType.ACCEPTER);
		}
		//dossierService.valider(operationCode,Arrays.asList(dossiers),personne);
	}
	
	private void creerBordereau(Personne personne,Dossier...dossiers){
		BordereauTransmission b = new BordereauTransmission(RandomStringUtils.randomAlphanumeric(4), typePieceProduiteBT, new Date());
		//bordereauTransmissionService.creer(null, personne);
	}
	
	private Grade creerGrade(Categorie categorie,int index){
		Grade g;
		em.persist(g = new Grade(categorie, categorie.getCode()+index+"", categorie.getCode()+index+""));
		grades.add(g);
		return g;
	}
	

	
	private void indemniteMission(GroupeMission groupe,String montantAfrique,String montantResteMonde){
		LocaliteGroupeMission lgm = new LocaliteGroupeMission(new LocaliteGroupeMissionId(zoneAfrique.getCode(),groupe.getCode()),new BigDecimal(montantAfrique));
		em.persist(lgm);
		lgm = new LocaliteGroupeMission(new LocaliteGroupeMissionId(zoneResteMonde.getCode(),groupe.getCode()),new BigDecimal(montantResteMonde));
		em.persist(lgm);
	}
	
	private int professionId=0;
	private Profession profession(String libelle,Grade grade){
		Profession profession = new Profession("PROF"+(professionId++), libelle, grade);
		em.persist(profession);
		return profession;
	}
	
	private int fonctionId=0;
	private Fonction fonction(String libelle/*,Boolean horsGroupe*/){
		Fonction fonction = new Fonction("FONCTION"+(fonctionId++), libelle/*, horsGroupe*/);
		em.persist(fonction);
		return fonction;
	}
	
	/*
	private Fonction fonction(String libelle){
		return fonction(libelle, false);
	}
	*/

}
