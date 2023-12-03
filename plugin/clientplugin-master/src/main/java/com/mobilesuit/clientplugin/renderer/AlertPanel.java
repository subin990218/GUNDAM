package com.mobilesuit.clientplugin.renderer;

import com.mobilesuit.clientplugin.singleton.DataContainer;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AlertPanel extends JPanel {
    private final DataContainer dataContainer = DataContainer.getInstance();
    private final ImageIcon miaPing;
    private final ImageIcon kikenPing;
    private final ImageIcon helpPing;
    private final ImageIcon goingPing;
    private JLabel label;
    private JLabel remoteLabel;
    private boolean isLoad;
    private boolean isRemoteLoad;
    private final Map<String,Clip> audioClip;
    private final Map<String,Clip> audioClip2;

    public AlertPanel(){
        URL miaPingUrl = getClass().getResource("/images/mia.gif");
        URL kikenPingUrl = getClass().getResource("/images/kiken.gif");
        URL helpPingUrl = getClass().getResource("/images/help.gif");
        URL goingPingUrl = getClass().getResource("/images/going.gif");

        audioClip = new HashMap<>();
        audioClip2 = new HashMap<>();
        String[] iter = {"mia","kiken","help","going"};

        for(String str:iter){
            try {
                URL url = getClass().getResource("/sounds/" + str + ".wav");
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(url);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                volume.setValue(-10.0f);

                AudioInputStream audioStream2 = AudioSystem.getAudioInputStream(url);
                Clip clip2 = AudioSystem.getClip();
                clip2.open(audioStream2);
                FloatControl volume2 = (FloatControl) clip2.getControl(FloatControl.Type.MASTER_GAIN);
                volume2.setValue(-10.0f);

                audioClip.put(str,clip);
                audioClip2.put(str,clip2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        miaPing = new ImageIcon(miaPingUrl);
        kikenPing = new ImageIcon(kikenPingUrl);
        helpPing = new ImageIcon(helpPingUrl);
        goingPing = new ImageIcon(goingPingUrl);
        setLayout(null);
        setOpaque(false);
    }

    public void myPingDraw(Point point,String pingName){
        if(isLoad) return;
        isLoad = true;

        int delay = drawPing(label,point,pingName,false);

        Timer timer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isLoad = false;
                remove(label);
                repaint();
                getParent().repaint();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    public void otherPingDraw(Point point,String pingName,String userName){
        if(isRemoteLoad) return;
        isRemoteLoad = true;

        int delay = drawPing(remoteLabel,point,pingName,true);
        remoteLabel.setText(userName);
        remoteLabel.setHorizontalTextPosition(JLabel.CENTER);
        remoteLabel.setVerticalTextPosition(JLabel.BOTTOM);
        remoteLabel.setIconTextGap(-20);
        remoteLabel.setFont(new Font("Default", Font.PLAIN, 22));
        remoteLabel.setForeground(new Color(43, 125, 159));

        Timer timer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isRemoteLoad = false;
                remove(remoteLabel);
                repaint();
                getParent().repaint();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private int drawPing(JLabel whatLabel, Point point, String pingName, boolean fromRemote){

        runSound(pingName);
        Point point_ide;
        if(fromRemote) point_ide = new Point(-50,-50);
        else point_ide = dataContainer.getMainFrame().getLocationOnScreen();

        int delay = 0;
        if(pingName.equals("kiken")){
            whatLabel = new JLabel(kikenPing);
            whatLabel.setBounds(point.x-miaPing.getIconWidth()/2-point_ide.x
                    ,point.y-miaPing.getIconHeight()/2-point_ide.y
                    ,miaPing.getIconWidth()
                    ,miaPing.getIconHeight());
            delay = 500;
        }
        else if(pingName.equals("mia")){
            whatLabel = new JLabel(miaPing);
            whatLabel.setBounds(point.x-miaPing.getIconWidth()/2-point_ide.x
                    ,point.y-miaPing.getIconHeight()/2-point_ide.y
                    ,miaPing.getIconWidth()
                    ,miaPing.getIconHeight());
            delay = 600;
        }
        else if(pingName.equals("help")){
            whatLabel = new JLabel(helpPing);
            delay = 700;
            whatLabel.setBounds(point.x-miaPing.getIconWidth()/2-point_ide.x
                    ,point.y-miaPing.getIconHeight()/2-point_ide.y-50
                    ,miaPing.getIconWidth()
                    ,miaPing.getIconHeight()+50);
        }
        else if(pingName.equals("going")){
            whatLabel = new JLabel(goingPing);
            whatLabel.setBounds(point.x-goingPing.getIconWidth()/3-point_ide.x
                    ,point.y-goingPing.getIconHeight()/3*2-point_ide.y
                    ,goingPing.getIconWidth()
                    ,goingPing.getIconHeight());
            delay = 1100;
        }
        add(whatLabel);
        if(!fromRemote) label = whatLabel;
        else remoteLabel = whatLabel;
        return delay;
    }

    private void runSound(String pingName){
        new Thread(()->{
            try{
                if(audioClip.get(pingName).isRunning()){
                    audioClip2.get(pingName).setFramePosition(0);  // 사운드를 처음부터 재생
                    audioClip2.get(pingName).start();
                    Thread.sleep(audioClip2.get(pingName).getMicrosecondLength() / 1000);
                }else {
                    audioClip.get(pingName).setFramePosition(0);  // 사운드를 처음부터 재생
                    audioClip.get(pingName).start();
                    Thread.sleep(audioClip.get(pingName).getMicrosecondLength() / 1000);
                }
                //audioClip.get(pingName).stop();
            }catch (Exception e){
                e.printStackTrace();
            }
        }).start();

    }

    /*public void pingShow(Point point,String pingName){
        if(isLoad) return;
        isLoad = true;

        Point point_ide = dataContainer.getMainFrame().getLocationOnScreen();

        setVisible(true);

        int delay = 0;
        if(pingName.equals("kiken")){
            label = new JLabel(kikenPing);
            label.setBounds(point.x-miaPing.getIconWidth()/2-point_ide.x
                    ,point.y-miaPing.getIconHeight()/2,miaPing.getIconWidth()-point_ide.y
                    ,miaPing.getIconHeight());
            delay = 500;
            System.out.println(kikenPing.getIconWidth());
            System.out.println(kikenPing.getIconHeight());
            label.setVisible(true);
            System.out.println(label.getBounds());
            System.out.println("그리는 중 !"+point);
            repaint();
            System.out.println(getBounds());
        }
        else if(pingName.equals("mia")){
            label = new JLabel(miaPing);
            label.setBounds(point.x-miaPing.getIconWidth()/2-point_ide.x
                    ,point.y-miaPing.getIconHeight()/2,miaPing.getIconWidth()-point_ide.y
                    ,miaPing.getIconHeight());
            delay = 600;
        }
        else if(pingName.equals("help")){
            label = new JLabel(helpPing);
            delay = 700;
            label.setBounds(point.x-miaPing.getIconWidth()/2-point_ide.x
                    ,point.y-miaPing.getIconHeight()/2,miaPing.getIconWidth()-point_ide.y
                    ,miaPing.getIconHeight());
        }
        else if(pingName.equals("going")){
            label = new JLabel(goingPing);
            label.setBounds(point.x-goingPing.getIconWidth()/3-point_ide.x
                    ,point.y-goingPing.getIconHeight()/3*2,goingPing.getIconWidth()-point_ide.y
                    ,goingPing.getIconHeight());
            delay = 1100;
        }


        add(label);

        Timer timer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isLoad = false;
                System.out.println("끝아님?");
                setVisible(false);
                remove(label);
                repaint();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    public void showPing(Point point,String pingName){

        if(isRemoteLoad) return;
        isRemoteLoad = true;

        Point point_ide = dataContainer.getMainFrame().getLocationOnScreen();

        setVisible(true);

        int delay = 0;
        if(pingName.equals("kiken")){
            remoteLabel = new JLabel(kikenPing);
            remoteLabel.setBounds(point.x-miaPing.getIconWidth()/2-point_ide.x
                    ,point.y-miaPing.getIconHeight()/2,miaPing.getIconWidth()-point_ide.y
                    ,miaPing.getIconHeight());
            delay = 500;
            System.out.println(kikenPing.getIconWidth());
            System.out.println(kikenPing.getIconHeight());
            remoteLabel.setVisible(true);
            System.out.println("오는 핑 그리는 중 !"+point);
            repaint();
            System.out.println(getBounds());
        }
        else if(pingName.equals("mia")){
            remoteLabel = new JLabel(miaPing);
            remoteLabel.setBounds(point.x-miaPing.getIconWidth()/2-point_ide.x
                    ,point.y-miaPing.getIconHeight()/2,miaPing.getIconWidth()-point_ide.y
                    ,miaPing.getIconHeight());
            delay = 600;
        }
        else if(pingName.equals("help")){
            remoteLabel = new JLabel(helpPing);
            delay = 700;
            remoteLabel.setBounds(point.x-miaPing.getIconWidth()/2-point_ide.x
                    ,point.y-miaPing.getIconHeight()/2,miaPing.getIconWidth()-point_ide.y
                    ,miaPing.getIconHeight());
        }
        else if(pingName.equals("going")){
            remoteLabel = new JLabel(goingPing);
            remoteLabel.setBounds(point.x-goingPing.getIconWidth()/3-point_ide.x
                    ,point.y-goingPing.getIconHeight()/3*2,goingPing.getIconWidth()-point_ide.y
                    ,goingPing.getIconHeight());
            delay = 1100;
        }


        add(remoteLabel);

        Timer timer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isRemoteLoad = false;
                System.out.println("끝아님?");
                setVisible(false);
                remove(remoteLabel);
                repaint();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }*/

    @Override
    protected void paintComponent(Graphics g) {
        g.clearRect(0, 0, getWidth(), getHeight());
        //g.drawRect(100+(int)(Math.random()*100),100+(int)(Math.random()*100),100,100);
        super.paintComponent(g);
    }
}
