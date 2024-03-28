package com.xibin.report;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class NewReportController {

    @Resource
    private DataSource dataSource;


    /**
     * 转换为pdf展示
     *
     * @param reportName
     * @param parameters
     * @param response
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws JRException
     * @throws IOException
     */
    @GetMapping("/report/{reportName}")
    public void getReportByParam(
            @PathVariable("reportName") final String reportName,
            @RequestParam(required = false) Map<String, Object> parameters,@RequestParam(required = false,value = "array")String [] array,
            HttpServletResponse response) throws SQLException, ClassNotFoundException, JRException, IOException {
        parameters = parameters == null ? new HashMap<>() : parameters;
        String listKey = (String) parameters.get("arrayKey");
        if(null!=listKey&&!"".equals(listKey)){
            List<String> list = new ArrayList<>();
            for (String item : array) {
                list.add(item);
            }
            parameters.put(listKey,list);
        }
        //获取文件流
        ClassPathResource resource = new ClassPathResource("jaspers" + File.separator + reportName + ".jasper");
        InputStream jasperStream = resource.getInputStream();
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
        Connection con = dataSource.getConnection();
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, con);
        // JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, new JREmptyDataSource());
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline;");
        final OutputStream outputStream = response.getOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
        con.close();
    }
}
