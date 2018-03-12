package com.secunet.ipsmall.cardsimulation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.secunet.testbedutils.cvc.cvcertificate.CVCertificate;
import com.secunet.testbedutils.cvc.cvcertificate.DataBuffer;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.secunet.ipsmall.util.VariableParameterContainer;
import com.secunet.testbedutils.utilities.VariableParser;

/**
 * Personalization of PersoSim
 */
public class PersoSimPersonalization implements ICardPersonalization {

    /**
     * Certificate holder name parser.
     */
    private static class CertificateHolderName {
        private static final String CERT_NAME_PATTERN = "^([A-Z]{2})([a-zA-Z]*)(\\w{5})$";

        private String name;
        private String countryCode = "";
        private String holderMnemonic = "";
        private String sequenceNumber = "";

        /**
         * Parses a certificate holder name.
         * @param name Name.
         */
        public CertificateHolderName(String name) {
            this.name = name;

            Pattern pattern = Pattern.compile(CERT_NAME_PATTERN);
            Matcher matcher = pattern.matcher(name);

            if (matcher.matches()) {
                countryCode = matcher.group(1);
                holderMnemonic = matcher.group(2);
                sequenceNumber = matcher.group(3);
            }
        }

        /**
         * Gets name.
         * @return Name.
         */
        public String getName() {
            return name;
        }

        /**
         * Gets country code.
         * @return Country code.
         */
        public String getCountryCode() {
            return countryCode;
        }

        /**
         * Gets holder mnemonic.
         * @return Holder mnemonic.
         */
        public String getHolderMnemonic() {
            return holderMnemonic;
        }

        /**
         * Gets sequence number.
         * @return Sequence number.
         */
        public String getSequenceNumber() {
            return sequenceNumber;
        }
    };

    private VariableParameterContainer variables = null;

	private List<CVCertificate> trustpointCerts = new ArrayList<CVCertificate>();
	private Date cardDate = null;
	private String pin = null;
	
	private String configFileTemplate = "";
	private File profileConfigurationFile = null;
	
	private SimpleDateFormat dateFormat = null;
	private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss.S";

    private static final String TRUSTPOINT1 = "cardsimulation.cvca.trustpoint1";
    private static final String TRUSTPOINT2 = "cardsimulation.cvca.trustpoint2";

    private static final String PUBKEY_SUFFIX = ".pubkey";
    private static final String EFFDATE_SUFFIX = ".effdate";
    private static final String EXPDATE_SUFFIX = ".expdate";
    private static final String COUNTRYCODE_SUFFIX = ".countrycode";
    private static final String HOLDERMNEMONIC_SUFFIX = ".holdermnemonic";
    private static final String SEQUENCENUMBER_SUFFIX = ".sequencenumber";



	private static final String keyCardDate = "cardsimulation.cardDate";

	private static final String keyPIN = "cardsimulation.pin";
	
	/**
	 * Creates a new object for personalization of a PersoSim profile.
	 * 
	 * @param profileConfigTemplate Configuration template file of PersoSim profile.
	 * @param newProfilConfig Configuration template file of PersoSim profile.
	 * @throws IOException
	 */
	public PersoSimPersonalization(File profileConfigTemplate, File newProfilConfig) throws IOException {
        Security.addProvider(new BouncyCastleProvider());

        variables = new VariableParameterContainer();

		if (profileConfigTemplate.canRead()) {
	         FileReader reader = new FileReader(profileConfigTemplate);
	         char[] temp = new char[(int) profileConfigTemplate.length()];
	         reader.read(temp);
	         reader.close();
	         configFileTemplate = new String(temp);
		} else
			throw new FileNotFoundException("File " + profileConfigTemplate.getAbsolutePath() + " not found or not readable!");
		
		this.profileConfigurationFile = newProfilConfig;
		
		this.dateFormat = new SimpleDateFormat(DATE_PATTERN);
	}

	@Override
	public void addTrustpoint(CVCertificate trustpointCertificate) {
			this.trustpointCerts.add(trustpointCertificate);
	}

	@Override
	public void addCardDate(Date cardDate) {
		this.cardDate = cardDate;
	}
	
	@Override
	public void addCardPIN(String pin) {
		this.pin = pin;
	}

	@Override
	public void personalizeCard() throws Exception {
		
		// convert public keys for trustpoint
        if (trustpointCerts.size() >= 1) {
            personalizeTrustpoint(trustpointCerts.get(0), TRUSTPOINT1);
        }

        if (trustpointCerts.size() >= 2) {
            personalizeTrustpoint(trustpointCerts.get(1), TRUSTPOINT2);
        }
		
		// convert date
		variables.addKeyValue(keyCardDate, this.dateFormat.format(cardDate) + " UTC");
		
		// convert pin
		DataBuffer pin = new DataBuffer(this.pin);
		variables.addKeyValue(keyPIN, pin.asHex(""));
		
		// delete if exists
		if (this.profileConfigurationFile.exists())
			this.profileConfigurationFile.delete();
				
		// replace keys in configuration template and write to file
		VariableParser parser = new VariableParser(variables);
        FileWriter writer = new FileWriter(this.profileConfigurationFile ,true);
        writer.write(parser.format(configFileTemplate));
        writer.flush();
        writer.close();
	}

    /**
     * Personalizes trustpoint.
     * @param cert Certificate containing the trustpoint.
     * @param trustpointKey Properties key of trustpoint.
     * @throws Exception
     */
    private void personalizeTrustpoint(CVCertificate cert, String trustpointKey) throws Exception {
        // public key
        DataBuffer pubKey = new DataBuffer(cert.getPublicKey().getPublicKey().getEncoded());
        variables.addKeyValue(trustpointKey + PUBKEY_SUFFIX, pubKey.asHex(""));

        // eff date
        Date effDate = cert.getEffDate().getDate();
        variables.addKeyValue(trustpointKey + EFFDATE_SUFFIX, this.dateFormat.format(effDate) + " UTC");

        // eff date
        Date expDate = cert.getExpDate().getDate();
        variables.addKeyValue(trustpointKey + EXPDATE_SUFFIX, this.dateFormat.format(expDate) + " UTC");

        // holder name
        CertificateHolderName name = new CertificateHolderName(cert.getCertHolderRef());
        variables.addKeyValue(trustpointKey + COUNTRYCODE_SUFFIX, name.getCountryCode());
        variables.addKeyValue(trustpointKey + HOLDERMNEMONIC_SUFFIX, name.getHolderMnemonic());
        variables.addKeyValue(trustpointKey + SEQUENCENUMBER_SUFFIX, name.getSequenceNumber());
    }
}
