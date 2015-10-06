package com.secunet.testbedutils.gtsimcontrol;

import com.hjp.globaltester.control.soap.SimulatorControlSoapProxy;
import com.hjp.globaltester.control.soap.SimulatorControlSoapProxyService;
import com.hjp.globaltester.control.soap.SoapServiceProvider;
import com.hjp.globaltester.control.soap.SoapServiceProviderService;
import com.hjp.globaltester.prove.eidclient.SimulatorControl;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.ws.BindingProvider;
import net.java.dev.jaxb.array.StringArray;

public class SimulatorControlInstance implements SimulatorControl {
    
    private static final String CONNECT_TIMEOUT = "com.sun.xml.internal.ws.connect.timeout";
    private static final String REQUEST_TIMEOUT = "com.sun.xml.internal.ws.request.timeout";
    
    private static final String WS_PATH_PREFIX = "/globaltester";
    private static final String WS_PATH_PROVIDER_WS = WS_PATH_PREFIX + "/control";
    
    SimulatorControlSoapProxy simulator = null;
    
    public SimulatorControlInstance(String host, int port) throws MalformedURLException {
        String domain = "http://" + host + ":" + port;
        
        // get list of avaliable providers
        SoapServiceProviderService providerService = new SoapServiceProviderService(new URL(domain + WS_PATH_PROVIDER_WS + "?wsdl"));
        SoapServiceProvider providerWS = providerService.getSoapServiceProviderPort();
        
        ((BindingProvider)providerWS).getRequestContext().put(CONNECT_TIMEOUT, 1000);
        ((BindingProvider)providerWS).getRequestContext().put(REQUEST_TIMEOUT, 2000);
        
        StringArray providers = providerWS.getAvailableHandlers();
        
        // select first one
        if (providers != null && providers.getItem() != null && providers.getItem().size() > 0) {
            String providerName = providers.getItem().get(0);
            String providerWSPath = WS_PATH_PREFIX + "/" + providerName;
            SimulatorControlSoapProxyService simService = new SimulatorControlSoapProxyService(new URL(domain + providerWSPath + "?wsdl"));
            simulator = simService.getSimulatorControlSoapProxyPort();
            
            ((BindingProvider)simulator).getRequestContext().put(CONNECT_TIMEOUT, 1000);
            ((BindingProvider)simulator).getRequestContext().put(REQUEST_TIMEOUT, 4000);
        }
    }
    
    @Override
    public boolean startSimulator() {
        boolean result = false;
        if (simulator != null) {
            result = simulator.startSimulator();
        }
        return result;
    }
    
    @Override
    public boolean stopSimulator() {
        boolean result = false;
        if (simulator != null) {
            result = simulator.stopSimulator();
        }
        return result;
    }
    
    @Override
    public boolean loadConfiguration(String configurationIdentifier) {
        boolean result = false;
        if (simulator != null) {
            result = simulator.loadConfiguration(configurationIdentifier);
        }
        return result;
    }
    
    @Override
    public boolean updateState(String updateType, String[] updateParameters) {
        boolean result = false;
        if (simulator != null) {
            result = simulator.updateState(updateType, encodeStringArray(updateParameters));
        }
        return result;
    }
    
    @Override
    public String[] getState(String stateType, String[] stateParameters) {
        List<String> result = new ArrayList<>();
        if (simulator != null) {
            result = simulator.getState(stateType, encodeStringArray(stateParameters)).getItem();
        }
        return result.toArray(new String[result.size()]);
    }
    
    @Override
    public String getError() {
        String result = null;
        if (simulator != null) {
            result = simulator.getError();
        }
        return result;
    }
    
    /**
     * Encodes array of {@link String} to {@link StringArray}.
     * @param input - Array of {@link String}.
     * @return {@link StringArray}.
     */
    private static StringArray encodeStringArray(String[] input) {
        StringArray result = new StringArray();
        for (String element : input) {
            result.getItem().add(element);
        }
        return result;
    }
}
