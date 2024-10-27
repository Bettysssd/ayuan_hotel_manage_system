package com.comfort.hotel.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpStatus;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.StyleSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.*;

/**
 * 表格下载导出
 *
 * @author ayuan
 * @date 2024/1/12
 * @since 1.0.0
 */
@Slf4j
public class ExportExcelUtil {
    private static final int COLUMN_WIDTH = 20;
    private static final String XLX = ".xls";



    /**
     * @param response    服务响应
     * @param title       文件名，不带文件后缀
     * @param headerAlias 需导出列及对应表头别名
     * @param resultList  数据
     * @throws Exception 通用异常
     */
    public static void exportExcel(HttpServletResponse response, String title, List<String[]> headerAlias, List<?> resultList) throws Exception {

        String fileName = title + XLX;
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
        response.setHeader("fileName", fileName);

        //提取指定导出列数据
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Object rowBean : resultList) {
            if (BeanUtil.isBean(rowBean.getClass())) {
                Map<String, Object> row = new HashMap();
                for (String[] arr : headerAlias) {
                    row.put(arr[0], getValue(rowBean, arr[0]));
                }
                rows.add(row);
            } else if (rowBean instanceof Map) {
                Map data = (Map) rowBean;
                Map<String, Object> row = new HashMap();
                for (String[] arr : headerAlias) {
                    row.put(arr[0], data.get(arr[0]));
                }
                rows.add(row);
            }
        }
        resultList.clear();

        try (ExcelWriter writer = ExcelUtil.getBigWriter()) {
            ServletOutputStream out = response.getOutputStream();
            StyleSet style = writer.getStyleSet();
            CellStyle cellStyle = style.getHeadCellStyle(); //表头设置
            cellStyle.setAlignment(HorizontalAlignment.CENTER);

            for (int i = 0; i < headerAlias.size(); i++) {
                writer.setColumnWidth(i, COLUMN_WIDTH);
                String[] arr = headerAlias.get(i);
                writer.addHeaderAlias(arr[0], arr[1]);
            }
            writer.write(rows, true);
            writer.flush(out, true);
        }
    }


    private static Object getValue(Object bean, String fieldName) {
        Object value = null;
        try {
            String methodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            Method method = bean.getClass().getMethod(methodName, new Class[]{});
            value = method.invoke(bean, new Object[]{});
        } catch (Exception ignored) {

        }
        return value;
    }

}
