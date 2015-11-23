
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.KeyboardFocusManager;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class RFcard2 {
	public final static String url="jdbc:sqlserver://218.53.104.76:1433;databaseName=unicool";
	public final static String user="unicool";
	public final static String pwd="unicool";
	
	Connection con1, con2;
	PreparedStatement pstmt;
	ResultSet rs;
	String sql="";
	
	ArrayList<String> list;
	
	JFrame jFrame = new JFrame("RF카드번호");
	
	DefaultTableModel defaultModel =
			new DefaultTableModel(new String[]{"바코드","학년","반","번호","이름","예전 카드 번호","신규 카드 번호"}, 0) {
		@Override
		public boolean isCellEditable(int row,int cloumn) {
			return false;
		}
	};
	JTable table = new JTable(defaultModel);
	JScrollPane jScrollPane = new JScrollPane(table);
	JTextField txtName = new JTextField(10);
	JButton btnSearch = new JButton("검색");
	JButton btnUpdate = new JButton("변경");
	JPanel jPanel = new JPanel(new FlowLayout());
	
	public RFcard2() {
		table.getTableHeader().setReorderingAllowed(false);
		jPanel.add(new JLabel("이름"));
		jPanel.add(txtName);
		jPanel.add(btnSearch);
		jPanel.add(btnUpdate);
		
		jFrame.setSize(800, 500);
		jFrame.setVisible(true);
		jFrame.setDefaultCloseOperation(jFrame.EXIT_ON_CLOSE);
		
		jFrame.add(jPanel,"North");
		jFrame.add(jScrollPane,"Center");
		txtName.requestFocus();
		txtName.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}
			@Override
			public void keyReleased(KeyEvent e) {
			}
			// 키보드 Enter 이벤트
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == 10) {
					search();
				}
			}
		});
		// 검색버튼 이벤트
		btnSearch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				search();
			}
		});
		btnUpdate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				list = fileRead();
				switch(table.getSelectedRowCount()) {
				case 0:
					JOptionPane.showMessageDialog(jFrame, "선택된 칼럼이 없습니다");
					break;
				case 1:
					String DBurl = "jdbc:sqlserver://"+list.get(0)+":1433;databaseName="+list.get(1);
					String user = "sa";
					String pwd = "unicool";
					try {
						con2 = DriverManager.getConnection(DBurl, user, pwd);
						Statement statement = con2.createStatement();
						String rf = String.valueOf(defaultModel.getValueAt(table.getSelectedRow(), 5));
						String st_id = String.valueOf(defaultModel.getValueAt(table.getSelectedRow(), 0));
						String sql = "UPDATE student SET rf_card_num='"+rf+"' WHERE st_id='"+st_id+"'";
						statement.executeUpdate(sql);
						JOptionPane.showMessageDialog(jFrame, "변경완료");
						txtName.requestFocus();
						dbClose();
					} catch (Exception err) {
						JOptionPane.showMessageDialog(jFrame, err.getMessage());
					}
					break;
				default:
					JOptionPane.showMessageDialog(jFrame, "하나의 컬럼만 선택해주세요");
					break;
				}
			}
		});
	}
	
	public ArrayList<String> fileRead() {
		list = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader("C:/Uni_Cool/KCM_IP.DAT"));
			String ip = br.readLine();
			String license = br.readLine();
			list.add(ip);
			list.add(license);
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public void dbClose() {
		if(con1!=null)try{con1.close();}catch (Exception e) {}
		if(rs!=null)try{rs.close();}catch(Exception e){}
		if(pstmt!=null)try{pstmt.close();}catch(Exception e){}
		sql = "";
	}
	
	public void search(){
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			con1 = DriverManager.getConnection(url, user, pwd);
			
			sql="select st_id,class,ban,num,s_name,old_rf_num,rf_num from card_receipt where bra_code='hudt_h' and s_name like ?";
			pstmt=con1.prepareStatement(sql);
			pstmt.setString(1, '%'+txtName.getText().trim()+'%');
			//4단계 rs<=실행 저장 
			rs=pstmt.executeQuery();//데이터
			//
			ResultSetMetaData rsMetaData = rs.getMetaData();
			
			Object [] tempObject = new Object[rsMetaData.getColumnCount()];
			
			defaultModel.setRowCount(0);
			
			while(rs.next()) {
				for(int i=0; i < rsMetaData.getColumnCount(); i++) {
					tempObject[i] = rs.getString(i+1);
				}
				defaultModel.addRow(tempObject);
			}
			
			if(defaultModel.getRowCount() > 0) {
				table.setRowSelectionInterval(0, 0);
			}
		} catch (ClassNotFoundException e) {
			JOptionPane.showMessageDialog(jFrame, e.getMessage());
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(jFrame, e.getMessage());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(jFrame, e.getMessage());
		} finally {
			dbClose();
		}
	}
	public static void main(String[] args) {
		new RFcard2();
	}
		
}
