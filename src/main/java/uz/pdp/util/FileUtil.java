package uz.pdp.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FileUtil {
    private static final ObjectMapper objectMapper;
    private static final XmlMapper xmlMapper;

    static {
        objectMapper = JsonMapper.builder().enable(MapperFeature.PROPAGATE_TRANSIENT_MARKER)
                .addModule(new JavaTimeModule()).build();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        xmlMapper = XmlMapper.builder().serializationInclusion(JsonInclude.Include.NON_NULL)
                .addModule(new JavaTimeModule()).build();
        xmlMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public static <T> void write(String path, T t) throws IOException {
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(path), t);
    }

    public static <T> List<T> read(String path, Class<T> clazz) throws IOException {
        return objectMapper.readValue(new File(path),
                objectMapper.getTypeFactory().constructCollectionType(List.class, clazz)
        );
    }

    public static <T> void writeToXml(String path, T t) throws IOException {
        xmlMapper.writerWithDefaultPrettyPrinter().writeValue(new File(path), t);
    }

    public static <T> T readFromXml(String path, Class<T> clazz) throws IOException {
        return xmlMapper.readValue(new File(path), clazz);
    }

    public static <V> Map<V, UUID> readMap(String filePath, Class<V> valueType) throws IOException {
        JavaType mapType = objectMapper.getTypeFactory().constructMapType(Map.class, valueType, UUID.class);
        return objectMapper.readValue(new File(filePath), mapType);
    }

}
