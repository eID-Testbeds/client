package com.secunet.ipsmall.cardsimulation;

import com.hjp.globaltester.prove.eidclient.SimulatorControl;
import com.secunet.ipsmall.log.IModuleLogger;
import com.secunet.ipsmall.log.Logger;
import com.secunet.ipsmall.test.ITestData;
import com.secunet.testbedutils.cvc.cvcertificate.DataBuffer;
import com.secunet.testbedutils.gtsimcontrol.SimulatorControlInstance;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Controls card simulation remotely.
 */
public class CardSimulationRemoteControl {

    private static final String PROPERTIES_SIM_PREFIX = "application.cardsimulation.";
    private static final String PROPERTIES_SIM_HOST = PROPERTIES_SIM_PREFIX + "host";
    private static final String PROPERTIES_SIM_PORT = PROPERTIES_SIM_PREFIX + "port";

    private static final String UPDATE_TYPE_TRUSTPOINT = "updateTrustPoint";
    private static final String UPDATE_TYPE_CARDDATE = "updateChipTime";

    private static final String DATE_PATTERN = "yyyy-MM-dd";

    private static final String STATE_TYPE_GET_FILE = "getFileContent";

    private static final String STATE_PARAM_EFCARDACCESS = "EF.CardAccess";
    private static final String STATE_PARAM_EFCARDSECURITY = "EF.CardSecurity";

    private SimulatorControl simControl = null;
    private final DateFormat dateFormat;

    private final String host;
    private final int port;

    /**
     * Creates new remote control for card simulation.
     *
     * @param applicationProperties Properties containing connection information
     * for remote control.
     */
    public CardSimulationRemoteControl(Properties applicationProperties) {

        host = applicationProperties.getProperty(PROPERTIES_SIM_HOST, "");
        port = Integer.parseInt(applicationProperties.getProperty(PROPERTIES_SIM_PORT, "0"));
        this.dateFormat = new SimpleDateFormat(DATE_PATTERN);

        init();
    }

    /**
     * Initializes card simulation remote control.
     */
    private void init() {
        if (host != null && !host.isEmpty()) {
            try {
                simControl = new SimulatorControlInstance(host, port);
                Logger.Global.logState("CardSimulationRemoteControl connected to " + host + ":" + port, IModuleLogger.LogLevel.Info);
            } catch (Exception ex) {
                simControl = null;
                Logger.Global.logState("Unable to connected to remote card simulation: " + ex.getMessage(), IModuleLogger.LogLevel.Error);
            }

        } else {
            simControl = null;
            Logger.Global.logState("Not connected to remote card simulation.", IModuleLogger.LogLevel.Warn);
        }
    }

    /**
     * Starts card simulation for current test case.
     *
     * @param testdata Current test case configuration.
     * @return <i>True</i> if test case was started successfully, otherwise
     * <i>false</i>.
     */
    public boolean start(ITestData testdata) {
        boolean result = false;

        if (simControl == null) {
            init();
        }

        if (simControl != null) {
            try {
                // stop simulation
                simControl.stopSimulator();

                // personalize simulation ...
                if (testdata != null) {
                    if (testdata.isCardSimulationResetAtStart()) { // ... only if personalization is required
                        // repersonalize configuration
                        String cardSimID = testdata.getCardSimulationConfigurationIdentifier();
                        if (simControl.loadConfiguration(cardSimID)) {
                            Logger.CardSim.logState("Load configuration: " + cardSimID);

                            // add trustpoints
                            List<String> trustpoints = new ArrayList<>();

                            byte[] rawTrustpoint1 = testdata.getSimulatedCard_Trustpoint1();
                            if (rawTrustpoint1 != null) {
                                DataBuffer trustpoint1 = new DataBuffer(rawTrustpoint1);
                                trustpoints.add(trustpoint1.asHex(""));
                            }

                            byte[] rawTrustpoint2 = testdata.getSimulatedCard_Trustpoint2();
                            if (rawTrustpoint2 != null) {
                                DataBuffer trustpoint2 = new DataBuffer(rawTrustpoint2);
                                trustpoints.add(trustpoint2.asHex(""));
                            }

                            if (!simControl.updateState(UPDATE_TYPE_TRUSTPOINT, trustpoints.toArray(new String[trustpoints.size()]))) {
                                throw new Exception("Error while updating trustpoints: " + simControl.getError());
                            }

                            // add card date
                            String date = dateFormat.format(testdata.getSimulatedCardDate());
                            if (!simControl.updateState(UPDATE_TYPE_CARDDATE, new String[]{date})) {
                                throw new Exception("Error while updating card date: " + simControl.getError());
                            }
                        } else {
                            throw new Exception("Error while loading configuration: " + simControl.getError());
                        }
                    }
                }

                // start simulation
                Logger.CardSim.logState("Starting remote card simulation ...", IModuleLogger.LogLevel.Debug);
                if (!simControl.startSimulator()) {
                    throw new Exception("Error while starting remote card simulation: " + simControl.getError());
                }

                result = true;
            } catch (Exception ex) {
                Logger.CardSim.logException(ex);
                Logger.CardSim.logState("Unable to start card simulation.", IModuleLogger.LogLevel.Error);
            }
        }

        return result;
    }

    /**
     * Stops card simulation.
     */
    public void stop() {
        if (simControl != null) {
            // stop simulation
            try {
                if (!simControl.stopSimulator()) {
                    throw new Exception("Error while stopping remote card simulation: " + simControl.getError());
                }
            } catch (Exception ex) {
                Logger.CardSim.logException(ex);
                Logger.CardSim.logState("Unable to stop card simulation.", IModuleLogger.LogLevel.Error);
            }
        }
    }

    /**
     * Gets EF.CardAccess of current simulation.
     *
     * @return EF.CardAccess.
     */
    public byte[] getCurrentEFCardAccess() {
        return getCurrentEF(STATE_PARAM_EFCARDACCESS);
    }

    /**
     * Gets EF.CardSecurity of current simulation.
     *
     * @return EF.CardSecurity.
     */
    public byte[] getCurrentEFCardSecurity() {
        return getCurrentEF(STATE_PARAM_EFCARDSECURITY);
    }

    /**
     * Gets EF of current simulation.
     *
     * @param fID File identifier of EF.
     * @return EF (<i>null</i> if not able to read.
     */
    private byte[] getCurrentEF(String fID) {
        byte[] result = null;
        if (simControl != null) {
            try {
                String[] results = simControl.getState(STATE_TYPE_GET_FILE, new String[]{fID});
                if (results != null && results.length > 0) {
                    DataBuffer file = new DataBuffer();
                    file.fromHex(results[0]);
                    result = file.toByteArray();
                } else {
                    throw new Exception("No result returned for getState: " + simControl.getError());
                }
            } catch (Exception ex) {
                Logger.CardSim.logException(ex);
                Logger.CardSim.logState("Unable to load " + fID + " card simulation.", IModuleLogger.LogLevel.Error);
            }
        } else {
            Logger.CardSim.logState("Not connected to remote card simulation.", IModuleLogger.LogLevel.Warn);
        }

        return result;
    }
}
