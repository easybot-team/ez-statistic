import com.springwater.easybot.statistic.utils.StringFormatUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link StringFormatUtils}.
 */
class StringFormatUtilsTest {

    // --- camelToSnake Tests ---

    @Test
    void testCamelToSnake_withNull_shouldReturnEmptyString() {
        assertThat(StringFormatUtils.camelToSnake(null)).isEqualTo("");
    }

    @Test
    void testCamelToSnake_withEmptyString_shouldReturnEmptyString() {
        assertThat(StringFormatUtils.camelToSnake("")).isEqualTo("");
    }

    @Test
    void testCamelToSnake_withNormalCamelCase_shouldConvertToSnakeCase() {
        assertThat(StringFormatUtils.camelToSnake("doFunTest")).isEqualTo("do_fun_test");
    }

    @Test
    void testCamelToSnake_withLeadingCapital_shouldConvertCorrectly() {
        assertThat(StringFormatUtils.camelToSnake("DoFunTest")).isEqualTo("do_fun_test");
    }

    @Test
    void testCamelToSnake_withConsecutiveCapitals_shouldHandleProperly() {
        assertThat(StringFormatUtils.camelToSnake("XMLHttpRequest")).isEqualTo("xml_http_request");
    }


    // --- snakeToCamel Tests ---

    @Test
    void testSnakeToCamel_withNull_shouldReturnEmptyString() {
        assertThat(StringFormatUtils.snakeToCamel(null)).isEqualTo("");
    }

    @Test
    void testSnakeToCamel_withEmptyString_shouldReturnEmptyString() {
        assertThat(StringFormatUtils.snakeToCamel("")).isEqualTo("");
    }

    @Test
    void testSnakeToCamel_withNormalSnakeCase_shouldConvertToCamelCase() {
        assertThat(StringFormatUtils.snakeToCamel("do_fun_test")).isEqualTo("doFunTest");
    }


    // --- colonToDot Tests ---
    
    @Test
    void testColonToDot_withValidInput_shouldReplaceColonWithDot() {
        assertThat(StringFormatUtils.colonToDot("a:b:c")).isEqualTo("a.b.c");
    }


    // --- dotToColon Tests ---
    

    @Test
    void testDotToColon_withValidInput_shouldReplaceDotWithColon() {
        assertThat(StringFormatUtils.dotToColon("a.b.c")).isEqualTo("a:b:c");
    }


    // --- convertDelimiter Tests ---
    

    @Test
    void testConvertDelimiter_withValidChars_shouldReplaceCorrectly() {
        assertThat(StringFormatUtils.convertDelimiter("a-b-c", '-', '.')).isEqualTo("a.b.c");
    }
}
