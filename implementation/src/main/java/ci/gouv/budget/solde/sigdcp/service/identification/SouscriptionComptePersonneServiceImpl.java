package ci.gouv.budget.solde.sigdcp.service.identification;

import static ci.gouv.budget.solde.sigdcp.service.ServiceExceptionType.IDENTIFICATION_SOUSCRIPTION_COMPTE_ACCEPTE;
import static ci.gouv.budget.solde.sigdcp.service.ServiceExceptionType.IDENTIFICATION_SOUSCRIPTION_COMPTE_ENCOURS;
import static ci.gouv.budget.solde.sigdcp.service.ServiceExceptionType.IDENTIFICATION_SOUSCRIPTION_COMPTE_EXISTE;
import static ci.gouv.budget.solde.sigdcp.service.ServiceExceptionType.IDENTIFICATION_SOUSCRIPTION_COMPTE_MAIL_EXISTE;
import static ci.gouv.budget.solde.sigdcp.service.ServiceExceptionType.IDENTIFICATION_SOUSCRIPTION_COMPTE_MATRCULE_INCONNU;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import ci.gouv.budget.solde.sigdcp.dao.identification.AgentEtatDao;
import ci.gouv.budget.solde.sigdcp.dao.identification.AgentEtatReferenceDao;
import ci.gouv.budget.solde.sigdcp.dao.identification.CompteUtilisateurDao;
import ci.gouv.budget.solde.sigdcp.dao.identification.SouscriptionComptePersonneDao;
import ci.gouv.budget.solde.sigdcp.model.Code;
import ci.gouv.budget.solde.sigdcp.model.communication.NotificationMessageType;
import ci.gouv.budget.solde.sigdcp.model.geographie.Contact;
import ci.gouv.budget.solde.sigdcp.model.identification.AgentEtat;
import ci.gouv.budget.solde.sigdcp.model.identification.CompteUtilisateur;
import ci.gouv.budget.solde.sigdcp.model.identification.Personne;
import ci.gouv.budget.solde.sigdcp.model.identification.ReponseSecrete;
import ci.gouv.budget.solde.sigdcp.model.identification.Role;
import ci.gouv.budget.solde.sigdcp.model.identification.souscription.SouscriptionComptePersonne;
import ci.gouv.budget.solde.sigdcp.service.ServiceException;
import ci.gouv.budget.solde.sigdcp.service.utils.validaton.SouscriptionComptePersonneValidator;

public class SouscriptionComptePersonneServiceImpl extends AbstractSouscriptionServiceImpl<SouscriptionComptePersonne> implements SouscriptionComptePersonneService,Serializable {
	
	private static final long serialVersionUID = 1170771216036513138L;

	@Inject private SouscriptionComptePersonneValidator validator;
	@Inject private AgentEtatReferenceDao agentEtatReferenceDao;
	@Inject private SouscriptionComptePersonneDao souscriptionComptePersonneDao;
	@Inject private AgentEtatDao agentEtatDao;
	@Inject private CompteUtilisateurDao compteUtilisateurDao;
	
	@Inject
	public SouscriptionComptePersonneServiceImpl(SouscriptionComptePersonneDao dao) {
		super(dao);
	}

	@Transactional(value=TxType.REQUIRED)
	@Override
	public void souscrire(SouscriptionComptePersonne souscriptionComptePersonne) throws ServiceException {
		
		if(!validator.validate(souscriptionComptePersonne).isSucces())
			serviceException(validator.getMessagesAsString());
		String matricule = souscriptionComptePersonne.getPersonneDemandeur().getMatricule();
		// Est ce qu'il a déja une inscription en cours de validation ou acceptée
		SouscriptionComptePersonne souscriptionComptePersonneExistante = souscriptionComptePersonneDao.read(matricule);
		
		if(souscriptionComptePersonneExistante!=null){			
			if(souscriptionComptePersonneExistante.getDateValidation()==null)
				serviceException(IDENTIFICATION_SOUSCRIPTION_COMPTE_ENCOURS);
		
			if( Boolean.TRUE.equals(souscriptionComptePersonneExistante.getAcceptee()))
				serviceException(IDENTIFICATION_SOUSCRIPTION_COMPTE_ACCEPTE);
		}
		
		
		if(!Code.TYPE_AGENT_ETAT_GENDARME.equals(souscriptionComptePersonne.getPersonneDemandeur().getType().getCode())){
			//ce n'est pas un gendarme
			//Est ce qu'il est dans la table de référence
			if(!agentEtatReferenceDao.exist(matricule))
				serviceException(IDENTIFICATION_SOUSCRIPTION_COMPTE_MATRCULE_INCONNU);
			//Est ce qu'il à un compte
			CompteUtilisateur compteUtilisateur = compteUtilisateurDao.readByMatricule(matricule);
			if(compteUtilisateur!=null)
				serviceException(IDENTIFICATION_SOUSCRIPTION_COMPTE_EXISTE);
			//Est ce que ce compte n'est pas déja lié à l'adresse email
			compteUtilisateur = compteUtilisateurDao.readByEMail(souscriptionComptePersonne.getPersonneDemandeur().getPersonne().getContact().getEmail());
			if(compteUtilisateur!=null)
				serviceException(IDENTIFICATION_SOUSCRIPTION_COMPTE_MAIL_EXISTE);
			
			createSouscription(souscriptionComptePersonne);
			souscriptionComptePersonne.setDateValidation(souscriptionComptePersonne.getDateCreation());
			souscriptionComptePersonne.setAcceptee(Boolean.TRUE);
			
			AgentEtat agentEtat = createAgentEtat(souscriptionComptePersonne);
			compteUtilisateur = createCompteUtilisteur(souscriptionComptePersonne,agentEtat);
			notifier(NotificationMessageType.AVIS_SOUSCRIPTION_COMPTE_PERSONNE_FONCTIONNAIRE,new Object[]{
					"loginUtilisateur",compteUtilisateur.getCredentials().getUsername(),"motPasseUtilisateur",compteUtilisateur.getCredentials().getPassword(),
					"nomPrenomsAgentEtat",agentEtat.getNomPrenoms()
			},agentEtat);
			
		}else{
			//c'est un gendarme
			
			//Utiliser un validator Specific pour la coherence
			createSouscription(souscriptionComptePersonne);
			notifier(NotificationMessageType.AVIS_SOUSCRIPTION_COMPTE_PERSONNE_ENREGISTREE,new Object[]{"nomPrenomsAgentEtat",souscriptionComptePersonne.getPersonneDemandeur().getPersonne().getNomPrenoms()},
					souscriptionComptePersonne.getPersonneDemandeur().getPersonne());
		}	
	}
	
