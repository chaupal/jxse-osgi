package net.jxta.membership.pse;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.X509Certificate;

import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocument;
import net.jxta.exception.PeerGroupException;
import net.jxta.id.ID;
import net.jxta.membership.IPSECredentialBridge;
import net.jxta.service.Service;
import net.jxta.socket.ISocketPSEBridge;

public interface IPSECredential {

	/**
	 * {@inheritDoc}
	 */
	boolean equals(Object target);

	/**
	 * {@inheritDoc}
	 */
	int hashCode();

	/**
	 * {@inheritDoc}
	 */
	String toString();

	/**
	 * Add a listener
	 *
	 * @param listener the listener
	 */
	void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Add a listener
	 *
	 * @param propertyName the property to watch
	 * @param listener     the listener
	 */
	void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

	/**
	 * Remove a listener
	 *
	 * @param listener the listener
	 */
	void removePropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Remove a listener
	 *
	 * @param propertyName the property which was watched
	 * @param listener     the listener
	 */
	void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);

	/**
	 * {@inheritDoc}
	 */
	ID getPeerGroupID();

	/**
	 * {@inheritDoc}
	 */
	ID getPeerID();

	/**
	 * {@inheritDoc}
	 * <p/>
	 * A PSE Credential is valid as long as the associated certificate is
	 * valid.
	 */
	boolean isExpired();

	/**
	 * {@inheritDoc}
	 * <p/>
	 * A PSE Credential is valid as long as the associated certificate is
	 * valid and as long as the membership service still has the credential.
	 */
	boolean isValid();

	/**
	 * {@inheritDoc}
	 */
	Object getSubject();

	/**
	 * {@inheritDoc}
	 */
	Service getSourceService();

	/**
	 * {@inheritDoc}
	 */
	StructuredDocument<?> getDocument(MimeMediaType encodeAs) throws Exception;

	/**
	 * Returns the certificate associated with this credential.
	 *
	 * @return the certificate associated with this credential.
	 */
	X509Certificate getCertificate();

	/**
	 * Returns the certificate chain associated with this credential.
	 *
	 * @return the certificate chain associated with this credential.
	 */
	X509Certificate[] getCertificateChain();

	/**
	 * Support for TlsTransport key requirement
	 * @param pseCredentialKeyRetriever
	 */
	void tlsKeyBridge(IPSECredentialBridge pseCredentialKeyRetriever)
			throws SecurityException;

	/**
	 * Support for JxtaSocketInputStream key requirement
	 * @param pseCredentialKeyRetriever
	 */
	void socketKeyBridge( ISocketPSEBridge pseCredentialKeyRetriever)
			throws SecurityException;

	PrivateKey getPrivateKey();

	/**
	 * Returns the key id associated with this credential, if any. Only locally
	 * generated credentials have a key ID.
	 *
	 * @return Returns the key id associated with this credential, if any.
	 */
	ID getKeyID();

	/**
	 * Get a Signature object based upon the private key associated with this
	 * credential.
	 *
	 * @param algorithm the signing algorithm to use.
	 * @return Signature.
	 */
	Signature getSigner(String algorithm) throws NoSuchAlgorithmException;

	/**
	 * /**
	 * Get a Signature verifier object based upon the certificate associated
	 * with this credential.
	 *
	 * @param algorithm the signing algorithm to use.
	 * @return Signature.
	 */
	Signature getSignatureVerifier(String algorithm) throws NoSuchAlgorithmException;

	X509Certificate[] generateServiceCertificate(ID assignedID)
			throws IOException, KeyStoreException, InvalidKeyException, SignatureException;

	IPSECredential getServiceCredential(ID assignedID)
			throws IOException, PeerGroupException, InvalidKeyException, SignatureException;

	void pseKeyBridge(IPSECredentialBridge pseCredentialBridge);

}