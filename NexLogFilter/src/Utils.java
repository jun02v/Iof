import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;


public class Utils implements PropertyChangeListener {
	public HashMap<Integer, CategoryPanel> categoryMap;
	public ArrayList<String> searchArray = null;
	public JProgressBar parsingProgress;
	private MainFrame mainFrame;
	
	/* allSelect - ��� ī�װ����� Enable ��Ű��, All category�� ������ ���� �����Ų��. categoryMap�� All category�� ���������� �޴´�. */
	public void allSelect (HashMap<Integer, CategoryPanel> paramMap, boolean equalsChkbox, boolean lessthanChkbox, Object selectLevel) {
		Collection<CategoryPanel> collection = paramMap.values();
		Iterator<CategoryPanel> iterator = collection.iterator();
		while(iterator.hasNext()){
			CategoryPanel categoryPanel = iterator.next();
			categoryPanel.enableChk.setSelected(true);
			categoryPanel.equalChk.setSelected(equalsChkbox);
			categoryPanel.lessThanChk.setSelected(lessthanChkbox);
			categoryPanel.levelCombo.setSelectedItem(selectLevel);
		}
	}
	
	/* settingSearchWords Category�� �ִ� ������� ���� ���Ͽ��� ã������ String���� �����, ArrayList�� ���´�. */
	public boolean settingSearchWords (HashMap<Integer, CategoryPanel> paramMap, String separatorStart, String separatorEnd, String separatorMid) {
		searchArray = new ArrayList<String>();
		Collection<CategoryPanel> collection = paramMap.values();
		Iterator<CategoryPanel> iterator = collection.iterator();
		int checkSelected = 0;
		while(iterator.hasNext()){
			CategoryPanel categoryPanel = (CategoryPanel) iterator.next();
			if (categoryPanel.enableChk.isSelected() && categoryPanel.categoryName.getText().trim().length() != 0) { // Enable Checkbox�� üũ �Ǿ���, ī�װ����� ����ִ��� Ȯ��.
				System.out.println("Utils 48 : categoryPanel.categoryName.getText().trim().length() : "+categoryPanel.categoryName.getText().trim().length());
				String searchString;
				if (separatorMid.trim().length() == 0) { 
					searchString = separatorStart + categoryPanel.categoryName.getText(); // �߰������ڰ� ���ٸ�, "[FLW" ���� String���� �����,
				} else {
					searchString = separatorStart + categoryPanel.categoryName.getText() + separatorMid + "-" + separatorMid; // " �ִٸ�, [ENGN:-:" ���� String���� �����,
				}
				int parseInt = Integer.parseInt((String) categoryPanel.levelCombo.getSelectedItem()); // level���� int������ ��ȯ�Ѵ���,
				if (categoryPanel.lessThanChk.isSelected() && parseInt > 0) { // lessthan �� üũ �Ǿ� �ְ�, level�� 0 �̻��� ���,
					int j = 0;
					do { // 0���� level��-1 ��ŭ ���鼭,
						searchString += Integer.toString(j) + separatorEnd;// "[ENGN:-:1]" �Ǵ� "[FLW1]" �����ִ� ���� ������ �����ڸ� ������� String�� �ٿ��� , 
						searchArray.add(searchString); // ArrayList<String>�� �߰��Ѵ�.
						System.out.println("Utils 61 : "+searchString);
						// String���� ������ ������ ���� + level����(1) ��ŭ �ڸ���."[ENGN:-:", "[FLW"
						searchString = searchString.substring(0, searchString.length()-separatorEnd.length()-1); 
						j++;
					} while (j < parseInt); // �ݺ��Ѵ�.
				}
				if (categoryPanel.equalChk.isSelected()) { // equal Combobox�� üũ�Ǿ��ִ� �� Ȯ���� ��, 
					searchString += Integer.toString(parseInt) + separatorEnd; // ������ ������� level���� ������ ������ �����ڿ� �Բ� �ٿ��ش�.
					searchArray.add(searchString); // List�� �߰�.
					System.out.println("Utils 70 : "+searchString);
				}
			} else {
				checkSelected++; // Category enable Checkbox�� üũ ���� �Ǿ��ְų�, ī�װ����� ���°��� Ȯ���Ͽ�,   
				System.out.println("checkSelected:"+checkSelected);
				if (checkSelected == collection.size() ){ // ���ī�װ��� �� �׷��ٸ�, False�� �����Ѵ�.
					return false;
				}
			}
		}
		System.out.println("Utils:80 collection.size()"+collection.size());
		return true;
	}
	
	/* ������ �˻��ϰ� �����Ҷ� ���� ó�� ȣ��Ǵ� �޼ҵ�.
	 * �˻��� ����, ������, Progress bar�� Parameter�� �޴´�.
	 * ������ �����ϸ鼭, Progress bar�� �����ϱ� ���ؼ���,
	 * Background���� ���ư��� Class�� �����, ó�����࿡ ���� Progress Bar��  ������� �Ѵ�. 
	 */
	public void parseAndSave (File file, String destPath, JProgressBar progressBar, MainFrame mainFrame) {
		parsingProgress = progressBar;
		ParseTask parseTask = new ParseTask(file, destPath);
		this.mainFrame = mainFrame;
		parseTask.addPropertyChangeListener(this);
		parseTask.execute();
	}