	private AgentEtat createAgentEtat(SouscriptionComptePersonne souscriptionComptePersonne){
		Personne p = souscriptionComptePersonne.getPersonneDemandeur().getPersonne();
	
		AgentEtat agentEtat = new AgentEtat(souscriptionComptePersonne.getPersonneDemandeur().getType(),souscriptionComptePersonne.getPersonneDemandeur().getMatricule(),
				p.getNom() ,p.getPrenoms(), p.getDateNaissance(), new Contact(p.getContact()), p.getSexe(), p.getSituationMatrimoniale(), p.getNationalite(),null,null, null, null, null, null, null, null,null);
		
		agentEtatDao.create(agentEtat);
		return agentEtat;
	}
	
	/**
	 * Initialisations des attributs et persistence
	 * @param souscriptionComptePersonne
	 */
	private void createSouscription(SouscriptionComptePersonne souscriptionComptePersonne){
		souscriptionComptePersonne.setPersonneReferencee(null);
		souscriptionComptePersonne.setCode(System.currentTimeMillis()+"");
		souscriptionComptePersonne.setDateCreation(new Date());
		souscriptionComptePersonneDao.create(souscriptionComptePersonne);
	}
	
	/**
	 * Initialisations des attributs et persistence
	 * 
	 */
	private CompteUtilisateur createCompteUtilisteur(SouscriptionComptePersonne souscriptionComptePersonne,AgentEtat agentEtat){
		CompteUtilisateur compteUtilisateur = new CompteUtilisateur();
		compteUtilisateur.setDateCreation(new Date());
		compteUtilisateur.setUtilisateur(agentEtat);
		compteUtilisateur.getCredentials().setUsername(souscriptionComptePersonne.getPersonneDemandeur().getPersonne().getContact().getEmail());
		compteUtilisateur.getCredentials().setPassword(souscriptionComptePersonne.getPassword());
		for(ReponseSecrete reponseSecrete : souscriptionComptePersonne.getReponseSecretes()){
			reponseSecrete.setId(null);
			compteUtilisateur.getReponseSecretes().add(reponseSecrete);
		}
		compteUtilisateur.getRoles().add(genericDao.readByClass(Role.class, Code.ROLE_AGENT_ETAT));
		compteUtilisateurDao.create(compteUtilisateur);
		return compteUtilisateur;
	}

	@Transactional(value=TxType.REQUIRED)
	@Override
	public void accepter(SouscriptionComptePersonne souscriptionCompte)throws ServiceException {
		dao.update(souscriptionCompte);
	} 

	@Transactional(value=TxType.REQUIRED)
	@Override
	public void accepter(Collection<SouscriptionComptePersonne> souscriptionComptes) throws ServiceException {
		for(SouscriptionComptePersonne souscriptionCompte : souscriptionComptes)
			accepter(souscriptionCompte);
	}
	
	@Transactional(value=TxType.REQUIRED)
	@Override
	public void rejeter(SouscriptionComptePersonne souscriptionCompte) throws ServiceException {
		souscriptionCompte.setDateValidation(new Date());
		souscriptionCompte.setAcceptee(Boolean.FALSE);
		dao.update(souscriptionCompte);
	}
	
	@Transactional(value=TxType.REQUIRED)
	@Override
	public void rejeter(Collection<SouscriptionComptePersonne> souscriptionComptes) throws ServiceException {
		for(SouscriptionComptePersonne souscriptionCompte : souscriptionComptes)
			rejeter(souscriptionCompte);
	}
	
	/*
	 * Read only services
	 */

	@Override
	public Collection<SouscriptionComptePersonne> findSouscriptionsAValiderByTypePersonneId(String typePersonneId) {
		return ((SouscriptionComptePersonneDao)dao).readDateValidationIsNullByTypePersonneId(typePersonneId);
	}
	
	@Override
	public Collection<SouscriptionComptePersonne> findSouscriptionsAValider() {
		return ((SouscriptionComptePersonneDao)dao).readDateValidationIsNull();
	}

}
