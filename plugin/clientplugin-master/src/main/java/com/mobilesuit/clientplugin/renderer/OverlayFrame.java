package com.mobilesuit.clientplugin.renderer;

import javax.swing.*;
import java.awt.*;

public class OverlayFrame extends JWindow {

    public OverlayFrame(Frame owner) {
        super(owner);
        //setUndecorated(true); // 제목 표시줄과 경계선 제거
        System.out.println("SIZE");
        System.out.println(owner.getWidth());
        setSize(owner.getWidth(), owner.getHeight()); // 윈도우 크기 설정
        setLocationRelativeTo(null); // 중앙 위치

        setBackground(new Color(0, 0, 0, 0)); // 배경을 투명하게 설정

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image img = Toolkit.getDefaultToolkit().getImage("D:\\Image\\KakaoTalk_20231017_213618519.png");
                g.drawImage(img, 0, 0, this);
            }
        };


        owner.add(panel);

        panel.setOpaque(false); // 패널을 불투명하지 않게 설정

        add(panel);



    }
}
