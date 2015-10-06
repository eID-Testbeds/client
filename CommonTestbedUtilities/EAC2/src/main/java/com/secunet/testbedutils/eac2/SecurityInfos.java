package com.secunet.testbedutils.eac2;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;

public class SecurityInfos {
    
    private byte[] data;
    private final LinkedHashMap<Integer, PACEDomainParameter> listPACEDomainParameter;
    private TerminalAuthenticationInfo terminalAuthenticationInfo;
    private int defaultCAKeyID;
    private final LinkedHashMap<Integer, CADomainParameter> listCADomainParameter;
    private final LinkedHashMap<Integer, ChipAuthenticationPublicKeyInfo> listChipAuthenticationPublicKeyInfo;
    private LinkedList<RestrictedIdentificationInfo> listRestrictedIdentificationInfo;
    
    public enum Type {
        UNKNOWN,
        PACEInfo,
        PACEDomainParameterInfo,
        ChipAuthenticationInfo,
        ChipAuthenticationDomainParameterInfo,
        ChipAuthenticationPublicKeyInfo,
        TerminalAuthenticationInfo,
        CardInfoLocator,
        RestrictedIdentificationInfo
    }
    
    SecurityInfos() {
        data = null;
        listPACEDomainParameter = new LinkedHashMap<Integer, PACEDomainParameter>();
        terminalAuthenticationInfo = null;
        listRestrictedIdentificationInfo = new LinkedList<RestrictedIdentificationInfo>();
        defaultCAKeyID = -1;
        listCADomainParameter = new LinkedHashMap<Integer, CADomainParameter>();
        listChipAuthenticationPublicKeyInfo = new LinkedHashMap<Integer, ChipAuthenticationPublicKeyInfo>();
    }
    
    public void fromAsn1(byte[] data, int defaultCAKeyID) throws IOException, EIDException {
        this.data = data;
        LinkedList<PACEInfo> listPACEInfo = new LinkedList<PACEInfo>();
        LinkedList<PACEDomainParameterInfo> listPACEDomainParameterInfo = new LinkedList<PACEDomainParameterInfo>();
        LinkedList<ChipAuthenticationInfo> listCAInfo = new LinkedList<ChipAuthenticationInfo>();
        LinkedList<ChipAuthenticationDomainParameterInfo> listCADomainParameterInfo = new LinkedList<ChipAuthenticationDomainParameterInfo>();
        LinkedList<ChipAuthenticationPublicKeyInfo> listCAPublicKeyInfo = new LinkedList<ChipAuthenticationPublicKeyInfo>();
        LinkedList<TerminalAuthenticationInfo> listTerminalAuthenticationInfo = new LinkedList<TerminalAuthenticationInfo>();
        LinkedList<RestrictedIdentificationInfo> listRestrictedIdentificationInfo = new LinkedList<RestrictedIdentificationInfo>();
        
        parseASN1(data, listPACEInfo, listPACEDomainParameterInfo, listCAInfo, listCADomainParameterInfo,
                listCAPublicKeyInfo, listTerminalAuthenticationInfo, listRestrictedIdentificationInfo, true);
        
        completePACEDomainParameters(listPACEInfo, listPACEDomainParameterInfo);
        this.defaultCAKeyID = defaultCAKeyID;
        completeCADomainParameters(listCAInfo, listCADomainParameterInfo);
        completeChipAuthenticationPublicKeyInfos(listCAPublicKeyInfo);
        terminalAuthenticationInfo = listTerminalAuthenticationInfo.getFirst();
        this.listRestrictedIdentificationInfo = listRestrictedIdentificationInfo;
    }
    
