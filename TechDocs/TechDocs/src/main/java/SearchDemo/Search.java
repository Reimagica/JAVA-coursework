package src.main.java.SearchDemo;

import java.io.File;
import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.FuzzyQuery;


public class Search {
	/**
	 * @param args
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws IOException, ParseException {
		
		Directory d=FSDirectory.open(new File(".\\index"));//������ַ         
        IndexSearcher searcher  =   new  IndexSearcher(IndexReader.open(d));//������������������
        // ������ѯ������,����������Ҫ��ѯ���ֶε����ƣ���/���ֶΣ����ִ���
        Analyzer analyzer=new IKAnalyzer(true);
        //���ֶ�
        MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_35, new String[] {"Description","Item"},analyzer); 
        //���ֶ�
        //QueryParser parser = new QueryParser(Version.LUCENE_35,"Description",analyzer);
        // ������ѯ����,������
        String keyword = "Technology";
        Query query = parser.parse(keyword);
		TopDocs hits  =  searcher.search(query,10); //���м�������෵��ǰ10�����   
        System.out.println("�����ʣ�"+keyword+"\n"+"�������"+hits.totalHits);
        System.out.println("---------------------------------------------------");
        //��ʾǰʮ�����
        for(ScoreDoc scoreDoc:hits.scoreDocs){
        	Document doc=searcher.doc(scoreDoc.doc);
        	//��ӡ�����˳���ţ�Description����Ŀ����
        	System.out.println("ECCN         "+doc.get("ECCN")+"\n"+"Description  "+doc.get("Description")+"\n"+"Content      "+doc.get("Item"));
        	System.out.println("---------------------------------------------------");
        }
        searcher.close();//�ͷ���Դ
        d.close();
	}
	//�ֿ�����ģʽ��ûд�꣩
	public static void searchProcess(Query query) throws IOException, ParseException {
		Directory d=FSDirectory.open(new File(".\\index"));//������ַ         
        IndexSearcher searcher  =   new  IndexSearcher(IndexReader.open(d));//������������������
//        // ������ѯ������,����������Ҫ��ѯ���ֶε����ƣ���/���ֶΣ����ִ���
//        Analyzer analyzer=new IKAnalyzer(true);
//        //���ֶ�
//        MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_35, new String[] {"Description","Item"},analyzer); 
//        //���ֶ�
//        //QueryParser parser = new QueryParser(Version.LUCENE_35,"Description",analyzer);
//        // ������ѯ����,������
//        String keyword = "Technology";
//        Query query = parser.parse(keyword);
		TopDocs hits  =  searcher.search(query,10); //���м�������෵��ǰ10�����   
//        System.out.println("�����ʣ�"+keyword+"\n"+"�������"+hits.totalHits);
		System.out.println("�������"+hits.totalHits);
        System.out.println("---------------------------------------------------");
        //��ʾǰʮ�����
        for(ScoreDoc scoreDoc:hits.scoreDocs){
        	Document doc=searcher.doc(scoreDoc.doc);
        	//��ӡ�����˳���ţ�Description����Ŀ����
        	System.out.println(doc.get("Item_Number")+"\n"+doc.get("Description")+"\n"+doc.get("Item"));
        	System.out.println("---------------------------------------------------");
        }
        searcher.close();//�ͷ���Դ
        d.close();
	}
	//��ͨ��ѯ
	public static Query normalQ(String keyword) throws ParseException {
		// ������ѯ������,����������Ҫ��ѯ���ֶε����ƣ���/���ֶΣ����ִ���
		Analyzer analyzer=new IKAnalyzer(true);
		//���ֶ�
		MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_35, new String[] {"Description","Item"},analyzer); 
		//���ֶ�
		//QueryParser parser = new QueryParser(Version.LUCENE_35,"Description",analyzer);
		// ������ѯ����,������
		Query query = parser.parse(keyword);
		return query;
	}
	//ģ����ѯ
	public static Query FuzzyQ(String keyword) throws ParseException {
		//������ѯ����,������
		Query query = new FuzzyQuery(new Term("Description",keyword),1);
		return query;
	}
	

}
