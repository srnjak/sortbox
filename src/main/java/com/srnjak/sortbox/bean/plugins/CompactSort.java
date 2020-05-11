package com.srnjak.sortbox.bean.plugins;

import com.srnjak.sortbox.bean.BeanSortBox;
import com.srnjak.sortbox.PropertySortElement;
import com.srnjak.sortbox.SortOrder;
import com.srnjak.sortbox.bean.SortReader;
import com.srnjak.sortbox.bean.SortWriter;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CompactSort<O>
        implements SortReader<O, String>, SortWriter<O, String> {

    public static final String DELIMITER = ",";

    public static final String ELEMENT_PATTERN_STRING =
            "([+-]?)([A-Za-z_$][A-Za-z0-9_$.]*)";

    public static final String LIST_PATTERN_STRING =
            ELEMENT_PATTERN_STRING
                    + "(" + DELIMITER + ELEMENT_PATTERN_STRING + ")*";

    private static final Map<String, SortOrder> ORDER_MAP =
            new LinkedHashMap<>();

    static {
        ORDER_MAP.put("", SortOrder.ASCENDING);
        ORDER_MAP.put("+", SortOrder.ASCENDING);
        ORDER_MAP.put("-", SortOrder.DESCENDING);
    }

    @Override
    public String write(@NonNull BeanSortBox<O> beanSortBox) {

        return beanSortBox.stream()
                .map(this::formatElement)
                .collect(Collectors.joining(DELIMITER));
    }

    @Override
    public BeanSortBox<O> read(String sortListString) {

        if (StringUtils.isBlank(sortListString)) {
            return new BeanSortBox<>();
        }

        if (!isValid(sortListString)) {
            throw new IllegalArgumentException(sortListString);
        }

        return Stream.of(sortListString.split(DELIMITER))
                .map(this::parseElement)
                .collect(BeanSortBox.collector());
    }

    @Override
    public boolean isValid(String sortListString) {
        Pattern pattern = Pattern.compile("^" + LIST_PATTERN_STRING + "$");
        Matcher matcher = pattern.matcher(sortListString);
        return matcher.matches();
    }

    private String formatElement(
            PropertySortElement<?> propertySortElement) {
        String prefix = Optional.of(propertySortElement.getSortOrder())
                .filter(o -> o.equals(SortOrder.DESCENDING))
                .map(o -> "-")
                .orElse("");

        return prefix + propertySortElement.getSortBy();
    }

    private PropertySortElement<O> parseElement(
            @NonNull String sortElement) {
        Pattern pattern = Pattern.compile("^" + ELEMENT_PATTERN_STRING + "$");
        Matcher matcher = pattern.matcher(sortElement);

        if (matcher.find()) {
            return new PropertySortElement<>(
                    matcher.group(2), ORDER_MAP.get(matcher.group(1)));
        } else {
            throw new IllegalArgumentException(sortElement);
        }
    }
}
