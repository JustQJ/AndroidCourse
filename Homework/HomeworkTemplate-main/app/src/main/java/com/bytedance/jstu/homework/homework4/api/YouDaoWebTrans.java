package com.bytedance.jstu.homework.homework4.api;

import java.util.ArrayList;
import java.util.List;

public class YouDaoWebTrans {

        private List<Webtranslation> web_translation = new ArrayList<>();


        public class Webtranslation{
            private String key;
            private List<Trans> trans = new ArrayList<>();


            public class Trans{
                private String value;
                public void setValue(String value){this.value = value;}
                public  String getValue(){return value;}

            }

            public void setKey(String key){this.key=key;}
            public String getKey(){return key;}

            public void setTrans(List<Trans> trans){this.trans = trans;}
            public List<Trans> getTrans(){return  trans;}



        }

        public void setWeb_translation(List<Webtranslation> webtranslation){this.web_translation=webtranslation;}
        public List<Webtranslation> getWeb_translation(){return web_translation;}



}
