package com.bytedance.jstu.homework.homework4.api;

import java.util.List;

public class YouDaoMetaBean {


        private String input;
        private String guessLanguage;
        private String isisHasSimpleDict;
        private String le;
        private String lang;
        private List<String> dicts;

        public void setInput(String input) {
            this.input = input;
        }

        public String getInput() {
            return input;
        }

        public void setGuessLanguage(String guessLanguage) {
            this.guessLanguage = guessLanguage;
        }

        public String getGuessLanguage() {
            return guessLanguage;
        }


        public void setLe(String le) {
            this.le = le;
        }

        public String getLe() {
            return le;
        }


        public void setDicts(List<String> dicts) {
            this.dicts = dicts;
        }

        public List<String> getDicts() {
            return dicts;
        }

        public String getIsisHasSimpleDict() {
            return isisHasSimpleDict;
        }

        public void setIsisHasSimpleDict(String isisHasSimpleDict) {
            this.isisHasSimpleDict = isisHasSimpleDict;
        }


        public String getLang() {
            return lang;
        }

        public void setLang(String lang) {
            this.lang = lang;
        }


}
