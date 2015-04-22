package com.unigames.controller;

import com.unigames.exceptions.RequestException;
import com.unigames.model.GameRecord;
import com.unigames.service.GameRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping(value = "/record")
public class GameRecordController {
    @Autowired
    private GameRecordService gameRecordService;

    @RequestMapping(value = "/upload")
    public String upload() {
        return "upload";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void upload(@RequestParam("deviceId") String deviceId, @RequestParam("deviceVersion") String deviceVersion,
                       @RequestParam("gameId") String gameId, @RequestParam("gameVersion") String gameVersion,
                       @RequestParam("recordFile") MultipartFile recordFile) {
        if (!deviceId.isEmpty() &&
                !deviceVersion.isEmpty() &&
                !gameId.isEmpty() &&
                !gameVersion.isEmpty() &&
                !recordFile.isEmpty()) {
            gameRecordService.addRecord(deviceId, deviceVersion, gameId, gameVersion, recordFile);
        } else {
            throw new RequestException();
        }
    }

    @RequestMapping(value = "/list")
    @ResponseBody
    public List<GameRecord> upload(@RequestParam(value = "deviceId", required = false) String deviceId, @RequestParam(value = "deviceVersion", required = false) String deviceVersion,
                                   @RequestParam(value = "gameId", required = false) String gameId, @RequestParam(value = "gameVersion", required = false) String gameVersion,
                                   @RequestParam(value = "recordId", required = false) String recordId) {
        return gameRecordService.listRecords(deviceId, deviceVersion, gameId, gameVersion, recordId);
    }

    @RequestMapping(value = "/download")
    public void upload(@RequestParam("recordId") String recordId, HttpServletResponse response) {
        gameRecordService.downloadRecord(recordId, response);
    }

    @RequestMapping(value = "/update")
    public String update(@RequestParam(value = "recordId") String recordId, ModelMap modelMap) {
        GameRecord gameRecord = gameRecordService.getRecord(recordId);
        modelMap.addAttribute("model", gameRecord);
        return "update";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
//    public void update(@RequestBody GameRecord gameRecord) {
    public void update(@RequestParam(value = "recordId") String recordId, @RequestParam(value = "recordDesc") String recordDesc) {

        gameRecordService.updateRecord(recordId, recordDesc);

    }

}
