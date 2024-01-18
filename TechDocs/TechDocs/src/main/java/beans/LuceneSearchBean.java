package beans;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import sun.rmi.transport.TransportConstants;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static sun.rmi.transport.TransportConstants.*;

public class LuceneSearchBean {

    // 中文和英文索引文件夹路径
    private static final String CHINESE_INDEX_PATH = "/中文索引路径";
    private static final String ENGLISH_INDEX_PATH = "/英文索引路径";


    /*比较 Lucene 检索结果得分的自定义比较器
对于按照关键词检索出的所有条目，如果Parent_ID非空，则无论Parent_ID对应的其父类条目是否相关，都要放进检索结果中展示，
 但若对应的父类条目是不相关的，则放在所有条目后面（即当作相关度最低的，如果有多个不相关的子类条目，它们之间要按照 Item_Number 自然编号排序）；
对于相关度相同的条目，按照Item_Number自然编号前后顺序排序。最终结果要包含所有得到的条目*/
    private static class CustomScoreComparator implements Comparator<ScoreDoc> {
        @Override
        public int compare(ScoreDoc doc1, ScoreDoc doc2) {
            // 比较得分，得分高的排在前面
            int scoreComparison = Float.compare(doc2.score, doc1.score);

            if (scoreComparison == 0) {
                // 如果得分相同，则按照 Item_Number 自然编号排序
                int itemNumberComparison = compareItemNumber(doc1, doc2);
                return itemNumberComparison;
            }

            return scoreComparison;
        }

        private int compareItemNumber(ScoreDoc doc1, ScoreDoc doc2) {
            // 实现对 Item_Number 自然编号的比较逻辑
            // 这里简单比较 Item_Number 的字符串形式，可能需要根据实际需求进行更精细的处理
            return doc1.toString().compareTo(doc2.toString());
        }
    }
    /**
     * 执行 Lucene 检索和排序操作。
     *
     * @param userQuery 用户查询关键词
     * @return searchResults 检索结果的列表
     */

    //执行检索及赋分排序，可以调用这个执行检索得到检索结果
    public List<SearchResultBean> searchAndSort(UserQueryBean userQuery) {
        try {
            // 获取用户输入的查询关键词
            String query = userQuery.getQuery();

            // 判断用户输入的语言类型（中文或英文）
            boolean isChinese = userQuery.isChinese();

            // 根据语言类型选择相应的索引文件夹路径
            String indexPath = isChinese ? CHINESE_INDEX_PATH : ENGLISH_INDEX_PATH;

            // 打开相应的索引文件夹
            Directory directory = FSDirectory.open(Paths.get(indexPath).toFile().toPath());
            IndexReader indexReader = DirectoryReader.open(directory);
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);

            // 使用标准分析器
            StandardAnalyzer analyzer = new StandardAnalyzer();

            // 构建查询解析器，指定检索的字段为 "Item"  我这里报错了应该是Lucene版本的问题）
            QueryParser parser = new QueryParser("Item", analyzer);
            Query luceneQuery = parser.parse(query);

            // 指定 topN 为最大整数值，以返回所有被检索出的文档
            int topN = Integer.MAX_VALUE;
            TopDocs topDocs = indexSearcher.search(luceneQuery, topN);

            ScoreDoc[] hits = topDocs.scoreDocs;

            // 处理检索结果
            List<SearchResultBean> searchResults = mapToSearchResults(indexSearcher, hits);

            // 用自定义评分规则排序
            customSort(searchResults);

            // 关闭资源
            indexReader.close();
            directory.close();

            return searchResults;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * 将 Lucene 检索结果映射为 SearchResultBean 列表。
     *
     * @param indexSearcher IndexSearcher 对象
     * @param hits          检索结果的 ScoreDoc 数组
     * @return SearchResultBean 列表
     * @throws Exception 如果映射过程中发生异常
     */
    private List<SearchResultBean> mapToSearchResults(IndexSearcher indexSearcher, ScoreDoc[] hits) throws Exception {
        List<SearchResultBean> searchResults = new ArrayList<>();

        for (ScoreDoc hit : hits) {
            Document document = indexSearcher.doc(hit.doc);

            SearchResultBean resultBean = new SearchResultBean();
            resultBean.setEccn(document.get("ECCN"));
            resultBean.setDescription(document.get("Description"));
            resultBean.setItem(document.get("item"));
            // Item_Number等如果可以再添加

            searchResults.add(resultBean);
        }

        return searchResults;
    }

    /**
     * 对 SearchResultBean 列表进行自定义排序。
     *
     * @param searchResults SearchResultBean 列表
     */
    private void customSort(List<SearchResultBean> searchResults) {
        // 实现自定义排序逻辑
        Collections.sort(searchResults, new Comparator<SearchResultBean>() {
            @Override
            public int compare(SearchResultBean result1, SearchResultBean result2) {
                // 根据规则进行比较，这里示例按照 Item_Number 自然编号排序
                return result1.getItemNumber().compareTo(result2.getItemNumber());
            }
        });
    }
}