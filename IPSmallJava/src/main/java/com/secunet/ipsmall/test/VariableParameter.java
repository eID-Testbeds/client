package com.secunet.ipsmall.test;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.secunet.testbedutils.utilities.Base64Util;
import com.secunet.testbedutils.utilities.CommonUtil;
import com.secunet.testbedutils.utilities.VariableParser.VariableProvider;

public class VariableParameter implements VariableProvider {
    
    public final static String c_eCardTestCaseNumber = "ecard.testcase.number";
    public final static String c_eCardTestCaseDescription = "ecard.testcase.description";
    
    public final static String c_clientURL = "client.url";
    
    public final static String c_eServiceHostname = "eservice.hostname";
    public final static String c_eServicePort = "eservice.port";
    public final static String c_eServiceTCTokenURLPath = "eservice.tctoken.urlpath";
    public final static String c_eServiceRefreshURLPath = "eservice.refresh.urlpath";
    public final static String c_eServiceCommunicationErrorURLPath = "eservice.communicationerror.urlpath";
    public final static String c_eServiceRedirectURL = "eservice.redirect.urlpath";
    
    public final static String c_tcTokenProviderHost = "tctokenprovider.hostname";
    public final static String c_tcTokenProviderPort = "tctokenprovider.port";
    public final static String c_tcTokenProviderTCTokenURL = "tctokenprovider.tctoken.urlpath";
    
    public final static String c_commErrorAddressServerHost = "commerroraddressserver.hostname";
    public final static String c_commErrorAddressServerPort = "commerroraddressserver.port";
    public final static String c_commErrorAddressServerCommunicationErrorPageURL = "commerroraddressserver.communicationerror.urlpath";
        
    public final static String c_eidServiceHostname = "eidservice.hostname";
    public final static String c_eidServicePort = "eidservice.port";
    public final static String c_eidServicePSKKey = "eidservice.pskkey";
    
    public final static String c_eidServiceCV_CVCA = "eidservice.cv.cvca";
    public final static String c_eidServiceCV_DVCA = "eidservice.cv.dvca";
    public final static String c_eidServiceCV_TERM = "eidservice.cv.terminal";
    public final static String c_eidServiceCV_TERM_KEY = "eidservice.cv.terminal.key";
    public final static String c_eidServiceCV_TERM_SECTOR = "eidservice.cv.terminal.sector";
    public final static String c_eidServiceCertDescription = "eidservice.cert.description";
    public final static String c_eidServiceChatRequired = "eidservice.chat.req";
    public final static String c_eidServiceChatOptional = "eidservice.chat.opt";
    public final static String c_eidServiceAuxData = "eidservice.aux.data";
    
    public final static String c_eidServiceCertsCV_CVCAPrefix = "eidservice.cv.cvca.";
    public final static String c_eidServiceCertsCV_DVCAPrefix = "eidservice.cv.dvca.";
    public final static String c_eidServiceCertsCV_TERMPrefix = "eidservice.cv.terminal.";
    
    public final static String c_redirectorsTCTokenPrefix = "redirector.tctoken.";
    public final static String c_redirectorsRefreshPrefix = "redirector.refresh.";
    
    public final static String c_redirectorTCTokenHostNamesPrefix = "redirector.tctoken.hostname.";
    public final static String c_redirectorRefreshHostNamesPrefix = "redirector.refresh.hostname.";
    public final static String c_redirectorTCTokenPortsPrefix = "redirector.tctoken.port.";
    public final static String c_redirectorRefreshPortsPrefix = "redirector.refresh.port.";
    public final static String c_redirectorTCTokenUrlPathsPrefix = "redirector.tctoken.urlpath.";
    public final static String c_redirectorRefreshUrlPathsPrefix = "redirector.refresh.urlpath.";
    public final static String c_redirectorTCTokenCertificatesPrefix = "redirector.tctoken.certificate.";
    public final static String c_redirectorRefreshCertificatesPrefix = "redirector.refresh.certificate.";
    public final static String c_redirectorTCTokenPrivateKeysPrefix = "redirector.tctoken.privatekey.";
    public final static String c_redirectorRefreshPrivateKeysPrefix = "redirector.refresh.privatekey.";
    public final static String c_redirectorTCTokenStatusPrefix = "redirector.tctoken.status.";
    public final static String c_redirectorRefreshStatusPrefix = "redirector.refresh.status.";
    
    ArrayList<String> m_allVars = new ArrayList<String>();
    
    ITestData m_testData;
    