    private void parseASN1(byte[] data, LinkedList<PACEInfo> listPACEInfo,
            LinkedList<PACEDomainParameterInfo> listPACEDomainParameterInfo,
            LinkedList<ChipAuthenticationInfo> listCAInfo,
            LinkedList<ChipAuthenticationDomainParameterInfo> listCADomainParameterInfo,
            LinkedList<ChipAuthenticationPublicKeyInfo> listCAPublicKeyInfo,
            LinkedList<TerminalAuthenticationInfo> listTerminalAuthenticationInfo,
            LinkedList<RestrictedIdentificationInfo> listRestrictedIdentificationInfo, boolean doLog)
            throws IOException, EIDException {
        ASN1InputStream ais = new ASN1InputStream(data);
        try {
            ASN1Set securityInfoSet = (ASN1Set) ais.readObject();
            for (int i = 0; i < securityInfoSet.size(); i++) {
                ASN1Sequence securityInfo = (ASN1Sequence) securityInfoSet.getObjectAt(i);
                ASN1ObjectIdentifier protocol = (ASN1ObjectIdentifier) securityInfo.getObjectAt(0);
                ASN1Encodable required = securityInfo.getObjectAt(1);
                ASN1Encodable optional = (securityInfo.size() != 3) ? null : securityInfo.getObjectAt(2);
                switch (getType(protocol)) {
                    case PACEInfo:
                        // if( doLog ) log.debug("found PACEInfo");
                        
                        PACEInfo pi = new PACEInfo(protocol, doLog);
                        pi.fromAsn1(required, optional);
                        listPACEInfo.add(pi);
                        break;
                    case PACEDomainParameterInfo:
                        // if( doLog )
                        // log.debug("found PACEDomainParameterInfo");
                        
                        PACEDomainParameterInfo pdpi = new PACEDomainParameterInfo(protocol, doLog);
                        pdpi.fromAsn1(required, optional);
                        listPACEDomainParameterInfo.add(pdpi);
                        break;
                    case ChipAuthenticationInfo:
                        // if( doLog )
                        // log.debug("found ChipAuthenticationInfo");
                        
                        ChipAuthenticationInfo ci = new ChipAuthenticationInfo(protocol, doLog);
                        ci.fromAsn1(required, optional);
                        listCAInfo.add(ci);
                        break;
                    case ChipAuthenticationDomainParameterInfo:
                        // if( doLog )
                        // log.debug("found ChipAuthenticationDomainParameterInfo");
                        
                        ChipAuthenticationDomainParameterInfo cdpi = new ChipAuthenticationDomainParameterInfo(
                                protocol, doLog);
                        cdpi.fromAsn1(required, optional);
                        listCADomainParameterInfo.add(cdpi);
                        break;
                    case ChipAuthenticationPublicKeyInfo:
                        // if( doLog )
                        // log.debug("found ChipAuthenticationPublicKeyInfo");
                        
                        ChipAuthenticationPublicKeyInfo cpki = new ChipAuthenticationPublicKeyInfo(protocol, doLog);
                        cpki.fromAsn1(required, optional);
                        listCAPublicKeyInfo.add(cpki);
                        break;
                    case TerminalAuthenticationInfo:
                        // if( doLog )
                        // log.debug("found TerminalAuthenticationInfo");
                        
                        TerminalAuthenticationInfo tai = new TerminalAuthenticationInfo(protocol, doLog);
                        tai.fromAsn1(required, optional);
                        listTerminalAuthenticationInfo.add(tai);
                        break;
                    case CardInfoLocator:
                        // if( doLog ) log.debug("found CardInfoLocator");
                        
                        break;
                    case RestrictedIdentificationInfo:
                        // if( doLog )
                        // log.debug("found RestrictedIdentificationInfo");
                        
                        RestrictedIdentificationInfo rii = new RestrictedIdentificationInfo(protocol, doLog);
                        rii.fromAsn1(required, optional);
                        listRestrictedIdentificationInfo.add(rii);
                        break;
                    case UNKNOWN:
                        // if( doLog ) log.debug("found no eID relevant type");
                        break;
                    default:
                        throw new IOException("reached unreacheable code");
                }
            }
        } finally {
            try {
                ais.close();
            } catch (Exception e) {
                // ???
            }
        }
    }
    
