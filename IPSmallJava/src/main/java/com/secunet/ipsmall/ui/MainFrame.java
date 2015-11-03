package com.secunet.ipsmall.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.secunet.ipsmall.GlobalInfo;
import com.secunet.ipsmall.GlobalSettings;
import com.secunet.ipsmall.IPSmallManager;
import com.secunet.ipsmall.log.IModuleLogger.ConformityMode;
import com.secunet.ipsmall.log.Logger;
import com.secunet.ipsmall.log.IModuleLogger.ConformityResult;
import com.secunet.ipsmall.log.IModuleLogger.LogLevel;
import com.secunet.ipsmall.report.ReportGenerator;
import com.secunet.ipsmall.test.FileBasedTestData;
import com.secunet.ipsmall.test.ITestData;
import com.secunet.ipsmall.test.TestProject;
import com.secunet.ipsmall.test.TestState;
import com.secunet.testbedutils.utilities.CommonUtil;
import com.secunet.ipsmall.util.FileUtils;

/**
 * Main UI which reacts the property changes of the currently active project.
 *
 * @author olischlaeger.dennis
 */
public class MainFrame extends JFrame implements PropertyChangeListener {

    private static final long serialVersionUID = -5476778853560038629L;

    private static final String TRIGGER_LABEL_START = "Start";
    private static final String TRIGGER_LABEL_CANCEL = "Cancel";
    private static final String STATE_LABEL_IDLE = "idle";
    private static final Color STATE_COLOR_IDLE = Color.LIGHT_GRAY;
    private static final Color STATE_COLOR_RUNNING = Color.YELLOW.darker();
    private static final Color STATE_COLOR_PASSED = Color.GREEN.darker();
    private static final Color STATE_COLOR_FAILED = Color.RED.darker();
    private static final Color STATE_COLOR_UNDETERMINED = Color.YELLOW;

    private JPanel pnlContentPane;
    private JLabel lblStateIcon;
    private JLabel lblStatusbar;
    private JLabel lblTestcaseName;
    private JLabel lblTaskname;
    private JButton btnStart;
    private JButton btnFail;
    private JButton btnPass;
    private JFXPanel jfxUILog;
    private WebEngine webEngine;
    private JTextArea taDescr;
    private JTextArea taProtocol;
    private JProgressBar proBarTestcase;
    private JTree treeTestcases;
    private ArrayList<String> treeTestcasesExpansionState;
    private TreePath[] treeSelection;
    private JTabbedPane tabbedPane;

    //private TreePath savedPath;
    private boolean autoClear = true;
    private boolean enforceAsk = false;

    private String lastSelectedTestcase = null;

