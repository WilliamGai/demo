package com.sincetimes.website.app.article;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sincetimes.website.core.common.manager.ManagerBase;
import com.sincetimes.website.core.common.manager.annotation.ManangerInject;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.Result;
import com.sincetimes.website.core.common.support.Util;
import com.sincetimes.website.manager.DataManager;
@ManangerInject
@Component
public class ArticleManager extends ManagerBase {
	private static Map<Integer,Article> ARTICLE_MAP = new HashMap<>();
	private static Map<String, ArticleType> ARTICLE_TYPE_MAP = new LinkedHashMap<>();//ordered
	
	private static final String KEY_ARTICLE_BROWES_NUM = "article_browes_num";

	@Autowired
	ArticleService articleService;
	
	public static ArticleManager inst() {
		return ManagerBase.inst(ArticleManager.class);
	}

	/* 优化项, 费时操作 */
	@Override
	public void init() {
		reloadAll();
	}

	public void reloadAll() {
		reloadArticles();
		reloadArticleTypes();
		LogCore.BASE.info("articleManager inited, articleTypes.size={}, articles.size={}",ARTICLE_TYPE_MAP.size(), ARTICLE_MAP.size());
	}
	/**
	 * 初始化标题名称和阅读人数
	 */
	public static Article initArticle(Article art){
		String type_name = Util.toNullDefalut(ARTICLE_TYPE_MAP.get(art.getType_id()), ArticleType::getName, art.getType_id()+"");
		art.setType_name(type_name);
		art.setRead_num(DataManager.inst().getCountByDb(KEY_ARTICLE_BROWES_NUM, art.getId()));
		return art;
	}
	public Map<Integer,Article> getArticles(){
		return ARTICLE_MAP;
	}
	
	public Map<String, ArticleType> getArticleTypes(){
		return ARTICLE_TYPE_MAP;
	}
	
	public Map<Integer,Article> reloadArticles(){
		return ARTICLE_MAP = articleService.getAllArticles();
	}
	/**
	 * 使用Collectors.toMap这样的方式构建Map,key可以存null,value永远没有机会存入null。
	 * @See {@link Map#merge(Object, Object, BiFunction)}
	 */
	public void reloadArticleTypes(){
		List<ArticleType> types = articleService.getAllArticleTypes();//取出所有
		types.sort(Comparator.comparing(ArticleType::getCreate_time));//根据创建时间排序
		LogCore.BASE.info("reloadArticleTypes result:{}", types);
		ARTICLE_TYPE_MAP = types.stream().collect(Collectors.toMap(ArticleType::getId, Function.identity(), (oldValue, newValue)->newValue, LinkedHashMap::new));
	}
	/*普通查阅文章*/
	public Article getArticleWithInit(Integer id){
		Article art =  ARTICLE_MAP.get(id);
		if(null != art) initArticle(art);
		return art;
	}
	/*客户端阅读,阅读次数+1*/
	public Article getArticleWithRead(Integer id){
		Article art =  ARTICLE_MAP.get(id);
		if(null != art) {
			art.setType_name(Objects.toString(ARTICLE_TYPE_MAP.get(art.getType_id()).getName(), ""+art.getType_id()));
			art.setRead_num(incrReadNum(art.getId()));
		}
		return art;
	}
	private int incrReadNum(Integer id){
		return DataManager.inst().incrCountByDb(KEY_ARTICLE_BROWES_NUM, id);
	}
	public Article getArticle(Integer id){
		return ARTICLE_MAP.get(id);
	}
	
	public Article getArticleNotNullById(Integer id){
		Article atc =  ARTICLE_MAP.get(id);
		return atc == null?new Article() : atc;
	}
	/** article的id为0说明为新增 */
	public Result saveOrUpdateArticles(Article atc){
		if(0 == atc.getId()){
			int generateId = articleService.insertArtical(atc);
			atc.setId(generateId);
		}else{
			articleService.updateArtical(atc);
		}
		ARTICLE_MAP.put(atc.getId(), atc);
		return new Result().success();
	}

	/**
	 * TODO 删除标志
	 */
	public Result deleteArticle(Integer id) {
		if(null == ARTICLE_MAP.remove(id)){
			return new Result().failure().setDesc("article not exist");
		}
		if(0 == articleService.deleteArticle(id)){
			return new Result().failure().setDesc("db delete fail");
		}
		return new Result().success();
	}

	public void saveOrUpdateArticleType(ArticleType articleType) {
		ARTICLE_TYPE_MAP.put(articleType.getId(), articleType);
		articleService.insertArticalType(articleType);
	}
	
	public void deleteArticleType(String type_id) {
		ARTICLE_TYPE_MAP.remove(type_id);
		articleService.deleteArticleType(type_id);
	}
}