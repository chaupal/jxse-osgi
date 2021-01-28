package net.jxta.endpoint;

import net.jxta.document.MimeMediaType;

public interface IEndpointService {

	String VERIFIED_ADDRESS_SET = "VERIFIED_ADDRESS_SET";
	String MESSAGE_SIGNER_SET = "MESSAGE_SIGNER_SET";
	String MESSAGE_LOOPBACK = "MESSAGE_LOOPBACK";
	/**
	 * The Wire Message Format we will use by default.
	 */
	MimeMediaType DEFAULT_MESSAGE_TYPE = new MimeMediaType("application/x-jxta-msg").intern();
	/**
	 * The name of this service.
	 */
	String ENDPOINTSERVICE_NAME = "EndpointService";
	/**
	 * The Message empty namespace. This namespace is reserved for use by
	 * applications. It will not be used by core protocols.
	 */
	String MESSAGE_EMPTY_NS = "";
	/**
	 * The Message "jxta" namespace. This namespace is reserved for use by
	 * core protocols. It will not be used by applications.
	 */
	String MESSAGE_JXTA_NS = "jxta";
	/**
	 * Namespace in which the message source address will be placed.
	 */
	String MESSAGE_SOURCE_NS = MESSAGE_JXTA_NS;
	/**
	 * Element name in which the message source address will be placed.
	 */
	String MESSAGE_SOURCE_NAME = "EndpointSourceAddress";
	/**
	 * Namespace in which the message destination address will be placed.
	 */
	String MESSAGE_DESTINATION_NS = MESSAGE_JXTA_NS;
	/**
	 * Element name in which the message destination address will be placed.
	 * This element is used for loopback detection during propagate. Only
	 * propagate messages currently contain this element.
	 */
	String MESSAGE_DESTINATION_NAME = "EndpointDestinationAddress";
	/**
	 * Namespace in which the message source peer address will be placed.
	 */
	String MESSAGE_SRCPEERHDR_NS = MESSAGE_JXTA_NS;
	/**
	 * Element name in which the message source peer address will be placed.
	 * This element is used for loopback detection during propagate. Only
	 * propagated messages currently contain this element.
	 */
	String MESSAGE_SRCPEERHDR_NAME = "EndpointHeaderSrcPeer";

}