    /**
     * Create the frame.
     */
    public MainFrame() {
        setName("mainframe");
        setTitle(generateTitle());
        try {
            setIconImage(UIUtils.createImageIcon("/com/secunet/ipsmall/ui/icons/title.png").getImage());
            // setIconImage(UIUtils.scaleImageIconTo(UIUtils.createImageIcon("/com/secunet/ipsmall/ui/icons/title.png"),
            // 16, 16).getImage());
        } catch (Exception ignore) {
        }
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int H = 700;
        int W = 1100;
        setBounds((screenSize.width / 2 - W / 2), (screenSize.height / 2 - H / 2), W, H);

        initialize();

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                shutdown();
            }
        });

        UIUtils.centerFrame(this);
    }

    private void initialize() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu mnFile = new JMenu("File");
        menuBar.add(mnFile);

        JMenu mnTCFoldersMen = new JMenu("Testcase folders");
        mnFile.add(mnTCFoldersMen);

        JMenuItem mntmReloadTestcases = new JMenuItem("Reload testcases");
        mntmReloadTestcases.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
        mntmReloadTestcases.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    IPSmallManager.getInstance().saveProject();
                } catch (IOException ex) {
                    displayErrorMessage(ex);
                    return;
                }

                loadTestcases(IPSmallManager.testobjectDirectory);
            }
        });
        mnTCFoldersMen.add(mntmReloadTestcases);

        JSeparator separator_4 = new JSeparator();
        mnTCFoldersMen.add(separator_4);

        JMenuItem mntTestObjectFolderItem = new JMenuItem("Set testobject folder...");
        mnTCFoldersMen.add(mntTestObjectFolderItem);
        mntTestObjectFolderItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    IPSmallManager.getInstance().saveProject();
                } catch (IOException ex) {
                    displayErrorMessage(ex);
                    return;
                }

                actionSetTestDirectoryDialog();
                loadTestcases(IPSmallManager.testobjectDirectory);
            }
        });

        JCheckBoxMenuItem chckbxmntmEnforceAskToStop = new JCheckBoxMenuItem("Ask if really want to stop");
        chckbxmntmEnforceAskToStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                enforceAsk = !enforceAsk;
            }
        });
        chckbxmntmEnforceAskToStop.setSelected(enforceAsk);

        JSeparator separator_3 = new JSeparator();
        mnFile.add(separator_3);
        mnFile.add(chckbxmntmEnforceAskToStop);

        JSeparator separator = new JSeparator();
        mnFile.add(separator);

        JMenuItem mntmExit = new JMenuItem("Exit");
        mntmExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                shutdown();
            }
        });
        mntmExit.setMnemonic('E');
        mntmExit.setToolTipText("Exit this application");
        mnFile.add(mntmExit);

        JMenu mnLogMenu = new JMenu("Log");
        menuBar.add(mnLogMenu);

        JMenu mnLogProfilesMenu = new JMenu("Profiles");
        mnLogMenu.add(mnLogProfilesMenu);

        ButtonGroup profileGroup = new ButtonGroup();

        File profileDir = new File(GlobalSettings.getLogProfilesDir());
        if (profileDir.exists() && profileDir.isDirectory()) {
            for (File profileFile : profileDir.listFiles()) {
                String profileFileName = profileFile.getName();
                if (profileFile.isFile() && profileFileName.endsWith(".properties")) {
                    JRadioButtonMenuItem rbmnLogProfile = new JRadioButtonMenuItem(IPSmallManager.getInstance().getLoggingProfileName(profileFileName));
                    mnLogProfilesMenu.add(rbmnLogProfile);
                    profileGroup.add(rbmnLogProfile);

                    rbmnLogProfile.setSelected(profileFileName.equals(IPSmallManager.getInstance().getLoggingProfileFileName()));
                    rbmnLogProfile.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            actionUpdateProfile(e);
                        }
                    });

                }
            }
        }

        JSeparator separator_5 = new JSeparator();
        mnLogMenu.add(separator_5);

        JMenuItem mntmViewDebugLogfile = new JMenuItem("View Debug Logfile");
        mntmViewDebugLogfile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
        mntmViewDebugLogfile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewDebugLogfile();
            }
        });
        mntmViewDebugLogfile.setMnemonic('D');
        mnLogMenu.add(mntmViewDebugLogfile);

        JMenuItem mntmViewTCLogItem = new JMenuItem("View Testcase Logfile");
        mntmViewTCLogItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
        mntmViewTCLogItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (Logger.getTestCasefile() != null) {
                    viewLogFile(Logger.getTestCasefile());
                } else if (getSelectedTestcase() != null && getLatestLogFile(getSelectedTestcase()) != null) {
                    viewLogFile(getLatestLogFile(getSelectedTestcase()).getAbsolutePath());
                } else {
                    publishToStatusbar("No logfile path set or wrong selection");
                }
            }
        });
        mntmViewTCLogItem.setMnemonic('T');
        mnLogMenu.add(mntmViewTCLogItem);

        JMenuItem mILogFolder = new JMenuItem("Show Log Folder...");
        mILogFolder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    File logDir = new File(IPSmallManager.getInstance().testobjectDirectory, GlobalSettings.getTOLogDir());
                    if (logDir.exists()) {
                        Desktop.getDesktop().open(logDir);
                    }
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            }
        });
        mnLogMenu.add(mILogFolder);

        JSeparator separator_1 = new JSeparator();
        mnLogMenu.add(separator_1);

        JMenuItem mntmClearUILogItem = new JMenuItem("Clear UI Log");
        mntmClearUILogItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
        mntmClearUILogItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionClearUILog();
            }
        });

        mntmClearUILogItem.setMnemonic('C');
        mnLogMenu.add(mntmClearUILogItem);

        JCheckBoxMenuItem chckbxmntmAutoClearUILog = new JCheckBoxMenuItem("Auto clear UI on testcase start");
        chckbxmntmAutoClearUILog.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                autoClear = !autoClear;
            }
        });
        chckbxmntmAutoClearUILog.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
        chckbxmntmAutoClearUILog.setSelected(true);
        mnLogMenu.add(chckbxmntmAutoClearUILog);

        JMenu mnProject = new JMenu("Project");
        mnProject.setMnemonic('P');
        menuBar.add(mnProject);

        JMenuItem mntmGenerateReport = new JMenuItem("Generate Report");
        mntmGenerateReport.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File report = null;
                Logger.Global.logState("Start generating report ...", LogLevel.Debug);
                try {
                    ReportGenerator reportGen = new ReportGenerator(IPSmallManager.testobjectDirectory);
                    report = reportGen.generateReport(IPSmallManager.testobjectDirectory.getName()
                            + " Report "
                            + new SimpleDateFormat("YYYY-MM-dd HH-mm-ss").format(new Date(System.currentTimeMillis()))
                            + ".xml");
                } catch (FileNotFoundException exc) {
                    Logger.Global.logState("Unable to load report generator: " + exc.getMessage(), LogLevel.Error);
                }

                if (report != null) {
                    Logger.Global.logState("Report generated: " + report.getAbsolutePath());
                    showReportGeneratedDialog(report);
                }
            }
        });
        mnProject.add(mntmGenerateReport);

        JSeparator separator_6 = new JSeparator();
        mnProject.add(separator_6);

        JMenuItem mntmResetProject = new JMenuItem("Reset Project");
        mntmResetProject.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetProject();
            }
        });
        mnProject.add(mntmResetProject);

        JMenu menuInfo = new JMenu("?");
        menuBar.add(menuInfo);

        JMenuItem mntmAboutItem = new JMenuItem("About");
        mntmAboutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        mntmAboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionShowAboutDialog();
            }
        });
        menuInfo.add(mntmAboutItem);
        pnlContentPane = new JPanel();
        pnlContentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(pnlContentPane);
        SpringLayout sl_contentPane = new SpringLayout();
        pnlContentPane.setLayout(sl_contentPane);

        JPanel panelTestcase = new JPanel();
        sl_contentPane.putConstraint(SpringLayout.NORTH, panelTestcase, 0, SpringLayout.NORTH, pnlContentPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, panelTestcase, 220, SpringLayout.WEST, pnlContentPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, panelTestcase, 272, SpringLayout.NORTH, pnlContentPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, panelTestcase, -2, SpringLayout.EAST, pnlContentPane);
        panelTestcase.setBorder(BorderFactory.createTitledBorder("Testcase Details"));
        panelTestcase.setPreferredSize(new Dimension(780, 150));
        SpringLayout sl_panelTestcase = new SpringLayout();
        panelTestcase.setLayout(sl_panelTestcase);

        lblStatusbar = new JLabel("Testcases NOT loaded; idle mode");
        lblStatusbar.setEnabled(false);
        lblStatusbar.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        sl_contentPane.putConstraint(SpringLayout.WEST, lblStatusbar, 0, SpringLayout.WEST, pnlContentPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, lblStatusbar, 0, SpringLayout.SOUTH, pnlContentPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, lblStatusbar, 0, SpringLayout.EAST, pnlContentPane);
        pnlContentPane.add(lblStatusbar);

        lblTestcaseName = new JLabel("Testcase: <no testcase selected>");
        sl_panelTestcase.putConstraint(SpringLayout.NORTH, lblTestcaseName, 0, SpringLayout.NORTH, panelTestcase);
        sl_panelTestcase.putConstraint(SpringLayout.WEST, lblTestcaseName, 0, SpringLayout.WEST, panelTestcase);
        sl_panelTestcase.putConstraint(SpringLayout.SOUTH, lblTestcaseName, 25, SpringLayout.NORTH, panelTestcase);
        sl_panelTestcase.putConstraint(SpringLayout.EAST, lblTestcaseName, 200, SpringLayout.WEST, panelTestcase);
        panelTestcase.add(lblTestcaseName);

        lblStateIcon = new JLabel(STATE_LABEL_IDLE);
        lblStateIcon.setEnabled(false);
        lblStateIcon.setHorizontalTextPosition(SwingConstants.CENTER);
        lblStateIcon.setHorizontalAlignment(SwingConstants.CENTER);
        lblStateIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblStateIcon.setOpaque(true);
        lblStateIcon.setBackground(STATE_COLOR_IDLE);
        lblStateIcon.setToolTipText("Displays current status");
        sl_panelTestcase.putConstraint(SpringLayout.NORTH, lblStateIcon, 0, SpringLayout.NORTH, panelTestcase);
        sl_panelTestcase.putConstraint(SpringLayout.SOUTH, lblStateIcon, 24, SpringLayout.NORTH, panelTestcase);
        sl_panelTestcase.putConstraint(SpringLayout.WEST, lblStateIcon, -100, SpringLayout.EAST, lblStateIcon);
        sl_panelTestcase.putConstraint(SpringLayout.EAST, lblStateIcon, -2, SpringLayout.EAST, panelTestcase);
        panelTestcase.add(lblStateIcon);

        JLabel lblTCState = new JLabel("State:");
        lblTCState.setHorizontalAlignment(SwingConstants.TRAILING);
        sl_panelTestcase.putConstraint(SpringLayout.NORTH, lblTCState, 0, SpringLayout.NORTH, lblStateIcon);
        sl_panelTestcase.putConstraint(SpringLayout.WEST, lblTCState, 10, SpringLayout.EAST, lblTestcaseName);
        sl_panelTestcase.putConstraint(SpringLayout.SOUTH, lblTCState, 0, SpringLayout.SOUTH, lblStateIcon);
        sl_panelTestcase.putConstraint(SpringLayout.EAST, lblTCState, -10, SpringLayout.WEST, lblStateIcon);
        panelTestcase.add(lblTCState);

        sl_panelTestcase.putConstraint(SpringLayout.SOUTH, lblStatusbar, 0, SpringLayout.SOUTH, lblStateIcon);

        JPanel tabbedPanePanel = new JPanel();
        sl_panelTestcase.putConstraint(SpringLayout.WEST, tabbedPanePanel, 0, SpringLayout.WEST, panelTestcase);
        sl_panelTestcase.putConstraint(SpringLayout.EAST, tabbedPanePanel, 0, SpringLayout.EAST, panelTestcase);
        sl_panelTestcase.putConstraint(SpringLayout.SOUTH, tabbedPanePanel, 0, SpringLayout.SOUTH, panelTestcase);
        tabbedPanePanel.setPreferredSize(new Dimension(600, 300));
        panelTestcase.add(tabbedPanePanel);
        tabbedPanePanel.setLayout(new GridLayout(1, 1));

        taDescr = new JTextArea("<no data loaded>");
        taDescr.setLineWrap(true);
        taDescr.setWrapStyleWord(true);
        taDescr.setEditable(false);
        taProtocol = new JTextArea("<no data loaded>");
        taProtocol.setEditable(false);
        JScrollPane spDescr = new JScrollPane(taDescr);
        JScrollPane spProtocol = new JScrollPane(taProtocol);
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.addTab("Description", spDescr);
        tabbedPane.setEnabledAt(0, true);
        tabbedPane.addTab("Testcase Protocol", spProtocol);
        tabbedPane.setEnabledAt(1, true);
        tabbedPane.setToolTipTextAt(0, "Short description of the testcase");
        tabbedPane.setToolTipTextAt(1, "The testcase protocol");
        tabbedPanePanel.add(tabbedPane);

        JScrollPane spTopTree = new JScrollPane();

        treeTestcases = new JTree();
        treeTestcases.setToolTipText("Please select any avaible testcases to see details or to start execution");
        treeTestcases.setEnabled(false);
        TreeMultiListener multiListener = new TreeMultiListener();
        treeTestcases.addTreeSelectionListener(multiListener);
        treeTestcases.addMouseListener(multiListener);
        treeTestcases.setShowsRootHandles(true);
        treeTestcases.setRootVisible(false);
        treeTestcases.setCellRenderer(new TestcaseCellRenderer());
        spTopTree.setViewportView(treeTestcases);

        JLabel lblHeaderTree = new JLabel("Available Testcases");
        spTopTree.setColumnHeaderView(lblHeaderTree);

        
        
        JSplitPane horizontalSplitPane = new JSplitPane();
        horizontalSplitPane.setResizeWeight(0.3);
        horizontalSplitPane.setOneTouchExpandable(true);
        horizontalSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        
        sl_contentPane.putConstraint(SpringLayout.SOUTH, horizontalSplitPane, -2, SpringLayout.NORTH, lblStatusbar);
        sl_contentPane.putConstraint(SpringLayout.NORTH, horizontalSplitPane, 0, SpringLayout.NORTH, pnlContentPane);
        sl_contentPane.putConstraint(SpringLayout.WEST, horizontalSplitPane, 0, SpringLayout.WEST, pnlContentPane);
        sl_contentPane.putConstraint(SpringLayout.EAST, horizontalSplitPane, 0, SpringLayout.EAST, pnlContentPane);
        
        sl_contentPane.putConstraint(SpringLayout.EAST, spTopTree, 220, SpringLayout.WEST, pnlContentPane);
        sl_contentPane.putConstraint(SpringLayout.NORTH, spTopTree, 0, SpringLayout.NORTH, pnlContentPane);

        JPanel panelUILog = new JPanel();
        sl_contentPane.putConstraint(SpringLayout.NORTH, panelUILog, 5, SpringLayout.SOUTH, panelTestcase);
        panelUILog.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Testcase Log", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        sl_contentPane.putConstraint(SpringLayout.WEST, panelUILog, 0, SpringLayout.WEST, panelTestcase);
        sl_contentPane.putConstraint(SpringLayout.EAST, panelUILog, 0, SpringLayout.EAST, panelTestcase);

        jfxUILog = new JFXPanel();
        panelUILog.setLayout(new BoxLayout(panelUILog, BoxLayout.X_AXIS));

        JScrollPane spUILog = new JScrollPane(jfxUILog);
        panelUILog.add(spUILog);
        sl_contentPane.putConstraint(SpringLayout.NORTH, spUILog, 5, SpringLayout.SOUTH, panelTestcase);
        sl_contentPane.putConstraint(SpringLayout.WEST, spUILog, 300, SpringLayout.WEST, panelTestcase);
        sl_contentPane.putConstraint(SpringLayout.EAST, spUILog, 0, SpringLayout.EAST, panelTestcase);
        sl_panelTestcase.putConstraint(SpringLayout.NORTH, lblStatusbar, 0, SpringLayout.NORTH, spUILog);

        JSplitPane splitPane = new JSplitPane();
        splitPane.setResizeWeight(0.3);
        splitPane.setOneTouchExpandable(true);
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);

        sl_contentPane.putConstraint(SpringLayout.NORTH, splitPane, 0, SpringLayout.NORTH, pnlContentPane);

        splitPane.setLeftComponent(panelTestcase);
        splitPane.setRightComponent(panelUILog);


        sl_contentPane.putConstraint(SpringLayout.NORTH, spTopTree, 0, SpringLayout.NORTH, splitPane);
        sl_contentPane.putConstraint(SpringLayout.SOUTH, spTopTree, 0, SpringLayout.SOUTH, splitPane);
        
        pnlContentPane.add(horizontalSplitPane);
        horizontalSplitPane.setLeftComponent(spTopTree);
        horizontalSplitPane.setRightComponent(splitPane);
        
        
        
        
        btnStart = new JButton(TRIGGER_LABEL_START);
        btnStart.setEnabled(false);
        btnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // iff one item was selected before running the test make sure it is selected again
                if (treeSelection != null) {
                    treeTestcases.setSelectionPaths(UIUtils.getUpdatedTreePath(treeSelection, treeTestcases));
                }
                actionTestcaseTrigger();

            }
        });
        sl_panelTestcase.putConstraint(SpringLayout.NORTH, btnStart, 5, SpringLayout.SOUTH, lblTestcaseName);
        sl_panelTestcase.putConstraint(SpringLayout.WEST, btnStart, 0, SpringLayout.WEST, lblTestcaseName);
        sl_panelTestcase.putConstraint(SpringLayout.SOUTH, btnStart, 35, SpringLayout.NORTH, btnStart);
        sl_panelTestcase.putConstraint(SpringLayout.EAST, btnStart, 180, SpringLayout.WEST, btnStart);
        panelTestcase.add(btnStart);

        btnPass = new JButton("pass");
        btnPass.setEnabled(false);
        sl_panelTestcase.putConstraint(SpringLayout.NORTH, btnPass, 5, SpringLayout.SOUTH, btnStart);
        sl_panelTestcase.putConstraint(SpringLayout.WEST, btnPass, 0, SpringLayout.WEST, btnStart);
        sl_panelTestcase.putConstraint(SpringLayout.SOUTH, btnPass, 25, SpringLayout.NORTH, btnPass);
        sl_panelTestcase.putConstraint(SpringLayout.EAST, btnPass, 85, SpringLayout.WEST, btnPass);

        sl_panelTestcase.putConstraint(SpringLayout.NORTH, tabbedPanePanel, 5, SpringLayout.SOUTH, btnPass);
        btnPass.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // if one item was selected before running the test make sure it is selected again
                if (treeSelection != null) {
                    treeTestcases.setSelectionPaths(UIUtils.getUpdatedTreePath(treeSelection, treeTestcases));
                }
                actionUpdateTestcaseStates(TestState.Passed);
            }
        });
        try {
            btnPass.setIcon(UIUtils.scaleImageIconTo(UIUtils.createImageIcon("/com/secunet/ipsmall/ui/icons/passed.png"), 16, 16));
        } catch (Exception ignore) {
        }
        panelTestcase.add(btnPass);

        btnFail = new JButton("fail");
        btnFail.setEnabled(false);
        sl_panelTestcase.putConstraint(SpringLayout.NORTH, btnFail, 0, SpringLayout.NORTH, btnPass);
        sl_panelTestcase.putConstraint(SpringLayout.WEST, btnFail, 10, SpringLayout.EAST, btnPass);
        sl_panelTestcase.putConstraint(SpringLayout.SOUTH, btnFail, 25, SpringLayout.NORTH, btnFail);
        sl_panelTestcase.putConstraint(SpringLayout.EAST, btnFail, 85, SpringLayout.WEST, btnFail);
        btnFail.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // if one item was selected before running the test make sure it is selected again
                if (treeSelection != null) {
                    treeTestcases.setSelectionPaths(UIUtils.getUpdatedTreePath(treeSelection, treeTestcases));
                }
                actionUpdateTestcaseStates(TestState.Failed);
            }
        });
        try {
            btnFail.setIcon(UIUtils.scaleImageIconTo(UIUtils.createImageIcon("/com/secunet/ipsmall/ui/icons/failed.png"), 16, 16));
        } catch (Exception ignore) {
        }
        panelTestcase.add(btnFail);

        proBarTestcase = new JProgressBar();
        sl_panelTestcase.putConstraint(SpringLayout.NORTH, proBarTestcase, 0, SpringLayout.NORTH, btnStart);
        sl_panelTestcase.putConstraint(SpringLayout.WEST, proBarTestcase, 10, SpringLayout.EAST, btnStart);
        sl_panelTestcase.putConstraint(SpringLayout.SOUTH, proBarTestcase, 0, SpringLayout.SOUTH, btnStart);
        sl_panelTestcase.putConstraint(SpringLayout.EAST, proBarTestcase, -2, SpringLayout.EAST, panelTestcase);
        panelTestcase.add(proBarTestcase);

        lblTaskname = new JLabel("no task running");
        lblTaskname.setEnabled(false);
        lblTaskname.setHorizontalAlignment(SwingConstants.TRAILING);
        lblTaskname.setLabelFor(proBarTestcase);
        sl_panelTestcase.putConstraint(SpringLayout.NORTH, lblTaskname, 0, SpringLayout.NORTH, btnFail);
        sl_panelTestcase.putConstraint(SpringLayout.WEST, lblTaskname, 0, SpringLayout.WEST, proBarTestcase);
        sl_panelTestcase.putConstraint(SpringLayout.SOUTH, lblTaskname, 0, SpringLayout.SOUTH, btnFail);
        sl_panelTestcase.putConstraint(SpringLayout.EAST, lblTaskname, 0, SpringLayout.EAST, proBarTestcase);
        panelTestcase.add(lblTaskname);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                WebView view = new WebView();
                webEngine = view.getEngine();
                jfxUILog.setScene(new Scene(view));
            }
        });
    }

    protected ITestData getSelectedTestcase() {
        if (treeTestcases.getSelectionPath() != null && treeTestcases.getSelectionPath().getLastPathComponent() instanceof DefaultMutableTreeNode
                && ((DefaultMutableTreeNode) treeTestcases.getSelectionPath().getLastPathComponent()).getUserObject() instanceof ITestData) {
            return (ITestData) ((DefaultMutableTreeNode) treeTestcases.getSelectionPath().getLastPathComponent()).getUserObject();
        }

        return null;
    }

    protected void actionUpdateProfile(ActionEvent e) {
        JRadioButtonMenuItem rbmnLogProfile = (JRadioButtonMenuItem) e.getSource();
        rbmnLogProfile.getText();

        File profileDir = new File(GlobalSettings.getLogProfilesDir());
        if (profileDir.exists() && profileDir.isDirectory()) {
            for (File profileFile : profileDir.listFiles()) {
                String profileFileName = profileFile.getName();
                if (profileFile.isFile() && profileFileName.endsWith(".properties")) {
                    if (rbmnLogProfile.getText().equals(IPSmallManager.getInstance().getLoggingProfileName(profileFileName))) {
                        IPSmallManager.getInstance().setLoggingProfileFileName(profileFileName);
                        IPSmallManager.getInstance().loadLoggingProfile(profileFileName);
                        break;
                    }
                }
            }
        }
    }

    protected void actionUpdateTestcaseStates(TestState state) {
        // check if testcase running, if so stop it and fail it
        if (IPSmallManager.getInstance().isTestCaseRunning()) {
            if (enforceAsk) {
                int answer = JOptionPane.showConfirmDialog(this, "Stop queue and set running to " + state + "?", "Stop?", JOptionPane.YES_NO_OPTION);
                if (answer != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            String testcase = IPSmallManager.getInstance().getProject().getRunningTestcaseId();
            try {
                String msg = "Testcase set to " + state + " by operator!";
                if (state == TestState.Passed) {
                    Logger.UI.logConformity(ConformityResult.passed, ConformityMode.manual, msg);
                } else if (state == TestState.Failed) {
                    Logger.UI.logConformity(ConformityResult.failed, ConformityMode.manual, msg, LogLevel.Error);
                } else {
                    Logger.UI.logState(msg);
                }

                IPSmallManager.getInstance().cancelTestcase();
                IPSmallManager.getInstance().getProject().updateState(testcase, state);
            } catch (Exception ex) {
                displayErrorMessage(ex);
            }

            // whenever testcase-state changes:
            // save new project state to prevent lost test-results after crash
            // EIDCLIENTC-244:
            try {
                IPSmallManager.getInstance().saveProject();
            } catch (IOException ex) {
                IPSmallManager.getInstance().getMainFrame().displayErrorMessage(ex);
                return;
            }

        } else {
            // if no testcase running, check if testcases selected
            if (treeTestcases.getSelectionPaths() == null || treeTestcases.getSelectionPaths().length == 0) {
                publishToStatusbar("No testcase running or selected!");
            } else {
                // if many selected, ask if all tcs should be set to fail
                if (treeTestcases.getSelectionPaths().length > 1) {
                    int answer = JOptionPane.showConfirmDialog(this, "Set all selected testcases to " + state + "?", "All?", JOptionPane.YES_NO_OPTION);
                    if (answer != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
                List<ITestData> tcDatas = new ArrayList<>();
                for (TreePath path : treeTestcases.getSelectionPaths()) {
                    if (path.getLastPathComponent() instanceof DefaultMutableTreeNode
                            && ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject() instanceof ITestData) {
                        ITestData testdata = (ITestData) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
                        tcDatas.add(testdata);
                    }
                }

                if (tcDatas.isEmpty()) {
                    publishToStatusbar("No valid testcases selected!");
                }

                if (autoClear) {
                    setLogContent("");
                }

                for (ITestData tcID2fail : tcDatas) {
                    try {
                        tcID2fail.generateNewTestcaseLogfile();
                        Logger.setTestCasefile(tcID2fail.getLogTestcasesFilepath());
                        String msg = "Testcase set to " + state + " by operator!";
                        if (state == TestState.Passed) {
                            Logger.UI.logConformity(ConformityResult.passed, ConformityMode.manual, msg);
                        } else if (state == TestState.Failed) {
                            Logger.UI.logConformity(ConformityResult.failed, ConformityMode.manual, msg, LogLevel.Error);
                        } else {
                            Logger.UI.logState(msg);
                        }
                        IPSmallManager.getInstance().getProject().updateState(tcID2fail.getTestName(), state);
                    } catch (Exception ex) {
                        displayErrorMessage(ex);
                    }
                }

                // whenever testcase-state changes:
                // save new project state to prevent lost test-results after crash
                // EIDCLIENTC-244:
                try {
                    IPSmallManager.getInstance().saveProject();
                } catch (IOException ex) {
                    IPSmallManager.getInstance().getMainFrame().displayErrorMessage(ex);
                    return;
                }

            }
        }
    }

    protected void viewDebugLogfile() {
        viewLogFile(Logger.getGlobalLogfile());
    }

    /**
     * Trigger the reset of the projects test case states
     */
    protected void resetProject() {
        int answerReset = JOptionPane.showConfirmDialog(this, "Reseting testcases will set all testcase states to not run!\nDo you really want to reset?", "Reset?",
                JOptionPane.YES_NO_OPTION);
        if (JOptionPane.YES_OPTION == answerReset) {

            int answerDeleteLogs = JOptionPane.showConfirmDialog(this, "Do you want to delete old logfiles?", "Delete Logs?",
                    JOptionPane.YES_NO_OPTION);
            if (JOptionPane.YES_OPTION == answerDeleteLogs || JOptionPane.NO_OPTION == answerDeleteLogs) {
                IPSmallManager.getInstance().resetTestcases();
                clear();
            }

            if (JOptionPane.YES_OPTION == answerDeleteLogs) {
                File logDir = new File(IPSmallManager.testobjectDirectory + System.getProperty("file.separator") + "Log");
                if (logDir.exists() && logDir.isDirectory()) {
                    File[] files = logDir.listFiles();
                    for (File file : files) {
                        file.delete();
                    }
                }
            }
            // trigger ui update
            treeTestcases.fireTreeExpanded(null);
        }
    }

    /**
     * react on the changes of the ui tree element
     *
     * @param e selection event
     */
    protected void testcaseTreeSelectionValueChanged(TreeSelectionEvent e) {
        if (IPSmallManager.getInstance().isTestCaseRunning()) {
            return;
        }

        if (e.getNewLeadSelectionPath() != null && e.getNewLeadSelectionPath().getLastPathComponent() instanceof DefaultMutableTreeNode
                && ((DefaultMutableTreeNode) e.getNewLeadSelectionPath().getLastPathComponent()).getUserObject() instanceof ITestData) {
            ITestData testdata = (ITestData) ((DefaultMutableTreeNode) e.getNewLeadSelectionPath().getLastPathComponent()).getUserObject();
            display(testdata);

            // reset the protocol if a new test was selected from the tree
            if (lastSelectedTestcase != null && !lastSelectedTestcase.equals(testdata.getTestName())) {
                taProtocol.setText("<no data loaded>");
                tabbedPane.setSelectedIndex(0);
            }
            lastSelectedTestcase = testdata.getTestName();

        } else {
            if (e.getNewLeadSelectionPath() != null) {
                clear();
            }
        }
    }

    private void clear() {
        lblTestcaseName.setText("Testcase: <no testcase selected>");

        btnStart.setEnabled(false);
        btnPass.setEnabled(false);
        btnFail.setEnabled(false);

        tabbedPane.setSelectedIndex(0);
        taDescr.setText("<no data loaded>");
        taProtocol.setText("<no data loaded>");

        setLogContent("");

        lblStateIcon.setText(STATE_LABEL_IDLE);
        lblStateIcon.setBackground(STATE_COLOR_IDLE);
    }

    /**
     * Display the given test data in the main ui elements and enables/disables
     * certain ui elements.
     *
     * @param data the test data. must not be null
     */
    private void display(final ITestData data) {
        lblTestcaseName.setText("Testcase: " + data.getTestName());
        btnStart.setEnabled(data.getTestEnabled());
        btnPass.setEnabled(true);
        btnFail.setEnabled(true);
        StringBuilder descrBldr = new StringBuilder();
        descrBldr.append(data.getTestDescription()).append(System.lineSeparator()).append(System.lineSeparator());
        descrBldr.append("Browser Type:").append(data.getTestType().toString().toLowerCase()).append(System.lineSeparator());
        if (!data.isFailOnXMLEvaluationError()) {
            descrBldr.append("Warning: hard xml message validation is deactivated!").append(System.lineSeparator());
        }
        taDescr.setText(descrBldr.toString());

        TestState state = IPSmallManager.getInstance().getProject().getState(data.getTestName());
        lblStateIcon.setText(state.toString());
        lblStateIcon.setEnabled(true);
        switch (state) {
            case Running:
                lblStateIcon.setBackground(STATE_COLOR_RUNNING);
                break;

            case Failed:
                lblStateIcon.setBackground(STATE_COLOR_FAILED);
                break;

            case Passed:
                lblStateIcon.setBackground(STATE_COLOR_PASSED);
                break;

            case Undetermined:
                lblStateIcon.setBackground(STATE_COLOR_UNDETERMINED);
                break;

            case Idle:
            default:
                lblStateIcon.setBackground(STATE_COLOR_IDLE);
        }

        File logFile = getLatestLogFile(data);

        if (logFile != null && logFile.exists() && logFile.canRead()) {
            setLogContent("Loading log...");
            LogWorker worker = new LogWorker(this, logFile);
            worker.execute();
        } else {
            setLogContent("");
            publishToStatusbar("Could not load log into UI: " + data.getTestName());
        }
    }

    private File getLatestLogFile(final ITestData data) {
        File logFile = null;
        if (data.getLogTestcasesFilepath() != null) {
            logFile = new File(data.getLogTestcasesFilepath());
        }

        if (logFile == null || (logFile != null && !logFile.exists())) {
            // try get latest
            File baseDir = new File(IPSmallManager.testobjectDirectory, "log");
            if (baseDir.exists() && baseDir.isDirectory()) {
                String[] candidates = baseDir.list(new FilenameFilter() {

                    @Override
                    public boolean accept(File dir, String name) {
                        return name.contains(data.getTestModuleName()) && name.contains(data.getTestName()) && name.endsWith(".log");
                    }
                });
                if (candidates != null && candidates.length > 0) {
                    Arrays.sort(candidates);
                    logFile = new File(baseDir, candidates[0]);
                }
            }
        }
        return logFile;
    }

    @SuppressWarnings("unused")  // for future use?
    private void actionSaveLogFile(String filepath) {
        if (filepath == null) {
            displayErrorMessage("File not set or does not exist", "File not set or not existent");
            return;
        }
        try {
            File source = new File(filepath);

            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (JFileChooser.APPROVE_OPTION == chooser.showSaveDialog(this)) {
                CommonUtil.copyFileTo(source, chooser.getSelectedFile());
                publishToStatusbar("Copied log");
            } else {
                publishToStatusbar("Saving logfile canceled by user");
            }
        } catch (IOException e) {
            displayErrorMessage(e);
            publishToStatusbar("Failed to copy log");
        }
    }

    private void viewLogFile(String filepath) {
        if (filepath == null) {
            displayErrorMessage("File not set or does not exist", "File not set or not existent");
            return;
        }

        try {
            // handle windows bug
            // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6631015
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                String cmd = "rundll32 url.dll,FileProtocolHandler " + new File(filepath).getCanonicalPath();
                Runtime.getRuntime().exec(cmd);
            } else {
                Desktop.getDesktop().edit(new File(filepath));
            }
        } catch (IOException exc) {
            displayErrorMessage(exc);
        }
    }

    @SuppressWarnings("unused") // for future use?
    private void actionSaveUILog(ActionEvent e) {
        Logger.UI.logState("actionSaveUILog(" + e + ")", LogLevel.Debug);
        JFileChooser saveChooser = new JFileChooser();
        saveChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int interactionResult = saveChooser.showSaveDialog(this);
        FileOutputStream fos = null;
        try {
            if (interactionResult == JFileChooser.APPROVE_OPTION) {
                File f = saveChooser.getSelectedFile();
                if (f.exists()) {
                    int override = JOptionPane.showConfirmDialog(this, "File exist, override?", "Override?", JOptionPane.YES_NO_OPTION);
                    if (JOptionPane.NO_OPTION == override) {
                        Logger.UI.logState("Canceled by user", LogLevel.Debug);
                        return;
                    }
                } else {
                    f.createNewFile();
                }

                fos = new FileOutputStream(f, false);
                //fos.write(taUILog.getText().getBytes());
                Logger.UI.logState("Saved UI Log successfully: " + f.getAbsolutePath());
                publishToStatusbar("Saved successfully: " + f.getName());
            } else {
                Logger.UI.logState("Canceled by user", LogLevel.Debug);
            }
        } catch (IOException io) {
            displayErrorMessage(io);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ignore) {
                }
            }
        }
    }

    private void actionClearUILog() {
        setLogContent("");
    }

    private void actionSetTestDirectoryDialog() {
        Logger.UI.logState("UI::actionSet-testobject-DirectoryDialog", LogLevel.Debug);
        JFileChooser chooser = null;
        chooser = new JFileChooser(IPSmallManager.testobjectDirectory);

        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int interactionResult = chooser.showOpenDialog(this);
        if (interactionResult == JFileChooser.APPROVE_OPTION) {
            File dir = chooser.getSelectedFile();
            boolean hasCommonFolder = false;
            boolean hasFBTestFolder = false;

            File tests = new File(dir, GlobalSettings.getTOTestsDir());
            if (tests.exists() && tests.isDirectory()) {
                for (File child : tests.listFiles()) {
                    hasCommonFolder |= (new File(GlobalSettings.getTOTestsCommonDir()).getName().toLowerCase().equals(child.getName().toLowerCase()) && child.isDirectory());
                    hasFBTestFolder |= child.isDirectory() && CommonUtil.containsFilebasedTestcaseFolder(child);
                    if (hasCommonFolder && hasFBTestFolder) {
                        break;
                    }
                }
            }

            if (!hasCommonFolder) {
                displayErrorMessage("No valid folder selected, could not find common folder!", "No valid folder");
                return;
            }
            if (!hasFBTestFolder) {
                displayErrorMessage("No valid folder selected, could not find testcase folder!", "No valid folder");
                return;
            }

            IPSmallManager.testobjectDirectory = dir;

        }
    }

    private void actionShowAboutDialog() {
        String aboutTitle = "About " + GlobalInfo.Title.getValue();
        String aboutMessage = GlobalInfo.Title.getValue();
        aboutMessage += "\n";
        aboutMessage += "\n" + GlobalInfo.Copyright.getValue() + "\n";
        aboutMessage += "\n" + "Version: \t" + GlobalInfo.SoftwareVersion.getValue();
        aboutMessage += "\n" + "Published: \t" + GlobalInfo.PublishDate.getValue();
        JOptionPane.showMessageDialog(this, aboutMessage, aboutTitle, JOptionPane.PLAIN_MESSAGE);
    }

    private void showReportGeneratedDialog(File reportFile) {
        String title = GlobalInfo.Title.getValue();

        String message = "Report generated: ";
        message += "\n";
        message += "\n" + reportFile.getAbsolutePath().toString() + "\n";

        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Sets UI to stae testcase startet.
     */
    public void setUITestcaseStarted() {
        switchUIOperational(false);
        taProtocol.setText("");
        btnStart.setText(TRIGGER_LABEL_CANCEL);
        tabbedPane.setSelectedIndex(1);
    }

    /**
     * Sets UI to stae testcase stopped.
     */
    public void setUITestcaseStopped() {
        btnStart.setText(TRIGGER_LABEL_START);
        switchUIToOperational();
        switchUIOperational(true);
    }

    /**
     * Selects given testcase in tree view.
     *
     * @param testdata - Given testcase to select.
     * @return <i>True</i>, if testcase was selected successfully.
     */
    public boolean selectTestcase(ITestData testdata) {
        for (int i = 0; i < treeTestcases.getRowCount(); i++) {
            TreePath path = treeTestcases.getPathForRow(i);
            if (path.getLastPathComponent() instanceof DefaultMutableTreeNode
                    && ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject() instanceof ITestData) {
                ITestData pathTC = (ITestData) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
                if (pathTC.getTestName().equals(testdata.getTestName())) {
                    treeTestcases.setSelectionPath(path);
                    treeTestcasesExpansionState = UIUtils.saveExpansionStateStrings(treeTestcases);
                    treeSelection = UIUtils.saveTreeSelection(treeTestcases);
                    display(pathTC);
                    return true;
                }
            }
        }

        return false;
    }

    private DefaultMutableTreeNode findTestcaseInTree(String testid) {
        for (int i = 0; i < treeTestcases.getRowCount(); i++) {
            TreePath path = treeTestcases.getPathForRow(i);
            if (path.getLastPathComponent() instanceof DefaultMutableTreeNode
                    && ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject() instanceof ITestData) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                ITestData pathTC = (ITestData) node.getUserObject();
                if (pathTC.getTestName().equals(testid)) {
                    return node;
                }
            }
        }

        return null;
    }

    /**
     * Main method to start or cancel test case runs by checking the selected
     * tree elements and filling/filtering the selections into the queue. than
     * setting the queue into the project and trigger the manager to start the
     * test cases.
     */
    private void actionTestcaseTrigger() {
        // Log to global, because testcase may not run
        Logger.Global.logState("UI::actionTestcaseTrigger:" + btnStart.getText(), LogLevel.Debug);

        if (TRIGGER_LABEL_START.equals(btnStart.getText())) {
            try {
                if (autoClear) {
                    setLogContent("");
                }

                taProtocol.setText("");

                TreePath[] paths = treeTestcases.getSelectionPaths();

                // if nothing selected and queue not empty
                if (paths == null && IPSmallManager.getInstance().getProject().getQueue() != null
                        && !IPSmallManager.getInstance().getProject().getQueue().isEmpty() && IPSmallManager.getInstance().getProject().hasAutostartInQueue()) {

                    int answer = JOptionPane.showConfirmDialog(this, "Startable Testcases in queue, want to start them?", "Use queue?",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (JOptionPane.YES_OPTION == answer) {
                        if (!IPSmallManager.getInstance().startTestcase(false)) {
                            publishToStatusbar("Could not start any testcases");
                        }
                    }
                }

                if (paths == null) {
                    return;
                }

                // transfer selection after filtering for test cases into queue
                List<ITestData> queueItems = new ArrayList<>(paths.length);
                for (TreePath path : paths) {
                    if (path.getLastPathComponent() instanceof DefaultMutableTreeNode
                            && ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject() instanceof ITestData) {
                        queueItems.add((ITestData) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject());
                    }

                    // get all test cases of a module
                    if (path.getLastPathComponent() instanceof DefaultMutableTreeNode
                            && ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject() instanceof String) {
                        for (Enumeration<?> e = ((DefaultMutableTreeNode) path.getLastPathComponent()).children(); e.hasMoreElements();) {
                            DefaultMutableTreeNode child = (DefaultMutableTreeNode) e.nextElement();
                            if (child.getUserObject() instanceof ITestData) {
                                queueItems.add((ITestData) child.getUserObject());
                            }
                        }
                    }
                }

                if (queueItems.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No testcases after filtering.", "Queue empty", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                IPSmallManager.getInstance().getProject().setQueue(queueItems);

                // start processing of the queue
                IPSmallManager.getInstance().getProject().setOnlyAutonomic(false);
                if (IPSmallManager.getInstance().startTestcase(false)) {
                    setUITestcaseStarted();
                } else {
                    JOptionPane.showMessageDialog(this, "No testcases started", "Queue empty", JOptionPane.INFORMATION_MESSAGE);
                    publishToStatusbar("No Testcases started");
                }
            } catch (Exception ex) {
                IPSmallManager.getInstance().getProject().clearQueue();
                displayErrorMessage(ex);
                publishToStatusbar("Error while starting testcase: " + ex.getMessage());
            }
        } else { // STOP
            if (enforceAsk) {
                int uiResult = JOptionPane.showConfirmDialog(this, "Really want to stop?", "Really?", JOptionPane.YES_NO_OPTION);
                if (JOptionPane.YES_OPTION != uiResult) {
                    return;
                }
            }

            try {
                IPSmallManager.getInstance().cancelTestcase();
                setUITestcaseStopped();
            } catch (Exception ex) {
                displayErrorMessage(ex);
            } finally {
                // TODO: switch always to operational okay??
                switchUIToOperational();
                switchUIOperational(true);
            }
        }
    }

    /**
     * Checks if testcase is running (ask if exit anyways) Checks if project was
     * changed (ask if user wishes to save and exits when no error occured, does
     * not exit on cancel or close)
     */
    private void shutdown() {
        if (!IPSmallManager.getInstance().canShutdown()
                && JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(this, "Application currently busy, shutdown anyways?", "Busy",
                        JOptionPane.YES_NO_OPTION)) {
            return;
        }

        /*if (IPSmallManager.getInstance().getProject().isUnsaved()) {
         int userchoice = JOptionPane.showConfirmDialog(this, "Project changed, want to save before exit?", "Project unsaved",
         JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
         switch (userchoice) {
         case JOptionPane.YES_OPTION:
         try {
         IPSmallManager.getInstance().saveProject();
         } catch (IOException ex) {
         displayErrorMessage(ex);
         return;
         }
         break;
                
         case JOptionPane.NO_OPTION:
         // do not save but exit anyways
         break;
                
         case JOptionPane.CANCEL_OPTION:
         case JOptionPane.CLOSED_OPTION:
         return;
         }
         }*/
        try {
            IPSmallManager.getInstance().saveProject();
        } catch (IOException ex) {
            displayErrorMessage(ex);
            return;
        }

        IPSmallManager.getInstance().shutdown();
    }

    private void updateTestcase(String testid) {
        TestState state = IPSmallManager.getInstance().getProject().getState(testid);
        boolean running = TestState.Running == state;
        
        DefaultMutableTreeNode node = findTestcaseInTree(testid);
        ((DefaultTreeModel) treeTestcases.getModel()).nodeChanged(node);
        
        updateQueue();

        publishToStatusbar(testid + " " + state);

        //proBarTestcase.setIndeterminate(running);
        proBarTestcase.setEnabled(running);
        treeTestcases.setEnabled(!running);

        // update ui to reflect the current state and reselect previously selected nodes if applicable
        lblStateIcon.setText(state.toString());
        switch (state) {
            case Running:
                lblStateIcon.setBackground(STATE_COLOR_RUNNING);
                break;

            case Failed:
                lblStateIcon.setBackground(STATE_COLOR_FAILED);
                break;

            case Passed:
                lblStateIcon.setBackground(STATE_COLOR_PASSED);
                break;

            case Undetermined:
                lblStateIcon.setBackground(STATE_COLOR_UNDETERMINED);
                break;

            case Idle:
                break;
            default:
                lblStateIcon.setBackground(STATE_COLOR_IDLE);
        }

        if (running) {
            if (autoClear) {
                setLogContent("");
            }

            lblTaskname.setText(testid + " " + state);
        }
        switchUIOperational(IPSmallManager.getInstance().getProject().getRunningTestcaseId() == null);
    }

    public void switchUIOperational(final boolean operational) {
        treeTestcases.setEnabled(operational);
    }

    /* udpates the queue ui elements */
    private void updateQueue() {
        List<ITestData> queue = IPSmallManager.getInstance().getProject().getQueue();
        DefaultListModel<String> model = new DefaultListModel<>();

        for (ITestData data : queue) {
            model.addElement(data.getTestName());
        }
    }

    /**
     * updates the tree, queue and enables/disables certain interactive ui
     * elements
     */
    private void updateProjectUI() {
        updateTree();
        updateQueue();
        switchUIToOperational();
        switchUIOperational(IPSmallManager.getInstance().getProject().getRunningTestcaseId() != null);
    }

    /**
     * creates a new tree model and updates the tree ui.
     */
    private void updateTree() {
        TestProject project = IPSmallManager.getInstance().getProject();
        List<ITestData> testcases = project.getTestcases();

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Testcases", true);
        Map<String, DefaultMutableTreeNode> categories = new HashMap<>();

        for (ITestData data : testcases) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(data, false);
            String category = data.getTestModuleName().replace('_', ' ');
            if (!categories.containsKey(category)) {
                categories.put(category, new DefaultMutableTreeNode(category, true));
                root.add(categories.get(category));
            }

            categories.get(category).add(node);
        }
        treeTestcases.setModel(new DefaultTreeModel(UIUtils.sortTreeNodesAlphaNumeric(root)));

        if (treeTestcasesExpansionState != null) {
            UIUtils.expandByExpansionStateStrings(treeTestcases, treeTestcasesExpansionState);
        } else {
            UIUtils.expandAll(treeTestcases, true);
        }
        if (treeSelection != null) {
            treeTestcases.setSelectionPaths(UIUtils.getUpdatedTreePath(treeSelection, treeTestcases));
        }

    }

    // ### public methods ###
    /**
     * Must be called from the EDT.
     *
     * @param message must not be null
     */
    public void publishToStatusbar(String message) {
        lblStatusbar.setText(message);
    }

    /**
     * Must only be called in Event Dispatcher Thread!
     */
    public void displayErrorMessage(String message, String title) {
        // currently only simple text message
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.OK_OPTION);
    }

    /**
     * Must only be called in Event Dispatcher Thread!
     */
    public void displayErrorMessage(Exception ex) {
        Logger.UI.logException(ex);
        displayErrorMessage("Error occured:\n" + ex.getMessage(), "Error: " + ex.getClass().getName());
    }

    /**
     * Must only be called in Event Dispatcher Thread!
     */
    public void loadTestcases(final File testsdataDirectory) {
        try {
            boolean successfully = IPSmallManager.getInstance().loadTestcases(testsdataDirectory);
            if (!successfully) {
                setLogContent("Could not load all testcases!\nSee log for more information.");
                // displayErrorMessage("Could not load all testcases!\nSee log for more information.", "Error loading testcases");
            }
            setProject(IPSmallManager.getInstance().getProject());

            clear();

            publishToStatusbar("Loaded testcases from: " + testsdataDirectory.getAbsolutePath());
        } catch (Exception ex) {
            switchUIOperational(false);
            displayErrorMessage(ex);
            publishToStatusbar("Failed to load testcases: " + ex.getMessage());
        }
    }

    /**
     * Must only be called in Event Dispatcher Thread!
     */
    public void switchUIToOperational() {
        //proBarTestcase.setIndeterminate(false);
        proBarTestcase.setMaximum(1);
        proBarTestcase.setValue(0);
        publishToStatusbar("");
        proBarTestcase.setEnabled(false);
        btnStart.setText(TRIGGER_LABEL_START);
        lblStateIcon.setText(STATE_LABEL_IDLE);
        lblStateIcon.setEnabled(false);
        lblStateIcon.setBackground(STATE_COLOR_IDLE);

        if (lblStatusbar.isEnabled()) {
            publishToStatusbar("Testcases loaded; idle mode");
        } else {
            publishToStatusbar("Testcases NOT loaded; idle mode");
        }
    }

    /**
     * Must only be called in Event Dispatcher Thread!
     */
    public void setProject(TestProject newProject) {
        Logger.UI.logState("Building UI to new project...", LogLevel.Debug);
        IPSmallManager.getInstance().getProject().removePropertyChangeListener(this);
        newProject.addPropertyChangeListener(this);

        lblStatusbar.setEnabled(true);
        updateProjectUI();
        switchUIToOperational();
        switchUIOperational(true);

        if (newProject.isUnsaved()) {
            setTitle(generateTitle("(*)"));
        } else {
            setTitle(generateTitle());
        }
    }

    @Override
    /**
     * React to changes of testcases and the project.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource().equals(IPSmallManager.getInstance().getProject())) {
            switch (evt.getPropertyName()) {
                case TestProject.PROPERTY_PROJECT_CHANGED:
                    if (evt.getNewValue() instanceof Boolean && ((Boolean) evt.getNewValue()).booleanValue()) {
                        setTitle(generateTitle(" (*)"));
                    }
                    if (evt.getNewValue() instanceof Boolean && !((Boolean) evt.getNewValue()).booleanValue()) {
                        setTitle(generateTitle());
                    }

                    break;
                case TestProject.PROPERTY_TC_STATECHANGE:
                    updateTestcase((String) evt.getNewValue());
                    break;
                case TestProject.PROPERTY_QUEUE_CHANGED:
                    updateQueue();
                    break;
                case TestProject.PROPERTY_PROJECT_LOADED:
                    updateProjectUI();
                    break;
            }
        }
    }

    /**
     * Set title based on loaded test object
     *
     * @param addition will be added after base title separated with space
     * @return title
     */
    private String generateTitle(String... addition) {
        StringBuilder strBldr = new StringBuilder(GlobalInfo.Title.getValue());
        try {
            strBldr.append(" [").append(IPSmallManager.testobjectDirectory.getName()).append("]");
        } catch (Exception ignore) {
        }

        for (String add : addition) {
            strBldr.append(" ").append(add);
        }

        return strBldr.toString();
    }

    /**
     * Must only be called in Event Dispatcher Thread!
     *
     * @param text must not be null
     */
    public void setLogContent(final String content) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                webEngine.loadContent(content);
            }
        });
    }

    public void loadLogFile(final File file) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    webEngine.load(file.toURI().toURL().toString());
                } catch (Exception e) {
                }
            }
        });
    }

    /**
     * Listener to be used with tree items
     */
    private class TreeMultiListener implements TreeSelectionListener, MouseListener {

        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                // if there are not multiple rows selected, set selection to closest row
                TreePath[] selPaths = treeTestcases.getSelectionPaths();
                if (selPaths == null || selPaths.length < 2) {
                    int row = treeTestcases.getClosestRowForLocation(e.getX(), e.getY());
                    if (row != -1) {
                        treeTestcases.setSelectionRow(row);
                    }
                }
                // get current selection and show popup
                selPaths = treeTestcases.getSelectionPaths();
                if (selPaths.length > 0) {
                    RefreshPopup menu = new RefreshPopup(selPaths);
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            // EIDCLIENTC-214: cache expansion state of tree to restore it after UI 
            // changes (e.g. changes triggered by running test cases).
            treeTestcasesExpansionState = UIUtils.saveExpansionStateStrings(treeTestcases);
            treeSelection = UIUtils.saveTreeSelection(treeTestcases);
        }

        public void valueChanged(TreeSelectionEvent e) {
            testcaseTreeSelectionValueChanged(e);
        }

        // can't use an adapter here...
        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }

    /**
     * Right click menu for tree items
     */
    private class RefreshPopup extends JPopupMenu {

        private static final long serialVersionUID = 1L;
        JMenuItem refreshItem;
        JMenuItem resetItem;
        JMenuItem copyTestcase;
        JMenuItem editTestcase;

        public RefreshPopup(TreePath[] selPaths) {
            // only allow reloading of a single test case
            if (selPaths.length == 1) {
                refreshItem = new JMenuItem("Reload testcase");
                refreshItem.addActionListener(new RefreshListener(selPaths[0]));
                add(refreshItem);
            }
            resetItem = new JMenuItem("Reset status");
            resetItem.addActionListener(new ResetListener(selPaths));
            add(resetItem);

            //add(new JSeparator());
            // only allow copying of a single test case
            if (selPaths.length == 1) {
                copyTestcase = new JMenuItem("Copy testcase");
                copyTestcase.addActionListener(new CopyListener(selPaths[0]));
                add(copyTestcase);
            }

            // only allow editing of a single test case
            if (selPaths.length == 1) {
                editTestcase = new JMenuItem("Edit testcase");
                editTestcase.addActionListener(new EditListener(selPaths[0]));
                add(editTestcase);
            }
        }
    }

    /**
     * Reset the state of a test through the context menu.
     */
    private class ResetListener implements ActionListener {

        private final TreePath[] selPaths;

        public ResetListener(TreePath[] selPaths) {
            super();
            this.selPaths = selPaths;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            for (TreePath currPath : selPaths) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) currPath.getLastPathComponent();
                if (node.getUserObject() instanceof ITestData) {
                    IPSmallManager.getInstance().getProject().updateState(((ITestData) node.getUserObject()).getTestName(), TestState.Idle);

                    // whenever testcase-state changes:
                    // save new project state to prevent lost test-results after crash
                    // EIDCLIENTC-244:
                    try {
                        IPSmallManager.getInstance().saveProject();
                    } catch (IOException ex) {
                        IPSmallManager.getInstance().getMainFrame().displayErrorMessage(ex);
                        return;
                    }
                    
                    ((DefaultTreeModel) treeTestcases.getModel()).nodeChanged(node);

                }
            }
        }
    }

    /**
     * Reload a test configuration through the context menu.
     */
    private class RefreshListener implements ActionListener {

        private final TreePath selPath;

        public RefreshListener(TreePath selPath) {
            super();
            this.selPath = selPath;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
            if (node.getUserObject() instanceof ITestData) {
                ITestData data = IPSmallManager.getInstance().reloadTestcase((ITestData) node.getUserObject());
                node.setUserObject(data);
                ((DefaultTreeModel) treeTestcases.getModel()).nodeChanged(node);
            }
        }
    }

    /**
     * Copies a testcase through the context menu.
     */
    private class CopyListener implements ActionListener {

        private final TreePath selPath;

        public CopyListener(TreePath selPath) {
            super();
            this.selPath = selPath;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
            if (node.getUserObject() instanceof ITestData) {
                ITestData data = (ITestData) node.getUserObject();

                ITestData newData = IPSmallManager.getInstance().copyTestCase(data);
				if (newData != null) {
                	IPSmallManager.getInstance().addTestcase(newData);
                
                	// update tree view
                	DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(newData);
                	DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
                	parent.add(newChild);
                	UIUtils.sortTreeNodesAlphaNumeric(parent);
                	((DefaultTreeModel)treeTestcases.getModel()).reload(parent);
				}
            }
        }
    }

    /**
     * Opens system editor (notepad) to edit the selected testcase
     */
    private class EditListener implements ActionListener {

        private final TreePath selPath;

        public EditListener(TreePath selPath) {
            super();
            this.selPath = selPath;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
            if (node.getUserObject() instanceof ITestData) {
                ITestData data = (ITestData) node.getUserObject();
                IPSmallManager.getInstance().editTestCaseInExternalEditor(data);
            }
        }
    }

    /**
     * Loader to load huge log files in an own thread to increase responsiveness
     * of UI.
     */
    private class LogWorker extends SwingWorker<String, String> {

        private MainFrame frame;
        private File file;

        public LogWorker(MainFrame frame, final File f) {
            this.frame = frame;
            file = f;
        }

        @Override
        protected String doInBackground() throws Exception {
            String formatted;
            try {
                String content = FileUtils.getLogContent(file);
                formatted = transformXML(content);
            } catch (IOException ex) {
                formatted =  "Error while loading log file: " + ex.getMessage();
            }
            return formatted;
        }

        private String transformXML(String input) throws Exception {
            TransformerFactory tFactory = TransformerFactory.newInstance();

            Transformer transformer = tFactory.newTransformer(new StreamSource(new File(GlobalSettings.getConfigDir(), GlobalSettings.getLogStyleFileName())));
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "html");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            StringWriter writer = new StringWriter();
            StreamSource in = new StreamSource(new ByteArrayInputStream(input.getBytes()));
            StreamResult out = new StreamResult(writer);

            transformer.transform(in, out);

            return writer.toString();
        }

        @Override
        public void done() {
            try {
                frame.setLogContent(get());
            } catch (Exception ex) {
                frame.setLogContent("Error loading log: " + ex.getMessage());
            }
        }

    }

    /**
     * Must only be called in Event Dispatcher Thread! Adds a new line in the
     * protocol containing the given text
     *
     * @param stepDescr must not be null.
     */
    public void addProtocollTestStep(String stepDescr, int currentSetp, int maxSteps) {
        String protocoll = taProtocol.getText() + stepDescr + System.lineSeparator();
        taProtocol.setText(protocoll);

        if (currentSetp >= 0) {
            proBarTestcase.setValue(currentSetp);
        } else // if value is invalid increment progressbar slowly
        if (proBarTestcase.getValue() < proBarTestcase.getMaximum()) {
            proBarTestcase.setValue(proBarTestcase.getValue() + 1);
        }

        if (maxSteps >= 0) {
            proBarTestcase.setMaximum(maxSteps);
        }
    }

    /**
     * Must only be called in Event Dispatcher Thread!
     */
    public void setTriggerToStart() {
        btnStart.setText(TRIGGER_LABEL_START);
        proBarTestcase.setMaximum(1);
        proBarTestcase.setValue(0);
        lblTaskname.setText("");
        proBarTestcase.setEnabled(false);
    }
}
