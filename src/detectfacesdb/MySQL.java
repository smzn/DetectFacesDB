package detectfacesdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQL {
	String driver;// JDBCドライバの登録
    String server, dbname, url, user, password;// データベースの指定
    Connection con;
    Statement stmt;
    ResultSet rs;
    
	public MySQL() {
		this.driver = "org.gjt.mm.mysql.Driver";
		this.server = "ms000.sist.ac.jp";
		this.dbname = "ms000";
		this.url = "jdbc:mysql://" + server + "/" + dbname + "?useUnicode=true&characterEncoding=UTF-8";
		this.user = "xxx";
		this.password = "yyy";
		try {
			this.con = DriverManager.getConnection(url, user, password);
			this.stmt = con.createStatement ();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getDetectFaces() { 
		String sql = "SELECT * FROM detectfaces WHERE fear IS NULL";
		ResultSet rs;
		try {
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				int id = rs.getInt("id");
				String bucketname = rs.getString("bucketname");
				String filename = rs.getString("filename");
				//DetectFaces_lib呼び出し
				System.out.println("DetectFaces id = "+id+", bucketname = "+bucketname+", filename = "+filename);
				DetectFaces_lib dlib = new DetectFaces_lib(bucketname, filename, id);
				dlib.getDetectFaces();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateDetectFaces(int id, int agelow, int agehigh, String smile_value, String glasses_value, int gender_number, double happy, double angry, double confused, double calm, double surprised, double sad, double disgusted, double fear){
		System.out.println("DetectFaces : Insert開始");
        try{
        	StringBuffer buf = new StringBuffer();
			buf.append("UPDATE detectfaces SET agelow = "+agelow+", agehigh = "+agehigh+", smile = "+smile_value +", eyeglasses = "+glasses_value +", gender = "+gender_number+", happy ="+happy+", angry = "+angry+",confused = "+confused+",calm = "+calm+",surprised = "+surprised+",sad = "+sad+",disgusted = "+disgusted+",fear = "+fear+" where id = "+id);
            String sql = buf.toString();
            stmt.execute (sql);
        }
        catch (SQLException e) {
                e.printStackTrace();
        }
        System.out.println("DetectFaces : Insert完了");
	}
	
	
	/*
	public void insertSimulationL(double L[][], int time){//L[C][K]
		System.out.println("Simulation平均系内人数L : Insert開始");
        try{
        	StringBuffer buf = new StringBuffer();
			buf.append("INSERT INTO simulations(combination_id, transition_id, class_id, node_id, L,time) VALUES");
        	for(int i = 0; i < L.length; i++){
        		for(int j = 0; j < L[0].length; j++) {
        			if(i == L.length -1 && j == L[0].length -1) 
        				buf.append("("+combination_id+","+transition_id+","+i+","+j+","+L[i][j]+","+time+")");
        			else buf.append("("+combination_id+","+transition_id+","+i+","+j+","+L[i][j]+","+time+"),");
        		}
            }
            String sql = buf.toString();
            stmt.execute (sql);
        }
        catch (SQLException e) {
                e.printStackTrace();
        }
        System.out.println("Simualtion平均系内人数L : Insert完了");
	}*/

}
