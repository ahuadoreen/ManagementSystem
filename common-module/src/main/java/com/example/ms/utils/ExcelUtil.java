package com.example.ms.utils;

import com.example.tools.entity.TableColumn;
import com.example.tools.utils.CommonUtils;
import com.example.tools.utils.Constant;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;

public class ExcelUtil {
    private static final String EXCEL_XLS = ".xls";
    private static final String EXCEL_XLSX = ".xlsx";

    /**
     * 根据excel的版本，获取相应的Workbook
     *
     * @param fileName 文件名
     * @return Workbook
     */
    @SneakyThrows
    public static Workbook getWorkbook(String fileName) {
        Workbook wb = null;
        if (fileName.endsWith(EXCEL_XLS)) //2003
        {
            wb = new HSSFWorkbook();
        } else if (fileName.endsWith(EXCEL_XLSX)) {
            wb = new XSSFWorkbook();//2007 2010
        }
        return wb;
    }

    /**
     * @param list      数据源
     * @param fieldMap  类的英文属性和Excel中的中文列名的对应关系
     *                  如果需要的是引用对象的属性，则英文属性使用类似于EL表达式的格式
     *                  如：list中存放的都是student，student中又有college属性，而我们需要学院名称，则可以这样写
     *                  fieldMap.put("college.collegeName","学院名称")
     * @param sheetName 工作表的名称
     * @param sheetSize 每个工作表中记录的最大个数
     * @param out       导出流
     */
    @SneakyThrows
    public static <T> void listToExcel(
            List<T> list,
            List<TableColumn> fieldMap,
            String sheetName,
            int sheetSize,
            String fileName,
            OutputStream out,
            Consumer<T> preprocess
    ) {
        if (sheetSize > 65535 || sheetSize < 1) {
            sheetSize = 65535;
        }

        //创建工作簿并发送到OutputStream指定的地方
        Workbook workbook;
        workbook = getWorkbook(fileName);

        //因为2003的Excel一个工作表最多可以有65536条记录，除去列头剩下65535条
        //所以如果记录太多，需要放到多个工作表中，其实就是个分页的过程
        //1.计算一共有多少个工作表
        double sheetNum = 1;
        if (!list.isEmpty()) sheetNum = Math.ceil(list.size() / (double) sheetSize);

        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setWrapText(true);
        //2.创建相应的工作表，并向其中填充数据
        for (int i = 0; i < sheetNum; i++) {
            //如果只有一个工作表的情况
            if (1 == sheetNum) {
                Sheet sheet = workbook.createSheet(sheetName);
                fillSheet(sheet, list, fieldMap, 0, list.size() - 1, style, preprocess);

                //有多个工作表的情况
            } else {
                Sheet sheet = workbook.createSheet(sheetName + (i + 1));

                //获取开始索引和结束索引
                int firstIndex = i * sheetSize;
                int lastIndex = Math.min((i + 1) * sheetSize - 1, list.size() - 1);
                //填充工作表
                fillSheet(sheet, list, fieldMap, firstIndex, lastIndex, style, preprocess);
            }
        }

        workbook.write(out);
        out.flush();
        workbook.close();
    }

    /**
     * @param list     数据源
     * @param fieldMap 类的英文属性和Excel中的中文列名的对应关系
     * @param out      导出流
     */
    public static <T> void listToExcel(
            List<T> list,
            List<TableColumn> fieldMap,
            String sheetName,
            String fileName,
            OutputStream out,
            Consumer<T> preprocess
    ) {
        listToExcel(list, fieldMap, sheetName, 65535, fileName, out, preprocess);
    }


    /**
     * @param list      数据源
     * @param fieldMap  类的英文属性和Excel中的中文列名的对应关系
     * @param sheetSize 每个工作表中记录的最大个数
     * @param response  使用response可以导出到浏览器
     */
    @SneakyThrows
    public static <T> void listToExcel(
            List<T> list,
            List<TableColumn> fieldMap,
            String sheetName,
            int sheetSize,
            String fileName,
            HttpServletResponse response,
            Consumer<T> preprocess
    ) {

        //设置默认文件名为当前时间：年月日时分秒
        fileName = fileName + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + EXCEL_XLSX;

        //设置response头信息
        response.reset();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");        //改成输出excel文件
        response.setHeader("Content-disposition", "attachment; filename=" + fileName);

        //创建工作簿并发送到浏览器
        OutputStream out = response.getOutputStream();
        listToExcel(list, fieldMap, sheetName, sheetSize, fileName, out, preprocess);
    }


