package com.goktug.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductViewEvent {
    private String event;
    private String messageid;
    private String userid;
    private Properties properties;
    private Context context;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Properties {
        private String productid;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Context {
        private String source;
    }
}
