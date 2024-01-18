package beans;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class UserQueryBeanTest {

    @Test
    public void isChinese() {
        UserQueryBean chineseQueryBean = new UserQueryBean("中文查询");
        assertTrue(chineseQueryBean.isChinese());

        UserQueryBean englishQueryBean = new UserQueryBean("English Query");
        assertFalse(englishQueryBean.isChinese());
    }

    @Test
    public void getKeywords() {
        // 测试中文分词
        UserQueryBean chineseQueryBean = new UserQueryBean("中文 分词 测试");
        List<String> chineseKeywords = chineseQueryBean.getKeywords();
        assertTrue(chineseKeywords.contains("中文"));
        assertTrue(chineseKeywords.contains("分词"));
        assertTrue(chineseKeywords.contains("测试"));

        // 测试英文空格分隔
        UserQueryBean englishQueryBean = new UserQueryBean("English Space Separated Test");
        List<String> englishKeywords = englishQueryBean.getKeywords();
        assertTrue(englishKeywords.contains("English"));
        assertTrue(englishKeywords.contains("Space"));
        assertTrue(englishKeywords.contains("Separated"));
        assertTrue(englishKeywords.contains("Test"));

        // 测试停用词过滤
        UserQueryBean stopWordsQueryBean = new UserQueryBean("哈哈哈java可真是有趣");
        List<String> stopWordsKeywords = stopWordsQueryBean.getKeywords();
        assertFalse(stopWordsKeywords.contains("哈哈"));
        assertFalse(stopWordsKeywords.contains("可"));
    }
}