    /**
     * @param list     数据源
     * @param fieldMap 类的英文属性和Excel中的中文列名的对应关系
     * @param response 使用response可以导出到浏览器
     */
    public static <T> void listToExcel(
            List<T> list,
            List<TableColumn> fieldMap,
            String fileName,
            HttpServletResponse response,
            Consumer<T> preprocess
    ) {
        listToExcel(list, fieldMap, "sheet", 65535, fileName, response, preprocess);
    }

    public static <T> void listToExcel(
            List<T> list,
            List<TableColumn> fieldMap,
            String fileName,
            HttpServletResponse response
    ) {
        listToExcel(list, fieldMap, "sheet", 65535, fileName, response, null);
    }

    /**
     * @param in          ：承载着Excel的输入流
     * @param entityClass ：List中对象的类型（Excel中的每一行都要转化为该类型的对象）
     * @param fieldMap    ：Excel中的中文列头和类的英文属性的对应关系Map
     * @return ：List
     */
    @SneakyThrows
    public static <T> List<T> excelToList(
            InputStream in,
            String sheetName,
            Class<T> entityClass,
            List<TableColumn> fieldMap,
            Consumer<T> preprocess
    ) {
        //定义要返回的list
        List<T> resultList = new ArrayList<T>();

        //根据Excel数据源创建WorkBook
        Workbook wb = WorkbookFactory.create(in);
        //获取工作表
        Sheet sheet = wb.getSheet(sheetName);

        //获取工作表的有效行数
        int realRows = 0;
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            if (sheet.getRow(i) == null) continue; //空行跳过
            int nullCols = 0;
            int colQty = sheet.getRow(i).getPhysicalNumberOfCells();
            for (int j = 0; j < colQty; j++) {
                Cell currentCell = sheet.getRow(i).getCell(j);
                if (currentCell == null || StringUtils.isBlank(currentCell.getStringCellValue())) {
                    nullCols++;
                }
            }

            if (nullCols == colQty) {
                break;
            } else {
                realRows++;
            }
        }

        //如果Excel中没有数据则提示错误
        if (realRows <= 1) {
            throw new Exception("Excel文件中没有任何数据");
        }

        Row firstRow = sheet.getRow(0);

        String[] excelFieldNames = new String[firstRow.getPhysicalNumberOfCells()];

        //获取Excel中的列名
        for (int i = 0; i < firstRow.getPhysicalNumberOfCells(); i++) {
            excelFieldNames[i] = firstRow.getCell(i).getStringCellValue().trim();
        }

        //判断需要的字段在Excel中是否都存在
        boolean isExist = true;
        List<String> excelFieldList = Arrays.asList(excelFieldNames);
        for (TableColumn tableColumn : fieldMap) {
            if (!excelFieldList.contains(tableColumn.getTitle())) {
                isExist = false;
                break;
            }
        }

        //如果有列名不存在，则抛出异常，提示错误
        if (!isExist) {
            throw new Exception("Excel中缺少必要的字段，或字段名称有误");
        }

        //将列名和列号放入Map中,这样通过列名就可以拿到列号
        LinkedHashMap<String, Integer> colMap = new LinkedHashMap<>();
        for (int i = 0; i < excelFieldNames.length; i++) {
            colMap.put(excelFieldNames[i], firstRow.getCell(i).getColumnIndex());
        }

