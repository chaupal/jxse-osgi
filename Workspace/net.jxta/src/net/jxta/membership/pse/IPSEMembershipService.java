package net.jxta.membership.pse;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.Enumeration;

import net.jxta.credential.AuthenticationCredential;
import net.jxta.credential.Credential;
import net.jxta.document.Advertisement;
import net.jxta.document.Element;
import net.jxta.document.XMLDocument;
import net.jxta.exception.PeerGroupException;
import net.jxta.exception.ProtocolNotSupportedException;
import net.jxta.id.ID;
import net.jxta.membership.Authenticator;
import net.jxta.membership.IPSECredentialBridge;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.core.ModuleSpecID;
import net.jxta.service.Service;

public interface IPSEMembershipService extends Service{

	/**
	 * Well known service specification identifier: pse membership
	 */
	ModuleSpecID pseMembershipSpecID = (ModuleSpecID) ID.create(
			URI.create(ID.URIEncodingName + ":" + ID.URNNamespace + ":uuid-DeadBeefDeafBabaFeedBabe000000050306"));

	/**
	 *  @inheritDoc
	 **/
	void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 *  @inheritDoc
	 **/
	void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

	/**
	 *  @inheritDoc
	 **/
	void removePropertyChangeListener(PropertyChangeListener listener);

