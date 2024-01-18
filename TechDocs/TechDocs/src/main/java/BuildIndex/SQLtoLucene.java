package src.main.java.BuildIndex;

import java.io.File;

import java.io.IOException;
import java.sql.*;


import org.apache.lucene.analysis.Analyzer;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;


public class SQLtoLucene {

	public static void main(String[] args) {
		Connection connection ;
		Statement stmt;
		ResultSet rs;
		try{
			Class.forName("com.mysql.cj.jdbc.Driver"); 
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/javadoc","root","root");
			stmt=connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,	
    					ResultSet.CONCUR_UPDATABLE);//15行		
		
			String sql="SELECT * FROM eccn_en_fin";//10行
			rs=stmt.executeQuery(sql);
			
			
			//指定索引目录
			Directory d=FSDirectory.open(new File(".\\index").toPath());
			//指定分析器
			Analyzer analyzer=new IKAnalyzer(true);
			
			//创建索引配置对象//15行 
			IndexWriterConfig conf=new IndexWriterConfig(analyzer);
			//覆盖方式创建索引
			conf.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
			//追加方式创建索引
			//conf.setOpenMode(IndexWriterConfig.OpenMode.APPEND );
			//BasicIndexDemo bid=new BasicIndexDemo();
		    //创建索引	   
			
			IndexWriter indexWriter=new  IndexWriter(d, conf);//25行
	
			while(rs.next()){ //后移			
			//此处做索引		
			
				System.out.println(rs.getString("Item_Number")+"\n"+ rs.getString("ECCN")+"\n"+
					rs.getString("Description")+"\n"+rs.getString("Item_Prefix")+"\n"+rs.getString("Item")+"\n"+rs.getString("Parent_ID"));
		
				Document doc =new  Document();
				//创建字段
				//ItemNumber自然顺序编号
	    		Field ItemNumber=new  Field( "Item_Number",rs.getString("Item_Number"), 
		   					Field.Store.YES, Field.Index.NOT_ANALYZED);//40行 
	    		doc.add(ItemNumber);
	    		//ECCN文档名称
	    		Field ECCN=new  Field("ECCN",
	    				      rs.getString("ECCN"),
			   				  Field.Store.YES,
			   				  Field.Index.NOT_ANALYZED,
			   				  Field.TermVector.WITH_POSITIONS_OFFSETS); //50行     
	    		doc.add(ECCN);//将ECCN字段加入文档      
	    		//Description文档描述
	    		String description;
	    		if(rs.getString("Description")!=null) {
	    			description = rs.getString("Description");
	    		}else {
	    			description = "none";
	    		}
	    		Field Description=new  Field("Description",
	    					description, //55行 
			                Field.Store.YES,
			                Field.Index.ANALYZED,
			                Field.TermVector.WITH_POSITIONS_OFFSETS);      
	    		doc.add(Description); 
	    		//Item_Prefix条目编号
	    		String itemID;
	    		if(rs.getString("Item_Prefix")!=null) {
	    			itemID = rs.getString("Item_Prefix");
	    		}else {
	    			itemID = "none";
	    		}
	    		Field Item_Prefix=new  Field("Item_Prefix",
	    				    itemID,  
			                Field.Store.YES,
			                Field.Index.NOT_ANALYZED,
			                Field.TermVector.WITH_POSITIONS_OFFSETS);//80行      
                doc.add(Item_Prefix);
                //Item条目内容
                String content;
	    		if(rs.getString("Item")!=null) {
	    			content = rs.getString("Item");
	    		}else {
	    			content = "none";
	    		}
	    		Field Item=new  Field("Item",
	    				    content,  
			                Field.Store.YES,
			                Field.Index.ANALYZED,
			                Field.TermVector.WITH_POSITIONS_OFFSETS);//80行      
                doc.add(Item);         
	    		//Parent_ID父编号
                String parent;
	    		if(rs.getString("Parent_ID")!=null) {
	    			parent = rs.getString("Parent_ID");
	    		}else {
	    			parent = "none";
	    		}
	    		Field Parent_ID=new  Field("Parent_ID",//60行 
	    				 	parent,  
		                    Field.Store.YES,
		                    Field.Index.NOT_ANALYZED,
		                    Field.TermVector.WITH_POSITIONS_OFFSETS);      
	    		doc.add(Parent_ID);//65行            
	    		
	    		indexWriter.addDocument(doc);//将文档添加到索引编写器     
				
			}
			indexWriter.close();////关闭索引编写器
  		    System.out.println("索引创建完毕"); //110行
  			
  		    analyzer.close();//关闭分析器 //20行
  		
			rs.close();
	
			stmt.close();
			connection.close() ;
			System.out.println("索引中文档总数="+readDoc(d));	
		    d.close();//关闭目录	 
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CorruptIndexException e) {
			
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
	static int readDoc(Directory d) throws CorruptIndexException, IOException {
		IndexReader  indexReader=IndexReader.open(d);
		int docNum=indexReader.numDocs();//获取文档实际数量		
		indexReader.close();//关闭索引读取器 //40行	    
        return docNum; //返回文档实际数量
	}

  
}
