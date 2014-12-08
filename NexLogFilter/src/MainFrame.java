import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.transform.TransformerConfigurationException;


@SuppressWarnings("serial")
public class MainFrame extends JFrame implements KeyListener, ActionListener, Runnable {

	private CategoryPanel categoryPanel;
	private FileSetterGetter fileSetterGetter;
	private XmlReaderWriter xmlReadWrite;
	private Utils utils;
	private JPanel fullPanel;
	private JPanel allCategoryPanel;
	private JPanel checkboxPanel;
	private JPanel categoryListPanel;
	private JPanel categoryItemsPanel;
	private Box categoryListBox;	
	private HashMap<Integer, CategoryPanel> categoryMap;
	private JLabel filePathLabel;
	private JLabel savedPathLabel;
	private JLabel exampleLabel;
	private JTextField separatorStart;
	private JTextField separatorEnd;
	private JTextField separatorMid;
	private JCheckBox allCheckbox;
	private JCheckBox lessThanCheckBox;
	private JCheckBox equalCheckBox;
	private JComboBox<String> levelCombo;
	private JButton saveFileBtn;
	private JProgressBar parsingProgress;
	
	private static MainFrame mainFrame;
	private static final int categoriWidth = 326;
	private static final int categoriHight = 40;
	private static int categoryKey = 0;
	
