package com.woosan.hr_system.holiday.service;

import com.woosan.hr_system.aspect.LogAfterExecution;
import com.woosan.hr_system.aspect.LogBeforeExecution;
import com.woosan.hr_system.aspect.RequireHRPermission;
import com.woosan.hr_system.aspect.RequireManagerPermission;
import com.woosan.hr_system.common.service.CommonService;
import com.woosan.hr_system.holiday.model.Holiday;
import com.woosan.hr_system.holiday.dao.HolidayDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class HolidayServiceImpl implements HolidayService {
    @Autowired
    private HolidayDAO holidayDAO;
    @Autowired
    private CommonService commonService;

    private static final String SERVICE_KEY = "RAkHJ6RjhKqi3n0QzIOSv691iUpS%2B7e5vbMU4Epf5iGdCpHQI34ji%2FPQstUq8wWtEwxYwB48qYoBOH7DcCbqNg%3D%3D";

    @Override // 해당 년도 공휴일 조회
    public List<Holiday> getHolidayByYear(int year) {
        return holidayDAO.getHolidayByYear(year);
    }

    @Override // 해당 년월 공휴일 조회
    public List<Holiday> getHolidayByYearMonth(YearMonth yearMonth) {
        return holidayDAO.getHolidayByYearMonth(yearMonth);
    }

    @Override // 모든 공휴일 조회
    public List<Holiday> getAllHoliday() {
        return holidayDAO.getAllHoliday();
    }

    @RequireHRPermission
    @RequireManagerPermission
    @LogBeforeExecution
    @LogAfterExecution
    @Override // 공휴일 등록
    public String addHoliday(Holiday holiday) {
        holidayDAO.insertHoliday(holiday);
        return "새로운 공휴일이 등록되었습니다.\n" + holiday.getDateName() + "(" + holiday.getLocDate() + ")";
    }

    @RequireHRPermission
    @RequireManagerPermission
    @LogBeforeExecution
    @LogAfterExecution
    @Override // 공휴일 수정
    public String editHoliday(Holiday holiday) {
        // 원본 공휴일 정보 조회
        Holiday originalHoliday = holidayDAO.getHolidayById(holiday.getHolidayId());

        // 원본과 수정본 비교
        checkForHolidayChanges(originalHoliday, holiday);

        // 공휴일 수정
        holidayDAO.updateHoliday(holiday);
        return "공휴일 정보가 수정되었습니다.\n" +
                "변경 전 : " + originalHoliday.getDateName() + "(" + originalHoliday.getLocDate() + ")\n" +
                "변경 후 : " + holiday.getDateName() + "(" + holiday.getLocDate() + ")";
    }

    // Holiday 특정 필드만 비교하도록 필드 이름을 Set으로 전달하는 메소드
    private void checkForHolidayChanges(Holiday original, Holiday updated) {
        Set<String> fieldsToCompare = new HashSet<>(Arrays.asList(
                "dateName", "locDate"
        ));
        commonService.processFieldChanges(original, updated, fieldsToCompare);
    }

    @RequireHRPermission
    @RequireManagerPermission
    @LogBeforeExecution
    @LogAfterExecution
    @Override // 공휴일 삭제
    public String deleteHoliday(int holidayId) {
        // 공휴일 정보 조회 후 검사
        Holiday holiday = holidayDAO.getHolidayById(holidayId);
        if (holiday == null) {
            throw new IllegalArgumentException("해당 공휴일이 존재하지 않습니다.\n 공휴일 번호 : " + holidayId);
        }

        // 공휴일 삭제
        holidayDAO.deleteHoliday(holidayId);
        return holiday.getDateName() + "(" + holiday.getLocDate() + ")이(가) 삭제되었습니다." ;
    }

    @Override // 해당 날짜가 공휴일인지 확인
    public boolean isHoliday(LocalDate date) {
        return holidayDAO.isHoliday(date) != 0;
    }

    @Transactional
    @LogBeforeExecution
    @LogAfterExecution
    @Scheduled(cron = "0 0 0 1 1 *") // 새해 공휴일 자동 생성
    public void createNewYearHoliday() {
        createHolidays(Year.now());
    }

    // 해당 년도 공휴일 생성
    private void createHolidays(Year year) {
        try {
            // URL
            StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo");

            urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + SERVICE_KEY); // Service Key
            urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); // 페이지번호
            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("100", "UTF-8")); // 한 페이지 결과 수
            urlBuilder.append("&" + URLEncoder.encode("solYear", "UTF-8") + "=" + URLEncoder.encode(year.toString(), "UTF-8")); // 년

            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            System.out.println("Response code: " + conn.getResponseCode());
            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();

            // XML 응답 파싱 및 공휴일 리스트 생성
            List<Holiday> holidayList = parseXmlResponse(sb.toString());

            // 파싱한 공휴일 리스트 DB에 저장
            holidayDAO.insertHolidayList(holidayList);

            log.info("{}년 공휴일이 등록되었습니다.", year);
        } catch (IOException e) {
            log.error("IOException 발생 : ", e);
        }
    }

    // XML 응답을 파싱하는 메서드
    private List<Holiday> parseXmlResponse(String xmlResponse) {
        List<Holiday> holidays = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(xmlResponse.getBytes(StandardCharsets.UTF_8)));

            // XML 요소 중 <item> 태그를 찾아 공휴일 정보 추출
            NodeList itemList = document.getElementsByTagName("item");

            // 날짜 포맷터 (yyyyMMdd 형식)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

            for (int i = 0; i < itemList.getLength(); i++) {
                Element item = (Element) itemList.item(i);
                String dateName = item.getElementsByTagName("dateName").item(0).getTextContent();
                String locdateStr = item.getElementsByTagName("locdate").item(0).getTextContent();
                LocalDate locDate = LocalDate.parse(locdateStr, formatter);  // 날짜 포맷을 맞춰 변환

                holidays.add(new Holiday(dateName, locDate));
            }
        } catch (Exception e) {
            log.error("XML 파싱 오류 : ", e);
        }
        return holidays;
    }
}

