package net.jxta.membership.pse;

import net.jxta.document.XMLSignature;
import net.jxta.document.XMLSignatureInfo;

public interface IPSEAdvertisementSignatureToken {

	/**
	 * If the advertisement is validated with the signature then true
	 *
	 * @return boolean isValid
	 */
	XMLSignatureInfo getXMLSignatureInfo();

	/**
	 * If the peerid that signed the advertisment is present in the
	 * membership keystore then true
	 *
	 * @return boolean isMember
	 */
	XMLSignature getXMLSignature();

}