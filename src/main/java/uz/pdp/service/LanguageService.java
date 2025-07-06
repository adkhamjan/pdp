package uz.pdp.service;

import uz.pdp.model.Language;
import uz.pdp.util.FileUtil;

import java.io.IOException;
import java.util.List;

public class LanguageService {
    private static final String fileName = "language.json";
    private static final List<Language> languages;

    static {
        try {
            languages = FileUtil.read(fileName, Language.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Language> getLanguage() {
        return languages;
    }
}
