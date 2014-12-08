import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import java.awt.Font;


@SuppressWarnings("serial")
/* �߰��Ǵ� ī�װ��� �ϳ��� Ŭ������ ����. */
public class CategoryPanel extends JPanel implements ActionListener {
	FlowLayout categoryLayout;
	JCheckBox enableChk;
	JTextField categoryName;
	TextField textLevel;
	//static FilterFrame filterFrame;
	private MainFrame mainFrame;  
	JButton removeButton;
	int categoryKey;
	JComboBox<String> levelCombo;
	JPanel checkboxPanel;
	JCheckBox lessThanChk;
	JCheckBox equalChk;

	/* �����Ҷ� Key���� �޾ƿ�, ������ �� ���δ�. */
	public CategoryPanel(MainFrame mainFrame, int receiveKey) {
		// TODO Auto-generated constructor stub
		categoryKey = receiveKey;
		//filterFrame = new FilterFrame();
		this.mainFrame = mainFrame; 
		categoryLayout = (FlowLayout)this.getLayout();
		categoryLayout.setVgap(0);
		categoryLayout.setHgap(10);
		categoryLayout.setAlignment(FlowLayout.LEFT);
		setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		
		/* enable checkbox, üũ���� �Ǹ� AllCheckbox�� üũ���� ��Ų��. */
		enableChk = new JCheckBox("");
		enableChk.addActionListener(this);
		enableChk.setSelected(true);
		add(enableChk);
		categoryName = new JTextField();
		categoryName.setColumns(30);
		add(categoryName);
		add(Box.createRigidArea(new Dimension(15, 0)));

		checkboxPanel = new JPanel();
		add(checkboxPanel);
		checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
		
		lessThanChk = new JCheckBox("<");
		lessThanChk.setSelected(true);
		checkboxPanel.add(lessThanChk);
		
		equalChk = new JCheckBox("=");
		equalChk.setSelected(true);
		checkboxPanel.add(equalChk);

		String level[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
		
		levelCombo = new JComboBox<String>(level);
		add(levelCombo);
		
		/* ������ư�� ������ FilterFrame�� removeCategory �Լ��� ȣ���ϸ鼭 key���� parameter�� �ѱ�. */
		removeButton = new JButton("-");
		removeButton.setFont(new Font("����", Font.PLAIN, 13));
		removeButton.addActionListener(this);
		removeButton.setActionCommand("remove");;
		
		add(removeButton);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getActionCommand() == "remove") {
			mainFrame.removeCategori(categoryKey);
		}
		if (e.getActionCommand() == "uncheckAllCheckbox") {
			mainFrame.unselectAllCheckbox();
		}
		
	}
}