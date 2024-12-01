package com.example.tools.utils;

import com.example.tools.entity.CustomException;
import com.example.tools.entity.DataType;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.tools.utils.Constant.DATETIME_FORMATTER;
import static com.example.tools.utils.Constant.DATE_FORMATTER;

public class CommonUtils {

    @SneakyThrows
    public static <T> Field getEntityField(Class<?> entityClass, String fieldName) {
        Class<?> superClass = entityClass.getSuperclass();
        Field field;
        try {
            field = entityClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            field = superClass.getDeclaredField(fieldName);
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        }
        field.setAccessible(true);
        return field;
    }

    public static <T> Field getEntityField(String fieldName, T t) {
        Class<?> entityClass = t.getClass();
        return getEntityField(entityClass, fieldName);
    }

    @SneakyThrows
    public static <T> Object getFieldValue(Class<?> entityClass, String fieldName, T t) {
        if (t instanceof Map) {
            return ((Map<?, ?>) t).get(fieldName);
        }
        Field field = getEntityField(entityClass, fieldName);
        return field.get(t);
    }

    public static <T> Object getFieldValue(String fieldName, T t) {
        Class<?> entityClass = t.getClass();
        return getFieldValue(entityClass, fieldName, t);
    }

    /**
     * @param fieldNameSequence 带路径的属性名或简单属性名
     * @param t                 对象
     * @return 属性值
     * @throws Exception
     * @MethodName : getFieldValueByNameSequence
     * @Description :
     * 根据带路径或不带路径的属性名获取属性值
     * 即接受简单属性名，如userName等，又接受带路径的属性名，如student.department.name等
     */
    public static <T> Object getFieldValueByNameSequence(String fieldNameSequence, T t) throws Exception {
        Object value;
        //将fieldNameSequence进行拆分
        String[] attributes = fieldNameSequence.split("\\.");
        if (attributes.length == 1) {
            value = getFieldValue(fieldNameSequence, t);
        } else {
            //根据属性名获取属性对象
            Object fieldObj = getFieldValue(attributes[0], t);
            String subFieldNameSequence = fieldNameSequence.substring(fieldNameSequence.indexOf(".") + 1);
            value = getFieldValueByNameSequence(subFieldNameSequence, fieldObj);
        }
        return value;

    }

    @SneakyThrows
    public static <T> void setFieldValue(Class<?> entityClass, String fieldName, T t, Object value) {
        if (t instanceof Map) {
            ((Map<String, Object>) t).put(fieldName, value);
        } else {
            Field field = getEntityField(entityClass, fieldName);
            field.set(t, value);
        }
    }

    public static <T> void setFieldValue(String fieldName, T t, Object value) {
        Class<?> entityClass = t.getClass();
        setFieldValue(entityClass, fieldName, t, value);
    }

    public static <T> void formatListToTree(List<T> list, List<T> tree, Consumer<T> preprocess) {
        list.forEach(it -> {
            if (preprocess != null) preprocess.accept(it);
        });
        List<T> rootNodes = list.stream().filter(it -> list.stream().noneMatch(e -> getFieldValue("parentId", it) == getFieldValue("id", e))).toList();
        if (rootNodes.isEmpty()) {
            tree.addAll(list);
            return;
        }
        rootNodes.forEach(d -> {
            tree.add(d);
            List<T> nextLevelNodes = list.stream().filter(it -> getFieldValue("parentId", it) == getFieldValue("id", d)).toList();
            if (!nextLevelNodes.isEmpty()) {
                findChildrenNodes(nextLevelNodes, d, list);
            }
        });
    }

    public static <T> void findChildrenNodes(List<T> currency, T lastLevelNode, List<T> list) {
        List<T> children = new ArrayList<>();
        currency.forEach(d -> {
            children.add(d);
            setFieldValue("children", lastLevelNode, children);
            List<T> nextLevelNodes = list.stream().filter(it -> getFieldValue("parentId", it) == getFieldValue("id", d)).toList();
            if (!nextLevelNodes.isEmpty()) {
                findChildrenNodes(nextLevelNodes, d, list);
            }
        });
    }

    public static <T> void getChildrenForTreeNodes(List<T> list, List<T> currentNodes, List<T> children, Consumer<T> preprocess) {
        currentNodes.forEach(d -> {
            if (preprocess != null) preprocess.accept(d);
            List<T> nextLevelNodes = list.stream().filter(it -> {
                Object parentId = getFieldValue("parentId", it);
                Object id = getFieldValue("id", d);
                return parentId.toString().equals(id.toString());
            }).toList();
            if (!nextLevelNodes.isEmpty()) {
                children.addAll(nextLevelNodes);
                getChildrenForTreeNodes(list, nextLevelNodes, children, preprocess);
            }
        });
    }

    /**
     * 计算字符串中中文字符的数量
     * 参见 <a hrft="https://www.cnblogs.com/straybirds/p/6392306.html">《汉字unicode编码范围》</a>
     *
     * @param input
     * @return
     */
    public static int chineseCharCountOf(String input) {
        int count = 0;//汉字数量
        if (null != input) {
            String regEx = "[\\u4e00-\\u9fa5]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(input);
            int len = m.groupCount();
            //获取汉字个数
            while (m.find()) {
                for (int i = 0; i <= len; i++) {
                    count = count + 1;
                }
            }
        }
        return count;
    }

    @SneakyThrows
    public static String getMD5Hash(String input) {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static String getIp(ServerHttpRequest request){
        HttpHeaders headers = request.getHeaders();
        String ip = headers.getFirst("x-forwarded-for");
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            if (ip.indexOf(",") != -1) {
                ip = ip.split(",")[0];
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = headers.getFirst("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddress().getAddress().getHostAddress();
        }

        return ip.replaceAll(":", ".");
    }

    public static void checkDataType(String rawData, int dataTypeValue, String prefixMessage) {
        DataType dataType = DataType.getDataType(dataTypeValue);
        switch (dataType) {
            case STRING:
                break;
            case NUMBER:
                try {
                    Long.parseLong(rawData);
                } catch (NumberFormatException e) {
                    throw new CustomException(prefixMessage + "，请输入数字");
                }
                break;
            case BOOL:
                if (!"true".equals(rawData) && !"false".equals(rawData)) {
                    throw new CustomException(prefixMessage + "，布尔类型仅能输入true或false");
                }
                break;
            case DATE:
                try {
                    DATE_FORMATTER.parse(rawData);
                } catch (Exception e) {
                    throw new CustomException(prefixMessage + "，请输入yyyy-MM-dd格式的日期");
                }
                break;
            case DATETIME:
                try {
                    DATETIME_FORMATTER.parse(rawData);
                } catch (Exception e) {
                    throw new CustomException(prefixMessage + "，请输入yyyy-MM-dd HH:mm:ss格式的日期和时间");
                }
                break;
            default:
                // 处理未知类型的情况
                throw new CustomException("未知的数据类型");
        }
    }

    @SneakyThrows
    public static String getMacAddress(ServerHttpRequest request) {
        String clientIP = getIp(request);
        InetAddress inetAddress = InetAddress.getByName(clientIP);
        NetworkInterface networkInterface = NetworkInterface.getByInetAddress(inetAddress);
        byte[] macAddressByte = networkInterface.getHardwareAddress();
        StringBuilder macAddress = new StringBuilder();
        for (byte b : macAddressByte) {
            macAddress.append(String.format("%02X:", b));
        }
        if (!macAddress.isEmpty()) {
            macAddress.deleteCharAt(macAddress.length() - 1);
        }
        return macAddress.toString();
    }
}
