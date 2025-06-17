package uz.pdp.model;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Data @Getter
public class CategoryNode {

    @JacksonXmlProperty(localName = "info")
    private Category category;

    @JacksonXmlElementWrapper(localName = "children")
    @JacksonXmlProperty(localName = "category")
    private List<CategoryNode> children = new ArrayList<>();

    public CategoryNode(Category category) {
        this.category = category;
    }
}
