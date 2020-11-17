package com.ctv.settings.security.bean;

public class PermissionBean {
    private String name; //中文名
    private String groupName ; //英文名字
    private boolean Enabled;
    private boolean Checked;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isEnabled() {
        return Enabled;
    }

    public void setEnabled(boolean enabled) {
        Enabled = enabled;
    }

    public boolean isChecked() {
        return Checked;
    }

    public void setChecked(boolean checked) {
        Checked = checked;
    }
    public PermissionBean(){

    }
    public PermissionBean(String name,String groupName, boolean Enabled, boolean Checked) {
        this.name = name;
        this.groupName = groupName;
        this.Enabled = Enabled;
        this.Checked = Checked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
