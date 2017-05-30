package com.sincetimes.website.app.article;

import java.sql.Connection;
import java.sql.ResultSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.sincetimes.website.core.common.support.LogCore;
/***
 * 	<pre>
   finally{  
      JdbcUtils.closeConnection(con);
      JdbcUtils.closeResultSet(rs); 
   }
   </pre> 
 */
@Service
public class ArticleTableCreateService{

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public Long checkTables() {
		long updateNum = 0;
		if(!existTable("t_article_type")){
			updateNum += createTableArticleType();
		}
		if(!existTable("t_article")){
			updateNum += createTableArticle();
		}
		return updateNum;
	}
	private boolean existTable(String tableName){
        try(Connection con = jdbcTemplate.getDataSource().getConnection();      
            ResultSet rs = con.getMetaData().getTables(null, null, tableName, new String[]{ "TABLE" })){ 
            if (rs.next()) {    
                return true;    
            }    
        } catch (Exception e) {    
        	LogCore.BASE.error("existTable{}, {}", tableName, e);   
        }
        return false; 
	}
	private long createTableArticleType() {
		String sql = "CREATE TABLE `t_article_type` ( `id` varchar(22) NOT NULL, `name` varchar(100) NOT NULL, `create_time` bigint(13) DEFAULT NULL, `update_time` bigint(13) DEFAULT NULL, `created_by` varchar(22) DEFAULT NULL, `updated_by` varchar(22) DEFAULT NULL, PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8";
		return jdbcTemplate.update(sql);
	}
	private long createTableArticle() {
		String sql = "CREATE TABLE `t_article` ("
				+"`id` int(11) NOT NULL AUTO_INCREMENT,"
				+"`title` varchar(200) NOT NULL,"
				+"`content` mediumtext,"
				+"`create_time` bigint(13) DEFAULT NULL,"
				+"`user_id` int(11) NOT NULL,"
				+"`read_num` int(11) DEFAULT NULL,"
				+"`type_id` varchar(22) DEFAULT NULL,"
			    +"`img_url` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '图片连接地址',"
			    +"`update_time` bigint(13) DEFAULT NULL,"
				+"`short_title` varchar(100) DEFAULT NULL COMMENT '短标题',"
				+"`sort` varchar(1) DEFAULT NULL,"
				+"`referenceurl` varchar(1) DEFAULT '0',"
				+"`ismobile` varchar(1) DEFAULT '0',"
				+"`descriptionArticle` varchar(500) DEFAULT NULL,"
				+"`link_url` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '新闻连接',"
				+"`created_by` varchar(22) DEFAULT NULL COMMENT '创建者',"
				+"`updated_by` varchar(22) DEFAULT NULL COMMENT '修改者',"
			    +"PRIMARY KEY (`id`),"
				+"UNIQUE KEY `idx_id_title` (`id`,`title`),"
				+"KEY `NewIndex1test` (`title`)"
				+") ENGINE=InnoDB AUTO_INCREMENT=69 DEFAULT CHARSET=utf8";
		return jdbcTemplate.update(sql);
	}

}