    private void completePACEDomainParameters(LinkedList<PACEInfo> listPACEInfo,
            LinkedList<PACEDomainParameterInfo> listPACEDomainParameterInfo) {
        for (PACEInfo pi : listPACEInfo) {
            for (PACEDomainParameterInfo pdpi : listPACEDomainParameterInfo) {
                if (PACEDomainParameter.getType(pdpi.protocol) != PACEDomainParameter.getType(pi.protocol))
                    continue;
                if (pdpi.getParameterId() != pi.getParameterId())
                    continue;
                if (listPACEDomainParameter.containsKey(pi.getParameterId()))
                    continue;
                
                PACEDomainParameter dp = new PACEDomainParameter(pi.protocol, pi.getVersion(), pi.getParameterId(),
                        pdpi.getDomainParameter(), pdpi.getAlgorithmParameterSpec());
                listPACEDomainParameter.put(dp.getParameterId(), dp);
                // log.debug( "added PACEDomainParameter with parameterId " +
                // dp.getParameterId() );
            }
        }
    }
    
    private void completeCADomainParameters(LinkedList<ChipAuthenticationInfo> listCAInfo,
            LinkedList<ChipAuthenticationDomainParameterInfo> listCADomainParameterInfo) {
        for (ChipAuthenticationInfo ci : listCAInfo) {
            for (ChipAuthenticationDomainParameterInfo cdpi : listCADomainParameterInfo) {
                if (DHDomainParameter.getType(cdpi.protocol) != DHDomainParameter.getType(ci.protocol))
                    continue;
                
                if (cdpi.getKeyId() != ci.getKeyId())
                    continue;
                
                if (listCADomainParameter.containsKey(ci.getKeyId()))
                    continue;
                
                CADomainParameter dp = new CADomainParameter(ci.protocol, ci.getVersion(), ci.getKeyId(),
                        cdpi.getDomainParameter(), cdpi.getAlgorithmParameterSpec());
                listCADomainParameter.put(dp.getKeyId(), dp);
                
                // log.debug( "added CADomainParameter with keyId " +
                // dp.getKeyId() );
            }
        }
    }
    
    private void completeChipAuthenticationPublicKeyInfos(
            LinkedList<ChipAuthenticationPublicKeyInfo> listCAPublicKeyInfo) {
        for (ChipAuthenticationPublicKeyInfo cpki : listCAPublicKeyInfo) {
            if (listChipAuthenticationPublicKeyInfo.containsKey(cpki.getKeyId()))
                continue;
            
            listChipAuthenticationPublicKeyInfo.put(cpki.getKeyId(), cpki);
            
            // log.debug( "added ChipAuthenticationPublicKeyInfo with keyId " +
            // cpki.getKeyId() );
        }
    }
    
    public PACEDomainParameter getDefaultPACEDomainParameter() {
        return listPACEDomainParameter.values().iterator().next();
    }
    
    public TerminalAuthenticationInfo getDefaultTerminalAuthenticationInfo() {
        return terminalAuthenticationInfo;
    }
    
    public RestrictedIdentificationInfo getDefaultRestrictedIdentificationInfo(boolean authorizedOnly) {
        for (RestrictedIdentificationInfo rii : listRestrictedIdentificationInfo) {
            if (rii.isAuthorizedOnly() != authorizedOnly)
                continue;
            return rii;
        }
        return null;
    }
    
    public CADomainParameter getDefaultCADomainParameter() {
        CADomainParameter param = null;
        if (defaultCAKeyID < 0)
            param = listCADomainParameter.values().iterator().next();
        else
            param = listCADomainParameter.get(defaultCAKeyID);

        return param;
    }
    
    public ChipAuthenticationPublicKeyInfo getDefaultChipAuthenticationPublicKeyInfo() {
        ChipAuthenticationPublicKeyInfo pubKey = null;
        if (defaultCAKeyID < 0)
            pubKey = listChipAuthenticationPublicKeyInfo.values().iterator().next();
        else
            pubKey = listChipAuthenticationPublicKeyInfo.get(defaultCAKeyID);

        return pubKey;
    }
    
