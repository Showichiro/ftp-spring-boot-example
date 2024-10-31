package com.example.demo.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.ftp.session.FtpRemoteFileTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FTPService {
    @Autowired
    private FtpRemoteFileTemplate ftpRemoteFileTemplate;

    // ファイルの読み込み
    public String readFile(String remoteFilePath) {
        try {
            return ftpRemoteFileTemplate.execute(session -> {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                session.read(remoteFilePath, outputStream);
                return new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
            });
        } catch (Exception e) {
            log.error("Failed to read file: " + remoteFilePath, e);
            throw new RuntimeException("File read error", e);
        }
    }

    public void writeFile(String remoteFilePath, String content) {
        try {
            ftpRemoteFileTemplate.execute(session -> {
                InputStream inputStream = new ByteArrayInputStream(
                        content.getBytes(StandardCharsets.UTF_8));
                session.write(inputStream, remoteFilePath);
                return null;
            });
        } catch (Exception e) {
            log.error("Failed to write file: " + remoteFilePath, e);
            throw new RuntimeException("File write error", e);
        }
    }

    // ファイルの削除
    public void deleteFile(String remoteFilePath) {
        try {
            ftpRemoteFileTemplate.execute(session -> {
                session.remove(remoteFilePath);
                return null;
            });
        } catch (Exception e) {
            log.error("Failed to delete file: " + remoteFilePath, e);
            throw new RuntimeException("File delete error", e);
        }
    }
}
