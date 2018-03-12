package com.secunet.ipsmall.test;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.secunet.ipsmall.log.IModuleLogger.LogLevel;
import com.secunet.ipsmall.log.Logger;

public class TestProject {
    
    public final static String PROPERTY_PROJECT_CHANGED = "PROJECT_CHANGED";
    public final static String PROPERTY_PROJECT_LOADED = "PROJECT_LOADED";
    /** oldvalue = null, newvalue is always the testcase id */
    public final static String PROPERTY_TC_STATECHANGE = "TC_STATECHANGE";
    public final static String PROPERTY_QUEUE_CHANGED = "QUEUE_CHANGED";
    public final static String PROPERTY_STARTED_TC = "STARTED_TC";
    public final static String PROPERTY_STOPPED_TC = "STOPPED_TC";
    
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    private final Map<String, ITestData> testcases = new HashMap<>();
    private final Map<String, Metainformation> metainformation = new HashMap<>();
    private List<ITestData> queue = new ArrayList<>();
    private boolean onlyAutonomic = false;
    private String currentlyRunning = null;
    
    private boolean unsaved = false;
    
    private TestProject() {
        // use static factory method
    }
    
    /**
     * <b>Notice: Does not trigger property change!</b>
     * 
     * @param td
     *            must not be null
     */
    public void addTestcase(final ITestData td) {
        testcases.put(td.getTestName(), td);
        metainformation.put(td.getTestName(), new Metainformation(td));
    }
    
    public void setOnlyAutonomic(final boolean flag) {
        onlyAutonomic = flag;
    }
    
    public void updateState(final String testId, TestState newState) {
        if (testId == null || newState == null || !metainformation.containsKey(testId)) {
            Logger.Global.logState("Unkown testcase: " + testId + ", or state or id is null", LogLevel.Debug);
            return;
        }
        
        TestState old = metainformation.get(testId).getState();
        if (old != newState) {
            if (testcases.get(testId).getTestManualResult())
                if (old == TestState.Running && newState != TestState.Running)
                    newState = TestState.Idle;
            
            metainformation.get(testId).setState(newState);
            if (TestState.Running == newState && !testId.equals(currentlyRunning))
                currentlyRunning = testId;
            
            if (currentlyRunning != null && currentlyRunning.equals(testId) && TestState.Running != newState)
                currentlyRunning = null;
            
            setUnsaved(true);
            pcs.firePropertyChange(PROPERTY_TC_STATECHANGE, old, testId);
        }
    }
    
    public TestState getState(final String testId) {
        if (testId == null || !metainformation.containsKey(testId))
            throw new IllegalArgumentException("project does not contain testcase id: " + testId);
        
        return metainformation.get(testId).getState();
    }
    
    /** mark project as changed since last save */
    public boolean isUnsaved() {
        return unsaved;
    }
    
    /** mark project as changed since last save */
    public void setUnsaved(final boolean newState) {
        if (unsaved != newState)
            pcs.firePropertyChange(PROPERTY_PROJECT_CHANGED, unsaved, newState);
        
        unsaved = newState;
    }
    
    public synchronized boolean removeFromQueue(final ITestData data) {
        boolean success = queue.remove(data);
        pcs.firePropertyChange(PROPERTY_QUEUE_CHANGED, data, null);
        return success;
    }
    
    public synchronized void clearQueue() {
        queue.clear();
        pcs.firePropertyChange(PROPERTY_QUEUE_CHANGED, null, queue);
    }
    
    public boolean hasAutostartInQueue() {
        return getTopFromQueue(onlyAutonomic) != null;
    }
    
    /**
     * @return can be null; does not remove from queue
     */
    public ITestData getTopFromQueue() {
        return getTopFromQueue(onlyAutonomic);
    }
    
    public ITestData getAutonomicTopFromQueue() {
        return getTopFromQueue(true);
    }
    
    public ITestData getTestcase(final String id) {
        return testcases.get(id);
    }
    
    public List<ITestData> getTestcases() {
        return new ArrayList<>(testcases.values());
    }
    
    /**
     * @param onlyAutonomic
     * @return can be null, if no autonomic can be found; does not remove from queue
     */
    protected ITestData getTopFromQueue(final boolean onlyAutonomic) {
        for (ITestData curData : queue) {
            if (!onlyAutonomic) {
                return curData;
            }
        }
        return null;
    }
    
