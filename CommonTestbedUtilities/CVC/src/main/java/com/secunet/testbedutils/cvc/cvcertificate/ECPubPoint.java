package com.secunet.testbedutils.cvc.cvcertificate;

import java.math.BigInteger;
/**
 * This class stores simply the public point of an EC asymmetric key pair
 * @author meier.marcus
 *
 */
public class ECPubPoint {

	private BigInteger m_x;
	private BigInteger m_y;
	/**
	 * 
	 * @brief copy constructor 
	 *
	 * @param point copies these object
	 */
	public ECPubPoint(ECPubPoint point)
	{
		this.m_x = new BigInteger(1,point.getX().toByteArray());
		this.m_y = new BigInteger(1,point.getY().toByteArray());
	}
	
	/**
	 * @brief constructor
	 *
	 * @param x x coordinate of the point 
	 * @param y y coordinate of the point 
	 */
	public ECPubPoint(BigInteger x,BigInteger y)
	{
		this.m_x = x;
		this.m_y = y;
	}
	/**
	 * 
	 * @brief returns the x coordinate
	 * 
	 * @return returns the coordinate as BigInteger
	 */
	public BigInteger getX() {
		return this.m_x;
	}
	/**
	 * 
	 * @brief returns the y coordinate
	 * 
	 * @return returns the coordinate as BigInteger
	 */
	public BigInteger getY() {
		return this.m_y;
	}
	
}
