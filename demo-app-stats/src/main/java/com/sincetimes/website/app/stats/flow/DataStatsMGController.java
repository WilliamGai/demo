package com.sincetimes.website.app.stats.flow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sincetimes.website.app.security.interfaces.SecureControllerInterface;
import com.sincetimes.website.app.stats.flow.builder.Builder;
import com.sincetimes.website.app.stats.flow.builder.ExcelBuilder;
import com.sincetimes.website.app.stats.flow.builder.TextBuilder;
import com.sincetimes.website.core.common.support.LogCore;
import com.sincetimes.website.core.common.support.Util;
@RequestMapping("/mg")
@Controller
@Order(value = 5)
public class DataStatsMGController implements SecureControllerInterface {
	private static final int DEFAULT_PAGE_SIZE_20000 = 20000;
	@RequestMapping("/tables")
	@ResponseBody
	public Object tables() {
		return DataStatsManager.inst().tables();
	}
	@RequestMapping("/dbs")
	@ResponseBody
	public Object dbs() {
		return DataStatsManager.inst().databases();
	}
	@RequestMapping("/tst")
	@ResponseBody
	public Object tst(String tableName) {
		return DataStatsManager.inst().tst(tableName);
	}
	@RequestMapping("/test")
	@ResponseBody
	public Object test(String id) {
		DataStatsManager.inst().testAll(id);
		return "test done";
	}
	@RequestMapping("/clear")
	@ResponseBody
	public Object clear(String id) {
		return DataStatsManager.inst().clear(id);
	}
	@RequestMapping("/stats")
	public Object stats(Model model, String id) {
		if(Util.isEmpty(id)){
			Optional<String> randomId = DataStatsManager.inst().getAllConfigKeys().stream().findFirst();
			id = randomId.orElse(id);
		}
		snapShotBaseView(model, id);
		return "stats";
	}
	@RequestMapping("/stats_config")
	public Object stats_config( HttpServletRequest req, Model model, String id) {
		setUser(model, req);
		snapShotBaseView(model, id);
//		Collection<DataStatsConfig>  configs = DataStatsManager.inst().getAllConfigs();
//		DataStatsConfig config = DataStatsManager.inst().getConfig(id);
//		LogCore.BASE.info("all configs={}", configs);
//		model.addAttribute("configs", configs);
//		model.addAttribute("config", config);
//		model.addAttribute("type_list", FilterType.values());
//		LogCore.BASE.info("type_list.size()={}, type_list={}",FilterType.values().length, FilterType.values());
//		for(FilterType i:FilterType.values()){
//			LogCore.BASE.info("FilterType={}, id={}", i.name, i.id);
//		}
		return "stats_config";
	}
	@RequestMapping("/add_stats_config")
	public void stats_config(HttpServletResponse resp, String id, String name, String tableName, String incrName, String sumName) {
		DataStatsConfig config = new DataStatsConfig(id, name);
		if(!Util.isEmpty(incrName)){
			config.setIncrName(incrName);
		}
		if(!Util.isEmpty(tableName)){
			config.setTableName(tableName);
		}
		if(!Util.isEmpty(sumName)){
			config.setSumName(sumName);
		}
		DataStatsManager.inst().saveOrUpdateConfig(config);
		redirect(resp, "stats_config?id=" + id);
	}
	@RequestMapping("/config_alter")
	public void config_alter(HttpServletResponse resp, String id, String tableName, String incrName, String sumName) {
		if(Util.isEmpty(id)){
			redirect(resp, "stats_config");
			return;
		}
		DataStatsConfig config = DataStatsManager.inst().getConfig(id);

		if(!Util.isEmpty(incrName)){
			config.setIncrName(incrName);
		}
		if(!Util.isEmpty(tableName)){
			config.setTableName(tableName);
		}
		if(!Util.isEmpty(sumName)){
			config.setSumName(sumName);
		}
		DataStatsManager.inst().saveOrUpdateConfig(config);
		redirect(resp, "stats_config?id=" + id);
	}
	@RequestMapping("/config_filter_add")
	public void config_filter_add(HttpServletResponse resp, String id, byte type_id, String columnName, String desc) {
		if(Util.isEmpty(columnName)){
			redirect(resp, "stats_config?id=" + id);
			return;
		}
		DataStatsConfig config = DataStatsManager.inst().getConfig(id);
		config.putFilterItem(new FilterItem(columnName, desc, FilterType.getType(type_id)));
		DataStatsManager.inst().saveOrUpdateConfig(config);
		redirect(resp, "stats_config?id=" + id);
	}
	@RequestMapping("/config_filter_del")
	public void config_filter_del(HttpServletResponse resp, String id, String key) {
		if(Util.isEmpty(key)){
			redirect(resp, "stats_config?id=" + id);
			return;
		}
		DataStatsConfig config = DataStatsManager.inst().getConfig(id);
		config.removeFilterItem(key);
		DataStatsManager.inst().saveOrUpdateConfig(config);
		redirect(resp, "stats_config?id=" + id);
	}
	@RequestMapping("/config_column_add")
	public void config_column_add(HttpServletResponse resp, String id, byte type_column_id, String columnName, String desc) {
		if(Util.isEmpty(columnName)){
			redirect(resp, "stats_config?id=" + id);
			return;
		}
		DataStatsConfig config = DataStatsManager.inst().getConfig(id);
		config.putColumnItem(columnName, new ColumnItem(columnName, desc, ColumnItemType.getType(type_column_id)));
		DataStatsManager.inst().saveOrUpdateConfig(config);
		redirect(resp, "stats_config?id=" + id);
	}

