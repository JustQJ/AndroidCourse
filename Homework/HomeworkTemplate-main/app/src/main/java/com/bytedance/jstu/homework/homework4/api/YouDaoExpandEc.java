package com.bytedance.jstu.homework.homework4.api;

import java.util.List;

public class YouDaoExpandEc {

    private Source source;
    private List<Word> word;


    public class Source{
        private String name;

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public class Word{
        private List<TransList> transList;
        private String pos;
        public class TransList{
            private String tran;

            public String getTran() {
                return tran;
            }

            public void setTran(String tran) {
                this.tran = tran;
            }
        }

        public String getPos() {
            return pos;
        }

        public void setPos(String pos) {
            this.pos = pos;
        }

        public List<TransList> getTransList() {
            return transList;
        }

        public void setTransList(List<TransList> transList) {
            this.transList = transList;
        }
    }


    public void setWord(List<Word> word) {
        this.word = word;
    }

    public List<Word> getWord() {
        return word;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Source getSource() {
        return source;
    }
}
