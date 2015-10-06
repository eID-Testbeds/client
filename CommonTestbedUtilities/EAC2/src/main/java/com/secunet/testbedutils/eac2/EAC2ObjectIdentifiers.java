package com.secunet.testbedutils.eac2;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.eac.EACObjectIdentifiers;

public interface EAC2ObjectIdentifiers extends EACObjectIdentifiers {
    /**
     * id-eID OBJECT IDENTIFIER ::= {
     * bsi-de applications(3) 2 }
     */
    static final ASN1ObjectIdentifier id_eID = new ASN1ObjectIdentifier(bsi_de + ".3.2");

    /**
     * id-SecurityObject OBJECT IDENTIFIER ::= {
     * bsi-de applications(3) eID(2) 1 }
     */
    static final ASN1ObjectIdentifier id_SecurityObject = new ASN1ObjectIdentifier(id_eID + ".1");
    
    /**
     * id-PACE OBJECT IDENTIFIER ::= {
     * bsi-de protocols(2) smartcard(2) 4 }
     */
    static final ASN1ObjectIdentifier id_PACE = new ASN1ObjectIdentifier(bsi_de + ".2.2.4");

    static final ASN1ObjectIdentifier id_PACE_DH_GM = new ASN1ObjectIdentifier(id_PACE + ".1");
    static final ASN1ObjectIdentifier id_PACE_DH_GM_3DES_CBC_CBC = new ASN1ObjectIdentifier(id_PACE_DH_GM + ".1");
    static final ASN1ObjectIdentifier id_PACE_DH_GM_AES_CBC_CMAC_128 = new ASN1ObjectIdentifier(id_PACE_DH_GM + ".2");
    static final ASN1ObjectIdentifier id_PACE_DH_GM_AES_CBC_CMAC_192 = new ASN1ObjectIdentifier(id_PACE_DH_GM + ".3");
    static final ASN1ObjectIdentifier id_PACE_DH_GM_AES_CBC_CMAC_256 = new ASN1ObjectIdentifier(id_PACE_DH_GM + ".4");
    static final ASN1ObjectIdentifier id_PACE_ECDH_GM = new ASN1ObjectIdentifier(id_PACE + ".2");
    static final ASN1ObjectIdentifier id_PACE_ECDH_GM_3DES_CBC_CBC = new ASN1ObjectIdentifier(id_PACE_ECDH_GM + ".1");
    static final ASN1ObjectIdentifier id_PACE_ECDH_GM_AES_CBC_CMAC_128 = new ASN1ObjectIdentifier(id_PACE_ECDH_GM + ".2");
    static final ASN1ObjectIdentifier id_PACE_ECDH_GM_AES_CBC_CMAC_192 = new ASN1ObjectIdentifier(id_PACE_ECDH_GM + ".3");
    static final ASN1ObjectIdentifier id_PACE_ECDH_GM_AES_CBC_CMAC_256 = new ASN1ObjectIdentifier(id_PACE_ECDH_GM + ".4");
    static final ASN1ObjectIdentifier id_PACE_DH_IM = new ASN1ObjectIdentifier(id_PACE + ".3");
    static final ASN1ObjectIdentifier id_PACE_DH_IM_3DES_CBC_CBC = new ASN1ObjectIdentifier(id_PACE_DH_IM + ".1");
    static final ASN1ObjectIdentifier id_PACE_DH_IM_AES_CBC_CMAC_128 = new ASN1ObjectIdentifier(id_PACE_DH_IM + ".2");
    static final ASN1ObjectIdentifier id_PACE_DH_IM_AES_CBC_CMAC_192 = new ASN1ObjectIdentifier(id_PACE_DH_IM + ".3");
    static final ASN1ObjectIdentifier id_PACE_DH_IM_AES_CBC_CMAC_256 = new ASN1ObjectIdentifier(id_PACE_DH_IM + ".4");
    static final ASN1ObjectIdentifier id_PACE_ECDH_IM = new ASN1ObjectIdentifier(id_PACE + ".4");
    static final ASN1ObjectIdentifier id_PACE_ECDH_IM_3DES_CBC_CBC = new ASN1ObjectIdentifier(id_PACE_ECDH_IM + ".1");
    static final ASN1ObjectIdentifier id_PACE_ECDH_IM_AES_CBC_CMAC_128 = new ASN1ObjectIdentifier(id_PACE_ECDH_IM + ".2");
    static final ASN1ObjectIdentifier id_PACE_ECDH_IM_AES_CBC_CMAC_192 = new ASN1ObjectIdentifier(id_PACE_ECDH_IM + ".3");
    static final ASN1ObjectIdentifier id_PACE_ECDH_IM_AES_CBC_CMAC_256 = new ASN1ObjectIdentifier(id_PACE_ECDH_IM + ".4");
    
