package uz.pdp.util;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileUtil {
    private static final ObjectMapper objectMapper;
    private static final XmlMapper xmlMapper;

    static {
        objectMapper = JsonMapper.builder().enable(MapperFeature.PROPAGATE_TRANSIENT_MARKER).build();
        xmlMapper = XmlMapper.builder().build();
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

    public static <T> List<T> readFromXml(String path, Class<T> clazz) throws IOException {
        return xmlMapper.readValue(new File(path),
                xmlMapper.getTypeFactory().constructCollectionType(List.class, clazz)
        );
    }
}