	/**
	 *  @inheritDoc
	 **/
	void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);

	/**
	 * {@inheritDoc}
	 * @param impl
	 * @throws net.jxta.exception.PeerGroupException
	 **/
	void init(PeerGroup group, ID assignedID, Advertisement impl) throws PeerGroupException;

	/**
	 * {@inheritDoc}
	 **/
	Service getInterface();

	/**
	 * {@inheritDoc}
	 **/
	Advertisement getImplAdvertisement();

	/**
	 * {@inheritDoc}
	 *
	 * <p/>Currently this service starts by itself and does not expect
	 * arguments.
	 */
	int startApp(String[] arg);

	/**
	 * {@inheritDoc}
	 **/
	void stopApp();

	PeerGroup getGroup();

	ID getAssignedID();

	/**
	 * {@inheritDoc}
	 *
	 * <p/>Supports methods <code>"StringAuthentication"</code>,
	 * <code>"DialogAuthentication"</code>,
	 * <code>"EngineAuthentication"</code> and
	 * <code>"InteractiveAuthentication"</code> (an alias for
	 * <code>"DialogAuthentication"</code>)
	 * @throws net.jxta.exception.ProtocolNotSupportedException
	 **/
	Authenticator apply(AuthenticationCredential application) throws ProtocolNotSupportedException;

	/**
	 * {@inheritDoc}
	 **/
	Credential getDefaultCredential();

	/**
	 * {@inheritDoc}
	 **/
	Enumeration<Credential> getCurrentCredentials();

	/**
	 * {@inheritDoc}
	 **/
	Credential join(Authenticator authenticated) throws PeerGroupException;

	/**
	 * {@inheritDoc}
	 **/
	void resign();

	/**
	 * {@inheritDoc}
	 **/
	Credential makeCredential(Element<?> element);

	/**
	 *  Returns the key store object associated with this PSE Membership Service.
	 **/
	PSEConfig getPSEConfig();

	/**
	 *  Returns the PeerGroup associated with this PSE Membership Service.
	 **/
	PeerGroup getPeerGroup();

	/**
	 *  Recover the service credential for the assigned ID given an authenticated local credential.
	 *
	 *  @param assignedID   The assigned ID of the service credential.
	 *  @param credential   The issuer credential for the service credential.
	 **/
	IPSECredential getServiceCredential(ID assignedID, IPSECredential credential)
			throws IOException, PeerGroupException, InvalidKeyException, SignatureException;

	/**
	 * Signs an advertisement for publication. The signed document needs to have
	 * at least one key reference included - either the encoded key that was used
	 * when signing or the peerid (so that the verifying peer can look up the key
	 * in the PSEMembershipService keystore). If both the public key and the peerid
	 * are sent then the receiving peer can use the enclosed key with the option
	 * of occasionally verifying the key via the keystore.
	 *
	 * The returned PSEAdvertismentSignatureToken contains two XMLElement classes.
	 * Both elements are appended to the advertisement during publication:
	 * XMLSignatureInfo contains a digest of the original advertisement and key info
	 * XMLSignature contains a digest of XMLSignatureInfo and a signature of that digest
	 * Refer to the following for reasoning:
	 * http://java.sun.com/developer/technicalArticles/xml/dig_signatures/
	 *
	 * @param advertismentDocument
	 * @param includePublicKey
	 * @param includePeerID
	 * @return PSEAdvertismentSignatureToken
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 * @throws IOException
	 */
	IPSEAdvertisementSignatureToken signAdvertisement(XMLDocument<?> advertismentDocument, boolean includePublicKey,
			boolean includePeerID)
			throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, IOException;

	/**
	 * Validates a signed advertisement and returns a PSEAdvertismentValidationToken
	 * which specifies whether:
	 * 1. The signature is good
	 * 2. The enclosed peerid is present in the peergroups PSEMembership keystore
	 * 3. The enclosed public key is the same as the key in the keystore
	 * If there is no enclosed public key then an attempt is made to use the key in
	 * the keystore if a peerid is enclosed.
	 * If ignoreKeystore is true then a comparison of the enclosed key and the keystore
	 * key is not undertaken.
	 * If there is no key and no peerid enclosed then the signature will, obviously, fail.
	 * @param advertismentDocument
	 * @param verifyKeyWithKeystore
	 * @return PSEAdvertismentValidationToken
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws InvalidKeyException
	 * @throws KeyStoreException
	 * @throws IOException
	 * @throws SignatureException
	 */
	IPSEAdvertisementValidationToken validateAdvertisement(XMLDocument<?> advertismentDocument,
			boolean verifyKeyWithKeystore) throws NoSuchAlgorithmException, InvalidKeySpecException,
			InvalidKeyException, KeyStoreException, IOException, SignatureException;

	/**
	 *
	 * AccessService support
	 *
	 * @param accessServiceOffererCredential
	 * @param peerids
	 * @throws CertPathValidatorException
	 */
	void validateOffererCredential(IPSECredential accessServiceOffererCredential, String[] aliases)
			throws CertPathValidatorException;

	/**
	 *
	 * AccessService support
	 *
	 * @param accessServiceOffererCredential
	 * @param peerids
	 * @throws CertPathValidatorException
	 */
	void validateOffererCredential(IPSECredential accessServiceOffererCredential) throws CertPathValidatorException;

	/**
	 * validatePeer Validates the certificate chain in the keystore of a peer
	 * against a CA - the user must install a PSEPeerValidationEngineFactory for
	 * this to be useful
	 * @param peerID
	 * @throws CertPathValidatorException
	 * @throws KeyStoreException
	 * @throws IOException
	 */
	void validatePeer(PeerID peerID) throws CertPathValidatorException, KeyStoreException, IOException;

	/**
	 * Support for WireFormatMessageBinary message signing
	 * @param bridge
	 * @return
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 * @throws IOException
	 */
	byte[] signWireFormatMessageBinary(
			net.jxta.endpoint.WireFormatMessageBinary.WireFormatMessageBinarySignatureBridge bridge)
			throws InvalidKeyException, SignatureException, IOException, SecurityException;

	/**
	 *
	 * Support for EndpointRouterMessage message signing
	 * @param bridge
	 * @return
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 * @throws IOException
	 */
	byte[] signEndpointRouterMessage(
			net.jxta.endpoint.router.EndpointRouterMessage.EndpointRouterMessageSignatureBridge bridge)
			throws InvalidKeyException, SignatureException, IOException, SecurityException;

	String getPeerSecurityEngineSignatureAlgorithm();

	byte[] signPSECredentialDocument(IPSECredentialBridge pseCredentialBridge) throws InvalidKeyException, SignatureException, IOException, SecurityException;

	X509Certificate[] generateServiceCertificate(ID assignedID, IPSECredential pseCredential) throws InvalidKeyException, SignatureException, IOException, KeyStoreException;
}