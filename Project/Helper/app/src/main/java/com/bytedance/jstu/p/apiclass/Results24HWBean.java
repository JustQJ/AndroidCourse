package com.bytedance.jstu.p.apiclass;
import java.util.List;
public class Results24HWBean {




        private Location24HWBean location;
        private List<Hourly24HWBean> hourly;
        public void setLocation(Location24HWBean location) {
            this.location = location;
        }
        public Location24HWBean getLocation() {
            return location;
        }

        public void setHourly(List<Hourly24HWBean> hourly) {
            this.hourly = hourly;
        }
        public List<Hourly24HWBean> getHourly() {
            return hourly;
        }


}
