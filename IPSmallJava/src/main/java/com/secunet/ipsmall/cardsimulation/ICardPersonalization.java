package com.secunet.ipsmall.cardsimulation;

import com.secunet.testbedutils.cvc.cvcertificate.CVCertificate;

import java.util.Date;

/**
 * Interface for card simulation personalization.
 */
public interface ICardPersonalization {
	/**
	 * Adds trustpoint to personalization.
	 * 
	 * @param trustpointCertificate Trustpoint as CV certificate.
	 */
	public void addTrustpoint(CVCertificate trustpointCertificate);
	
	/**
	 * Adds current card date to personalization.
	 * 
	 * @param cardDate Current date of card.
	 */
	public void addCardDate(Date cardDate);
	
	/**
	 * Adds pin to card.
	 * 
	 * @param pin The pin
	 */
	public void addCardPIN(String pin);
	
	/**
	 * Starts card personalization.
	 * 
	 * @throws Exception
	 */
	public void personalizeCard() throws Exception;
}
