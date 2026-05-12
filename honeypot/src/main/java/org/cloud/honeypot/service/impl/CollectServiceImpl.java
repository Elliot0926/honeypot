package org.cloud.honeypot.service.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.cloud.honeypot.dto.AptComplexDto;
import org.cloud.honeypot.mapper.AptComplexMapper;
import org.cloud.honeypot.service.CollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

@Service
public class CollectServiceImpl implements CollectService {

    @Autowired
    private AptComplexMapper aptComplexMapper;

    // 매매 실거래가 Mapper
    @Autowired
    private org.cloud.honeypot.mapper.SaleTransactionMapper saleTransactionMapper;

    // 전월세 실거래가 Mapper
    @Autowired
    private org.cloud.honeypot.mapper.RentTransactionMapper rentTransactionMapper;

    // ✅ Decoding 키 그대로 사용 (URLEncoder 절대 금지)
    private static final String API_KEY = "99b01a75222bb4353bea0757991929e04ead10426cfba9fb7cec429ad3e3a8b7";

    // 카카오 REST API 키
    private static final String KAKAO_KEY = "a16d46f1e6d0664d84e554e555384583";

    @Override
    public int collectAptComplex(String sigunguCd) throws Exception {
        int count = 0;
        int[] years  = {2023, 2024, 2025};
        int[] months = {1,2,3,4,5,6,7,8,9,10,11,12};

        for (int year : years) {
            for (int month : months) {
                String dealYmd = String.format("%d%02d", year, month);

                String rawUrl = "https://apis.data.go.kr/1613000/RTMSDataSvcAptTrade/getRTMSDataSvcAptTrade"
                        + "?serviceKey=" + API_KEY
                        + "&LAWD_CD=" + sigunguCd
                        + "&DEAL_YMD=" + dealYmd
                        + "&pageNo=1"
                        + "&numOfRows=1000";

                System.out.println("[단지수집 API 호출] " + sigunguCd + " " + dealYmd);

                String xmlData = callApi(rawUrl);
                if (xmlData == null) continue;

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new InputSource(new StringReader(xmlData)));

                NodeList items = doc.getElementsByTagName("item");

                for (int i = 0; i < items.getLength(); i++) {
                    Element item = (Element) items.item(i);

                    String aptNm = getTagValue("aptNm", item);
                    String umdNm = getTagValue("umdNm", item);
                    String buildYear = getTagValue("buildYear", item);

                    if (aptNm == null || umdNm == null) continue;

                    aptNm = aptNm.trim();
                    umdNm = umdNm.trim();

                    if (aptComplexMapper.countByNameAndDong(aptNm, umdNm) > 0) continue;

                    AptComplexDto dto = new AptComplexDto();
                    dto.setComplexName(aptNm);
                    dto.setSigunguCd(sigunguCd);
                    dto.setLegalDong(umdNm);
                    String jibun = getTagValue("jibun", item);
                    dto.setJibunAddress(umdNm + " " + (jibun != null ? jibun.trim() : ""));
                    dto.setGeoStatus("PENDING");

                    if (buildYear != null && !buildYear.trim().isEmpty()) {
                        try { dto.setBuildYear(Integer.parseInt(buildYear.trim())); } catch (Exception e) {}
                    }

                    aptComplexMapper.insertComplex(dto);
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public int convertGeoLocation() throws Exception {
        int count = 0;
        List<AptComplexDto> list = aptComplexMapper.selectAllComplex();

        for (AptComplexDto dto : list) {
            if (!"PENDING".equals(dto.getGeoStatus())) continue;

            String address = dto.getRoadAddress() != null
                    ? dto.getRoadAddress()
                    : dto.getJibunAddress();
            if (address == null || address.isEmpty()) {
                dto.setGeoStatus("FAIL");
                aptComplexMapper.updateGeoLocation(dto);
                continue;
            }

            try {
                // 카카오는 query 파라미터만 인코딩 (정상)
                String encodedAddr = URLEncoder.encode(address, "UTF-8");
                String url = "https://dapi.kakao.com/v2/local/search/address.json?query=" + encodedAddr;

                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Authorization", "KakaoAK " + KAKAO_KEY);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "UTF-8"));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String json = response.toString();
                if (json.contains("\"documents\":[]")) {
                    dto.setGeoStatus("FAIL");
                } else {
                    int xIdx = json.indexOf("\"x\":\"") + 5;
                    int xEnd = json.indexOf("\"", xIdx);
                    int yIdx = json.indexOf("\"y\":\"") + 5;
                    int yEnd = json.indexOf("\"", yIdx);

                    if (xIdx > 5 && yIdx > 5) {
                        double lng = Double.parseDouble(json.substring(xIdx, xEnd));
                        double lat = Double.parseDouble(json.substring(yIdx, yEnd));
                        dto.setLongitude(lng);
                        dto.setLatitude(lat);
                        dto.setGeoStatus("SUCCESS");
                        count++;
                    } else {
                        dto.setGeoStatus("FAIL");
                    }
                }
                aptComplexMapper.updateGeoLocation(dto);
                Thread.sleep(100);

            } catch (Exception e) {
                dto.setGeoStatus("FAIL");
                aptComplexMapper.updateGeoLocation(dto);
            }
        }
        return count;
    }

    /**
     * 매매 실거래가 수집
     * 국토부 RTMSDataSvcAptTradeDev API 호출
     * → apt_complex 단지명+법정동으로 매칭 후 sale_transaction 저장
     */
    @Override
    public int collectSaleTransaction(String sigunguCd, String dealYmd) throws Exception {
        int count = 0;

        String rawUrl = "https://apis.data.go.kr/1613000/RTMSDataSvcAptTradeDev/getRTMSDataSvcAptTradeDev"
                + "?serviceKey=" + API_KEY
                + "&LAWD_CD=" + sigunguCd
                + "&DEAL_YMD=" + dealYmd
                + "&pageNo=1"
                + "&numOfRows=1000";

        System.out.println("[매매 실거래가 호출] " + sigunguCd + " " + dealYmd);

        String xmlData = callApi(rawUrl);
        if (xmlData == null) return 0;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xmlData)));

        NodeList items = doc.getElementsByTagName("item");
        System.out.println("[매매] " + dealYmd + " - " + items.getLength() + "건");

        for (int i = 0; i < items.getLength(); i++) {
            Element item = (Element) items.item(i);

            String aptNm = getTagValue("aptNm", item);
            String umdNm = getTagValue("umdNm", item);
            if (aptNm == null || umdNm == null) continue;

            // apt_complex 테이블에서 단지명+법정동으로 매칭
            AptComplexDto complex = aptComplexMapper.selectByNameAndDong(aptNm.trim(), umdNm.trim());
            if (complex == null) continue; // 매칭 안되면 스킵

            String dealAmountStr = getTagValue("dealAmount", item);
            String dealYearStr   = getTagValue("dealYear",   item);
            String dealMonthStr  = getTagValue("dealMonth",  item);
            String dealDayStr    = getTagValue("dealDay",    item);

            if (dealAmountStr == null || dealYearStr == null) continue;

            int dealAmount = Integer.parseInt(dealAmountStr.replaceAll(",", "").trim());
            int dealYear   = Integer.parseInt(dealYearStr.trim());
            int dealMonth  = Integer.parseInt(dealMonthStr.trim());
            int dealDay    = Integer.parseInt(dealDayStr.trim());

            // 중복 체크
            if (saleTransactionMapper.countByComplexAndDate(
                    complex.getComplexId(), dealYear, dealMonth, dealDay, dealAmount) > 0) continue;

            org.cloud.honeypot.dto.SaleTransactionDto dto = new org.cloud.honeypot.dto.SaleTransactionDto();
            dto.setComplexId(complex.getComplexId());
            dto.setDealAmount(dealAmount);
            dto.setDealYear(dealYear);
            dto.setDealMonth(dealMonth);
            dto.setDealDay(dealDay);

            String areaStr = getTagValue("excluUseAr", item);
            if (areaStr != null && !areaStr.trim().isEmpty()) {
                try { dto.setArea(Double.parseDouble(areaStr.trim())); } catch (Exception e) {}
            }
            String floorStr = getTagValue("floor", item);
            if (floorStr != null && !floorStr.trim().isEmpty()) {
                try { dto.setFloor(Integer.parseInt(floorStr.trim())); } catch (Exception e) {}
            }
            dto.setAptDong(getTagValue("aptDong",    item));
            dto.setDealType(getTagValue("dealingGbn", item)); // 거래유형
            dto.setSlerGbn(getTagValue("slerGbn",     item)); // 매도자구분
            dto.setBuyerGbn(getTagValue("buyerGbn",   item)); // 매수자구분
            dto.setRegDate(getTagValue("rgstDate",    item)); // 등기일자

            saleTransactionMapper.insertSale(dto);
            count++;
        }
        System.out.println("[매매 수집 완료] " + count + "건");
        return count;
    }