    public VariableParameter(ITestData data) throws IllegalArgumentException, IllegalAccessException {
        m_testData = data;
        
        Field[] declaredFields = VariableParameter.class.getDeclaredFields();
        for (Field field : declaredFields) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                m_allVars.add((String) field.get(null));
            }
        }
        
    }
    
    @Override
    public String getValue(String varname) throws Exception {
        if (c_eCardTestCaseNumber.equals(varname)) {
            return m_testData.getTestName();
        }
        
        if (c_eCardTestCaseDescription.equals(varname)) {
            return m_testData.getTestDescription();
        }
        
        if (c_clientURL.equals(varname)) {
            return m_testData.getClientURL();
        }
        
        if (c_eServiceHostname.equals(varname)) {
            return m_testData.getEServiceHost();
        }
        if (c_eServicePort.equals(varname)) {
            return "" + m_testData.getEServicePort();
        }
        if (c_eServiceTCTokenURLPath.equals(varname)) {
            return m_testData.getEServiceTCTokenURL();
        }
        if (c_eServiceRefreshURLPath.equals(varname)) {
            return m_testData.getEServiceRefreshPageURL();
        }
        if (c_eServiceCommunicationErrorURLPath.equals(varname)) {
            return m_testData.getEServiceCommunicationErrorPageURL();
        }
        if (c_eidServiceHostname.equals(varname)) {
            return m_testData.getEIDServiceHost();
        }
        if (c_eidServicePort.equals(varname)) {
            return "" + m_testData.getEIDServicePort();
        }
        if (c_eServiceRedirectURL.equals(varname)) {
            return m_testData.getEServiceRedirectURL();
        }
        
        if (c_tcTokenProviderHost.equals(varname)) {
            return m_testData.getTCTokenProviderHost();
        }
        if (c_tcTokenProviderPort.equals(varname)) {
            return "" + m_testData.getTCTokenProviderPort();
        }
        if (c_tcTokenProviderTCTokenURL.equals(varname)) {
            return m_testData.getTCTokenProviderTCTokenURL();
        }
        
        if (c_eidServiceCV_CVCA.equals(varname)) {
            return Base64Util.encodeHEX(m_testData.getEIDServiceCV_CVCA());
        }
        if (c_eidServiceCV_DVCA.equals(varname)) {
            return Base64Util.encodeHEX(m_testData.getEIDServiceCV_DVCA());
        }
        if (c_eidServiceCV_TERM.equals(varname)) {
            return Base64Util.encodeHEX(m_testData.getEIDServiceCV_TERM());
        }
        
        if (c_eidServiceCertDescription.equals(varname)) {
            return Base64Util.encodeHEX(m_testData.getEIDServiceCertificateDescription());
        }
        if (c_eidServiceChatRequired.equals(varname)) {
            return Base64Util.encodeHEX(m_testData.getEIDServiceChatRequired());
        }
        if (c_eidServiceChatOptional.equals(varname)) {
            return Base64Util.encodeHEX(m_testData.getEIDServiceChatOptional());
        }
        if (c_eidServiceAuxData.equals(varname)) {
            return Base64Util.encodeHEX(m_testData.getEIDServiceAuxData());
        }
        
        if (varname.startsWith(c_eidServiceCertsCV_CVCAPrefix)) {
            byte[] value = m_testData.getEIDServiceCV_CVCA(Integer.parseInt(CommonUtil.getSubstringAfter(varname, ".", true)));
            return (value != null) ? Base64Util.encodeHEX(value) : null;
        }
        if (varname.startsWith(c_eidServiceCertsCV_DVCAPrefix)) {
            byte[] value = m_testData.getEIDServiceCV_DVCA(Integer.parseInt(CommonUtil.getSubstringAfter(varname, ".", true)));
            return (value != null) ? Base64Util.encodeHEX(value) : null;
        }
        if (varname.startsWith(c_eidServiceCertsCV_TERMPrefix)) {
            byte[] value = m_testData.getEIDServiceCV_TERM(Integer.parseInt(CommonUtil.getSubstringAfter(varname, ".", true)));
            return (value != null) ? Base64Util.encodeHEX(value) : null;
        }
        
        // if ((varname.startsWith(c_redirectorsRefreshPrefix)) && !(varname.length() > c_redirectorsRefreshPrefix.length() + 2)) {
        // Integer noRedirector = Integer.parseInt(CommonUtil.getSubstringAfter(varname, ".", true));
        // if (noRedirector != null) {
        // if (m_testData.getRedirectorsInfoRefreshAddress() != null) {
        // String[] redirectorInfo = m_testData.getRedirectorsInfoRefreshAddress().get(noRedirector);
        // if (redirectorInfo != null) {
        // return CommonUtil.getSubstringBefore(redirectorInfo[0], "/", false);
        // }
        // }
        // }
        // throw new Exception("Variable not defined correctly \"" + varname + "\"");
        // }
        
        if (varname.startsWith(c_redirectorTCTokenHostNamesPrefix)) {
            return m_testData.getRedirectorTCTokenHost(Integer.parseInt(CommonUtil.getSubstringAfter(varname, ".", true)));
        }
        if (varname.startsWith(c_redirectorRefreshHostNamesPrefix)) {
            return m_testData.getRedirectorRefreshHost(Integer.parseInt(CommonUtil.getSubstringAfter(varname, ".", true)));
        }
        if (varname.startsWith(c_redirectorTCTokenPortsPrefix)) {
            return m_testData.getRedirectorTCTokenPort(Integer.parseInt(CommonUtil.getSubstringAfter(varname, ".", true)));
        }
        if (varname.startsWith(c_redirectorRefreshPortsPrefix)) {
            return m_testData.getRedirectorRefreshPort(Integer.parseInt(CommonUtil.getSubstringAfter(varname, ".", true)));
        }
        if (varname.startsWith(c_redirectorTCTokenUrlPathsPrefix)) {
            return m_testData.getRedirectorTCTokenURL(Integer.parseInt(CommonUtil.getSubstringAfter(varname, ".", true)));
        }
        if (varname.startsWith(c_redirectorRefreshUrlPathsPrefix)) {
            return m_testData.getRedirectorRefreshURL(Integer.parseInt(CommonUtil.getSubstringAfter(varname, ".", true)));
        }
        if (varname.startsWith(c_redirectorTCTokenCertificatesPrefix)) {
            return m_testData.getRedirectorTCTokenCertificate(Integer.parseInt(CommonUtil.getSubstringAfter(varname, ".", true)));
        }
        if (varname.startsWith(c_redirectorRefreshCertificatesPrefix)) {
            return m_testData.getRedirectorRefreshCertificate(Integer.parseInt(CommonUtil.getSubstringAfter(varname, ".", true)));
        }
        if (varname.startsWith(c_redirectorTCTokenPrivateKeysPrefix)) {
            return m_testData.getRedirectorTCTokenPrivateKey(Integer.parseInt(CommonUtil.getSubstringAfter(varname, ".", true)));
        }
        if (varname.startsWith(c_redirectorRefreshPrivateKeysPrefix)) {
            return m_testData.getRedirectorRefreshPrivateKey(Integer.parseInt(CommonUtil.getSubstringAfter(varname, ".", true)));
        }
        if (varname.startsWith(c_redirectorTCTokenStatusPrefix)) {
            return m_testData.getRedirectorTCTokenStatus(Integer.parseInt(CommonUtil.getSubstringAfter(varname, ".", true)));
        }
        if (varname.startsWith(c_redirectorRefreshStatusPrefix)) {
            return m_testData.getRedirectorRefreshStatus(Integer.parseInt(CommonUtil.getSubstringAfter(varname, ".", true)));
        }
        
        if (c_commErrorAddressServerCommunicationErrorPageURL.equals(varname)) {
            return m_testData.getCommErrorAddressServerCommunicationErrorPageURL();
        }
        if (c_commErrorAddressServerHost.equals(varname)) {
            return m_testData.getCommErrorAddressServerHost();
        }
        if (c_commErrorAddressServerPort.equals(varname)) {
            return "" + m_testData.getCommErrorAddressServerPort();
        }
        
        
        throw new Exception("Variable not defined \"" + varname + "\"");
    }
    
    @Override
    public boolean checkVarName(String substring) {
        if (m_allVars.contains(substring)) {
            return true;
        } else if (substring.startsWith(c_redirectorsTCTokenPrefix) || substring.startsWith(c_redirectorsRefreshPrefix)
                || substring.startsWith(c_redirectorTCTokenHostNamesPrefix) || substring.startsWith(c_redirectorRefreshHostNamesPrefix)
                || substring.startsWith(c_redirectorTCTokenPortsPrefix) || substring.startsWith(c_redirectorRefreshPortsPrefix)
                || substring.startsWith(c_redirectorTCTokenUrlPathsPrefix) || substring.startsWith(c_redirectorRefreshUrlPathsPrefix)
                || substring.startsWith(c_redirectorTCTokenCertificatesPrefix) || substring.startsWith(c_redirectorRefreshCertificatesPrefix)
                || substring.startsWith(c_redirectorTCTokenPrivateKeysPrefix) || substring.startsWith(c_redirectorRefreshPrivateKeysPrefix)
                || substring.startsWith(c_redirectorTCTokenStatusPrefix) || substring.startsWith(c_redirectorRefreshStatusPrefix)
                || substring.startsWith(c_eidServiceCertsCV_CVCAPrefix) || substring.startsWith(c_eidServiceCertsCV_DVCAPrefix)
                || substring.startsWith(c_eidServiceCertsCV_TERMPrefix)) {
            return true;
        }
        return false;
    }
    
}
