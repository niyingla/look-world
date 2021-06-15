package db;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;
import org.wltea.analyzer.lucene.IKQueryParser;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import javax.swing.plaf.PanelUI;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;

public class LuceneCN {

    static final String INDEX_DIR = "./index_data";

    public IndexWriter createWrite()throws IOException {

        Path path = Paths.get(INDEX_DIR);

        FSDirectory open = FSDirectory.open(path);

        IKAnalyzer analyzer = new IKAnalyzer();

        IndexWriterConfig writerConfig = new IndexWriterConfig(analyzer);

        return new IndexWriter(open, writerConfig);
    }

    @Test
    public void testCreateIndex()throws IOException{
        IndexWriter indexWriter = createWrite();

        Document document = new Document();

        document.add(new TextField("content", "today i learn lucene", Field.Store.YES));

        indexWriter.addDocument(document);

        indexWriter.flush();

        indexWriter.close();
    }

    @Test
    public void testCreateIndexFromNovel()throws ClassNotFoundException, IOException,InterruptedException {
        IndexWriter write = createWrite();
        //
        BookLoader bookLoader = new BookLoader();

        ArrayList<BookLoader.Sentence> sentences = bookLoader.sentences();
        //
        LinkedBlockingDeque<Document> docs = new LinkedBlockingDeque<>();

        CountDownLatch countDownLatch = new CountDownLatch(sentences.size());

        Observable.from(sentences)
                .window(20)
                .map(o->o.subscribeOn(Schedulers.newThread()))
                .forEach(o->{
                    o.subscribe(sentence -> {
                        Document doc = new Document();
                        doc.add(new TextField("text", sentence.getText(), Field.Store.YES));
                        doc.add(new TextField("chapter", sentence.getChapterId() + "", Field.Store.YES));
                        docs.offer(doc);
                        countDownLatch.countDown();
                    });
                });
        countDownLatch.await();

        while (docs.size() > 0) {
            //
            write.addDocument(docs.poll());
        }
        write.flush();
        write.close();
    }

    @Test
    public void query() throws ParseException, IOException {
        Query query = IKQueryParser.parse("text", "宝宝很开心");

        IndexWriter write = createWrite();

        IndexSearcher search = createSearch();

        TopDocs topDocs = search.search(query, 10);

        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            //
            int docId = scoreDoc.doc;
            //
            Document doc = search.doc(docId);
            System.out.format("%s %s %s\n ", scoreDoc.score, doc.get("chapter"), doc.get("text"));
        }

    }

    @Test
    public void query2() throws ParseException, IOException {
        Query query = IKQueryParser.parse("text", "lucene");

        IndexWriter write = createWrite();

        IndexSearcher search = createSearch();

        TopDocs topDocs = search.search(query, 10);

        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            //
            int docId = scoreDoc.doc;
            //
            Document doc = search.doc(docId);
            System.out.format("%s %s %s\n ", scoreDoc.score, doc.get("chapter"), doc.get("text"));
        }

    }


    /**
     * 创建搜索器
     * @return
     * @throws IOException
     */
    private IndexSearcher createSearch()throws IOException{

        Path path = Paths.get(INDEX_DIR);

        FSDirectory directory = FSDirectory.open(path);

        DirectoryReader directoryReader = DirectoryReader.open(directory);

        return new IndexSearcher(directoryReader);
    }
}
