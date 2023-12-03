package com.mobilesuit.clientplugin.renderer;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class PingPanel extends JPanel {


    private final Image image, select, center;
    private char dir;

    public PingPanel() {
        URL imageUrl = getClass().getResource("/images/ping_select.png");
        String filePath = imageUrl.getFile();
        System.out.println("위치" + filePath);
        URL selectUrl = getClass().getResource("/images/ping_hover_resize.png");
        URL centerUrl = getClass().getResource("/images/ping_center.png");


        dir = 'X';
        // 상대경로로 받아와야 합니다
        this.image = new ImageIcon(imageUrl).getImage();
        this.select = new ImageIcon(selectUrl).getImage();
        this.center = new ImageIcon(centerUrl).getImage();
        setVisible(false);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.clearRect(0, 0, getWidth(), getHeight()); // 궤적 지우기, 새 이미지 그리기
        super.paintComponent(g);
        Graphics2D graphics2D = (Graphics2D) g;
        graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g.drawImage(image, 0, 0, this);
        if (dir != 'X') {
            int centerX = select.getWidth(this) / 2;
            int centerY = select.getHeight(this) / 2;

            Graphics2D g2d = (Graphics2D) g.create();
            g2d.translate(centerX, centerY);

            int rot = switch (dir) {
                case 'U' -> 180;
                case 'L' -> 90;
                case 'R' -> -90;
                default -> 0;
            };

            g2d.rotate(Math.toRadians(rot));
            g2d.translate(-centerX, -centerY);
            g2d.drawImage(select, 0, 0, this);
            g2d.dispose();
        }
        graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        g.drawImage(center, select.getWidth(this) / 2 - center.getWidth(this) / 2,
                select.getHeight(this) / 2 - center.getHeight(this) / 2,
                this);
    }

    public void setDir(char dir) {
        this.dir = dir;
    }
}
