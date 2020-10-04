package ru.comearth.russianpost.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.comearth.russianpost.domain.CSAT;
import ru.comearth.russianpost.domain.TimeStats;
import ru.comearth.russianpost.services.CSATService;
import ru.comearth.russianpost.services.ParseXlsService;
import ru.comearth.russianpost.services.TimeStatsService;
import ru.comearth.russianpost.services.UploadXlsService;

import java.io.File;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Controller
public class UploadController {

    private  final ParseXlsService parseXlsService;
    private final UploadXlsService uploadXlsService;
    private final CSATService csatService;
    private final TimeStatsService timeStatsService;

    public UploadController(ParseXlsService parseXlsService, UploadXlsService uploadXlsService, CSATService csatService, TimeStatsService timeStatsService) {
        this.parseXlsService = parseXlsService;
        this.uploadXlsService = uploadXlsService;
        this.csatService = csatService;
        this.timeStatsService = timeStatsService;
    }

    private final String PATH = "C:\\java\\pochta\\";
    private List<CSAT> csats = new ArrayList<>();
    private List<TimeStats> timeStats = new ArrayList<>();
    private String error = null;

    @GetMapping("/upload")
    public String showUploadForm( Model model){
        model.addAttribute("csats", csats);
        model.addAttribute("timestats",timeStats);
        model.addAttribute("error", error);
        error=null;
        return "upload/upload";
    }



    @PostMapping("/uploadCsat")
    public String UploadCsat( @RequestParam("xlsCSAT") MultipartFile CsatFile)  {

        try {
            uploadRoutine(CsatFile);
            csats = parseXlsService.parseCSAT(Paths.get(PATH + CsatFile.getOriginalFilename()));

            return "redirect:/upload";
        }
        catch (Exception e){
            error=e.getMessage()+"\n";
            return "redirect:/upload";
        }
    }

    @PostMapping("/uploadAht")
    public String UploadTimeStats(@RequestParam("xlsAHT") MultipartFile AhtFile)  {

        try {
            uploadRoutine(AhtFile);
            timeStats = parseXlsService.parseTimeStats(Paths.get(PATH + AhtFile.getOriginalFilename()));


            return "redirect:/upload";
        }
        catch (Exception e){
            error=e.getMessage()+"\n";
            return "redirect:/upload";
        }
    }

    @GetMapping("/savestats")
    public String saveStats() {
        for (File myFile : new File(PATH).listFiles())
            if (myFile.isFile()) {
                myFile.delete();
                System.out.println("Deleted file");
            }

        try {
            if(csats.size()>0) csatService.saveAll(csats);
            if(timeStats.size()>0) timeStatsService.saveAll(timeStats);

            csats =new ArrayList<>();
            timeStats=new ArrayList<>();

            return "redirect:/upload";
        } catch (Exception e) {
            error = e.getMessage() + "\n";
            return "redirect:/upload";
        }
    }

    @Transactional
    @PostMapping("/deletecsats")
    public String removeCsat(Date date) {
        try {
            csatService.removeAllByDate(date.toLocalDate());
            return "redirect:/upload";
        } catch (Exception e) {
            error=e.getMessage()+"\n";
            return "redirect:/upload";
        }
    }

    @Transactional
    @PostMapping("/deletetimestats")
    public String removeTimeStats(Date date) {
        try {
            timeStatsService.removeAllByDate(date.toLocalDate());
            return "redirect:/upload";
        } catch (Exception e) {
            error=e.getMessage()+"\n";
            return "redirect:/upload";
        }
    }

    public void uploadRoutine(MultipartFile file) throws Exception {
        for (File myFile : new File(PATH).listFiles())
            if (myFile.isFile()) {
                myFile.delete();
                System.out.println("Deleted file");
            }
        error=null;
        csats = new ArrayList<>();
        timeStats = new ArrayList<>();

        uploadXlsService.uploadXls(file);
    }

}
