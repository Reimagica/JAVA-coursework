package beans;

import java.util.List;

//用于呈现检索结果的类，把结果呈现为字符串（如果感觉不需要可以不调用这个类x）

public class SearchResultRenderer {

    /**
     * 将检索结果呈现为字符串。
     * @param searchResults 检索结果列表
     * @return 呈现的字符串
     */

    //接受searchResults类型数据，对信息进行格式化
    public static String renderResults(List<SearchResultBean> searchResults) {
        StringBuilder resultString = new StringBuilder();

        for (SearchResultBean result : searchResults) {
            resultString.append("ECCN: ").append(result.getEccn()).append("\n");
            resultString.append("Description: ").append(result.getDescription()).append("\n");
            resultString.append("Item: ").append(result.getItem()).append("\n");
            resultString.append("------------------------------\n");
        }

        return resultString.toString();
    }
}
