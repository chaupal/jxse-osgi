package net.jxta.membership;

import java.io.InputStream;
import java.security.PrivateKey;

import net.jxta.membership.pse.IPSECredential;

public interface IPSECredentialBridge {

	void setPrivateKey(java.security.PrivateKey privateKey);

	PrivateKey getPrivateKey();

	String getSignatureAlgorithm();

	IPSECredential getPSECredential();

	InputStream getInputStream();

}