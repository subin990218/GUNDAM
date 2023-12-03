package com.mobilesuit.clientplugin.renderer;

import com.mobilesuit.clientplugin.singleton.DataContainer;

import javax.swing.*;
import java.awt.*;

public class LivePanel extends JPanel {
    private final DataContainer dataContainer = DataContainer.getInstance();

    @Override
    public void updateUI() {
        super.updateUI();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.BLUE); // 색상 설정
        g.drawRect(0, 0, dataContainer.getMainFrame().getWidth(), dataContainer.getMainFrame().getHeight()); // 패널 전체에 파란색 네모 그리기
        //System.out.println("It draws on "+ this.getLocation() + "with" + g.getClipBounds());
    }
}
