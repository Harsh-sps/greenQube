
package com.aistream.greenqube.chromecast;

/**
 * 开放给GUI的监听器
 */
public interface CastSessionListener {
    void onConnect();

    void onDisconnect();
}