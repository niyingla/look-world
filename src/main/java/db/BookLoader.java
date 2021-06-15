package db;

import java.util.ArrayList;

public class BookLoader {

    class Sentence{
       private Long chapterId;

        private String text;

        public Long getChapterId() {
            return chapterId;
        }

        public void setChapterId(Long chapterId) {
            this.chapterId = chapterId;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public ArrayList<Sentence> sentences(){
        return new ArrayList<>();
    }
}