	private static final String TITLE = "Log Filter";
	private static final String FILE_NAME = "File Name";
	private static final String APPLIES_ALL = " Applies to all";
	private static final String OPEN = "Open";
	private static final String SAVE_PATH = "Saved Path";	
	private static final String level[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
	
	private static final String ACT_FILE_OPEN = "fileOpen";
	private static final String ACT_GET_PATH = "getPath";
	private static final String ACT_UNCHECK_ALL = "uncheckAllCheckbox";
	private static final String ACT_APPLIES = "appliesAll";
	private static final String ACT_ADD_CTGR = "createCategory";
	private static final String ACT_SAVE = "searchNSave";
	
	private static String destPath = System.getProperty("user.dir");
	private static String searchExample ="Ex.) ";
	
	public MainFrame(String title) {
		// TODO Auto-generated constructor stub
		super(title);
		xmlReadWrite = new XmlReaderWriter();
		utils = new Utils();
	}

	public void showGUI() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // ���������� �����ϱ� ���� �޼ҵ�
		getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.X_AXIS));
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		setIconImage(toolkit.getImage("LogFilterIcon.png")); // ������ �߰�
		
		fullPanel = new JPanel();
		getContentPane().add(fullPanel);
		fullPanel.setLayout(null);
		
		filePathLabel = new JLabel(FILE_NAME);
		filePathLabel.setBounds(131, 10, 468, 25);
		fileSetterGetter = new FileSetterGetter();
		
		/* File open button */
		JButton btnOpen = new JButton(OPEN);
		btnOpen.setActionCommand(ACT_FILE_OPEN);
		btnOpen.addActionListener(this);
		btnOpen.setBounds(12, 10, 107, 23);
		fullPanel.add(btnOpen);
		fullPanel.add(filePathLabel);
		
		setDropTarget(new DropTarget() {

			public synchronized void drop(DropTargetDropEvent evt) {
				try {

					evt.acceptDrop(DnDConstants.ACTION_COPY);
					@SuppressWarnings("unchecked")
					List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
					String fileName = droppedFiles.get(0).getName();
					String extensionName = null;
					/* Drag and Drop�� ���� �޾ƿ� ������ Ȯ���ڸ� ���ؼ� txt, log������ �ƴ϶�� �˾��� ������, �´ٸ� File��ü�� ����� �д�. */
					if (fileName.lastIndexOf('.') >= 0){
						extensionName = fileName.substring(fileName.lastIndexOf('.')+1);
					}
					if (null != extensionName && (extensionName.equals("txt") || extensionName.equals("log") || extensionName.equals("TXT") || extensionName.equals("LOG"))){
						setFile(droppedFiles.get(0));
						checkSaveFileBtn();
					} else {
						JOptionPane.showMessageDialog(null,
			                    "Only '.txt', '.log' file can be read!", "Message",
			                    JOptionPane.INFORMATION_MESSAGE);
					}
					
				} catch (Exception ex){
					ex.printStackTrace();
				}
			}
		});
		
		savedPathLabel = new JLabel(destPath);
		savedPathLabel.setBounds(131, 43, 468, 15);
		fullPanel.add(savedPathLabel);
		
		/* Path ���� ��ư, ������ Path �� �޾ƿ����� ������ */
		JButton savedPathButton = new JButton(SAVE_PATH);
		savedPathButton.addActionListener(this);
		savedPathButton.setActionCommand(ACT_GET_PATH);
		savedPathButton.setBounds(12, 39, 107, 23);
		fullPanel.add(savedPathButton);
		
		JLabel categoryStartLbl = new JLabel("Category Separator - Begining");
		categoryStartLbl.setBounds(12, 95, 174, 15);
		fullPanel.add(categoryStartLbl);
		
		/* ����, �߰�, �� �����ڿ� KeyListener�� �޾Ƽ� �۾��� �����ų� ������ ��, �ٷ� ���ù��� ��Ÿ������ ��.
		 * ������ TextField �� key�� ��������, keyReleased() �Լ��� ȣ���.  */
		separatorStart = new JTextField();
		separatorStart.setDocument(new JTextFieldLimit(1)); // 1���ڸ� �Էµǵ��� ��.
		separatorStart.setBounds(187, 92, 35, 21);
		separatorStart.addKeyListener(this);
		fullPanel.add(separatorStart);
		separatorStart.setColumns(10);
		
		JLabel endLbl = new JLabel("End");
		endLbl.setBounds(234, 95, 35, 15);
		fullPanel.add(endLbl);
		
		separatorEnd = new JTextField();
		separatorEnd.setDocument(new JTextFieldLimit(1));
		separatorEnd.setColumns(10);
		separatorEnd.setBounds(260, 92, 35, 21);
		separatorEnd.addKeyListener(this);
		fullPanel.add(separatorEnd);
		
		
		JLabel middleLbl = new JLabel("Middle");
		middleLbl.setBounds(307, 95, 46, 15);
		fullPanel.add(middleLbl);
		
		separatorMid = new JTextField();
		separatorMid.setDocument(new JTextFieldLimit(1));
		separatorMid.setColumns(10);
		separatorMid.setBounds(351, 92, 35, 21);
		separatorMid.addKeyListener(this);
		fullPanel.add(separatorMid);
		
		/* categoryListPanel - All category �� ���� category���� ���ΰ� �ִ� Panel */
		categoryListPanel = new JPanel();
		categoryListPanel.setBounds(12, 123, 587, 458);
		categoryListPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED),"Category"));
		fullPanel.add(categoryListPanel);
		categoryListPanel.setLayout(new BorderLayout(0, 0));
		
		/* allCategoryPanel - allCheckbox, lessThan/equal Checkbox, levelCombobox�� ��� �ִ�. */
		allCategoryPanel = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) allCategoryPanel.getLayout();
		flowLayout_2.setVgap(0);
		flowLayout_2.setHgap(10);
		allCategoryPanel.setSize(279, 25);

		categoryListPanel.add("North", allCategoryPanel);
		
		/* allCheckbox, üũ�Ǹ� ��� ī�װ��� Ȱ��ȭ �Ǹ�, allCategory�ȿ� �ִ� ������� ��� ����ȴ�. üũ ������ ������ ��� ��Ȱ��ȭ. */
		allCheckbox = new JCheckBox(APPLIES_ALL);
		allCheckbox.setFont(new Font("����", Font.BOLD, 14));
		allCheckbox.addActionListener(this);
		allCheckbox.setActionCommand(ACT_APPLIES);
		allCategoryPanel.add(allCheckbox);
		allCategoryPanel.add(Box.createRigidArea(new Dimension(250, 0))); // ������ �Ѷ� ����.
		
		checkboxPanel = new JPanel();
		checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
		allCategoryPanel.add(checkboxPanel);
		
		// allCategory �ȿ� �ִ� "<" checkbox, �̰��� ������, allCheckbox�� üũ���� �ǵ��� �Ͽ���. allCategory�� �ٸ� checkbox�� ���������̴�.
		lessThanCheckBox = new JCheckBox("<");
		lessThanCheckBox.addActionListener(this);
		lessThanCheckBox.setActionCommand(ACT_UNCHECK_ALL);
		checkboxPanel.add(lessThanCheckBox);
		
		equalCheckBox = new JCheckBox("=");
		equalCheckBox.setActionCommand(ACT_UNCHECK_ALL);
		equalCheckBox.addActionListener(this);
		checkboxPanel.add(equalCheckBox);
		
		levelCombo = new JComboBox<String>(level);
		levelCombo.setActionCommand(ACT_UNCHECK_ALL);
		levelCombo.addActionListener(this);
		allCategoryPanel.add(levelCombo);
		
		/* '+' Button�� ���� ������ �ϳ��� ī�װ��� ������. ī�װ� �ϳ��� Ŭ���� ��ü �ϳ�. */
		JButton addCategoryBtn = new JButton("+");
		addCategoryBtn.setActionCommand(ACT_ADD_CTGR);
		addCategoryBtn.addActionListener(this);
		allCategoryPanel.add(addCategoryBtn);
		
		categoryItemsPanel = new JPanel(); // Category���� ���ΰ� �ִ� Panel
		FlowLayout flowLayout = (FlowLayout) categoryItemsPanel.getLayout();
		flowLayout.setVgap(3);
		categoryItemsPanel.setSize(categoriWidth, 279);
		
		JScrollPane categoryScrollPane = new JScrollPane(categoryItemsPanel); // Scroll�� ����
		categoryListPanel.add(categoryScrollPane);
		
		categoryListBox = Box.createVerticalBox(); // Category���� ���δ� Box�߰�, categoryItemsPanel �ȿ� ���Ե�.
		categoryListBox.setSize(categoriWidth, 279);
		
		categoryMap = new HashMap<Integer, CategoryPanel>();
		fullPanel.add(categoryListPanel);
		
		System.out.println("categoryKey : "+categoryKey+", categoryMap.size() : "+categoryMap.size());
		
		/* Progress Bar */
		parsingProgress = new JProgressBar(); // Progress Bar ����, Parsing�ϰ�, Save�� �� �� ���̰� �ȴ�.
		parsingProgress.setBounds(22, 591, 468, 15);
		parsingProgress.setStringPainted(true);
		parsingProgress.setVisible(false);
		fullPanel.add(parsingProgress);
		
		/* Save Button */
		saveFileBtn = new JButton("Save File");
		saveFileBtn.setActionCommand(ACT_SAVE);
		saveFileBtn.addActionListener(this);
		saveFileBtn.setBounds(502, 591, 97, 28);
		fullPanel.add(saveFileBtn);
		
		exampleLabel = new JLabel(searchExample); // �˻��� String ���� ���ù���.
		exampleLabel.setBounds(22, 589, 288, 15);
		fullPanel.add(exampleLabel);

		setXmlValues();	// xml���Ͽ��� �о�� ������ �����ͼ� �� Component�� Setting ���ش�. 
		showSearchExample(); // ���ù����� �������� �Ǵ��Ͽ� �����ش�.
		checkSaveFileBtn(); // Save Button�� Ư�����ǿ��� Ȱ��ȭ �����ִ� Method.
		setSize(627, 674);
		setVisible(true);
		
		/* ������ ���¿� ���� ȣ��Ǵ� WindowListener �̴�. */
		addWindowListener(new WindowListener() {

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				try {
					/* �� Component��� ���� ������ �޾ƿ��� */
					xmlReadWrite.setOpenFile(fileSetterGetter.getPath());
					xmlReadWrite.setSavedPath(savedPathLabel.getText());
					xmlReadWrite.setSeparatorB(separatorStart.getText());
					xmlReadWrite.setSeparatorE(separatorEnd.getText());
					xmlReadWrite.setSeparatorM(separatorMid.getText());
					
					System.out.println("finalize() OpenFile-" + fileSetterGetter.getPath() + ", savedPath-" + savedPathLabel.getText()
							+ ", separatorB-"+separatorStart.getText()+", separatorE-"+separatorEnd.getText() + ", separatorM-"+separatorMid.getText());
					
					/* all Category�� "enable|lessThan|equal|level"(T|F|T|4) ���·� ����� Setting�Ѵ�. */
					String allCategoryValues = boolToString(allCheckbox.isSelected()) + "|"
							+ boolToString(lessThanCheckBox.isSelected()) + "|"
							+ boolToString(equalCheckBox.isSelected()) + "|"
							+ levelCombo.getSelectedItem();
					xmlReadWrite.setAllCateValues(allCategoryValues);
					
					System.out.println("finalize() allCategoryValues-"+allCategoryValues);
					
					/* Map<Integer, CategoryPanel>���� ����Ǿ� �ִ� ���� Category�� ���� ������ ArrayList<String>�� �����Ѵ�. */
					ArrayList<String> categoryArray = new ArrayList<String>(); 
					Collection<CategoryPanel> collection = categoryMap.values();
					Iterator<CategoryPanel> iterator = collection.iterator();
					while (iterator.hasNext()) {
						CategoryPanel categoryPanel = iterator.next();
						// category ������ �̷� "enable(bool)|name(string)|lessThan(bool)|equal(bool)|level(string)"(T|ENGN|T|F|5) ������ ��Ʈ������ ����ȴ�.
						String categoryValues = boolToString(categoryPanel.enableChk.isSelected()) + "|"
								+ categoryPanel.categoryName.getText() + "|"
								+ boolToString(categoryPanel.lessThanChk.isSelected()) + "|"
								+ boolToString(categoryPanel.equalChk.isSelected()) + "|"
								+ categoryPanel.levelCombo.getSelectedItem();
						categoryArray.add(categoryValues); // �� ī�װ��� �ϳ��� ��Ʈ������ ����.
						System.out.println("finalize() categoryValues-"+categoryValues);
					}
					xmlReadWrite.setCategoryArray(categoryArray);
					xmlReadWrite.setWriteXml();
				} catch (TransformerConfigurationException exception) {
					// TODO Auto-generated catch block
					exception.printStackTrace();
					dispose(); // �� Try�� �ȿ��� Exeption �߻��ϸ� printing �� �׳� ������.
				}
				dispose(); // �� ���� ������ â ����.
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}			
		});
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		JFileChooser fileChooser;
		/* Open button�� ������ �� ����Ǵ� Action. */
		if (e.getActionCommand() == ACT_FILE_OPEN) { 
			fileChooser = new JFileChooser(fileSetterGetter.getPath());
			FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("Text file", "txt", "log");
			fileChooser.setFileFilter(fileFilter);
			int result = fileChooser.showDialog(this, null);
			if(result == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				setFile(file);
				checkSaveFileBtn();
			}
		/* Saved Path button�� ������ �� ����Ǵ� Action */
		} else if (e.getActionCommand() == ACT_GET_PATH) {
			fileChooser = new JFileChooser(destPath);
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileChooser.showDialog(this, null);
			File file = fileChooser.getSelectedFile();
			if (file.exists()) {
				destPath = file.getPath();
				savedPathLabel.setText(file.getPath());
			}
			
		} else if (e.getActionCommand() == ACT_UNCHECK_ALL) {
			
			unselectAllCheckbox();
			
		} else if (e.getActionCommand() == ACT_APPLIES) {
			if (allCheckbox.isSelected()) { // allCheckbox�� �������� ��,
				if (categoryMap.size() != 0) { // �ϴ� �߰��� ī�װ��� �ִ��� Ȯ���Ѵ�. ������ ī�װ��� �߰��϶�� Dialog�� ������.
					// ������ Parameter�� �޾ƿ� ��� ī�װ��� �����ϰ�, ��� üũ�Ѵ�.
					utils.allSelect(categoryMap, equalCheckBox.isSelected(), lessThanCheckBox.isSelected(), levelCombo.getSelectedItem()); 
					allCheckbox.setSelected(true);
				} else {
					JOptionPane.showMessageDialog(this, "There is no category to select. please add category.");
					allCheckbox.setSelected(false);
				}
			} else {
				Collection<CategoryPanel> collection = categoryMap.values();
				Iterator<CategoryPanel> iterator = collection.iterator();
				while (iterator.hasNext()){
					iterator.next().enableChk.setSelected(false);
				}
			}
		} else if (e.getActionCommand() == ACT_ADD_CTGR) {
			if(categoryMap.size() <= 19){ // ī�װ� ������ 20���� ������ ����� ���ٰ� Dialog ����. 
				createCategory(); // ī�װ� ���� Method. 
				allCheckbox.setSelected(false); // allCheckbox üũ���� �ǵ��� ��.
				checkSaveFileBtn(); // Save Button Ȱ��ȭ ��ų�� ���� �������ִ� Method.
				showSearchExample(); // Search ������ �����ִ� Method.
			} else {
				JOptionPane.showMessageDialog(this, "Categories can not be more than 20.");
			}
			setVisible(true);  // �� Method�� ���� ������, Frame ȭ���� ���ŵ��� �ʴ´�.
		} else if (e.getActionCommand() == ACT_SAVE) {
			boolean check = utils.settingSearchWords(categoryMap, separatorStart.getText(), separatorEnd.getText(), separatorMid.getText());
			if (check) { // �� �Լ�����, category �鿡 �̸��� ���ų� üũ�� ���� ���� ��, false�� �����Ѵ�. 
				disableAllComponents(); // Progress Bar�� ������ ��� ������Ʈ���� ��Ȱ��ȭ ��Ű��,
				utils.parseAndSave(fileSetterGetter.getFile(), destPath, parsingProgress, mainFrame); // �˻��� ������ �����ؼ� �����Ѵ�.
			} else {
				JOptionPane.showMessageDialog(this, "Can not create file!");
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		showSearchExample();
	}
	
	/* ī�װ� ���� Method. ī�װ��� Key���� �ο����ش�. �׸��� Map�� �̿��� Key, CategoryPanel��ü�� �����Ѵ�. 
	 * �Ʒ��� '+' Button�� �������� ȣ��Ǵ� �޼ҵ� */
	public void createCategory() {
		categoryKey ++; // Key �� ����. 1 ���� �����Ѵ�.
		categoryPanel = new CategoryPanel(this, categoryKey); // categoryPanel ��ü �����Ҷ� Key���� �ο��Ѵ�.
		categoryPanel.setSize(categoriWidth, categoriHight);
		categoryPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		categoryMap.put(categoryKey, categoryPanel); // categoryMap�� Key���� categoryPanel��ü�� �־��ش�.
		System.out.println("Press Button categoryKey : "+categoryKey+", categoryMap.size : "+categoryMap.size());
		categoryListBox.add(categoryPanel); // categoryPanel��ü�� categoryBox�� �߰���Ű��
		categoryItemsPanel.add(categoryListBox); // categoryBox�� categoryItemsPanel�� �߰���Ų��.
	}
	
	/* �� Method�� ���� ���������, �Ʒ� Method�� xml�� �Ľ��Ͽ� category�� ���鶧 ȣ��Ǵ�  Method �̴�. */
	public void createCategory(ArrayList<String> categoryArray) {
		for (int i = 0 ; i < categoryArray.size() ; i++) {
			categoryKey ++;
			String[] categoryValues = categoryArray.get(i).split("\\|"); // CategoryArray�� ����� String�� '|'�������� �ɰ��� �迭�� �����Ѵ�. 
			if (categoryValues.length == 5) { // '|'�� �������� �ڸ� String��  5���� ���� ������ ī�װ��� ������ �ʴ´�.
				categoryPanel = new CategoryPanel(this, categoryKey);
				categoryPanel.setSize(categoriWidth, categoriHight);
				categoryPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
				
				/* ������ ������ categoryPanel ��ü���� ������ setting. ������ �κ� ���� �ִ� Method�� ����. */
				categoryPanel.enableChk.setSelected(stringToBool(categoryValues[0]));
				categoryPanel.categoryName.setText(categoryValues[1]);
				categoryPanel.lessThanChk.setSelected(stringToBool(categoryValues[2]));
				categoryPanel.equalChk.setSelected(stringToBool(categoryValues[3]));
				categoryPanel.levelCombo.setSelectedItem(categoryValues[4]);
				
				categoryMap.put(categoryKey, categoryPanel);
				categoryListBox.add(categoryPanel);
				categoryItemsPanel.add(categoryListBox);
				System.out.println("Press Button categoryKey : "+categoryKey+", categoryMap.size : "+categoryMap.size());
			}
		}
	}
	
	/*��� ������Ʈ���� ��Ȱ��ȭ ��Ŵ - Progress bar ������ ȣ����.*/
	public void disableAllComponents(){
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		parsingProgress.setVisible(true);
		
		Collection<CategoryPanel> collection = categoryMap.values();
		Iterator<CategoryPanel> iterator = collection.iterator();
		exampleLabel.setVisible(false);
		while (iterator.hasNext()){
			CategoryPanel categoryPanel = iterator.next();
			for (Component c : categoryPanel.checkboxPanel.getComponents()) {
				c.setEnabled(false);
			}
			for (Component c : categoryPanel.getComponents()) {
				c.setEnabled(false);
			}
		}
		for (Component c : checkboxPanel.getComponents()) {
			c.setEnabled(false);
		}
		for (Component c : fullPanel.getComponents()) {
			c.setEnabled(false);
		}
		for (Component c : allCategoryPanel.getComponents()) {
			c.setEnabled(false);
		}
	}
	
	/*��� ������Ʈ���� Ȱ��ȭ ��Ŵ - Progress bar ���� �� ȣ����.*/
	public void enableAllComponents(){
		setCursor(null);
		parsingProgress.setVisible(false);
		
		for (Component c : fullPanel.getComponents()) {
			c.setEnabled(true);
		}
		for (Component c : allCategoryPanel.getComponents()) {
			c.setEnabled(true);
		}
		for (Component c : checkboxPanel.getComponents()) {
			c.setEnabled(true);
		}
		exampleLabel.setVisible(true);
		Collection<CategoryPanel> collection = categoryMap.values();
		Iterator<CategoryPanel> iterator = collection.iterator();
		
		while (iterator.hasNext()) {
			CategoryPanel categoryPanel = iterator.next();
			for (Component c : categoryPanel.getComponents()) {
				c.setEnabled(true);
			}
			for (Component c : categoryPanel.checkboxPanel.getComponents()) {
				c.setEnabled(true);
			}
		}
	}
	
	/* xml���Ͽ��� ������ ������ �� component�鿡�� setting���ִ� Method. */
	public void setXmlValues() {
		if (xmlReadWrite.parseXml()) { // xml ������ ������ false, ������ ������ set���ְ� true ��ȯ. 
			if (null != xmlReadWrite.getOpenFile()) { // Open�� ���ϸ��� null���� Ȯ��.
				setFile(new File(xmlReadWrite.getOpenFile())); // file setting.(��ȿ�� ���������� ȣ���� �Լ� �ȿ��� Ȯ�� �Ѵ�.)
			}
			if ((new File(xmlReadWrite.getSavedPath()).exists())) { // �����ΰ� �����ϴ��� Ȯ��.
				destPath = xmlReadWrite.getSavedPath(); // ������ Setting
			}
			savedPathLabel.setText(destPath);
			separatorStart.setText(xmlReadWrite.getSeparatorB());
			separatorEnd.setText(xmlReadWrite.getSeparatorE());
			separatorMid.setText(xmlReadWrite.getSeparatorM());
			if (null != xmlReadWrite.getAllCateValues()) { // AllCategory ������ null���� Ȯ��.
				String[] allCateValArray = xmlReadWrite.getAllCateValues().split("\\|"); // '|'�������� �ɰ��� String�迭�� ����.
				if ( allCateValArray.length == 4) { // �迭���̰� 4�� �ƴϸ� �� setting���� �ʴ´�.
					lessThanCheckBox.setSelected(stringToBool(allCateValArray[1]));
					equalCheckBox.setSelected(stringToBool(allCateValArray[2]));
					levelCombo.setSelectedItem(allCateValArray[3]);
					allCheckbox.setSelected(stringToBool(allCateValArray[0]));
				}
			}
			// �޾ƿ� categoryArray�� ���� null�� �ƴ��� Ȯ�� �Ŀ�, category���� �����Ѵ�.
			if (null != xmlReadWrite.getCategoryArray()){
				createCategory(xmlReadWrite.getCategoryArray());
			}
		}
	}
	
	
	/* category �� ���� �� �� ȣ��Ǵ� Method, CategoryPanel�ȿ� '-'��ư�� ������ �Ʒ��� Method�� ȣ��ȴ�. 
	 * categoryPanel ��ü�� �����Ҷ� �ο��Ǿ��� Key���� Parameter�� �ѱ��. Key���� ���� ������ �ȴ�. */
	public void removeCategori(int receivedKey){
		categoryListBox.remove(categoryMap.get(receivedKey)); // Box�ȿ� �ִ� categoryPanel�� ����
		categoryMap.remove(receivedKey); // Map�ȿ����� �����.
		this.setVisible(true); // Frame ����
		showSearchExample(); // ���ù��� ����� ���� �ִ��� Ȯ��.
		System.out.println("categoryMap.size : "+categoryMap.size()+", categoryNum : "+receivedKey);
	}
	
	/* Save Button�� Open�� ���� ���翩��, cateogy������ ���� Ȱ��ȭ �����ִ� Method. */
	public void checkSaveFileBtn() {
		if ((fileSetterGetter.settingFile == null) || (categoryMap.size() == 0)) {
			saveFileBtn.setEnabled(false);
		} else {
			saveFileBtn.setEnabled(true);
		}
	}
	
	/* File�� ���翩�θ� �Ǵ��� File�� Setting ���ִ� Method. */
	public void setFile(File file) {
		if (file.exists()) {
			fileSetterGetter.setFile(file);
			setTitle(TITLE+" - "+fileSetterGetter.getPath());
			filePathLabel.setText(fileSetterGetter.getName());
		}
	}
	
	/* ���ñ����� ���ǿ� ���� �����ִ� Method */
	public void showSearchExample() {
		String testString = "TEST";
		String testNum = "0";
		if (categoryMap.size() > 0) { // category�� ���� �� �����ش�.
			exampleLabel.setVisible(true);
			/* �߰������ڰ� ��/���� ���� ���ù����� �ٸ���. Search��ĵ� �ٸ���. 
			 * �߰� �����ڰ� ���� �� - ���۱����� + ī�װ� �̸� + �߰������� + �ƹ����� + �߰������� + ���� + ������������ [ENGN:*:0] 
			 * �߰� �����ڰ� ���� �� - ���۱����� + ī�װ� �̸� + ���� + ������ ������ [FLW3]*/
			if (0 < separatorMid.getText().length()) {
				exampleLabel.setText(searchExample + separatorStart.getText() + testString + separatorMid.getText() + "*"
						+ separatorMid.getText() + testNum + separatorEnd.getText());
			} else {
				exampleLabel.setText(searchExample + separatorStart.getText() + testString + testNum + separatorEnd.getText());
			}
		} else {
			exampleLabel.setVisible(false);
		}
	}
	
	/* String T/F �� boolean ���� �����ϴ� Method */
	public boolean stringToBool(String trueFalse) {
		return ("T".equals(trueFalse));
	}
	
	/* boolean�� String T/F �� �����ϴ� Method */
	public String boolToString(boolean trueFalse) {
		return (trueFalse ? "T" : "F");
	}
	
	public void unselectAllCheckbox() {
		if (allCheckbox.isSelected()) {
			allCheckbox.setSelected(false);
		}
	}
	
//	public MainFrame getCompnentFrame() {
//		return mainFrame;
//	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		showGUI();
	}
	
	public static void main(String[] args) {
		mainFrame = new MainFrame(TITLE);
		SwingUtilities.invokeLater(mainFrame);
	}

}
