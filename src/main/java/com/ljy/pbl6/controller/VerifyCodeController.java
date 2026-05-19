package com.ljy.pbl6.controller;

import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class VerifyCodeController {

    private static final int WIDTH = 100;
    private static final int HEIGHT = 44;
    private static final int CODE_LENGTH = 4;
    
    private static final ConcurrentHashMap<String, String> codeCache = new ConcurrentHashMap<>();
    
    private static final String CHAR_POOL = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz0123456789";

    @GetMapping("/api/verifyCode")
    public void getVerifyCode(HttpServletResponse response) throws IOException {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        Random random = new Random();
        
        for (int i = 0; i < 20; i++) {
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            int w = random.nextInt(10) + 1;
            int h = random.nextInt(10) + 1;
            g.setColor(getRandomColor(160, 250));
            g.drawOval(x, y, w, h);
        }

        for (int i = 0; i < 3; i++) {
            int x1 = random.nextInt(WIDTH);
            int y1 = random.nextInt(HEIGHT);
            int x2 = random.nextInt(WIDTH);
            int y2 = random.nextInt(HEIGHT);
            g.setColor(getRandomColor(100, 200));
            g.drawLine(x1, y1, x2, y2);
        }

        String code = generateCode(random);
        
        Font font = new Font("Arial", Font.BOLD, 28);
        g.setFont(font);
        
        for (int i = 0; i < code.length(); i++) {
            char c = code.charAt(i);
            g.setColor(getRandomColor(50, 160));
            int x = 15 + i * 22;
            int y = 32 + random.nextInt(8) - 4;
            double rotate = (random.nextDouble() - 0.5) * 0.3;
            g.rotate(rotate, x, y);
            g.drawString(String.valueOf(c), x, y);
            g.rotate(-rotate, x, y);
        }

        g.dispose();

        String codeKey = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        codeCache.put(codeKey, code);
        
        new Thread(() -> {
            try {
                Thread.sleep(300000);
                codeCache.remove(codeKey);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();

        response.setContentType("image/png");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        
        ImageIO.write(image, "png", response.getOutputStream());
    }

    private String generateCode(Random random) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CHAR_POOL.charAt(random.nextInt(CHAR_POOL.length())));
        }
        return sb.toString();
    }

    private Color getRandomColor(int min, int max) {
        Random random = new Random();
        int r = min + random.nextInt(max - min);
        int g = min + random.nextInt(max - min);
        int b = min + random.nextInt(max - min);
        return new Color(r, g, b);
    }

    public static boolean validateCode(String code) {
        if (code == null || code.isEmpty()) {
            return false;
        }
        String upperCode = code.toUpperCase();
        return codeCache.values().stream().anyMatch(c -> c.equalsIgnoreCase(code));
    }

    public static boolean validateAndRemoveCode(String code) {
        if (code == null || code.isEmpty()) {
            return false;
        }
        String upperCode = code.toUpperCase();
        for (java.util.Map.Entry<String, String> entry : codeCache.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(code)) {
                codeCache.remove(entry.getKey());
                return true;
            }
        }
        return false;
    }
}
