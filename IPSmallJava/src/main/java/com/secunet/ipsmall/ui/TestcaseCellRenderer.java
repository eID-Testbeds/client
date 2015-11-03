package com.secunet.ipsmall.ui;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.secunet.ipsmall.IPSmallManager;
import com.secunet.ipsmall.test.ITestData;

public class TestcaseCellRenderer extends DefaultTreeCellRenderer {
    
    private ImageIcon passed = null;
    private ImageIcon failed = null;
    private ImageIcon running = null;
    private ImageIcon undetermined = null;
    
    private static final long serialVersionUID = 1138507170920260535L;
    
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
            boolean leaf, int row, boolean hasFocus) {
        Component comp = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        
        if (value instanceof DefaultMutableTreeNode
                && ((DefaultMutableTreeNode) value).getUserObject() instanceof ITestData) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            ITestData data = (ITestData) node.getUserObject();
            setText(data.getTestName());
            
            try {
                if (failed == null || passed == null || running == null || undetermined == null) // lazy
                                                                         // load
                {
                    failed = UIUtils.scaleImageIconTo(
                            UIUtils.createImageIcon("/com/secunet/ipsmall/ui/icons/failed.png"), 16, 16);
                    passed = UIUtils.scaleImageIconTo(
                            UIUtils.createImageIcon("/com/secunet/ipsmall/ui/icons/passed.png"), 16, 16);
                    running = UIUtils.scaleImageIconTo(
                            UIUtils.createImageIcon("/com/secunet/ipsmall/ui/icons/running.png"), 16, 16);
                    undetermined = UIUtils.scaleImageIconTo(
                            UIUtils.createImageIcon("/com/secunet/ipsmall/ui/icons/undetermined.png"), 16, 16);
                }
                
                switch (IPSmallManager.getInstance().getProject().getState(data.getTestName())) {
                    case Failed:
                        setIcon(failed);
                        setDisabledIcon(failed);
                        break;
                    case Passed:
                        setIcon(passed);
                        setDisabledIcon(passed);
                        break;
                    case Running:
                        setIcon(running);
                        setDisabledIcon(running);
                        break;
                    case Undetermined:
                        setIcon(undetermined);
                        setDisabledIcon(undetermined);
                        break;
                    default:
                        // use system default icon
                }
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
        }
        
        return comp;
    }
}
