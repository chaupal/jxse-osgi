package net.jxta.membership.pse;

public interface IPSEAdvertisementValidationToken {

	/**
	 * If the advertisement is validated with the signature then true
	 *
	 * @return boolean isValid
	 */
	boolean isValid();

	/**
	 * If the peerid that signed the advertisment is present in the
	 * membership keystore then true
	 *
	 * @return boolean isMember
	 */
	boolean isMember();

	/**
	 * If the publickey (corresponding to the peerid) in the keystore is
	 * identical to the public key supplied with the advertisement then
	 * true
	 *
	 * @return boolean isValid
	 */
	boolean isCorrectMembershipKey();

}