    /**
     * 전월세 실거래가 수집
     * 국토부 RTMSDataSvcAptRent API 호출
     * → apt_complex 단지명+법정동으로 매칭 후 rent_transaction 저장
     */
    @Override
    public int collectRentTransaction(String sigunguCd, String dealYmd) throws Exception {
        int count = 0;

        String rawUrl = "https://apis.data.go.kr/1613000/RTMSDataSvcAptRent/getRTMSDataSvcAptRent"
                + "?serviceKey=" + API_KEY
                + "&LAWD_CD=" + sigunguCd
                + "&DEAL_YMD=" + dealYmd
                + "&pageNo=1"
                + "&numOfRows=1000";

        System.out.println("[전월세 실거래가 호출] " + sigunguCd + " " + dealYmd);

        String xmlData = callApi(rawUrl);
        if (xmlData == null) return 0;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xmlData)));

        NodeList items = doc.getElementsByTagName("item");
        System.out.println("[전월세] " + dealYmd + " - " + items.getLength() + "건");

        for (int i = 0; i < items.getLength(); i++) {
            Element item = (Element) items.item(i);

            String aptNm = getTagValue("aptNm", item);
            String umdNm = getTagValue("umdNm", item);
            if (aptNm == null || umdNm == null) continue;

            // apt_complex 테이블에서 단지명+법정동으로 매칭
            AptComplexDto complex = aptComplexMapper.selectByNameAndDong(aptNm.trim(), umdNm.trim());
            if (complex == null) continue;

            String depositStr   = getTagValue("deposit",   item);
            String dealYearStr  = getTagValue("dealYear",  item);
            String dealMonthStr = getTagValue("dealMonth", item);
            String dealDayStr   = getTagValue("dealDay",   item);

            if (depositStr == null || dealYearStr == null) continue;

            int deposit   = Integer.parseInt(depositStr.replaceAll(",", "").trim());
            int dealYear  = Integer.parseInt(dealYearStr.trim());
            int dealMonth = Integer.parseInt(dealMonthStr.trim());
            int dealDay   = Integer.parseInt(dealDayStr.trim());

            // 중복 체크
            if (rentTransactionMapper.countByComplexAndDate(
                    complex.getComplexId(), dealYear, dealMonth, dealDay, deposit) > 0) continue;

            org.cloud.honeypot.dto.RentTransactionDto dto = new org.cloud.honeypot.dto.RentTransactionDto();
            dto.setComplexId(complex.getComplexId());
            dto.setDeposit(deposit);
            dto.setDealYear(dealYear);
            dto.setDealMonth(dealMonth);
            dto.setDealDay(dealDay);

            // 월세 (없으면 0)
            String monthlyStr = getTagValue("monthlyRent", item);
            if (monthlyStr != null && !monthlyStr.trim().isEmpty()) {
                try {
                    dto.setMonthlyRent(Integer.parseInt(monthlyStr.replaceAll(",", "").trim()));
                } catch (Exception e) { dto.setMonthlyRent(0); }
            } else {
                dto.setMonthlyRent(0);
            }

            // 전월세 구분: 월세 > 0 이면 월세, 아니면 전세
            dto.setRentType(dto.getMonthlyRent() != null && dto.getMonthlyRent() > 0 ? "월세" : "전세");

            String areaStr = getTagValue("excluUseAr", item);
            if (areaStr != null && !areaStr.trim().isEmpty()) {
                try { dto.setArea(Double.parseDouble(areaStr.trim())); } catch (Exception e) {}
            }
            String floorStr = getTagValue("floor", item);
            if (floorStr != null && !floorStr.trim().isEmpty()) {
                try { dto.setFloor(Integer.parseInt(floorStr.trim())); } catch (Exception e) {}
            }
            dto.setAptDong(getTagValue("aptDong", item));

            rentTransactionMapper.insertRent(dto);
            count++;
        }
        System.out.println("[전월세 수집 완료] " + count + "건");
        return count;
    }

    // ✅ URI.create() 사용 → URL 객체의 추가 인코딩 방지
    private String callApi(String apiUrl) {
        try {
            URI uri = URI.create(apiUrl);
            HttpURLConnection con = (HttpURLConnection) uri.toURL().openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(10000);
            con.setReadTimeout(15000);

            int responseCode = con.getResponseCode();
            System.out.println("[HTTP 응답코드] " + responseCode);

            BufferedReader in;
            if (responseCode >= 400) {
                in = new BufferedReader(new InputStreamReader(con.getErrorStream(), "UTF-8"));
                StringBuilder errBody = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) errBody.append(line);
                in.close();
                System.out.println("[에러 응답 body] " + errBody.toString());
                return null;
            } else {
                in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            }

            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // XML 태그값 추출
    private String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent().trim();
        }
        return null;
    }
}