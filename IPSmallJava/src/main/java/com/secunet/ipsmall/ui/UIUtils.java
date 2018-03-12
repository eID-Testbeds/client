package com.secunet.ipsmall.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.secunet.ipsmall.IPSmallManager;

public class UIUtils {
    
    public static void expandAll(final JTree tree, final boolean expand) {
        TreeNode root = (TreeNode) tree.getModel().getRoot();
        tree.cancelEditing();
        expandAll(tree, new TreePath(root), expand);
    }
    
    private static void expandAll(final JTree tree, final TreePath parent, final boolean expand) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path, expand);
            }
        }
        
        if (expand)
            tree.expandPath(parent);
        else
            tree.collapsePath(parent);
    }
    
    /**
     * not tail recursive!
     * 
     * @param node
     *            must not be null
     * @return the node with sorted children
     */
    public static DefaultMutableTreeNode sortTreeNodesAlphaNumeric(final DefaultMutableTreeNode node) {
        List<String> keys = new ArrayList<String>(node.getChildCount());
        HashMap<String, DefaultMutableTreeNode> childMap = new HashMap<String, DefaultMutableTreeNode>(node.getChildCount());
        
        for (int i = 0; i < node.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
            String key = child.getUserObject().toString();
            keys.add(key);
            childMap.put(key, child);
            
            sortTreeNodesAlphaNumeric(child);
        }
        
        Collections.sort(keys);
        
        node.removeAllChildren();
        for (String key : keys) {
            node.add(childMap.get(key));
        }
        
        // put folders first
        // for (int i = 0; i < node.getChildCount() - 1; i++) {
        // DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
        // for (int j = i + 1; j <= node.getChildCount() - 1; j++) {
        // DefaultMutableTreeNode prevNode = (DefaultMutableTreeNode) node.getChildAt(j);
        //
        // if (!prevNode.isLeaf() && child.isLeaf()) {
        // node.insert(child, j);
        // node.insert(prevNode, i);
        // }
        // }
        // }
        
        return node;
    }
    
    public static ImageIcon scaleImageIconTo(ImageIcon source, int height, int width) {
        return new ImageIcon(source.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }
    
    public static void centerFrameRelativistic(final JFrame frameToCenter, final JFrame orientationFrame) {
        centerFrameTo(frameToCenter, orientationFrame.getSize());
    }
    
    public static void centerFrameTo(final JFrame frameToCenter, final Dimension orientDim) {
        Dimension frameSize = frameToCenter.getSize();
        int x = (orientDim.width - frameSize.width) / 2;
        int y = (orientDim.height - frameSize.height) / 2;
        frameToCenter.setLocation(x, y);
    }
    
    public static void centerFrame(final JFrame frameToCenter) {
        centerFrameTo(frameToCenter, Toolkit.getDefaultToolkit().getScreenSize());
    }
    
    public static ImageIcon createImageIcon(final String path) {
        return new ImageIcon(UIUtils.class.getResource(path));
    }
    
    public static void showInfoDialog(String message) {
        JTextArea area = new JTextArea(message);
        area.setBackground((Color) UIManager.get("OptionPane.messageBackground"));
        area.setTabSize(2);
        // area.setColumns(40);
        // area.setEditable(false);
        // area.setLineWrap(true);
        // area.setWrapStyleWord(true);
        area.setSize(area.getPreferredSize().width, area.getPreferredSize().height);
        
        JOptionPane.showMessageDialog(IPSmallManager.getInstance().getMainFrame(), area, "", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void showInfoDialogs(List<String> messages) {
        if ((messages == null) || (messages.size() == 0)) {
            return;
        }
        for (String message : messages) {
            showInfoDialog(message);
        }
    }
    
    /**
     * Save the expansion state of a tree.
     *
     * @param tree
     * @return expanded tree path as Enumeration
     */
    public static Enumeration<TreePath> saveExpansionState(JTree tree) {
        return tree.getExpandedDescendants(new TreePath(tree.getModel().getRoot()));        
    }
    
    public static ArrayList<String> saveExpansionStateStrings(JTree tree) {
        ArrayList<String> result = new ArrayList<String>();
    	Enumeration<TreePath> expansionState = tree.getExpandedDescendants(new TreePath(tree.getModel().getRoot()));
        if (expansionState != null) {
            while (expansionState.hasMoreElements()) {
                TreePath treePath = (TreePath) expansionState.nextElement();
                result.add(treePath.toString());
            }
        }
        return result;
    }
    
    
    public static TreePath[] saveTreeSelection(JTree tree) {
    	return tree == null ? null : tree.getSelectionPaths();
    }
    
    public static TreePath[] getUpdatedTreePath(TreePath[] paths, JTree tree){
    	if( paths == null || tree == null ){
    		return null;
    	}
    	
    	TreeNode root = (TreeNode) tree.getModel().getRoot();
    	ArrayList<TreePath> result = new ArrayList<TreePath>();
    	for (int i = 0; i < paths.length; i++) {
			TreePath updatedPath = findTreePath(new TreePath(root), root, getTreePathString(paths[i]));
			if( updatedPath != null ){
				result.add(updatedPath);
			}
		}
    	return result.toArray(new TreePath[result.size()]);
    }
    
    private static String getTreePathString(TreePath path){
    	return path.toString();
    }
    
    private static TreePath findTreePath(TreePath path, TreeNode node, String pathName){
    	if( path == null || pathName == null || node == null ){
    		return null;
    	}
    	
    	if( pathName.equals(getTreePathString(path))){
    		return path;
    	}
    	
    	if (node.getChildCount() >= 0) {
            for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
                TreeNode child = (TreeNode) e.nextElement();
                TreePath childPath = path.pathByAddingChild(child);
                TreePath childMatch = findTreePath(childPath, child, pathName);
                if( childMatch != null ){
                	return childMatch;
                }
            }
        }
    	
    	return null;
    }
    
    /*
    public static ArrayList<String> saveTreeSelection(JTree tree) {
    	ArrayList<String> result = new ArrayList<String>();
    	TreePath[] treePaths = tree.getSelectionPaths();
    	if( treePaths != null ){
    		for (TreePath treePath : treePaths) {
				result.add(treePath.getPath());
			}
    	}
    	return result;
    }
    */

    /**
     * Restore the expansion state of a JTree.
     * Note: this will NOT work for most use-cases in IPSmallJava as data-model of tree is reset after most tasks.
     *
     * @param tree
     * @param expansionState an Enumeration of expansion state. You can get it using {@link #saveExpansionState(javax.swing.JTree)}.
     */
    public static void loadExpansionState(JTree tree, Enumeration<TreePath> expansionState) {
        if (expansionState != null) {
            while (expansionState.hasMoreElements()) {
                TreePath treePath = (TreePath) expansionState.nextElement();
                tree.expandPath(treePath);
                
            }
        }
    }
    
    public static void expandByExpansionStateStrings(JTree tree, ArrayList<String> expansionStateStrings) {
        TreeNode root = (TreeNode) tree.getModel().getRoot();
        tree.cancelEditing();
        expandByExpansionStateStrings(tree, new TreePath(root), expansionStateStrings);
    }
    
    private static void expandByExpansionStateStrings(final JTree tree, final TreePath parent, ArrayList<String> expansionStateStrings) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandByExpansionStateStrings(tree, path, expansionStateStrings);
            }
        }
        
        if (isTreePathContainedInExpansionState(parent, expansionStateStrings))
        {
        	tree.expandPath(parent);
        }
        else
        {
        	tree.collapsePath(parent);
        }
    }
    
    private static boolean isTreePathContainedInExpansionState(TreePath treePath, ArrayList<String> expansionStateStrings){
    	if( treePath != null && expansionStateStrings != null){
    		return expansionStateStrings.contains(treePath.toString());
        }
    	return false;
    }
    
}
