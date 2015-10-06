package com.secunet.testbedutils.cvc.cvcertificate;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.secunet.testbedutils.cvc.cvcertificate.exception.CVDecodeErrorException;
import com.secunet.testbedutils.cvc.cvcertificate.exception.CVInvalidDateException;

/**
 * @class  CCVDate
 * @brief  This class is used for date calculation and generation
 * 
 * 
 * @author meier.marcus
 * @version 1.0
 * @created 27-Aug-2009 14:18:51
 */
public class CVDate {

	/**
	 * < this member hold the date of this object in second
	 */
	private Date m_currentDate = null;

	
	/**
	 * @brief constructor
	 */
	public CVDate(){	 
	}

	/**
	 * @brief This function encode the date for the certificate
	 * 
	 * 
	 * @return returns the encoded date
	 */
	public DataBuffer generateDate()
	{		
		DataBuffer dateOutput = new DataBuffer();
		      
		
		DateFormat dateFormat = new SimpleDateFormat("yyMMdd"); 
		//dateFormat.setTimeZone(TimeZone.getDefault());
		String strDate = dateFormat.format(m_currentDate);
		
		//convert the date as ASCII string
		for(int i = 0; i < 6;i++)
		{
			//convert from ASCII to BCD unpacked
			dateOutput.append((byte)(strDate.charAt(i) - 0x30));
		}
		      
		return dateOutput;
	}

	/**
	 * @brief This function decode a BCD unpacked date data stream
	 * 
	 * @param buffer consigns the Date as raw BCD encoded DataBuffer
	 * @throws CVInvalidDateException 
	 * @throws CVDecodeErrorException 
	 */
	public void parseRawDate(DataBuffer buffer) throws CVInvalidDateException, CVDecodeErrorException{
		//check the length
		
		if(buffer.size() != 6)
		{
			//m_rLog << "Error: Date size " << (unsigned int)buffer.size() << " unexpected" << std::endl;
			throw new CVDecodeErrorException();
		}
		// convert from BCD unpacked to unsigned int
		int year = buffer.get(0)*10 + buffer.get(1);
		int mon = buffer.get(2)*10 + buffer.get(3); 
		int day = buffer.get(4)*10 + buffer.get(5);
		
		//simple parameter check (invalid BCD encoding)
		if(mon > 12 || mon < 1 || day > 31 || day < 1 || year > 99 || year < 0)
		{
			//m_rLog << "Error: Date unexpected " << year << ":" << mon << ":" << day << std::endl;
			throw new CVInvalidDateException();
		}
		
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(2000+year,mon-1,day);
		m_currentDate = cal.getTime();
		
	}

	/**
	 * This function set the date of this object
	 * 
	 * @param date consigns the new date for this object
	 */
	public void setDate(Date date){
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);
		m_currentDate = cal.getTime();
	}

	/**
	 * @brief This function return the date of this object
	 * 
	 * @return returns date of this object or null
	 */
	public Date getDate(){
		return m_currentDate;
	}
	/**
	 * @brief this method returns an Date object of the current day but without a time
	 * @return a Date without the Time
	 */
	public static Date getCurrentDateWithoutTime()
	{
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);
		
		return cal.getTime();
	}

}
