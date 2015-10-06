package com.secunet.ipsmall.report;

/**
 * Represents a Testcase.
 */
public class TestCase {    
    private final String name;
    private final String module;
    private boolean activated;
    
    /**
     * Creates a TestCase.
     * @param name Name of Testcase.
     * @param module Name of Module.
     */
    public TestCase(final String name, final String module, boolean activated) {
        this.name = name;
        this.module = module;
        this.activated = activated;
    }

    /**
     * Gets name of the testcase.
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets name of module of the testcase.
     * @return The name of module.
     */
    public String getModule() {
        return module;
    }
    
    /**
     * Gets if testcase is activated.
     * @return True, if testcase is activated.
     */
    public boolean isActivated() {
        return activated;
    }
      
    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof TestCase) {
            TestCase other = (TestCase)obj;
            result = name.equals(other.getName()) && module.equals(other.getModule());
        }

        return result;
    }
}
