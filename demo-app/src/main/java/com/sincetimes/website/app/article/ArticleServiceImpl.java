package com.sincetimes.website.app.article;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import com.sincetimes.website.core.common.support.LogCore;

@Service
public class ArticleServiceImpl implements ArticleService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public static final ArticleWrapper ARTICLE_WRAPPER = new ArticleWrapper();

	public static class ArticleWrapper implements RowMapper<Article> {
		@Override
		public Article mapRow(ResultSet rs, int rowNum) throws SQLException {
			Article article = new Article();
			article.setId(rs.getInt("id"));
			article.setContent(rs.getString("content"));
			article.setCreate_time(rs.getLong("create_time"));
			article.setImg_url(rs.getString("img_url"));
			article.setLink_url(rs.getString("link_url"));
			article.setRead_num(rs.getInt("read_num"));
			article.setShort_title(rs.getString("short_title"));
			article.setSort(rs.getString("sort"));
			article.setTitle(rs.getString("title"));
			article.setType_id(rs.getString("type_id"));
			article.setUpdate_time(rs.getLong("update_time"));
			article.setUser_id(rs.getInt("user_id"));
			article.setCreated_by(rs.getString("created_by"));
			article.setUpdated_by(rs.getString("updated_by"));
			return article;
		}
	}

	@Override
	public int insertArtical(Article atc) {
		String sql = "INSERT INTO t_article (content, create_time, img_url, link_url, read_num, short_title, sort,title, type_id, update_time, user_id, created_by, updated_by) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		Object[] args = new Object[] { atc.getContent(), atc.getCreate_time(), atc.getImg_url(), atc.getLink_url(),
				atc.getRead_num(), atc.getShort_title(), atc.getSort(), atc.getTitle(), atc.getType_id(),
				atc.getUpdate_time(), atc.getUser_id(), atc.getCreated_by(), atc.getUpdated_by() };
		KeyHolder generatedKeyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement state = con.prepareStatement(sql, new String[] { "id" });
				for (int i = 0; i < args.length; i++)
					state.setObject(i + 1, args[i]);
				return state;
			}
		}, generatedKeyHolder);
		return generatedKeyHolder.getKey().intValue();
	}
	@Override
	public int updateArtical(Article atc) {
		String sql = "update t_article set content=? , create_time=?, img_url=?, link_url=?, read_num=?, short_title=?, sort=?,title=?, type_id=?, update_time=?, user_id=?, created_by=?,  updated_by=? where id=?";
		Object[] args = new Object[] { 
				atc.getContent(), atc.getCreate_time(), atc.getImg_url(), atc.getLink_url(),
				atc.getRead_num(), atc.getShort_title(), atc.getSort(), atc.getTitle(), 
				atc.getType_id(), atc.getUpdate_time(), atc.getUser_id(), 
				atc.getCreated_by(), atc.getUpdated_by(), atc.getId()};
		LogCore.BASE.info("update title:{}", atc.getTitle());
		return jdbcTemplate.update(sql, args);
	}
	
	@Override
	public Article getArticleById(int id) {
		String querySql = "select * from t_article WHERE id =?";
		Object[] args = new Object[] { id };
		try {
			Article a = jdbcTemplate.queryForObject(querySql, args, ARTICLE_WRAPPER);
			LogCore.BASE.info("artical:{}", a);
			return a;
		} catch (Exception e) {
			LogCore.BASE.error("getArticalErr{}, {}", querySql, e);
			return null;
		}
	}

	@Override
	public Map<Integer, Article> getAllArticles() {
		Map<Integer, Article> map = new HashMap<>();
		List<Article> articals = jdbcTemplate.query("select * from t_article", ARTICLE_WRAPPER);
		articals.forEach(a -> map.put(a.getId(), a));
		return map;
	}

	@Override
	public int deleteArticle(int id) {
		return jdbcTemplate.update("DELETE FROM t_article	WHERE id = ?", new Object[] { id });
	}
	
	/**
	 * 如果是自增的ID用new TreeMap<>(Integer::compare)或new TreeMap<>(Comparator.comparing(Integer::intValue));
	 */
	@Override
	public List<ArticleType> getAllArticleTypes() {
		List<ArticleType> types = new ArrayList<>();
		jdbcTemplate.query("select * from t_article_type", 
			(ResultSet rs, int rowNum) -> {
				ArticleType tp = new ArticleType();
				tp.setId(rs.getString("id"));
				tp.setName(rs.getString("name"));
				tp.setCreate_time(rs.getLong("create_time")); 
				tp.setUpdate_time(rs.getLong("update_time"));
				tp.setCreated_by(rs.getString("created_by"));
				tp.setUpdated_by(rs.getString("updated_by"));
				types.add(tp);
			return tp;
		});
		return types;
	}

	@Override
	public int insertArticalType(ArticleType tp) {
		String sql = "REPLACE INTO t_article_type (id, name, create_time, update_time, created_by, updated_by) VALUES(?, ?, ?, ?, ?, ?)";
		Object[] args = new Object[] {tp.getId(), tp.getName(), tp.getCreate_time(), tp.getUpdate_time(), tp.getCreated_by(), tp.getUpdated_by() };
		return jdbcTemplate.update(sql, args);
	}
	@Override
	public int updateArticalType(ArticleType tp) {
		String sql = "update t_article_type set id=?, name=?, create_time=?, update_time=?, create_by=?, update_by=? where id=?";
		Object[] args = new Object[] { 
				tp.getName(),
				tp.getCreate_time(),
				tp.getUpdate_time(),
				tp.getCreated_by(),
				tp.getUpdated_by(),
				tp.getId()};
		return jdbcTemplate.update(sql, args);
	}
	
	@Override
	public int deleteArticleType(String id) {
		return jdbcTemplate.update("DELETE FROM t_article_type WHERE id = ?", new Object[] { id });
	}
}
