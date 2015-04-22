package com.unigames.service;

import com.mysql.jdbc.StringUtils;
import com.unigames.exceptions.InternalException;
import com.unigames.exceptions.RequestException;
import com.unigames.model.GameRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.*;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Component
public class GameRecordService {

    private static final Logger logger = LoggerFactory.getLogger(GameRecordService.class);
    private static final int BUFFER_SIZE = 4096;
    @Value("${record.dir}")
    private String recordDir;
    @Autowired
    private DataSource dataSource;

    public static String FileName2Path(String fileName) {
        int hashcode = fileName.hashCode();
        int mask = 255;
        int firstDir = hashcode & mask;
        int secondDir = (hashcode >> 8) & mask;
        StringBuilder path = new StringBuilder(File.separator);
        path.append(String.format("%02x", firstDir));
        path.append(File.separator);
        path.append(String.format("%02x", secondDir));
        return path.toString();
    }

    public void addRecord(String deviceId, String deviceVersion, String gameId, String gameVersion,
                          MultipartFile recordFile) {

        String recordId = recordFile.getOriginalFilename();
        String filePath = FileName2Path(recordId);
        File _file = new File(recordDir + File.separator + filePath + File.separator + recordId);
        if (_file.isFile()) {
            logger.warn("File:" + _file.getPath() + " exists.");
            throw new RequestException();
        }

        if (!_file.getParentFile().exists()) {
            if (!_file.getParentFile().mkdirs()) {
                logger.warn("Make dir:" + _file.getParent() + " failed.");
                throw new InternalException();
            }
        }

        try {
            byte[] bytes = recordFile.getBytes();
            BufferedOutputStream buffStream = new BufferedOutputStream(
                    new FileOutputStream(_file));
            buffStream.write(bytes);
            buffStream.close();
        } catch (IOException e) {
            logger.warn("Save file:" + _file.getPath() + " failed.");
            throw new InternalException();
        }
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String statement = "INSERT INTO records(recordId,deviceId,deviceVersion,gameId,gameVersion,uploadTime) VALUES (?,?,?,?,?,NOW())";
        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(statement);
            preparedStatement.setString(1, recordId);
            preparedStatement.setString(2, deviceId);
            preparedStatement.setString(3, deviceVersion);
            preparedStatement.setString(4, gameId);
            preparedStatement.setString(5, gameVersion);
            preparedStatement.execute();
        } catch (SQLException e) {
            logger.warn("Add record to mysql for record:" + recordId + " failed.");
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ignored) {

            }
            throw new InternalException();
        }
    }

    public List<GameRecord> listRecords(String deviceId, String deviceVersion, String gameId, String gameVersion, String recordId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String statement = "SELECT deviceId,deviceVersion,gameId,gameVersion,recordId,uploadTime,recordDesc FROM records";
        String condition = " WHERE";
        String order = " ORDER BY uploadTime DESC";
        List<String> params = new ArrayList<>();
        if (!StringUtils.isNullOrEmpty(deviceId)) {
            statement += condition + " deviceId = ?";
            condition = " AND";
            params.add(deviceId);

            if (!StringUtils.isNullOrEmpty(deviceVersion)) {
                statement += condition + " deviceVersion = ?";
                condition = " AND";
                params.add(deviceVersion);
            }
        }

        if (!StringUtils.isNullOrEmpty(gameId)) {
            statement += condition + " gameId = ?";
            condition = " AND";
            params.add(gameId);

            if (!StringUtils.isNullOrEmpty(gameVersion)) {
                statement += condition + " gameVersion = ?";
                condition = " AND";
                params.add(gameVersion);
            }
        }

        if (!StringUtils.isNullOrEmpty(recordId)) {
            statement += condition + " recordId = ?";
            params.add(recordId);
        }
        statement += order;
        List<GameRecord> result = new ArrayList<>();
        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(statement);
            int i = 1;
            for (String param : params) {
                preparedStatement.setString(i++, param);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                GameRecord gameRecord = new GameRecord();
                gameRecord.setDeviceId(resultSet.getString("deviceId"));
                gameRecord.setDeviceVersion(resultSet.getString("deviceVersion"));
                gameRecord.setGameId(resultSet.getString("gameId"));
                gameRecord.setGameVersion(resultSet.getString("gameVersion"));
                gameRecord.setRecordId(resultSet.getString("recordId"));
                gameRecord.setRecordDesc(resultSet.getString("recordDesc"));
                gameRecord.setUploadTime(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(resultSet.getTimestamp("uploadTime")));
                result.add(gameRecord);
            }
        } catch (SQLException e) {
            logger.warn("Query records failed.");
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ignored) {

            }
            throw new InternalException();
        }
        return result;
    }

    public void downloadRecord(String recordId, HttpServletResponse response) {
        String filePath = FileName2Path(recordId);
        File _file = new File(recordDir + File.separator + filePath + File.separator + recordId);

        response.setContentType("application/octet-stream");
        response.setContentLength((int) _file.length());
        String headerKey = "Content-Disposition";
        String headerValue;
        try {
            headerValue = String.format("attachment; filename=\" %s\";filename*=UTF-8''%s", _file.getName(), URLEncoder.encode(_file.getName(), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new InternalException();
        }
        response.setHeader(headerKey, headerValue);

        try {
            InputStream inputStream = new FileInputStream(_file);
            OutputStream outStream = response.getOutputStream();
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            outStream.close();
        } catch (FileNotFoundException e) {
            logger.warn("File:" + _file.getPath() + " does not exist.");
            throw new InternalException();
        } catch (IOException e) {
            logger.warn("Get output stream failed.");
            throw new InternalException();
        }
    }

    public void updateRecord(String recordId, String recordDesc) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String statement = "UPDATE records set recordDesc = ? WHERE recordId = ?";
        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(statement);
            preparedStatement.setString(1, recordDesc);
            preparedStatement.setString(2, recordId);
            int updated = preparedStatement.executeUpdate();
            if (updated == 0) {
                logger.warn("Record:" + recordId + " does not exist.");
                throw new RequestException();
            }
        } catch (SQLException e) {
            logger.warn("Query records failed.");
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ignored) {

            }
            throw new InternalException();
        }
    }

    public GameRecord getRecord(String recordId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String statement = "SELECT deviceId,deviceVersion,gameId,gameVersion,recordId,uploadTime,recordDesc FROM records WHERE recordId = ?";

        try {
            connection = dataSource.getConnection();
            preparedStatement = connection.prepareStatement(statement);
            preparedStatement.setString(1, recordId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                GameRecord gameRecord = new GameRecord();
                gameRecord.setDeviceId(resultSet.getString("deviceId"));
                gameRecord.setDeviceVersion(resultSet.getString("deviceVersion"));
                gameRecord.setGameId(resultSet.getString("gameId"));
                gameRecord.setGameVersion(resultSet.getString("gameVersion"));
                gameRecord.setRecordId(resultSet.getString("recordId"));
                gameRecord.setRecordDesc(resultSet.getString("recordDesc"));
                gameRecord.setUploadTime(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(resultSet.getTimestamp("uploadTime")));
                return gameRecord;
            } else {
                logger.warn("Record:" + recordId + " does not exist.");
                throw new RequestException();
            }
        } catch (SQLException e) {
            logger.warn("Query records failed.");
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ignored) {

            }
            throw new InternalException();
        }
    }
}
