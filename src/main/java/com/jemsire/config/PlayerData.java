package com.jemsire.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class PlayerData {
    public static final BuilderCodec<PlayerData> CODEC = BuilderCodec.builder(PlayerData.class, PlayerData::new)
            .append(new KeyedCodec<Integer>("Lives", Codec.INTEGER),
                    (data, value, info) -> data.lives = value != null ? value : 0,
                    (data, info) -> data.lives).add()
            .append(new KeyedCodec<Long>("LastDeathTime", Codec.LONG),
                    (data, value, info) -> data.lastDeathTime = value != null ? value : 0L,
                    (data, info) -> data.lastDeathTime).add()
            .build();

    private int lives;
    private long lastDeathTime;

    public PlayerData() {
        this.lives = 0;
        this.lastDeathTime = 0;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public long getLastDeathTime() {
        return lastDeathTime;
    }

    public void setLastDeathTime(long lastDeathTime) {
        this.lastDeathTime = lastDeathTime;
    }
}
