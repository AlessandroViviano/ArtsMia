package it.polito.tdp.artsmia.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.artsmia.model.Adiacenza;
import it.polito.tdp.artsmia.model.ArtObject;

public class ArtsmiaDAO {

	public void listObjects(Map<Integer, ArtObject> idMap) {
		
		String sql = "SELECT * from objects";

		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				
				if(!idMap.containsKey(res.getInt("object_id"))) {
					ArtObject artObj = new ArtObject(res.getInt("object_id"), res.getString("classification"), res.getString("continent"), 
							res.getString("country"), res.getInt("curator_approved"), res.getString("dated"), res.getString("department"), 
							res.getString("medium"), res.getString("nationality"), res.getString("object_name"), res.getInt("restricted"), 
							res.getString("rights_type"), res.getString("role"), res.getString("room"), res.getString("style"), res.getString("title"));
					idMap.put(artObj.getId(), artObj);
				}
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int getpeso(ArtObject a1, ArtObject a2) {
		
		String sql = "SELECT COUNT(*) AS peso " + 
				"FROM exhibition_objects AS eo1, exhibition_objects eo2 " + 
				"WHERE eo1.exhibition_id = eo2.exhibition_id AND " + 
				"eo1.object_id = ? AND eo2.object_id = ?";
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, a1.getId());
			st.setInt(2, a2.getId());
			
			ResultSet rs = st.executeQuery();
			if(rs.next()) {
				int peso = rs.getInt("peso");
				conn.close();
				return peso;
			}
			conn.close();
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public List<Adiacenza> getAdiacenze(){
		
		String sql = "SELECT eo1.object_id AS obj1, eo2.object_id AS obj2, COUNT(*) AS peso " + 
				"FROM exhibition_objects AS eo1, exhibition_objects AS eo2 " + 
				"WHERE eo1.exhibition_id = eo2.exhibition_id AND " + 
				"eo2.object_id > eo1.object_id " + 
				"GROUP BY eo1.object_id, eo2.object_id";
		
		List<Adiacenza> adiacenze = new ArrayList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			while(rs.next()) {
				adiacenze.add(new Adiacenza(rs.getInt("obj1"), rs.getInt("obj2"), rs.getInt("peso")));
			}
			conn.close();
			return adiacenze;
		}catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
