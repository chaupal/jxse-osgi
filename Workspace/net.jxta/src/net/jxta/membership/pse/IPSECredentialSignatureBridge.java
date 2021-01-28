package net.jxta.membership.pse;

import java.io.InputStream;

public interface IPSECredentialSignatureBridge {

	String getSignatureAlgorithm();

	IPSECredential getPSECredential();

	InputStream getInputStream();

}