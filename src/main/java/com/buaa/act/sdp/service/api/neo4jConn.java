package com.buaa.act.sdp.service.api;

import org.springframework.stereotype.Component;

import java.sql.*;

/**
 * Created by YLT on 2017/3/28.
 */
@Component
public class neo4jConn {



    String login = "neo4j";
    String password = "123456";

    public Connection getTry() {
        Connection con = null;
        try {
            con = DriverManager.getConnection("jdbc:neo4j:http://192.168.7.109:7474/",login,password);


            /*String cypher = "MATCH (n:User{handle:{1}}) SET n.skillDegree = {2}";
            PreparedStatement stmt = con.prepareStatement(cypher);
            stmt.setString(1,"ksladkov");
            stmt.setString(2,"{\"studno\":\"11111\",\"studname\":\"wwww\",\"studsex\":\"男\"}");
            stmt.executeUpdate();*/
          /*  try (Statement stmt = con.createStatement()) {
                String user = "ksladkov";
                String skill = "lll";
                String cypher = "MATCH (n:User{handle:{1}}) SET n.skillDegree = {2}";
              //  String cypher = "MATCH (n:User{handle:'" + user + "'})SET n.skillDegree ='" + skill+"'";
                ResultSet rs = stmt.executeQuery(cypher);
                while (rs.next()) {
                    System.out.println(rs.getString("n.country"));
                }
            }
            con.close();*/
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
        return con;
    }
}
