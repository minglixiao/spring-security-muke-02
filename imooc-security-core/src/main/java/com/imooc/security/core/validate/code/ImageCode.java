package com.imooc.security.core.validate.code;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;

public class ImageCode {

    // 验证码图片
    private BufferedImage image;

    // 随机数
    private String code;

    // 验证码过期时间。举例：到2020-07-01 15:31:00验证码过期
    private LocalDateTime expireTime;


    public  boolean isExpried(){
        return LocalDateTime.now().isAfter(expireTime);
    }

    /**
     * @param code
     * @param image
     * @param expireTimeIn : 举例：从现在起，有效期600秒，600秒之后验证码过期。
     */
    public ImageCode(BufferedImage image, String code,  int expireTimeIn){
        this.code=code;
        this.image=image;
        this.expireTime=LocalDateTime.now().plusSeconds(expireTimeIn);
    }


    public ImageCode( BufferedImage image, String code, LocalDateTime expireTime){
        this.code=code;
        this.image=image;
        this.expireTime=expireTime;
    }



    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
}
