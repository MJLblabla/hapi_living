package com.hapi.chat;

/**
 *
 * @date 2019/2/18
 */
public interface UserinfoProvider {

    String getUid();

    String getNickName();

    String getAvatar();

    String getSign();

    int getLevel();

    UserinfoProvider DEFAULT = new UserinfoProvider() {
        @Override
        public String getUid() {
            return "";
        }

        @Override
        public String getNickName() {
            return "";
        }

        @Override
        public String getAvatar() {
            return "";
        }

        @Override
        public String getSign() {
            return "";
        }

        @Override
        public int getLevel() {
            return 1;
        }
    };
}