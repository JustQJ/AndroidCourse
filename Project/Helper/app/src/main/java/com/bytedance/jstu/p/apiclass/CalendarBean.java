package com.bytedance.jstu.p.apiclass;

public class CalendarBean {


        private int error_code;
        private String reason;
        private CalendarResultBean result;
        public void setError_code(int error_code) {
            this.error_code = error_code;
        }
        public int getError_code() {
            return error_code;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
        public String getReason() {
            return reason;
        }

        public void setResult(CalendarResultBean result) {
            this.result = result;
        }
        public CalendarResultBean getResult() {
            return result;
        }

}
