package com.hjp.globaltester.prove.eidclient;

/**
 * This interface defines the methods used to control a simulator. Its
 * implementations abstract from the specific used simulator and provide a
 * general means to start, stop and configure the used simulator. Using this
 * interface a client can create the simulation environment it needs.
 * Specifically it can be used to provide the necessary controls to set up
 * simulated cards for testing environments. The transport layer that is used to
 * call implementations of this interface is not explicitly defined but it is
 * intended to be used in networked setting (e.g. by using a setup with proxy
 * objects).
 * <p>
 * To ensure reproducible results and consistent simulator states, error
 * handling using this interface is reduced to treating all errors as
 * unrecoverable. To accomplish this, all commands that are able to change the
 * state, reset the internal state in case of an error. The only exception to
 * this is to keep an error description for the client that can be delivered at
 * any point in time after such an error happened. The behavior for all state
 * changing methods but {@link #getError()} is to return <code>false</code> if
 * they are called after an error occurred.
 * 
 * @author mboonk
 *
 */
public interface SimulatorControl {
	/**
	 * Start the simulator using the recently loaded configuration.
	 * <p/>
	 * 
	 * A Configuration has to be loaded through
	 * {@link #loadConfiguration(String)} to allow a successful start. It might
	 * be modified before the simulator is started for the first time and can
	 * not be modified using {@link #updateState(String, String[])} afterwards.
	 * 
	 * @return <code>true</code>, if the simulator started successfully
	 */
	public boolean startSimulator();

	/**
	 * This stops the simulator.
	 * 
	 * @return <code>true</code>, if the simulator stopped successfully.
	 */
	public boolean stopSimulator();

	/**
	 * This methods loads a specific configuration as a preparing step before
	 * starting a simulator. The configuration contains all necessary
	 * information to launch the simulator including data contents and behavior
	 * definitions.
	 * <p>
	 * This also resets the internal state of the {@link SimulatorControl}
	 * implementation to the default state and represents the initial command
	 * used to prepare a simulator. Particularly the error message is reset.
	 * 
	 * @param configurationIdentifier
	 *            The identifier of the configuration to load. It should
	 *            uniquely identify the configuration to load.
	 * @return <code>true</code>, if loading of the configuration was
	 *         successful.
	 */
	public boolean loadConfiguration(String configurationIdentifier);

	/**
	 * This method is used to update the state of a configuration after loading
	 * it. Changes can only be done between loading a configuration and starting
	 * the simulator. The exact supported update operation types and parameters
	 * are up to the implementations of this interface. Depending on the
	 * underlying simulator type there should be generic operation definitions
	 * usable for all simulator implementations of a given type.
	 * 
	 * 
	 * @param updateType
	 *            An identifier for the operation to execute.
	 * @param updateParameters
	 *            An {@link String} encoded set of parameters. Exact encodings
	 *            are up to the operation definition and must be parsed by the
	 *            implementation.
	 * @return <code>true</code>, when the state update has been completed
	 *         successfully.
	 */
	public boolean updateState(String updateType, String[] updateParameters);

	/**
	 * This retrieves information from the configuration. Implementations must
	 * not change the state of the simulator or the configuration in any way.
	 * 
	 * @param stateType
	 *            An identifier for the data retrieval operation to execute.
	 *
	 * @param stateParameters
	 *            An {@link String} encoded set of parameters defining what
	 *            specific set of data should be retrieved. Exact encodings are
	 *            up to the operation definition and must be parsed by the
	 *            implementation.
	 * @return An {@link String} set of results fitting the given parameters.
	 *         The result is potentially empty.
	 */
	public String[] getState(String stateType, String[] stateParameters);

	/**
	 * Other methods available in this interface return boolean values as
	 * success indicators. If a method returns false, this method can be used to
	 * retrieve a human readable error description. The delivered description is
	 * of the first error.
	 * 
	 * @return A {@link String} containing the description of the previously
	 *         occurred error or an empty {@link String} if no error has
	 *         happened after the last {@link #loadConfiguration(String)} call.
	 */
	public String getError();
}
