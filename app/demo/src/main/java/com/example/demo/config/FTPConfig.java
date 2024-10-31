package com.example.demo.config;

import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.integration.ftp.session.FtpRemoteFileTemplate;

@Configuration
public class FTPConfig {
    @Value("${ftp.host}")
    private String host;

    @Value("${ftp.port}")
    private int port;

    @Value("${ftp.username}")
    private String username;

    @Value("${ftp.password}")
    private String password;

    @Bean
    DefaultFtpSessionFactory ftpSessionFactory() {
        DefaultFtpSessionFactory factory = new DefaultFtpSessionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        // パッシブモードの設定
        factory.setClientMode(FTPClient.PASSIVE_LOCAL_DATA_CONNECTION_MODE);
        
        // 接続タイムアウトの設定
        factory.setConnectTimeout(5000);
        
        // 追加のFTPクライアント設定
        factory.setControlEncoding("UTF-8");
        factory.setDefaultTimeout(5000);
        factory.setDataTimeout(5000);
        return factory;
    }

    @Bean
    FtpRemoteFileTemplate ftpTemplate(DefaultFtpSessionFactory ftpSessionFactory) {
        FtpRemoteFileTemplate template = new FtpRemoteFileTemplate(ftpSessionFactory);
        template.setAutoCreateDirectory(true);
        return template;
    }
}
