package com.android.jdrd.headcontrol.entity;

import java.util.List;

/**
 * Created by Administrator on 2016/11/4 0004.
 */

public class Clean4 {


    /**
     * timer : [{"flag":1,"endtime":9,"starttime":8}]
     * surface : 1
     */

    private DataBean data;
    /**
     * data : {"timer":[{"flag":1,"endtime":9,"starttime":8}],"surface":1}
     * function : clean
     * type : param
     */

    private String function;
    private String type;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static class DataBean {
        private int surface;
        /**
         * flag : 1
         * endtime : 9
         * starttime : 8
         */

        private List<TimerBean> timer;

        public int getSurface() {
            return surface;
        }

        public void setSurface(int surface) {
            this.surface = surface;
        }

        public List<TimerBean> getTimer() {
            return timer;
        }

        public void setTimer(List<TimerBean> timer) {
            this.timer = timer;
        }

        public static class TimerBean {
            private int flag;
            private float endtime;
            private float starttime;

            public int getFlag() {
                return flag;
            }

            public void setFlag(int flag) {
                this.flag = flag;
            }

            public float getEndtime() {
                return endtime;
            }

            public void setEndtime(float endtime) {
                this.endtime = endtime;
            }

            public float getStarttime() {
                return starttime;
            }

            public void setStarttime(float starttime) {
                this.starttime = starttime;
            }
        }
    }
}
