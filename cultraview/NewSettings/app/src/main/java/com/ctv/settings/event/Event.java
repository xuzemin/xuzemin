package com.ctv.settings.event;

/**
 * Desc:事件类集合
 *
 * @author wang
 * @time 2017/8/24.
 */
public class Event {
    /**
     * 更新信号列表事件
     */
	public static class UpdateSignalList {
		public UpdateSignalList() {
        }
	}

	/**
     * 更新亮度
     */
    public static class UpdateLightEvent {
        public Integer state = -1;
        public UpdateLightEvent(){}
        public UpdateLightEvent(Integer state) {
            this.state = state;
        }
    }

    /**
     * 更新音量
     */
    public static class UpdateVoiceBarEvent {
        public boolean state;

        public UpdateVoiceBarEvent(boolean state) {
            this.state = state;
        }
    }

    /**
     * 更新禁音
     */
    public static class UpdateMuteEvent {
    }

}
