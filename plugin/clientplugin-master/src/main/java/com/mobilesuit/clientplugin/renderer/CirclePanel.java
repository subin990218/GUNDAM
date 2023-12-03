package com.mobilesuit.clientplugin.renderer;

import com.mobilesuit.clientplugin.singleton.DataContainer;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

/*public class CirclePanel extends JPanel implements ActionListener {
    private int x, y; // 현재 위치
    private final int radius = 50; // 원의 반지름
    private char alphabet; // 표시할 알파벳
    private Color color; // 동그라미 색상

    public CirclePanel() {
        setPreferredSize(new Dimension(800, 600)); // 패널 크기 설정

        Random random = new Random();
        alphabet = (char) (random.nextInt(26) + 'A'); // A부터 Z까지 랜덤 알파벳 선택
        color = getRandomColor(); // 랜덤 색상 선택
        setOpaque(true);
        setBackground(new Color(0, 0, 0, 0));
        Timer timer = new Timer(1000, this); // 1초마다 actionPerformed 호출하도록 타이머 설정
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(color);
        g.fillOval(x - radius, y - radius, radius * 2, radius * 2); // 원 그리기

        Font font = new Font("Arial", Font.BOLD, 24);
        g.setFont(font);

        FontMetrics metrics = g.getFontMetrics(font);
        int textWidth = metrics.stringWidth(String.valueOf(alphabet));
        int textHeight = metrics.getHeight();

        g.setColor(Color.WHITE);
        g.drawString(String.valueOf(alphabet), x - (textWidth / 2), y + (textHeight / 4));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Random random = new Random();

        x = random.nextInt(getWidth() - radius * 2) + radius;
        y = random.nextInt(getHeight() - radius * 2) + radius;

        alphabet++;

        repaint(); // 화면 다시 그리기 요청
    }

    private Color getRandomColor() {
        Random randomColorGenerator= new Random();
        float r= randomColorGenerator.nextFloat();
        float g= randomColorGenerator.nextFloat();
        float b= randomColorGenerator.nextFloat();

        return new Color(r,g,b);
    }
}*/
@Slf4j
public class CirclePanel extends JPanel {
    private final DataContainer dataContainer = DataContainer.getInstance();
    private final int radius = 10; // 원의 반지름
    private Color color; // 동그라미 색상
    private int x, y; // 좌표

    private String initial;

    private float alpha = 1.0f; // 투명도 초기값
    private Timer timer; // Timer를 클래스 변수로 선언

    public CirclePanel(int x, int y,String userName) {
        this.x = x;
        this.y = y;
        this.initial = userName.substring(0,1);

        setOpaque(false); // 배경 투명 설정

        Random randomColorGenerator= new Random();
        float r= randomColorGenerator.nextFloat();
        float g= randomColorGenerator.nextFloat();
        float b= randomColorGenerator.nextFloat();

        color = new Color(r,g,b);

        setBounds(x - radius, y - radius, radius * 2, radius * 2); // 위치 및 크기 설정
        log.info("circle Drawing : "+userName);
    }

    public CirclePanel(int x, int y,String userName,Color color) {
        this.x = x;
        this.y = y;
        this.initial = userName.substring(0,1);

        setOpaque(false); // 배경 투명 설정

        this.color = color;

        setBounds(x - radius, y - radius, radius * 2, radius * 2); // 위치 및 크기 설정
        log.info("circle Drawing : "+userName);
    }

    public boolean isEnded(){
        return this.alpha==0;
    }

    public void startDisappearAnimation() {
        // Timer 생성
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha)); // 투명도 설정

        g.setColor(color);
        g.fillOval(0, 0, getWidth(), getHeight()); // 원 그리기

        Font font = new Font("Arial", Font.BOLD, 10);
        g.setFont(font);

        FontMetrics metrics = g.getFontMetrics(font);
        int textWidth = metrics.stringWidth(initial);
        int textHeight = metrics.getHeight();

        g.setColor(Color.WHITE);
        g.drawString(initial, getWidth() / 2 - (textWidth / 2), getHeight() / 2 + (textHeight / 4));
    }
}