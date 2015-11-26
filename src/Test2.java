import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

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

import com.microsoft.sqlserver.jdbc.SQLServerException;

class MemberMan extends JFrame implements ActionListener{
	JPanel pNorth,p1,p2,panWest;
	JTextField txtName;
	JButton btnSearch;
	JComboBox combo;
	JTable table;
	
	Connection con=null;
	String url="jdbc:sqlserver://218.53.104.76:1433;databaseName=unicool";
	String user="unicool";
	String pwd="unicool";
	PreparedStatement pstmtSelect, pstmtRow, pstmtBranch, pstmtCombo;
	ResultSet rsCount, rsSearch, rsBranch, rsCombo;
	String brName = "";
	String sql="";
	
	public MemberMan() {
		pNorth = new JPanel(new FlowLayout());
		combo = new JComboBox<>();
		con = connectDb();
		sql = "select sch_name from branch where s_yno='Y'";
		try {
			pstmtBranch = con.prepareStatement(sql);
			rsBranch = pstmtBranch.executeQuery();
			while(rsBranch.next()) {
				combo.addItem(rsBranch.getString(1));
			}
		} catch(SQLException e2) {
			
		} finally {
			if(rsBranch != null) try{rsBranch.close();}catch(Exception e){} 
			if(pstmtBranch != null) try{pstmtBranch.close();}catch(Exception e){}
		}
		pNorth.add(combo);
		pNorth.add(new JLabel("이름"));
		pNorth.add(txtName = new JTextField(10));
		pNorth.add(btnSearch=new JButton("검색"));
		add(pNorth, "North");
		
		setSize(800, 500);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		//이벤트
		btnSearch.addActionListener(this);
		combo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sql = "SELECT license FROM BRANCH WHERE sch_name=?";
				try {
					pstmtCombo = con.prepareStatement(sql);
					pstmtCombo.setString(1, combo.getSelectedItem().toString());
					rsCombo = pstmtCombo.executeQuery();
					if(rsCombo.next()) {
						brName = rsCombo.getString(1);
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				} finally {
					if(rsCombo!=null)try{rsCombo.close();}catch(Exception e2){}
					if(pstmtCombo!=null)try{pstmtCombo.close();}catch(Exception e2){}
				}
				
				combo.getSelectedItem().toString();
			}
		});
	}
	public Connection connectDb(){
		try {
			//1단계
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			//2단계
			con=DriverManager.getConnection(url,user,pwd);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return con;
	}
	public void search(){
		try {
			DefaultTableModel defaultModel =
					new DefaultTableModel(new String[]{"바코드","학년","반","번호","이름","예전 카드 번호","신규 카드 번호"}, 0) {
				@Override
				public boolean isCellEditable(int row,int cloumn) {
					return false;
				}
			};
			sql = "select count(st_id) from card_receipt where bra_code='"+brName+"'";
			pstmtRow = con.prepareStatement(sql);
			rsCount = pstmtRow.executeQuery();
			int result = 0;
			if(rsCount.next()) {
				result = Integer.valueOf(rsCount.getString(1));
			}
			
			sql="select st_id,class,ban,num,s_name,old_rf_num,rf_num from card_receipt where bra_code='"+brName+"' and s_name like ?";
			pstmtSelect=con.prepareStatement(sql);
			pstmtSelect.setString(1, '%'+txtName.getText()+'%');
			//4단계 rs<=실행 저장 
			rsSearch=pstmtSelect.executeQuery();//데이터
			//
			ResultSetMetaData rsMetaData = rsSearch.getMetaData();
			
			Object [] tempObject = new Object[rsMetaData.getColumnCount()];
			
			defaultModel.setRowCount(0);
			
			while(rsSearch.next()) {
				for(int i=0; i < rsMetaData.getColumnCount(); i++) {
					tempObject[i] = rsSearch.getString(i+1);
				}
				defaultModel.addRow(tempObject);
			}
			table=new JTable(defaultModel);
			
			if(defaultModel.getRowCount() > 0) {
				table.setRowSelectionInterval(0, 0);
			}
			//Jtable생성(데이터,열이름) 붙이기
			add(new JScrollPane(table),"Center");
			//화면보이기
			setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rsSearch!=null)try{rsSearch.close();}catch(Exception e){}
			if(rsCount!=null)try{rsCount.close();}catch(Exception e){}
			if(pstmtSelect!=null)try{pstmtSelect.close();}catch(Exception e){}
			if(pstmtRow!=null)try{pstmtRow.close();}catch(Exception e){}
			sql = "";
		}
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		search();
	}
	
}

public class Test2 {
	public static void main(String[] args) {
		MemberMan m=new MemberMan();
	}
}
