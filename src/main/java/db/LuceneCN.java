package db;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LuceneCN {

    static final String INDEX_DIR = "./index_data";

    public IndexWriter createWrite()throws IOException {

        Path path = Paths.get(INDEX_DIR);

        FSDirectory open = FSDirectory.open(path);

        StandardAnalyzer analyzer = new StandardAnalyzer();

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
}
