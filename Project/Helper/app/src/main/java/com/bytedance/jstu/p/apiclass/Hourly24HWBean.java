package com.bytedance.jstu.p.apiclass;

import java.util.Date;

public class Hourly24HWBean {

        private Date time;
        private String text;
        private String code;
        private String temperature;
        private String humidity;
        private String wind_direction;
        private String wind_speed;
        public void setTime(Date time) {
            this.time = time;
        }
        public Date getTime() {
            return time;
        }

        public void setText(String text) {
            this.text = text;
        }
        public String getText() {
            return text;
        }

        public void setCode(String code) {
            this.code = code;
        }
        public String getCode() {
            return code;
        }

        public void setTemperature(String temperature) {
            this.temperature = temperature;
        }
        public String getTemperature() {
            return temperature;
        }

        public void setHumidity(String humidity) {
            this.humidity = humidity;
        }
        public String getHumidity() {
            return humidity;
        }

        public void setWind_direction(String wind_direction) {
            this.wind_direction = wind_direction;
        }
        public String getWind_direction() {
            return wind_direction;
        }

        public void setWind_speed(String wind_speed) {
            this.wind_speed = wind_speed;
        }
        public String getWind_speed() {
            return wind_speed;
        }


}
