package com.robprane.game;

import java.util.Random;

class Star {
    private int x;
    private int y;
    private int speed;

    private int maxX;
    private int maxY;

    Star(int screenX, int screenY) {
        maxX = screenX;
        maxY = screenY;
        Random generator = new Random();
        speed = generator.nextInt(10);

        x = generator.nextInt(maxX);
        y = generator.nextInt(maxY);
    }

    void update(boolean boosting, int playerSpeed) {
        if (boosting) {
            speed += playerSpeed / 10;
            x -= speed;
        } else {
            x -= setSpeed(playerSpeed);
        }
        if (x < 0) {
            Random generator = new Random();
            x = maxX + generator.nextInt(maxX / 2);
            y = generator.nextInt(maxY);
            speed = generator.nextInt(10);
        }
    }

    float getStarWidth() {
        float minX = 1.0f;
        float maxX = 4.0f;
        Random rand = new Random();
        return rand.nextFloat() * (maxX - minX) + minX;
    }

    private float setSpeed(int playerSpeed) {
        return speed + playerSpeed;
    }

    float getSpeed() {
        return speed;
    }

    int getX() {
        return x;
    }

    int getY() {
        return y;
    }
}