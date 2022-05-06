package com.bytedance.jstu.p.apiclass;
import java.util.List;
import java.util.Date;
public class Results6DWBean
{
        private Location6DWBean location;
        private List<Daily6DWBean> daily;
        private Date last_update;
        public void setLocation(Location6DWBean location) {
            this.location = location;
        }
        public Location6DWBean getLocation() {
            return location;
        }

        public void setDaily(List<Daily6DWBean> daily) {
            this.daily = daily;
        }
        public List<Daily6DWBean> getDaily() {
            return daily;
        }

        public void setLast_update(Date last_update) {
            this.last_update = last_update;
        }
        public Date getLast_update() {
            return last_update;
        }


}