    // additional CA algorithms
    static final ASN1ObjectIdentifier id_CA_DH_AES_CBC_CMAC_128 = new ASN1ObjectIdentifier(id_CA_DH + ".2");
    static final ASN1ObjectIdentifier id_CA_DH_AES_CBC_CMAC_192 = new ASN1ObjectIdentifier(id_CA_DH + ".3");
    static final ASN1ObjectIdentifier id_CA_DH_AES_CBC_CMAC_256 = new ASN1ObjectIdentifier(id_CA_DH + ".4");
    static final ASN1ObjectIdentifier id_CA_ECDH_AES_CBC_CMAC_128 = new ASN1ObjectIdentifier(id_CA_ECDH + ".2");
    static final ASN1ObjectIdentifier id_CA_ECDH_AES_CBC_CMAC_192 = new ASN1ObjectIdentifier(id_CA_ECDH + ".3");
    static final ASN1ObjectIdentifier id_CA_ECDH_AES_CBC_CMAC_256 = new ASN1ObjectIdentifier(id_CA_ECDH + ".4");
    
    /**
     * id-CI OBJECT IDENTIFIER ::= {
     * bsi-de protocols(2) smartcard(2) 6 }
     */
    static final ASN1ObjectIdentifier id_CI = new ASN1ObjectIdentifier(bsi_de + ".2.2.6");
    
    /**
     * id-roles OBJECT IDENTIFIER ::= {
     * bsi-de applications(3) mrtd(1) 2 }
     */
    static final ASN1ObjectIdentifier id_roles = new ASN1ObjectIdentifier(bsi_de + ".3.1.2");
    static final ASN1ObjectIdentifier id_IS = new ASN1ObjectIdentifier(id_roles + ".1");
    static final ASN1ObjectIdentifier id_AT = new ASN1ObjectIdentifier(id_roles + ".2");
    static final ASN1ObjectIdentifier id_ST = new ASN1ObjectIdentifier(id_roles + ".3");

    /**
     * id-extensions OBJECT IDENTIFIER ::= {
     * bsi-de applictions(3) mrtd(1) 3 }
     */
    static final ASN1ObjectIdentifier id_extensions = new ASN1ObjectIdentifier(bsi_de + ".3.1.3");
    // certificate description extension
    static final ASN1ObjectIdentifier id_description = new ASN1ObjectIdentifier(id_extensions + ".1");
    static final ASN1ObjectIdentifier id_plainFormat = new ASN1ObjectIdentifier(id_description + ".1");
    static final ASN1ObjectIdentifier id_htmlFormat = new ASN1ObjectIdentifier(id_description + ".2");
    static final ASN1ObjectIdentifier id_pdfFormat = new ASN1ObjectIdentifier(id_description + ".3");
    // terminal sector extension
    static final ASN1ObjectIdentifier id_sector = new ASN1ObjectIdentifier(id_extensions + ".2");

    
    /**
     * id-auxiliaryData OBJECT IDENTIFIER ::= {
     * bsi-de applications(3) mrtd(1) 4 }
     */
    static final ASN1ObjectIdentifier id_auxiliaryData = new ASN1ObjectIdentifier(bsi_de + ".3.1.4");
    static final ASN1ObjectIdentifier id_dateOfBirth   = new ASN1ObjectIdentifier(id_auxiliaryData + ".1");
    static final ASN1ObjectIdentifier id_dateOfExpiry  = new ASN1ObjectIdentifier(id_auxiliaryData + ".2");
    static final ASN1ObjectIdentifier id_communityID   = new ASN1ObjectIdentifier(id_auxiliaryData + ".3");
    
    /**
     * id-RI OBJECT IDENTIFIER ::= {
     * bsi-de protocols(2) smartcard(2) 5 }
     */
    static final ASN1ObjectIdentifier id_RI				= new ASN1ObjectIdentifier(bsi_de + ".2.2.5");
    static final ASN1ObjectIdentifier id_RI_DH			= new ASN1ObjectIdentifier(id_RI + ".1");
    static final ASN1ObjectIdentifier id_RI_DH_SHA_1		= new ASN1ObjectIdentifier(id_RI_DH + ".1");
    static final ASN1ObjectIdentifier id_RI_DH_SHA_224	= new ASN1ObjectIdentifier(id_RI_DH + ".2");
    static final ASN1ObjectIdentifier id_RI_DH_SHA_256	= new ASN1ObjectIdentifier(id_RI_DH + ".3");
    static final ASN1ObjectIdentifier id_RI_ECDH			= new ASN1ObjectIdentifier(id_RI + ".2");
    static final ASN1ObjectIdentifier id_RI_ECDH_SHA_1	= new ASN1ObjectIdentifier(id_RI_ECDH + ".1");
    static final ASN1ObjectIdentifier id_RI_ECDH_SHA_224	= new ASN1ObjectIdentifier(id_RI_ECDH + ".2");
    static final ASN1ObjectIdentifier id_RI_ECDH_SHA_256	= new ASN1ObjectIdentifier(id_RI_ECDH + ".3");
    
    
}
