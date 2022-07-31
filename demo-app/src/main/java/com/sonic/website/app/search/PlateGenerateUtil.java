package com.sonic.website.app.search;

import java.util.List;
import java.util.stream.Collectors;

import com.sonic.website.core.common.support.DrawUtil;
import com.sonic.website.core.common.support.FileTool;
import com.sonic.website.core.common.support.LogCore;

/***
 * 随机生成车牌号
 * @author bao 
 */
public class PlateGenerateUtil {
    public static final String[][] ZONES = { { "北京市", "京", "北京" }, { "上海市", "沪", "上海" }, { "天津市", "津", "天津" },
            { "重庆市", "渝", "重庆" }, { "黑龙江省", "黑", "哈尔滨" }, { "吉林省", "吉", "长春" }, { "辽宁省", "辽", "沈阳" },
            { "内蒙古", "内蒙古", "呼和浩特" }, { "河北省", "冀", "石家庄" }, { "新疆", "新", "乌鲁木齐" }, { "甘肃省", "甘", "兰州" },
            { "青海省", "青", "西宁" }, { "陕西省", "陕", "西安" }, { "宁夏", "宁", "银川" }, { "河南省", "豫", "郑州" }, { "山东省", "鲁", "济南" },
            { "山西省", "晋", "太原" }, { "安徽省", "皖", "合肥" }, { "湖北省", "鄂", "武汉" }, { "湖南省", "湘", "长沙" },
            { "江苏省", "苏", "南京" }, { "四川省", "川", "成都" }, { "贵州省", "黔", "贵阳" }, { "云南省", "滇", "昆明" },
            { "广西省", "桂", "南宁" }, { "西藏", "藏", "拉萨" }, { "浙江省", "浙", "杭州" }, { "江西省", "赣", "南昌" }, { "广东省", "粤", "广州" },
            { "福建省", "闽", "福州" }, { "台湾省", "台", "台北" }, { "海南省", "琼", "海口" }, { "香港", "港", "香港" },
            { "澳门", "澳", "澳门" }, };
    /** uppercase characters **/
    public static final Character[] UPPER_CASE_CHARS = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
    public static final Integer[] DIGITS = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

    public static void main2(String args[]) throws Exception {
        List<String> lines = FileTool.readLines("a.txt");
        LogCore.BASE.info("{},{}", lines, lines.size());
        DrawUtil.any(ZONES);
        lines.forEach(s -> {
//            String[] ss = s.split(" ");
//            System.out.println("{\"" + ss[0] + "\",\"" + ss[1] + "\",\"" + ss[2] + "\"},");
            String rst = geneateOnePlate();
            System.out.println(rst);

        });
        System.out.println("tst");
    }

    private static String geneateOnePlate() {
        return DrawUtil.any(ZONES)[1]+DrawUtil.any(UPPER_CASE_CHARS)+DrawUtil.any(DIGITS,5).stream().map(Object::toString).collect(Collectors.joining());
    }
}
