package com.bytedance.jstu.homework.homework4.api;

public class TranslatorBean {
    private YouDaoWebTrans web_trans;
    private YouDaoExpandEc expand_ec;
    private YouDaoMetaBean meta;
    private YouDaoSyno syno;



    public void setWeb_trans(YouDaoWebTrans web_trans) {
        this.web_trans = web_trans;
    }
    public YouDaoWebTrans getWeb_trans() {
        return web_trans;
    }


    public void setSyno(YouDaoSyno syno) {
        this.syno = syno;
    }

    public YouDaoSyno getSyno() {
        return syno;
    }

    public void setMeta(YouDaoMetaBean meta) {
        this.meta = meta;
    }

    public YouDaoMetaBean getMeta() {
        return meta;
    }

    public void setExpand_ec(YouDaoExpandEc expand_ec) {
        this.expand_ec = expand_ec;
    }

    public YouDaoExpandEc getExpand_ec() {
        return expand_ec;
    }

}
