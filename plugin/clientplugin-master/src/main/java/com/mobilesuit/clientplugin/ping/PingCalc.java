package com.mobilesuit.clientplugin.ping;

import java.awt.*;

public class PingCalc {

    private static final int LIMIT_LEN = 20;

    public static char getDir(Point startPoint, Point endPoint) {
        double y = -endPoint.y + startPoint.y;

        double x = endPoint.x - startPoint.x;

        double len = Math.sqrt(y * y + x * x);

        if (len < LIMIT_LEN) return 'X';

        y /= len;
        x /= len;

        double deg = Math.atan2(y, x) * 180 / Math.PI;

        if (deg >= 45 && deg < 135) return 'U';
        else if (deg < -45 && deg >= -135) return 'D';
        else if (deg >= 135 || deg < -135) return 'L';
        else if (deg >= -45 || deg < 45) return 'R';
        return 'X';
    }
}