    public Type getType(ASN1ObjectIdentifier oid) {
        if (EAC2ObjectIdentifiers.id_PACE_DH_GM_3DES_CBC_CBC.equals(oid)
                || EAC2ObjectIdentifiers.id_PACE_DH_GM_AES_CBC_CMAC_128.equals(oid)
                || EAC2ObjectIdentifiers.id_PACE_DH_GM_AES_CBC_CMAC_192.equals(oid)
                || EAC2ObjectIdentifiers.id_PACE_DH_GM_AES_CBC_CMAC_256.equals(oid)
                || EAC2ObjectIdentifiers.id_PACE_ECDH_GM_3DES_CBC_CBC.equals(oid)
                || EAC2ObjectIdentifiers.id_PACE_ECDH_GM_AES_CBC_CMAC_128.equals(oid)
                || EAC2ObjectIdentifiers.id_PACE_ECDH_GM_AES_CBC_CMAC_192.equals(oid)
                || EAC2ObjectIdentifiers.id_PACE_ECDH_GM_AES_CBC_CMAC_256.equals(oid)
                || EAC2ObjectIdentifiers.id_PACE_DH_IM_3DES_CBC_CBC.equals(oid)
                || EAC2ObjectIdentifiers.id_PACE_DH_IM_AES_CBC_CMAC_128.equals(oid)
                || EAC2ObjectIdentifiers.id_PACE_DH_IM_AES_CBC_CMAC_192.equals(oid)
                || EAC2ObjectIdentifiers.id_PACE_DH_IM_AES_CBC_CMAC_256.equals(oid)
                || EAC2ObjectIdentifiers.id_PACE_ECDH_IM_3DES_CBC_CBC.equals(oid)
                || EAC2ObjectIdentifiers.id_PACE_ECDH_IM_AES_CBC_CMAC_128.equals(oid)
                || EAC2ObjectIdentifiers.id_PACE_ECDH_IM_AES_CBC_CMAC_192.equals(oid)
                || EAC2ObjectIdentifiers.id_PACE_ECDH_IM_AES_CBC_CMAC_256.equals(oid)) {
            return Type.PACEInfo;
        }
        if (EAC2ObjectIdentifiers.id_PACE_DH_GM.equals(oid) || EAC2ObjectIdentifiers.id_PACE_ECDH_GM.equals(oid)
                || EAC2ObjectIdentifiers.id_PACE_DH_IM.equals(oid) || EAC2ObjectIdentifiers.id_PACE_ECDH_IM.equals(oid)) {
            return Type.PACEDomainParameterInfo;
        }
        if (EAC2ObjectIdentifiers.id_CA_DH_3DES_CBC_CBC.equals(oid)
                || EAC2ObjectIdentifiers.id_CA_DH_AES_CBC_CMAC_128.equals(oid)
                || EAC2ObjectIdentifiers.id_CA_DH_AES_CBC_CMAC_192.equals(oid)
                || EAC2ObjectIdentifiers.id_CA_DH_AES_CBC_CMAC_256.equals(oid)
                || EAC2ObjectIdentifiers.id_CA_ECDH_3DES_CBC_CBC.equals(oid)
                || EAC2ObjectIdentifiers.id_CA_ECDH_AES_CBC_CMAC_128.equals(oid)
                || EAC2ObjectIdentifiers.id_CA_ECDH_AES_CBC_CMAC_192.equals(oid)
                || EAC2ObjectIdentifiers.id_CA_ECDH_AES_CBC_CMAC_256.equals(oid)) {
            return Type.ChipAuthenticationInfo;
        }
        if (EAC2ObjectIdentifiers.id_CA_DH.equals(oid) || EAC2ObjectIdentifiers.id_CA_ECDH.equals(oid)) {
            return Type.ChipAuthenticationDomainParameterInfo;
        }
        if (EAC2ObjectIdentifiers.id_PK_DH.equals(oid) || EAC2ObjectIdentifiers.id_PK_ECDH.equals(oid)) {
            return Type.ChipAuthenticationPublicKeyInfo;
        }
        if (EAC2ObjectIdentifiers.id_TA.equals(oid)) {
            return Type.TerminalAuthenticationInfo;
        }
        if (EAC2ObjectIdentifiers.id_CI.equals(oid)) {
            return Type.CardInfoLocator;
        }
        if (EAC2ObjectIdentifiers.id_RI_DH_SHA_1.equals(oid) || EAC2ObjectIdentifiers.id_RI_DH_SHA_224.equals(oid)
                || EAC2ObjectIdentifiers.id_RI_DH_SHA_256.equals(oid)
                || EAC2ObjectIdentifiers.id_RI_ECDH_SHA_1.equals(oid)
                || EAC2ObjectIdentifiers.id_RI_ECDH_SHA_224.equals(oid)
                || EAC2ObjectIdentifiers.id_RI_ECDH_SHA_256.equals(oid)) {
            return Type.RestrictedIdentificationInfo;
        }
        
        return Type.UNKNOWN;
    }
    