    /**
     * @return unmodifiable
     */
    public synchronized List<ITestData> getQueue() {
        return Collections.unmodifiableList(queue);
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }
    
    public synchronized void resetTestcases() {
        if (isTestcaseRunning())
            throw new IllegalStateException("Can not change queue while running testcase");
        
        for (Metainformation meta : metainformation.values()) {
            meta.setState(TestState.Idle);
        }
        
        pcs.firePropertyChange(PROPERTY_PROJECT_CHANGED, null, this);
    }
    
    public synchronized String getRunningTestcaseId() {
        return currentlyRunning;
    }
    
    /**
     * Checks if a testcase is running.
     * 
     * @return true if running
     */
    public synchronized boolean isTestcaseRunning() {
        return currentlyRunning != null;
    }
    
    /**
     * throws IllegalStateException if called while testcase is running
     * 
     * @param queueItems
     *            must not be null
     */
    public synchronized void setQueue(final List<ITestData> queueItems) {
        if (isTestcaseRunning())
            throw new IllegalStateException("Can not change queue while running testcase");
        
        List<ITestData> oldQueue = queue;
        queue = new ArrayList<>(queueItems);
        pcs.firePropertyChange(PROPERTY_QUEUE_CHANGED, oldQueue, queue);
    }
    
    /**
     * @param destination
     *            file to write to
     * @throws IOException
     *             on error
     */
    public void saveTestProject(final File destination) throws IOException {
        if (!destination.exists()) {
            destination.getParentFile().mkdirs();
            destination.createNewFile();
        }
        
        try (FileWriter fWriter = new FileWriter(destination); BufferedWriter writer = new BufferedWriter(fWriter)) {
            for (ITestData tc : testcases.values()) {
                if (metainformation.containsKey(tc.getTestName())) {
                    String output = tc.getTestName() + ";" + metainformation.get(tc.getTestName()).getState().toString();
                    writer.write(output);
                    writer.newLine();
                }
            }
        }
        setUnsaved(false);
    }
    
    /**
     * Factory for creating Testproject
     * 
     * @param file
     * @param testdatas
     * @return the loaded TestProject
     */
    public static synchronized TestProject loadTestProject(final File file, final Collection<ITestData> testdatas) {
        TestProject project = new TestProject();
        
        for (ITestData testData : testdatas) {
            project.addTestcase(testData);
        }
        
        if (file != null && file.canRead()) {
            Logger.Global.logState("Loading project from file: " + file.getAbsolutePath());
            try (FileReader fReader = new FileReader(file); BufferedReader reader = new BufferedReader(fReader)) {
                String line = null;
                String[] split = null;
                while ((line = reader.readLine()) != null) {
                    split = line.split(";"); // use split to avoid missing empty
                                             // blocks
                    String key = null;
                    TestState state = TestState.Idle;
                    for (int i = 0; i < split.length; i++) {
                        if (split[i] == null || split[i].equals(""))
                            continue; // skip empty statements
                            
                        switch (i) {
                            case 0:
                                key = split[i];
                                break;
                            case 1:
                                try {
                                    state = TestState.valueOf(split[i]);
                                } catch (Exception ex) {
                                    state = TestState.Idle;
                                }
                                break;
                            
                            // add more if needed
                            
                            default:
                                Logger.Global.logState("Unkown testcase project entry: " + i + ": " + split[i], LogLevel.Warn);
                        }
                    }
                    if (key != null && state != null) {
                        project.updateState(key, state);
                    }
                }
                project.setUnsaved(false);
            } catch (IOException exc) {
                Logger.Global.logState("Error loading TestProject: IOException:" + exc.getMessage(), LogLevel.Error);
            }
        } else {
            Logger.Global.logState("No project file exists!", LogLevel.Warn);
            // project does not come from file and was not saved
            project.setUnsaved(true);
        }
        
        return project;
    }
    
    /**
     * Caseclass which contains meta information
     */
    public class Metainformation {
        private TestState state = TestState.Idle;
        
        public Metainformation(ITestData testdata) {
        }
        
        public void setState(TestState state) {
            this.state = state;
        }
        
        public TestState getState() {
            return state;
        }
    }
    
}