        //将sheet转换为list
        for (int i = 1; i < realRows; i++) {
            //新建要转换的对象
            Row row = sheet.getRow(i);
            if (row != null) {//给对象中的字段赋值
                T entity = entityClass.getDeclaredConstructor().newInstance();
                for (TableColumn entry : fieldMap) {
                    //获取中文字段名
                    String cnNormalName = entry.getTitle();
                    //获取英文字段名
                    String enNormalName = entry.getIndex();
                    //根据中文字段名获取列号
                    int col = colMap.get(cnNormalName);

                    //获取当前单元格中的内容
                    Cell cell = row.getCell(col);
                    if (cell != null) {
                        String content = cell.getStringCellValue();
                        if (StringUtils.isNotBlank(content)) {//给对象赋值
                            Object objValue = content.trim();
                            // 用前端回传的字典表数据映射要显示的字段
                            TableColumn tableColumn = fieldMap.stream().filter(t -> t.getIndex().equals(enNormalName)).findFirst().orElse(null);
                            assert tableColumn != null;
                            List<Map<String, Object>> enums = tableColumn.getEnums();
                            if (enums != null) {
                                Object finalObjValue = content.trim();
                                Map<String, Object> map = enums.stream().filter(t -> t.get("label").equals(finalObjValue)).findFirst().orElse(null);
                                if (map != null) {
                                    objValue = map.get("value");
                                }
                            }
                            CommonUtils.setFieldValue(enNormalName, entity, objValue);
                        }
                    }
                }
                if (preprocess != null) {
                    preprocess.accept(entity);
                }
                resultList.add(entity);
            }
        }
        return resultList;
    }

    /**
     * @param sheet
     * @MethodName : setColumnAutoSize
     * @Description : 设置工作表自动列宽
     */
    private static void setColumnAutoSize(Sheet sheet, int columnSize, int extraWith) {
        //获取本列的最宽单元格的宽度
        for (int i = 0; i < columnSize; i++) {
            int colWith = 0;
            for (int j = 0; j <= sheet.getLastRowNum(); j++) {
                Row row = sheet.getRow(j);
                if (row != null) {
                    Cell cell = row.getCell(i);
                    if (cell != null) {
                        String content = cell.getStringCellValue();
                        // 计算字符串中中文字符的数量
                        int chineseCharCount = com.example.tools.utils.CommonUtils.chineseCharCountOf(content);
                        // 在该列字符长度的基础上加上汉字个数计算列宽
                        int cellWith = content.length() + chineseCharCount;
                        if (colWith < cellWith) {
                            colWith = cellWith;
                        }
                    }
                }
            }
            int width = colWith + extraWith;
            if (width > 255) width = 255;
            //设置单元格的宽度为最宽宽度+额外宽度
            sheet.setColumnWidth(i, width * 256);
        }

    }

    /**
     * @param sheet      工作表
     * @param list       数据源
     * @param fieldMap   中英文字段对应关系的Map
     * @param firstIndex 开始索引
     * @param lastIndex  结束索引
     * @MethodName : fillSheet
     * @Description : 向工作表中填充数据
     */
    @SneakyThrows
    private static <T> void fillSheet(
            Sheet sheet,
            List<T> list,
            List<TableColumn> fieldMap,
            int firstIndex,
            int lastIndex,
            CellStyle titleStyle,
            Consumer<T> preprocess
    ) {
        //定义存放英文字段名和中文字段名的数组
        String[] enFields = new String[fieldMap.size()];
        String[] cnFields = new String[fieldMap.size()];

        //填充数组
        int count = 0;
        for (TableColumn entry : fieldMap) {
            enFields[count] = entry.getIndex();
            cnFields[count] = entry.getTitle();
            count++;
        }
        Row titleRow = sheet.createRow(0);
        //填充表头
        for (int i = 0; i < cnFields.length; i++) {
            Cell titleCell = titleRow.createCell(i);
            titleCell.setCellValue(cnFields[i]);
            titleCell.setCellStyle(titleStyle);
        }

        //填充内容
        int rowNo = 1;
        for (int index = firstIndex; index <= lastIndex; index++) {
            //获取单个对象
            T item = list.get(index);
            if (preprocess != null) {
                preprocess.accept(item);
            }
            Row row = sheet.createRow(rowNo);
            for (int i = 0; i < enFields.length; i++) {
                String fieldName = enFields[i];
                Object objValue = CommonUtils.getFieldValueByNameSequence(enFields[i], item);
                if (objValue instanceof LocalDateTime localDateTime) {
                    objValue = localDateTime.format(Constant.DATETIME_FORMATTER);
                }
                if (objValue instanceof Instant instant) {
                    objValue = Constant.DATETIME_FORMATTER.format(instant);
                }
                // 用前端回传的字典表数据映射要显示的字段
                TableColumn tableColumn = fieldMap.stream().filter(t -> t.getIndex().equals(fieldName)).findFirst().orElse(null);
                assert tableColumn != null;
                List<Map<String, Object>> enums = tableColumn.getEnums();
                if (enums != null) {
                    Object finalObjValue = objValue;
                    Map<String, Object> map = enums.stream().filter(t -> t.get("value").equals(finalObjValue)).findFirst().orElse(null);
                    if (map != null) {
                        objValue = map.get("label");
                    }
                }
                String fieldValue = objValue == null ? "" : objValue.toString();
                Cell cell = row.createCell(i);
                cell.setCellValue(fieldValue);
            }
            rowNo++;
        }

        //设置自动列宽
        setColumnAutoSize(sheet, enFields.length, 3);
    }
}