    public boolean contains(SecurityInfos securityInfos) throws EIDException {
        LinkedList<PACEInfo> listPACEInfo = new LinkedList<PACEInfo>();
        LinkedList<PACEDomainParameterInfo> listPACEDomainParameterInfo = new LinkedList<PACEDomainParameterInfo>();
        LinkedList<ChipAuthenticationInfo> listCAInfo = new LinkedList<ChipAuthenticationInfo>();
        LinkedList<ChipAuthenticationDomainParameterInfo> listCADomainParameterInfo = new LinkedList<ChipAuthenticationDomainParameterInfo>();
        LinkedList<ChipAuthenticationPublicKeyInfo> listCAPublicKeyInfo = new LinkedList<ChipAuthenticationPublicKeyInfo>();
        LinkedList<TerminalAuthenticationInfo> listTerminalAuthenticationInfo = new LinkedList<TerminalAuthenticationInfo>();
        LinkedList<RestrictedIdentificationInfo> listRestrictedIdentificationInfo = new LinkedList<RestrictedIdentificationInfo>();
        
        LinkedList<PACEInfo> listPACEInfoSI = new LinkedList<PACEInfo>();
        LinkedList<PACEDomainParameterInfo> listPACEDomainParameterInfoSI = new LinkedList<PACEDomainParameterInfo>();
        LinkedList<ChipAuthenticationInfo> listCAInfoSI = new LinkedList<ChipAuthenticationInfo>();
        LinkedList<ChipAuthenticationDomainParameterInfo> listCADomainParameterInfoSI = new LinkedList<ChipAuthenticationDomainParameterInfo>();
        LinkedList<ChipAuthenticationPublicKeyInfo> listCAPublicKeyInfoSI = new LinkedList<ChipAuthenticationPublicKeyInfo>();
        LinkedList<TerminalAuthenticationInfo> listTerminalAuthenticationInfoSI = new LinkedList<TerminalAuthenticationInfo>();
        LinkedList<RestrictedIdentificationInfo> listRestrictedIdentificationInfoSI = new LinkedList<RestrictedIdentificationInfo>();
        
        try {
            parseASN1(data, listPACEInfo, listPACEDomainParameterInfo, listCAInfo, listCADomainParameterInfo,
                    listCAPublicKeyInfo, listTerminalAuthenticationInfo, listRestrictedIdentificationInfo, false);
            parseASN1(securityInfos.data, listPACEInfoSI, listPACEDomainParameterInfoSI, listCAInfoSI,
                    listCADomainParameterInfoSI, listCAPublicKeyInfoSI, listTerminalAuthenticationInfoSI,
                    listRestrictedIdentificationInfoSI, false);
        } catch (IOException e) {
            throw new EIDException(e);
        }
        
        return listPACEInfo.containsAll(listPACEInfoSI)
                && listPACEDomainParameterInfo.containsAll(listPACEDomainParameterInfoSI)
                && listCAInfo.containsAll(listCAInfoSI)
                && listCADomainParameterInfo.containsAll(listCADomainParameterInfoSI)
                && listCAPublicKeyInfo.containsAll(listCAPublicKeyInfoSI)
                && listTerminalAuthenticationInfo.containsAll(listTerminalAuthenticationInfoSI)
                && listRestrictedIdentificationInfo.containsAll(listRestrictedIdentificationInfoSI);
    }
    
}
