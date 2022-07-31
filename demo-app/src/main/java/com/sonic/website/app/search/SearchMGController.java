package com.sonic.website.app.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.sonic.website.app.file.FileManager;
import com.sonic.website.app.file.function.FileConsumer;
import com.sonic.website.app.security.annoation.NeedLogin;
import com.sonic.website.app.security.vo.UserVO;
import com.sonic.website.core.common.support.DataVO;
import com.sonic.website.core.spring.interfaces.AccessSupport;

/**
 * 激活码
 */
@NeedLogin
@RequestMapping("/mg/search")
@Controller
@Order(value = 5)
public class SearchMGController implements AccessSupport {
    private static final String SPACE_TAG_REARCH = "search";

    @RequestMapping
    void pageTemplate(HttpServletRequest req, HttpServletResponse resp) {
        redirectAppend(req, resp, SPACE_TAG_REARCH);
    }

    @RequestMapping("/" + SPACE_TAG_REARCH)
    String code(Model model, @RequestParam Optional<String> sn, HttpServletRequest req, HttpServletResponse resp) {
        List<SearchInfoVO> code_infos = SearchManager.inst().getAllCodeInfo();
        Optional<SearchInfoVO> code_info;
        if (sn.isPresent()) {
            code_info = code_infos.stream().filter(a -> a.sn.equals(sn.get())).findFirst();
        } else {
            code_info = code_infos.stream().findFirst();
        }
        if (code_info.isPresent()) {
            model.addAttribute("code_info", code_info.get());
            model.addAttribute("code_infos", code_infos);
        } else {
            SearchInfoVO vo = new SearchInfoVO();
            vo.name = "例子演示";
            model.addAttribute("code_info", vo);
            model.addAttribute("code_infos", new SearchInfoVO[] { vo });
        }
        String user_name = getSessionAttrFncOrElse(req, "user", UserVO::getName, null);
        model.addAttribute("user_name", user_name);
        return SPACE_TAG_REARCH;
    }

    @RequestMapping("/add_code_cf")
    void add_code(@RequestParam Optional<String> code_sn, 
            @RequestParam Optional<String> code_name,
            @RequestParam Optional<String> code_desc, 
            @RequestParam Optional<Integer> threads,
            StandardMultipartHttpServletRequest req,
            HttpServletResponse resp) {

        List<String> codes = new ArrayList<>();
        FileConsumer consume = (m) -> codes.addAll(FileManager.inst().readFileLines(m));

        Map<String, FileConsumer> comsumeMap = new HashMap<>();
        comsumeMap.put("code_file", consume);
        FileManager.inst().handleMultiFile(comsumeMap, req);

        String create_by = getSessionAttrFncOrElse(req, "user", UserVO::getName, "unknown");
        SearchManager.inst().addCodeInfo(code_sn.get(), create_by, code_name.get(), code_desc.get(), codes, threads.orElse(1));

        redirect(resp, SPACE_TAG_REARCH + "?sn=" + code_sn.get());
    }

    /**
     * 修改激活码配置，修改的时候不可以修改刷新类型
     */
    @RequestMapping("/edit_code_cf")
    void edit_code_cf(@RequestParam String code_sn, 
            @RequestParam Optional<Integer> threds, 
            StandardMultipartHttpServletRequest req,
            HttpServletResponse resp) {

        List<String> codes = new ArrayList<>();
        FileConsumer consume = (m) -> codes.addAll(FileManager.inst().readFileLines(m));

        Map<String, FileConsumer> comsumeMap = new HashMap<>();
        comsumeMap.put("code_file", consume);
        FileManager.inst().handleMultiFile(comsumeMap, req);
        SearchManager.inst().editCodeInfo(code_sn, codes, threds);
        redirect(resp, SPACE_TAG_REARCH + "?sn=" + code_sn);

    }
    @RequestMapping("    estWordsSearch")
    String testWordsSearch(Model model,
            @RequestParam String code_sn, 
            @RequestParam Optional<Integer> threds, 
            StandardMultipartHttpServletRequest req,
            HttpServletResponse resp) {

        List<String> codes = new ArrayList<>();
        FileConsumer consume = (m) -> codes.addAll(FileManager.inst().readFileLines(m));

        Map<String, FileConsumer> comsumeMap = new HashMap<>();
        comsumeMap.put("code_file", consume);
        FileManager.inst().handleMultiFile(comsumeMap, req);
        
        
        Object[] rst = SearchManager.inst().testWordsSearch(code_sn, codes, threds);
        List<DataVO> sn_list = new ArrayList<>();
        sn_list.add(new DataVO(rst[0]+"", rst[1]));
        model.addAttribute("title", "大转盘测试结果");
        model.addAttribute("tablename1", "物品sn结果");
//        model.addAttribute("tablename2", "下标结果");
        model.addAttribute("colums1", new String[]{"耗时","匹配结果"});
//        model.addAttribute("colums2",new String[]{"下标","次数"});
        model.addAttribute("list1", sn_list);
//        model.addAttribute("list2", ordinal_list);
        return "bi_view";

    }
    @RequestMapping("testSearch")
    String testdraw(Model model,
            @RequestParam String sn, 
            @RequestParam Optional<Integer> num){
        Map<Object, Integer> goods_sn_map = new HashMap<>();
        Map<Object, Integer> ordinal_map = new HashMap<>();
//        BiConsumer<Object, Object> consumer = (goods_sn, ordinal)->{
//            goods_sn_map.merge(goods_sn, 1, Integer::sum);
//            ordinal_map.merge(ordinal, 1, Integer::sum);
//        };
//        int _num = num.orElse(100);
//        while(_num-- >0){
//            LuckyDrawManager.inst().draw(sn, "test"+_num, consumer, true, Optional.empty());
//        }
        List<DataVO> sn_list = new ArrayList<>();
        List<DataVO> ordinal_list = new ArrayList<>();
        goods_sn_map.forEach((k, v)->sn_list.add(new DataVO(String.valueOf(k), v)));
        ordinal_map.forEach((k, v)->ordinal_list.add(new DataVO(String.valueOf(k), v)));
        model.addAttribute("title", "大转盘测试结果");
        model.addAttribute("tablename1", "物品sn结果");
        model.addAttribute("tablename2", "下标结果");
        model.addAttribute("colums1", new String[]{"sn","次数"});
        model.addAttribute("colums2",new String[]{"下标","次数"});
        model.addAttribute("list1", sn_list);
        model.addAttribute("list2", ordinal_list);
        return "bi_view";
    }
    /**
     * 删除激活码配置
     */
    @RequestMapping("/delete_code_cf")
    void delete_code_cf(@RequestParam Optional<String> sn, HttpServletResponse resp) {
        sn.ifPresent(SearchManager.inst()::deleteCodeInfo);
        redirect(resp, SPACE_TAG_REARCH);
    }

    @RequestMapping("/delete_code_list")
    void delete_code_list(@RequestParam String sn, HttpServletResponse resp) {
        SearchManager.inst().cleanCodes(sn);
        redirect(resp, SPACE_TAG_REARCH + "?sn=" + sn);
    }
}
