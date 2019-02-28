package com.wangxingxing.demo.videocache;

import com.danikula.videocache.util.EncryptUtils;

public class Test {

    private String url = "";

    private void de() {
        if (!url.startsWith("file") && !url.endsWith("ping")) {
            url = new String(EncryptUtils.decryptHexStringDES(url, "ashd0303".getBytes(), "DES/ECB/PKCS5Padding", null));
        }
    }

    private void en() {
        String encryptUrl = EncryptUtils.encryptDES2HexString(url.getBytes(), "ashd0303".getBytes(), "DES/ECB/PKCS5Padding", null);
    }
}
