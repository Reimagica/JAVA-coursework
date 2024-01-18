package beans;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserQueryBean {
    private String query; // 用户输入的查询关键词

    public UserQueryBean(String query) {
        this.query = query;
    }

    // Getter and setter for query
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    //暂时没找到啥比较好使的中英文识别API（）现在只是只有有中文就识别为中文x
    public boolean isChinese() {
        // 判断用户输入是否包含中文
        return query.matches(".*[\u4e00-\u9fa5]+.*");
    }

    // 获取关键词列表
    public List<String> getKeywords() {
        //System.out.println(System.getProperty("java.class.path"));
        // 根据中英文进行切词处理
        List<String> keywords = new ArrayList<>();

        List<Term> terms = new ArrayList<>();
        if (isChinese()) {
            // 中文分词
            terms = ToAnalysis.parse(query).getTerms();
            // 添加分词结果到关键词列表
            for (Term term : terms) {
                keywords.add(term.getName());
            }
        } else {
            // 英文空格分隔
            keywords.addAll(Arrays.asList(query.split("\\s+")));
        }

        // 去除停用词
        List<String> stopWords = loadStopWords();
        keywords.removeAll(stopWords);

        return keywords;

    }

    // 从文件加载停用词表
    private List<String> loadStopWords() {
        //System.out.println(System.getProperty("java.class.path"));

        List<String> stopWords = new ArrayList<>();

        // 加载 TextBayesClassifier 停用词表
        loadStopWordsFromResource("TextBayesClassifier停用词表.txt", stopWords);

        // 加载 哈工大 停用词表
        loadStopWordsFromResource("哈工大停用词表.txt", stopWords);

        return stopWords;
    }

    private void loadStopWordsFromResource(String resourcePath, List<String> stopWords) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(resourcePath)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stopWords.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
