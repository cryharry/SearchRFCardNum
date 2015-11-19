import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

class MemberMan extends JFrame implements ActionListener{
	JPanel pNorth,p1,p2,panWest;
	JTextField txtName;
	JButton btnSearch;
	JTable table;
	
	Connection con=null;
	String url="jdbc:sqlserver://218.53.104.76:1433;databaseName=unicool";
	String user="unicool";
	String pwd="unicool";
	PreparedStatement pstmtSelect;
	ResultSet rs;
	String sql="";
	
	public MemberMan() {
		pNorth = new JPanel(new FlowLayout());
		add(pNorth, "North");
		
		//배치 GridLayout(5,1)
		panWest=new JPanel(new GridLayout(2,1));
		//판넬 p1 이름 txtName
		p1=new JPanel();
		p1.add(new JLabel("이름"));
		p1.add(txtName=new JTextField(10));
		panWest.add(p1);
		
		add(panWest,"West");
		//판넬 p5 전체보기 추가 삭제
		p2=new JPanel();
		p2.add(btnSearch=new JButton("검색"));
		add(p2,"South");
		
		setSize(700, 300);
		setVisible(true);
		//이벤트
		btnSearch.addActionListener(this);
		//디비연결메서드
		connectDb();
	}
	public void connectDb(){
		try {
			//1단계
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			//2단계
			con=DriverManager.getConnection(url,user,pwd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void search(){
		try { 
			sql="select st_id,class,ban,num,s_name,old_rf_num,rf_num from card_receipt where bra_code='hudt_h'";
			pstmtSelect=con.prepareStatement(sql);
			//4단계 rs<=실행 저장 
			rs=pstmtSelect.executeQuery();//데이터
			//
			while(rs.next()) {
				table = new JTable();
			}
			//Jtable생성(데이터,열이름) 붙이기
			table=new JTable();
			add(new JScrollPane(table),"Center");
			//화면보이기
			setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
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