	/* Progress event �� �ޱ����� Method. */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		if ("progress" == evt.getPropertyName()) {
			int progress = (Integer) evt.getNewValue();
			parsingProgress.setValue(progress);
		}
		
	}
	
	/* SwingWorker�� ��ӹ��� Ŭ����, Thread ó�� ��׶��忡�� ������, UI������ Message�� ����ټ� �ִ�. 
	 * Background���� ��Ʈ���� Search�ϸ鼭 ���� ������� �˷��ش�.
	 * Search / File Write ���� �� ������ �� �Ŀ� done Method�� ȣ��Ǹ鼭 ������. 
	 */
	class ParseTask extends SwingWorker<Void, Void> {
		
		File readFile = null;
		String destPath = null;
		
		ParseTask(File file, String path){
			readFile = file;
			destPath = path;
		}

		/* ���� Background���� ���� Method */
		@Override
		protected Void doInBackground() throws Exception {
			// TODO Auto-generated method stub
			setProgress(0); // Progress 0���� ����
			int progress;
			long fileLength = readFile.length(); // ���� ��üũ�⸦ Byte������ �޾ƿ´�.
			try {
				/* ProgressInputStream - FilterInputStream�� ��ӹ޾� ���� Ŭ����, ������ ������, �� Ŭ���� �ȿ� read�Լ��� ȣ��Ǵµ�,
				 * read �� ������ byte������ counting�Ͽ�, ��ü ũ��� ������� counting�� ���� ���� ����������� �˼� �ִ�.*/
				ProgressInputStream pis = new ProgressInputStream(new FileInputStream(readFile), fileLength);
				BufferedReader bufferReader = new BufferedReader(new InputStreamReader(pis));
				BufferedWriter bufferWriter = new BufferedWriter(new FileWriter(destPath+"\\Parsing_"+readFile.getName().split("\\.")[0]+"_"+System.currentTimeMillis()+".txt"));
				String readLine = null;
				System.out.println("START : "+System.currentTimeMillis());
				boolean isMiddleSep = searchArray.get(0).contains("-"); // '-'�� �ִ� ���� �߰������� �� �ִ� ���̰�, ���°��� �߰������ڰ� ���� ���̴�.
				while ((readLine = bufferReader.readLine()) != null) { // BufferedReader�� 1 line�� �д´�.
					progress = (int)(pis.getProgress() * 100.0); // ���� �� �ۼ�Ʈ read�ߴ��� �޾ƿ�.
					for (int i = 0 ; i < searchArray.size() ; i++) { // readLine�� ArrayList�� ������ String��� ����. ArrayList������ŭ �ݺ�.
						if (isMiddleSep) { // �߰� �����ڰ� �ִ��� üũ,
							String splitSearch[] = searchArray.get(i).split("-"); // "[ENGN:-:0]"�̷� �������� �Ǿ��ִ� String�� "-" �������� �ڸ�.
							if (readLine.contains(splitSearch[0]) && readLine.contains(splitSearch[1])) { // "[ENGN:", ":0]"�� ���ԵǾ� �ִ��� Ȯ���Ͽ� write��., 
								bufferWriter.write(readLine+"\n");
							}
						} else { // �߰� �����ڰ� ���� ���� �ϳ��� String���� ã���� ��."[FLW3]"
							if (readLine.contains(searchArray.get(i))) {
								bufferWriter.write(readLine+"\n");
							}
						}						
					}
					setProgress(progress); // �� line�а��� progress Set�� ��.
				}
				bufferWriter.flush();
				bufferReader.close();
				bufferWriter.close();
				System.out.println("END : "+System.currentTimeMillis());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		/* �۾��� �Ϸ�� �Ŀ� ȣ��Ǵ� Method */
		@Override
		protected void done() {
			// TODO Auto-generated method stub
			if (!isCancelled()){ // �������� �ʾҴٸ�, Success�ߴٰ� Dialog�� �˷���.
				JOptionPane.showMessageDialog(null,
	                    "File created successfully!", "Message",
	                    JOptionPane.INFORMATION_MESSAGE);
			}
			setProgress(0); // �ٽ� progress�� 0
//			FilterFrame filterFrame = new FilterFrame();
//			filterFrame.enableAllComponents(); // ��� ������Ʈ�� enable��Ŵ.
//			filterFrame.showSearchExample(); // ���ù����� ������.
			
			mainFrame.enableAllComponents(); // ��� ������Ʈ�� enable��Ŵ.
			mainFrame.showSearchExample(); // ���ù����� ������.
			super.done();
		}
	}
}