	@RequestMapping("/config_column_del")
	public void config_column_del(HttpServletResponse resp, String id, String columnName) {
		if(Util.isEmpty(columnName)){
			redirect(resp, "stats_config?id=" + id);
			return;
		}
		DataStatsConfig config = DataStatsManager.inst().getConfig(id);
		config.removeColumnItem(columnName);
		DataStatsManager.inst().saveOrUpdateConfig(config);
		redirect(resp, "stats_config?id=" + id);
	}
	@RequestMapping("/config_column_group_add")
	public void config_column_group_add(HttpServletResponse resp,byte groupTypeId, String id, String columnName, String desc) {
		if(!Util.nonEmpty(id, columnName, desc)){
			redirect(resp, "stats_config?id=" + id);
			return;
		}
		DataStatsConfig config = DataStatsManager.inst().getConfig(id);
		ColumnItem clm = config.getColumnItems().get(columnName);
		GroupColumnItem groupCol = new GroupColumnItem();
		groupCol.setColumnName(clm.getColumnName());
		groupCol.setColumnType(clm.getColumnType());
		groupCol.setDesc(desc);
		groupCol.setGroupType(GroupColumnItemType.getType(groupTypeId));
		config.putGroupItem(groupCol);
		DataStatsManager.inst().saveOrUpdateConfig(config);
		redirect(resp, "stats_config?id=" + id);
	}
	@RequestMapping("/config_column_group_del")
	public void config_column_group_del(HttpServletResponse resp, String id, String key) {
		if(Util.isEmpty(key)){
			redirect(resp, "stats_config?id=" + id);
			return;
		}
		DataStatsConfig config = DataStatsManager.inst().getConfig(id);
		config.removeGroupItem(key);
		DataStatsManager.inst().saveOrUpdateConfig(config);
		redirect(resp, "stats_config?id=" + id);
	}
	@RequestMapping("/flow_query")
	public String query(@RequestParam Optional<String> download, @RequestParam Optional<String> excel, @RequestParam Optional<Byte> type_query_id, @RequestParam Optional<Integer> pageNo, @RequestParam  Optional<Integer> pageSize, HttpServletRequest req, HttpServletResponse resp, Model model) {
		String id = req.getParameter("id");
		if(Util.isEmpty(id)){
			redirect(resp, "stats");
			return null;
		}
		
		DataStatsConfig config = DataStatsManager.inst().getConfig(id);
		List<FilterItem> filters = new ArrayList<>();
		config.getFilterItems().values().forEach((filterItem)->{
			FilterItem filter = filterItem.createClone();
			filter.setInputValue(req.getParameter(filter.getKey()));
			filters.add(filter);
		});
		if(download.isPresent()){
			StopWatch stopWatch = new StopWatch("stats flow");
			Builder<?> downloadBuilder = excel.isPresent()?new ExcelBuilder():new TextBuilder();
			/**显示*/
			Collection<ColumnItem> columnItems = config.getColumnItems().values();
			stopWatch.start("paging query");
			List<Map<String, Object>> result = DataStatsManager.inst().queryPayFlowAll(id, filters);
			String fileName = config.getName() + "." + downloadBuilder.getSuffixName();
			fileName = urlEncode(fileName);
			resp.setContentType("text/html;charset=utf-8");
			resp.setCharacterEncoding("utf-8");
			resp.setHeader("content-disposition", "attachment;filename=" + fileName);
			downloadBuilder.setTitle(config.getName());
			downloadBuilder.setAuthor(getUserName(req));
			String descs[] = columnItems.stream().map(ColumnItem::getDesc).toArray(String[]::new);
			downloadBuilder.writeWords(descs).writeLine();
			
			result.forEach((Map<String, Object> map)->{
				columnItems.forEach((col)->{
					String value = map.get(col.getColumnName())+"";
					downloadBuilder.writeWords(col.format(value));
				});
				downloadBuilder.writeLine();
			});
			
			
			byte[] data = downloadBuilder.getByteArray();
			try {
				resp.getOutputStream().write(data , 0, data.length);
			} catch (IOException e) {
				LogCore.BASE.error("send fileStream err", e);
			}
			stopWatch.stop();
			LogCore.BASE.info("download file {}", stopWatch.prettyPrint());
			return null;
		}
		
		QueryType queryType = QueryType.getType(type_query_id.orElse((byte)0));
		snapShotBaseView(model, id, queryType.id);
		
		if(queryType == QueryType.GROUP){//如果是汇总查询
			queryGroupReduce(model, id, config, filters);
			return "stats";
		}
		queryFollow(pageNo, pageSize, resp, model, id, config, filters);
		return "stats";
	}
	/** 汇总 */
	private void queryGroupReduce(Model model, String id, DataStatsConfig config, List<FilterItem> filters) {
		StopWatch stopWatch = new StopWatch("stats flow");
		stopWatch.start("ready query");
		/**显示*/
		Map<String, GroupColumnItem> groupColumns = config.getGroupItems();
		LogCore.BASE.info("filters={}", filters);
		LogCore.BASE.info("groupColumn={}", groupColumns);
		List<List<String>>  viewList = DataStatsManager.inst().queryGroupBySkipLimit(id, filters, groupColumns.values());
		
		model.addAttribute("columnItems", groupColumns.values());
		model.addAttribute("filterItems", filters);//保留参数
		model.addAttribute("viewList", viewList);
		stopWatch.stop();
		LogCore.BASE.info("query time used{}", stopWatch.prettyPrint());
	}
	private void queryFollow(Optional<Integer> pageNo, Optional<Integer> pageSize,
			HttpServletResponse resp, Model model, String id, DataStatsConfig config, List<FilterItem> filters) {
		StopWatch stopWatch = new StopWatch("stats flow");
		stopWatch.start("ready query");
		/**显示*/
		Map<String, ColumnItem> columnItems = config.getColumnItems();
		LogCore.BASE.info("filters={}", filters);
		LogCore.BASE.info("columnItems={}", columnItems);
		stopWatch.stop();
		stopWatch.start("paging query");
		/**分页*/
		int _page_size = pageSize.orElse(DEFAULT_PAGE_SIZE_20000);
		long sum = DataStatsManager.inst().queryCount(id, filters);
		int page_no = pageNo.orElse(0);
		int pages_num = (int) Math.ceil((double)sum/(double)_page_size);
		int skip_num = page_no * _page_size;
		
		List<Map<String, Object>> result = DataStatsManager.inst().queryPayFlowBySkipLimit(id, skip_num, _page_size, filters);
		List<List<String>> viewList = new ArrayList<>();
		result.forEach((Map<String, Object> map)->{
			List<String> views = new ArrayList<>();
			columnItems.values().forEach((col)->{
				String value = map.get(col.getColumnName())+"";
				views.add(col.format(value));
			});
			viewList.add(views);
		});
		stopWatch.stop();
		stopWatch.start("reduce sum query");
		/**求和*/
		long sumValue = 0;
		long sumAllValues = 0;
		Optional<Map<String, Object>> any = result.stream().findAny();
		if(!Util.isEmpty(config.getSumName())&& any.isPresent() && any.get().containsKey(config.getSumName())){
			sumValue = result.stream().mapToInt(map->(Integer)map.get(config.getSumName())).sum();
			stopWatch.stop();
			stopWatch.start("reduce sum all query");
			sumAllValues = DataStatsManager.inst().querySum(id, filters);
		}
		stopWatch.stop();
		
		stopWatch.start("ready return");
		/**总求和*/
		model.addAttribute("sumValue", sumValue);
		model.addAttribute("sumAllValues", sumAllValues);
		model.addAttribute("columnItems", columnItems.values());
		model.addAttribute("filterItems", filters);//保留参数
		model.addAttribute("viewList", viewList);
		model.addAttribute("sum", sum);//总文章数
		model.addAttribute("pages_num", pages_num);//总页数
		model.addAttribute("page_no", page_no);//第几页
		model.addAttribute("page_size", _page_size);//每页大小
		List<Integer> page_nos = new ArrayList<Integer>();
		for(int i=0; i< pages_num; i++){
			page_nos.add(i);
		}
		model.addAttribute("page_nos", page_nos);
		LogCore.BASE.info("page_no={}", page_no);
		stopWatch.stop();
		LogCore.BASE.info("query time used{}", stopWatch.prettyPrint());
	}
	private void snapShotBaseView(Model model, String id) {
		snapShotBaseView(model, id, (byte)0);
	}
	private void snapShotBaseView(Model model, String id, byte type_query_id) {
		/** base start*/
		Collection<DataStatsConfig>  configs = DataStatsManager.inst().getAllConfigs();
		DataStatsConfig config = DataStatsManager.inst().getConfig(id);
		model.addAttribute("configs", configs);
		model.addAttribute("config", config);
		model.addAttribute("type_list", FilterType.values());
		model.addAttribute("type_column_list", ColumnItemType.values());
		model.addAttribute("type_group_list", GroupColumnItemType.values());
		model.addAttribute("type_query_list", QueryType.values());
		Collection<ColumnItem> columnItems = config.getColumnItems().values();
		Collection<FilterItem> filterItems = config.getFilterItems().values();
		Collection<GroupColumnItem> groupItems = config.getGroupItems().values();
		model.addAttribute("columnItems", columnItems);
		model.addAttribute("filterItems", filterItems);
		model.addAttribute("groupItems", groupItems);
		model.addAttribute("type_query_id", type_query_id);
		LogCore.BASE.info("config={}", config);
		/** base end*/
	}
}
