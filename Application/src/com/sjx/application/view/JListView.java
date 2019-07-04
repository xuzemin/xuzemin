package com.sjx.application.view;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
public class JListView extends JFrame{
    private static final int DEFAULT_WIDTH=400;
    private static final int DEFAULT_HEIGHT=300;
    private JPanel listPanel;
    private JList<String> wordlist;
    private JLabel label;
    private JPanel buttonPanel;
    private ButtonGroup group;
    private String prefix="The ";
    private String suffix="fox jump over the lazy dog.";
	public JListView() {
		String[] words = {"quick","brown","hungry","wild","silent","huge","private","abstract","static","final"};
        wordlist = new JList<>(words);
        wordlist.setVisibleRowCount(10);
        JScrollPane scrollPane = new JScrollPane(wordlist);
        listPanel = new JPanel();
        listPanel.add(scrollPane);
        buttonPanel = new JPanel();
        group = new ButtonGroup();
        makeButton("Vertical", JList.VERTICAL);
        makeButton("Vertical Wrap",JList.VERTICAL_WRAP);
        makeButton("Horizontal Wrap",JList.HORIZONTAL_WRAP);
        
        this.add(listPanel, BorderLayout.NORTH);
        label = new JLabel(prefix+suffix);
        this.add(label, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.SOUTH);
        this.setTitle("ListTest");
        this.setVisible(true);
        this.setLocation(300,300);
        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
    /**
     * 设置按钮
     * @param string 按钮名字
     * @param vertical 按钮类型
     */
    private void makeButton(String string, final int vertical) {
        JRadioButton button = new JRadioButton(string);
        buttonPanel.add(button);
        if(group.getButtonCount()==0) button.setSelected(true);
        group.add(button);
        button.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                wordlist.setLayoutOrientation(vertical);
                listPanel.revalidate();            //重新布局并绘制    
            }
            
        });
        
    }
}



class MyListModel extends AbstractListModel<String> {
	private static final long serialVersionUID = 1L;
	// 设置列表框内容
	private String[] contents = { "列表 1", "列表 2", "列表 3", "列表 4", "列表 5", "列表 6" };
	@Override
	public String getElementAt(int index) {
		if (index < contents.length) {
			return contents[index++];
		} else {
			return null;
		}
	}
	
	@Override
	public int getSize() {
		return contents.length;
	}
}

