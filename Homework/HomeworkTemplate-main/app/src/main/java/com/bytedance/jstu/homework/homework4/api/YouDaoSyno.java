package com.bytedance.jstu.homework.homework4.api;

import java.util.List;

public class YouDaoSyno {

    private List<Synos> synos;
    private String word;
    public class Synos {
        private Syno syno;

        public class Syno {
            private String pos;
            private String tran;

            public void setPos(String pos) {
                this.pos = pos;
            }

            public String getPos() {
                return pos;
            }

            public String getTran() {
                return tran;
            }

            public void setTran(String tran) {
                this.tran = tran;
            }
        }

        public Syno getSyno() {
            return syno;
        }

        public void setSyno(Syno syno) {
            this.syno = syno;
        }
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public List<Synos> getSynos() {
        return synos;
    }

    public void setSynos(List<Synos> synos) {
        this.synos = synos;
    }



}
